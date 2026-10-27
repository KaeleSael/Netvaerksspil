import java.io.DataOutputStream;
import java.net.*;
import java.util.ArrayList;

public class Server {
	/**
	 * @param args
	 */
	public static void main(String[] args)throws Exception {
		common c = new common("eksempel");
		ArrayList<Player> players = new ArrayList<>();
		ArrayList<DataOutputStream> clients = new ArrayList<>();
		ServerSocket welcomeSocket = new ServerSocket(8810);
		while (true) {
			Socket connectionSocket = welcomeSocket.accept();
			new ServerThread(connectionSocket,c, players, clients).start();
		}
	}

}
