package com.victor.security2.dto;

public record LoginResponse(String acessToken, Long expiresIn) {
}
