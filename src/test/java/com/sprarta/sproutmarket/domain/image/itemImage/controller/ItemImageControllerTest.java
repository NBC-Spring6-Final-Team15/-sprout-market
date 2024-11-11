package com.sprarta.sproutmarket.domain.image.itemImage.controller;

import com.sprarta.sproutmarket.domain.CommonMockMvcControllerTestSetUp;
import com.sprarta.sproutmarket.domain.image.itemImage.service.ItemImageService;
import com.sprarta.sproutmarket.domain.image.s3Image.service.S3ImageService;
import com.sprarta.sproutmarket.domain.item.dto.request.ImageNameRequest;
import com.sprarta.sproutmarket.domain.user.entity.CustomUserDetails;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.restdocs.mockmvc.MockMvcRestDocumentation;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.multipart.MultipartFile;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

@ExtendWith(RestDocumentationExtension.class)
@WebMvcTest(ItemImageController.class)
@AutoConfigureMockMvc(addFilters = false)
class ItemImageControllerTest extends CommonMockMvcControllerTestSetUp {

    @MockBean
    private ItemImageService itemImageService;

    @MockBean
    private S3ImageService s3ImageService;

    @InjectMocks
    private ItemImageController itemImageController;

    @Autowired
    private WebApplicationContext webApplicationContext;

    private MockMvc mockMvc;

    @BeforeEach
    void setup(RestDocumentationContextProvider restDocumentation) {
        MockitoAnnotations.openMocks(this);
        this.mockMvc = webAppContextSetup(webApplicationContext)
                .apply(MockMvcRestDocumentation.documentationConfiguration(restDocumentation))
                .build();
    }

    @Test
    @DisplayName("여러 이미지 업로드 성공")
    void itemImageUpload_success() throws Exception {
        // Mock 이미지 파일 생성
        MockMultipartFile mockFile1 = new MockMultipartFile("images", "image1.jpg", "image/jpeg", "image1".getBytes());
        MockMultipartFile mockFile2 = new MockMultipartFile("images", "image2.jpg", "image/jpeg", "image2".getBytes());

        CustomUserDetails mockAuthUser = mock(CustomUserDetails.class);
        when(mockAuthUser.getUsername()).thenReturn("testUser");
        when(mockAuthUser.getId()).thenReturn(1L);

        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(mockAuthUser, null, mockAuthUser.getAuthorities())
        );

        when(s3ImageService.uploadImageAsync(anyLong(), any(MultipartFile.class), any(CustomUserDetails.class)))
                .thenReturn(CompletableFuture.completedFuture("http://example.com/image1.jpg"));

        mockMvc.perform(MockMvcRequestBuilders.multipart("/items/1/images")
                        .file(mockFile1)
                        .file(mockFile2)
                        .header("Authorization", "Bearer token"))
                .andExpect(status().isOk())
                .andDo(document("item-image-upload",
                        requestParts(
                                partWithName("images").description("업로드할 이미지 파일들")
                        ),
                        responseFields(
                                fieldWithPath("statusCode").description("응답 상태 코드"),
                                fieldWithPath("message").description("응답 메시지"),
                                fieldWithPath("data").description("업로드된 이미지 URL 리스트"),
                                fieldWithPath("data[]").description("개별 이미지 URL")
                        )));
    }

    @Test
    @DisplayName("매물 이미지 삭제 성공")
    void itemImage_delete_success() throws Exception {
        doNothing().when(itemImageService).deleteItemImage(anyLong(), any(ImageNameRequest.class), any(CustomUserDetails.class));

        mockMvc.perform(RestDocumentationRequestBuilders.delete("/items/{itemId}/images", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"imageName\":\"image.jpg\"}")
                        .header("Authorization", "Bearer token")
                )
                .andExpect(status().isOk())
                .andDo(document(
                        "item-image-delete",
                        pathParameters(
                                parameterWithName("itemId").description("아이템의 고유 ID")
                        ),
                        requestFields(
                                fieldWithPath("imageName").type(JsonFieldType.STRING).description("삭제할 이미지 이름")
                        )
                ));
    }
}