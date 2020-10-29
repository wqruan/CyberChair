package MainService.Service;

import MainService.domain.Meeting;
import MainService.domain.ReviewRelation;
import MainService.request.meeting.RebuttalRequest;
import MainService.request.meeting.ReviewConfirmRequest;
import MainService.request.meeting.ReviewRequest;
import MainService.request.meeting.UpdateReviewRequest;
import MainService.request.user.ArticleRequest;
import MainService.util.contract.MeetingStatus;
import MainService.util.contract.RebuttalStatus;
import MainService.util.contract.ReviewStatus;
import MainService.util.response.ResponseGenerator;
import MainService.util.response.ResponseWrapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;

/**
 * @author : wqruan
 * @version : 1.0.0
 * @date : 2020/10/28 20:15
 */
@org.springframework.stereotype.Service
@RestController
public class Service {

    Logger logger = LoggerFactory.getLogger(Service.class);




    private ArticleService articleService;
    private static String fetched = " have been fetched";
    private static String requested = " have been requested";
    private static String forArticle = "for Article ";

    @Autowired
    public Service(

                  ArticleService articleService
    ) {
        this.articleService=articleService;

    }


    public Service(){}

    public ResponseWrapper<?> getArticleDetail(String articleId){
        logger.debug("service for article detail called. article id = " + articleId);
        return  articleService.getArticleDetail(articleId);
    }
    public ResponseWrapper<?> submitNewArticle(ArticleRequest request, String rootDir){
        ResponseWrapper<?> ret = articleService.uploadNewArticle(request, rootDir);
        if(ret.getResponseMessage().equals(ResponseGenerator.success)){
            logger.info("user " + request.getUsername() + " submit a essay title " + request.getEssayTitle()
                    +" to meeting " + request.getMeetingName() + " at date " + new Date());
        }
        return ret;
    }

    public ResponseWrapper<?> updateArticle(String articleId, ArticleRequest request, String rootDir){
        ResponseWrapper<?> ret = articleService.updateExistedArticle(articleId, request, rootDir);
        if(ret.getResponseMessage().equals((ResponseGenerator.success))){
            logger.info("user " + request.getUsername() + " update the article with id " +
                    articleId + " at time " + new Date());
        }
        return ret;
    }

    public ResponseWrapper<?> getReviewsOfArticle(String articleId){
        ResponseWrapper<?> ret = articleService.getAllReviews(articleId);
        if(ret.getResponseMessage().equals(ResponseGenerator.success)){
            logger.debug("the reviews of article with id " + articleId + " is requested");
        }
        return ret;
    }
    public ResponseWrapper<?> getInfoOfReview(String pcMemberName, String meetingName) {
        ResponseWrapper<?> ret = articleService.getInfoOfReview(pcMemberName,meetingName);
        if(ret.getResponseMessage().equals(ResponseGenerator.success)){
            logger.debug("Review Information of " + pcMemberName + "in " + meetingName + requested);
        }
        return ret;
    }

    public ResponseWrapper<?> getInfoOfArticleToReview(String pcMemberName, String articleId) {
        ResponseWrapper<?> ret = articleService.getInfoOfArticleToReview(pcMemberName,articleId);
        if(ret.getResponseMessage().equals(ResponseGenerator.success)){
            logger.debug("Article Information of Reviewer " + pcMemberName + forArticle + articleId + requested);
        }
        return ret;
    }
    public ResponseWrapper<?> review(ReviewRequest request) {
        ResponseWrapper<?> ret =articleService.review(request);
        if(ret.getResponseMessage().equals(ResponseGenerator.success)){
            logger.info("Review of Reviewer " + request.getPcMemberName() + forArticle + request.getArticleid() + " have been submitted");
        }
        return ret;
    }
    public ResponseWrapper<?> getAlreadyReviewedInfo(String pcMemberName, String articleId) {
        ResponseWrapper<?> ret = articleService.getAlreadyReviewedInfo(pcMemberName,articleId);
        if(ret.getResponseMessage().equals(ResponseGenerator.success)){
            logger.debug("Review of Reviewer " + pcMemberName + forArticle + articleId + requested);
        }
        return ret;
    }
    public ResponseWrapper<?> updateReview(UpdateReviewRequest request) {
        ResponseWrapper<?> ret = articleService.updateReview(request);
        if(ret.getResponseMessage().equals(ResponseGenerator.success)){
            logger.info("update Review: " + request.toString());
        }
        return ret;
    }
    public ResponseWrapper<?> reviewConfirm(ReviewConfirmRequest request) {
        ResponseWrapper<?> ret = articleService.reviewConfirm(request);
        if(ret.getResponseMessage().equals(ResponseGenerator.success)){
            logger.info("Review Confirm: " + request.toString());
        }
        return ret;
    }
    public ResponseWrapper<?> getRebuttalInfo(String articleId) {
        ResponseWrapper<?> ret = articleService.getRebuttalInfo(articleId);
        if(ret.getResponseMessage().equals(ResponseGenerator.success)){
            logger.debug("Get Rebuttal Info for article: ID " + articleId);
        }
        return ret;
    }
    public ResponseWrapper<?> rebuttal(RebuttalRequest request) {
        ResponseWrapper<?> ret = articleService.rebuttal(request);
        if(ret.getResponseMessage().equals(ResponseGenerator.success)){
            logger.info("Rebuttal: " + request.toString());
        }
        return ret;
    }

}
