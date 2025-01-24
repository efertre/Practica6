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
            sprite = ImageIO.read(new File("assets/techo.jpg"));
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
}
