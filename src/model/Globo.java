package model;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

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
        
        this.explotado = false;
        this.tiempoInicio = System.currentTimeMillis();
        this.principal = principal;

        // Cargar sprite
        try {
        	String ruta = "";
        	switch(id) {
        	case 1: ruta="assets/corazon_rosa.png";
        	break;
        	case 2: ruta="assets/corazon_azul.png";
        	break;
        	case 3: ruta="assets/corazon_naranja.png";
        	break;
        	case 4: ruta="assets/corazon_verde.png";
        	break;
        	}
            sprite = ImageIO.read(new File(ruta));
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

    private static final double ANGULO_MAX = Math.toRadians(22.5); // 45 grados en radianes
    private static final double ANGULO_MIN = -Math.toRadians(22.5); // -45 grados en radianes
    private static final double BALANCEO_MAX = 0.05; // Rango máximo del cambio de balanceo
    private static final double BALANCEO_MIN = -0.05; // Rango mínimo del cambio de balanceo

    @Override
    public void run() {
        while (true) {
            if (principal.isCarreraIniciada() && !explotado) {
                y -= velocidad;

                // Generar un pequeño cambio en el balanceo
                double cambio = Math.random() * (BALANCEO_MAX - BALANCEO_MIN) + BALANCEO_MIN;
                angulo += cambio;

                // Limitar el ángulo a ±45 grados
                angulo = Math.sin(System.currentTimeMillis() / 1000.0) * ANGULO_MAX;            }

            try {
                Thread.sleep(1000 / 60); // 60 FPS
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    
    
    
	public void setVelocidad(int velocidad) {
		this.velocidad = velocidad;
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
        velocidad = (int) Math.max(1, velocidad - velocidad *0.1); // Reducir velocidad
    }
    
    
    
   
}
