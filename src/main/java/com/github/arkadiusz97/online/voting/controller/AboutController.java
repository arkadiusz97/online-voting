package com.github.arkadiusz97.online.voting.controller;

import com.github.arkadiusz97.online.voting.dto.responsebody.About;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;

@RestController
@RequestMapping("about")
public class AboutController {
    @GetMapping("")
    public ResponseEntity<About> about() { //todo refactor, remove hardcoded version
        return new ResponseEntity<About>(new About("1.0", new Date()), HttpStatusCode.valueOf(200));
    }
}
