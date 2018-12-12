package com.pxkj.service;

import com.alibaba.fastjson.JSON;
import com.pxkj.entity.Article;
import com.pxkj.util.ApiResult;
import org.elasticsearch.action.admin.indices.exists.indices.IndicesExistsRequestBuilder;
import org.elasticsearch.action.admin.indices.mapping.put.PutMappingRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.Requests;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.highlight.HighlightField;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Component
public class ElasticService {

    @Autowired
    private Client client;

    /**
     * 删除索引名称
     *
     * @param indices 索引名称
     */
    public void deleteIndicesName(String indices) {
        IndicesExistsRequestBuilder requestBuilder = client.admin().indices().prepareExists(indices);
        boolean exists = requestBuilder.get().isExists();
        if (exists) {
            client.admin().indices().prepareDelete(indices).get();
        }
    }

    public void createArticle(Article article, String indices, String mappingType) {
        client.prepareIndex(indices, mappingType, String.valueOf(article.getId())).setSource(JSON.toJSONString(article)).get();
    }

    public String getArticle(String indices, String mappingType, String id) {
        GetResponse response = client.prepareGet(indices, mappingType, id).get();
        return response.getSourceAsString();
    }

    public ApiResult<?> searchAll() {
        Map<String, Object> map = new HashMap<>();
        SearchResponse response = client.prepareSearch("megacorp").setTypes("employee").setQuery(QueryBuilders.matchAllQuery()).get();
        SearchHits hits = response.getHits();
        long totalHits = hits.getTotalHits();
        map.put("count", totalHits);
        Iterator<SearchHit> iterator = hits.iterator();
        List<Map<String, String>> list = new ArrayList<>();
        while (iterator.hasNext()) {
            Map<String, String> content = new HashMap<>();
            SearchHit next = iterator.next();
            String source = next.getSourceAsString();
            content.put("content", source);
            list.add(content);
            map.put("data", list);
        }
        return new ApiResult<>("获取成功", map);
    }

    public ApiResult<?> fuzzySearch(String keyWords) {
        Map<String, Object> map = new HashMap<>();
        SearchResponse searchResponse = client.prepareSearch("megacorp")
                .setTypes("employee")
                .setQuery(QueryBuilders.matchQuery("last_name", keyWords))
                .addHighlightedField("last_name")
                .setHighlighterPreTags("<em>")
                .setHighlighterPostTags("</em>")
                .setFrom(0)// 设置查询数据的位置,分页用
                .setSize(60)// 设置查询结果集的最大条数
                .setExplain(true)// 设置是否按查询匹配度排序
                .execute()
                .actionGet();// 最后就是返回搜索响应信息
        SearchHits hits = searchResponse.getHits();
        long totalHits = hits.getTotalHits();
        map.put("count", totalHits);
        List<Map<String, Object>> list = new ArrayList<>();
        SearchHit[] hitsHits = hits.getHits();
        for (SearchHit searchHit : hitsHits) {
            Map<String, HighlightField> highlightFields = searchHit.getHighlightFields();
            HighlightField highlightField = highlightFields.get("last_name");
            System.out.println("高亮字段:" + highlightField.getName() + "\n高亮部分内容:" + highlightField.getFragments()[0].string());
            Map<String, Object> sourceAsMap = searchHit.sourceAsMap();
            list.add(sourceAsMap);
        }
        map.put("list", list);
        return new ApiResult<>("获取成功", map);
    }

}
