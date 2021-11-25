package com.ead.authuser.controllers;

import com.ead.authuser.dtos.UserDTO;
import com.ead.authuser.models.UserModel;
import com.ead.authuser.services.UserService;
import com.fasterxml.jackson.annotation.JsonView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping(value = "/users")
@CrossOrigin(origins = "*", maxAge = 3600)
public class UserController {
    @Autowired
    UserService userService;

    @GetMapping
    public ResponseEntity<List<UserModel>> getAllUsers() {
        return ResponseEntity.status(HttpStatus.OK).body(userService.findAll());
    }

    @GetMapping(value = "/{userId}")
    public ResponseEntity<Object> findUserById(@PathVariable(value = "userId") UUID userId) {
        Optional<UserModel> optional = userService.findById(userId);
        if (!optional.isPresent()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User with id = " + userId + " not found");
        } else {
            return ResponseEntity.status(HttpStatus.OK).body(optional.get());
        }
    }

    @DeleteMapping(value = "/{userId}")
    public ResponseEntity<Object> deleteUser(@PathVariable(value = "userId") UUID userId) {
        Optional<UserModel> optional = userService.findById(userId);
        if (!optional.isPresent()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User with id = " + userId + " not found");
        } else {
            userService.delete(optional.get());
            return ResponseEntity.status(HttpStatus.OK).body("User with id = " + userId + " successfully deleted");
        }
    }

    @PutMapping(value = "/{userId}")
    public ResponseEntity<Object> updateUser(@PathVariable(value = "userId") UUID userId,
                                             @RequestBody @JsonView(UserDTO.UserView.UserPut.class) UserDTO dto) {
        Optional<UserModel> optional = userService.findById(userId);
        if (!optional.isPresent()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User with id = " + userId + " not found");
        } else {
            var userModel = optional.get();

            userModel.setFullName(dto.getFullName());
            userModel.setPhoneNumber(dto.getPhoneNumber());
            userModel.setCpf(dto.getCpf());

            userModel.setLastUpdateDate(LocalDateTime.now(ZoneId.of("UTC")));

            userService.save(userModel);

            return ResponseEntity.status(HttpStatus.OK).body(userModel);
        }
    }

    @PutMapping(value = "/{userId}/password")
    public ResponseEntity<Object> updatePassword(@PathVariable(value = "userId") UUID userId,
                                                 @RequestBody @JsonView(UserDTO.UserView.PasswordPut.class) UserDTO dto) {
        Optional<UserModel> optional = userService.findById(userId);
        if (!optional.isPresent()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User with id = " + userId + " not found");
        }
        if(!optional.get().getPassword().equals(dto.getOldPassword())){
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Error: Old Password Mismatch");
        }
        else {
            var userModel = optional.get();

            userModel.setPassword(dto.getPassword());
            userModel.setLastUpdateDate(LocalDateTime.now(ZoneId.of("UTC")));

            userService.save(userModel);

            return ResponseEntity.status(HttpStatus.OK).body("Password Successfully Updated!");
        }
    }

    @PutMapping(value = "/{userId}/image")
    public ResponseEntity<Object> updateImage(@PathVariable(value = "userId") UUID userId,
                                                 @RequestBody @JsonView(UserDTO.UserView.ImagePut.class) UserDTO dto) {
        Optional<UserModel> optional = userService.findById(userId);
        if (!optional.isPresent()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User with id = " + userId + " not found");
        }
        else {
            var userModel = optional.get();

            userModel.setImageUrl(dto.getImageURL());
            userModel.setLastUpdateDate(LocalDateTime.now(ZoneId.of("UTC")));

            userService.save(userModel);

            return ResponseEntity.status(HttpStatus.OK).body(userModel);
        }
    }
}