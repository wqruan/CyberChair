package SELab.service;

import SELab.domain.Meeting;
import SELab.domain.PCMemberRelation;
import SELab.repository.*;
import SELab.request.admin.ApplicationRatifyRequest;
import SELab.request.meeting.MeetingApplicationRequest;
import SELab.request.meeting.PCMemberInvitationRequest;
import SELab.request.user.InvitationRepoRequest;
import SELab.request.meeting.*;
import SELab.utility.response.ResponseGenerator;
import SELab.utility.response.ResponseWrapper;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@org.springframework.stereotype.Service
@RestController
public class Service {

    Logger logger = LoggerFactory.getLogger(Service.class);

    private MeetingRepository meetingRepository;
    private PCMemberRelationRepository pcMemberRelationRepository;

    private MeetingService meetingService;
    private static String fetched = " have been fetched";

    @Autowired
    public Service(
                   MeetingRepository meetingRepository,
                   PCMemberRelationRepository pcMemberRelationRepository,
                   MeetingService meetingService
    ) {

        this.meetingRepository = meetingRepository;
        this.pcMemberRelationRepository = pcMemberRelationRepository;
        this.meetingService = meetingService;
    }


    public Service(){}


    public ResponseWrapper<?> meetingApplication(MeetingApplicationRequest request) {
        ResponseWrapper<?> ret = meetingService.meetingApplication(request);
        if(ret.getResponseMessage().equals(ResponseGenerator.success)){
            logger.info("Meeting named "+ request.getMeetingName() +" has been added");
        }
        return  ret;
    }

    public ResponseWrapper<?> getmeetingInfo(String meetingName) {
        ResponseWrapper<?> ret = meetingService.getmeetingInfo(meetingName);
        if(ret.getResponseMessage().equals(ResponseGenerator.success)){
            logger.debug("Information of Meeting named "+ meetingName +fetched);
        }
        return  ret;
    }

    public ResponseWrapper<?> getmeetingInfoByID(long meetingID) {
        ResponseWrapper<?> ret = meetingService.getmeetingInfoByID(meetingID);
        if(ret.getResponseMessage().equals(ResponseGenerator.success)){
            logger.debug("Information of Meeting with ID "+ meetingID +fetched);
        }
        return  ret;
    }

    public ResponseWrapper<?> pcmInvitation(PCMemberInvitationRequest request) {
        ResponseWrapper<?> ret = meetingService.pcmInvitation(request);
        if(ret.getResponseMessage().equals(ResponseGenerator.success)){
            logger.info("Invitation of Meeting named "+ request.getMeetingName() +" to "+"User named " + request.getPcMemberName() + " has been added");
        }
        return  ret;
    }

    public ResponseWrapper<?> getInvitationStatus(String meetingName) {
        ResponseWrapper<?> ret = meetingService.getInvitationStatus(meetingName);
        if(ret.getResponseMessage().equals(ResponseGenerator.success)){
            logger.debug("Invitations of Meeting named "+ meetingName +fetched);
        }
        return  ret;
    }



    public ResponseWrapper<?> getqueueingApplication() {
        ResponseWrapper<?> ret = meetingService.getqueueingApplication();
        System.out.println(12346);
        if(ret.getResponseMessage().equals(ResponseGenerator.success)){
            logger.debug("Queuing applications have been fetched by admin");
        }
        return  ret;
    }

    public ResponseWrapper<?> getalreadyApplication() {
        ResponseWrapper<?> ret = meetingService.getalreadyApplication();

        System.out.println(1234);

        if(ret.getResponseMessage().equals(ResponseGenerator.success)){
            logger.debug("Dealed applications have been fetched by admin");
        }
        return  ret;
    }

    public ResponseWrapper<?> applicationRatify(ApplicationRatifyRequest request) {
        ResponseWrapper<?> ret = meetingService.applicationRatify(request);
        if(ret.getResponseMessage().equals(ResponseGenerator.success)){
            logger.info("Status of Meeting named "+ request.getMeetingName() + fetched);
        }
        return  ret;
    }

    public ResponseWrapper<?> chairMeeting(String username){
        ResponseWrapper<?> ret = meetingService.chairMeeting(username);
        if (ret.getResponseMessage().equals(ResponseGenerator.success)){
            logger.debug("Meeting list "+ username + " role as chair has been fetched.");
        }
        return  ret;
    }

    public ResponseWrapper<?> pcMemberMeeting(String username){
        ResponseWrapper<?> ret = meetingService.pcMemberMeeting(username);
        if (ret.getResponseMessage().equals(ResponseGenerator.success)){
            logger.debug("Meeting list "+ username + " role as pcMember has been fetched.");
        }
        return  ret;
    }

    public ResponseWrapper<?> authorMeeting(String username) throws Exception {
        ResponseWrapper<?> ret = meetingService.authorMeeting(username);
        if (ret.getResponseMessage().equals(ResponseGenerator.success)){
            logger.debug("Meeting list "+ username + " role as author has been fetched.");
        }
        return  ret;
    }

    public ResponseWrapper<?> availableMeeting(String username){
        ResponseWrapper<?> ret = meetingService.availableMeeting(username);
        if (ret.getResponseMessage().equals(ResponseGenerator.success)){
            logger.debug("Meeting list available to "+ username + fetched);
        }
        return  ret;
    }

    public ResponseWrapper<?> undealedNotifications(String username){
        ResponseWrapper<?> ret = meetingService.undealedNotifications(username);
        if(ret.getResponseMessage().equals(ResponseGenerator.success)){
            logger.debug("undealed messages of " + username + fetched);
        }
        return ret;
    }





    public ResponseWrapper<?> beginSubmission(BeginSubmissionRequest request) {
        ResponseWrapper<?> ret = meetingService.beginSubmission(request);
        if(ret.getResponseMessage().equals(ResponseGenerator.success)){
            logger.info("Submission begin for Meeting named " + request.getMeetingName());
        }
        return ret;
    }





    public ResponseWrapper<?> invitationRepo(InvitationRepoRequest request){
        ResponseWrapper<?> ret = meetingService.invitationRepo(request);
        if(ret.getResponseMessage().equals(ResponseGenerator.success)){
            logger.debug("Invitation Repo by "+ request.getUsername() + "to " + request.getMeetingName() + " have done.");
        }
        return ret;
    }



    public ResponseWrapper<?> undealedNotificationsNum(String username){
        ResponseWrapper<?> ret = meetingService.undealedNotificationsNum(username);
        if(ret.getResponseMessage().equals(ResponseGenerator.success)){
            logger.debug("the num of undealed messages of " + username + "has been fetched.");
        }
        return ret;
    }


    public ResponseWrapper<?> alreadyDealedNotifications(String username){
        ResponseWrapper<?> ret = meetingService.alreadyDealedNotifications(username);
        if(ret.getResponseMessage().equals(ResponseGenerator.success)){
            logger.debug("alreadyDealed messages of " + username + "has been fetched.");
        }
        return ret;
    }
    
    public ResponseWrapper<?> beginReview(BeginReviewRequest request) throws Exception {
        ResponseWrapper<?> ret = meetingService.beginReview(request);
        if(ret.getResponseMessage().equals(ResponseGenerator.success)){
            logger.info("Meeting named " + request.getMeetingName() + " review begin");
        }
        return ret;
    }

    public ResponseWrapper<?> getSubmissionList(String authorName, String meetingName) {
        ResponseWrapper<?> ret = meetingService.getSubmissionList(authorName,meetingName);
        if(ret.getResponseMessage().equals(ResponseGenerator.success)){
            logger.debug("Meeting named " + meetingName + " Articles of "+ authorName +fetched);
        }
        return ret;
    }

    public ResponseWrapper<?> reviewPublish(ResultPublishRequest request) {
        ResponseWrapper<?> ret = meetingService.reviewPublish(request);
        if(ret.getResponseMessage().equals(ResponseGenerator.success)){
            logger.info("Meeting named " + request.getMeetingName() + " have published reviews");
        }
        return ret;
    }


    public ResponseWrapper<?> finalPublish(FinalPublishRequest request) {
        ResponseWrapper<?> ret = meetingService.finalPublish(request);
        if(ret.getResponseMessage().equals(ResponseGenerator.success)){
            logger.info("Final Publish: " + request.toString());
        }
        return ret;
    }

    public void saveMeeting(Meeting meeting){
        Boolean ret = meetingService.saveMeeting(meeting);
        if(ret){
            logger.info("saved meeting");
        }
    }

    public List<PCMemberRelation> getPCMemberRelationByIdAndStatus(long userID, String undealed) {
        List<PCMemberRelation> ret = meetingService.getPCMemberRelationByIdAndStatus(userID, undealed);
        if(ret != null){
            logger.info("Get PCMemberRelation by id and status: " + userID + ", " + undealed);
        }
        return ret;
    }
}
