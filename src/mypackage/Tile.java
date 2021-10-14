package mypackage;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JTextField;


public class Tile extends JLabel {
	
	JLabel info;

	public Tile() {
		setPreferredSize(new Dimension(30, 30));
		setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));
		setLayout(new FlowLayout(FlowLayout.LEFT));
		info = new JLabel();
		info.setBounds(TOP, TOP, 0, 0);
		info.setFont(new Font("Arial Bold", Font.BOLD, 12));
		info.setForeground(Color.WHITE);
		add(info, BorderLayout.BEFORE_FIRST_LINE);
	}
	
	public void setInfo(String info) {
		this.info.setText(info);
	}
	
	public JLabel getInfo() {
		return info;
	}
}
