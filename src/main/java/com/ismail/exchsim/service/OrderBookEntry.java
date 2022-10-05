package com.ismail.exchsim.service;

import lombok.ToString;

/**
 * Order book entry
 * 
 * @author ismail
 * @since 20221001
 */
@ToString
public class OrderBookEntry
{
    public double quantity = 0.0;

    public double price = 0.0;

    public int orderCount = 0;
}
