package com.example.aws;

import org.socialsignin.spring.data.dynamodb.repository.config.EnableDynamoDBRepositories;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.AmazonSQSAsync;
import com.amazonaws.services.sqs.AmazonSQSAsyncClientBuilder;
import com.amazonaws.services.sqs.AmazonSQSClientBuilder;
import com.amazonaws.services.sqs.buffered.AmazonSQSBufferedAsyncClient;
import com.amazonaws.services.sqs.buffered.QueueBufferConfig;

import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@EnableSwagger2
@EnableDynamoDBRepositories
@SpringBootApplication
public class AwsApplication {

	@Value("${aws.accessKey}")
	private String accessKey;

	@Value("${aws.secretKey}")
	private String secretKey;

	public static void main(String[] args) {
		SpringApplication.run(AwsApplication.class, args);
	}

	@Bean
	public Docket productApi() {
		return new Docket(DocumentationType.SWAGGER_2).select()
				.apis(RequestHandlerSelectors.basePackage("com.example.aws")).build();

	}

	@Bean
	public AmazonSQS getClientBuilder() {
		BasicAWSCredentials bAWSc = new BasicAWSCredentials(accessKey, secretKey);
		AmazonSQS sqs = AmazonSQSClientBuilder.standard().withRegion(Regions.US_EAST_2)
				.withCredentials(new AWSStaticCredentialsProvider(bAWSc)).build();
		return sqs;
	}

	@Bean
	public AmazonDynamoDB amazonDynamoDB() {
		BasicAWSCredentials bAWSc = new BasicAWSCredentials(accessKey, secretKey);
		AmazonDynamoDB amazonDynamoDB = AmazonDynamoDBClientBuilder.standard().withRegion(Regions.US_EAST_2)
				.withCredentials(new AWSStaticCredentialsProvider(bAWSc)).build();
		return amazonDynamoDB;
	}

	final QueueBufferConfig config = new QueueBufferConfig()
		    .withMaxInflightReceiveBatches(10)
		    .withMaxDoneReceiveBatches(5)
		    .withLongPoll(true)
		    .withMaxBatchOpenMs(100);
	
	@Bean
	@Primary
	public AmazonSQSAsync  amazonSQSAsync() {
		BasicAWSCredentials bAWSc = new BasicAWSCredentials(accessKey, secretKey);
		AmazonSQSAsync sqsAsync= AmazonSQSAsyncClientBuilder.standard().withCredentials(new AWSStaticCredentialsProvider(bAWSc))
				.withRegion(Regions.US_EAST_1).build();
		
		return new AmazonSQSBufferedAsyncClient(sqsAsync,config);
	}

}
