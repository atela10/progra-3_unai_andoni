package main;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.GridLayout;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;

//import prog3_2024_25_proyecto_gimnasio.Actividad;
//import prog3_2024_25_proyecto_gimnasio.Usuario;
//vvhs

public class Menu extends JPanel {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private JPanel pNorte, pSur, pCentro, pEste, pOeste;
	
	private JLabel lbMensaje;
	
	private JProgressBar progressBar;
	
	private List<Actividad> listaActividades;
	private Usuario usuario;
	public Menu(List<Actividad> la, Usuario u) {
		listaActividades = la;
		usuario = u;
		this.setLayout(new BorderLayout());
			
		pNorte = new JPanel();
		pSur = new JPanel();
		pCentro = new JPanel();
		pCentro.setLayout(new GridLayout(3, 1,0,20));
		pEste = new JPanel();
		pOeste = new JPanel();
		pOeste.setLayout(new GridLayout(4, 1));
		
		this.add(pNorte, BorderLayout.NORTH);
		this.add(pSur, BorderLayout.SOUTH);
		this.add(pCentro, BorderLayout.CENTER);
		this.add(pEste, BorderLayout.EAST);
		this.add(pOeste, BorderLayout.WEST);
		
			
		//lbMensaje = new JLabel("Hola " + VentanaPrincipal.usuario.getNombre()+ ". Estas son tus clases para hoy");
		lbMensaje = new JLabel("Hola "+usuario.getNombre()+". Estos son tu porcentaje de clases hechas y de calorias esta semana: ");
		lbMensaje.setFont(new Font("Arial", Font.BOLD, 16));
		lbMensaje.setHorizontalAlignment(JLabel.CENTER);
		pCentro.add(lbMensaje, BorderLayout.SOUTH);
		
		
		progressBar = new JProgressBar(0, contarActividadesporUsuario());
		progressBar.setValue(contarActividadesPasadas());
        progressBar.setStringPainted(true); // Muestra el porcentaje en la barra
        progressBar.setForeground(Color.BLUE);
        pCentro.add(progressBar, BorderLayout.CENTER);
        
        
        LocalDateTime fechaLoc = LocalDateTime.of(2024, 11, 5, 10, 00);
	    LocalDateTime haceUnaSemana = fechaLoc.minusWeeks(1);
	    List<Actividad> actividadesUltimaSemana = new ArrayList<Actividad>();
	    for (Actividad actividad: listaActividades) {
	    	if(actividad.getFecha().isAfter(haceUnaSemana) && actividad.getFecha().isBefore(fechaLoc) && listaDni(actividad.getListaUsuarios()).contains(usuario.getDni())) {
	    		actividadesUltimaSemana.add(actividad);
	    	}
	    }
		JProgressBar barraCal = new JProgressBar(0, 5000);
        barraCal.setStringPainted(true);
        barraCal.setForeground(Color.WHITE);
        int cal = 0;
        for (Actividad actividad: actividadesUltimaSemana) {
        	cal += actividad.getCalorias();
        }
        if(cal > 5000) {
        	cal = 5000;
        }
        barraCal.setValue(cal);
        if (cal < 400) {
            barraCal.setForeground(Color.RED);
        } else {
            barraCal.setForeground(Color.GREEN);
        }
        pCentro.add(barraCal);
     
        
         
		
			
		setVisible(true);
	}
	private int contarActividadesporUsuario() {
		int contador = 0;
		for (Actividad actividad : listaActividades) {
				if (listaDni(actividad.getListaUsuarios()).contains(usuario.getDni())) {
					contador++;
				}
		}
		return contador;
	}
	
	private int contarActividadesPasadas() {
		int contador = 0;
		LocalDateTime fActual = LocalDateTime.of(2024, 11, 2, 10, 00);
		for(Actividad actividad: listaActividades) {
			if(actividad.getFecha().isBefore(fActual)) {
				contador++;
			}
		}
		return contador;
	}
	
	private List<String> listaDni(List<Usuario> usuarios){
		List<String> dnis = new ArrayList<String>();
		for (Usuario u:usuarios) {
			dnis.add(u.getDni());
		}
		return dnis;
	}
	
}
