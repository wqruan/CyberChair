package SELab.controller.user;

import SELab.controller.meeting.MeetingUtilController;
import SELab.utility.contract.portStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;
import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


@RestController
public class UserArticleController {

    Logger logger = LoggerFactory.getLogger(UserArticleController.class);

    //get the detailed information about a article
    @GetMapping("/user/articleDetail")
    public void getArticleDetail(HttpServletRequest request , HttpServletResponse response) throws Exception{
        logger.debug("article detail get request received, article id = " );
        RequestDispatcher requestDispatcher =request.getRequestDispatcher("http://localhost:"+ portStore.Article+"/user/articleDetail");
        requestDispatcher.forward(request, response);
    }

    //user submit a new article for a meeting
    @PostMapping("/user/articleSubmission")
    public void submitNewArticle(HttpServletRequest request , HttpServletResponse response) throws Exception {
        RequestDispatcher requestDispatcher =request.getRequestDispatcher("http://localhost:"+ portStore.Article+"/user/articleSubmission");
        requestDispatcher.forward(request, response);
    }

    //user update an existing paper
    @PostMapping("/user/updateArticle")
    public void updateArticle(HttpServletRequest request , HttpServletResponse response) throws Exception{
        RequestDispatcher requestDispatcher =request.getRequestDispatcher("http://localhost:"+ portStore.Article+"/user/updateArticle");
        requestDispatcher.forward(request, response);
    }


    @GetMapping("/user/reviews")
    public void getAllReviewsOfArticle(HttpServletRequest request , HttpServletResponse response) throws Exception{
        RequestDispatcher requestDispatcher =request.getRequestDispatcher("http://localhost:"+ portStore.Article+"/user/reviews");
        requestDispatcher.forward(request, response);
    }






    //post a new article
//    @PostMapping("/user/articleSubmission")

}
