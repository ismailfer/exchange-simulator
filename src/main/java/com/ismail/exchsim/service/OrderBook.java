package com.ismail.exchsim.service;

import java.util.ArrayList;
import java.util.Arrays;

import gnu.trove.map.hash.TDoubleObjectHashMap;
import lombok.ToString;

/**
 * Full Order Book
 * 
 * @author ismail
 * @since 20221001
 */
@ToString
public class OrderBook
{
    public ArrayList<OrderBookEntry> bids = null;

    public ArrayList<OrderBookEntry> asks = null;

    public long updateTime = 0L;

    public long updateNumber = 0L;
    
    public OrderBook()
    {

    }
    
    public void updateBook(ArrayList<Order> buyList, ArrayList<Order> sellList)
    {
        {
            if (buyList.size() > 0)
            {
                TDoubleObjectHashMap<OrderBookEntry> bidsByPrice = new TDoubleObjectHashMap<>();

                for (Order order : buyList)
                {
                    OrderBookEntry entry = bidsByPrice.get(order.price);
                    if (entry == null)
                    {
                        entry = new OrderBookEntry();
                        entry.price = order.price;

                        bidsByPrice.put(order.price, entry);
                    }

                    entry.quantity += order.getRemainingQuantity();

                    entry.orderCount++;
                }

                OrderBookEntry[] bidsArr = bidsByPrice.values(new OrderBookEntry[bidsByPrice.size()]);
                if (bidsArr.length > 1)
                    Arrays.sort(bidsArr, (o1, o2) -> -1 * Double.compare(o1.price, o2.price));

                bids = new ArrayList<>(bidsArr.length);
                for (OrderBookEntry entry : bidsArr)
                    bids.add(entry);

            }
            else
            {
                bids = null;
            }
        }

        {
            if (sellList.size() > 0)
            {
                TDoubleObjectHashMap<OrderBookEntry> asksByPrice = new TDoubleObjectHashMap<>();

                for (Order order : sellList)
                {
                    OrderBookEntry entry = asksByPrice.get(order.price);
                    if (entry == null)
                    {
                        entry = new OrderBookEntry();
                        entry.price = order.price;

                        asksByPrice.put(order.price, entry);
                    }

                    entry.quantity += order.getRemainingQuantity();

                    entry.orderCount++;
                }
                
                OrderBookEntry[] asksArr = asksByPrice.values(new OrderBookEntry[asksByPrice.size()]);
                if (asksArr.length > 1)
                    Arrays.sort(asksArr, (o1, o2) -> Double.compare(o1.price, o2.price));
                
                asks = new ArrayList<>(asksArr.length);
                for (OrderBookEntry entry : asksArr)
                    asks.add(entry);
            }
            else
            {
                asks = null;
            }
        }

    }
    
    public int bidsSize()
    {
        return (bids == null ? 0 : bids.size());
    }

    public int asksSize()
    {
        return (asks == null ? 0 : asks.size());
    }
    
    
    public int getLevels()
    {
        return Math.max(bidsSize(), asksSize());
    }

    
    public OrderBookEntry getTopBid()
    {
        if (bids != null && bids.size() > 0)
        {
            return bids.get(0);
        }
        else
        {
            return null;
        }
    }
    
    public OrderBookEntry getTopAsk()
    {
        if (asks != null && asks.size() > 0)
        {
            return asks.get(0);
        }
        else
        {
            return null;
        }
    }
}
