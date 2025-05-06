package com.susanta.SwiftMart.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.susanta.SwiftMart.entities.User;
import com.susanta.SwiftMart.entities.UserForm;
import com.susanta.SwiftMart.helpers.Message;
import com.susanta.SwiftMart.helpers.MessageType;
import com.susanta.SwiftMart.services.UserService;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

@Controller
public class PageController {
    @Autowired
    private UserService userService;

    @GetMapping("/")
    public String index() {
        return "redirect:/home";
    }

    @RequestMapping("/home")
    public String home(Model model) {
        // model.addAttribute("name", "Susanta");
        return "home";
    }

    @RequestMapping("/about")
    public String about() {
        return "about";
    }

    @RequestMapping("/services")
    public String services() {
        return "services";
    }

    // showing login form
    @GetMapping("/login")
    public String login() {
        return "login";
    }

    // registration form view
    @GetMapping("/signup")
    public String signup(Model model) {
        UserForm userForm = new UserForm();
        model.addAttribute("userForm", userForm);
        return "signup";
    }

    @GetMapping("/contact")
    public String contact() {
        return "contact";
    }

    // processing signup form data
    @PostMapping(value = "/do_signup")
    public String processSignup(@Valid @ModelAttribute("userForm") UserForm userForm, BindingResult bindingResult,
            HttpSession session) {
        // fetch data from the form
        // System.out.println(userForm);
        // validate
        if (bindingResult.hasErrors()) {
            // System.out.println("Error in form data: " + bindingResult.getAllErrors());
            Message message = Message.builder()
                    .content("Error in form data!")
                    .type(MessageType.red)
                    .build();
            session.setAttribute("message", message);
            return "signup";
        }

        // Check if the email is already registered
        if (userService.isEmailRegistered(userForm.getEmail())) {
            Message message = Message.builder()
                    .content("This email is already registered. Please use a different email.")
                    .type(MessageType.red)
                    .build();
            session.setAttribute("message", message);
            return "signup";
        }

        User user = new User();
        user.setName(userForm.getName());
        user.setEmail(userForm.getEmail());
        user.setPassword(userForm.getPassword());
        user.setPhone(userForm.getPhone());
        user.setAddress(userForm.getAddress());
        user.setRole(userForm.getRole());

        User savedUser = userService.saveUser(user);
        System.out.println(savedUser.toString());

        Message message = Message.builder()
                .content("Sign Up successful!")
                .type(MessageType.green)
                .build();

        session.setAttribute("message", message);
        // if data is saved successfully, redirect to login page
        // else redirect to signup page with error message
        return "redirect:/login";

    }

}
