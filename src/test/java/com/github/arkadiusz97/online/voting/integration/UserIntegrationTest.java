package com.github.arkadiusz97.online.voting.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.arkadiusz97.online.voting.domain.Role;
import com.github.arkadiusz97.online.voting.domain.User;
import com.github.arkadiusz97.online.voting.domain.UserRole;
import com.github.arkadiusz97.online.voting.domain.UserRoleKey;
import com.github.arkadiusz97.online.voting.dto.requestbody.ChangePasswordDTO;
import com.github.arkadiusz97.online.voting.dto.requestbody.NewUserDTO;
import com.github.arkadiusz97.online.voting.repository.RoleRepository;
import com.github.arkadiusz97.online.voting.repository.UserRepository;
import com.github.arkadiusz97.online.voting.repository.UserRoleRepository;
import com.github.arkadiusz97.online.voting.utils.SampleDomains;
import com.github.arkadiusz97.online.voting.utils.Utils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@ActiveProfiles("test")
public class UserIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserRoleRepository userRoleRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Value("${online-voting.default-admin-login}")
    private String defaultAdminLogin;

    @Value("${online-voting.default-admin-password}")
    private String defaultAdminPassword;

    private ObjectMapper mapper = new ObjectMapper();

    @Test
    public void it_should_register_user_and_not_register_when_exists() throws Exception {
        assertThat(userRepository.findAll().size()).isEqualTo(1);
        var adminUserRequest = user(defaultAdminLogin).password(defaultAdminPassword).roles("ADMIN");

        String email = "some-email@domain.com";
        NewUserDTO newUserDTO = new NewUserDTO(email);

        mockMvc.perform(post("/user/register").with(csrf())
                        .with(adminUserRequest)
                        .content(mapper.writeValueAsString(newUserDTO))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(MockMvcResultMatchers.jsonPath("$.message")
                        .value("Registered a new user with " + email + " email"));

        List<User> users = userRepository.findAll();
        assertThat(users.size()).isEqualTo(2);
        assertThat(users.get(1).getEmail()).isEqualTo("some-email@domain.com");

        mockMvc.perform(post("/user/register").with(csrf())
                        .with(adminUserRequest)
                        .content(mapper.writeValueAsString(newUserDTO))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isConflict())
                .andExpect(MockMvcResultMatchers.jsonPath("$.message")
                        .value("User already exists"));

        assertThat(users.size()).isEqualTo(2);
    }

    @Test
    public void it_should_get_many_users() throws Exception {
        var adminUserRequest = user(defaultAdminLogin).password(defaultAdminPassword).roles("ADMIN");
        assertThat(userRepository.findAll().size()).isEqualTo(1);
        User adminUser = userRepository.findAll().get(0);
        User newUser = userRepository.save(SampleDomains.getSampleUser());
        assertThat(userRepository.findAll().size()).isEqualTo(2);

        Role role = roleRepository.findByName("ROLE_USER");
        UserRoleKey urk = new UserRoleKey(newUser.getId(), role.getId());
        userRoleRepository.save(new UserRole(urk, newUser, role));

        String url = "/user/get?pageNumber=0&pageSize=5";
        mockMvc.perform(get(url).with(csrf()).with(adminUserRequest))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.[0].id").value(adminUser.getId()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.[0].roles[0].name")
                        .value("ROLE_ADMIN"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.[0].created")
                        .value(Utils.getFormattedDate(adminUser.getCreated())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.[0].email").value(adminUser.getEmail()))

                .andExpect(MockMvcResultMatchers.jsonPath("$.[1].id").value(newUser.getId()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.[1].roles[0].name")
                        .value("ROLE_USER"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.[1].created")
                        .value(Utils.getFormattedDate(newUser.getCreated())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.[1].email").value(newUser.getEmail()));

    }

    @Test
    public void it_should_get_user() throws Exception {
        var adminUserRequest = user(defaultAdminLogin).password(defaultAdminPassword).roles("ADMIN");
        User newUser = userRepository.save(SampleDomains.getSampleUser());
        assertThat(userRepository.findAll().size()).isEqualTo(2);

        Role role = roleRepository.findByName("ROLE_USER");
        UserRoleKey urk = new UserRoleKey(newUser.getId(), role.getId());
        userRoleRepository.save(new UserRole(urk, newUser, role));

        String url = "/user/get/" + newUser.getId().toString();
        mockMvc.perform(get(url).with(csrf()).with(adminUserRequest))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(newUser.getId()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.roles[0].name")
                        .value("ROLE_USER"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.created")
                        .value(Utils.getFormattedDate(newUser.getCreated())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.email").value(newUser.getEmail()));
    }

    @Test
    public void it_should_not_get_user_when_doesnt_exist() throws Exception {
        var adminUserRequest = user(defaultAdminLogin).password(defaultAdminPassword).roles("ADMIN");
        assertThat(userRepository.findAll().size()).isEqualTo(1);

        mockMvc.perform(get("/user/get/2").with(csrf()).with(adminUserRequest))
                .andExpect(status().isNotFound())
                .andExpect(MockMvcResultMatchers.jsonPath("$.message")
                        .value("Resource not found"));

    }

    @Test
    public void it_should_get_current_user() throws Exception {
        var adminUserRequest = user(defaultAdminLogin).password(defaultAdminPassword).roles("ADMIN");
        assertThat(userRepository.findAll().size()).isEqualTo(1);
        User adminUser = userRepository.findAll().get(0);

        String url = "/user/get-current";
        mockMvc.perform(get(url).with(csrf()).with(adminUserRequest))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(adminUser.getId()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.roles[0].name")
                        .value("ROLE_ADMIN"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.created")
                        .value(Utils.getFormattedDate(adminUser.getCreated())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.email").value(adminUser.getEmail()));
    }

    @Test
    public void it_should_delete_user() throws Exception {
        var adminUserRequest = user(defaultAdminLogin).password(defaultAdminPassword).roles("ADMIN");
        User newUser = userRepository.save(SampleDomains.getSampleUser());
        assertThat(userRepository.findAll().size()).isEqualTo(2);

        Role role = roleRepository.findByName("ROLE_USER");
        UserRoleKey urk = new UserRoleKey(newUser.getId(), role.getId());
        userRoleRepository.save(new UserRole(urk, newUser, role));

        String idString = newUser.getId().toString();
        String url = "/user/delete/" + idString;
        mockMvc.perform(delete(url).with(csrf()).with(adminUserRequest))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.message")
                        .value("User " + idString + " deleted"));

        assertThat(userRepository.findAll().size()).isEqualTo(1);
    }

    @Test
    public void it_should_not_delete_user_when_doesnt_exist() throws Exception {
        var adminUserRequest = user(defaultAdminLogin).password(defaultAdminPassword).roles("ADMIN");
        assertThat(userRepository.findAll().size()).isEqualTo(1);

        mockMvc.perform(delete("/user/delete/2").with(csrf()).with(adminUserRequest))
                .andExpect(status().isNotFound())
                .andExpect(MockMvcResultMatchers.jsonPath("$.message")
                        .value("Resource not found"));

        assertThat(userRepository.findAll().size()).isEqualTo(1);
    }

    @Test
    public void it_should_change_password() throws Exception {
        var adminUserRequest = user(defaultAdminLogin).password(defaultAdminPassword).roles("ADMIN");
        List<User> users = userRepository.findAll();
        assertThat(users.size()).isEqualTo(1);
        String oldPassword = users.get(0).getPassword();

        ChangePasswordDTO changePasswordToNotSecureDTO = new ChangePasswordDTO("a");
        mockMvc.perform(post("/user/change-password").with(csrf())
                        .with(adminUserRequest)
                        .content(mapper.writeValueAsString(changePasswordToNotSecureDTO))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isConflict());
        assertThat(userRepository.findAll().getFirst().getPassword()).isEqualTo(oldPassword);

        ChangePasswordDTO changePasswordToSecureDTO = new ChangePasswordDTO("AAaa11--");
        mockMvc.perform(post("/user/change-password").with(csrf())
                        .with(adminUserRequest)
                        .content(mapper.writeValueAsString(changePasswordToSecureDTO))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
        assertThat(userRepository.findAll().getFirst().getPassword()).isNotEqualTo(oldPassword);
    }

}
