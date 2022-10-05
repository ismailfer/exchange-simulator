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
public class ResponseMessage
{  
    public String messageType;
    
    public long time;
    
    public boolean success;

    public String errorMessage;
    
    public String errorDetail;
    
    public ResponseMessage()
    {
        this.time = System.currentTimeMillis();
    }
    
    public ResponseMessage(String messageType)
    {
        this.messageType = messageType;
        this.time = System.currentTimeMillis();
    }
    
    public ResponseMessage(String messageType, boolean success)
    {
        this.messageType = messageType;
        this.success = success;
        this.time = System.currentTimeMillis();
    }
    
    
    public ResponseMessage(String messageType, String errorMessage)
    {
        this.messageType = messageType;
        this.success = false;
        this.errorMessage = errorMessage;
        this.time = System.currentTimeMillis();
    }
}
