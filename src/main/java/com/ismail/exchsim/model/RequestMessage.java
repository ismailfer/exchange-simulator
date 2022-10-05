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
public class RequestMessage
{  
    public String messageType;
    
    public long time;
    
    public RequestMessage()
    {
        
    }
    
    public RequestMessage(String messageType)
    {
        this.messageType = messageType;
        this.time = System.currentTimeMillis();
    }
}
