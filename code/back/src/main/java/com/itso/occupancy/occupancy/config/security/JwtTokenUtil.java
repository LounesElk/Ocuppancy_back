package com.itso.occupancy.occupancy.config.security;

import com.itso.occupancy.occupancy.helper.enumeration.SecurityJWTEnum;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Data
@Component
public class JwtTokenUtil implements Serializable {

    @Serial
    private static final long serialVersionUID      = -2550185165626007488L;

    @Value("${jwt.secret}")
    private String secret;

    /**
     * retrieve username from jwt token
     * @param token
     * @return
     */
    public String getUsernameFromToken(String token) {
        return getClaimFromToken(token, Claims::getSubject);
    }


    /**
     *
     * retrieve expiration date from jwt token
     *
     * @param token
     * @return
     */
    public Date getExpirationDateFromToken(String token) {
        return getClaimFromToken(token, Claims::getExpiration);
    }


    /**
     *
     * get Claim From Token
     *
     * @param <T>
     * @param token
     * @param claimsResolver
     * @return
     */
    public <T> T getClaimFromToken(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = getAllClaimsFromToken(token);

        return claimsResolver.apply(claims);
    }


    /**
     * for retrieveing any information from token we will need the secret key
     *
     * @param token
     * @return
     */
    private Claims getAllClaimsFromToken(String token) {
        return Jwts.parser().setSigningKey(secret).parseClaimsJws(token).getBody();
    }


    /**
     *
     * check if the token has expired
     *
     * @param token
     * @return
     */
    private Boolean isTokenExpired(String token) {
        final Date expiration = getExpirationDateFromToken(token);

        return expiration.before(new Date());
    }


    /**
     * generate token for user
     *
     * @param userDetails
     * @param id
     * @param firstName
     * @param lastName
     * @param userId
     * @return
     */
    public String generateToken(UserDetails userDetails, boolean shortToken, Long id, String firstName, String lastName, long userId) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("roleId",id);
        claims.put("firstName",firstName);
        claims.put("lastName",lastName);
        claims.put("Id",userId);

        return doGenerateToken(claims, userDetails.getUsername(), shortToken);
    }


    /**
     * while creating the token -
     *
     * @param claims
     * @param subject
     * @return
     */
    private String doGenerateToken(Map<String, Object> claims, String subject, boolean shortToken) {
        //1. Define  claims of the token, like Issuer, Expiration, Subject, and the ID
        //2. Sign the JWT using the HS512 algorithm and secret key.
        //3. According to JWS Compact Serialization(https://tools.ietf.org/html/draft-ietf-jose-json-web-signature-41#section-3.1)
        //4. compaction of the JWT to a URL-safe string
        return Jwts.builder().setClaims(claims).setSubject(subject).setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + (shortToken ? SecurityJWTEnum.JWT_SHORT_TOKEN_VALIDITY.get() : SecurityJWTEnum.JWT_TOKEN_VALIDITY.get())))
                .signWith(SignatureAlgorithm.HS512, secret)
                .compact();
    }


    /**
     * validate token
     *
     * @param token
     * @param userDetails
     * @return
     */
    public Boolean validateToken(String token, UserDetails userDetails) {
        final String username = getUsernameFromToken(token);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }
}
