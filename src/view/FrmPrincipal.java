package view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import model.Globo;
import model.Techo;

public class FrmPrincipal extends JFrame {

	private static final long serialVersionUID = 1L;

	private ArrayList<Globo> globos;
	private Techo techo;
	private boolean carreraIniciada = false;

	private JButton btnIniciar;
	private JButton btnReiniciar;
	private boolean podioMostrado = false;

	private int orden = 4;

	private GamePanel panelJuego; // Panel para la animación

	// En FrmPrincipal
	private int fps = 0;
	private long lastTime = System.currentTimeMillis();

	private Map<Integer, Globo> ordenLlegada = new LinkedHashMap<>(); // Para almacenar el orden de llegada

	private Clip clip; // Atributo para la música

	public FrmPrincipal() {
		super("Carrera de Globos");

		 try {
	            // Cambiar el Look and Feel a Nimbus
	            UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");
	        } catch (Exception e) {
	            e.printStackTrace();
	        }

		setSize(695, 760);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setLocationRelativeTo(null);
		setResizable(false);
		getContentPane().setLayout(new BorderLayout());

		// Inicializar globos con velocidades únicas
		globos = new ArrayList<>();

		double[] velocidades = { 2.4, 2.2, 1.7, 1.4 }; // Rango de velocidades
		List<Double> listaVelocidades = new ArrayList<>();
		for (double velocidad : velocidades) {
			listaVelocidades.add(velocidad); // Agregar cada elemento al List<Double>
		}
		Collections.shuffle(listaVelocidades); // Mezclar las velocidades
		for (int i = 0; i < 4; i++) {
			Globo globo = new Globo(80 + i * 150, 575, this);
			globo.setVelocidad(listaVelocidades.get(i)); // Asignar velocidad única
			globos.add(globo);
		}

		// Inicializar techo
		techo = new Techo(0, 0);

		// Crear el panel de juego
		panelJuego = new GamePanel();
		getContentPane().add(panelJuego, BorderLayout.CENTER);

		// Panel de controles
		JPanel panelControles = new JPanel();
		panelControles.setBackground(new Color(0, 0, 0));
		panelControles.setLayout(new FlowLayout());

		// Botón para iniciar la carrera
		btnIniciar = new JButton("Iniciar Carrera");
		btnIniciar.setForeground(new Color(0, 0, 0));
        btnIniciar.setBackground(new Color(255, 69, 0));
		btnIniciar.addActionListener(e -> {
			carreraIniciada = true;
			btnIniciar.setEnabled(false);
			podioMostrado = false;
			playMusic(); // Inicia la música al comenzar la carrera
		});

		panelControles.add(btnIniciar);

		// Botón para reiniciar la carrera
		btnReiniciar = new JButton("Reiniciar Carrera");
		btnReiniciar.addActionListener(e -> reiniciarCarrera());
		btnReiniciar.setVisible(false);
		panelControles.add(btnReiniciar);

		getContentPane().add(panelControles, BorderLayout.SOUTH); // Agregar el panel de controles abajo

		setVisible(true);

		// Iniciar el ciclo de animación en un hilo
		// Modificación en el bucle de repintado
		new Thread(() -> {
			while (true) {
				long now = System.currentTimeMillis();
				fps = (int) (1000 / (now - lastTime)); // Calcula FPS
				lastTime = now;

				panelJuego.repaint();

				try {
					Thread.sleep(16); // Aproximadamente 60 FPS (1000ms / 60 ≈ 16ms)
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}).start();
	}

	// Método para reproducir la música de fondo
	private void playMusic() {
		try {
			// Carga el archivo de audio desde los recursos
			AudioInputStream audioInputStream = AudioSystem
					.getAudioInputStream(getClass().getResource("/imagen/musica.wav"));
			clip = AudioSystem.getClip();
			clip.open(audioInputStream);
			// Si deseas que la música se repita continuamente:
			clip.loop(Clip.LOOP_CONTINUOUSLY);
			// Si solo quieres reproducirla una vez, usa:
			// clip.start();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// Opcional: método para detener la música (por ejemplo, al reiniciar la
	// carrera)
	private void stopMusic() {
		if (clip != null && clip.isRunning()) {
			clip.stop();
			clip.close();
		}
	}

	private void reiniciarCarrera() {
	    // Detener la música si está sonando
	    stopMusic();
	    
	    carreraIniciada = false;
	    podioMostrado = false;
	    btnIniciar.setEnabled(true);
	    btnReiniciar.setVisible(false);

	    // Resetear el orden de llegada
	    ordenLlegada.clear();
	    orden = 4;

	    // Resetear globos a su posición inicial y velocidad
	    double[] velocidades = { 2.4, 2.2, 1.7, 1.4 };
	    List<Double> listaVelocidades = new ArrayList<>();
	    for (double velocidad : velocidades) {
	        listaVelocidades.add(velocidad);
	    }
	    Collections.shuffle(listaVelocidades);

	    for (int i = 0; i < globos.size(); i++) {
	        globos.get(i).resetear(80 + i * 150, 575, listaVelocidades.get(i));
	    }

	    panelJuego.repaint();

	    JOptionPane.showMessageDialog(this, "¡Carrera reiniciada!");
	}

	private void mostrarPodio() {
	    SwingUtilities.invokeLater(() -> {
	        // Crear el panel del podio
	        PodioPanel podioPanel = new PodioPanel(ordenLlegada);

	        // Mostrar el podio en un cuadro de diálogo
	        JDialog dialog = new JDialog(this, "¡Carrera Finalizada!", true);
	        dialog.getContentPane().add(podioPanel);
	        dialog.pack();
	        dialog.setSize(700, 400);
	        dialog.setLocationRelativeTo(this);
	        dialog.setVisible(true);

	        // Mostrar el botón para reiniciar
	        btnReiniciar.setVisible(true);
	    });
	}
	public boolean isCarreraIniciada() {
		return carreraIniciada;
	}

	private class GamePanel extends JPanel {
		private static final long serialVersionUID = 1L;

		private BufferedImage backgroundImage;

		public GamePanel() {
			setPreferredSize(new Dimension(695, 600));

			try {
				backgroundImage = javax.imageio.ImageIO.read(getClass().getResource("/imagen/fondo.jpg"));
			} catch (Exception e) {
				e.printStackTrace();
			}

			addMouseListener(new java.awt.event.MouseAdapter() {
				@Override
				public void mouseClicked(java.awt.event.MouseEvent e) {
					// No procesar clics si la carrera no ha iniciado
					if (!carreraIniciada) {
						return;
					}

					int mouseX = e.getX();
					int mouseY = e.getY();

					for (Globo globo : globos) {
						Rectangle bounds = globo.getBounds();
						if (bounds.contains(mouseX, mouseY)) {
							
							// Si el globo ya explotó, se ignora el clic
			                if (globo.isExplotado()) {
			                    continue;
			                }
			                
							// Si el globo ya está frenado, se ignora el clic
							if (globo.isFrenado()) {
								continue;
							}

							// Se captura la velocidad actual en una variable temporal
							double velocidadOriginalTemporal = globo.getVelocidad();

							// Activar el modo frenado (cambia la imagen internamente)
							globo.setFrenado(true);

							// Calcular y asignar la nueva velocidad reducida
							double nuevaVelocidad = Math.max(velocidadOriginalTemporal - 2, 0.5);
							globo.setVelocidad(nuevaVelocidad);

							// Se lanza un hilo para restaurar la velocidad e imagen original después de 500
							// ms
							new Thread(() -> {
								try {
									Thread.sleep(500);
									globo.setFrenado(false);
									globo.setVelocidad(velocidadOriginalTemporal);
								} catch (InterruptedException ex) {
									ex.printStackTrace();
								}
							}).start();
						}
					}
				}
			});

		}

		@Override
		protected void paintComponent(Graphics g) {
			super.paintComponent(g);

			// Dibujar la imagen de fondo
			if (backgroundImage != null) {
				g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
			}

			// Dibujar el techo
			techo.dibujar(g);

			// Dibujar los globos
			for (Globo globo : globos) {
				globo.dibujar(g);
			}

			// Mostrar FPS
			g.setColor(Color.WHITE);
			g.drawString("FPS: " + fps, 325, 680);

			if (carreraIniciada) {
				for (Globo globo : globos) {
					// Si un globo llega al techo, se añade su orden al mapa y se explota (si no
					// está ya dentro del mismo)
					if (globo.getY() <= techo.getY() + 60 && !ordenLlegada.containsValue(globo)) {
						globo.explotar();

						ordenLlegada.put(orden--, globo);
					}
				}
				if (ordenLlegada.size() == globos.size() && !podioMostrado) {
					podioMostrado = true;
					mostrarPodio();
				}
			}

		}

	}

}
