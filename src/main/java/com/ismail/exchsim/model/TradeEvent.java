package com.ismail.exchsim.model;

import com.ismail.exchsim.service.Order;
import com.ismail.exchsim.service.Trade;

/**
 * Trade between two clients on a given Instrument
 * 
 * @author ismail
 * @since 20221001
 */
public class TradeEvent extends ResponseMessage
{
    public String instrumentID;

    public String exchangeID;

    public String clientID;

    public String clientOrderID;

    // order detail

    public int orderID;
    
    public String orderType;
    
    public boolean side;
    
    public double price;
    
    public double quantity;
    
    //
    public boolean active;
    
    public String status;

    public double remainingQuantity;
    
    public double doneQuantity;
    
    public double averagePrice;
    
    public String notes;
    
    // trade detail
    
    public int tradeID;

    public double tradeQuantity;

    public double tradePrice;

    public boolean isTaker;

    public TradeEvent()
    {
        super(MessageType.Trade, true);
    }

    public TradeEvent(Order order, Trade trade)
    {
        super(MessageType.Trade, true);

        // order detail
        instrumentID = order.instrumentID;
        exchangeID = order.exchangeID;
        
        clientID = order.clientID;
        clientOrderID = order.clientOrderID;
        
        orderID = order.orderID;
        orderType = order.orderType;
        side = order.side;
        price = order.price;
        quantity = order.quantity;
        
        //
        status = order.status;
        active = order.active;
        
        remainingQuantity = order.getRemainingQuantity();
        doneQuantity = order.filledQuantity;
        averagePrice = order.getAveragePrice();
        
        notes = order.notes;
        
        // trade detail

        tradeID = trade.tradeID;
        tradeQuantity = trade.quantity;
        tradePrice = trade.price;

        isTaker = trade.buyIsTaker;
    }
}
