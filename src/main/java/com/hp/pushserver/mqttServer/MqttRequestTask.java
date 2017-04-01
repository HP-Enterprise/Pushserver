package com.hp.pushserver.mqttServer;

import com.hp.pushserver.mq.MQPublishFactory;
import com.hp.pushserver.utils.DataTool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by jackl on 2017/3/4.
 */
public class MqttRequestTask  implements  Runnable{
    private String receiveData;
    private DataTool dataTool;
    private MQPublishFactory mqPublishFactory;
    public String publishDataRouteKey;
    private Logger _logger= LoggerFactory.getLogger(getClass());

    public MqttRequestTask(String receiveData,  DataTool dataTool,  MQPublishFactory mqPublishFactory, String publishDataRouteKey) {
        this.receiveData = receiveData;
        this.dataTool = dataTool;

        this.mqPublishFactory = mqPublishFactory;
        this.publishDataRouteKey = publishDataRouteKey;
    }

    @Override
    public void run() {
        _logger.info("收到的消息为:" + receiveData);


    }
}

