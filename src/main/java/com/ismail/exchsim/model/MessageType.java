package com.ismail.exchsim.model;

/**
 * Rest message types
 * 
 * @author ismail
 * @since 20221001
 */
public class MessageType
{
    public static final String Request = "Request";

    public static final String Response = "Response";

    public static final String Error = "Error";

    public static final String Ping = "Ping";

    public static final String Pong = "Pong";

    public static final String Subscribed = "Subscribed";

    public static final String Subscription = "Subscription";

    public static final String NewOrderRequest = "Order";

    public static final String OrderEvent = "OrderAck";

    public static final String OrderReject = "OrderReject";

    public static final String CancelAccept = "CanelAccept";

    public static final String CancelReject = "CancelReject";

    public static final String Order = "Order";
    
    public static final String Trade = "Trade";
    
    public static final String TopOfBook = "TopOfBook";
    
    public static final String OrderBook = "OrderBook";
    
}
