package com.hp.pushserver.utils;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;


/**
 * 工具类
 * Created by luj on 2015/9/17.
 */
@Component
public class DataTool {
    public static final String tcp_connection_hashmap_name="tcp-connections";//TCP连接标志 remoteAddress:address-server.instance
    private Logger _logger = LoggerFactory.getLogger(DataTool.class);


}
