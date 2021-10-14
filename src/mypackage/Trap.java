package mypackage;

import java.io.Serializable;
import javax.swing.ImageIcon;

public class Trap implements Serializable{
	int id;
	int x;
	int y;
	int points;
	String type;
	ImageIcon trapIcon;
	
  // Constructor using fields.
	public Trap(int id, int x, int y, int points, String type){
		this.id = id;
		this.x = x;	
		this.y = y;
    this.points = points;
    this.type = type;
	}
	
  // Constructor using an object as argument.	
	public Trap(Trap trap){
		this(trap.id, trap.x, trap.y, trap.points, trap.type);
	}
	
	// Getters.
	public int getId() {return id;}
	public int getX() {return x;}
	public int getY() {return y;}
	public int getPoints() {return points;}
	public String getType() {return type;}
	public ImageIcon getTrapIcon() {return trapIcon;}
	
	// Setters.
	public void setId(int id) {this.id = id;}
	public void setX(int x) {this.x = x;}
	public void setY(int y) {this.y = y;}
	public void setPoints(int points) {this.points = points;}
	public void setType(String type) {this.type = type;}
	public void setTrapIcon(ImageIcon trapIcon) {this.trapIcon = trapIcon;}
}
