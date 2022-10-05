package com.ismail.exchsim.controller;

import java.time.Duration;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.eclipse.jetty.websocket.api.RemoteEndpoint;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.WebSocketAdapter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.ismail.exchsim.model.MessageType;
import com.ismail.exchsim.model.NewOrderRequest;
import com.ismail.exchsim.model.PingRequest;
import com.ismail.exchsim.model.PongResponse;
import com.ismail.exchsim.model.RequestMessage;
import com.ismail.exchsim.model.ResponseMessage;
import com.ismail.exchsim.service.ExchangeEventSubscriber;
import com.ismail.exchsim.service.ExchangeSimulatorService;
import com.ismail.exchsim.service.TopOfBookEventSubscriber;
import com.ismail.exchsim.util.WebUtil;

import lombok.extern.slf4j.Slf4j;

/**
 * Top Of Book websocket
 * 
 * @author ismail
 * @since 20221001
 */
@Slf4j
public class TopOfBookWebsocket extends WebSocketAdapter
{

    private Session sess = null;

    private RemoteEndpoint remoteEndpoint = null;

    private ExchangeSimulatorService exchSimService = null;

    // Jackson
    private ObjectMapper mapper = new ObjectMapper();

    private ObjectReader jsonRequestReader = null;

    private ObjectReader jsonPingRequestReader = null;

    private String instrumentID = null;

    private String exchangeID = null;

    private TopOfBookEventSubscriberImpl tobSubscriber = null;

    private ExecutorService executor = null;

    public TopOfBookWebsocket()
    {
        mapper = new ObjectMapper();
        jsonRequestReader = mapper.readerFor(RequestMessage.class);
        jsonPingRequestReader = mapper.readerFor(PingRequest.class);
    }

    @Override
    public void onWebSocketConnect(Session sess)
    {
        super.onWebSocketConnect(sess);

        this.sess = sess;

        sess.setIdleTimeout(Duration.ZERO);

        remoteEndpoint = sess.getRemote();

        String remoteAddr = WebUtil.getRemoteAddress(sess.getRemoteAddress());

        executor = Executors.newSingleThreadExecutor();

        exchSimService = ExchangeSimulatorService.getInstance();

        instrumentID = WebUtil.get(sess.getUpgradeRequest(), "instrumentID", null);
        exchangeID = WebUtil.get(sess.getUpgradeRequest(), "exchangeID", null);

        log.info("onWebSocketConnect() << ", remoteAddr, " instrumentID=", instrumentID, " exchangeID=", exchangeID);

        // send subscription accepted
        ResponseMessage msg = new ResponseMessage();
        msg.messageType = MessageType.Subscribed;
        msg.success = true;
        msg.time = System.currentTimeMillis();

        sendMessage(msg);

        tobSubscriber = new TopOfBookEventSubscriberImpl();

        exchSimService.subscribeToTopOfBookEvent(tobSubscriber);

    }

    @Override
    public void onWebSocketText(String message)
    {
        super.onWebSocketText(message);

        log.info("onWebSocketText() << " + message);

        try
        {
            RequestMessage req = jsonRequestReader.readValue(message);

            if (MessageType.Ping.equals(req.messageType))
            {
                PingRequest pingReq = jsonPingRequestReader.readValue(message);

                processPingRequest(pingReq);
            }
            else
            {
                ResponseMessage msg = new ResponseMessage(MessageType.Error, "Invalid MessageType: " + req.getMessageType());

                sendMessage(msg);
            }
        }
        catch (IllegalArgumentException e)
        {
            ResponseMessage msg = new ResponseMessage(MessageType.Error, e.getMessage());

            sendMessage(msg);
        }
        catch (Exception e)
        {
            ResponseMessage msg = new ResponseMessage(MessageType.Error, "Error processing request");

            sendMessage(msg);
        }
    }

    private void processPingRequest(PingRequest pingReq)
    {
        PongResponse pongResp = new PongResponse(pingReq.pingID);

        sendMessage(pongResp);
    }

    @Override
    public void onWebSocketClose(int statusCode, String reason)
    {
        super.onWebSocketClose(statusCode, reason);

        log.info("onWebSocketClose() << " + statusCode + ":" + reason);

        exchSimService.unsubscribeFromTopOfBookEvent(tobSubscriber);

        executor.shutdown();
    }

    @Override
    public void onWebSocketError(Throwable cause)
    {
        super.onWebSocketError(cause);

        log.info("onWebSocketError() << " + cause.getMessage(), cause);

        exchSimService.unsubscribeFromTopOfBookEvent(tobSubscriber);

        executor.shutdown();
    }

    public void awaitClosure() throws InterruptedException
    {
        log.info("onWebSocketClose() ");

    }

    public void sendMessage(String text)
    {
        executor.execute(() -> {
            try
            {
                if (log.isDebugEnabled())
                    log.debug("sendMessage() >> " + text);

                if (sess.isOpen())
                    remoteEndpoint.sendString(text);
            }
            catch (Exception e)
            {
                log.warn("Non-fatal Error: ", e.getMessage(), e);
            }
        });
    }

    public void sendMessage(ResponseMessage resp)
    {
        executor.execute(() -> {
            try
            {
                String text = mapper.writeValueAsString(resp);

                if (log.isDebugEnabled())
                    log.debug("sendMessage() >> " + text);

                if (sess.isOpen())
                    remoteEndpoint.sendString(text);
            }
            catch (JsonProcessingException e)
            {
                log.warn("Non-fatal Error: ", e.getMessage(), e);
            }
            catch (Exception e)
            {
                log.warn("Non-fatal Error: ", e.getMessage(), e);
            }
        });
    }

    public class TopOfBookEventSubscriberImpl implements TopOfBookEventSubscriber
    {
        @Override
        public String getInstrumentID()
        {
            return instrumentID;
        }

        @Override
        public String getExchangeID()
        {
            return exchangeID;
        }
        
        @Override
        public void processMessage(ResponseMessage msg)
        {
            sendMessage(msg);
        }
    }
}