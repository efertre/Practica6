package view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import model.Globo;
import model.Techo;

public class FrmPrincipal extends JFrame {

	private ArrayList<Globo> globos;
	private Techo techo;
	private boolean carreraIniciada = false;

	private JButton btnIniciar;
	private JButton btnReiniciar;
	private boolean podioMostrado = false;

	private GamePanel panelJuego; // Panel para la animación

	public FrmPrincipal() {
		super("Carrera de Globos");
		setSize(695, 760);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setLocationRelativeTo(null);
		setResizable(false);
		getContentPane().setLayout(new BorderLayout());

		// Inicializar globos con velocidades únicas
		globos = new ArrayList<>();

		int[] velocidades = generadorVelocidadAleatoria(); // Rango de velocidades
		Collections.shuffle(Arrays.asList(velocidades)); // Mezclar las velocidades

		for (int i = 0; i < 4; i++) {
			Globo globo = new Globo(50 + i * 150, 500, this);
			globo.setVelocidad(velocidades[i]); // Asignar velocidad única
			globos.add(globo);
		}

		// Inicializar techo
		techo = new Techo(0, 0);

		// Crear el panel de juego
		panelJuego = new GamePanel();
		getContentPane().add(panelJuego, BorderLayout.CENTER);

		// Panel de controles
		JPanel panelControles = new JPanel();
		panelControles.setBackground(new Color(240, 240, 240));
		panelControles.setLayout(new FlowLayout());

		// Botón para iniciar la carrera
		btnIniciar = new JButton("Iniciar Carrera");
		btnIniciar.addActionListener(e -> {
			carreraIniciada = true;
			btnIniciar.setEnabled(false);
			podioMostrado = false;
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
		new Thread(() -> {
			while (true) {
				panelJuego.repaint();
				try {
					Thread.sleep(1000 / 60); // 60 FPS
				} catch (InterruptedException ex) {
					ex.printStackTrace();
				}
			}
		}).start();
	}

	 public int[] generadorVelocidadAleatoria() {
	    // Definir un rango pequeño para las velocidades (por ejemplo, entre 2 y 6)
	    int minVelocidad = 2;
	    int maxVelocidad = 6;

	    // Crear una lista con todos los números posibles en el rango [minVelocidad, maxVelocidad]
	    List<Integer> todasLasVelocidades = new ArrayList<>();
	    for (int i = minVelocidad; i <= maxVelocidad; i++) {
	        todasLasVelocidades.add(i);
	    }

	    // Mezclar la lista para obtener un orden aleatorio
	    Collections.shuffle(todasLasVelocidades);

	    // Seleccionar las primeras 4 velocidades únicas
	    int[] velocidadesAleatorias = new int[4];
	    for (int i = 0; i < 4; i++) {
	        velocidadesAleatorias[i] = todasLasVelocidades.get(i);
	    }

	    return velocidadesAleatorias;
	}

	private void reiniciarCarrera() {
		
		carreraIniciada = false;
		podioMostrado = false;

		btnIniciar.setEnabled(true);
		btnReiniciar.setVisible(false);

		JOptionPane.showMessageDialog(this, "¡Carrera reiniciada!");
	}

	private boolean todosExplotados() {
		for (Globo globo : globos) {
			if (!globo.isExplotado()) {
				return false;
			}
		}
		return true;
	}


	private void mostrarPodioEnHilo() {
		SwingUtilities.invokeLater(() -> {
			Collections.sort(globos, Comparator.comparingInt(Globo::getTiempo));
			JOptionPane.showMessageDialog(this, "Podio:\n" + "1. Globo " + globos.get(3).getId() + "\n" + "2. Globo "
					+ globos.get(2).getId() + "\n" + "3. Globo " + globos.get(1).getId() + "\n");

			btnReiniciar.setVisible(true);
		});
	}

	public boolean isCarreraIniciada() {
		return carreraIniciada;
	}

	// Panel personalizado para el juego
	private class GamePanel extends JPanel {
		public GamePanel() {
			setPreferredSize(new Dimension(695, 600));
		}

		@Override
		protected void paintComponent(Graphics g) {
			super.paintComponent(g);
			setBackground(Color.WHITE);

			// Dibujar techo
			techo.dibujar(g);

			// Dibujar globos
			for (Globo globo : globos) {
				globo.dibujar(g);
			}

			// Control de colisiones y fin de carrera
			if (carreraIniciada) {
				for (Globo globo : globos) {
					if (globo.getY() <= techo.getY() + 100) {
						globo.explotar();
					}
				}
				if (todosExplotados() && !podioMostrado) {
					podioMostrado = true;
					mostrarPodioEnHilo();
				}
			}
		}
	}

	public static void main(String[] args) {
		new FrmPrincipal();
	}
}
