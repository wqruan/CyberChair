package SELab.remote;

import SELab.domain.Article;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.List;

public class RemoteArticle {

    public static List<Article> findByContributorName(String contributorName) throws JsonProcessingException, IOException {
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> responseEntity = restTemplate.getForEntity("http://localhost:8082/meeting/getArticlesByContributorName?contributorName="+contributorName,String.class);

        String jsonString = responseEntity==null?"":responseEntity.getBody();
        ObjectMapper mapper = new ObjectMapper();
        List<Article> ret = mapper.readValue(jsonString, new TypeReference<List<Article>>(){});
        return ret;
//        return new ArrayList<>();
    }

    public static List<Article> findByMeetingNameAndStatus(String meetingName,String status) throws JsonProcessingException, IOException {
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> responseEntity = restTemplate.getForEntity("http://localhost:8082/meeting/getArticlesByMeetingNameAndStatus?meetingName="+meetingName + "&status=" + status, String.class);

        String jsonString = responseEntity==null?"":responseEntity.getBody();
        ObjectMapper mapper = new ObjectMapper();
        List<Article> ret = mapper.readValue(jsonString, new TypeReference<List<Article>>(){});
        return ret;
//        return new ArrayList<>();
    }
}
