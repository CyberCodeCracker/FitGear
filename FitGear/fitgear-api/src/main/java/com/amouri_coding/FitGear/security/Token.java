package com.amouri_coding.FitGear.security;

import com.amouri_coding.FitGear.user.User;
import jakarta.persistence.*;
import lombok.Builder;

import java.time.LocalDateTime;

@Entity
@Builder
public class Token {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Long id;
    @Column(name = "TOKEN",unique = true, nullable = false)
    private String token;

    @Enumerated(EnumType.STRING)
    @Column(name = "TOKEN_TYPE",nullable = false)
    private TokenType tokenType;

    @Column(name = "ISSUED_AT")
    private LocalDateTime issuedAt;
    @Column(name = "EXPIRES_AT")
    private LocalDateTime expiresAt;
    @Column(name = "VALIDATED_AT")
    private LocalDateTime validatedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    public Token() {

    }

    public Token(Long id, String token,TokenType tokenType , LocalDateTime issuedAt, LocalDateTime expiresAt, LocalDateTime validatedAt, User user) {
        this.id = id;
        this.token = token;
        this.tokenType = tokenType;
        this.issuedAt = issuedAt;
        this.expiresAt = expiresAt;
        this.validatedAt = validatedAt;
        this.user = user;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return this.id;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getToken() {
        return this.token;
    }

    public void setTokenType(TokenType tokenType) {
        this.tokenType = tokenType;
    }

    public TokenType getTokenType() {
        return this.tokenType;
    }

    public void setIssuedAt(LocalDateTime issuedAt) {
        this.issuedAt = issuedAt;
    }

    public LocalDateTime getIssuedAt() {
        return this.issuedAt;
    }

    public void setExpiresAt(LocalDateTime expiresAt) {
        this.expiresAt = expiresAt;
    }

    public LocalDateTime getExpiresAt() {
        return this.expiresAt;
    }

    public void setValidatedAt(LocalDateTime validatedAt) {
        this.validatedAt = validatedAt;
    }

    public LocalDateTime getValidatedAt() {
        return this.validatedAt;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Long getUser() {
        return this.user.getId();
    }


}
