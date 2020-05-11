package restaurantClient;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Hashtable;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import restaurant.Customer;
import restaurant.Dish;
import restaurant.Menu;
import restaurant.Order;

public class ClientView extends JFrame implements ActionListener
{
	// Static Variables: 
	private static Integer[] possibleQuantityValues = {0,1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20};
	private static final long serialVersionUID = 1L;
	
	// Instance variables
	private ClientModel model;
	private Menu menu;
	private Hashtable<Dish, Integer> reservation;
	private ArrayList<Dish> dishList;
	private ArrayList<JComboBox<Integer>> quantityFieldList;
	private ArrayList<JLabel> costList;
	private JPanel headerPanel, centerPanel, footerPanel;
	private JLabel headerLabel;
	private JPanel quantityPanel, costPanel;
	private JPanel typePanel, idPanel, descriptionPanel, pricePanel, columnPanel;
	private JLabel typeLabel, idLabel, descriptionLabel, priceLabel, quantityLabel, totalLabel;
	private JScrollPane scrollPane;
	private JButton done;
	private int userChoice;
	
	// Constructor 
	public ClientView(ClientModel model, Menu menu)
	{
		super ("Restaurant food & drinks order application");
		this.model = model;
		this.menu = menu;
		reservation = new Hashtable<Dish, Integer>();
		dishList = this.menu.getMenuItems();
		quantityFieldList = new ArrayList<JComboBox<Integer>>();
		costList = new ArrayList<JLabel>();
				
		headerLabel = new JLabel("Restaurant reservation application");
		headerLabel.setFont(new Font("Comic Sans MS", Font.BOLD, 40));
		headerLabel.setOpaque(true);
		headerLabel.setBackground(new Color(200,200,200));
		headerPanel = new JPanel(new BorderLayout());
		
		typeLabel = new JLabel("Dish Type");
		typeLabel.setBackground(Color.WHITE);
		idLabel = new JLabel("Dish ID");
		descriptionLabel = new JLabel("Dish Description");
		priceLabel = new JLabel("Dish Price");
		totalLabel = new JLabel("Total Cost");
		quantityLabel = new JLabel("Quantity");
		columnPanel = new JPanel(new GridLayout(1, 0, 7, 7));
		columnPanel.add(typeLabel);
		columnPanel.add(idLabel);
		columnPanel.add(descriptionLabel);
		columnPanel.add(priceLabel);
		columnPanel.add(typeLabel);
		columnPanel.add(quantityLabel);
		columnPanel.add(totalLabel);
		
		headerPanel.add(headerLabel, BorderLayout.CENTER);
		headerPanel.add(columnPanel, BorderLayout.SOUTH);
		add(headerPanel, BorderLayout.NORTH);
		
		typePanel = new JPanel(new GridLayout(0,1,10,10));
		typePanel.setBorder(BorderFactory.createLineBorder(Color.black));
		idPanel = new JPanel(new GridLayout(0,1,10,10));
		idPanel.setBorder(BorderFactory.createLineBorder(Color.black));
		descriptionPanel = new JPanel(new GridLayout(0,1,10,10));
		descriptionPanel.setBorder(BorderFactory.createLineBorder(Color.black));
		pricePanel = new JPanel(new GridLayout(0,1,10,10));
		pricePanel.setBorder(BorderFactory.createLineBorder(Color.black));
		quantityPanel = new JPanel(new GridLayout(0,1,10,10));
		quantityPanel.setBorder(BorderFactory.createLineBorder(Color.black));
		costPanel = new JPanel(new GridLayout(0,1,10,10));
		costPanel.setBorder(BorderFactory.createLineBorder(Color.black));
		centerPanel = new JPanel(new GridLayout(0,6,20,20));
		centerPanel.add(typePanel);
		centerPanel.add(idPanel);
		centerPanel.add(descriptionPanel);
		centerPanel.add(pricePanel);
		centerPanel.add(quantityPanel);
		centerPanel.add(costPanel);
		scrollPane = new JScrollPane(centerPanel);;
		add(scrollPane, BorderLayout.CENTER);
		
		done = new JButton("PRESS THIS BUTTON WHEN YOU'RE DONE SELECTION ITEMS");
		done.addActionListener(this);
		done.setFont(new Font("Comic Sans MS", Font.BOLD, 30));
		done.setForeground(Color.BLUE);
		done.setBackground(Color.WHITE);
		done.setBorder(BorderFactory.createLineBorder(Color.black));
		footerPanel = new JPanel(new BorderLayout());
		footerPanel.add(done);
		add(footerPanel, BorderLayout.SOUTH);
		
		for (Dish dish: dishList)
		{			
			// add dish id: 
			idPanel.add(new JLabel(dish.getDishID()));
			
			// add dish type:
			typePanel.add(new JLabel(dish.getDishType()));
			
			// add dish description:
			descriptionPanel.add(new JLabel(dish.getDishDecription()));
			
			// add dish price: 
			pricePanel.add(new JLabel(Double.toString(dish.getDishPrice())));
			
			// add quantity field:
			JComboBox<Integer> quantityComboBox = new JComboBox<Integer>(possibleQuantityValues);
			quantityComboBox.addActionListener(this);
			quantityFieldList.add(quantityComboBox);
			quantityPanel.add(quantityComboBox);
			
			// add total sum for dish item:
			JLabel itemTotalCost = new JLabel("0.00");
			costList.add(itemTotalCost);
			costPanel.add(itemTotalCost);
		}
		
		setSize(1300, 500);
		setVisible(true);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}

	@Override
	public void actionPerformed(ActionEvent event) 
	{
		// treatment for quantity changes: 
		if(event.getSource().getClass() == JComboBox.class)
		{
			@SuppressWarnings("unchecked")
			JComboBox<Integer> comboBox = (JComboBox<Integer>)event.getSource();
			int quantity = (Integer)comboBox.getSelectedItem();
			int dishIndex = quantityFieldList.indexOf(comboBox);
			Dish dish = dishList.get(dishIndex);
			JLabel totalCost = costList.get(dishIndex);
			totalCost.setText(Double.toString(quantity * dish.getDishPrice()));
			reservation.remove(dish); // remove in case it already exists
			if (quantity > 0)
				reservation.put(dish, quantity);
		}
		
		// treatment for pressing done button:
		else if(event.getSource().getClass() == JButton.class)
		{
			if (reservation.size() == 0)
				JOptionPane.showMessageDialog(this, "You haven't seleced any items!", "Notice", JOptionPane.INFORMATION_MESSAGE);
			else 
			{
				approveReservationPrompt();
				if (userChoice == JOptionPane.OK_OPTION)
				{
					model.setOrder(new Order(model.getCustomer(), this.reservation));
					String serverResponse;
					try 
					{
						serverResponse = model.requestOrder();
						if (serverResponse.equals("APPROVED"))
						{
							String orderPrintout = model.getOrderPrintout();
							System.out.printf(orderPrintout);
							
							String promptAgain = "\nDo you want to start a new order?\n";
							String message = orderPrintout + promptAgain;
							userChoice = JOptionPane.showConfirmDialog(this, message, "Order Approved", JOptionPane.INFORMATION_MESSAGE);
							if (userChoice == JOptionPane.OK_OPTION)
								this.model.startNewOrder(this);
							else model.closeStreams();
						}
						else 
						{
							JOptionPane.showMessageDialog(this, model.getServerErrorMessage(), "Error", JOptionPane.ERROR_MESSAGE);
							System.err.printf(model.getServerErrorMessage());
						}
					} 
					catch (ClassNotFoundException | IOException exception)
					{
						String errorMessage = "Failure sending order:\n"; 
						exception.printStackTrace();
						JOptionPane.showMessageDialog(this, errorMessage, "Error", JOptionPane.ERROR_MESSAGE);
					}
				}// endif of approve 
			} //endif of item list not empty 
		} // endif of triggering object 
	} //end of action performed 
	
	// Approve order before sending to server 
	private int approveReservationPrompt()
	{
		Customer customer = model.getCustomer();
		JTextField firstName = new JTextField(customer.getFirstName());
		JTextField lastName = new JTextField(customer.getLastName());
		JTextField phone = new JTextField(customer.getPhoneNumber());
		JTextField address = new JTextField(customer.getAddress());
		JTextArea orderDetails = new JTextArea(20,60);
		model.setOrder(new Order(model.getCustomer(), this.reservation));
		orderDetails.append("Order details:");
		orderDetails.append(model.getOrder().getOrderPrintPreview(this.menu));
		orderDetails.setEditable(false);
		JPanel personalDetails = new JPanel(new GridLayout(4,2));
		personalDetails.add(new JLabel("First Name:"));
		personalDetails.add(firstName);
		personalDetails.add(new JLabel("Last Name:"));
		personalDetails.add(lastName);
		personalDetails.add(new JLabel("Phone:"));
		personalDetails.add(phone);
		personalDetails.add(new JLabel("Address:"));
		personalDetails.add(address);
		JPanel approvePanel = new JPanel(new BorderLayout());
		approvePanel.add(orderDetails, BorderLayout.NORTH);
		approvePanel.add(personalDetails, BorderLayout.CENTER);
		
		String title = "Enter personal details and approve order:";
		
		userChoice = JOptionPane.showConfirmDialog(this, approvePanel,title, JOptionPane.OK_CANCEL_OPTION);
		
		customer.setFirstName(firstName.getText()); 
		customer.setLastName(lastName.getText());
		customer.setPhoneNumber(phone.getText()); 
		customer.setAddress(address.getText());
		
		// check mandatory fields:
		if ((userChoice == JOptionPane.OK_OPTION)
			&& (firstName.getText().isEmpty() || lastName.getText().isEmpty() 
			||	phone.getText().isEmpty() || address.getText().isEmpty()))
		{
			String errorMessage = "Customer personal details input fields are mandatory!";
			JOptionPane.showMessageDialog(this, errorMessage, "missing input", JOptionPane.ERROR_MESSAGE);
			userChoice = approveReservationPrompt();
		}
		return userChoice;
	} // end of approval method
}
