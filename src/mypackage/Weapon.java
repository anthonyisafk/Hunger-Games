package mypackage;

import java.io.Serializable;
import javax.swing.ImageIcon;

public class Weapon implements Serializable{
	int id;
	int x;
	int y;
	int playerId;
	String type;
	ImageIcon weaponIcon;

  // Constructor using fields.		
	public Weapon(int id, int x, int y, int playerId, String type){
		this.id = id;
		this.x = x;	
		this.y = y;
    this.playerId = playerId;
    this.type = type;
	}

  // Constructor using an object as argument.		
	public Weapon(Weapon weapon){
		this(weapon.id, weapon.x, weapon.y, weapon.playerId, weapon.type);
	}

	// Setters.
	public int getId() {return id;}
	public int getX() {return x;}
	public int getY() {return y;}
	public int getPlayerId() {return playerId;}
	public String getType() {return type;}
	public ImageIcon getWeaponIcon() {return weaponIcon;}
	
  // Getters.
	public void setId(int id) {this.id = id;}
	public void setX(int x) {this.x = x;}
	public void setY(int y) {this.y = y;}
	public void setPlayerId(int playerId) {this.playerId = playerId;}
	public void setType(String type) {this.type = type;}			
	public void setWeaponIcon(ImageIcon weaponIcon) {this.weaponIcon = weaponIcon;} 
}
