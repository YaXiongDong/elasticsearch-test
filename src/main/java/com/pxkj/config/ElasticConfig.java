package com.pxkj.config;

import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.TransportAddress;
import org.elasticsearch.transport.client.PreBuiltTransportClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.net.InetAddress;


@Configuration
public class ElasticConfig {

    @Bean
    public TransportClient client() throws Exception {
        Settings settings = Settings.builder().put("cluster.name", "elasticsearch").build();
        TransportAddress transportAddress = new TransportAddress(InetAddress.getByName("192.168.163.129"), 9300);
        TransportClient client = new PreBuiltTransportClient(settings).addTransportAddress(transportAddress);
        return client;
    }

}
