package com.github.arkadiusz97.online.voting.service;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;

import org.apache.commons.lang3.RandomStringUtils;

@Service
public class PasswordGeneratorServiceImpl implements PasswordGeneratorService {

    private static final Logger logger = LogManager.getLogger(PasswordGeneratorServiceImpl.class);

    @Override
    public String generatePassword() {
        logger.debug("Generated random password");
        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        return RandomStringUtils.random(8, characters);
    }

}
