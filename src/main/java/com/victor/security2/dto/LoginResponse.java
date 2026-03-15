package com.victor.security2.dto;

public record LoginResponse(String token, Long expiresIn) {
}
