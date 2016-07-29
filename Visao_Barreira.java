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

//colocar botoes embaixo da legenda para resetar tudo, e ligar/desligar grade
//Mudar a pintura do campo de modo a utilizar grandes retangulos ao inves de inumeros pequenos 

//Classe que faz os desenhos e trata o mouse
class Draw extends Canvas{

  Graphics2D g2;
  
  //Contém os quadrados que desenham o campo
  Vector<QColor> special = new Vector<QColor>();
  //Contém os quadrados que fazem a grade da matriz, e as coordenadas
  Vector<Quadrante> quadrantes = new Vector<Quadrante>();
  Vector<Stringco> coords = new Vector<Stringco>();
  //Parte que compõem a legenda
  Vector<QColor> legenda = new Vector<QColor>();
  Vector<Stringco> legenda2 = new Vector<Stringco>();
  Vector<Quadrante> legenda3 = new Vector<Quadrante>();
  //Desenha as areas de selção do robo,bola, e do gol
  Vector<QColor> area_bola = new Vector<QColor>();
  Vector<QColor> area_robo = new Vector<QColor>();
  Vector<QColor> area_gol = new Vector<QColor>();
  //Desenham os limites em volta das areas de seleção
  Quadrante robo_area;
  Quadrante bola_area;
  Quadrante gol_esq;
  Quadrante gol_dir;
  //Desenha o quadrado do robo que segue o mouse
  QColor aux = new QColor (2000,2000,new Color(100,100,100));
  Ellipse2D.Float aux2 = new Ellipse2D.Float (2000,2000,20,20);
  //Desenha a circunferencia da bola que segue o mouse
  Stringco instrucao= new Stringco("Posicione o robo",900,35);
  Ellipse2D.Float blegenda = new Ellipse2D.Float (910,365,18,18);
  //Determinam as areas de seleção que aparecerão na tela
  boolean bolaboo=false;
  boolean roboboo=true;
  boolean golboo=false;
  //Guardam as coordenadas finais dos elementos e os desenham nesse local
  int xrobo,yrobo;
  QColor robo;
  int xbola,ybola;
  Ellipse2D.Float bola;
  int xobj,yobj;
  QColor objetivo;
  //Listener do mouse e auxiliares
  MouseAdapter mouseAdapter;
  int px,py; 
  
  Draw () {
	
	mouseAdapter = new MouseAdapter() {

      //Recebe todas as informações do mouse
	  void mouse(MouseEvent e) {
		
	    roboMouse(e);
	    bolaMouse(e);

	  }
	  
	  //Mantém o robo seguindo o mouse
	  public void roboMouse (MouseEvent e) {
		
		if (roboboo&&robo_area.rect.contains(e.getX(),e.getY())) {
		  
		  aux = new QColor (e.getX()-10,e.getY()-10,new Color(100,100,100));
		  
		  if (px!=e.getX()||py!=e.getY()) {
		  
		    repaint (px-10,py-10,40,40);
		    px=e.getX();
		    py=e.getY();
		  
	      }
		
	    }
		  
	  }
	  
	  //Mantém a bola seguindo o mouse
	  public void bolaMouse (MouseEvent e) {
		
		if (bolaboo&&bola_area.rect.contains(e.getX(),e.getY())) {
		  
		  aux2 = new Ellipse2D.Float (e.getX()-10,e.getY()-10,20,20);
		  
		  if (px!=e.getX()||py!=e.getY()) {
		  
		    repaint (px-10,py-10,40,40);
		    px=e.getX();
		    py=e.getY();
		  
	      }
		
	    }
		  
	  }

      //Recebe informações de quando o mouse for clicado
	  @Override
	  public void mousePressed(MouseEvent e) {
		  
		posicionaObj(e);
		posicionaBola(e);
		posicionaRobo(e);
	  
	  }
	  
	  //define e guarda as coordenadas finais do robo
	  public void posicionaRobo(MouseEvent e) {
		
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
		
	  }
	  
	  //define e guarda as coordenadas finais da bola
	  public void posicionaBola(MouseEvent e) {
		
		if (bolaboo) {
		  
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
		
	  }
	  
	  //define e guarda as coordenadas finais do objetivo
	  public void posicionaObj(MouseEvent e) {
		
		if (golboo) {
			
		  if (gol_esq.rect.contains(e.getX(),e.getY())) {
			
			objetivo = new QColor (43*20,15*20,new Color(96,0,48));
			xobj=860;
			yobj=310;
			golboo=false;
			instrucao= null;
			repaint();
			  
		  }
		  
		  else if (gol_dir.rect.contains(e.getX(),e.getY())) {
			
			objetivo = new QColor (43*20,20*20,new Color(96,0,48));
			xobj=860;
			yobj=410;
			golboo=false;
			instrucao= null;
			repaint();
			  
		  }
		  	
		}
		
	  }
	  
	  //Recebe informações de quando o mouse for movido
	  @Override
	  public void mouseMoved(MouseEvent e) {
	  
		mouse(e);
		
	  }
      
      //Recebe informações de quando o mouse for clicado e arrastado
	  @Override
	  public void mouseDragged(MouseEvent e) {
		
		mouse(e);
			
	  }
      
      //Recebe informações de quando o botão do mouse for solto
	  @Override
	  public void mouseReleased(MouseEvent e) {
		 
		mouse(e);
	  
	  }
	  
    };
	
	//Adiciona os listeners na classe
	addMouseListener(mouseAdapter);
    addMouseMotionListener(mouseAdapter);
	  
  
  }
  
  //Faz os desenhos na tela
  @Override
  public void paint(Graphics g) {
	
	super.paint(g);
	g2 = (Graphics2D) g;
        
    campo(g2);
    //grid(g2);
    ajuda(g2);
    posicionar(g2);
	  
  }
  
  //Desenha as diferentes cores que compõem o campo
  public void campo (Graphics2D g2) {
   
	for (QColor qc:special) {
	 
	 g2.setColor(qc.c);
	 g2.fillRect(qc.x*20+20,qc.y*20+20,20,20);
	 g2.setColor(getForeground());
	 
	}
	  
  }
  
  //Desenha a grade de matriz e suas coordenadas
  public void grid (Graphics2D g2) {
	
	for (Quadrante q:quadrantes) 
      
      g2.draw(q.rect);
      
    for (Stringco s:coords) 
      
      g2.drawString(s.str,s.x,s.y);
	  
  }
  
  //Desenha a legenda e as instruções
  public void ajuda (Graphics2D g2) {
	
	for (QColor qc:legenda) {
	 
	 g2.setColor(qc.c);
	 g2.fillRect(qc.x+1,qc.y+1,19,19);
	 g2.setColor(getForeground());
	 
	}
	
	for (Stringco s:legenda2) 
      
      g2.drawString(s.str,s.x,s.y);
	
	for (Quadrante q:legenda3) 
      
      g2.draw(q.rect);
	
	g2.draw(blegenda);
	g2.setColor(new Color (196,105,77));
	g2.fillArc((int)blegenda.getX(),(int)blegenda.getY(),(int)blegenda.getWidth(),(int)blegenda.getHeight(),0,360);
	g2.setColor(getForeground());
	
	if (instrucao!=null) g2.drawString(instrucao.str,instrucao.x,instrucao.y);
	  
  }
  
  //Desenha as áreas de seleção dos elementos e redesenha o robo e a bola que seguem o mouse alem das coordenadas finais do mesmo e do objetivo
  public void posicionar (Graphics2D g2) {
	
	if (roboboo) {
	  
	  for (QColor qc:area_robo) {
	 
	    g2.setColor(qc.c);
	    g2.fillRect(qc.x*20+20,qc.y*20+20,20,20);
	    g2.setColor(getForeground());
	    
	  }
	  
	  g2.draw(robo_area.rect);
	  
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
	    g2.fillRect(qc.x*20+20,qc.y*20+20,20,20);
	    g2.setColor(getForeground());
	    
	  }
	  
	  g2.draw(bola_area.rect);
	  
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
	    g2.fillRect(qc.x*20+20,qc.y*20+20,20,5*20);
	    g2.setColor(getForeground());
	    
	  }
	  
	  g2.draw(gol_esq.rect);
	  g2.draw(gol_dir.rect);
	  	
    }
    
    else if (!roboboo&&!bolaboo) {
	  
	  g2.setColor(objetivo.c);
	  g2.fillRect(objetivo.x,objetivo.y,20,20);
	  g2.setColor(getForeground());
	  	
	}
	  
  }
  	
}

//Classe que guarda as String que serão impressas e suas coordenadas
class Stringco {
  
  String str;
  int x,y;
  
  Stringco (String aux,int a,int b) {  
    
    str=aux;
    x=a;
    y=b;
    
  } 
  	
}

//Classe que guarda os inumeros retangulos que compõem o programa
class Quadrante {
	
  Rectangle2D.Float rect;
  
  Quadrante (int x,int y,int w,int h) {
	
	rect = new Rectangle2D.Float(x,y,w,h);
	  
  }
  	
}

//Classe que armazena as coordenadas iniciais e cores impressas
class QColor {
  
  Color c;
  int x,y;
  
  QColor (int x,int y,Color c) {
    
    this.x=x;
    this.y=y;
    this.c=c;
      
  }
  	
}

//Classe que inicia as coisas que são impressas inicialmente
class Inicio {
	
  int i,j;
  Draw draw;
  private int offset=20;
  JFrame jan;
  Dimension dimension=null;
  
  //Chama as funções auxiliares
  Inicio (Draw aux,JFrame jan) {
	  
	draw=aux;
	this.jan=jan;
	
	prepGrid();
	prepCampo();
	prepSelecao();
	prepLegenda();
        
  }
  
  //Prepara a grade de matriz e suas coordenadas
  public void prepGrid () {
	
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
	  
  }
  
  //Prepara o desengo do campo
  public void prepCampo () {
	
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
	  
  }
  
  //Prepara o desenho das áreas de seleção
  public void prepSelecao() {
	
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
	    
	draw.gol_esq = new Quadrante (43*20,13*20,19,5*20);
	draw.gol_dir = new Quadrante (43*20,18*20,19,5*20);
	  
  }
  
  //Prepara os elemento que compõem a legenda
  public void prepLegenda () {
	
	offset=265;
	
	draw.legenda3.add(new Quadrante(900,offset,160,190));
	
	draw.legenda3.add(new Quadrante(910,offset+10,20,20));
	draw.legenda.add(new QColor (910,offset+10,new Color (0,18,175)));
	draw.legenda2.add(new Stringco("- Gol do Time",935,offset+25));
	
	draw.legenda3.add(new Quadrante(910,offset+40,20,20));
	draw.legenda.add(new QColor (910,offset+40,new Color (209,0,0)));
	draw.legenda2.add(new Stringco("- Gol do Adversário",935,offset+55));
	
	draw.legenda3.add(new Quadrante(910,offset+70,20,20));
	draw.legenda.add(new QColor (910,offset+70,new Color (100,100,100)));
	draw.legenda2.add(new Stringco("- Robo",935,offset+85));
	
	draw.legenda2.add(new Stringco("- Bola",935,offset+115));
	
	draw.legenda3.add(new Quadrante(910,offset+130,20,20));
	draw.legenda.add(new QColor (910,offset+130,new Color (0,255,100)));
	draw.legenda2.add(new Stringco("- Área de Seleção",935,offset+145));
	
	draw.legenda3.add(new Quadrante(910,offset+160,20,20));
	draw.legenda.add(new QColor (910,offset+160,new Color (96,0,48)));
	draw.legenda2.add(new Stringco("- Objetivo",935,offset+175));
	  
  }
  
  //Função que centraliza a janela na tela
  public void centreWindow(JFrame frame) {
	
    dimension = Toolkit.getDefaultToolkit().getScreenSize();
    int x = (int) ((dimension.getWidth() - frame.getWidth()) / 2);
    int y = (int) ((dimension.getHeight() - frame.getHeight()) / 2);
    frame.setLocation(x, y);
    
  }
  	
}

//Classe que contém a main
class Visao_Barreira {
  
  //Cria as instâncias e inicia o programa
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
