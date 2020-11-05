package SELab.service;

import SELab.domain.*;
import SELab.exception.*;
import SELab.remote.RemoteArticle;
import SELab.remote.RemoteReviewRelation;
import SELab.remote.RemoteUser;
import SELab.repository.*;
import SELab.request.admin.ApplicationRatifyRequest;
import SELab.request.meeting.*;
import SELab.request.user.InvitationRepoRequest;
import SELab.utility.contract.*;
import SELab.utility.response.ResponseGenerator;
import SELab.utility.response.ResponseWrapper;
import com.fasterxml.jackson.core.JsonProcessingException;
import javafx.util.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
public class MeetingService {

    @Autowired
    private MeetingRepository meetingRepository;
    @Autowired
    private PCMemberRelationRepository pcMemberRelationRepository;


    private static boolean flag;
    private Random random = new Random();

    @Autowired
    public MeetingService(MeetingRepository meetingRepository, PCMemberRelationRepository pcMemberRelationRepository) {
        this.meetingRepository = meetingRepository;
        this.pcMemberRelationRepository = pcMemberRelationRepository;
    }


    private List<Article> findArticleByContributorNameAndMeetingName(String contributeName,String meetingName){
        return new ArrayList<>();
    }

    public ResponseWrapper<?> meetingApplication(MeetingApplicationRequest request){
        if (RemoteUser.findByUsername(request.getChairName()) == null) {
            throw new UserNamedidntExistException(request.getChairName());
        }//chair是否是一个用户

        if (meetingRepository.findByMeetingName(request.getMeetingName()) != null) {
            throw new MeetingNameHasbeenregisteredException(request.getMeetingName());
        }//会议名称是否可用
        if(request.getTopic().isEmpty()){
            throw new AtLeastOneTopicException();
        }

        Meeting meeting = new Meeting(request.getChairName(), request.getMeetingName(), request.getAcronym(), request.getRegion(), request.getCity(), request.getVenue(), request.getTopic(), request.getOrganizer(), request.getWebPage(), request.getSubmissionDeadlineDate(), request.getNotificationOfAcceptanceDate(), request.getConferenceDate(), MeetingStatus.unprocessed);
        meetingRepository.save(meeting);

        User chair = RemoteUser.findByUsername(request.getChairName());
        PCMemberRelation pcMemberRelationForChair = new PCMemberRelation(chair.getId(),meeting.getId(), PCmemberRelationStatus.accepted,request.getTopic());
        pcMemberRelationRepository.save(pcMemberRelationForChair);
        return new ResponseWrapper<>(200, ResponseGenerator.success, null);
    }

    @Transactional
    public Boolean saveMeeting(Meeting meeting){
        meetingRepository.save(meeting);
        return true;
    }

    @Transactional
    public ResponseWrapper<?> getmeetingInfo(String meetingName){
        Meeting meeting = meetingRepository.findByMeetingName(meetingName);
        if (meeting == null) {
            throw new MeetingOfNoExistenceException(meetingName);
        }

        return ResponseGenerator.injectObjectFromObjectToResponse("meetingInfo",meeting,new String[]{"chairName","meetingName","acronym","region","city","venue","topic","organizer","webPage","submissionDeadlineDate","notificationOfAcceptanceDate","conferenceDate","status"}, null);
    }

    @Transactional
    public ResponseWrapper<?> getmeetingInfoByID(long meetingID){
        Meeting meeting = meetingRepository.findById(meetingID);
        if (meeting == null) {
            throw new MeetingOfNoExistenceException(String.valueOf(meetingID));
        }

        return ResponseGenerator.injectObjectFromObjectToResponse("meetingInfo",meeting,new String[]{"chairName","meetingName","acronym","region","city","venue","topic","organizer","webPage","submissionDeadlineDate","notificationOfAcceptanceDate","conferenceDate","status"}, null);
    }

    public ResponseWrapper<?> pcmInvitation(PCMemberInvitationRequest request){
        String meetingName = request.getMeetingName();
        Meeting meeting = meetingRepository.findByMeetingName(meetingName);

        if (meeting == null) {
            throw new MeetingOfNoExistenceException(meetingName);
        }//会议是否存在

        if(meeting.getChairName().equals(request.getPcMemberName())){
            throw new InvitationTargetIsForbiddenException(meetingName);
        }//邀请对象不能是chair本人

        String meetingStatus = meeting.getStatus();
        if(meetingStatus.equals(MeetingStatus.applyFailed)||meetingStatus.equals(MeetingStatus.unprocessed)||meetingStatus.equals(MeetingStatus.reviewing)||meetingStatus.equals(MeetingStatus.reviewCompleted)){
            throw new MeetingStatusUnavailableForPCMemberInvitationException(meetingName);
        }//会议状态是否允许进行成员邀请

        User user = RemoteUser.findByUsername(request.getPcMemberName());
        if (user == null) {
            throw new UserNamedidntExistException(request.getPcMemberName());
        }//邀请对象是否存在邀请

        PCMemberRelation pcMemberRelation = new PCMemberRelation(user.getId(),meeting.getId(), PCmemberRelationStatus.undealed,null);

        pcMemberRelationRepository.save(pcMemberRelation);
        return new ResponseWrapper<>(200, ResponseGenerator.success, null);
    }
    @Transactional
    public ResponseWrapper<?> getInvitationStatus(String meetingName) {
        Meeting meeting = meetingRepository.findByMeetingName(meetingName);
        if (meeting == null) {
            throw new MeetingOfNoExistenceException(meetingName);
        }//会议是否存在
        List<PCMemberRelation> pcMemberRelations = pcMemberRelationRepository.findByMeetingId(meeting.getId());
        Set<HashMap<String, Object>> responseSet = new HashSet<>();
        for (PCMemberRelation x: pcMemberRelations) {
            User user = RemoteUser.findById((long)x.getPcmemberId());
            HashMap<String, Object> map = ResponseGenerator.generate(user,new String[]{"username","fullname","email","institution","region"},null);
            map.put("status",x.getStatus());
            responseSet.add(map);
        }
        HashMap<String, Set<HashMap<String, Object>>> body = new HashMap<>();
        body.put("invitationStatus", responseSet);
        return new ResponseWrapper<>(200, ResponseGenerator.success, body);
    }

    @Transactional
    public ResponseWrapper<?> getSubmissionList(String authorName, String meetingName) {
        Meeting meeting = meetingRepository.findByMeetingName(meetingName);
        if (meeting == null) {
            throw new MeetingOfNoExistenceException(meetingName);
        }//会议是否存在
        List<Article> articles = findArticleByContributorNameAndMeetingName(authorName,meetingName);
        return ResponseGenerator.injectObjectFromListToResponse("articles",articles,new String[]{"id","title","topic","status"},null);
    }

    public ResponseWrapper<?> reviewPublish(ResultPublishRequest request) {
        Meeting meeting = meetingRepository.findByMeetingName(request.getMeetingName());
        if (meeting == null) {
            throw new MeetingOfNoExistenceException(request.getMeetingName());
        }//会议是否存在
        if(!meeting.getStatus().equals(MeetingStatus.reviewCompleted)){
            throw new MeetingStatusUnAvailableToReviewException();
        }
        meeting.setStatus(MeetingStatus.resultPublished);
        meetingRepository.save(meeting);
        return new ResponseWrapper<>(200, ResponseGenerator.success, null);
    }

    @Transactional
    public ResponseWrapper<?> getqueueingApplication() {
        List<Meeting> meetings = meetingRepository.findByStatus(MeetingStatus.unprocessed);
        return ResponseGenerator.injectObjectFromListToResponse("queueingApplication",meetings,new String[]{"chairName","meetingName","acronym","region","city","venue","topic","organizer","webPage","submissionDeadlineDate","notificationOfAcceptanceDate","conferenceDate"},null);
    }

    @Transactional
    public ResponseWrapper<?> getalreadyApplication() {
        List<Meeting> meetings = meetingRepository.findByStatusNot(MeetingStatus.unprocessed);
        return ResponseGenerator.injectObjectFromListToResponse("alreadyApplication",meetings,new String[]{"chairName","meetingName","acronym","region","city","venue","topic","organizer","webPage","submissionDeadlineDate","notificationOfAcceptanceDate","conferenceDate"},null);
    }

    @Transactional
    public ResponseWrapper<?> applicationRatify(ApplicationRatifyRequest request) {
        String meetingName = request.getMeetingName();
        Meeting meeting = meetingRepository.findByMeetingName(meetingName);
        if(meeting==null){
            throw new MeetingOfNoExistenceException(meetingName);
        }
        if(!meeting.getStatus().equals(MeetingStatus.unprocessed)){
            throw new MeetingUnavaliableToOperateException(meetingName);
        }
        meeting.setStatus(request.getApprovalStatus());
        meetingRepository.save(meeting);
        return new ResponseWrapper<>(200, ResponseGenerator.success, null);
    }

    @Transactional
    public ResponseWrapper<?> chairMeeting(String username){
        List<Meeting> meetingList = meetingRepository.findByChairName(username);
        HashMap<String, Set<HashMap<String, Object>>> body = new HashMap<>();
        Set<HashMap<String, Object>> response = new HashSet<>();
        for (Meeting meeting : meetingList){
            HashMap<String, Object> meetingInfo = ResponseGenerator.generate(meeting,
                    new String[]{"meetingName", "acronym", "conferenceDate", "topic"}, null);
            response.add(meetingInfo);
        }
        body.put("meetings", response);
        return new ResponseWrapper<>(200, ResponseGenerator.success, body);
    }

    @Transactional
    public ResponseWrapper<?> pcMemberMeeting(String username){
        Long userId = RemoteUser.findByUsername(username).getId();
        List<PCMemberRelation> relationList = pcMemberRelationRepository.findByPcmemberIdAndStatus(userId, PCmemberRelationStatus.accepted);
        HashMap<String, Set<HashMap<String, Object>>> body = new HashMap<>();
        Set<HashMap<String, Object>> response = new HashSet<>();
        for (PCMemberRelation relation : relationList){
            Meeting meeting = meetingRepository.findById((long)relation.getMeetingId());
            HashMap<String, Object> meetingInfo = ResponseGenerator.generate(meeting,
                    new String[]{"meetingName", "acronym", "conferenceDate", "topic"}, null);
            response.add(meetingInfo);
        }
        body.put("meetings", response);
        return new ResponseWrapper<>(200, ResponseGenerator.success, body);
    }

    @Transactional
    public ResponseWrapper<?> authorMeeting(String username) throws Exception {
        List<Article> articleList = RemoteArticle.findByContributorName(username);
        HashMap<String, Set<HashMap<String, Object>>> body = new HashMap<>();
        Set<HashMap<String, Object>> response = new HashSet<>();
        Set<Long>meetingCount = new HashSet<>();
        for(Article article : articleList){
            Meeting meeting = meetingRepository.findByMeetingName(article.getMeetingname());
            if(meeting != null && !meetingCount.contains(meeting.getId())){
                meetingCount.add(meeting.getId());
                response.add(ResponseGenerator.generate(meeting,
                        new String[]{"meetingName", "acronym", "submissionDeadlineDate", "topic"}, null));
            }
        }
        body.put("meetings", response);
        return new ResponseWrapper<>(200, ResponseGenerator.success, body);
    }

    @Transactional
    public ResponseWrapper<?> availableMeeting(String username){
        List<Meeting>allMeeting = meetingRepository.findByStatusAndChairNameNot(MeetingStatus.submissionAvaliable, username);
        HashMap<String, Set<HashMap<String, Object>>> body = new HashMap<>();
        Set<HashMap<String, Object>> response = new HashSet<>();
        for(Meeting meeting: allMeeting){
            response.add(ResponseGenerator.generate(meeting,
                    new String[]{"meetingName", "acronym", "submissionDeadlineDate", "topic"}, null));
        }
        body.put("meetings", response);
        return new ResponseWrapper<>(200, ResponseGenerator.success, body);
    }

    public ResponseWrapper<?> undealedNotifications(String username){
        Long userId = RemoteUser.findByUsername(username).getId();
        List<PCMemberRelation> relationList = pcMemberRelationRepository.findByPcmemberIdAndStatus(userId, PCmemberRelationStatus.undealed);
        HashMap<String, Set<HashMap<String, Object>>> body = new HashMap<>();
        Set<HashMap<String, Object>> response = new HashSet<>();
        for(PCMemberRelation relation : relationList){
            Meeting meeting = meetingRepository.findById((long)relation.getMeetingId());
            HashMap<String, Object> invitationInfo = ResponseGenerator.generate(meeting,
                    new String[]{"meetingName", "chairName","topic"}, null);
            response.add(invitationInfo);
        }
        body.put("invitations", response);
        return new ResponseWrapper<>(200, ResponseGenerator.success, body);
    }

    public ResponseWrapper<?> finalPublish(FinalPublishRequest request) {
        Meeting meeting = meetingRepository.findByMeetingName(request.getMeetingName());
        if (meeting.getStatus().equals(MeetingStatus.reviewFinish)) {
            meeting.setStatus(MeetingStatus.reviewPublish);
            meetingRepository.save(meeting);
            return new ResponseWrapper<>(200, ResponseGenerator.success, null);
        } else {
            return new ResponseWrapper<>(200, "failed: unable to do final publish for incorrect meeting status", null);
        }
    }

    @Transactional
    public ResponseWrapper<?> beginReview(BeginReviewRequest request) throws Exception {
        String meetingName = request.getMeetingName();
        Meeting meeting = meetingRepository.findByMeetingName(meetingName);
        if (meeting == null) {
            throw new MeetingOfNoExistenceException(meetingName);
        }

        List<Article> articles = RemoteArticle.findByMeetingNameAndStatus(meetingName, ArticleStatus.queuing);
        if(!meeting.getStatus().equals(MeetingStatus.submissionAvaliable)){
            throw new MeetingUnavaliableToOperateException(meetingName);
        }

        if (articles.isEmpty()){
            throw new NoneArticleToReviewException(meetingName);
        }//暂时无人投稿

        List<PCMemberRelation> pcMemberRelations = pcMemberRelationRepository.findByMeetingIdAndStatus(meeting.getId(), PCmemberRelationStatus.accepted);
        if(pcMemberRelations.size()<3){
            throw new AtLeastThreePCMemberException();
        }//pcmember过少

        //选择分配策略
        if(request.getAssignStrategy().equals(ArticleAssignStrategy.loadBalancing)){
            ArticleAssignInLoadBalancing(articles,pcMemberRelations,meeting.getId());
        }
        else if(request.getAssignStrategy().equals(ArticleAssignStrategy.topicRelevant)){
            ArticleAssignInTopicRelevant(articles,pcMemberRelations,meeting.getId());
        }
        //变更会议状态
        meeting.setStatus(MeetingStatus.reviewing);
        meetingRepository.save(meeting);
        return new ResponseWrapper<>(200, ResponseGenerator.success, null);
    }

    private void ArticleAssignInLoadBalancing(List<Article> articles, List<PCMemberRelation> pcMemberRelations, long meetingId){
        List<User> pcmembers = new ArrayList<>();
        for(PCMemberRelation relation : pcMemberRelations){
            pcmembers.add(RemoteUser.findById((long)relation.getPcmemberId()));
        }
        int[] ass = new int[articles.size() * 3];
        int[] count = new int[pcmembers.size()];
        flag = false;
        dfs(articles, pcmembers, ass, count, 0);
        if(flag){
            for(int i=0; i < articles.size(); i++){
                RemoteReviewRelation.save(new ReviewRelation(pcmembers.get(ass[i]).getId(), meetingId, articles.get(i).getId(),
                        ReviewStatus.unReviewed, 0, null, null));
                RemoteReviewRelation.save(new ReviewRelation(pcmembers.get(ass[i + articles.size()]).getId(), meetingId, articles.get(i).getId(),
                        ReviewStatus.unReviewed, 0, null, null));
                RemoteReviewRelation.save(new ReviewRelation(pcmembers.get(ass[i + articles.size()*2]).getId(), meetingId, articles.get(i).getId(),
                        ReviewStatus.unReviewed, 0, null, null));
            }
        }
        else throw new cannotAssignArticlesException();
    }

    private void ArticleAssignInTopicRelevant(List<Article> articles,List<PCMemberRelation> pcMemberRelations,long meetingId){

        for(Article article: articles){
            List<PCMemberRelation> pcMemberRelationSetUnConsiderTopic = pcMemberMatchFilter(article,pcMemberRelations,false);
            List<PCMemberRelation> pcMemberRelationSetConsiderTopic = pcMemberMatchFilter(article,pcMemberRelations,true);

            if(pcMemberRelationSetUnConsiderTopic.size()<3){
                throw new AtLeastThreePCMemberException();
            }
            int numOfPCMember = pcMemberRelationSetConsiderTopic.size();
            Set<PCMemberRelation> assignSet = generateAssignSet(numOfPCMember,pcMemberRelationSetUnConsiderTopic,pcMemberRelationSetConsiderTopic);

            for(PCMemberRelation x: assignSet){
                ReviewRelation reviewRelation = new ReviewRelation(x.getPcmemberId(),meetingId,article.getId(),ReviewStatus.unReviewed,0,null,null);
                RemoteReviewRelation.save(reviewRelation);
            }
        }
    }

    //筛选符合分配要求的PCMemberRelation,必须筛选的是Article的author是否包含该PCMember，通过 topicConsider布尔值决定是否使用topic有交集这一筛选条件
    private List<PCMemberRelation> pcMemberMatchFilter(Article article,List<PCMemberRelation> pcMemberRelations,boolean topicConsider){

        Set<Pair<Author,Integer>> authors = article.getAuthors();
        Set<Long> authorId = new HashSet<>();
        for(Pair<Author,Integer> pair: authors){
            Author author = pair.getKey();
            User authorUser = RemoteUser.findByFullnameAndEmail(author.getFullname(),author.getEmail());
            if(authorUser==null){
                authorId.add((long)-1);
            }
            else {
                authorId.add(authorUser.getId());
            }
        }

        return generatePcMemberRelationSet(topicConsider,authorId,article.getTopic(),pcMemberRelations);
    }

    private List<PCMemberRelation> generatePcMemberRelationSet(boolean topicConsider,Set<Long> authorId, Set<String> topic,List<PCMemberRelation> pcMemberRelations){
        List<PCMemberRelation> pcMemberRelationSet = new ArrayList<>();
        for (PCMemberRelation pcMemberRelation : pcMemberRelations) {
            if(authorId.contains(pcMemberRelation.getPcmemberId())){
                continue;
            }
            if(topicConsider) {
                Set<String> topicForPcm = pcMemberRelation.getTopic();
                for (String x : topicForPcm) {
                    if (topic.contains(x)) {
                        pcMemberRelationSet.add(pcMemberRelation);
                        break;
                    }
                }
            }
            else{
                pcMemberRelationSet.add(pcMemberRelation);
            }
        }
        return pcMemberRelationSet;
    }

    Set<PCMemberRelation> generateAssignSet(int numOfPCMember,List<PCMemberRelation> pcMemberRelationSetUnConsiderTopic,List<PCMemberRelation> pcMemberRelationSetConsiderTopic){
        Set<PCMemberRelation> assignSet = new HashSet<>();

        if(numOfPCMember<3){
            int size = pcMemberRelationSetUnConsiderTopic.size();
            while (assignSet.size()<3){
                assignSet.add(pcMemberRelationSetUnConsiderTopic.get(random.nextInt(size)));
            }
        }
        else if(numOfPCMember>3){
            int size = pcMemberRelationSetConsiderTopic.size();
            while (assignSet.size()<3){
                assignSet.add(pcMemberRelationSetConsiderTopic.get(random.nextInt(size)));
            }
        }
        else{
            for(PCMemberRelation x: pcMemberRelationSetConsiderTopic){
                assignSet.add(x);
            }
        }
        return assignSet;
    }

    void dfs(List<Article> articles, List<User> pcmembers, int[] ass, int[] count, int p){
        if(flag) return;
        int articleNum = articles.size();
        int mx = countMax(count);
        int mn = countMin(count);
        if(mx - mn > articleNum * 3 - p + 1){ return; }
        if(p == articleNum * 3){
            flag = balanceCheck(mx, mn);
            return;
        }
        Article article = articles.get(p % articleNum);
        Set<Pair<Author,Integer>> authorsAndRank = article.getAuthors();
        Set<User> authors = new HashSet<>();
        for(Pair<Author, Integer> pair : authorsAndRank){
            User author = RemoteUser.findByEmail(pair.getKey().getEmail());
            if(author == null) continue;
            authors.add(author);
        }
        for(int i=0; i<pcmembers.size(); i++){
            if(authors.contains(pcmembers.get(i))) continue;
            if(!checkSamePCMember(p, articleNum, i, ass)) continue;
            ass[p] = i;
            count[i] += 1;
            dfs(articles, pcmembers, ass, count, p+1);
            if(flag)return;
            count[i] -= 1;
            ass[p] = 0;
        }
    }

    int countMax(int[] count){
        int mx = -1;
        for(int i : count){
            mx = Math.max(mx, i);
        }
        return mx;
    }

    int countMin(int[] count){
        int mn = 1<<30;
        for(int i : count){
            mn = Math.min(mn, i);
        }
        return mn;
    }

    boolean balanceCheck(int mx, int mn){
        return (mx - mn <= 1);
    }

    boolean checkSamePCMember(int p, int articleNum, int i, int[] ass){
        if((p >= articleNum && ass[p - articleNum] == i)||(p >= articleNum *2 && ass[p - articleNum*2] == i)) return false;
        return true;
    }

    public ResponseWrapper<?> alreadyDealedNotifications(String username){
        Long userId = RemoteUser.findByUsername(username).getId();
        List<PCMemberRelation> relationList1 = pcMemberRelationRepository.findByPcmemberIdAndStatusNot(userId, PCmemberRelationStatus.undealed);
        HashMap<String, Set<HashMap<String, Object>>> body = new HashMap<>();
        Set<HashMap<String, Object>> response = new HashSet<>();
        for(PCMemberRelation relation : relationList1){
            HashMap<String, Object> invitaionInfo = ResponseGenerator.generate(relation,
                    new String[]{"status"}, null);
            invitaionInfo.put("meetingName", meetingRepository.findById((long)relation.getMeetingId()).getMeetingName());
            invitaionInfo.put("chairName", meetingRepository.findById((long)relation.getMeetingId()).getChairName());
            response.add(invitaionInfo);
        }
        body.put("alreadyDealedNotifications", response);
        return new ResponseWrapper<>(200, ResponseGenerator.success, body);
    }

    public  ResponseWrapper<?> undealedNotificationsNum(String username){
        Long userId = RemoteUser.findByUsername(username).getId();
        List<PCMemberRelation> relationList = pcMemberRelationRepository.findByPcmemberIdAndStatus(userId, PCmemberRelationStatus.undealed);
        HashMap<String, Object> body = new HashMap<>();
        body.put("undealedNotificationsNum", relationList.size());
        return new ResponseWrapper<>(200, ResponseGenerator.success, body);
    }

    public ResponseWrapper<?> invitationRepo(InvitationRepoRequest request){
        Long userId = RemoteUser.findByUsername(request.getUsername()).getId();
        Long meetingId = meetingRepository.findByMeetingName(request.getMeetingName()).getId();
        List<PCMemberRelation> relationList = pcMemberRelationRepository.findByPcmemberIdAndMeetingId(userId, meetingId);
        for(PCMemberRelation relation : relationList){
            if (relation.getStatus().equals(PCmemberRelationStatus.undealed)){
                relation.setStatus(request.getResponse());
                if(request.getResponse().equals(PCmemberRelationStatus.accepted)){
                    relation.setTopic(request.getTopics());
                }
                pcMemberRelationRepository.save(relation);
                break;
            }
        }
        return new ResponseWrapper<>(200, ResponseGenerator.success, null);
    }

    @Transactional
    public ResponseWrapper<?> beginSubmission(BeginSubmissionRequest request) {
        String meetingName = request.getMeetingName();
        Meeting meeting = meetingRepository.findByMeetingName(meetingName);
        if (meeting == null) {
            throw new MeetingOfNoExistenceException(meetingName);
        }

        String meetingStatus = meeting.getStatus();
        if(!meetingStatus.equals(MeetingStatus.applyPassed)){
            throw new MeetingUnavaliableToOperateException(meetingName);
        }
        meeting.setStatus(MeetingStatus.submissionAvaliable);
        meetingRepository.save(meeting);
        return new ResponseWrapper<>(200, ResponseGenerator.success, null);
    }

    @Transactional
    public List<PCMemberRelation> getPCMemberRelationByIdAndStatus(long userID, String undealed) {
        List<PCMemberRelation> pcMember = pcMemberRelationRepository.findByPcmemberIdAndStatus(userID, undealed);
        if(pcMember == null)
            System.out.println("getPCMemberRelationByIdAndStatus: can not find pc members!");
        return pcMember;
    }
}

