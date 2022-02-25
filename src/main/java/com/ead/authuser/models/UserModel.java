package com.ead.authuser.models;

import com.ead.authuser.enums.UserStatus;
import com.ead.authuser.enums.UserType;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import org.springframework.hateoas.RepresentationModel;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@Entity
@Table(name = "tb_users")
public class UserModel extends RepresentationModel<UserModel> implements Serializable {
    private static final Long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID userId;
    @Column(nullable = false, unique = true, length = 50)
    private String username;
    @Column(nullable = false, unique = true, length = 50)
    private String email;
    @JsonIgnore
    @Column(nullable = false, length = 255)
    private String password;
    @Column(nullable = false, length = 150)
    private String fullName;
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private UserStatus userStatus;
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private UserType userType;
    @Column(length = 20)
    private String phoneNumber;
    @Column(length = 20)
    private String cpf;
    @Column
    private String imageUrl;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'")
    @Column(nullable = false)
    private LocalDateTime creationDate;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'")
    @Column(nullable = false)
    private LocalDateTime lastUpdateDate;

}
