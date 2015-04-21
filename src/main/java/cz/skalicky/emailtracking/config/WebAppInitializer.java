package cz.skalicky.emailtracking.config;

import javax.servlet.ServletContext;
import javax.servlet.ServletRegistration;

import org.springframework.web.WebApplicationInitializer;
import org.springframework.web.context.ContextLoaderListener;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.springframework.web.servlet.DispatcherServlet;
import org.springframework.web.util.Log4jConfigListener;

public class WebAppInitializer implements WebApplicationInitializer {

    @Override
    public void onStartup(final ServletContext servletContext) {

        AnnotationConfigWebApplicationContext rootContext = new AnnotationConfigWebApplicationContext();
        rootContext.register(AppConfig.class);

        servletContext.setInitParameter("webAppRootKey", "spring_with_email_tracking.root");

        servletContext.setInitParameter("log4jConfigLocation", "classpath:log4j-emailTracking.xml");

        servletContext.addListener(new Log4jConfigListener());

        servletContext.addListener(new ContextLoaderListener(rootContext));

        addDispatcher(servletContext);
    }

    private void addDispatcher(final ServletContext servletContext) {

        AnnotationConfigWebApplicationContext webContext = new AnnotationConfigWebApplicationContext();
        webContext.register(MvcConfig.class);

        DispatcherServlet dispatcherServlet = new DispatcherServlet(webContext);
        ServletRegistration.Dynamic dispatcher = servletContext.addServlet("emailTrackingDispatcher",
                dispatcherServlet);
        dispatcher.addMapping("/");
    }

}
