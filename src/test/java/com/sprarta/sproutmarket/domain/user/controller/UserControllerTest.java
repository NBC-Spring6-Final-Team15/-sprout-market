package com.sprarta.sproutmarket.domain.user.controller;

import com.sprarta.sproutmarket.config.JwtUtil;
import com.sprarta.sproutmarket.domain.common.enums.ErrorStatus;
import com.sprarta.sproutmarket.domain.common.exception.ApiException;
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
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.http.MediaType;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.patch;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
@MockBean(JpaMetamodelMappingContext.class)
@AutoConfigureMockMvc(addFilters = false)
@ExtendWith(RestDocumentationExtension.class)
@AutoConfigureRestDocs(outputDir = "build/generated-snippets")
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
                .andDo(document("get-user",
                        responseFields(
                                fieldWithPath("message").description("응답 메시지"),
                                fieldWithPath("statusCode").description("응답 상태 코드"),
                                fieldWithPath("data.id").description("유저 ID"),
                                fieldWithPath("data.email").description("유저 이메일")
                        )
                ));
    }

    @Test
    @WithMockUser
    void getUserFail_UserNotFound() throws Exception {
        // given
        given(userService.getUser(anyLong())).willThrow(new ApiException(ErrorStatus.NOT_FOUND_USER));

        // when, then
        mockMvc.perform(get("/users/{userId}", 999L) // 존재하지 않는 유저 ID
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound()) // 404 Not Found 기대
                .andDo(document("get-user-fail-not-found",
                        responseFields(
                                fieldWithPath("message").description("에러 메시지"),
                                fieldWithPath("statusCode").description("응답 상태 코드"),
                                fieldWithPath("data").description("응답 데이터, 실패 시 null 반환").optional() // null일 수 있음
                        )
                ));
    }

    @Test
    @WithMockUser
    void changePasswordSuccess() throws Exception {
        // given
        UserChangePasswordRequest passwordRequest = new UserChangePasswordRequest("oldPassword", "NewPass1!"); // 패턴에 맞는 비밀번호로 수정

        // when, then
        mockMvc.perform(RestDocumentationRequestBuilders.put("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"oldPassword\":\"oldPassword\",\"newPassword\":\"NewPass1!\"}")
                        .principal(() -> "email@example.com"))
                .andExpect(status().isOk())
                .andDo(document("change-password",
                        requestFields(
                                fieldWithPath("oldPassword").description("The user's old password"),
                                fieldWithPath("newPassword").description("The new password to set")
                        )
                ));
    }

    @Test
    @WithMockUser
    void changePasswordFail_IncorrectOldPassword() throws Exception {
        // given
        UserChangePasswordRequest passwordRequest = new UserChangePasswordRequest("wrongOldPassword", "NewPass1!");
        doThrow(new ApiException(ErrorStatus.BAD_REQUEST_PASSWORD))
                .when(userService).changePassword(any(CustomUserDetails.class), any(UserChangePasswordRequest.class));

        // when, then
        mockMvc.perform(RestDocumentationRequestBuilders.put("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"oldPassword\":\"wrongOldPassword\",\"newPassword\":\"NewPass1!\"}"))
                .andExpect(status().isBadRequest()) // 400 Bad Request 기대
                .andDo(document("change-password-fail-wrong-old-password",
                        responseFields(
                                fieldWithPath("message").description("에러 메시지"),
                                fieldWithPath("statusCode").description("응답 상태 코드"),
                                fieldWithPath("data").description("응답 데이터, 실패 시 null 반환").optional() // null일 수 있음
                        )
                ));
    }

    @Test
    @WithMockUser
    void deleteUserSuccess() throws Exception {
        // given
        UserDeleteRequest deleteRequest = new UserDeleteRequest("password");

        // when, then
        mockMvc.perform(RestDocumentationRequestBuilders.delete("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"password\":\"password\"}"))
                .andExpect(status().isOk())
                .andDo(document("delete-user",
                        requestFields(
                                fieldWithPath("password").description("The password of the user")
                        )
                ));
    }

    @Test
    @WithMockUser
    void deleteUserFail_IncorrectPassword() throws Exception {
        // given
        UserDeleteRequest deleteRequest = new UserDeleteRequest("wrongPassword");
        doThrow(new ApiException(ErrorStatus.BAD_REQUEST_PASSWORD))
                .when(userService).deleteUser(any(CustomUserDetails.class), any(UserDeleteRequest.class));

        // when, then
        mockMvc.perform(RestDocumentationRequestBuilders.delete("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"password\":\"wrongPassword\"}"))
                .andExpect(status().isBadRequest()) // 400 Bad Request 기대
                .andDo(document("delete-user-fail-wrong-password",
                        responseFields(
                                fieldWithPath("message").description("에러 메시지"),
                                fieldWithPath("statusCode").description("응답 상태 코드"),
                                fieldWithPath("data").description("null 값, 추가 데이터는 없음")
                        )
                ));
    }

    @Test
    @WithMockUser
    void updateUserAddressSuccess() throws Exception {
        // given
        String jsonBody = "{ \"longitude\": 126.9780, \"latitude\": 37.5665 }";

        // when, then
        mockMvc.perform(patch("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonBody)) // JSON body로 데이터 전달
                .andExpect(status().isOk())
                .andDo(document("update-user-address",
                        requestFields( // requestFields로 JSON body를 문서화
                                fieldWithPath("longitude").description("The longitude of the user address"),
                                fieldWithPath("latitude").description("The latitude of the user address")
                        )
                ));
    }
}
