package com.manthan.userservice.clients;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class KafkaProducerClient{
    private KafkaTemplate<String,String> kafkaTemplate;

    public KafkaProducerClient(KafkaTemplate<String, String> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    // message is stringified json
    public void sendMessage(String topic, String message){
        kafkaTemplate.send(topic,message);
    }
}