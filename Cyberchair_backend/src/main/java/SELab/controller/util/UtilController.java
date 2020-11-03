package SELab.controller.util;


import SELab.utility.contract.portStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;

@RestController
public class UtilController {
    Logger logger = LoggerFactory.getLogger(UtilController.class);
    @GetMapping("/welcome")
    public ResponseEntity<?> welcome() {
        Map<String, String> response = new HashMap<>();
        String message = "Welcome to 2020 Software Engineering Lab2";
        response.put("message", message);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/register")
    public void register(HttpServletRequest request , HttpServletResponse response) throws Exception {
        logger.debug("RegistrationForm: " + request.toString());
        RequestDispatcher requestDispatcher =request.getRequestDispatcher("http://localhost:"+ portStore.UserService+"/register");
        requestDispatcher.forward(request, response);
    }

    @PostMapping("/login")
    public void login(HttpServletRequest request , HttpServletResponse response) throws Exception {
        logger.debug("LoginForm: " + request.toString());
        logger.info(request.toString());
        RequestDispatcher requestDispatcher =request.getRequestDispatcher("http://localhost:"+ portStore.UserService+"/login");
        requestDispatcher.forward(request, response);
    }

    @GetMapping("/user/userinfo")
    public void getUserinfo(HttpServletRequest request , HttpServletResponse response) throws Exception {
        logger.debug("Get user info: " );
        RequestDispatcher requestDispatcher =request.getRequestDispatcher("http://localhost:"+ portStore.UserService+"/user/userinfo");
        requestDispatcher.forward(request, response);
    }

    @GetMapping("/util/users")
    public void searchUsersbyFullname(HttpServletRequest request , HttpServletResponse response) throws Exception {
        logger.debug("Users with fullname " );
        RequestDispatcher requestDispatcher =request.getRequestDispatcher("http://localhost:"+ portStore.UserService+"/util/users");
        requestDispatcher.forward(request, response);
    }

    @GetMapping("/utils/pdf")
    @ResponseBody
    public void getImage(HttpServletRequest request , HttpServletResponse response) throws Exception  {
        logger.debug("Get file for pdfUrl " );
        RequestDispatcher requestDispatcher =request.getRequestDispatcher("http://localhost:"+ portStore.UserService+"/utils/pdf");
        requestDispatcher.forward(request, response);
    }
}
