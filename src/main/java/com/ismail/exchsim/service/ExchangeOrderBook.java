package com.ismail.exchsim.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import com.ismail.exchsim.model.CancelResponse;
import com.ismail.exchsim.model.NewOrderResponse;
import com.ismail.exchsim.model.OrderBookEntryEvent;
import com.ismail.exchsim.model.OrderBookEvent;
import com.ismail.exchsim.model.OrderState;
import com.ismail.exchsim.model.OrderType;
import com.ismail.exchsim.model.TopOfBookEvent;
import com.ismail.exchsim.model.TradeEvent;

import lombok.extern.slf4j.Slf4j;

/**
 * Order book for a given instrument / exchange
 * 
 * Contains list of buy and sell orders; sorted by price and time
 * 
 * @author ismail
 * @since 20221001
 */
@Slf4j
public class ExchangeOrderBook
{
    private ExchangeSimulatorService exchSimService;

    private Exchange exchange;

    private String instrumentID;

    private String exchangeID;

    /**
     * Active order list
     */
    //private ArrayList<Order> orderList = new ArrayList<>();

    /**
     * Historical order list
     */
    private ArrayList<Order> histOrderList = new ArrayList<>();

    // Buy orders are sorted by highest price first; then time (lowest first)
    private ArrayList<Order> buyOrderList = new ArrayList<>();

    // Sell orders are sorted by lowest price first; then time (lowest first)
    private ArrayList<Order> sellOrderList = new ArrayList<>();

    private HashMap<Integer, Order> ordersMapByOrderId = new HashMap<>();

    private ArrayList<Trade> tradeList = new ArrayList<>();

    /**
     * Lock used to maintain concurrency on the order cache
     */
    private Lock lock = new ReentrantLock();

    private ExecutorService executor = null;

    private OrderBook mdOrderBook = new OrderBook();

    private TopOfBook mdTopOfBook = new TopOfBook();

    // snapshot of latest order book event
    private OrderBookEvent mdOrderBookEv = null;

    // snapshot of latest top of book event
    private TopOfBookEvent mdTopOfBookEv = null;

    public ExchangeOrderBook(ExchangeSimulatorService exchSimService, Exchange exchange, String instrumentId, String exchangeID)
    {
        this.exchSimService = exchSimService;
        this.exchange = exchange;
        this.instrumentID = instrumentId;
        this.exchangeID = exchangeID;

        executor = Executors.newSingleThreadExecutor();
    }

    public String getExchangeID()
    {
        return exchangeID;
    }

    public String getInstrumentID()
    {
        return instrumentID;
    }

    /**
     * adds the order to the Order Book; then processes it for potential matches 
     * 
     * @param pOrder
     * @return return number of trades after adding this order
     */
    public void addOrder(Order order)
    {
        executor.execute(() -> addOrder_(order));
    }

    /**
     * adds the order to the Order Book; then processes it for potential matches 
     * 
     * @param pOrder
     * @return return number of trades after adding this order
     */
    private void addOrder_(Order order)
    {
        log.info("addOrder() " + instrumentID + ": " + order);

        try
        {
            // Validate the order type
            if ((OrderType.Limit.equals(order.orderType) || OrderType.Market.equals(order.orderType)) == false)
                throw new IllegalArgumentException("Invalid OrderType: " + order.orderType);

            if (ordersMapByOrderId.containsKey(order.orderID))
                throw new IllegalArgumentException("Duplicate OrderId: " + order.orderID);

        }
        catch (IllegalArgumentException ie)
        {

            order.active = false;
            order.status = OrderState.Rejected;
            order.notes = ie.getMessage();
            order.updateTime = System.currentTimeMillis();

            NewOrderResponse resp = new NewOrderResponse(order);

            resp.success = false;
            resp.errorMessage = order.notes;

            exchSimService.onOrderEvent(resp);

            return;
        }
        catch (Exception e)
        {
            order.active = false;
            order.status = OrderState.Rejected;
            order.notes = "Error processing request";
            order.updateTime = System.currentTimeMillis();

            NewOrderResponse resp = new NewOrderResponse(order);

            resp.success = false;
            resp.errorMessage = order.notes;

            exchSimService.onOrderEvent(resp);

            return;
        }

        lock.lock();
        try
        {
            ordersMapByOrderId.put(order.orderID, order);

            //orderList.add(order);

            if (order.side)
            {
                buyOrderList.add(order);

                // sort the buy orders by price (highest first) and time (lowest first)
                if (buyOrderList.size() > 1)
                    Collections.sort(buyOrderList, new OrderComparatorByPriceAndTime(false));
            }
            else
            {
                sellOrderList.add(order);

                // sort the sell orders by price (lowest first) and time (lowest first)
                if (sellOrderList.size() > 1)
                    Collections.sort(sellOrderList, new OrderComparatorByPriceAndTime(true));
            }

            // update order status
            order.status = OrderState.New;
            order.updateTime = System.currentTimeMillis();

            // ack the order
            NewOrderResponse resp = new NewOrderResponse(order);
            exchSimService.onOrderEvent(resp);

            // process the order
            processOrder(order);

            // update MD book and top of book
            updateTopOfBookAndOrderBookEvent();

        }
        finally
        {
            lock.unlock();
        }
    }
    
    

    public void cancelOrder(Order order)
    {
        executor.execute(() -> cancelOrder_(order));
    }

    private void cancelOrder_(Order order)
    {
        log.info("cancelOrder() " + instrumentID + ": " + order);

        try
        {
            // Validate the order type
            if (order.active == false)
                throw new IllegalArgumentException("Order is not active");

            if (order.getRemainingQuantity() <= 0.0)
                throw new IllegalArgumentException("Too late to cancel");

        }
        catch (IllegalArgumentException ie)
        {
            CancelResponse resp = new CancelResponse(order);

            resp.success = false;
            resp.errorMessage = ie.getMessage();

            exchSimService.onCancelEvent(resp);

            return;
        }
        catch (Exception e)
        {
            CancelResponse resp = new CancelResponse(order);

            resp.success = false;
            resp.errorMessage = "Error processing cancel request";

            exchSimService.onCancelEvent(resp);

            return;
        }

        lock.lock();
        try
        {
            ordersMapByOrderId.remove(order.orderID);

            if (order.side)
            {
                buyOrderList.remove(order);
            }
            else
            {
                sellOrderList.remove(order);
            }

            // update order status
            order.active = false;
            if (order.filledQuantity == 0.0)
                order.status = OrderState.Canceled;
            else if (order.getRemainingQuantity() > 0.0)
                order.status = OrderState.PartiallyFilled;
            else
                order.status = OrderState.Filled;
            
            order.updateTime = System.currentTimeMillis();

            // ack the cancel
            CancelResponse resp = new CancelResponse(order);
            resp.success = true;
            exchSimService.onCancelEvent(resp);

            // update MD book and top of book
            updateTopOfBookAndOrderBookEvent();

        }
        finally
        {
            lock.unlock();
        }
    }

    /**
     * Processes the order 
     * 
     * @param pOrder
     * @return return number of trades after processing this order
     */
    private void processOrder(Order pOrder)
    {
        int matchCount = 0;

        // ------------------------------------------------------------------------------------
        // Limit Order
        // ------------------------------------------------------------------------------------
        if (OrderType.Limit.equals(pOrder.orderType))
        {
            processOrder_Limit(pOrder);
        }
        // ------------------------------------------------------------------------------------
        // Market Order
        // ------------------------------------------------------------------------------------
        else if (OrderType.Market.equals(pOrder.orderType))
        {
            processOrder_Market(pOrder);
        }
    }

    /**
     * Processes Limit Order
     * 
     * @param order
     * @return return number of trades after processing this order
     */
    private void processOrder_Limit(Order order)
    {
        int matchCount = 0;

        // ------------------------------------------------------------------------------------
        // Buy Side
        // ------------------------------------------------------------------------------------
        if (order.side)
        {

            for (Order sellOrder : sellOrderList)
            {
                if (sellOrder.getRemainingQuantity() <= 0)
                    continue;

                if (order.price >= sellOrder.price)
                {
                    Trade trade = new Trade();
                    trade.tradeID = exchange.nextTradeId();
                    trade.time = System.currentTimeMillis();
                    trade.instrumentID = instrumentID;

                    // we use the maker price
                    trade.price = sellOrder.price;
                    trade.quantity = Math.min(order.getRemainingQuantity(), sellOrder.getRemainingQuantity());

                    trade.buyClientID = order.clientID;
                    trade.buyIsTaker = true;
                    trade.buyOrderID = order.orderID;

                    trade.sellClientID = sellOrder.clientID;
                    trade.sellIsTaker = false;
                    trade.sellOrderID = sellOrder.orderID;

                    matchCount++;

                    // Update buy order
                    order.filledQuantity += trade.quantity;
                    order.filledValue += trade.quantity * trade.price;

                    // update sell order
                    sellOrder.filledQuantity += trade.quantity;
                    sellOrder.filledValue += trade.quantity * trade.price;

                    processTrade(trade, order, sellOrder);

                    if (order.getRemainingQuantity() <= 0)
                        break;
                }
                else
                {
                    break;
                }
            }

            // if there are matches; remove done orders from the list
            if (matchCount > 0)
            {
                if (order.getRemainingQuantity() <= 0)
                    buyOrderList.remove(order);

                for (int i = sellOrderList.size() - 1; i >= 0; i--)
                {
                    Order ord = sellOrderList.get(i);
                    if (ord.getRemainingQuantity() <= 0)
                    {
                        histOrderList.add(ord);
                        sellOrderList.remove(i);
                    }
                }
            }
        }
        // ------------------------------------------------------------------------------------
        // Sell 
        // ------------------------------------------------------------------------------------
        else
        {
            for (Order buyOrder : buyOrderList)
            {
                if (buyOrder.getRemainingQuantity() <= 0)
                    continue;

                if (order.price <= buyOrder.price)
                {
                    Trade trade = new Trade();
                    trade.tradeID = exchange.nextTradeId();
                    trade.time = System.currentTimeMillis();
                    trade.instrumentID = instrumentID;

                    // we use the maker price
                    trade.price = buyOrder.price;
                    trade.quantity = Math.min(order.getRemainingQuantity(), buyOrder.getRemainingQuantity());

                    trade.buyClientID = buyOrder.clientID;
                    trade.buyIsTaker = false;
                    trade.buyOrderID = buyOrder.orderID;

                    trade.sellClientID = order.clientID;
                    trade.sellIsTaker = true;
                    trade.sellOrderID = order.orderID;

                    matchCount++;

                    // Update buy order
                    order.filledQuantity += trade.quantity;
                    order.filledValue += trade.quantity * trade.price;

                    // update buy order
                    buyOrder.filledQuantity += trade.quantity;
                    buyOrder.filledValue += trade.quantity * trade.price;

                    processTrade(trade, buyOrder, order);

                    if (order.getRemainingQuantity() <= 0)
                        break;
                }
                else
                {
                    break;
                }
            }

            // if there are matches; remove done orders from the list
            if (matchCount > 0)
            {
                if (order.getRemainingQuantity() <= 0)
                    sellOrderList.remove(order);

                for (int i = buyOrderList.size() - 1; i >= 0; i--)
                {
                    Order ord = buyOrderList.get(i);
                    if (ord.getRemainingQuantity() <= 0)
                    {
                        histOrderList.add(ord);
                        buyOrderList.remove(i);
                    }
                }
            }
        }

    }

    /**
     * Processes Limit Order
     * 
     * @param order
     * @return return number of trades after processing this order
     */
    private void processOrder_Market(Order order)
    {
        int matchCount = 0;

        // ------------------------------------------------------------------------------------
        // Buy Side
        // ------------------------------------------------------------------------------------
        if (order.side)
        {

            for (Order sellOrder : sellOrderList)
            {
                if (sellOrder.getRemainingQuantity() <= 0)
                    continue;

                Trade trade = new Trade();
                trade.tradeID = exchange.nextTradeId();
                trade.time = System.currentTimeMillis();
                trade.instrumentID = instrumentID;

                // we use the maker price
                trade.price = sellOrder.price;
                trade.quantity = Math.min(order.getRemainingQuantity(), sellOrder.getRemainingQuantity());

                trade.buyClientID = order.clientID;
                trade.buyIsTaker = true;
                trade.buyOrderID = order.orderID;

                trade.sellClientID = sellOrder.clientID;
                trade.sellIsTaker = false;
                trade.sellOrderID = sellOrder.orderID;

                matchCount++;

                // Update buy order
                order.filledQuantity += trade.quantity;
                order.filledValue += trade.quantity * trade.price;

                // update sell order
                sellOrder.filledQuantity += trade.quantity;
                sellOrder.filledValue += trade.quantity * trade.price;

                processTrade(trade, order, sellOrder);

                if (order.getRemainingQuantity() <= 0)
                    break;
            }

            // if there are matches; remove done orders from the list
            if (matchCount > 0)
            {
                // always remove buy order (it's a market order)
                buyOrderList.remove(order);

                // remove any filled sell order
                for (int i = sellOrderList.size() - 1; i >= 0; i--)
                {
                    Order ord = sellOrderList.get(i);
                    if (ord.getRemainingQuantity() <= 0)
                    {
                        histOrderList.add(ord);
                        sellOrderList.remove(i);
                    }
                }
            }
        }
        // ------------------------------------------------------------------------------------
        // Sell 
        // ------------------------------------------------------------------------------------
        else
        {
            for (Order buyOrder : buyOrderList)
            {
                if (buyOrder.getRemainingQuantity() <= 0)
                    continue;

                Trade trade = new Trade();
                trade.tradeID = exchange.nextTradeId();
                trade.time = System.currentTimeMillis();
                trade.instrumentID = instrumentID;

                // we use the maker price
                trade.price = buyOrder.price;
                trade.quantity = Math.min(order.getRemainingQuantity(), buyOrder.getRemainingQuantity());

                trade.buyClientID = buyOrder.clientID;
                trade.buyIsTaker = false;
                trade.buyOrderID = buyOrder.orderID;

                trade.sellClientID = order.clientID;
                trade.sellIsTaker = true;
                trade.sellOrderID = order.orderID;

                matchCount++;

                // Update buy order
                order.filledQuantity += trade.quantity;
                order.filledValue += trade.quantity * trade.price;

                // update buy order
                buyOrder.filledQuantity += trade.quantity;
                buyOrder.filledValue += trade.quantity * trade.price;

                processTrade(trade, buyOrder, order);

                if (order.getRemainingQuantity() <= 0)
                    break;

            }

            // if there are matches; remove done orders from the list
            if (matchCount > 0)
            {
                // always remove sell order (it's a market order)
                sellOrderList.remove(order);

                // remove any filled buy order
                for (int i = buyOrderList.size() - 1; i >= 0; i--)
                {
                    Order ord = buyOrderList.get(i);
                    if (ord.getRemainingQuantity() <= 0)
                    {
                        histOrderList.add(ord);
                        buyOrderList.remove(i);
                    }
                }
            }
        }

        // expire the order (if it's partially filled, or not filled at all)
        if (matchCount == 0)
        {
            sellOrderList.remove(order);
        }

        if (order.getRemainingQuantity() > 0.0)
        {
            // expire the order
            if (order.filledQuantity > 0.0)
            {
                order.status = OrderState.PartiallyFilled;
            }
            else
            {
                order.status = OrderState.Expired;
            }
            order.active = false;
            order.updateTime = System.currentTimeMillis();

            NewOrderResponse resp = new NewOrderResponse(order);

            exchSimService.onOrderEvent(resp);
        }

    }

    private void processTrade(Trade trade, Order buyOrder, Order sellOrder)
    {
        log.info("processTrade() " + instrumentID + ": " + trade);

        lock.lock();
        try
        {
            tradeList.add(trade);
        }
        finally
        {
            lock.unlock();
        }

        // buy order
        {
            // update buyOrder state
            if (buyOrder.getRemainingQuantity() > 0.0)
            {
                buyOrder.status = OrderState.PartiallyFilled;
            }
            else
            {
                buyOrder.status = OrderState.Filled;
                sellOrder.active = false;
            }
            buyOrder.updateTime = trade.time;

            // send trade events

            TradeEvent resp = new TradeEvent(buyOrder, trade);

            exchSimService.onTradeEvent(resp);
        }

        // sell order
        {
            // update sell Order state
            if (sellOrder.getRemainingQuantity() > 0.0)
            {
                sellOrder.status = OrderState.PartiallyFilled;
            }
            else
            {
                sellOrder.status = OrderState.Filled;
                sellOrder.active = false;
            }
            sellOrder.updateTime = trade.time;

            // send trade events

            TradeEvent resp = new TradeEvent(sellOrder, trade);

            exchSimService.onTradeEvent(resp);
        }
    }

    public void updateTopOfBookAndOrderBookEvent()
    {
        long now = System.currentTimeMillis();

        // update order book
        {
            mdOrderBook.updateBook(buyOrderList, sellOrderList);

            mdOrderBook.updateNumber++;

            mdOrderBook.updateTime = now;
        }

        // update top of book
        boolean tobChanged = false;
        {
            OrderBookEntry bid = mdOrderBook.getTopBid();
            OrderBookEntry ask = mdOrderBook.getTopAsk();

            double bid1 = 0.0;
            double bidQty1 = 0.0;
            if (bid != null)
            {
                bid1 = bid.price;
                bidQty1 = bid.quantity;
            }

            double ask1 = 0.0;
            double askQty1 = 0.0;
            if (ask != null)
            {
                ask1 = ask.price;
                askQty1 = ask.quantity;
            }

            if (mdTopOfBook.bid != bid1 || mdTopOfBook.bidQty != bidQty1)
            {
                tobChanged = true;

                mdTopOfBook.bid = bid1;
                mdTopOfBook.bidQty = bidQty1;
            }

            if (mdTopOfBook.ask != ask1 || mdTopOfBook.askQty != askQty1)
            {
                tobChanged = true;

                mdTopOfBook.ask = ask1;
                mdTopOfBook.askQty = askQty1;
            }

            if (tobChanged)
            {
                mdTopOfBook.updateNumber++;
                mdTopOfBook.updateTime = now;
            }
        }

        // send order book event
        {
            OrderBookEvent ev = new OrderBookEvent();
            ev.instrumentID = instrumentID;
            ev.exchangeID = exchangeID;
            ev.bid = mdTopOfBook.bid;
            ev.bidQty = mdTopOfBook.bidQty;
            ev.ask = mdTopOfBook.ask;
            ev.askQty = mdTopOfBook.askQty;
            ev.updateNumber = mdOrderBook.updateNumber;
            ev.updateTime = mdOrderBook.updateTime;
            ev.levels = mdOrderBook.getLevels();

            if (mdOrderBook.bids != null)
            {
                for (OrderBookEntry entry : mdOrderBook.bids)
                {
                    OrderBookEntryEvent bid = new OrderBookEntryEvent();
                    bid.quantity = entry.quantity;
                    bid.price = entry.price;
                    bid.orderCount = entry.orderCount;

                    ev.addBid(bid);
                }
            }

            if (mdOrderBook.asks != null)
            {
                for (OrderBookEntry entry : mdOrderBook.asks)
                {
                    OrderBookEntryEvent ask = new OrderBookEntryEvent();
                    ask.quantity = entry.quantity;
                    ask.price = entry.price;
                    ask.orderCount = entry.orderCount;

                    ev.addAsk(ask);
                }
            }

            mdOrderBookEv = ev;

            exchSimService.onOrderBookEvent(ev);
        }

        // send top of book event (if there is a change)
        if (tobChanged)
        {
            TopOfBookEvent ev = new TopOfBookEvent();
            ev.instrumentID = instrumentID;
            ev.exchangeID = exchangeID;
            ev.bid = mdTopOfBook.bid;
            ev.bidQty = mdTopOfBook.bidQty;
            ev.ask = mdTopOfBook.ask;
            ev.askQty = mdTopOfBook.askQty;
            ev.updateNumber = mdTopOfBook.updateNumber;
            ev.updateTime = mdTopOfBook.updateTime;

            mdTopOfBookEv = ev;
            exchSimService.onTopOfBookEvent(ev);
        }

    }

    public OrderBookEvent getOrderBookEventSnapshot()
    {
        return mdOrderBookEv;
    }

    public TopOfBookEvent getTopOfBookEventSnapshot()
    {
        return mdTopOfBookEv;
    }

    public ArrayList<Trade> getAllTrades()
    {
        ArrayList<Trade> list = new ArrayList<>();

        lock.lock();
        try
        {
            list.addAll(tradeList);
        }
        finally
        {
            lock.unlock();
        }

        return list;
    }

    public ArrayList<Order> getActiveOrders()
    {
        ArrayList<Order> list = new ArrayList<>();

        lock.lock();
        try
        {
            list = new ArrayList<>(buyOrderList.size() + sellOrderList.size());
            list.addAll(buyOrderList);
            list.addAll(sellOrderList);
        }
        finally
        {
            lock.unlock();
        }

        return list;
    }

}
