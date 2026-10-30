import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.*;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.text.*;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.List;

public class Gui extends Application {

	public static final int size = 30; 
	public static final int scene_height = size * 20 + 50;
	public static final int scene_width = size * 20 + 200;

	public static Image image_floor;
	public static Image image_wall;
	public static Image hero_right,hero_left,hero_up,hero_down;

	private static Label[][] fields;
	private static TextArea scoreList;

	
	// -------------------------------------------
	// | Maze: (0,0)              | Score: (1,0) |
	// |-----------------------------------------|
	// | boardGrid (0,1)          | scorelist    |
	// |                          | (1,1)        |
	// -------------------------------------------

	@Override
	public void start(Stage primaryStage) {
		try {
			GridPane grid = new GridPane();
			grid.setHgap(10);
			grid.setVgap(10);
			grid.setPadding(new Insets(0, 10, 0, 10));

			Text mazeLabel = new Text("Maze:");
			mazeLabel.setFont(Font.font("Arial", FontWeight.BOLD, 20));
	
			Text scoreLabel = new Text("Score:");
			scoreLabel.setFont(Font.font("Arial", FontWeight.BOLD, 20));

			scoreList = new TextArea();

			GridPane boardGrid = new GridPane();

			image_wall  = new Image(getClass().getResourceAsStream("Image/wall4.png"),size,size,false,false);
			image_floor = new Image(getClass().getResourceAsStream("Image/floor1.png"),size,size,false,false);

			hero_right  = new Image(getClass().getResourceAsStream("Image/heroRight.png"),size,size,false,false);
			hero_left   = new Image(getClass().getResourceAsStream("Image/heroLeft.png"),size,size,false,false);
			hero_up     = new Image(getClass().getResourceAsStream("Image/heroUp.png"),size,size,false,false);
			hero_down   = new Image(getClass().getResourceAsStream("Image/heroDown.png"),size,size,false,false);

			fields = new Label[20][20];
			for (int j=0; j<20; j++) {
				for (int i=0; i<20; i++) {
					switch (Generel.board[j].charAt(i)) {
					case 'w':
						fields[i][j] = new Label("", new ImageView(image_wall));
						break;
					case ' ':					
						fields[i][j] = new Label("", new ImageView(image_floor));
						break;
					default: throw new Exception("Illegal field value: "+Generel.board[j].charAt(i) );
					}
					boardGrid.add(fields[i][j], i, j);
				}
			}
			scoreList.setEditable(false);
			
			
			grid.add(mazeLabel,  0, 0); 
			grid.add(scoreLabel, 1, 0); 
			grid.add(boardGrid,  0, 1);
			grid.add(scoreList,  1, 1);
						
			Scene scene = new Scene(grid,scene_width,scene_height);
			primaryStage.setScene(scene);
			primaryStage.show();

			scene.addEventFilter(KeyEvent.KEY_PRESSED, event -> {
				switch (event.getCode()) {
				case UP:
					try { playerMoved(0,-1,"up");
                    } catch (IOException e) { throw new RuntimeException(e); }
                    break;

				case DOWN:
                    try { playerMoved(0,+1,"down");
                    } catch (IOException e) { throw new RuntimeException(e); }
                    break;

				case LEFT:
                    try { playerMoved(-1,0,"left"); }
					catch (IOException e) { throw new RuntimeException(e); }
                    break;

				case RIGHT:
                    try { playerMoved(+1,0,"right"); }
					catch (IOException e) { throw new RuntimeException(e); }
                    break;

				case SPACE:
					try {
						playerShot("space");
					} catch (IOException e) {
						throw new RuntimeException(e);
					}


				case ESCAPE:System.exit(0); 
				default: break;
				}
			});
			
            // Putting default players on screen
			scoreList.setText("");
			new ClientThread(App.inFromServer).start();
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void removePlayerOnScreen(pair oldpos) {
		Platform.runLater(() -> {
			fields[oldpos.getX()][oldpos.getY()].setGraphic(new ImageView(image_floor));
			});
	}
	
	public static void placePlayerOnScreen(pair newpos,String direction) {
		Platform.runLater(() -> {
			int newx = newpos.getX();
			int newy = newpos.getY();
			if (direction.equals("right")) {
				fields[newx][newy].setGraphic(new ImageView(hero_right));
			};
			if (direction.equals("left")) {
				fields[newx][newy].setGraphic(new ImageView(hero_left));
			};
			if (direction.equals("up")) {
				fields[newx][newy].setGraphic(new ImageView(hero_up));
			};
			if (direction.equals("down")) {
				fields[newx][newy].setGraphic(new ImageView(hero_down));
			};
			});
	}
	
	public static void movePlayerOnScreen(pair oldpos, pair newpos, String direction)
	{
		removePlayerOnScreen(oldpos);
		placePlayerOnScreen(newpos,direction);
	}

	
	public void updateScoreTable(List<Player> players)
	{
		Platform.runLater(() -> {
			scoreList.setText(getScoreList(players));
			});
	}

	public void playerMoved(int delta_x, int delta_y, String direction) throws IOException {
		App.outToServer.writeBytes( direction + '\n');

	public void playerShot(String button) throws IOException {
		App.outToServer.writeBytes(button + '\n');
	}
	
	public static String getScoreList(List<Player> players) {
		StringBuffer b = new StringBuffer(100);
		for (Player p : players) {
			b.append(p+"\r\n");
		}
		return b.toString();
	}

	public static void updateFromServer(List<Player> updatedPlayers) {
		Platform.runLater(() -> {

			for (int y = 0; y < 20; y++) {
				for (int x = 0; x < 20; x++) {
					if (Generel.board[y].charAt(x) == ' ') {
						fields[x][y].setGraphic(
								new ImageView(image_floor)
						);
					}
				}
			}
			for (Player p : updatedPlayers) {
				int x = p.getXpos();
				int y = p.getYpos();

				Image image = hero_up;

				switch (p.getDirection()) {
					case "right": image = hero_right; break;
					case "left": image = hero_left; break;
					case "down": image = hero_down; break;
					case "up": image = hero_up; break;
				}

				fields[x][y].setGraphic(new ImageView(image));
			}
			scoreList.setText(getScoreList(updatedPlayers));
		});
	}

	public static void showWinner(String name) {
		Platform.runLater(() -> {
			Alert alert = new Alert(Alert.AlertType.INFORMATION);
			alert.setTitle("WINNER WINNER CHICKEN DINNER!!");
			alert.setHeaderText("Tillykke!");
			alert.setContentText(name + " har nået 150 point!");
			alert.show();
		});
	}
}

