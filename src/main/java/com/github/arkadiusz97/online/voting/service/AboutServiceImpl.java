package com.github.arkadiusz97.online.voting.service;

import com.github.arkadiusz97.online.voting.dto.responsebody.About;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class AboutServiceImpl implements AboutService {

    private final String version;
    private static final Logger logger = LogManager.getLogger(UserServiceImpl.class);

    public AboutServiceImpl(@Value("${app-version}") String version) {
        this.version = version;
    }
    public About getAbout() {
        logger.debug("Called about method");
        return new About(version, new Date());
    }
}
