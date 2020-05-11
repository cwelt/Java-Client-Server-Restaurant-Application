package restaurantServer;

import java.io.IOException;
import java.io.InvalidClassException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Set;

import restaurant.Customer;
import restaurant.Dish;
import restaurant.Menu;
import restaurant.Order;

public class requestProcessorThread extends Thread 
{
	// Static variables: 
	public final String menuRequest = "MENU"; 
	public final String orderRequest = "ORDER";
	public final String orderApproved = "APPROVED";
	public final String orderDenied = "DENIED";
	
	// Instance variables: 
	private final Menu menu;
	private final Socket connectionSocket;
	private final ObjectInputStream inputStream;
	private final ObjectOutputStream outputStream;
	private String clientsMessage;
	private Order clientsOrder;
	private String clientsOrderStatus;
	private String DenialReasonMessage;
	
	// Constructor: 
	public requestProcessorThread(Socket connectionSocket, Menu menu) throws IOException
	{
		// initialize connection socket to client and menu:
		this.connectionSocket = connectionSocket;
		this.menu = menu;
		
		// setup output stream for sending objects to client:
		outputStream = new ObjectOutputStream(connectionSocket.getOutputStream());
		outputStream.flush(); // flush output buffer to send header information 
		
		// setup input stream for receiving objects from client: 
		inputStream = new ObjectInputStream(connectionSocket.getInputStream());
	}
	
	// Run thread to deal with currents client request 
	@Override
	public void run() 
	{
		try
		{
			processRequest();
		}
		catch (IOException ioException) 
		{
			System.err.print("Error occoured during processing clients request\n");
			ioException.printStackTrace();
			closeStreams();
			System.exit(1);
		}
	}
	
	// Processing message requests from client:
	private void processRequest() throws IOException
	{ 
		try 
		{  // examine clients message type and handle it appropriately:
			clientsMessage = (String)inputStream.readObject();
			
			switch (clientsMessage)
			{
				case menuRequest: 
					processMenuRequest(); break;
				case orderRequest: 
					processOrderRequest(); break;
				default: 
					outputStream.writeObject(String.format("Invalid message: \"s\"", clientsMessage));
					outputStream.flush();
			} 
		} 
		catch (ClassNotFoundException | InvalidClassException classException)
		{ // print error log and send error to client:
			System.err.printf("Exception occoured during attempt to proccess message\n");
			classException.printStackTrace();
			outputStream.writeObject(classException);
			outputStream.flush();
			closeStreams();
		}		
	}
	
	// Process clients requests for menu 
	public void processMenuRequest() throws IOException
	{
		System.out.printf(String.format("Server processing a request for a menu on port %s from %s...\n",
							connectionSocket.getPort(), connectionSocket.getInetAddress()));
		outputStream.writeObject(menu);
		outputStream.flush(); 
		closeStreams();
	}
	
	// Process clients requests for an order from the restaurant 
	public void processOrderRequest() throws IOException, ClassNotFoundException 
	{
		System.out.printf(String.format("Server processing a request for an order on port %d from %s...\n",
				connectionSocket.getPort(), connectionSocket.getInetAddress()));
		clientsOrder = (Order)inputStream.readObject();
		validateOrder();
		if (clientsOrderStatus.equals(orderApproved))
		{
			sendOrderApproval();
			System.out.printf(getOrderPrintout());
		}
		else 
			sendOrderDenial();
	}
	
	// Validation method to assure client's order includes only valid items
	public void validateOrder()
	{
		String currentItemId;
		boolean isOrderValid = true;
		ArrayList<Dish> validItemIDs = this.menu.getMenuItems();
		Set<String> orderItemIDs = clientsOrder.getItems().keySet();
		Iterator<String> itemIterator = orderItemIDs.iterator();
		
		while (itemIterator.hasNext() && isOrderValid)
		{
			isOrderValid = false;
			currentItemId = itemIterator.next();
			for (Dish dish: validItemIDs)
			{
				if (dish.getDishID().equals(currentItemId))
				{
					isOrderValid = true;
					break;
				}
			}
			if (!isOrderValid)
			{
				clientsOrderStatus = orderDenied;
				DenialReasonMessage = String.format("Item %s does not exist in menu", currentItemId);
			}
			else clientsOrderStatus = orderApproved;
		}
	}
	
	// Send approval of order to client 
	public void sendOrderApproval() throws IOException
	{
		outputStream.writeObject(clientsOrderStatus);
		outputStream.flush(); 
		outputStream.writeObject(getOrderPrintout());
		outputStream.flush();
		closeStreams();
	}
	
	// Send denial of order to client
	public void sendOrderDenial() throws IOException
	{
		outputStream.writeObject(clientsOrderStatus);
		outputStream.flush(); 
		outputStream.writeObject(DenialReasonMessage);
		outputStream.flush();
		closeStreams();
	}
	
	// Utility method for closing streams & resources: 
	private void closeStreams() 
	{
		try 
		{
			inputStream.close();
			outputStream.close();
			connectionSocket.close();
		}
		catch (IOException ioExcpetion)
		{
			System.err.printf("System failed to close connections & streams" + ioExcpetion.getStackTrace());
			System.exit(1);
		}
	}
	
	public String getOrderPrintout()
	{
		Customer customer = clientsOrder.getCustomer();
		String title = "=================================\n";
		String orderNum = String.format("Order number %d in Status Approved\n", clientsOrder.getOrderNumber());
		String firstName = String.format("---->First Name: %s\n", customer.getFirstName());
		String lastName = String.format("---->Last Name: %s\n", customer.getLastName());
		String phone = String.format("---->Phone Number: %s\n", customer.getPhoneNumber());
		String address = String.format("---->Address: %s\n", customer.getAddress());
		String itemId;
		String itemDescription;
		Dish item = null;
		int quantity;
		double price; 
		double totalItemCost;
		float totalOrderCost = 0;
		ArrayList<Dish> menuItems = this.menu.getMenuItems();
		Set<String> orderItems = clientsOrder.getItems().keySet();
		Iterator<String> itemIterator = orderItems.iterator();
		String itemList = "-->Item list:";
		String tableHeader = String.format("\n---->|%-6s |%-23s |%-15s |%-12s |%-7s|\n", 
				"ID", "Description", "Quantity", "Price", "Cost");
		String line = "---->|-------|------------------------|----------------|-------------|-------|\n";
		String itemsTable = "";
		
		while (itemIterator.hasNext())
		{
			itemId = itemIterator.next();
			for (int i = 0; i < menuItems.size(); i++)
				if (menuItems.get(i).getDishID().equals(itemId))
				{
					item = menuItems.get(i);
					break;
				}
			itemDescription = item.getDishDecription();
			quantity = clientsOrder.getItems().get(itemId);
			price = item.getDishPrice();
			totalItemCost = quantity * price;
			totalOrderCost += totalItemCost;
			
			String itemDetails = String.format("---->|%-6s |%-23s |%-15s |%-12s |%-7s|\n", 
					itemId, itemDescription, quantity, price, totalItemCost);
			itemsTable = itemsTable + itemDetails;		
		}
		
		String totalOrderAmount = String.format("\n-->Total Sum of order: %.2f\n", totalOrderCost);  
		
		return "\n" + title + orderNum + title + firstName + lastName + phone + address +
				  itemList + tableHeader + line + itemsTable + totalOrderAmount + "\n";	
	}
}
