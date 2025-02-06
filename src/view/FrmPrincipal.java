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

 // Variable para controlar si se muestra el botón de reinicio
    private JButton btnReiniciar;

    public FrmPrincipal() {
        super("Carrera de Globos");
        setSize(695, 760);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout()); // Usar BorderLayout para organizar componentes

        // Crear panel personalizado
        PanelDibujo panelDibujo = new PanelDibujo(this);
        add(panelDibujo, BorderLayout.CENTER); // Agregar el panel al centro

        // Botón "Iniciar Carrera"
        btnIniciar = new JButton("Iniciar Carrera");
        btnIniciar.setBounds(300, 20, 150, 30);
        btnIniciar.addActionListener(e -> {
            carreraIniciada = true;
            btnIniciar.setEnabled(false); // Deshabilitar después de iniciar
        });
        getContentPane().add(btnIniciar);

        // Botón "Reiniciar Carrera"
        btnReiniciar = new JButton("Reiniciar Carrera");
        btnReiniciar.setBounds(300, 60, 150, 30);
        btnReiniciar.addActionListener(e -> reiniciarCarrera());
        btnReiniciar.setVisible(false); // Oculto al inicio
        getContentPane().add(btnReiniciar);

        // Inicializar globos y techo
        globos = new ArrayList<>();
        for (int i = 0; i < 4; i++) {
            Globo globo = new Globo(50 + i * 150, 500, this);
            globos.add(globo);
        }
        techo = new Techo(0, 0);

        setVisible(true);

        // Ciclo de animación
        new Thread(() -> {
            while (true) {
                repaint(); // Actualizar la pantalla
                try {
                    Thread.sleep(1000 / 60); // Control de FPS
                } catch (InterruptedException ex) {
                    ex.printStackTrace();
                }
            }
        }).start();
    }

    // Métodos adicionales...
    public ArrayList<Globo> getGlobos() {
        return globos;
    }

    public Techo getTecho() {
        return techo;
    }

    // Método para reiniciar la carrera
    private void reiniciarCarrera() {
        // Restablecer estado de los globos
        for (Globo globo : globos) {
            globo.reiniciar();
        }

        // Reiniciar variables de estado
        carreraIniciada = false;
        podioMostrado = false;

        // Mostrar botones correspondientes
        btnIniciar.setEnabled(true);
        btnReiniciar.setVisible(false);

        // Limpiar podio
        JOptionPane.showMessageDialog(this, "¡Carrera reiniciada!");
    }

 // Nueva variable de estado para evitar que se muestre el podio varias veces
    private boolean podioMostrado = false;

    @Override
    public void addNotify() {
        super.addNotify();
        // Crear una estrategia de búfer con 2 buffers
        createBufferStrategy(2);
    }

    @Override
    public void paint(Graphics g) {
        // Obtener la estrategia de búfer
        Graphics bufferGraphics = getBufferStrategy().getDrawGraphics();

        try {
            // Fondo
            bufferGraphics.setColor(Color.white);
            bufferGraphics.fillRect(0, 0, getWidth(), getHeight());

            // Dibujar techo
            techo.dibujar(bufferGraphics);

            // Dibujar globos
            for (Globo globo : globos) {
                globo.dibujar(bufferGraphics);
            }

            // Mostrar FPS
            long currentFrame = System.nanoTime();
            fps = (int) (1e9 / (currentFrame - lastFrame));
            lastFrame = currentFrame;
            bufferGraphics.setColor(Color.BLACK);
            bufferGraphics.drawString("FPS: " + fps, 10, 50);

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
        } finally {
            // Liberar recursos y mostrar el buffer
            bufferGraphics.dispose();
            getBufferStrategy().show();
        }
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
