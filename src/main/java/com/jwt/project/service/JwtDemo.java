package com.jwt.project.service;

import com.jwt.project.model.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.core.userdetails.UserDetails;

import javax.crypto.SecretKey;
import java.security.Key;
import java.util.Date;
import java.util.function.Function;

public class JwtDemo {

    private final String SECRET_KEY = System.getenv("JWT_SECRET_KEY");


//    generateToken(User user): Creates a secure JSON Web Token (JWT) containing a user's information, including
//    username and expiration time.
    public String generateToken(User user){
        String token = Jwts
                .builder()
                .subject(user.getUsername())
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + 24*60*60*1000))
                .signWith(getSignInKey())
                .compact();
        return token;
    }

//    isValid(String token, UserDetails user): Verifies if a JWT token is valid and hasn't expired, and checks if the
//    username in the token matches the provided user.
    public boolean isValid(String token, UserDetails user){
        String username = extractUsername(token);
        return username.equals(user.getUsername()) && !isTokenExpired(token);
    }

//    isTokenExpired(String token): Checks if a JWT token has passed its expiration date and is no longer valid.
//    The "before" method is used on the extracted expiration date (extractExpiration(token)) to check if it occurs before the current date (new Date()).
//    Expired Token: If the expiration date comes before the current date (meaning it has passed), the before method returns true.
    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    //  'extractExpiration(String token): Extracts the date and time when a JWT token expires and is no longer valid.'
    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    //    extractUsername(String token): Retrieves the username associated with a JWT token.
    private String extractUsername(String token){
        return extractClaim(token, Claims::getSubject);
    }

//    The extractClaim method uses the retrieved information to extract a specific detail based on the provided function.
    private <T> T extractClaim(String token, Function<Claims, T> resolver){
        Claims claims = extractAllClaims(token);
        return resolver.apply(claims);
    }


//    The extractAllClaims method retrieves all information from the token.
    private Claims extractAllClaims(String token){
        return Jwts
                .parser()
                .verifyWith(getSignInKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

//    getSigninKey(): Retrieves the secret key used to sign and verify the authenticity of JWT tokens.
    private SecretKey getSignInKey() {
        byte[] bytes = Decoders.BASE64URL.decode(SECRET_KEY);
        return Keys.hmacShaKeyFor(bytes);
    }


}
