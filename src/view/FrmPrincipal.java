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
import java.awt.Toolkit;

public class FrmPrincipal extends JFrame {
    private static final long serialVersionUID = 1L;

    // Lista de globos participantes
    private ArrayList<Globo> globos;

    // Objeto Techo que representa la línea de llegada
    private Techo techo;

    // Variable para controlar si la carrera ha comenzado
    private boolean carreraIniciada = false;

    // Botones para iniciar y reiniciar la carrera
    private JButton btnIniciar;
    private JButton btnReiniciar;

    // Variable para saber si el podio ya fue mostrado
    private boolean podioMostrado = false;

    // Variable para llevar el orden de llegada de los globos
    private int orden = 4;

    // Panel principal donde se dibuja la animación
    private GamePanel panelJuego;

    // Variables para calcular los FPS
    private int fps = 0;
    private long lastTime = System.currentTimeMillis();

    // Mapa para almacenar el orden de llegada de los globos
    private Map<Integer, Globo> ordenLlegada = new LinkedHashMap<>();

    // Atributo para manejar la reproducción de música
    private Clip clip;

    public FrmPrincipal() {
        super("HellRace"); // Título de la ventana

        // Establecer el ícono de la ventana
        setIconImage(Toolkit.getDefaultToolkit().getImage(FrmPrincipal.class.getResource("/imagen/corazon_rosa.png")));

        try {
            // Cambiar el "Look and Feel" a Nimbus para darle un estilo moderno
            UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");
        } catch (Exception e) {
            e.printStackTrace(); // Capturar errores al cambiar el Look and Feel
        }

        setSize(695, 760); // Dimensiones de la ventana
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // Cerrar la aplicación al cerrar la ventana
        setLocationRelativeTo(null); // Centrar la ventana en la pantalla
        setResizable(false); // Hacer la ventana no redimensionable
        getContentPane().setLayout(new BorderLayout()); // Layout principal

        // Inicializar los globos con velocidades únicas
        globos = new ArrayList<>();
        double[] velocidades = {2.4, 2.2, 1.7, 1.4}; // Rango de velocidades
        List<Double> listaVelocidades = new ArrayList<>();
        for (double velocidad : velocidades) {
            listaVelocidades.add(velocidad); // Agregar cada velocidad a una lista
        }
        Collections.shuffle(listaVelocidades); // Mezclar las velocidades aleatoriamente
        for (int i = 0; i < 4; i++) {
            Globo globo = new Globo(80 + i * 150, 575, this); // Crear globos con posiciones iniciales
            globo.setVelocidad(listaVelocidades.get(i)); // Asignar velocidades únicas
            globos.add(globo);
        }

        // Inicializar el techo
        techo = new Techo(0, 0);

        // Crear el panel de juego
        panelJuego = new GamePanel();
        getContentPane().add(panelJuego, BorderLayout.CENTER); // Agregar el panel al centro

        // Crear el panel de controles
        JPanel panelControles = new JPanel();
        panelControles.setBackground(new Color(0, 0, 0)); // Fondo negro
        panelControles.setLayout(new FlowLayout());

        // Botón para iniciar la carrera
        btnIniciar = new JButton("Iniciar Carrera");
        btnIniciar.setForeground(new Color(0, 0, 0)); // Texto negro
        btnIniciar.setBackground(new Color(255, 69, 0)); // Fondo rojo
        btnIniciar.addActionListener(e -> {
            carreraIniciada = true; // Activar la carrera
            podioMostrado = false; // Reiniciar el estado del podio
            playMusic(); // Reproducir música al iniciar la carrera
        });
        panelControles.add(btnIniciar);

        // Botón para reiniciar la carrera
        btnReiniciar = new JButton("Reiniciar Carrera");
        btnReiniciar.setForeground(new Color(0, 0, 0)); // Texto negro
        btnReiniciar.setBackground(new Color(255, 69, 0)); // Fondo rojo
        btnReiniciar.addActionListener(e -> reiniciarCarrera());
        btnReiniciar.setVisible(false); // Ocultar inicialmente
        panelControles.add(btnReiniciar);

        getContentPane().add(panelControles, BorderLayout.SOUTH); // Agregar el panel de controles abajo

        setVisible(true); // Mostrar la ventana

        // Iniciar el ciclo de animación en un hilo separado
        new Thread(() -> {
            while (true) {
                long now = System.currentTimeMillis();
                fps = (int) (1000 / (now - lastTime)); // Calcular los FPS
                lastTime = now;
                panelJuego.repaint(); // Actualizar el panel de juego
                try {
                    Thread.sleep(16); // Dormir el hilo para mantener aproximadamente 60 FPS
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    // Método para reproducir la música de fondo
    private void playMusic() {
        try {
            // Cargar el archivo de audio desde los recursos
            AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(getClass().getResource("/imagen/musica.wav"));
            clip = AudioSystem.getClip();
            clip.open(audioInputStream);
            clip.loop(Clip.LOOP_CONTINUOUSLY); // Reproducir continuamente
        } catch (Exception e) {
            e.printStackTrace(); // Capturar errores al cargar o reproducir la música
        }
    }

    // Método para detener la música de fondo
    private void stopMusic() {
        if (clip != null && clip.isRunning()) { // Verificar si la música está reproduciéndose
            clip.stop(); // Detener la música
            clip.close(); // Cerrar el recurso
        }
    }

    // Método para reiniciar la carrera
    private void reiniciarCarrera() {
        stopMusic(); // Detener la música si está sonando
        carreraIniciada = false; // Reiniciar el estado de la carrera
        podioMostrado = false; // Reiniciar el estado del podio
        btnIniciar.setText("Iniciar Carrera"); // Restaurar el texto del botón
        btnReiniciar.setVisible(false); // Ocultar el botón de reinicio

        // Limpiar el orden de llegada
        ordenLlegada.clear();
        orden = 4;

        // Reiniciar las posiciones y velocidades de los globos
        double[] velocidades = {2.4, 2.2, 1.7, 1.4};
        List<Double> listaVelocidades = new ArrayList<>();
        for (double velocidad : velocidades) {
            listaVelocidades.add(velocidad);
        }
        Collections.shuffle(listaVelocidades); // Mezclar las velocidades nuevamente
        for (int i = 0; i < globos.size(); i++) {
            globos.get(i).resetear(80 + i * 150, 575, listaVelocidades.get(i));
        }

        panelJuego.repaint(); // Redibujar el panel de juego
        JOptionPane.showMessageDialog(this, "¡Carrera reiniciada!"); // Mostrar mensaje de confirmación
    }

    // Método para mostrar el podio al finalizar la carrera
    private void mostrarPodio() {
        btnIniciar.setText("Ver Resultados"); // Cambiar el texto del botón
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

    // Método para verificar si la carrera ha comenzado
    public boolean isCarreraIniciada() {
        return carreraIniciada;
    }

    // Clase interna para el panel de juego
    private class GamePanel extends JPanel {
        private static final long serialVersionUID = 1L;

        // Imagen de fondo del panel
        private BufferedImage backgroundImage;

        public GamePanel() {
            setPreferredSize(new Dimension(695, 600)); // Dimensiones del panel

            // Cargar la imagen de fondo
            try {
                backgroundImage = javax.imageio.ImageIO.read(getClass().getResource("/imagen/fondo.jpg"));
            } catch (Exception e) {
                e.printStackTrace(); // Capturar errores al cargar la imagen
            }

            // Manejador de eventos del ratón
            addMouseListener(new java.awt.event.MouseAdapter() {
                @Override
                public void mouseClicked(java.awt.event.MouseEvent e) {
                    if (!carreraIniciada) { // Ignorar clics si la carrera no ha comenzado
                        return;
                    }

                    int mouseX = e.getX(); // Coordenada X del clic
                    int mouseY = e.getY(); // Coordenada Y del clic

                    for (Globo globo : globos) {
                        Rectangle bounds = globo.getBounds(); // Obtener los límites del globo
                        if (bounds.contains(mouseX, mouseY)) { // Verificar si el clic está dentro del globo
                            if (globo.isExplotado()) { // Ignorar si el globo ya explotó
                                continue;
                            }
                            if (globo.isFrenado()) { // Ignorar si el globo ya está frenado
                                continue;
                            }

                            // Guardar la velocidad original temporalmente
                            double velocidadOriginalTemporal = globo.getVelocidad();

                            // Activar el modo frenado y reducir la velocidad
                            globo.setFrenado(true);
                            double nuevaVelocidad = Math.max(velocidadOriginalTemporal - 2, 0.5);
                            globo.setVelocidad(nuevaVelocidad);

                            // Restaurar la velocidad después de 500 ms usando un hilo
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

            // Mostrar los FPS
            g.setColor(Color.WHITE);
            g.drawString("FPS: " + fps, 325, 680);

            // Verificar si algún globo llegó al techo
            if (carreraIniciada) {
                for (Globo globo : globos) {
                    if (globo.getY() <= techo.getY() + 60 && !ordenLlegada.containsValue(globo)) {
                        globo.explotar(); // Explotar el globo
                        ordenLlegada.put(orden--, globo); // Agregar al mapa de orden de llegada
                    }
                }

                // Si todos los globos han llegado, mostrar el podio
                if (ordenLlegada.size() == globos.size() && !podioMostrado) {
                    podioMostrado = true;
                    mostrarPodio();
                }
            }
        }
    }
}