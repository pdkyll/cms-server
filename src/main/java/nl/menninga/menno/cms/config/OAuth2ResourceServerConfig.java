package nl.menninga.menno.cms.config;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.oauth2.provider.token.RemoteTokenServices;

@Configuration
@EnableResourceServer
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class OAuth2ResourceServerConfig extends ResourceServerConfigurerAdapter {

	@Value("${spring.security.tokenservice.endpoint:http://localhost:8810/oauth/check_token}")
	private String tokenServiceUrl;
	@Value("${spring.security.tokenservice.clientid:cms}")
	private String clientId;
	@Value("${spring.security.tokenservice.clientsecret:123456789}")
	private String clientSecret;
	
	@Override
	public void configure(HttpSecurity http) throws Exception {
		http.cors()
		.and()
		.csrf()
			.disable()
		.exceptionHandling()
		.authenticationEntryPoint(
				(request, response, authException) -> response.sendError(HttpServletResponse.SC_UNAUTHORIZED)
		)
		.and()
		.authorizeRequests()
			.antMatchers(HttpMethod.GET, "/api/**")
		    	.permitAll()
    		.antMatchers(HttpMethod.POST, "/api/order")
				.permitAll()
		    .antMatchers("/actuator/**","/assets/**")
	        	.permitAll()
			.antMatchers(HttpMethod.GET, "/api/orders/**")
				.hasRole("CMS_ADMIN")
			.antMatchers(HttpMethod.GET, "/api/order/**")
				.hasRole("CMS_ADMIN")
		    .antMatchers(HttpMethod.GET, "/api/cmsobject/checkexistence")
		    	.hasRole("CMS_ADMIN")
	    	.antMatchers(HttpMethod.POST, "/api/**")
    			.hasRole("CMS_ADMIN")
		    .antMatchers(HttpMethod.PUT, "/api/**")
			    .hasAnyRole("CMS_ADMIN")
		    .antMatchers(HttpMethod.DELETE, "/api/**")
			    .hasRole("CMS_ADMIN")
            .antMatchers(HttpMethod.OPTIONS)
                .permitAll()
			.anyRequest()
				.authenticated();
//				.permitAll();
	}
	
	@Bean
	@Primary
    public RemoteTokenServices tokenServices() {
        final RemoteTokenServices tokenService = new RemoteTokenServices();
        tokenService.setCheckTokenEndpointUrl(tokenServiceUrl);
        tokenService.setClientId(clientId);
        tokenService.setClientSecret(clientSecret);
        return tokenService;
    }
}
