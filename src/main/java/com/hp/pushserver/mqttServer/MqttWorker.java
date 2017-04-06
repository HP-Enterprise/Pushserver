package com.hp.pushserver.mqttServer;

import com.hp.pushserver.mq.MQConsumerFactory;
import com.hp.pushserver.mq.MQPublishFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

/**
 * Created by jackl on 2017/3/1.
 */
public class MqttWorker implements Runnable{
    private String mqttServerAddress="";
    private MqttConsumerFactory mqttConsumerFactory;
    private MqttPublishFactory mqttPublishFactory;
    private MQPublishFactory mqPublishFactory;
    private MQConsumerFactory mqConsumerFactory;

    private Logger _logger= LoggerFactory.getLogger(getClass());


    public MqttWorker(String mqttServerAddress, MqttConsumerFactory mqttConsumerFactory, MqttPublishFactory mqttPublishFactory, MQPublishFactory mqPublishFactory, MQConsumerFactory mqConsumerFactory) {
        this.mqttServerAddress = mqttServerAddress;
        this.mqttConsumerFactory = mqttConsumerFactory;
        this.mqttPublishFactory = mqttPublishFactory;
        this.mqPublishFactory = mqPublishFactory;
        this.mqConsumerFactory = mqConsumerFactory;

    }
    ScheduledExecutorService executorService = Executors.newScheduledThreadPool(10);

    @Override
    public void run() {
      _logger.info("MQTT服务器连接成功,服务器信息："+mqttServerAddress);
        MqttServer mqttServer=new MqttServer(mqttConsumerFactory,mqttPublishFactory,executorService);
        mqttServer.receive();
    }
}
