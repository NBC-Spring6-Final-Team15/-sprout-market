package com.sprarta.sproutmarket.domain.chat.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprarta.sproutmarket.config.JwtUtil;
import com.sprarta.sproutmarket.config.SecurityConfig;
import com.sprarta.sproutmarket.config.WebSocketConfig;
import com.sprarta.sproutmarket.domain.item.controller.ItemController;
import com.sprarta.sproutmarket.domain.item.service.ItemService;
import com.sprarta.sproutmarket.domain.tradeChat.controller.TradeChatController;
import com.sprarta.sproutmarket.domain.tradeChat.dto.TradeChatDto;
import com.sprarta.sproutmarket.domain.tradeChat.repository.TradeChatRepository;
import com.sprarta.sproutmarket.domain.tradeChat.service.TradeChatService;
import com.sprarta.sproutmarket.domain.user.entity.CustomUserDetails;
import com.sprarta.sproutmarket.domain.user.entity.User;
import com.sprarta.sproutmarket.domain.user.enums.UserRole;
import com.sprarta.sproutmarket.domain.user.service.CustomUserDetailService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandlerAdapter;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;
import org.springframework.web.socket.sockjs.client.SockJsClient;
import org.springframework.web.socket.sockjs.client.Transport;
import org.springframework.web.socket.sockjs.client.WebSocketTransport;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.fail;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT) // 웹 포트 지정
@Import({SecurityConfig.class, WebSocketConfig.class})
public class WebSocketTest {

    @MockBean
    private TradeChatService tradeChatService;
    @MockBean
    private CustomUserDetailService customUserDetailService;
    @MockBean
    private JwtUtil jwtUtil;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private SimpMessagingTemplate simpMessagingTemplate;
    @Autowired
    private WebSocketConfig webSocketConfig;
    @MockBean
    private CustomUserDetails mockAuthUser;
    @MockBean
    private TradeChatRepository tradeChatRepository;
    @Autowired
    private TradeChatController tradeChatController;
    @InjectMocks
    private ItemController itemController;
    @MockBean
    private ItemService itemService;

    private WebSocketStompClient stompClient;
    private StompSession stompSession;
    private User mockUser;

    public void StompSupport() {
        this.stompClient = new WebSocketStompClient(new SockJsClient(createTransport()));
        this.stompClient.setMessageConverter(new MappingJackson2MessageConverter());
    } // 웹 소켓 STOMP 클라이언트 생성 , 통신 , 웹 소켓 전송 방식 정의

    @BeforeEach
    public void connect() throws Exception {
        MockitoAnnotations.openMocks(this);

        mockUser = new User(1L, "username", "email@email.com", "ABcd2Fg*", "nickname", "01012345678", "address", UserRole.USER);
        mockAuthUser = new CustomUserDetails(mockUser);

        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(mockAuthUser, null, mockAuthUser.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authentication);

        StompSupport();

        this.stompSession = this.stompClient.
                connect("ws://localhost:8080/ws",
                        new StompSessionHandlerAdapter() {})
                .get(5, TimeUnit.SECONDS); // 연결이 완료될 때 까지 최대 5초 대기
    } // 지정 url에 연결 , 유저 설정

    @AfterEach
    public void disconnect() {
        if (this.stompSession.isConnected()){
            this.stompSession.disconnect();
        }
    } // 연결 종료

    private List<Transport> createTransport() {
        List<Transport> transports = new ArrayList<>(1);
        transports.add(new WebSocketTransport(new StandardWebSocketClient()));
        return transports;
    } // StandardWebSocketClient를 사용하여 WebSocketTransport 생성

    @Test
    @WithMockUser
    void 채팅_전송() throws Exception {
        Long roomId = 1L;
        TradeChatDto chatDto = new TradeChatDto(roomId, "sender", "content");
        CountDownLatch latch = new CountDownLatch(1); // 비동기 작업이 완료될 때까지 대기

        // 구독 sub
        stompSession.subscribe("/sub/chat/" + roomId, new StompSessionHandlerAdapter() {
            @Override
            public Type getPayloadType(StompHeaders headers) {
                return TradeChatDto.class;
            } // 페이로드 변환 타입 지정

            @Override
            public void handleFrame(StompHeaders headers, Object payload) {
                TradeChatDto receivedMessage = (TradeChatDto) payload;
                assertThat(receivedMessage.getRoomId()).isEqualTo(roomId);
                assertThat(receivedMessage.getSender()).isEqualTo("sender");
                assertThat(receivedMessage.getContent()).isEqualTo("content");

                latch.countDown(); // 예상한 대로 수신되면 대기 해제
            } // 전달받은 메시지 내용 검증

        });

        // 발행 pub
        stompSession.send("/pub/chat/" + roomId, chatDto);

        boolean messageReceived = latch.await(5, TimeUnit.SECONDS); //수신할 때까지 대기, 수신 여부 검증
        assertThat(messageReceived).isTrue();

    }

}