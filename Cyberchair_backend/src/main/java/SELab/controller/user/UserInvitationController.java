package SELab.controller.user;



import SELab.utility.contract.portStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@RestController
public class UserInvitationController {
    Logger logger = LoggerFactory.getLogger(UserInvitationController.class);

    @GetMapping("/user/undealedNotifications")
    public void undealedNotifications(HttpServletRequest request , HttpServletResponse response) throws Exception{
        logger.debug("Get undealedNotifications info : ");
        RequestDispatcher requestDispatcher =request.getRequestDispatcher("http://localhost:"+ portStore.MeetingService+"/user/undealedNotifications");
        requestDispatcher.forward(request, response);
    }

    @PostMapping("/user/invitationRepo")
    public void invitationRepo(HttpServletRequest request , HttpServletResponse response) throws Exception{
        logger.debug("Post invitationRepo info : "+request.toString());
        RequestDispatcher requestDispatcher =request.getRequestDispatcher("http://localhost:"+ portStore.MeetingService+"/user/invitationRepo");
        requestDispatcher.forward(request, response);
    }

    @GetMapping("/user/undealedNotificationsNum")
    public void undealedNotificationsNum(HttpServletRequest request , HttpServletResponse response) throws Exception{
        logger.debug("Get undealedNotificationsNum info : ");
        RequestDispatcher requestDispatcher =request.getRequestDispatcher("http://localhost:"+ portStore.MeetingService+"/user/undealedNotificationsNum");
        requestDispatcher.forward(request, response);
    }

    @GetMapping("/user/alreadyDealedNotifications")
    public void alreadyDealedNotifications(HttpServletRequest request , HttpServletResponse response) throws Exception{
        logger.debug("Get alreadyDealedNotifications info : ");
        RequestDispatcher requestDispatcher =request.getRequestDispatcher("http://localhost:"+ portStore.MeetingService+"/user/alreadyDealedNotifications");
        requestDispatcher.forward(request, response);
    }

}
