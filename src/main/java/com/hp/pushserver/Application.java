package com.hp.pushserver;
import com.hp.pushserver.mq.MQConsumerFactory;
import com.hp.pushserver.mq.MQPublishFactory;
import com.hp.pushserver.mqttServer.MqttConsumerFactory;
import com.hp.pushserver.mqttServer.MqttPublishFactory;
import com.hp.pushserver.mqttServer.MqttWorker;
import com.hp.pushserver.utils.DataTool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Created by jackl on 2017/2/6.
 */
@SpringBootApplication
public class Application implements CommandLineRunner{
    private Logger _logger=LoggerFactory.getLogger(Application.class);
    @Autowired
    DataTool dataTool;

    @Autowired
    MQPublishFactory mqPublishFactory;
    @Autowired
    MQConsumerFactory mqConsumerFactory;

    @Autowired
    MqttConsumerFactory mqttConsumerFactory;
    @Autowired
    MqttPublishFactory mqttPublishFactory;


    @Value("${com.hp.pushserver.mqtt.acquire.serverAddress}")
    private String _mqttServerAddress="";
    @Value("${com.hp.pushserver.mqtt.acquire.userName}")
    private String _mqttUserName="";
    @Value("${com.hp.pushserver.mqtt.acquire.password}")
    private String _mqttPassword="";
    @Value("${com.hp.pushserver.mqtt.acquire.clientId}")
    private String _mqttClientId="";
    @Value("${com.hp.pushserver.mqtt.acquire.subscribeTopic}")
    private String _mqttSubscribeTopic="";
    @Value("${com.hp.pushserver.mqtt.acquire.publicTopicPrefix}")
    private String _mqttPublicTopicPrefix="";


    public static void main(String[] args) {

        SpringApplication.run(Application.class, args);

    }

    @Override
    public void run(String... args) throws Exception {
        // 启动数据接受程序
        _logger.info("Pushserver正在启动...");

        MqttWorker mqttWorkerThread = new MqttWorker(_mqttServerAddress, mqttConsumerFactory, mqttPublishFactory, mqPublishFactory, mqConsumerFactory);
        Thread mqttWorker = new Thread(mqttWorkerThread);
        mqttWorker.setName("mqtt-worker");
        mqttWorker.start();
        mqConsumerFactory.receiveMessage();//接收MQ消息
        mqttPublishFactory.init();

    }




}
