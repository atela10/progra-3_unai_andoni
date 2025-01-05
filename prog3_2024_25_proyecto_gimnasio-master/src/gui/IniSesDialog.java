package gui;

import java.awt.Dimension;
import java.awt.GridLayout;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

public class IniSesDialog extends JDialog{
	private static final long serialVersionUID = 1L;
	private JTextField txtDni, txtCont;
	private boolean aceptado = false;
	
	public IniSesDialog() {
		setTitle("Formulario para Iniciar Sesion");
		setModal(true);
		JPanel panel = new JPanel(new GridLayout(3,2));
		setMinimumSize(new Dimension(400, 350));
		
		txtDni = new JTextField();
		txtCont = new JTextField();
		
		panel.add(new JLabel("DNI:"));
		panel.add(txtDni);
		panel.add(new JLabel("Contraseña:"));
		panel.add(txtCont);
		
		JButton botonAceptar = new JButton("Aceptar");
		botonAceptar.addActionListener(e -> {
			aceptado = true;
			JFrame ventanaPrincipal = (JFrame) SwingUtilities.getWindowAncestor(this.getParent());
		    new VentanaHilo(ventanaPrincipal);
			setVisible(false);
		});
		
		JButton botonCancelar = new JButton("Cancelar");
		botonCancelar.addActionListener(e -> {
			setVisible(false);
		});
		
		panel.add(botonCancelar);
		panel.add(botonAceptar);
		add(panel);
		
		setLocationRelativeTo(null);
	}
		
		public boolean isAceptado() {
            return aceptado;
        }

        public String getDni() {
            return txtDni.getText();
        }
        
        public String getCont() {
        	return txtCont.getText();
        }
}
