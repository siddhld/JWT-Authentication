package com.jwt.project.repository;

import com.jwt.project.model.Token;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TokenRepository extends JpaRepository<Token, Integer> {

//    @Query("SELECT t FROM Token t JOIN t.user u WHERE u.id = :userId AND t.isLoggedOut = false")
    @Query("select t from Token t inner join User u on t.user.id = u.id WHERE u.id = :userId AND t.isLoggedOut = false")
    List<Token> findAllTokenByUser(Integer userId);
    Optional<Token> findByToken(String token);
}
