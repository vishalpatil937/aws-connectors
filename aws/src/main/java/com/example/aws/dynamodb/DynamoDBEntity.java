package com.example.aws.dynamodb;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;

@DynamoDBTable(tableName = "demo-aws")
public class DynamoDBEntity {

	private String customerId;
	private String customerName;
	private int customerAccNo;
	
	@DynamoDBHashKey(attributeName = "CustomerId")
	public String getCustomerId() {
		return customerId;
	}
	public void setCustomerId(String customerId) {
		this.customerId = customerId;
	}
	
	@DynamoDBAttribute(attributeName = "CustomerName")
	public String getCustomerName() {
		return customerName;
	}
	public void setCustomerName(String customerName) {
		this.customerName = customerName;
	}
	
	@DynamoDBAttribute(attributeName = "CustomerAccNo")
	public int getCustomerAccNo() {
		return customerAccNo;
	}
	public void setCustomerAccNo(int customerAccNo) {
		this.customerAccNo = customerAccNo;
	}
	
	
}
