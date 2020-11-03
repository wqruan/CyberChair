package SELab.controller.user;


import SELab.utility.contract.portStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@RestController
public class UserMeetingController {

    Logger logger = LoggerFactory.getLogger(UserMeetingController.class);

    @GetMapping("/user/chairMeeting")
    public void chairMeeting(HttpServletRequest request , HttpServletResponse response) throws Exception{
        logger.debug("Get chair meeting info: ");
        RequestDispatcher requestDispatcher =request.getRequestDispatcher("http://localhost:"+ portStore.MeetingService+"/user/chairMeeting");
        requestDispatcher.forward(request, response);
    }

    @GetMapping("/user/pcMemberMeeting")
    public void pcMemberMeeting(HttpServletRequest request , HttpServletResponse response) throws Exception{
        logger.debug("Get pcMemberMeeting info : ");
        RequestDispatcher requestDispatcher =request.getRequestDispatcher("http://localhost:"+ portStore.MeetingService+"/user/pcMemberMeeting");
        requestDispatcher.forward(request, response);
    }

    @GetMapping("/user/authorMeeting")
    public void authorMeeting(HttpServletRequest request , HttpServletResponse response) throws Exception{
        logger.debug("Get author meeting info : ");
        RequestDispatcher requestDispatcher =request.getRequestDispatcher("http://localhost:"+ portStore.MeetingService+"/user/authorMeeting");
        requestDispatcher.forward(request, response);
    }

    @GetMapping("/user/availableMeeting")
    public void availableMeeting(HttpServletRequest request , HttpServletResponse response) throws Exception{
        logger.debug("Get available meeting info : ");
        RequestDispatcher requestDispatcher =request.getRequestDispatcher("http://localhost:"+ portStore.MeetingService+"/user/availableMeeting");
        requestDispatcher.forward(request, response);
    }
}
