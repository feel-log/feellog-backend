package com.feellog.backend.domain.user.service;

import com.feellog.backend.domain.user.dto.UpdateUserRequest;
import com.feellog.backend.domain.user.dto.UserResponse;
import com.feellog.backend.domain.user.entity.User;
import com.feellog.backend.domain.user.entity.UserStatus;
import com.feellog.backend.domain.user.repository.RefreshTokenRepository;
import com.feellog.backend.domain.user.repository.UserRepository;
import com.feellog.backend.global.exception.BusinessException;
import com.feellog.backend.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;

    @Transactional(readOnly = true)
    public UserResponse getMe(Long userId) {
        User user = findActiveUser(userId);
        return UserResponse.from(user);
    }

    @Transactional
    public UserResponse updateMe(Long userId, UpdateUserRequest request) {
        User user = findActiveUser(userId);
        user.updateProfile(request.nickname(), request.birthDate(), request.gender());
        return UserResponse.from(user);
    }

    @Transactional
    public void withdraw(Long userId) {
        User user = findActiveUser(userId);
        refreshTokenRepository.deleteByUser(user);
        user.withdraw();
    }

    private User findActiveUser(Long userId) {
        return userRepository.findByIdAndStatus(userId, UserStatus.ACTIVE)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));
    }
}