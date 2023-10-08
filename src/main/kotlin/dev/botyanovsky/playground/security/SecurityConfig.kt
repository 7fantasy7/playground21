package dev.botyanovsky.playground.security

import dev.botyanovsky.playground.utils.Logger
import org.springframework.boot.CommandLineRunner
import org.springframework.context.ApplicationContext
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpMethod
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.DelegatingPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.oauth2.server.resource.web.BearerTokenAuthenticationEntryPoint
import org.springframework.security.oauth2.server.resource.web.access.BearerTokenAccessDeniedHandler
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.util.matcher.AntPathRequestMatcher
import org.springframework.web.cors.CorsConfiguration
import org.springframework.web.cors.CorsConfigurationSource
import org.springframework.web.cors.UrlBasedCorsConfigurationSource


private val logger = Logger.logger {}

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(securedEnabled = true, jsr250Enabled = true, prePostEnabled = true)
class SecurityConfig(
    val oAuthSuccessHandler: OAuthSuccessHandler
) {

    @Bean
    fun passwordEncoder(): PasswordEncoder {
        val bCryptEncoder = BCryptPasswordEncoder()
        return DelegatingPasswordEncoder("bcrypt", mapOf("bcrypt" to bCryptEncoder))
    }

//    @Bean
//    fun authenticationManager(
//        http: HttpSecurity, passwordEncoder: BCryptPasswordEncoder
//    ): AuthenticationManager? {
//        return http.getSharedObject<AuthenticationManagerBuilder>(AuthenticationManagerBuilder::class.java)
//            .userDetailsService<UserDetailsService>(
//                UserDetailsService { username: String? ->
//                    userRepo
//                        .findByUsername(username)
//                        .orElseThrow { UsernameNotFoundException(format("User: %s, not found", username)) }
//                })
//            .passwordEncoder(passwordEncoder)
//            .and()
//            .build()
//    }

    @Bean
    fun corsConfigurationSource(): CorsConfigurationSource {
        val configuration = CorsConfiguration()
        configuration.addAllowedOriginPattern(CorsConfiguration.ALL)
        configuration.allowedMethods = listOf(CorsConfiguration.ALL)
        configuration.allowedHeaders = listOf(CorsConfiguration.ALL)
        val source = UrlBasedCorsConfigurationSource()
        source.registerCorsConfiguration("/**", configuration)
        return source
    }

    @Bean
    fun filterChain(http: HttpSecurity): SecurityFilterChain {
        http
            .cors {}// { c -> c.disable() } // TODO allow localhost:3000, real domain
            .csrf { c -> c.disable() }

        http
            .sessionManagement { c -> c.sessionCreationPolicy(SessionCreationPolicy.STATELESS) }

        http.exceptionHandling { exceptions ->
            exceptions
                .authenticationEntryPoint(BearerTokenAuthenticationEntryPoint())
                .accessDeniedHandler(BearerTokenAccessDeniedHandler())
        }

        http
            .authorizeHttpRequests {
                it.requestMatchers(
                    AntPathRequestMatcher("/swagger-ui.html"),
                    AntPathRequestMatcher("/swagger-ui/**"),
                    AntPathRequestMatcher("/api-docs/**")
                ).permitAll()
                    .requestMatchers(AntPathRequestMatcher("/api/public/**")).permitAll()
                    // TODO should be more generic cors config
//                    .requestMatchers(AntPathRequestMatcher("/auth/login", HttpMethod.OPTIONS.name())).permitAll()
                    .requestMatchers(AntPathRequestMatcher("/auth/login", HttpMethod.POST.name())).permitAll()
                    .requestMatchers(AntPathRequestMatcher("/auth/register", HttpMethod.POST.name())).permitAll()
                    .requestMatchers(AntPathRequestMatcher("/auth/token/refresh", HttpMethod.POST.name())).permitAll()
                    .requestMatchers(
                        // done by security-oauth, here for explicitness
                        AntPathRequestMatcher("/login/oauth2/**"),
                        AntPathRequestMatcher("/oauth2/**"),
                    ).permitAll()

                    .anyRequest().authenticated()
            }

        http
            .httpBasic { c -> c.disable() }

        http
            .formLogin { c -> c.disable() }

        http
            .oauth2ResourceServer { c -> c.jwt {} }

        http
            .oauth2Login { c -> c.successHandler(oAuthSuccessHandler) }

        return http.build()
    }

    @Bean
    fun authenticationManager(
        authenticationConfiguration: AuthenticationConfiguration
    ): AuthenticationManager {
        return authenticationConfiguration.authenticationManager
    }

//    @Bean
//    fun webSecurityCustomizer(): WebSecurityCustomizer {
//        return WebSecurityCustomizer { web: WebSecurity ->
//            web.ignoring()
//                .requestMatchers("/ignore1", "/ignore2")
//        }
//    }

//    @Bean
//    fun users(dataSource: DataSource?): UserDetailsManager? {
//        val user: UserDetails = User.withDefaultPasswordEncoder()
//            .username("user")
//            .password("password")
//            .roles("USER")
//            .build()
//        val users = JdbcUserDetailsManager(dataSource)
//        users.createUser(user)
//        return users
//    }

//    @Bean
//    fun userDetailsService(passwordEncoder: PasswordEncoder): InMemoryUserDetailsManager {
//        val pass = passwordEncoder.encode("password")
//        val user = User.builder()
//            .username("user")
//            .password(pass)
//            .roles("USER")
//            .build()
//        return InMemoryUserDetailsManager(user)
//    }

    @Bean
    fun clr(ctx: ApplicationContext): CommandLineRunner {
        return CommandLineRunner {
            logger.info((ctx.getBean("passwordEncoder") as PasswordEncoder).encode("324i2djewjdkajd129uedjiasjkd1!"))
        }
    }

}