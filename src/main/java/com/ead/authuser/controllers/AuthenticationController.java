package com.ead.authuser.controllers;

import com.ead.authuser.dtos.UserDTO;
import com.ead.authuser.enums.UserStatus;
import com.ead.authuser.enums.UserType;
import com.ead.authuser.models.UserModel;
import com.ead.authuser.services.UserService;
import com.fasterxml.jackson.annotation.JsonView;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.ZoneId;

@RestController
@RequestMapping(value = "/auth")
@CrossOrigin(origins = "*", maxAge = 3600)
public class AuthenticationController {

    @Autowired
    private UserService userService;

    private Logger logger = LoggerFactory.getLogger(AuthenticationController.class);

    @PostMapping("/signup")
    public ResponseEntity<Object> registerUser(@RequestBody @Validated(UserDTO.UserView.RegistrationPost.class)
                                                   @JsonView(UserDTO.UserView.RegistrationPost.class) UserDTO dto){
        if(userService.existsByUsername(dto.getUsername())) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Username already exists!");
        }
        else if (userService.existsByEmail(dto.getEmail())){
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Email already exists!");
        }
            var model = new UserModel();
            BeanUtils.copyProperties(dto, model);
            model.setUserStatus(UserStatus.ACTIVE);
            model.setUserType(UserType.STUDENT);
            model.setCreationDate(LocalDateTime.now(ZoneId.of("UTC")));
            model.setLastUpdateDate(LocalDateTime.now(ZoneId.of("UTC")));
            userService.save(model);
            return ResponseEntity.status(HttpStatus.CREATED).body(model);
        }
}
