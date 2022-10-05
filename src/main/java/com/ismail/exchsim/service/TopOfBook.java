package com.ismail.exchsim.service;

import lombok.ToString;

/**
 * Top of Book
 * 
 * @author ismail
 * @since 20221001
 */
@ToString
public class TopOfBook
{
    public double bidQty = 0.0;

    public double bid = 0.0;

    public double ask = 0.0;

    public double askQty = 0.0;

    public double last = 0.0;

    public long updateTime = 0L;

    public long updateNumber = 0;
}
