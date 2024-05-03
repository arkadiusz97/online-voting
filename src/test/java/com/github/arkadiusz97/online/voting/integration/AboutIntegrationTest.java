package com.github.arkadiusz97.online.voting.integration;

import com.github.arkadiusz97.online.voting.utils.Utils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@ActiveProfiles("test")
public class AboutIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Value("${online-voting.app-version}")
    private String appVersion;

    @Value("${online-voting.default-admin-login}")
    private String defaultAdminLogin;

    @Value("${online-voting.default-admin-password}")
    private String defaultAdminPassword;

    @Test
    public void it_should_return_about_page() throws Exception {
        var adminUserRequest = user(defaultAdminLogin).password(defaultAdminPassword).roles("ADMIN");
        mockMvc.perform(get("/about").with(csrf()).with(adminUserRequest))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.version").value(appVersion))
                .andExpect(MockMvcResultMatchers.jsonPath("$.startedAt").exists());
    }

}
