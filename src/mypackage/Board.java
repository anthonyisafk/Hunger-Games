package mypackage;

import mypackage.Weapon;
import mypackage.Food;
import mypackage.Trap;
import java.io.Serializable;
import java.util.Arrays;
import java.util.Random;
import javax.swing.ImageIcon;

public class Board implements Serializable{
	int N, M;
	int W, F, T;
	int[][] weaponAreaLimits;
	int[][] foodAreaLimits;
	int[][] trapAreaLimits;
	
	Weapon[] weapons;
	Food[] food;
	Trap[] traps;
	
// Blank constructor.
	Board(){
		N = M = 20;
		W = 6;
		F = 10;
		T = 8;
	}
	
// Constructor using fields.		
	Board(int N, int M, int W, int F, int T, int[][] weaponAreaLimits, int[][] foodAreaLimits, int[][] trapAreaLimits){
		this.N = N; 
		this.M = M;
		this.W = W ;
		this.F = F;
		this.T = T;
		this.weaponAreaLimits = weaponAreaLimits;
		this.foodAreaLimits = foodAreaLimits;
		this.trapAreaLimits = trapAreaLimits;
	}
	
// Constructor using an object as argument.		
	Board(Board board){
		this(board.getN(), board.getM(), board.getW(), board.getF() , board.getT(),
      board.weaponAreaLimits, board.foodAreaLimits, board.trapAreaLimits);
	}
		
	// Getters.
	public int getN() {return N;}
	public int getM() {return M;}
	public int getW() {return W;}
	public int getF() {return F;}
	public int getT() {return T;}
	public Weapon[] getWeapons() {return weapons;}
	public Food[] getFood() {return food;}
	public Trap[] getTraps() {return traps;}
	public int[][] getWeaponAreaLimits() {return weaponAreaLimits;} 
	public int[][] getFoodAreaLimits() {return foodAreaLimits;}
	public int[][] getTrapAreaLimits() {return trapAreaLimits;}
	
	// Setters.
	public void setN(int n) {N = n;}
	public void setM(int m) {M = m;}
	public void setW(int w) {W = w;}
	public void setF(int f) {F = f;	}
	public void setT(int t) {T = t;}
	public void setWeaponAreaLimits(int[][] weaponAreaLimits) {this.weaponAreaLimits = weaponAreaLimits;}
	public void setFoodAreaLimits(int[][] foodAreaLimits) {this.foodAreaLimits = foodAreaLimits;}
	public void setTrapAreaLimits(int[][] trapAreaLimits) {this.trapAreaLimits = trapAreaLimits;}
	public void setWeapons(Weapon[] weapons) {this.weapons = weapons;}
	public void setFood(Food[] food) {this.food = food;}
	public void setTraps(Trap[] traps) {this.traps = traps;}

///////////////////////////////////////////////////////////////////////////
	
	// Functions taken from recommendation. Will help with the heuristic player.
	public int x2i(int x) {
		if(x > 0) 
			return x + N/2 - 1;
		else
			return x + N/2;
	}
	
	public int y2j(int y) {
		if(y > 0) 
			return y + M/2 - 1;
		else
			return y + M/2;
	}

	public void createRandomWeapon() {
		Random rand = new Random();
		
		weapons = new Weapon[W]; // Allocate memory for W weapons.
		int tempid = 1;
		int tempx = 0, tempy  = 0;
		int same; 
		String temptype = "pistol" ;
		int weapon_id = 1; // Weapon id related to weapon type.
		
		for(int i = 0 ; i < W ; i++) {
			same = 1 ;
			while(same == 1) {
				same = 0 ;
				// Generate numbers between the minimum and maximum value of the Weapon Area for both x & y.
				tempx = ((rand.nextInt( Math.abs(getWeaponAreaLimits()[1][1] )) + 1 ) * (int)Math.pow(-1, rand.nextInt(2)));
				tempy = ((rand.nextInt( Math.abs(getWeaponAreaLimits()[1][1] )) + 1 ) * (int)Math.pow(-1, rand.nextInt(2)));
				
				for (int k=0 ; k < i ; k++) {
					if(weapons[k].getX() == tempx && weapons[k].getY() == tempy) {
					same = 1 ; // Make sure that only one object corresponds to each square.
					}
				}
				
			}
					
				weapons[i] = new Weapon(weapon_id , tempx , tempy , tempid, temptype);
				tempid = (tempid == 1) ? 2 : 1; // Change the player id's so that each player has exactly one weapon of each type.
				if(temptype == "pistol") {
					temptype = "bow";
					 weapon_id = 2; 
					 // The algorithm does the same so that the id's correspond to the proper weapon type.
				}
				else if(temptype == "bow") {
					temptype = "sword";
					weapon_id = 3;
				}
				else {
					temptype = "pistol";
					weapon_id = 1;
				}
				System.out.println(weapons[i].getType() +  " owned by " + weapons[i].getPlayerId() + " created at: ");
				System.out.print(weapons[i].getX() + " ");
				System.out.println(weapons[i].getY());
		}	
		for(int i = 0 ; i < W ; i++) {
			if(weapons[i].getType() == "pistol")
				weapons[i].setWeaponIcon(new ImageIcon(this.getClass().getResource("/mypackage/images/gunResized.png")));
			else if(weapons[i].getType() == "sword")
				weapons[i].setWeaponIcon(new ImageIcon(this.getClass().getResource("/mypackage/images/swordResized.png")));
			else
				weapons[i].setWeaponIcon(new ImageIcon(this.getClass().getResource("/mypackage/images/bowResized.png")));
		}
	}
	
	public void createRandomFood() {
		Random rand = new Random();
		
		food = new Food[F]; // Allocate memory for F pieces of food.
		int tempx = 0; 
		int tempy = 0, same;
		
		for(int i = 0 ; i < F ; i++) {
		same = 1;
		while(same == 1) {	
			same = 0 ;
			do {
				// Generate random x & y between the foodAreaLimits, with an added check later... (Dragons ahead?)
				tempx = (rand.nextInt( Math.abs(getFoodAreaLimits()[1][1] )) + 1) * (int)Math.pow(-1, rand.nextInt(2));
				tempy = (rand.nextInt( Math.abs(getFoodAreaLimits()[1][1] )) + 1 ) * (int)Math.pow(-1, rand.nextInt(2));
				
			}while(!( (Math.abs(tempx) > Math.abs(getWeaponAreaLimits()[1][1]) && Math.abs(tempx) < Math.abs(getTrapAreaLimits()[1][1]) )  || 
					(Math.abs(tempy) > Math.abs(getWeaponAreaLimits()[1][1]) && Math.abs(tempy) < Math.abs(getTrapAreaLimits()[1][1]) )));	
					//The check makes sure that no square in the Weapon or Food Area is used, 
					//while the values generated still range between minimum and maximum.

			for (int k = 0 ; k < i ; k++) {
			if( food[k].getX() == tempx && food[k].getY() == tempy )
				same = 1 ;
			}
		}
					
		food[i] = new Food(i+1, tempx , tempy , rand.nextInt(9)+1, 
      new ImageIcon(this.getClass().getResource("/mypackage/images/baconResized.png"))); // i+1: Food id is 1-index based.
		System.out.println("food created at: ");
		System.out.print(food[i].getX() + " ");
		System.out.println(food[i].getY());
		}	
	}
	
	public void createRandomTrap() {
		Random rand = new Random();
		
		traps = new Trap[T]; // Allocate memory for T traps.
		int tempx = 0 , tempy = 0, same;
		String temptype = "ropes";
		for(int i = 0 ; i < T ; i++) {
		same = 1;
		while(same == 1) {
			same = 0;
			do {
			tempx = ( (rand.nextInt( Math.abs(getTrapAreaLimits()[1][1] )) + 1  )) * (int)Math.pow(-1, rand.nextInt(2) ) ;
			tempy =  ( (rand.nextInt( Math.abs(getTrapAreaLimits()[1][1] )) + 1 )) * (int)Math.pow(-1, rand.nextInt(2) )  ;
			
			}while(!((Math.abs(tempx) > Math.abs(getFoodAreaLimits()[1][1]) && Math.abs(tempx) <= Math.abs(getTrapAreaLimits()[1][1]) )
					|| ( (Math.abs(tempy) > Math.abs(getFoodAreaLimits()[1][1])) && Math.abs(tempy) <= Math.abs(getTrapAreaLimits()[1][1])))  );
					// The random generator guarantees that values don't exceed the Food Area,
					// so we just need to make sure we don't use the other areas.

			for (int k = 0 ; k < i ; k++) {
			if( traps[k].getX() == tempx && traps[k].getY() == tempy )  
				same = 1 ;
			}
		}
			
      // id is 1-index based and points are negative.
			traps[i] = new Trap(i+1, tempx, tempy, (-1)*(rand.nextInt(9)+1), temptype);
			System.out.println("trap created at: ");
			System.out.print(traps[i].getX() + " ");
			System.out.println(traps[i].getY());
			
      // Switch between the two types of traps.
			temptype = (temptype == "ropes") ? "animals" : "ropes"; 
		}
		for(int i = 0 ; i < T ; i++) {
			if(traps[i].getType() == "animals")
				traps[i].setTrapIcon(new ImageIcon(this.getClass().getResource("/mypackage/images/animalsResized.png")));
			else 
				traps[i].setTrapIcon(new ImageIcon(this.getClass().getResource("/mypackage/images/ropeResized.png")));
		}	
	} 	
	
	public void createBoard() {
		createRandomWeapon();
		createRandomFood(); 
		createRandomTrap();
	}
	
	public boolean resizeBoard(Player p1, Player p2) {
		// The board is resized only if none of the players is on any side.
		if(!(Math.abs(p1.getX()) == this.getM()/2 || Math.abs(p2.getX()) == this.getM()/2 
		  || Math.abs(p1.getY()) == this.getM()/2 || Math.abs(p2.getY()) == this.getM()/2)) {
			this.setM(this.getM()-2);
			this.setN(this.getN()-2);

			// Re-center the area limits in case of asymmetrical cutting of the edges.
			this.setWeaponAreaLimits(weaponAreaLimits);
			this.setFoodAreaLimits(foodAreaLimits);
			this.setTrapAreaLimits(trapAreaLimits);
			this.setWeapons(weapons);
			this.setTraps(traps);
			this.setFood(food);
			
			// The same re-centering is done for the players too. 
			p1.setX(p1.getX());
			p1.setY(p1.getY());
			p2.setX(p2.getX());
			p2.setY(p2.getY());
			
			System.out.println("Resizing board...");
			return true;
		} else 
			System.out.println("Could not resize board!");

		return false;
	}
	
	public String[][] getStringRepresentation(Player p1, Player p2) {
		// Create a board with a cell for each square of the board.
    String[][] rep= new String[N][M]; 
		
    // Initially fill the board with the "empty" character.
		for (int i = 0; i < rep.length; i++) {
	        Arrays.fill(rep[i], " ___ "); 
	    }
		
		int y = -N/2;
		int x = -N/2;
		int i, j ;
    
		/**
     * A correlation between the coordinate system on the board and the string table is made,
		 * scanning through each square in a line before moving to the next one.
		 * During each iteration the algorithm searches for weapons, traps and food and continues 
		 * when one of them or nothing is found.
     */
		for(i = 0 ; i < N ; i++) {
			for(j = 0 ; j < N ; j++) {						
				for(int k = 0 ; k < W; k++) {
					if(weapons[k].getX() == x && weapons[k].getY() == y) { 						
						rep[i][j] = " W" + weapons[k].getPlayerId() + weapons[k].getId() + " ";
						break;
					}
				}
				for(int k = 0 ; k < F ; k++) {
					if(food[k].getX() == x && food[k].getY() == y) {
						rep[i][j] = " F" + food[k].getId() + "  ";
						break;
					}
				}
				for(int k = 0 ; k < T ; k++) {
				     if(traps[k].getX() == x && traps[k].getY() == y) {
						rep[i][j] = " T" + traps[k].getId() + "  ";
						break;
				     }
				}
				if(x == p1.getX() && y == p1.getY())
					rep[i][j] = " P1 ";
				else if(x == p2.getX() && y == p2.getY())
					rep[i][j] = " P2 ";
				
				System.out.print(rep[i][j]);
				x = (x == -1) ? 1 : (x + 1);
			}	
			System.out.println();
			x = -N/2 ;
			y = (y == -1) ? 1 : (y + 1);
		}		
		return rep;
	}
}	

