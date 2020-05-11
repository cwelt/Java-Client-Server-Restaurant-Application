package restaurant;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Set;

public class Order implements Serializable
{
	// Static variables: 
	private static int nextOrderNumber = 1;
	private static final long serialVersionUID = 1L;
	
	// Instance variables:
	private int orderNumber;
	private Customer customer;
	private Hashtable<String, Integer> items; // <key=itemId, value=quantity>
	
	// Constructor 
	public Order(Customer customer, String[] itemIdList, Integer[] quantityList)
	{
		orderNumber = nextOrderNumber++;
		customer = new Customer(customer);
		int listSize = itemIdList.length;
		for (int i = 0; i < listSize; i++)
			items.put(itemIdList[i], quantityList[i]);
	}
	
	// Customized constructor for construction from hash table: 
	public Order (Customer customer, Hashtable<Dish, Integer> itemQuantityList)
	{
		// update numerator: 
		orderNumber = nextOrderNumber++;
		
		// initialize customer: 
		this.customer = new Customer(customer);
		
		// initialize hash table:
		int listSize = itemQuantityList.size();
		this.items = new Hashtable<String, Integer>(listSize);
		
		Dish[] dishItems = itemQuantityList.keySet().toArray(new Dish[listSize]);
		Integer[] quantities = itemQuantityList.values().toArray(new Integer[listSize]);
		
		for (int i = 0; i < listSize; i++)
			items.put(dishItems[i].getDishID(), quantities[i]);
	}
	
	// Getters:
	public int getOrderNumber() {return orderNumber;}
	public Customer getCustomer() {return customer;}
	public Hashtable<String, Integer> getItems() {return items;}
	
	// Print preview of order before approving
	public String getOrderPrintPreview(Menu menu)
	{
		String itemId;
		String itemDescription;
		Dish item = null;
		int quantity;
		double price; 
		double totalItemCost;
		float totalOrderCost = 0;
		ArrayList<Dish> menuItems = menu.getMenuItems();
		Set<String> orderItems = this.getItems().keySet();
		Iterator<String> itemIterator = orderItems.iterator();
		String itemList = "-->Item list:";
		String tableHeader = String.format("\n---->|%-8s |%-26s |%-13s |%-12s |%-8s|\n", 
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
			quantity = this.getItems().get(itemId);
			price = item.getDishPrice();
			totalItemCost = quantity * price;
			totalOrderCost += totalItemCost;
			
			String itemDetails = String.format("---->|%-8s |%-26s |%-13s |%-12s |%-8s|\n", 
					itemId, itemDescription, quantity, price, totalItemCost);
			itemsTable = itemsTable + itemDetails;		
		}
		
		String totalOrderAmount = String.format("\n-->Total Sum of order: %.2f\n", totalOrderCost);  
		
		return "\n" + itemList + tableHeader + line + itemsTable + totalOrderAmount + "\n";	 
	}
}
