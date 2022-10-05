package com.ismail.exchsim.model;

import com.ismail.exchsim.service.Order;
import com.ismail.exchsim.service.Trade;

/**
 * Trade between two clients on a given Instrument
 * 
 * @author ismail
 * @since 20221001
 */
public class TradeEvent extends ResponseMessage
{
    public OrderDetail orderDetail;
    
    public TradeDetail tradeDetail;

    public TradeEvent()
    {
        super(MessageType.Trade, true);
    }

    public TradeEvent(Order order, Trade trade)
    {
        super(MessageType.Trade, true);

        // order detail
        orderDetail = new OrderDetail(order);
        
        
        // trade detail
        tradeDetail = new TradeDetail(trade);
    }
}
