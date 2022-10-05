package com.ismail.exchsim.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ismail.exchsim.model.MessageType;
import com.ismail.exchsim.model.NewOrderRequest;
import com.ismail.exchsim.model.OrderEvent;
import com.ismail.exchsim.model.OrderStatus;
import com.ismail.exchsim.model.ResponseMessage;
import com.ismail.exchsim.service.ExchangeSimulatorService;

/**
 * @author ismail
 * @since 20221001
 */
@RestController
public class OrderController
{
    @Autowired
    private ExchangeSimulatorService exchSimService;

    @PostMapping({ "/api/v1/order" })
    public ResponseMessage submitNewOrder(@RequestBody NewOrderRequest request)
    {
        ResponseMessage resp = new ResponseMessage(MessageType.Response, true);
        
        try
        {
            exchSimService.submitNewOrder(request);

           // resp.orderID = order.orderId;
           // resp.success = true;
           // resp.time = order.time;
        }
        catch (IllegalArgumentException ie)
        {            
            resp.success = false;
            resp.errorMessage = ie.getMessage();
        }
        catch (Exception e)
        {
            resp.success = false;
            resp.errorMessage = "Error processing request";
        }

        return resp;

    }

}
