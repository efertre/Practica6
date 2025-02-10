package view;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Map;
import javax.imageio.ImageIO;
import javax.swing.JPanel;
import model.Globo;

// Panel de Podio: Esta clase representa el panel donde se muestra el podio con los globos ganadores.
public class PodioPanel extends JPanel {
    private static final long serialVersionUID = 1L;

    // Mapa con los globos y el orden en el que llegaron.
    // Este mapa contiene la posición (1, 2, 3) como clave y el objeto Globo correspondiente como valor.
    private Map<Integer, Globo> ordenLlegada;

    // Imágenes para el podio y del fondo.
    // `podioImagen`: Imagen que representa el podio.
    // `backgroundImage`: Imagen de fondo del panel.
    private BufferedImage podioImagen;
    private BufferedImage backgroundImage;

    // Constructor de la clase que recibe el mapa del frmPrincipal.
    // El mapa `ordenLlegada` se utiliza para determinar qué globos deben mostrarse en el podio.
    public PodioPanel(Map<Integer, Globo> ordenLlegada) {
        this.ordenLlegada = ordenLlegada;

        // Dimensiones del panel: Ancho de 695 píxeles y alto de 300 píxeles.
        setPreferredSize(new Dimension(695, 300));

        // Cargar imágenes desde archivos locales o recursos empaquetados.
        try { 
            // Carga la imagen del podio desde la carpeta "assets".
            podioImagen = ImageIO.read(new File("assets/podio.png")); 

            // Carga la imagen de fondo desde un recurso empaquetado dentro del proyecto.
            backgroundImage = javax.imageio.ImageIO.read(getClass().getResource("/imagen/fondo_podio.png"));
        } catch (IOException e) {
            // En caso de error al cargar las imágenes, imprime el stack trace para depuración.
            e.printStackTrace();
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        // Dibujar la imagen de fondo.
        // Si la imagen de fondo está cargada, se dibuja ocupando todo el panel.
        if (backgroundImage != null) {
            g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
        }

        // Intentar cargar una fuente personalizada para darle estilo al texto.
        try {
            // Carga la fuente personalizada "Heartless.ttf" desde la carpeta "assets".
            Font customFont = Font.createFont(Font.TRUETYPE_FONT, new File("assets/Heartless.ttf"));

            // Establece el color del texto en negro.
            g.setColor(Color.BLACK);

            // Aplica la fuente personalizada con tamaño 60 y estilo bold.
            g.setFont(customFont.deriveFont(Font.BOLD, 60));

            // Dibuja el título "PODIO" centrado en la parte superior del panel para dar un efecto de sombra.
            g.drawString("PODIO", 285, 80);

            // Cambia el color del texto a amarillo brillante para crear un efecto visual.
            g.setColor(Color.YELLOW);

            // Repite el título "PODIO" con el nuevo color.
            g.drawString("PODIO", 290, 80); // Ajuste de coordenadas para sobreponer los textos.

        } catch (Exception e) {
            // En caso de error al cargar la fuente, imprime el stack trace para depuración.
            e.printStackTrace();
        }

        // Título del podio (este es redundante porque ya se dibuja arriba).
        g.drawString("PODIO", 290, 80);

        // Dibujar el podio.
        // Si la imagen del podio está cargada, se dibuja en la posición especificada.
        if (podioImagen != null) {
            g.drawImage(podioImagen, 147, 230, 400, 120, this); 
            // Coordenadas: x=147, y=230; Ancho=400, Alto=120.
        }

        // Dibujar los globos en sus posiciones.
        // Llama a la función `dibujarGlobos` para cada posición del podio.
        dibujarGlobos(g, 320, 60, 1); // Primer lugar (centro)
        dibujarGlobos(g, 190, 110, 2); // Segundo lugar (izquierda)
        dibujarGlobos(g, 450, 110, 3); // Tercer lugar (derecha)
    }

    private void dibujarGlobos(Graphics g, int x, int y, int posicion) {
        // Verifica si existe un globo en la posición indicada.
        if (ordenLlegada.containsKey(posicion)) {
            Globo globo = ordenLlegada.get(posicion); // Obtiene el globo correspondiente.

            // Determina la ruta de la imagen del globo según su ID.
            String ruta = "";
            switch ((int) globo.getId()) {
                case 1: ruta = "assets/corazon_rosa.png"; break;
                case 2: ruta = "assets/corazon_azul.png"; break;
                case 3: ruta = "assets/corazon_amarillo.png"; break;
                case 4: ruta = "assets/corazon_verde.png"; break;
            }

            // Dibuja la imagen del globo en la posición especificada.
            try {
                g.drawImage(ImageIO.read(new File(ruta)), x - 20, y + 30, 100, 150, this);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}