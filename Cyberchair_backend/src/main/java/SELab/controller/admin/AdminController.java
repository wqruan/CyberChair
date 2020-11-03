package SELab.controller.admin;

import SELab.request.admin.ApplicationRatifyRequest;

import SELab.utility.contract.portStore;
import SELab.utility.response.ResponseWrapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@RestController
public class AdminController {

    Logger logger = LoggerFactory.getLogger(AdminController.class);


    @GetMapping("/admin/queueingApplication")
    public void getqueueingApplication(HttpServletRequest request , HttpServletResponse response) throws Exception{
        logger.debug("Get queuing applications by admin");
        RequestDispatcher requestDispatcher =request.getRequestDispatcher("http://localhost:"+ portStore.MeetingService+"/admin/queueingApplication");
        requestDispatcher.forward(request, response);
    }

    @GetMapping("/admin/alreadyApplication")
    public void getalreadyApplication(HttpServletRequest request , HttpServletResponse response) throws Exception {
        logger.debug("Get dealed applications by admin");
        RequestDispatcher requestDispatcher =request.getRequestDispatcher("http://localhost:"+ portStore.MeetingService+"/admin/alreadyApplication");
        requestDispatcher.forward(request, response);
    }

    @PostMapping("/admin/ratify")
    public void applicationRatify(HttpServletRequest request , HttpServletResponse response)  throws Exception{
        logger.debug("Ratification for Meeting named ");
        RequestDispatcher requestDispatcher =request.getRequestDispatcher("http://localhost:"+ portStore.MeetingService+"//admin/ratify");
        requestDispatcher.forward(request, response);
    }


}
