package view;

import model.Globo;
import model.Techo;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class FrmPrincipal extends JFrame {

    private ArrayList<Globo> globos;
    private Techo techo;
    private boolean carreraIniciada = false;
    
    private JButton btnIniciar;
    private long lastFrame = System.nanoTime();
    private int fps;

    public FrmPrincipal() {
        super("Carrera de Globos");
        setSize(650, 760);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        getContentPane().setLayout(null);
        setLocationRelativeTo(null);

        // Inicializar globos
        globos = new ArrayList<>();
        for (int i = 0; i < 4; i++) {
            Globo globo = new Globo(50 + i * 150, 500, this);
            globos.add(globo);
        }

        // Inicializar techo
        techo = new Techo(0, 0);

        // Botón para iniciar la carrera
        btnIniciar = new JButton("Iniciar Carrera");
        btnIniciar.setBounds(300, 20, 150, 30);
        btnIniciar.addActionListener(e -> {
            carreraIniciada = true;
            btnIniciar.setEnabled(false);
            podioMostrado = false; // Reinicia la bandera cuando inicia la carrera
        });
        getContentPane().add(btnIniciar);

        // Listener para simular el viento al hacer clic
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                for (Globo globo : globos) {
                    if (globo.getBounds().contains(e.getPoint())) {
                        globo.afectarConViento();
                    }
                }
            }
        });

        setVisible(true);

        // Iniciar el ciclo de animación
        new Thread(() -> {
            while (true) {
                repaint();
                try {
                    Thread.sleep(1000 / 60); // Control de FPS
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

 // Nueva variable de estado para evitar que se muestre el podio varias veces
    private boolean podioMostrado = false;

    @Override
    public void paint(Graphics g) {
        Image img = createImage(getWidth(), getHeight());
        Graphics offscreen = img.getGraphics();

        // Fondo
        offscreen.setColor(Color.CYAN);
        offscreen.fillRect(0, 0, getWidth(), getHeight());

        // Dibujar techo
        techo.dibujar(offscreen);

        // Dibujar globos
        for (Globo globo : globos) {
            globo.dibujar(offscreen);
        }

        // Mostrar FPS
        long currentFrame = System.nanoTime();
        fps = (int) (1e9 / (currentFrame - lastFrame));
        lastFrame = currentFrame;
        offscreen.setColor(Color.BLACK);
        offscreen.drawString("FPS: " + fps, 10, 50);

        // Control de colisiones y fin de carrera
        if (carreraIniciada) {
            for (Globo globo : globos) {
                if (globo.getY() <= techo.getY() + 10) {
                    globo.explotar();
                }
            }

            // Verificar si todos explotaron fuera de paint()
            if (todosExplotados() && !podioMostrado) {
                podioMostrado = true;
                mostrarPodioEnHilo();
            }
        }

        g.drawImage(img, 0, 0, this);
    }

    // Método para verificar si todos los globos explotaron
    private boolean todosExplotados() {
        for (Globo globo : globos) {
            if (!globo.isExplotado()) {
                return false;
            }
        }
        return true;
    }

    // Mostrar podio en un hilo separado
    private void mostrarPodioEnHilo() {
        SwingUtilities.invokeLater(() -> {
            Collections.sort(globos, Comparator.comparingInt(Globo::getTiempo));
            JOptionPane.showMessageDialog(this,
                    "Podio:\n" +
                            "1. Globo " + globos.get(3).getId() + "\n" +
                            "2. Globo " + globos.get(2).getId() + "\n" +
                            "3. Globo " + globos.get(1).getId() + "\n");
        });
    }


    public static void main(String[] args) {
        new FrmPrincipal();
    }

    public boolean isCarreraIniciada() {
        return carreraIniciada;
    }
}
