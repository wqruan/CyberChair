package SELab.controller.meeting;

import SELab.request.meeting.MeetingApplicationRequest;
import SELab.request.meeting.PCMemberInvitationRequest;
import SELab.utility.contract.portStore;
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
public class MeetingUtilController {
    Logger logger = LoggerFactory.getLogger(MeetingUtilController.class);


    @PostMapping("/meeting/application")
    public void meetingApplication(HttpServletRequest request , HttpServletResponse response) throws Exception{
        logger.debug("Meeting application: " + request.toString());
        RequestDispatcher requestDispatcher =request.getRequestDispatcher("http://localhost:"+ portStore.MeetingService+"/meeting/application");
        requestDispatcher.forward(request, response);
    }

    @GetMapping("/meeting/meetingInfo")
    public void getmeetingInfo(HttpServletRequest request , HttpServletResponse response) throws Exception {
        logger.debug("Meeting Information: " );
        RequestDispatcher requestDispatcher =request.getRequestDispatcher("http://localhost:"+ portStore.MeetingService+"/meeting/meetingInfo");
        requestDispatcher.forward(request, response);
    }

    @PostMapping("/meeting/pcmInvitation")
    public void pcmInvitation(HttpServletRequest request , HttpServletResponse response) throws Exception {
        logger.debug("PCMember Invitation: " + request.toString());
        RequestDispatcher requestDispatcher =request.getRequestDispatcher("http://localhost:"+ portStore.MeetingService+"/meeting/pcmInvitation");
        requestDispatcher.forward(request, response);
    }

    @GetMapping("/meeting/invitationStatus")
    public void getInvitationStatus(HttpServletRequest request , HttpServletResponse response) throws Exception {
        logger.debug("Invitation Status: ");
        RequestDispatcher requestDispatcher =request.getRequestDispatcher("http://localhost:"+ portStore.MeetingService+"/meeting/invitationStatus");
        requestDispatcher.forward(request, response);
    }

    @GetMapping("/meeting/submissionList")
    public void getSubmissionList(HttpServletRequest request , HttpServletResponse response) throws Exception {
        logger.debug("Submission List");
        RequestDispatcher requestDispatcher =request.getRequestDispatcher("http://localhost:"+ portStore.MeetingService+"/meeting/submissionList");
        requestDispatcher.forward(request, response);
    }

}
