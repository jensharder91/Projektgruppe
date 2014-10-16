package tdti;

import java.applet.Applet;
import java.awt.*;
import java.awt.event.*;

import javax.swing.*;

public class TDTI {
	private static final String TITLE = "Tree Decontamination with Temporary Immunity";
	private static final int DEFAULT_WIDTH = 800;
	private static final int DEFAULT_HEIGHT = 600;

	public static int IMMUNITY_TIME = 0;

	public TDTI(){
		JFrame frame = new JFrame();
		frame.setSize(DEFAULT_WIDTH, DEFAULT_HEIGHT);
		frame.setMinimumSize(new Dimension(DEFAULT_WIDTH, DEFAULT_HEIGHT));
		frame.setTitle(TITLE);
		frame.setLayout(new BorderLayout());
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setResizable(true);
		frame.setLocationRelativeTo(null);

		JPanel panel = new JPanel();

		JButton testButton = new JButton("Baum ausgeben");
		panel.add(testButton);

		frame.add(panel, "South");
		frame.setVisible(true);

		testButton.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {

				Vertice a = new Vertice("0");
				Vertice b = new Vertice("0.0",a);
				Vertice c = new Vertice("0.1",a);
				Vertice d = new Vertice("0.2",a);
				new Vertice("0.0.0",b);
				new Vertice("0.0.1",b);
				new Vertice("0.1.0",c);
				new Vertice("0.2.0",d);
				new Vertice("0.2.1",d);
				new Vertice("0.2.2",d);

				a.init();

				a.logSubtree();
			}
		});
	}

	public static void main(String[] args){
		new TDTI();
		Applet applet = new Applet();
		applet.setVisible(true);
	}
}
