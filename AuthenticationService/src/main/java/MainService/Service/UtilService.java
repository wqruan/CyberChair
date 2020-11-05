package MainService.Service;



import MainService.Request.LoginRequest;
import MainService.Request.RegisterRequest;

import MainService.domain.Meeting;
import MainService.domain.PCMemberRelation;
import MainService.domain.User;
import MainService.exception.*;
import MainService.utility.contract.PCmemberRelationStatus;
import MainService.utility.contract.portStore;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.util.Streamable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Service;
import MainService.respository.UserRepository;
import MainService.security.jwt.JwtConfigProperties;
import MainService.security.jwt.JwtTokenUtil;
import MainService.security.jwt.SampleManager;
import MainService.response.ResponseGenerator;
import MainService.response.ResponseWrapper;
import org.springframework.web.client.RestTemplate;

import java.io.File;
import java.io.FileInputStream;
import java.util.*;
import java.util.regex.Pattern;

@Service
public class UtilService {
    Logger logger = LoggerFactory.getLogger(UtilService.class);
    @Autowired
    private UserRepository userRepository;


    @Autowired
    public UtilService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    private void InformationCheck(String usrname, String password, String email) {

        String usernamePattern = "^[A-Za-z-\\-][A-Za-z_\\d\\-]{4,31}$";
        String pdPattern = "^[a-zA-Z_\\d\\-]{6,32}$";
        String emailPattern = "^([A-Za-z0-9_\\-\\.])+\\@([A-Za-z0-9_\\-\\.])+\\.([A-Za-z]{2,4})$";

        if (!Pattern.matches(usernamePattern, usrname))
            throw new IllegalUserNameException();
        if (!Pattern.matches(pdPattern, password)) {
            throw new PasswordLowSecurityAlertException();
        } else {
            int appearance = 0;
            String[] pdPatterns = new String[]{".*[a-zA-Z].*", ".*\\d.*", ".*[_\\-].*"};
            for (String pattern : pdPatterns) {
                if (Pattern.matches(pattern, password)) appearance++;
            }
            if (appearance < 2) throw new PasswordLowSecurityAlertException();
        }
      //  if (!Pattern.matches(emailPattern, email))
         //   throw new IllegalEmailAddressException();
    }

    public ResponseWrapper<?> Register(RegisterRequest request) {

        String username = request.getUsername();
        String password = request.getPassword();
        String email = request.getEmail();

        InformationCheck(username, password, email);

        if (userRepository.findByEmail(email) != null){
            throw new EmailHasBeenRegisteredException(email);//邮箱已被注册
        }

        if (userRepository.findByUsername(username) != null) {//用户名已经被注册
            throw new UsernameHasBeenRegisteredException(username);
        }

        User user = new User(username, request.getFullname(), BCrypt.hashpw(request.getPassword(), BCrypt.gensalt()), request.getEmail(), request.getInstitution(), request.getRegion());//该user构造完成
        userRepository.save(user);

        return new ResponseWrapper<>(200, ResponseGenerator.success, null);//注册成功
    }

    public ResponseWrapper<?> login(LoginRequest request){

        logger.info(request.getUsername());
        if (userRepository.findByUsername(request.getUsername()) == null) {
            throw new UserNamedidntExistException(request.getUsername());
        }
       // logger.info(userRepository.findByUsername(request.getUsername()).getUsername());
        try {
            SampleManager sampleManager = new SampleManager(userRepository);
            Authentication authentication = new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword());

            Authentication result = sampleManager.authenticate(authentication);//校验
            logger.info(result.getName());

            SecurityContextHolder.getContext().setAuthentication(result);
            System.out.println(12345566);
        } catch (AuthenticationException e) {
            //invalid username
            throw new AuthenticationFailedException("Authentication failed, invalid username or password");
        }
        //passed the authentication, return the userDetail
        HashMap<String, Object> body = ResponseGenerator.generate(userRepository.findByUsername(request.getUsername()),
                new String[]{"username"}, null);
        JwtConfigProperties jwtConfigProperties = new JwtConfigProperties();
        JwtTokenUtil jwtTokenUtil = new JwtTokenUtil(jwtConfigProperties);
        body.put("token", jwtTokenUtil.generateToken(userRepository.findByUsername(request.getUsername())));

        return new ResponseWrapper<>(200, ResponseGenerator.success, body);
    }

    public ResponseWrapper<?> getUserinfo(String username){
        User user = userRepository.findByUsername(username);
        if (user == null) {
            throw new UserNamedidntExistException(username);
        }//邀请对象是否存在
        HashMap<String, HashMap<String, Object>> body = new HashMap<>();
        HashMap<String, Object> response = ResponseGenerator.generate(user,
                new String[]{"id","username","fullname","email","institution","region"}, null);

        body.put("UserInformation",response);
        return new ResponseWrapper<>(200, ResponseGenerator.success, body);
    }
    public ResponseWrapper<?> getUserinfoByID(int userID){
        User user = userRepository.findById(userID);
        if (user == null) {
            throw new UserNamedidntExistException("id wrong");
        }//邀请对象是否存在
        HashMap<String, HashMap<String, Object>> body = new HashMap<>();
        HashMap<String, Object> response = ResponseGenerator.generate(user,
                new String[]{"id","username","fullname","email","institution","region"}, null);

        body.put("UserInformation",response);
        return new ResponseWrapper<>(200, ResponseGenerator.success, body);
    }


    public ResponseWrapper<?> getUserinfoByEmail(String email){
        User user = userRepository.findByEmail(email);
        if (user == null) {
            throw new UserNamedidntExistException("email wrong");
        }//邀请对象是否存在
        HashMap<String, HashMap<String, Object>> body = new HashMap<>();
        HashMap<String, Object> response = ResponseGenerator.generate(user,
                new String[]{"id","username","fullname","email","institution","region"}, null);

        body.put("UserInformation",response);
        return new ResponseWrapper<>(200, ResponseGenerator.success, body);
    }

    public ResponseWrapper<?> getUserinfoByFullnameAndEmail(String fullname,String email){
        User user = userRepository.findByFullnameAndEmail(fullname, email);
        if (user == null) {
            throw new UserNamedidntExistException("fullname or email wrong");
        }//邀请对象是否存在
        HashMap<String, HashMap<String, Object>> body = new HashMap<>();
        HashMap<String, Object> response = ResponseGenerator.generate(user,
                new String[]{"id","username","fullname","email","institution","region"}, null);

        body.put("UserInformation",response);
        return new ResponseWrapper<>(200, ResponseGenerator.success, body);
    }


    public ResponseWrapper<?> searchUsersbyFullname(String fullname){
        Streamable<User> users = userRepository.findByFullnameContains(fullname);
        HashMap<String, Set<HashMap<String, Object>>> body = new HashMap<>();
        Set<HashMap<String, Object>> response = new HashSet<>();
        for (User user: users) {
            HashMap<String, Object> userInfo = ResponseGenerator.generate(user,
                    new String[]{"id","username","fullname","email","institution","region"}, null);
            response.add(userInfo);
        }
        body.put("users",response);
        return new ResponseWrapper<>(200, ResponseGenerator.success, body);
    }
    public  ResponseWrapper<?> undealedNotificationsNum(String username){
        Long userId = userRepository.findByUsername(username).getId();
        List<PCMemberRelation> relationList =new ArrayList<>();
        try {
            relationList = findByPcmemberIdAndStatus(userId, PCmemberRelationStatus.undealed);
        }catch (Exception e ){
            logger.info(e.getLocalizedMessage());
        }
        HashMap<String, Object> body = new HashMap<>();
        body.put("undealedNotificationsNum", relationList.size());
        return new ResponseWrapper<>(200, ResponseGenerator.success, body);
    }
    public byte[] getPdfContent(String pdfUrl){
        File file;
        FileInputStream inputStream=null;
        byte[] bytes=null;
        try {
            file = new File(pdfUrl);
            inputStream = new FileInputStream(file);
            bytes = new byte[inputStream.available()];
            inputStream.read(bytes, 0, inputStream.available());
        }catch (Exception e){
            logger.info("pdf get error");
        }finally {
            try{
                if(inputStream!=null)
                    inputStream.close();
            }catch (Exception e){
                logger.info("FileStream close error");
            }
        }
        return bytes;
    }

    public ResponseWrapper<?> undealedNotifications(String username) throws Exception{
        Long userId = userRepository.findByUsername(username).getId();
        List<PCMemberRelation> relationList = findByPcmemberIdAndStatus(userId, PCmemberRelationStatus.undealed);
        HashMap<String, Set<HashMap<String, Object>>> body = new HashMap<>();
        Set<HashMap<String, Object>> response = new HashSet<>();
        for(PCMemberRelation relation : relationList){
            Meeting meeting = findByID((long)relation.getMeetingId());
            HashMap<String, Object> invitationInfo = ResponseGenerator.generate(meeting,
                    new String[]{"meetingName", "chairName","topic"}, null);
            response.add(invitationInfo);
        }
        body.put("invitations", response);
        return new ResponseWrapper<>(200, ResponseGenerator.success, body);
    }

    public static List<PCMemberRelation> findByPcmemberIdAndStatus(long userID, String undealed) throws JsonProcessingException {
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> responseEntity = restTemplate.getForEntity("http://localhost:"+portStore.MeetingService+"/meeting/getPCMemberRelationByIdAndStatus?userID=" + userID + "&undealed=" + undealed,String.class);

        String jsonString = responseEntity.getBody();
        ObjectMapper mapper = new ObjectMapper();
        List<PCMemberRelation> ret = mapper.readValue(jsonString, new TypeReference<List<PCMemberRelation>>(){});
        return ret;
    }
    public Meeting findByID(long id){
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> responseEntity = restTemplate.getForEntity("http://localhost:"+ portStore.MeetingService+"/meeting/meetingInfoById?meetingID="+id,String.class);

        JSONObject tmp = JSON.parseObject(responseEntity.getBody());
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
}
