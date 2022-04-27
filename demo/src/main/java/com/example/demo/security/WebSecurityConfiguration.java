package com.example.demo.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.logout.HeaderWriterLogoutHandler;
import org.springframework.security.web.header.writers.ClearSiteDataHeaderWriter;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.authentication.logout.DelegatingServerLogoutHandler;
import org.springframework.security.web.server.authentication.logout.SecurityContextServerLogoutHandler;
import org.springframework.security.web.server.authentication.logout.WebSessionServerLogoutHandler;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
@EnableWebSecurity
public class WebSecurityConfiguration extends WebSecurityConfigurerAdapter {
    @Autowired
    private UserDetailsService userDetailsService;

    @Bean
    AuthenticationProvider authenticationProvider(){
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userDetailsService);
        provider.setPasswordEncoder(new BCryptPasswordEncoder());
        return provider;
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception{
        http.csrf().disable();

        http.authorizeRequests()
                .antMatchers("/vaadinServlet/**")
                .permitAll()
                .antMatchers("/vaadinServlet/UIDL/**")
                .permitAll()
                .antMatchers("/vaadinServlet/HEARTBEAT/**")
                .permitAll()
                .antMatchers("/VAADIN/**")
                .permitAll()
                .antMatchers("/")
                .permitAll()
                .antMatchers("/client")
                .hasAuthority("USER")
                .antMatchers("/admin")
                .hasAuthority("ADMIN")
                .antMatchers("/addClient")
                .hasAuthority("ADMIN")
                .antMatchers("/addDocuments")
                .hasAuthority("USER")
                .antMatchers("/home")
                .hasAuthority("USER")
                .antMatchers("/DashboardClient")
                .hasAuthority("USER")
                .antMatchers("/DashboardAdmin")
                .hasAuthority("ADMIN")
                .antMatchers("/order")
                .hasAuthority("ADMIN")
                .antMatchers("/Notifications")
                .hasAuthority("ADMIN")
                .anyRequest()
                .authenticated()
                .and()
                .httpBasic()
        ;
        http
              .logout().clearAuthentication(true).logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
              .logoutSuccessUrl("/")
              .deleteCookies("JSESSIONID","remember-me","auth_code")
              .invalidateHttpSession(true)
              .addLogoutHandler(new HeaderWriterLogoutHandler(
                            new ClearSiteDataHeaderWriter(
                                ClearSiteDataHeaderWriter.Directive.CACHE,
                                ClearSiteDataHeaderWriter.Directive.COOKIES,
                                ClearSiteDataHeaderWriter.Directive.STORAGE,
                                ClearSiteDataHeaderWriter.Directive.EXECUTION_CONTEXTS)));
    }

}
