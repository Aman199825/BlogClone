package com.example.demo.service;

import com.example.demo.domain.NotificationEmail;
import com.example.demo.domain.User;
import com.example.demo.domain.VerificationToken;
import com.example.demo.dto.AuthenticationResponse;
import com.example.demo.dto.LoginRequest;
import com.example.demo.dto.RefreshTokenRequest;
import com.example.demo.dto.RegisterRequest;
import com.example.demo.exceptions.SpringRedditException;
import com.example.demo.repository.UserRepository;
import com.example.demo.repository.VerificationTokenRepository;
import com.example.demo.security.JwtTokenProvider;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Service
@Slf4j
@AllArgsConstructor
public class AuthService {

    private final RefreshTokenService refreshTokenService;
   private final PasswordEncoder passwordEncoder;
      private final UserRepository  userRepository;
       private final VerificationTokenRepository verificationTokenRepository;
       private final MailService mailService;
       private final AuthenticationManager authenticationManager;
       private final JwtTokenProvider jwtTokenProvider;
      @Transactional
    public User signup(RegisterRequest registerRequest)
    {
        User user=new User();
        user.setEmail(registerRequest.getEmail());
        user.setUsername(registerRequest.getUsername());
        user.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
        user.setEnabled(true);
        user.setCreated(Instant.now());
        userRepository.save(user);
        String token=generateVerificationToken(user);
        mailService.sendEmail(new NotificationEmail("please activate your account",user.getEmail(),
                "Thank you for signing up to this blogging service, "+"please click on the below url to activate your account :"+"http://localhost:8080/api/auth/verify/"+token));
        return user;
    }

    private String generateVerificationToken(User user) {
          log.info("inside the method");
        String token=UUID.randomUUID().toString();

        VerificationToken verificationToken=new VerificationToken();
        verificationToken.setToken(token);
        verificationToken.setUser(user);
        verificationTokenRepository.save(verificationToken);
        return token;
    }

    public void verifyAccount(String token) {
          Optional<VerificationToken> verificationToken= verificationTokenRepository.findByToken(token);
          verificationToken.orElseThrow(()->new SpringRedditException("invalid token"));
          fetchUserAndEnable(verificationToken.get());
    }

    private void fetchUserAndEnable(VerificationToken verificationToken) {
          String username=verificationToken.getUser().getUsername();
          User user=userRepository.findByUsername(username).orElseThrow(()->new SpringRedditException("user not found"));
           user.setEnabled(true);
           userRepository.save(user);
    }

    public AuthenticationResponse login(LoginRequest loginRequest) {
          Authentication authenticate=authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginRequest.getUsername(),loginRequest.getPassword()));
        SecurityContextHolder.getContext().setAuthentication(authenticate);
        String token=jwtTokenProvider.generateToken(authenticate);
        return  AuthenticationResponse.builder().authenticationToken(token).refreshToken(refreshTokenService.generateRefreshToken().getToken()).username(loginRequest.getUsername()).expiresAt(Instant.now().plusMillis(jwtTokenProvider.getJwtExpirationInMillis())).build();

    }

    @Transactional(readOnly = true)
    public User getCurrentUser() {
        org.springframework.security.core.userdetails.User prinicipal= (org.springframework.security.core.userdetails.User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return userRepository.findByUsername(prinicipal.getUsername()).orElseThrow(()->new UsernameNotFoundException("username not found: "+prinicipal.getUsername()));
    }
    public boolean isLoggedIn() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return !(authentication instanceof AnonymousAuthenticationToken) && authentication.isAuthenticated();
    }

    public AuthenticationResponse refreshToken(RefreshTokenRequest refreshTokenRequest) {
        refreshTokenService.validateRefreshToken(refreshTokenRequest.getRefreshToken());
        String token = jwtTokenProvider.generateTokenWithUserName(refreshTokenRequest.getUsername());
        return AuthenticationResponse.builder()
                .authenticationToken(token)
                .refreshToken(refreshTokenRequest.getRefreshToken())
                .expiresAt(Instant.now().plusMillis(jwtTokenProvider.getJwtExpirationInMillis()))
                .username(refreshTokenRequest.getUsername())
                .build();
    }
}
