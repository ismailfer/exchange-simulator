package com.ismail.exchsim.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.ismail.exchsim.service.Order;

import lombok.Data;
import lombok.ToString;

/**
 * @author ismail
 * @since 20221001
 */
@Data
@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(Include.NON_NULL)
public class OrderDetail
{  
    public String instrumentID;

    public String exchangeID;

    public String clientID;
    
    public String clientOrderID;
    
    // order detail
    
    public long orderID;

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
    
    public OrderDetail()
    {
       
    }
    
    public OrderDetail(NewOrderRequest order)
    {
        instrumentID = order.instrumentID;
        exchangeID = order.exchangeID;

        clientID = order.clientID;
        clientOrderID = order.clientOrderID;

        orderType = order.orderType;
        side = order.side;
        price = order.price;
        quantity = order.quantity;

        status = OrderState.PendingNew;

    }
    
    
    public OrderDetail(Order order)
    {
        
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
        
        remainingQuantity =  order.getRemainingQuantity();
        doneQuantity = order.filledQuantity;
        averagePrice = order.getAveragePrice();
        
        notes = order.notes;
        
    }
}
