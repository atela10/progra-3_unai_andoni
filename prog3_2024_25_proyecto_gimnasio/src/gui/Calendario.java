package gui;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.LocalDate;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

public class Calendario extends JFrame {
    private static final long serialVersionUID = 1L;
    private JLabel lblMes, lblAno;
    private Set<LocalDate> diasAsistidos;
    private Map<LocalDate, String> actividadesDias;
    private JButton btnAnterior, btnSiguiente, highlightTodayButton;
    private JTable tabla;
    private int anoActual, mesActual, diaActual;
    private final String[] columnas = {"Dom", "Lun", "Mar", "Mie", "Jue", "Vie", "Sab"};
    private final String[] meses = {"Enero", "Febrero", "Marzo", "Abril", "Mayo", "Junio",
            "Julio", "Agosto", "Septiembre", "Octubre", "Noviembre", "Diciembre"};
    private final Calendar calendario = new GregorianCalendar();

    public Calendario() {
        setTitle("Calendario de Actividades");
        setSize(700, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        LocalDate.now();
        mesActual = calendario.get(Calendar.MONTH);
        anoActual = calendario.get(Calendar.YEAR);
        diaActual = calendario.get(Calendar.DAY_OF_MONTH);

        DefaultTableModel tableModel = new DefaultTableModel(6, 7) {
            private static final long serialVersionUID = 1L;

            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tableModel.setColumnIdentifiers(columnas);
        tabla = new JTable(tableModel);
        tabla.setRowHeight(70);

        lblMes = new JLabel(meses[mesActual]);
        lblAno = new JLabel(String.valueOf(anoActual));
        btnAnterior = new JButton("<");
        btnSiguiente = new JButton(">");
        highlightTodayButton = new JButton("Registrar Actividad");

        JPanel pnlNavegacion = new JPanel(new FlowLayout());
        pnlNavegacion.add(btnAnterior);
        pnlNavegacion.add(lblMes);
        pnlNavegacion.add(lblAno);
        pnlNavegacion.add(btnSiguiente);

        JPanel pnlCalendario = new JPanel(new BorderLayout());
        pnlCalendario.add(pnlNavegacion, BorderLayout.NORTH);
        pnlCalendario.add(new JScrollPane(tabla), BorderLayout.CENTER);

        add(pnlCalendario, BorderLayout.CENTER);
        add(highlightTodayButton, BorderLayout.SOUTH);

        btnAnterior.addActionListener(e -> cambiarMes(-1));
        btnSiguiente.addActionListener(e -> cambiarMes(1));
        
        diasAsistidos = new HashSet<>();
        actividadesDias = new HashMap<>();

        highlightTodayButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                LocalDate hoy = LocalDate.now();
                if (!diasAsistidos.contains(hoy)) {
                    String[] actividades = {"Andar", "Core", "Core Avanzado", "Equilibrio", "Gimnasia", "Hiit", "Yoga"};
                    String actividadSeleccionada = (String) JOptionPane.showInputDialog(
                        null, 
                        "Selecciona tu actividad de hoy:", 
                        "Registro de Actividad", 
                        JOptionPane.QUESTION_MESSAGE, 
                        null, 
                        actividades, 
                        actividades[0]
                    );
                    
                    if (actividadSeleccionada != null) {
                        diasAsistidos.add(hoy);
                        actividadesDias.put(hoy, actividadSeleccionada);
                        highlightToday(actividadSeleccionada);
                    }
                } else {
                    JOptionPane.showMessageDialog(null, "Ya registraste actividad para hoy.");
                }
            }
        });

        actualizarCalendario(mesActual, anoActual);

        setVisible(true);
    }

    private void cambiarMes(int incremento) {
        mesActual += incremento;
        if (mesActual < 0) {
            mesActual = 11;
            anoActual--;
        } else if (mesActual > 11) {
            mesActual = 0;
            anoActual++;
        }
        lblMes.setText(meses[mesActual]);
        lblAno.setText(String.valueOf(anoActual));
        actualizarCalendario(mesActual, anoActual);
    }

    private void actualizarCalendario(int mes, int ano) {
        calendario.set(Calendar.MONTH, mes);
        calendario.set(Calendar.YEAR, ano);
        calendario.set(Calendar.DAY_OF_MONTH, 1);

        int inicioDiaSemana = calendario.get(Calendar.DAY_OF_WEEK) - 1;
        int diasMes = calendario.getActualMaximum(Calendar.DAY_OF_MONTH);

        DefaultTableModel model = (DefaultTableModel) tabla.getModel();
        for (int i = 0; i < model.getRowCount(); i++) {
            for (int j = 0; j < model.getColumnCount(); j++) {
                model.setValueAt(null, i, j);
            }
        }

        int dia = 1;
        for (int i = inicioDiaSemana; i < 7; i++) {
            model.setValueAt(dia++, 0, i);
        }

        int fila = 1;
        while (dia <= diasMes) {
            for (int i = 0; i < 7; i++) {
                if (dia > diasMes) {
                    break;
                }
                model.setValueAt(dia++, fila, i);
            }
            fila++;
        }
    }

    public void highlightToday(String actividad) {
        tabla.setDefaultRenderer(Object.class, new CalendarCellRenderer(actividad));
        tabla.repaint();
    }

    private class CalendarCellRenderer extends DefaultTableCellRenderer {
        private static final long serialVersionUID = 1L;
        private String actividadHoy;

        public CalendarCellRenderer(String actividadHoy) {
            this.actividadHoy = actividadHoy;
        }

       /** @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            Component cell = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            cell.setBackground(Color.WHITE);

            if (value != null && value.equals(diaActual)) {
                cell.setBackground(Color.GREEN);
                
                if (actividadHoy != null) {
                    ImageIcon icono = null;
                    switch (actividadHoy) {
                        case "Andar":
                            icono = new ImageIcon("images/Andar.png");
                            break;
                        case "Core":
                            icono = new ImageIcon("images/Core.png");
                            break;
                        case "Gimnasio":
                            icono = new ImageIcon("images/Gimnasia.png");
                            break;
                    }
                    
                    if (icono != null) {
                        // Scale image to fit cell
                       Image scaledImage = icono.getImage().getScaledInstance(tabla.getColumnModel().getColumnWidth(column), tabla.getRowHeight(), Image.SCALE_SMOOTH);
                       ((JLabel)cell).setIcon(new ImageIcon(scaledImage));
                        
                    }
                }
            }
            return cell;
        }
        **/
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            // Llamar al método de la superclase
            Component cell = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

            // Asegurarse de que el componente es un JLabel
            if (cell instanceof JLabel) {
                JLabel label = (JLabel) cell;
                label.setIcon(null); // Reiniciar icono por defecto
                label.setBackground(Color.WHITE); // Fondo blanco por defecto

                // Comprobar si el valor coincide con el día actual
                if (value != null && value.equals(diaActual)) {
                    label.setBackground(Color.GREEN); // Fondo verde para el día actual

                    // Asignar icono según la actividad del día
                    if (actividadHoy != null) {
                        ImageIcon icono = null;
                        switch (actividadHoy) {
                            case "Andar":
                                icono = new ImageIcon("Images/Andar.png");
                                break;
                            case "Core":
                                icono = new ImageIcon("Images/Core.png");
                                break;
                            case "Core Avanzado":
                                icono = new ImageIcon("Images/CoreAvanzado.png");
                                break;
                            case "Gimnasio":
                                icono = new ImageIcon("Images/Gimnasia.png");
                                break;
                            case "Equilibrio":
                                icono = new ImageIcon("Images/Equilibrio.png");
                                break;
                            case "Hiit":
                                icono = new ImageIcon("Images/HIIT.png");
                                break;
                            case "Yoga":
                                icono = new ImageIcon("Images/Yoga.png");
                                break;
                        }

                        // Escalar el icono si existe
                        if (icono != null) {
                            int columnWidth = table.getColumnModel().getColumn(column).getWidth();
                            int rowHeight = table.getRowHeight(row);
                            Image scaledImage = icono.getImage().getScaledInstance(columnWidth, rowHeight, Image.SCALE_SMOOTH);
                            label.setIcon(new ImageIcon(scaledImage));
                        }
                    }
                }
            }

            return cell;
        }

    }

    public static void main(String[] args) {
        new Calendario();
    }
}