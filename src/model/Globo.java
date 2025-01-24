package model;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import view.FrmPrincipal;

public class Globo extends Thread {

    private static int contador = 0;
    private int id;
    private int x, y;
    private int velocidad;
    private boolean explotado;
    private long tiempoInicio;
    private int tiempo;
    private double angulo = 0; // Ángulo para el balanceo
    private BufferedImage sprite; // Sprite del globo
    private FrmPrincipal principal;

    public Globo(int x, int y, FrmPrincipal principal) {
        this.id = ++contador;
        this.x = x;
        this.y = y;
        this.velocidad = (int) (Math.random() * 5 + 2);
        this.explotado = false;
        this.tiempoInicio = System.currentTimeMillis();
        this.principal = principal;

        // Cargar sprite
        try {
            sprite = ImageIO.read(new File("assets/globo.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        start();
    }

    public void dibujar(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        AffineTransform transform = new AffineTransform();
        transform.translate(x, y);
        transform.rotate(angulo, sprite.getWidth() / 2, sprite.getHeight() / 2);
        g2d.drawImage(sprite, transform, null);
    }

    @Override
    public void run() {
        while (true) {
            if (principal.isCarreraIniciada()&& !explotado) {
                y -= velocidad;
                angulo += Math.random() * 0.2 - 0.1;
            }

            try {
                Thread.sleep(1000 / 60);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void explotar() {
        explotado = true;
        tiempo = (int) (System.currentTimeMillis() - tiempoInicio);

        // Cambiar sprite a explosión
        try {
            sprite = ImageIO.read(new File("assets/globo_explosion.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public int getY() {
        return y;
    }


    public boolean isExplotado() {
        return explotado;
    }

    public long getId() {
        return id;
    }

    public int getTiempo() {
        return tiempo;
    }

    public Rectangle getBounds() {
        return new Rectangle(x, y, sprite.getWidth(), sprite.getHeight());
    }

    public void afectarConViento() {
        velocidad = Math.max(1, velocidad - 1); // Reducir velocidad
    }
}
