package com.n11bootcamp.jwtornek.auth;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

import java.security.Key;
import java.util.Date;

import org.springframework.stereotype.Service;

//token üretir ve token'ı validate eder
@Service
public class TokenManager {

    //token geçerlilik süresi
    private static final int validity = 5 * 60 * 1000;

    Key key = Keys.secretKeyFor(SignatureAlgorithm.HS256);

    //username'e göre jwt üretir
    public String generateToken(String username) {
        return Jwts.builder() //token olusturmayı baslatir
                .setSubject(username)
                .setIssuer("www.opendart.com") //token'ı kim üretti bilgisi
                .setIssuedAt(new Date(System.currentTimeMillis())) //ne zaman üretildi bilgisi
                .setExpiration(new Date(System.currentTimeMillis() + validity)) //token'ın süresi ne zama dolacak
                .signWith(key) //token'ı key'le imzalar
                .compact(); //token'ı stringe çevirir
    }

    //bu metod token geçerli mi diye bakar
    public boolean tokenValidate(String token) {
        if (getUsernameToken(token) != null && isExpired(token)) {
            return true;
        }
        return false;
    }

    public String getUsernameToken(String token) {
        Claims claims = getClaims(token);
        return claims.getSubject();
    }

    //token’ın bitiş zamanı şu andan sonra mı diye kontrol eden method
    public boolean isExpired(String token) {
        Claims claims = getClaims(token);
        return claims.getExpiration().after(new Date(System.currentTimeMillis()));
    }

    //token'ı key ile doğrulayıp içindeki claimleri alır
    private Claims getClaims(String token) {
        return Jwts.parser().setSigningKey(key).parseClaimsJws(token).getBody();
    }

}