package mypackage;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Random;
import javax.swing.ImageIcon;

public class Player implements Serializable {
	int id;
	int score;
	int x;
	int y;
	String name;
	Board board;
	Weapon bow;
	Weapon pistol;
	Weapon sword; 
	Player opponent;
	ImageIcon playerIcon;

	// Empty constructor.
	public Player() {
		x  = 0; 
		y = 0;
		id = 0;
		score = 0;
		name = "";
	}
	
	// Constructor using fields.
	public Player(int id, int score, int x, int y, String name, Board board, ImageIcon playerIcon) {
		this.id = id;
		this.score = score;
		this.x = x;
		this.y = y;
		this.name = name;
		this.board = board;
		this.bow = null;
		this.pistol = null;
		this.sword = null;
		this.playerIcon = playerIcon;
	}
	
	// Constructor using an object as argument.
	public Player(Player player) {
		this(player.id, player.score, player.x, player.y, player.name, player.board, player.playerIcon);
		this.opponent = player.opponent;
		this.pistol = player.pistol;
		this.bow = player.bow;
		this.sword = player.sword;
	}
	
	// Constructor only needing new position(for MinMax use)
	public Player(int x, int y, Board board){
		this.x = x;
		this.y = y;
		this.board = new Board(board);
	}
	
  // Makes a deep (and not a shallow) copy of a player.
  // Used to create deep copies during the making of a MinMaxPlayer's decision tree.
  public static Object deepCopy(Object object) {
    try {
      ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
      ObjectOutputStream outputStrm = new ObjectOutputStream(outputStream);
      outputStrm.writeObject(object);
      ByteArrayInputStream inputStream = new ByteArrayInputStream(outputStream.toByteArray());
      ObjectInputStream objInputStream = new ObjectInputStream(inputStream);

      return objInputStream.readObject();
    } catch (Exception e) {
      e.printStackTrace();
      return null;
    }
	}

	// Getters.
	public int getId() {return id;}
	public int getScore() {return score;}
	public int getX() {return x;}
	public int getY() {return y;}
	public String getName() {return name;}
	public Board getBoard() {return board;}
	public Weapon getBow() {return bow;}
	public Weapon getPistol() {return pistol;}
	public Weapon getSword() {return sword;}
	public Player getOpp() {return opponent;}
	public ImageIcon getPLayerIcon() {return playerIcon;}
	
	// Setters.
	public void setId(int id) {this.id = id;}
	public void setScore(int score) {this.score = score;}
	public void setX(int x) {this.x = x;}
	public void setY(int y) {this.y = y;}
	public void setName(String name) {this.name = name;}
	public void setBoard(Board board) {this.board = board;}
	public void setBow(Weapon bow) {this.bow = bow;}
	public void setPistol(Weapon pistol) {this.pistol = pistol;}
	public void setSword(Weapon sword) {this.sword = sword;}
	public void setOpp(Player opponent) {this.opponent = opponent;}
	public void setPlayerIcon(ImageIcon playerIcon) {this.playerIcon = playerIcon;}
	
	/////////////////////////////////////////////////////////////////////
	
  // Calculates distance between 2 players.
	public float playersDistance(Player p) {	
		double square = Math.pow((board.x2i(this.x) - board.x2i(p.x)), 2) + Math.pow((board.y2j(this.y) - board.y2j(p.y)),2);
			return (float)Math.sqrt(square);
	} 
	
  // Calculates distance between a player and a tile.
	public float distanceFromTile(int x, int y) {
		double square = Math.pow((board.x2i(this.x) - board.x2i(x)), 2) + Math.pow((board.y2j(this.y) - board.y2j(y)),2);
		return (float)Math.sqrt(square);
	}

  // Calculates distance between 2 points.
  public double pointDistance(int x1, int y1, int x2, int y2) {
    return Math.sqrt((double)((x1-x2)*(x1-x2)+(y1-y2)*(y1-y2)) ); 
  }
	
	// Function linking dice values with changes in coordinates.
	public int[] makeMove(int dice) {
		int checkX = 0, checkY = 0;

		switch (dice) {
		// Make the necessary changes in coordinates regarding each value.
		case 1: 
			checkY = getY() - 1;
			checkX = getX();
			break;
		case 2: 
			checkY = getY() - 1;
			checkX = getX() + 1;
			break;
		case 3: 
			checkY = getY();
			checkX = getX() + 1;
			break;
		case 4: 
			checkY = getY() + 1 ;
			checkX = getX() + 1;
			break;
		case 5: 
			checkY = getY() + 1;
			checkX = getX();
			break;
		case 6: 
			checkY = getY() + 1;
			checkX = getX() - 1;
			break;
		case 7:
			checkY = getY();
			checkX = getX() - 1;
			break;
		case 8: 
			checkY = getY() - 1;
			checkX = getX() - 1;
			break;
		default:
			System.out.println("How did you stumble upon this wrecked land?");
		}
		int[] newPosition = new int[2];
		
		//Check if the new coordinates allow the player to stay on the board.
		//Else "0,0" is returned as an error code for further notice.
		if(Math.abs(checkX) > (board.getN() / 2) || Math.abs(checkY) > (board.getN() / 2)) {
			newPosition[0] = 0;
			newPosition[1] = 0;
			return newPosition;
		}
		
		// Final check to eliminate any chance for the players to move to zero.
		if(checkX > getX() && checkX == 0)
			newPosition[0] = 1;
		else if(checkX < getX() && checkX == 0)
			newPosition[0] = -1;
		else 
			newPosition[0] = checkX;
		
		if(checkY > getY() && checkY == 0)
			newPosition[1] = 1;
		else if(checkY < getY() && checkY == 0)
			newPosition[1] = -1;
		else 
			newPosition[1] = checkY;
		
		return newPosition;
}
 	
	// Function to generate a random move.
	public int[] getRandomMove() {
		Random rand = new Random();
		int[] newMove = new int[2];
		
		do {
			int dice = rand.nextInt(7) + 1; //Generate a random value between 1 and 8.
			newMove = makeMove(dice);
    }
		while(newMove[0] == 0 && newMove[1] == 0);
		// Check if the new coordinates allow the player to stay on the board.
			
		return newMove;
	}
	
  // Makes the move that is assesed as "the best". Then makes an Array of useful pieces of
  // information about the best dice, weapons picked up, food, traps and points.
	public int[] move(int[] newMove) {
		// Move the player where the "dice" dictates.
		setX(newMove[0]);
		setY(newMove[1]);
		
		int[] report = new int[5];
		report[0] = getX();
		report[1] = getY();
		
		int hasWeapon = 0;
		for(int i = 0 ; i < board.getW() ; i++) {
			if(x == board.weapons[i].getX() && y == board.weapons[i].getY() && id == board.weapons[i].getPlayerId()) {
				hasWeapon = 1;

				// Checks if the square location matches any of the locations in the board of weapons. 
				if(board.weapons[i].getType() == "pistol") 
					this.pistol = board.weapons[i];
				else if(board.weapons[i].getType() == "bow")
					this.bow = board.weapons[i];
				else 
					this.sword = board.weapons[i];
				
				// Clear the weapon off the board.
				board.weapons[i].setY(100);
				board.weapons[i].setX(100);
				break;
			}
			
		}
		report[2] = hasWeapon;
		
		int hasFood = 0;
		for(int i = 0 ; i < board.getF() ; i++) {
			if(x == board.food[i].getX() && y == board.food[i].getY()) {
				hasFood = 1; 

				// Checks if the square location matches any of the locations in the board of food.
				setScore( score+ board.food[i].getPoints());
				
				// Clear the food off the board.
				board.food[i].setX(100);
				board.food[i].setY(100);
				break;
			}
			
		}
		report[3] = hasFood;
		
		int hasTrap = 0;
		for(int i = 0 ; i < board.getT() ; i++) {
			if(x == board.traps[i].getX() && y == board.traps[i].getY()) {
				hasTrap = 1;

				// Checks if the square location matches any of the locations in the board of traps.
				System.out.println(getName() + " has stumbled upon trap " + board.traps[i].getId() + ".");
				if((this.bow != null && board.traps[i].getType() == "animal") 
          || (this.sword != null && board.traps[i].getType() == "ropes"))
					System.out.println(this.name + " has escaped the trap.");
				else {
					setScore( score+ board.traps[i].getPoints());
				}	
				//System.out.println("New score: " + score);
				break;
			}
		}
		report[4] = hasTrap;
		
		return report;
	}
}