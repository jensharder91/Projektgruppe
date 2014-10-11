package tdti;

import java.applet.Applet;
import java.awt.*;
import java.awt.event.*;

import javax.swing.*;

public class TDTI {
	private static final String TITLE = "Tree Decontamination with Temporary Immunity";
	private static final int DEFAULT_WIDTH = 800;
	private static final int DEFAULT_HEIGHT = 600;

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

		JButton testButton = new JButton("Test");
		panel.add(testButton);

		frame.add(panel, "South");
		frame.setVisible(true);

		final Vertice a = new Vertice("0.0");
		new Vertice("1.0",a);
		new Vertice("1.1",a);

		testButton.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				//System.out.println(a);
				a.logSubtree();
				System.out.println("Test pressed");
			}
		});
	}

	public static void main(String[] args){
		new TDTI();
		Applet applet = new Applet();
		applet.setVisible(true);
	}
}
