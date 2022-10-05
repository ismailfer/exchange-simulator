package com.ismail.exchsim.config;

import java.util.HashMap;

import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.boot.web.servlet.server.Jsp;
import org.springframework.stereotype.Component;

/**
 * https://tomcat.apache.org/tomcat-8.5-doc/jasper-howto.html
 * 
 * This will make JSP recompile each time there is a change in the JSP page
 * 
 * @author ismail
 * @since 20221001
 */
@Component
public class TomcatConfig implements WebServerFactoryCustomizer<TomcatServletWebServerFactory>
{

    @Override
    public void customize(TomcatServletWebServerFactory factory)
    {
        // customize the factory here

        HashMap<String, String> initParams = new HashMap<>();
        initParams.put("development", "true");
        initParams.put("checkInterval", "0");
        //initParams.put("keepgenerated", "false");

        initParams.put("listing", "false");

        Jsp jsp = factory.getJsp();
        jsp.setInitParameters(initParams);

        //factory.setInitParameters(initParams);

        // update the JSP root folder

        // https://www.baeldung.com/spring-boot-jsp
        /* doesn't work; it must be located in src/main/webapp
        try
        {
            URL webAppDir = AlgoClientApp.class.getClassLoader().getResource("/META-INFO/webapp");
            factory.setContextPath("");
            factory.setBaseDirectory(new File(webAppDir.toURI()));
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        */
        // webapp.setResourceBase(webAppDir.toURI().toString());

        //File docRoot = new File(docRooURL.get);
        //factory.setDocumentRoot();
    }
}