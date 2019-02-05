package com.example.app.ws.ui.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.app.ws.service.UserService;
import com.example.app.ws.shared.dto.UserDto;
import com.example.app.ws.ui.model.request.UserDetailsRequestModel;
import com.example.app.ws.ui.model.response.OperationStatusModel;
import com.example.app.ws.ui.model.response.RequestOperationStatus;
import com.example.app.ws.ui.model.response.UserRest;

@RestController
@RequestMapping("users")
public class UserController {

	UserService userService;

	@Autowired
	UserController(UserService userService) {
		this.userService = userService;
	}

	@GetMapping(path = "/{id}", produces = { MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE })
	public UserRest getUser(@PathVariable String id) {
		UserRest returnValue = new UserRest();

		UserDto userDto = this.userService.getUserByUserId(id);
		BeanUtils.copyProperties(userDto, returnValue);

		return returnValue;
	}

	@GetMapping(produces = { MediaType.APPLICATION_XML_VALUE,
					MediaType.APPLICATION_JSON_VALUE })
	public List<UserRest> getUsers(@RequestParam(value="page", defaultValue="0") int page,
			@RequestParam(value="limit", defaultValue="25") int limit) {

		List<UserRest> returnValues = new ArrayList<>();
		
		List<UserDto> userDto = this.userService.getAllUsers(page, limit);
		for (UserDto user : userDto) {
			UserRest temp = new UserRest();
			BeanUtils.copyProperties(user, temp);
			returnValues.add(temp);
		}

		return returnValues;
	}

	@PostMapping(consumes = { MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE }, produces = {
			MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE })
	public UserRest createUser(@RequestBody UserDetailsRequestModel userDetails) throws Exception {

		UserRest returnValue = new UserRest();

		UserDto userDto = new UserDto();
		BeanUtils.copyProperties(userDetails, userDto); // copies properties from source object to target object
		UserDto createdUser = userService.createUser(userDto);
		BeanUtils.copyProperties(createdUser, returnValue);
		return returnValue;
	}

	@PutMapping(path = "/{id}", consumes = { MediaType.APPLICATION_XML_VALUE,
			MediaType.APPLICATION_JSON_VALUE }, produces = { MediaType.APPLICATION_XML_VALUE,
					MediaType.APPLICATION_JSON_VALUE })
	public UserRest updateUser(@RequestBody UserDetailsRequestModel userDetails, @PathVariable String id) {
		UserRest returnValue = new UserRest();

		UserDto userDto = new UserDto();
		BeanUtils.copyProperties(userDetails, userDto); // copies properties from source object to target object
		
		UserDto updatedUser = userService.updateUser(id, userDto);
		BeanUtils.copyProperties(updatedUser, returnValue);
		return returnValue;
	}

	@DeleteMapping(path="/{id}",produces = { MediaType.APPLICATION_XML_VALUE,
			MediaType.APPLICATION_JSON_VALUE })
	public OperationStatusModel deleteUser(@PathVariable String id) {
		
		OperationStatusModel returnValue = new OperationStatusModel();
		returnValue.setOprationName(RequestOperationName.DELETE.name());
		
		this.userService.deleteUser(id);
		
		returnValue.setOperationResult(RequestOperationStatus.SUCCES.name());
		
		return returnValue;
	}
}
