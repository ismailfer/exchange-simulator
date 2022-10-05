package com.ismail.exchsim;

import java.util.ArrayList;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import com.ismail.exchsim.service.Exchange;
import com.ismail.exchsim.service.Order;
import com.ismail.exchsim.service.Trade;

@SpringBootTest
public class ExchangeSimulatorTest
{

    @Test
    public void test1()
    {

        Exchange exch = new Exchange(null);

        exch.addOrder(new Order("A", "FX", "AUDUSD", 100, 1.47));
        exch.addOrder(new Order("B", "FX", "AUDUSD", -50, 1.45));

        ArrayList<Trade> trades = exch.getAllTrades();

        // there must be 1 trade
        Assertions.assertTrue(trades.size() == 1);

        for (Trade trade : trades)
        {
            Assertions.assertTrue(trade.buyClientID.equals("A") && trade.sellClientID.equals("B") && trade.quantity == 50 && trade.price == 1.47);
        }
    }

    @Test
    public void test2()
    {
        Exchange exch = new Exchange(null);

        exch.addOrder(new Order("A", "FX", "GBPUSD", 100, 1.66));
        exch.addOrder(new Order("B", "FX", "EURUSD", -100, 1.11));
        exch.addOrder(new Order("F", "FX", "EURUSD", -50, 1.1));
        exch.addOrder(new Order("C", "FX", "GBPUSD", -10, 1.5));
        exch.addOrder(new Order("C", "FX", "GBPUSD", -20, 1.6));
        exch.addOrder(new Order("C", "FX", "GBPUSD", -20, 1.7));
        exch.addOrder(new Order("D", "FX", "EURUSD", 100, 1.11));

        ArrayList<Trade> trades = exch.getAllTrades();

        Assertions.assertTrue(trades.size() == 4);

        for (int i = 0; i < trades.size(); i++)
        {
            Trade trade = trades.get(i);

            if (i == 0)
            {
                Assertions.assertTrue(trade.buyClientID.equals("A") && trade.sellClientID.equals("C") && trade.quantity == 10 && trade.price == 1.66);
            }
            else if (i == 1)
            {
                Assertions.assertTrue(trade.buyClientID.equals("A") && trade.sellClientID.equals("C") && trade.quantity == 20 && trade.price == 1.66);
            }
            else if (i == 2)
            {
                Assertions.assertTrue(trade.buyClientID.equals("D") && trade.sellClientID.equals("F") && trade.quantity == 50 && trade.price == 1.1);
            }
            else if (i == 3)
            {
                Assertions.assertTrue(trade.buyClientID.equals("D") && trade.sellClientID.equals("B") && trade.quantity == 50 && trade.price == 1.11);
            }

        }
    }

}
