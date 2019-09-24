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
	public ThreadedClient findFriend(String nameToFindIn, ThreadedClient lookingClient)
	{
		for (int i = 0; i < clientList.size(); i++)
		{
			ThreadedClient client = clientList.get(i);
			if (!client.equals(lookingClient))
			{
				if (client.getName().equals(nameToFindIn))
				{
					System.out.println(client.getName() + " " + lookingClient.getName());
					return client;
				}
			}
			
		}
		
		return null;
	}
	
	public boolean sendMessageRequest(String clientToFind, ThreadedClient lookingClient)
	{
		ThreadedClient foundClient = findFriend(clientToFind, lookingClient);
		if (foundClient == null)
		{
			return false;
		}
		else return foundClient.sendMessagePermissionRequest(foundClient, lookingClient);
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
	
	public boolean SendMessageToConnectedUser(ThreadedClient clientIn, String message)
	{
		
		if (clientIn == null)
		{
			return false;
		}
		else
		{
			clientIn.sendUserMessage("\t\t\t\t" + message);
			return true;
		}
	}
	
}
