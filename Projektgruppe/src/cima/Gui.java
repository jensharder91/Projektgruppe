package cima;


import java.awt.BorderLayout;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public abstract class Gui extends JPanel{

	protected static final long serialVersionUID = 1L;

	public static Vertice rootVertice = null;
	protected Vertice homeBase = null;
	protected Vertice currentVertice = null;
//	protected boolean hideHomebase = false;

	protected JPanel buttonBarSouth = new JPanel();
	protected JPanel buttonBarNorth = new JPanel();
	
	private String[] chooseAlgo = {"Paperalgorithmus", "Modifikation"};
	/** Buttons */
	protected JButton buttonCalculate = new JButton("Nachrichten sofort berechnen");
	protected JButton buttonCalculateAnimation = new JButton("Nachrichtenberechnung animieren");
	protected JButton buttonClear = new JButton("Clear");
	protected JButton buttonBack = new JButton("Zurück");
	private JButton buttonNextAgentAnimationStep = new JButton("\u25BA");//RightArrow
	private JButton buttonNextCalculateAnimationStep = new JButton("\u25BA");
	protected JButton buttonCompleteAgentAnimation = new JButton("Baumdekontamination animieren");
	private JLabel lableAnimationSpeed = new JLabel("Animation speed:");
	private SpinnerNumberModel spinnerModel_speed = new SpinnerNumberModel(3, 0, 10, 1);
	private SpinnerNumberModel spinnerModel_potential = new SpinnerNumberModel(0, 0, 999, 1);
	private JSpinner spinnerAnimationSpeed = new JSpinner(spinnerModel_speed);
	private JSpinner spinnerPotential = new JSpinner(spinnerModel_potential);
	private JCheckBox checkboxShowMessageData = new JCheckBox("zeige die Berechnung an");
	protected JComboBox<String> comboBoxCalcStrategy = new JComboBox<String>(chooseAlgo);
	protected JLabel lableChosePotantial = new JLabel("ohne Potantial");
	
	
	/** bollean if gui element should be visible*/
	private boolean buttonCalculateBoolean = false;
	private boolean buttonCalculateAnimationBoolean = false;
	private boolean buttonClearBoolean = false;
	private boolean buttonBackBoolean = false;
	private boolean buttonNextAgentAnimationStepBoolean = false;
	private boolean buttonNextCalculateAnimationStepBoolean = false;
	private boolean buttonCompleteAgentAnimationBoolean = true;
	private boolean spinnerAnimationSpeedBoolean = false;
	private boolean spinnerPotentialBoolean = false;
	private boolean checkboxShowMessageDataBoolean = false;
	private boolean lableChosePotantialBoolean = false;
	
	private enum ButtonState{
		state1, //<2 knoten
		state2,	//"normal"
		state3,	//bei msgData animation
		state4, //nach msgData animation
		state5,	//bei baumdekonta. animation
		state6, //next msgData animation
		state7, //wait step msgData animation
		state8,	//next step agent animation
		state9, //wait step agent animation
		
		//north
		state20, //ausgangssituation, wähle paperModell oder modifikation
		state21, //mod ausgewählt: wähle msgData
		state22; //mod ausgewählt: wähle pot
		
		
	};
	private ButtonState buttonStateSouth = ButtonState.state2;
	private ButtonState buttonStateNorth = ButtonState.state20;
	
	
	protected boolean autoAlgo = false;
	protected boolean editorOn = true;

	protected Gui(){

		createGui();

		addGuiMouseListener();
	}

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
				
				CIMAAnimation cimaAnimation = CIMAAnimation.getCIMAAnimation();
				
				if(cimaAnimation.animationIsInProgress()){
					cimaAnimation.stopAllAnimations();
					buttonCalculateAnimation.setText("Nachrichtenberechnung animieren");
				}else{
					homeBase = ((CIMAVertice) rootVertice).findHomeBase();
					((CIMAVertice) rootVertice).animateMsgData();
					buttonCalculateAnimation.setText("Nachrichtenanimation anhalten");
				}
			}
		});
		
		buttonCompleteAgentAnimation.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				CIMAAnimation cimaAnimation = CIMAAnimation.getCIMAAnimation();
				
				if(cimaAnimation.animationIsInProgress()){
					cimaAnimation.stopAllAnimations();
					buttonCompleteAgentAnimation.setText("Baumdekontamination animieren");
				}else{
					((CIMAVertice) rootVertice).animateAgents();
					buttonCompleteAgentAnimation.setText("Dekontaminationsanimation anhalten");
				}
				
			}
		});
				
		buttonNextAgentAnimationStep.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {

				((CIMAVertice) homeBase).animateAgents(true);
				
			}
		});
		
		buttonNextCalculateAnimationStep.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				
				homeBase = ((CIMAVertice) rootVertice).findHomeBase();
				((CIMAVertice) rootVertice).animateMsgData(true);
				
			}
		});
		
		spinnerAnimationSpeed.addChangeListener(new ChangeListener() {
			
			@Override
			public void stateChanged(ChangeEvent e) {
				AgentWayData.setAnimationSpeed((int) spinnerAnimationSpeed.getValue());
				MessageData.setAnimationSpeed((int) spinnerAnimationSpeed.getValue());
			}
		});
		
	spinnerPotential.addChangeListener(new ChangeListener() {
		@Override
		public void stateChanged(ChangeEvent e) {
			
			treeChanged();
			
			CIMAVertice.setPotential((int) spinnerPotential.getValue());
			if((int) spinnerPotential.getValue() <= 0){
				lableChosePotantial.setText("ohne Potantial");
				CIMAVertice.setStrategy(new ModelMultPotential());
				ICalcStrategy.setShowPotential(false);
			}else if((int) spinnerPotential.getValue() == 1){
				lableChosePotantial.setText("Potantial = 1");
				CIMAVertice.setStrategy(new ModellMinimalDanger());
				ICalcStrategy.setShowPotential(true);
			}else{
				lableChosePotantial.setText("Potential \u2265 1");
				CIMAVertice.setStrategy(new ModelMultPotential());
				ICalcStrategy.setShowPotential(true);
			}
		}
	});
		
		checkboxShowMessageData.addChangeListener(new ChangeListener() {
			
			@Override
			public void stateChanged(ChangeEvent e) {
//				MessageData_old.setShowMessageData(checkboxShowMessageData.isSelected());
				repaint();
			}
		});
		
		
		comboBoxCalcStrategy.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {

				if(comboBoxCalcStrategy.getSelectedIndex() == 0){
					CIMAVertice.setStrategy(new ModelStandardPaper());
					buttonStateNorth = ButtonState.state20;
					treeChanged();
				}else{
					CIMAVertice.setStrategy(new ModelMultPotential());
					buttonStateNorth = ButtonState.state21;
					treeChanged();
				}
			}
		});
		//init
		comboBoxCalcStrategy.setSelectedIndex(0);
		CIMAVertice.setStrategy(new ModelStandardPaper());
		

		buttonBarNorth.add(comboBoxCalcStrategy);
		buttonBarNorth.add(lableChosePotantial);
		buttonBarNorth.add(spinnerPotential);
		this.add(buttonBarNorth, "North");
	
		buttonBarSouth.add(lableAnimationSpeed);
		buttonBarSouth.add(spinnerAnimationSpeed);
		buttonBarSouth.add(buttonBack);
		buttonBarSouth.add(buttonCalculate);
		buttonBarSouth.add(buttonCalculateAnimation);
		buttonBarSouth.add(buttonCompleteAgentAnimation);
		buttonBarSouth.add(buttonNextAgentAnimationStep);
		buttonBarSouth.add(buttonNextCalculateAnimationStep);
		buttonBarSouth.add(buttonClear);
		buttonBarSouth.add(checkboxShowMessageData);
		this.add(buttonBarSouth, "South");
		
		
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
		g2.setColor(Color.white);
		g2.fillRect(0, 0, (int)getSize().getWidth(), (int)getSize().getHeight());
		
		//messageDataLine
		for(MessageData msgData : CIMAVertice.messageDataList){
			msgData.drawAnimationLine(g2);
		}

		//mark current point
		g2.setColor(Color.blue);
		if(currentVertice != null){
			g2.drawRect(currentVertice.getX(), currentVertice.getY(), currentVertice.getDiameter(), currentVertice.getDiameter());
		}
		
		//mark homebase
		if(CIMAVertice.drawMu){
			g2.setColor(Color.RED);
			if(rootVertice != null){
				homeBase = ((CIMAVertice) rootVertice).findHomeBase();
			}
			if(homeBase != null){
				g2.drawRect(homeBase.getX(), homeBase.getY(), homeBase.getDiameter(), homeBase.getDiameter());
			}
		}

		//draw all vertices recursively
		if(rootVertice != null){
			((CIMAVertice) rootVertice).drawTree(g2,10,10 + buttonBarNorth.getHeight(),getWidth()-20,getHeight()-50);
		}
		
		//messageData
		for(MessageData msgData : CIMAVertice.messageDataList){
			msgData.drawAnimation(g2);
		}
		
		for(AgentWayData agentData : CIMAVertice.agentWayList){
			agentData.drawAnimation(g2);
		}
				
		if(rootVertice != null){
			CIMAVertice.drawDisplayInformation(((CIMAVertice) rootVertice), g2);
		}
		

		
		/***  BUTTON logic   **/
		/* button south*/
		
		buttonStateSouth = ButtonState.state2; //default
		
		if(CIMAAnimation.getCIMAAnimation().singleStepAnimationIsInProgress() && !CIMAVertice.drawMu){
			buttonStateSouth = ButtonState.state7;
		}
		
		if(rootVertice == null || rootVertice.getChildren().size() < 1){
			buttonStateSouth = ButtonState.state1;
		}
		
		if(CIMAAnimation.getCIMAAnimation().animationIsInProgress() && !CIMAVertice.activeAgentAnimation){
			buttonStateSouth = ButtonState.state3;
			
			if(CIMAAnimation.singeAnimationModus){
				buttonStateSouth = ButtonState.state6;
			}
		}
		
		if(!CIMAAnimation.getCIMAAnimation().animationIsInProgress() && CIMAVertice.drawMu){
			buttonStateSouth = ButtonState.state4;
			
			if(CIMAAnimation.getCIMAAnimation().singleStepAnimationIsInProgress()){
				buttonStateSouth = ButtonState.state9;
			}
		}
		
		if(CIMAAnimation.getCIMAAnimation().animationIsInProgress() && CIMAVertice.activeAgentAnimation){
			buttonStateSouth = ButtonState.state5;
			
			if(CIMAAnimation.singeAnimationModus){
				buttonStateSouth = ButtonState.state8;
			}
		}
		
			
		
		//enabe / disable the buttons
		
		//default disable all buttons
		buttonCalculateBoolean = false;
		buttonCalculateAnimationBoolean = false;
		buttonClearBoolean = false;
		buttonBackBoolean = false;
		buttonNextAgentAnimationStepBoolean = false;
		buttonNextCalculateAnimationStepBoolean = false;
		buttonCompleteAgentAnimationBoolean = false;
		spinnerAnimationSpeedBoolean = false;
		spinnerPotentialBoolean = false;
		checkboxShowMessageDataBoolean = false;
		buttonNextAgentAnimationStepBoolean = false;
		buttonNextCalculateAnimationStepBoolean = false;
		
		if(buttonStateSouth == ButtonState.state1){//<2 knoten
			buttonClearBoolean = true;
		}
		if(buttonStateSouth == ButtonState.state2){//"normal"
			buttonClearBoolean = true;
			buttonCalculateBoolean = true;
			buttonCalculateAnimationBoolean = true;
			buttonNextCalculateAnimationStepBoolean  = true;
		}
		if(buttonStateSouth == ButtonState.state3){//bei msgData animation
			buttonCalculateAnimationBoolean = true;
			spinnerAnimationSpeedBoolean = true;
		}
		if(buttonStateSouth == ButtonState.state4){//nach msgData animation
			buttonBackBoolean = true;
			buttonCompleteAgentAnimationBoolean = true;
			buttonClearBoolean = true;
			buttonNextAgentAnimationStepBoolean = true;
		}
		if(buttonStateSouth == ButtonState.state5){//bei baumdekonta. animation
			buttonCompleteAgentAnimationBoolean = true;
			spinnerAnimationSpeedBoolean = true;
		}
		
		//next steps...
		if(buttonStateSouth == ButtonState.state6){//next step msgData animation
			buttonNextCalculateAnimationStepBoolean = false;
			spinnerAnimationSpeedBoolean = true;
		}
		
		if(buttonStateSouth == ButtonState.state7){//wait step msgData animation
			buttonNextCalculateAnimationStepBoolean = true;
			buttonCalculateAnimationBoolean = true;
		}
		
		if(buttonStateSouth == ButtonState.state8){//next step agent animation
			buttonNextAgentAnimationStepBoolean = false;
			spinnerAnimationSpeedBoolean = true;
		}
		
		if(buttonStateSouth == ButtonState.state9){//wait step agent animation
			buttonNextAgentAnimationStepBoolean = true;
			buttonCompleteAgentAnimationBoolean = true;
		}
		
		
		//north
		if(buttonStateNorth == ButtonState.state20){//ausgangssituation
			spinnerPotentialBoolean = false;		
			lableChosePotantialBoolean = false;
		}

		if(buttonStateNorth == ButtonState.state21){//modifikation ausgewählt, ohne pot
			spinnerPotentialBoolean = true;
			lableChosePotantialBoolean = true;
		}
		
		if(buttonStateNorth == ButtonState.state22){//modifikation ausgewählt, mit pot
			spinnerPotentialBoolean = true;
			lableChosePotantialBoolean = true;
		}

		

		buttonSetEnabled(buttonCalculate, buttonCalculateBoolean);
		buttonSetEnabled(buttonCalculateAnimation, buttonCalculateAnimationBoolean);
		buttonSetEnabled(buttonClear, buttonClearBoolean);
		buttonSetEnabled(buttonBack, buttonBackBoolean);
		buttonSetEnabled(buttonCompleteAgentAnimation, buttonCompleteAgentAnimationBoolean);
		buttonSetEnabled(buttonNextAgentAnimationStep, buttonNextAgentAnimationStepBoolean);
		buttonSetEnabled(buttonNextCalculateAnimationStep, buttonNextCalculateAnimationStepBoolean);
		lableAnimationSpeed.setVisible(spinnerAnimationSpeedBoolean);
		spinnerAnimationSpeed.setVisible(spinnerAnimationSpeedBoolean);
		spinnerPotential.setVisible(spinnerPotentialBoolean);
		checkboxShowMessageData.setVisible(checkboxShowMessageDataBoolean);
		lableChosePotantial.setVisible(lableChosePotantialBoolean);
		
	}
	
	private void buttonSetEnabled(JButton button, boolean shouldBeVisible){
		if(shouldBeVisible && !button.isEnabled()){
			button.setEnabled(true);
			button.setVisible(true);
		}
		
		if(!shouldBeVisible && button.isEnabled()){
			button.setEnabled(false);
			button.setVisible(false);
		}
	}

	protected void clearGui(){
		
		treeChanged();
		
		rootVertice = null;
		currentVertice = null;
		homeBase = null;
		repaint();
	}
	
	public JPanel getButtonBarNorth(){
		return buttonBarNorth;
	}
	public JPanel getButtonBarSouth(){
		return buttonBarSouth;
	}

	protected abstract void calcAlgorithmus(boolean repaintBool);
	protected abstract void addGuiMouseListener();
	protected abstract void treeChanged();
	

}

