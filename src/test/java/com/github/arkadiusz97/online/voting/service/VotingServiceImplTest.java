package com.github.arkadiusz97.online.voting.service;

import com.github.arkadiusz97.online.voting.domain.User;
import com.github.arkadiusz97.online.voting.domain.Voting;
import com.github.arkadiusz97.online.voting.dto.requestbody.CreateVotingDTO;
import com.github.arkadiusz97.online.voting.dto.responsebody.VotingWithOptionsDTO;
import com.github.arkadiusz97.online.voting.repository.OptionRepository;
import com.github.arkadiusz97.online.voting.repository.UserOptionRepository;
import com.github.arkadiusz97.online.voting.repository.VotingRepository;
import com.github.arkadiusz97.online.voting.utils.SampleDomains;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class VotingServiceImplTest {

    @InjectMocks
    private VotingServiceImpl votingServiceImpl;

    @Mock
    private VotingRepository votingRepository;

    @Mock
    private OptionRepository optionRepository;

    @Mock
    private UserService userService;

    @Mock
    private UserOptionRepository userOptionRepository;

    @Test
    public void createTest() {
        Date now = new Date();
        Date tomorrow = new Date(now.getTime() + (1000 * 60 * 60 * 24));

        CreateVotingDTO createVotingDTO = new CreateVotingDTO(
            "sample voting", tomorrow, List.of("opt1", "opt2", "opt3"));
        votingServiceImpl.create(createVotingDTO);

        List<Voting> votings = votingRepository.findAll();
        verify(votingRepository, times(1)).save(any());
        verify(optionRepository, times(3)).save(any());
    }

    @Test
    public void getTest() {
        User user = SampleDomains.getSampleUser();
        List<Voting> votings = SampleDomains.getSampleVotings(user);
        Voting voting = votings.get(0);
        Long id = 1L;
        Mockito.when(votingRepository.findById(id)).thenReturn(Optional.of(voting));
        VotingWithOptionsDTO dto = votingServiceImpl.get(id);
        assertThat(voting.getDescription()).isEqualTo(dto.description());
        assertThat(voting.getCreatedDate()).isEqualTo(dto.createdDate());
        assertThat(voting.getEndDate()).isEqualTo(dto.endDate());
    }

    @Test
    public void showManyTest() {
        User user = SampleDomains.getSampleUser();
        List<Voting> votings = SampleDomains.getSampleVotings(user);
        PageRequest pageRequest = PageRequest.of(0, 2);
        List<Voting> votingsFromPage = Arrays.asList(votings.get(0), votings.get(1));
        Page<Voting> votingPage = new PageImpl<>(votingsFromPage, pageRequest, 2);
        Mockito.when(votingRepository.findAll(pageRequest)).thenReturn(votingPage);
        List<VotingWithOptionsDTO> result = votingServiceImpl.showMany(0, 2);
        assertThat(2).isEqualTo(result.size());
        assertThat(votings.get(0).getDescription()).isEqualTo(result.get(0).description());
    }

}
