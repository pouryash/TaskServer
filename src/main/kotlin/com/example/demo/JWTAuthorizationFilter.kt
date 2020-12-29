package com.example.demo

import javax.servlet.http.HttpServletResponse

import javax.servlet.http.HttpServletRequest

import org.springframework.security.core.context.SecurityContextHolder

import java.util.stream.Collectors

import org.springframework.security.core.authority.SimpleGrantedAuthority

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken

import io.jsonwebtoken.Claims

import io.jsonwebtoken.Jwts

import io.jsonwebtoken.MalformedJwtException

import io.jsonwebtoken.UnsupportedJwtException

import io.jsonwebtoken.ExpiredJwtException
import io.jsonwebtoken.io.IOException
import io.jsonwebtoken.security.SignatureException

import javax.servlet.ServletException

import javax.servlet.FilterChain

import org.springframework.web.filter.OncePerRequestFilter


class JWTAuthorizationFilter : OncePerRequestFilter() {
    private val HEADER = "Authorization"
    private val PREFIX = "Bearer "
    private val SECRET = "mySecretKeyForJWTUserTaskApplicationthisisfortestinjwtddddddddddddd"

    @Throws(ServletException::class, IOException::class)
    override fun doFilterInternal(request: HttpServletRequest, response: HttpServletResponse, chain: FilterChain) {
        try {
            if (checkJWTToken(request, response)) {
                val claims = validateToken(request)
                if (claims["authorities"] != null) {
                    setUpSpringAuthentication(claims)
                } else {
                    SecurityContextHolder.clearContext()
                }
            } else {
                SecurityContextHolder.clearContext()
            }
            chain.doFilter(request, response)
        } catch (e: ExpiredJwtException) {
            response.status = HttpServletResponse.SC_FORBIDDEN
            response.sendError(HttpServletResponse.SC_FORBIDDEN, e.message)
            return
        } catch (e: UnsupportedJwtException) {
            response.status = HttpServletResponse.SC_FORBIDDEN
            response.sendError(HttpServletResponse.SC_FORBIDDEN, e.message)
            return
        } catch (e: MalformedJwtException) {
            response.status = HttpServletResponse.SC_FORBIDDEN
            response.sendError(HttpServletResponse.SC_FORBIDDEN, e.message)
            return
        }catch (e: SignatureException){
            response.status = HttpServletResponse.SC_UNAUTHORIZED
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, e.message)
            return
        }
    }

    private fun validateToken(request: HttpServletRequest): Claims {
        val jwtToken = request.getHeader(HEADER).replace(PREFIX, "")
        return Jwts.parser().setSigningKey(SECRET.toByteArray()).parseClaimsJws(jwtToken).body
    }

    /**
     * Authentication method in Spring flow
     *
     * @param claims
     */
    private fun setUpSpringAuthentication(claims: Claims) {
        val authorities: List<String> = claims["authorities"] as List<String>
        val auth = UsernamePasswordAuthenticationToken(claims.subject, null,
            authorities.stream().map { role: String? ->
                SimpleGrantedAuthority(
                    role
                )
            }.collect(Collectors.toList())
        )
        SecurityContextHolder.getContext().authentication = auth
    }

    private fun checkJWTToken(request: HttpServletRequest, res: HttpServletResponse): Boolean {
        val authenticationHeader = request.getHeader(HEADER)
        return if (authenticationHeader == null || !authenticationHeader.startsWith(PREFIX)) false else true
    }
}