package com.ismail.exchsim.web;

/**
 * Represents a web page info
 * 
 * stuff that needs to be communicated between jsp includes
 * 
 * @author ismail
 * @since 20221001
 */
public class PageInfo
{
    public static final String PAGE_INFO_KEY = "pageInfo";
    
    public String appTitle = null;
    
    public String pageTitle = null;
    
    public String successMsg = null;
    
    public String errorMsg = null;
    
    public String errorDetail = null;
    
    public Throwable error = null;
    
    public void setError(Throwable t)
    {
        errorMsg = t.getMessage();
        error = t;
    }
}
