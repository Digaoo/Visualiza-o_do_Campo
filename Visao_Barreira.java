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
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.lang.StringBuilder;
import java.awt.geom.AffineTransform;

//Tentar possibilitar ajuste de seleção já feita ou copia
//Tentar impedir a seleção de áreas já selecionadas

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
	vb.draw.addKeyListener(new Teclado(vb.draw));
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
        vb.draw.requestFocusInWindow();
		  
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
        vb.jan.add(vb.elem);
        vb.jan.add(vb.last);
        vb.jan.add(vb.draw);
        vb.in.centreWindow(vb.jan);
        vb.draw.opc=2;
        vb.draw.requestFocusInWindow();
		  
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
    vb.jan.remove(vb.last);
    vb.jan.remove(vb.elem);
    vb.jan.remove(vb.draw);
	vb.jan.setLayout(new GridBagLayout());
	gbc.gridy=0;
	vb.jan.add(func,gbc);
	gbc.gridy=1;
	vb.jan.add(est,gbc);
	
  }
  	
}

//Classe que faz os desenhos e trata o mouse
class Draw extends JPanel{

  Graphics2D g2;
  
  //Variaveis que controlam a distância em pixels do canto superio  esquerdo da tela
  int dx=55;
  int dy=25;
  //Variáveis que controlam o tamanho do robo
  int robo_largura=30;
  int robo_altura=30;
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
  Quadrante robo_area_aux = new Quadrante (7*20+dx+11,2*20+dy+11,13*20-robo_largura,30*20-robo_altura);
  Quadrante bola_area;
  Quadrante bola_area_aux = new Quadrante (23*20+dx+10,3*20+dy+10,13*20+1,27*20+1);
  Quadrante gol_esq;
  Quadrante gol_dir;
  //Desenha o quadrado do robo que segue o mouse
  QColor aux = new QColor (2000,2000,30,30,new Color(100,100,100));
  Ellipse2D.Float aux2 = new Ellipse2D.Float (2000,2000,20,20);
  //Desenha a circunferencia da bola que segue o mouse
  Stringco instrucao= null;
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
  //Variáveis para cria a area de seleção da estratégia
  Quadrante campo = new Quadrante(dx+20,dy+20,41*20,32*20);
  Quadrante campo2 = new Quadrante(dx+30,dy+30,(41*20)-19,(32*20)-19);
  Vector<Quadrante> selecao = new Vector<Quadrante>();
  Quadrante buffer=new Quadrante(dx+50,dy+50,1,1);
  boolean selecionando=false;
  int h=0,w=0;
  //Variáveis para funcionamento da seleção e mudança da legenda na estratégia
  int enter = 0;
  Rectangle2D.Float legselect = new Rectangle2D.Float (875+dx,250+dy,150,30);
  boolean legselectboo=false;
  int escolha=0;
  //Variáveis para controle do posicionamento de elementos na estratégia
  int elemento=0;
  Vector<QColor> robos = new Vector<QColor>();
  Vector<Ellipse2D.Float> bolas = new Vector<Ellipse2D.Float>();
  Line2D.Float buffer3 = new Line2D.Float(2000,2000,2001,2001);
  Vector<Seta> setas = new Vector<>();
  AffineTransform aft=null;
  //Variavel que guarda as ações realizadas pelo usuário na estratégia
  Vector <Integer> action = new Vector<> (); 
  //Listener do mouse e auxiliares
  MouseAdapter mouseAdapter;
  int px,py;
  Visao_Barreira vb;
  //Variáveis para funcinamento da geração de função
  Interpola inter;
  boolean gera=false;
  int i;
  //Variavel para controle da grade
  int grade=0;
  
  //Construtor, inicia o double buffering e contém o mouse listener
  Draw (Visao_Barreira v) {
	  
	super(true);
	
    vb=v;
	
	mouseAdapter = new MouseAdapter() {
	  
      //Recebe informações de quando o mouse for clicado
	  @Override
	  public void mousePressed(MouseEvent e) {
		
		if (opc==1) {
		  
		  posicionaObj(e);
		  posicionaBola(e);
		  posicionaRobo(e);
		  
		}
		
		else if (opc==2) {
		    
		  if(elemento==0) restringeCampo (e);
		  
		  else if (elemento==1) elemRoboFinal(e);
		  
		  else if (elemento==2) elemBolaFinal(e);
		  
		  else if (elemento==3) restringeCampo (e);
			  	
		}
	  
	  }
	  
	  //define e guarda as coordenadas finais do robo
	  public void posicionaRobo(MouseEvent e) {
		
		h=e.getY();
		w=e.getX();
		
		if (roboboo) {
		  
		if (!robo_area_aux.rect.contains(w,h)) restringeCampo2(robo_area_aux);
		  
		  roboxy = new Point2D.Float ((int)w-10,(int)h-10);
		  
		  robo = new QColor ((int)roboxy.x-robo_largura/2,(int)roboxy.y-robo_altura/2,robo_largura,robo_altura,new Color(100,100,100));
		  roboboo=false;
		  bolaboo=true;
		  repaint();
		
	    }
		
	  }
	  
	  //define e guarda as coordenadas finais da bola
	  public void posicionaBola(MouseEvent e) {
		
		h=e.getY();
		w=e.getX();
		
		if (bolaboo) {
		  
		  if (!bola_area_aux.rect.contains(w,h)) restringeCampo2(bola_area_aux);
		  
		  bolaxy = new Point2D.Float ((int)w,(int)h);
		  bola = new Ellipse2D.Float ((int)bolaxy.x-10,bolaxy.y-10,20,20);
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
	  
	  //Faz o posicionamento final de um elemento robo
	  public void elemRoboFinal (MouseEvent e) {
		  
		QColor temp = aux;
		
		robos.add(temp);
		action.add(1);
		elemento=0;
		vb.elem.setText("Colocar Elemento");
		  
	  }
	  
	  //Faz o posicionamento final de um elemento bola
	  public void elemBolaFinal (MouseEvent e) {
		  
		Ellipse2D.Float temp = aux2;
		
		bolas.add(temp);
		action.add(2);
		elemento=0;
		vb.elem.setText("Colocar Elemento");
		  
	  }
	  
	  //Recebe informações de quando o mouse for movido
	  @Override
	  public void mouseMoved(MouseEvent e) {
	  
		if (opc==1) {
		
	      if (roboboo) roboMouse(e);
	      
	      else if (bolaboo) bolaMouse(e);
	      
	    }
	    
	    else if (opc==2) {
		  
		  if (elemento==1) roboMouse(e);
		  
		  else if (elemento==2) bolaMouse(e);
		  	
		}
		
	  }
      
      //Mantém o robo seguindo o mouse
	  public void roboMouse (MouseEvent e) {
		
		h=e.getY();
		w=e.getX();
		  
		if (opc==1&&!robo_area_aux.rect.contains(w,h)) restringeCampo2(robo_area_aux);
		
		else if (opc==2&&!campo2.rect.contains(w,h)) restringeCampo2(campo2);
		  
		aux = new QColor (w-10,h-10,robo_largura,robo_altura,new Color(100,100,100));
		  
		if (px!=w||py!=h) {
		  
		  if (opc==1) repaint ();
		  
		  else if (opc==2) repaint ();
		  
		  px=w;
		  py=h;
		  
	    }
		  
	  }
	  
	  //Mantém a bola seguindo o mouse
	  public void bolaMouse (MouseEvent e) {
		
		h=e.getY();
		w=e.getX();
		  
		if (opc==1&&!bola_area_aux.rect.contains(w,h)) restringeCampo2(bola_area_aux);
		
		else if (opc==2&&!campo2.rect.contains(w,h)) restringeCampo2(campo2);
		  
		aux2 = new Ellipse2D.Float (w-10,h-10,20,20);
		  
		if (px!=w||py!=h) {
		  
		  if (opc==1) repaint ();
		  
		  else if (opc==2) repaint ();
		  
		  px=w;
		  py=h;
		  
	    }
		  
	  }
      
      //Recebe informações de quando o mouse for clicado e arrastado
	  @Override
	  public void mouseDragged(MouseEvent e) {
		
		if (opc==2) {
		  
		  if(elemento==0) criaSelecao(e);
		  
		  else if (elemento==3) criaSeta(e);
		  
	    }  
			
	  }
	  
	  //Controla a criação da área de seleção
	  public void criaSelecao (MouseEvent e) {
		  
		h=e.getY();
		w=e.getX();
		
		selecionando=true;
		  
		if (!campo.rect.contains(w,h)) {

		  restringeCampo2(campo);
		  	
		}
		  
		if (px<w&&py<h) buffer = new Quadrante (px,py,w-px,h-py);
		  
		else if (px<w&&py>h) buffer = new Quadrante (px,h,w-px,py-h);
		  
		else if (px>w&&py<h) buffer = new Quadrante (w,py,px-w,h-py);
		  
		else if (px>w&&py>h) buffer = new Quadrante (w,h,px-w,py-h);
		  
		repaint();
		  
	  }
	  
	  //Controla a criação da seta
	  public void criaSeta (MouseEvent e) {
		
		h=e.getY();
		w=e.getX();
		  
		if (!campo.rect.contains(w,h)) {

		  restringeCampo2(campo);
		  	
		}
		
		buffer3 = new Line2D.Float(px,py,w,h);
		
		repaint();
		  
	  }
	  
	  //Restringe o crescimento da área de seleção
	  public void restringeCampo2 (Quadrante campo) {
		  
		int aux=campo.rect.outcode(w,h);
			  
		if(aux==Rectangle2D.OUT_TOP) h = (int)campo.rect.y;
			  
		else if (aux==Rectangle2D.OUT_LEFT) w = (int)campo.rect.x;
			  
		else if (aux==Rectangle2D.OUT_BOTTOM) h = (int)(campo.rect.y+campo.rect.height)-1;
			  
		else if (aux==Rectangle2D.OUT_RIGHT) w = (int)(campo.rect.x+campo.rect.width)-1;
			  
		else if (aux==Rectangle2D.OUT_TOP+Rectangle2D.OUT_LEFT) {
			    
	      h = (int)campo.rect.y;
	      w = (int)campo.rect.x;
			    
		}
			  
		else if (aux==Rectangle2D.OUT_TOP+Rectangle2D.OUT_RIGHT) {
			    
	      h = (int)campo.rect.y;
	      w = (int)(campo.rect.x+campo.rect.width)-1;
			    
		}
			  
		else if (aux==Rectangle2D.OUT_BOTTOM+Rectangle2D.OUT_LEFT) {
			    
		  h = (int)(campo.rect.y+campo.rect.height)-1;
		  w = (int)campo.rect.x;
			    
		}
			  
		else if (aux==Rectangle2D.OUT_BOTTOM+Rectangle2D.OUT_RIGHT) {
			    
		  h = (int)(campo.rect.y+campo.rect.height)-1;
		  w = (int)(campo.rect.x+campo.rect.width)-1;
			    
		}
	    
	  }
      
      //Recebe informações de quando o botão do mouse for solto
	  @Override
	  public void mouseReleased(MouseEvent e) {
		 
		if (opc==2) {
		  
		  if (elemento==0) fimSelecao();
		  
		  else if (elemento==3) elemSetaFinal();
		  
	    }
	  
	  }
	  
	  //Finaliza a criação da área de seleção
	  public void fimSelecao () {
		
		if (selecionando) {
		
		  selecao.add(new Quadrante((int)buffer.rect.x,(int)buffer.rect.y,(int)buffer.rect.width,(int)buffer.rect.height));
		  action.add(3);
		  selecionando=false;
		  buffer=new Quadrante(dx+20,dy+20,0,0);
		  
		}
		  
	  }
	  
	  //Finaliza a criação da setas
	  public void elemSetaFinal () {
		
		Line2D.Float temp = buffer3;
		
		setas.add(new Seta(temp));
		action.add(4);
		buffer3=new Line2D.Float(2000,2000,2001,2001);
		elemento=0;
		vb.elem.setText("Colocar Elemento");
		repaint();
		  
	  }
	  
    };
	
	//Adiciona os listeners na classe
	addMouseListener(mouseAdapter);
    addMouseMotionListener(mouseAdapter);
  
  }
  
  //Faz os desenhos na tela
  @Override
  public void paintComponent(Graphics g) { update(g); }
  
  //Redesenha o necessário na tela
  @Override
  public void update(Graphics g) {
	
	g2 = (Graphics2D) g;
	
    campo(g2);
    ajuda(g2);
    ajuda2(g2);
    maintenance(g2);
    
    if(grade==1)grid(g2);
    else if(grade==2)grid2(g2);
    
    if(opc==1) {
    
      posicionar(g2);
      funcao(g2);
      
    }
    
    else if (opc==2) {
	  
	  select(g2);
	  element(g2);
	  
	  if (legselectboo) modleg (g2);
	  
    }
	  
  }
  
  //Desenha as diferentes cores que compõem o campo
  public void campo (Graphics2D g2) {
    
    g2.setColor(new Color(235,235,235));
    g2.fillRect(0,0,1100,800);
    
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
	  
  }
  
  //Controla as instruções dadas ao usuário
  public void ajuda2 (Graphics2D g2) {
	 
	 String temp;
	 String aux;
	 int cont=0;
	 boolean boo=true;
	 
	 if (opc==1) {
	 
	   if(roboboo) instrucao= new Stringco("Posicione o robo",870+dx,15+dy);
	   
	   else if(bolaboo) instrucao= new Stringco("Posicione a bola",870+dx,15+dy);
	   
	   else if(golboo) instrucao= new Stringco("Escolha o lado",870+dx,15+dy);
	   
	   else instrucao= new Stringco("Função Gerada, se deseja testar outra situação aperte 'reset' para recome- çar",870+dx,15+dy);
	 
     }
     
     else if (opc==2) {
	   
	   if (elemento==1) instrucao= new Stringco("Leve o robo até a posiçãodesejada e clique para   coloca-lo lá",870+dx,15+dy);
	   
	   else if (elemento==2) instrucao= new Stringco("Leve a bola até a posiçãodesejada e clique para   coloca-la lá",870+dx,15+dy);
	   
	   else if (elemento==3) instrucao= new Stringco("Clique e arraste a seta  para a posição que dese- je que ela aponte",870+dx,15+dy);
	   
	   else if (enter==1) instrucao= new Stringco("Use as setas para cima e para baixo para selecio- nar o campo que modifi-  cará e aperte enter para continuar",870+dx,15+dy);
	   
	   else if (enter==2) instrucao= new Stringco("Escreva o desejado, use  os numeros para letras   acentuadas e aperte      enter para finalizar",870+dx,15+dy);
	   
	   else instrucao= new Stringco("Clique e arraste o mouse para selecionar uma área,clique o botão para adi- cionar novos elementos,  aperte enter para modifi-car a legenda ou desfazer ação para retirar um    elemento indesejado",870+dx,15+dy);
	    
	 }
	 
	 if (instrucao.str.length()>25) {
		 
	   temp=instrucao.str;
		 
	   do {
		   
		 if(temp.length()>25) {
			 
		   aux=temp.substring(0,25);
		   temp=temp.substring(25); 
		   
		 }
		 
		 else {
		  
		  aux=temp.substring(0,temp.length());
		  boo=false;
		  
		 }
	 
	     g2.drawString(aux,instrucao.x,instrucao.y+(12*cont));
	     
	     cont++;
	   
       } while(boo);
	 
     }
     
     else g2.drawString(instrucao.str,instrucao.x,instrucao.y);
	 
  }
  
  //Repinta os botões
  public void maintenance (Graphics2D g2) {
	
	vb.reset.repaint();
	vb.grade.repaint();
	vb.rmenu.repaint();
	
	if (opc==2) {
	  
	  vb.elem.repaint();
	  vb.last.repaint();
	  	
	}
	  
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
	  g2.fillRect(aux.x,aux.y,robo_largura,robo_altura);
	  g2.setColor(getForeground());
	  	
	}
	
	else if (!roboboo) {
	  
	  g2.setColor(robo.c);
	  g2.fillRect(robo.x,robo.y,robo_largura,robo_altura);
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
      g2.setColor(new Color(255,255,255,150));
	  g2.fillRect((int)buffer.rect.x+1,(int)buffer.rect.y+1,(int)buffer.rect.width-1,(int)buffer.rect.height-1);
	  g2.setColor(getForeground());
      
    }
    
    if(!selecao.isEmpty())
    
      for (Quadrante q:selecao) { 
      
        g2.draw(q.rect);
        g2.setColor(new Color(255,255,255,150));
	    g2.fillRect((int)q.rect.x+1,(int)q.rect.y+1,(int)q.rect.width-1,(int)q.rect.height-1);
	    g2.setColor(getForeground());
        
      }
    
  }
  
  //Desenha os elementos do campo de estratégia
  public void element (Graphics2D g2) {
	
	if (elemento==1) {
	  
	  g2.setColor(aux.c);
	  g2.fillRect(aux.x,aux.y,robo_largura,robo_altura);
	  g2.setColor(getForeground());
	  	
	}
	
	else if (elemento==2) {
	  
	  g2.setColor(new Color (196,105,77));
	  g2.fillArc((int)aux2.getX(),(int)aux2.getY(),(int)aux2.getWidth(),(int)aux2.getHeight(),0,360);
	  g2.setColor(getForeground());
	  	
	}
	
	else if (elemento==3) { 
      
      g2.draw(buffer3);
	  	
	}
	
	for (QColor qc:robos) {
	 
	  g2.setColor(qc.c);
	  g2.fillRect(qc.x+1,qc.y+1,qc.w,qc.h);
	  g2.setColor(getForeground());
	 
	}
	
	for (Ellipse2D.Float ell:bolas) {
	 
	  g2.setColor(new Color (196,105,77));
	  g2.fillArc((int)ell.x,(int)ell.y,(int)ell.width,(int)ell.height,0,360);
	  g2.setColor(getForeground());
	 
	}
	
	for (Seta s:setas) { 
      
      g2.draw(s.l);
      aft = AffineTransform.getTranslateInstance(s.l.x2-5,s.l.y2-5);
	  aft.rotate (s.angulo,s.img.getWidth()/2,s.img.getHeight()/2);
	  g2.drawImage (s.img,aft,null);
      
    }
	
  }
  
  //Controla o mecanismo de seleção do elemento da legenda a ser modificado na estratégia
  public void modleg (Graphics2D g2) {
	
	g2.draw(legselect);  
	  
  }
  
}

//Classe que guarda as informações que formam as setas
class Seta {
  
  BufferedImage img=null;
  Line2D.Float l=null;
  double angulo;
  
  //Carrega a imagem da ponta da seta e calcula sua angulação
  Seta (Line2D.Float a) {
	  
	l=a;
	
	try {
		
	  img = ImageIO.read(new File("tip_final.png"));
      
    } 
    catch (IOException e) {
      System.out.println("Não Carrego");
    }
    
    angulo=(Math.atan((float)(l.y1-l.y2)/(l.x1-l.x2)));
    
    if (l.x1>=l.x2) angulo+=Math.PI;
	  
  }
  
}

//Classe que guarda as String que serão impressas e suas coordenadas
class Stringco {
  
  String str;
  int x,y;
  
  //Guarda as informações
  Stringco (String aux,int a,int b) {  
    
    str=aux;
    x=a;
    y=b;
    
  } 
  	
}

//Classe que guarda os inumeros retangulos que compõem o programa
class Quadrante {
	
  Rectangle2D.Float rect;
  
  //Cria o retẫngulo nos parametros dados
  Quadrante (float x,float y,int w,int h) {
	
	rect = new Rectangle2D.Float(x,y,w,h);
	  
  }
  	
}

//Classe que armazena as coordenadas iniciais e cores impressas
class QColor {
  
  Color c;
  int x,y;
  int w,h;
  
  //Guarda o tamanho e cor de uma area que será pintada
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
	  
	draw.area_robo.add(new QColor (7,2,13*20,30*20,new Color (255,255,255,150)));
	draw.robo_area = new Quadrante (7*20+draw.dx,2*20+draw.dy,13*20,30*20);
	    
	draw.area_bola.add(new QColor (23,3,14*20,28*20,new Color (255,255,255,150)));
	draw.bola_area = new Quadrante (23*20+draw.dx,3*20+draw.dy,14*20,28*20);
	
	draw.area_gol.add(new QColor (42,12,20,5*20,new Color (255,255,255,150)));
	draw.area_gol.add(new QColor (42,17,20,5*20,new Color (255,255,255,150)));
	    
	draw.gol_esq = new Quadrante (42*20+draw.dx,12*20+draw.dy,19,5*20);
	draw.gol_dir = new Quadrante (42*20+draw.dx,17*20+draw.dy,19,5*20);
	  
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
	draw.legenda.add(new QColor (880+draw.dx,375+draw.dy,20,20,new Color (255,255,255)));
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

//Classe com o listener do teclado
class Teclado extends KeyAdapter {
  
  Draw draw;
  int temp;
  String acento = "çãáÁâéêõó";
  
  //Pega a instância de draw
  Teclado (Draw d) {
	  
	draw = d;
	  
  }
  
  //Detecta teclas apertadas no teclado
  @Override
  public void keyPressed (KeyEvent e) {

    if (e.getKeyCode()==KeyEvent.VK_ENTER) { 
		
	  draw.enter++;
	  draw.repaint();
	  
	}
    
    if (draw.enter==1) mod(e);
    
    else if (draw.enter==2) mod2(e);
    
    else if (draw.enter>=3) {
    
      draw.enter=0;
      draw.legselectboo=false;
      draw.repaint();
      
    }
    
  }
  
  //Controla a subida e descida do selecionador da legenda
  public void mod (KeyEvent e) {
	
	draw.legselectboo=true;
	
	if (e.getKeyCode()==KeyEvent.VK_DOWN) {
		
	  draw.legselect.y+=30;
	  draw.escolha++;
	  
	  if (draw.legselect.y>(400+draw.dy)) {
	  
	    draw.legselect.y=250+draw.dy;
	    draw.escolha=0; 
	  
      }
	  
	}
	  
    else if (e.getKeyCode()==KeyEvent.VK_UP) {
		
	  draw.legselect.y-=30;
	  draw.escolha--;
	  
	  if (draw.legselect.y<(250+draw.dy)) {
	  
	    draw.legselect.y=400+draw.dy;
	    draw.escolha=5;
	    
	  }
	  
	}
	
	draw.repaint();
	  
  }
  
  //Controla o que é escrito na legenda
  public void mod2 (KeyEvent e) {
	  
	temp = e.getKeyCode();
	
	Stringco aux = draw.legenda2.get(draw.escolha);
	
	if (temp==KeyEvent.VK_BACK_SPACE&&aux.str.length()>2) aux.str=aux.str.substring(0,aux.str.length()-1);
	
	else if (((temp>64&&temp<91)||temp==32)&&aux.str.length()<16) aux.str=aux.str.concat(""+e.getKeyChar());
	
	else if ((temp>48&&temp<58)&&aux.str.length()<16) aux.str=aux.str.concat(""+acento.charAt(temp-49));
	
	draw.repaint();
	  
  }
  
}

//Classe que contém a main
class Visao_Barreira {

  //Variáveis base que iniciam o programa
  JFrame jan = new JFrame ("Campo");
  BufferedImage img=null;
  Visao_Barreira vb;
  Draw draw;
  Inicio in;
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
  JButton elem = new JButton("Colocar Elemento");
  JButton last = new JButton("Desfazer Ação");
  
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
	
	vb.draw = new Draw(vb);
	vb.in = new Inicio(vb.draw,vb.jan);
	vb.menu = new Menu(vb);
    
    vb.prep(vb);
    vb.prep2(vb);
    vb.prep3(vb);
    vb.prep4(vb);
    vb.prep5(vb);
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
		  vb.draw=new Draw(vb);
		  vb.draw.addKeyListener(new Teclado(vb.draw));
		  vb.draw.opc=opc;
		  vb.in = new Inicio(vb.draw,vb.jan);
		  vb.jan.add(vb.draw);
		  vb.draw.setBounds(0,0,1100,800);
		  vb.elem.setText("Colocar Elemento");
		  vb.resetboo=false;
		  vb.draw.requestFocusInWindow();
		  
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
  
  //Prepara o botão que posiciona elementos
  public static void prep4 (Visao_Barreira vb) {
	
	vb.elem.addActionListener(new ActionListener() {
	  
	  @Override
	  public void actionPerformed(ActionEvent e) {
		
		vb.aux = ((JButton)e.getSource()).getText();
		
		if (vb.aux.equalsIgnoreCase("Colocar Elemento")) {
		  
		  ((JButton)e.getSource()).setText("Colocar: Robo");
		  vb.draw.elemento=1;
		  vb.draw.repaint();
		  	
	    }
	      
	    else if (vb.aux.equalsIgnoreCase("Colocar: Robo")) {
		  
		  ((JButton)e.getSource()).setText("Colocar: Bola");
		  vb.draw.elemento=2;
		  vb.draw.repaint();
		  	
	    }
	      
	    else if (vb.aux.equalsIgnoreCase("Colocar: Bola")) {
		  
		  ((JButton)e.getSource()).setText("Colocar: Seta");
		  vb.draw.elemento=3;
		  vb.draw.repaint();
		  	
	    }
	    
	    else if (vb.aux.equalsIgnoreCase("Colocar: Seta")) {
		  
		  ((JButton)e.getSource()).setText("Colocar Elemento");
		  vb.draw.elemento=0;
		  vb.draw.repaint();
		  	
	    }
	    
	    vb.draw.repaint();
		  
	  }
	  	
	});
	
	vb.elem.setBounds(870+vb.draw.dx,180+vb.draw.dy,161,50);
	  
  }
  
  //Prepara o botão que retira a ultima coisa colocada no campo da estratégia
  public static void prep5 (Visao_Barreira vb) {
	
	vb.last.addActionListener(new ActionListener() {
	  
	  @Override
	  public void actionPerformed(ActionEvent e) {
		  
		if (!vb.draw.action.isEmpty()) {
		  
		  if ((vb.draw.action.lastElement()).intValue()==1) {
			
		    vb.draw.action.remove(vb.draw.action.indexOf(vb.draw.action.lastElement()));
		    vb.draw.robos.remove(vb.draw.robos.indexOf(vb.draw.robos.lastElement()));
		    vb.draw.aux = new QColor (2000,2000,20,20,new Color(100,100,100));
			  
		  }
		  
		  else if ((vb.draw.action.lastElement()).intValue()==2) {
			
			vb.draw.action.remove(vb.draw.action.indexOf(vb.draw.action.lastElement()));
		    vb.draw.bolas.remove(vb.draw.bolas.indexOf(vb.draw.bolas.lastElement()));
            vb.draw.aux2 = new Ellipse2D.Float (2000,2000,20,20);
			
		  }
		  
		  else if ((vb.draw.action.lastElement()).intValue()==3) {
			
			vb.draw.action.remove(vb.draw.action.indexOf(vb.draw.action.lastElement()));
		    vb.draw.selecao.remove(vb.draw.selecao.indexOf(vb.draw.selecao.lastElement()));
			 
		  }
		  
		  else if ((vb.draw.action.lastElement()).intValue()==4) {
			
			vb.draw.action.remove(vb.draw.action.indexOf(vb.draw.action.lastElement()));
		    vb.draw.setas.remove(vb.draw.setas.indexOf(vb.draw.setas.lastElement()));
			
		  }
		  
		  vb.draw.repaint();
		  	
	    }
		  
	  }
	  	
	});
	
	vb.last.setBounds(870+vb.draw.dx,125+vb.draw.dy,161,50);
	  
  }
  
}
