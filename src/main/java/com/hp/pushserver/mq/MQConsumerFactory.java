package com.hp.pushserver.mq;

import com.alibaba.fastjson.JSON;
import com.hp.pushserver.mqttServer.MqttPublishFactory;
import com.hp.pushserver.mqttServer.MqttRespTask;
import com.hp.pushserver.utils.DataTool;
import com.rabbitmq.client.*;
import com.rabbitmq.client.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.SerializationUtils;

import java.io.IOException;
import java.util.concurrent.*;

/**
 * 接收MQ消息并处理
 * Created by jackl on 2017/2/6.
 */
@Component
public class MQConsumerFactory {
    @Value("${com.hp.pushserver.mq.exchangeName}")
    private  String exchangeName;
    @Value("${com.hp.pushserver.mq.host}")
    private  String host;
    @Value("${com.hp.pushserver.mq.port}")
    private  int port;
    @Value("${com.hp.pushserver.mq.userName}")
    private  String userName;
    @Value("${com.hp.pushserver.mq.password}")
    private  String password;
    @Value("${com.hp.pushserver.mq.consumeRouteKeys}")
    private  String consumeRouteKeys;
    @Value("${com.hp.pushserver.mq.durable}")
    private  boolean durable;
    @Value("${com.hp.pushserver.mq.serializeMode}")
    private int serializeMode;
    @Autowired
    private DataTool dataTool;

    @Autowired
    MqttPublishFactory mqttPublishFactory;
    private static final String QUEUE_NAME = "queue";
    private Channel channel=null;
    private Logger _logger= LoggerFactory.getLogger(MQConsumerFactory.class);


    ScheduledExecutorService respScheduledService = Executors.newScheduledThreadPool(10);

    private void init()throws IOException, TimeoutException {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost(host);
        factory.setPort(port);
        factory.setUsername(userName);
        factory.setPassword(password);
        //getting a connection
        Connection connection = factory.newConnection();
        //creating a channel
        channel = connection.createChannel();
        //declaring a queue for this channel. If queue does not exist,
        //it will be created on the server.
        channel.queueDeclare(QUEUE_NAME, durable, false, false, null);
        channel.exchangeDeclare(exchangeName, "topic");
    }



    /**
     * 接收MQ消息
     */
    public void receiveMessage() {
        String[] routeKeys=consumeRouteKeys.split(",");
        StringBuilder suffixBuilder=new StringBuilder();
        for (int i = 0; i < routeKeys.length; i++) {
            routeKeys[i]= routeKeys[i]+suffixBuilder.toString();
        }
        try {
            if (channel == null) {
                init();
            }
            for (String rKey : routeKeys) {
                channel.queueBind(QUEUE_NAME, exchangeName, rKey);
            }
            _logger.info(" [*]开始接收处理带以下routeKeys的消息:"+ JSON.toJSON(routeKeys));
            Consumer consumer = new DefaultConsumer(channel) {
                @Override
                public void handleDelivery(String consumerTag, Envelope envelope,
                                           AMQP.BasicProperties properties, byte[] body) throws IOException {
                    //String message = new String(body, "UTF-8");
                    String message = new String(body, "UTF-8");
                    if(serializeMode==2) {
                        message = (String)SerializationUtils.deserialize(body);
                    }

                    handleMessage(message);
                }
            };
            channel.basicConsume(QUEUE_NAME, true, consumer);
        }catch (IOException ie){
            _logger.error("MQ异常:" + ie.getMessage());
        }catch (TimeoutException te){
            _logger.error("MQ异常:" + te.getMessage());
        }
    }

    /**
     * 将接收到的消息发送到MQTT
     * @param msg
     */
    public void handleMessage(String msg){
       // _logger.info("收到消息，开始异步处理:" + msg);
        respScheduledService.schedule(new MqttRespTask( msg, dataTool, mqttPublishFactory), 0, TimeUnit.MILLISECONDS);
    }
}
