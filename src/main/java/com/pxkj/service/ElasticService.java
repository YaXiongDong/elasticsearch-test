package com.pxkj.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.pxkj.entity.Article;
import com.pxkj.util.Constants;
import org.elasticsearch.action.admin.indices.exists.indices.IndicesExistsResponse;
import org.elasticsearch.action.admin.indices.mapping.put.PutMappingRequest;
import org.elasticsearch.action.admin.indices.mapping.put.PutMappingResponse;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.client.Requests;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.text.Text;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.rest.RestStatus;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Component
public class ElasticService {


    @Autowired
    private TransportClient client;

    public void createMapping() throws Exception {
        // 创建索引

        boolean exit = isExit();
        if (exit) {
            deleteIndex();
        }
        createIndex();

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
        mappings.put(Constants.TYPE, article);

        String json = JSON.toJSONString(mappings);

        PutMappingRequest request = Requests.putMappingRequest(Constants.INDEX).type(Constants.TYPE).source(json, XContentType.JSON);
        PutMappingResponse response = client.admin().indices().putMapping(request).get();
        System.out.println(response.isAcknowledged());
    }

    private void createIndex() {
        Settings settings = Settings.builder()
                .put("refresh_interval", "5s")
                .put("index.number_of_shards", 3)
                .put("index.number_of_replicas", 2)
                .build();
        client.admin().indices().prepareCreate(Constants.INDEX).setSettings(settings).get();
    }

    private boolean isExit() {
        IndicesExistsResponse response = client.admin().indices().prepareExists(Constants.INDEX).get();
        return response.isExists();
    }

    private void deleteIndex() {
        client.admin().indices().prepareDelete(Constants.INDEX).get();
    }

    public void addIndex() {
        Article article = new Article();
        article.setId(127);
        article.setTitle("高颜值通缉犯");
        article.setContent("在这个看脸的时代，高颜值通缉犯走红再正常不过。不论是男版还是女版，只要被通缉，很快就会被眼尖的网友“逮着”，就会在网上引发热议，进而一炮走红。只可惜，这种走红不是正能量的，成不了可以赚钱的网红，顶多就是在各方持续的关注下，加快投案自首步伐而已。事实已经证明，最美女嫌犯卿晨璟靓如此，男版高颜值通缉犯李庆武也是如此。");
        String json = JSON.toJSONString(article);
        IndexResponse response = client.prepareIndex(Constants.INDEX, Constants.TYPE, "127").setSource(json, XContentType.JSON).get();
        RestStatus restStatus = response.status();
        int status = restStatus.getStatus();
        System.out.println(status);
    }

    public String getIndex() {
        GetResponse response = client.prepareGet(Constants.INDEX, Constants.TYPE, "125").get();
        String source = response.getSourceAsString();
        System.out.println(source);
        return source;
    }

    public List<Map<String, Object>> search() {
        List<Map<String, Object>> result = new ArrayList<>();
        HighlightBuilder highlightBuilder = new HighlightBuilder().field("content").preTags("<em>").postTags("</em>");
        SearchResponse response = client.prepareSearch(Constants.INDEX)
                .setTypes(Constants.TYPE)
                .setSearchType(SearchType.DFS_QUERY_THEN_FETCH)
                .setQuery(QueryBuilders.matchQuery("content", "李庆武"))
                .setFrom(0)
                .setSize(10)
                .setExplain(true)
                .highlighter(highlightBuilder)
                .get();
        SearchHits hits = response.getHits();
        long totalHits = hits.getTotalHits();
        for (SearchHit hit : hits) {
            Map<String, Object> map = hit.getSourceAsMap();
            Map<String, HighlightField> highlightFields = hit.getHighlightFields();
            HighlightField field = highlightFields.get("content");
            Text[] fragments = field.getFragments();
            StringBuilder content = new StringBuilder();
            for (Text text : fragments) {
                content.append(text);
            }
            float score = hit.getScore();
            map.put("_score", score);
            map.put("_highlight", content.toString());
            map.put("totalHits", totalHits);
            result.add(map);
        }
        return result;
    }

}
