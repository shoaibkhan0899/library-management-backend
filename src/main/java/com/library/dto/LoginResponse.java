package com.library.dto;

import com.library.enums.Role;

public record LoginResponse(String message, String token, Role role) {
}
