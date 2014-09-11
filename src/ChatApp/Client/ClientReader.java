package ChatApp.Client;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.Socket;
import java.util.HashMap;

import ChatApp.Common.Message;
import ChatApp.Common.Peer;

public class ClientReader implements Runnable {
	
	private Socket clientSocket;
	// The input stream
	private ObjectInputStream ois = null;
	private ClientGUI clientGUI;
	private HashMap<String, Peer> peerMap;
	private Peer self;
	
	public ClientReader(Socket cSocket, ClientGUI cGUI, HashMap<String, Peer> pMap){
		clientSocket = cSocket;
		clientGUI = cGUI;
		peerMap = pMap;
		
		try {
			ois = new ObjectInputStream(clientSocket.getInputStream());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	/*
	 * Create a thread to read from the server.
	 * Updates the peer list from the server
	 * 
	 * @see java.lang.Runnable#run()
	 */
	@Override
	public void run() {
		/*
		 * Keep on reading from the socket till we receive "Bye" from the
		 * server. Once we received that then we want to break.
		 */
		Peer p = null;
		Message msg = null;
		String ip;
		Object obj;
		try {
			while(true){
				obj = ois.readObject();
				if(obj instanceof String){
					System.out.println((String) obj);
					clientGUI.setInboundText((String)obj);
				}
				else if(obj instanceof Message){
					msg = (Message) obj;
					if(msg.getReceiver().equals("Broadcast")){
						if(msg.getSender().equals(self.getIp())){
							clientGUI.setInboundText("<"+self.getName()+"> "+msg.getBody());
						}
						else{
							p = peerMap.get(msg.getSender());
							clientGUI.setInboundText("<"+p.getName()+"> "+msg.getBody());
						}
					}
					else if (msg.getSender().equals("Server") && msg.getBody().indexOf("*** Bye") != -1) {
						clientGUI.setInboundText("<Server> "+msg.getBody());
						break;
					}
					else{
						clientGUI.setPrivateMsg(msg);
					}
					
							
				}
				else if(obj instanceof Peer)
				{
					p = (Peer) obj;
					ip = p.getIp().trim();
					if (p.getStatus().equals("J") && !peerMap.containsKey(ip)) {
						peerMap.put(ip, p);
						clientGUI.refreshPeerList();
					} else if (p.getStatus().equals("L") && peerMap.containsKey(ip)) {
						System.out.println(p);
						peerMap.remove(ip);
						clientGUI.refreshPeerList();
						clientGUI.notifyPrivateGUI(ip);
						
					}
					else{
						self = p;
						clientGUI.setSelf(self);
						
					}
					
					
				}
				else{
					System.out.println(obj);
				}

			}
			Thread.sleep(2000);
			clientGUI.close();
		
			
		} catch (ClassNotFoundException e) {
			System.err.println("Class Exception:  " + e);
		}
		catch (IOException e){
			System.err.println("IOException:  " + e);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
