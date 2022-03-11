package com.ead.authuser.controllers;

import com.ead.authuser.dtos.PostInstructorDTO;
import com.ead.authuser.enums.UserType;
import com.ead.authuser.models.UserModel;
import com.ead.authuser.services.UserService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Optional;

@Log4j2
@RestController
@CrossOrigin(origins = "*", maxAge = 3600)
@RequestMapping("/instructors")
public class InstructorController {

    @Autowired
    private UserService userService;

    @PostMapping("/subscription")
    public ResponseEntity<Object> updateSubscriptionAsInstructor(@RequestBody @Valid PostInstructorDTO newInstructorDto) {

        Optional<UserModel> userModelOptional = userService.findById(newInstructorDto.getUserId());

        if(!userModelOptional.isPresent()) {

            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found.");

        }
        else {
            var userModel = userModelOptional.get();

            userModel.setUserType(UserType.INSTRUCTOR);
            userModel.setLastUpdateDate(LocalDateTime.now(ZoneId.of("UTC")));

            userService.updateUser(userModel);

            return ResponseEntity.status(HttpStatus.OK).body(userModel);
        }
    }

}
