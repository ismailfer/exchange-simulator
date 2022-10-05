package com.ismail.exchsim.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import lombok.ToString;

/**
 * @author ismail
 * @since 20221001
 */
@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(Include.NON_NULL)
public class PongResponse extends ResponseMessage
{  
    public String pongID;
    
    public PongResponse()
    {
        super(MessageType.Pong, true);
    }

    public PongResponse(String pongID)
    {
        super(MessageType.Pong, true);
        this.pongID = pongID;
    }
}
