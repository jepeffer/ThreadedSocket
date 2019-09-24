/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package threadedserver;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author unouser
 */
public class ThreadedServer
{
	
	Socket                client;
	CommunicationsManager manager;
	
	/**
	 * @param args the command line arguments
	 */
	public static void main(String[] args)
	{
		new ThreadedServer();
	}
	
	public ThreadedServer()
	{
		manager = new CommunicationsManager();
		try
		{
			// Create a server on port 55555
			ServerSocket serverSocket = new ServerSocket(5555);
			while (true)
			{
				// Wait for connections
				client = serverSocket.accept(); // Blocking call
				manager.addClient(client);
				
			}
			
		} catch (IOException ex)
		{
			Logger.getLogger(ThreadedServer.class.getName()).log(Level.SEVERE, null, ex);
		}
	}
}
