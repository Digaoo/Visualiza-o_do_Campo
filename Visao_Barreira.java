import java.awt.Dimension;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.Vector;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.JButton;
import javax.swing.Timer;
import javax.imageio.ImageIO;
import java.io.File;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import javax.swing.JComponent;
import java.awt.Canvas;
import java.util.Vector;
import java.awt.Color;
import javax.swing.JSpinner;
import javax.swing.JSpinner.DefaultEditor;
import javax.swing.SpinnerModel;
import javax.swing.SpinnerNumberModel;
import java.awt.BorderLayout;
import javax.swing.JComboBox;
import java.awt.geom.Ellipse2D;
import javax.swing.JOptionPane;
import java.awt.Toolkit;
import java.awt.event.WindowFocusListener;
import java.awt.event.WindowEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;

class Draw extends Canvas{

  Graphics2D g2;
  Vector<Quadrante> quadrantes = new Vector<Quadrante>();
  Vector<Stringco> coords = new Vector<Stringco>();
  Vector<QColor> special = new Vector<QColor>();
  Vector<QColor> legenda = new Vector<QColor>();
  Vector<QColor> area_bola = new Vector<QColor>();
  Vector<QColor> area_robo = new Vector<QColor>();
  Vector<QColor> area_gol = new Vector<QColor>();
  
  Quadrante robo_area;
  Quadrante bola_area;
  Quadrante gol_esq;
  Quadrante gol_dir;
  
  QColor aux = new QColor (2000,2000,new Color(100,100,100));
  Ellipse2D.Float aux2 = new Ellipse2D.Float (2000,2000,20,20);
  
  Stringco instrucao= new Stringco("Posicione o robo",900,35);
  Ellipse2D.Float blegenda = new Ellipse2D.Float (910,365,18,18);
  
  boolean bolaboo=false;
  boolean roboboo=true;
  boolean golboo=false;
  
  int xrobo,yrobo;
  QColor robo;
  int xbola,ybola;
  Ellipse2D.Float bola;
  int xobj,yobj;
  QColor objetivo;
  
  int px,py;
  MouseAdapter mouseAdapter;
  
  //se achar necessario, fazer funçao para percorrer vector d cor e retirar iteraçoes de mesmas coordenadas (atraves de matriz auxiliar)
  //colocar botoes embaixo da legenda para resetar tudo, e para fechar o programa
  //fazer esquema de função principal chamando subfunçoes para cada tarefa para melhorar visualização do codigo 
  Color c;
  
  Draw () {
	
	mouseAdapter = new MouseAdapter() {

	  void mouse(MouseEvent e) {
		
		if (roboboo&&robo_area.rect.contains(e.getX(),e.getY())) {
		  
		  aux = new QColor (e.getX()-10,e.getY()-10,new Color(100,100,100));
		  
		  if (px!=e.getX()||py!=e.getY()) {
		  
		    repaint (px-10,py-10,40,40);
		    px=e.getX();
		    py=e.getY();
		  
	      }
		
	    }
	    
	    else if (bolaboo&&bola_area.rect.contains(e.getX(),e.getY())) {
		  
		  aux2 = new Ellipse2D.Float (e.getX()-10,e.getY()-10,20,20);
		  
		  if (px!=e.getX()||py!=e.getY()) {
		  
		    repaint (px-10,py-10,40,40);
		    px=e.getX();
		    py=e.getY();
		  
	      }
		
	    }

	  }

	  @Override
	  public void mouseMoved(MouseEvent e) {
	  
		mouse(e);
		
	  }

	  @Override
	  public void mouseDragged(MouseEvent e) {
		
		mouse(e);
			
	  }

	  @Override
	  public void mousePressed(MouseEvent e) {
		
		if (roboboo) {
		  
		  if (robo_area.rect.contains(e.getX(),e.getY())) {
		  
		    xrobo=e.getX();
		    yrobo=e.getY();
		    
		  }
		  
		  else {
			  
			xrobo=aux.x;
			yrobo=aux.y;
			  
		  }
		  
		  robo = new QColor (xrobo,yrobo,new Color(100,100,100));
		  roboboo=false;
		  bolaboo=true;
		  instrucao= new Stringco("Posicione a bola",900,35);
		  repaint();
		
	    }
	    
	    else if (bolaboo) {
		  
		  if(bola_area.rect.contains(e.getX(),e.getY())) {
		  
		    xbola=e.getX();
		    ybola=e.getY();
		  
	      }
	      
	      else {
			
			xbola=(int)aux2.x;
			ybola=(int)aux2.y;
			  
		  }
		  
		  instrucao= new Stringco("Selecione o lado objetivo",900,35);
		  bola = new Ellipse2D.Float (e.getX()-10,e.getY()-10,20,20);
		  bolaboo=false;
		  golboo=true;
		  repaint();
		
	    }
	    
	    else if (golboo) {
			
		  if (gol_esq.rect.contains(e.getX(),e.getY())) {
			
			objetivo = new QColor (43*20+1,15*20+1,new Color(96,0,48));
			xobj=860;
			yobj=310;
			golboo=false;
			repaint();
			  
		  }
		  
		  else if (gol_dir.rect.contains(e.getX(),e.getY())) {
			
			objetivo = new QColor (43*20+1,20*20+1,new Color(96,0,48));
			xobj=860;
			yobj=410;
			golboo=false;
			repaint();
			  
		  }
		  	
		}
	  
	  }

	  @Override
	  public void mouseReleased(MouseEvent e) {
		 
		mouse(e);
	  
	  }
	  
    };
	  
	addMouseListener(mouseAdapter);
    addMouseMotionListener(mouseAdapter);
	  
  
  }
  
  @Override
  public void paint(Graphics g) {
	
	super.paint(g);
	g2 = (Graphics2D) g;
	
	for (Quadrante q:quadrantes) 
      
      g2.draw(q.rect);
        
    for (Stringco s:coords) 
      
      g2.drawString(s.str,s.x,s.y);
   
	for (QColor qc:special) {
	 
	 g2.setColor(qc.c);
	 g2.fillRect(qc.x*20+21,qc.y*20+21,19,19);
	 g2.setColor(getForeground());
	 
	}
	
	for (QColor qc:legenda) {
	 
	 g2.setColor(qc.c);
	 g2.fillRect(qc.x+1,qc.y+1,19,19);
	 g2.setColor(getForeground());
	 
	}
	
	g2.draw(blegenda);
	g2.setColor(new Color (196,105,77));
	g2.fillArc((int)blegenda.getX(),(int)blegenda.getY(),(int)blegenda.getWidth(),(int)blegenda.getHeight(),0,360);
	g2.setColor(getForeground());
	
	if (instrucao!=null) g2.drawString(instrucao.str,instrucao.x,instrucao.y);
	
	if (roboboo) {
	  
	  for (QColor qc:area_robo) {
	 
	    g2.setColor(qc.c);
	    g2.fillRect(qc.x*20+21,qc.y*20+21,19,19);
	    g2.setColor(getForeground());
	    
	  }
	  
	  g2.setColor(aux.c);
	  g2.fillRect(aux.x,aux.y,20,20);
	  g2.setColor(getForeground());
	  	
	}
	
	else if (!roboboo) {
	  
	  g2.setColor(robo.c);
	  g2.fillRect(robo.x,robo.y,20,20);
	  g2.setColor(getForeground());
	  	
	}
	
	if (bolaboo) {
	  
	  for (QColor qc:area_bola) {
	 
	    g2.setColor(qc.c);
	    g2.fillRect(qc.x*20+21,qc.y*20+21,19,19);
	    g2.setColor(getForeground());
	    
	  }
	  
	  g2.setColor(new Color (196,105,77));
	  g2.fillArc((int)aux2.getX(),(int)aux2.getY(),(int)aux2.getWidth(),(int)aux2.getHeight(),0,360);
	  g2.setColor(getForeground());
	  	
	}
	
	else if (!roboboo) {
	  
	  g2.setColor(new Color (196,105,77));
	  g2.fillArc((int)bola.getX(),(int)bola.getY(),(int)bola.getWidth(),(int)bola.getHeight(),0,360);
	  g2.setColor(getForeground());
	  	
	}
	
	if (golboo) {
	  
	  for (QColor qc:area_gol) {
	 
	    g2.setColor(qc.c);
	    g2.fillRect(qc.x*20+21,qc.y*20+21,19,5*20-1);
	    g2.setColor(getForeground());
	    
	  }
	  	
    }
    
    else if (!roboboo&&!bolaboo) {
	  
	  g2.setColor(objetivo.c);
	  g2.fillRect(objetivo.x,objetivo.y,19,19);
	  g2.setColor(getForeground());
	  	
	}
	  
  }
  	
}

class Stringco {
  
  String str;
  int x,y;
  
  Stringco (String aux,int a,int b) {  
    
    str=aux;
    x=a;
    y=b;
    
  } 
  	
}

class Quadrante {
	
  Rectangle2D.Float rect;
  
  Quadrante (int x,int y,int w,int h) {
	
	rect = new Rectangle2D.Float(x,y,w,h);
	  
  }
  	
}

class QColor {
  
  Color c;
  int x,y;
  
  QColor (int x,int y,Color c) {
    
    this.x=x;
    this.y=y;
    this.c=c;
      
  }
  	
}

class Ponto {
	
  
  
  Ponto () {
    
    
      
  }
  	
}

class Inicio {
	
  int i,j;
  Draw draw;
  private int offset=20;
  JFrame jan;
  JFrame warning;
  int xb,yb,xr,yr;
  Dimension dimension=null;
  
  Inicio (Draw aux,JFrame jan) {
	  
	draw=aux;
	this.jan=jan;
	
	for (i=0;i<34;i++)
      for (j=0;j<43;j++)
        draw.quadrantes.add(new Quadrante(offset+20*j,offset+20*i,20,20));
        
    for (i=0;i<43;i++) {
		
	  
	  if (i<10) draw.coords.add(new Stringco(i+" ",offset+6+20*i,offset));
	  
	  else draw.coords.add(new Stringco(i+" ",offset+2+20*i,offset));
	  
	}
    
    for (i=0;i<34;i++) {
		
	  
	  if (i<10) draw.coords.add(new Stringco(i+" ",offset-8,offset+15+20*i));
	  
	  else draw.coords.add(new Stringco(i+" ",offset-16,offset+15+20*i));
	  
	}
	
	for (i=0;i<34;i++) draw.special.add(new QColor (0,i,new Color (0,0,0)));
	
	for (i=0;i<43;i++) draw.special.add(new QColor (i,0,new Color (0,0,0)));
	
	for (i=0;i<34;i++) draw.special.add(new QColor (42,i,new Color (0,0,0)));
	
	for (i=0;i<43;i++) draw.special.add(new QColor (i,33,new Color (0,0,0)));
	
	for (i=12;i<22;i++) draw.special.add(new QColor (42,i,new Color (209,0,0)));
	
	for (i=12;i<22;i++) draw.special.add(new QColor (0,i,new Color (0,18,175)));
	
	for (i=1;i<42;i++)
	  for (j=1;j<33;j++)
	  
	    draw.special.add(new QColor (i,j,new Color (15,115,45)));
	    
	for (i=1;i<33;i++) draw.special.add(new QColor (21,i,new Color (255,255,255)));
	
	for (i=8;i<26;i++) {
	
	  draw.special.add(new QColor (38,i,new Color (255,255,255)));
      draw.special.add(new QColor (4,i,new Color (255,255,255)));
      
    }
	
	for (i=0;i<4;i++) {
	
	  draw.special.add(new QColor (i+1,8,new Color (255,255,255)));
	  draw.special.add(new QColor (i+1,25,new Color (255,255,255)));
	  draw.special.add(new QColor (i+38,8,new Color (255,255,255)));
	  draw.special.add(new QColor (i+38,25,new Color (255,255,255)));
	  
	}
	
	for (i=7;i<20;i++)
	  for (j=2;j<32;j++)
	  
	    draw.area_robo.add(new QColor (i,j,new Color (0,255,100)));
	    
	draw.robo_area = new Quadrante (7*20+20,2*20+20,13*20,30*20);
	    
	for (i=23;i<37;i++)
	  for (j=3;j<31;j++)
	  
	    draw.area_bola.add(new QColor (i,j,new Color (0,255,100)));
	    
	draw.bola_area = new Quadrante (23*20+20,3*20+20,14*20,28*20);
	
	draw.area_gol.add(new QColor (42,12,new Color (0,255,100)));
	draw.area_gol.add(new QColor (42,17,new Color (0,255,100)));
	    
	draw.gol_esq = new Quadrante (42*20+20,12*20+20,20,5*20);
	draw.gol_dir = new Quadrante (42*20+20,17*20+20,20,5*20);
	
	offset=265;
	
	draw.quadrantes.add(new Quadrante(900,offset,160,190));
	
	draw.quadrantes.add(new Quadrante(910,offset+10,20,20));
	draw.legenda.add(new QColor (910,offset+10,new Color (0,18,175)));
	draw.coords.add(new Stringco("- Gol do Time",935,offset+25));
	
	draw.quadrantes.add(new Quadrante(910,offset+40,20,20));
	draw.legenda.add(new QColor (910,offset+40,new Color (209,0,0)));
	draw.coords.add(new Stringco("- Gol do Adversário",935,offset+55));
	
	draw.quadrantes.add(new Quadrante(910,offset+70,20,20));
	draw.legenda.add(new QColor (910,offset+70,new Color (100,100,100)));
	draw.coords.add(new Stringco("- Robo",935,offset+85));
	
	draw.coords.add(new Stringco("- Bola",935,offset+115));
	
	draw.quadrantes.add(new Quadrante(910,offset+130,20,20));
	draw.legenda.add(new QColor (910,offset+130,new Color (0,255,100)));
	draw.coords.add(new Stringco("- Área de Seleção",935,offset+145));
	
	draw.quadrantes.add(new Quadrante(910,offset+160,20,20));
	draw.legenda.add(new QColor (910,offset+160,new Color (96,0,48)));
	draw.coords.add(new Stringco("- Objetivo",935,offset+175));
        
  }
  
  public void setPosition() {
	
	draw.bola = new Ellipse2D.Float (xb*20+21,yb*20+21,18,18);
	draw.special.add(new QColor (xr,yr,new Color (150,100,250)));
	draw.repaint();
	  
  }
  
  public void centreWindow(JFrame frame) {
	
    dimension = Toolkit.getDefaultToolkit().getScreenSize();
    int x = (int) ((dimension.getWidth() - frame.getWidth()) / 2);
    int y = (int) ((dimension.getHeight() - frame.getHeight()) / 2);
    frame.setLocation(x, y);
    
  }
  	
}

class Visao_Barreira {
  
  public static void main (String[] args) {
	  
	int i,j;
    
    JFrame jan = new JFrame ("Campo");
    jan.setLayout(new BorderLayout());
    Draw draw = new Draw();
    Inicio in = new Inicio(draw,jan);
    jan.setSize(new Dimension (1100,800));
    jan.add(draw);
	jan.setResizable(false);
    jan.setVisible(true);
    in.centreWindow(jan);
    
  }
  
}
