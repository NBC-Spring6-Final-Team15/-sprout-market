package com.sprarta.sproutmarket.domain.areas.controller;

import com.epages.restdocs.apispec.MockMvcRestDocumentationWrapper;
import com.epages.restdocs.apispec.ResourceSnippetParameters;
import com.epages.restdocs.apispec.Schema;
import com.sprarta.sproutmarket.domain.CommonMockMvcControllerTestSetUp;
import com.sprarta.sproutmarket.domain.areas.dto.AdministrativeAreaRequestDto;
import com.sprarta.sproutmarket.domain.areas.service.AdministrativeAreaService;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.web.servlet.ResultActions;

import java.util.ArrayList;
import java.util.List;

import static com.epages.restdocs.apispec.ResourceDocumentation.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AdministrativeAreaController.class)
@AutoConfigureMockMvc(addFilters = false)
class AdministrativeAreaControllerTest extends CommonMockMvcControllerTestSetUp {
    @MockBean
    AdministrativeAreaService administrativeAreaService;

    @Test
    void 좌표로_행정동_조회_성공() throws Exception {
        //when
        AdministrativeAreaRequestDto requestDto = new AdministrativeAreaRequestDto(126.927872, 37.523254);
        String returnString = "서울특별시 영등포구 여의동";
        when(administrativeAreaService
                .getAdministrativeAreaByCoordinates(requestDto.getLongitude(), requestDto.getLatitude()))
                .thenReturn(returnString);

        ResultActions result = mockMvc.perform(RestDocumentationRequestBuilders.post("/test/areas")
                        .header("Authorization", "Bearer (JWT 토큰)")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andDo(
                        MockMvcRestDocumentationWrapper.document(
                                "get-HJD",
                                resource(ResourceSnippetParameters.builder()
                                        .description("double 타입의 위도, 경도를 받아서 특정 행정구역을 리턴합니다.")
                                        .summary("행정구역 반환 API")
                                        .tag("AdministrativeArea")
                                        .requestHeaders(
                                                headerWithName("Authorization")
                                                        .description("Bearer (JWT 토큰)")
                                        )
                                        .requestFields(List.of(
                                                fieldWithPath("longitude").type(JsonFieldType.NUMBER)
                                                        .description("위도"),
                                                fieldWithPath("latitude").type(JsonFieldType.NUMBER)
                                                        .description("경도")
                                        ))
                                        .requestSchema(Schema.schema("행정동-조회-성공-요청"))
                                        .responseFields(List.of(
                                                fieldWithPath("message").type(JsonFieldType.STRING)
                                                        .description("Ok"),
                                                fieldWithPath("statusCode").type(JsonFieldType.NUMBER)
                                                        .description("201 상태코드"),
                                                fieldWithPath("data").type(JsonFieldType.STRING)
                                                        .description("행정구역('시도' '시군구' '읍면동')")
                                        ))
                                        .responseSchema(Schema.schema("행정동-조회-성공-응답"))
                                        .build()
                                )
                        )
                );
        verify(administrativeAreaService, times(1)).getAdministrativeAreaByCoordinates(requestDto.getLongitude(), requestDto.getLatitude());
        result.andExpect(status().isOk());
        result.andExpect(jsonPath("data").value(returnString));
        result.andExpect(jsonPath("statusCode").value(200));
        result.andExpect(jsonPath("message").value("Ok"));
    }

    @Test
    void 주변_행정동_조회_성공() throws Exception {
        String paramAdmNm = "경상남도 산청군 생초면";
        List<String> listResult = new ArrayList<>();
        String admNameDto1 = "경상남도 산청군 오부면";
        String admNameDto2 = "경상남도 산청군 생초면";
        listResult.add(admNameDto1);
        listResult.add(admNameDto2);
        given(administrativeAreaService.getAdmNameListByAdmName(paramAdmNm)).willReturn(listResult);
        ResultActions result = mockMvc.perform(RestDocumentationRequestBuilders.get("/test/areas")
                        .header("Authorization", "Bearer (JWT 토큰)")
                        .queryParam("admNm", paramAdmNm))
                .andDo(MockMvcRestDocumentationWrapper.document(
                                "get-admNm-List",
                                resource(ResourceSnippetParameters.builder()
                                        .description("특정 행정동을 받아서 주변 5km의 행정동 이름을 담은 리스트를 반환")
                                        .summary("주변 행정동 리스트 반환")
                                        .tag("AdministrativeArea")
                                        .requestHeaders(
                                                headerWithName("Authorization")
                                                        .description("Bearer (JWT 토큰)")
                                        )
                                        .queryParameters(
                                                parameterWithName("admNm")
                                                        .description("행정동 이름")
                                        )
                                        .responseFields(
                                                fieldWithPath("message")
                                                        .description("성공 시 응답 메시지"),
                                                fieldWithPath("statusCode")
                                                        .description("성공 시 응답 코드 : 200"),
                                                fieldWithPath("data[]")
                                                        .description("행정동 이름")
                                        )
                                        .responseSchema(Schema.schema("행정동리스트-조회-성공-응답"))
                                        .build()
                                )

                        )
                );

        result.andExpect(status().isOk())
                .andExpect(jsonPath("data[0]").value("경상남도 산청군 오부면"))
                .andExpect(jsonPath("data[1]").value("경상남도 산청군 생초면"));
    }
}