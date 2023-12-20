package melowave.config;

import lombok.RequiredArgsConstructor;
import melowave.filter.CustomAuthenticationFilter;
import melowave.filter.CustomAuthorizationFilter;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import static org.springframework.security.config.http.SessionCreationPolicy.*;

import org.springframework.beans.factory.annotation.Value;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
@RequiredArgsConstructor
public class SecurityConfig extends WebSecurityConfigurerAdapter {
    
    @Value("${api.base-path}")
    private String apiBasePath;

    private final UserDetailsService userDetailsService;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService).passwordEncoder(bCryptPasswordEncoder);
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.cors().and().csrf().disable();
        http.sessionManagement().sessionCreationPolicy(STATELESS);
        /*http.authorizeRequests().antMatchers(apiBasePath + "/login").permitAll();
        http.authorizeRequests().antMatchers(apiBasePath + "/user/create").permitAll();
        http.authorizeRequests().antMatchers(apiBasePath + "/song/find/*").permitAll();
        http.authorizeRequests().antMatchers(apiBasePath + "/song/search").permitAll();
        http.authorizeRequests().antMatchers(apiBasePath + "/song/search/count").permitAll();
        http.authorizeRequests().antMatchers(apiBasePath + "/song/stream/*").permitAll();
        http.authorizeRequests().antMatchers(apiBasePath + "/song/artists").permitAll();
        http.authorizeRequests().antMatchers(apiBasePath + "/song/genres").permitAll();
        http.authorizeRequests().antMatchers(apiBasePath + "/song/status").permitAll();
        http.authorizeRequests()
        .antMatchers("/swagger-ui.html", "/v2/api-docs", "/webjars/**", "/swagger-resources/**").permitAll();
        http.authorizeRequests().anyRequest().authenticated();
        http.addFilter(customAuthenticationFilter());
        http.addFilterBefore(new CustomAuthorizationFilter(), UsernamePasswordAuthenticationFilter.class);*/
        http.httpBasic();
    }

    private CustomAuthenticationFilter customAuthenticationFilter() throws Exception {
        CustomAuthenticationFilter filter = new CustomAuthenticationFilter(authenticationManagerBean());
        filter.setFilterProcessesUrl("/api/v1/login");
        return filter;
    }

    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }
}
