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

import cima.CIMAAnimation;
import cima.CIMAVertice;
import cima.MessageData;
import Tree.Vertice;

public abstract class Gui extends JPanel{

	protected static final long serialVersionUID = 1L;

//	protected Vertice rootVertice = null;
	public static Vertice rootVertice = null;
	protected Vertice homeBase = null;
	protected Vertice currentVertice = null;

	protected JPanel buttonBar = new JPanel();

	/** Buttons */
	protected JButton buttonCalculate = new JButton("Sofort berechnen");
	protected JButton buttonCalculateAnimation = new JButton("Berechnung animieren");
//	protected JButton buttonAnimation = new JButton("Animation berechnen");
	protected JButton buttonClear = new JButton("Clear");
	protected JButton buttonBack = new JButton("Zurück");
//	protected JToggleButton toggleAutoAlgo = new JToggleButton("AutoCalc");
	private JButton buttonNextAgentAnimationStep = new JButton("\u25BA");//RightArrow
	private JButton buttonNextCalculateAnimationStep = new JButton("\u25BA");
//	private JButton buttonPrev = new JButton("\u25c4");//LeftArro
	protected JButton buttonCompleteAgentAnimation = new JButton("Baum dekontaminieren");
	
	protected boolean autoAlgo = false;
//	public static boolean calcAgentMovesReady = false;

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
		
		buttonBack.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				CIMAVertice.drawMu = false;
				repaint();
			}
		});

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
		

		buttonCalculateAnimation.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {

				homeBase = ((CIMAVertice) rootVertice).findHomeBase();
				((CIMAVertice) rootVertice).doCompleteSendMessageAnimation();

			}
		});
		
//		buttonAnimation.addActionListener(new ActionListener() {
//
//			@Override
//			public void actionPerformed(ActionEvent e) {
//
////				homeBase = ((CIMAVertice) rootVertice).findHomeBase();
//				((CIMAVertice) homeBase).calcAgentsMove();
//				
//				if(calcAgentMovesReady){
//					calcAgentMovesReady = false;
//				}else{
//					calcAgentMovesReady = true;
//				}
//				repaint();
//
//			}
//		});
		
		buttonCompleteAgentAnimation.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				
				((CIMAVertice) homeBase).doCompleteAgentAnimation();
				
			}
		});
		
//		buttonPrev.addActionListener(new ActionListener() {
//
//			@Override
//			public void actionPerformed(ActionEvent e) {
//
//				((CIMAVertice) homeBase).doStepAnimation(false);
//
//			}
//		});
		
		buttonNextAgentAnimationStep.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {

				((CIMAVertice) homeBase).doStepAgentAnimation();
				
			}
		});
		
		buttonNextCalculateAnimationStep.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				
				homeBase = ((CIMAVertice) rootVertice).findHomeBase();
				((CIMAVertice) rootVertice).doStepSendMessageAnimation();
				
			}
		});

//		toggleAutoAlgo.addItemListener(new ItemListener() {
//
//			@Override
//			public void itemStateChanged(ItemEvent e) {
//				if(e.getStateChange() == ItemEvent.SELECTED){
//					autoAlgo = true;
//					calcAlgorithmus(true);
//				}else if(e.getStateChange() == ItemEvent.DESELECTED){
//					autoAlgo = false;
//				}
//			}
//		});

		buttonBar.add(buttonBack);
		buttonBar.add(buttonCalculate);
		buttonBar.add(buttonCalculateAnimation);
//		buttonBar.add(buttonAnimation);
		buttonBar.add(buttonCompleteAgentAnimation);
//		buttonBar.add(buttonPrev);
		buttonBar.add(buttonNextAgentAnimationStep);
		buttonBar.add(buttonNextCalculateAnimationStep);
		buttonBar.add(buttonClear);
//		buttonBar.add(toggleAutoAlgo);
		this.add(buttonBar, "South");
		
		
		repaint();
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
		if(CIMAVertice.drawMu){
			g.setColor(Color.RED);
			if(homeBase != null){
				g.drawRect(homeBase.getX(), homeBase.getY(), homeBase.getWidth(), homeBase.getHeight());
			}
		}
		
		//messageData
		for(MessageData msgData : CIMAVertice.messageDataList){
			msgData.draw(g); //TODO
		}

		//draw all vertices recursively
		if(rootVertice != null){
			rootVertice.drawTree(g,10,10,getWidth()-20,getHeight()-50);
		}
		
		
		//disable / enable buttons
		buttonBack.setVisible(false);
		buttonCalculate.setVisible(true);
		buttonCalculateAnimation.setVisible(true);
		buttonCalculateAnimation.setText("Berechnung animieren");
		buttonClear.setVisible(true);
//		toggleAutoAlgo.setVisible(true);
		buttonNextAgentAnimationStep.setVisible(false);
		buttonNextCalculateAnimationStep.setVisible(true);
//		buttonPrev.setVisible(false);
		buttonCompleteAgentAnimation.setVisible(false);
//		buttonAnimation.setText("Animation berechnen");
		
		if(rootVertice == null || rootVertice.getChildren().size() < 1){
			buttonBack.setVisible(false);
			buttonCalculate.setVisible(false);
			buttonCalculateAnimation.setVisible(false);
			buttonClear.setVisible(false);
			buttonNextAgentAnimationStep.setVisible(false);
			buttonNextCalculateAnimationStep.setVisible(false);
			buttonCompleteAgentAnimation.setVisible(false);
		}

		
		if(MessageData.animationInProgress){
			buttonCalculate.setVisible(false);
			buttonCalculateAnimation.setText("Animation abbrechen");
			buttonClear.setVisible(false);
			buttonNextCalculateAnimationStep.setVisible(false);
			
			if(CIMAAnimation.singeAnimationModus){
				buttonNextCalculateAnimationStep.setVisible(true);
//				buttonPrev.setVisible(true);
				buttonCalculateAnimation.setVisible(true);//TODO let the complete animation finish the step by step modus
				buttonCalculateAnimation.setText("komplette Animation");
			}
		}
		
//		if(calcAgentMovesReady){
		if(CIMAVertice.drawMu == true){
			buttonBack.setVisible(true);
			buttonNextAgentAnimationStep.setVisible(true);
//			buttonPrev.setVisible(true);
			buttonCompleteAgentAnimation.setVisible(true);
			buttonCompleteAgentAnimation.setText("Baum dekontaminieren");
			buttonCalculate.setVisible(false);
			buttonCalculateAnimation.setVisible(false);
			buttonClear.setVisible(false);
			buttonNextCalculateAnimationStep.setVisible(false);
//			toggleAutoAlgo.setVisible(false);
//			buttonAnimation.setText("Animation abbrechen");
		}
		
		if(CIMAVertice.activeAnimation){
			buttonBack.setVisible(false);
			buttonCalculate.setVisible(false);
			buttonCalculateAnimation.setVisible(false);
			buttonClear.setVisible(false);
//			toggleAutoAlgo.setVisible(false);
			buttonNextAgentAnimationStep.setVisible(false);
			buttonNextCalculateAnimationStep.setVisible(false);
//			buttonPrev.setVisible(false);
			buttonCompleteAgentAnimation.setVisible(true);
			buttonCompleteAgentAnimation.setText("Animation abbrechen");
//			buttonAnimation.setText("Animation abbrechen");
			
			if(CIMAAnimation.singeAnimationModus){
				buttonNextAgentAnimationStep.setVisible(true);
//				buttonPrev.setVisible(true);
				buttonCompleteAgentAnimation.setVisible(true);//TODO let the complete animation finish the step by step modus
				buttonCompleteAgentAnimation.setText("komplette Animation");
			}
		}
	}

	protected void clearGui(){
		rootVertice = null;
		currentVertice = null;
		homeBase = null;
		MessageData.clearGui = true;
		repaint();
	}

	protected abstract void calcAlgorithmus(boolean repaintBool);
	protected abstract void addGuiMouseListener();

}

