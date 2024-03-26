package com.github.arkadiusz97.online.voting.service;

import com.github.arkadiusz97.online.voting.domain.*;
import com.github.arkadiusz97.online.voting.dto.requestbody.CreateVotingDTO;
import com.github.arkadiusz97.online.voting.dto.responsebody.OptionDTO;
import com.github.arkadiusz97.online.voting.dto.responsebody.VotingWithOptionsDTO;
import com.github.arkadiusz97.online.voting.repository.OptionRepository;
import com.github.arkadiusz97.online.voting.repository.UserOptionRepository;
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
    private final UserOptionRepository userOptionRepository;

    @Autowired
    public VotingServiceImpl(VotingRepository votingRepository, OptionRepository optionRepository,
            UserService userService, UserOptionRepository userOptionRepository) {
        this.votingRepository = votingRepository;
        this.optionRepository = optionRepository;
        this.userService = userService;
        this.userOptionRepository = userOptionRepository;
    }

    public String create(CreateVotingDTO createVotingDTO) {//todo add validation, etc
        Voting voting = getVotingFromDto(createVotingDTO);
        voting = votingRepository.save(voting);
        List<Option> options = getVotingOptionsFromVoting(createVotingDTO, voting);
        options.forEach(optionRepository::save);
        return "created";
    }

    public VotingWithOptionsDTO get(Long id) {
        return getDTO(votingRepository.findById(id).get());//todo throw error instead of returning value by get
    }

    public List<VotingWithOptionsDTO> showMany(Integer pageNumber, Integer pageSize) {
        Pageable pageable = PageRequest.of(pageNumber, pageSize);
        return votingRepository
            .findAll(pageable)
            .stream()
            .map(this::getDTO)
            .collect(Collectors.toList());
    }

    public void vote(Long optionId) {
        User currentUser = userService.getCurrentUser();
        Optional<Option> selectedOptionOpt = optionRepository.findById(optionId);
        if(selectedOptionOpt.isPresent()) {
            Option selectedOption = selectedOptionOpt.get();
            UserOption userOption = new UserOption(currentUser, selectedOption);
            //todo add checking if user already voted
            userOptionRepository.save(userOption);
        } else {
            //throw new Exception("Option with id %d not found", Long.valueOf(optionId));//todo change to own exception class
        }
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
