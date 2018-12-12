package com.pxkj.controller;

import com.pxkj.entity.Article;
import com.pxkj.service.ElasticService;
import com.pxkj.util.ApiResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/elastic")
public class ElasticTestController {

    @Autowired
    private ElasticService elasticService;

    @GetMapping(value = "/delete")
    public ApiResult<?> delete(String indices) {
        try {
            elasticService.deleteIndicesName(indices);
            return new ApiResult<>("删除成功", "");
        } catch (Exception e) {
            return new ApiResult<>(e.getMessage());
        }
    }

    @GetMapping(value = "/addArticle")
    public ApiResult<?> addArticle(Article article) {
        elasticService.createArticle(article, "pxkj_test", "article");
        return new ApiResult<>("创建成功", "");
    }

    @GetMapping(value = "/getArticle")
    public ApiResult<?> getArticle(String id) {
        String article = elasticService.getArticle("pxkj_test", "article", id);
        return new ApiResult<>("查询成功", article);
    }

    @GetMapping(value = "/getAll")
    public ApiResult<?> getAll() {
        return elasticService.searchAll();
    }

    @GetMapping(value = "/getByKey")
    public ApiResult<?> getByKey(String keyWords) {
        return elasticService.fuzzySearch(keyWords);
    }

}
