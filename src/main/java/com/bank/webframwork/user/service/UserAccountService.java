package com.bank.webframwork.user.service;

import com.bank.webframwork.user.dto.*;
import com.bank.webframwork.user.entity.UserAccountEntity;
import com.bank.webframwork.user.repository.UserAccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class UserAccountService {
    private final UserAccountRepository userAccountRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtEncoder jwtEncoder;

    @Transactional(readOnly = true)
    public MeResponse me(Long accountId) {
        UserAccountEntity userAccountEntity = userAccountRepository.findByIdAndDeleted(accountId, false)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "사용할 수 없는 계정입니다."));

        return new MeResponse(userAccountEntity.getId(), userAccountEntity.getEmail(), userAccountEntity.getNickName());
    }



    public LoginResponseDto login(LoginRequestDto loginRequestDto) {
        if (loginRequestDto.password()
                .getBytes(StandardCharsets.UTF_8).length > 72) {
            throw loginFailed();
        }
        UserAccountEntity userAccountEntity = userAccountRepository
                .findByEmailAndDeleted(loginRequestDto.email(), false)
                .orElseThrow(() -> loginFailed());
        // 입력한 비밀번호와 DB에 저장된 해시를 비교
        boolean matched = passwordEncoder.matches(
                loginRequestDto.password(), userAccountEntity.getPasswordHash()
        );

        if (!matched) {
            throw loginFailed();
        }
        // jwt를 만들어준다.
        Instant now = Instant.now();
        long expiresIn = 15 * 60;

        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer("daelim")
                .subject(userAccountEntity.getId().toString())
                .issuedAt(now)
                .expiresAt(now.plusSeconds(expiresIn))
                .claim("nickname", userAccountEntity.getNickName())
                .build();

        JwsHeader header = JwsHeader
                .with(MacAlgorithm.HS256)
                .type("JWT")
                .build();

        String accessToken = jwtEncoder.encode(
                JwtEncoderParameters
                        .from(header, claims)
        ).getTokenValue();

        return new LoginResponseDto(accessToken, "Bearer", expiresIn);
    }

    private ResponseStatusException loginFailed() {
        return new ResponseStatusException(HttpStatus.UNAUTHORIZED, "이메일 또는 비밀번호가 올바르지 않습니다. ");
    }

    public SighUpResponseDto signup(SignUpRequestDto request) {

        // 이메일 닉네임 중복 체크
        // 체크했는데 이미 있으면 에러 (Exception)
        if (userAccountRepository.existsByEmail(request.email())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "가입된 이메일이 존재합니다.");
        }

        if(userAccountRepository.existsByNickName(request.nickname())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "닉네임이 중복");
        }

        System.out.println("내가 입력한 비밀번호 : " + request.password());

        String passwordHash = passwordEncoder.encode((request.password()));
        System.out.println("passwordHash : " + passwordHash);

        UserAccountEntity userAccount = new UserAccountEntity(request.email(), passwordHash, request.nickname());
        UserAccountEntity saved = userAccountRepository.save(userAccount);

        return new SighUpResponseDto(saved.getId());
    }

    // create
    public String addUserAccount(String email, String passwordHash, String nickname) {
        UserAccountEntity userAccountEntity = new UserAccountEntity(email, passwordHash, nickname);
        userAccountRepository.save(userAccountEntity);

        return "생성 success !";
    }

    public boolean checkEmail(String email) {
        return userAccountRepository.existsByEmail(email);
    }

    // read
    public String getUserAccountByEmail(String email) {
        Optional<UserAccountEntity> userAccountOptional = userAccountRepository.findByEmail(email);

        if (userAccountOptional.isPresent()) {
            return userAccountOptional.get().getNickName();
        }

        return "존재하지 않는 이메일입니다.";
    }

// update
    public String updateUserAccount(String email, String password, String nickname) {
        UserAccountEntity userAccountEntity = userAccountRepository
                .findByEmailAndDeleted(email, false)
                .orElseThrow(() -> new RuntimeException("존재하지 않는 이메일"));

        userAccountEntity.setNickName(nickname);
        userAccountEntity.setPasswordHash(password);

        return "수정 success !";
    }

    // delete
    public String deleteUserAccount(String email) {
        Optional<UserAccountEntity> userAccountOptional = userAccountRepository.findByEmail(email);

        if (userAccountOptional.isPresent()) {
            userAccountRepository.delete(userAccountOptional.get());

            return "삭제 success !";
        }

        return "존재하지 않는 이메일입니다.";
    }

}