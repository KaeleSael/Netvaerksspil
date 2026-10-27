import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ClientThread extends Thread {
    private final BufferedReader inFromServer;

    public ClientThread(BufferedReader inFromServer) {
        this.inFromServer = inFromServer;
    }

    @Override
    public void run() {
        try {
            String message;

            while ((message = inFromServer.readLine()) != null) {
                System.out.println("Modtaget fra server: [" + message + "]");
                List<Player> updatedPlayers = new ArrayList<>();

                String[] playerEntries = message.split(";");

                for (String entry : playerEntries) {
                    if (entry.isBlank()) {
                        continue;
                    }

                    String[] data = entry.split("#");

                    if (data.length != 5) {
                        continue;
                    }

                    String name = data[0];
                    int x = Integer.parseInt(data[1]);
                    int y = Integer.parseInt(data[2]);
                    String direction = data[3];
                    int points = Integer.parseInt(data[4]);

                    Player player = new Player(
                            name,
                            new pair(x, y),
                            direction
                    );

                    player.addPoints(points);
                    updatedPlayers.add(player);
                }

                // Opdater GUI'en med serverens data
                Gui.updateFromServer(updatedPlayers);

                // Find denne klients egen spiller
                for (Player p : updatedPlayers) {
                    if (p.getName().equals(App.playerName)) {
                        App.me = p;
                        break;
                    }
                }
            }
        } catch (IOException | NumberFormatException e) {
            System.out.println("Forbindelsen til serveren blev afbrudt.");
        }
    }
}
