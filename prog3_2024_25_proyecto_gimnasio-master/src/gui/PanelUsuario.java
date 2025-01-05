package gui;

import java.awt.Color;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.SwingUtilities;
import javax.swing.border.Border;

import main.Usuario;
import main.VentanaPrincipal;
import persistence.GestorBD;

public class PanelUsuario extends JPanel{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public PanelUsuario(Usuario usuario) {
		
		setLayout(null);
		
		JLabel nom = new JLabel("Nombre");
		nom.setBounds(300, 10, 90, 30);
		add(nom);
		JLabel noml = new JLabel(usuario.getNombre());
		noml.setBounds(300, 42, 150, 30);
		Border border = BorderFactory.createLineBorder(Color.GRAY, 2);
        noml.setBorder(border);
		add(noml);
		
		JLabel ape = new JLabel("Apellido");
		ape.setBounds(460, 10, 90, 30);
		add(ape);
		JLabel apel = new JLabel(usuario.getApellido());
		apel.setBounds(460, 42, 150, 30);
        apel.setBorder(border);
		add(apel);
		
		JSlider ageSlider = new JSlider(0, 100);
        ageSlider.setValue(usuario.getEdad()); 
        ageSlider.setEnabled(false);
        JLabel pointLabel = new JLabel(String.valueOf(usuario.getEdad()));
        ageSlider.setBounds(300, 130, 300, 50);
        pointLabel.setBounds(300 + (int)(usuario.getEdad() * 3.0), 110, 30, 20);//IA used to know how to put the point at the same level of the slider
        add(ageSlider);
        add(pointLabel);
        JLabel label = new JLabel("Edad");
        label.setBounds(300, 80, 90, 30);
        add(label);
        
        JLabel dni = new JLabel("DNI");
		dni.setBounds(300, 170, 90, 30);
		add(dni);
		JLabel dnil = new JLabel(usuario.getDni());
		dnil.setBounds(300, 202, 150, 30);
        dnil.setBorder(border);
		add(dnil);
		
		JLabel tel = new JLabel("Telefono");
		tel.setBounds(460, 170, 90, 30);
		add(tel);
		JLabel tell = new JLabel(String.valueOf(usuario.getTelefono()));
		tell.setBounds(460, 202, 150, 30);
        tell.setBorder(border);
		add(tell);

		JLabel sex = new JLabel("Sexo");
		sex.setBounds(390, 235, 90, 30);
		add(sex);
		JLabel sexl = new JLabel(usuario.getSexo().toString());
		sexl.setBounds(390, 260, 150, 30);
        sexl.setBorder(border);
		add(sexl);
		
		ImageIcon iconoGym = new ImageIcon("Images/Usuario.jpeg");
		JLabel icono = new JLabel(iconoGym);
		icono.setBounds(10,10,210,210);
		add(icono);
		
		JButton cerrarSes = new JButton("Cerrar Sesion");
		cerrarSes.setBounds(10,230,210,30);
		cerrarSes.addActionListener(e -> {
			VentanaPrincipal ventana = (VentanaPrincipal) SwingUtilities.getWindowAncestor(this);
			ventana.setUsuario(null);
		});	
		add(cerrarSes);
		
		JButton darseBaja = new JButton("Darse de baja");
		darseBaja.setBounds(10,262,210,30);
		darseBaja.addActionListener(e -> {
			VentanaPrincipal ventana = (VentanaPrincipal) SwingUtilities.getWindowAncestor(this);
			ventana.getUsuarios().remove(usuario);
			GestorBD.eliminarUsuario(usuario.getDni());
			ventana.setUsuario(null);
		});
		add(darseBaja);
	}
}
