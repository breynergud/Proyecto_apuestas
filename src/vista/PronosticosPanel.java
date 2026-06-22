package vista;

import modelo.Usuario;
import modelo.Partido;
import modelo.Apuesta;
import controlador.ApuestaControlador;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;

public class PronosticosPanel extends JPanel {

    private final MenuPrincipal parent;
    private final Usuario usuarioLogueado;
    private final ApuestaControlador apuestaControlador;

    private JComboBox<String> comboGruposPronosticos;
    private JPanel panelPartidosApuestas;
    private List<PartidoApuestaPanel> listaPanelesApuestas = new ArrayList<>();
    private final String[] gruposLetras = {"A","B","C","D","E","F","G","H","I","J","K","L"};

    public PronosticosPanel(MenuPrincipal parent, Usuario usuarioLogueado, ApuestaControlador apuestaControlador) {
        this.parent = parent;
        this.usuarioLogueado = usuarioLogueado;
        this.apuestaControlador = apuestaControlador;

        setLayout(new BorderLayout(0, 8));
        setBackground(UIStyleUtil.FONDO);
        setBorder(new EmptyBorder(12, 16, 12, 16));

        JPanel norte = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        norte.setOpaque(false);
        JLabel lbl = new JLabel("Grupo:");
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lbl.setForeground(UIStyleUtil.VERDE_OSCURO);
        
        comboGruposPronosticos = new JComboBox<>(gruposLetras);
        comboGruposPronosticos.setBackground(Color.WHITE);
        comboGruposPronosticos.setForeground(Color.BLACK);
        comboGruposPronosticos.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200)));
        comboGruposPronosticos.addActionListener(e -> cargarPartidosParaApuestas());
        
        norte.add(lbl); norte.add(comboGruposPronosticos);
        add(norte, BorderLayout.NORTH);

        panelPartidosApuestas = new JPanel();
        panelPartidosApuestas.setLayout(new BoxLayout(panelPartidosApuestas, BoxLayout.Y_AXIS));
        panelPartidosApuestas.setBackground(UIStyleUtil.CARD_BG);
        
        JScrollPane sp = new JScrollPane(panelPartidosApuestas);
        sp.setBorder(BorderFactory.createLineBorder(new Color(60, 60, 70)));
        sp.getViewport().setBackground(UIStyleUtil.FONDO);
        add(sp, BorderLayout.CENTER);

        JButton btn = UIStyleUtil.btnRedondeado("Guardar Pron\u00f3stico", UIStyleUtil.VERDE_BTN, Color.WHITE);
        btn.addActionListener(this::guardarApuestasGrupo);
        
        JPanel sur = new JPanel(new FlowLayout(FlowLayout.CENTER));
        sur.setOpaque(false);
        sur.add(btn);
        add(sur, BorderLayout.SOUTH);

        cargarPartidosParaApuestas();
    }

    public void cargarPartidosParaApuestas() {
        if (panelPartidosApuestas == null) return;
        panelPartidosApuestas.removeAll();
        listaPanelesApuestas = new ArrayList<>();
        String grupo = (String) comboGruposPronosticos.getSelectedItem();
        if (grupo == null) grupo = "A";
        
        for (Partido p : apuestaControlador.obtenerPartidosPorGrupo(grupo)) {
            Apuesta prev = apuestaControlador.buscarApuestaUsuario(usuarioLogueado.getId(), p.getId());
            PartidoApuestaPanel pp = new PartidoApuestaPanel(p, prev);
            panelPartidosApuestas.add(pp);
            listaPanelesApuestas.add(pp);
        }
        panelPartidosApuestas.revalidate();
        panelPartidosApuestas.repaint();
    }

    private void guardarApuestasGrupo(ActionEvent e) {
        boolean guardadoAlguna = false;
        for (PartidoApuestaPanel pPanel : listaPanelesApuestas) {
            if (!pPanel.tieneApuestaPrevia() && !pPanel.isFechaPasada()) {
                int golesL = pPanel.getGolesLocal();
                int golesV = pPanel.getGolesVisitante();
                if (golesL >= 0 && golesV >= 0) {
                    apuestaControlador.guardarApuestaUsuario(
                        usuarioLogueado.getId(),
                        pPanel.getPartidoId(),
                        golesL,
                        golesV
                    );
                    guardadoAlguna = true;
                }
            }
        }
        if (guardadoAlguna) {
            JOptionPane.showMessageDialog(parent, "\u00a1Pron\u00f3sticos guardados con \u00e9xito!", "\u00c9xito", JOptionPane.INFORMATION_MESSAGE);
            parent.refrescarTodo();
            cargarPartidosParaApuestas();
        } else {
            JOptionPane.showMessageDialog(parent, "No hay nuevos pron\u00f3sticos para guardar en este grupo.", "Informaci\u00f3n", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    class PartidoApuestaPanel extends JPanel {
        private final int partidoId;
        private final JSpinner spinLocal, spinVisita;
        private final boolean tieneApuestaPrevia;
        private final boolean fechaPasada;

        PartidoApuestaPanel(Partido part, Apuesta prev) {
            this.partidoId = part.getId();
            this.tieneApuestaPrevia = (prev != null);
            this.fechaPasada = UIStyleUtil.haPasadoFecha(part.getFecha());
            setLayout(new GridLayout(1, 5, 8, 0));
            setBackground(UIStyleUtil.CARD_BG);
            setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(220, 225, 215)),
                new EmptyBorder(10, 14, 10, 14)));
            setMaximumSize(new Dimension(Integer.MAX_VALUE, 52));

            JLabel lbl = new JLabel(part.getLocal() + " vs " + part.getVisitante());
            lbl.setFont(new Font("Segoe UI", Font.BOLD, 12));
            lbl.setForeground(UIStyleUtil.VERDE_OSCURO);
            add(lbl);

            JLabel fecha = new JLabel(part.getFecha(), SwingConstants.CENTER);
            fecha.setFont(new Font("Segoe UI", Font.PLAIN, 11));
            fecha.setForeground(UIStyleUtil.TEXTO_GRIS);
            add(fecha);

            int vL = Math.min(prev != null ? prev.getGolesLocal() : 0, 15);
            int vV = Math.min(prev != null ? prev.getGolesVisitante() : 0, 15);
            spinLocal  = new JSpinner(new SpinnerNumberModel(vL, 0, 15, 1));
            spinVisita = new JSpinner(new SpinnerNumberModel(vV, 0, 15, 1));
            UIStyleUtil.configurarSoloNumeros(spinLocal, 15);
            UIStyleUtil.configurarSoloNumeros(spinVisita, 15);
            UIStyleUtil.styleSpinner(spinLocal);
            UIStyleUtil.styleSpinner(spinVisita);
            
            if (tieneApuestaPrevia || fechaPasada) {
                spinLocal.setEnabled(false);
                spinVisita.setEnabled(false);
            }
            
            add(UIStyleUtil.spinPanel("Local", spinLocal));
            add(UIStyleUtil.spinPanel("Visita", spinVisita));

            String estadoTexto = part.isRegistrado() ? "Final" : (fechaPasada ? "Finalizado" : "Pendiente");
            JLabel estado = new JLabel(estadoTexto, SwingConstants.CENTER);
            estado.setFont(new Font("Segoe UI", Font.BOLD, 11));
            estado.setForeground((part.isRegistrado() || fechaPasada) ? UIStyleUtil.VERDE_OK : UIStyleUtil.AMARILLO_WARN);
            add(estado);
        }

        int getPartidoId()      { return partidoId; }
        
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
        
        boolean tieneApuestaPrevia() { return tieneApuestaPrevia; }
        boolean isFechaPasada() { return fechaPasada; }
    }
}
