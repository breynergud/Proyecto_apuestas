package vista;

import modelo.Partido;
import controlador.ApuestaControlador;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;

public class ResultadosPanel extends JPanel {

    private final MenuPrincipal parent;
    private final ApuestaControlador apuestaControlador;

    private JComboBox<String> comboGruposResultados;
    private JPanel panelPartidosResultados;
    private List<PartidoResultadoPanel> listaPanelesResultados = new ArrayList<>();
    private final String[] gruposLetras = {"A","B","C","D","E","F","G","H","I","J","K","L"};

    public ResultadosPanel(MenuPrincipal parent, ApuestaControlador apuestaControlador) {
        this.parent = parent;
        this.apuestaControlador = apuestaControlador;

        setLayout(new BorderLayout(0, 8));
        setBackground(UIStyleUtil.FONDO);
        setBorder(new EmptyBorder(12, 16, 12, 16));

        JPanel norte = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        norte.setOpaque(false);
        JLabel lbl = new JLabel("Grupo (Admin):");
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lbl.setForeground(UIStyleUtil.VERDE_OSCURO);
        
        comboGruposResultados = new JComboBox<>(gruposLetras);
        comboGruposResultados.setBackground(Color.WHITE);
        comboGruposResultados.setForeground(Color.BLACK);
        comboGruposResultados.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200)));
        comboGruposResultados.addActionListener(e -> cargarPartidosParaResultados());
        
        norte.add(lbl); norte.add(comboGruposResultados);
        add(norte, BorderLayout.NORTH);

        panelPartidosResultados = new JPanel();
        panelPartidosResultados.setLayout(new BoxLayout(panelPartidosResultados, BoxLayout.Y_AXIS));
        panelPartidosResultados.setBackground(UIStyleUtil.CARD_BG);
        
        JScrollPane sp = new JScrollPane(panelPartidosResultados);
        sp.setBorder(BorderFactory.createLineBorder(new Color(60, 60, 70)));
        sp.getViewport().setBackground(UIStyleUtil.FONDO);
        add(sp, BorderLayout.CENTER);

        JButton btn = UIStyleUtil.btnRedondeado("Registrar Resultados", new Color(52, 130, 200), Color.WHITE);
        btn.addActionListener(this::guardarResultadosGrupo);
        
        JPanel sur = new JPanel(new FlowLayout(FlowLayout.CENTER));
        sur.setOpaque(false);
        sur.add(btn);
        add(sur, BorderLayout.SOUTH);

        cargarPartidosParaResultados();
    }

    public void cargarPartidosParaResultados() {
        if (panelPartidosResultados == null) return;
        panelPartidosResultados.removeAll();
        listaPanelesResultados = new ArrayList<>();
        String grupo = (String) comboGruposResultados.getSelectedItem();
        if (grupo == null) grupo = "A";
        
        for (Partido p : apuestaControlador.obtenerPartidosPorGrupo(grupo)) {
            PartidoResultadoPanel pp = new PartidoResultadoPanel(p);
            panelPartidosResultados.add(pp);
            listaPanelesResultados.add(pp);
        }
        panelPartidosResultados.revalidate();
        panelPartidosResultados.repaint();
    }

    private void guardarResultadosGrupo(ActionEvent e) {
        int ok = JOptionPane.showConfirmDialog(parent, "¿Confirmar actualización de marcadores?", "Confirmar", JOptionPane.YES_NO_OPTION);
        if (ok != JOptionPane.YES_OPTION) return;
        for (PartidoResultadoPanel p : listaPanelesResultados) {
            if (p.isFinalizadoChecked())
                apuestaControlador.registrarResultadoReal(p.getPartidoId(), p.getGolesLocal(), p.getGolesVisitante());
            else
                apuestaControlador.registrarResultadoReal(p.getPartidoId(), null, null);
        }
        JOptionPane.showMessageDialog(parent, "Resultados registrados con éxito.", "Listo", JOptionPane.INFORMATION_MESSAGE);
        cargarPartidosParaResultados();
        parent.refrescarTodo();
    }

    class PartidoResultadoPanel extends JPanel {
        private final int partidoId;
        private final JSpinner spinLocal, spinVisita;
        private final JCheckBox chk;

        PartidoResultadoPanel(Partido part) {
            this.partidoId = part.getId();
            setLayout(new GridLayout(1, 4, 8, 0));
            setBackground(UIStyleUtil.CARD_BG);
            setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(220, 225, 215)),
                new EmptyBorder(10, 14, 10, 14)));
            setMaximumSize(new Dimension(Integer.MAX_VALUE, 52));

            JLabel lbl = new JLabel(part.getLocal() + " vs " + part.getVisitante());
            lbl.setFont(new Font("Segoe UI", Font.BOLD, 12));
            lbl.setForeground(UIStyleUtil.VERDE_OSCURO);
            add(lbl);

            int vL = part.getGolesLocal()     != null ? part.getGolesLocal()     : 0;
            int vV = part.getGolesVisitante() != null ? part.getGolesVisitante() : 0;
            spinLocal  = new JSpinner(new SpinnerNumberModel(vL, 0, 20, 1));
            spinVisita = new JSpinner(new SpinnerNumberModel(vV, 0, 20, 1));
            UIStyleUtil.configurarSoloNumeros(spinLocal);
            UIStyleUtil.configurarSoloNumeros(spinVisita);
            UIStyleUtil.styleSpinner(spinLocal);
            UIStyleUtil.styleSpinner(spinVisita);
            
            add(UIStyleUtil.spinPanel("Local", spinLocal));
            add(UIStyleUtil.spinPanel("Visita", spinVisita));

            boolean fechaPasada = UIStyleUtil.haPasadoFecha(part.getFecha());
            chk = new JCheckBox("Finalizado", part.isRegistrado() || fechaPasada);
            chk.setOpaque(false);
            chk.setForeground(UIStyleUtil.VERDE_OSCURO);
            add(chk);

            // Seleccionar "Finalizado" automáticamente al cambiar los goles (flechas o texto)
            javax.swing.event.ChangeListener autoCheck = e -> chk.setSelected(true);
            spinLocal.addChangeListener(autoCheck);
            spinVisita.addChangeListener(autoCheck);

            if (spinLocal.getEditor() instanceof JSpinner.DefaultEditor) {
                ((JSpinner.DefaultEditor) spinLocal.getEditor()).getTextField().getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
                    public void insertUpdate(javax.swing.event.DocumentEvent e) { chk.setSelected(true); }
                    public void removeUpdate(javax.swing.event.DocumentEvent e) { chk.setSelected(true); }
                    public void changedUpdate(javax.swing.event.DocumentEvent e) { chk.setSelected(true); }
                });
            }
            if (spinVisita.getEditor() instanceof JSpinner.DefaultEditor) {
                ((JSpinner.DefaultEditor) spinVisita.getEditor()).getTextField().getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
                    public void insertUpdate(javax.swing.event.DocumentEvent e) { chk.setSelected(true); }
                    public void removeUpdate(javax.swing.event.DocumentEvent e) { chk.setSelected(true); }
                    public void changedUpdate(javax.swing.event.DocumentEvent e) { chk.setSelected(true); }
                });
            }
        }

        int getPartidoId()        { return partidoId; }
        
        int getGolesLocal() {
            try {
                spinLocal.commitEdit();
            } catch (java.text.ParseException pe) {
                // Revert to last valid value
            }
            return (int) spinLocal.getValue();
        }
        
        int getGolesVisitante() {
            try {
                spinVisita.commitEdit();
            } catch (java.text.ParseException pe) {
                // Revert to last valid value
            }
            return (int) spinVisita.getValue();
        }
        
        boolean isFinalizadoChecked() { return chk.isSelected(); }
    }
}
