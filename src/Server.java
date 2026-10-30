import java.io.DataOutputStream;
import java.net.*;
import java.util.ArrayList;

public class Server {
	
	/**
	 * @param args
	 */
	public static void main(String[] args)throws Exception {
		int numberOfPlayers = 0;
		int maxAntalSpillere = 4;
		common c = new common("eksempel");
		ArrayList<Player> players = new ArrayList<>();
		ArrayList<DataOutputStream> clients = new ArrayList<>();
		ServerSocket welcomeSocket = new ServerSocket(8810);
		while (true) {
			if (numberOfPlayers < maxAntalSpillere ){
				Socket connectionSocket = welcomeSocket.accept();
				(new ServerThread(connectionSocket,c,players,clients)).start();
				numberOfPlayers++;
			}
		}
	}
}
