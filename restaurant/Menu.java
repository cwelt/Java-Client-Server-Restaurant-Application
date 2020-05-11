package restaurant;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;

public class Menu implements Serializable
{
	//Static Variables:
	private static final long serialVersionUID = 1L;
	
	//Instance Variables:
	private final ArrayList<Dish> menuItems;

	// Constructor with file argument:
	public Menu(String menuFile) throws IOException
	{
		// local data: 
		String[] tokens;
		int lineCounter = 0;
		double dishPrice;
		String dishId, dishType, dishDescription, currentLine;
		Dish dish;
		BufferedReader inputData = null;

		// initialize instance variable of list Dishes in menu: 
		this.menuItems = new ArrayList<Dish>();
		
		// try to open input file:
		try 
		{
			inputData = new BufferedReader(new FileReader(menuFile));
		}
		catch (FileNotFoundException exception)
		{
			throw new FileNotFoundException(String.format("File %s not found", menuFile));
		}
		
		// parse every line in input file
		while ((currentLine = inputData.readLine()) != null)
		{
			lineCounter++;
			tokens = currentLine.split(";");
			
			// try parsing line into individual Dish fields:  
			dishId = tokens[0];
			dishType = tokens[1];
			dishDescription = tokens[2];
			try 
			{
				dishPrice = Double.parseDouble(tokens[3]);
			}
			catch (NumberFormatException exception)
			{
				System.err.printf("Error in line %d: invalid price %s", lineCounter, tokens[3]);
				currentLine = inputData.readLine();
				continue;
			}
			
			// try creating a new Dish instance:  
			try 
			{
				dish = new Dish(dishId, dishType, dishDescription, dishPrice);
			}
			catch (IllegalArgumentException exception)
			{
				System.err.println(exception.getMessage());
				currentLine = inputData.readLine();
				continue;
			}
			
			// add Dish to menu if does not already exist:
			if (!menuItems.contains(dish))
				menuItems.add(dish);
			else 
				System.err.printf("\nLine %d ignored. ID %d already exists", lineCounter, dishId);			
		} // end of while loop for line iteration
		
		inputData.close();
	} // end of constructor 

	public ArrayList<Dish> getMenuItems()
	{
		return menuItems;
	}
}
