package com.sprarta.sproutmarket.domain.user.dto.response;

import com.sprarta.sproutmarket.domain.common.entity.Status;
import com.sprarta.sproutmarket.domain.user.entity.User;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UserAdminResponse {
    private Long userId;
    private String username;
    private String email;
    private Status status;

    public UserAdminResponse(User user) {
        this.userId = user.getId();
        this.username = user.getUsername();
        this.email = user.getEmail();
        this.status = user.getStatus();
    }
}
