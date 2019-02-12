package com.example.app.ws.api;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import com.example.app.ws.io.entity.UserEntity;

import java.util.Optional;

@Repository
public interface UserRepository extends PagingAndSortingRepository<UserEntity, Long> {

	UserEntity findUserByEmail(String email);
	UserEntity findByEmail(String email);
	UserEntity findByUserId(String userId);
	Optional<UserEntity> findUserByEmailVerificationToken(String token);
}
