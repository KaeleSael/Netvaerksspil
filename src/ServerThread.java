import java.net.*;
import java.io.*;
import java.util.ArrayList;

public class ServerThread extends Thread{
	private final Socket connSocket;
	private final ArrayList<Player> players;
	private final ArrayList<DataOutputStream> clients;
	private Player me;
	private DataOutputStream outToClient;

	public ServerThread(Socket connSocket, ArrayList<Player> players, ArrayList<DataOutputStream> clients) {
		this.connSocket = connSocket;
		this.players = players;
        this.clients = clients;
    }

    @Override
    public void run() {
        try {
            BufferedReader inFromClient = new BufferedReader(new InputStreamReader(connSocket.getInputStream()));
            outToClient = new DataOutputStream(connSocket.getOutputStream());

            String playerName = inFromClient.readLine();
            if (playerName == null || playerName.isBlank()) {
                System.out.println("Skriv et navn! Kør programmet igen.");
                return;
            }

            synchronized (players) {
                pair position = GameLogic.getRandomFreePosition(players);
                me = new Player(playerName, position, "up");
                players.add(me);
                System.out.println(playerName + " er tilsluttet");
                synchronized (clients) {
                    clients.add(outToClient);
                }
                sendPlayersToAll();
            }

            String clientSentence;
            while ((clientSentence = inFromClient.readLine()) != null) {
                System.out.println(playerName + " sendte: " + clientSentence);
                int dx = 0;
                int dy = 0;
                boolean validMove = true;

                switch (clientSentence.toLowerCase()) {
                    case "up":
                        dy = -1;
                        break;
                    case "down":
                        dy = 1;
                        break;
                    case "left":
                        dx = -1;
                        break;
                    case "right":
                        dx = 1;
                        break;
                    default:
                        validMove = false;
                        break;
                }

                if (validMove) {
                    synchronized (players) {
                        GameLogic.updatePlayer(
                                players, me, dx, dy,
                                clientSentence.toLowerCase());

                        sendPlayersToAll();
                    }
                }
            }

        } catch (IOException e) {
            System.out.println("Forbindelsen blev afbrudt.");
        } finally {
            synchronized (players) {
                if (me != null) {
                    players.remove(me);
                    System.out.println(me.getName() + " er afbrudt.");
                }
                synchronized (clients) {
                    clients.remove(outToClient);
                }
            }
            try {
                sendPlayersToAll();
            } catch (IOException e) {
            }
        }
        try {
            connSocket.close();
        } catch (IOException e) {

		}
	}
	private synchronized void sendPlayersToAll() throws IOException {
		for (int i = clients.size() - 1; i >= 0; i--) {
			DataOutputStream out = clients.get(i);

			try {
				GameLogic.sendSpillere(players, out);
			} catch (IOException e) {
				clients.remove(i);
			}
		}
	}
}