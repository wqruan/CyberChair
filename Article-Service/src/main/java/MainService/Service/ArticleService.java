package MainService.Service;

import MainService.domain.*;
import MainService.exception.*;
import MainService.exception.user.ArticleNotFoundException;
import MainService.repository.PostRepository;
import MainService.repository.RebuttalRepository;
import MainService.request.meeting.*;
import MainService.request.user.ArticleRequest;
import MainService.util.contract.ArticleStatus;
import MainService.util.contract.MeetingStatus;
import MainService.util.contract.RebuttalStatus;
import MainService.util.contract.ReviewStatus;
import MainService.repository.ArticleRepository;
import MainService.repository.ReviewRelationRepository;
import MainService.util.response.ResponseGenerator;
import MainService.util.response.ResponseWrapper;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import javafx.util.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;


import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Timestamp;
import java.util.*;

/**
 * @author : wqruan
 * @version : 1.0.0
 * @date : 2020/10/28 19:47
 */
@Service
public class ArticleService {
    @Autowired
    private ArticleRepository articleRepository;
    @Autowired
    private ReviewRelationRepository reviewRelationRepository;
    @Autowired
    private RebuttalRepository rebuttalRepository;
    @Autowired
    private PostRepository postRepository;

    @Autowired
    public ArticleService(ArticleRepository articleRepository, ReviewRelationRepository reviewRelationRepository,RebuttalRepository rebuttalRepository,PostRepository postRepository){
        this.articleRepository=articleRepository;
        this.reviewRelationRepository=reviewRelationRepository;
        this.rebuttalRepository=rebuttalRepository;
        this.postRepository=postRepository;
    }
    @Transactional
    public ResponseWrapper<?> review(ReviewRequest request) {
        Meeting meeting =findByMeetingName(articleRepository.findById((long)Long.valueOf(request.getArticleid())).getMeetingname());
        if(!meeting.getStatus().equals(MeetingStatus.reviewing)){
            throw new MeetingStatusUnAvailableToReviewException();
        }
        ReviewRelation reviewRelation = securityCheckForReview(request.getPcMemberName(),request.getArticleid());

        reviewRelation.setScore(Integer.valueOf(request.getScore()));
        reviewRelation.setConfidence(request.getConfidence());
        reviewRelation.setReviews(request.getReviews());
        reviewRelation.setReviewStatus(ReviewStatus.alreadyReviewed);
        reviewRelationRepository.save(reviewRelation);
        ArticleStatusUpdate(Long.valueOf(request.getArticleid()));


        final ResponseWrapper responseWrapper = new ResponseWrapper<>(200, ResponseGenerator.success, null);
        return responseWrapper;
    }
    @Transactional
    public ResponseWrapper<?> getInfoOfReview(String pcMemberName, String meetingName) {
        User reviewer = findByUsername(pcMemberName);
        Meeting meeting =findByMeetingName(meetingName);

        if (reviewer == null) {
            throw new UserNamedidntExistException(pcMemberName);
        }//用户是否存在

        if (meeting == null) {
            throw new MeetingOfNoExistenceException(meetingName);
        }//会议是否存在

        HashMap<String, Set<HashMap<String, Object>>> body = new HashMap<>();
        Set<HashMap<String, Object>> responseSet = new HashSet<>();

        List<ReviewRelation> reviewRelationList = reviewRelationRepository.findByReviewerIdAndMeetingId(reviewer.getId(),meeting.getId());

        for(ReviewRelation x: reviewRelationList){
            Article article = articleRepository.findById((long)x.getArticleId());

            HashMap<String, Object> response = ResponseGenerator.generate(article,
                    new String[]{"title","topic"}, null);
            response.put("articleId",x.getArticleId());
            response.put("reviewStatus",x.getReviewStatus());

            responseSet.add(response);
        }
        body.put("reviewArticles", responseSet);
        return new ResponseWrapper<>(200, ResponseGenerator.success, body);
    }
    @Transactional
    public ResponseWrapper<?> getInfoOfArticleToReview(String pcMemberName, String articleId) {
        ReviewRelation reviewRelation = securityCheckForReview(pcMemberName,articleId);
        long id = Long.valueOf(reviewRelation.getArticleId());
        Article article = articleRepository.findById(id);

        return ResponseGenerator.injectObjectFromObjectToResponse("reviewArticle",article,new String[]{"title","articleAbstract","submitDate","filePath","topic"},null);
    }

    @Transactional
    public ResponseWrapper<?> getAlreadyReviewedInfo(String pcMemberName, String articleId) {
        return ResponseGenerator.injectObjectFromObjectToResponse("alreadyReviewedInfo",securityCheckForReview(pcMemberName,articleId),new String[]{"score","confidence","reviews"},null);
    }

    @Transactional
    public ResponseWrapper<?> getArticleDetail(String articleId) {
        Article article = articleRepository.findById(Long.parseLong(articleId));
        if (article == null) {
            throw new ArticleNotFoundException(articleId);
        }
        HashMap<String, Object> returnMap = ResponseGenerator.generate(
                article,
                new String[]{"contributorName", "meetingName", "submitDate",
                        "title", "articleAbstract", "filePath", "status"}, null
        );
        if (returnMap == null)
            throw new InternalServerError("in Article MainService.Service, in getArticleDetail");
        Set<Author> returnAuthors = new HashSet<>();
        for(Pair<Author, Integer> p: article.getAuthors()){
            returnAuthors.add(p.getKey());
        }
        returnMap.put("authors", returnAuthors);
        returnMap.put("topic", article.getTopic());

        HashMap<String, HashMap<String, Object>> body = new HashMap<>();
        body.put("articleDetail",returnMap);

        return new ResponseWrapper<>(200, ResponseGenerator.success, body);

    }

    private ReviewRelation securityCheckForReview(String pcMemberName, String articleId){
        // TODO: 2020/10/28  add authentication service call
        User reviewer = null;

        if (reviewer == null) {
            throw new UserNamedidntExistException(pcMemberName);
        }//用户是否存在

        ReviewRelation reviewRelation = reviewRelationRepository.findByReviewerIdAndArticleId(reviewer.getId(),Long.valueOf(articleId));

        if(reviewRelation==null){
            throw new RejectToReviewException(pcMemberName,articleId);
        }
        return reviewRelation;
    }
    private void ArticleStatusUpdate(long articleId){
        List<ReviewRelation> reviewRelations = reviewRelationRepository.findByArticleId(articleId);
        Article article = articleRepository.findById(articleId);
        int acceptSign = 1;
        for(ReviewRelation x : reviewRelations){
            if(x.getScore()<0){
                acceptSign = 0;
                break;
            }
        }
        article.setStatus(acceptSign>0?ArticleStatus.accepted:ArticleStatus.rejected);
        articleRepository.save(article);
    }
    @Transactional
    public ResponseWrapper<?> uploadNewArticle(ArticleRequest request, String targetRootDir) {
        String meetingName = request.getMeetingName();
        String username = request.getUsername();

        Meeting meeting =findByMeetingName(meetingName);
        User articleUploader = findByUsername(username);

        //guarantee that this operation is valid
        authenticateArticle(meeting, articleUploader);

        MultipartFile pdfFile = request.getFile();
        //save the file, if exceptions happens, throw new InternalServerError
        String internalFilePath = targetRootDir +
                articleUploader.getUsername() + File.separator +
                request.getSubmitDate() + File.separator;
        try {
            saveFileToServer(pdfFile, internalFilePath);
        } catch (IOException ex) {
            throw new InternalServerError("UserArticleService.uploadNewArticle(): error occurred when saving the article pdf");
        }


        Set<String> clearTopics = getClearedTopics(request.getTopics());

        Article newArticle = new Article(
                request.getUsername(),
                request.getMeetingName(),
                request.getSubmitDate(),
                request.getEssayTitle(),
                request.getEssayAbstract(),
                internalFilePath + pdfFile.getOriginalFilename(),
                ArticleStatus.queuing,
                clearTopics,
                request.getAuthors()
        );
        articleRepository.save(newArticle);

        return new ResponseWrapper<>(200, ResponseGenerator.success, new HashMap<>());
    }
    @Transactional
    public ResponseWrapper<?> updateExistedArticle(String articleId, ArticleRequest request, String targetRootDir) {
        Article article = articleRepository.findById(Long.parseLong(articleId));
        if (article == null)
            throw new ArticleNotFoundException(articleId);

        Meeting meeting =findByMeetingName(request.getMeetingName());
        User user = findByUsername(request.getUsername());

        authenticateArticle(meeting, user);
        if(request.getFile() != null) {

            //delete the previous pdf file
            String previousPdfPath = article.getFilePath();
            File file = new File(previousPdfPath);
            if (file.exists()) {
                if (!file.delete())
                    throw new InternalServerError("UserArticleService.updateExistedArticle(): file delete failed");
            }
            else {
                throw new InternalServerError("UserArticleService.updateExistedArticle(): previous pdf doesn't exist");
            }
            String internalFilePath = targetRootDir +
                    user.getUsername() + File.separator +
                    request.getSubmitDate() + File.separator;

            MultipartFile pdfFile = request.getFile();

            try {
                saveFileToServer(pdfFile, internalFilePath);
            } catch (IOException ex) {
                throw new InternalServerError("UserArticleService.uploadNewArticle(): error occurred when saving the article pdf");
            }
            article.setFilePath(internalFilePath + pdfFile.getOriginalFilename());
        }
        Set<String> clearTopics = getClearedTopics(request.getTopics());


        //then update all the information of the previous article
        article.setMeetingname(request.getMeetingName());
        article.setContributorName(request.getUsername());
        article.setTitle(request.getEssayTitle());
        article.setArticleAbstract(request.getEssayAbstract());
        article.setSubmitDate(request.getSubmitDate());

        article.setTopic(clearTopics);
        article.setAuthors(request.getAuthors());

        articleRepository.save(article);
        //after all the update, return the success message

        return new ResponseWrapper<>(200, ResponseGenerator.success, new HashMap<>());
    }

    @Transactional
    public ResponseWrapper<?> getAllReviews(String articleId){
        Article article = articleRepository.findById(Long.parseLong(articleId));
        if(article == null)
            throw new ArticleNotFoundException(articleId);

        Set<ReviewRelation> allReviews = reviewRelationRepository.findReviewRelationsByArticleId(article.getId());

        HashMap<String, Set<HashMap<String, Object>>> respBody = new HashMap<>();
        Set<HashMap<String, Object>> reviews = new HashSet<>();
        for(ReviewRelation relation: allReviews){
            HashMap<String, Object> items = new HashMap<>();
            items.put("score", relation.getScore());
            items.put("confidence", relation.getConfidence());
            items.put("review", relation.getReviews());

            reviews.add(items);
        }
        respBody.put("reviews", reviews);
        return new ResponseWrapper<>(200, ResponseGenerator.success, respBody);

    }
    @Transactional
    public ResponseWrapper<?> reviewPost(ReviewPostRequest request) {
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        PostMessage post = new PostMessage(
                findByUsername(request.getPosterName()).getId(),
                Long.parseLong(request.getArticleId()),
                Long.parseLong(request.getTargetId()),
                request.getContent(),
                request.getStatus(),
                timestamp.toString()
        );
        postRepository.save(post);
        return new ResponseWrapper<>(200, ResponseGenerator.success, null);
    }
    //this function is used to guarantee a article can be upload or update
    private void authenticateArticle(Meeting meeting, User user) {
        if (meeting == null)
            throw new MeetingUnavaliableToOperateException("Not created");
        if (user == null)
            throw new UserNamedidntExistException("not a valid user");

        if (!meeting.getStatus().equals(MeetingStatus.submissionAvaliable))
            throw new MeetingUnavaliableToOperateException("update or upload articles");
    }
    public ResponseWrapper<?> updateReview(UpdateReviewRequest request) {
        ReviewRelation reviewRelation = reviewRelationRepository.findByReviewerIdAndArticleId(findByUsername(request.getPcMemberName()).getId(),Long.valueOf(request.getArticleId()));
        Meeting meeting = findByID((long)reviewRelation.getMeetingId());
        String meetingStatus = meeting.getStatus();
        String updateStatus = request.getStatus();

        if(updateStatus.equals(RebuttalStatus.beforeRebuttal)){
            if (meetingStatus.equals(MeetingStatus.resultPublished) && reviewRelation.getReviewStatus().equals(ReviewStatus.alreadyReviewed)){
                reviewRelation.setScore(Integer.valueOf(request.getScore()));
                reviewRelation.setReviews(request.getReviews());
                reviewRelation.setConfidence(request.getConfidence());
                reviewRelation.setReviewStatus(ReviewStatus.firstUpdated);
                reviewRelationRepository.save(reviewRelation);
                ArticleStatusUpdate(Long.valueOf(request.getArticleId()));

                return new ResponseWrapper<>(200, ResponseGenerator.success, null);
            }
            else{
                return new ResponseWrapper<>(200, "failed : meeting or review status unavailable to do first update(Meeting should be resultPublished and Review should be alreadyReviewed)", null);
            }
        }
        else {
            if (meetingStatus.equals(MeetingStatus.rebuttalFnish) && reviewRelation.getReviewStatus().equals(ReviewStatus.reviewConfirmed)){
                reviewRelation.setScore(Integer.valueOf(request.getScore()));
                reviewRelation.setReviews(request.getReviews());
                reviewRelation.setConfidence(request.getConfidence());
                reviewRelation.setReviewStatus(ReviewStatus.secondUpdated);
                reviewRelationRepository.save(reviewRelation);
                ArticleStatusUpdate(Long.valueOf(request.getArticleId()));

                return new ResponseWrapper<>(200, ResponseGenerator.success, null);
            }
            else{
                return new ResponseWrapper<>(200, "failed : meeting or review status unavailable to do second update(Meeting should be rebuttalFnish and Review should be reviewConfirmed)", null);
            }

        }
    }
    public ResponseWrapper<?> getRebuttalInfo(String articleId) {

        List<Rebuttal> rebuttals = rebuttalRepository.findByArticleId(Long.valueOf(articleId));
        if(rebuttals.isEmpty()){
            return new ResponseWrapper<>(200, "failed : no rebuttal for this article exist", null);
        }
        else{
            Rebuttal rebuttal = rebuttals.get(0);
            return ResponseGenerator.injectObjectFromObjectToResponse("rebuttal",rebuttal,new String[]{"content"},null);
        }
    }
    public ResponseWrapper<?> rebuttal(RebuttalRequest request) {
        Article article = articleRepository.findById((long)Long.valueOf(request.getArticleId()));
        Meeting meeting = findByMeetingName(article.getMeetingname());
        if(!meeting.getStatus().equals(MeetingStatus.reviewConfirmed)){
            return new ResponseWrapper<>(200, "failed : current status unable to rebuttal for meeting status isn't being reviewConfirmed", null);
        }
        if(!article.getStatus().equals(ArticleStatus.rejected) && request.getStatus().equals(RebuttalStatus.rebuttal)){//没有被拒绝
            return new ResponseWrapper<>(200, "failed : current status unable to rebuttal for accepted article", null);
        }
        Rebuttal rebuttal = new Rebuttal(Long.valueOf(request.getArticleId()),request.getContent(),request.getStatus());
        rebuttalRepository.save(rebuttal);
        if(rebuttalRepository.findByIdNot(-1).size()==articleRepository.findByIdNot(-1).size()){

          setMeetingStatus(MeetingStatus.rebuttalFnish);
        }
        return new ResponseWrapper<>(200, ResponseGenerator.success, null);
    }
    public ResponseWrapper<?> getPostList(String articleId, String postStatus) {
        ArrayList<PostMessage> postList = (ArrayList<PostMessage>) postRepository.findByArticleIdAndStatus(Long.parseLong(articleId),postStatus);
        postList.sort(new Comparator<PostMessage>() {
            @Override
            public int compare(PostMessage o1, PostMessage o2) {
                return Timestamp.valueOf(o1.getTimeStamp()).before(Timestamp.valueOf(o2.getTimeStamp()))?1:-1;
            }
        });
        HashMap<String, ArrayList<HashMap<String, Object>>> body = new HashMap<>();
        ArrayList<HashMap<String, Object>> retList = new ArrayList<>();

        for(PostMessage x: postList){
            HashMap<String,Object> ret = new HashMap<>();

            String targetContent = "";
            PostMessage target = postRepository.findById(x.getTargetId());
            if(target!=null) {
                User targetUser = findById(target.getPosterId());
                targetContent = "Response to " + targetUser.getUsername() + ": " + target.getContent();
            }
            ret.put("postId",x.getId());
            ret.put("targetContent",targetContent);
            ret.put("postContent",x.getContent());
            ret.put("posterName",findById((long)Long.valueOf(x.getPosterId())).getUsername());
            ret.put("timeStamp",x.getTimeStamp());

            retList.add(ret);
        }
        body.put("postlist",retList);

        return new ResponseWrapper<>(200, ResponseGenerator.success, body);
    }

    //Before use this internal method
    //please Guarantee that file is savable (not null)
    private void saveFileToServer(MultipartFile file, String rootDirPath)
            throws IOException {

        byte[] fileBytes = file.getBytes();
        Path restorePath = Paths.get(rootDirPath + file.getOriginalFilename());

        //如果没有rootDirPath文件夹，则创建
        if (!Files.isWritable(restorePath)) {
            Files.createDirectories(Paths.get(rootDirPath));
        }

        Files.write(restorePath, fileBytes);
    }
    private Set<String> getClearedTopics(Set<String> topic){
        Set<String> clearTopics = new HashSet<>();

        for(String t: topic){
            t = t.replaceAll("\"", "");
            t = t.replace("[", "");
            t = t.replace("]", "");
            clearTopics.add(t);
        }

        return clearTopics;

    }
    public ResponseWrapper<?> reviewConfirm(ReviewConfirmRequest request) {
        ReviewRelation reviewRelation = reviewRelationRepository.findByReviewerIdAndArticleId(findByUsername(request.getPcMemberName()).getId(),Long.valueOf(request.getArticleId()));
        Meeting meeting = findByID((long)reviewRelation.getMeetingId());
        String meetingStatus = meeting.getStatus();
        String confirmStatus = request.getStatus();

        if(confirmStatus.equals(RebuttalStatus.beforeRebuttal)){//第一次确认
            if((reviewRelation.getReviewStatus().equals(ReviewStatus.alreadyReviewed)||reviewRelation.getReviewStatus().equals(ReviewStatus.firstUpdated)) && meetingStatus.equals(MeetingStatus.resultPublished)){
                reviewRelation.setReviewStatus(ReviewStatus.reviewConfirmed);
                reviewRelationRepository.save(reviewRelation);
                meetingStatusModifyBeforeRebuttal(meeting, ReviewStatus.reviewConfirmed, MeetingStatus.reviewConfirmed);
                return new ResponseWrapper<>(200, ResponseGenerator.success, null);
            }
            else {
                return new ResponseWrapper<>(200, "failed : current status unable to do first confirm", null);
            }
        }
        else{//第二次确认
            if((reviewRelation.getReviewStatus().equals(ReviewStatus.reviewConfirmed)||reviewRelation.getReviewStatus().equals(ReviewStatus.secondUpdated)) && meetingStatus.equals(MeetingStatus.rebuttalFnish)){
                reviewRelation.setReviewStatus(ReviewStatus.finalConfirmed);//最终确认
                reviewRelationRepository.save(reviewRelation);
                meetingStatusModifyBeforeRebuttal(meeting, ReviewStatus.finalConfirmed, MeetingStatus.reviewFinish);
                return new ResponseWrapper<>(200, ResponseGenerator.success, null);
            }
            else {
                return new ResponseWrapper<>(200, "failed : current status unable to do second confirm", null);
            }
        }
    }
    public static User findByUsername(String username){
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> responseEntity = restTemplate.getForEntity("http://localhost:8080/user/userinfo?username="+username,String.class);
        JSONObject tmp =JSON.parseObject(responseEntity.getBody());
        JSONObject userInfo = (JSONObject) tmp.get("responseBody");
        userInfo = (JSONObject) userInfo.get("UserInformation");

        User user = new User((String) userInfo.get("username"),userInfo.getString("fullname"),"",userInfo.getString("email"),userInfo.getString("institution"),userInfo.getString("region"));
        return user;
    }
    public static User findById(long id){

        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> responseEntity = restTemplate.getForEntity("http://localhost:8080/user/userinfoByID?userID="+id,String.class);
        JSONObject tmp =JSON.parseObject(responseEntity.getBody());
        JSONObject userInfo = (JSONObject) tmp.get("responseBody");
        userInfo = (JSONObject) userInfo.get("UserInformation");

        User user = new User((String) userInfo.get("username"),userInfo.getString("fullname"),"",userInfo.getString("email"),userInfo.getString("institution"),userInfo.getString("region"));
        return user;
    }
    private void meetingStatusModifyBeforeRebuttal(Meeting meeting, String reviewConfirmed, String reviewConfirmed2) {
        if (reviewRelationRepository.findByReviewStatusAndMeetingId(reviewConfirmed, meeting.getId()).size() == reviewRelationRepository.findByMeetingId(meeting.getId()).size()) {
            setMeetingStatus(reviewConfirmed2);
    }
    }
    public Meeting findByID(long id){
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> responseEntity = restTemplate.getForEntity("http://localhost:8080/meeting/meetingInfoById?meetingID="+id,String.class);

        JSONObject tmp =JSON.parseObject(responseEntity.getBody());
        JSONObject meetingInfo = (JSONObject) tmp.get("responseBody");
        meetingInfo = (JSONObject) meetingInfo.get("meetingInfo");

        Set<String> topicSet = new HashSet<>();
        JSONArray parseArray = JSON.parseArray(meetingInfo.getString("topic"));
        for(Object t: parseArray){
            topicSet.add((String)t);
        }

        Meeting meeting = new Meeting((String) meetingInfo.get("chairName"),
                meetingInfo.getString("meetingName"),
                meetingInfo.getString("acronym"),
                meetingInfo.getString("region"),
                meetingInfo.getString("city"),
                meetingInfo.getString("venue"),
                topicSet,
                meetingInfo.getString("organizer"),
                meetingInfo.getString("webPage"),
                meetingInfo.getString("submissionDeadlineDate"),
                meetingInfo.getString("notificationOfAcceptanceDate"),
                meetingInfo.getString("conferenceDate"),
                meetingInfo.getString("status")
        );
        return meeting;
    }

    public Meeting findByMeetingName(String name){
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> responseEntity = restTemplate.getForEntity("http://localhost:8080/meeting/meetingInfo?meetingName="+name,String.class);

        JSONObject tmp =JSON.parseObject(responseEntity.getBody());
        JSONObject meetingInfo = (JSONObject) tmp.get("responseBody");
        meetingInfo = (JSONObject) meetingInfo.get("meetingInfo");

        Set<String> topicSet = new HashSet<>();
        JSONArray parseArray = JSON.parseArray(meetingInfo.getString("topic"));
        for(Object t: parseArray){
            topicSet.add((String)t);
        }

        Meeting meeting = new Meeting((String) meetingInfo.get("chairName"),
                meetingInfo.getString("meetingName"),
                meetingInfo.getString("acronym"),
                meetingInfo.getString("region"),
                meetingInfo.getString("city"),
                meetingInfo.getString("venue"),
                topicSet,
                meetingInfo.getString("organizer"),
                meetingInfo.getString("webPage"),
                meetingInfo.getString("submissionDeadlineDate"),
                meetingInfo.getString("notificationOfAcceptanceDate"),
                meetingInfo.getString("conferenceDate"),
                meetingInfo.getString("status")
        );
        return meeting;
    }

    public List<Article> getArticlesByContributorName(String contributorName) {
        List<Article> articles = articleRepository.findByContributorName(contributorName);
        if(articles == null)
            throw new ArticleNotFoundException(contributorName);

        return articles;
    }

    public List<Article> getArticlesByMeetingNameAndStatus(String meetingName,String status) {
        List<Article> articles = articleRepository.findByMeetingNameAndStatus(meetingName, status);
        if(articles == null)
            throw new ArticleNotFoundException(meetingName + ", " + status);

        return articles;
    }

    //todo sent signal to meeting service to set the status of the meeting as rebuttal finish
    private void setMeetingStatus(String status){

    }
}
