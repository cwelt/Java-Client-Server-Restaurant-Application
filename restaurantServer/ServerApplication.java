package restaurantServer;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import restaurant.Menu;

public class ServerApplication
{
	// Static variables:
	public static final int SERVER_PORT = 3333; 
	private static final int BACKLOG_MAX_QUEUE_LENGTH = 20;
	private static final String menuFileName = "Menu.txt"; 
	
	public static void main (String[] args) 
	{
		// data initialization: 
		ServerSocket serverSocket = null;
		Socket connectionSocket = null;
		Menu menu = null;
		
		// step 1 - create server on desired port:   
		try
		{
			serverSocket = new ServerSocket(SERVER_PORT, BACKLOG_MAX_QUEUE_LENGTH);
		}
		catch (IOException ioException)
		{
			ioException.printStackTrace();
			System.err.print("System faild binding server to port");
			System.exit(1);
		}
		
		// step 2 - read restaurant menu from file: 
		try
		{
			menu = new Menu(menuFileName);
		} 
		catch (IOException ioException)
		{
			ioException.printStackTrace();
			System.err.printf("System faild reading menu file \"%s\"", menuFileName);
			System.exit(1);
		}
		
		// Step 3 - Accept clients requests and delegate them to thread workers: 
		System.out.printf("Restaurant server is now ready to accept connections...\n\n");
		while (!serverSocket.isClosed())
		{
			try 
			{
				connectionSocket = serverSocket.accept();
				System.out.printf("Server connected to %s on port %d\n",
						connectionSocket.getInetAddress(), connectionSocket.getPort());
				new requestProcessorThread(connectionSocket, menu).start();
			}
			catch (IOException | SecurityException exception)
			{
				System.err.printf("Connection to client failed\n");
				exception.printStackTrace();
			}
		}
		
		System.out.printf("\nServer socket has been closed\n");
	} //end of main 
} // end of class
