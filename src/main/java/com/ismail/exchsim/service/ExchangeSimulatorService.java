package com.ismail.exchsim.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ismail.exchsim.JettyServer;
import com.ismail.exchsim.config.ExchangeSimulatorConfig;
import com.ismail.exchsim.controller.OrderBookWebsocket;
import com.ismail.exchsim.controller.OrderWebsocket;
import com.ismail.exchsim.controller.TopOfBookWebsocket;
import com.ismail.exchsim.model.OrderBookEvent;
import com.ismail.exchsim.model.TopOfBookEvent;
import com.ismail.exchsim.model.NewOrderRequest;
import com.ismail.exchsim.model.OrderEvent;
import com.ismail.exchsim.model.OrderStatus;
import com.ismail.exchsim.model.TradeEvent;
import com.ismail.exchsim.util.StringUtil;

import lombok.extern.slf4j.Slf4j;

/**
 * @author ismail
 * @since 20221001
 */
@Service
@Slf4j
public class ExchangeSimulatorService
{
    private final ExchangeSimulatorConfig config;

    /**
     * Lock used to maintain concurrency on the order book cache
     */
    //private Lock cacheLock = new ReentrantLock();

    private ArrayList<Exchange> exchangeList = new ArrayList<>();

    private HashMap<String, Exchange> exchangeMap = new HashMap<>();

    /**
     * Lock used to maintain concurrency on the order book cache
     */
    //private Lock subscriberLock = new ReentrantLock();

    private ArrayList<ExchangeEventSubscriber> orderEventSubscriberList = new ArrayList<>();

    private ArrayList<TopOfBookEventSubscriber> topOfBookSubscriberList = new ArrayList<>();

    private ArrayList<OrderBookEventSubscriber> orderBookSubscriberList = new ArrayList<>();

    private JettyServer server = null;

    private static ExchangeSimulatorService sMe = null;

    private ExecutorService executor = null;

    public static ExchangeSimulatorService getInstance()
    {
        return sMe;
    }

    @Autowired
    public ExchangeSimulatorService(ExchangeSimulatorConfig config)
    {
        sMe = this;

        this.config = Objects.requireNonNull(config, "Config is required.");

        executor = Executors.newSingleThreadExecutor();

        try
        {
            int portNumber = config.getWsPort();

            server = new JettyServer(portNumber);
            server.start();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    /**
     * lookup the Exchange by ID
     * 
     * If none exists; it will create one
     * 
     * @param instrumentID
     * @return
     */
    public Exchange getExchangeByID(String exchangeId)
    {
        Exchange exchange = null;

        exchange = exchangeMap.get(exchangeId);

        return exchange;
    }

    /**
     * lookup the Exchange by ID
     * 
     * If none exists; it will create one
     * 
     * @param instrumentID
     * @return
     */
    public Exchange getOrCreateExchangeByID(String exchangeId)
    {
        Exchange exchange = null;

        exchange = exchangeMap.get(exchangeId);
        if (exchange == null)
        {
            exchange = new Exchange(this, exchangeId);
            exchangeMap.put(exchangeId, exchange);
            exchangeList.add(exchange);
        }

        return exchange;
    }

    public void submitNewOrder(NewOrderRequest request)
    {
        executor.execute(() -> submitNewOrder_(request));
    }

    private void submitNewOrder_(NewOrderRequest request)
    {
        try
        {
            // input validation: clientId
            if (StringUtil.isDefined(request.exchangeID) == false)
                throw new IllegalArgumentException("exchangeID is required!");

            Exchange exchange = getOrCreateExchangeByID(request.exchangeID);

            if (exchange == null)
                throw new IllegalArgumentException("Invalid Exchange: " + request.exchangeID);

            Order order = new Order();
            order.active = true;
            order.status = OrderStatus.PendingNew;
            order.time = System.currentTimeMillis();
            order.updateTime = order.time;
            order.clientID = request.clientID;
            order.clientOrderID = request.clientOrderID;
            order.exchangeID = request.exchangeID;
            order.instrumentID = request.instrumentID;
            order.orderType = request.orderType;
            order.side = request.side;
            order.price = request.price;
            order.quantity = request.quantity;
            order.stopPrice = request.stopPrice;

            exchange.addOrder(order);

        }
        catch (IllegalArgumentException ie)
        {
            OrderEvent resp = new OrderEvent();
            resp.clientID = request.clientID;
            resp.clientOrderID = request.clientOrderID;
            resp.active = false;
            resp.status = OrderStatus.Rejected;
            resp.notes = ie.getMessage();
            
            resp.success = false;
            resp.errorMessage = resp.notes;

            onOrderEvent(resp);
        }
        catch (Exception e)
        {
            OrderEvent resp = new OrderEvent();
            resp.clientID = request.clientID;
            resp.clientOrderID = request.clientOrderID;
            resp.active = false;
            resp.status = OrderStatus.Rejected;
            resp.notes = e.getMessage();
            
            resp.success = false;
            resp.errorMessage = resp.notes;
            onOrderEvent(resp);
        }
    }

    public void onOrderEvent(OrderEvent msg)
    {
        executor.execute(() -> {
            for (ExchangeEventSubscriber subscriber : orderEventSubscriberList)
            {
                String clientID = subscriber.getClientID();

                if (StringUtil.isDefined(clientID) == false //
                        || clientID.equals(msg.clientID))
                {
                    subscriber.processMessage(msg);
                }
            }
        });
    }

    public void onTradeEvent(TradeEvent msg)
    {
        executor.execute(() -> {
            for (ExchangeEventSubscriber subscriber : orderEventSubscriberList)
            {
                String clientID = subscriber.getClientID();

                if (StringUtil.isDefined(clientID) == false //
                        || clientID.equals(msg.clientID))
                {
                    subscriber.processMessage(msg);
                }
            }
        });
    }

    public void onTopOfBookEvent(TopOfBookEvent msg)
    {
        executor.execute(() -> {

            for (TopOfBookEventSubscriber subscriber : topOfBookSubscriberList)
            {
                String instrumentID = subscriber.getInstrumentID();
                String exchangeID = subscriber.getExchangeID();

                boolean instrumentIDMatch = (StringUtil.isDefined(instrumentID) == false || instrumentID.equals(msg.instrumentID));
                boolean exchangeIDMatch = (StringUtil.isDefined(exchangeID) == false || exchangeID.equals(msg.exchangeID));

                if (instrumentIDMatch && exchangeIDMatch)
                {
                    subscriber.processMessage(msg);
                }
            }
        });
    }

    public void onOrderBookEvent(OrderBookEvent ev)
    {
        executor.execute(() -> {
            for (OrderBookEventSubscriber subscriber : orderBookSubscriberList)
            {
                String instrumentID = subscriber.getInstrumentID();
                String exchangeID = subscriber.getExchangeID();

                boolean instrumentIDMatch = (StringUtil.isDefined(instrumentID) == false || instrumentID.equals(ev.instrumentID));
                boolean exchangeIDMatch = (StringUtil.isDefined(exchangeID) == false || exchangeID.equals(ev.exchangeID));

                if (instrumentIDMatch && exchangeIDMatch)
                {
                    subscriber.processMessage(ev.trimToLevel(subscriber.getLevels()));
                }
            }
        });
    }

    public void subscribeToOrderEvent(ExchangeEventSubscriber subscriber)
    {
        executor.execute(() -> {
            if (orderEventSubscriberList.contains(subscriber) == false)
                orderEventSubscriberList.add(subscriber);
        });
    }

    public void unsubscribeFromOrderEvent(ExchangeEventSubscriber subscriber)
    {
        executor.execute(() -> orderEventSubscriberList.remove(subscriber));
    }

    public void subscribeToTopOfBookEvent(TopOfBookEventSubscriber subscriber)
    {
        executor.execute(() -> {
            if (topOfBookSubscriberList.contains(subscriber) == false)
                topOfBookSubscriberList.add(subscriber);

            // send a snapshot based on subscriptions

            String instrumentID = subscriber.getInstrumentID();
            String exchangeID = subscriber.getExchangeID();

            if (StringUtil.isDefined(exchangeID) && StringUtil.isDefined(instrumentID))
            {
                Exchange exchange = getExchangeByID(exchangeID);

                if (exchange != null)
                {
                    ExchangeOrderBook orderBook = exchange.getOrderBookByInstrument(instrumentID);
                    if (orderBook != null)
                    {
                        TopOfBookEvent ev = orderBook.getTopOfBookEventSnapshot();
                        if (ev != null)
                            subscriber.processMessage(ev);
                    }
                }

            }
            else if (StringUtil.isDefined(exchangeID))
            {
                Exchange exchange = getExchangeByID(exchangeID);
                if (exchange != null)
                {
                    ArrayList<ExchangeOrderBook> orderBookList = exchange.getAllOrderBooks();
                    for (ExchangeOrderBook orderBook : orderBookList)
                    {
                        TopOfBookEvent ev = orderBook.getTopOfBookEventSnapshot();
                        if (ev != null)
                            subscriber.processMessage(ev);
                    }
                }
            }
            else
            {
                //
                for (Exchange exchange : exchangeList)
                {
                    ArrayList<ExchangeOrderBook> orderBookList = exchange.getAllOrderBooks();
                    for (ExchangeOrderBook orderBook : orderBookList)
                    {
                        TopOfBookEvent ev = orderBook.getTopOfBookEventSnapshot();
                        if (ev != null)
                            subscriber.processMessage(ev);
                    }
                }

            }

        });
    }

    public void unsubscribeFromTopOfBookEvent(TopOfBookEventSubscriber subscriber)
    {
        executor.execute(() -> topOfBookSubscriberList.remove(subscriber));
    }

    public void subscribeToOrderBookEvent(OrderBookEventSubscriber subscriber)
    {
        executor.execute(() -> {
            if (orderBookSubscriberList.contains(subscriber) == false)
                orderBookSubscriberList.add(subscriber);

            // send a snapshot based on subscriptions

            String instrumentID = subscriber.getInstrumentID();
            String exchangeID = subscriber.getExchangeID();

            if (StringUtil.isDefined(exchangeID) && StringUtil.isDefined(instrumentID))
            {
                Exchange exchange = getExchangeByID(exchangeID);

                if (exchange != null)
                {
                    ExchangeOrderBook orderBook = exchange.getOrderBookByInstrument(instrumentID);
                    if (orderBook != null)
                    {
                        OrderBookEvent ev = orderBook.getOrderBookEventSnapshot();

                        if (ev != null)
                            subscriber.processMessage(ev.trimToLevel(subscriber.getLevels()));
                    }
                }

            }
            else if (StringUtil.isDefined(exchangeID))
            {
                Exchange exchange = getExchangeByID(exchangeID);
                if (exchange != null)
                {
                    ArrayList<ExchangeOrderBook> orderBookList = exchange.getAllOrderBooks();
                    for (ExchangeOrderBook orderBook : orderBookList)
                    {
                        OrderBookEvent ev = orderBook.getOrderBookEventSnapshot();

                        if (ev != null)
                            subscriber.processMessage(ev.trimToLevel(subscriber.getLevels()));
                    }
                }
            }
            else
            {
                //
                for (Exchange exchange : exchangeList)
                {
                    ArrayList<ExchangeOrderBook> orderBookList = exchange.getAllOrderBooks();
                    for (ExchangeOrderBook orderBook : orderBookList)
                    {
                        OrderBookEvent ev = orderBook.getOrderBookEventSnapshot();

                        if (ev != null)
                            subscriber.processMessage(ev.trimToLevel(subscriber.getLevels()));
                    }
                }

            }
        });
    }

    public void unsubscribeFromOrderBookEvent(OrderBookEventSubscriber subscriber)
    {
        executor.execute(() -> orderBookSubscriberList.remove(subscriber));
    }
}
