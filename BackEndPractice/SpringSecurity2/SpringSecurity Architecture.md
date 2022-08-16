# SpringSecurity Architecture

중요하다. 스프링 세큐리티를 이해하기 위해.

스프링 세큐리티는 인증-인가가 가장 중요한 과정이다.

인증 : Authentication Manager에 의해서 이루어지고

인가 : Access Decision Manager에 의해 이루어진다. 

## FilterChainProxy

Spring Security 는 서블릿 필터를 통해 이루어지는데, 우리가 이전에 작성했던 코드

```java
@Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .authorizeRequests()
                .antMatchers("/me").hasAnyRole("USER", "ADMIN")
                .anyRequest().permitAll()
                .and()
                .formLogin()
                .defaultSuccessUrl("/")
                .permitAll()
                .and()
                .logout()
                .logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
                .logoutSuccessUrl("/")
                .invalidateHttpSession(true)
                .clearAuthentication(true)
                .and()
                .rememberMe()
                .rememberMeParameter("remember-me")
                .tokenValiditySeconds(300)
        ;
    }
```

이부분이 다 필터라고 보면된다. 

웹 요청을 가로챈 후 전-후처리를 수행하거나, 요청으로 리다이렉트를 시켜주는것들.

웹 요청은 이 필터를 차례로 통과하게 된다. (모든필터가 동작하는건 아님.)

요청을 처리하고 응답을 반환하면, 필터체인 호출스택은 모든필터에 대해 역순으로 진행!

어떻게 Filter Chain Proxy로 전달을 할까??

웹 요청이 일어나면, 서블릿 컨테이너는 해당 요청을 Delegating Filter Proxy로 전달을 하는데 이 delegating Filter Proxy가 요청을 Filter Chain Proxy로 전달을 하기 때문에 필터체인으로 요청이 전달되는것!