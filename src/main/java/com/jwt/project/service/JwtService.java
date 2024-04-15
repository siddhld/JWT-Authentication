package com.jwt.project.service;

import com.jwt.project.model.Token;
import com.jwt.project.model.User;
import com.jwt.project.repository.TokenRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import javax.crypto.SecretKey;
import java.util.Date;
import java.util.function.Function;

@Service
public class JwtService {

    private final String SECRET_KEY = "d9931cfb2347026cf9e604c1febc19eb2cb0296ebf933230d0cb92428310f206";
    @Autowired
    private TokenRepository tokenRepository;
//    private final String SECRET_KEY = System.getenv("JWT_SECRET_KEY");
    public String generateToken(User user){
        String token = Jwts
                .builder()
                .subject(user.getUsername())
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + 24*60*60*1000))
                .signWith(getSigninKey())
                .compact();

        return token;
    }

    public boolean isValid(String token, UserDetails user){
        String username = extractUsername(token);
        boolean isValidToken = tokenRepository.findByToken(token).map(Token::isLoggedOut).orElse(false);
        return username.equals(user.getUsername()) && !isTokenExpired(token) && !isValidToken;
    }

    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    public String extractUsername(String token){
        return extractClaim(token, Claims::getSubject);
    }


//    These two methods "extractClaim" & "extractAllClaims" work together to extract specific information from a
//    JSON Web Token (JWT). Imagine a JWT as a locked box containing information about a user. These methods help
//    you unlock the box, retrieve specific details, and then close it again.
    public <T> T extractClaim(String token, Function<Claims, T> resolver){
        Claims claims = extractAllClaims(token);
        return resolver.apply(claims);
    }

    private Claims extractAllClaims(String token){
        return Jwts
                .parser()
                .verifyWith(getSigninKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    private SecretKey getSigninKey() {
        byte[] keyBytes = Decoders.BASE64URL.decode(SECRET_KEY);
        return Keys.hmacShaKeyFor(keyBytes);
    }

}
