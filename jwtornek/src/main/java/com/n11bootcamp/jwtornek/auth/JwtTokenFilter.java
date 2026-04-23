package com.n11bootcamp.jwtornek.auth;

import java.io.IOException;
import java.util.ArrayList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

//bu class her requestte token var mı diye kontrol eder, OncePerRequestFilter bu class sayesinde
@Component
public class JwtTokenFilter extends OncePerRequestFilter {

    @Autowired
    private TokenManager tokenManager;

    //bu method her request geldiğinde çalışır
    @Override
    protected void doFilterInternal(HttpServletRequest httpServletRequest,
                                    HttpServletResponse httpServletResponse,
                                    FilterChain filterChain) throws ServletException, IOException {

        /**
         * "Bearer 123hab2355"
         */
        final String authHeader = httpServletRequest.getHeader("Authorization");

        String username = null;
        String token = null;

        //token'ı ayıklar
        if (authHeader != null && authHeader.contains("Bearer")) {
            token = authHeader.substring(7);
            try {
                username = tokenManager.getUsernameToken(token);
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        }
    //username varsa ve henüz authentication yapılmamışsa burası çalışır
        if (username != null && token != null
                && SecurityContextHolder.getContext().getAuthentication() == null) {
            if (tokenManager.tokenValidate(token)) {
                //kullanıcı giriş yapmış olarak işaretleniyor
                UsernamePasswordAuthenticationToken upassToken =
                        new UsernamePasswordAuthenticationToken(username, null, new ArrayList<>());
                upassToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(httpServletRequest));
                SecurityContextHolder.getContext().setAuthentication(upassToken);
            }
        }
        //filter işini yaptıktan sonra isteği bir sonraki filter’a veya controller’a gönderiyor.
        filterChain.doFilter(httpServletRequest, httpServletResponse);
    }
}