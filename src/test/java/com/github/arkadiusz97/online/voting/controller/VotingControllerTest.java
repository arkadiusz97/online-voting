package com.github.arkadiusz97.online.voting.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.arkadiusz97.online.voting.dto.requestbody.CreateVotingDTO;
import com.github.arkadiusz97.online.voting.dto.requestbody.VoteDTO;
import com.github.arkadiusz97.online.voting.dto.responsebody.VotingSummaryDto;
import com.github.arkadiusz97.online.voting.dto.responsebody.VotingWithOptionsDTO;
import com.github.arkadiusz97.online.voting.service.VotingService;

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

import java.util.LinkedList;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(VotingController.class)
public class VotingControllerTest {

    @MockBean
    private VotingService votingService;

    @Autowired
    private MockMvc mockMvc;

    private ObjectMapper mapper = new ObjectMapper();

    @Test
    @WithMockUser(roles = "ADMIN", username = "some-mail1@domain.eu")
    public void it_should_create_voting() throws Exception {
        CreateVotingDTO createVotingDTO = SampleDomains.getSampleVotingDTO();
        Mockito.doNothing().when(votingService).create(createVotingDTO);
        mockMvc.perform(post("/voting/create").with(csrf())
                    .content(mapper.writeValueAsString(createVotingDTO))
                    .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(MockMvcResultMatchers.jsonPath("$.message")
                        .value("created"));
        verify(votingService, times(1)).create(createVotingDTO);
    }

    @Test
    @WithMockUser(roles = "USER", username = "some-mail1@domain.eu")
    public void it_should_get_many_votings() throws Exception {
        LinkedList<VotingWithOptionsDTO> votingsWithOptionsDTO = SampleDomains.getSampleVotingWithOptionsDTOs();
        Integer pageNumber = 1;
        Integer pageSize = 3;
        Mockito.when(votingService.showMany(pageNumber, pageSize)).thenReturn(votingsWithOptionsDTO);
        String url = "/voting/get?pageNumber=" + pageNumber.toString() + "&pageSize=" + pageSize.toString();
        mockMvc.perform(get(url).with(csrf()))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.[1].id")
                        .value(votingsWithOptionsDTO.get(1).id()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.[1].description")
                        .value(votingsWithOptionsDTO.get(1).description()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.[1].endDate")
                        .value(Utils.getFormattedDate(votingsWithOptionsDTO.get(1).endDate())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.[1].createdDate")
                        .value(Utils.getFormattedDate(votingsWithOptionsDTO.get(1).createdDate())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.[1].createdBy.email")
                        .value(votingsWithOptionsDTO.get(1).createdBy().email()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.[1].votingOptions[2].description")
                        .value(votingsWithOptionsDTO.get(1).votingOptions().get(2).description()));
    }

    @Test
    @WithMockUser(roles = "USER", username = "some-mail1@domain.eu")
    public void it_should_get_voting() throws Exception {
        VotingWithOptionsDTO votingWithOptionsDTO = SampleDomains.getSampleVotingWithOptionsDTO();
        Long id = 1L;
        Mockito.when(votingService.get(id)).thenReturn(votingWithOptionsDTO);
        String url = "/voting/get/" + id.toString();
        mockMvc.perform(get(url).with(csrf()))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id")
                        .value(votingWithOptionsDTO.id()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.description")
                        .value(votingWithOptionsDTO.description()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.endDate")
                        .value(Utils.getFormattedDate(votingWithOptionsDTO.endDate())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.createdDate")
                        .value(Utils.getFormattedDate(votingWithOptionsDTO.createdDate())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.createdBy.email")
                        .value(votingWithOptionsDTO.createdBy().email()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.votingOptions[2].description")
                        .value(votingWithOptionsDTO.votingOptions().get(2).description()));
    }

    @Test
    @WithMockUser(roles = "ADMIN", username = "some-mail1@domain.eu")
    public void it_should_delete_voting() throws Exception {
        Long id = 1L;
        String url = "/voting/delete/" + id.toString();
        mockMvc.perform(delete(url).with(csrf()))
                .andExpect(status().isOk());
        verify(votingService, times(1)).delete(id);
    }

    @Test
    @WithMockUser(roles = "USER", username = "some-mail1@domain.eu")
    public void it_should_vote() throws Exception {
        Long id = 1L;
        mockMvc.perform(post("/voting/vote").with(csrf())
                    .content(mapper.writeValueAsString(new VoteDTO(id)))
                    .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.message")
                        .value("User voted"));
        verify(votingService, times(1)).vote(id);
    }

    @Test
    @WithMockUser(roles = "USER", username = "some-mail1@domain.eu")
    public void it_get_voting_result() throws Exception {
        Long id = 1L;
        VotingSummaryDto votingSummaryDto = SampleDomains.getSampleVotingSummaryDto();
        Mockito.when(votingService.getVotingResult(id)).thenReturn(votingSummaryDto);
        String url = "/voting/result/" + id.toString();
        mockMvc.perform(get(url).with(csrf()))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.votingDescription")
                        .value(votingSummaryDto.votingDescription()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.totalVotes")
                        .value(votingSummaryDto.totalVotes()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.optionResults[0].optionDescription")
                        .value(votingSummaryDto.optionResults().get(0).optionDescription()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.winningOptions[0]")
                        .value(votingSummaryDto.winningOptions().get(0)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.isFinished")
                        .value(votingSummaryDto.isFinished()));
    }

}
