package com.ismail.exchsim.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

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
public class NewOrderRequest extends RequestMessage
{
    public String instrumentID;

    public String exchangeID;

    public String clientID;
    
    public String clientOrderID;
    
    public boolean side;

    public String orderType;

    public double quantity;

    public double price;

    public double stopPrice;

    public NewOrderRequest()
    {
        super(MessageType.NewOrderRequest);
    }
}
