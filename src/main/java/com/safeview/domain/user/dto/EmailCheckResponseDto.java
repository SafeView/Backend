package com.safeview.domain.user.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
public class EmailCheckResponseDto {
    private final boolean exists;
    private final String message;
}
