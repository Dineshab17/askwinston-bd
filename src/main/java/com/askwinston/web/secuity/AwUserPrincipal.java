package com.askwinston.web.secuity;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class AwUserPrincipal {
    private Long id;
    private String email;
    private String authority;
}
