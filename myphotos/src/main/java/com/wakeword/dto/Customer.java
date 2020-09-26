package com.wakeword.dto;

import org.slf4j.Logger;
import static org.slf4j.LoggerFactory.getLogger;

public class Customer {
    private static Logger LOG = getLogger(Customer.class);
	private String customer_version = "v1.0";
	private String customerID;    
	private boolean paidPreiumAccess;
	
	public boolean getPaidPreiumAccess() {
		return paidPreiumAccess;
	}
	public void setPaidPreiumAccess(boolean paidPreiumAccess) {
		this.paidPreiumAccess = paidPreiumAccess;
	}
	public Customer(String id) {
		super();
		this.customerID = id.substring(18);  // chopping off "amzn1.ask.account." from ASK user id
	};
	public String getCustomer_version() {
		return customer_version;
	}
	public void setCustomer_version(String cv) {
		customer_version = cv;
	}

	public String getCustomerID() {
		return customerID;
	}
	public void setCustomerID(String id) {
		customerID = id.substring(18);  // chopping off "amzn1.ask.account." from user id
	}

	@Override
	public String toString() { 
        return JsonFactory.toJson(this); 
    } 	
	
}
