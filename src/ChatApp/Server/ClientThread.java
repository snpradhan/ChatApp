package ChatApp.Server;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.HashMap;
import ChatApp.Common.*;

public class ClientThread extends Thread {

	private Socket clientSocket = null;
	private ObjectInputStream ois = null;
	private ObjectOutputStream oos = null;
	private final HashMap<String, ClientThread> clientMap;
	private String name;
	private String ip;
	private final int maxClients;

	public ClientThread(Socket clientSocket, HashMap<String, ClientThread> clientMap, int maxClients) {
		this.clientSocket = clientSocket;
		this.clientMap = clientMap;
		this.ip = clientSocket.getRemoteSocketAddress().toString();
		this.maxClients = maxClients;

		try {
			//input output streams
			oos = new ObjectOutputStream(clientSocket.getOutputStream());
			ois = new ObjectInputStream(clientSocket.getInputStream());

		} catch (final IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void run() {

		synchronized (clientMap) {
			//add the thread to the list
			clientMap.put(ip, this);
			System.out.println("Client size: " + clientMap.size());
		}
		try {
			
			//if server threshold reached boot client
			if (clientMap.size() > maxClients) {
				synchronized (clientMap) {
					clientMap.remove(ip);
				}
				oos.writeObject(new Message("Server", ip, "Server is full *** Bye"));
				oos.flush();
			} 
			else {
				
				Object obj = ois.readObject();
				if (obj instanceof Message) {
					name = ((Message) obj).getBody();
				} else {
					name = "Anonymous";
				}
				//check if name already exists
				int n = 0;
				while(true){
					for(ClientThread ct : clientMap.values()){
						if(!ct.getIp().equals(ip) && ct.getClientName().equals(name)){
							n = 1;
							break;
						}
					}
					//name used
					if(n==1){
						oos.writeObject(new String("Username " + name	+ " is used. Enter a different username"));
						oos.flush();
						obj = ois.readObject();
						if (obj instanceof Message) {
							name = ((Message) obj).getBody();
						} else {
							name = "Anonymous";
						}
						n=0;
					}
					else{
						break;
					}
				}
				oos.writeObject(new String("Hello " + name	+ " welcome to ChatApp."));
				oos.flush();

				Peer p = new Peer(name, ip, "S");
				oos.writeObject(p);
				oos.flush();

				p = new Peer(name, ip, "J");
				// notify all peers a new user has joined
				synchronized (clientMap) {

					for (ClientThread ct : clientMap.values()) {
						if (ct != this) {
							// notify peer about me
							ct.getOos().writeObject(p);
							ct.getOos().flush();
							// notify my client about my peer
							oos.writeObject(new Peer(ct.name, ct.ip, "J"));
							oos.flush();
						}
					}
				}

				// conversation
				Message msg;
				while (true) {
					obj = ois.readObject();
					if (obj instanceof Message) {
						msg = (Message) obj;
						// client was to quit
						if (msg.getReceiver().equals("Server")
								&& msg.getBody().equals("/quit")) {
							break;
						}
						// message for all peers
						else if (msg.getReceiver().equals("Broadcast")) {
							System.out.println(msg);
							synchronized (clientMap) {
								for (ClientThread ct : clientMap.values()) {
									ct.getOos().writeObject(obj);
									ct.getOos().flush();
								}
							}
						}
						// private message for only one peer
						else {
							synchronized (clientMap) {
								for (ClientThread ct : clientMap.values()) {
									if (ct.getIp().equals(msg.getReceiver())) {
										ct.getOos().writeObject(obj);
										ct.getOos().flush();
										break;
									}
								}
								oos.writeObject(obj);
								oos.flush();

							}
						}
					}

				}
				// notify all peers a user is leaving
				p = new Peer(name, ip, "L");
				synchronized (clientMap) {
					for (ClientThread ct : clientMap.values()) {
						if (ct != this) {
							ct.getOos().writeObject(p);
							ct.getOos().flush();
						}
					}

					clientMap.remove(ip);
				}
				System.out.println(p.toString() + " is leaving");

				oos.writeObject(new Message("Server", ip, "*** Bye"));
				oos.flush();
			}
			/*
			 * Clean up. Set the current thread variable to null so that a new
			 * client could be accepted by the server.
			 */
			// remove this thread from the map

			/*
			 * Close the output stream, close the input stream, close the
			 * socket.
			 */

			ois.close();
			oos.close();
			clientSocket.close();
			synchronized (clientMap) {
				System.out.println("Client size: " + clientMap.size());
			}
		} catch (ClassNotFoundException e) {
		} catch (IOException e) {

		}
	}

	public String getClientName() {
		return name;
	}

	public String getIp() {
		return ip;
	}

	public ObjectInputStream getOis() {
		return ois;
	}

	public void setOis(ObjectInputStream ois) {
		this.ois = ois;
	}

	public ObjectOutputStream getOos() {
		return oos;
	}

	public void setOos(ObjectOutputStream oos) {
		this.oos = oos;
	}
}
