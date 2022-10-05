package com.ismail.exchsim.service;

import com.ismail.exchsim.model.ResponseMessage;

/**
 * @author ismail
 * @since 20221001
 */
public interface ExchangeEventSubscriber
{
    String getClientID();
    
    void processMessage(ResponseMessage msg);
}
