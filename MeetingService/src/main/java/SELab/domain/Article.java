package SELab.domain;


import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import javafx.util.Pair;

import javax.persistence.*;
import java.io.IOException;
import java.util.Set;


public class Article {

    private Long id;
    private String contributorName;
    private String meetingName;
    private String submitDate;
    private String title;
    private String articleAbstract;
    private String filePath;
    private String status;//accepted/rejected/queuing

    private Set<String> topic;

    @JsonDeserialize(contentUsing = MyPairDeserializer.class)
    private Set<Pair<Author,Integer>> authors;

    public Article() {}

    public Article(String contributorName, String meetingName, String submitDate, String title, String articleAbstract, String filePath, String status, Set<String> topic, Set<Pair<Author, Integer>> authors) {
        this.contributorName = contributorName;
        this.meetingName = meetingName;
        this.submitDate = submitDate;
        this.title = title;
        this.articleAbstract = articleAbstract;
        this.filePath = filePath;
        this.status = status;
        this.topic = topic;
        this.authors = authors;
    }

    public Article(Article article) {
        this.contributorName = article.getContributorName();
        this.meetingName = article.getMeetingname();
        this.submitDate = article.getSubmitDate();
        this.title = article.getTitle();
        this.articleAbstract = article.getArticleAbstract();
        this.filePath = article.getFilePath();
        this.status = article.getStatus();
        this.topic = article.getTopic();
        this.authors = article.getAuthors();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getContributorName() {
        return contributorName;
    }

    public void setContributorName(String contributorName) {
        this.contributorName = contributorName;
    }

    public String getMeetingname() {
        return meetingName;
    }

    public void setMeetingname(String meetingName) {
        this.meetingName = meetingName;
    }

    public String getSubmitDate() {
        return submitDate;
    }

    public void setSubmitDate(String submitDate) {
        this.submitDate = submitDate;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getArticleAbstract() {
        return articleAbstract;
    }

    public void setArticleAbstract(String articleAbstract) {
        this.articleAbstract = articleAbstract;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Set<String> getTopic() {
        return topic;
    }

    public void setTopic(Set<String> topic) {
        this.topic = topic;
    }

    public Set<Pair<Author, Integer>> getAuthors() {
        return authors;
    }

    public void setAuthors(Set<Pair<Author, Integer>> authors) {
        this.authors = authors;
    }
}

class MyPairDeserializer extends JsonDeserializer<Pair<Author, Integer>> {

    public MyPairDeserializer() {
    }

    @Override
    public Pair<Author, Integer> deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException, JsonProcessingException {
        JsonNode node = jsonParser.getCodec().readTree(jsonParser);
        ObjectMapper mapper = new ObjectMapper();

//        System.out.println(node.get("key").toString());

        Author author = mapper.readValue(node.get("key").toString(), Author.class);
        int value = (Integer) (node.get("value")).numberValue();

        return new Pair(author, value);
    }
}