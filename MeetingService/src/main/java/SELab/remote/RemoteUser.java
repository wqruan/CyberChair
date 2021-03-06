package SELab.remote;

import SELab.domain.User;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

public class RemoteUser {

    public static User findByUsername(String username){
        Map<String,String> map = new HashMap();
        map.put("username",username);
        RestTemplate restTemplate = new RestTemplate();

        ResponseEntity<String> responseEntity = restTemplate.getForEntity("http://localhost:8081/user/userinfo?username="+username,String.class);
        JSONObject tmp = JSON.parseObject(responseEntity.getBody());
        JSONObject userInfo = (JSONObject) tmp.get("responseBody");
        userInfo = (JSONObject) userInfo.get("UserInformation");

        User user = new User((int)userInfo.get("id"),(String) userInfo.get("username"),userInfo.getString("fullname"),"",userInfo.getString("email"),userInfo.getString("institution"),userInfo.getString("region"));
        return user;
    }
    public static User findById(long id){
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> responseEntity = restTemplate.getForEntity("http://localhost:8081/user/userinfoByID?userID="+id,String.class);
        JSONObject tmp =JSON.parseObject(responseEntity.getBody());
        JSONObject userInfo = (JSONObject) tmp.get("responseBody");
        userInfo = (JSONObject) userInfo.get("UserInformation");
        System.out.println(userInfo);
        User user = new User((int)userInfo.get("id"),(String) userInfo.get("username"),userInfo.getString("fullname"),"",userInfo.getString("email"),userInfo.getString("institution"),userInfo.getString("region"));
        return user;
    }
    public static User findByEmail(String email){
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> responseEntity = restTemplate.getForEntity("http://localhost:8081/user/userinfoByEmail?email="+email,String.class);
        JSONObject tmp =JSON.parseObject(responseEntity.getBody());
        JSONObject userInfo = (JSONObject) tmp.get("responseBody");
        userInfo = (JSONObject) userInfo.get("UserInformation");

        User user = new User((int)userInfo.get("id"),(String) userInfo.get("username"),userInfo.getString("fullname"),"",userInfo.getString("email"),userInfo.getString("institution"),userInfo.getString("region"));
        return user;
//        return new User();
    }

    public static User findByFullnameAndEmail(String fullname,String email){
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> responseEntity = restTemplate.getForEntity("http://localhost:8081/user/userinfoByFullnameAndEmail?fullname="+fullname+"&email="+email,String.class);
        JSONObject tmp =JSON.parseObject(responseEntity.getBody());
        JSONObject userInfo = (JSONObject) tmp.get("responseBody");
        userInfo = (JSONObject) userInfo.get("UserInformation");

        User user = new User((int)userInfo.get("id"),(String) userInfo.get("username"),userInfo.getString("fullname"),"",userInfo.getString("email"),userInfo.getString("institution"),userInfo.getString("region"));
        return user;

//        return new User();
    }
}
