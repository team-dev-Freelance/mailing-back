package com.api.mailing.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.api.mailing.entities.Token;

public interface TokenRepo extends JpaRepository<Token, Long> {

    @Query(value = """
        select t from Token t inner join Utilisateur u\s
        on t.utilisateur.id = u.id\s
        where u.id = :id and (t.expired = false or t.revoked = false)\s
        """)
    List<Token> findAllValidTokenByUtilisateur(Long id);
  
    Optional<Token> findByToken(String token);
  }
  
