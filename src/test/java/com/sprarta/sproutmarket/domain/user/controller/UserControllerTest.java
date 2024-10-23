package com.sprarta.sproutmarket.domain.user.controller;

import com.sprarta.sproutmarket.config.JwtUtil;
import com.sprarta.sproutmarket.domain.user.dto.request.UserChangePasswordRequest;
import com.sprarta.sproutmarket.domain.user.dto.request.UserDeleteRequest;
import com.sprarta.sproutmarket.domain.user.dto.response.UserResponse;
import com.sprarta.sproutmarket.domain.user.entity.CustomUserDetails;
import com.sprarta.sproutmarket.domain.user.entity.User;
import com.sprarta.sproutmarket.domain.user.enums.UserRole;
import com.sprarta.sproutmarket.domain.user.service.CustomUserDetailService;
import com.sprarta.sproutmarket.domain.user.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
@MockBean(JpaMetamodelMappingContext.class)
@AutoConfigureMockMvc(addFilters = false)
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @InjectMocks
    private UserController userController;

    @MockBean
    private JwtUtil jwtUtil;

    @MockBean
    private CustomUserDetailService customUserDetailService;

    @MockBean
    private CustomUserDetails mockAuthUser;

    @BeforeEach
    void setUp() {
        User mockUser = new User(1L, "username", "email@example.com", "encodedOldPassword", "nickname", "010-1234-5678", "address", UserRole.USER);
        CustomUserDetails mockAuthUser = new CustomUserDetails(mockUser);

        // Set the authenticated user in the SecurityContext
        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(mockAuthUser, null, mockAuthUser.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authentication);

        when(userService.getUser(anyLong())).thenReturn(new UserResponse(mockUser.getId(), mockUser.getEmail()));
        doNothing().when(userService).changePassword(any(CustomUserDetails.class), any(UserChangePasswordRequest.class));
        doNothing().when(userService).deleteUser(any(CustomUserDetails.class), any(UserDeleteRequest.class));
    }

    @Test
    @WithMockUser
    void getUserSuccess() throws Exception {
        // given
        UserResponse userResponse = new UserResponse(1L, "email@example.com");
        given(userService.getUser(anyLong())).willReturn(userResponse);

        // when, then
        mockMvc.perform(get("/users/{userId}", 1L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.email").value("email@example.com"));
    }

    @Test
    @WithMockUser
    void changePasswordSuccess() throws Exception {
        // given
        UserChangePasswordRequest passwordRequest = new UserChangePasswordRequest("oldPassword", "NewPass1!"); // 패턴에 맞는 비밀번호로 수정

        // when, then
        mockMvc.perform(put("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"oldPassword\":\"oldPassword\",\"newPassword\":\"NewPass1!\"}") // 수정된 비밀번호 사용
                        .principal(() -> "email@example.com"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser
    void deleteUserSuccess() throws Exception {
        // given
        UserDeleteRequest deleteRequest = new UserDeleteRequest("password");

        // when, then
        mockMvc.perform(delete("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"password\":\"password\"}"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser
    void updateUserAddressSuccess() throws Exception {
        // given
        double longitude = 126.9780;
        double latitude = 37.5665;

        // when, then
        mockMvc.perform(patch("/users")
                        .param("longitude", String.valueOf(longitude))
                        .param("latitude", String.valueOf(latitude)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").value("주소가 성공적으로 업데이트되었습니다."));
    }
}
