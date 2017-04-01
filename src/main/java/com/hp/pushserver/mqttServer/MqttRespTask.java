package com.hp.pushserver.mqttServer;

import com.alibaba.fastjson.JSON;
import com.hp.pushserver.utils.DataTool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;

/**
 * Created by jackl on 2017/3/4.
 */
public class MqttRespTask implements Runnable{
    private DataTool dataTool;
    private String msg;
    private MqttPublishFactory mqttPublishFactory;
    private Logger _logger= LoggerFactory.getLogger(getClass());

    public MqttRespTask( String msg, DataTool dataTool,MqttPublishFactory mqttPublishFactory) {
        this.dataTool = dataTool;
        this.msg = msg;
        this.mqttPublishFactory = mqttPublishFactory;
    }

    @Override
    public void run() {
        _logger.info("准备发送消息到设备:"+msg);
        Map<String,Object> dataMap = (Map<String,Object>) JSON.parse(msg);
        List devices=(List)dataMap.get("devices");
        if(devices!=null) {
            for (int i = 0; i < devices.size(); i++) {
                Map<String,String> device = (Map<String,String>)devices.get(i);
                String token=device.get("token");
                _logger.debug("推送消息到token:"+token+",msg:"+msg);
               mqttPublishFactory.publish(token,msg);
            }
        }

    }
}
