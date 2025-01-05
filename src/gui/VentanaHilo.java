package gui;

import java.awt.BorderLayout;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JProgressBar;

public class VentanaHilo extends JDialog{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JProgressBar pb;
	private JLabel label;
	private JDialog vActual;
	public VentanaHilo(JFrame parent) {
		super(parent, true);
		setBounds(300, 200, 300, 100);
		setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
		setLocationRelativeTo(parent);
		vActual = this;
		pb = new JProgressBar(0, 100);
		label = new JLabel("Cargando...");
		add(pb, BorderLayout.CENTER);
		add(label, BorderLayout.SOUTH);
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				for(int i=0;i<=100;i++) {
					pb.setValue(i);
					try {
						Thread.sleep(50);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
				vActual.dispose();
			}
		}).start();
		setVisible(true);
	}

}

