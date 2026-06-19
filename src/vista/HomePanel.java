package vista;

import modelo.Usuario;
import controlador.UsuarioControlador;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import java.util.List;

public class HomePanel extends JPanel {

    private final MenuPrincipal parent;
    private final Usuario usuarioLogueado;
    private final UsuarioControlador usuarioControlador;

    private JLabel lblPuntos;
    private JLabel lblRango;

    public HomePanel(MenuPrincipal parent, Usuario usuarioLogueado, UsuarioControlador usuarioControlador) {
        this.parent = parent;
        this.usuarioLogueado = usuarioLogueado;
        this.usuarioControlador = usuarioControlador;

        setLayout(new GridBagLayout());
        setBackground(UIStyleUtil.FONDO);
        setBorder(new EmptyBorder(20, 25, 20, 25));

        JPanel colLeft = new JPanel();
        colLeft.setLayout(new BoxLayout(colLeft, BoxLayout.Y_AXIS));
        colLeft.setOpaque(false);

        // Saludo
        JLabel lblHola = new JLabel("Hola, " + usuarioLogueado.getNombre() + "!");
        lblHola.setFont(new Font("Segoe UI", Font.BOLD, 26));
        lblHola.setForeground(UIStyleUtil.VERDE_OSCURO);
        lblHola.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        JLabel lblSub = new JLabel("Listo para la jornada de hoy en el Mundial?");
        lblSub.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        lblSub.setForeground(UIStyleUtil.TEXTO_GRIS);
        lblSub.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        colLeft.add(lblHola);
        colLeft.add(Box.createVerticalStrut(4));
        colLeft.add(lblSub);
        colLeft.add(Box.createVerticalStrut(18));

        // Badge PRO MEMBER
        JPanel badge = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 6)) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(UIStyleUtil.AMARILLO);
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 20, 20));
                g2.dispose();
            }
        };
        badge.setOpaque(false);
        badge.setMaximumSize(new Dimension(150, 32));
        badge.setAlignmentX(Component.LEFT_ALIGNMENT);
        JLabel lblBadge = new JLabel("PRO MEMBER");
        lblBadge.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblBadge.setForeground(UIStyleUtil.VERDE_OSCURO);
        badge.add(lblBadge);
        
        colLeft.add(badge);
        colLeft.add(Box.createVerticalStrut(20));

        // Stats cards row
        JPanel rowStats = new JPanel(new GridLayout(1, 2, 16, 0));
        rowStats.setOpaque(false);
        rowStats.setMaximumSize(new Dimension(Integer.MAX_VALUE, 110));
        rowStats.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Card puntos
        JPanel cardPuntos = UIStyleUtil.card();
        cardPuntos.setLayout(new BoxLayout(cardPuntos, BoxLayout.Y_AXIS));
        cardPuntos.setBorder(new EmptyBorder(16, 20, 16, 20));
        JLabel lblPLabel = new JLabel("TUS PUNTOS");
        lblPLabel.setFont(new Font("Segoe UI", Font.BOLD, 11));
        lblPLabel.setForeground(UIStyleUtil.TEXTO_GRIS);
        lblPuntos = new JLabel("0");
        lblPuntos.setFont(new Font("Segoe UI", Font.BOLD, 30));
        lblPuntos.setForeground(UIStyleUtil.VERDE_OSCURO);
        JLabel lblPChange = new JLabel("Puntos acumulados");
        lblPChange.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblPChange.setForeground(UIStyleUtil.TEXTO_GRIS);
        cardPuntos.add(lblPLabel); cardPuntos.add(Box.createVerticalStrut(4));
        cardPuntos.add(lblPuntos); cardPuntos.add(lblPChange);

        // Card rango
        JPanel cardRango = UIStyleUtil.card();
        cardRango.setLayout(new BoxLayout(cardRango, BoxLayout.Y_AXIS));
        cardRango.setBorder(new EmptyBorder(16, 20, 16, 20));
        JLabel lblRLabel = new JLabel("RANGO ACTUAL");
        lblRLabel.setFont(new Font("Segoe UI", Font.BOLD, 11));
        lblRLabel.setForeground(UIStyleUtil.TEXTO_GRIS);
        lblRango = new JLabel("S/R");
        lblRango.setFont(new Font("Segoe UI", Font.BOLD, 30));
        lblRango.setForeground(UIStyleUtil.VERDE_OSCURO);
        JLabel lblRSub = new JLabel("Posición actual");
        lblRSub.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblRSub.setForeground(UIStyleUtil.TEXTO_GRIS);
        cardRango.add(lblRLabel); cardRango.add(Box.createVerticalStrut(4));
        cardRango.add(lblRango); cardRango.add(lblRSub);

        rowStats.add(cardPuntos); rowStats.add(cardRango);
        colLeft.add(rowStats);
        colLeft.add(Box.createVerticalStrut(25));

        // Acciones Rápidas
        JLabel lblAcciones = new JLabel("Acciones Rápidas");
        lblAcciones.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblAcciones.setForeground(UIStyleUtil.VERDE_OSCURO);
        lblAcciones.setAlignmentX(Component.LEFT_ALIGNMENT);
        colLeft.add(lblAcciones);
        colLeft.add(Box.createVerticalStrut(12));

        JPanel gridAcciones = new JPanel(new GridLayout(2, 2, 12, 12));
        gridAcciones.setMaximumSize(new Dimension(Integer.MAX_VALUE, 160));
        gridAcciones.setOpaque(false);
        gridAcciones.setAlignmentX(Component.LEFT_ALIGNMENT);

        if (usuarioLogueado.esAdministrador()) {
            gridAcciones.add(accionCard("Gestionar\nPartidos", false, e -> parent.mostrarTab("resultados", 3)));
            gridAcciones.add(accionCard("Consultar\nPosiciones", false, e -> parent.mostrarTab("posiciones", 2)));
            gridAcciones.add(accionCard("Ver Todas Apuestas",  false, e -> parent.mostrarTab("apuestas", 1)));
            gridAcciones.add(accionCard("Historial Auditoría", false, e -> parent.mostrarTab("historial", 4)));
        } else {
            gridAcciones.add(accionCard("Ver Grupos",         false, e -> parent.mostrarTab("pronosticos", 1)));
            gridAcciones.add(accionCard("Consultar\nPosiciones", false, e -> parent.mostrarTab("posiciones", 3)));
            gridAcciones.add(accionCard("Mis Apuestas",       false, e -> parent.mostrarTab("apuestas", 2)));
            gridAcciones.add(accionCard("Gestionar\nPartidos", true, e -> JOptionPane.showMessageDialog(parent, "Sólo disponible para Administradores.", "Restringido", JOptionPane.WARNING_MESSAGE)));
        }
        colLeft.add(gridAcciones);

        GridBagConstraints gbcLeft = new GridBagConstraints();
        gbcLeft.gridx = 0;
        gbcLeft.gridy = 0;
        gbcLeft.weightx = 1.0;
        gbcLeft.weighty = 1.0;
        gbcLeft.fill = GridBagConstraints.BOTH;
        add(colLeft, gbcLeft);

        actualizarPuntosYRango();
    }

    public void actualizarPuntosYRango() {
        if (lblPuntos == null || lblRango == null) return;
        if (usuarioLogueado.esAdministrador()) {
            lblPuntos.setText("0");
            lblRango.setText("S/R");
            return;
        }
        List<Object[]> posiciones = usuarioControlador.obtenerTablaPosiciones();
        int puntos = 0;
        int puesto = 0;
        boolean encontrado = false;
        for (int i = 0; i < posiciones.size(); i++) {
            Object[] fila = posiciones.get(i);
            int id = (Integer) fila[0];
            if (id == usuarioLogueado.getId()) {
                puntos = (Integer) fila[2];
                puesto = i + 1;
                encontrado = true;
                break;
            }
        }
        if (encontrado) {
            lblPuntos.setText(String.valueOf(puntos));
            lblRango.setText("#" + puesto);
        } else {
            Object[] data = usuarioControlador.obtenerPuntosYRankUsuario(usuarioLogueado.getId());
            lblPuntos.setText(String.valueOf(data[0]));
            lblRango.setText(data[1].equals(0) ? "S/R" : "#" + data[1]);
        }
    }

    private JPanel accionCard(String texto, boolean gris, java.awt.event.ActionListener action) {
        JPanel p = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(gris ? new Color(220, 220, 220) : UIStyleUtil.CARD_BG);
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 16, 16));
                g2.dispose();
            }
        };
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setOpaque(false);
        p.setBorder(new EmptyBorder(22, 10, 22, 10));
        p.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        p.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override public void mouseClicked(java.awt.event.MouseEvent e) { action.actionPerformed(null); }
        });

        String htmlText = "<html><center>" + texto.replace("\n", "<br>") + "</center></html>";
        JLabel lblTxt = new JLabel(htmlText, SwingConstants.CENTER);
        lblTxt.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblTxt.setForeground(gris ? new Color(80, 80, 80) : UIStyleUtil.VERDE_OSCURO);
        lblTxt.setAlignmentX(Component.CENTER_ALIGNMENT);

        p.add(Box.createVerticalGlue());
        p.add(lblTxt);
        p.add(Box.createVerticalGlue());
        return p;
    }
}
