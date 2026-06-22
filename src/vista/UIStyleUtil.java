package vista;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;

public class UIStyleUtil {

    public static final Color SIDEBAR_BG = new Color(20, 60, 30);
    public static final Color VERDE_OSCURO = new Color(20, 60, 30);
    public static final Color VERDE_BTN = new Color(22, 68, 35);
    public static final Color AMARILLO = new Color(230, 190, 40);
    public static final Color FONDO = new Color(242, 244, 240);
    public static final Color CARD_BG = Color.WHITE;
    public static final Color TEXTO_GRIS = new Color(110, 110, 110);
    public static final Color ROJO_LIVE = new Color(210, 50, 50);
    public static final Color VERDE_OK = new Color(39, 174, 96);
    public static final Color AMARILLO_WARN = new Color(200, 150, 10);
    public static final Color AMARILLO_SUAVE = new Color(255, 248, 210);

    public static JPanel card() {
        JPanel p = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(CARD_BG);
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 16, 16));
                g2.dispose();
            }
        };
        p.setOpaque(false);
        return p;
    }

    public static JButton btnRedondeado(String texto, Color bg, Color fg) {
        JButton btn = new JButton(texto) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getModel().isPressed() ? bg.darker() : bg);
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 12, 12));
                g2.dispose();
                super.paintComponent(g);
            }
        };
        btn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btn.setForeground(fg);
        btn.setOpaque(false);
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setPreferredSize(new Dimension(220, 44));
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return btn;
    }

    public static JPanel spinPanel(String label, JSpinner spin) {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.CENTER, 4, 0));
        p.setOpaque(false);
        JLabel lbl = new JLabel(label + ":");
        lbl.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lbl.setForeground(TEXTO_GRIS);
        p.add(lbl);
        p.add(spin);
        return p;
    }

    private static boolean alertaMostrada = false;

    public static void configurarSoloNumeros(JSpinner spinner) {
        configurarSoloNumeros(spinner, 20);
    }

    public static void configurarSoloNumeros(JSpinner spinner, int max) {
        JComponent editor = spinner.getEditor();
        if (!(editor instanceof JSpinner.DefaultEditor)) return;
        JFormattedTextField txt = ((JSpinner.DefaultEditor) editor).getTextField();

        // 1. Bloquear caracteres que no sean dígitos en el teclado
        txt.addKeyListener(new java.awt.event.KeyAdapter() {
            @Override
            public void keyTyped(java.awt.event.KeyEvent e) {
                char c = e.getKeyChar();
                if (!Character.isDigit(c) && c != java.awt.event.KeyEvent.VK_BACK_SPACE
                        && c != java.awt.event.KeyEvent.VK_DELETE) {
                    e.consume();
                }
            }
        });

        // 2. DocumentListener para validar en tiempo real mientras el usuario escribe
        txt.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            @Override
            public void insertUpdate(javax.swing.event.DocumentEvent e) { verificar(); }
            @Override
            public void removeUpdate(javax.swing.event.DocumentEvent e) { verificar(); }
            @Override
            public void changedUpdate(javax.swing.event.DocumentEvent e) { verificar(); }

            private void verificar() {
                SwingUtilities.invokeLater(() -> {
                    try {
                        String texto = txt.getText().trim();
                        if (!texto.isEmpty()) {
                            int val = Integer.parseInt(texto);
                            if (val > max) {
                                mostrarAlertaLimite(spinner, max);
                                txt.setText(String.valueOf(max));
                                spinner.setValue(max);
                            }
                        }
                    } catch (NumberFormatException ex) {
                        txt.setText("0");
                        spinner.setValue(0);
                    }
                });
            }
        });

        // 3. FocusListener para validar cuando pierde el foco (por ejemplo, si queda vacío o es inválido)
        txt.addFocusListener(new java.awt.event.FocusAdapter() {
            @Override
            public void focusLost(java.awt.event.FocusEvent e) {
                validarYFijar(spinner, txt, max);
            }
        });

        // 4. ActionListener para validar al presionar Enter
        txt.addActionListener(e -> validarYFijar(spinner, txt, max));

        // 5. ChangeListener para las flechas del spinner
        spinner.addChangeListener(e -> {
            int val = ((Number) spinner.getValue()).intValue();
            if (val > max) {
                spinner.setValue(max);
                mostrarAlertaLimite(spinner, max);
            }
        });
    }

    private static void validarYFijar(JSpinner spinner, JFormattedTextField txt, int max) {
        try {
            String texto = txt.getText().trim();
            if (texto.isEmpty()) {
                spinner.setValue(0);
                txt.setText("0");
            } else {
                int valor = Integer.parseInt(texto);
                if (valor > max) {
                    mostrarAlertaLimite(spinner, max);
                    spinner.setValue(max);
                    txt.setText(String.valueOf(max));
                } else if (valor < 0) {
                    spinner.setValue(0);
                    txt.setText("0");
                } else {
                    spinner.setValue(valor);
                }
            }
        } catch (NumberFormatException ex) {
            spinner.setValue(0);
            txt.setText("0");
        }
    }

    private static void mostrarAlertaLimite(JSpinner spinner, int maxValor) {
        if (alertaMostrada) return;
        alertaMostrada = true;
        Toolkit.getDefaultToolkit().beep();
        SwingUtilities.invokeLater(() -> {
            JOptionPane.showMessageDialog(
                SwingUtilities.getWindowAncestor(spinner),
                "El valor m\u00e1ximo permitido es " + maxValor + " goles.",
                "L\u00edmite excedido",
                JOptionPane.WARNING_MESSAGE
            );
            alertaMostrada = false;
        });
    }

    public static void styleTable(JTable table) {
        table.setBackground(CARD_BG);
        table.setForeground(Color.BLACK);
        table.setGridColor(new Color(220, 225, 215));
        table.setRowHeight(30);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        table.setSelectionBackground(VERDE_BTN);
        table.setSelectionForeground(Color.WHITE);
        table.setShowGrid(true);
        table.setFillsViewportHeight(true);

        javax.swing.table.JTableHeader header = table.getTableHeader();
        header.setBackground(new Color(230, 235, 225));
        header.setForeground(VERDE_OSCURO);
        header.setFont(new Font("Segoe UI", Font.BOLD, 13));
        header.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, VERDE_BTN));

        javax.swing.table.DefaultTableCellRenderer cellRenderer = new javax.swing.table.DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object val, boolean isSelected, boolean hasFocus,
                    int row, int col) {
                Component c = super.getTableCellRendererComponent(t, val, isSelected, hasFocus, row, col);
                setHorizontalAlignment(SwingConstants.CENTER);
                if (isSelected) {
                    c.setBackground(VERDE_BTN);
                    c.setForeground(Color.WHITE);
                } else {
                    c.setForeground(Color.BLACK);
                    if (row % 2 == 0) {
                        c.setBackground(CARD_BG);
                    } else {
                        c.setBackground(new Color(245, 247, 242));
                    }
                }
                return c;
            }
        };
        for (int i = 0; i < table.getColumnCount(); i++) {
            table.getColumnModel().getColumn(i).setCellRenderer(cellRenderer);
        }
    }

    public static void styleSpinner(JSpinner spinner) {
        JComponent editor = spinner.getEditor();
        if (editor instanceof JSpinner.DefaultEditor) {
            JFormattedTextField ft = ((JSpinner.DefaultEditor) editor).getTextField();
            ft.setBackground(Color.WHITE);
            ft.setForeground(Color.BLACK);
            ft.setCaretColor(Color.BLACK);
            ft.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200)));
        }
    }

    public static boolean haPasadoFecha(String fechaStr) {
        return false;
    }
}
