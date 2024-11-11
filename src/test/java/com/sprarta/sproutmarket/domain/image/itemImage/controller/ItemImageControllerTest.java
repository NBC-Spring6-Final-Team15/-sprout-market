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
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.restdocs.mockmvc.MockMvcRestDocumentation;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.context.WebApplicationContext;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.anyLong;
import static org.mockito.Mockito.doNothing;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
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