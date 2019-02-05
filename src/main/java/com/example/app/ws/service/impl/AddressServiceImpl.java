package com.example.app.ws.service.impl;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.app.ws.api.AddressRepository;
import com.example.app.ws.api.UserRepository;
import com.example.app.ws.io.entity.AddressEntity;
import com.example.app.ws.io.entity.UserEntity;
import com.example.app.ws.service.AddressService;
import com.example.app.ws.shared.dto.AddressDto;
import com.example.app.ws.ui.model.response.ErrorMessages;

@Service
public class AddressServiceImpl implements AddressService {

	UserRepository userRepository;
	AddressRepository addressRepository;

	@Autowired
	public AddressServiceImpl(UserRepository userRepository, AddressRepository addressRepository) {
		this.userRepository = userRepository;
		this.addressRepository = addressRepository;
	}

	@Override
	public AddressDto findByAddressId(String addressId) {

		Optional<AddressEntity> foundAddress = this.addressRepository.findByAddressId(addressId);

		if (!foundAddress.isPresent())
			throw new RuntimeException(ErrorMessages.NO_RECORD_FOUND.getErrorMessage());

		return new ModelMapper().map(foundAddress.get(), AddressDto.class);
	}

	@Override
	public List<AddressDto> getAddresses(String userId) {

		List<AddressDto> returnValue = new ArrayList<>();

		UserEntity userEntity = this.userRepository.findByUserId(userId);

		if (userEntity == null)
			return returnValue;

		Iterable<AddressEntity> addresses = this.addressRepository.findAllByUserDetails(userEntity);

		Type addressesDto = new TypeToken<List<AddressDto>>() {
		}.getType();

		returnValue = new ModelMapper().map(addresses, addressesDto);

		return returnValue;
	}

	@Override
	public AddressDto getAddressesByUserId(String userId, String addressId) {

		UserEntity userEntity = this.userRepository.findByUserId(userId);

		if (userEntity == null)
			throw new RuntimeException("User does not exist with this ID");

		Optional<AddressEntity> addresses = this.addressRepository.findByUserDetailsAndAddressId(userEntity, addressId);

		if (!addresses.isPresent())
			throw new RuntimeException(ErrorMessages.NO_RECORD_FOUND.getErrorMessage());

		return new ModelMapper().map(addresses.get(), AddressDto.class);
	}

}
