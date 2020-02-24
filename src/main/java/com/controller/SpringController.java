package com.controller;


import com.model.Skill;
import com.model.SkillsRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
public class SpringController {

    @RequestMapping("/home")
    public String hello() {
        return "redirect:hello.html";
    }

    @RequestMapping(value = "/welcome")
    public ResponseEntity<String> getWelcomeMessage() {
        String message = "Hello! Welcome on the home page! ";
        return new ResponseEntity<>(message, HttpStatus.OK);
    }

    @RequestMapping(value = "/skillsList", method = RequestMethod.GET)
    public ResponseEntity<List<Skill>> getSkillsList() {

        List<Skill> skills = SkillsRepository.getSkillsData();
        return new ResponseEntity<>(skills, HttpStatus.OK);
    }

    @RequestMapping("/ajaxCall")
    public String ajax() {
        return "redirect:ajax.html";
    }

    @RequestMapping("/fetchCall")
    public String fetch() {

        return "redirect:fetch.html";
    }

    @RequestMapping("/promiseCall")
    public String promise() {
        return "redirect:promise.html";
    }

    @RequestMapping("/arrays")
    public String getSkills() {
        return "redirect:arrayLoops.html";
    }


}

 
