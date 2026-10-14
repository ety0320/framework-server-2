package com.bank.webframwork.user.repository;

import com.bank.webframwork.user.entity.UserAccountEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserAccountRepository extends JpaRepository<UserAccountEntity, Long> {

    Optional<UserAccountEntity> findByIdAndDeleted(Long id, boolean deleted);

    Optional<UserAccountEntity> findByEmail(String email);
    Optional<UserAccountEntity> findByEmailAndDeleted(String email, boolean deleted);

    // 이메일 닉네임 중복 체크
    // 체크했는데 이미 있으면 에러 (Exception)

    boolean existsByEmail(String email);
    boolean existsByNickName(String nickname);
}