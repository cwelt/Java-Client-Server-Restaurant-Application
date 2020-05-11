package restaurantClient;

import java.io.IOException;

import restaurant.Menu;
import restaurantServer.ServerApplication;

public class ClientApplication 
{
	public static void main (String[] args) throws IOException
	{
		// data initialization: 
		String host = (args.length > 0) ? args[0] : "localhost"; 
		int port = ServerApplication.SERVER_PORT;
		ClientModel clientModel = null;
		@SuppressWarnings("unused")
		ClientView clientView = null;
		Menu menu = null;
		
		// step 1 - create connection to server: 
		try 
		{
			clientModel = new ClientModel(host, port);
		}
		catch (IOException ioException)
		{
			ioException.printStackTrace();
			String errorMessage = String.format("Connection to host %s port %d failed\n", host, port);
			ClientModel.terminateWithError(errorMessage);
		}

		// step 2 - set I\O streams between client and server: 
		try 
		{
			clientModel.setStreams();
		}
		catch (IOException ioException)
		{
			ioException.printStackTrace();
			ClientModel.terminateWithError("System faild setting stream connections\n");
		} 
		
		// step 3 - request menu from server and close connections: 	
		try 
		{
			menu = clientModel.requestMenu();
		}
		catch (ClassNotFoundException | IOException exception)
		{
			clientModel.closeStreams();
			exception.printStackTrace();
			ClientModel.terminateWithError("Failure requesting menu from server\n");
		}
		
		// step 4 - display menu to user and wait for an order: 	
			clientModel.setConnection();
			clientModel.setStreams();
			clientView = new ClientView(clientModel, menu);
	}
}
