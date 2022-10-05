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
public class PingRequest extends RequestMessage
{
    public String pingID;
    
    public PingRequest()
    {
        super(MessageType.Ping);
    }

    public PingRequest(String pingID)
    {
        super(MessageType.Pong);
        this.pingID = pingID;
    }
}
