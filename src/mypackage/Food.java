package mypackage;

import java.io.Serializable;
import javax.swing.ImageIcon;

public class Food implements Serializable{
	int id;
	int x;
	int y;
	int points;
	ImageIcon foodIcon;
	
  // Constructor using fields.	
	public Food(int id, int x, int y, int points, ImageIcon foodIcon) {
		this.id = id;
		this.x = x;
		this.y = y;
		this.points = points;
		this.foodIcon = foodIcon;
	}
	
  // Constructor using an object as argument.	
	public Food(Food food) {
		this(food.id, food.x, food.y, food.points, food.foodIcon);
	}

	// Getters.
	public int getId() {return id;}
	public int getX() {return x;}
	public int getY() {return y;}
	public int getPoints() {return points;}
	public ImageIcon getFoodIcon() {return foodIcon;}
	
	// Setters.
	public void setId(int id) {this.id = id;}
	public void setX(int x) {this.x = x;}
	public void setY(int y) {this.y = y;}
	public void setPoints(int points) {this.points = points;}	
	public void setFoodIcon(ImageIcon foodIcon) {this.foodIcon = foodIcon;}
		
}
