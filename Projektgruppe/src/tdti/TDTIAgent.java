package tdti;

import java.awt.Color;
import java.awt.Graphics;

public class TDTIAgent {

  private TDTIVertice start;
  private TDTIVertice end;
  private int progress = 0;
  private int numberOfAgents = 1;
  private int maximumNumberOfAgents = 3;

  public TDTIAgent(TDTIVertice sender, TDTIVertice receiver, int agents, int maxAgents){
    this.start = sender;
    this.end = receiver;
    this.numberOfAgents = agents;
    this.maximumNumberOfAgents = maxAgents;
  }
  public TDTIAgent(TDTIVertice sender, TDTIVertice receiver, int agents){
    this(sender,receiver,agents,6);
  }
  public TDTIAgent(TDTIVertice sender, TDTIVertice receiver){
    this(sender,receiver,1);
  }

  public void nextStep(){
    progress = 100;
  }

  @Override
  public String toString() {
    return "Agent ("+this.numberOfAgents+") from "+this.start.getName()+" to "+this.end.getName();
  }

  public TDTIVertice getStart(){
    return start;
  }

  public TDTIVertice getEnd(){
    return end;
  }

  public int getNumberOfAgents(){
    return numberOfAgents;
  }

  public void setVertice(TDTIVertice newVertice){
    start = newVertice;
  }

  public void draw(Graphics g){
    double p = progress/100;
    double q = 1-p;
    int xMittel = (int)(start.getMittelX()*q) + (int)(end.getMittelX()*p);
    int yMittel = (int)(start.getMittelY()*q) + (int)(end.getMittelY()*p);
    int diameter = 30;
    Color[] colors = new Color[3];
    colors[0] = new Color(0xff,0x55,0x00);
    colors[1] = new Color(0xcc,0x11,0x00);
    colors[2] = new Color(0x99,0x00,0x33);

    int arc = (360/maximumNumberOfAgents);
    for(int a=0; a<numberOfAgents; a++){
      g.setColor(colors[a%3]);
      g.drawArc(xMittel - diameter/2, yMittel - diameter/2, diameter, diameter, a*arc+10+90, arc-20);
    }
    System.out.println("Drawing Agent "+this);
  }
}
