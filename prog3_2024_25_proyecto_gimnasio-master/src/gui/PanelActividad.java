package gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Vector;

import javax.swing.AbstractCellEditor;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;

import main.Actividad;
import main.Actividad.Tipo;

public class PanelActividad extends JPanel {
    private static final long serialVersionUID = 1L;
    DefaultTableModel modelo;
    JTable tabla;
    List<Actividad> listaActividades;
    HashMap<LocalDateTime, ArrayList<ArrayList<Actividad>>> actividades = new HashMap<>();
    String tipo = "ANDAR";
    LocalDateTime fecha = LocalDateTime.of(2024, 11, 4, 9, 00);
    private int hoveredRow = -1;
    private int hoveredColumn = -1;

    public PanelActividad(List<Actividad> listaActividades) {
        this.listaActividades = listaActividades;
        actualizarMap();

        iniciarTabla();
        loadActividades();

        JPanel principal = new JPanel(new BorderLayout());
        JComboBox<Tipo> tipoActividadCombo = new JComboBox<>(Tipo.values());
        tipoActividadCombo.setPreferredSize(new Dimension(625, 35));
        tipoActividadCombo.addActionListener(e -> {
            tipo = ((Tipo) tipoActividadCombo.getSelectedItem()).toString();
            actualizarMap();
            loadActividades();
            modelo.fireTableDataChanged();
        });
        principal.add(tipoActividadCombo, BorderLayout.NORTH);
        principal.add(new JScrollPane(tabla), BorderLayout.CENTER);
        this.add(principal);
    }
    
    public void actualizarMap() {
        actividades = new HashMap<>();
        ArrayList<Actividad> actividadesA = new ArrayList<>();
        for (Actividad actividad : listaActividades) {
            if (actividad.getTipo().toString().equals(tipo)) {
                actividadesA.add(actividad);
            }
        }
        for (Actividad actividad : actividadesA) {
            int diaSe = actividad.getFecha().getDayOfWeek().getValue();
            LocalDateTime lunesSe = actividad.getFecha().minusDays(diaSe - 1);
            ArrayList<ArrayList<Actividad>> actSe = actividades.getOrDefault(lunesSe, new ArrayList<>());
        
            while (actSe.size() < 7) {
                actSe.add(new ArrayList<>(Arrays.asList(null, null, null, null)));
            }

            ArrayList<Actividad> actDiaSe = actSe.get(diaSe - 1);
            int slotIndex = getSlotIndex(actividad.getFecha().toLocalTime());

            if (slotIndex != -1) {
                actDiaSe.set(slotIndex, actividad);
            }
            actividades.put(lunesSe, actSe);
        }
    }

    private int getSlotIndex(LocalTime time) {
        if (!time.isBefore(LocalTime.of(9, 0)) && time.isBefore(LocalTime.of(11, 0))) return 0;
        if (!time.isBefore(LocalTime.of(12, 0)) && time.isBefore(LocalTime.of(14, 0))) return 1;
        if (!time.isBefore(LocalTime.of(16, 0)) && time.isBefore(LocalTime.of(18, 0))) return 2;
        if (!time.isBefore(LocalTime.of(18, 0)) && time.isBefore(LocalTime.of(20, 0))) return 3;
        return -1;
    }

    public void iniciarTabla() {
        Vector<String> diasSemana = new Vector<>(Arrays.asList("Horarios", "Lunes", "Martes", "Miércoles", "Jueves", "Viernes", "Sábado", "Domingo"));
        modelo = new DefaultTableModel(diasSemana, 0);
        tabla = new JTable(modelo) {
            private static final long serialVersionUID = 1L;

            @Override
            public boolean isCellEditable(int row, int column) {
                return column > 0;
            }
        };

        tabla.setDefaultRenderer(Object.class, new TableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                                                           boolean hasFocus, int row, int column) {
                if (value instanceof Actividad) {
                    Actividad actividad = (Actividad) value;
                    JButton button = new JButton(PanelActividadDialog.escalarImagen(actividad.getLogo(),55,55));
                    if (row == hoveredRow && column == hoveredColumn) {
                        button.setBackground(Color.LIGHT_GRAY);
                    } else {
                        button.setBackground(null);
                    }
                    return button;
                } else {
                    JLabel label = new JLabel(value != null ? value.toString() : "");
                    if (row == hoveredRow && column == hoveredColumn) {
                        label.setBackground(Color.LIGHT_GRAY);
                        label.setOpaque(true);
                    } else {
                        label.setOpaque(false);
                    }
                    return label;
                }
            }
        });

        tabla.setDefaultEditor(Object.class, new ButtonEditor());

        tabla.setRowHeight(60);
        tabla.getColumnModel().getColumn(0).setPreferredWidth(350);
        for (int i = 1; i < tabla.getColumnCount(); i++) {
            tabla.getColumnModel().getColumn(i).setPreferredWidth(300);
        }
        tabla.getTableHeader().setReorderingAllowed(false);
        tabla.getTableHeader().setResizingAllowed(false);

        tabla.addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                int row = tabla.rowAtPoint(e.getPoint());
                int column = tabla.columnAtPoint(e.getPoint());

                if (row != hoveredRow || column != hoveredColumn) {
                    hoveredRow = row;
                    hoveredColumn = column;
                    tabla.repaint(); 
                }
            }
        });
    }

    public void loadActividades() {
        modelo.setRowCount(0);
        ArrayList<String> horarios = new ArrayList<>(Arrays.asList("9:00-11:00", "12:00-14:00", "16:00-18:00", "18:00-20:00"));

        ArrayList<ArrayList<Actividad>> as = actividades.get(fecha);
        if (as != null) {
            for (int i = 0; i < 4; i++) {
                Vector<Object> row = new Vector<>();
                row.add(horarios.get(i));
                for (int j = 0; j < 7; j++) {
                    Actividad actividad = as.get(j).get(i);
                    row.add(actividad != null ? actividad : "");
                }
                modelo.addRow(row);
            }
        }
    }

    // Hecho con ayuda de la IA
    class ButtonEditor extends AbstractCellEditor implements TableCellEditor {
		private static final long serialVersionUID = 1L;
		private JButton button;

        public ButtonEditor() {
            button = new JButton();
            button.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    Actividad actividad = (Actividad) button.getClientProperty("actividad");
                    if (actividad != null) {
                        new PanelActividadDialog(actividad).setVisible(true);
                    }
                    fireEditingStopped();
                }
            });
        }

        @Override
        public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
            if (value instanceof Actividad) {
                Actividad actividad = (Actividad) value;
                button.setIcon(PanelActividadDialog.escalarImagen(actividad.getLogo(),55,55));
                button.putClientProperty("actividad", actividad);
                return button;
            }
            return null;
        }

        @Override
        public Object getCellEditorValue() {
            return button.getClientProperty("actividad");
        }
    }
}