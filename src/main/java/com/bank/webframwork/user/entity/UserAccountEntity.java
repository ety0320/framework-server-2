package com.bank.webframwork.user.entity;

import com.bank.webframwork.user.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "account")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
//@Sqlrestriction("deleted = false")
@Getter
@Setter
public class UserAccountEntity extends BaseEntity {

    @Column(nullable = false, length = 100)
    private String email;

    @Column(name = "password_hash", nullable = false,length = 100)
    private String passwordHash;

    @Column(nullable = false, length = 20)
    private String nickName;

    public UserAccountEntity(String email, String passwordHash, String nickName) {
        this.email = email;
        this.passwordHash = passwordHash;
        this.nickName = nickName;
    }
}
