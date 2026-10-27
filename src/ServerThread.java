import java.net.*;
import java.io.*;
public class ServerThread extends Thread{
	Socket connSocket;
	common c;
	
	public ServerThread(Socket connSocket,common c) {
		this.connSocket = connSocket;
		this.c=c; // Til Web-server opgaven skal denne ikke anvendes
	}
	public void run() {
		try {
			BufferedReader inFromClient = new BufferedReader(new InputStreamReader(connSocket.getInputStream()));
			DataOutputStream outToClient = new DataOutputStream(connSocket.getOutputStream());
			
			// Do the work and the communication with the client here	
			// The following two lines are only an example

			while(true) {
				String clientSentence = inFromClient.readLine();
				System.out.println(clientSentence);
			}

			//outToClient.writeBytes("Hej"+ c.getTekst() + '\n' );
		
		} catch (IOException e) {
			e.printStackTrace();
		}		
		// do the work here
	}
}
