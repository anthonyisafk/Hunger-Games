/*
* Antonios Antoniou
* aantonii@ece.auth.gr
* * * * * * * * * * * * * * 
 * Polydoros Giannouris 
 * polydoros@ece.auth.gr
 * * * * * * * * * * * * * *
 * Aristotle University of Thessaloniki
 */

/* 
 * Icons made by:
 * https://www.flaticon.com/authors/smashicons
 * https://www.flaticon.com/authors/smalllikeart
 * https://www.flaticon.com/authors/freepik
 * https://www.flaticon.com/authors/eucalyp
 * https://www.flaticon.com/authors/surang
 */

package mypackage;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import java.awt.Graphics;

public class Game {

  // Each round consists of 2 turns.
  static int round;
  static int turn;
  static ImageIcon player1icon;
  static ImageIcon player2icon;
  
  public Game(){
    round = 1;
    turn = 1; 
  }
  
  public static int getRound() {return round;}
  public static void setRound(final int newRound) {round = newRound;}
  
  public static void main(String[] args) throws CloneNotSupportedException, InterruptedException {
    // Initialize the area limits so they can be used in the constructor.
    int[][] w_arr = {{2, 2}, {-2, 2}, {-2, -2}, {2, -2}}; 
    int[][] f_arr = {{3, 3}, {-3, 3}, {-3, -3}, {3, -3}}; 
    int[][] t_arr = {{4, 4}, {-4, 4}, {-4, -4}, {4, -4}}; 
    
    Player p1;
    Player p2;
    
    player1icon = new ImageIcon(Game.class.getResource("/mypackage/images/player1Resized.png"));
    player2icon = new ImageIcon(Game.class.getResource("/mypackage/images/player2Resized.png"));
    
    Board board = new Board(20, 20, 6, 10, 8, w_arr, f_arr, t_arr);
    GameFrame frame = new GameFrame(board);   
    String[][] boardRepresentation;
    JLabel[][] labelRepresentation;
    
    // Still have to display components after "Generate board" is pressed(move graphics to appropriate position).
    while(!frame.genPressed) { 
      Thread.sleep(500);
    }

    board.createBoard();
    frame.setBoard(board);
    frame.createAndRefreshGUI(board);

    System.out.println("PlayPressed: " + frame.playPressed);
    
    // Check for user input every half a second.
    while(!frame.playPressed) {
      Thread.sleep(500);
    }
    
    System.out.println("PlayPressed: " + frame.playPressed);
    
    // Initialize Player 1 according to the user's choice.
    if(frame.getSelection1().getSelectedItem() == "MinMax Player") {
      p1 = new MinMaxPlayer(1, 15, 10, 10, "Player1", board, player1icon);   
    }
    else if(frame.getSelection1().getSelectedItem() == "Heuristic Player") {
      p1 = new HeuristicPlayer(1, 15, 10, 10, "Player1", board, player1icon);
    }
    else {
      p1 = new Player(1, 15, 10, 10, "Player1", board, player1icon);
    }
    
    // Initialize Player 2 according to user choice.
    if(frame.getSelection2().getSelectedItem() == "MinMax Player" ) {
      p2 = new MinMaxPlayer(1, 15, 10, 10, "Player2", board, player2icon);   
    }
    
    else if(frame.getSelection2().getSelectedItem() == "Heuristic Player") {
      p2 = new HeuristicPlayer(1, 15, 10, 10, "Player2", board, player2icon);
    }
    
    else{
      p2 = new Player(1, 15, 10, 10, "Player2", board, player2icon);
    }
    
    p1.setOpp(p2);
    p2.setOpp(p1);
    frame.setPlayer1(p1);
    frame.setPlayer2(p2);
  
    int firstPlayer = frame.setTurns();
    System.out.println("Player " + firstPlayer + " goes first" );
    frame.getTurnLabel().setText("Player " + firstPlayer + " goes first");
    
    // Check for board resize.
    if(Game.round % 3 == 0) { 
      if(board.resizeBoard(p1, p2)) {
        frame.resizeGUI(board);
        frame.getTurnLabel().setText("Game board is resized.");
      }
    }
    
    boardRepresentation = board.getStringRepresentation(p1, p2);
    frame.createAndRefreshGUI(board);
    Game.round = 1;
    Game.turn = 1;
    
    // The game runs as long as the board is of size greater than 4X4.
    while(board.getM() > 4) {
      // Player 1 will either play when they have to go first,
      // or if player 2 has already played(turn is even).
      
      // Small wait to allow us to watch the games.
      Thread.sleep(1000);
      if(firstPlayer == 1 || turn%2 == 0) {	
        if(firstPlayer == 1 ) {
          System.out.println(" "); 
          System.out.println("------ Round " + Game.round + " ------");
          frame.getTurnLabel().setText("Round " + Game.round);
        }
        System.out.println("Player 1");
        
        // Move player1 according to player type.
        if(frame.getSelection1().getSelectedItem() == "MinMax Player" ) {
          ((MinMaxPlayer)p1).getNextMove(p1.getX(), p1.getY(), p2.getX(), p2.getY());
          frame.getLabel1().setText(((MinMaxPlayer)p1).statistics(Game.round - 1));
        }
        
        else if(frame.getSelection1().getSelectedItem() == "Heuristic Player" ) {
          ((HeuristicPlayer)p1).move(2);
          frame.getLabel1().setText(((HeuristicPlayer)p1).statistics(round - 1));
        }
        
        else {
          p1.move(p1.getRandomMove());	 
          frame.getLabel1().setText(p1.getName() + " moved to " + p1.getX() + ", " + p1.getY() + 
            "\nNew score: " + p1.getScore());
        }
        
        // Check if game ended.
        if(p1.getScore() < 0) {
          System.out.println(p1.getName() + " has reached a negative score, " + p2.getName() + " wins.");
          frame.getTurnLabel().setText(p1.getName() + " has reached a negative score, " + p2.getName() + " wins.");
        break;
        }

        if(HeuristicPlayer.kill(p1, p2, 2)) {
          System.out.println(p1.getName() + " kills player 2 and wins.");
          frame.getTurnLabel().setText(p1.getName() + " kills player 2 and wins.");
          break;
        }
        
        if(HeuristicPlayer.kill(p2, p1, 2)) {
          System.out.println(p2.getName() + " kills player 1 and wins.");
          frame.getTurnLabel().setText(p2.getName() + " kills player 1 and wins.");
          break;
        }
          
        // Check for board resize.
        if(Game.round % 3 == 0) { 
          if(board.resizeBoard(p1, p2)) {
            frame.resizeGUI(board);
            frame.getTurnLabel().setText("Game board has been resized");
          }
        }
        boardRepresentation = board.getStringRepresentation(p1, p2);
        frame.createAndRefreshGUI(board);
        
        System.out.println(" "); 
        System.out.println("Player 2");
        System.out.println(p2.getScore());
        
        // Increase round if p1 goes second.
        if(firstPlayer == 2) round++;
          
        // Check for board resize.
        if(Game.round % 3 == 0) { 
          if(board.resizeBoard(p1, p2)) {
            frame.resizeGUI(board);
            frame.getTurnLabel().setText("Game board has been resized");
          }
        }

        turn++;	
      }
      
      // Move player 2 according to player type.
      if(firstPlayer == 2) {
        System.out.println(" "); 
        System.out.println("------ Round " + Game.round + " ------");
        frame.getTurnLabel().setText("Round " + Game.round);
      }
      // Small wait to allow us to watch the games.
      Thread.sleep(1000);
      
      if(frame.getSelection2().getSelectedItem() == "MinMax Player") {
        ((MinMaxPlayer)p2).getNextMove(p2.getX(), p2.getY(), p1.getX(), p1.getY());
        frame.getLabel2().setText(((MinMaxPlayer)p2).statistics(Game.round - 1));
      }
      
      else if(frame.getSelection2().getSelectedItem() == "Heuristic Player") {
        ((HeuristicPlayer)p2).move(2);
        frame.getLabel2().setText(((HeuristicPlayer)p2).statistics(round - 1));
      }
      
      else {
        p2.move(p2.getRandomMove());
        frame.getLabel2().setText(p2.getName() + " moved to " + p2.getX() + ", " + p2.getY() + 
          "\nNew score: " + p2.getScore());
      }
      
      // Check if game ended.
      if(p2.getScore() < 0) {
        System.out.println(p2.getName() + " has reached a negative score\n " + p1.getName() + " wins.");
        frame.getTurnLabel().setText(p2.getName() + " has reached a negative score\n" + p1.getName() + " wins.");
      break;
      }
      if(HeuristicPlayer.kill(p2, p1, 2)) {
          System.out.println(p2.getName() + " kills player 1 and wins.");
          frame.getTurnLabel().setText(p2.getName() + " kills player 1 and wins.");
          break;
      }
      
      if(HeuristicPlayer.kill(p1, p2, 2)) {
          System.out.println(p1.getName() + " kills player 2 and wins.");
          frame.getTurnLabel().setText(p1.getName() + " kills player 2 and wins.");
          //p1wins++; 
          break;
      }
        
      boardRepresentation = board.getStringRepresentation(p1, p2);
      frame.createAndRefreshGUI(board);
      
      // Increase round if p2 goes second.
      if(firstPlayer == 1) Game.round++;
        
      // Check for board resize.
      if(Game.round % 3 == 0) { 
        if(board.resizeBoard(p1, p2)) {
          frame.resizeGUI(board);
          frame.getTurnLabel().setText("Game board has been resized");
        }
      }
      turn++;	
    }
        
    
    // Final check to see who won.
    frame.createAndRefreshGUI(board);
    System.out.println("Player 1 score: " + p1.getScore());
    System.out.println("Player 2 score: " + p2.getScore());

    if(board.getM() == 4) {
      if(p1.getScore() > p2.getScore()) {
        System.out.println(p1.getName() + " wins.");
        frame.getTurnLabel().setText(p1.getName() + " wins.");
      }
      else if(p2.getScore() > p1.getScore()) {
        System.out.println(p2.getName() + " wins.");
        frame.getTurnLabel().setText(p2.getName() + " wins.");
      }
      else {
        System.out.println("It's a draw.");
        frame.getTurnLabel().setText("It's a draw");
      }  
    }     
  } 
}
