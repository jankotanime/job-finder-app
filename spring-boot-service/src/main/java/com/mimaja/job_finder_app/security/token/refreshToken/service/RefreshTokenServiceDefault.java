package com.mimaja.job_finder_app.security.token.refreshToken.service;

import com.mimaja.job_finder_app.core.handler.exception.BusinessException;
import com.mimaja.job_finder_app.core.handler.exception.BusinessExceptionReason;
import com.mimaja.job_finder_app.feature.user.model.User;
import com.mimaja.job_finder_app.feature.user.repository.UserRepository;
import com.mimaja.job_finder_app.security.encoder.RefreshTokenEncoder;
import com.mimaja.job_finder_app.security.shared.dto.ResponseTokenDto;
import com.mimaja.job_finder_app.security.token.accessToken.dto.response.CreateAccessTokenResponseDto;
import com.mimaja.job_finder_app.security.token.accessToken.service.AccessTokenService;
import com.mimaja.job_finder_app.security.token.refreshToken.dto.request.RequestRefreshTokenRotateDto;
import com.mimaja.job_finder_app.security.token.refreshToken.dto.response.RefreshTokenResponseDto;
import com.mimaja.job_finder_app.security.token.refreshToken.model.RefreshToken;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class RefreshTokenServiceDefault implements RefreshTokenService {
    private final StringRedisTemplate redisTemplate;
    private final HashOperations<String, String, String> hashOps;
    private final RefreshTokenEncoder refreshTokenEncoder;
    private final AccessTokenService accessTokenService;
    private final UserRepository userRepository;

    public RefreshTokenServiceDefault(
            StringRedisTemplate redisTemplate,
            RefreshTokenEncoder refreshTokenEncoder,
            AccessTokenService accessTokenService,
            UserRepository userRepository) {
        this.redisTemplate = redisTemplate;
        this.hashOps = redisTemplate.opsForHash();
        this.refreshTokenEncoder = refreshTokenEncoder;
        this.accessTokenService = accessTokenService;
        this.userRepository = userRepository;
    }

    @Override
    public RefreshTokenResponseDto createRefreshToken(User user) {
        String refreshTokenId = UUID.randomUUID().toString();
        String refreshTokenKey = "RefreshToken-" + refreshTokenId;
        String refreshTokenValue = UUID.randomUUID().toString();

        String hashedRefreshTokenValue = refreshTokenEncoder.encodeToken(refreshTokenValue);

        int lifetimeDays = 30;
        LocalDate expiresAt = LocalDate.now().plusDays(30);

        hashOps.put(refreshTokenKey, "tokenValue", hashedRefreshTokenValue);
        hashOps.put(refreshTokenKey, "userId", user.getId().toString());
        hashOps.put(refreshTokenKey, "expiresAt", expiresAt.toString());
        redisTemplate.expire(refreshTokenKey, lifetimeDays, TimeUnit.DAYS);

        RefreshTokenResponseDto result =
                new RefreshTokenResponseDto(refreshTokenValue, refreshTokenId);

        return result;
    }

    @Override
    public void deleteToken(String tokenId) {
        redisTemplate.delete("RefreshToken-" + tokenId);
    }

    @Override
    public ResponseTokenDto createTokensSet(User user) {
        CreateAccessTokenResponseDto accessTokenDto = accessTokenService.createToken(user);
        RefreshTokenResponseDto newRefreshToken = createRefreshToken(user);

        ResponseTokenDto response =
                new ResponseTokenDto(
                        accessTokenDto.accessToken(),
                        newRefreshToken.refreshToken(),
                        newRefreshToken.refreshTokenId());

        return response;
    }

    @Override
    public ResponseTokenDto rotateToken(RequestRefreshTokenRotateDto reqData) {
        String refreshToken = reqData.refreshToken();
        String refreshTokenId = reqData.refreshTokenId();

        RefreshToken tokenData =
                new RefreshToken(refreshTokenId, hashOps.entries("RefreshToken-" + refreshTokenId));

        if (!refreshTokenEncoder.verifyToken(refreshToken, tokenData.getHashedToken())) {
            throw new BusinessException(BusinessExceptionReason.INVALID_REFRESH_TOKEN);
        }

        deleteToken(refreshTokenId);

        UUID userId = UUID.fromString(tokenData.getUserId());
        Optional<User> userOptional = userRepository.findById(userId);

        if (userOptional.isEmpty()) {
            throw new BusinessException(BusinessExceptionReason.INVALID_REFRESH_TOKEN);
        }

        User user = userOptional.get();

        return createTokensSet(user);
    }

    @Override
    public void deleteAllUserTokens(UUID userId) {
        Set<String> tokenIds = redisTemplate.opsForSet().members("userTokens:" + userId);

        if (tokenIds != null && !tokenIds.isEmpty()) {
            List<String> keysToDelete = tokenIds.stream().map(id -> "refreshToken:" + id).toList();

            redisTemplate.delete(keysToDelete);
            redisTemplate.delete("userTokens:" + userId);
        }
    }
}
