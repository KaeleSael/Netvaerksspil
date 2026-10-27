import java.net.*;
public class Server {
	
	/**
	 * @param args
	 */
	public static void main(String[] args)throws Exception {
		common c = new common("eksempel");
		ServerSocket welcomeSocket = new ServerSocket(8810);
		while (true) {
			if (numberOfPlayers < maxAntalSpillere ){
				Socket connectionSocket = welcomeSocket.accept();
				(new ServerThread(connectionSocket,c)).start();
				numberOfPlayers++;
			}
		}
	}

}
