package com.sprarta.sproutmarket.domain.chat.controller;

import com.epages.restdocs.apispec.MockMvcRestDocumentationWrapper;
import com.epages.restdocs.apispec.ResourceSnippetParameters;
import com.epages.restdocs.apispec.Schema;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprarta.sproutmarket.config.JwtUtil;
import com.sprarta.sproutmarket.config.SecurityConfig;
import com.sprarta.sproutmarket.domain.tradeChat.controller.TradeChatController;
import com.sprarta.sproutmarket.domain.tradeChat.dto.TradeChatDto;
import com.sprarta.sproutmarket.domain.tradeChat.repository.TradeChatRepository;
import com.sprarta.sproutmarket.domain.tradeChat.service.TradeChatService;
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
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.util.List;

import static com.epages.restdocs.apispec.ResourceDocumentation.*;
import static org.mockito.Mockito.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(TradeChatController.class)
@Import(SecurityConfig.class)
@MockBean(JpaMetamodelMappingContext.class)
@ExtendWith(RestDocumentationExtension.class)
@AutoConfigureRestDocs(outputDir = "build/generated-snippets")
@AutoConfigureMockMvc(addFilters = false)
public class TradeChatControllerTest {

    @MockBean
    private TradeChatService tradeChatService;
    @MockBean
    private CustomUserDetailService customUserDetailService;
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private JwtUtil jwtUtil;
    @Autowired
    private ObjectMapper objectMapper;
    @MockBean
    private SimpMessageSendingOperations messagingTemplate;
    @MockBean
    private CustomUserDetails mockAuthUser;
    @MockBean
    private TradeChatRepository tradeChatRepository;
    @InjectMocks
    private TradeChatController tradeChatController;

    private User mockUser;

    @BeforeEach
    public void setup() throws Exception {
        MockitoAnnotations.openMocks(this);

        mockUser = new User("name", "email@email.com", "ABcd2Fg*", "nick", "01012345678", "address name here", UserRole.USER);
        mockAuthUser = new CustomUserDetails(mockUser);

        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(mockAuthUser, null, mockAuthUser.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authentication);

    }

    @Test
    @WithMockUser
    void 채팅_조회() throws Exception {
        Long roomId = 1L;
        TradeChatDto chatDto1 = new TradeChatDto(roomId, "sender1", "content1", 1L);
        TradeChatDto chatDto2 = new TradeChatDto(roomId, "sender1", "content2", 1L);
        TradeChatDto chatDto3 = new TradeChatDto(roomId, "sender2", "content3", 1L);
        List<TradeChatDto> chatList = List.of(chatDto1, chatDto2, chatDto3);

        when(tradeChatService.getChats(anyLong(), any(CustomUserDetails.class))).thenReturn(chatList);

        ResultActions result = mockMvc.perform(RestDocumentationRequestBuilders.get("/chatRooms/{roomId}/chats", roomId)
                        .header("Authorization", "Bearer (JWT 토큰)"))
                .andDo(MockMvcRestDocumentationWrapper.document(
                        "get-chats",
                        resource(ResourceSnippetParameters.builder()
                                .description("채팅방 id로 채팅 조회")
                                .summary("채팅 조회")
                                .tag("chat")
                                .requestHeaders(
                                        headerWithName("Authorization")
                                                .description("Bearer (JWT 토큰)")
                                )
                                .pathParameters(
                                        parameterWithName("roomId").description("조회할 채팅방 ID")
                                )
                                .responseFields(
                                        fieldWithPath("message")
                                                .description("성공 메시지 : Ok"),
                                        fieldWithPath("statusCode")
                                                .description("성공 상태 코드 : 200"),
                                        fieldWithPath("data")
                                                .description("본문 응답"),
                                        fieldWithPath("data[].roomId")
                                                .description("채팅방 ID"),
                                        fieldWithPath("data[].sender")
                                                .description("채팅 보낸 사용자"),
                                        fieldWithPath("data[].content")
                                                .description("채팅 내용"),
                                        fieldWithPath("data[].readCount")
                                                .description("읽음 확인")
                                )
                                .responseSchema(Schema.schema("특정-채팅방-채팅-다건-조회-성공-응답"))
                                .build()
                        )));

        result.andExpect(status().isOk())
                .andExpect(jsonPath("$.data", Matchers.hasSize(3)));

    }

}
