package com.example.erp.repositories;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.erp.entities.TokenBlackList;

public interface TokenBlackListRepository extends JpaRepository<TokenBlackList, UUID> {
    
    boolean existsByToken(String token);
    
}