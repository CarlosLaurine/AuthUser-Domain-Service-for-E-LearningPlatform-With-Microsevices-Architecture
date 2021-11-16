package com.ead.authuser.models;

import javax.persistence.Entity;
import javax.persistence.Table;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name="tb_users")
public class UserModel implements Serializable {
    private static final Long serialVersionUID = 1L;

    private UUID userId;
    private String username;
    private String email;
    private String password;
    private String fullName;
    private UserStatus userStatus;
    private UserType userType;
    private String phoneNumber;
    private String cpf;
    private String imageUrl;
    private LocalDateTime creationDate;
    private LocalDateTime lastUpdateDate;
}
