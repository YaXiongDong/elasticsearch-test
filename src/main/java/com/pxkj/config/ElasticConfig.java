package com.pxkj.config;

import org.elasticsearch.client.Client;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.net.InetAddress;
import java.net.UnknownHostException;

@Configuration
public class ElasticConfig {

    @Bean
    public Client client() {
        Client client = null;
        try {
            client = TransportClient.builder().build()
                    .addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName("127.0.0.1"), 9300));
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        return client;
    }

}
