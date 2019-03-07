package com.example.app.ws.service.impl;

import com.example.app.ws.api.UserRepository;
import com.example.app.ws.exceptions.UserAlreadyExistsException;
import com.example.app.ws.exceptions.UserServiceException;
import com.example.app.ws.io.entity.UserEntity;
import com.example.app.ws.service.UserService;
import com.example.app.ws.shared.AmazonSES;
import com.example.app.ws.shared.Utils;
import com.example.app.ws.shared.dto.AddressDto;
import com.example.app.ws.shared.dto.UserDto;
import com.example.app.ws.ui.model.response.ErrorMessages;
import org.modelmapper.ModelMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class UserServiceImpl implements UserService {

    private BCryptPasswordEncoder bCryptPasswordEncoder;

    private UserRepository userRepository;

    private Utils utils;

    @Autowired
    UserServiceImpl(BCryptPasswordEncoder bCryptPasswordEncoder, UserRepository userRepository, Utils utils) {
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
        this.userRepository = userRepository;
        this.utils = utils;
    }

    @Override
    public boolean verifyEmailToken(String token) {
        boolean returnValue = false;

        UserEntity userEntity = this.userRepository.findUserByEmailVerificationToken(token)
                .orElseThrow(() -> new UserServiceException(ErrorMessages.NO_RECORD_FOUND.getErrorMessage()));

        boolean hasTokenExpired = Utils.hasTokenExpired(token);

        if (!hasTokenExpired) {
            userEntity.setEmailVerificationStatus(Boolean.TRUE);
            userEntity.setEmailVerificationToken("Verified");
            this.userRepository.save(userEntity);
            returnValue = true;
        }
        return returnValue;
    }

    @Override
    public void deleteUser(String userId) {

        UserEntity userEntity = this.userRepository.findByUserId(userId);

        if (userEntity == null)
            throw new UserServiceException(ErrorMessages.NO_RECORD_FOUND.getErrorMessage());

        this.userRepository.delete(userEntity);
    }

    @Override
    public UserDto updateUser(String userId, UserDto user) {

        UserDto returnValue = new UserDto();
        UserEntity userEntity = this.userRepository.findByUserId(userId);

        if (userEntity == null)
            throw new UserServiceException(ErrorMessages.NO_RECORD_FOUND.getErrorMessage());

        userEntity.setFirstName(user.getFirstName());
        userEntity.setLastName(user.getLastName());

        UserEntity updatedUserDetails = this.userRepository.save(userEntity);
        BeanUtils.copyProperties(updatedUserDetails, returnValue);

        return returnValue;

    }

    @Override
    public UserDto getUser(String email) {
        UserEntity userEntity = this.userRepository.findByEmail(email);

        if (userEntity == null)
            throw new UsernameNotFoundException(email);

        UserDto returnValue = new UserDto();
        BeanUtils.copyProperties(userEntity, returnValue);

        return returnValue;
    }

    @Override
    public UserDto getUserByUserId(String userId) {

        UserDto returnValue = new UserDto();

        UserEntity userEntity = this.userRepository.findByUserId(userId);

        if (userEntity == null)
            throw new UsernameNotFoundException("User with ID: " + userId + " not found");

        BeanUtils.copyProperties(userEntity, returnValue);

        return returnValue;
    }

    @Override
    public List<UserDto> getAllUsers(int page, int limit) {

        List<UserDto> returnValue = new ArrayList<>();

        if (page > 0) page -= 1;

        Pageable pageableRequest = PageRequest.of(page, limit);

        Page<UserEntity> usersPage = this.userRepository.findAll(pageableRequest);
        List<UserEntity> users = usersPage.getContent();

        for (UserEntity userEntity : users) {
            UserDto temp = new UserDto();
            BeanUtils.copyProperties(userEntity, temp);
            returnValue.add(temp);
        }
        return returnValue;
    }

    @Override
    public UserDto createUser(UserDto user) {

        if (this.userRepository.findByEmail(user.getEmail()) != null)
            throw new UserAlreadyExistsException(ErrorMessages.RECORD_ALREADY_EXISTS.getErrorMessage());

        for (int i = 0; i < user.getAddresses().size(); i++) {
            AddressDto address = user.getAddresses().get(i);
            address.setUserDetails(user);
            address.setAddressId(this.utils.generateAddressId(30));
            user.getAddresses().set(i, address);
        }

        ModelMapper modelMapper = new ModelMapper();
        UserEntity userEntity = modelMapper.map(user, UserEntity.class);

        String publicUserId = this.utils.generateUserId(30);
        userEntity.setEncrypedPassword(this.bCryptPasswordEncoder.encode(user.getPassword()));
        userEntity.setUserId(publicUserId);
        userEntity.setEmailVerificationToken(Utils.generateEmailVerificationToken(publicUserId));
        userEntity.setEmailVerificationStatus(Boolean.FALSE);

        UserEntity storedUserDetails = this.userRepository.save(userEntity);

        UserDto returnValue = modelMapper.map(storedUserDetails, UserDto.class);

        new AmazonSES().verifyEmail(returnValue);

        return returnValue;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        UserEntity userEntity = this.userRepository.findByEmail(email);

        if (userEntity == null)
            throw new UsernameNotFoundException(email);

        return new User(userEntity.getEmail(), userEntity.getEncrypedPassword(), userEntity.getEmailVerificationStatus()
        , true, true, true, new ArrayList<>());
//        return new User(userEntity.getEmail(), userEntity.getEncrypedPassword(), new ArrayList<>());
    }

}
