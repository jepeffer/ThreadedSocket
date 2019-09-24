/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package threadedserver;

import java.io.IOException;
import java.io.PrintStream;
import java.net.Socket;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author unouser
 */
public class ThreadedClient implements Runnable
{
	
	private Socket         socket;
	private PrintStream    out;
	private Scanner        scanner;
	private String         name;
	private String         currentlyMessaging = null;
	private ThreadedClient connectedClient    = null;
	public boolean         requestingFriend   = false;
	public boolean         currentlyChatting  = false;
	
	private CommunicationsManager manager;
	
	public ThreadedClient(Socket inSocket, CommunicationsManager managerIn)
	{
		socket = inSocket;
		manager = managerIn;
	}
	
	@Override
	public void run()
	{
		try
		{
			System.out.println("A socket connected");
			out = new PrintStream(socket.getOutputStream());
			scanner = new Scanner(socket.getInputStream());
			
			String welcomeMessage = "================================================================================\r\n"
					+ "\t\tWelcome to BareBones text messaging application.\r\n"
					+ "================================================================================";
			
			sendUserMessage(welcomeMessage);
			
			sendUserMessage("Please enter your name?");
			
			name = scanner.nextLine();
			
			waitingForFriend();
			
		} catch (IOException ex)
		{
			Logger.getLogger(ThreadedClient.class.getName()).log(Level.SEVERE, null, ex);
		}
	}
	
	public void waitingForFriend()
	{
		sendUserMessage("Who would you like to talk too (enter a name)");
		
		String attemptedFriend = scanner.nextLine();
		
		System.out.println("Test; " + attemptedFriend);
		
		boolean connected = manager.sendMessageRequest(attemptedFriend, this);
		
		if (!connected)
		{
			sendUserMessage("No user found.");
			waitingForFriend();
		}
	}
	
	/**
	 * Sends the user a message and flushes the PrintStream
	 * 
	 * @param messageIn - The message to be send to the user
	 */
	public void sendUserMessage(String messageIn)
	{
		out.println(messageIn);
		out.flush();
	}
	
	/**
	 * Returns the user's name
	 * 
	 * @return
	 */
	public String getName()
	{
		if (name == null || name == "")
		{
			return null;
		}
		else return name;
	}
	
	public void messageState()
	{
		currentlyChatting = true;
		sendUserMessage(
				"You are now connected with : " + connectedClient.getName() + "\nDon't be shy, send a message!");
		boolean isCurrentlyMessaging = currentlyMessaging(connectedClient);
		
		if (!isCurrentlyMessaging)
		{
			sendUserMessage("Disconnecting from " + currentlyMessaging + ".");
			currentlyMessaging = null;
		}
	}
	
	public boolean currentlyMessaging(ThreadedClient clientIn)
	{
		String message = scanner.nextLine();
		
		if (message != "LEAVE" || currentlyMessaging == null)
		{
			sendUserMessage(message);
			sendConnectedUserMessage(clientIn, message);
			return currentlyMessaging(clientIn);
		}
		else
		{
			return false;
		}
	}
	
	private void sendConnectedUserMessage(ThreadedClient clientIn, String message)
	{
		manager.SendMessageToConnectedUser(clientIn, message);
	}
	
	public boolean sendMessagePermissionRequest(ThreadedClient clientIn, ThreadedClient lookingClient)
	{
		String tempString = "";
		out.println("Would you like to talk to " + lookingClient.getName() + "?");
		out.println("Y or N");
		out.flush();
		try
		{
			
			tempString = scanner.nextLine();
		} catch (IndexOutOfBoundsException e)
		{
			System.out.println("ERROR");
		}
		
		if (tempString.equals("Y"))
		{
			connectedClient = clientIn;
			return true;
		}
		else if (tempString.equals("N"))
		{
			return false;
		}
		else
		{
			return false;
		}
	}
}
