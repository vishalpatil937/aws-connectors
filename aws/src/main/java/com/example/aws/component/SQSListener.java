package com.example.aws.component;

import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.aws.messaging.listener.annotation.SqsListener;
import org.springframework.stereotype.Component;

import com.amazonaws.services.sqs.AmazonSQSAsync;

@Component
public class SQSListener {
	
	@Autowired
	AmazonSQSAsync sqsAsync;	
	
	@SqsListener("demo-sqs")
	public void listen(String message){
//		long totalTime=0;
		try {
			long currentTs = System.currentTimeMillis();
			JSONObject json_msg = new JSONObject(message);
			long sentTs = Long.valueOf(json_msg.getString("epoch"));
			String msgId = json_msg.getString("msgId");
			long timeDiff = currentTs - sentTs;
//			totalTime = totalTime+timeDiff;
			System.out.println("MSG-ID:-" + msgId + " TimeDiff::" + timeDiff);
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
	
}
