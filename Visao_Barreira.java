import java.awt.Dimension;
import java.io.IOException;
import java.util.Vector;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.JButton;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import java.awt.Canvas;
import java.awt.Color;
import java.awt.BorderLayout;
import java.awt.geom.Ellipse2D;
import java.awt.Toolkit;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.Timer;
import java.awt.geom.Line2D;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import javax.swing.ImageIcon;
import java.awt.Image;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;

//Classe que gera o menu inicial
class Menu {
	
  Visao_Barreira vb;
  GridBagConstraints gbc = new GridBagConstraints();
  ImageIcon icone = new ImageIcon (getClass().getResource("botao_carrossel.png"));
  JButton func = new JButton("Gera Função",icone);
  JButton est = new JButton(" Estatégia ",icone);
  Color c;
  
  //Prepara a tela inicial do menu
  Menu(Visao_Barreira v) {
	
	vb=v;
	c=vb.jan.getContentPane().getBackground();
	vb.jan.getContentPane().setBackground(new Color(170,0,170));
	vb.jan.setLayout(new GridBagLayout());
	gbc.fill=gbc.HORIZONTAL;
	gbc.gridx=0;
	gbc.gridy=0;
	gbc.insets = new Insets(50,0,50,0);
	vb.jan.add(func,gbc);
	func.setPreferredSize(new Dimension (300,100));
	gbc.gridy=1;
	vb.jan.add(est,gbc);
	est.setPreferredSize(new Dimension (300,100));
	vb.jan.setSize(new Dimension (1100,800));
	listeners();
	  
  }
  
  //Prepara as ações dos botões do menu
  public void listeners () {
	
	func.addActionListener( new ActionListener() {
	  
	  @Override
	  public void actionPerformed(ActionEvent e) {
		
		vb.jan.getContentPane().setBackground(c);
        vb.jan.setLayout(null);
        vb.jan.remove(func);
        vb.jan.remove(est);
        vb.jan.add(vb.grade);
        vb.jan.add(vb.reset);
        vb.jan.add(vb.rmenu);
        vb.jan.add(vb.draw);
        vb.in.centreWindow(vb.jan);
        vb.draw.opc=1;
		  
	  }
	  
	}); 
	
	est.addActionListener( new ActionListener() {
	  
	  @Override
	  public void actionPerformed(ActionEvent e) {
		
		vb.jan.getContentPane().setBackground(c);
        vb.jan.setLayout(null);
        vb.jan.remove(func);
        vb.jan.remove(est);
        vb.jan.add(vb.grade);
        vb.jan.add(vb.reset);
        vb.jan.add(vb.rmenu);
        vb.jan.add(vb.draw);
        vb.in.centreWindow(vb.jan);
        vb.draw.opc=2;
		  
	  }
	  
	}); 
	  
  }
  
  //Faz as preparações para retornar ao menu após ja ter ido adiante
  public void back () {
	
	vb.jan.getContentPane().setBackground(new Color(170,0,170));
	vb.draw.opc=0;
	vb.jan.remove(vb.grade);
    vb.jan.remove(vb.reset);
    vb.jan.remove(vb.rmenu);
    vb.jan.remove(vb.draw);
	vb.jan.setLayout(new GridBagLayout());
	gbc.gridy=0;
	vb.jan.add(func,gbc);
	gbc.gridy=1;
	vb.jan.add(est,gbc);
	
  }
  	
}

//Classe que faz os desenhos e trata o mouse
class Draw extends Canvas{

  Graphics2D g2;
  
  //Variaveis que controlam a distância em pixels do canto superio  esquerdo da tela
  int dx=55;
  int dy=25;
  //Variavel que controla a opção escolhida no menu, e como o campo será utilizado
  int opc = 0;
  //Contém os quadrados que desenham o campo
  Vector<QColor> special = new Vector<QColor>();
  //Contém os quadrados que fazem a grade da matriz, e as coordenadas
  Vector<Quadrante> quadrantes = new Vector<Quadrante>();
  Vector<Stringco> coords = new Vector<Stringco>();
  //Variaveis que desenham as dimensões do campo
  Vector<Line2D.Float> medidas = new Vector<>();
  Vector<Stringco> medidas2 = new Vector<Stringco>();
  //Parte que compõem a legenda
  Vector<QColor> legenda = new Vector<QColor>();
  Vector<Stringco> legenda2 = new Vector<Stringco>();
  Vector<Quadrante> legenda3 = new Vector<Quadrante>();
  //Desenha as areas de selção do robo,bola, e do gol
  Vector<QColor> area_bola = new Vector<QColor>();
  Vector<QColor> area_robo = new Vector<QColor>();
  Vector<QColor> area_gol = new Vector<QColor>();
  //Guarda os pontos da função que serão impressos
  Vector<Quadrante> arco = new Vector<Quadrante>();
  //Desenham os limites em volta das areas de seleção
  Quadrante robo_area;
  Quadrante bola_area;
  Quadrante gol_esq;
  Quadrante gol_dir;
  //Desenha o quadrado do robo que segue o mouse
  QColor aux = new QColor (2000,2000,20,20,new Color(100,100,100));
  Ellipse2D.Float aux2 = new Ellipse2D.Float (2000,2000,20,20);
  //Desenha a circunferencia da bola que segue o mouse
  Stringco instrucao= new Stringco("Posicione o robo",870+dx,15+dy);
  Ellipse2D.Float blegenda = new Ellipse2D.Float (880+dx,345+dy,18,18);
  //Determinam as areas de seleção que aparecerão na tela
  boolean bolaboo=false;
  boolean roboboo=true;
  boolean golboo=false;
  //Guardam as coordenadas finais dos elementos e os desenham nesse local
  Point2D.Float roboxy;
  QColor robo;
  Point2D.Float bolaxy;
  Ellipse2D.Float bola;
  Point2D.Float objxy;
  QColor objetivo;
  //Variáveis para cria a area de selação da estratégia
  Quadrante campo = new Quadrante(dx+20,dy+20,41*20,32*20);
  Vector<Quadrante> selecao = new Vector<Quadrante>();
  Quadrante buffer=new Quadrante(dx+50,dy+50,1,1);
  QColor buffer2;
  boolean selecionando=false;
  boolean proceed=true;
  int h=0,w=0;
  //Listener do mouse e auxiliares
  MouseAdapter mouseAdapter;
  int px,py;
  //Variáveis para funcinamento da geração de função
  Interpola inter;
  boolean gera=false;
  int i;
  //Variavel para controle da grade
  int grade=0;
  
  Draw () {
	
	mouseAdapter = new MouseAdapter() {

      //Recebe todas as informações do mouse
	  void mouse(MouseEvent e) {
		
		if (opc==1) {
		
	      roboMouse(e);
	      bolaMouse(e);
	      
	    }

	  }
	  
	  //Mantém o robo seguindo o mouse
	  public void roboMouse (MouseEvent e) {
		
		if (roboboo&&robo_area.rect.contains(e.getX(),e.getY())) {
		  
		  aux = new QColor (e.getX()-10,e.getY()-10,20,20,new Color(100,100,100));
		  
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
		
		if (opc==1) {
		  
		  posicionaObj(e);
		  posicionaBola(e);
		  posicionaRobo(e);
		  
		}
		
		else if (opc==2) {
		    
		  restringeCampo (e);  
			  	
		}
	  
	  }
	  
	  //define e guarda as coordenadas finais do robo
	  public void posicionaRobo(MouseEvent e) {
		
		if (roboboo) {
		  
		  if (robo_area.rect.contains(e.getX(),e.getY())) {
		  
		    roboxy = new Point2D.Float ((int)e.getX()-10,(int)e.getY()-10);
		    
		  }
		  
		  else return;
		  
		  robo = new QColor ((int)roboxy.x-10,(int)roboxy.y-10,20,20,new Color(100,100,100));
		  roboboo=false;
		  bolaboo=true;
		  instrucao= new Stringco("Posicione a bola",870+dx,15+dy);
		  repaint();
		
	    }
		
	  }
	  
	  //define e guarda as coordenadas finais da bola
	  public void posicionaBola(MouseEvent e) {
		
		if (bolaboo) {
		  
		  if(bola_area.rect.contains(e.getX(),e.getY())) {
		  
		    bolaxy = new Point2D.Float ((int)e.getX(),(int)e.getY());
		  
	      }
		  
		  else return;
		  
		  instrucao= new Stringco("Selecione o lado objetivo",870+dx,15+dy);
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
			
			objetivo = new QColor (42*20+dx,14*20+dy,20,20,new Color(96,0,48));
			objxy = new Point2D.Float ((int)840+dx,(int)290+dy);
			golboo=false;
			instrucao= null;
			gera=true;
			repaint();
			  
		  }
		  
		  else if (gol_dir.rect.contains(e.getX(),e.getY())) {
			
			objetivo = new QColor (42*20+dx,19*20+dy,20,20,new Color(96,0,48));
			objxy = new Point2D.Float ((int)840+dx,(int)390+dy);
			golboo=false;
			instrucao= null;
			gera=true;
			repaint();
			  
		  }
		  	
		}
		
	  }
	  
	  //Restinge a criação de áreas de seleção ao campo
	  public void restringeCampo (MouseEvent e) {
		  
		int aux;
		  
		selecionando=true;
		
		if (campo.rect.contains(e.getX(),e.getY())) {
		    
		  px=e.getX();
		  py=e.getY();
		      
		}
		    
		else {
			  
		  aux=campo.rect.outcode(e.getX(),e.getY());
			  
		  if(aux==Rectangle2D.OUT_TOP) {
				
		    px=e.getX();
			py=e.getY()+(20+dy-e.getY());
				  
		  }
			  
		  else if (aux==Rectangle2D.OUT_LEFT) {
			    
			px=e.getX()+(20+dx-e.getX());
		    py=e.getY();
			    
		  }
			  
		  else if (aux==Rectangle2D.OUT_BOTTOM) {
			    
			px=e.getX();
		    py=e.getY()-(int)(e.getY()-(campo.rect.y+campo.rect.height));
			  
		  }
			  
		  else if (aux==Rectangle2D.OUT_RIGHT) {
			    
			px=e.getX()-(int)(e.getX()-(campo.rect.x+campo.rect.width));
		    py=e.getY();
			    
		  }
			  
		  else if (aux==Rectangle2D.OUT_TOP+Rectangle2D.OUT_LEFT) {
			    
			px=e.getX()+(20+dx-e.getX());
		    py=e.getY()+(20+dy-e.getY());
			    
		  }
			  
		  else if (aux==Rectangle2D.OUT_TOP+Rectangle2D.OUT_RIGHT) {
			    
			px=e.getX()-(int)(e.getX()-(campo.rect.x+campo.rect.width));
		    py=e.getY()+(20+dy-e.getY());
			    
		  }
			  
		  else if (aux==Rectangle2D.OUT_BOTTOM+Rectangle2D.OUT_LEFT) {
			    
			px=e.getX()+(20+dx-e.getX());
		    py=e.getY()-(int)(e.getY()-(campo.rect.y+campo.rect.height));
			    
		  }
			  
		  else if (aux==Rectangle2D.OUT_BOTTOM+Rectangle2D.OUT_RIGHT) {
			    
			px=e.getX()-(int)(e.getX()-(campo.rect.x+campo.rect.width));
		    py=e.getY()-(int)(e.getY()-(campo.rect.y+campo.rect.height));
			    
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
		
		if (opc==2) {
			
		  criaSelecao(e);
		  
	    }  
			
	  }
	  
	  //Controla a criação da área de seleção
	  public void criaSelecao (MouseEvent e) {
		  
		h=e.getY();
		w=e.getX();
		  
		if (!campo.rect.contains(w,h)) {

		  restringeCampo2();
		  	
		}
		
		//for (Quadrante q:selecao) {
			
	      //if (q.rect.contains(e.getX(),e.getY())||q.rect.intersects(buffer.rect.x,buffer.rect.y,buffer.rect.width+1,buffer.rect.height+1))
			  
		  //proceed=false;
			  
		//}
		  
		//if (proceed) {
			
		  
		  if (px<w&&py<h) buffer = new Quadrante (px,py,w-px,h-py);
		  
		  else if (px<w&&py>h) buffer = new Quadrante (px,h,w-px,py-h);
		  
		  else if (px>w&&py<h) buffer = new Quadrante (w,py,px-w,h-py);
		  
		  else if (px>w&&py>h) buffer = new Quadrante (w,h,px-w,py-h);
		  
	    //}
	    
	    if(buffer!=null)buffer2 = new QColor ((int)buffer.rect.x,(int)buffer.rect.y,(int)buffer.rect.width,(int)buffer.rect.height,new Color (255,153,51,220));
		  
		repaint(dx+20,dy+20,41*20,32*20);
	      
	    proceed=true;
		  
	  }
	  
	  //Restringe o crescimento da área de seleção
	  public void restringeCampo2 () {
		  
		int aux=campo.rect.outcode(w,h);
			  
		if(aux==Rectangle2D.OUT_TOP) h = (int)campo.rect.y+1;
			  
		else if (aux==Rectangle2D.OUT_LEFT) w = (int)campo.rect.x+1;
			  
		else if (aux==Rectangle2D.OUT_BOTTOM) h = (int)(campo.rect.y+campo.rect.height)-1;
			  
		else if (aux==Rectangle2D.OUT_RIGHT) w = (int)(campo.rect.x+campo.rect.width)-1;
			  
		else if (aux==Rectangle2D.OUT_TOP+Rectangle2D.OUT_LEFT) {
			    
	      h = (int)campo.rect.y+1;
	      w = (int)campo.rect.x+1;
			    
		}
			  
		else if (aux==Rectangle2D.OUT_TOP+Rectangle2D.OUT_RIGHT) {
			    
	      h = (int)campo.rect.y+1;
	      w = (int)(campo.rect.x+campo.rect.width)-1;
			    
		}
			  
		else if (aux==Rectangle2D.OUT_BOTTOM+Rectangle2D.OUT_LEFT) {
			    
		  h = (int)(campo.rect.y+campo.rect.height)-1;
		  w = (int)campo.rect.x+1;
			    
		}
			  
		else if (aux==Rectangle2D.OUT_BOTTOM+Rectangle2D.OUT_RIGHT) {
			    
		  h = (int)(campo.rect.y+campo.rect.height)-1;
		  w = (int)(campo.rect.x+campo.rect.width)-1;
			    
		}
	    
	  }
      
      //Recebe informações de quando o botão do mouse for solto
	  @Override
	  public void mouseReleased(MouseEvent e) {
		 
		if (opc==2&&buffer!=null) {
		  
		  fimSelecao(e);
		  
	    }
	  
	  }
	  
	  //Finaliza a criação da área de seleção
	  public void fimSelecao (MouseEvent e) {
		
		proceed=true;
		selecionando=false;
		selecao.add(new Quadrante((int)buffer.rect.x,(int)buffer.rect.y,(int)buffer.rect.width,(int)buffer.rect.height));
		buffer=new Quadrante(dx+20,dy+20,0,0);
		  
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
    ajuda(g2);
    
    if(grade==1)grid(g2);
    else if(grade==2)grid2(g2);
    
    if(opc==1) {
    
      posicionar(g2);
      funcao(g2);
      
    }
    
    else if (opc==2) {
	  
	  select(g2);
	  
    }
	  
  }
  
  //Desenha as diferentes cores que compõem o campo
  public void campo (Graphics2D g2) {
   
	for (QColor qc:special) {
	 
	 g2.setColor(qc.c);
	 g2.fillRect(qc.x*20+dx,qc.y*20+dy,qc.w,qc.h);
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
  
  //Desenha as medidas do campo
  public void grid2 (Graphics2D g2) {
	
	for (Line2D.Float l:medidas) 
      
      g2.draw(l);
      
    for (Stringco s:medidas2) 
      
      g2.drawString(s.str,s.x,s.y);
	  
  }
  
  //Desenha a legenda e as instruções
  public void ajuda (Graphics2D g2) {
	
	for (QColor qc:legenda) {
	 
	 g2.setColor(qc.c);
	 g2.fillRect(qc.x+1,qc.y+1,qc.w,qc.h);
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
	    g2.fillRect(qc.x*20+dx,qc.y*20+dy,qc.w,qc.h);
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
	    g2.fillRect(qc.x*20+dx,qc.y*20+dy,qc.w,qc.h);
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
	    g2.fillRect(qc.x*20+dx,qc.y*20+dy,qc.w,qc.h);
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
  
  //Desenha a função que represetará a trajetória do robo,para a bolo e depois para o gol
  public void funcao (Graphics2D g2) {
	
	if (gera) {
	
	  inter = new Interpola(roboxy,bolaxy,objxy);
	  
	  for(i=(int)roboxy.x;i<=objxy.x;i++) {
	    
	    arco.add(new Quadrante(i,inter.funcao(i),0,0));
	    
	  }
	    
	  gera=false;
	  
	}
	
	if(!arco.isEmpty()) 
	
	  for (Quadrante q:arco) 
      
        g2.draw(q.rect);
	  
  }
  
  //Desenha as áreas de seleção da estratégia
  public void select (Graphics2D g2) {
    
    if(buffer.rect.width>1&&buffer.rect.height>1) {
    
      g2.draw(buffer.rect);
      g2.setColor(new Color(255,153,51,220));
	  g2.fillRect((int)buffer2.x+1,(int)buffer2.y+1,(int)buffer2.w-1,(int)buffer2.h-1);
	  g2.setColor(getForeground());
      
    }
    
    if(!selecao.isEmpty())
    
      for (Quadrante q:selecao) { 
      
        g2.draw(q.rect);
        g2.setColor(new Color(255,153,51,220));
	    g2.fillRect((int)q.rect.x+1,(int)q.rect.y+1,(int)q.rect.width-1,(int)q.rect.height-1);
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
  
  Quadrante (float x,float y,int w,int h) {
	
	rect = new Rectangle2D.Float(x,y,w,h);
	  
  }
  	
}

//Classe que armazena as coordenadas iniciais e cores impressas
class QColor {
  
  Color c;
  int x,y;
  int w,h;
  
  QColor (int x,int y,int w,int h,Color c) {
    
    this.x=x;
    this.y=y;
    this.h=h;
    this.w=w;
    this.c=c;
      
  }
  	
}

//Classe que inicia as coisas que são impressas inicialmente
class Inicio {
	
  int i,j;
  Draw draw;
  JFrame jan;
  Dimension dimension=null;
  
  //Chama as funções auxiliares
  Inicio (Draw aux,JFrame jan) {
	  
	draw=aux;
	this.jan=jan;
	
	prepGrid();
	prepMed();
	prepCampo();
	prepSelecao();
	prepLegenda();
        
  }
  
  //Prepara a grade de matriz e suas coordenadas
  public void prepGrid () {
	
	for (i=0;i<34;i++)
      for (j=0;j<43;j++)
        draw.quadrantes.add(new Quadrante(draw.dx+20*j,draw.dy+20*i,20,20));
        
    for (i=0;i<43;i++) {
		
	  
	  if (i<10) draw.coords.add(new Stringco(i+" ",draw.dx+6+20*i,draw.dy));
	  
	  else draw.coords.add(new Stringco(i+" ",draw.dx+2+20*i,draw.dy));
	  
	}
    
    for (i=0;i<34;i++) {
		
	  
	  if (i<10) draw.coords.add(new Stringco(i+" ",draw.dx-8,draw.dy+15+20*i));
	  
	  else draw.coords.add(new Stringco(i+" ",draw.dx-16,draw.dy+15+20*i));
	  
	}
	  
  }
  
  //Prepara as linhas que ditam as dimensões das diversas partes do campo
  public void prepMed () {
	
	draw.medidas.add(new Line2D.Float(draw.dx,draw.dy-5,859+draw.dx,draw.dy-5));
	draw.medidas.add(new Line2D.Float(draw.dx,draw.dy-5,draw.dx,draw.dy-2));
	draw.medidas.add(new Line2D.Float(859+draw.dx,draw.dy-5,859+draw.dx,draw.dy-2));
	draw.medidas2.add(new Stringco("150 cm",408+draw.dx,draw.dy-9));
	
	draw.medidas.add(new Line2D.Float(draw.dx-5,draw.dy,draw.dx-5,679+draw.dy));
	draw.medidas.add(new Line2D.Float(draw.dx-5,draw.dy,draw.dx-2,draw.dy));
	draw.medidas.add(new Line2D.Float(draw.dx-5,679+draw.dy,draw.dx-2,679+draw.dy));
	draw.medidas2.add(new Stringco("130 cm",draw.dx-54,340+draw.dy));
	
	draw.medidas.add(new Line2D.Float(draw.dx+105,draw.dy+161,draw.dx+105,draw.dy+520));
	draw.medidas.add(new Line2D.Float(draw.dx+102,draw.dy+161,draw.dx+105,draw.dy+161));
	draw.medidas.add(new Line2D.Float(draw.dx+102,draw.dy+520,draw.dx+105,draw.dy+520));
	draw.medidas2.add(new Stringco("70 cm",draw.dx+106,draw.dy+340));
	
	draw.medidas.add(new Line2D.Float(draw.dx+25,draw.dy+240,draw.dx+25,draw.dy+440));
	draw.medidas.add(new Line2D.Float(draw.dx+22,draw.dy+240,draw.dx+25,draw.dy+240));
	draw.medidas.add(new Line2D.Float(draw.dx+22,draw.dy+440,draw.dx+25,draw.dy+440));
	draw.medidas2.add(new Stringco("40 cm",draw.dx+27,draw.dy+340));
	
	draw.medidas.add(new Line2D.Float(draw.dx+32,draw.dy+180,draw.dx+32,draw.dy+240));
	draw.medidas.add(new Line2D.Float(draw.dx+30,draw.dy+180,draw.dx+32,draw.dy+180));
	draw.medidas.add(new Line2D.Float(draw.dx+30,draw.dy+240,draw.dx+32,draw.dy+240));
	draw.medidas2.add(new Stringco("15 cm",draw.dx+34,draw.dy+213));
	
	draw.medidas.add(new Line2D.Float(draw.dx+29,draw.dy+520,draw.dx+29,draw.dy+659));
	draw.medidas.add(new Line2D.Float(draw.dx+26,draw.dy+520,draw.dx+29,draw.dy+520));
	draw.medidas.add(new Line2D.Float(draw.dx+26,draw.dy+659,draw.dx+29,draw.dy+659));
	draw.medidas2.add(new Stringco("30 cm",draw.dx+31,draw.dy+590));
	
	draw.medidas.add(new Line2D.Float(draw.dx+429,draw.dy+655,draw.dx+839,draw.dy+655));
	draw.medidas.add(new Line2D.Float(draw.dx+429,draw.dy+655,draw.dx+429,draw.dy+658));
	draw.medidas.add(new Line2D.Float(draw.dx+839,draw.dy+655,draw.dx+839,draw.dy+658));
	draw.medidas2.add(new Stringco("75 cm",draw.dx+613,draw.dy+651));
	
	draw.medidas.add(new Line2D.Float(draw.dx+835,draw.dy+20,draw.dx+835,draw.dy+239));
	draw.medidas.add(new Line2D.Float(draw.dx+835,draw.dy+20,draw.dx+838,draw.dy+20));
	draw.medidas.add(new Line2D.Float(draw.dx+835,draw.dy+239,draw.dx+838,draw.dy+239));
	draw.medidas2.add(new Stringco("45 cm",draw.dx+796,draw.dy+123));
	  
  }
  
  //Prepara o desengo do campo
  public void prepCampo () {
	
	draw.special.add(new QColor (0,0,43*20,20,new Color (0,0,0)));
	draw.special.add(new QColor (0,33,43*20,20,new Color (0,0,0)));
	draw.special.add(new QColor (0,0,20,34*20,new Color (0,0,0)));
	draw.special.add(new QColor (42,0,20,34*20,new Color (0,0,0)));
	
	draw.special.add(new QColor (42,12,20,10*20,new Color (209,0,0)));
	draw.special.add(new QColor (0,12,20,10*20,new Color (0,18,175)));
	  
	draw.special.add(new QColor (1,1,41*20,32*20,new Color (15,115,45)));
	    
    draw.special.add(new QColor (21,1,20,32*20,new Color (255,255,255)));
	draw.special.add(new QColor (38,8,20,18*20,new Color (255,255,255)));
    draw.special.add(new QColor (4,8,20,18*20,new Color (255,255,255)));
	draw.special.add(new QColor (1,8,4*20,20,new Color (255,255,255)));
	draw.special.add(new QColor (1,25,4*20,20,new Color (255,255,255)));
	draw.special.add(new QColor (38,8,4*20,20,new Color (255,255,255)));
	draw.special.add(new QColor (38,25,4*20,20,new Color (255,255,255)));
	  
  }
  
  //Prepara o desenho das áreas de seleção
  public void prepSelecao() {
	  
	draw.area_robo.add(new QColor (7,2,13*20,30*20,new Color (0,255,100,220)));
	draw.robo_area = new Quadrante (7*20+draw.dx,2*20+draw.dy,13*20,30*20);
	    
	draw.area_bola.add(new QColor (23,3,14*20,28*20,new Color (0,255,100,220)));
	draw.bola_area = new Quadrante (23*20+draw.dx,3*20+draw.dy,14*20,28*20);
	
	draw.area_gol.add(new QColor (42,12,20,5*20,new Color (0,255,100,220)));
	draw.area_gol.add(new QColor (42,17,20,5*20,new Color (0,255,100,220)));
	    
	draw.gol_esq = new Quadrante (42*20+draw.dx,12*20+draw.dy,20,5*20);
	draw.gol_dir = new Quadrante (42*20+draw.dx,17*20+draw.dy,20,5*20);
	  
  }
  
  //Prepara os elemento que compõem a legenda
  public void prepLegenda () {
	
	//usei offset somando para facilitar mudar a altura da legenda em caso de necessidade
	
	draw.legenda3.add(new Quadrante(870+draw.dx,245+draw.dy,160,190));
	
	draw.legenda3.add(new Quadrante(880+draw.dx,255+draw.dy,20,20));
	draw.legenda.add(new QColor (880+draw.dx,255+draw.dy,20,20,new Color (0,18,175)));
	draw.legenda2.add(new Stringco("- Gol do Time",905+draw.dx,270+draw.dy));
	
	draw.legenda3.add(new Quadrante(880+draw.dx,285+draw.dy,20,20));
	draw.legenda.add(new QColor (880+draw.dx,285+draw.dy,20,20,new Color (209,0,0)));
	draw.legenda2.add(new Stringco("- Gol do Adversário",905+draw.dx,300+draw.dy));
	
	draw.legenda3.add(new Quadrante(880+draw.dx,315+draw.dy,20,20));
	draw.legenda.add(new QColor (880+draw.dx,315+draw.dy,20,20,new Color (100,100,100)));
	draw.legenda2.add(new Stringco("- Robo",905+draw.dx,330+draw.dy));
	
	draw.legenda2.add(new Stringco("- Bola",905+draw.dx,360+draw.dy));
	
	draw.legenda3.add(new Quadrante(880+draw.dx,375+draw.dy,20,20));
	draw.legenda.add(new QColor (880+draw.dx,375+draw.dy,20,20,new Color (0,255,100)));
	draw.legenda2.add(new Stringco("- Área de Seleção",905+draw.dx,390+draw.dy));
	
	draw.legenda3.add(new Quadrante(880+draw.dx,405+draw.dy,20,20));
	draw.legenda.add(new QColor (880+draw.dx,405+draw.dy,20,20,new Color (96,0,48)));
	draw.legenda2.add(new Stringco("- Objetivo",905+draw.dx,420+draw.dy));
	  
  }
  
  //Função que centraliza a janela na tela
  public void centreWindow(JFrame frame) {
	
    dimension = Toolkit.getDefaultToolkit().getScreenSize();
    int x = (int) ((dimension.getWidth() - frame.getWidth()) / 2);
    int y = (int) ((dimension.getHeight() - frame.getHeight()) / 2);
    frame.setLocation(x, y);
    
  }
  	
}

//Gera a função que será seguida pelo robô na hora do chute
class Interpola {

  int i,j,k;
  double[][] matrizX = new double[ORDEM][ORDEM];
  double[] vetorA = new double [ORDEM];
  double[] vetorY = new double [ORDEM];
  double coeficiente;
  static final int ORDEM = 3;
  
  //Cria uma matriz formada pelas potencias dos valores em x dos pontos dados de 0 a n-1, sendo n o número de pontos dado
  Interpola(Point2D.Float robo, Point2D.Float bola, Point2D.Float objetivo){

    for(int i=0; i<ORDEM; i++) // completa a primeira coluna da matriz dos X's com 1.
    
      matrizX[i][0] = 1;
      
    matrizX[1][1] = robo.x; // completando a segunda coluna da matriz dos X's com a posicao x do robo.
    matrizX[0][1] = bola.x; // completando a segunda coluna da matriz dos X's com a posicao x da bola.
    matrizX[2][1] = objetivo.x;  // completando a segunda coluna da matriz dos X's com a posicao x do objetivo.
    
    for(int i=0; i<ORDEM; i++)
    
      matrizX[i][2] = matrizX[i][1]*matrizX[i][1]; // completa a ultima coluna da matriz dos X's com as posições x dos elementos ao quadrado.
    
    vetorY[1] = robo.y; // completando o vetor dos Y's com a posicao y do robo.
    vetorY[0] = bola.y; // completando o vetor dos Y's com a posicao y da bola.
    vetorY[2] = objetivo.y;  // completando o vetor dos Y's com a posicao y do objetivo.
    triangulariza(); // triangulariza a matriz dos valores de X.
    vetorA[2] = vetorY[2]/matrizX[2][2];
    vetorA[1] = (vetorY[1] - vetorA[2]*matrizX[1][2]) / matrizX[1][1];
    vetorA[0] = vetorY[0] - vetorA[2]*matrizX[0][2] - vetorA[1]*matrizX[0][1];
    
  }
  
  //Faz a triangularização da matriz para resolver o sistema
  public void triangulariza(){

    for(i = 0;i < ORDEM - 1 ; i++){
		
      for(j = i + 1; j < ORDEM; j++){
		  
        coeficiente = matrizX[j][i] / matrizX[i][i];
        
        for(k = i; k < ORDEM; k++)
          
          matrizX[j][k] = matrizX[j][k] - coeficiente*matrizX[i][k];
        
        vetorY[j] = vetorY[j] - coeficiente*vetorY[i];
      
      }
    }
  
  }
  
  //Gerada a função, esse método retorna o valor da mesma para cada x dado
  public float funcao (int x) {
	
	return (float)vetorA[0]+(float)vetorA[1]*x+(float)vetorA[2]*x*x;
	
  }
  	
}

//Classe que contém a main
class Visao_Barreira {

  //Variáveis base que iniciam o programa
  JFrame jan = new JFrame ("Campo");
  BufferedImage img=null;
  Draw draw = new Draw();
  Inicio in = new Inicio(draw,jan);
  Visao_Barreira vb;
  Menu menu ;
  //Variáveis que auxiliam no funcionamento dos botões
  JButton reset = new JButton ("Reset");
  JButton grade = new JButton ("Grade: Nula");
  JButton rmenu = new JButton ("Voltar ao Menu");
  String aux;
  boolean resetboo=true;
  boolean gradeboo=true;
  ActionListener resetac = new ActionListener() {
	
	@Override
	public void actionPerformed(ActionEvent e) {
	  
	  resetboo=true;
	  resett.stop();
	  	
	}
	  
  };
  ActionListener gradeac = new ActionListener() {
	
	@Override
	public void actionPerformed(ActionEvent e) {
	  
	  gradeboo=true;
	  gradet.stop();
	  	
	}
	  
  };
  Timer resett;
  Timer gradet;
  
  //Cria as instâncias e inicia o programa
  public static void main (String[] args) {
	  
	int i,j;
	
	Visao_Barreira vb = new Visao_Barreira();
	
	try {
		
	  vb.img = ImageIO.read(new File("botao_carrossel.png"));
      
    } 
    catch (IOException e) {
      System.out.println("Não Carrego");
    }
	
	vb.menu = new Menu(vb);
    
    vb.prep(vb);
    vb.prep2(vb);
    vb.prep3(vb);
    vb.draw.setBounds(0,0,1100,800);
	vb.jan.setResizable(false);
	vb.jan.setIconImage(vb.img);
    vb.jan.setVisible(true);
    vb.in.centreWindow(vb.jan);
    
  }
  
  //Prepara o botão que reseta o programa
  public static void prep (Visao_Barreira vb) {
	
	vb.reset.addActionListener(new ActionListener() {
	  
	  @Override
	  public void actionPerformed(ActionEvent e) {
		
		int opc;
		  
		if (vb.resetboo) {
		
		  vb.jan.remove(vb.draw);
		  opc=vb.draw.opc;
		  vb.draw=new Draw();
		  vb.draw.opc=opc;
		  vb.in = new Inicio(vb.draw,vb.jan);
		  vb.jan.add(vb.draw);
		  vb.draw.setBounds(0,0,1100,800);
		  vb.resetboo=false;
		  
	      vb.resett = new Timer(4000,vb.resetac);
	      vb.resett.start();
		
	    }
		  
	  }
	  	
	});
	
	vb.reset.setBounds(870+vb.draw.dx,575+vb.draw.dy,161,50);
	  
  }
  
  //Prepara o botão que desenha as grades do campo
  public static void prep2 (Visao_Barreira vb) {
	
	vb.grade.addActionListener(new ActionListener() {
	  
	  @Override
	  public void actionPerformed(ActionEvent e) {
		  
		if (vb.gradeboo) {
		
		  vb.aux = ((JButton)e.getSource()).getText();
		
		  if (vb.aux.equalsIgnoreCase("Grade: Nula")) {
		  
		    ((JButton)e.getSource()).setText("Grade: C.P.");
		    vb.draw.grade=1;
		    vb.draw.repaint();
		  	
	      }
	      
	      else if (vb.aux.equalsIgnoreCase("Grade: C.P.")) {
		  
		    ((JButton)e.getSource()).setText("Grade: Medidas");
		    vb.draw.grade=2;
		    vb.draw.repaint();
		  	
	      }
	      
	      else if (vb.aux.equalsIgnoreCase("Grade: Medidas")) {
		  
		    ((JButton)e.getSource()).setText("Grade: Nula");
		    vb.draw.grade=0;
		    vb.draw.repaint();
		  	
	      }
	      
	      vb.gradeboo=false;
	      vb.gradet = new Timer(1000,vb.gradeac);
	      vb.gradet.start();
	    
	    }
		  
	  }
	  	
	});
	
	vb.grade.setBounds(870+vb.draw.dx,520+vb.draw.dy,161,50);
	  
  }
  
  //Prepara o botão que volta ao menu
  public static void prep3 (Visao_Barreira vb) {
	
	vb.rmenu.addActionListener(new ActionListener() {
	  
	  @Override
	  public void actionPerformed(ActionEvent e) {
		
		vb.menu.back();
		  
	  }
	  	
	});
	
	vb.rmenu.setBounds(870+vb.draw.dx,630+vb.draw.dy,161,50);
	  
  }
  
}
