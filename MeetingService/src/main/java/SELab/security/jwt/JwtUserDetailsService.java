package SELab.security.jwt;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class JwtUserDetailsService implements UserDetailsService {


    public JwtUserDetailsService(){};


    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = findByUsername(username);
        if (user == null) {
            throw new UsernameNotFoundException("User: '" + username + "' not found.");
        } else {
            return new JwtUser(user.getId(), user.getUsername(), user.getPassword(), user.getAuthorities(), user.isEnabled());
        }
    }
    public static User findByUsername(String username){

        RestTemplate restTemplate = new RestTemplate();

        ResponseEntity<String> responseEntity = restTemplate.getForEntity("http://localhost:8081/user/userinfo?username="+username,String.class);
        JSONObject tmp = JSON.parseObject(responseEntity.getBody());
        JSONObject userInfo = (JSONObject) tmp.get("responseBody");
        userInfo = (JSONObject) userInfo.get("UserInformation");

        User user = new User((String) userInfo.get("username"),userInfo.getString("fullname"),"",userInfo.getString("email"),userInfo.getString("institution"),userInfo.getString("region"));
        return user;
    }

}