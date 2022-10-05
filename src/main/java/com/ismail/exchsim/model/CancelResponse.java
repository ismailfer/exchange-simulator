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
public class CancelResponse extends ResponseMessage
{
    public OrderDetail orderDetail;

    
    public CancelResponse()
    {
        super(MessageType.CancelResponse, true);
    }

    public CancelResponse(Order order)
    {
        super(MessageType.CancelResponse, true);

        orderDetail = new OrderDetail(order);
    }
}
