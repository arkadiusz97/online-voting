package com.github.arkadiusz97.online.voting.service;

import com.github.arkadiusz97.online.voting.domain.Option;
import com.github.arkadiusz97.online.voting.domain.User;
import com.github.arkadiusz97.online.voting.domain.UserOption;
import com.github.arkadiusz97.online.voting.domain.Voting;
import com.github.arkadiusz97.online.voting.dto.requestbody.CreateVotingDTO;
import com.github.arkadiusz97.online.voting.dto.responsebody.VotingSummaryDto;
import com.github.arkadiusz97.online.voting.dto.responsebody.VotingWithOptionsDTO;
import com.github.arkadiusz97.online.voting.exception.*;
import com.github.arkadiusz97.online.voting.repository.OptionRepository;
import com.github.arkadiusz97.online.voting.repository.UserOptionRepository;
import com.github.arkadiusz97.online.voting.repository.VotingRepository;
import com.github.arkadiusz97.online.voting.utils.SampleDomains;
import com.github.arkadiusz97.online.voting.utils.Utils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.util.*;

import static org.assertj.core.api.Assertions.*;
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
        Date tomorrow = Utils.getDateAheadOfDays(1);

        CreateVotingDTO createVotingDTO = new CreateVotingDTO(
            "sample voting", tomorrow, List.of("opt1", "opt2", "opt3"));
        votingServiceImpl.create(createVotingDTO);

        List<Voting> votings = votingRepository.findAll();
        verify(votingRepository, times(1)).save(any());
        verify(optionRepository, times(3)).save(any());
    }

    @Test
    public void createWhenEndDateIsBehindTodayTest() {
        Date yesterday = Utils.getDateAheadOfDays(-1);
        CreateVotingDTO createVotingDTO = new CreateVotingDTO(
                "sample voting", yesterday, List.of("opt1", "opt2", "opt3"));

        assertThatExceptionOfType(VotingEndDateIsBehindTodayException.class).isThrownBy(() ->
                votingServiceImpl.create(createVotingDTO)
        );
        verify(votingRepository, times(0)).save(any());
        verify(optionRepository, times(0)).save(any());
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

    @Test
    public void voteTest() {
        Long optionId = 1L;
        User user = SampleDomains.getSampleUser();
        LinkedList<Voting> votings = SampleDomains.getSampleVotings(user);
        LinkedList<Option> options = SampleDomains.getSampleOptions(votings.get(0));
        Mockito.when(optionRepository.findById(optionId)).thenReturn(Optional.of(options.get(0)));
        Mockito.when(userService.getCurrentUser()).thenReturn(user);

        votingServiceImpl.vote(optionId);
        verify(userService).getCurrentUser();
        verify(optionRepository).findById(1L);
        verify(userOptionRepository).save(any(UserOption.class));
    }

    @Test
    public void voteWhenUserAlreadyVotedTest() {
        Long optionId = 1L;
        User user = SampleDomains.getSampleUser();
        LinkedList<Voting> votings = SampleDomains.getSampleVotings(user);
        LinkedList<Option> options = SampleDomains.getSampleOptions(votings.get(0));
        Option selectedOption = options.get(0);
        Mockito.when(optionRepository.findById(optionId)).thenReturn(Optional.of(selectedOption));
        Mockito.when(userOptionRepository.findAllByUser(user)).thenReturn(
                Collections.singletonList(new UserOption(user, selectedOption))
        );
        Mockito.when(userService.getCurrentUser()).thenReturn(user);

        assertThatExceptionOfType(UserAlreadyVotedException.class).isThrownBy(() -> votingServiceImpl.vote(optionId));
        verify(userService).getCurrentUser();
        verify(optionRepository).findById(1L);
    }

    @Test
    public void voteWhenVotingIsExpiredTest() {
        Long optionId = 1L;
        User user = SampleDomains.getSampleUser();
        LinkedList<Voting> votings = SampleDomains.getSampleVotings(user);
        Voting selectedVoting = votings.get(0);
        selectedVoting.setEndDate(Utils.getDateAheadOfDays(-1));
        LinkedList<Option> options = SampleDomains.getSampleOptions(selectedVoting);
        Mockito.when(optionRepository.findById(optionId)).thenReturn(Optional.of(options.get(0)));
        Mockito.when(userService.getCurrentUser()).thenReturn(user);

        assertThatExceptionOfType(VotingIsExpiredException.class).isThrownBy(() -> votingServiceImpl.vote(optionId));
        verify(userService).getCurrentUser();
        verify(optionRepository).findById(1L);
    }

    @Test
    public void voteTestWhenSelectedOptionDoesntExists() {
        Long optionId = 1L;
        User user = SampleDomains.getSampleUser();
        Mockito.when(optionRepository.findById(optionId)).thenReturn(Optional.empty());
        Mockito.when(userService.getCurrentUser()).thenReturn(user);
        assertThatExceptionOfType(OptionNotFoundException.class).isThrownBy(() -> votingServiceImpl.vote(optionId));
        verify(userService).getCurrentUser();
    }

    @Test
    public void getVotingResultTest() {
        Long votingId = 2L;
        LinkedList<User> user = SampleDomains.getSampleUsers();
        LinkedList<Voting> votings = SampleDomains.getSampleVotings(user.get(0));
        Voting selectedVoting = votings.get(0);
        LinkedList<Option> options = SampleDomains.getSampleOptions(selectedVoting);
        Mockito.when(votingRepository.findById(votingId)).thenReturn(Optional.of(selectedVoting));
        Mockito.when(userOptionRepository.findAll()).thenReturn(List.of(
            new UserOption(user.get(0), options.get(0)),
            new UserOption(user.get(1), options.get(0)),
            new UserOption(user.get(2), options.get(1))
        ));

        VotingSummaryDto result = votingServiceImpl.getVotingResult(votingId);
        assertThat(result.votingDescription()).isEqualTo("voting 1");
        assertThat(result.totalVotes()).isEqualTo(3);
        assertThat(result.optionResults().size()).isEqualTo(2);
        assertThat(result.optionResults().get(0).optionDescription()).isEqualTo("opt1");
        assertThat(result.optionResults().get(0).numberOfChoices()).isEqualTo(2);
        assertThat(result.optionResults().get(1).optionDescription()).isEqualTo("opt2");
        assertThat(result.optionResults().get(1).numberOfChoices()).isEqualTo(1);
        assertThat(result.winningOptions().size()).isEqualTo(1);
        assertThat(result.winningOptions().get(0)).isEqualTo("opt1");
    }

    @Test
    public void deleteTest() {
        Long votingId = 1L;
        LinkedList<User> user = SampleDomains.getSampleUsers();
        LinkedList<Voting> votings = SampleDomains.getSampleVotings(user.get(0));
        Voting voting = votings.get(0);
        voting.setId(votingId);
        Mockito.when(votingRepository.findById(votingId)).thenReturn(Optional.of(voting));
        LinkedList<Option> options = SampleDomains.getSampleOptions(voting);
        Mockito.when(optionRepository.findAllByVoting(voting)).thenReturn(options);
        Mockito.when(userOptionRepository.findAll()).thenReturn(List.of(
                new UserOption(user.get(0), options.get(0)),
                new UserOption(user.get(1), options.get(0)),
                new UserOption(user.get(2), options.get(1))
        ));

        votingServiceImpl.delete(votingId);
        verify(userOptionRepository, times(3)).delete(any());
        verify(optionRepository, times(3)).deleteById(any());
        verify(votingRepository).deleteById(votingId);
    }

}
