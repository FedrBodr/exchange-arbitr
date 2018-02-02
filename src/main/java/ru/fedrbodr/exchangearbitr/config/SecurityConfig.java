package ru.fedrbodr.exchangearbitr.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {
	@Override
	protected void configure(HttpSecurity http) throws Exception {
		// Have to disable it for POST methods:
		// http://stackoverflow.com/a/20608149/1199132
		http.csrf().disable();

		// Logout and redirection:
		// http://stackoverflow.com/a/24987207/1199132
		http.logout()
				.logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
				.invalidateHttpSession(true)
				.logoutSuccessUrl(
						"/index.xhtml");

		http.authorizeRequests().
				antMatchers("/secure/**").access("hasRole('ADMIN')").
				and().formLogin().  //login configuration
				loginPage("/login.xhtml").
				loginProcessingUrl("/appLogin").
				usernameParameter("app_username").
				passwordParameter("app_password").
				defaultSuccessUrl("/secure/admin.xhtml");

	}
	@Autowired
	public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
		auth.inMemoryAuthentication().withUser("123").password("123").roles("ADMIN");
	}
}