package com.hp.pushserver.mqttServer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ScheduledExecutorService;

/**
 * Created by jackl on 2017/3/4.
 */
public class MqttServer {
    private MqttConsumerFactory mqttConsumerFactory;
    private MqttPublishFactory mqttPublishFactory;
    private ScheduledExecutorService executorService;
    private Logger _logger= LoggerFactory.getLogger(getClass());
    public MqttServer(MqttConsumerFactory mqttConsumerFactory, MqttPublishFactory mqttPublishFactory,ScheduledExecutorService executorService) {
        this.mqttConsumerFactory = mqttConsumerFactory;
        this.mqttPublishFactory = mqttPublishFactory;
        this.executorService=executorService;
    }
    public void receive(){
        mqttConsumerFactory.setExecutorService(executorService);
       // mqttConsumerFactory.receiveMessage(); 不需要通过MQTT接收消息
    }
}
