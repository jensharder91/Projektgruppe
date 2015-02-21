package cima;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;

import javax.swing.JApplet;
import javax.swing.JFrame;

public class MyApplet {
	protected String title;
	protected static final int DEFAULT_WIDTH = 800;
	protected static final int DEFAULT_HEIGHT = 600;

	public MyApplet(Component panel, String title) {
		this.title = title;

		JApplet applet = new JApplet();
		applet.add(panel, "Center");

		JFrame frame = new JFrame();
		frame.setSize(DEFAULT_WIDTH, DEFAULT_HEIGHT);
		frame.setMinimumSize(new Dimension(DEFAULT_WIDTH, DEFAULT_HEIGHT));
		frame.setTitle(this.title);
		frame.setLayout(new BorderLayout());
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setResizable(true);
		frame.setLocationRelativeTo(null);




		frame.add(applet);
		frame.setVisible(true);
	}
}
