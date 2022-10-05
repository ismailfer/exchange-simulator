package com.ismail.exchsim.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Data;

/**
 * Configuration
 * 
 * @author ismail
 * @since 20221001
 */
@Data
@ConfigurationProperties("exch")
public class ExchangeSimulatorConfig
{
    public int wsPort;


}
