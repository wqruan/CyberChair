package MainService.Controller;


import MainService.Request.LoginRequest;
import MainService.Request.RegisterRequest;

import MainService.Service.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
public class UtilController {

    Logger logger = LoggerFactory.getLogger(UtilController.class);
    private Service service;

    @Autowired
    public UtilController(Service service) { this.service = service;}

    @GetMapping("/welcome")
    public ResponseEntity<?> welcome() {
        Map<String, String> response = new HashMap<>();
        String message = "Welcome to 2020 Software Engineering Lab2";
        response.put("message", message);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest request) {
        logger.debug("RegistrationForm: " + request.toString());

        return ResponseEntity.ok(service.register(request));
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
     //   logger.debug("LoginForm: " + request.toString());
        System.out.println("asfgf");
        logger.info(request.toString());
        return ResponseEntity.ok(service.login(request));
    }

    @GetMapping("/user/userinfo")
    public ResponseEntity<?> getUserinfo(String username) {
        logger.debug("Get user info: " + username);
        return ResponseEntity.ok(service.getUserinfo(username));
    }
    @GetMapping("/user/userinfoByID")
    public ResponseEntity<?> getUserinfoByID(int userID) {
        logger.info("Get user info: " + userID);
        return ResponseEntity.ok(service.getUserinfoByID(userID));
    }

    @GetMapping("/util/users")
    public ResponseEntity<?> searchUsersbyFullname(String fullname) {
        logger.debug("Users with fullname " + fullname + " : ");
        return ResponseEntity.ok(service.searchUsersbyFullname(fullname));
    }
}
