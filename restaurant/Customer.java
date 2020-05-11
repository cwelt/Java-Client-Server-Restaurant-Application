package restaurant;

import java.io.Serializable;

public class Customer implements Serializable
{
	// Static variables:
	private static final long serialVersionUID = 1L;
	
	// Instance variables:
	private String firstName;
	private String lastName;
	private String phoneNumber;
	private String address;
	
	// Default Constructor: 
	public Customer()
	{
		firstName = "";
		lastName = "";
		phoneNumber = "";
		address = "";
	}
	
	// Customized Constructor: 
	public Customer (String fName, String lName, String phone, String orderAddress)
	{
		firstName = fName;
		lastName = lName;
		phoneNumber = phone;
		address = orderAddress;
	}
	
	// Copy Constructor: 
	public Customer (Customer customer)
	{
		firstName = customer.firstName;
		lastName = customer.getLastName();
		phoneNumber = customer.phoneNumber;
		address = customer.address;
	}
	

	// Getters:  
	public String getFirstName() {return firstName;}
	public String getLastName() {return lastName;}
	public String getPhoneNumber() {return phoneNumber;}
	public String getAddress() {return address;}

	// Setters: 
	public void setFirstName(String firstName) {this.firstName = firstName;}
	public void setLastName(String lastName) {this.lastName = lastName;}
	public void setPhoneNumber(String phoneNumber) {this.phoneNumber = phoneNumber;	}
	public void setAddress(String address) {this.address = address;}
	
	@Override 
	public String toString()
	{
		String firstName = "First name: " + getFirstName();
		String lastName = "Last name: " + getLastName();
		String phoneNumber = "Phone number: " + getPhoneNumber();
		String address = "Address: " + getAddress();
		return String.format("%s, %s, %s, %s", firstName, lastName, phoneNumber, address); 
	}
}
