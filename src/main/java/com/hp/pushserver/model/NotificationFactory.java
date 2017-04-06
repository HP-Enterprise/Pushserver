package com.hp.pushserver.model;



import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;

import java.util.ArrayList;
import java.util.List;


public class NotificationFactory {

	public static List<Notification> buildNotifications(String json)
			throws JSONException {
		List<Notification> notifications = new ArrayList<Notification>();
		JSONObject jsonObject = JSONObject.parseObject(json);
		if (jsonObject.containsKey("devices")) {
			JSONArray devices = jsonObject.getJSONArray("devices");
			for(int i=0;i<devices.size();i++)
			{
				JSONObject device = devices.getJSONObject(i);
				String deviceTypeStr = device.getString("os");

				Notification notification = new Notification(json);
				notification.setToken(device.getString("token"));
				if(device.containsKey("mode")){
					notification.setMode(device.getString("mode"));
				}

				notifications.add(notification);
			}

		}

		return notifications;
	}
}
