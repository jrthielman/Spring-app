package com.example.app.ws.ui.controller;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.app.ws.service.UserService;
import com.example.app.ws.shared.dto.UserDto;
import com.example.app.ws.ui.model.request.UserDetailsRequestModel;
import com.example.app.ws.ui.model.response.UserRest;

import org.springframework.web.bind.annotation.RequestBody;

@RestController
@RequestMapping("users")
public class UserController {
	
	UserService userService;
	
	@Autowired
	UserController(UserService userService){
		this.userService = userService;
	}
	

	@GetMapping
	public String getUser() {
		return "get user was called";
	}

	@PostMapping
	public UserRest createUser(@RequestBody UserDetailsRequestModel userDetails) {

		UserRest returnValue = new UserRest();
		UserDto userDto = new UserDto();
		BeanUtils.copyProperties(userDetails, userDto); // copies properties from source object to target object
		UserDto createdUser = userService.createUser(userDto);
		BeanUtils.copyProperties(createdUser, returnValue);
		
		return returnValue;
	}

	@PutMapping
	public String updateUser() {
		return "user has been updated";
	}

	@DeleteMapping
	public String deleteUser() {
		return "user has been deleted";
	}
}
