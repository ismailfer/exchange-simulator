package com.ismail.exchsim.service;

import com.ismail.exchsim.model.OrderState;

import lombok.ToString;

/**
 * @author ismail
 * @since 20221001
 */
@ToString
public class Order
{
    public int orderID;

    public long time;
    
    public String clientID;
    
    public String clientOrderID;

    public String exchangeID;
    
    public String instrumentID;
    
    /**
     * Side of the order
     * 
     * true=Buy
     * false=Sell
     */
    public boolean side;
    
    public String orderType;
    
    /**
     * Quantity is always positive
     */
    public double quantity;
    
    public double price;
    
    public double stopPrice;
    
    //
    public boolean active;
    
    public String status;
    
    public String notes;
    
    public long updateTime;
    
    public double filledQuantity;
    
    public double filledValue;
    
    
    public Order()
    {
        
    }
        
    public Order(String clientId, String exchID, String instrumentId, double quantity, double price)
    {
        this.clientID = clientId;
        this.exchangeID = exchID;
        this.instrumentID = instrumentId;
        this.quantity = Math.abs(quantity);
        this.price = price;
        this.stopPrice = 0.0;
        
        this.orderType = "Limit";
        this.active = true;
        this.status = OrderState.PendingNew;
        
        side = (quantity >= 0);            
    }

    public Order(String clientId, String exchID, String instrumentId, String orderType, int quantity, double price, double stopPrice)
    {
        this.clientID = clientId;
        this.exchangeID = exchID;
        this.instrumentID = instrumentId;
        this.quantity = Math.abs(quantity);        
        this.orderType = orderType;
        this.price = price;
        this.stopPrice = stopPrice;
        
        this.active = true;
        this.status = OrderState.PendingNew;

        side = (quantity >= 0);            
    }

    
    public double getRemainingQuantity()
    {
        double remainingQuantity = 0.0;
        
        if (active)
        remainingQuantity = quantity - filledQuantity;

        return remainingQuantity;
            
    }
    
    public double getAveragePrice()
    {
        double avgPrice = 0.0;
        
        if (filledQuantity > 0)
            avgPrice = filledValue / filledQuantity;
        
        return avgPrice;
    }
}
