package com.hp.pushserver.mqttServer;

import com.hp.pushserver.mq.MQPublishFactory;
import com.hp.pushserver.utils.DataTool;
import org.eclipse.paho.client.mqttv3.*;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Created by jackl on 2017/3/4.
 */
@Component
public class MqttConsumerFactory {
    @Value("${com.hp.pushserver.mqtt.acquire.serverAddress}")
    private String mqttServerAddress="";
    @Value("${com.hp.pushserver.mqtt.acquire.userName}")
    private String mqttUserName="";
    @Value("${com.hp.pushserver.mqtt.acquire.password}")
    private String mqttPassword="";
    @Value("${com.hp.pushserver.mqtt.acquire.clientId}")
    private String mqttClientId="";
    @Value("${com.hp.pushserver.mqtt.acquire.subscribeTopic}")
    private String mqttSubscribeTopic="";
    @Value("${com.hp.pushserver.mqtt.acquire.publicTopicPrefix}")
    private String mqttPublicTopicPrefix="";


    @Autowired
    MQPublishFactory mqPublishFactory;

    @Autowired
    DataTool dataTool;

    private ScheduledExecutorService executorService;
    private Logger _logger= LoggerFactory.getLogger(getClass());

    private  MqttClient client ;
    private  MemoryPersistence persistence;

    public void setExecutorService(ScheduledExecutorService executorService) {
        this.executorService = executorService;
    }

    public void receiveMessage(){
        try {
            client=new MqttClient("tcp://"+mqttServerAddress,mqttClientId+"_receiver");
            persistence = new MemoryPersistence();
            client.setCallback(new MqttCallback() {
                @Override
                public void connectionLost(Throwable throwable) {
                }

                @Override
                public void messageArrived(String s, MqttMessage message) throws Exception {
                    _logger.info(s + "收到的消息为:" + message.toString());
                    executorService.schedule(new MqttRequestTask(message.toString(), dataTool, mqPublishFactory, null), 0, TimeUnit.MILLISECONDS);
                }

                @Override
                public void deliveryComplete(IMqttDeliveryToken iMqttDeliveryToken) {
                }
            });
            MqttConnectOptions conOptions = new MqttConnectOptions();
            conOptions.setUserName(mqttUserName);
            conOptions.setPassword(mqttPassword.toCharArray());
            conOptions.setCleanSession(true);
            //conOptions.setWill(mqttSubscribeTopic, "will msg".getBytes(), 1, true);
            conOptions.setCleanSession(false);
            client.connect(conOptions);
            client.subscribe(mqttSubscribeTopic, 1);
            boolean isSuccess =client.isConnected();
            //client.disconnect();
        } catch (MqttException e) {
            e.printStackTrace();
            _logger.error("MQTT异常:"+e.getMessage());
        }
    }
}
