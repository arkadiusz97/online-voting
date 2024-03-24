package com.github.arkadiusz97.online.voting.service;

import com.github.arkadiusz97.online.voting.dto.responsebody.About;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class AboutServiceImpl implements AboutService {

    private final String version;

    public AboutServiceImpl(@Value("${app-version}") String version) {
        this.version = version;
    }
    public About getAbout() {
        return new About(version, new Date());
    }
}
