package ru.fedrbodr.exchangearbitr.config;

import com.google.common.collect.ImmutableMap;
import com.sun.faces.config.ConfigureListener;
import org.springframework.beans.factory.config.CustomScopeConfigurer;
import org.springframework.boot.web.servlet.ServletListenerRegistrationBean;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.boot.web.support.SpringBootServletInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.ServletContextAware;
import ru.fedrbodr.exchangearbitr.view.scope.ViewScope;

import javax.faces.webapp.FacesServlet;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;

@Configuration
public class FacesConfiguration extends SpringBootServletInitializer implements ServletContextAware {

	@Override
	public void onStartup(ServletContext servletContext) throws ServletException {
		super.onStartup(servletContext);
		servletContext.setInitParameter("primefaces.THEME", "start");
		servletContext.setInitParameter("primefaces.CLIENT_SIDE_VALIDATION", "true");
		servletContext.setInitParameter("javax.faces.PROJECT_STAGE", "Development");
		/*servletContext.setInitParameter("javax.faces.DATETIMECONVERTER_DEFAULT_TIMEZONE_IS_SYSTEM_TIMEZONE", "true");*/
	}

	@Override
	public void setServletContext(ServletContext servletContext) {
		servletContext.setInitParameter("com.sun.faces.forceLoadConfiguration", Boolean.TRUE.toString());
		servletContext.setInitParameter("javax.faces.FACELETS_SKIP_COMMENTS", "true");
		servletContext.setInitParameter("javax.faces.FACELETS_LIBRARIES", "/WEB-INF/springsecurity.taglib.xml");
	}
	@Bean
	public static CustomScopeConfigurer viewScope() {
		CustomScopeConfigurer configurer = new CustomScopeConfigurer();
		configurer.setScopes(
				new ImmutableMap.Builder<String, Object>().put("view", new ViewScope()).build());
		return configurer;
	}
	@Bean
	public ServletRegistrationBean facesServletRegistrationBean() {
		ServletRegistrationBean registration = new ServletRegistrationBean();
		registration.setServlet(new FacesServlet());
		registration.addUrlMappings("*.xhtml", "*.jsf");
		registration.setLoadOnStartup(1);
		registration.setName("facesServlet");
		return registration;
	}

	@Bean
	public ServletListenerRegistrationBean<ConfigureListener> jsfConfigureListener() {
		return new ServletListenerRegistrationBean<ConfigureListener>(new ConfigureListener());
	}
}
