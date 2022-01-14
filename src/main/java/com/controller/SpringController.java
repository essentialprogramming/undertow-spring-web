package com.controller;


import com.model.Skill;
import com.repository.SkillsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Controller
public class SpringController {

    @Autowired
    private ServletContext context;

    @RequestMapping("/home")
    public String hello() {
        return "forward:hello.html";
    }

    @RequestMapping(value = "/welcome")
    public  ResponseEntity<String> getWelcomeMessage() {
        String message = "Hello! Welcome on the home page! ";
        return new ResponseEntity<>(message, HttpStatus.OK);
    }

    @RequestMapping(value = "/skillsList", method = RequestMethod.GET)
    public ResponseEntity<List<Skill>> getSkillsList() {

        List<Skill> skills = SkillsRepository.getSkillsData();
        return new ResponseEntity<>(skills, HttpStatus.OK);
    }

    @RequestMapping("/ajaxSkills")
    public String ajax() {
        return "forward:ajax.html";
    }

    @RequestMapping("/fetchSkills")
    public String fetch() {
        return "forward:fetch.html";
    }

    @RequestMapping("/promiseSkills")
    public String promise() {
        return "forward:promise.html";
    }

    @RequestMapping("/arrays")
    public String getSkills() {
        return "forward:arrayLoops.html";
    }

    @GetMapping("/jspPageWithModel")
    public String testJSP(Model model) {
        model.addAttribute("modelVar", "Hurray!");
        return "test";
    }

    @GetMapping("/jspPage")
    public String testJSP(HttpServletRequest request) {
        request.setAttribute("modelVar", "Hurray!");
        return "test";
    }

    @GetMapping("/aaa")
    public @ResponseBody ResponseEntity<String> getGreeting() {
        return new ResponseEntity<>("Hello World!", HttpStatus.OK);
    }
}

 
