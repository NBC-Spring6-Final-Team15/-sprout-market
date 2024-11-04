package com.sprarta.sproutmarket.domain.item.controller;

import com.epages.restdocs.apispec.MockMvcRestDocumentationWrapper;
import com.epages.restdocs.apispec.ResourceSnippetParameters;
import com.epages.restdocs.apispec.Schema;
import com.sprarta.sproutmarket.domain.CommonMockMvcControllerTestSetUp;
import com.sprarta.sproutmarket.domain.category.entity.Category;
import com.sprarta.sproutmarket.domain.common.entity.Status;
import com.sprarta.sproutmarket.domain.image.itemImage.entity.ItemImage;
import com.sprarta.sproutmarket.domain.item.dto.request.FindItemsInMyAreaRequestDto;
import com.sprarta.sproutmarket.domain.item.dto.request.ItemContentsUpdateRequest;
import com.sprarta.sproutmarket.domain.item.dto.request.ItemCreateRequest;
import com.sprarta.sproutmarket.domain.item.dto.request.ItemSearchRequest;
import com.sprarta.sproutmarket.domain.item.dto.response.ItemResponse;
import com.sprarta.sproutmarket.domain.item.dto.response.ItemResponseDto;
import com.sprarta.sproutmarket.domain.item.dto.response.ItemSearchResponse;
import com.sprarta.sproutmarket.domain.item.entity.Item;
import com.sprarta.sproutmarket.domain.item.entity.ItemSaleStatus;
import com.sprarta.sproutmarket.domain.item.repository.ItemRepository;
import com.sprarta.sproutmarket.domain.item.service.ItemService;
import com.sprarta.sproutmarket.domain.user.dto.request.UserChangePasswordRequest;
import com.sprarta.sproutmarket.domain.user.dto.request.UserDeleteRequest;
import com.sprarta.sproutmarket.domain.user.dto.response.UserResponse;
import com.sprarta.sproutmarket.domain.user.entity.CustomUserDetails;
import com.sprarta.sproutmarket.domain.user.entity.User;
import com.sprarta.sproutmarket.domain.user.enums.UserRole;
import com.sprarta.sproutmarket.domain.user.service.UserService;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static com.epages.restdocs.apispec.MockMvcRestDocumentationWrapper.document;
import static com.epages.restdocs.apispec.ResourceDocumentation.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.multipart;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ItemController.class)
@AutoConfigureMockMvc(addFilters = false)
class ItemControllerTest extends CommonMockMvcControllerTestSetUp {
    @MockBean
    private ItemService itemService;
    @MockBean
    private ItemRepository itemRepository;
    @MockBean
    private UserService userService;

    private Item mockItem;
    private Category mockCategory;
    private ItemImage itemImage;
    private User mockUser;
    private MockMultipartFile mockImage;

    @BeforeEach
        // 테스트 전 수행
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockImage = new MockMultipartFile("file", "itemImage.jpg", "image/jpeg", "itemImage content".getBytes());

        // 클래스 인스턴스 생성
        mockUser = new User("김지민", "mock@mock.com", "encodedOldPassword", "오만한천원", "010-1234-5678", "서울특별시 관악구 신림동", UserRole.USER);
        ReflectionTestUtils.setField(mockUser, "id", 1L);
        // CustomUserDetails mockAuthUser = new CustomUserDetails(mockUser);
        mockAuthUser = new CustomUserDetails(mockUser);
        itemImage = ItemImage.builder()
            .id(1L)
            .item(mockItem)
            .name("https://sprout-market.s3.ap-northeast-2.amazonaws.com/4da210e1-7.jpg")
            .build();

        // 객체 생성
        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(mockAuthUser, null, mockAuthUser.getAuthorities());
        // 인증 정보 설정(인증된 사용자 정보 사용O)
        SecurityContextHolder.getContext().setAuthentication(authentication);

        mockCategory = new Category("청소");

        // Mock Item 생성
        mockItem =  new Item(
            "가짜 아이템",
            "가짜 설명",
            10000,
            mockUser,
            ItemSaleStatus.WAITING,
            mockCategory,
            Status.ACTIVE
        );

        ReflectionTestUtils.setField(mockItem, "id", 1L);
        ReflectionTestUtils.setField(mockCategory, "id", 1L);

        // 아이템을 반환하도록 Mock 설정
        given(itemRepository.findByIdAndSellerIdOrElseThrow(mockItem.getId(), mockUser)).willReturn(mockItem);
        // UserService Mock 설정
        when(userService.getUser(anyLong())).thenReturn(new UserResponse(mockUser.getId(), mockUser.getEmail()));
        doNothing().when(userService).changePassword(any(CustomUserDetails.class), any(UserChangePasswordRequest.class));
        doNothing().when(userService).deleteUser(any(CustomUserDetails.class), any(UserDeleteRequest.class));
    }

    @Test
    @WithMockUser
    void 매물_검색_성공 () throws Exception {
        ItemSearchRequest requestDto = ItemSearchRequest.builder()
            .searchKeyword("1")
            .categoryId(1L)
            .saleStatus(true)
            .build();

        //페이지 직접 만들어주기
        List<ItemSearchResponse> responseList = new ArrayList<>();
        for (int i = 1; i <= 5; i++) {
            ItemSearchResponse response = new ItemSearchResponse(
                (long) i,
                "제목" + i,
                15000,
                "주소" + i,
                "url" + i,
                LocalDateTime.now() // 생성 날짜
            );
            responseList.add(response);
        }

        Pageable pageable = PageRequest.of(0 , 10);
        Page<ItemSearchResponse> pageResult = new PageImpl<>(responseList, pageable, responseList.size());
        given(itemService.searchItems(anyInt(),anyInt(),any(ItemSearchRequest.class),any(CustomUserDetails.class)))
            .willReturn(pageResult);

        ResultActions result = mockMvc.perform(RestDocumentationRequestBuilders.post("/items/search")
                .contentType(MediaType.APPLICATION_JSON)
                .param("page", String.valueOf(1))
                .param("size", String.valueOf(10))
                .content(objectMapper.writeValueAsString(requestDto))
                .header("Authorization", "Bearer (JWT 토큰)"))
            .andDo(MockMvcRestDocumentationWrapper.document(
                "search-items-by-my-areas",
                resource(ResourceSnippetParameters.builder()
                    .description("우리 동네의 검색 조건에 속하는 매물을 조회합니다.")
                    .summary("조건에 해당하는 우리 동네 매물 조회")
                    .tag("Items")
                    .requestHeaders(
                        headerWithName("Authorization")
                            .description("Bearer (JWT 토큰)")
                    )
                    .queryParameters(
                        parameterWithName("page").description("페이지 넘버, 기본값 1, 최소값 1"),
                        parameterWithName("size").description("페이지 크기, 기본값 10, 최소값 1")
                    )
                    .requestFields(
                        fieldWithPath("searchKeyword").type(JsonFieldType.STRING)
                            .description("검색 명"),
                        fieldWithPath("categoryId").type(JsonFieldType.NUMBER)
                            .description("검색할 카테고리 아이디"),
                        fieldWithPath("saleStatus").type(JsonFieldType.BOOLEAN)
                            .description("판매중")
                    )
                    .responseFields(
                        fieldWithPath("message").type(JsonFieldType.STRING)
                            .description("성공 상태 메시지"),
                        fieldWithPath("statusCode").type(JsonFieldType.NUMBER)
                            .description("성공 시 응답 코드 : 200"),
                        fieldWithPath("data").type(JsonFieldType.OBJECT)
                            .description("응답 본문"),
                        fieldWithPath("data.content").type(JsonFieldType.ARRAY)
                            .description("아이템 리스트"),
                        fieldWithPath("data.content[].id").type(JsonFieldType.NUMBER)
                            .description("아이템 ID"),
                        fieldWithPath("data.content[].title").type(JsonFieldType.STRING)
                            .description("아이템 제목"),
                        fieldWithPath("data.content[].price").type(JsonFieldType.NUMBER)
                            .description("아이템 가격"),
                        fieldWithPath("data.content[].address").type(JsonFieldType.STRING)
                            .description("판매자 행정동"),
                        fieldWithPath("data.content[].imageUrl").type(JsonFieldType.STRING)
                            .description("이미지 Url"),
                        fieldWithPath("data.content[].createAt").type(JsonFieldType.STRING)
                            .description("생성일"),
                        fieldWithPath("data.pageable").type(JsonFieldType.OBJECT)
                            .description("페이지 관련 정보"),
                        fieldWithPath("data.pageable.pageNumber").type(JsonFieldType.NUMBER)
                            .description("현재 페이지 번호 (0부터 시작)"),
                        fieldWithPath("data.pageable.pageSize").type(JsonFieldType.NUMBER)
                            .description("페이지 크기"),
                        fieldWithPath("data.pageable.sort").type(JsonFieldType.OBJECT)
                            .description("정렬 정보"),
                        fieldWithPath("data.pageable.sort.empty").type(JsonFieldType.BOOLEAN)
                            .description("정렬 정보가 비어 있는지 여부"),
                        fieldWithPath("data.pageable.sort.sorted").type(JsonFieldType.BOOLEAN)
                            .description("정렬되었는지 여부"),
                        fieldWithPath("data.pageable.sort.unsorted").type(JsonFieldType.BOOLEAN)
                            .description("정렬되지 않았는지 여부"),
                        fieldWithPath("data.pageable.offset").type(JsonFieldType.NUMBER)
                            .description("현재 페이지의 시작점 오프셋"),
                        fieldWithPath("data.pageable.paged").type(JsonFieldType.BOOLEAN)
                            .description("페이징 여부"),
                        fieldWithPath("data.pageable.unpaged").type(JsonFieldType.BOOLEAN)
                            .description("페이징되지 않은 여부"),
                        fieldWithPath("data.last").type(JsonFieldType.BOOLEAN)
                            .description("마지막 페이지인지 여부"),
                        fieldWithPath("data.totalPages").type(JsonFieldType.NUMBER)
                            .description("전체 페이지 수"),
                        fieldWithPath("data.totalElements").type(JsonFieldType.NUMBER)
                            .description("전체 아이템 수"),
                        fieldWithPath("data.size").type(JsonFieldType.NUMBER)
                            .description("페이지 크기"),
                        fieldWithPath("data.number").type(JsonFieldType.NUMBER)
                            .description("현재 페이지 번호"),
                        fieldWithPath("data.sort").type(JsonFieldType.OBJECT)
                            .description("현재 페이지의 정렬 정보"),
                        fieldWithPath("data.sort.empty").type(JsonFieldType.BOOLEAN)
                            .description("현재 페이지의 정렬 정보가 비어 있는지 여부"),
                        fieldWithPath("data.sort.sorted").type(JsonFieldType.BOOLEAN)
                            .description("현재 페이지가 정렬되었는지 여부"),
                        fieldWithPath("data.sort.unsorted").type(JsonFieldType.BOOLEAN)
                            .description("현재 페이지가 정렬되지 않았는지 여부"),
                        fieldWithPath("data.first").type(JsonFieldType.BOOLEAN)
                            .description("첫 번째 페이지인지 여부"),
                        fieldWithPath("data.numberOfElements").type(JsonFieldType.NUMBER)
                            .description("현재 페이지에 있는 아이템 수"),
                        fieldWithPath("data.empty").type(JsonFieldType.BOOLEAN)
                            .description("현재 페이지가 비어 있는지 여부")
                    )
                    .requestSchema(Schema.schema("동네-특정-조건-매물-검색-성공-요청"))
                    .responseSchema(Schema.schema("동네-특정-조건-매물-검색-성공-응답"))
                    .build()
                )
            ));
        verify(itemService,times(1)).searchItems(anyInt(),anyInt(),any(ItemSearchRequest.class),any(CustomUserDetails.class));
        result.andExpect(status().isOk())
            .andExpect(jsonPath("$.data.totalElements").value(responseList.size()));
    }

    @Test
    @WithMockUser
    void 내_주변_특정카테고리_매물_조회_성공() throws Exception {
        Long categoryId = 1L;
        FindItemsInMyAreaRequestDto requestDto = FindItemsInMyAreaRequestDto.builder()
            .page(1)
            .size(10)
            .build();
        //페이지 직접 만들어주기
        List<ItemResponseDto> dtoList = new ArrayList<>();
        for(int i = 1; i <= 5; i++) {
            ItemResponseDto dto = new ItemResponseDto(
                (long) i, //
                "제목" + i,
                "내용" + i,
                15000,
                "닉네임" + i,
                ItemSaleStatus.WAITING,
                mockCategory.getName(),
                Status.ACTIVE
            );
            dtoList.add(dto);
        }

        Pageable pageable = PageRequest.of(requestDto.getPage() - 1 , requestDto.getSize());
        Page<ItemResponseDto> pageResult = new PageImpl<>(dtoList,pageable,dtoList.size());

        given(itemService.getCategoryItems(any(FindItemsInMyAreaRequestDto.class),any(Long.class),any(CustomUserDetails.class))).willReturn(pageResult);
        ResultActions result = mockMvc.perform(RestDocumentationRequestBuilders.get("/items/category/{categoryId}", categoryId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDto))
                .header("Authorization", "Bearer (JWT 토큰)"))
            .andDo(MockMvcRestDocumentationWrapper.document(
                "get-items-by-my-areas-and-category",
                resource(ResourceSnippetParameters.builder()
                    .description("우리 동네의 특정 카테고리에 속하는 매물을 조회합니다.")
                    .summary("동네 특정 카테고리 매물 조회")
                    .tag("Items")
                    .requestHeaders(
                        headerWithName("Authorization")
                            .description("Bearer (JWT 토큰)")
                    )
                    .requestFields(
                        fieldWithPath("page").type(JsonFieldType.NUMBER)
                            .description("페이지 넘버, 기본값 1, 최소값 1"),
                        fieldWithPath("size").type(JsonFieldType.NUMBER)
                            .description("페이지 크기, 기본값 10, 최소값 1")
                    )
                    .responseFields(
                        fieldWithPath("message").type(JsonFieldType.STRING)
                            .description("성공 상태 메시지"),
                        fieldWithPath("statusCode").type(JsonFieldType.NUMBER)
                            .description("성공 시 응답 코드 : 200"),
                        fieldWithPath("data").type(JsonFieldType.OBJECT)
                            .description("응답 본문"),
                        fieldWithPath("data.content").type(JsonFieldType.ARRAY)
                            .description("아이템 리스트"),
                        fieldWithPath("data.content[].id").type(JsonFieldType.NUMBER)
                            .description("아이템 ID"),
                        fieldWithPath("data.content[].title").type(JsonFieldType.STRING)
                            .description("아이템 제목"),
                        fieldWithPath("data.content[].description").type(JsonFieldType.STRING)
                            .description("아이템 설명"),
                        fieldWithPath("data.content[].price").type(JsonFieldType.NUMBER)
                            .description("아이템 가격"),
                        fieldWithPath("data.content[].nickname").type(JsonFieldType.STRING)
                            .description("판매자 닉네임"),
                        fieldWithPath("data.content[].itemSaleStatus").type(JsonFieldType.STRING)
                            .description("아이템 판매 상태"),
                        fieldWithPath("data.content[].categoryName").type(JsonFieldType.STRING)
                            .description("아이템 카테고리 이름"),
                        fieldWithPath("data.content[].status").type(JsonFieldType.STRING)
                            .description("아이템 상태 (예: ACTIVE, INACTIVE)"),
                        fieldWithPath("data.pageable").type(JsonFieldType.OBJECT)
                            .description("페이지 관련 정보"),
                        fieldWithPath("data.pageable.pageNumber").type(JsonFieldType.NUMBER)
                            .description("현재 페이지 번호 (0부터 시작)"),
                        fieldWithPath("data.pageable.pageSize").type(JsonFieldType.NUMBER)
                            .description("페이지 크기"),
                        fieldWithPath("data.pageable.sort").type(JsonFieldType.OBJECT)
                            .description("정렬 정보"),
                        fieldWithPath("data.pageable.sort.empty").type(JsonFieldType.BOOLEAN)
                            .description("정렬 정보가 비어 있는지 여부"),
                        fieldWithPath("data.pageable.sort.sorted").type(JsonFieldType.BOOLEAN)
                            .description("정렬되었는지 여부"),
                        fieldWithPath("data.pageable.sort.unsorted").type(JsonFieldType.BOOLEAN)
                            .description("정렬되지 않았는지 여부"),
                        fieldWithPath("data.pageable.offset").type(JsonFieldType.NUMBER)
                            .description("현재 페이지의 시작점 오프셋"),
                        fieldWithPath("data.pageable.paged").type(JsonFieldType.BOOLEAN)
                            .description("페이징 여부"),
                        fieldWithPath("data.pageable.unpaged").type(JsonFieldType.BOOLEAN)
                            .description("페이징되지 않은 여부"),
                        fieldWithPath("data.last").type(JsonFieldType.BOOLEAN)
                            .description("마지막 페이지인지 여부"),
                        fieldWithPath("data.totalPages").type(JsonFieldType.NUMBER)
                            .description("전체 페이지 수"),
                        fieldWithPath("data.totalElements").type(JsonFieldType.NUMBER)
                            .description("전체 아이템 수"),
                        fieldWithPath("data.size").type(JsonFieldType.NUMBER)
                            .description("페이지 크기"),
                        fieldWithPath("data.number").type(JsonFieldType.NUMBER)
                            .description("현재 페이지 번호"),
                        fieldWithPath("data.sort").type(JsonFieldType.OBJECT)
                            .description("현재 페이지의 정렬 정보"),
                        fieldWithPath("data.sort.empty").type(JsonFieldType.BOOLEAN)
                            .description("현재 페이지의 정렬 정보가 비어 있는지 여부"),
                        fieldWithPath("data.sort.sorted").type(JsonFieldType.BOOLEAN)
                            .description("현재 페이지가 정렬되었는지 여부"),
                        fieldWithPath("data.sort.unsorted").type(JsonFieldType.BOOLEAN)
                            .description("현재 페이지가 정렬되지 않았는지 여부"),
                        fieldWithPath("data.first").type(JsonFieldType.BOOLEAN)
                            .description("첫 번째 페이지인지 여부"),
                        fieldWithPath("data.numberOfElements").type(JsonFieldType.NUMBER)
                            .description("현재 페이지에 있는 아이템 수"),
                        fieldWithPath("data.empty").type(JsonFieldType.BOOLEAN)
                            .description("현재 페이지가 비어 있는지 여부")
                    )
                    .requestSchema(Schema.schema("동네-특정-카테고리-매물-조회-성공-요청"))
                    .responseSchema(Schema.schema("동네-특정-카테고리-매물-조회-성공-응답"))
                    .build()
                )
            ));
        verify(itemService,times(1)).getCategoryItems(any(FindItemsInMyAreaRequestDto.class),any(Long.class),any(CustomUserDetails.class));
        result.andExpect(status().isOk())
            .andExpect(jsonPath("$.data.totalElements").value(dtoList.size()));
    }

    @Test
    @WithMockUser
    void 매물_수정_성공_혜민() throws Exception {
        ItemResponse itemResponse = ItemResponse.builder()
            .title("만년필")
            .description("한번도안썼습니다")
            .price(3000)
            .nickname("김커피")
            .build();
        Long itemId = 1L;

        given(itemService.updateContents(any(Long.class), any(ItemContentsUpdateRequest.class), any(CustomUserDetails.class))).willReturn(itemResponse);

        ResultActions result = mockMvc.perform(RestDocumentationRequestBuilders.put("/items/{itemId}/contents", itemId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"title\":\"만년필\",\"description\":\"한번도안썼습니다\",\"price\":3000}")
                        .header("Authorization", "Bearer (JWT 토큰)"))
                .andDo(
                        MockMvcRestDocumentationWrapper.document(
                                "update-Contents",
                                resource(ResourceSnippetParameters.builder()
                                        .description("매물의 정보를 변경합니다.")
                                        .pathParameters(
                                                parameterWithName("itemId").description("수정할 매물 ID")
                                        )
                                        .summary("매물 정보 업데이트")
                                        .tag("Items")
                                        .requestFields(
                                                fieldWithPath("title").type(JsonFieldType.STRING)
                                                        .description("수정할 제목"),
                                                fieldWithPath("description").type(JsonFieldType.STRING)
                                                        .description("수정할 내용"),
                                                fieldWithPath("price").type(JsonFieldType.NUMBER)
                                                        .description("수정할 가격")
                                        )
                                        .requestHeaders(
                                                headerWithName("Authorization")
                                                        .description("Bearer (JWT 토큰)")
                                        )
                                        .requestSchema(Schema.schema("매물-내용-수정-성공-요청"))
                                        .responseFields(
                                                fieldWithPath("message").type(JsonFieldType.STRING)
                                                        .description("성공 시 메시지"),
                                                fieldWithPath("statusCode").type(JsonFieldType.NUMBER)
                                                        .description("200 상태 코드"),
                                                fieldWithPath("data").type(JsonFieldType.OBJECT)
                                                        .description("반환된 정보"),
                                                fieldWithPath("data.title").type(JsonFieldType.STRING)
                                                        .description("수정된 제목"),
                                                fieldWithPath("data.description").type(JsonFieldType.STRING)
                                                        .description("수정된 내용"),
                                                fieldWithPath("data.price").type(JsonFieldType.NUMBER)
                                                        .description("수정된 가격"),
                                                fieldWithPath("data.nickname").type(JsonFieldType.STRING)
                                                        .description("수정한 유저 닉네임")
                                        )
                                        .responseSchema(Schema.schema("매물-내용-수정-성공-응답"))
                                        .build()
                                )
                        )
                );

        verify(itemService, times(1)).updateContents(any(Long.class), any(ItemContentsUpdateRequest.class), any(CustomUserDetails.class));
        result.andExpect(status().isOk())
                .andExpect(jsonPath("$.data.title").value(itemResponse.getTitle()))
                .andExpect(jsonPath("$.data.description").value(itemResponse.getDescription()))
                .andExpect(jsonPath("$.data.price").value(itemResponse.getPrice()));
    }

    @Test
    @WithMockUser
        // 인증된 사용자로 테스트
    void 매물_단건_상세_조회_성공() throws Exception {
        // Given
        Long itemId = mockItem.getId();
        ItemResponseDto itemResponseDto = new ItemResponseDto(
                mockItem.getId(),
                mockItem.getTitle(),
                mockItem.getDescription(),
                mockItem.getPrice(),
                mockItem.getSeller().getNickname(),
                mockItem.getItemSaleStatus(),
                mockItem.getCategory().getName(),
                mockItem.getStatus()
        );

        given(itemService.getItem(anyLong(), any(CustomUserDetails.class))).willReturn(itemResponseDto);

        // When, Then
        mockMvc.perform(RestDocumentationRequestBuilders.get("/items/{itemId}", itemId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer (JWT 토큰)")
                )
                .andDo(document("get-detail-items",
                        resource(ResourceSnippetParameters.builder()
                                .description("매물 단건 상세 조회 API")
                                .summary("로그인한 사용자가 특정 매물의 상세 조회를 합니다.")
                                .tag("Items")
                                .requestHeaders(
                                        headerWithName("Authorization")
                                                .description("Bearer (JWT 토큰)")
                                )
                                .responseFields(
                                        fieldWithPath("message").type(JsonFieldType.STRING).description("성공 응답 메시지"),
                                        fieldWithPath("statusCode").type(JsonFieldType.NUMBER).description("성공 시 상태 코드 : 200"),
                                        fieldWithPath("data").type(JsonFieldType.OBJECT).description("응답본문"),
                                        fieldWithPath("data.id").type(JsonFieldType.NUMBER).description("매물ID"),
                                        fieldWithPath("data.title").type(JsonFieldType.STRING).description("제목"),
                                        fieldWithPath("data.description").type(JsonFieldType.STRING).description("설명"),
                                        fieldWithPath("data.price").type(JsonFieldType.NUMBER).description("가격"),
                                        fieldWithPath("data.nickname").type(JsonFieldType.STRING).description("판매자 닉네임"),
                                        fieldWithPath("data.itemSaleStatus").type(JsonFieldType.STRING).description("판매 상태"),
                                        fieldWithPath("data.categoryName").type(JsonFieldType.STRING).description("카테고리 이름"),
                                        fieldWithPath("data.status").type(JsonFieldType.STRING).description("매물 활성 상태")
                                )
                                .responseSchema(Schema.schema("매물-상세조회-성공-응답"))
                                .build())
                ));

    }

    @Test
    @WithMockUser // 인증된 사용자로 테스트
    void 나의_모든_매물_조회_성공() throws Exception {
        // Given
        //페이지 직접 만들어주기
        FindItemsInMyAreaRequestDto requestDto = FindItemsInMyAreaRequestDto.builder()
            .page(1)
            .size(10)
            .build();;
        List<ItemResponseDto> dtoList = new ArrayList<>();
        for (int i = 1; i <= 2; i++) {
            ItemResponseDto dto = new ItemResponseDto(
                    (long) i, //
                    "제목" + i,
                    "내용" + i,
                    15000,
                    "닉네임" + i,
                    ItemSaleStatus.WAITING,
                    "카테고리 이름" + i,
                    Status.ACTIVE
            );
            dtoList.add(dto);
        }
        Pageable pageable = PageRequest.of(0, 10);
        Page<ItemResponseDto> pageResult = new PageImpl<>(dtoList, pageable, 2);
        given(itemService.getMyItems(any(int.class), any(int.class), any(CustomUserDetails.class))).willReturn(pageResult);

        // When, Then
        mockMvc.perform(RestDocumentationRequestBuilders.get("/items/mine")
                        .content(objectMapper.writeValueAsString(requestDto))
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer (JWT 토큰)")
                )
                .andDo(document("get-my-all-items",
                        resource(ResourceSnippetParameters.builder()
                                .description("나의 전체 매물 조회 API")
                                .summary("로그인한 사용자가 자신이 등록한 매물을 전체 조회합니다.")
                                .tag("Items")
                                .requestHeaders(
                                        headerWithName("Authorization")
                                                .description("Bearer (JWT 토큰)")
                                )
                                .requestFields(
                                        fieldWithPath("page").type(JsonFieldType.NUMBER).description("페이지 넘버, 기본값 1, 최소값 1"),
                                        fieldWithPath("size").type(JsonFieldType.NUMBER).description("페이지 크기, 기본값 10, 최소값 1")
                                )
                                .responseFields(
                                        fieldWithPath("message").type(JsonFieldType.STRING).description("성공 응답 메시지"),
                                        fieldWithPath("statusCode").type(JsonFieldType.NUMBER).description("성공 시 상태 코드 : 200"),
                                        fieldWithPath("data").type(JsonFieldType.OBJECT).description("응답본문"),
                                        fieldWithPath("data.content").type(JsonFieldType.ARRAY).description("아이템 리스트"),
                                        fieldWithPath("data.content[].id").type(JsonFieldType.NUMBER).description("아이템 ID"),
                                        fieldWithPath("data.content[].title").type(JsonFieldType.STRING).description("제목"),
                                        fieldWithPath("data.content[].description").type(JsonFieldType.STRING).description("설명"),
                                        fieldWithPath("data.content[].price").type(JsonFieldType.NUMBER).description("가격"),
                                        fieldWithPath("data.content[].nickname").type(JsonFieldType.STRING).description("닉네임"),
                                        fieldWithPath("data.content[].itemSaleStatus").type(JsonFieldType.STRING).description("판매상태"),
                                        fieldWithPath("data.content[].categoryName").type(JsonFieldType.STRING).description("카테고리 이름"),
                                        fieldWithPath("data.content[].status").type(JsonFieldType.STRING).description("활성상태"),
                                        fieldWithPath("data.pageable").type(JsonFieldType.OBJECT).description("페이지 관련 정보"),
                                        fieldWithPath("data.pageable.pageNumber").type(JsonFieldType.NUMBER).description("현재 페이지 번호 (0부터 시작)"),
                                        fieldWithPath("data.pageable.pageSize").type(JsonFieldType.NUMBER).description("페이지 크기"),
                                        fieldWithPath("data.pageable.sort").type(JsonFieldType.OBJECT).description("정렬 정보"),
                                        fieldWithPath("data.pageable.sort.empty").type(JsonFieldType.BOOLEAN).description("정렬 정보가 비어 있는지 여부"),
                                        fieldWithPath("data.pageable.sort.sorted").type(JsonFieldType.BOOLEAN).description("정렬되었는지 여부"),
                                        fieldWithPath("data.pageable.sort.unsorted").type(JsonFieldType.BOOLEAN).description("정렬되지 않았는지 여부"),
                                        fieldWithPath("data.pageable.offset").type(JsonFieldType.NUMBER).description("현재 페이지의 시작점 오프셋"),
                                        fieldWithPath("data.pageable.paged").type(JsonFieldType.BOOLEAN).description("페이징 여부"),
                                        fieldWithPath("data.pageable.unpaged").type(JsonFieldType.BOOLEAN).description("페이징되지 않은 여부"),
                                        fieldWithPath("data.last").type(JsonFieldType.BOOLEAN).description("마지막 페이지인지 여부"),
                                        fieldWithPath("data.totalElements").type(JsonFieldType.NUMBER).description("전체 아이템 수"),
                                        fieldWithPath("data.totalPages").type(JsonFieldType.NUMBER).description("전체 페이지 수"),
                                        fieldWithPath("data.size").type(JsonFieldType.NUMBER).description("페이지 크기"),
                                        fieldWithPath("data.number").type(JsonFieldType.NUMBER).description("현재 페이지 번호"),
                                        fieldWithPath("data.sort").type(JsonFieldType.OBJECT).description("현재 페이지의 정렬 정보"),
                                        fieldWithPath("data.sort.empty").type(JsonFieldType.BOOLEAN).description("현재 페이지의 정렬 정보가 비어 있는지 여부"),
                                        fieldWithPath("data.sort.sorted").type(JsonFieldType.BOOLEAN).description("현재 페이지가 정렬되었는지 여부"),
                                        fieldWithPath("data.sort.unsorted").type(JsonFieldType.BOOLEAN).description("현재 페이지가 정렬되지 않았는지 여부"),
                                        fieldWithPath("data.first").type(JsonFieldType.BOOLEAN).description("첫 번째 페이지인지 여부"),
                                        fieldWithPath("data.numberOfElements").type(JsonFieldType.NUMBER).description("현재 페이지에 있는 아이템 수"),
                                        fieldWithPath("data.empty").type(JsonFieldType.BOOLEAN).description("현재 페이지가 비어 있는지 여부")
                                )
                                .responseSchema(Schema.schema("나의 매물-전체조회-성공-응답"))
                                .build())
                ));

    }

    @Test
    @WithMockUser
    void 매물_등록_성공() throws Exception {
        // Given
        // 결과값 설정
        ItemResponse itemResponse = ItemResponse.builder()
            .title("가짜11")
            .price(1000)
            .nickname("오만한천원")
            .build();
        given(itemService.addItem(any(ItemCreateRequest.class), any(CustomUserDetails.class))).willReturn(itemResponse);

        // when, then
        mockMvc.perform(RestDocumentationRequestBuilders.post("/items")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"title\":\"가짜11\",\"description\":\"등록할 설명\",\"price\":1000,\"categoryId\":1,\"imageName\":\"https://sprout-market.s3.ap-northeast-2.amazonaws.com/1a70f21b-f.jpg\"}")
            )
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.data.title").value(itemResponse.getTitle()))  // 응답 검증
            .andExpect(jsonPath("$.data.price").value(itemResponse.getPrice()))  // 응답 검증
            .andExpect(jsonPath("$.data.nickname").value(itemResponse.getNickname()))
            .andDo(document("add-item",
                resource(ResourceSnippetParameters.builder()
                    .description("매물 생성 API")
                    .summary("로그인한 사용자가 매물을 등록합니다.")
                    .tag("Items")
                    .requestFields(
                        fieldWithPath("title").description("등록할 제목"),
                        fieldWithPath("description").description("등록할 설명"),
                        fieldWithPath("price").description("등록할 가격"),
                        fieldWithPath("categoryId").description("등록할 카테고리 아이디"),
                        fieldWithPath("imageName").description("이미 저장된 매물 이미지 이름")
                    )
                    .responseFields(
                        fieldWithPath("message").description("응답 메시지"),
                        fieldWithPath("statusCode").description("응답 상태 코드"),
                        fieldWithPath("data.title").description("등록된 제목"),
                        fieldWithPath("data.price").description("등록된 가격"),
                        fieldWithPath("data.nickname").description("등록한 유저 닉네임")
                    )
                    .requestSchema(Schema.schema("매물-생성-성공-요청"))
                    .responseSchema(Schema.schema("매물-생성-성공-응답"))
                    .build())
            ));

    }

    @Test
    @WithMockUser
    void 매물_판매상태_변경_성공() throws Exception {
        String SsaleStatus = ItemSaleStatus.SOLD.toString();

        // Given
        ItemResponse itemResponse = ItemResponse.builder()
            .title(mockItem.getTitle())
            .itemSaleStatus(mockItem.getItemSaleStatus())
            .price(mockItem.getPrice())
            .nickname(mockItem.getSeller().getNickname())
            .build();

        given(itemService.updateSaleStatus(mockItem.getId(), SsaleStatus, mockAuthUser)).willReturn(itemResponse);


        // when, then
        mockMvc.perform(RestDocumentationRequestBuilders.put("/items/{itemId}/sale-status", mockItem.getId())
                        .param("saleStatus", SsaleStatus)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"itemSaleStatus\":\"" + SsaleStatus + "\"}")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Ok"))  // 응답 메시지 검증
                .andExpect(jsonPath("$.statusCode").value(200)) // 응답 상태 코드 검증
                .andExpect(jsonPath("$.data.title").value(itemResponse.getTitle()))  // 응답 검증
                .andExpect(jsonPath("$.data.price").value(itemResponse.getPrice()))  // 응답 검증
                .andExpect(jsonPath("$.data.itemSaleStatus").value(itemResponse.getItemSaleStatus().toString()))
                .andExpect(jsonPath("$.data.nickname").value(itemResponse.getNickname()))
                .andDo(document("update-item-sale-status",
                        resource(ResourceSnippetParameters.builder()
                                .description("매물 판매 상태 수정 API")
                                .summary("로그인한 사용자가 매물의 판매상태를 수정합니다.")
                                .tag("Items")
                                .pathParameters(parameterWithName("itemId").description("수정할 매물 ID"))
                                .queryParameters(parameterWithName("saleStatus").description("판매 상태 수정 내용"))
                                .requestFields(
                                        fieldWithPath("itemSaleStatus").description("판매 상태 수정 내용")
                                )
                                .responseFields(
                                        fieldWithPath("message").description("응답 메시지"),
                                        fieldWithPath("statusCode").description("응답 상태 코드"),
                                        fieldWithPath("data.title").description("판매상태를 수정한 매물의 제목"),
                                        fieldWithPath("data.price").description("판매상태를 수정한 매물의 가격"),
                                        fieldWithPath("data.itemSaleStatus").description("수정된 판매상태"),
                                        fieldWithPath("data.nickname").description("수정을 한 유저 닉네임")
                                )
                                .requestSchema(Schema.schema("매물-상태수정-성공-요청"))
                                .responseSchema(Schema.schema("매물-상태수정-성공-응답"))
                                .build())
                ));
    }



    @Test
    @WithMockUser
    void 매물_삭제_성공() throws Exception {
        // Given
        ItemResponse itemResponse = ItemResponse.builder()
            .title(mockItem.getTitle())
            .status(Status.DELETED)
            .price(mockItem.getPrice())
            .nickname(mockItem.getSeller().getNickname())
            .build();

        given(itemService.softDeleteItem(mockItem.getId(), mockAuthUser)).willReturn(itemResponse);

        // When, Then
        mockMvc.perform(RestDocumentationRequestBuilders.delete("/items/{itemId}", mockItem.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Ok"))  // 응답 메시지 검증
                .andExpect(jsonPath("$.statusCode").value(200)) // 응답 상태 코드 검증
                .andExpect(jsonPath("$.data.title").value(itemResponse.getTitle()))  // 응답 검증
                .andExpect(jsonPath("$.data.status").value(itemResponse.getStatus().toString()))  // 응답 검증
                .andExpect(jsonPath("$.data.nickname").value(itemResponse.getNickname()))
                .andDo(document("soft-delete-my-item",
                        resource(ResourceSnippetParameters.builder()
                                .description("매물 삭제 API")
                                .summary("로그인한 사용자가 매물을 삭제합니다.")
                                .tag("Items")
                                .pathParameters(parameterWithName("itemId").description("삭제할 매물 ID"))
                                .responseFields(
                                        fieldWithPath("message").description("응답 메시지"),
                                        fieldWithPath("statusCode").description("응답 상태 코드"),
                                        fieldWithPath("data.title").description("삭제된 매물의 제목"),
                                        fieldWithPath("data.price").description("삭제된 매물의 가격"),
                                        fieldWithPath("data.status").description("삭제된 매물의 상태"),
                                        fieldWithPath("data.nickname").description("삭제를 한 유저의 닉네임")
                                )
                                .responseSchema(Schema.schema("매물-삭제-성공-응답"))
                                .build())
                ));
    }

    @Test
    @WithMockUser
    void 관리자_신고매물_삭제_성공 () throws Exception {
        // Given
        ItemResponse itemResponse = ItemResponse.builder()
            .title(mockItem.getTitle())
            .description(mockItem.getDescription())
            .price(mockItem.getPrice())
            .status(Status.DELETED)
            .build();

        given(itemService.softDeleteReportedItem(mockItem.getId(), mockAuthUser)).willReturn(itemResponse);

        // When, Then
        mockMvc.perform(RestDocumentationRequestBuilders.delete("/admin/items/{itemId}", mockItem.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Ok"))  // 응답 메시지 검증
                .andExpect(jsonPath("$.statusCode").value(200)) // 응답 상태 코드 검증
                .andExpect(jsonPath("$.data.title").value(itemResponse.getTitle()))  // 응답 검증
                .andExpect(jsonPath("$.data.description").value(itemResponse.getDescription()))  // 응답 검증
                .andExpect(jsonPath("$.data.price").value(itemResponse.getPrice()))  // 응답 검증
                .andExpect(jsonPath("$.data.status").value(itemResponse.getStatus().toString()))
                .andDo(document("soft-delete-report-item",
                        resource(ResourceSnippetParameters.builder()
                                .description("신고된 매물 삭제 API")
                                .summary("관리자가 신고된 매물을 삭제합니다.")
                                .tag("Items")
                                .pathParameters(parameterWithName("itemId").description("삭제할 매물 ID"))
                                .responseFields(
                                        fieldWithPath("message").description("응답 메시지"),
                                        fieldWithPath("statusCode").description("응답 상태 코드"),
                                        fieldWithPath("data.title").description("삭제된 매물의 제목"),
                                        fieldWithPath("data.description").description("삭제된 매물의 설명"),
                                        fieldWithPath("data.price").description("삭제된 매물의 가격"),
                                        fieldWithPath("data.status").description("삭제된 매물의 상태")
                                )
                                .responseSchema(Schema.schema("신고매물-삭제-성공-응답"))
                                .build())
                ));
    }

    @Test
    @WithMockUser
    void 내_주변_매물_조회_성공() throws Exception {
        FindItemsInMyAreaRequestDto requestDto = FindItemsInMyAreaRequestDto.builder()
            .page(1)
            .size(10)
            .build();;
        //페이지 직접 만들어주기
        List<ItemResponseDto> dtoList = new ArrayList<>();
        for(int i = 1; i <= 2; i++) {
            ItemResponseDto dto = new ItemResponseDto(
                    (long) i, //
                    "제목" + i,
                    "내용" + i,
                    15000,
                    "닉네임" + i,
                    ItemSaleStatus.WAITING,
                    "카테고리 이름" + i,
                    Status.ACTIVE
            );
            dtoList.add(dto);
        }

        Pageable pageable = PageRequest.of(0, 10);
        Page<ItemResponseDto> pageResult = new PageImpl<>(dtoList, pageable, 2);

        given(itemService.findItemsByMyArea(any(CustomUserDetails.class), any(FindItemsInMyAreaRequestDto.class))).willReturn(pageResult);
        ResultActions result = mockMvc.perform(RestDocumentationRequestBuilders.get("/items/myAreas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto))
                        .header("Authorization", "Bearer (JWT 토큰)"))
                .andDo(MockMvcRestDocumentationWrapper.document(
                        "get-items-by-my-areas",
                        resource(ResourceSnippetParameters.builder()
                                .description("우리 동네의 매물을 조회합니다.")
                                .summary("동네 매물 조회")
                                .tag("Items")
                                .requestHeaders(
                                        headerWithName("Authorization")
                                                .description("Bearer (JWT 토큰)")
                                )
                                .requestFields(
                                        fieldWithPath("page").type(JsonFieldType.NUMBER)
                                                .description("페이지 넘버, 기본값 1, 최소값 1"),
                                        fieldWithPath("size").type(JsonFieldType.NUMBER)
                                                .description("페이지 크기, 기본값 10, 최소값 1")
                                )
                                .responseFields(
                                        fieldWithPath("message").type(JsonFieldType.STRING)
                                                .description("성공 상태 메시지"),
                                        fieldWithPath("statusCode").type(JsonFieldType.NUMBER)
                                                .description("성공 시 응답 코드 : 200"),
                                        fieldWithPath("data").type(JsonFieldType.OBJECT)
                                                .description("응답 본문"),
                                        fieldWithPath("data.content").type(JsonFieldType.ARRAY)
                                                .description("아이템 리스트"),
                                        fieldWithPath("data.content[].id").type(JsonFieldType.NUMBER)
                                                .description("아이템 ID"),
                                        fieldWithPath("data.content[].title").type(JsonFieldType.STRING)
                                                .description("아이템 제목"),
                                        fieldWithPath("data.content[].description").type(JsonFieldType.STRING)
                                                .description("아이템 설명"),
                                        fieldWithPath("data.content[].price").type(JsonFieldType.NUMBER)
                                                .description("아이템 가격"),
                                        fieldWithPath("data.content[].nickname").type(JsonFieldType.STRING)
                                                .description("판매자 닉네임"),
                                        fieldWithPath("data.content[].itemSaleStatus").type(JsonFieldType.STRING)
                                                .description("아이템 판매 상태"),
                                        fieldWithPath("data.content[].categoryName").type(JsonFieldType.STRING)
                                                .description("아이템 카테고리 이름"),
                                        fieldWithPath("data.content[].status").type(JsonFieldType.STRING)
                                                .description("아이템 상태 (예: ACTIVE, INACTIVE)"),
                                        fieldWithPath("data.pageable").type(JsonFieldType.OBJECT)
                                                .description("페이지 관련 정보"),
                                        fieldWithPath("data.pageable.pageNumber").type(JsonFieldType.NUMBER)
                                                .description("현재 페이지 번호 (0부터 시작)"),
                                        fieldWithPath("data.pageable.pageSize").type(JsonFieldType.NUMBER)
                                                .description("페이지 크기"),
                                        fieldWithPath("data.pageable.sort").type(JsonFieldType.OBJECT)
                                                .description("정렬 정보"),
                                        fieldWithPath("data.pageable.sort.empty").type(JsonFieldType.BOOLEAN)
                                                .description("정렬 정보가 비어 있는지 여부"),
                                        fieldWithPath("data.pageable.sort.sorted").type(JsonFieldType.BOOLEAN)
                                                .description("정렬되었는지 여부"),
                                        fieldWithPath("data.pageable.sort.unsorted").type(JsonFieldType.BOOLEAN)
                                                .description("정렬되지 않았는지 여부"),
                                        fieldWithPath("data.pageable.offset").type(JsonFieldType.NUMBER)
                                                .description("현재 페이지의 시작점 오프셋"),
                                        fieldWithPath("data.pageable.paged").type(JsonFieldType.BOOLEAN)
                                                .description("페이징 여부"),
                                        fieldWithPath("data.pageable.unpaged").type(JsonFieldType.BOOLEAN)
                                                .description("페이징되지 않은 여부"),
                                        fieldWithPath("data.last").type(JsonFieldType.BOOLEAN)
                                                .description("마지막 페이지인지 여부"),
                                        fieldWithPath("data.totalPages").type(JsonFieldType.NUMBER)
                                                .description("전체 페이지 수"),
                                        fieldWithPath("data.totalElements").type(JsonFieldType.NUMBER)
                                                .description("전체 아이템 수"),
                                        fieldWithPath("data.size").type(JsonFieldType.NUMBER)
                                                .description("페이지 크기"),
                                        fieldWithPath("data.number").type(JsonFieldType.NUMBER)
                                                .description("현재 페이지 번호"),
                                        fieldWithPath("data.sort").type(JsonFieldType.OBJECT)
                                                .description("현재 페이지의 정렬 정보"),
                                        fieldWithPath("data.sort.empty").type(JsonFieldType.BOOLEAN)
                                                .description("현재 페이지의 정렬 정보가 비어 있는지 여부"),
                                        fieldWithPath("data.sort.sorted").type(JsonFieldType.BOOLEAN)
                                                .description("현재 페이지가 정렬되었는지 여부"),
                                        fieldWithPath("data.sort.unsorted").type(JsonFieldType.BOOLEAN)
                                                .description("현재 페이지가 정렬되지 않았는지 여부"),
                                        fieldWithPath("data.first").type(JsonFieldType.BOOLEAN)
                                                .description("첫 번째 페이지인지 여부"),
                                        fieldWithPath("data.numberOfElements").type(JsonFieldType.NUMBER)
                                                .description("현재 페이지에 있는 아이템 수"),
                                        fieldWithPath("data.empty").type(JsonFieldType.BOOLEAN)
                                                .description("현재 페이지가 비어 있는지 여부")
                                )
                                .requestSchema(Schema.schema("동네-매물-조회-성공-요청"))
                                .responseSchema(Schema.schema("동네-매물-조회-성공-응답"))
                                .build()
                        )
                ));

        verify(itemService, times(1)).findItemsByMyArea(any(CustomUserDetails.class), any(FindItemsInMyAreaRequestDto.class));
        result.andExpect(status().isOk())
                .andExpect(jsonPath("$.data.totalElements").value(2));
    }

    @Test
    void 주변_인기_매물_조회_성공() throws Exception {
        // given
        ItemResponseDto itemResponseDto = new ItemResponseDto(1L, "제목", "설명", 10000, "판매자", ItemSaleStatus.WAITING, "전자기기", Status.ACTIVE);
        ItemResponseDto itemResponseDto2 = new ItemResponseDto(2L, "제목2", "설명2", 10000, "판매자2", ItemSaleStatus.WAITING, "전자기기", Status.ACTIVE);
        List<ItemResponseDto> dtoList = new ArrayList<>();
        dtoList.add(itemResponseDto);
        dtoList.add(itemResponseDto2);

        when(itemService.getTopItems(any(CustomUserDetails.class))).thenReturn(dtoList);

        // when, then
        ResultActions result = mockMvc.perform(RestDocumentationRequestBuilders.get("/items/topItems")
                        .header("Authorization", "Bearer (JWT 토큰)"))
                .andDo(print())
                .andDo(MockMvcRestDocumentationWrapper.document(
                        "get-topItems",
                        resource(ResourceSnippetParameters.builder()
                                .description("동네 인기 매물을 조회합니다.")
                                .requestHeaders(
                                        headerWithName("Authorization")
                                                .description("Bearer (JWT 토큰)")
                                )
                                .summary("인기 매물 조회")
                                .tag("Items")
                                .responseFields(List.of(
                                        fieldWithPath("message").description("응답 메시지"),
                                        fieldWithPath("statusCode").description("HTTP 상태 코드"),
                                        fieldWithPath("data[]").description("인기 아이템 목록"),
                                        fieldWithPath("data[].id").description("아이템 ID"),
                                        fieldWithPath("data[].title").description("아이템 제목"),
                                        fieldWithPath("data[].description").description("아이템 설명"),
                                        fieldWithPath("data[].price").description("아이템 가격"),
                                        fieldWithPath("data[].nickname").description("판매자 닉네임"),
                                        fieldWithPath("data[].itemSaleStatus").description("아이템 판매 상태"),
                                        fieldWithPath("data[].categoryName").description("아이템 카테고리 이름"),
                                        fieldWithPath("data[].status").description("아이템 상태 (예: ACTIVE, INACTIVE)")
                                ))
                                .requestSchema(Schema.schema("인기매물-조회-성공-요청"))
                                .responseSchema(Schema.schema("인기매물-조회-성공-응답"))
                                .build()
                        )
                ));
        result.andExpect(status().isOk())
                .andExpect(jsonPath("$.data", Matchers.hasSize(2)));

    }


}