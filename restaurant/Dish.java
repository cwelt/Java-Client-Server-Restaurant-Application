package restaurant;

import java.io.Serializable;

public class Dish implements Serializable, Comparable<Dish>
{
	// Static variables:
	public static final String STARTER = "Starter";
	public static final String MAIN = "Main Dish";
	public static final String DESERT = "Desert";
	public static final String DRINK = "Drink";
	private static final long serialVersionUID = 1L;
	
	//Instance variables: 
	private final String dishID;
	private final String dishType;
	private String dishDecription;
	private double dishPrice;
	
	// Constructor:  
	public Dish (String id, String type, String description, double price)
	{
		// validation of dish type: 
		if (!(type.equals(STARTER)) && !(type.equals(MAIN)) && !(type.equals(DESERT)) && !(type.equals(DRINK)))
			throw new IllegalArgumentException(String.format("Invalid Dish type: %s", type));
		
		// validation of price: 
		if (price < 0)
			throw new IllegalArgumentException(String.format("Price cannot be negative: %.2f", price));
			
		// Initialization:
		this.dishID = id;
		this.dishType = type;
		this.dishDecription = description;
		this.dishPrice = price;
	} // end of constructor  

	// equals override: compare via dishID
	@Override
	public boolean equals (Object obj)
	{
		if(obj instanceof Dish)
		{
			return(this.dishID.equals(((Dish)obj).dishID));
		}
		else if (obj instanceof String)
			return this.dishID.equals(((String)obj));
		else
			return false;
	}
	
	// compareTo implementation: compare via dishID
	@Override
	public int compareTo(Dish otherDish) 
	{
		return this.dishID.compareTo(otherDish.dishID);
	}
	
	
	// Getters:
	public String getDishID() {return dishID;}
	public String getDishType() {return dishType;}
	public String getDishDecription() {return dishDecription;}
	public double getDishPrice() {return dishPrice;}

	// Setters: 
	public void setDishDecription(String dishDecription) {this.dishDecription = dishDecription;}
	public void setDishPrice(double dishPrice) 
	{
		if (dishPrice >= 0)
			this.dishPrice = dishPrice;
		else throw new IllegalArgumentException(String.format("Price cannot be negtive: %.2f", dishPrice)); 
	}
	
} // end of class Dish
