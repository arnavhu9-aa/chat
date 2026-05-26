package com.ai.chat.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ai.chat.models.AppUser;

@Repository
public interface UserRepository extends JpaRepository<AppUser,Long> {
	Optional<AppUser> findByUsername (String username);

}
