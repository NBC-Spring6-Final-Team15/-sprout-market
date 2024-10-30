package com.sprarta.sproutmarket.domain.auth.service;

import com.sprarta.sproutmarket.config.JwtUtil;
import com.sprarta.sproutmarket.domain.areas.service.AdministrativeAreaService;
import com.sprarta.sproutmarket.domain.auth.dto.request.AdminSignupRequest;
import com.sprarta.sproutmarket.domain.auth.dto.request.SigninRequest;
import com.sprarta.sproutmarket.domain.auth.dto.request.SignupRequest;
import com.sprarta.sproutmarket.domain.auth.dto.response.SigninResponse;
import com.sprarta.sproutmarket.domain.auth.dto.response.SignupResponse;
import com.sprarta.sproutmarket.domain.common.entity.Status;
import com.sprarta.sproutmarket.domain.common.enums.ErrorStatus;
import com.sprarta.sproutmarket.domain.common.exception.ApiException;
import com.sprarta.sproutmarket.domain.user.entity.User;
import com.sprarta.sproutmarket.domain.user.enums.UserRole;
import com.sprarta.sproutmarket.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final AdministrativeAreaService administrativeAreaService;

    @Transactional
    public SignupResponse signup(SignupRequest request) {
        return createUser(request, UserRole.USER);
    }

    @Transactional
    public SignupResponse adminSignup(AdminSignupRequest request) {
        return createAdminUser(request, UserRole.ADMIN);
    }

    public SigninResponse signin(SigninRequest request) {
        return authenticateUser(request, UserRole.USER);
    }

    public SigninResponse adminSignin(SigninRequest request) {
        return authenticateUser(request, UserRole.ADMIN);
    }

    private SignupResponse createUser(SignupRequest request, UserRole userRole) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new ApiException(ErrorStatus.BAD_REQUEST_EMAIL);
        }

        String encodedPassword = passwordEncoder.encode(request.getPassword());
        String address = getAddressFromCoordinates(request.getLongitude(), request.getLatitude());

        User newUser = new User(
                request.getUsername(),
                request.getEmail(),
                encodedPassword,
                request.getNickname(),
                request.getPhoneNumber(),
                address,
                userRole
        );
        User savedUser = userRepository.save(newUser);
        String bearerToken = jwtUtil.createToken(savedUser.getId(), savedUser.getEmail(), userRole);

        return new SignupResponse(bearerToken);
    }

    private SignupResponse createAdminUser(AdminSignupRequest request, UserRole userRole) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new ApiException(ErrorStatus.BAD_REQUEST_EMAIL);
        }

        String encodedPassword = passwordEncoder.encode(request.getPassword());

        User newUser = new User(
                request.getUsername(),
                request.getEmail(),
                encodedPassword,
                request.getNickname(),
                request.getPhoneNumber(),
                null, // adminSignup 에서는 address 가 필요하지 않으므로 null로 설정
                userRole
        );
        User savedUser = userRepository.save(newUser);
        String bearerToken = jwtUtil.createToken(savedUser.getId(), savedUser.getEmail(), userRole);

        return new SignupResponse(bearerToken);
    }

    private SigninResponse authenticateUser(SigninRequest request, UserRole requiredRole) {
        User user = findUserByEmail(request.getEmail());

        if (user.getStatus() == Status.DELETED) {
            throw new ApiException(ErrorStatus.NOT_FOUND_USER);
        }

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new ApiException(ErrorStatus.BAD_REQUEST_PASSWORD);
        }

        if (user.getUserRole() != requiredRole) {
            throw new ApiException(ErrorStatus.FORBIDDEN_ACCESS);
        }

        String bearerToken = jwtUtil.createToken(user.getId(), user.getEmail(), user.getUserRole());

        return new SigninResponse(bearerToken);
    }

    private User findUserByEmail(String email) {
        return userRepository.findByEmail(email).orElseThrow(
                () -> new ApiException(ErrorStatus.NOT_FOUND_USER));
    }

    private String getAddressFromCoordinates(double longitude, double latitude) {
        return administrativeAreaService.getAdministrativeAreaByCoordinates(longitude, latitude);
    }
}
