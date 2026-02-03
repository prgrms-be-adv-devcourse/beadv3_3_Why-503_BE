package io.why503.accountservice;

import io.why503.accountservice.domain.accounts.controller.AccountsController;
import io.why503.accountservice.domain.accounts.service.AccountService;
import io.why503.accountservice.domain.auth.config.SecurityConfig;
import io.why503.accountservice.domain.companies.service.CompanyService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Slf4j
@WebMvcTest(AccountsController.class)
@Import(TestSecurityConfig.class)
public class AccountControllerTest {
    @Autowired
    private MockMvc mvc;
    @MockitoBean
    private AccountService service;
    @MockitoBean
    private CompanyService companyService;

    @Test
    void valid_should_return_400() throws Exception {
        String invalidJson = """
        {
          "title": "",
          "content": ""
        }
        """;
        mvc.perform(post("/accounts")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidJson))
                .andExpect(status().isBadRequest())
                .andDo(print());
    }
}
