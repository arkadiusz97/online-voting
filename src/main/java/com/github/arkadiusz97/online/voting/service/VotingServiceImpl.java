package com.github.arkadiusz97.online.voting.service;

import com.github.arkadiusz97.online.voting.domain.*;
import com.github.arkadiusz97.online.voting.dto.requestbody.CreateVotingDTO;
import com.github.arkadiusz97.online.voting.dto.responsebody.OptionDTO;
import com.github.arkadiusz97.online.voting.dto.responsebody.VotingWithOptionsDTO;
import com.github.arkadiusz97.online.voting.exception.OptionNotFoundException;
import com.github.arkadiusz97.online.voting.exception.ResourceNotFoundException;
import com.github.arkadiusz97.online.voting.exception.UserAlreadyVotedException;
import com.github.arkadiusz97.online.voting.repository.OptionRepository;
import com.github.arkadiusz97.online.voting.repository.UserOptionRepository;
import com.github.arkadiusz97.online.voting.repository.VotingRepository;

import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.List;
import java.util.LinkedList;
import java.util.Optional;
import java.util.Collection;
import java.util.Date;

@Service
@RequiredArgsConstructor
public class VotingServiceImpl implements VotingService {
    private final VotingRepository votingRepository;
    private final OptionRepository optionRepository;
    private final UserService userService;
    private final UserOptionRepository userOptionRepository;

    private static final Logger logger = LogManager.getLogger(UserServiceImpl.class);

    public void create(CreateVotingDTO createVotingDTO) {
        Voting voting = getVotingFromDto(createVotingDTO);
        voting = votingRepository.save(voting);
        List<Option> options = getVotingOptionsFromVoting(createVotingDTO, voting);
        options.forEach(optionRepository::save);
        logger.debug("Created voting with description '{}'", createVotingDTO.description());
    }

    public VotingWithOptionsDTO get(Long id) {
        logger.debug("Get voting with id", id);
        return getDTO(votingRepository.findById(id).orElseThrow(ResourceNotFoundException::new));
    }

    public List<VotingWithOptionsDTO> showMany(Integer pageNumber, Integer pageSize) {
        logger.debug("Show many votings with page number {} and page size", pageNumber, pageSize);
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
        if(selectedOptionOpt.isEmpty()) {
            String errorMessage = String.format("Option with id %d not found", Long.valueOf(optionId));
            logger.debug(errorMessage);
            throw new OptionNotFoundException();
        }
        Option selectedOption = selectedOptionOpt.get();
        UserOption userOption = new UserOption(currentUser, selectedOption);
        if(checkIfUserDidntVote(selectedOption, currentUser)) {
            userOptionRepository.save(userOption);
        } else {
            throw new UserAlreadyVotedException();
        }
    }

    @Transactional
    public void delete(Long votingId) {
        logger.debug("Called delete voting with id {}", votingId);
        Voting voting = votingRepository.findById(votingId).orElseThrow(ResourceNotFoundException::new);

        List<UserOption> userOptions = userOptionRepository.findAll();
        userOptions.forEach( userOption -> {
            if(userOption.getOption().getVoting().getId().equals(votingId)) {
                userOptionRepository.delete(userOption);
            }
        });

        List<Option> options = optionRepository.findAllByVoting(voting);
        options.forEach(option -> { //todo use one query
            optionRepository.deleteById(option.getId());
        });

        votingRepository.deleteById(votingId);
        logger.debug("Deleted voting {}", String.valueOf(votingId));
    }

    private boolean checkIfUserDidntVote(Option selectedOption, User currentUser) {
        List<UserOption> userOptions = userOptionRepository.findAllByUser(currentUser);
        Optional<UserOption> optionWithCurrentVoting = userOptions.stream()
                .filter( uo ->
                        uo.getOption().getVoting().equals(selectedOption.getVoting())
                ).findFirst();
        if(optionWithCurrentVoting.isPresent()) {
            logger.debug("User {} has already voted in voting {}", currentUser.getEmail(),
                selectedOption.getDescription());
            return false;
        }
        logger.debug("User {} has't voted in this voting yet", currentUser.getEmail());
        return true;
    }

    private Voting getVotingFromDto(CreateVotingDTO createVotingDTO) {
        Voting voting = new Voting();
        voting.setDescription(createVotingDTO.description());
        voting.setEndDate(createVotingDTO.endDate());
        voting.setCreatedDate(new Date());
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
