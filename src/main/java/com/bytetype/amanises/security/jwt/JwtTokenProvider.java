package com.bytetype.amanises.security.jwt;

import com.bytetype.amanises.security.UserDetailsImpl;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseCookie;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import org.springframework.web.util.WebUtils;

import javax.crypto.SecretKey;
import java.util.Date;

@Component
public class JwtTokenProvider {
    private static final Logger logger = LoggerFactory.getLogger(JwtTokenProvider.class);

    @Value("${app.jwt.secret}")
    private String jwtSecret;

    @Value("${app.jwt.expiration-ms}")
    private int jwtExpirationMs;

    @Value("${app.jwt.cookie-name}")
    private String jwtCookie;

    /**
     * Retrieves JWT token from the cookies in the HttpServletRequest.
     *
     * @param request the HttpServletRequest from which to extract the JWT cookie
     * @return the JWT as a string if it exists, otherwise null
     */
    public String getJwtFromCookies(HttpServletRequest request) {
        Cookie cookie = WebUtils.getCookie(request, jwtCookie);
        if (cookie != null) {
            return cookie.getValue();
        } else {
            return null;
        }
    }

    /**
     * Creates a cookie to clean JWT, effectively logging the user out.
     *
     * @return a ResponseCookie signaling the client to clear the JWT
     */
    public ResponseCookie getCleanJwtCookie() {
        return ResponseCookie.from(jwtCookie).path("/api").build();
    }

    /**
     * Generates a JWT cookie containing the provided payload.
     *
     * @param payload the payload to be included in the JWT token
     * @return a ResponseCookie containing the JWT used for authentication
     */
    public ResponseCookie generateJwtCookie(String payload) {
        String jwt = generateTokenFromUsername(payload);

        return ResponseCookie.from(jwtCookie, jwt).path("/api").maxAge(24 * 60 * 60).httpOnly(true).build();
    }

    /**
     * Returns a signing key built from a specification string for use in JWT creation and validation.
     *
     * @return a SecretKey used for signing the JWT token
     */
    private SecretKey getSigningKey(){
        return Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtSecret));
    }

    /**
     * Extracts and returns the username from a JWT token.
     *
     * @param token the JWT token to parse
     * @return the username (subject) contained in the token
     */
    public String getUsernameFromJwtToken(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getSubject();
    }

    /**
     * Validates the provided JWT token's integrity and expiration.
     *
     * @param authentication the JWT token to validate
     * @return a JWT token as String
     */
    public String generateJwtToken(Authentication authentication) {
        UserDetailsImpl userPrincipal = (UserDetailsImpl) authentication.getPrincipal();

        return generateTokenFromUsername(userPrincipal.getUsername());
    }

    /**
     * Generates a JWT token from a given username.
     *
     * @param username the username to be used as the subject of the token
     * @return a JWT token as String
     */
    public String generateTokenFromUsername(String username) {
        return Jwts.builder()
                .subject(username)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + jwtExpirationMs))
                .signWith(getSigningKey())
                .compact();
    }

    /**
     * Validates the provided JWT token's integrity and expiration.
     *
     * @param authToken the JWT token to validate
     * @return true if the token is valid, false otherwise
     */
    public boolean validateJwtToken(String authToken) {
        try {
            Jwts.parser().verifyWith(getSigningKey()).build().parseSignedClaims(authToken);
            return true;
        } catch (MalformedJwtException e) {
            logger.error("Invalid JWT token: {}", e.getMessage());
        } catch (ExpiredJwtException e) {
            logger.error("JWT token is expired: {}", e.getMessage());
        } catch (UnsupportedJwtException e) {
            logger.error("JWT token is unsupported: {}", e.getMessage());
        } catch (IllegalArgumentException e) {
            logger.error("JWT claims string is empty: {}", e.getMessage());
        }

        return false;
    }
}