package Gui;


import java.awt.BorderLayout;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JToggleButton;

import cima.CIMAVertice;
import Tree.Vertice;

public abstract class Gui extends JPanel{

	protected static final long serialVersionUID = 1L;

//	protected Vertice rootVertice = null;
	public static Vertice rootVertice = null;
	protected Vertice homeBase = null;
	protected Vertice currentVertice = null;

	protected JPanel buttonBar = new JPanel();

	/** Buttons */
	protected JButton buttonCalculate = new JButton("Berechnung starten");
	protected JButton buttonAnimation = new JButton("Animation starten");
	protected JButton buttonClear = new JButton("Clear");
	protected JToggleButton toggleAutoAlgo = new JToggleButton("AutoCalc");

	protected boolean autoAlgo = false;

	protected Gui(){
//		super(true);

		createGui();

		addGuiMouseListener();
	}
	
//	public void makeStrate(){
//		this.createBufferStrategy(2);
//		strat = getBufferStrategy();
//	}

	protected void createGui(){

		setLayout(new BorderLayout());

		buttonClear.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e){

				clearGui();

			}
		});

		buttonCalculate.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {

				calcAlgorithmus(true);

			}
		});
		
		buttonAnimation.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {

				homeBase = ((CIMAVertice) rootVertice).findHomeBase();
				((CIMAVertice) homeBase).calcAgentsMove();

			}
		});

		toggleAutoAlgo.addItemListener(new ItemListener() {

			@Override
			public void itemStateChanged(ItemEvent e) {
				if(e.getStateChange() == ItemEvent.SELECTED){
					autoAlgo = true;
					calcAlgorithmus(true);
				}else if(e.getStateChange() == ItemEvent.DESELECTED){
					autoAlgo = false;
				}
			}
		});

		buttonBar.add(buttonCalculate);
		buttonBar.add(buttonAnimation);
		buttonBar.add(buttonClear);
		buttonBar.add(toggleAutoAlgo);
		this.add(buttonBar, "South");
	}

	public Vertice getRoot(){
		return rootVertice;
	}

	@Override
	public void paintComponent(Graphics g) {

		if(rootVertice != null && autoAlgo){
			calcAlgorithmus(false);
		}

		Graphics2D g2 = (Graphics2D) g;
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
		g2.setStroke(new BasicStroke(2));
		super.paintComponent(g);
		
		//background
		g.setColor(Color.white);
		g.fillRect(0, 0, (int)getSize().getWidth(), (int)getSize().getHeight());

		//mark current point
		g.setColor(Color.blue);
		if(currentVertice != null){
			g.drawRect(currentVertice.getX(), currentVertice.getY(), currentVertice.getWidth(), currentVertice.getHeight());
		}
		
		//mark homebase
		g.setColor(Color.RED);
		if(homeBase != null){
			g.drawRect(homeBase.getX(), homeBase.getY(), homeBase.getWidth(), homeBase.getHeight());
		}

		//draw all vertices recursively
		if(rootVertice != null){
			rootVertice.drawTree(g,10,10,getWidth()-20,getHeight()-50);
		}
		
		
		//disable / enable buttons
		if(CIMAVertice.activeAnimation){
			buttonCalculate.setVisible(false);
			buttonClear.setVisible(false);
			toggleAutoAlgo.setVisible(false);
			buttonAnimation.setText("Animation abbrechen");
		}else{
			buttonCalculate.setVisible(true);
			buttonClear.setVisible(true);
			toggleAutoAlgo.setVisible(true);
			buttonAnimation.setText("Animation starten");
		}
	}

	protected void clearGui(){
		rootVertice = null;
		currentVertice = null;
		homeBase = null;
		repaint();
	}

	protected abstract void calcAlgorithmus(boolean repaintBool);
	protected abstract void addGuiMouseListener();

}

