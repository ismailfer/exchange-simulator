package com.ismail.exchsim.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import com.ismail.exchsim.model.OrderType;

/**
 * Exchange
 * 
 * - Maintains multiple order books; one per instrument
 * - Accepts new orders from clients
 * - Validate the orders
 * - Process the orders
 * - Send trade events back to clients
 * 
 * @author ismail
 * @since 20221001
 */
public class Exchange
{
    private ExchangeSimulatorService exchSimService = null;
    
    /**
     * Lock used to maintain concurrency on the order book cache
     */
    private Lock cacheLock = new ReentrantLock();

    private ArrayList<ExchangeOrderBook> orderBookList = new ArrayList<>();

    private HashMap<String, ExchangeOrderBook> orderBookByInstrumentID = new HashMap<>();

    private ArrayList<Order> orderList = new ArrayList<>();

    private HashMap<Integer, Order> orderByOrderID = new HashMap<>();

    private HashMap<String, Order> orderByClientOrderID = new HashMap<>();

    /**
     * Lock used to maintain concurrency on the orderId generator
     */
    private Lock orderIdLock = new ReentrantLock();

    private int orderIdGenerator = 0;

    /**
     * Lock used to maintain concurrency on the tradeId generator
     */
    private Lock tradeIdLock = new ReentrantLock();

    private int tradeIdGenerator = 0;


    public Exchange(ExchangeSimulatorService exchSimService)
    {
        this.exchSimService = exchSimService;
    }
    
    private int nextOrderId()
    {
        orderIdLock.lock();
        try
        {
            orderIdGenerator++;
            return orderIdGenerator;
        }
        finally
        {
            orderIdLock.unlock();
        }
    }

    protected int nextTradeId()
    {
        tradeIdLock.lock();
        try
        {
            tradeIdGenerator++;
            return tradeIdGenerator;
        }
        finally
        {
            tradeIdLock.unlock();
        }
    }

    /**
     * lookup the OrderBook by instrumentId
     * 
     * If none exists; it will create one
     * 
     * @param instrumentId
     * @return
     */
    public ExchangeOrderBook getOrCreateOrderBookByInstrument(String instrumentID, String exchangeID)
    {
        ExchangeOrderBook orderBook = null;

        String key = instrumentID + "." + exchangeID;
        
        cacheLock.lock();
        try
        {
            orderBook = orderBookByInstrumentID.get(key);
            if (orderBook == null)
            {
                orderBook = new ExchangeOrderBook(exchSimService, this, instrumentID, exchangeID);
                orderBookByInstrumentID.put(key, orderBook);
                orderBookList.add(orderBook);
            }

        }
        finally
        {
            cacheLock.unlock();
        }

        return orderBook;
    }

    /**
     * lookup the OrderBook by instrumentId
     * 
     * If none exists; it will create one
     * 
     * @param instrumentId
     * @return
     */
    public ExchangeOrderBook getOrderBookByInstrument(String instrumentID, String exchangeID)
    {
        String key = instrumentID + "." + exchangeID;
        
        cacheLock.lock();
        try
        {
            return orderBookByInstrumentID.get(key);
        }
        finally
        {
            cacheLock.unlock();
        }
    }
    
    public ArrayList<ExchangeOrderBook> getAllOrderBooks()
    {
        cacheLock.lock();
        try
        {
            ArrayList<ExchangeOrderBook> list = new ArrayList<>(orderBookList);
            return list;
        }
        finally
        {
            cacheLock.unlock();
        }
    }
    
    public void addOrder(Order order)
    {
        // input validation: clientId
        if (order.clientID == null || order.clientID.trim().length() == 0)
            throw new IllegalArgumentException("clientId is required!");

        // input validation: instrument
        if (order.exchangeID == null || order.exchangeID.trim().length() == 0)
            throw new IllegalArgumentException("exchangeID is required!");

        // input validation: instrument
        if (order.instrumentID == null || order.instrumentID.trim().length() == 0)
            throw new IllegalArgumentException("instrumentId is required!");

        // input validation: price for limit order
        if (OrderType.Limit.equals(order.orderType) && order.price <= 0.0)
            throw new IllegalArgumentException("invalid price");

        // input validation: price for market order
        if (OrderType.Market.equals(order.orderType) && order.price > 0.0)
            throw new IllegalArgumentException("price should be 0.0 for market order");

        // input validation: quantity
        if (order.quantity <= 0)
            throw new IllegalArgumentException("invalid quantity");

        // assign an order id            
        order.time = System.currentTimeMillis();
        order.updateTime = order.time;

        // add order to the cache
        orderIdLock.lock();
        try
        {
            order.orderID = nextOrderId();
            orderByOrderID.put(order.orderID, order);
            orderList.add(order);
        }
        finally
        {
            orderIdLock.unlock();
        }
        
        // lookup the OrderBook
        ExchangeOrderBook orderBook = getOrCreateOrderBookByInstrument(order.instrumentID,  order.exchangeID);

        // add the order to orderBook
        orderBook.addOrder(order);
    }
    
    public void cancelOrder(Order order)
    {
        if (order.active == false)
            throw new IllegalArgumentException("Order is not active");

        // assign an order id            
        order.time = System.currentTimeMillis();
        order.updateTime = order.time;

       
        // lookup the OrderBook
        ExchangeOrderBook orderBook = getOrderBookByInstrument(order.instrumentID,  order.exchangeID);

        // add the order to orderBook
        if (orderBook != null)
            orderBook.cancelOrder(order);
    }
    
    public Order getOrderByID(int orderID)
    {
        // add order to the cache
        orderIdLock.lock();
        try
        {
            return orderByOrderID.get(orderID);
        }
        finally
        {
            orderIdLock.unlock();
        }
    }
    
    public Order getOrderByClientOrderID(String clientID, String clientOrderID)
    {
        String key = clientID + "." + clientOrderID;
        
        // add order to the cache
        orderIdLock.lock();
        try
        {
            return orderByClientOrderID.get(key);
        }
        finally
        {
            orderIdLock.unlock();
        }
    }
    
    public ArrayList<Trade> getAllTrades()
    {
        ArrayList<Trade> tradeList = new ArrayList<>();

        for (ExchangeOrderBook orderBook : orderBookList)
        {
            tradeList.addAll(orderBook.getAllTrades());
        }

        // sort trades by tradeId (TradeId is already sorted by time)
        if (tradeList.size() > 1)
            Collections.sort(tradeList, (o1, o2) -> Integer.compare(o1.tradeID, o2.tradeID));

        return tradeList;
    }


    public ArrayList<Order> getActiveOrders()
    {
        ArrayList<Order> orderList = new ArrayList<>();

        for (ExchangeOrderBook orderBook : orderBookList)
        {
            orderList.addAll(orderBook.getActiveOrders());
        }

        // sort trades by tradeId (TradeId is already sorted by time)
        if (orderList.size() > 1)
            Collections.sort(orderList, (o1, o2) -> Integer.compare(o1.orderID, o2.orderID));

        return orderList;
    }
    
    public ArrayList<Order> getActiveOrdersByInstrumentId(String instrumentId, String exchangeId)
    {
        ExchangeOrderBook orderBook = getOrderBookByInstrument(instrumentId, exchangeId);
        
        ArrayList<Order> orderList = orderBook.getActiveOrders();

        // sort trades by tradeId (TradeId is already sorted by time)
        if (orderList.size() > 1)
            Collections.sort(orderList, (o1, o2) -> Integer.compare(o1.orderID, o2.orderID));

        return orderList;
    }
}
