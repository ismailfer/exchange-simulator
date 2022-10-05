package com.ismail.exchsim.service;

import com.ismail.exchsim.model.ResponseMessage;

/**
 * @author ismail
 * @since 20221001
 */
public interface TopOfBookEventSubscriber
{
    String getInstrumentID();

    String getExchangeID();

    void processMessage(ResponseMessage msg);
}
