package com.example.app.ws.service.impl;

import java.util.ArrayList;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.app.ws.api.UserRepository;
import com.example.app.ws.io.entity.UserEntity;
import com.example.app.ws.service.UserService;
import com.example.app.ws.shared.Utils;
import com.example.app.ws.shared.dto.UserDto;

@Service
public class UserServiceImpl implements UserService {
	
	BCryptPasswordEncoder bCryptPasswordEncoder;
	
	UserRepository userRepository;
	
	Utils utils;
	
	@Autowired
	UserServiceImpl(BCryptPasswordEncoder bCryptPasswordEncoder, 
			UserRepository userRepository, Utils utils){
		this.bCryptPasswordEncoder = bCryptPasswordEncoder;
		this.userRepository = userRepository;
		this.utils = utils;
	}

	@Override
	public UserDto createUser(UserDto user) {

		UserEntity userEntity = new UserEntity();
		BeanUtils.copyProperties(user, userEntity);	
		
		String publicUserId = this.utils.generateUserId(30);
		userEntity.setEncrypedPassword(this.bCryptPasswordEncoder.encode(user.getPassword()));
		userEntity.setUserId(publicUserId);
		UserEntity storedUserDetails = this.userRepository.save(userEntity);
		UserDto returnValue = new UserDto();
		BeanUtils.copyProperties(storedUserDetails, returnValue);
		
		return returnValue;
	}

	@Override
	public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
		UserEntity userEntity = this.userRepository.findByEmail(email);
		
		if(userEntity == null) throw new UsernameNotFoundException(email);
		
		return new User(userEntity.getEmail(), userEntity.getEncrypedPassword(), new ArrayList<>());
	}

}
