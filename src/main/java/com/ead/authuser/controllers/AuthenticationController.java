package com.ead.authuser.controllers;

import com.ead.authuser.configs.security.JwtProvider;
import com.ead.authuser.dtos.JwtDTO;
import com.ead.authuser.dtos.LoginDTO;
import com.ead.authuser.dtos.UserDTO;
import com.ead.authuser.enums.RoleType;
import com.ead.authuser.enums.UserStatus;
import com.ead.authuser.enums.UserType;
import com.ead.authuser.models.RoleModel;
import com.ead.authuser.models.UserModel;
import com.ead.authuser.services.RoleService;
import com.ead.authuser.services.UserService;
import com.fasterxml.jackson.annotation.JsonView;
import lombok.extern.log4j.Log4j2;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.time.ZoneId;

@Log4j2
@RestController
@RequestMapping(value = "/auth")
@CrossOrigin(origins = "*", maxAge = 3600)
public class AuthenticationController {

    @Autowired
    private UserService userService;
    @Autowired
    private RoleService roleService;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private JwtProvider jwtProvider;
    @Autowired
    private AuthenticationManager authenticationManager;


    @PostMapping("/signup")
    public ResponseEntity<Object> registerUser(@RequestBody @Validated(UserDTO.UserView.RegistrationPost.class)
                                                   @JsonView(UserDTO.UserView.RegistrationPost.class) UserDTO dto){

        log.debug("POST Method registerUser userDTO received {} ", dto.toString());

        if(userService.existsByUsername(dto.getUsername())) {
            log.warn("Username {} already exists", dto.getUsername());
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Username already exists!");
        }
        else if (userService.existsByEmail(dto.getEmail())){
            log.warn("Email {} already exists", dto.getEmail());
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Email already exists!");
        }
            RoleModel roleModel = roleService.findByRoleName(RoleType.ROLE_STUDENT).orElseThrow(() -> new RuntimeException("Error: Role Not Found at the Database"));

            dto.setPassword(passwordEncoder.encode(dto.getPassword()));

            var model = new UserModel();
            BeanUtils.copyProperties(dto, model);
            model.setUserStatus(UserStatus.ACTIVE);
            model.setUserType(UserType.STUDENT);
            model.setCreationDate(LocalDateTime.now(ZoneId.of("UTC")));
            model.setLastUpdateDate(LocalDateTime.now(ZoneId.of("UTC")));
            model.getRoles().add(roleModel);
            userService.saveUser(model);
            log.debug("POST Method registerUser userId saved {} ", model.getUserId());
            log.info("User Successfully Saved userId {} ", model.getUserId());
            return ResponseEntity.status(HttpStatus.CREATED).body(model);
        }

        @PostMapping("/login")
        public ResponseEntity<JwtDTO> authenticateUser(@Valid @RequestBody LoginDTO loginDto) {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginDto.getUsername(), loginDto.getPassword()));
            SecurityContextHolder.getContext().setAuthentication(authentication);
            String jwt = jwtProvider.generateJwt(authentication);
            return ResponseEntity.ok(new JwtDTO(jwt));
        }

    @PostMapping("/signup/admin/usr")
    public ResponseEntity<Object> registerUserAdmin(@RequestBody @Validated(UserDTO.UserView.RegistrationPost.class)
                                                    @JsonView(UserDTO.UserView.RegistrationPost.class) UserDTO dto){
        log.debug("POST registerUser userDto received {} ", dto.toString());

        if(userService.existsByUsername(dto.getUsername())) {
            log.warn("Username {} already exists", dto.getUsername());
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Username already exists!");
        }
        else if (userService.existsByEmail(dto.getEmail())){
            log.warn("Email {} already exists", dto.getEmail());
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Email already exists!");
        }
        RoleModel roleModel = roleService.findByRoleName(RoleType.ROLE_ADMIN)
                .orElseThrow(() -> new RuntimeException("Error: Role is Not Found."));
        dto.setPassword(passwordEncoder.encode(dto.getPassword()));
        var userModel = new UserModel();
        BeanUtils.copyProperties(dto, userModel);
        userModel.setUserStatus(UserStatus.ACTIVE);
        userModel.setUserType(UserType.ADMIN);
        userModel.setCreationDate(LocalDateTime.now(ZoneId.of("UTC")));
        userModel.setLastUpdateDate(LocalDateTime.now(ZoneId.of("UTC")));
        userModel.getRoles().add(roleModel);
        userService.saveUser(userModel);
        log.debug("POST registerUser userId saved {} ", userModel.getUserId());
        log.info("User saved successfully userId {} ", userModel.getUserId());
        return  ResponseEntity.status(HttpStatus.CREATED).body(userModel);
    }
}
