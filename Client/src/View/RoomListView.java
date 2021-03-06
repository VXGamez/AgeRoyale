package src.View;

import src.Controller.GameController;
import src.Controller.RoomsController;
import src.Controller.WaitingController;
import src.Model.Database.DAO.partidaDAO;
import src.Model.Network.UserService;
import src.Partida;
import src.Usuari;
import src.Utils;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

/**
* Vista destinada a mostrar la llista de partides en una finestra
* */
public class RoomListView extends JFrame{

	private ArrayList<Partida> allGames;
	private ArrayList<Partida> pPrivades = new ArrayList<>();
	private ArrayList<Partida> pPubliques= new ArrayList<>();
	private JPanel jpPare;
	private JPanel jpPartidesPubliques;
	private JPanel jpPartidesPrivades;
	private JScrollPane scrollPubliquesF;
	private JScrollPane scrollPrivadesF;
	private boolean visible=false;
	private Usuari usuari;
	private Object[] options = {"Entèsos"};

	private RoomsController roomsController;

	/**
	* Divideix l'array de partides que reb del servidor en dos, publiques i privades
	* */
	private void dividirPartides(){
		pPrivades = new ArrayList<>();
		pPubliques = new ArrayList<>();
		for(Partida p : allGames){
			if(p.isPublic()){
				pPubliques.add(p);
			}else{
				if(isFriendGame(p) || usuari.getNickName().equals(p.getHost())){
					pPrivades.add(p);
				}
			}
		}
	}

	/**
	* Funció que mira si la partida és una partida de un amic nostre, ja que privades només podem mostrar les dels nostres amics
	 * @param game objecte partida amb la informació de la partida actual
	 * @return boolea que indicarà si ho és o no
	* */
	private boolean isFriendGame(Partida game){
		for (Usuari amic: usuari.getAmics()) {
			if (game.getHost().equals(amic.getNickName())) {
				return true;
			}
		}
		return false;
	}

	/**
	* Retorna el panell principal de la vista
	 * @return jpPare JPanel amb tots els elements de UI de la vista de llistat de partides
	* */
	public JPanel getJpPare(){
		return jpPare;
	}

	/**
	* Constructor de la classe
	 * @param roomsController variable de tipus controlador que s'assigna a la vista
	 * @param user variable que correspon al usuari que ha iniciat sessió en el nostre client
	* */
	public RoomListView(RoomsController roomsController, Usuari user){
		this.roomsController = roomsController;
		this.usuari = user;
		roomsController.initMessage();
	}

	/**
	* Inicialitza les vistes de la classe
	* */
	private void initAll(){
		this.removeAll();
		dividirPartides();
		initComponents();
		revalidate();
		repaint();
	}

	/**
	* Crida a les funcions necessaries per inicialitzar la classe
	* */
	private void initComponents() {
		colocarPanel();
		colocarElements();
	}

	/**
	 * Coloca el JPanel principal a la finestra e inicialitza els layouts.
	* */
	private void colocarPanel(){

		jpPare = new JPanel();
		jpPare.setLayout(null);
		jpPare.setOpaque(true);

		jpPartidesPubliques =new JPanel();
		jpPartidesPubliques.setLayout(new BoxLayout(jpPartidesPubliques, BoxLayout.Y_AXIS));
		jpPartidesPubliques.setOpaque(false);

		jpPartidesPrivades = new JPanel();
		jpPartidesPrivades.setLayout(new BoxLayout(jpPartidesPrivades, BoxLayout.Y_AXIS));
		jpPartidesPrivades.setOpaque(false);

		scrollPrivadesF = new JScrollPane();
		scrollPubliquesF = new JScrollPane();
		scrollPrivadesF.setBounds(0, 200, 450, 490);
		scrollPrivadesF.setEnabled(true);
		scrollPrivadesF.setOpaque(false);
		scrollPrivadesF.getViewport().setOpaque(false);

		scrollPubliquesF.setBounds(0, 200, 450, 490);
		scrollPubliquesF.setEnabled(true);
		scrollPubliquesF.setOpaque(false);
		scrollPubliquesF.getViewport().setOpaque(false);

	}

	/**
	* Crear un element de la llista de elements que ha de mostrar. Cada partida és un element
	 * @param p partida que s'ha de crear en el element
	 * @return JPanel generat amb els elements de la partida
	* */
	private JPanel crearElement(Partida p){
		JPanel element = new JPanel(new GridLayout(3, 1)) {
			protected void paintComponent(Graphics g) {
				ImageIcon elementButton = new ImageIcon(this.getClass().getResource("/resources/fonsElement.png"));
				g.drawImage(elementButton.getImage(), 0, 0, null);
				super.paintComponent(g);
			}
		};

		element.setOpaque(false);
		element.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				roomsController.gameSelected(p);
			}
		});

		JLabel nom = new JLabel();
		nom.setText("<html><font color='white'>" + Utils.ferEspais(28) + " Room Name: " + p.getName() + "</font></html>");
		nom.setForeground(Color.WHITE);
		nom.setFont(new Font("Helvetica", 0, 15));
		nom.setPreferredSize(new Dimension(250,15));
		element.add(nom);
		int count = (int) p.getJugadors().stream().count() + (int) p.getEspectadors().stream().count();
		JLabel persones = new JLabel();
		persones.setText("<html><font color='white'> " + Utils.ferEspais(28) + " Total Connected: " + (count) + "</font></html>");
		persones.setForeground(Color.WHITE);
		persones.setPreferredSize(new Dimension(250,15));
		persones.setFont(new Font("Helvetica", 0, 15));
		element.add(persones);

		JLabel create = new JLabel();
		create.setText("<html><font color='white'> " + Utils.ferEspais(28) + "  Host: " + p.getHost() +  "</font></html>");
		create.setForeground(Color.WHITE);
		create.setPreferredSize(new Dimension(250,15));
		create.setFont(new Font("Helvetica", 0, 15));
		element.add(create);

		element.setPreferredSize(new Dimension(430,100));
		element.setMaximumSize(new Dimension(430,100));
		element.setSize(430, 100);
		return element;
	}

	/**
	* Coloca els elements a la UI de la finestra
	* */
	private void colocarElements(){
		JButton nova = new JButton();
		nova.setText("Crear nova partida");
		nova.setBounds(20, 70, 410, 70);
		nova.setOpaque(false);
		nova.setHorizontalTextPosition(JButton.CENTER);
		nova.setVerticalTextPosition(JButton.CENTER);
		nova.setFont(new Font("Helvetica", Font.BOLD, 30));
		nova.setForeground(Color.WHITE);
		nova.setContentAreaFilled(false);
		nova.setBorderPainted(false);
		nova.addActionListener(roomsController.getActionListenerCreaPartida());
		ImageIcon fonsButton= new ImageIcon(this.getClass().getResource("/resources/newGameBanner.png"));
		Icon iconoButton = new ImageIcon(fonsButton.getImage().getScaledInstance(400, 70, Image.SCALE_FAST));
		nova.setIcon(iconoButton);
		jpPare.add(nova);

		if(allGames.size()>0) {
			JPanel showPrivate = new JPanel();
			JLabel mostrar = new JLabel();
			showPrivate.setOpaque(false);
			mostrar.setText("Mostrar privades");
			mostrar.setForeground(Color.WHITE);
			mostrar.setFont(new Font("Helvetica", Font.BOLD, 20));
			mostrar.setBounds(35, 165, 250, 15);
			showPrivate.add(mostrar);
			JButton show = new JButton();
			show.setBounds(300, 165, 50, 15);
			show.setText("No");
			show.addActionListener(new ActionListener( ) {
				public void actionPerformed(ActionEvent ev) {
					if(visible){
						visible = false;
						getScrollPrivades().setVisible(false);
						jpPartidesPrivades.setVisible(false);
						getScrollPubliques().setVisible(true);
						jpPartidesPubliques.setVisible(true);
						show.setText("No");
					}else{
						visible = true;
						getScrollPrivades().setVisible(true);
						jpPartidesPrivades.setVisible(true);
						getScrollPubliques().setVisible(false);
						jpPartidesPubliques.setVisible(false);
						show.setText("Si");
					}
				}});

			showPrivate.add(show);
			showPrivate.setBounds(35, 160, 350, 50);
			jpPare.add(showPrivate);

			//------------------------------ELEMENT------------------------------
			for(int i=0; i<pPubliques.size() ;i++){
				jpPartidesPubliques.add(crearElement(pPubliques.get(i)));
				jpPartidesPubliques.add(addSeparator());
			}

			for(int i =0; i<pPrivades.size(); i++){
				jpPartidesPrivades.add(crearElement(pPrivades.get(i)));
				jpPartidesPrivades.add(addSeparator());
			}


			if(!visible){
				visible = false;
				scrollPrivadesF.setVisible(false);
				scrollPubliquesF.setVisible(true);
			}else{
				visible = true;
				scrollPrivadesF.setVisible(true);
				scrollPubliquesF.setVisible(false);
			}

			scrollPrivadesF.setViewportView(jpPartidesPrivades);
			scrollPubliquesF.setViewportView(jpPartidesPubliques);

			jpPare.add(scrollPrivadesF);
			jpPare.add(scrollPubliquesF);
		}

		//Fons fusta
		ImageIcon img = new ImageIcon(this.getClass().getResource("/resources/fondoMadera.png"));
		Icon icono = new ImageIcon(img.getImage().getScaledInstance(450, 700, Image.SCALE_DEFAULT));
		JLabel fondo = new JLabel();
		fondo.setIcon(icono);
		getLayeredPane().add(fondo, JLayeredPane.FRAME_CONTENT_LAYER);
		fondo.setBounds(0, 0, 450, 700);
		jpPare.add(fondo);

	}


	/**
	* Retorna el scroll pane de les partides publiques
	 * @return scrollPubliquesF variable de tipus JScrollPane
	* */
	public JScrollPane getScrollPubliques(){
		return this.scrollPubliquesF;
	}

	/**
	 * Retorna el scroll pane de les partides privades
	 * @return scrollPrivadesF variable de tipus JScrollPane
	 * */
	public JScrollPane getScrollPrivades(){
		return this.scrollPrivadesF;
	}

	/**
	* Genera un JPanel separador per que els elements de les partides no estiguin molt junts
	 * @return JPanel que retorna la funció
	* */
	public JPanel addSeparator(){
		JPanel separator = new JPanel();
		separator.setPreferredSize(new Dimension(430, 10));
		separator.setOpaque(false);
		separator.setMaximumSize(new Dimension(430,10));
		separator.setSize(430, 10);
		return separator;
	}

	/**
	* Assigna la llista de partides a les que reb per parametre
	 * @param partides llista de partides que reb del servidor
	* */
	public void setAllGames(ArrayList<Partida> partides){
		if(partides!=null){
			cleanGames(partides);
			allGames = partides;
			initAll();
		}else{
			JOptionPane.showOptionDialog(new JFrame(), "LOKO HI HA QUELCOM MALAMENT" , "Alerta", JOptionPane.PLAIN_MESSAGE, JOptionPane.WARNING_MESSAGE, null, options, options[0]);
		}
	}

	/**
	* Fa una neteja de les partides que reb.
	 * @param games llista de partides que li envia el servidor
	* */
	private void cleanGames(ArrayList<Partida> games){
		for(Partida p : games){
			boolean done = true;
			if(p.getJugadors()!=null){
				while(done){
					done = false;
					for(int i=0 ; i<p.getJugadors().size() ;i++) {
						if (p.getJugadors().get(i).getIdUsuari() == 0 || p.getJugadors().get(i).getNickName() == null) {
							p.getJugadors().remove(i);
							done = true;
						}
					}
				}
			}
			if(p.getEspectadors()!=null){
				done = true;
				while(done) {
					done = false;
					for (int i = 0; i < p.getEspectadors().size(); i++) {
						if (p.getEspectadors().get(i).getIdUsuari() == 0 || p.getEspectadors().get(i).getNickName() == null) {
							p.getEspectadors().remove(i);
							done = true;
						}
					}
				}
			}
			int count = 0;
			for(Partida g : games) {
				if (p.getIdPartida() == g.getIdPartida())
					count++;
			}
			if (count > 1)
				games.remove(p);
		}
	}

	/**
	* Retorna el tamany total de les llistes de les partides
	 * @param p partida seleccionada
	 * @return enter que representa el total que retorna
	* */
	public int getTotal(Partida p){
		if (p.isPublic()){
			for (int i = 0; i < pPubliques.size(); i++) {
				if (p.getIdPartida() == pPubliques.get(i).getIdPartida()) return i;
			}
		} else {
			for (int i = 0; i < pPrivades.size(); i++) {
				if (p.getIdPartida() == pPrivades.get(i).getIdPartida()) return i;
			}
		}
		return -1;
	}
}
