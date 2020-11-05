package SELab.controller;


import SELab.domain.Meeting;
import SELab.domain.PCMemberRelation;
import SELab.request.admin.ApplicationRatifyRequest;
import SELab.request.meeting.*;
import SELab.request.user.InvitationRepoRequest;
import SELab.service.Service;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class MeetingController {
    Logger logger = LoggerFactory.getLogger(MeetingController.class);

    private Service service;
    @Autowired
    public MeetingController(Service service){
        this.service = service;
    }
    @PostMapping("/meeting/application")
    public ResponseEntity<?> meetingApplication(@RequestBody MeetingApplicationRequest request) {
        logger.debug("Meeting application: " + request.toString());
        return ResponseEntity.ok(service.meetingApplication(request));
    }

    @GetMapping("/meeting/meetingInfo")
    public ResponseEntity<?> getmeetingInfo(String meetingName) {
        logger.debug("Meeting Information: " + meetingName);
        return ResponseEntity.ok(service.getmeetingInfo(meetingName));
    }

    @PostMapping("/meeting/saveMeeting")
    public void saveMeeting(Meeting meeting) {
        logger.info("save meeting" + meeting.getId());
        service.saveMeeting(meeting);
    }

    @GetMapping("/meeting/meetingInfoById")
    public ResponseEntity<?> getmeetingInfoByID(long meetingID) {
        logger.info("Meeting Information By Id: " + meetingID);
        return ResponseEntity.ok(service.getmeetingInfoByID(meetingID));
    }

    @PostMapping("/meeting/pcmInvitation")
    public ResponseEntity<?> pcmInvitation(@RequestBody PCMemberInvitationRequest request) {
        logger.debug("PCMember Invitation: " + request.toString());
        return ResponseEntity.ok(service.pcmInvitation(request));
    }

    @GetMapping("/meeting/invitationStatus")
    public ResponseEntity<?> getInvitationStatus(String meetingName) {
        logger.debug("Invitation Status: " + meetingName);
        return ResponseEntity.ok(service.getInvitationStatus(meetingName));
    }

    @GetMapping("/admin/queueingApplication")
    public ResponseEntity<?> getqueueingApplication() {
        logger.debug("Get queuing applications by admin");
        return ResponseEntity.ok(service.getqueueingApplication());
    }

    @GetMapping("/admin/alreadyApplication")
    public ResponseEntity<?> getalreadyApplication() {
        logger.debug("Get dealed applications by admin");
        return ResponseEntity.ok(service.getalreadyApplication());
    }

    @PostMapping("/admin/ratify")
    public ResponseEntity<?> applicationRatify(@RequestBody ApplicationRatifyRequest request) {
        logger.debug("Ratification for Meeting named "+ request.getMeetingName());
        return ResponseEntity.ok(service.applicationRatify(request));
    }

    @GetMapping("/meeting/chairMeeting")
    public ResponseEntity<?> chairMeeting(String username){
        logger.debug("Get chair meeting info: "+username);
        return ResponseEntity.ok(service.chairMeeting(username));
    }

    @GetMapping("/meeting/pcMemberMeeting")
    public ResponseEntity<?> pcMemberMeeting(String username){
        logger.debug("Get pcMemberMeeting info : "+username);
        return  ResponseEntity.ok(service.pcMemberMeeting(username));
    }

    @GetMapping("/meeting/authorMeeting")
    public ResponseEntity<?> authorMeeting(String username) throws Exception {
        logger.debug("Get author meeting info : "+username);
        return  ResponseEntity.ok(service.authorMeeting(username));
    }

    @GetMapping("/meeting/availableMeeting")
    public ResponseEntity<?> availableMeeting(String username){
        logger.debug("Get available meeting info : "+username);
        return  ResponseEntity.ok(service.availableMeeting(username));
    }

    @GetMapping("/meeting/undealedNotifications")
    public ResponseEntity<?> undealedNotifications(String username){
        logger.debug("Get undealedNotifications info : "+username);
        return ResponseEntity.ok(service.undealedNotifications(username));
    }

    @PostMapping("/meeting/beginSubmission")
    public ResponseEntity<?> beginSubmission(@RequestBody BeginSubmissionRequest request) {
        logger.debug("Begin Submission: " + request.toString());
        return ResponseEntity.ok(service.beginSubmission(request));
    }

    @PostMapping("/meeting/invitationRepo")
    public ResponseEntity<?> invitationRepo(@RequestBody InvitationRepoRequest request){
        logger.debug("Post invitationRepo info : "+request.toString());
        return ResponseEntity.ok(service.invitationRepo(request));
    }



    @GetMapping("/meeting/alreadyDealedNotifications")
    public ResponseEntity<?> alreadyDealedNotifications(String username){
        logger.debug("Get alreadyDealedNotifications info : "+username);
        return  ResponseEntity.ok(service.alreadyDealedNotifications(username));
    }

    @PostMapping("/meeting/beginReview")
    public ResponseEntity<?> beginReview(@RequestBody BeginReviewRequest request) throws Exception {
        logger.debug("Begin Review: " + request.toString());
        return ResponseEntity.ok(service.beginReview(request));
    }

    @GetMapping("/meeting/submissionList")
    public ResponseEntity<?> getSubmissionList(String authorName,String meetingName) {
        logger.debug("Submission List");
        return ResponseEntity.ok(service.getSubmissionList(authorName,meetingName));
    }

    @PostMapping("/meeting/publish")
    public ResponseEntity<?> reviewPublish(@RequestBody ResultPublishRequest request) {
        logger.debug("Review MainService.Request to Publish: " + request.toString());
        return ResponseEntity.ok(service.reviewPublish(request));
    }

    @PostMapping("/meeting/finalPublish")
    public ResponseEntity<?> finalPublish(@RequestBody FinalPublishRequest request) {
        logger.debug("Final Publish: " + request.toString());
        return ResponseEntity.ok(service.finalPublish(request));
    }

    @GetMapping("/meeting/getPCMemberRelationByIdAndStatus")
    public List<PCMemberRelation> getPCMemberRelationByIdAndStatus(long userID, String undealed){
        logger.debug("Get PCMemberRelation by id and status: " + userID + ", " + undealed);
        return service.getPCMemberRelationByIdAndStatus(userID, undealed);
    }
}
