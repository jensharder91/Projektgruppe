package tdti;

import java.applet.Applet;
import java.awt.BorderLayout;
import java.awt.Color;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JLabel;

public class TDTI {
	private static final String TITLE = "Tree Decontamination with Temporary Immunity";
	private static final int DEFAULT_WIDTH = 800;
	private static final int DEFAULT_HEIGHT = 600;
	
	public TDTI(){
		JFrame frame = new JFrame();
		frame.setSize(DEFAULT_WIDTH, DEFAULT_HEIGHT);
		frame.setTitle(TITLE);
		frame.setLayout(new BorderLayout());
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setResizable(true);
		frame.setLocationRelativeTo(null);
		
		JPanel panel = new JPanel();
		panel.setBackground(Color.WHITE);
		panel.add(new JLabel("Test"));
		
		frame.add(panel, "Center");
		frame.setVisible(true);
	}
	
	public static void main(String[] args){
		Applet applet = new Applet();
		applet.setVisible(true);
	}
}
