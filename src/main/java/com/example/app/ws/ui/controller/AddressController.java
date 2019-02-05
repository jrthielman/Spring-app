package com.example.app.ws.ui.controller;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.app.ws.service.AddressService;
import com.example.app.ws.shared.dto.AddressDto;
import com.example.app.ws.ui.model.response.AddressRest;

@RestController
@RequestMapping("addresses")
public class AddressController {
	
	AddressService addressService;

	@Autowired
	AddressController(AddressService addressService) {
		this.addressService = addressService;
	}
	
	@GetMapping(path = "/{addressId}", produces = { MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE })
	public AddressRest getAddress(@PathVariable String addressId) {
		
		AddressDto foundAddress = this.addressService.findByAddressId(addressId);
		
		return new ModelMapper().map(foundAddress, AddressRest.class);
	}
}
