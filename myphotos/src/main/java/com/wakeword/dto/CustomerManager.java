package com.wakeword.dto;

import java.util.Scanner;

import org.boon.json.ObjectMapper;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.SdkClientException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import com.wakeword.main.Constants;

import org.slf4j.Logger;
import static org.slf4j.LoggerFactory.getLogger;

public class CustomerManager {
    private static Logger LOG = getLogger(CustomerManager.class);
	private Customer customer;
    
    
	public CustomerManager(Customer customer) {
		super();
		this.customer = customer;
	}
	
	public CustomerManager(String customerId) {
		super();
		this.customer = new Customer(customerId);
	}
	
	/*
	 * Save the customer info in json format to S3 keyed by ASK user ID
	 */
	public boolean saveCustomer() {

        String stringObjKeyName = customer.getCustomerID();

        try {      	
            AmazonS3 s3Client = AmazonS3ClientBuilder.standard().withRegion("us-west-2").build();
            s3Client.putObject(Constants.BUCKET, stringObjKeyName, customer.toString());
            return true;
            
        } catch (AmazonServiceException e) {
        	LOG.debug("AmazonServiceException " + e.getStackTrace());
        } catch (SdkClientException e) {
        	LOG.debug("SdkClientException " + e.getStackTrace());
        } catch (Exception e) {
        	LOG.debug("Save Customer exception " +e.getStackTrace());
        }
        return false;
	}
	
	/*
	 * Fetch customer json per the ASK User Id and return a Customer object for managing book list info
	 */
	public Customer getCustomer(String customerID) {
        try {      	
            AmazonS3 s3Client = AmazonS3ClientBuilder.standard().withRegion("us-west-2").build();
            S3Object s3o = s3Client.getObject(new GetObjectRequest(Constants.BUCKET, customer.getCustomerID()));
            if (s3o !=null) {
            	unmarshallCustomer(s3o);
            }
        } catch (AmazonServiceException e) {
        	LOG.debug("AmazonServiceException " + e.getStackTrace());
        } catch (SdkClientException e) {
        	LOG.debug("SdkClientException " + e.getStackTrace());
        } catch (Exception e) {
        	LOG.debug("Save Customer exception " +e.getStackTrace());
        }
        
		return customer;
	}
	
	/*
	 * See if customer exists already 
	 */
	public boolean hasCustomer(String customerID) {
        try {      	
            AmazonS3 s3Client = AmazonS3ClientBuilder.standard().withRegion("us-west-2").build();
            return s3Client.doesObjectExist(Constants.BUCKET, customer.getCustomerID());
        } catch (AmazonServiceException e) {
        	LOG.debug("AmazonServiceException " + e.getStackTrace());
        } catch (SdkClientException e) {
        	LOG.debug("SdkClientException " + e.getStackTrace());
        } catch (Exception e) {
        	LOG.debug("Save Customer exception " +e.getStackTrace());
        }
        return false;
	}
	
	private void unmarshallCustomer(S3Object s3o)
	{
		
		Scanner sc = new Scanner(s3o.getObjectContent());
	    StringBuffer sb = new StringBuffer();
	    while(sc.hasNext()){
	         sb.append(sc.nextLine());
	    }

		ObjectMapper mapper = JsonFactory.create();
		customer = mapper.readValue(sb.toString(), Customer.class);
	}
	
	
}
