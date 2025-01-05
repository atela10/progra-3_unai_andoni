package gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import main.Usuario;

public class MatriculaDialog extends JDialog{
	private static final long serialVersionUID = 1L;
	private JTextField txtNombre, txtApellido, txtDni, txtNum, txtEdad, txtCont;
	private JComboBox<Usuario.Sexo> comboSexo;
	private boolean aceptado = false;
	
	public MatriculaDialog() {
		setTitle("Formulario para matricularse");
		setModal(true);
		JPanel panel = new JPanel(new GridLayout(8,2));
		setMinimumSize(new Dimension(400, 350));
		
		txtNombre = new JTextField();
		txtApellido = new JTextField();
		txtDni = new JTextField();
		txtNum = new JTextField();
		txtEdad = new JTextField();
		txtCont = new JTextField();
		comboSexo = new JComboBox<>(Usuario.Sexo.values());
		
		panel.add(new JLabel("Nombre:"));
		panel.add(txtNombre);
		panel.add(new JLabel("Apellido:"));
		panel.add(txtApellido);
		panel.add(new JLabel("DNI:"));
		panel.add(txtDni);
		panel.add(new JLabel("Contraseña:"));
		panel.add(txtCont);
		panel.add(new JLabel("Numero de telefono:"));
		panel.add(txtNum);
		panel.add(new JLabel("Edad:"));
		panel.add(txtEdad);
		panel.add(new JLabel("Sexo:"));
		panel.add(comboSexo);
		
		JButton botonAceptar = new JButton("Aceptar");
		botonAceptar.addActionListener(e -> {
			if (txtDni.getText().length() != 9 || !txtDni.getText().substring(0, 8).matches("\\d{8}") || !txtDni.getText().substring(8).matches("[A-Z]")) {
		        txtDni.setForeground(Color.RED);
		        JOptionPane.showMessageDialog(
			            null, 
			            "El DNI debe tener 8 numeros y una letra.", 
			            "Error", 
			            JOptionPane.ERROR_MESSAGE
			        );
			}else if(txtCont.getText().length() == 0) {
				JOptionPane.showMessageDialog(
			            null, 
			            "El apartado contraseña no puede estar vacio", 
			            "Error", 
			            JOptionPane.ERROR_MESSAGE
			        );
			}else {
				aceptado = true;
				setVisible(false);
			}
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

        public String getNombre() {
            return txtNombre.getText();
        }

        public String getApellido() {
            return txtApellido.getText();
        }

        public String getDni() {
            return txtDni.getText();
        }

        public int getNumero() {
            try {
                return Integer.parseInt(txtNum.getText());
            } catch (NumberFormatException e) {
                return 0;
            }
        }

        public int getEdad() {
            try {
                return Integer.parseInt(txtEdad.getText());
            } catch (NumberFormatException e) {
                return 0;
            }
        }

        public Usuario.Sexo getSexo() {
            return (Usuario.Sexo) comboSexo.getSelectedItem();
	}
        
        public String getCont() {
        	return txtCont.getText();
        }
}
