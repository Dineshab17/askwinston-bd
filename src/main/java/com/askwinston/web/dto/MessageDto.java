package com.askwinston.web.dto;

import com.fasterxml.jackson.annotation.JsonView;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MessageDto {
    @JsonView(DtoView.UserVisibility.class)
    private Long id;
    @JsonView(DtoView.UserVisibility.class)
    private Date date;
    @JsonView(DtoView.UserVisibility.class)
    private Long senderId;
    @JsonView(DtoView.UserVisibility.class)
    private Long recipientId;
    @JsonView(DtoView.UserVisibility.class)
    private String message;
    @JsonView(DtoView.UserVisibility.class)
    private int isRead;
}
