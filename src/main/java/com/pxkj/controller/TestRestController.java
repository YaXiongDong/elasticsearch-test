package com.pxkj.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.pxkj.entity.Article;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@RestController
public class TestRestController {

    @Autowired
    private RestTemplate restTemplate;

    @GetMapping(value = "/testAnalyze")
    public JSONObject testAnalyze() {
        String object = restTemplate.getForObject("http://192.168.163.129:9200/test_article/_analyze?analyzer={1}&text={2}",
                String.class, "ik_max_word", "测试中文分词");
        return JSON.parseObject(object);
    }

    @GetMapping(value = "/createIndex")
    public JSONObject createIndex() {
        JSONObject id = new JSONObject();
        id.put("type", "integer");
        JSONObject title = new JSONObject();
        title.put("type", "text");
        JSONObject content = new JSONObject();
        content.put("type", "text");
        content.put("analyzer", "ik_max_word");
        content.put("search_analyzer", "ik_max_word");
        JSONObject properties = new JSONObject();
        properties.put("id", id);
        properties.put("title", title);
        properties.put("content", content);
        JSONObject article = new JSONObject();
        article.put("dynamic", false);
        article.put("properties", properties);
        JSONObject mappings = new JSONObject();
        mappings.put("article", article);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<JSONObject> entity = new HttpEntity<>(mappings, headers);
        ResponseEntity<String> responseEntity = restTemplate.exchange("http://192.168.163.129:9200/test_article", HttpMethod.PUT, entity, String.class);
        String body = responseEntity.getBody();
        JSONObject obj = JSON.parseObject(body);
        return obj;
    }

    @GetMapping(value = "/addArticle")
    public JSONObject addArticle(){
        Article article = new Article();
        article.setId(124);
        article.setTitle("自动调整存储带宽");
        article.setContent("如果可以承担的器SSD盘，最好使用SSD盘。如果使用SSD，最好调整I/O调度算法。RAID0是加快速度的不错方法。统计一下");
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Article> entity = new HttpEntity<>(article, headers);
        ResponseEntity<String> responseEntity = restTemplate.exchange("http://192.168.163.129:9200/test_article/article", HttpMethod.POST, entity, String.class);
        String body = responseEntity.getBody();
        JSONObject obj = JSON.parseObject(body);
        return obj;
    }

    @GetMapping(value = "/searchAll")
    public JSONObject searchAll(){
        String object = restTemplate.postForObject("http://192.168.163.129:9200/test_article/article/_search", HttpEntity.EMPTY, String.class);
        JSONObject obj = JSON.parseObject(object);
        return obj;
    }

    @GetMapping(value = "/search")
    public JSONObject search(String key){
        JSONObject content = new JSONObject();
        content.put("query", key);
        JSONArray fields1 = new JSONArray();
        fields1.add("content");
        content.put("fields", fields1);
        JSONObject match = new JSONObject();
        match.put("multi_match", content);
        JSONObject fields = new JSONObject();
        fields.put("content", new JSONObject());
        JSONObject highLight = new JSONObject();
        highLight.put("fields", fields);
        JSONArray pre_tags = new JSONArray();
        pre_tags.add("<font color=\"red\">");
        highLight.put("pre_tags", pre_tags);
        JSONArray post_tags = new JSONArray();
        post_tags.add("</font>");
        highLight.put("post_tags", post_tags);
        JSONObject query = new JSONObject();
        query.put("query", match);
        query.put("highlight", highLight);
        System.out.println(JSON.toJSONString(query));
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<JSONObject> entity = new HttpEntity<>(query, headers);
        ResponseEntity<String> responseEntity = restTemplate.exchange("http://192.168.163.129:9200/test_article/article/_search", HttpMethod.POST, entity, String.class);
        String body = responseEntity.getBody();
        return JSON.parseObject(body);
    }

}
