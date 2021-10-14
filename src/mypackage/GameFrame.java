package mypackage;

import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.UIManager;

public class GameFrame {

  // The numbers of times of the board has been resized.
	int timesresized = 0;
	
	Board board;
	int boardN;
	int boardM;
	Player player1;
	Player player2;
	
	boolean playPressed;
	boolean genPressed;
	JFrame frame;
	JComboBox<String> selection1;
	JTextArea label1;
	JComboBox<String> selection2;
	JTextArea label2;
	JButton play;
	JButton generateBoard;
	JButton quit;
	JPanel boardPanel;
	JTextArea turnLabel;
	JLabel p1Label;
	JLabel p2Label;
	Tile[][] labelRepresentation;
	
	// GETTERS AND SETTERS WERE DELIBERATELY PLACED AT THE END OF THE CODE
	// TO MAKE THE ACTUAL PROGRAM EASILY READ AND COMPACT.
	
	// Set turns simply returns either 1 or 2 randomly. Logic will be implemented in main.
	public int setTurns() {
		return (int)Math.floor( Math.random() * 2 + 1) ;
	}
	
	GameFrame(Board board) {
		
    this.board = board;	
    boardN = board.getN();
    boardM = board.getM();

    // Wait for user input before running any piece of code.
    playPressed = false;
    genPressed = false;
    
    frame = new JFrame("Hunger Games");
    
    // Change UI.
    try { 
        UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
    } catch (Exception e) {
        e.printStackTrace();
    }
    
    frame.setLayout(new FlowLayout(FlowLayout.LEFT));
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    String players[] = {"Random Player", "Heuristic Player", "MinMax Player"};
    
    // Initialize all the components of the frame.
    selection1 = new JComboBox<String>(players);
    label1 = new JTextArea();
    selection2 = new JComboBox<String>(players);
    label2 = new JTextArea();
    turnLabel = new JTextArea();
    play = new JButton("Play");
    generateBoard = new JButton("Generate Board");
    quit = new JButton("Quit");
    boardPanel = new JPanel();
    labelRepresentation = new Tile[20][20];
        
    // Place every component to the appropriate position.
    selection1.setBounds(30, 5, 200, 40);
    selection2.setBounds(610, 5, 200, 40);
    play.setBounds(100, 760, 150, 40);
    play.setEnabled(false);
    generateBoard.setBounds(350, 760, 150, 40);
    quit.setBounds(600, 760, 150, 40);
    
    // Set fonts.
    label1.setBounds(30, 50, 300, 100);
    label1.setFont(new Font("Trebuchet MS", Font.PLAIN, 12));
    label1.setForeground(Color.RED);
    label1.setLineWrap(true);
    label1.setBackground(frame.getBackground());
    label2.setFont(new Font("Trebuchet MS", Font.PLAIN, 12));
    label2.setBounds(610, 50, 200, 100);
    label2.setForeground(Color.BLACK);
    label2.setLineWrap(true);
    label2.setBackground(frame.getBackground());
    turnLabel.setBounds(320, 10, 200, 40);
    turnLabel.setFont(new Font("Trebuchet MS", Font.BOLD, 13));
    turnLabel.setForeground(Color.GRAY);
    turnLabel.setBackground(frame.getBackground());
    turnLabel.setLineWrap(true);
    
    // Initialize the panel in which the game table will be placed. 
    // Set the desired grid layout and fix the constraints.
    boardPanel.setBounds(115, 150, 600, 600);
    boardPanel.setLayout(new GridBagLayout());
    GridBagConstraints gbc = new GridBagConstraints();
    
    // Set the cursor on the upper left cell of the grid.
    gbc.gridx = 0;
    gbc.gridy = 0;
    
        
    for(int i = 0 ; i < 20 ; i++) {
      for(int j = 0 ; j < 20 ; j++) {
        gbc.gridx = i;
        gbc.gridy = j;
        labelRepresentation[i][j] = new Tile();
        boardPanel.add(labelRepresentation[i][j], gbc);
        
        // Make the whole panel red, then set the appropriate background.
        labelRepresentation[i][j].setOpaque(true);
        labelRepresentation[i][j].setBackground(Color.GRAY);
        
        // Set rectangles in order to check the area each point is inside.
        Rectangle trapArea = new Rectangle(x2i(board.getTrapAreaLimits()[2][0], boardN),
                                y2j(board.getTrapAreaLimits()[2][1], boardM),
                                2 * Math.abs(board.getTrapAreaLimits()[0][0]),
                                2 * Math.abs(board.getTrapAreaLimits()[0][0]));
        
        Rectangle foodArea = new Rectangle(x2i(board.getFoodAreaLimits()[2][0], boardN),
                                y2j(board.getFoodAreaLimits()[2][1], boardM),
                                2 * Math.abs(board.getFoodAreaLimits()[0][0]),
                                2 * Math.abs(board.getFoodAreaLimits()[0][0]));
        
        Rectangle weaponArea = new Rectangle(x2i(board.getWeaponAreaLimits()[2][0], boardN),
                                y2j(board.getWeaponAreaLimits()[2][1], boardM),
                                2 * Math.abs(board.getWeaponAreaLimits()[0][0]),
                                2 * Math.abs(board.getWeaponAreaLimits()[0][0]));
        
        // The structure used to check where the point is.
        Point check = new Point(i, j);
        
        // Set the according backgrounds.
        if(trapArea.contains(check))
          labelRepresentation[i][j].setBackground(Color.LIGHT_GRAY);
        if(foodArea.contains(check))
          labelRepresentation[i][j].setBackground(Color.CYAN);
        if(weaponArea.contains(check))
          labelRepresentation[i][j].setBackground(Color.RED);
      }
    }
    
    // Place the components inside the frame.
    frame.add(selection1);
    frame.add(label1);
    frame.add(selection2);
    frame.add(label2);
    frame.add(turnLabel, FlowLayout.CENTER);
    frame.add(play);
    frame.add(generateBoard);
    frame.add(quit);
    
    // Last settings, frame size and visibility.
    frame.setBounds(10, 10, 850, 850);
    frame.setLayout(null);
    frame.setVisible(true);
    frame.setResizable(false);
    
    // Set an action for every button pressed.
    play.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e1) {
        GameFrame.this.playPressed = true;

        String player1 = "Player 1 will be a " + 
            selection1.getItemAt(selection1.getSelectedIndex());
            label1.setText(player1);
        
        String player2 = "Player 2 will be a " + 
            selection2.getItemAt(selection2.getSelectedIndex());
            label2.setText(player2);
            
        // Disable player selection.
        selection1.setEnabled(false);
        selection2.setEnabled(false);
        generateBoard.setEnabled(false);
      }			
    });
    
    generateBoard.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e2) {
        GameFrame.this.genPressed = true;	
        frame.add(boardPanel);

        System.out.println("Panel printed");
        label1.setText(" ");
        play.setEnabled(true);
      }			
    });
    
    quit.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e2) {
        System.exit(0);
        }			
    });
	}
	
	public void createAndRefreshGUI(Board board) {
		
		boardPanel.removeAll();
		
		// Make a grid to place the JLabels, in order to create the game table.
		boardPanel.setLayout(new GridBagLayout());
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.gridy = 0;
			
		for(int i = 0 ; i < 20 ; i++) {
			for(int j = 0 ; j < 20 ; j++) {
				gbc.gridx = i;
				gbc.gridy = j;
				labelRepresentation[i][j] = new Tile();
				boardPanel.add(labelRepresentation[i][j], gbc);
				
				labelRepresentation[i][j].setOpaque(true);
				labelRepresentation[i][j].setBackground(Color.GRAY);
				
				
				Rectangle trapArea = new Rectangle(x2i(board.getTrapAreaLimits()[2][0], boardN),
																y2j(board.getTrapAreaLimits()[2][1], boardM),
																2 * Math.abs(board.getTrapAreaLimits()[0][0]),
																2 * Math.abs(board.getTrapAreaLimits()[0][0]));
				
				Rectangle foodArea = new Rectangle(x2i(board.getFoodAreaLimits()[2][0], boardN),
																y2j(board.getFoodAreaLimits()[2][1], boardM),
																2 * Math.abs(board.getFoodAreaLimits()[0][0]),
																2 * Math.abs(board.getFoodAreaLimits()[0][0]));
				
				Rectangle weaponArea = new Rectangle(x2i(board.getWeaponAreaLimits()[2][0], boardN),
																y2j(board.getWeaponAreaLimits()[2][1], boardM),
																2 * Math.abs(board.getWeaponAreaLimits()[0][0]),
																2 * Math.abs(board.getWeaponAreaLimits()[0][0]));			
				
				Point check = new Point(i, j);
				
				if(trapArea.contains(check))
					labelRepresentation[i][j].setBackground(Color.LIGHT_GRAY);
				if(foodArea.contains(check))
					labelRepresentation[i][j].setBackground(Color.CYAN);
				if(weaponArea.contains(check))
					labelRepresentation[i][j].setBackground(Color.RED);	
			}
		}
		
		// Check every tile and set the appropriate icon, along with the necessary info.
		for(int i = 0 ; i < board.getN() ; i++) {
			for(int j = 0 ; j < board.getN() ; j++) {
				for(int k = 0 ; k < board.getW() ; k++) {
					if(x2i(board.getWeapons()[k].getX(), board.getN()) == i && y2j(board.getWeapons()[k].getY(), board.getM()) == j) {
						labelRepresentation[i+timesresized][j+timesresized].setIcon(board.getWeapons()[k].getWeaponIcon());
						labelRepresentation[i+timesresized][j+timesresized].setInfo(Integer.toString(board.getWeapons()[k].getPlayerId()));
					}
				}
				for(int k = 0 ; k < board.getT() ; k++) {
					if(x2i(board.getTraps()[k].getX(), board.getN()) == i && y2j(board.getTraps()[k].getY(), board.getM()) == j) {
						labelRepresentation[i+timesresized][j+timesresized].setIcon(board.getTraps()[k].getTrapIcon());
						labelRepresentation[i+timesresized][j+timesresized].setInfo(Integer.toString(board.getTraps()[k].getPoints()));
						labelRepresentation[i+timesresized][j+timesresized].getInfo().setForeground(Color.YELLOW);
					}
				}
				for(int k = 0 ; k < board.getF() ; k++) {
					if(x2i(board.getFood()[k].getX(), board.getN()) == i && y2j(board.getFood()[k].getY(), board.getN()) == j) {
						labelRepresentation[i+timesresized][j+timesresized].setIcon(board.getFood()[k].getFoodIcon());
						labelRepresentation[i+timesresized][j+timesresized].setInfo('+' + Integer.toString(board.getFood()[k].getPoints()));
						labelRepresentation[i+timesresized][j+timesresized].getInfo().setForeground(Color.BLACK);
					}
				}
				if(player1 != null || player2 != null) {
					if(x2i(player1.getX(), board.getN()) == i && y2j(player1.getY(), board.getM()) == j 
						&& x2i(player2.getX(), board.getN()) == i && y2j(player2.getY(), board.getM()) == j) {
						labelRepresentation[i+timesresized][j+timesresized].setIcon(new ImageIcon(Game.class.getResource("/mypackage/images/bothPlayersResized.png")));
					}
					else if(x2i(player1.getX(), board.getN()) == i && y2j(player1.getY(), board.getM()) == j)
						labelRepresentation[i+timesresized][j+timesresized].setIcon(player1.getPLayerIcon());
					else if(x2i(player2.getX(), board.getN()) == i && y2j(player2.getY(), board.getM()) == j)
						labelRepresentation[i+timesresized][j+timesresized].setIcon(player2.getPLayerIcon());
				}	 
			}
		}
	}

	public void resizeGUI(Board board) {
		timesresized++;
		createAndRefreshGUI(board);
	}
		
	/////////////////////////////////////////////////////////////////////////////
		
	// These functions were altered. The original N and M values are constantly used
	// to keep every area of the panelBoard symmetrical.
		public int x2i(int x, int boardN) {
			if(x > 0) 
				return x + boardN/2 - 1;
			else
				return x + boardN/2;
		}
		
		public int y2j(int y, int boardM) {
			if(y > 0) 
				return y + boardM/2 - 1;
			else
				return y + boardM/2;
		}
		
	////////////////////////// Getters & Setters ///////////////////////////////
	
	public boolean isPlayPressed() {
		return playPressed;
	}
	public boolean isGenPressed() {
		return genPressed;
	}
	public JFrame getFrame() {
		return frame;
	}
	public JComboBox<String> getSelection1() {
		return selection1;
	}
	public JTextArea getLabel1() {
		return label1;
	}
	public JComboBox<String> getSelection2() {
		return selection2;
	}
	public JTextArea getLabel2() {
		return label2;
	}
	public JButton getPlay() {
		return play;
	}
	public JButton getGenerateBoard() {
		return generateBoard;
	}
	public JButton getQuit() {
		return quit;
	}
	public JPanel getBoardPanel() {
		return boardPanel;
	}
	public JTextArea getTurnLabel() {
		return turnLabel;
	}
	public JLabel getP1Label() {
		return p1Label;
	}
	public JLabel getP2Label() {
		return p2Label;
	}
	public Board getBoard() {
		return board;
	}	
	public Player getPlayer1() {
		return player1;
	}
	public Player getPlayer2() {
		return player2;
	}
	public JLabel[][] getLabelRepresentation() {
		return labelRepresentation;
	}
	public int getTimesresized() {
		return timesresized;
	}
	public int getBoardN() {
		return boardN;
	}
	public int getBoardM() {
		return boardM;
	}
	
	
	public void setPlayPressed(boolean playPressed) {
		this.playPressed = playPressed;
	}
	public void setGenPressed(boolean genPressed) {
		this.genPressed = genPressed;
	}
	public void setFrame(JFrame frame) {
		this.frame = frame;
	}
	public void setSelection1(JComboBox<String> selection1) {
		this.selection1 = selection1;
	}
	public void setLabel1(JTextArea label1) {
		this.label1 = label1;
	}
	public void setSelection2(JComboBox<String> selection2) {
		this.selection2 = selection2;
	}
	public void setLabel2(JTextArea label2) {
		this.label2 = label2;
	}
	public void setPlay(JButton play) {
		this.play = play;
	}
	public void setGenerateBoard(JButton generateBoard) {
		this.generateBoard = generateBoard;
	}
	public void setQuit(JButton quit) {
		this.quit = quit;
	}
	public void setBoardPanel(JPanel boardPanel) {
		this.boardPanel = boardPanel;
	}
	public void setTurnLabel(JTextArea turnLabel) {
		this.turnLabel = turnLabel;
	}
	public void setP1Label(JLabel p1Label) {
		this.p1Label = p1Label;
	}
	public void setP2Label(JLabel p2Label) {
		this.p2Label = p2Label;
	}
	public void setBoard(Board board) {
		this.board = board;
	}
	public void setPlayer1(Player player1) {
		this.player1 = player1;
	}
	public void setPlayer2(Player player2) {
		this.player2 = player2;
	}
	public void setLabelRepresentation(Tile[][] labelRepresentation) {
		this.labelRepresentation = labelRepresentation;
	}
	public void setTimesresized(int timesresized) {
		this.timesresized = timesresized;
	}
	public void setBoardN(int boardN) {
		this.boardN = boardN;
	}
	public void setBoardM(int boardM) {
		this.boardM = boardM;
	}
}

