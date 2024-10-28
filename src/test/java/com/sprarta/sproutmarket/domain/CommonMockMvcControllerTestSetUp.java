package com.sprarta.sproutmarket.domain;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprarta.sproutmarket.config.JwtUtil;
import com.sprarta.sproutmarket.config.SecurityConfig;
import com.sprarta.sproutmarket.domain.user.entity.CustomUserDetails;
import com.sprarta.sproutmarket.domain.user.service.CustomUserDetailService;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

@Import(SecurityConfig.class)
@AutoConfigureMockMvc(addFilters = false)
@ExtendWith(SpringExtension.class)
@AutoConfigureRestDocs
public abstract class CommonMockMvcControllerTestSetUp {
    @Autowired
    protected MockMvc mockMvc;

    @MockBean
    protected JwtUtil jwtUtil;

    @MockBean
    protected CustomUserDetailService customUserDetailService;

    @MockBean
    protected JpaMetamodelMappingContext jpaMappingContext;

    @MockBean
    protected CustomUserDetails mockAuthUser;

    protected ObjectMapper objectMapper = new ObjectMapper();
}
