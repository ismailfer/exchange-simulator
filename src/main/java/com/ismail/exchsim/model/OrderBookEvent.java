package com.ismail.exchsim.model;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.ToString;

/**
 * Full Order Book
 * 
 * @author ismail
 * @since 20221001
 */
@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
public class OrderBookEvent extends ResponseMessage
{
    public String instrumentID = null;

    public String exchangeID = null;

    public double bidQty = 0.0;

    public double bid = 0.0;

    public double ask = 0.0;

    public double askQty = 0.0;

    public long updateTime = 0L;

    public long updateNumber = 0L;

    public int levels = 0;

    public List<OrderBookEntryEvent> bids = null;

    public List<OrderBookEntryEvent> asks = null;

    public OrderBookEvent()
    {
        super(MessageType.OrderBook, true);
    }

    public void addBid(OrderBookEntryEvent bid)
    {
        if (bids == null)
        {
            bids = new ArrayList<>(100);
        }
        
        bids.add(bid);

    }
    
    public void addAsk(OrderBookEntryEvent ask)
    {
        if (asks == null)
        {
            asks = new ArrayList<>(100);
        }
        
        asks.add(ask);
    }
    
    public int levels()
    {
        int bidsSize = (bids == null ? 0 : bids.size());
        int asksSize = (asks == null ? 0 : asks.size());

        int levels = Math.max(bidsSize, asksSize);

        return levels;
    }

    /**
     * Trims the order book to a given max levels
     * 
     * If the order book is smaller than max levels; this instance will be returned
     * 
     * @param levels
     * @return
     */
    public OrderBookEvent trimToLevel(int levels)
    {
        if (levels < levels())
        {
            OrderBookEvent ev = new OrderBookEvent();

            ev.instrumentID = instrumentID;
            ev.exchangeID = exchangeID;
            ev.bidQty = bidQty;
            ev.bid = bid;
            ev.ask = ask;
            ev.askQty = askQty;
            ev.updateTime = updateTime;
            ev.updateNumber = updateNumber;

            if (bids != null)
            {
                ev.bids = new ArrayList<>(levels);
                for (int i = 0; i < bids.size() && i < levels; i++)
                    ev.bids.add(bids.get(i));
            }

            if (asks != null)
            {
                ev.asks = new ArrayList<>(levels);
                for (int i = 0; i < asks.size() && i < levels; i++)
                    ev.asks.add(asks.get(i));
            }

            return ev;
        }
        else
        {
            return this;
        }
    }
}
