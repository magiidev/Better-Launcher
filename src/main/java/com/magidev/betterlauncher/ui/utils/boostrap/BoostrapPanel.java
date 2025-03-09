package com.magidev.betterlauncher.ui.utils.boostrap;

import javax.swing.*;
import java.awt.*;

public class BoostrapPanel extends JPanel {
    private Image logoIcon;

    public BoostrapPanel() {
        setLayout(null);
        setOpaque(false);

        // Charger l'icône et redimensionner
        ImageIcon icon = new ImageIcon(getClass().getResource("/images/icon.png")); // Remplace par ton chemin
        logoIcon = icon.getImage().getScaledInstance(128, 128, Image.SCALE_SMOOTH);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (logoIcon != null) {
            int centerX = (getWidth() - logoIcon.getWidth(null)) / 2;
            int centerY = (getHeight() - logoIcon.getHeight(null)) / 2;
            g.drawImage(logoIcon, centerX, centerY, this);
        }
    }

    public void launch() {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame();
            frame.setUndecorated(true);
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(128, 128);
            frame.setLocationRelativeTo(null);
            frame.setBackground(new Color(0, 0, 0, 0));

            BoostrapPanel panel = new BoostrapPanel();
            frame.add(panel);

            frame.setVisible(true);

            new Boostrap().start();
        });
    }
}
