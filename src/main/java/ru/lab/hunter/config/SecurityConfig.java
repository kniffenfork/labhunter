package ru.lab.hunter.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import ru.lab.hunter.security.Permission;
import ru.lab.hunter.security.jwt.JwtConfigurer;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private final JwtConfigurer jwtConfigurer;

    public SecurityConfig(JwtConfigurer jwtConfigurer) {
        this.jwtConfigurer = jwtConfigurer;
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .csrf().disable()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .authorizeRequests()
                .antMatchers("/").permitAll()
                .antMatchers("/actuator/*").permitAll()
                .antMatchers("/api/auth/login").anonymous()
                .antMatchers("/api/auth/logout").authenticated()
                .antMatchers("/api/auth/registration/employee").anonymous()
                .antMatchers("/api/auth/registration/employer").anonymous()
                .antMatchers("/api/auth/registration/admin").hasAnyAuthority(Permission.DEVELOPERS_WRITE.getPermission())
                .antMatchers("api/employee/vacancies").hasAnyAuthority(Permission.EMPLOYEE_READ.getPermission())
                .antMatchers("/api/employee/cv").hasAuthority(Permission.EMPLOYEE_READ.getPermission())
                .antMatchers("/api/admin/*").hasAuthority(Permission.DEVELOPERS_WRITE.getPermission())
                .antMatchers("/api/employer/*").hasAuthority(Permission.EMPLOYER_WRITE.getPermission())
                .anyRequest()
                .authenticated()
                .and()
                .apply(jwtConfigurer);
    }

    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(12);
    }
}
