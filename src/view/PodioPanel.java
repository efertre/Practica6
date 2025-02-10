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

public class PodioPanel extends JPanel {
    private static final long serialVersionUID = 1L;
    private Map<Integer, Globo> ordenLlegada;

    // Imágenes para el podio, medallas y globos
    private BufferedImage podioImagen;

    private BufferedImage backgroundImage;

    public PodioPanel(Map<Integer, Globo> ordenLlegada) {
        this.ordenLlegada = ordenLlegada;
        setPreferredSize(new Dimension(695, 300));

        // Cargar imágenes
        try {
            podioImagen = ImageIO.read(new File("assets/podio.png")); // Imagen del podio
            backgroundImage = javax.imageio.ImageIO.read(getClass().getResource("/imagen/fondo_podio.png"));

            
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        
        
     // Dibujar la imagen de fondo
	    if (backgroundImage != null) {
	        g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
	    }

	    try {
	    	
	    	Font customFont = Font.createFont(Font.TRUETYPE_FONT, new File("assets/Heartless.ttf"));
	        g.setColor(Color.BLACK);
	        g.setFont(customFont.deriveFont(Font.BOLD, 60));
	        
	        g.drawString("PODIO", 285, 80);
	        
	
	        
	        g.setColor(Color.YELLOW); // Rojo intenso
	        g.setFont(customFont.deriveFont(Font.BOLD, 60));
	        
	        

	    } catch (Exception e) {
	        e.printStackTrace();
	    }

        // Título del podio
        g.drawString("PODIO", 290, 80);

        // Dibujar el podio
        if (podioImagen != null) {
            g.drawImage(podioImagen, 147, 230, 400, 120, this); // Ajusta las coordenadas según el tamaño de la imagen
        }

        // Dibujar los globos y medallas en sus posiciones
        dibujarGloboYMedalla(g, 320, 60, 1); // Primer lugar
        dibujarGloboYMedalla(g, 190, 110, 2); // Segundo lugar
        dibujarGloboYMedalla(g, 450, 110, 3); // Tercer lugar
    }

    private void dibujarGloboYMedalla(Graphics g, int x, int y, int posicion) {
        
        // Obtener el globo correspondiente
        if (ordenLlegada.containsKey(posicion)) {
            Globo globo = ordenLlegada.get(posicion);

            String ruta = "";
	        switch ((int)globo.getId()) {
	            case 1: ruta = "assets/corazon_rosa.png"; break;
	            case 2: ruta = "assets/corazon_azul.png"; break;
	            case 3: ruta = "assets/corazon_naranja.png"; break;
	            case 4: ruta = "assets/corazon_verde.png"; break;
	        }
	      
            
                try {
					g.drawImage(ImageIO.read(new File(ruta)), x - 20, y + 30, 100, 150, this);
				} catch (IOException e) {
					
					e.printStackTrace();
				} // Ajusta las coordenadas según el tamaño del globo
            
        }
        
     
 
    }
}