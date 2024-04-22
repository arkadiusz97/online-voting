package com.github.arkadiusz97.online.voting.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.arkadiusz97.online.voting.dto.requestbody.ChangePasswordDTO;
import com.github.arkadiusz97.online.voting.dto.requestbody.NewUserDTO;
import com.github.arkadiusz97.online.voting.dto.responsebody.UserDTO;
import com.github.arkadiusz97.online.voting.service.UserService;
import com.github.arkadiusz97.online.voting.utils.SampleDomains;
import com.github.arkadiusz97.online.voting.utils.Utils;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.Collections;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;

@WebMvcTest(UserController.class)
@WithMockUser(roles = "ADMIN", username = "some-mail1@domain.eu")
public class UserControllerTest {

    @MockBean
    private UserService userService;

    @Autowired
    private MockMvc mockMvc;

    private ObjectMapper mapper = new ObjectMapper();

    @Test
    public void it_should_register_new_user() throws Exception {
        String email = "some-email@domain.com";
        NewUserDTO newUserDTO = new NewUserDTO(email);
        String responseString = "Created a new user " + email;
        Mockito.when(userService.registerNewUser(newUserDTO.recipient())).thenReturn(responseString);

        mockMvc.perform(post("/user/register").with(csrf())
                    .content(mapper.writeValueAsString(newUserDTO))
                    .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value(responseString));
    }

    @Test
    public void it_should_show_many_users() throws Exception {
        Integer pageNumber = 1;
        Integer pageSize = 3;
        UserDTO userDTO = SampleDomains.getSampleUserDTO();
        Mockito.when(userService.showMany(pageNumber, pageSize)).
                thenReturn(Collections.singletonList(userDTO));

        String url = "/user/get?pageNumber=" + pageNumber.toString() + "&pageSize=" + pageSize.toString();
        mockMvc.perform(get(url).with(csrf()))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.[0].id").value(userDTO.id()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.[0].roles[0].name")
                        .value(userDTO.roles().get(0).name()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.[0].created")
                        .value(Utils.getFormattedDate(userDTO.created())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.[0].email").value(userDTO.email()));
    }

    @Test
    public void it_should_get_current_user() throws Exception {
        UserDTO userDTO = SampleDomains.getSampleUserDTO();
        Mockito.when(userService.getByEmail(userDTO.email())).
                thenReturn(userDTO);

        mockMvc.perform(get("/user/get-current").with(csrf()))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(userDTO.id()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.roles[0].name")
                        .value(userDTO.roles().get(0).name()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.created")
                        .value(Utils.getFormattedDate(userDTO.created())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.email").value(userDTO.email()));
    }

    @Test
    public void it_should_get_user() throws Exception {
        UserDTO userDTO = SampleDomains.getSampleUserDTO();
        Mockito.when(userService.getById(userDTO.id())).
                thenReturn(userDTO);

        String url = "/user/get/" + userDTO.id().toString();
        mockMvc.perform(get(url).with(csrf()))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(userDTO.id()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.roles[0].name")
                        .value(userDTO.roles().get(0).name()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.created")
                        .value(Utils.getFormattedDate(userDTO.created())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.email").value(userDTO.email()));
    }

    @Test
    public void it_should_delete_user() throws Exception {
        UserDTO userDTO = SampleDomains.getSampleUserDTO();

        String idString = userDTO.id().toString();
        String url = "/user/delete/" + idString;
        mockMvc.perform(delete(url).with(csrf()))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.message")
                        .value("User " + idString + " deleted"));
        verify(userService, times(1)).delete(userDTO.id());
    }

    @Test
    public void it_should_change_user_password() throws Exception {
        String newPassword = "some-password";
        ChangePasswordDTO changePasswordDTO = new ChangePasswordDTO(newPassword);

        mockMvc.perform(post("/user/change-password").with(csrf())
                        .content(mapper.writeValueAsString(changePasswordDTO))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
        verify(userService, times(1)).changeCurrentUserPassword(newPassword);
    }

}
