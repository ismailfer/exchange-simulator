package com.ismail.exchsim.service;

import com.ismail.exchsim.model.ResponseMessage;

/**
 * @author ismail
 * @since 20221001
 */
public interface OrderBookEventSubscriber
{
    String getInstrumentID();

    String getExchangeID();
    
    int getLevels();

    void processMessage(ResponseMessage msg);
}
