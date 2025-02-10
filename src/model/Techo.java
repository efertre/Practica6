package model;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

// Clase Techo: Representa el techo en el juego.
public class Techo {
    // Coordenadas x e y del techo.
    private int x, y;

    // Imagen (sprite) del techo.
    private BufferedImage sprite;

    // Constructor de la clase Techo.
    public Techo(int x, int y) {
        this.x = x; // Inicializa la coordenada x del techo.
        this.y = y; // Inicializa la coordenada y del techo.

        // Cargar el sprite del techo desde un archivo local.
        try {
            sprite = ImageIO.read(new File("assets/techo_fuego.png")); 
            // Intenta cargar la imagen "techo_fuego.png" desde la carpeta "assets".
        } catch (IOException e) {
            e.printStackTrace(); 
        }
    }

    // Método para dibujar el techo en la pantalla.
    public void dibujar(Graphics g) {
        if (sprite != null) { 
            // Verifica si la imagen del techo se cargó correctamente.
            g.drawImage(sprite, x, y, null); 
            // Dibuja la imagen del techo en las coordenadas (x, y).
        }
    }

    // Getter para obtener la coordenada Y del techo.
    public int getY() {
        return y; 
        // Devuelve la coordenada Y del techo.
    }
}