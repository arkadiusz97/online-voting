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
import com.github.arkadiusz97.online.voting.utils.Utils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedList;
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
@Rollback
@Transactional
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
    public void it_should_create_voting() throws Exception {
        User adminUser = userRepository.findAll().getFirst();
        assertThat(userRepository.findAll().size()).isEqualTo(1);
        var adminUserRequest = user(defaultAdminLogin).password(defaultAdminPassword).roles("ADMIN");

        CreateVotingDTO createVotingDTO = new CreateVotingDTO("sample voting",
                Utils.getDateAheadOfDays(1), List.of("o1", "o2", "o3"));
        mockMvc.perform(post("/voting/create").with(csrf())
                        .with(adminUserRequest)
                        .content(mapper.writeValueAsString(createVotingDTO))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated());
        assertThat(votingRepository.findAll().size()).isEqualTo(1);
        Voting voting = votingRepository.findAll().getFirst();
        List<Option> options = optionRepository.findAllByVoting(voting);
        List<String> optionDescriptions = options.stream().map( it -> it.getDescription()).toList();

        assertThat(voting.getCreatedBy().getEmail()).isEqualTo(defaultAdminLogin);
        assertThat(voting.getEndDate()).isEqualTo(createVotingDTO.endDate());
        assertThat(optionDescriptions.contains("o1")).isEqualTo(true);
        assertThat(optionDescriptions.contains("o2")).isEqualTo(true);
        assertThat(optionDescriptions.contains("o3")).isEqualTo(true);
    }

    @Test
    public void it_should_get_votings() throws Exception {
        User adminUser = userRepository.findAll().getFirst();
        assertThat(userRepository.findAll().size()).isEqualTo(1);
        var adminUserRequest = user(defaultAdminLogin).password(defaultAdminPassword).roles("ADMIN");

        List<Voting> votings = SampleDomains.getSampleVotings(adminUser);
        votings.forEach( voting ->
                votingRepository.save(voting)
        );
        List<Voting> votingsFromDatabase = votingRepository.findAll();
        assertThat(votingsFromDatabase.size()).isEqualTo(3);
        LinkedList<Option> optionsToSaveFor1Voting = SampleDomains.getSampleOptions(votingsFromDatabase.get(1));
        optionsToSaveFor1Voting.forEach(option -> {
            optionRepository.save(option);
        });

        mockMvc.perform(get("/voting/get?pageNumber=0&pageSize=5").with(csrf())
                        .with(adminUserRequest))

                .andExpect(MockMvcResultMatchers.jsonPath("$.[0].id")
                        .value(1))
                .andExpect(MockMvcResultMatchers.jsonPath("$.[0].description")
                        .value("voting 1"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.[0].endDate")
                        .value(Utils.getFormattedDate(votings.get(0).getEndDate())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.[0].createdDate")
                        .value(Utils.getFormattedDate(votings.get(0).getCreatedDate())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.[0].createdBy.id")
                        .value(votings.get(0).getCreatedBy().getId()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.[0].createdBy.email")
                        .value(votings.get(0).getCreatedBy().getEmail()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.[0].createdBy.created")
                        .value(Utils.getFormattedDate(votings.get(0).getCreatedBy().getCreated())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.[0].createdBy.roles[0].name")
                        .value("ROLE_ADMIN"))

                .andExpect(MockMvcResultMatchers.jsonPath("$.[1].id")
                        .value(2))
                .andExpect(MockMvcResultMatchers.jsonPath("$.[1].description")
                        .value("voting 2"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.[1].endDate")
                        .value(Utils.getFormattedDate(votings.get(1).getEndDate())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.[1].createdDate")
                        .value(Utils.getFormattedDate(votings.get(1).getCreatedDate())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.[1].createdBy.id")
                        .value(votings.get(1).getCreatedBy().getId()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.[1].createdBy.email")
                        .value(votings.get(1).getCreatedBy().getEmail()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.[1].createdBy.created")
                        .value(Utils.getFormattedDate(votings.get(1).getCreatedBy().getCreated())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.[1].createdBy.roles[0].name")
                        .value("ROLE_ADMIN"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.[1].votingOptions[0].description")
                        .value("opt1"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.[1].votingOptions[1].description")
                        .value("opt2"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.[1].votingOptions[2].description")
                        .value("opt3"))

                .andExpect(MockMvcResultMatchers.jsonPath("$.[2].id")
                        .value(3))
                .andExpect(MockMvcResultMatchers.jsonPath("$.[2].description")
                        .value("voting 3"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.[2].endDate")
                        .value(Utils.getFormattedDate(votings.get(2).getEndDate())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.[2].createdDate")
                        .value(Utils.getFormattedDate(votings.get(2).getCreatedDate())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.[2].createdBy.id")
                        .value(votings.get(2).getCreatedBy().getId()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.[2].createdBy.email")
                        .value(votings.get(2).getCreatedBy().getEmail()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.[2].createdBy.created")
                        .value(Utils.getFormattedDate(votings.get(2).getCreatedBy().getCreated())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.[2].createdBy.roles[0].name")
                        .value("ROLE_ADMIN"));
    }

    @Test
    public void it_should_vote_and_calculate_voting_result() throws Exception {
        User adminUser = userRepository.findAll().getFirst();
        assertThat(userRepository.findAll().size()).isEqualTo(1);
        var adminUserRequest = user(defaultAdminLogin).password(defaultAdminPassword).roles("ADMIN");

        CreateVotingDTO createVotingDTO = new CreateVotingDTO("sample voting",
                Utils.getDateAheadOfDays(1), List.of("o1", "o2", "o3"));
        mockMvc.perform(post("/voting/create").with(csrf())
                        .with(adminUserRequest)
                        .content(mapper.writeValueAsString(createVotingDTO))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated());
        assertThat(votingRepository.findAll().size()).isEqualTo(1);
        Voting firstVoting = votingRepository.findAll().getFirst();

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
