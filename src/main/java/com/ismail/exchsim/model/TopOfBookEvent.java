package com.ismail.exchsim.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import lombok.ToString;

/**
 * Top of Book
 * 
 * @author ismail
 * @since 20221001
 */
@ToString
@JsonInclude(Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class TopOfBookEvent extends ResponseMessage
{
    public String instrumentID = null;

    public String exchangeID = null;

    public double bidQty = 0.0;

    public double bid = 0.0;

    public double ask = 0.0;

    public double askQty = 0.0;

    public long updateTime = 0L;

    public long updateNumber = 0L;

    public TopOfBookEvent()
    {
        super(MessageType.TopOfBook, true);
    }
    

}
