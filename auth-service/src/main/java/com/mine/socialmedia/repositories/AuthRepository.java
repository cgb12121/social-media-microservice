package com.mine.socialmedia.repositories;


import com.mine.socialmedia.entities.UserCredentials;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface AuthRepository extends JpaRepository<UserCredentials, String> {
    Optional<UserCredentials> findByEmail(String email);

    Optional<UserCredentials> findByUserId(UUID id);
}
