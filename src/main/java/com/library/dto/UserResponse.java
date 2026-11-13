package com.library.dto;

import com.library.enums.Role;

public record UserResponse(Long id, String name, String email, Role role) {
}
