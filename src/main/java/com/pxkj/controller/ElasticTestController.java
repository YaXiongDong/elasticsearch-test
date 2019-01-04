package com.pxkj.controller;

import com.pxkj.service.ElasticService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping(value = "/elastic")
public class ElasticTestController {

    @Autowired
    private ElasticService elasticService;

    @GetMapping(value = "/createMapping")
    public String createMapping() {
        try {
            elasticService.createMapping();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "success";
    }

    @GetMapping(value = "/addIndex")
    public String addIndex() {
        elasticService.addIndex();
        return "success";
    }

    @GetMapping(value = "/getIndex")
    public String getIndex() {
        String index = elasticService.getIndex();
        return index;
    }

    @GetMapping(value = "/search")
    public List<Map<String, Object>> search(){
        List<Map<String, Object>> list = elasticService.search();
        return list;
    }

}
