package model;

import javax.imageio.ImageIO;
import javax.swing.*;
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

	private double angulo = 0; // Ángulo para el balanceo
	private BufferedImage sprite; // Sprite del globo
	private FrmPrincipal principal;

	private static final double ANGULO_MAX = Math.toRadians(22.5); // 45 grados en radianes
	private static final double BALANCEO_MAX = 0.05; // Rango máximo del cambio de balanceo
	private static final double BALANCEO_MIN = -0.05; // Rango mínimo del cambio de balanceo

	private double velocidad;
	private double velocidadOriginal; // Guarda la velocidad inicial

	private boolean frenado;

	// Animación de explosión
	private BufferedImage[] imagenesExplosion;
	private int indiceExplosion = 0;
	private Timer timerExplosion;

	public Globo(int x, int y, FrmPrincipal principal) {
		this.id = ++contador;
		this.x = x;
		this.y = y;

		this.explotado = false;
		this.principal = principal;

		// Cargar sprite
		try {
			String ruta = "";
			switch (id) {
			case 1:
				ruta = "assets/corazon_rosa.png";
				break;
			case 2:
				ruta = "assets/corazon_azul.png";
				break;
			case 3:
				ruta = "assets/corazon_amarillo.png";
				break;
			case 4:
				ruta = "assets/corazon_verde.png";
				break;
			}
			sprite = ImageIO.read(new File(ruta));
		} catch (IOException e) {
			e.printStackTrace();
		}

		// Cargar imágenes de la explosión
		imagenesExplosion = new BufferedImage[12];
		for (int i = 0; i < 12; i++) {
			try {
				imagenesExplosion[i] = ImageIO.read(new File("assets/explosion/" + (i + 1) + ".png"));
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		// Configurar el Timer para la animación de explosión
		timerExplosion = new Timer(100, e -> {
			if (indiceExplosion < imagenesExplosion.length - 1) {
				indiceExplosion++;
				sprite = imagenesExplosion[indiceExplosion];
			} else {
				((Timer) e.getSource()).stop(); // Detener el Timer al finalizar la animación
			}
		});

		start();
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

	public void setFrenado(boolean frenado) {
		this.frenado = frenado;

		if (!frenado) {
			String ruta = "";
			switch (id) {
			case 1:
				ruta = "assets/corazon_rosa.png";
				break;
			case 2:
				ruta = "assets/corazon_azul.png";

				break;
			case 3:
				ruta = "assets/corazon_amarillo.png";

				break;
			case 4:
				ruta = "assets/corazon_verde.png";

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
				break;
			case 2:
				ruta = "assets/corazon_azul_frenado.png";

				break;
			case 3:
				ruta = "assets/corazon_amarillo_frenado.png";

				break;
			case 4:
				ruta = "assets/corazon_verde_frenado.png";

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

	public boolean isFrenado() {
		return frenado;
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
			case 1:
				ruta = "assets/corazon_rosa.png";
				break;
			case 2:
				ruta = "assets/corazon_azul.png";
				break;
			case 3:
				ruta = "assets/corazon_amarillo.png";
				break;
			case 4:
				ruta = "assets/corazon_verde.png";
				break;
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

	public void setVelocidad(double velocidad) {
		this.velocidad = velocidad;
	}

	public double getVelocidadOriginal() {
		return velocidadOriginal;
	}

	public void explotar() {
		explotado = true;


		// Iniciar la animación de explosión
		indiceExplosion = 0;
		sprite = imagenesExplosion[indiceExplosion];
		timerExplosion.start();
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


	public BufferedImage getSprite() {
		return sprite;
	}

	public Rectangle getBounds() {
		double scaleFactor = 0.25; 
		int ancho = (int) (sprite.getWidth() * scaleFactor);
		int alto = (int) (sprite.getHeight() * scaleFactor);
		return new Rectangle(x, y, ancho, alto);
	}

	public double getVelocidad() {

		return velocidad;
	}
}