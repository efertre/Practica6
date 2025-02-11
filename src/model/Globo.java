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
	private final int id;
	private int x, y;
	private boolean explotado;

	private double angulo = 0; // Ángulo para el balanceo
	private BufferedImage sprite; // Sprite del globo
	private final FrmPrincipal principal;

	private static final double ANGULO_MAX = Math.toRadians(22.5); // 22.5° en radianes
	
	private static final double ESCALA_SPRITE = 0.25; // Factor de escala para el sprite

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

		// Cargar el sprite inicial (no frenado)
		cargarSprite(false);

		// Cargar imágenes de la explosión
		cargarExplosionImages();

		// Configurar el Timer para la animación de explosión
		timerExplosion = new Timer(100, e -> {
			if (indiceExplosion < imagenesExplosion.length - 1) {
				indiceExplosion++;
				sprite = imagenesExplosion[indiceExplosion];
			} else {
				((Timer) e.getSource()).stop(); // Detener el Timer al finalizar la animación
				sprite = null;
			}
		});

		start(); // Iniciar el hilo del globo
	}

	/**
	 * Carga el sprite según el estado (frenado o no) utilizando el método auxiliar.
	 */
	private void cargarSprite(boolean isFrenado) {
		String ruta = getSpritePath(isFrenado);
		try {
			sprite = ImageIO.read(new File(ruta));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Retorna la ruta del sprite según el id del globo y el estado de frenado.
	 */
	private String getSpritePath(boolean isFrenado) {
		String basePath = null;
		switch (id) {
		case 1:
			basePath = "assets/globo_rojo";
			break;
		case 2:
			basePath = "assets/globo_azul";
			break;
		case 3:
			basePath = "assets/globo_amarillo";
			break;
		case 4:
			basePath = "assets/globo_verde";
			break;
		}
		return basePath + (isFrenado ? "_frenado.png" : ".png");
	}

	/**
	 * Carga las imágenes de la animación de explosión.
	 */
	private void cargarExplosionImages() {
		imagenesExplosion = new BufferedImage[12];
		for (int i = 0; i < 12; i++) {
			try {
				imagenesExplosion[i] = ImageIO.read(new File("assets/explosion/" + (i + 1) + ".png"));
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * Inicia la animación de explosión.
	 */
	public void explotar() {
		explotado = true;
		indiceExplosion = 0;
		sprite = imagenesExplosion[indiceExplosion];
		timerExplosion.start();
	}

	/**
	 * Dibuja el globo aplicando una transformación para posición, escala y
	 * rotación.
	 */
	public void dibujar(Graphics g) {
		Graphics2D g2d = (Graphics2D) g;
		int anchoSprite = sprite.getWidth();
		int altoSprite = sprite.getHeight();
		AffineTransform oldTransform = g2d.getTransform();

		AffineTransform transform = new AffineTransform();
		transform.translate(x, y);
		transform.scale(ESCALA_SPRITE, ESCALA_SPRITE);
		transform.rotate(angulo, anchoSprite / 2.0, altoSprite / 2.0);

		g2d.drawImage(sprite, transform, null);
		g2d.setTransform(oldTransform);
	}

	@Override
	public void run() {
		while (true) {
			if (principal.isCarreraIniciada() && !explotado) {
				// Actualizar posición (subida) y agregar fluctuación aleatoria
				y -= velocidad * 3;

				// Actualizar el balanceo
				angulo = Math.sin(System.currentTimeMillis() / 1000.0) * ANGULO_MAX;
			}
			try {
				Thread.sleep(1800 / 30); // Control de frame rate para el globo
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * Cambia el estado de frenado y recarga el sprite correspondiente.
	 */
	public void setFrenado(boolean frenado) {
		this.frenado = frenado;
		cargarSprite(frenado);
	}

	public boolean isFrenado() {
		return frenado;
	}

	/**
	 * Reinicia la posición, velocidad y estado del globo.
	 */
	public void resetear(int nuevoX, int nuevoY, double nuevaVelocidad) {
		this.x = nuevoX;
		this.y = nuevoY;
		this.velocidad = nuevaVelocidad;
		this.explotado = false;
		this.angulo = 0;
		cargarSprite(false);
	}

	/**
	 * Cambia temporalmente la velocidad y la restaura después de 500ms.
	 */
	public void setVelocidadOriginal(double nuevaVelocidad) {
		if (velocidadOriginal == 0) {
			velocidadOriginal = velocidad;
		}
		this.velocidad = nuevaVelocidad;
		new Thread(() -> {
			try {
				Thread.sleep(500);
				this.velocidad = velocidadOriginal;
			} catch (InterruptedException ex) {
				ex.printStackTrace();
			}
		}).start();
	}

	/**
	 * Retorna los límites del globo en función del sprite y la escala.
	 */
	public Rectangle getBounds() {
		int ancho = (int) (sprite.getWidth() * ESCALA_SPRITE);
		int alto = (int) (sprite.getHeight() * ESCALA_SPRITE);
		return new Rectangle(x, y, ancho, alto);
	}

	public void setVelocidad(double velocidad) {
		this.velocidad = velocidad;
	}

	public double getVelocidadOriginal() {
		return velocidadOriginal;
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

	public double getVelocidad() {
		return velocidad;
	}
}
