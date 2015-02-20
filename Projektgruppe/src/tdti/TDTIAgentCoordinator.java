package tdti;

import java.util.*;
import java.awt.Graphics;

public class TDTIAgentCoordinator {

  static TDTIAgentCoordinator coordinator;
  private TDTIVertice base;
  private int numberOfAgents;
  private List<TDTIAgent> agents = new ArrayList<TDTIAgent>();

  private TDTIAgentCoordinator(){
    super();
  }

  public static TDTIAgentCoordinator getCoordinator(){
    if(coordinator == null){
      coordinator = new TDTIAgentCoordinator();
    }
    return coordinator;
  }

  public void setBase(TDTIVertice newBase){
    if(base == newBase){
      return;
    }
    this.base = newBase;
    if(base != null){
      this.numberOfAgents = base.getPsi();
      base.decontaminate();
    } else {
      this.numberOfAgents = 0;
    }
    this.agents.clear();
    for(int a=0; a<numberOfAgents; a++){
      this.agents.add(new TDTIAgent(base,base,a,numberOfAgents));
    }
  }

  public void moveAgents(){
    TDTIAgent agent = this.agents.get(this.agents.size()-1);
    TDTIVertice nextVertice = agent.getVertice().getContaminatedNeighborWithSmallestMessage();
    MessageData msg = agent.getVertice().getSmallestContaminatedNeighborMessage();
    if(nextVertice != null){
      agent.setVertice(nextVertice);
    } else {
      System.out.println("Done");
    }
  }

  public void drawAgents(Graphics g){
    for(TDTIAgent agent : this.agents){
      agent.draw(g);
    }
  }

  public void clear(){
    agents.clear();
    base = null;
  }

  public TDTIVertice getBase(){
    return this.base;
  }

}
