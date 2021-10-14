package mypackage;
import java.util.ArrayList;

public class Node {

	Node parent;
	ArrayList<Node> children;
	int nodeDepth;

	// nodeMove = current x,y and dice
	int[] nodeMove;

	// board after the move has been made.
	Board nodeBoard;
	double nodeEvaluation;
	
	public Node(Node parent, ArrayList<Node> children, int nodeDepth, int[] nodeMove, Board nodeBoard, double nodeEvaluation) {
		this.parent = parent;
		this.children = children;
		this.nodeDepth = nodeDepth;
		this.nodeMove = nodeMove;
		this.nodeBoard = nodeBoard;
		this.nodeEvaluation = nodeEvaluation;
	} 
	
	public Node(Node node) {
		this(node.parent, node.children, node.nodeDepth, node.nodeMove, node.nodeBoard, node.nodeEvaluation);
	}
	
	public Node() {
		parent = null;
		children = new ArrayList<Node>();
		nodeDepth = 0;
		nodeMove = new int[2];
		nodeBoard = null;
		nodeEvaluation = 0;
	}

	// Getters.
	public Node getParent() {return parent;}
	public ArrayList<Node> getChildren() {return children;}
	public int getNodeDepth() {return nodeDepth;}
	public int[] getNodeMove() {return nodeMove;}
	public Board getNodeBoard() {return nodeBoard;}
	public double getNodeEvaluation() {return nodeEvaluation;}

	// Setters.
	public void setParent(Node parent) {this.parent = parent;}
	public void setChildren(ArrayList<Node> children) {this.children = children;}
	public void setNodeDepth(int nodeDepth) {	this.nodeDepth = nodeDepth;}
	public void setNodeMove(int[] nodeMove) {this.nodeMove = nodeMove;}
	public void setNodeBoard(Board nodeBoard) {this.nodeBoard = nodeBoard;}
	public void setNodeEvaluation(double nodeEvaluation) {this.nodeEvaluation = nodeEvaluation;}

}
