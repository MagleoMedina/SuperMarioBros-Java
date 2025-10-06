package com.mariobros.objects;

import java.awt.*;
import java.awt.event.*;

import com.mariobros.objectsloaders.*;


/**
 * MainStage.java class
 */
public class MainStage extends Stage {

    // --------------------------------------------------------------------------------Atributos
    // de la
    // clase-------------------------------------------------------------------
    private ImagesLoader loader;
    private SoundsLoader sounds;
    private Map map;

    // Gravedad del escenario. Defecto 0.2
    private float gravity = 0.233F;// modificar

    // ----------------------------------------------------------------------------Constructor
    // de la
    // clase----------------------------------------------------------------------------------------

    public MainStage() {
        super(JFRAME);// opc 1 en la clase stage
        setFPS(60);
        setSize(960 - 6, 640 - 6);// 960-6, 640-6
        window.setResizable(false);
        // Creamos el mapa en el mundo=1 nivel=1.
        map = new Map(this, 1, 2);
        // Creamos los cargadores pero de momento
        // no cargamos nada.
        loader = new ImagesLoader("res/img", "loader");
        sounds = new SoundsLoader("res/sounds", "loader");
        // Añadimos los cargadores de sonido y de
        // imagen a el objeto Stage (superclase).
        setImagesLoader(loader);
        setSoundsLoader(sounds);

    }

    // -----------------------------------------------------------------------------------Metodos
    // de la
    // clase-------------------------------------------------------------------------------------------
    public synchronized void initStage() {
        // Cargamos las imagenes y los sonidos
        // que estan indicados en el archivo externo. Carpeta res
        loader.startLoader();
        sounds.startLoader();

        // Iniciamos el mapa.
        map.initMap();

        // Agg a los jugadores.
        Mario m = new Mario(this);
        map.addPlayer(m);

        Luigi l = new Luigi(this);
        map.addPlayer(l);

    }

    /*
     * actualiza el estado del juego.
     * El método es synchronized, lo que significa que solo un hilo puede ejecutarlo
     * a la vez.
     * El método llama al método act del objeto map para actualizar su estado.
     * Luego, el método verifica si el juego no ha terminado y si el número de
     * monedas recolectadas es igual al número total de monedas.
     * Si ambas condiciones se cumplen, se llama al método gameOver y se crea un
     * nuevo hilo para esperar un segundo,
     * reinicia el número de monedas recolectadas y cargar el siguiente nivel.
     * Finalmente, se agregan dos nuevos jugadores (Mario y Luigi) al mapa y se
     * reinicia el contador de monedas recolectadas.
     */
    public synchronized void updateStage() {
        map.act();
        if (!gameOver && Coin.N_COINS == Coin.COINS_CATCHED) {
            gameOver();
            map.stopMusic();// Detiene la musica al finalizar el nivel
            final Stage s = this;
            new Thread(new Runnable() {
                public void run() {
                    try {
                        Thread.sleep(1000);
                    } catch (Exception e) {
                        System.out.println(e);
                    }
                    Coin.N_COINS = 0;
                    gameOver = false;
                    try {

                        map.nextLevel();

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    map.addPlayer(new Mario(s));
                    map.addPlayer(new Luigi(s));
                    Coin.COINS_CATCHED = 0;
                }
            }).start();

        }

    }

    /*
     * Dibuja el estado actual del juego en un objeto Graphics.
     * El método es synchronized, lo que significa que solo un hilo puede ejecutarlo
     * a la vez.
     * El método establece el color del objeto Graphics en negro y dibuja un
     * rectángulo lleno que cubre toda la pantalla.
     * Luego, el método llama al método paint del objeto map para dibujar el mapa en
     * el objeto Graphics.
     * Después, el método verifica si el juego ha terminado y, de ser así, dibuja un
     * mensaje en la pantalla indicando que se ha llegado al final del nivel.
     */
    public void drawShadows(Graphics2D g2d, String text, int x, int y, Color textColor) {

        // Dibujar el texto
        g2d.setColor(textColor);
        g2d.drawString(text, x, y);
    }

    public void drawPrincipalString(Graphics2D g2d, String text, int x, int y, Color textColor) {

        g2d.drawString(text, x + 2, y + 2);

        // Dibujar el texto
        g2d.setColor(textColor);
        g2d.drawString(text, x, y);
    }

    public synchronized void renderStage(Graphics g) {

        g.fillRect(0, 0, WIDTH, HEIGHT);
        map.paint(g);
        Graphics2D g2 = (Graphics2D) g;

        // Indicamos que se ha llegado al final
        // del nivel si es necesario.

        if (!gameOver) {
            // Obtener el número de monedas recolectadas
            int coinsCollected = Coin.COINS_CATCHED;

            // Obtener el número total de monedas en el mapa
            int totalCoins = Coin.N_COINS;

            g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_GASP);
            Font font = new Font("Kristen ITC", Font.BOLD, 20);
            g2.setFont(font);
            drawShadows(g2, "Monedas recolectadas: " + coinsCollected + " / " + totalCoins, 10, 30, Color.BLACK);
            drawPrincipalString(g2, "Monedas recolectadas: " + coinsCollected + " / " + totalCoins, 10, 30,
                    Color.WHITE);

        }
        if (gameOver) {

            g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_GASP);
            g2.setFont(new Font(Font.MONOSPACED, Font.BOLD, 30));
            drawShadows(g2, "Fin del Nivel", WIDTH / 2 - 100, HEIGHT / 2 - 10, Color.BLACK);
            drawPrincipalString(g2, "Fin del Nivel", WIDTH / 2 - 100, HEIGHT / 2 - 10, Color.CYAN);

        }

    }

    // Metodos relativos al teclado

    public synchronized void mouseMoved(MouseEvent e) {
    }

    public void keyPressed(KeyEvent e) {
        for (int i = 0; i < map.players.size(); i++) {
            map.players.get(i).keyPressed(e);
        }
    }

    public void keyReleased(KeyEvent e) {
        for (int i = 0; i < map.players.size(); i++) {
            map.players.get(i).keyReleased(e);
        }
    }

    public void mousePressed(MouseEvent e) {
    }

    // -----------------------------------------------------------------Metodos
    // Get------------------------------------------------------------------------------
    public float getGravity() {
        return gravity;
    }

    public Map getCurrentMap() {
        return map;
    }

}
