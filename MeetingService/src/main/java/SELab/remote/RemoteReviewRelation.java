package SELab.remote;

import SELab.domain.ReviewRelation;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

public class RemoteReviewRelation {

    public static void save(ReviewRelation reviewRelation){
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        String url = "http://localhost:8082/postTest";

        HttpEntity<ReviewRelation> entity = new HttpEntity<ReviewRelation>(reviewRelation, headers);
        ResponseEntity<String> responseEntity = restTemplate.postForEntity(url, entity, String.class);
    }
}
