package com.ead.authuser.controllers;

import com.ead.authuser.dtos.UserDTO;
import com.ead.authuser.models.UserModel;
import com.ead.authuser.services.UserService;
import com.ead.authuser.specifications.SpecificationTemplate;
import com.fasterxml.jackson.annotation.JsonView;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Log4j2
@RestController
@RequestMapping(value = "/users")
@CrossOrigin(origins = "*", maxAge = 3600)
public class UserController {
    @Autowired
    UserService userService;

    @GetMapping
    public ResponseEntity<Page<UserModel>> getAllUsers(SpecificationTemplate.UserSpec spec,
            @PageableDefault(page = 0, size = 10, sort = "userId", direction = Sort.Direction.ASC) Pageable pageable) {
        Page<UserModel> userModelPage = userService.findAll(spec, pageable);
        if (!userModelPage.isEmpty()) {
            for (UserModel user : userModelPage.toList()) {
                user.add(linkTo(methodOn(UserController.class).findUserById(user.getUserId())).withSelfRel());
            }
        }
        return ResponseEntity.status(HttpStatus.OK).body(userModelPage);
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
        log.debug("DELETE Method deleteUser userId received {} ", userId);
        Optional<UserModel> optional = userService.findById(userId);
        if (!optional.isPresent()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User with id = " + userId + " not found");
        } else {
            userService.delete(optional.get());
            log.debug("DELETE Method deleteUser execute for userId {} ", optional.get().getUserId());
            log.info("User Successfully Deleted userId {} ", userId);
            return ResponseEntity.status(HttpStatus.OK).body("User with id = " + userId + " successfully deleted");
        }
    }

    @PutMapping(value = "/{userId}")
    public ResponseEntity<Object> updateUser(@PathVariable(value = "userId") UUID userId,
                                             @RequestBody @Validated(UserDTO.UserView.UserPut.class)
                                             @JsonView(UserDTO.UserView.UserPut.class) UserDTO dto) {
        log.debug("PUT Method updateUser userDTO received {} ", dto.toString());
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
            log.debug("PUT Method updateUser userModel saved userId {} ", userModel.getUserId());
            log.info("User Successfully Updated userId {} ", userModel.getUserId());

            return ResponseEntity.status(HttpStatus.OK).body(userModel);
        }
    }

    @PutMapping(value = "/{userId}/password")
    public ResponseEntity<Object> updatePassword(@PathVariable(value = "userId") UUID userId,
                                                 @RequestBody @Validated(UserDTO.UserView.PasswordPut.class)
                                                 @JsonView(UserDTO.UserView.PasswordPut.class) UserDTO dto) {
        log.debug("PUT Method updatePassword userDto received {} ", dto.toString());
        Optional<UserModel> optional = userService.findById(userId);
        if (!optional.isPresent()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User with id = " + userId + " not found");
        }
        if(!optional.get().getPassword().equals(dto.getOldPassword())){
            log.warn("Old Password Mismatch for userId {} ", dto.getUserId());
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Error: Old Password Mismatch");
        }
        else {
            var userModel = optional.get();

            userModel.setPassword(dto.getPassword());
            userModel.setLastUpdateDate(LocalDateTime.now(ZoneId.of("UTC")));

            userService.save(userModel);

            log.debug("PUT Method updatePassword userModel saved userId {} ", userModel.getUserId());
            log.info("Password successfully updated for userId {} ", userModel.getUserId());

            return ResponseEntity.status(HttpStatus.OK).body("Password Successfully Updated!");
        }
    }

    @PutMapping(value = "/{userId}/image")
    public ResponseEntity<Object> updateImage(@PathVariable(value = "userId") UUID userId,
                                                 @RequestBody @Validated(UserDTO.UserView.ImagePut.class)
                                                 @JsonView(UserDTO.UserView.ImagePut.class) UserDTO dto) {
        log.debug("PUT Method updateImage userDto received {} ", dto.toString());
        Optional<UserModel> optional = userService.findById(userId);
        if (!optional.isPresent()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User with id = " + userId + " not found");
        }
        else {
            var userModel = optional.get();

            userModel.setImageUrl(dto.getImageURL());
            userModel.setLastUpdateDate(LocalDateTime.now(ZoneId.of("UTC")));

            userService.save(userModel);
            log.debug("PUT Method updateImage userModel saved userId {} ", userModel.getUserId());
            log.info("Image successfully updated userId {} ", userModel.getUserId());

            return ResponseEntity.status(HttpStatus.OK).body(userModel);
        }
    }
}