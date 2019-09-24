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
	
	private Socket      socket;
	private PrintStream out;
	private Scanner     scanner;
	private String      name;
	private String      currentlyMessaging = null;
	
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
			
			sendUserMessage("Who would you like to talk too (enter a name)");
			
			waitingForFriend();
			
		} catch (IOException ex)
		{
			Logger.getLogger(ThreadedClient.class.getName()).log(Level.SEVERE, null, ex);
		}
	}
	
	public void waitingForFriend()
	{
		String attemptedFriend = scanner.nextLine();
		
		boolean connected = manager.sendMessageRequest(attemptedFriend);
		
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
		sendUserMessage("You are now connected with : " + currentlyMessaging + "\nDon't be shy, send a message!");
		boolean isCurrentlyMessaging = currentlyMessaging(currentlyMessaging);
		
		if (!isCurrentlyMessaging)
		{
			sendUserMessage("Disconnecting from " + currentlyMessaging + ".");
			currentlyMessaging = null;
		}
	}
	
	public boolean currentlyMessaging(String nameIn)
	{
		String message = scanner.nextLine();
		
		if (message != "LEAVE" || currentlyMessaging == null)
		{
			sendUserMessage(message);
			sendConnectedUserMessage(nameIn, message);
			return currentlyMessaging(nameIn);
		}
		else
		{
			return false;
		}
	}
	
	private void sendConnectedUserMessage(String nameIn, String message)
	{
		manager.SendMessageToConnectedUser(nameIn, message);
	}
	
	public boolean sendMessagePermissionRequest(String nameIn)
	{
		System.out.println(getName() + " here " + nameIn);
		out.println("Would you like to talk to " + nameIn + "?");
		out.println("Y or N");
		out.flush();
		String tempString = scanner.nextLine();
		
		if (tempString.equals("Y"))
		{
			currentlyMessaging = nameIn;
			return true;
		}
		else if (tempString.equals("N"))
		{
			return false;
		}
		else
		{
			out.println("Try again!");
			out.flush();
			return sendMessagePermissionRequest(nameIn);
		}
	}
}
