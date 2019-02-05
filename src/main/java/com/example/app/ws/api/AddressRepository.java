package com.example.app.ws.api;

import java.util.List;
import java.util.Optional;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.example.app.ws.io.entity.AddressEntity;
import com.example.app.ws.io.entity.UserEntity;

@Repository
public interface AddressRepository extends CrudRepository<AddressEntity, Long>{
	
	List<AddressEntity> findAllByUserDetails(UserEntity userEntity);
	Optional<AddressEntity> findByAddressId(String addressId);
	Optional<AddressEntity> findByUserDetailsAndAddressId(UserEntity userEntity, String addressId);
}
