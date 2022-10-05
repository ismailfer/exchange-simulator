package com.ismail.exchsim.config;

import java.util.concurrent.TimeUnit;

import org.springframework.context.annotation.Configuration;
import org.springframework.http.CacheControl;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * @author ismail
 * @since 20221001
 */
@Configuration
@EnableWebMvc
public class WebConfig implements WebMvcConfigurer
{
    /*
    @Bean
    public ViewResolver jspViewResolver()
    {
        InternalResourceViewResolver viewResolver = new InternalResourceViewResolver();
        viewResolver.setPrefix("/webapp/");
        viewResolver.setSuffix(".jsp");
        return viewResolver;
    }
    */

    @Override
    public void addViewControllers(ViewControllerRegistry registry)
    {
        
        
        registry.addRedirectViewController("/", "index.jsp");
        //registry.addRedirectViewController("/**", "index.jsp");

        registry.addRedirectViewController("/error", "error.jsp");

        //registry.addViewController("/images").setViewName("/images");

        registry.addViewController("/error").setViewName("forward:/error.jsp");

        //registry.addViewController("/**").setViewName("forward:index.jsp");

    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry)
    {
        // TODO Auto-generated method stub
        WebMvcConfigurer.super.addResourceHandlers(registry);

        registry.addResourceHandler("/css/**").addResourceLocations("/css/");
        registry.addResourceHandler("/js/**").addResourceLocations("/js/");

        // set resource map (without cache)
        //registry.addResourceHandler("/images/**").addResourceLocations("/images/");

        // set resource map with cache
        registry.addResourceHandler("/images/**") //
        .addResourceLocations("/images/") //
        .setCacheControl(CacheControl.maxAge(2, TimeUnit.HOURS).cachePublic());

    }
    
    
    

}
