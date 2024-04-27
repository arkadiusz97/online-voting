package com.github.arkadiusz97.online.voting.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.arkadiusz97.online.voting.domain.Option;
import com.github.arkadiusz97.online.voting.domain.User;
import com.github.arkadiusz97.online.voting.domain.Voting;
import com.github.arkadiusz97.online.voting.dto.requestbody.CreateVotingDTO;
import com.github.arkadiusz97.online.voting.dto.requestbody.VoteDTO;
import com.github.arkadiusz97.online.voting.repository.OptionRepository;
import com.github.arkadiusz97.online.voting.repository.UserRepository;
import com.github.arkadiusz97.online.voting.repository.VotingRepository;
import com.github.arkadiusz97.online.voting.utils.SampleDomains;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.Date;
import java.util.List;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
public class VotingIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private VotingRepository votingRepository;

    @Autowired
    private OptionRepository optionRepository;

    @Value("${online-voting.default-admin-login}")
    private String defaultAdminLogin;

    @Value("${online-voting.default-admin-password}")
    private String defaultAdminPassword;

    private ObjectMapper mapper = new ObjectMapper();

    @Test
    public void it_should_vote_and_calculate_voting_result() throws Exception {
        User adminUser = userRepository.findAll().getFirst();
        assertThat(userRepository.findAll().size()).isEqualTo(1);
        var adminUserRequest = user(defaultAdminLogin).password(defaultAdminPassword).roles("ADMIN");

        CreateVotingDTO createVotingDTO = new CreateVotingDTO("sample voting",//todo refactor new Date
                new Date(SampleDomains.NOW_AS_MILLISECONDS + SampleDomains.DAY_AS_MILLISECONDS), List.of("o1", "o2", "o3"));
        mockMvc.perform(post("/voting/create").with(csrf())
                        .with(adminUserRequest)
                        .content(mapper.writeValueAsString(createVotingDTO))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated());
        Voting firstVoting = votingRepository.findAll().getFirst();
        assertThat(votingRepository.findAll().size()).isEqualTo(1);

        List<User> users = SampleDomains.getSampleUsers();
        users.forEach( user ->
                userRepository.save(user)
        );

        List<Voting> votings = SampleDomains.getSampleVotings(adminUser);
        votings.forEach( voting ->
                votingRepository.save(voting)
        );

        assertThat(optionRepository.findAll().size()).isEqualTo(3);
        List<Option> optionsForFirstVoting = optionRepository.findAllByVoting(firstVoting);

        mockMvc.perform(post("/voting/vote").with(csrf())
                        .with(user(users.get(0).getEmail()).password(users.get(0).getPassword()).roles("USER"))
                        .content(mapper.writeValueAsString(new VoteDTO(optionsForFirstVoting.get(0).getId())))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        mockMvc.perform(post("/voting/vote").with(csrf())
                        .with(user(users.get(1).getEmail()).password(users.get(1).getPassword()).roles("USER"))
                        .content(mapper.writeValueAsString(new VoteDTO(optionsForFirstVoting.get(0).getId())))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());


        mockMvc.perform(post("/voting/vote").with(csrf())
                        .with(user(users.get(2).getEmail()).password(users.get(2).getPassword()).roles("USER"))
                        .content(mapper.writeValueAsString(new VoteDTO(optionsForFirstVoting.get(1).getId())))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        mockMvc.perform(get("/voting/result/" + firstVoting.getId().toString()).with(csrf())
                .with(adminUserRequest))
                .andExpect(MockMvcResultMatchers.jsonPath("$.votingDescription")
                        .value(createVotingDTO.description()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.totalVotes")
                        .value(3L))
                .andExpect(MockMvcResultMatchers.jsonPath("$.optionResults[0].optionDescription")
                        .value(optionsForFirstVoting.get(0).getDescription()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.optionResults[0].numberOfChoices")
                        .value(2))
                .andExpect(MockMvcResultMatchers.jsonPath("$.optionResults[0].percentageOfChoices")
                        .value(66.67))
                .andExpect(MockMvcResultMatchers.jsonPath("$.optionResults[1].optionDescription")
                        .value(optionsForFirstVoting.get(1).getDescription()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.optionResults[1].numberOfChoices")
                        .value(1))
                .andExpect(MockMvcResultMatchers.jsonPath("$.optionResults[1].percentageOfChoices")
                        .value(33))
                .andExpect(MockMvcResultMatchers.jsonPath("$.winningOptions[0]")
                        .value(optionsForFirstVoting.get(0).getDescription()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.winningOptions[1]")
                        .doesNotExist())
                .andExpect(MockMvcResultMatchers.jsonPath("$.isFinished")
                        .value(false));

    }

}
