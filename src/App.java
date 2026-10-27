import java.io.*;
import java.net.Socket;

import javafx.application.Application;

public class App {
	public static Player me=null;
	public static String playerName;
	public static BufferedReader inFromServer;
	public static DataOutputStream outToServer;
	public static void main(String[] args) throws Exception{
		BufferedReader inFromUser = new BufferedReader(new InputStreamReader(System.in));
		System.out.println("Indtast spillernavn");
		String navn = inFromUser.readLine();
		playerName = navn;
		Socket clientSocket= new Socket("10.10.139.11",8810);
		outToServer = new DataOutputStream(clientSocket.getOutputStream());
		inFromServer = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
		outToServer.writeBytes(navn + "\n");
		Application.launch(Gui.class);
	}
}