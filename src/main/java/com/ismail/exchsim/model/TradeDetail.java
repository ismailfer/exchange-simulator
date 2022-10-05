package com.ismail.exchsim.model;

import com.ismail.exchsim.service.Trade;

/**
 * Trade between two clients on a given Instrument
 * 
 * @author ismail
 * @since 20221001
 */
public class TradeDetail 
{
    
    // trade detail
    
    public int tradeID;

    public double tradeQuantity;

    public double tradePrice;

    public boolean isTaker;

    public TradeDetail()
    {
    }

    public TradeDetail(Trade trade)
    {        
        // trade detail

        tradeID = trade.tradeID;
        tradeQuantity = trade.quantity;
        tradePrice = trade.price;

        isTaker = trade.buyIsTaker;
    }
}
