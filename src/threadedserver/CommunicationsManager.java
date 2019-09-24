package threadedserver;

import java.net.Socket;
import java.util.ArrayList;

public class CommunicationsManager
{
	ArrayList<ThreadedClient> clientList;
	
	public CommunicationsManager()
	{
		clientList = new ArrayList<ThreadedClient>();
	}
	
	/*
	 * This method will find the user the other user would like to talk to Later
	 * this will call from a database
	 */
	public ThreadedClient findFriend(String nameIn)
	{
		for (int i = 0; i < clientList.size(); i++)
		{
			ThreadedClient client = clientList.get(i);
			
			if (client.getName().equals(nameIn))
			{
				System.out.println(client.getName() + " " + nameIn);
				return client;
			}
		}
		
		return null;
	}
	
	public boolean sendMessageRequest(String nameIn)
	{
		ThreadedClient client = findFriend(nameIn);
		if (client == null)
		{
			return false;
		}
		else return client.sendMessagePermissionRequest(nameIn);
	}
	
	/**
	 * Adds a client to the client list
	 * 
	 * @param userSocketIn
	 * @param client
	 */
	public void addClient(Socket client)
	{
		ThreadedClient threadedClient = new ThreadedClient(client, this);
		
		Thread clientThread = new Thread(threadedClient);
		
		clientList.add(threadedClient);
		
		clientThread.start();
	}
	
	public boolean SendMessageToConnectedUser(String nameIn, String message)
	{
		ThreadedClient client = findFriend(nameIn);
		
		if (client == null)
		{
			return false;
		}
		else
		{
			client.sendUserMessage("\t\t\t\t" + message);
			return true;
		}
	}
	
}
