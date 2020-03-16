package cc.oldx.config;

import cc.oldx.modules.security.fliter.JwtAuthenticationTokenFilter;
import cc.oldx.modules.security.handler.OldxAuthenticationAccessDeniedHandler;
import cc.oldx.modules.security.handler.OldxAuthenticationEntryPoint;
import cc.oldx.modules.security.service.OSysUserDetailsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * @Author: moli
 * @Email: 974751082@qq.com
 * @qq: 974751082
 * @Date: 2020/3/16 18:56
 * SecurityConfig配置类
 */
@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private Logger log = LoggerFactory.getLogger(this.getClass());
    @Autowired
    private OldxAuthenticationAccessDeniedHandler oldxAccessDeniedHandler;
    @Autowired
    private OldxAuthenticationEntryPoint oldxAuthenticationEntryPoint;
    @Autowired
    private OSysUserDetailsService oSysUserDetailsService;

    @Override
    protected void configure(HttpSecurity httpSecurity) throws Exception {
        httpSecurity.csrf()
                .disable()
                .sessionManagement()
                //禁止创建session
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                //处理用户逻辑
                .userDetailsService(oSysUserDetailsService)
                .authorizeRequests()
                .antMatchers(HttpMethod.GET,
                        "/",
                        "/*.html",
                        "/favicon.ico",
                        "/**/*.html",
                        "/**/*.css",
                        "/**/*.js",
                        "/swagger-resources/**"
                ).permitAll()
                //登录注册
                .antMatchers("/auth/login", "auth/register")
                .permitAll()
                //跨域前先进行options
                .antMatchers(HttpMethod.OPTIONS)
                .permitAll()
               /* //测试时全部运行访问
                .antMatchers("/**")
                .permitAll()*/
                .anyRequest()// 除上面外的所有请求全部需要鉴权认证
                .authenticated();
        // 禁用缓存
        httpSecurity.headers().cacheControl();
        //添加JWT过滤器
        httpSecurity.addFilterBefore(jwtAuthenticationTokenFilter(), UsernamePasswordAuthenticationFilter.class);
        //添加拦截器
        httpSecurity.exceptionHandling()
                .accessDeniedHandler(oldxAccessDeniedHandler)
                .authenticationEntryPoint(oldxAuthenticationEntryPoint);
    }


    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public JwtAuthenticationTokenFilter jwtAuthenticationTokenFilter() {
        return new JwtAuthenticationTokenFilter();
    }
    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }
}
