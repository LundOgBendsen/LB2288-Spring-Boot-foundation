package dk.lundogbendsen.api;

import dk.lundogbendsen.api.controller.SecurityFilter;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FilterConfig {

    @Bean
    public FilterRegistrationBean<SecurityFilter> registrationBean() {
        final FilterRegistrationBean<SecurityFilter> securityFilterFilterRegistrationBean = new FilterRegistrationBean<>(new SecurityFilter());
        securityFilterFilterRegistrationBean.addUrlPatterns("/person/*", "/admin/");
        return securityFilterFilterRegistrationBean;
    }
}
