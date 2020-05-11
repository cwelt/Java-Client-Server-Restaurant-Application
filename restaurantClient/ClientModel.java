package restaurantClient;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import restaurant.Customer;
import restaurant.Menu;
import restaurant.Order;

public class ClientModel 
{
	// Static variables:
	private static final int EXIT_FAILURE = 1;
	
	// Instance variables
	private String host;
	private int port;
	private Socket connectionSocket;
	private ObjectOutputStream outputStream;
	private ObjectInputStream inputStream;
	private Menu menu;
	private Order order;
	private Customer customer;
	private String serverOrderResponse;
	private String serverErrorMessage;
	private String orderPrintout; 
	
	private boolean customerInterestedToOrder;
	
	// Constructor 
	public ClientModel(String serverHost, int serverPort) throws IOException
	{
		host = serverHost;
		port = serverPort;
		connectionSocket = new Socket(host, port);
		customer = new Customer();
		customerInterestedToOrder = true;
	}
	
	// Terminate execution after printing customized error message: 
	public static void terminateWithError(String message)
	{
		System.err.printf(message);
		System.exit(EXIT_FAILURE);
	}
	
	// Utility method for initializing I\O streams:
	public void setStreams() throws IOException
	{
		outputStream = new ObjectOutputStream(connectionSocket.getOutputStream());
		outputStream.flush();

		inputStream = new ObjectInputStream(connectionSocket.getInputStream());
	}
	
	// Utility method for reseting connections:
	public void setConnection() throws IOException
	{
		connectionSocket = new Socket(host, port);
	}
	
	// Utility method for closing stream & resources:
	public void closeStreams() throws IOException
	{
		if (inputStream != null)
			inputStream.close();
		if (outputStream != null)
			outputStream.close();
		if(connectionSocket != null)
			connectionSocket.close();
	}
	
	// Sent menu request message to server:
	public Menu requestMenu() throws ClassNotFoundException, IOException
	{
		outputStream.writeObject("MENU");
		outputStream.flush(); 
			
		menu = (Menu)inputStream.readObject();
		return menu;
	}
	
	// Sent order request message to server:
	public String requestOrder() throws ClassNotFoundException, IOException
	{
		System.out.printf("%s", this.order);
		this.setConnection();
		this.setStreams();
		
		outputStream.writeObject("ORDER");
		outputStream.flush(); 
		outputStream.writeObject(this.order);
		outputStream.flush(); 
			
		serverOrderResponse = (String)inputStream.readObject();
		if (serverOrderResponse.equals("APPROVED"))
			this.orderPrintout = (String)inputStream.readObject();
		else 
			this.serverErrorMessage = (String)inputStream.readObject();
		
		return serverOrderResponse;
	}
	
	public void startNewOrder(ClientView view)
	{
		view.setVisible(false);
		try 
		{
			setConnection();
			setStreams();
		}
		catch (IOException ioException)
		{
			ioException.printStackTrace();
		}
		new ClientView(this, menu);
	}
	
	// Getters: 
	public Menu getMenu() {return menu;}
	public Order getOrder() {return order;}
	public Customer getCustomer() {return customer;}
	public boolean isCustomerInterestedToOrder() {return customerInterestedToOrder;}
	public String getServerOrderResponse() {return serverOrderResponse;}
	public String getServerErrorMessage() {return serverErrorMessage;}
	public String getOrderPrintout() {return orderPrintout;}
	
	// Setters: 
	public void setMenu(Menu menu) {this.menu = menu;}
	public void setCustomer(Customer customer) {this.customer = customer;}
	public void setOrder(Order order) {this.order = order;}
	public void setCustomerInterestedToOrder(boolean b) {this.customerInterestedToOrder = b;}
}
