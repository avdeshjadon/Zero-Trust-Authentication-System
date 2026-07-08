package com.zerotrust.auth.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class JwtAuthResponse {
    private String accessToken;
    private String refreshToken;
    private Long id;
    private String email;
    private List<String> roles;
    @Builder.Default
    private String tokenType = "Bearer";
}
