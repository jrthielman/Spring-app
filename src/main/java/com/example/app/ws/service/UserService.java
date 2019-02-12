package com.example.app.ws.service;

import com.example.app.ws.shared.dto.UserDto;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.List;

public interface UserService extends UserDetailsService{

	boolean verifyEmailToken(String token);
	UserDto createUser(UserDto user);
	List<UserDto> getAllUsers(int page, int limit);
	UserDto getUser(String email);
	UserDto getUserByUserId(String userId);
	UserDto updateUser(String userId, UserDto user);
	void deleteUser(String userId);
	
}
