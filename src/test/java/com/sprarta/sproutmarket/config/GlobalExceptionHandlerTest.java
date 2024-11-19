package com.sprarta.sproutmarket.config;

import com.sprarta.sproutmarket.domain.CommonMockMvcControllerTestSetUp;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ExceptionTestController.class)
@AutoConfigureMockMvc(addFilters = false)
class GlobalExceptionHandlerTest extends CommonMockMvcControllerTestSetUp {
    @Autowired
    private MockMvc mockMvc;

    @Test
    void 핸들링_되지_않은_예외_처리() throws Exception {
        mockMvc.perform(get("/test"))
                .andExpect(jsonPath("$.message").value("잠시 후 다시 시도해주십시오."))
                .andExpect(status().isInternalServerError())
                .andDo(print());
    }

    @Test
    void 자원_없음_예외_처리() throws Exception {
        mockMvc.perform(get("/testo"))
                .andExpect(jsonPath("$.message").value("주소를 찾을 수 없습니다."))
                .andExpect(status().isNotFound())
                .andDo(print());
    }
}