package ChatApp.Server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;

/*
 * A chat server that delivers public and private messages.
 */
public class ChatAppServer {

	// The client socket.
	private static Socket clientSocket = null;
	// This chat server can accept up to maxClientsCount clients' connections.
	private static final int maxClientsCount = 10;

	// The server socket.
	private static ServerSocket serverSocket = null;
	private static HashMap<String, ClientThread> clientMap = new HashMap<String, ClientThread>();
	
	public static void main(String args[]) {

		// The default port number.
		int portNumber = 2222;
		
		try {
			serverSocket = new ServerSocket(portNumber);
			System.out.println("Server ready at port "+portNumber);
		} catch (final IOException e) {
			System.out.println(e);
		}

		/*
		 * Create a client socket for each connection and pass it to a new
		 * client thread.
		 */
		while (true) {
			try {
				clientSocket = serverSocket.accept();
				new ClientThread(clientSocket, clientMap, maxClientsCount).start();


			} catch (final IOException e) {
				System.out.println(e);
			}
		}
	}
}
