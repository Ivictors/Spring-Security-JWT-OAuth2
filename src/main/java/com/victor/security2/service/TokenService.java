package com.victor.security2.service;

import com.victor.security2.dto.LoginRequest;
import com.victor.security2.dto.LoginResponse;
import com.victor.security2.entities.Role;
import com.victor.security2.repository.UserRepository;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.stream.Collectors;

@Service
public class TokenService {

    private final JwtEncoder jwtEncoder;
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;


    public TokenService(JwtEncoder jwtEncoder, UserRepository userRepository, BCryptPasswordEncoder bCryptPasswordEncoder) {
        this.jwtEncoder = jwtEncoder;
        this.userRepository = userRepository;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    }

        public LoginResponse login(LoginRequest loginRequest) {
            var user = userRepository.findByUsername(loginRequest.username())
                    .orElseThrow(() -> new BadCredentialsException("Username or password is invalid!"));

            if (!user.loginIsCorrect(loginRequest, bCryptPasswordEncoder)) {
                throw new BadCredentialsException("Username or password is invalid!");
            }

            var now = Instant.now();
            var expiresIn = 300L; // 5 minutos

            var scopes = user.getRoles()
                    .stream()
                    .map(Role::getName)
                    .collect(Collectors.joining(" "));

            var claims = JwtClaimsSet.builder()
                    .issuer("mybackend")
                    .subject(user.getUserId().toString())
                    .issuedAt(now)
                    .expiresAt(now.plusSeconds(expiresIn))
                    .claim("scope", scopes)
                    .build();

            var jwtValue = jwtEncoder.encode(JwtEncoderParameters.from(claims)).getTokenValue();

            return new LoginResponse(jwtValue, expiresIn);
        }
    }
