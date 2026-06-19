package vista;

import controlador.UsuarioControlador;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class PosicionesPanel extends JPanel {

    private final UsuarioControlador usuarioControlador;
    private DefaultTableModel modeloTablaPosiciones;

    public PosicionesPanel(UsuarioControlador usuarioControlador) {
        this.usuarioControlador = usuarioControlador;

        setLayout(new BorderLayout(0, 10));
        setBackground(UIStyleUtil.FONDO);
        setBorder(new EmptyBorder(16, 16, 16, 16));

        JLabel titulo = new JLabel("Tabla de Posiciones", SwingConstants.CENTER);
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 18));
        titulo.setForeground(UIStyleUtil.VERDE_OSCURO);
        add(titulo, BorderLayout.NORTH);

        String[] cols = {"Puesto", "Apostador", "Puntos"};
        modeloTablaPosiciones = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        JTable tabla = new JTable(modeloTablaPosiciones);
        UIStyleUtil.styleTable(tabla);

        JScrollPane sp = new JScrollPane(tabla);
        sp.setBorder(BorderFactory.createLineBorder(new Color(60, 60, 70)));
        sp.getViewport().setBackground(UIStyleUtil.FONDO);
        add(sp, BorderLayout.CENTER);

        JButton btn = UIStyleUtil.btnRedondeado("Actualizar", UIStyleUtil.VERDE_BTN, Color.WHITE);
        btn.addActionListener(e -> actualizarRankingTable());
        
        JPanel sur = new JPanel(new FlowLayout(FlowLayout.CENTER));
        sur.setOpaque(false);
        sur.add(btn);
        add(sur, BorderLayout.SOUTH);

        actualizarRankingTable();
    }

    public void actualizarRankingTable() {
        if (modeloTablaPosiciones == null) return;
        modeloTablaPosiciones.setRowCount(0);
        int puesto = 1;
        for (Object[] fila : usuarioControlador.obtenerTablaPosiciones()) {
            modeloTablaPosiciones.addRow(new Object[]{puesto++, fila[1], fila[2] + " pts"});
        }
    }
}
