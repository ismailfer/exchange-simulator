package com.ismail.exchsim;

import java.net.URI;
import java.util.LinkedHashMap;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.websocket.server.config.JettyWebSocketServletContainerInitializer;

import com.ismail.exchsim.controller.OrderBookWebsocket;
import com.ismail.exchsim.controller.OrderWebsocket;
import com.ismail.exchsim.controller.TopOfBookWebsocket;

import lombok.extern.slf4j.Slf4j;

/**
 * Jetty Server to host the websockets
 * 
 * @author ismail
 * @since 20221001
 */
@Slf4j
public class JettyServer
{

    private final Server server;

    private final ServerConnector connector;

    ServletContextHandler context = null;

    LinkedHashMap<String, Class> websocketMappings = new LinkedHashMap<>();

    public JettyServer(int portNumber)
    {
        server = new Server();
        connector = new ServerConnector(server);
        server.addConnector(connector);

        setPort(portNumber);
        
        // Setup the basic application "context" for this application at "/"
        // This is also known as the handler tree (in jetty speak)
        context = new ServletContextHandler(ServletContextHandler.SESSIONS);
        context.setContextPath("/");
        server.setHandler(context);
        
        addWebsocket("/ws/v1/order", OrderWebsocket.class);
        addWebsocket("/ws/v1/tob", TopOfBookWebsocket.class);
        addWebsocket("/ws/v1/orderBook", OrderBookWebsocket.class);

        configureWebsockets();
    }

    /**
     * example:
     * "/ticker/*", TickerWebsocket.class
     *
     * @param pMapping
     * @param pWebsocketClass
     */
    public void addWebsocket(String pMapping, Class pWebsocketClass)
    {

        // Add websockets
        websocketMappings.put(pMapping, pWebsocketClass);
    }

    public void configureWebsockets()
    {
        // Configure specific websocket behavior
        JettyWebSocketServletContainerInitializer.configure(context, (servletContext, wsContainer) -> {
            // Configure default max size
            wsContainer.setMaxTextMessageSize(65535);

            // Add websockets
            // wsContainer.addMapping("/ticker/*", TickerWebsocket.class);
            for (String mapping : websocketMappings.keySet())
            {
                wsContainer.addMapping(mapping, websocketMappings.get(mapping));
            }
        });
    }

    public void setPort(int port)
    {
        connector.setPort(port);
    }

    public void start() throws Exception
    {
        server.start();
    }

    public URI getURI()
    {
        return server.getURI();
    }

    public void stop() throws Exception
    {
        server.stop();
    }

    public void join() throws InterruptedException
    {
        server.join();
    }

}