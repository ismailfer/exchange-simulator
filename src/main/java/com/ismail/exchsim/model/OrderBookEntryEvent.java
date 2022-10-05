package com.ismail.exchsim.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * Order book entry
 * 
 * @author ismail
 * @since 20221001
 */
@Data
@ToString
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class OrderBookEntryEvent
{
    public double quantity = 0.0;

    public double price = 0.0;
    
    public int orderCount = 0;

}
