import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;



public class GameLogic {
	
	public static Player makePlayers(String name, List<Player> players) {
		pair p=getRandomFreePosition(players);
		Player me = new Player(name,p,"up");
		players.add(me);
		return me;
	}
	
	public static pair getRandomFreePosition(List<Player> players)
	// finds a random new position which is not wall 
	// and not occupied by other players 
	{
		int x = 1;
		int y = 1;
		boolean foundfreepos = false;
		while  (!foundfreepos) {
			Random r = new Random();
			x = Math.abs(r.nextInt()%18) +1;
			y = Math.abs(r.nextInt()%18) +1;
			if (Generel.board[y].charAt(x)==' ') // er det gulv ?
			{
				foundfreepos = true;
				for (Player p: players) {
					if (p.getXpos()==x && p.getYpos()==y) //pladsen optaget af en anden 
						foundfreepos = false;
				}
				
			}
		}
		pair p = new pair(x,y);
		return p;
	}
	
	public static void updatePlayer(List<Player> players,Player me,int delta_x, int delta_y, String direction) {
		me.direction = direction;
		int x = me.getXpos(),y = me.getYpos();

		if (Generel.board[y+delta_y].charAt(x+delta_x)=='w') {
			me.addPoints(-1);
		} 
		else {
			// collision detection
			Player p = getPlayerAt(players,x+delta_x,y+delta_y);
			if (p!=null && p != me) {
              me.addPoints(10);
              //update the other player
              p.addPoints(-10);
              pair pa = getRandomFreePosition(players);
              p.setLocation(pa);
			} else {
				me.addPoints(1);
			}
			pair oldpos = me.getLocation();
			pair newpos = new pair(x+delta_x,y+delta_y);
			me.setLocation(newpos);
		}
		
		
	}
	
	public static Player getPlayerAt(List<Player> players,int x, int y) {
		for (Player p : players) {
			if (p.getXpos()==x && p.getYpos()==y) {
				return p;
			}
		}
		return null;
	}

	public static void sendSpillere(ArrayList<Player> players, DataOutputStream outstream) throws IOException {
		String besked = "";
		for (Player p : players) {
			besked +=  p.getName() + "#" + p.getXpos() + "#" + p.getYpos() + "#" + p.getDirection() + "#" + p.getPoint() + ";";
		}
		outstream.writeBytes(besked + "\n");
	}


}
