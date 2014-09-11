package ChatApp.Client;


import java.io.IOException;

import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.LinkedList;

import ChatApp.Common.*;


public class ChatAppClient implements Runnable {

	// The client socket
	private Socket clientSocket = null;
	
	// The output stream
	private ObjectOutputStream oos = null;
	private HashMap<String, Peer> peerMap;
	private ClientGUI clientGUI = null;
	private LinkedList<String> msgQueue;
	
	
	public ChatAppClient(ClientGUI cGUI, HashMap<String, Peer> pMap, LinkedList<String> msgQ) {
		
		clientGUI = cGUI;
		peerMap = pMap;
		msgQueue = msgQ;
	}
	/**
	 * Starts client code here to connect to server
	 * @param pgl Client GUI that called init
	 */
	public void run(){
		
		// The default port.
		int portNumber = 2222;
		// The default host.
		//dempster.cs.uchicago.edu
		String host = "localhost";

		/*
		 * Open a socket on a given host and port. Open input and output
		 * streams.
		 */
		try {
			clientSocket = new Socket(host, portNumber);
			oos = new ObjectOutputStream(clientSocket.getOutputStream());
					

		} catch (final UnknownHostException e) {
			System.err.println("Don't know about host " + host);
		} 
		catch (final IOException e) {
			System.err
					.println("Couldn't get I/O for the connection to the host "
							+ host);
		}
		

		/*
		 * If everything has been initialized then we want to write some data to
		 * the socket we have opened a connection to on the port portNumber.
		 */
		if (clientSocket != null && oos != null) {
			try {

				/* Create a thread to read from the server. */
				new Thread(new ClientReader(clientSocket, clientGUI, peerMap)).start();
				
				Message msg = null;
				String outbound = null;
				//current thread sends data to server
				while (true) {
					Thread.sleep(100);
					//notify server if quitting
					synchronized(msgQueue){
						outbound = null;
						if(!msgQueue.isEmpty()){
							outbound = msgQueue.removeFirst();
						}
						
					}
					
					
					if(outbound != null){
						//System.out.println(peerMap.keySet());
						msg = parseMessage(outbound);
						oos.writeObject(msg);
						oos.flush();
						
						if(msg.equals("/quit")){
							break;
						}
					}
					
				}
				/*
				 * Close the output stream, close the input stream, close the
				 * socket.
				 */
				
				oos.close();
				clientSocket.close();
			} catch (final IOException | InterruptedException e) {
				System.err.println("IOException:  " + e);
			}
		}
	}
	
	public Message parseMessage(String message){
		String sender = clientSocket.getLocalSocketAddress().toString();
		String receiver;
		String body = message;
		if(message.indexOf('@') == 0 && peerMap.containsKey(message.substring(1, message.indexOf(' ')))){
			receiver = message.substring(1, message.indexOf(' '));
			body = message.substring(message.indexOf(' ')+1);
		}
		else if(message.equals("/quit")){
			receiver = "Server";
		}
		else{
			receiver = "Broadcast";
		}
		return new Message(sender, receiver, body);
	}

	
	
	public LinkedList<String> getMsgQueue(){
		return msgQueue;
	}
	
	
	

}