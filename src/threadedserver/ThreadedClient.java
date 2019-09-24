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
        private boolean loggedIn = false;
	
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
			
			sendUserMessageLN(welcomeMessage);
			
			sendUserMessage("Please enter your name: ");
			
			name = scanner.nextLine();
			
                        sendUserMessageLN("Welcome " + name + " to my server!\rn Type -help for a list of commands!");
                        
                        loggedIn = true;
                                           
                        baseState();
                        
			//waitingForFriend();
			
		} catch (IOException ex)
		{
			Logger.getLogger(ThreadedClient.class.getName()).log(Level.SEVERE, null, ex);
		}
	}
	
        
	public void waitingForFriend()
	{
		sendUserMessageLN("Who would you like to talk too (enter a name)");
		
		String attemptedFriend = scanner.nextLine();
		
		System.out.println("Test; " + attemptedFriend);
		
		boolean connected = manager.sendMessageRequest(attemptedFriend, this);
		
		if (!connected)
		{
			sendUserMessageLN("No user found.");
			waitingForFriend();
		}
	}
        
       private String helpString()
       {
           return "-fr: Returns friend requests.\rn"
                   + "-mf <Friend Name>: Opens up a chat with your friend.\rn"
                   + "-sfr <Friend Name>: Sends a friend request to the specified user\rn"
                   + "-cm <Friend Name>: Checks messages you have.";
       }
       
        public void baseState()
        {
            baseString();
            String commandIn = scanner.nextLine();
            String responce = "";
           
            switch (commandIn)
            {
                case "-help":
                {
                    responce = helpString();
                    sendUserMessageLN(responce);
                    baseState();
                    break;
                }
                case "-mf":
                {
                    sendUserMessageLN("ENTER A FRIEND NAME");
                    String friendName = scanner.nextLine();
                    sendUserMessageLN("Now attempting to contact " + friendName + ".");
                    ThreadedClient friendClient = manager.findFriend(friendName, this);
                    sendMessagePermissionRequest(friendClient, this);
                    break;
                }
                default:
                {
                    baseState();
                }
                
            }
            
          
                    
        }
        
        public void baseString()
	{
            String message = this.getName() + " ~ ";
	    out.print(message);
	    out.flush();
	}
        
        /**
	 * Sends the user a message and flushes the PrintStream
         * with a newline
	 * 
	 * @param messageIn - The message to be send to the user
	 */
	public void sendUserMessage(String messageIn)
	{
            if (!loggedIn)
            {
                // Do nothing
            }
            else
            {
                baseString();
            }
		out.print(messageIn);
		out.flush();
	}
        
	/**
	 * Sends the user a message and flushes the PrintStream
         * with a newline
	 * 
	 * @param messageIn - The message to be send to the user
	 */
	public void sendUserMessageLN(String messageIn)
	{
            if (!loggedIn)
            {
                // Do nothing
            }
            else
            {
                baseString();
            }
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
		sendUserMessageLN(
				"You are now connected with : " + connectedClient.getName() + "\rnDon't be shy, send a message!");
		boolean isCurrentlyMessaging = currentlyMessaging(connectedClient);
		
		if (!isCurrentlyMessaging)
		{
			sendUserMessageLN("Disconnecting from " + currentlyMessaging + ".");
			currentlyMessaging = null;
		}
	}
	
	public boolean currentlyMessaging(ThreadedClient clientIn)
	{
		String message = scanner.nextLine();
		
		if (message != "LEAVE" || currentlyMessaging == null)
		{
			sendUserMessageLN(message);
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
