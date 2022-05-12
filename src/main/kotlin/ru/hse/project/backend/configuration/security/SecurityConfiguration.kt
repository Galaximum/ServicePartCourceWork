package ru.hse.project.backend.configuration.security

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.oauth2.client.web.OAuth2LoginAuthenticationFilter
import ru.hse.project.backend.configuration.security.jwt.JwtEntryPoint
import ru.hse.project.backend.configuration.security.jwt.JwtFilter
import ru.hse.project.backend.configuration.security.oauth.OAuthSuccessHandler
import ru.hse.project.backend.service.CustomOAuth2UserService
import ru.hse.project.backend.service.CustomUserDetailsService

@Configuration
@EnableWebSecurity
class SecurityConfiguration(
    private val jwtEntryPoint: JwtEntryPoint,
    private val jwtFilter: JwtFilter,
    private val oAuthSuccessHandler: OAuthSuccessHandler,
    private val oAuth2UserService: CustomOAuth2UserService,
    private val userDetailsService: CustomUserDetailsService
) : WebSecurityConfigurerAdapter() {

    override fun configure(http: HttpSecurity) {
        http
            .cors().disable().csrf().disable()
            .exceptionHandling().authenticationEntryPoint(jwtEntryPoint)
            .and()
            .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            .and()
            .authorizeRequests()
            .antMatchers(*closeEndpoints).authenticated()
            //.antMatchers(*openEndpoints).permitAll()
            .anyRequest()
            .permitAll()
            //.authenticated()
            .and()
            .oauth2Login { oauth2Login ->
                oauth2Login
                    .userInfoEndpoint { it.userService(oAuth2UserService) }
                    .successHandler(oAuthSuccessHandler)
            }

        http.addFilterBefore(jwtFilter, OAuth2LoginAuthenticationFilter::class.java)
    }

    override fun configure(auth: AuthenticationManagerBuilder) {
        auth.userDetailsService(userDetailsService)
    }

    @Bean
    override fun authenticationManagerBean(): AuthenticationManager = super.authenticationManagerBean()

    companion object {
        val closeEndpoints = arrayOf(
            "/trashcans/**",
            "/users/**"
        )
    }
}