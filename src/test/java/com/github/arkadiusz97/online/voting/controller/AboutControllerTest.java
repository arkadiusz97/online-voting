package com.github.arkadiusz97.online.voting.controller;

import com.github.arkadiusz97.online.voting.dto.responsebody.AboutDTO;
import com.github.arkadiusz97.online.voting.service.AboutService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AboutController.class)
@WithMockUser(roles = "USER")
public class AboutControllerTest {

    @MockBean
    AboutService aboutService;

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void it_should_return_about_page() throws Exception {
        String version = "1.2.3";
        Date date = new Date();
        AboutDTO aboutDTO = new AboutDTO(version, date);
        Mockito.when(aboutService.getAbout()).thenReturn(aboutDTO);

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'+00:00'");
        simpleDateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
        String strFormattedDate = simpleDateFormat.format(date);

        mockMvc.perform(get("/about"))
            .andExpect(status().isOk())
            .andExpect(MockMvcResultMatchers.jsonPath("$.version").value(version))
            .andExpect(MockMvcResultMatchers.jsonPath("$.startedAt").value(strFormattedDate));
    }

}