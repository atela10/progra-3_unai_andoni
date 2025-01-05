package gui;

import java.awt.Color;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import main.Usuario;
import main.VentanaPrincipal;
import persistence.GestorBD;
	
	public class InicioSesion extends JPanel {
		private static final long serialVersionUID = 1L;
		
		private Usuario usu;
		
		public InicioSesion(VentanaPrincipal ventana){
			//La idea de tener una variable "aceptado" me la dio chat gpt
			JButton matricula = new JButton("Matricularse");
			matricula.addActionListener(e -> {
				MatriculaDialog matDialog = new MatriculaDialog();
				matDialog.setVisible(true);
				if (matDialog.isAceptado()) {
					this.usu = new Usuario(matDialog.getNombre(), matDialog.getApellido(), matDialog.getDni(), matDialog.getNumero(), matDialog.getEdad(), matDialog.getSexo(), matDialog.getCont());
					GestorBD.insertarUsuarios(usu);
					ventana.setUsuario(usu);
					ventana.getUsuarios().add(usu);
				}
			});
			JButton ini_ses = new JButton("Iniciar Sesion");
			ini_ses.addActionListener(e -> {
				IniSesDialog iniDialog = new IniSesDialog();
				iniDialog.setVisible(true);
				if (iniDialog.isAceptado()) {
					Usuario u = null;
					for(Usuario usuario:ventana.getUsuarios()) {
						if (usuario.getDni().equals(iniDialog.getDni())) {
							System.out.println(usuario.getDni());
							u = usuario;
							break;
						}
					}
					if (u ==null) {
						JOptionPane.showMessageDialog(null, "Usuario no existente");
						iniDialog.setVisible(false);
						return;
					}
					if (u.getContraseña().equals(iniDialog.getCont())) {
						this.usu = u;
						ventana.setUsuario(usu);
					} else {
						JOptionPane.showMessageDialog(null, "Contraseña incorrecta");
					}
				}
				});
			ImageIcon iconoGym = new ImageIcon("/Users/asier.gomez/GitHub/prog3_2024_25_proyecto_gimnasio/Images/gym.png");
			JLabel icono = new JLabel(iconoGym);
			setLayout(null);
			icono.setBounds(30,30,250,250);
			matricula.setBounds(500, 90, 150,50);
			ini_ses.setBounds(500, 150, 150,50);
			add(icono);;
			add(matricula);
			add(ini_ses);
			setBackground(Color.black);
		}
		
		public Usuario getUsuario() {
			return usu;
		}
	}
