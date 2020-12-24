package com.askwinston.web.dto;

import com.fasterxml.jackson.annotation.JsonView;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MessageCountDto {
    @JsonView(DtoView.UserVisibility.class)
    private Long senderId;
    @JsonView(DtoView.UserVisibility.class)
    private long unreadCount;
}
