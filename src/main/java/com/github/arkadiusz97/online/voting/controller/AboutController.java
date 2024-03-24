package com.github.arkadiusz97.online.voting.controller;

import com.github.arkadiusz97.online.voting.dto.responsebody.About;
import com.github.arkadiusz97.online.voting.service.AboutService;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;

@RestController
@RequestMapping("about")
@PreAuthorize("hasRole('ROLE_ADMIN')")//todo move to service
public class AboutController {

    private final AboutService aboutService;

    public AboutController(AboutService aboutService) {
        this.aboutService = aboutService;
    }
    @GetMapping("")
    public ResponseEntity<About> about() {
        About about = aboutService.getAbout();
        return new ResponseEntity<About>(about, HttpStatusCode.valueOf(200));
    }
}
