package tdti;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.SwingUtilities;
import javax.swing.JTextField;
import javax.swing.JButton;

import Gui.Gui;
import Tree.Vertice;

public class TDTIGui extends Gui{

	public int IMMUNITY_TIME = 0;
	public int MAX_STEPS = 0;
	public int remainingSteps = MAX_STEPS;

	private static final long serialVersionUID = 1L;
	private static TDTIGui gui;
	private JTextField textField = new JTextField("0",2);
	private JButton btnNext = new JButton("Weiter");
	private JButton btnPrev = new JButton("Zurück");
	private TDTIGui algo;

	private TDTIGui(){
		super();
		algo = this;
		textField.setText(String.valueOf(algo.IMMUNITY_TIME));
		textField.setHorizontalAlignment(JTextField.CENTER);
		textField.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				int newImmunityTime = Integer.parseInt(textField.getText());
				// TODO: set Immunity Time
				IMMUNITY_TIME = newImmunityTime;
				System.out.println("Immunity Time set to "+newImmunityTime);
				algo.calcAlgorithmus(true);
			}
		});
		addFieldToBar(textField);

		btnPrev.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				MAX_STEPS--;
				updateButtons();
				calcAlgorithmus(true);
			}
		});
		addButtonToBar(btnPrev);

		btnNext.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				MAX_STEPS++;
				updateButtons();
				calcAlgorithmus(true);
			}
		});
		addButtonToBar(btnNext);

		updateButtons();
	}

	public static TDTIGui getGui(){
		if(gui == null){
			gui = new TDTIGui();
		}
		return gui;
	}

	protected void updateButtons(){
		if(MAX_STEPS <= 0){
			btnPrev.setEnabled(false);
		} else {
			btnPrev.setEnabled(true);
		}
		if(MAX_STEPS >= numberOfSteps()){
			btnNext.setEnabled(false);
		} else {
			btnNext.setEnabled(true);
		}
	}

	protected int numberOfSteps(){
		if(rootVertice instanceof TDTIVertice){
			return 2*rootVertice.numberOfVertices()-2;
		}
		return 0;
	}

	@Override
	protected void calcAlgorithmus(boolean repaintBool) {
		remainingSteps = MAX_STEPS;
		if(rootVertice instanceof TDTIVertice){
			((TDTIVertice) rootVertice).algorithmus();
			rootVertice.logSubtree();

			if(repaintBool){
				repaint();
			}
		} else {
			System.out.println("No root available");
		}
	}

	@Override
	protected void reset(){
		MAX_STEPS = 0;
		remainingSteps = MAX_STEPS;
		updateButtons();
		calcAlgorithmus(true);
	}

	@Override
	protected void addGuiMouseListener() {
		addMouseListener(new MouseListener() {

			@Override
			public void mouseReleased(MouseEvent e) {
				// TODO Auto-generated method stub
			}

			@Override
			public void mousePressed(MouseEvent e) {
				// TODO Auto-generated method stub
			}

			@Override
			public void mouseExited(MouseEvent e) {
				// TODO Auto-generated method stub
			}

			@Override
			public void mouseEntered(MouseEvent e) {
				// TODO Auto-generated method stub
			}

			@Override
			public void mouseClicked(MouseEvent e) {
				System.out.println(e.getX() +" / "+e.getY());

				if(SwingUtilities.isRightMouseButton(e)){
					if(rootVertice != null){
						Vertice selectedVertice = rootVertice.pointExists(e.getX(), e.getY());
						if(selectedVertice != null){
							if(selectedVertice == rootVertice){
								rootVertice = null;
							} else {
								selectedVertice.delete();
							}
						}
					}
				} else {

					if(rootVertice == null){
						rootVertice = new TDTIVertice("root", null, algo);
					} else if(rootVertice.pointExists(e.getX(), e.getY()) != null){
						// add a child to this point
						TDTIVertice parent = null;
						parent = (TDTIVertice) rootVertice.pointExists(e.getX(), e.getY());
						if(parent != null){
							System.out.println("Add new Child to "+parent);
						}
						if(parent instanceof TDTIVertice){
							new TDTIVertice("test", parent, algo);
						}
					}
					updateButtons();
				}

				repaint();
			}
		});
	}
}
