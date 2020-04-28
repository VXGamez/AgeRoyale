package src.Controller;

import src.Message;
import src.Model.Network.UserService;
import src.Partida;
import src.Usuari;
import src.View.MenuView;
import src.View.RoomListView;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

public class RoomsController {
	private RoomListView vista;
	private Usuari usuari;
	private UserService uService;
	private ArrayList<Partida> allGames;
	private ActionListener actionListenerCreaPartida = new ActionListener() {
		@Override
		public void actionPerformed(ActionEvent e) {
			boolean ok =true;
			boolean privacitat=false;

			if(((JButton)e.getSource()).getText().equals("Crear nova partida")){
				String m = JOptionPane.showInputDialog("Enter game name:");
				if(m!=null){
					int a=JOptionPane.showConfirmDialog(vista, "Do you want your game to be private?");
					if(a==JOptionPane.YES_OPTION){
						privacitat=false;
					}else if(a==JOptionPane.NO_OPTION){
						privacitat=true;
					}else{
						ok=false;
					}
				}else{
					ok=false;
				}

				if(ok){
					DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
					LocalDateTime now = LocalDateTime.now();
					Partida p = new Partida(m, dtf.format(now), privacitat, usuari.getNickName());
					Message mes = new Message(p, "roomCreate");
					uService.sendPartida(mes);
				}

			}
		}
	};

	public RoomsController(Usuari u, UserService uService) {
		this.usuari = u;
		this.uService = uService;
	}

	public synchronized void initMessage() {
		Message m = new Message(null, "getAllGames");
		uService.sendGetPartides(m, this);
	}

	public void setAllGames(ArrayList<Partida> allGames) {
		if (allGames != null) {
			this.allGames = allGames;
			vista.setAllGames(allGames);
		}
	}

	public void setVista(RoomListView vista) {
		this.vista = vista;
	}

	public ActionListener getActionListenerCreaPartida() {
		return actionListenerCreaPartida;
	}
}
