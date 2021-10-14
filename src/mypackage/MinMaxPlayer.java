package mypackage;

import java.util.ArrayList;
import java.util.Arrays;
import javax.swing.ImageIcon;

public class MinMaxPlayer extends Player{
  ArrayList<Integer[]> path;
  static int d;
  
  public MinMaxPlayer() {
    super();
    path = new ArrayList<Integer[]>();
    opponent = null;
  }

  public MinMaxPlayer(int id, int score, int x, int y, String name, Board board, ImageIcon playerIcon) {
    super(id, score, x, y, name, board, playerIcon);
    this.path = new ArrayList<Integer[]>();
    this.opponent = null;
  }
  
  public MinMaxPlayer(MinMaxPlayer player) {
    this(player.id, player.score, player.x, player.y, player.name, player.board, player.playerIcon);
    this.path = new ArrayList<Integer[]>();
    this.opponent = player.opponent;
    this.pistol = player.pistol;
    this.bow = player.bow;
    this.sword = player.sword;
  }
  
  // Getters.
  public ArrayList<Integer[]> getPath() {return path;}
  
  // Setters.
  public void setPath(ArrayList<Integer[]> path) {this.path = path;}
  
  ////////////////////////////////////////////////////////////////////////////////////
  
  // Force the player to prioritize killing their opponent,
  // or picking up the pistol first.
  public float goforkill(int dice) {
    if(this.pistol != null) {
      int[] checkMove = new int[2];
      checkMove = makeMove(dice);
      return (float) (playersDistance(opponent) - pointDistance(checkMove[0], checkMove[1], opponent.x , opponent.y) );
    }

    return 0;
  }
  
  public boolean forceKill(int dice){
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
  
  public double goToCenter(int dice) { 
    int[] checkMove = new int[2];
    checkMove = makeMove(dice);
    
    if(checkMove[0] == 0 && checkMove[1] == 0)
      return -100;
    
    return pointDistance(x,y,0,0) - pointDistance(checkMove[0], checkMove[1], 0, 0);
  }
  
  public int gainWeapon(int dice) {
    int[] checkMove = new int[2];
    checkMove = makeMove(dice);
    
    if(checkMove[0] == 0 && checkMove[1] == 0)
      return -100;
    
    for(int i = 0 ; i < board.getW() ; i++) {
      if(checkMove[0] == board.weapons[i].getX() && checkMove[1]== board.weapons[i].getY()
       && board.weapons[i].getPlayerId() == id) 
      {
        if(board.weapons[i].getType() == "pistol") return 10;
        else return 5;
      }
    }

    return 0;
  }
      
  public int avoidTrap(int dice) {
    int[] checkMove = new int[2];
    checkMove = makeMove(dice);
    
    if(checkMove[0] == 0 && checkMove[1] == 0)
      return -100;
    
    for(int i = 0 ; i < board.getT() ; i++) {
      if(checkMove[0] == board.traps[i].getX() && checkMove[1]== board.traps[i].getY()) {     
        if( (this.bow != null && board.traps[i].getType() == "animal") || (this.sword != null && board.traps[i].getType() == "ropes") )
          return 0;
        else
          return board.traps[i].getPoints() ;
      }
    }

    return 0;
  }
              
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
      
  public double evaluate(int dice) throws CloneNotSupportedException {
    if(makeMove(dice)[0] == 0 && makeMove(dice)[1] == 0)
      return -100;
    
    if(forceKill(dice))
      return 100;         

    return  10*goforkill(dice) + gainFood(dice) + 0.5*goToCenter(dice) + 0.1*avoidTrap(dice) + gainWeapon(dice);
  }
  
  ///////////////////////////////////////////////////////////////////////////////////////////
  
  // We have agreed to use the opponent's scope when creating their subtree, thus xCurrentPos refers to the opponent's x etc...    
  void createOpponentSubtree(Node parent, int depth, int xCurrentPos, int yCurrentPos, 
    int xOpponentCurrentPos, int yOpponentCurrentPos) throws CloneNotSupportedException {
    ArrayList<Node> opponentAvailableMoves = new ArrayList<Node>(); 
    Board nBoard =  (Board) deepCopy(parent.nodeBoard);
    
    // This is our hypothetical move from the previous step.
    Player hypothMinMaxt = new Player(xOpponentCurrentPos, yOpponentCurrentPos, nBoard);
    Player hypothMinMax = (Player) deepCopy(hypothMinMaxt);

    // Player that will be moving.
    HeuristicPlayer checkPlayertemp = new HeuristicPlayer(xCurrentPos, yCurrentPos, nBoard);
    HeuristicPlayer checkPlayer = (HeuristicPlayer)deepCopy(checkPlayertemp);
    
    hypothMinMax.setOpp(checkPlayer);
    checkPlayer.setOpp(hypothMinMax);
    
    for(int dice = 1 ; dice <= 8 ; dice++) {    	
      int[] checkMove = new int[3];
      checkMove[0]= checkPlayer.makeMove(dice)[0];
      checkMove[1]= checkPlayer.makeMove(dice)[1];
      checkMove[2] = dice;
      // No need to actually move the player.

      if(checkMove[0] != 0 && checkMove[1] != 0) {             
        Node opponentNode = new Node(parent, null, depth, checkMove ,nBoard, parent.nodeEvaluation - checkPlayer.evaluate(dice,d));
        opponentAvailableMoves.add(opponentNode);
      }
    }

    // Link the subtree to its parent.
    parent.setChildren(opponentAvailableMoves);
    //System.out.println("Number of opponent available moves: " + nOfAvailableMoves);
  }
  
  void createMySubtree(Node root, int depth, int xCurrentPos, int yCurrentPos, int xOpponentCurrentPos, int yOpponentCurrentPos)
   throws CloneNotSupportedException {    
    ArrayList<Node> myAvailableMoves = new ArrayList<Node>();
    
    for(int dice = 1 ; dice <= 8 ; dice++) {
      int[] checkMove = new int[3];
      checkMove[0] = makeMove(dice)[0];
      checkMove[1] = makeMove(dice)[1];
      checkMove[2] = dice;
      
      if(checkMove[0] != 0 && checkMove[1] != 0) {
        // Create copies of the player and the board to simulate all available moves.
        Board checkBoard = (Board)deepCopy(board);
        Player tempPlayertemp = new Player(this);
        tempPlayertemp.setBoard(checkBoard);
        Player tempPlayer = (Player)deepCopy(tempPlayertemp);
        double tempEval = evaluate(dice);
        tempPlayer.move(makeMove(dice));
        
        // Under each node of available moves, we create the opponent's subtree,
        // that's filled with their own evaluation function utilizing the new state of the board.
        Node branch = new Node(root, null, depth, checkMove, tempPlayer.board, tempEval);
        myAvailableMoves.add(branch);
        createOpponentSubtree(branch, depth+1, opponent.getX(), opponent.getY(),checkMove[0], checkMove[1]);
        }
    }

    // Link the move nodes to the root.
    root.setChildren(myAvailableMoves);
    //System.out.println("Number of my available moves is: " + nOfMyAvailableMoves);
  }
  
  public int chooseMinMaxMove(Node root) {
    int firstLevelSize = root.getChildren().size();
    
    // Create a table of minimum values to be taken from the lowest level.
    double min[] = new double[firstLevelSize];
    Arrays.fill(min, Double.MAX_VALUE);
    
    for(int i = 0 ; i < firstLevelSize ; i++) {
      // Finds the minimum of each subtree, corresponding to each move.
      int secondLevelSize = root.getChildren().get(i).getChildren().size();   
      for(int j = 0 ; j < secondLevelSize ; j++) {
        if(root.getChildren().get(i).getChildren().get(j).getNodeEvaluation() < min[i]) 
          min[i] = root.getChildren().get(i).getChildren().get(j).getNodeEvaluation();
      }
    }
    
    // Choose the greatest minimum value.
    double max = Double.NEGATIVE_INFINITY;
    int bestDice = 0;
    for(int i = 0 ; i < min.length ; i++) {
      if(min[i] > max) {
        max = min[i];
        bestDice = root.getChildren().get(i).getNodeMove()[2];
      }
    }
    
    System.out.println("Evaluation of best dice is: " + max);
    return bestDice;
  }
  
  // Makes the move that is assesed as "the best". Then makes an Array of useful pieces of
  // information about the best dice, weapons picked up, food, traps and points.
  int[] getNextMove (int xCurrentPos, int yCurrentPos, int xOpponentCurrentPos, int yOpponentCurrentPos) 
    throws CloneNotSupportedException {
    // Create the subtree and find the best move.
    int[] myCurrentPos = { xCurrentPos, yCurrentPos };
    Node root = new Node(null, null, 0, myCurrentPos, this.board, 0);
    createMySubtree(root, 1, xCurrentPos, yCurrentPos, yOpponentCurrentPos, xOpponentCurrentPos);
    int bestDice = chooseMinMaxMove(root);

    // Change coordinates and complete the interactions.
    int[] newMove = makeMove(bestDice);
    System.out.println("BEST DICE WAS " + bestDice );
    System.out.println("NEW POSITION IS " + newMove[0] + " " + newMove[1]);
    
    setX(newMove[0]);
    setY(newMove[1]);
    
    int[] tempPath = new int[6];
    tempPath[0] = getX();
    tempPath[1] = getY();
    
    int hasWeapon = 0;
    for(int i = 0 ; i < board.getW() ; i++) {
      if(x == board.getWeapons()[i].getX() && y == board.getWeapons()[i].getY() && id == board.getWeapons()[i].getPlayerId()) {
        hasWeapon = 1;

        // Checks if the square location matches any of the locations in the board of weapons. 
        if(board.getWeapons()[i].getType() == "pistol") 
          this.pistol = board.getWeapons()[i];
        else if(board.getWeapons()[i].getType() == "bow")
          this.bow = board.getWeapons()[i];
        else 
          this.sword = board.getWeapons()[i];
        
        // Clear the weapon off the board.
        board.getWeapons()[i].setY(100);
        board.getWeapons()[i].setX(100);
        System.out.println(getName() + " has picked up a " + board.weapons[i].getType() + ".");
        break;
      } 
    }
    tempPath[2] = hasWeapon;
    
    int hasFood = 0;
    for(int i = 0 ; i < board.getF() ; i++) {
      if(x == board.getFood()[i].getX() && y == board.getFood()[i].getY()) {
        hasFood = 1; 

        // Checks if the square location matches any of the locations in the board of food.
        setScore(score+ board.getFood()[i].getPoints());
        
        // Clear the food off the board.
        board.getFood()[i].setX(100);
        board.getFood()[i].setY(100);
        System.out.println(getName() + " has found food " + board.getFood()[i].getId() + ".");
        System.out.println("New score: " + score);
        break;
      }
    }
    tempPath[3] = hasFood;
    
    int hasTrap = 0;
    for(int i = 0 ; i < board.getT() ; i++) {
      if(x == board.getTraps()[i].getX() && y == board.getTraps()[i].getY()) {
        hasTrap = 1;

        // Checks if the square location matches any of the locations in the board of traps.
        System.out.println(getName() + " has stumbled upon trap " + board.getTraps()[i].getId() + ".");
        if((this.bow != null && board.getTraps()[i].getType() == "animal") 
          || (this.sword != null && board.getTraps()[i].getType() == "ropes"))
          System.out.println(this.name + " has escaped the trap.");
        else
          setScore( score+ board.getTraps()[i].getPoints());

        System.out.println("New score: " + score);
        break;
      }
    }
    tempPath[4] = hasTrap;
    tempPath[5] = bestDice;
    
    Integer[] pathArray = new Integer[6];
    for(int i = 0 ; i < 6 ; i++)
      pathArray[i] = Integer.valueOf(tempPath[i]);
    path.add(pathArray);
      
    return tempPath;
  }   
  
  // Print out the new info about the move the player made.
  public String statistics(int round) {
    String statistics = name + " chose move with code " + path.get(round)[5] +
      "\nNew coordinates are: " + path.get(round)[0] + ", " + path.get(round)[1] +
      "\nWeapons picked up: " + path.get(round)[2] +
      "\nPieces of food collected: " + path.get(round)[3] + 
      "\nTraps " + name + " stumbled upon: " + path.get(round)[4] +
      "\nNew score: " + score;

    System.out.println(statistics);
    return statistics;
  }
}
