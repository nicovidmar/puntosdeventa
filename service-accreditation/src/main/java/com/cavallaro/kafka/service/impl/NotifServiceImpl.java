package com.cavallaro.kafka.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class NotifServiceImpl {

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    public void sendNotif(String notifMessage){
        kafkaTemplate.send("t-notif-cache-empty", notifMessage);
    }
}
