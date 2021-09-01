package com.example.aws.sqs.controller;

import javax.jms.JMSException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.aws.sqs.SQSClient;

@RestController
public class AWSController {

	@Autowired
	private SQSClient sqsClient;
	
	@GetMapping("/sendmessage")
		public void sendMessageSQS(@RequestParam("message") String message){
			sqsClient.sendSQS(message);
		}
	
	@GetMapping("/startListener")
	public void startAsyncSQSListener() throws JMSException, InterruptedException{
		sqsClient.startlistener();
	}
	
	@GetMapping("/createQ/fifo")
	public void createFIFOQ(@RequestParam("queueName") String queueName){
		if(!queueName.contains(".fifo")){
			queueName=queueName+".fifo";
		}
		sqsClient.createFIFOSQS(queueName);
	}
	
	@GetMapping("/sendmessage/fifo")
	public void sendMessageFIFO(@RequestParam("message") String message){
		sqsClient.sendSQSFIFO(message);
	}
	
//	@GetMapping("/saveEntity")
//	public void saveEntity
	
	@GetMapping("/send/bulk")
	public void sendBulkMessages(){
		sqsClient.sendBulkMessages();
	}
	
}
