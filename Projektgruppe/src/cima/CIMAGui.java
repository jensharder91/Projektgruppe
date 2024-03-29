package cima;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.SwingUtilities;

import cima.Gui;
import cima.Vertice;

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
		}

		if(repaintBool){
			repaint();
		}
	}

	@Override
	protected void addGuiMouseListener() {
		addMouseListener(new MouseListener() {

			@Override
			public void mouseReleased(MouseEvent e) {}

			@Override
			public void mousePressed(MouseEvent e) {}

			@Override
			public void mouseExited(MouseEvent e) {}

			@Override
			public void mouseEntered(MouseEvent e) {}

			@Override
			public void mouseClicked(MouseEvent e) {

				if(editorOn){
					editTree(e);
				}else{
					showInfos(e);
				}

				repaint();
			}
		});
	}
	
	private void editTree(MouseEvent e){
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
					((CIMAVertice) edgeWeigthOwner).getEdgeWeightToParent().edgeWeightDepress();
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
					((CIMAVertice) edgeWeigthOwner).getEdgeWeightToParent().edgeWeightIncrease();;
					treeChanged();
				}
			}
		}
	}
	
	private void showInfos(MouseEvent e){
		
		if(rootVertice != null){
			((CIMAVertice) rootVertice).algorithmus();
			((CIMAVertice) rootVertice).resetDrawPotentialData(); //TODO reset for edges
		}
		
		//clicked on a node?
		CIMAVertice selectedVertex = (CIMAVertice) rootVertice.getPoint(e.getX(), e.getY());
		if(selectedVertex != null){
			selectedVertex.drawPotentialDataForThisNode(true);
		}else{
			//clicked on a edgeWeightOval?
			Vertice edgeWeigthOwner = ((CIMAVertice) rootVertice).edgeWeightOvalExists(e.getX(), e.getY());
			if(edgeWeigthOwner != null){
				((CIMAVertice) edgeWeigthOwner).getEdgeWeightToParent().setDrawPotentialData(true);
			}
		}
		
		System.out.println("showInfos...");
		
		repaint();
	}
	
	private void treeChanged(){
		CIMAVertice.drawMu = false;
		CIMAAnimation.afterMessageDataCalc = false;
		CIMAAnimation.getCIMAAnimation().stopAllAnimations();
		if(rootVertice != null){
			((CIMAVertice) rootVertice).reset();
		}
	}

}
