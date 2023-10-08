package dev.botyanovsky.playground.security

import com.fasterxml.jackson.databind.ObjectMapper
import dev.botyanovsky.playground.security.details.UserDetailsImpl
import dev.botyanovsky.playground.security.entity.AccountEntity
import dev.botyanovsky.playground.security.entity.AccountSocialLinkEntity
import dev.botyanovsky.playground.security.repository.AccountRepository
import dev.botyanovsky.playground.security.repository.AccountSocialLinkRepository
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken
import org.springframework.security.web.authentication.AuthenticationSuccessHandler
import org.springframework.stereotype.Component
import org.springframework.transaction.support.TransactionTemplate
import org.springframework.util.MimeTypeUtils

@Component
class OAuthSuccessHandler(
    val transactionTemplate: TransactionTemplate,
    val accountRepository: AccountRepository,
    val accountSocialLinkRepository: AccountSocialLinkRepository,
    val jwtResponseBuilder: JwtResponseBuilder,
    val objectMapper: ObjectMapper
) : AuthenticationSuccessHandler {

    // todo how flow works from browser?
    // shouldn't return token directly?
    override fun onAuthenticationSuccess(
        request: HttpServletRequest,
        response: HttpServletResponse,
        authentication: Authentication
    ) {
        if (authentication !is OAuth2AuthenticationToken) {
            response.status = HttpStatus.BAD_REQUEST.value()
        }

        val oauth2authentication = authentication as OAuth2AuthenticationToken

        val jwtResponse = transactionTemplate.execute {
            val authentication = when (oauth2authentication.authorizedClientRegistrationId) {
                AccountSocialLinkEntity.socialTypeGithub -> convertGithub(authentication)
                else -> {
                    response.status = HttpStatus.BAD_REQUEST.value()
                    throw UsernameNotFoundException("Failed to authorize github: ${authentication.name}")
                }
            }

            SecurityContextHolder.getContext().authentication = authentication

            return@execute jwtResponseBuilder.build(authentication)
        }

        response.setHeader(HttpHeaders.CONTENT_TYPE, MimeTypeUtils.APPLICATION_JSON_VALUE)
        response.writer.write(objectMapper.writeValueAsString(jwtResponse))
    }

    fun convertGithub(authenticationToken: OAuth2AuthenticationToken): Authentication {
        val githubId = authenticationToken.name

        val accountSocialLink = accountSocialLinkRepository
            .findAccountBySocialTypeAndSocialId(AccountSocialLinkEntity.socialTypeGithub, githubId)
            ?: return createGithub(authenticationToken)

        val principal = accountSocialLink.account?.let { UserDetailsImpl.build(it) }
        return UsernamePasswordAuthenticationToken(
            principal, null, mutableListOf()
        )
    }

    fun createGithub(authenticationToken: OAuth2AuthenticationToken): Authentication {
        val githubId = authenticationToken.name

        val account = AccountEntity()
        account.username = AccountSocialLinkEntity.socialTypeGithub + '-' + githubId
        accountRepository.save(account)
        val socialLink = AccountSocialLinkEntity()
        socialLink.socialType = AccountSocialLinkEntity.socialTypeGithub
        socialLink.socialId = githubId
        socialLink.account = account
        accountSocialLinkRepository.save(socialLink)

        val principal = UserDetailsImpl.build(account)

        return UsernamePasswordAuthenticationToken(
            principal, null, mutableListOf()
        )
    }

}