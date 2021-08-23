package com.example.aws.dynamodb;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.aws.dynamodb.repository.DynamoDBRepository;

@Service
public class DynamoDBService {

	@Autowired
	DynamoDBRepository dynamoDbRepository;
	
//	public List<DynamoDBEntity> fetchAll(){
//		return dynamoDbRepository.findAll();
//	}
	
	public void  saveEntity(DynamoDBEntity entity){
		dynamoDbRepository.save(entity);
	}
}
