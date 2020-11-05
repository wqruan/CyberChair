package MainService.Service;


import MainService.Request.LoginRequest;
import MainService.Request.RegisterRequest;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;
import MainService.respository.UserRepository;
import MainService.response.ResponseGenerator;
import MainService.response.ResponseWrapper;

@org.springframework.stereotype.Service
@RestController
public class Service {

    Logger logger = LoggerFactory.getLogger(Service.class);
    @Autowired
    private UserRepository userRepository;

    private static String fetched = " have been fetched";
    private static String requested = " have been requested";
    private static String forArticle = "for Article ";

    @Autowired
    public Service(UserRepository userRepository

    ) {
        this.userRepository = userRepository;

    }
    @Autowired
    private UtilService utilService;
    public Service(){}

    public ResponseWrapper<?> register(RegisterRequest request) {
        ResponseWrapper<?> ret = utilService.Register(request);
        if(ret.getResponseMessage().equals(ResponseGenerator.success)){
            logger.info("Added registered user's username: "+request.getUsername());
        }
        return ret;
    }
    public ResponseWrapper<?> undealedNotifications(String username) throws Exception {
        ResponseWrapper<?> ret = utilService.undealedNotifications(username);
        if(ret.getResponseMessage().equals(ResponseGenerator.success)){
            logger.debug("undealed messages of " + username + fetched);
        }
        return ret;
    }
    public ResponseWrapper<?> undealedNotificationsNum(String username){
        ResponseWrapper<?> ret = utilService.undealedNotificationsNum(username);
        if(ret.getResponseMessage().equals(ResponseGenerator.success)){
            logger.debug("the num of undealed messages of " + username + "has been fetched.");
        }
        return ret;
    }
    public ResponseWrapper<?> login(LoginRequest request) {
        logger.info(request.getUsername());
        logger.info("asdf");
        ResponseWrapper<?> ret = utilService.login(request);
        if(ret.getResponseMessage().equals(ResponseGenerator.success)){
            logger.info("User named "+request.getUsername() +" login success");
        }
        System.out.println(ret.getResponseBody());
        return  ret;
    }

    public ResponseWrapper<?> getUserinfo(String username) {
        ResponseWrapper<?> ret = utilService.getUserinfo(username);
        if(ret.getResponseMessage().equals(ResponseGenerator.success)){
            logger.debug("Information of User named "+username +fetched);
        }
        return  ret;
    }
    public ResponseWrapper<?> getUserinfoByID(int userID){
        ResponseWrapper<?> ret = utilService.getUserinfoByID(userID);
        if(ret.getResponseMessage().equals(ResponseGenerator.success)){
            logger.debug("Information of User with ID "+ userID + fetched);
        }
        return  ret;
    }

    public ResponseWrapper<?> getUserinfoByEmail(String email){
        ResponseWrapper<?> ret = utilService.getUserinfoByEmail(email);
        if(ret.getResponseMessage().equals(ResponseGenerator.success)){
            logger.debug("Information of User with email "+ email + fetched);
        }
        return  ret;
    }
    public ResponseWrapper<?> getUserinfoByFullnameAndEmail(String fullname,String email){
        ResponseWrapper<?> ret = utilService.getUserinfoByFullnameAndEmail(fullname, email);
        if(ret.getResponseMessage().equals(ResponseGenerator.success)){
            logger.debug("Information of User with fullname and email " + fullname + ", " + email + fetched);
        }
        return  ret;
    }


    public ResponseWrapper<?> searchUsersbyFullname(String fullname) {
        ResponseWrapper<?> ret = utilService.searchUsersbyFullname(fullname);
        if(ret.getResponseMessage().equals(ResponseGenerator.success)){
            logger.debug("Information of User whose fullname is "+fullname +fetched);
        }
        return  ret;
    }


}
