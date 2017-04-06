package com.hp.pushserver.mqttServer;

import org.eclipse.paho.client.mqttv3.*;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.SerializationUtils;

import java.io.UnsupportedEncodingException;

/**
 * Created by jackl on 2017/3/4.
 */
@Component
public class MqttPublishFactory {
    @Value("${com.hp.pushserver.mqtt.acquire.serverAddress}")
    private String mqttServerAddress="";
    @Value("${com.hp.pushserver.mqtt.acquire.userName}")
    private String mqttUserName="";
    @Value("${com.hp.pushserver.mqtt.acquire.password}")
    private String mqttPassword="";
    @Value("${com.hp.pushserver.mqtt.acquire.clientId}")
    private String mqttClientId="";

    @Value("${com.hp.pushserver.mq.serializeMode}")
    private int serializeMode;


    private MqttClient client ;
    private MemoryPersistence persistence;
    private String lwtTopic= "lwt" ;
    private Logger _logger= LoggerFactory.getLogger(getClass());
    public void init() throws MqttException{
        _logger.info("初始化MQTT连接>>>>>>>>>>>>>>>.");
        client=new MqttClient("tcp://"+mqttServerAddress,mqttClientId+"_publisher");
        MqttConnectOptions conOptions = new MqttConnectOptions();
        conOptions.setUserName(mqttUserName);
        conOptions.setPassword(mqttPassword.toCharArray());
        conOptions.setCleanSession(true);
        conOptions.setMaxInflight(128);
        //conOptions.setWill(lwtTopic, "will msg".getBytes(), 1, true);

        client.connect(conOptions);
    }

    public synchronized void checkConnection(){
        try {
            if (client == null) {
                init();
            }
        }catch (MqttException e){
            e.printStackTrace();
        }
    }
    public  void publish(String publishTopic,String msg){
        try {
            checkConnection();
            MqttTopic topic = client.getTopic(publishTopic);
            _logger.info("发往"+publishTopic+"的消息:" + msg);
            //序列化模式
            byte[] bytes=msg.getBytes("UTF-8");
            //发往MQTT的消息不序列化，字符串转字节数组
           /* if(serializeMode==2) {
                bytes = SerializationUtils.serialize(msg);
            }*/
            MqttMessage message = new MqttMessage(bytes);
            message.setQos(1);
            message.setRetained(true);
            //     while(true){

            MqttDeliveryToken token = topic.publish(message);
            while (!token.isComplete()){
                token.waitForCompletion(5000);
            }
            //     }
        }catch (UnsupportedEncodingException ee){
            _logger.error(ee.getMessage());
            ee.printStackTrace();
        }catch (MqttException e) {
            _logger.error(e.getMessage());
            e.printStackTrace();
        }
    }
}
