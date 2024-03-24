package com.github.arkadiusz97.online.voting.service;

import com.github.arkadiusz97.online.voting.domain.Option;
import com.github.arkadiusz97.online.voting.domain.User;
import com.github.arkadiusz97.online.voting.domain.UserRole;
import com.github.arkadiusz97.online.voting.domain.Voting;
import com.github.arkadiusz97.online.voting.dto.requestbody.CreateVotingDTO;
import com.github.arkadiusz97.online.voting.dto.responsebody.OptionDTO;
import com.github.arkadiusz97.online.voting.dto.responsebody.UserDTO;
import com.github.arkadiusz97.online.voting.dto.responsebody.VotingWithOptionsDTO;
import com.github.arkadiusz97.online.voting.repository.OptionRepository;
import com.github.arkadiusz97.online.voting.repository.UserRepository;
import com.github.arkadiusz97.online.voting.repository.VotingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class VotingServiceImpl implements VotingService {
    private final VotingRepository votingRepository;
    private final OptionRepository optionRepository;
    private final UserService userService;

    @Autowired
    public VotingServiceImpl(VotingRepository votingRepository,
        OptionRepository optionRepository, UserService userService) {

        this.votingRepository = votingRepository;
        this.optionRepository = optionRepository;
        this.userService = userService;
    }

    public String create(CreateVotingDTO createVotingDTO) {//todo add validation, etc
        Voting voting = getVotingFromDto(createVotingDTO);
        voting = votingRepository.save(voting);
        List<Option> options = getVotingOptionsFromVoting(createVotingDTO, voting);
        options.forEach(optionRepository::save);
        return "created";
    }

    public VotingWithOptionsDTO get(Long id) {
        return getDTO(votingRepository.findById(id).get());//todo chage get
    }

    public List<VotingWithOptionsDTO> showMany(Integer pageNumber, Integer pageSize) {
        Pageable pageable = PageRequest.of(pageNumber, pageSize);
        return votingRepository
            .findAll(pageable)
            .stream()
            .map(this::getDTO)
            .collect(Collectors.toList());
    }

    public String vote(Long votingId, Long optionId) {
        return null;//todo implement
    }

    public String delete(Long votingId) {
        Voting voting = votingRepository.findById(votingId).get();//todo implement checking not existing voting
        List<Option> options = optionRepository.findAllByVoting(voting);
        options.forEach(option -> { //todo use one query
            optionRepository.deleteById(option.getId());
        });
        votingRepository.deleteById(votingId);
        return "deleted";//todo implement checking
    }

    private Voting getVotingFromDto(CreateVotingDTO createVotingDTO) {
        Voting voting = new Voting();
        voting.setDescription(createVotingDTO.description());
        voting.setEndDate(createVotingDTO.endDate());
        voting.setCreatedDate(new Date());
        voting.setIsFinished(false);
        voting.setCreatedBy(userService.getCurrentUser());
        return voting;
    }

    private VotingWithOptionsDTO getDTO(final Voting voting) {
        List<Option> options = optionRepository.findAllByVoting(voting);
        Stream<Option> optionStream = Optional.ofNullable(options)
            .map(Collection::stream)
            .orElseGet(Stream::empty);
        return new VotingWithOptionsDTO(
            voting.getId(),
            voting.getDescription(),
            voting.getEndDate(),
            voting.getCreatedDate(),
            voting.getIsFinished(),
            userService.getDTO(voting.getCreatedBy()),
            optionStream
                .map(this::getOptionDTO)
                .collect(Collectors.toList())
        );
    }

    private List<Option> getVotingOptionsFromVoting(CreateVotingDTO createVotingDTO, Voting voting) {
        LinkedList<Option> options = new LinkedList<Option>();
        createVotingDTO.options().forEach( option -> {
            options.add(new Option(
                option,
                voting
            ));
        });
        return options;
    }

    private OptionDTO getOptionDTO(Option option) {
        return new OptionDTO(
            option.getId(),
            option.getDescription()
        );
    }
}
