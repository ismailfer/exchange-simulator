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
public class OrderStatusEvent extends ResponseMessage
{  
    public OrderDetail orderDetail;

    
    public OrderStatusEvent()
    {
        super(MessageType.NewOrderResponse, true);
    }
    
 
    
    public OrderStatusEvent(Order order)
    {
        super(MessageType.NewOrderResponse, true);
        
        orderDetail = new OrderDetail(order);

        
    }
}
