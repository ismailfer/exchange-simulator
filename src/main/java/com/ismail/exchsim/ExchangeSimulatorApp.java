package com.ismail.exchsim;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

import com.ismail.exchsim.config.ColorThemeConfig;
import com.ismail.exchsim.config.ExchangeSimulatorConfig;

import lombok.extern.slf4j.Slf4j;


/**
 * Main Spring Boot App
 * 
 * @author ismail
 * @since 20221001
 */
@SpringBootApplication(scanBasePackages = "com.ismail.exchsim")
@EnableConfigurationProperties({ ExchangeSimulatorConfig.class, ColorThemeConfig.class })
@Slf4j
public class ExchangeSimulatorApp extends SpringBootServletInitializer
{

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder builder)
    {
        return builder.sources(ExchangeSimulatorApp.class);
    }

    public static void main(String[] args)
    {
        SpringApplication.run(ExchangeSimulatorApp.class, args);
    }

}
