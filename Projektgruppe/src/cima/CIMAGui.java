package cima;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.SwingUtilities;

import Gui.Gui;
import Tree.Vertice;

public class CIMAGui extends Gui{

	private static final long serialVersionUID = 1L;
	private static CIMAGui gui;

	private int verticeCoutner = 0;


	public static CIMAGui getGui(){
		if(gui == null){
			gui = new CIMAGui();
		}
		return gui;
	}



	@Override
	protected void calcAlgorithmus(boolean repaintBool) {

		if(rootVertice instanceof CIMAVertice){
			((CIMAVertice) rootVertice).algorithmus();
			homeBase = ((CIMAVertice) rootVertice).findHomeBase();
//			((CIMAVertice) homeBase).calcAgentsMove();
		}

		if(repaintBool){
			repaint();
		}
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
				
//				CIMAAnimation.getCIMAAnimation().stopAllAnimations();

				if(SwingUtilities.isRightMouseButton(e)){
					if(rootVertice != null){
						//check if its a vertice  -> LÖSCHEN
						Vertice selectedVertice = rootVertice.getPoint(e.getX(), e.getY());
						if(selectedVertice != null){
							if(selectedVertice == rootVertice){
								clearGui();
							}else{
								MessageData.clearGui = true;
								selectedVertice.delete();
							}
							treeChanged();
						}
					}
					if(rootVertice != null){
						//check if its a edgeWeightOval ->edgeWeight -1
						Vertice edgeWeigthOwner = ((CIMAVertice) rootVertice).edgeWeightOvalExists(e.getX(), e.getY());
						if(edgeWeigthOwner != null){
							((CIMAVertice) edgeWeigthOwner).edgeWeightDepress();
							treeChanged();
						}
					}
				//linksklick
				}else{

					// kein root ?  -> neues Kind
					if(rootVertice == null){
						verticeCoutner  = 1;
						rootVertice = new CIMAVertice(""+verticeCoutner, null);
						verticeCoutner++;
						treeChanged();
					//add child
					}else if(rootVertice.getPoint(e.getX(), e.getY()) != null){
						// add a child to this point
						CIMAVertice parent = null;
						parent = (CIMAVertice) rootVertice.getPoint(e.getX(), e.getY());
						if(parent != null){
						}
						if(parent instanceof CIMAVertice){
							new CIMAVertice(""+verticeCoutner, parent);
							MessageData.clearGui = true;
							verticeCoutner++;
							treeChanged();
						}
					
					}else {
						//check if its a edgeWeightOval ->edgeWeight +1
						Vertice edgeWeigthOwner = ((CIMAVertice) rootVertice).edgeWeightOvalExists(e.getX(), e.getY());
						if(edgeWeigthOwner != null){
							((CIMAVertice) edgeWeigthOwner).edgeWeightIncrease();;
							treeChanged();
						}
					}
				}

				repaint();
			}
		});
	}
	
	private void treeChanged(){
		CIMAVertice.drawMu = false;
		CIMAAnimation.getCIMAAnimation().stopAllAnimations();
		if(rootVertice != null){
			((CIMAVertice) rootVertice).reset();
		}
	}

}
