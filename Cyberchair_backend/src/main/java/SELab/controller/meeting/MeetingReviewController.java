package SELab.controller.meeting;

import SELab.request.meeting.*;
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
public class MeetingReviewController {
    Logger logger = LoggerFactory.getLogger(MeetingArticleController.class);


    @PostMapping("/meeting/reviewPost")
    public void reviewPost(HttpServletRequest request , HttpServletResponse response) throws Exception {
        logger.debug("Review post: " + request.toString());
        RequestDispatcher requestDispatcher =request.getRequestDispatcher("http://localhost:"+ portStore.Article+"/meeting/reviewPost");
        requestDispatcher.forward(request, response);
    }

    @GetMapping("/meeting/postList")
    public void getPostList(HttpServletRequest request , HttpServletResponse response) throws Exception {
        logger.debug("Get postList article: ID " );
        RequestDispatcher requestDispatcher =request.getRequestDispatcher("http://localhost:"+ portStore.Article+"/meeting/postList");
        requestDispatcher.forward(request, response);
    }

    @PostMapping("/meeting/updateReview")
    public void updateReview(HttpServletRequest request , HttpServletResponse response) throws Exception {
        logger.debug("update Review: " + request.toString());
        RequestDispatcher requestDispatcher =request.getRequestDispatcher("http://localhost:"+ portStore.Article+"/meeting/updateReview");
        requestDispatcher.forward(request, response);
    }

    @PostMapping("/meeting/reviewConfirm")
    public void reviewConfirm(HttpServletRequest request , HttpServletResponse response) throws Exception {
        logger.debug("Review Confirm: " + request.toString());
        RequestDispatcher requestDispatcher =request.getRequestDispatcher("http://localhost:"+ portStore.Article+"/meeting/reviewConfirm");
        requestDispatcher.forward(request, response);
    }

    @PostMapping("/meeting/rebuttal")
    public void rebuttal(HttpServletRequest request , HttpServletResponse response) throws Exception {
        logger.debug("Rebuttal: " + request.toString());
        RequestDispatcher requestDispatcher =request.getRequestDispatcher("http://localhost:"+ portStore.Article+"/meeting/rebuttal");
        requestDispatcher.forward(request, response);
    }

    @GetMapping("/meeting/rebuttalInfo")
    public void getRebuttalInfo(HttpServletRequest request , HttpServletResponse response) throws Exception {
        logger.debug("Get Rebuttal Info for article: ID " );
        RequestDispatcher requestDispatcher =request.getRequestDispatcher("http://localhost:"+ portStore.Article+"/meeting/reviewInfo");
        requestDispatcher.forward(request, response);
    }

    @PostMapping("/meeting/finalPublish")
    public void finalPublish(HttpServletRequest request , HttpServletResponse response) throws Exception {
        logger.debug("Final Publish: " + request.toString());
        RequestDispatcher requestDispatcher =request.getRequestDispatcher("http://localhost:"+ portStore.Article+"/meeting/finalPublish");
        requestDispatcher.forward(request, response);
    }
}
