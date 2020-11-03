package SELab.controller.meeting;

import SELab.request.meeting.BeginReviewRequest;
import SELab.request.meeting.BeginSubmissionRequest;
import SELab.request.meeting.ResultPublishRequest;
import SELab.request.meeting.ReviewRequest;
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
public class MeetingArticleController {
    Logger logger = LoggerFactory.getLogger(MeetingArticleController.class);


    @PostMapping("/meeting/beginSubmission")
    public void beginSubmission(HttpServletRequest request , HttpServletResponse response) throws Exception {
        logger.debug("Begin Submission: " + request.toString());
        RequestDispatcher requestDispatcher =request.getRequestDispatcher("http://localhost:"+ portStore.MeetingService+"/meeting/beginSubmission");
        requestDispatcher.forward(request, response);
    }

    @GetMapping("/meeting/reviewArticles")
    public void getInfoOfReview(HttpServletRequest request , HttpServletResponse response) throws Exception {
        logger.debug("Get review information: ");
        RequestDispatcher requestDispatcher =request.getRequestDispatcher("http://localhost:"+ portStore.Article+"/meeting/reviewArticles");
        requestDispatcher.forward(request, response);
    }

    @GetMapping("/meeting/reviewArticle")
    public void getInfoOfArticleToReview(HttpServletRequest request , HttpServletResponse response) throws Exception {
        logger.debug("Get Article information: " );
        RequestDispatcher requestDispatcher =request.getRequestDispatcher("http://localhost:"+ portStore.Article+"/meeting/reviewArticle");
        requestDispatcher.forward(request, response);
    }

    @PostMapping("/meeting/reviewer")
    public void review(HttpServletRequest request , HttpServletResponse response) throws Exception{
        logger.debug("Review: " + request.toString());
        RequestDispatcher requestDispatcher =request.getRequestDispatcher("http://localhost:"+ portStore.Article+"/meeting/reviewer");
        requestDispatcher.forward(request, response);
    }

    @GetMapping("/meeting/alreadyReviewedInfo")
    public void getAlreadyReviewedInfo(HttpServletRequest request , HttpServletResponse response) throws Exception{
        logger.debug("Get Review information: " );
        RequestDispatcher requestDispatcher =request.getRequestDispatcher("http://localhost:"+ portStore.Article+"/meeting/alreadyReviewedInfo");
        requestDispatcher.forward(request, response);
    }

    @PostMapping("/meeting/beginReview")
    public void beginReview(HttpServletRequest request , HttpServletResponse response) throws Exception{
        logger.debug("Begin Review: " + request.toString());
        RequestDispatcher requestDispatcher =request.getRequestDispatcher("http://localhost:"+ portStore.MeetingService+"/meeting/beginReview");
        requestDispatcher.forward(request, response);
    }

    @PostMapping("/meeting/publish")
    public void reviewPublish(HttpServletRequest request , HttpServletResponse response) throws Exception {
        logger.debug("Review MainService.Request to Publish: " + request.toString());
        RequestDispatcher requestDispatcher =request.getRequestDispatcher("http://localhost:"+ portStore.MeetingService+"/meeting/publish");
        requestDispatcher.forward(request, response);
    }
}
