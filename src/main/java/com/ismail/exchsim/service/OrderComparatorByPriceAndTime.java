package com.ismail.exchsim.service;

import java.util.Comparator;

/**
 * Compares two orders of the same instrument; by Price (ascending or descending) then by time priority (ascending only)
 * 
 * @author ismail
 * @since 20221001
 */
public class OrderComparatorByPriceAndTime implements Comparator<Order>
{
    private boolean priceAscending;
    
    public OrderComparatorByPriceAndTime(boolean priceAscending)
    {
        this.priceAscending = priceAscending;
    }
    
    @Override
    public int compare(Order o1, Order o2)
    {
        int comp = Double.compare(o1.price, o2.price);
        if (priceAscending == false)
            comp = -comp;
        
        // OrderId is already sorted by first received
        if (comp == 0)
            comp = Long.compare(o1.orderID, o2.orderID);
        
        return comp;
    }
}
