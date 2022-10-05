package com.ismail.exchsim.service;

import lombok.ToString;

/**
 * Trade between two clients on a given Instrument
 * 
 * @author ismail
 * @since 20221001
 */
@ToString
public class Trade
{
    public String instrumentID;
    
    public int tradeID;
    
    public long time;
    
    public double quantity;
    
    public double price;
    
    // buyer detail
    
    public String buyClientID;
    
    public int buyOrderID;
    
    public boolean buyIsTaker;

    // seller detail
    
    public String sellClientID;
    
    public int sellOrderID;
        
    public boolean sellIsTaker;
}
