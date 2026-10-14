package com.bank.webframwork.user.common.config;

import com.nimbusds.jose.jwk.source.ImmutableSecret;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.core.DelegatingOAuth2TokenValidator;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.OAuth2TokenValidator;
import org.springframework.security.oauth2.core.OAuth2TokenValidatorResult;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.*;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.time.Duration;
import java.util.Base64;

@Configuration
public class JwtConfig {

    @Bean
    public SecretKey jwtKey(@Value("${JWT_SECRET}") String secret) {
        byte[] keyBytes = Base64.getDecoder().decode(secret);

//        System.out.println(Base64.getUrlEncoder()
//                .withoutPadding()
//                .encodeToString(keyBytes));

        if (keyBytes.length < 32) {
            throw new IllegalArgumentException("JWT_SECRET은 디코딩 후 32바이트 이상이어야 합니다.");
        }

        return new SecretKeySpec(keyBytes, "HmacSHA256");
    }

    // JWT발급
    @Bean
    public JwtEncoder jwtEncoder(SecretKey jwtKey) {
        return new NimbusJwtEncoder(new ImmutableSecret<>(jwtKey));
    }

    // JWT 검증
    @Bean
    public JwtDecoder jwtDecoder(SecretKey jwtKey) {
        NimbusJwtDecoder decoder = NimbusJwtDecoder.withSecretKey(jwtKey)
                .macAlgorithm(MacAlgorithm.HS256)
                .build();

        JwtTimestampValidator time = new JwtTimestampValidator(Duration.ZERO);
        JwtIssuerValidator issuer = new JwtIssuerValidator("daelim");

        OAuth2TokenValidator<Jwt> requiredClaims = jwt -> {
            boolean validSubject;

            try {
                validSubject = Long.parseLong(jwt.getSubject()) > 0;
            }catch (RuntimeException ex) {
                validSubject = false;
            }

            if (jwt.getExpiresAt() == null || !validSubject) {
                return OAuth2TokenValidatorResult
                        .failure(new OAuth2Error("invalid_token", "Invalid JWT token", null));
            }

            return OAuth2TokenValidatorResult.success();
        };

        decoder.setJwtValidator(
                new DelegatingOAuth2TokenValidator<>(time,  requiredClaims, issuer)
        ); // Validator 적용

        return decoder;
    }

}
