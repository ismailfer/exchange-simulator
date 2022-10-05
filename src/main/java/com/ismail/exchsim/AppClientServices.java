package com.ismail.exchsim;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ismail.exchsim.config.ColorThemeConfig;
import com.ismail.exchsim.config.ExchangeSimulatorConfig;
import com.ismail.exchsim.service.ExchangeSimulatorService;

/**
 * AppClient Service Lookup; for jsp pages
 * 
 * We can't inject dependencies in JSP pages; so resorting to this trick
 * 
 * @author ismail
 * @since 20221001
 */
@Service
public class AppClientServices
{
    private static AppClientServices sMe;

    @Autowired
    public ColorThemeConfig colorTheme;
    
    @Autowired
    public ExchangeSimulatorConfig config;
    
    @Autowired
    public ExchangeSimulatorService apiService;
    
    
    public AppClientServices()
    {
        sMe = this;
    }
    
    public static AppClientServices getInstance()
    {
        return sMe;
    }
    

}
