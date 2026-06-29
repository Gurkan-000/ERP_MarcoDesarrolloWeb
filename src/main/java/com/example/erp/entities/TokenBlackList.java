package com.example.erp.entities;

import java.util.Date;
import java.util.UUID;

import org.hibernate.annotations.UuidGenerator;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(
    name = "token_blacklist"
)
public class TokenBlackList {

    @Id
    @UuidGenerator
    private UUID id;

    @Column(length = 500, unique = true)
    private String token;

    @Column(name = "fecha_expiracion")
    private Date fechaExpiracion;

}
