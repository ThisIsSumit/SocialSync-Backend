package com.socialsync.socialsyncbackend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MfaToggleRequest {
    private Boolean enabled;
}
