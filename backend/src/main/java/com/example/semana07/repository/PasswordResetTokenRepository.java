package com.example.semana07.repository;

import com.example.semana07.entity.PasswordResetToken;
import com.example.semana07.entity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, Long> {
    Optional<PasswordResetToken> findByTokenAndUsedFalse(String token);
    void deleteByUsuario(Usuario usuario);
    void deleteByExpiryDateBefore(java.time.LocalDateTime date);
}