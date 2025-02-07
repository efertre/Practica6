package model;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class Techo {

    private int x, y;
    private BufferedImage sprite;

    public Techo(int x, int y) {
        this.x = x;
        this.y = y;

        // Cargar sprite
        try {
            sprite = ImageIO.read(new File("assets/techo_fuego.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public void dibujar(Graphics g) {
        g.drawImage(sprite, x, y, null);
    }

    public int getY() {
        return y;
    }
    
    // Según el globo que explote primero, el láser cambiará de color al de dicho globo
    public void cambiarColor(int id) {
    	
    	String ruta = "assets/techo_";
    	
    	switch(id) {
    	case 1: ruta+="rosa";
    	break;
    	case 2: ruta+= "azul";
    	break;
    	case 3: ruta+="naranja";
    	break; 
    	case 4: ruta+= "verde";
    	break;
    	}
    	
    	ruta += ".png";
    	
    	// Cargar sprite
        try {
            sprite = ImageIO.read(new File(ruta));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}