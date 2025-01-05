
package main;



import java.awt.BorderLayout;

import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.FileReader;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;


import gui.Calendario;
import gui.InicioSesion;
import gui.PanelActividad;
import gui.PanelUsuario;
import persistence.GestorBD;

@SuppressWarnings("static-access")


public class VentanaPrincipal extends JFrame {
    private static final long serialVersionUID = 1L;
    private JPanel principal;
    public static Usuario usuario;
    public ArrayList<Actividad> listaActividades;
    public ArrayList<Usuario> listaUsuarios;
    public static String[] nombreClases = {"Andar", "Core", "Core Avanzado", "Equilibrio", "Gimnasia", "HIIT", "Yoga"};

	public void setUsuario(Usuario u) {
        this.usuario = u;
        this.ActualizarVentana();
    }
    
    public List<Usuario> getUsuarios() {
        return listaUsuarios;
    }
    
    public List<Actividad> getActividades(){
    	return listaActividades;
    }
    
    public void setUsuarios(ArrayList<Usuario> usuarios) {
    	this.listaUsuarios = usuarios;
    }
    
    public void setActividades(ArrayList<Actividad> actividades) {
    	this.listaActividades = actividades;
    }
    
    public Usuario getUsuario() {
    	return usuario;
    }

    public VentanaPrincipal(GestorBD gestor) {
        this.usuario = null;
        this.listaUsuarios = (ArrayList<Usuario>) gestor.obtenerTodosLosUsuarios();
        this.listaActividades = (ArrayList<Actividad>) gestor.obtenerTodosLasSesiones();
        for (Actividad a:listaActividades) {
        	List<Usuario> usuariosActividad = gestor.obtenerUsuarioPorActividad(a);
        	a.setListaUsuarios((ArrayList<Usuario>) usuariosActividad);
        }
        /**for (Actividad a:listaActividades) {
        	System.out.println(a.getListaUsuarios());
        }**/
        System.out.println(listaActividades.get(1).getListaUsuarios());
        this.principal = new InicioSesion(this);

        // COMPORATMIENTO VENTANA PRINCIPAL
        add(principal);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(850, 400));
        setResizable(false);
        setLocationRelativeTo(null);
        setTitle("GIMNASIO");
        try {
            UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.isControlDown() && e.getKeyCode() == KeyEvent.VK_E) {
                    abrirVentanaSecundaria();
                }
            }
        });
        setVisible(true);
        
    }
    private void abrirVentanaSecundaria() {
        // Instancia y muestra la ventana secundaria
        SwingUtilities.invokeLater(() -> {
            Calendario calendario = new Calendario();
            calendario.setVisible(true);
            calendario.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        });
    }
    

    public void ActualizarVentana() {
        remove(principal);
        if (usuario == null) {
            this.principal = new InicioSesion(this);
        } else {
            this.principal = new JPanel(new BorderLayout(2, 3));
            principal.setBorder(BorderFactory.createEmptyBorder(5, 5, 10, 10));

            // SIDEBAR (>> PRINCIPAL)
            JPanel sidebar = new JPanel(new GridLayout(4, 1, 2, 2));
            sidebar.setBorder(BorderFactory.createEmptyBorder(8, 10, 10, 10));
            principal.add(sidebar, BorderLayout.WEST);

            String[] clasesText = {"ACTIVIDADES", "SALUD", "USUARIO", "MENU" };

            for (String text : clasesText) {
            	
            	ImageIcon iconoBtn = new ImageIcon("Images/"+text+".png");
            	if (text.equals("MENU")) {
            		iconoBtn = new ImageIcon(iconoBtn.getImage().getScaledInstance(45, 45, Image.SCALE_DEFAULT));
            	} else if(text.equals("ACTIVIDADES")){
            		iconoBtn = new ImageIcon(iconoBtn.getImage().getScaledInstance(52, 52, Image.SCALE_DEFAULT));
            	} else {
            		iconoBtn = new ImageIcon(iconoBtn.getImage().getScaledInstance(50, 50, Image.SCALE_DEFAULT));
            	}
                JButton boton = new JButton(text);
                boton.setHorizontalTextPosition(SwingConstants.CENTER);
                boton.setVerticalTextPosition(SwingConstants.BOTTOM);
                boton.setContentAreaFilled(false);
                boton.setBorderPainted(false);
                boton.setIcon(iconoBtn);
                
                boton.addActionListener(e -> {
                    principal.removeAll();
                    principal.add(sidebar, BorderLayout.WEST);
                    switch (text) {
                    	case "MENU":
                    		principal.add(new Menu(listaActividades,usuario), BorderLayout.CENTER);
                    		break;
                        case "ACTIVIDADES":
                            principal.add(new PanelActividad(listaActividades), BorderLayout.CENTER);
                            break;
                        case "SALUD":
                            principal.add(new Salud(), BorderLayout.CENTER);
                            break;
                        case "USUARIO":
                        	default:
                            principal.add(new PanelUsuario(usuario), BorderLayout.CENTER);
                            break;
                    }
                    principal.revalidate();
                    principal.repaint();
                    principal.setBorder(BorderFactory.createEmptyBorder(5, 5, 10, 10));
                });
                sidebar.add(boton);
            }
        }
        add(principal);
        revalidate();
        repaint();
    }
 

    public static void main(String[] args) {
    	GestorBD gestorBD = new GestorBD();
    	
    	gestorBD.borrarBBDD();
    	
    	gestorBD.crearBBDD();
    	
    	gestorBD.initilizeFromCSV();
    	
    	SwingUtilities.invokeLater(() -> new VentanaPrincipal(gestorBD));
    }
}