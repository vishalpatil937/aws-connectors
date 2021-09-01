package com.example.aws.sqs;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.jms.JMSException;

import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.amazon.sqs.javamessaging.AmazonSQSMessagingClientWrapper;
import com.amazon.sqs.javamessaging.ProviderConfiguration;
import com.amazon.sqs.javamessaging.SQSConnection;
import com.amazon.sqs.javamessaging.SQSConnectionFactory;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.AmazonSQSClientBuilder;
import com.amazonaws.services.sqs.model.CreateQueueRequest;
import com.amazonaws.services.sqs.model.Message;
import com.amazonaws.services.sqs.model.ReceiveMessageRequest;
import com.amazonaws.services.sqs.model.SendMessageRequest;

@Service
public class SQSClient {

	@Autowired
	AmazonSQS sqs;

	@Value("${aws.sqs.url}")
	String sqsUrl;

	@Value("${aws.sqs.fifo.url}")
	String sqsFifoUrl;

	public SQSConnection getConnection() throws JMSException {

		SQSConnectionFactory connectionFactory = new SQSConnectionFactory(new ProviderConfiguration(), sqs);

		return connectionFactory.createConnection();
	}

	public void sendSQS(String message) {

		/*
		 * BasicAWSCredentials bAWSc = new BasicAWSCredentials(accessKey,
		 * secretKey); return
		 * AmazonSQSClientBuilder.standard().withRegion(region).withCredentials(
		 * new AWSStaticCredentialsProvider(bAWSc)).build();
		 */

		SendMessageRequest send_msg_request = new SendMessageRequest().withQueueUrl(sqsUrl).withMessageBody(message)
				.withDelaySeconds(5);
		// AmazonSQS sqs = getClientBuilder();
		sqs.sendMessage(send_msg_request);
		System.out.println("Sent message::" + message);

	}

	public void sendSQSFIFO(String message) {

		SendMessageRequest send_msg_request = new SendMessageRequest().withQueueUrl(sqsFifoUrl)
				.withMessageBody(message);
		send_msg_request.setMessageGroupId("group1");
		// AmazonSQS sqs = getClientBuilder();
		sqs.sendMessage(send_msg_request);
		System.out.println("Sent message::" + message);

	}

	public void startlistener() {
		// AmazonSQS sqs = getClientBuilder();
		for (;;) {
			ReceiveMessageRequest receiveMessageRequest = new ReceiveMessageRequest(sqsUrl);
			long totalTime=0;
			List<Message> messages = sqs.receiveMessage(receiveMessageRequest).getMessages();
			for (Message message : messages) {
				if (null != message) {
					/*
					 * System.out.println(" Message");
					 * System.out.println(" MessageId: " +
					 * message.getMessageId());
					 * System.out.println(" ReceiptHandle: " +
					 * message.getReceiptHandle());
					 * System.out.println(" MD5OfBody: " +
					 * message.getMD5OfBody()); System.out.println(" Body: " +
					 * message.getBody());
					 */
					/*
					 * for (Entry<String, String> entry :
					 * message.getAttributes().entrySet()) {
					 * System.out.println(" Attribute");
					 * System.out.println(" Name: " + entry.getKey());
					 * System.out.println(" Value: " + entry.getValue()); }
					 */
					String recevMsg = message.getBody();
					try {
						long currentTs = System.currentTimeMillis();
						JSONObject json_msg = new JSONObject(recevMsg);
						long sentTs = Long.valueOf(json_msg.getString("epoch"));
						String msgId = json_msg.getString("msgId");
						long timeDiff = currentTs - sentTs;
						totalTime=totalTime+timeDiff;
						System.out.println("MSG-ID:-" + msgId + " TimeDiff::" + timeDiff);
						sqs.deleteMessage(sqsUrl, message.getReceiptHandle());
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
			long avgTime=totalTime/messages.size();
			System.out.println("Avg time:- "+avgTime +" No. of msgs:-"+messages.size());
			
			totalTime=0;
		}
	}

	public void createFIFOSQS(String queueName) {

		try {
			AmazonSQSMessagingClientWrapper client = getConnection().getWrappedAmazonSQSClient();
			System.out.println(client.queueExists(queueName));
			if (!client.queueExists(queueName)) {
				Map<String, String> attributes = new HashMap<String, String>();
				attributes.put("FifoQueue", "true");
				attributes.put("ContentBasedDeduplication", "true");
				client.createQueue(new CreateQueueRequest().withQueueName(queueName).withAttributes(attributes));
			} else {
				System.out.println("MQ Exists");
			}
		} catch (JMSException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void sendBulkMessages() {
		String msg_template = "{\"msgId\":{MSG_ID},\"epoch\":{EPOCH}}";
		for (int i = 0; i < 500; i++) {
			String interim_msg = msg_template.replace("{MSG_ID}", String.valueOf(i));
			String msg = interim_msg.replace("{EPOCH}", String.valueOf(System.currentTimeMillis()));
			System.out.println("MSG:: " + msg);
			
			SendMessageRequest send_msg_request = new SendMessageRequest().withQueueUrl(sqsUrl).withMessageBody(msg);
			// AmazonSQS sqs = getClientBuilder();
			sqs.sendMessage(send_msg_request);

			/*try {
				long currentTs = System.currentTimeMillis();
				JSONObject json_msg = new JSONObject(msg);
				long sentTs = Long.valueOf(json_msg.getString("epoch"));
				String msgId = json_msg.getString("msgId");
				long timeDiff = currentTs - sentTs;
				System.out.println("MSG-ID:-" + msgId + " TimeDiff::" + timeDiff);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} */

		}

	}

}
