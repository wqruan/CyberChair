package MainService.Controller;

import MainService.Service.Service;
import MainService.domain.Author;
import MainService.request.meeting.RebuttalRequest;
import MainService.request.meeting.ReviewConfirmRequest;
import MainService.request.meeting.ReviewRequest;
import MainService.request.meeting.UpdateReviewRequest;
import MainService.request.user.ArticleRequest;
import com.alibaba.fastjson.JSONArray;
import javafx.util.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author : wqruan
 * @version : 1.0.0
 * @date : 2020/10/28 21:16
 */
public class ArticleController {

    private Service service;
    Logger logger = LoggerFactory.getLogger(ArticleController.class);

    @Autowired
    public ArticleController(Service service){
        this.service = service;
    }


    //get the detailed information about a article
    @GetMapping("/user/articleDetail")
    public ResponseEntity<?> getArticleDetail(String articleId){
        logger.debug("article detail get request received, article id = " + articleId);
        return ResponseEntity.ok(service.getArticleDetail(articleId));
    }
    //user submit a new article for a meeting
    @PostMapping("/user/articleSubmission")
    public ResponseEntity<?> submitNewArticle(
            @RequestParam("meetingName") String meetingName,
            @RequestParam("username") String username,
            @RequestParam("essayTitle") String essayTitle,
            @RequestParam("essayAbstract") String essayAbstract,
            @RequestParam("submitTime") String submitTime,
            @RequestParam("topic") Set<String> topic,
            @RequestParam("authors") String authors,
            @RequestParam("essayPDF") MultipartFile pdfFile,
            HttpServletRequest servletRequest

    ) {

        Set<Pair<Author, Integer>> authorArgument = generateAuthor(authors);

        ArticleRequest request = new ArticleRequest(
                meetingName, username, essayTitle, essayAbstract,
                submitTime, pdfFile, topic, authorArgument
        );
        String parentDir = servletRequest.getServletContext().getRealPath("src/resources/");
        return ResponseEntity.ok(service.submitNewArticle(request, parentDir));
    }

    //user update an existing paper
    @PostMapping("/user/updateArticle")
    public ResponseEntity<?> updateArticle(
            @RequestParam("articleId") String articleId,
            @RequestParam("meetingName") String meetingName,
            @RequestParam("username") String username,
            @RequestParam("essayTitle") String essayTitle,
            @RequestParam("essayAbstract") String essayAbstract,
            @RequestParam("submitTime") String submitTime,
            @RequestParam("topic") Set<String> topic,
            @RequestParam("authors") String authors,
            @RequestParam(value = "essayPDF", required = false) MultipartFile pdfFile,
            HttpServletRequest servletRequest

    ) {
        Set<Pair<Author, Integer>> authorArgument = generateAuthor(authors);

        ArticleRequest request = new ArticleRequest(
                meetingName, username, essayTitle, essayAbstract,
                submitTime, pdfFile, topic, authorArgument
        );


        String parentDir = servletRequest.getServletContext().getRealPath("src/resources/static/");
        return ResponseEntity.ok(service.updateArticle(articleId, request, parentDir));
    }


    @GetMapping("/user/reviews")
    public ResponseEntity<?> getAllReviewsOfArticle(String articleId){
        return ResponseEntity.ok(service.getReviewsOfArticle(articleId));
    }
    private Set<Pair<Author, Integer>> generateAuthor(String authors){
        List<Author> authorsList = JSONArray.parseArray(authors, Author.class);
        Set<Pair<Author, Integer>> authorArgument = new HashSet<>();
        int rank = 1;
        for (Author author: authorsList){
            authorArgument.add(new Pair<>(author, rank++));
        }
        return authorArgument;
    }
    @GetMapping("/meeting/reviewArticles")
    public ResponseEntity<?> getInfoOfReview(String pcMemberName,String meetingName) {
        logger.debug("Get review information: " + meetingName + " " + pcMemberName);
        return ResponseEntity.ok(service.getInfoOfReview(pcMemberName,meetingName));
    }

    @GetMapping("/meeting/reviewArticle")
    public ResponseEntity<?> getInfoOfArticleToReview(String pcMemberName,String articleId) {
        logger.debug("Get Article information: " + articleId + " Reviewer: " + pcMemberName);
        return ResponseEntity.ok(service.getInfoOfArticleToReview(pcMemberName,articleId));
    }
    @GetMapping("/meeting/alreadyReviewedInfo")
    public ResponseEntity<?> getAlreadyReviewedInfo(String pcMemberName,String articleId) {
        logger.debug("Get Review information: " + articleId + " Reviewer: " + pcMemberName);
        return ResponseEntity.ok(service.getAlreadyReviewedInfo(pcMemberName,articleId));
    }
    @PostMapping("/meeting/reviewer")
    public ResponseEntity<?> review(@RequestBody ReviewRequest request) {
        logger.debug("Review: " + request.toString());
        return ResponseEntity.ok(service.review(request));
    }
    @PostMapping("/meeting/reviewConfirm")
    public ResponseEntity<?> reviewConfirm(@RequestBody ReviewConfirmRequest request) {
        logger.debug("Review Confirm: " + request.toString());
        return ResponseEntity.ok(service.reviewConfirm(request));
    }

    @PostMapping("/meeting/rebuttal")
    public ResponseEntity<?> rebuttal(@RequestBody RebuttalRequest request) {
        logger.debug("Rebuttal: " + request.toString());
        return ResponseEntity.ok(service.rebuttal(request));
    }

    @GetMapping("/meeting/rebuttalInfo")
    public ResponseEntity<?> getRebuttalInfo(String articleId) {
        logger.debug("Get Rebuttal Info for article: ID " + articleId);
        return ResponseEntity.ok(service.getRebuttalInfo(articleId));
    }
    @PostMapping("/meeting/updateReview")
    public ResponseEntity<?> updateReview(@RequestBody UpdateReviewRequest request) {
        logger.debug("update Review: " + request.toString());
        return ResponseEntity.ok(service.updateReview(request));
    }
}
