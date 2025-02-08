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
	private boolean explotado;
	private long tiempoInicio;
	private int tiempo;
	private double angulo = 0; // Ángulo para el balanceo
	private BufferedImage sprite; // Sprite del globo
	private FrmPrincipal principal;
	private String nombre;

	private double velocidad;
	private double velocidadOriginal; // Guarda la velocidad inicial
	
	private boolean frenado;

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
			switch (id) {
			case 1:
				ruta = "assets/corazon_rosa.png";
				nombre = "Rojo";
				break;
			case 2:
				ruta = "assets/corazon_azul.png";
				nombre = "Azul";

				break;
			case 3:
				ruta = "assets/corazon_amarillo.png";
				nombre = "Amarillo";

				break;
			case 4:
				ruta = "assets/corazon_verde.png";
				nombre = "Verde";

				break;
			}
			sprite = ImageIO.read(new File(ruta));
		} catch (IOException e) {
			e.printStackTrace();
		}

		start();
	}
	
	
	public boolean isFrenado() {
		return frenado;
	}


	public void setFrenado(boolean frenado) {
		this.frenado = frenado;
		
		if(!frenado) {
			String ruta = "";
			switch (id) {
			case 1:
				ruta = "assets/corazon_rosa.png";
				nombre = "Rojo";
				break;
			case 2:
				ruta = "assets/corazon_azul.png";
				nombre = "Azul";

				break;
			case 3:
				ruta = "assets/corazon_amarillo.png";
				nombre = "Amarillo";

				break;
			case 4:
				ruta = "assets/corazon_verde.png";
				nombre = "Verde";

				break;
			}
			try {
				sprite = ImageIO.read(new File(ruta));
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else {
			String ruta = "";
			switch (id) {
			case 1:
				ruta = "assets/corazon_rosa_frenado.png";
				nombre = "Rojo";
				break;
			case 2:
				ruta = "assets/corazon_azul_frenado.png";
				nombre = "Azul";

				break;
			case 3:
				ruta = "assets/corazon_amarillo_frenado.png";
				nombre = "Amarillo";

				break;
			case 4:
				ruta = "assets/corazon_verde_frenado.png";
				nombre = "Verde";

				break;
			}
			try {
				sprite = ImageIO.read(new File(ruta));
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	public void dibujar(Graphics g) {
		
		
		
		Graphics2D g2d = (Graphics2D) g;
		double scaleFactor = 0.25; // Factor de escala para reducir el tamaño al 50%
		int spriteWidth = sprite.getWidth();
		int spriteHeight = sprite.getHeight();

		// Guardamos la transformación original para no afectar al resto del dibujo
		AffineTransform oldTransform = g2d.getTransform();

		// Creamos una nueva transformación
		AffineTransform transform = new AffineTransform();
		// Primero trasladamos la imagen a la posición (x, y)
		transform.translate(x, y);
		// Aplicamos la escala. Esto reducirá el tamaño del sprite.
		transform.scale(scaleFactor, scaleFactor);
		// Aplicamos la rotación alrededor del centro de la imagen original
		transform.rotate(angulo, spriteWidth / 2.0, spriteHeight / 2.0);

		// Dibujamos la imagen con la transformación aplicada
		g2d.drawImage(sprite, transform, null);

		// Restauramos la transformación original
		g2d.setTransform(oldTransform);
	}

	private static final double ANGULO_MAX = Math.toRadians(22.5); // 45 grados en radianes
	private static final double BALANCEO_MAX = 0.05; // Rango máximo del cambio de balanceo
	private static final double BALANCEO_MIN = -0.05; // Rango mínimo del cambio de balanceo

	@Override
	public void run() {
		while (true) {
			if (principal.isCarreraIniciada() && !explotado) {
				// Ajustar el desplazamiento para que sea más perceptible
				y -= velocidad * (1800.0 / 600); // Convertir velocidad a unidades por segundo
				// Agregar variabilidad aleatoria
				y -= Math.random() * 0.5; // Pequeña fluctuación aleatoria

				// Generar un pequeño cambio en el balanceo
				double cambio = Math.random() * (BALANCEO_MAX - BALANCEO_MIN) + BALANCEO_MIN;
				angulo += cambio;

				// Limitar el ángulo a ±45 grados
				angulo = Math.sin(System.currentTimeMillis() / 1000.0) * ANGULO_MAX;
			}

			try {
				Thread.sleep(1800 / 30); // Mantener un frame rate alto
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	public void setVelocidad(double velocidad) {
		this.velocidad = velocidad;
	}
	
	public void resetear(int nuevoX, int nuevoY, double nuevaVelocidad) {
	    this.x = nuevoX;
	    this.y = nuevoY;
	    this.velocidad = nuevaVelocidad;
	    this.explotado = false;
	    this.angulo = 0; 

	    try {
	        String ruta = "";
	        switch (id) {
	            case 1: ruta = "assets/corazon_rosa.png"; break;
	            case 2: ruta = "assets/corazon_azul.png"; break;
	            case 3: ruta = "assets/corazon_naranja.png"; break;
	            case 4: ruta = "assets/corazon_verde.png"; break;
	        }
	        sprite = ImageIO.read(new File(ruta));
	    } catch (IOException e) {
	        e.printStackTrace();
	    }
	}

	public void setVelocidadOriginal(double nuevaVelocidad) {
		if (velocidadOriginal == 0) { // Solo guardamos la velocidad original una vez
			velocidadOriginal = velocidad;
		}
		this.velocidad = nuevaVelocidad;

		// Restaurar la velocidad después de 0.5 segundos
		new Thread(() -> {
			try {
				Thread.sleep(500);
				this.velocidad = velocidadOriginal; // Restaurar velocidad
			} catch (InterruptedException ex) {
				ex.printStackTrace();
			}
		}).start();
	}

	public double getVelocidadOriginal() {
		return velocidadOriginal;
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
	
	public String getNombre() {
		return nombre;
	}

	public Rectangle getBounds() {
	    double scaleFactor = 0.25; // Debe coincidir con el de `dibujar`
	    int ancho = (int) (sprite.getWidth() * scaleFactor);
	    int alto = (int) (sprite.getHeight() * scaleFactor);
	    return new Rectangle(x, y, ancho, alto);
	}

	
	public double getVelocidad() {

		return velocidad;
	}

}
