package com.example.aws.dynamodb.repository;

import org.springframework.data.repository.CrudRepository;

import com.example.aws.dynamodb.DynamoDBEntity;

public interface DynamoDBRepository extends CrudRepository<DynamoDBEntity, String>{
	
}
