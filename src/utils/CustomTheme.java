// utils/CustomTheme.java
package utils;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.table.JTableHeader;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;

public class CustomTheme {

    // ==================== COLOR PALETTE ====================
    // LoginFrame colors
    public static final Color DEEP_NAVY = new Color(10, 25, 47);
    public static final Color SOFT_GOLD = new Color(212, 175, 55);
    public static final Color LIGHT_GOLD = new Color(255, 215, 0);
    public static final Color DARK_NAVY = new Color(5, 15, 30);
    public static final Color CREAM_WHITE = new Color(255, 250, 240);
    public static final Color INPUT_BG = new Color(248, 248, 248);
    
    // Dashboard colors
    public static final Color PRIMARY_COLOR = DEEP_NAVY;
    public static final Color SECONDARY_COLOR = SOFT_GOLD;
    public static final Color SUCCESS_COLOR = new Color(67, 160, 71);
    public static final Color DANGER_COLOR = new Color(211, 47, 47);
    public static final Color WARNING_COLOR = new Color(251, 140, 0);
    public static final Color INFO_COLOR = new Color(0, 172, 193);
    public static final Color DARK_COLOR = DEEP_NAVY;
    public static final Color LIGHT_COLOR = new Color(238, 238, 238);
    public static final Color WHITE_COLOR = Color.WHITE;
    public static final Color GRAY_COLOR = new Color(117, 117, 117);
    public static final Color BACKGROUND_COLOR = CREAM_WHITE;
    public static final Color SIDEBAR_COLOR = DEEP_NAVY;
    public static final Color PANEL_HEADER_COLOR = new Color(255, 250, 240);
    public static final Color CARD_BACKGROUND_COLOR = Color.WHITE;
    public static final Color TABLE_ROW_EVEN = Color.WHITE;
    public static final Color TABLE_ROW_ODD = new Color(248, 249, 250);
    public static final Color TEXT_COLOR = DEEP_NAVY;
    public static final Color HEADER_BACKGROUND = DEEP_NAVY;
    public static final Color HEADER_TEXT_COLOR = Color.WHITE;

    // ==================== FONTS ====================
    public static final Font TITLE_FONT = new Font("Georgia", Font.BOLD, 26);
    public static final Font HEADER_FONT = new Font("Georgia", Font.BOLD, 18);
    public static final Font NORMAL_FONT = new Font("Segoe UI", Font.PLAIN, 14);
    public static final Font SMALL_FONT = new Font("Segoe UI", Font.PLAIN, 12);
    public static final Font BUTTON_FONT = new Font("Segoe UI", Font.BOLD, 14);
    public static final Font STATS_FONT = new Font("Segoe UI", Font.BOLD, 24);
    
    public static final Font EMOJI_FONT;
    
    static {
        Font emojiFont;
        try {
            emojiFont = new Font("Segoe UI Emoji", Font.PLAIN, 14);
        } catch (Exception e) {
            emojiFont = new Font("Dialog", Font.PLAIN, 14);
        }
        EMOJI_FONT = emojiFont;
    }

    public static Font getEmojiFont() {
        return EMOJI_FONT;
    }

    // ==================== SHADOW PANEL ====================
    public static class ShadowPanel extends JPanel {
        private int shadowSize = 5;
        private float shadowOpacity = 0.1f;
        private int cornerRadius = 15;

        public ShadowPanel() {
            setOpaque(false);
            setBackground(Color.WHITE);
        }

        public ShadowPanel(int radius, int shadow) {
            this.cornerRadius = radius;
            this.shadowSize = shadow;
            setOpaque(false);
            setBackground(Color.WHITE);
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2d = (Graphics2D) g.create();
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2d.setColor(new Color(0, 0, 0, (int) (shadowOpacity * 255)));
            g2d.fillRoundRect(shadowSize, shadowSize, getWidth() - shadowSize, getHeight() - shadowSize, cornerRadius, cornerRadius);
            g2d.setColor(getBackground());
            g2d.fillRoundRect(0, 0, getWidth() - shadowSize, getHeight() - shadowSize, cornerRadius, cornerRadius);
            g2d.dispose();
            super.paintComponent(g);
        }
    }

    // ==================== ROUNDED PANEL ====================
    public static class RoundedPanel extends JPanel {
        private int cornerRadius;
        private Color backgroundColor;
        private Color borderColor;
        private int borderWidth;

        public RoundedPanel(int radius) {
            this.cornerRadius = radius;
            this.backgroundColor = Color.WHITE;
            this.borderColor = null;
            this.borderWidth = 0;
            setOpaque(false);
        }

        public RoundedPanel(int radius, Color bgColor) {
            this(radius);
            this.backgroundColor = bgColor;
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2d = (Graphics2D) g.create();
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            if (backgroundColor != null) {
                g2d.setColor(backgroundColor);
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), cornerRadius, cornerRadius);
            }
            if (borderColor != null && borderWidth > 0) {
                g2d.setColor(borderColor);
                g2d.setStroke(new BasicStroke(borderWidth));
                g2d.drawRoundRect(borderWidth/2, borderWidth/2,
                        getWidth() - borderWidth, getHeight() - borderWidth,
                        cornerRadius, cornerRadius);
            }
            g2d.dispose();
        }
    }

    // ==================== MODERN BUTTON ====================
    public static class ModernButton extends JButton {
        private int cornerRadius = 25;
        private Color hoverColor;
        private Color originalColor;
        private Color pressedColor;

        public ModernButton(String text) {
            super(text);
            setBorderPainted(false);
            setFocusPainted(false);
            setContentAreaFilled(false);
            setFont(BUTTON_FONT);
            setForeground(Color.WHITE);
            setCursor(new Cursor(Cursor.HAND_CURSOR));
            setMargin(new Insets(12, 25, 12, 25));

            originalColor = PRIMARY_COLOR;
            hoverColor = new Color(30, 50, 80);
            pressedColor = new Color(5, 15, 35);
            setBackground(originalColor);

            addMouseListener(new java.awt.event.MouseAdapter() {
                @Override
                public void mouseEntered(java.awt.event.MouseEvent evt) {
                    setBackground(hoverColor);
                }
                @Override
                public void mouseExited(java.awt.event.MouseEvent evt) {
                    setBackground(originalColor);
                }
                @Override
                public void mousePressed(java.awt.event.MouseEvent evt) {
                    setBackground(pressedColor);
                }
                @Override
                public void mouseReleased(java.awt.event.MouseEvent evt) {
                    setBackground(hoverColor);
                }
            });
        }

        public ModernButton(String text, Color bgColor) {
            this(text);
            this.originalColor = bgColor;
            this.hoverColor = bgColor.brighter();
            this.pressedColor = bgColor.darker();
            setBackground(originalColor);
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2d = (Graphics2D) g.create();
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2d.setColor(getBackground());
            g2d.fillRoundRect(0, 0, getWidth(), getHeight(), cornerRadius, cornerRadius);
            
            // Add subtle shadow
            g2d.setColor(new Color(0, 0, 0, 30));
            g2d.fillRoundRect(0, 3, getWidth(), getHeight(), cornerRadius, cornerRadius);
            
            g2d.setColor(getBackground());
            g2d.fillRoundRect(0, 0, getWidth(), getHeight(), cornerRadius, cornerRadius);
            
            FontMetrics fm = g2d.getFontMetrics();
            int x = (getWidth() - fm.stringWidth(getText())) / 2;
            int y = (getHeight() - fm.getHeight()) / 2 + fm.getAscent();
            g2d.setColor(getForeground());
            g2d.drawString(getText(), x, y);
            g2d.dispose();
        }
    }

    // ==================== MODERN TEXT FIELD ====================
    public static class ModernTextField extends JTextField {
        private int cornerRadius = 10;
        private Color borderColor = new Color(200, 200, 200);
        private Color focusColor = SOFT_GOLD;
        private String placeholder = "";

        public ModernTextField() {
            setOpaque(false);
            setFont(NORMAL_FONT);
            setForeground(TEXT_COLOR);
            setBackground(INPUT_BG);
            setCaretColor(SOFT_GOLD);
            setBorder(BorderFactory.createCompoundBorder(
                    new RoundedBorder(cornerRadius, borderColor, 1),
                    BorderFactory.createEmptyBorder(12, 15, 12, 15)));

            addFocusListener(new java.awt.event.FocusAdapter() {
                @Override
                public void focusGained(java.awt.event.FocusEvent evt) {
                    setBorder(BorderFactory.createCompoundBorder(
                            new RoundedBorder(cornerRadius, focusColor, 2),
                            BorderFactory.createEmptyBorder(11, 14, 11, 14)));
                }
                @Override
                public void focusLost(java.awt.event.FocusEvent evt) {
                    setBorder(BorderFactory.createCompoundBorder(
                            new RoundedBorder(cornerRadius, borderColor, 1),
                            BorderFactory.createEmptyBorder(12, 15, 12, 15)));
                }
            });
        }
        
        public ModernTextField(int columns) {
            this();
            setColumns(columns);
        }

        public void setPlaceholder(String text) {
            this.placeholder = text;
            repaint();
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2d = (Graphics2D) g.create();
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2d.setColor(getBackground());
            g2d.fillRoundRect(0, 0, getWidth(), getHeight(), cornerRadius, cornerRadius);
            super.paintComponent(g);
            
            if (getText().isEmpty() && !placeholder.isEmpty()) {
                g2d.setColor(GRAY_COLOR);
                g2d.setFont(getFont().deriveFont(Font.ITALIC));
                FontMetrics fm = g2d.getFontMetrics();
                int x = getInsets().left;
                int y = (getHeight() - fm.getHeight()) / 2 + fm.getAscent();
                g2d.drawString(placeholder, x, y);
            }
            g2d.dispose();
        }
    }

    // ==================== MODERN PASSWORD FIELD ====================
    public static class ModernPasswordField extends JPasswordField {
        private int cornerRadius = 10;
        private Color borderColor = new Color(200, 200, 200);
        private Color focusColor = SOFT_GOLD;

        public ModernPasswordField() {
            setOpaque(false);
            setFont(NORMAL_FONT);
            setForeground(TEXT_COLOR);
            setBackground(INPUT_BG);
            setCaretColor(SOFT_GOLD);
            setEchoChar('•');
            setBorder(BorderFactory.createCompoundBorder(
                    new RoundedBorder(cornerRadius, borderColor, 1),
                    BorderFactory.createEmptyBorder(12, 15, 12, 15)));

            addFocusListener(new java.awt.event.FocusAdapter() {
                @Override
                public void focusGained(java.awt.event.FocusEvent evt) {
                    setBorder(BorderFactory.createCompoundBorder(
                            new RoundedBorder(cornerRadius, focusColor, 2),
                            BorderFactory.createEmptyBorder(11, 14, 11, 14)));
                }
                @Override
                public void focusLost(java.awt.event.FocusEvent evt) {
                    setBorder(BorderFactory.createCompoundBorder(
                            new RoundedBorder(cornerRadius, borderColor, 1),
                            BorderFactory.createEmptyBorder(12, 15, 12, 15)));
                }
            });
        }
        
        public ModernPasswordField(int columns) {
            this();
            setColumns(columns);
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2d = (Graphics2D) g.create();
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2d.setColor(getBackground());
            g2d.fillRoundRect(0, 0, getWidth(), getHeight(), cornerRadius, cornerRadius);
            super.paintComponent(g);
            g2d.dispose();
        }
    }

    // ==================== OUTLINED BUTTON ====================
    public static class OutlinedButton extends JButton {
        private int cornerRadius = 25;
        private Color borderColor;

        public OutlinedButton(String text, Color color) {
            super(text);
            setBorderPainted(false);
            setFocusPainted(false);
            setContentAreaFilled(false);
            setFont(BUTTON_FONT);
            setForeground(color);
            setCursor(new Cursor(Cursor.HAND_CURSOR));
            setMargin(new Insets(10, 25, 10, 25));
            this.borderColor = color;

            addMouseListener(new java.awt.event.MouseAdapter() {
                @Override
                public void mouseEntered(java.awt.event.MouseEvent evt) {
                    setForeground(color.brighter());
                }
                @Override
                public void mouseExited(java.awt.event.MouseEvent evt) {
                    setForeground(color);
                }
            });
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2d = (Graphics2D) g.create();
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2d.setColor(borderColor);
            g2d.drawRoundRect(1, 1, getWidth() - 2, getHeight() - 2, cornerRadius, cornerRadius);
            FontMetrics fm = g2d.getFontMetrics();
            int x = (getWidth() - fm.stringWidth(getText())) / 2;
            int y = (getHeight() - fm.getHeight()) / 2 + fm.getAscent();
            g2d.setColor(getForeground());
            g2d.drawString(getText(), x, y);
            g2d.dispose();
        }
    }

    // ==================== ROUNDED BORDER ====================
    static class RoundedBorder implements Border {
        private int radius;
        private Color color;
        private int thickness;

        public RoundedBorder(int radius, Color color, int thickness) {
            this.radius = radius;
            this.color = color;
            this.thickness = thickness;
        }

        @Override
        public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
            Graphics2D g2d = (Graphics2D) g.create();
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2d.setColor(color);
            g2d.setStroke(new BasicStroke(thickness));
            g2d.drawRoundRect(x + thickness/2, y + thickness/2,
                    width - thickness, height - thickness, radius, radius);
            g2d.dispose();
        }

        @Override
        public Insets getBorderInsets(Component c) {
            return new Insets(thickness + 5, thickness + 10, thickness + 5, thickness + 10);
        }

        @Override
        public boolean isBorderOpaque() {
            return false;
        }
    }

    // ==================== GRADIENT PANEL ====================
    public static class GradientPanel extends JPanel {
        private Color startColor;
        private Color endColor;

        public GradientPanel(Color startColor, Color endColor) {
            this.startColor = startColor;
            this.endColor = endColor;
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2d = (Graphics2D) g.create();
            g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
            GradientPaint gradient = new GradientPaint(0, 0, startColor, getWidth(), 0, endColor);
            g2d.setPaint(gradient);
            g2d.fillRect(0, 0, getWidth(), getHeight());
            g2d.dispose();
        }
    }

    // ==================== STATS CARD ====================
    public static class StatsCard extends ShadowPanel {
        private JLabel valueLabel;
        private JLabel titleLabel;
        private JLabel iconLabel;

        public StatsCard(String icon, String title, Color iconColor) {
            super(12, 3);
            setBackground(Color.WHITE);
            setLayout(new BorderLayout(10, 5));
            setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));

            iconLabel = new JLabel(icon);
            iconLabel.setFont(getEmojiFont().deriveFont(32f));
            iconLabel.setForeground(iconColor);

            titleLabel = new JLabel(title);
            titleLabel.setFont(SMALL_FONT);
            titleLabel.setForeground(GRAY_COLOR);

            valueLabel = new JLabel("0");
            valueLabel.setFont(STATS_FONT);
            valueLabel.setForeground(TEXT_COLOR);

            JPanel textPanel = new JPanel(new GridLayout(2, 1, 0, 2));
            textPanel.setOpaque(false);
            textPanel.add(titleLabel);
            textPanel.add(valueLabel);

            add(iconLabel, BorderLayout.WEST);
            add(textPanel, BorderLayout.CENTER);
        }

        public void setValue(String value) { valueLabel.setText(value); }
        public void setValue(double value) { valueLabel.setText(String.format("₹%,.2f", value)); }
        public void setValue(int value) { valueLabel.setText(String.valueOf(value)); }
    }

    // ==================== STYLED TABLE ====================
    public static void styleTable(JTable table) {
        table.setFont(NORMAL_FONT);
        table.setRowHeight(50);
        table.setShowGrid(false);
        table.setIntercellSpacing(new Dimension(0, 0));
        table.setSelectionBackground(new Color(212, 175, 55, 50));
        table.setSelectionForeground(TEXT_COLOR);
        table.setBackground(Color.WHITE);
        table.setForeground(TEXT_COLOR);

        JTableHeader header = table.getTableHeader();
        header.setFont(HEADER_FONT);
        header.setBackground(HEADER_BACKGROUND);
        header.setForeground(HEADER_TEXT_COLOR);
        header.setPreferredSize(new Dimension(0, 50));
        header.setReorderingAllowed(false);
        header.setOpaque(true);
        
        header.setDefaultRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                JLabel label = new JLabel(value != null ? value.toString() : "");
                label.setFont(HEADER_FONT);
                label.setBackground(HEADER_BACKGROUND);
                label.setForeground(HEADER_TEXT_COLOR);
                label.setOpaque(true);
                label.setHorizontalAlignment(SwingConstants.CENTER);
                label.setBorder(BorderFactory.createEmptyBorder(12, 15, 12, 15));
                return label;
            }
        });

        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                JLabel label = new JLabel();
                label.setFont(NORMAL_FONT);
                label.setForeground(TEXT_COLOR);
                label.setOpaque(true);
                
                if (value != null) {
                    label.setText(value.toString());
                } else {
                    label.setText("");
                }
                
                if (isSelected) {
                    label.setBackground(new Color(212, 175, 55, 50));
                    label.setForeground(TEXT_COLOR);
                } else {
                    label.setBackground(row % 2 == 0 ? TABLE_ROW_EVEN : TABLE_ROW_ODD);
                    label.setForeground(TEXT_COLOR);
                }
                
                label.setBorder(new EmptyBorder(0, 15, 0, 15));
                label.setHorizontalAlignment(column == 0 ? SwingConstants.CENTER : SwingConstants.LEFT);
                
                return label;
            }
        });
    }

    // ==================== SCROLL PANE STYLING ====================
    public static void styleScrollPane(JScrollPane scrollPane) {
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getViewport().setBackground(Color.WHITE);
        scrollPane.setBackground(Color.WHITE);

        JScrollBar verticalBar = scrollPane.getVerticalScrollBar();
        verticalBar.setBackground(LIGHT_COLOR);
        verticalBar.setPreferredSize(new Dimension(8, 0));
        verticalBar.setUnitIncrement(16);

        JScrollBar horizontalBar = scrollPane.getHorizontalScrollBar();
        horizontalBar.setBackground(LIGHT_COLOR);
        horizontalBar.setPreferredSize(new Dimension(0, 8));
        horizontalBar.setUnitIncrement(16);
    }
    
    // ==================== COMBO BOX STYLING ====================
    public static void styleComboBox(JComboBox<?> comboBox) {
        comboBox.setFont(NORMAL_FONT);
        comboBox.setBackground(Color.WHITE);
        comboBox.setForeground(TEXT_COLOR);
        comboBox.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
            BorderFactory.createEmptyBorder(8, 12, 8, 12)
        ));
    }
    
    public static JLabel createLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(NORMAL_FONT);
        label.setForeground(TEXT_COLOR);
        return label;
    }
    
    public static JLabel createHeaderLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(HEADER_FONT);
        label.setForeground(TEXT_COLOR);
        return label;
    }
}