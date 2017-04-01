package com.hp.pushserver.mq;

import com.rabbitmq.client.MessageProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import java.io.IOException;
import java.util.concurrent.TimeoutException;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import org.springframework.stereotype.Component;

/**
 * 分发消息到MQ供后续逻辑处理
 * Created by jackl on 2017/2/6.
 */
@Component
public class MQPublishFactory {
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
    private Channel channel=null;
    private Logger _logger= LoggerFactory.getLogger(MQPublishFactory.class);


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
        channel.exchangeDeclare(exchangeName, "topic");
    }

    public void sendMessage(String routeKey,String str){
        try {
            if (channel == null) {
                init();
            }
            channel.basicPublish(exchangeName, routeKey, MessageProperties.PERSISTENT_TEXT_PLAIN, str.getBytes());
        }catch (IOException ie){
            _logger.error("MQ异常:" + ie.getMessage());
        }catch (TimeoutException te){
            _logger.error("MQ异常:" + te.getMessage());
        }
        _logger.debug("发送消息到MQ:" + routeKey + "  " + str);
    }
}
