package dev.botyanovsky.playground.security.details

import com.fasterxml.jackson.annotation.JsonIgnore
import dev.botyanovsky.playground.security.entity.AccountEntity
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.userdetails.UserDetails

class UserDetailsImpl(
    @JvmField
    val id: Long,
    @JvmField
    val username: String,
    @JvmField
    val email: String?,
    @JvmField
    @field:JsonIgnore val password: String?,
    @JvmField
    val authorities: Collection<GrantedAuthority>
) : UserDetails {

    override fun getAuthorities(): Collection<GrantedAuthority> {
        return authorities
    }

    override fun getPassword(): String? {
        return password
    }

    override fun getUsername(): String {
        return username
    }

    override fun isAccountNonExpired(): Boolean {
        return true
    }

    override fun isAccountNonLocked(): Boolean {
        return true
    }

    override fun isCredentialsNonExpired(): Boolean {
        return true
    }

    override fun isEnabled(): Boolean {
        return true
    }

    companion object {
        private const val serialVersionUID = 1L
        fun build(account: AccountEntity): UserDetailsImpl {
            return UserDetailsImpl(
                account.id!!,
                account.username!!,
                account.email,
                account.passwordHash,
                mutableListOf()
            )
        }
    }
}