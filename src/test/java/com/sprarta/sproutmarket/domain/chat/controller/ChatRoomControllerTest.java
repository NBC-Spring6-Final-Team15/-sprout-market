package com.sprarta.sproutmarket.domain.chat.controller;

import com.epages.restdocs.apispec.MockMvcRestDocumentationWrapper;
import com.epages.restdocs.apispec.ResourceSnippetParameters;
import com.epages.restdocs.apispec.Schema;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprarta.sproutmarket.config.JwtUtil;
import com.sprarta.sproutmarket.config.SecurityConfig;
import com.sprarta.sproutmarket.domain.tradeChat.controller.ChatRoomController;
import com.sprarta.sproutmarket.domain.tradeChat.dto.ChatRoomDto;
import com.sprarta.sproutmarket.domain.tradeChat.repository.ChatRoomRepository;
import com.sprarta.sproutmarket.domain.tradeChat.service.ChatRoomService;
import com.sprarta.sproutmarket.domain.user.entity.CustomUserDetails;
import com.sprarta.sproutmarket.domain.user.entity.User;
import com.sprarta.sproutmarket.domain.user.enums.UserRole;
import com.sprarta.sproutmarket.domain.user.service.CustomUserDetailService;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.http.MediaType;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.util.List;

import static com.epages.restdocs.apispec.ResourceDocumentation.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ChatRoomController.class)
@Import(SecurityConfig.class)
@MockBean(JpaMetamodelMappingContext.class)
@ExtendWith(RestDocumentationExtension.class)
@AutoConfigureRestDocs(outputDir = "build/generated-snippets")
@AutoConfigureMockMvc(addFilters = false)
public class ChatRoomControllerTest {

    @MockBean
    private ChatRoomService chatRoomService;
    @MockBean
    private CustomUserDetailService customUserDetailService;
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private JwtUtil jwtUtil;
    @Autowired
    private ObjectMapper objectMapper;
    @MockBean
    private JpaMetamodelMappingContext jpaMappingContext;
    @MockBean
    private CustomUserDetails mockAuthUser;
    @MockBean
    private ChatRoomRepository chatRoomRepository;
    @InjectMocks
    private ChatRoomController chatRoomController;

    private Long itemId;
    private ChatRoomDto chatRoomDto;
    private User mockUser;

    @BeforeEach
    void setup(RestDocumentationContextProvider restDocumentation) {
        MockitoAnnotations.openMocks(this);

        mockUser = new User(1L, "username", "email@email.com", "ABcd2Fg*", "nickname", "01012345678", "address", UserRole.USER);
        mockAuthUser = new CustomUserDetails(mockUser);

        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(mockAuthUser, null, mockAuthUser.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authentication);

        itemId = 1L;
        chatRoomDto = new ChatRoomDto(
                1L,
                2L,
                1L
        );
    }

    @Test
    @WithMockUser
    void 채팅방_생성_성공() throws Exception {
        //when
        when(chatRoomService.createChatRoom(anyLong(), any(CustomUserDetails.class))).thenReturn(chatRoomDto);

        ResultActions result = mockMvc.perform(RestDocumentationRequestBuilders.post("/items/{itemId}/chatrooms", itemId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(chatRoomDto))
                        .header("Authorization", "Bearer (JWT 토큰)"))
                .andDo(MockMvcRestDocumentationWrapper.document("create-report",
                                resource(ResourceSnippetParameters.builder()
                                        .description("특정 아이템에 대한 채팅방 생성")
                                        .summary("채팅방 생성")
                                        .tag("chatroom")
                                        .pathParameters(
                                                parameterWithName("itemId")
                                                        .description("채팅방 생성할 매물 ID")
                                        )
                                        .requestHeaders(
                                                headerWithName("Authorization")
                                                        .description("Bearer (JWT 토큰)")
                                        )
                                        .responseFields(
                                                fieldWithPath("message")
                                                        .description("성공 메시지 : Ok"),
                                                fieldWithPath("statusCode")
                                                        .description("성공 상태 코드 : 200"),
                                                fieldWithPath("data")
                                                        .description("본문 응답"),
                                                fieldWithPath("data.buyerId")
                                                        .description("구매자 Id"),
                                                fieldWithPath("data.sellerId")
                                                        .description("판매자 Id"),
                                                fieldWithPath("data.itemId")
                                                        .description("채팅방이 만들어진 매물 Id")
                                        )
                                        .requestSchema(Schema.schema("채팅방-생성-성공-요청"))
                                        .responseSchema(Schema.schema("채팅방-생성-성공-응답"))
                                        .build()
                                )));
        // then
        result.andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.buyerId").value(1L))
                .andExpect(jsonPath("$.data.sellerId").value(2L))
                .andExpect(jsonPath("$.data.itemId").value(1L));
    }

    @Test
    @WithMockUser
    void 채팅방_조회_성공() throws Exception {

        // given
        Long chatRoomId = 1L;

        when(chatRoomService.getChatRoom(anyLong(), any(CustomUserDetails.class))).thenReturn(chatRoomDto);

        ResultActions result = mockMvc.perform(RestDocumentationRequestBuilders.get("/chatrooms/{chatRoomId}", chatRoomId)
                        .header("Authorization", "Bearer (JWT 토큰)"))
                .andDo(MockMvcRestDocumentationWrapper.document(
                        "get-a-chatroom",
                        resource(ResourceSnippetParameters.builder()
                                .description("채팅방 Id로 채팅방을 단건 조회합니다.")
                                .summary("채팅방 단건 조회")
                                .tag("chatroom")
                                .requestHeaders(
                                        headerWithName("Authorization")
                                                .description("Bearer (JWT 토큰)")
                                )
                                .pathParameters(
                                        parameterWithName("chatRoomId").description("조회할 채팅방 ID")
                                )
                                .responseFields(
                                        fieldWithPath("message")
                                                .description("성공 메시지 : Ok"),
                                        fieldWithPath("statusCode")
                                                .description("성공 상태 코드 : 200"),
                                        fieldWithPath("data")
                                                .description("본문 응답"),
                                        fieldWithPath("data.buyerId")
                                                .description("구매자 Id"),
                                        fieldWithPath("data.sellerId")
                                                .description("판매자 Id"),
                                        fieldWithPath("data.itemId")
                                                .description("채팅방이 만들어진 매물 Id")
                                )
                                .responseSchema(Schema.schema("채팅방-단건-조회-성공-응답"))
                                .build()

                        )));

        // then
        result.andExpect(status().isOk());
        result.andExpect(jsonPath("$.statusCode").value(200));

    }

    @Test
    @WithMockUser
    void 채팅방_목록_조회_성공() throws Exception {

        // given
        ChatRoomDto chatRoomDto2 = new ChatRoomDto(
                1L,
                2L,
                3L
        );

        List<ChatRoomDto> chatRoomDtoList = List.of(chatRoomDto, chatRoomDto2);

        when(chatRoomService.getChatRooms(any(CustomUserDetails.class))).thenReturn(chatRoomDtoList);

        ResultActions result = mockMvc.perform(RestDocumentationRequestBuilders.get("/chatrooms")
                        .header("Authorization", "Bearer (JWT 토큰)"))
                .andDo(MockMvcRestDocumentationWrapper.document(
                        "get-chatrooms",
                        resource(
                                ResourceSnippetParameters.builder()
                                        .description("사용자 채팅방 목록 조회")
                                        .summary("사용자 채팅방 목록 조회")
                                        .tag("chatroom")
                                        .requestHeaders(
                                                headerWithName("Authorization")
                                                        .description("Bearer (JWT 토큰)")
                                        )
                                        .responseFields(
                                                fieldWithPath("message")
                                                        .description("성공 메시지 : Ok"),
                                                fieldWithPath("statusCode")
                                                        .description("성공 상태 코드 : 200"),
                                                fieldWithPath("data")
                                                        .description("본문 응답"),
                                                fieldWithPath("data[].buyerId")
                                                        .description("구매자 Id"),
                                                fieldWithPath("data[].sellerId")
                                                        .description("판매자 Id"),
                                                fieldWithPath("data[].itemId")
                                                        .description("채팅방이 만들어진 매물 ID")
                                        )
                                        .responseSchema(Schema.schema("특정-사용자-채팅방-다건-조회-성공-응답"))
                                        .build()
                        )));

        // then
        result.andExpect(status().isOk())
                .andExpect(jsonPath("$.data", Matchers.hasSize(2)));

    }

    @Test
    @WithMockUser
    void 채팅방_삭제_성공() throws Exception {

        // given
        Long chatRoomId = 1L;

        doNothing().when(chatRoomService).deleteChatRoom(anyLong(),any(CustomUserDetails.class));

        // when
        ResultActions result = mockMvc.perform(RestDocumentationRequestBuilders.delete("/chatrooms/{chatRoomId}", chatRoomId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer (JWT 토큰)"))
                .andDo(MockMvcRestDocumentationWrapper.document("update-report",
                        resource(ResourceSnippetParameters.builder()
                                .description("채팅방 Id를 받아 해당 채팅방 삭제")
                                .summary("채팅방 삭제")
                                .tag("chatroom")
                                .pathParameters(
                                        parameterWithName("chatRoomId")
                                                .description("삭제할 채팅방 Id")
                                )
                                .requestHeaders(
                                        headerWithName("Authorization")
                                                .description("Bearer (JWT 토큰)")
                                )
                                .responseFields(
                                        fieldWithPath("message")
                                                .description("성공 메시지 : Ok"),
                                        fieldWithPath("statusCode")
                                                .description("성공 상태 코드 : 200")
                                )
                                .responseSchema(Schema.schema("채팅방-삭제-성공-응답"))
                                .build()
                        )));

        // then
        result.andExpect(status().isOk())
                .andExpect(jsonPath("$.statusCode").value(200));

    }

}
