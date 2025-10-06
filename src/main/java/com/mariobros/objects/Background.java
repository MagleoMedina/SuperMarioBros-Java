package com.mariobros.objects;

import java.awt.*;
import java.awt.image.*;

/**
 *Background.java class
 */
public class Background {

    //--------------------------------------------------Atributos de la Clase----------------------------------------------------------------------------------------------
    
    //Se utiliza para indicar que las dimensiones de la imagen deben ser las mismas que las dimensiones originales de la imagen cargada.
    public static final int IMG_DIMENSIONS = -1;
    //es una variable de tipo Stage que representa el escenario en el que se encuentra el fondo.
    protected Stage stage;
    //es una variable de tipo Map que representa el mapa actual del escenario.
    protected Map map;
    //Representan las coordenadas del escenario, un factores de velocidad.
    protected double x, y, speedFactor, speedX, speedY;
    protected double defaultSpeed = 0;
    //Representa la Trasnparencia del fondo
    protected float alpha;
    //Representan el ancho y largo del fondo
    protected int width, height;
    //Para la creacion de imagenes de fondo
    protected BufferedImage img;

//-------------------------------------------------------------------Constructor de la Clase--------------------------------------------------------------------------------------------
    public Background(Stage s, String imgName) {
        this(s, imgName, 1.0, 0, IMG_DIMENSIONS, IMG_DIMENSIONS, 1.0F);
    }

    public Background(Stage s, String imgName, double speedFactor,double defaultSpeed, int width, int height,float alpha) {
        this.stage = s;
        this.map = ((MainStage) s).getCurrentMap();
        this.width = (width != IMG_DIMENSIONS) ? width : IMG_DIMENSIONS;
        this.height = (height != IMG_DIMENSIONS) ? height : IMG_DIMENSIONS;
        setImage(imgName);
        this.x = 0;
        this.y = 0;
        this.alpha = alpha;
        this.defaultSpeed = defaultSpeed;
        this.speedFactor = speedFactor;
    }

    //--------------------------------------------------------------------Metodos de la clase--------------------------------------------------------------------------------------

    /*
     *  Actualiza las coordenadas x e y del fondo para moverlo en el escenario según las velocidades especificadas 
     * en las variables de instancia defaultSpeed, speedX y speedY.
     */
    public void move() {
        if (defaultSpeed != 0) {
            x += defaultSpeed;
        }
        x += speedX;
        // Pasamos la 'y' como positiva
        // debido a que en pantalla la 'y'
        // aumenta al bajar y no al subir
        // como ocurre con los vectores
        // matematicos.
        y += speedY;
    }

    //Llamamos al metodo move
    public void act() {
        move();
    }

        //Metodos para pintar el fondo
    private void paintImg(Graphics g, int x2, int y2) {

        stage.getImagesEffects().paintAlphaImg( g, img, x2, y2, width, height,alpha);
    }

    private void paintQueue(Graphics g, int x, int y) {
        stage.getImagesEffects().paintAlphaImg(g, img, x, y, map.getDisplayableWidth(), 
        map.getDisplayableHeight(),0, 0, map.getDisplayableWidth() - x,map.getDisplayableHeight() - y,alpha);
    }

    private void paintCap(Graphics g, int x, int y) {
        stage.getImagesEffects().paintAlphaImg(g, img, 0, 0, x, map.getDisplayableHeight(),x, height, 0, height, alpha);
    }

     /*
     * Este método dibuja el fondo en el escenario utilizando los métodos privados paintCap, paintImg y paintQueue.
     */

    public void paint(Graphics g) {
        int X = (int) x % width;
        int Y = (int) y % height;
        int screenWidth = map.getDisplayableWidth();
        if (X != 0) {
            if (X > 0) {
                X -= width;
                        }
            paintCap(g, X, Y);
        }
        for (int i = X; i < screenWidth; i += width) {
            if (i + width < screenWidth) {
                paintImg(g, i, Y);
            } else {
                paintQueue(g, i, Y);
            }
        }
    }

    // ----------------------------------------------------------------------------Metodos Set---------------------------------------------------------------------------------------
    public void setX(double x) {
        this.x = x % width;
    }

    public void setY(double y) {
        this.y = y % height;
    }

    private void setImage(String imgName) {
        setImage(stage.getImagesLoader().getImage(imgName));
    }

    private void setImage(BufferedImage img) {
        if (width == IMG_DIMENSIONS) {
            width = img.getWidth();
        }
        if (height == IMG_DIMENSIONS) {
            height = img.getHeight();
        }
        if (img.getWidth() != width
                || img.getHeight() != height) {
            this.img = stage.getImagesEffects().returnRescaleImg(img, width, height);
        } else {
            this.img = img;
        }
    }
     public void setSpeedX(double toMove) {
        speedX = toMove * speedFactor;
    }

    public void setSpeedY(double toMove) {
        speedY = toMove * speedFactor;
    }

    // ------------------------------------------------------------------------------Metodos Get-----------------------------------------------------------------------------------------
    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

   }
