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
public class OrderStatusResponse extends ResponseMessage
{
    public OrderDetail orderDetail;
    
    public OrderStatusResponse()
    {
        super(MessageType.OrderStatusResponse, true);
    }

    public OrderStatusResponse(Order order)
    {
        super(MessageType.OrderStatusResponse, true);

        orderDetail = new OrderDetail(order);


    }
}
