package com.bank.webframwork.user.contorller;

import com.bank.webframwork.user.dto.*;
import com.bank.webframwork.user.service.UserAccountService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user-account")
@RequiredArgsConstructor
public class UserAccountController {
    private final UserAccountService userAccountService;


    @GetMapping("/me")
    public ResponseEntity<MeResponse> me(@AuthenticationPrincipal Jwt jwt) {
        Long accountId = Long.valueOf(jwt.getSubject());

        System.out.println("accountId: " + accountId);

        return ResponseEntity.ok()
                .body(userAccountService.me(accountId));
    }

    // login api
    // email, password = jwt
    // jwt는 payload에 특정 정보들을 적을 수 있다. 최소한의 정보만을 적는게 원칙 (보안 이슈)
    @PostMapping("/login")
    public ResponseEntity<LoginResponseDto> Login(
            @Valid @RequestBody LoginRequestDto loginRequestDto
    ){
        LoginResponseDto loginResponseDto = userAccountService.login(loginRequestDto);

        return ResponseEntity
                .ok()
                .header("Cache-Control", "no-store")
                .body(loginResponseDto);
    }

    // my api
    // 로그인 회원 정보 조회
    // 개인정보 : 전화번호, 생년월일, 이름

    @PostMapping("/signup")
    public SighUpResponseDto signup(@Valid @RequestBody SignUpRequestDto request) {
        return userAccountService.signup(request);
    }

    @GetMapping("/check-email")
    public boolean checkEmail(@RequestParam("email") String email) {
        return userAccountService.checkEmail(email);
    }

    // localhost:8080/user-account/add?email=321@naver.com&password=1234&nickname=nick321
    @GetMapping("/add")
    public String addUserAccount(String email, String password, String nickname) {
        return userAccountService.addUserAccount(email, password, nickname);
    }

    // localhost:8080/user-account/find-by-email?email=321@naver.com
    @GetMapping("/find-by-email")
    public String findUserAccountByEmail(String email) {
        return userAccountService.getUserAccountByEmail(email);
    }

    // localhost:8080/user-account/update?email=321@naver.com&password=4321&nickname=nick4321
    @GetMapping("/update")
    public String updateUserAccount(String email, String password, String nickname) {
        return userAccountService.updateUserAccount(email, password, nickname);
    }

    // localhost:8080/user-account/delete?email=321@naver.com
    @GetMapping("/delete")
    public String deleteUserAccount(String email) {
        return userAccountService.deleteUserAccount(email);
    }
}
