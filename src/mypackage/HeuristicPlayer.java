package mypackage;

import java.util.ArrayList;
import java.util.Map;
import java.util.TreeMap;
import javax.swing.ImageIcon;

class HeuristicPlayer extends Player {
  ArrayList<Integer[]> path;
  static int r = 3; // The range in which the player is able to see.
  
  // Blank constructor.
  public HeuristicPlayer() {
    super();
    path = new ArrayList<Integer[]>();
  }
  
  // Default constructor.
  public HeuristicPlayer(int id, int score, int x, int y, String name, Board board, ImageIcon playerIcon) {
      super(id, score, x, y, name, board, playerIcon);
      path = new ArrayList<Integer[]>();
  }
  // Copy constructor.
  public HeuristicPlayer(HeuristicPlayer player) {
      this(player.id, player.score, player.x, player.y, player.name, player.board, player.playerIcon);
  } 
  
  // Constructor only needing new position (for MinMax use).
  public HeuristicPlayer(int x, int y, Board board) {
    this.x = x;
    this.y = y;
    this.board = board;
  }

  // Calculates if a given position is visible to player.
  public boolean isInField(int x1, int y1){
      if ( board.x2i(x1) <= board.x2i(x) + r && board.x2i(x1) >= board.x2i(x) - r
          && board.y2j(y1) <= board.y2j(y) + r  && board.y2j(y1) >= board.y2j(y) - r)
          return true;
      else 
          return false;
  }
  
  public double goToCenter(int dice) { 
      int[] checkMove = new int[2];
      checkMove = makeMove(dice);
      
      // Make sure the player doesn't go off bounds.
      if(checkMove[0] == 0 && checkMove[1] == 0)
          return -100;
      
      return pointDistance(x,y,0,0) - pointDistance(checkMove[0], checkMove[1], 0, 0);
  }
      
  // Convert to i and j to avoid calculation regarding 0,0. This conversion doesn't affect the result. 
  public static boolean kill(Player p1, Player p2, float d) {
      if (p1.playersDistance(p2) <= d && (p1.playersDistance(p2) != -1  && p1.pistol != null))
        return true;
      else
        return false;
  }
  
  // Forces the player to kill their opponent.
  public boolean forceKill(int dice, int d){
    int[] checkMove = new int[2];
    checkMove = makeMove(dice);
    
    if(checkMove[0] == 0 && checkMove[1] == 0)
        return false;                                           
    
    Player checkPl = (Player)deepCopy(this);
    Board checkBoard = (Board)deepCopy(this.board);
    
    checkPl.setBoard(checkBoard);
    
    checkPl.setX(checkMove[0]);
    checkPl.setY(checkMove[1]);
    
    return HeuristicPlayer.kill(checkPl, opponent, d);
  }
  
  // Forces the player to obtain a weapon.
  public int gainWeapon(int dice) {
    int[] checkMove = new int[2];
    checkMove = makeMove(dice);
    
    if(checkMove[0] == 0 && checkMove[1] == 0)
      return -100;
    
    for(int i = 0 ; i < board.getW() ; i++) {
      if(checkMove[0] == board.weapons[i].getX() && checkMove[1]== board.weapons[i].getY() 
        && board.weapons[i].getPlayerId() == id ) 
      {
        if(board.weapons[i].getType() == "pistol") return 10;
        else return 5;
      }
    }
    return 0;
  }
      
  // Helps the player avoid traps.
  public int avoidTrap(int dice) {
    int[] checkMove = new int[2];
    checkMove = makeMove(dice);
    
    if(checkMove[0] == 0 && checkMove[1] == 0)
      return -100;
    
    for(int i = 0 ; i < board.getT() ; i++) {
      if(checkMove[0] == board.traps[i].getX() && checkMove[1]== board.traps[i].getY()) {     
        if((this.bow != null && board.traps[i].getType() == "animal") 
          ||(this.sword != null && board.traps[i].getType() == "ropes"))
          return 0;
        else
          return board.traps[i].getPoints() ;
      }
    }

    return 0 ;
  }
       
  // Encourages the player to gather food.
  public int gainFood(int dice) {
    int[] checkMove = new int[2];
    checkMove = makeMove(dice);
    
    if(checkMove[0] == 0 && checkMove[1] == 0)
      return -100;
    
    for(int i = 0 ; i < board.getF() ; i++) {
      if(checkMove[0] == board.food[i].getX() && checkMove[1]== board.food[i].getY())         
        return board.food[i].getPoints();
    }
    
    return 0;
  }
      
  // Gives a score to a potential move.
  public double evaluate(int dice, int d) throws CloneNotSupportedException {
    if(makeMove(dice)[0] == 0 && makeMove(dice)[1] == 0)
      return -100;
    
    if(forceKill(dice, d))
      return 100;      

    return  gainFood(dice) + 0.5*goToCenter(dice) + avoidTrap(dice) + gainWeapon(dice);
  }
      
  // Makes the move that is assesed as "the best". Then makes an Array of useful pieces of
  // information about the best dice, weapons picked up, food, traps and points.
  public int[] move(int d) throws CloneNotSupportedException {
    Map<Integer, Double> evaluation = new TreeMap<Integer, Double>();
    
    for (int dice = 1 ; dice <= 8 ; dice++) 
      evaluation.put(dice, evaluate(dice, d));
    
    double max = evaluation.get(1);
    int bestDice = 1;
  
    for(int dice = 2 ; dice <= 8 ; dice++) {
      if(evaluation.get(dice) > max) {
        max = evaluation.get(dice);
        bestDice = dice;
      }
    }
    // CHANGE TO BEST DICE.
    int[] info = new int[6];
    info[0] = bestDice;
    int[] newMove = makeMove(bestDice);

    // Move the player where the "dice" dictates.
    setX(newMove[0]);
    setY(newMove[1]);
    
    int[] report = new int[7];
    report[0] = bestDice;
    report[1] = getX();
    report[2] = getY();
    
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

        //System.out.println(getName() + " has picked up a " + board.weapons[i].getType() + ".");
        break;
      }
    }
    report[3] = hasWeapon;
    
    int hasFood = 0;
    for(int i = 0 ; i < board.getF() ; i++) {
      if(x == board.food[i].getX() && y == board.food[i].getY()) {
        hasFood = 1; 

        // Checks if the square location matches any of the locations in the board of food.
        setScore( score+ board.food[i].getPoints());
        
        // Clear the food off the board.
        board.food[i].setX(100);
        board.food[i].setY(100);
        System.out.println(getName() + " has found food " + board.food[i].getId() + ".");
        System.out.println("New score: " + score);
        break;
      }
        
    }
    report[4] = hasFood;
    
    int hasTrap = 0;
    for(int i = 0 ; i < board.getT() ; i++) {
      if(x == board.traps[i].getX() && y == board.traps[i].getY()) {
        hasTrap = 1;

        // Checks if the square location matches any of the locations in the board of traps.
        System.out.println(getName() + " has stumbled upon trap " + board.traps[i].getId() + ".");
        if((this.bow != null && board.traps[i].getType() == "animal") 
          || (this.sword != null && board.traps[i].getType() == "ropes"))
          System.out.println(this.name + " has escaped the trap.");
        else
          setScore( score+ board.traps[i].getPoints());
          
        System.out.println("New score: " + score);
        break;
      }
    }
    report[5] = hasTrap;
    report[6] = score;
    
    Integer[] pathArray = new Integer[7];
    for(int i = 0 ; i < 7 ; i++)
      pathArray[i] = Integer.valueOf(report[i]);
    
    path.add(pathArray);
    return report;
  }
     
  // Print out the new info about the move the player made.
  public String statistics(int round) {
    String statistics = name + " chose move with code " + path.get(round)[0] +
      "\nNew coordinates are: " + path.get(round)[1] + ", " + path.get(round)[2] +
      "\nWeapons picked up: " + path.get(round)[3] +
      "\nPieces of food collected: " + path.get(round)[4] + 
      "\nTraps " + name + " stumbled upon: " + path.get(round)[5] +
      "\nNew score: " + score;
    
    System.out.println(statistics);
    return statistics;
  }
}
