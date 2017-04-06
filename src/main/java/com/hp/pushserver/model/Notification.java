package com.hp.pushserver.model;

import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;


public class Notification {
	private Map<String, Object> root;
	private Map<String, Object> aps;
	private Map<String, Object> customAlert;
	private final static int COMMAND = 0;
	private String token;
	private String id;
	private String originalJson;
	private String mode;
	public String getOriginalJson() {
		return originalJson;
	}

	public void setOriginalJson(String originalJson) {
		this.originalJson = originalJson;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}



	private Logger logger = LoggerFactory.getLogger(Notification.class);
	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public Notification() {
		super();
		root = new HashMap<String, Object>();
		aps = new HashMap<String, Object>();
		customAlert = new HashMap<String, Object>();
	}

	public Notification(String json)  {
		this.originalJson = json;
		root = new HashMap<String, Object>();
		aps = new HashMap<String, Object>();
		customAlert = new HashMap<String, Object>();
		JSONObject jsonObject;
		try {
			jsonObject = JSONObject.parseObject(json);
			this.setAlert(jsonObject.getString("alert"));
			int badge = 0;
			try {
				badge = Integer.parseInt(jsonObject.getString("badge"));
			} catch (Exception e) {
				badge = 0;
			}
			this.setBadge(badge);
			this.addCustomField("content", jsonObject.get("content"));

		} catch (JSONException e1) {
			this.logger.error("Create Payload fail with json string:"+e1.getMessage());
			e1.printStackTrace();
		}
		
	}

	public void setSound(String sound) {
		aps.put("sound", sound);
	}

	public void setBadge(int badge) {
		aps.put("badge", badge);
	}

	public void setAlert(String alert) {
		customAlert.put("body", alert);
	}

	public void setActionKey(String actionKey) {
		customAlert.put("action-loc-key", actionKey);
	}

	public void setLocalizedKey(String locKey) {
		customAlert.put("loc-key", locKey);
	}

	public void setLocalizedArguments(Collection<String> arguments) {
		customAlert.put("loc-args", arguments);
	}

	public void setLunchImage(String image) {
		customAlert.put("launch-image", image);
	}

	public void addCustomField(String key, Object value) {
		root.put(key, value);
	}

	private byte[] marshall;


	
	public byte[] androidMarshall() {
		if (marshall == null) {
			byte[] payloadByte = this.toBytes();
			ByteArrayOutputStream boas = new ByteArrayOutputStream();
			DataOutputStream dos = new DataOutputStream(boas);

			try {
				dos.write(payloadByte);
				marshall = boas.toByteArray();
			} catch (IOException e) {
				throw new AssertionError();
			}
		}
		return marshall;
	}


	@Override
	public String toString() {
		this.insertAlert();
//		String apsflag = "aps";
//		if(this.deviceType==DeviceType.ANDROID){
//			apsflag="mqtt";
//		}
		root.put("aps", this.aps);
		JSONObject jo =(JSONObject)JSONObject.toJSON(root);
		logger.debug("APNS message:"+jo.toString());
		return jo.toString();
	}

	public byte[] toBytes() {
		return toUTF8Bytes(this.toString());
	}

	public static byte[] toUTF8Bytes(String s) {
		try {
			return s.getBytes("UTF-8");
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException(e);
		}
	}
	private void insertAlert() {
		switch (customAlert.size()) {
		case 0:
			aps.remove("alert");
			break;
		case 1:
			if (customAlert.containsKey("body")) {
				aps.put("alert", customAlert.get("body"));
			}
			break;
		default:
			aps.put("alert", customAlert);
			break;
		}
	}

	public String getMode() {
		return mode;
	}

	public void setMode(String mode) {
		this.mode = mode;
	}

}
