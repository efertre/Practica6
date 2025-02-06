package view;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;

import javax.swing.JPanel;

import model.Globo;

class PanelDibujo extends JPanel {
    private FrmPrincipal principal;

    public PanelDibujo(FrmPrincipal principal) {
        this.principal = principal;

    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g); // Importante: llama al método de la superclase
        Graphics2D g2d = (Graphics2D) g;

        // Fondo
        g2d.setColor(Color.WHITE);
        g2d.fillRect(0, 0, getWidth(), getHeight());

        // Dibujar techo
        principal.getTecho().dibujar(g2d);

        // Dibujar globos
        for (Globo globo : principal.getGlobos()) {
            globo.dibujar(g2d);
        }
    }
}