package com.ismail.exchsim.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.ismail.exchsim.service.Order;

import lombok.ToString;

/**
 * @author ismail
 * @since 20221001
 */
@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(Include.NON_NULL)
public class NewOrderResponse extends ResponseMessage
{
    public OrderDetail orderDetail;

    
    public NewOrderResponse()
    {
        super(MessageType.NewOrderResponse, true);
    }

    public NewOrderResponse(NewOrderRequest request)
    {
        super(MessageType.NewOrderResponse, true);
    }

    public NewOrderResponse(Order order)
    {
        super(MessageType.NewOrderResponse, true);

        orderDetail = new OrderDetail(order);


    }
}
