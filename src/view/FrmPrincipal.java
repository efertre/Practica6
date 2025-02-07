package view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

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

	// En FrmPrincipal
	private int fps = 0;
	private long lastTime = System.currentTimeMillis();
	
	private Map<Integer, Globo> ordenLlegada = new LinkedHashMap<>(); // Para almacenar el orden de llegada
	

	public FrmPrincipal() {
		super("Carrera de Globos");
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
			Globo globo = new Globo(100 + i * 150, 600, this);
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

	private void reiniciarCarrera() {

		carreraIniciada = false;
		podioMostrado = false;

		btnIniciar.setEnabled(true);
		btnReiniciar.setVisible(false);

		JOptionPane.showMessageDialog(this, "¡Carrera reiniciada!");
	}

	

	// Mostrar podio con nombres correctos
	private void mostrarPodio() {
	    SwingUtilities.invokeLater(() -> {
	        StringBuilder podioTexto = new StringBuilder("Podio:\n");

	        
	        // Asignar nombres a los globos según su posición
	        for (int i = 0; i < ordenLlegada.size(); i++) {
	        	
	        	int posicion = i + 1;
	        	
	            String nombreGlobo = ordenLlegada.get(posicion).getNombre();// Usar el nombre correspondiente
	            podioTexto.append(posicion).append(". ").append(nombreGlobo).append("\n");
	        }

	        JOptionPane.showMessageDialog(this, podioTexto.toString());
	        btnReiniciar.setVisible(true);
	    });
	}

	public boolean isCarreraIniciada() {
		return carreraIniciada;
	}

	private class GamePanel extends JPanel {
		private static final long serialVersionUID = 1L;

		public GamePanel() {
			setPreferredSize(new Dimension(695, 600));
			addMouseListener(new java.awt.event.MouseAdapter() {
				@Override
				public void mouseClicked(java.awt.event.MouseEvent e) {
					int mouseX = e.getX();
					int mouseY = e.getY();

					for (Globo globo : globos) {
						Rectangle bounds = globo.getBounds();
						if (bounds.contains(mouseX, mouseY)) {
							// Guardar la velocidad original si no lo hemos hecho antes
							if (globo.getVelocidadOriginal() == 0) {
								globo.setVelocidadOriginal(globo.getVelocidad());
							}

							// Reducir la velocidad
							double nuevaVelocidad = Math.max(globo.getVelocidad() - 0.5, 0.5);
							globo.setVelocidad(nuevaVelocidad);

							// Restaurar la velocidad después de 3 segundos
							new Thread(() -> {
								try {
									Thread.sleep(500); // Esperar 3 segundos
									globo.setVelocidad(globo.getVelocidadOriginal()); // Restaurar velocidad
								} catch (InterruptedException ex) {
									ex.printStackTrace();
								}
							}).start();
						}
					}
				}
			});

		}

		// Modificación en GamePanel -> paintComponent()
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

		    // Mostrar FPS en la pantalla
		    g.setColor(Color.BLACK);
		    g.drawString("FPS: " + fps, 10, 20);

		    // Control de colisiones y fin de carrera
		    if (carreraIniciada) {
		        for (Globo globo : globos) {
		            if (globo.getY() <= techo.getY() + 20 && !ordenLlegada.containsKey(globo)) {
		                globo.explotar();
		                registrarLlegada(globo);
		            }
		        }

		        if (ordenLlegada.size() == globos.size() && !podioMostrado) {
		            podioMostrado = true;
		            mostrarPodio();
		        }
		    }
		}

		private void registrarLlegada(Globo globo) {
		    if (!ordenLlegada.containsValue(globo)) {
		        ordenLlegada.put(ordenLlegada.size() + 1, globo );
		    }
		}
	}

}
