package com.github.arkadiusz97.online.voting.service;

import com.github.arkadiusz97.online.voting.domain.*;
import com.github.arkadiusz97.online.voting.dto.requestbody.CreateVotingDTO;
import com.github.arkadiusz97.online.voting.dto.responsebody.OptionDTO;
import com.github.arkadiusz97.online.voting.dto.responsebody.OptionResultDTO;
import com.github.arkadiusz97.online.voting.dto.responsebody.VotingSummaryDto;
import com.github.arkadiusz97.online.voting.dto.responsebody.VotingWithOptionsDTO;
import com.github.arkadiusz97.online.voting.exception.*;
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

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.LinkedList;
import java.util.HashMap;
import java.util.Comparator;
import java.util.Date;
import java.util.Collection;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class VotingServiceImpl implements VotingService {
    private final VotingRepository votingRepository;
    private final OptionRepository optionRepository;
    private final UserService userService;
    private final UserOptionRepository userOptionRepository;

    private static final Logger logger = LogManager.getLogger(VotingServiceImpl.class);

    @Override
    public void create(CreateVotingDTO createVotingDTO) {
        if(new Date().after(createVotingDTO.endDate())) {
            throw new VotingEndDateIsBehindTodayException();
        }
        Voting voting = getVotingFromDto(createVotingDTO);
        voting = votingRepository.save(voting);
        List<Option> options = getVotingOptionsFromVoting(createVotingDTO, voting);
        options.forEach(optionRepository::save);
        logger.debug("Created voting with description '{}'", createVotingDTO.description());
    }

    @Override
    public VotingWithOptionsDTO get(Long id) {
        logger.debug("Get voting with id", id);
        return getDTO(votingRepository.findById(id).orElseThrow(ResourceNotFoundException::new));
    }

    @Override
    public List<VotingWithOptionsDTO> showMany(Integer pageNumber, Integer pageSize) {
        logger.debug("Show many votings with page number {} and page size", pageNumber, pageSize);
        Pageable pageable = PageRequest.of(pageNumber, pageSize);
        return votingRepository
            .findAll(pageable)
            .stream()
            .map(this::getDTO)
            .collect(Collectors.toList());
    }

    @Override
    public void vote(Long optionId) {
        logger.debug("Vote at option {}", optionId);
        User currentUser = userService.getCurrentUser();
        Optional<Option> selectedOptionOpt = optionRepository.findById(optionId);
        if(selectedOptionOpt.isEmpty()) {
            String errorMessage = String.format("Option with id %d not found", Long.valueOf(optionId));
            logger.debug(errorMessage);
            throw new OptionNotFoundException();
        }
        Option selectedOption = selectedOptionOpt.get();
        Voting votingForSelectedOption = selectedOption.getVoting();
        if(new Date().after(votingForSelectedOption.getEndDate())) {
            throw new VotingIsExpiredException();
        }
        UserOption userOption = new UserOption(currentUser, selectedOption);
        if(checkIfUserDidntVote(selectedOption, currentUser)) {
            userOptionRepository.save(userOption);
        } else {
            throw new UserAlreadyVotedException();
        }
    }

    @Override
    public VotingSummaryDto getVotingResult(Long votingId) {
        Optional<Voting> votingOptional = votingRepository.findById(votingId);
        Voting voting = votingOptional.orElseThrow(ResourceNotFoundException::new);
        List<Option> choosenOptions = getChoosenOptionsByVoting(voting);
        HashMap<String, Long> votesOnOptions = getNumberOfVotesOnOptions(choosenOptions);
        List<OptionResultDTO> optionResults = new LinkedList<>();
        int totalNumberOfChoices = choosenOptions.size();
        votesOnOptions.forEach((k, v) -> {
            BigDecimal percentageOfChoices = new BigDecimal(v)
                .divide(new BigDecimal(totalNumberOfChoices), 4, RoundingMode.HALF_DOWN)
                .multiply(new BigDecimal(100));
            optionResults.add(new OptionResultDTO(k, Long.valueOf(v), percentageOfChoices));
        });
        boolean isFinished = checkIfVotingIsFinished(voting);
        List<String> winningOptions = getWinningOptions(optionResults);
        VotingSummaryDto result = new VotingSummaryDto(voting.getDescription(), Long.valueOf(totalNumberOfChoices),
            optionResults, winningOptions, isFinished);
        return result;
    }

    @Override
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
        options.forEach(option -> {
            optionRepository.deleteById(option.getId());
        });

        votingRepository.deleteById(votingId);
        logger.debug("Deleted voting {}", String.valueOf(votingId));
    }

    private List<String> getWinningOptions(List<OptionResultDTO> optionResults) {
        Optional<OptionResultDTO> winningFirstOptionOptional = optionResults.stream()
            .max(Comparator.comparing(OptionResultDTO::numberOfChoices));
        if(winningFirstOptionOptional.isEmpty()) {
            return Collections.emptyList();
        }
        OptionResultDTO winningFirstOption = winningFirstOptionOptional.get();
        List<String> result = optionResults.stream().filter( or ->
            or.numberOfChoices().equals(winningFirstOption.numberOfChoices())
            && or.optionDescription() != winningFirstOption.optionDescription()
        ).map(OptionResultDTO::optionDescription)
        .collect(Collectors.toList());
        result.add(winningFirstOption.optionDescription());
        return result;
    }

    private boolean checkIfVotingIsFinished(Voting voting) {
        Date now = new Date();
        return now.after(voting.getEndDate());
    }

    private List<Option> getChoosenOptionsByVoting(Voting voting) {
        return userOptionRepository.findAll().stream()
            .filter( uo ->
                uo.getOption().getVoting().getId().equals(voting.getId())
            )
            .map(UserOption::getOption)
            .collect(Collectors.toList());
    }

    private HashMap<String, Long> getNumberOfVotesOnOptions(List<Option> choosenOptions) {
        HashMap<String, Long> votesOnOptions = new HashMap<>();
        choosenOptions.forEach( option -> {
            String optionDescription = option.getDescription();
            if(votesOnOptions.containsKey(optionDescription)) {
                Long votesOnOption = votesOnOptions.get(optionDescription);
                votesOnOptions.put(optionDescription, votesOnOption + 1);
            } else {
                votesOnOptions.put(optionDescription, 1L);
            }
        });
       return votesOnOptions;
    }

    private boolean checkIfUserDidntVote(Option selectedOption, User currentUser) {
        List<UserOption> userOptions = userOptionRepository.findAllByUser(currentUser);
        Optional<UserOption> optionWithCurrentVoting = userOptions.stream()
                .filter( uo ->
                        uo.getOption().getVoting().getId().equals(selectedOption.getVoting().getId())
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
