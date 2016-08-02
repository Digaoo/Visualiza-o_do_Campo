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

//colocar sistema com timer para prevenir que reset e grade sejam clicados excessivamente

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
  Stringco instrucao= new Stringco("Posicione o robo",900,35);
  Ellipse2D.Float blegenda = new Ellipse2D.Float (910,365,18,18);
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
		
	    roboMouse(e);
	    bolaMouse(e);
	    //botaoMouse(e);

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
		  
		posicionaObj(e);
		posicionaBola(e);
		posicionaRobo(e);
	  
	  }
	  
	  //define e guarda as coordenadas finais do robo
	  public void posicionaRobo(MouseEvent e) {
		
		if (roboboo) {
		  
		  if (robo_area.rect.contains(e.getX(),e.getY())) {
		  
		    roboxy = new Point2D.Float ((int)e.getX()-10,(int)e.getY()-10);
		    
		  }
		  
		  else return;
		  
		  robo = new QColor ((int)roboxy.x,(int)roboxy.y,20,20,new Color(100,100,100));
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
		  
		    bolaxy = new Point2D.Float ((int)e.getX()-10,(int)e.getY()-10);
		  
	      }
		  
		  else return;
		  
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
			
			objetivo = new QColor (43*20,15*20,20,20,new Color(96,0,48));
			objxy = new Point2D.Float ((int)860,(int)310);
			golboo=false;
			instrucao= null;
			gera=true;
			repaint();
			  
		  }
		  
		  else if (gol_dir.rect.contains(e.getX(),e.getY())) {
			
			objetivo = new QColor (43*20,20*20,20,20,new Color(96,0,48));
			objxy = new Point2D.Float ((int)860,(int)410);
			golboo=false;
			instrucao= null;
			gera=true;
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
    if(grade==1)grid(g2);
    ajuda(g2);
    posicionar(g2);
    funcao(g2);
	  
  }
  
  //Desenha as diferentes cores que compõem o campo
  public void campo (Graphics2D g2) {
   
	for (QColor qc:special) {
	 
	 g2.setColor(qc.c);
	 g2.fillRect(qc.x*20+20,qc.y*20+20,qc.w,qc.h);
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
	    g2.fillRect(qc.x*20+20,qc.y*20+20,qc.w,qc.h);
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
	    g2.fillRect(qc.x*20+20,qc.y*20+20,qc.w,qc.h);
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
	    g2.fillRect(qc.x*20+20,qc.y*20+20,qc.w,qc.h);
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
	
	  inter = new Interpola(new Point2D.Float (340,200),new Point2D.Float (560,230),new Point2D.Float (860,310));

	  //System.out.println(roboxy.y+" "+inter.funcao((int)roboxy.x)+"-"+roboxy.x);
	  //System.out.println(bolaxy.y+" "+inter.funcao((int)bolaxy.x)+"-"+bolaxy.x);
	  //System.out.println(objxy.y+" "+inter.funcao((int)objxy.x)+"-"+objxy.x);
	  
	  for(i=(int)roboxy.x;i<=objxy.x;i++) {
	    
	    arco.add(new Quadrante(i,inter.funcao(i),0,0));
	    
	  }
	    
	  gera=false;
	  
	}
	
	if(!arco.isEmpty()) 
	
	  for (Quadrante q:arco) 
      
      g2.draw(q.rect);
	  
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
	  
	draw.area_robo.add(new QColor (7,2,13*20,30*20,new Color (0,255,100)));
	draw.robo_area = new Quadrante (7*20+20,2*20+20,13*20,30*20);
	    
	draw.area_bola.add(new QColor (23,3,14*20,28*20,new Color (0,255,100)));
	draw.bola_area = new Quadrante (23*20+20,3*20+20,14*20,28*20);
	
	draw.area_gol.add(new QColor (42,12,20,5*20,new Color (0,255,100)));
	draw.area_gol.add(new QColor (42,17,20,5*20,new Color (0,255,100)));
	    
	draw.gol_esq = new Quadrante (43*20,13*20,20,5*20);
	draw.gol_dir = new Quadrante (43*20,18*20,20,5*20);
	  
  }
  
  //Prepara os elemento que compõem a legenda
  public void prepLegenda () {
	
	//usei offset somando para facilitar mudar a altura da legenda em caso de necessidades
	
	offset=265;
	
	draw.legenda3.add(new Quadrante(900,offset,160,190));
	
	draw.legenda3.add(new Quadrante(910,offset+10,20,20));
	draw.legenda.add(new QColor (910,offset+10,20,20,new Color (0,18,175)));
	draw.legenda2.add(new Stringco("- Gol do Time",935,offset+25));
	
	draw.legenda3.add(new Quadrante(910,offset+40,20,20));
	draw.legenda.add(new QColor (910,offset+40,20,20,new Color (209,0,0)));
	draw.legenda2.add(new Stringco("- Gol do Adversário",935,offset+55));
	
	draw.legenda3.add(new Quadrante(910,offset+70,20,20));
	draw.legenda.add(new QColor (910,offset+70,20,20,new Color (100,100,100)));
	draw.legenda2.add(new Stringco("- Robo",935,offset+85));
	
	draw.legenda2.add(new Stringco("- Bola",935,offset+115));
	
	draw.legenda3.add(new Quadrante(910,offset+130,20,20));
	draw.legenda.add(new QColor (910,offset+130,20,20,new Color (0,255,100)));
	draw.legenda2.add(new Stringco("- Área de Seleção",935,offset+145));
	
	draw.legenda3.add(new Quadrante(910,offset+160,20,20));
	draw.legenda.add(new QColor (910,offset+160,20,20,new Color (96,0,48)));
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
  
  public void triangulariza(){

    for(i = 0;i < ORDEM - 1 ; i++){
      for(j = i + 1; j < ORDEM; j++){
        coeficiente = matrizX[j][i] / matrizX[i][i];
        for(k = i; k < ORDEM; k++)
          matrizX[j][k] = matrizX[j][k] - coeficiente*matrizX[i][k];
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
	
  JButton reset = new JButton ("Reset");
  JButton grade = new JButton ("Grade: Nula");
  String aux;
  JFrame jan = new JFrame ("Campo");
  Draw draw = new Draw();
  Inicio in = new Inicio(draw,jan);
  Visao_Barreira vb;
  
  //Cria as instâncias e inicia o programa
  public static void main (String[] args) {
	  
	int i,j;
	
	Visao_Barreira vb = new Visao_Barreira();
    
    vb.jan.setLayout(null);
    vb.jan.setSize(new Dimension (1100,800));
    vb.jan.add(vb.reset);
    vb.prep(vb.reset,vb);
    vb.jan.add(vb.grade);
    vb.prep2(vb.grade,vb);
    vb.jan.add(vb.draw);
    vb.draw.setBounds(0,0,1100,800);;
	vb.jan.setResizable(false);
    vb.jan.setVisible(true);
    vb.in.centreWindow(vb.jan);
    
  }
  
  //Prepara o botão que reseta o programa
  public static void prep (JButton reset,Visao_Barreira vb) {
	
	vb.reset.addActionListener(new ActionListener() {
	  
	  @Override
	  public void actionPerformed(ActionEvent e) {
		
		vb.jan.remove(vb.draw);
		vb.draw=new Draw();
		vb.in = new Inicio(vb.draw,vb.jan);
		vb.jan.add(vb.draw);
		vb.draw.setBounds(0,0,1100,800);
		  
	  }
	  	
	});
	
	vb.reset.setBounds(900,460,161,50);
	  
  }
  
  public static void prep2 (JButton grade,Visao_Barreira vb) {
	
	vb.grade.addActionListener(new ActionListener() {
	  
	  @Override
	  public void actionPerformed(ActionEvent e) {
		
		vb.aux = ((JButton)e.getSource()).getText();
		
		if (vb.aux.equalsIgnoreCase("Grade: Nula")) {
		  
		  ((JButton)e.getSource()).setText("Grade: C.P.");
		  vb.draw.grade=1;
		  vb.draw.repaint();
		  	
	    }
	    
	    else if (vb.aux.equalsIgnoreCase("Grade: C.P.")) {
		  
		  ((JButton)e.getSource()).setText("Grade: Nula");
		  vb.draw.grade=0;
		  vb.draw.repaint();
		  	
	    }
		  
	  }
	  	
	});
	
	vb.grade.setBounds(900,513,161,50);
	  
  }
  
}
