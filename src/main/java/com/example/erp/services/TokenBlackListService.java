package com.example.erp.services;

import java.util.Date;

import org.springframework.stereotype.Service;

import com.example.erp.entities.TokenBlackList;
import com.example.erp.repositories.TokenBlackListRepository;
import com.example.erp.security.JwtService;

@Service
public class TokenBlackListService {

    private final TokenBlackListRepository blacklistRepository;
    private final JwtService jwtService;

    public TokenBlackListService(TokenBlackListRepository blacklistRepository, JwtService jwtService) {
        this.blacklistRepository = blacklistRepository;
        this.jwtService = jwtService;
    }

    public void revocarToken(String token) {

        Date fechaExpiracion = jwtService.extractExpiration(token);

        TokenBlackList tokenBlacklist = TokenBlackList.builder()
                .token(token)
                .fechaExpiracion(fechaExpiracion)
                .build();
                
        blacklistRepository.save(tokenBlacklist);
    }

    public boolean esTokenInvalido(String token) {
        return blacklistRepository.existsByToken(token);
    }

}
