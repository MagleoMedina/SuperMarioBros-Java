package com.mariobros.objects;

import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;

import javax.swing.*;

import com.mariobros.objectsloaders.ImagesEffects;
import com.mariobros.objectsloaders.ImagesLoader;
import com.mariobros.objectsloaders.SoundsLoader;


/*
 * Stage.java Class
 */
public abstract class Stage implements Runnable, KeyListener, MouseListener, WindowListener,
        MouseMotionListener, MouseWheelListener, FocusListener,
        ComponentListener, WindowStateListener, WindowFocusListener {

    // ---------------------------------------------------------------Variables de
    // la
    // Clase---------------------------------------------------------------------------------------------------

    // Para el tamaño del escenario
    protected static int WIDTH = 500;
    protected static int HEIGHT = 400;

    // Para el loop de animacion
    protected Thread animator;

    // Detiene el hilo animador: pausa/detiene la animacion
    protected volatile boolean running = false;

    // Para la terminacion del juego, el juego no se detiene.
    protected volatile boolean gameOver = false;

    // Para la pausa del juego, el juego no se detiene.
    // protected volatile boolean pause = false;

    // Variables para el renderizado fuera de pantalla
    protected BufferStrategy bs;

    /*
     * Numero de frames con delay de 0 ms antes del hilo de animacion cede a otros
     * subprocesos en ejecucion
     */

    protected final static int NO_SLEEPS_FOR_YIELD = 15;

    /*
     * Frame que se pueden omitir en cualquier loop de animacion
     * El estado de los juego se actualiza pero no se renderiza (UPS)
     */
    protected final static int MAX_FRAME_SKIPS = 8;

    // Para calcular el periodo
    protected static int FPS;

    /*
     * Periodo es lo que indica FPS. 1000(1seg en miliS)/Period = FPS
     * Esto debe ser representado en nanoSeg
     */
    protected static long periodo;

    // Para visualizar el performance(Estaditicas de juego)
    protected static long totalFrames = 0;
    protected static long totalUpdates = 0;
    protected static long totalTiempoEspera = 0;

    // Almacena el tiempo inicial en nanoSeg despues de que se llama a initStage()
    protected static long initTime;
    // Almacena el tiempo en el que el juego no estuvo pausado.
    protected long playedTime;

    /*
     * Utilizada para cargar imagenes.
     * Debe ser antes modificada en getImagesLoader()
     */
    protected ImagesLoader imgLoader;

    /* Para los efectos de las imagenes */
    protected ImagesEffects imgEffects;

    /*
     * Utilizada para cargar sonidos.
     * Debe ser antes modificada en getSoundsLoader()
     */
    protected SoundsLoader soundsLoader;

    /*
     * Util para los modos de pantalla del juego
     */
    public static final int JFRAME = 1;
    protected JFrame window; // Util para JFRAME

    public static final int CANVAS = 2; // Pinta en una clase Canvas
    protected Canvas canvas; // Util para el modo CANVAS

    public static final int JPANEL = 3; // Pinta en la clase JPanel
    protected JPanel panel; // util para el modo JPANEL

    protected int mode; // Actual modo
    protected Component component; // Componente especifico del modo

    // Para el modo Exclusivo Full-Screen
    protected GraphicsEnvironment ge;
    protected GraphicsDevice screenDevice;
    protected DisplayMode defaultDisplay;

    // -----------------------------------------------------------------------------------Constructores
    // de la
    // clase-----------------------------------------------------------------------------

    /**
     * Se crea un escenario vacío.Para comenzar el juego debe llamar
     * <code> startGame () </code>.
     *
     * @Param Mode Surface sobre que se mostrará el juego.
     * @see startGame ();
     */
    public Stage() {

    }

    public Stage(int mode) {
        initTime = System.currentTimeMillis();
        setFPS(80);
        ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        screenDevice = ge.getDefaultScreenDevice();

        switchMode(mode);
    }

    // -------------------------------------------------------------------------Metodos
    // de la
    // Clase------------------------------------------------------------------------------------------------------

    // Metodos relativos a las dimensiones de la pantalla de juego

    /**
     * Cambie el modo de visualización actual o inicie una pantalla.
     */
    public void switchMode(int mode) {
        this.mode = mode;

        switch (mode) {
            case JFRAME:
                initJFrame();
                break;
            case CANVAS:
                initCanvas();
                break;
            case JPANEL:
                initJPanel();
                break;
            default:
                throw new IllegalArgumentException("El modo de juego del escenario es invalido.");
        }

        // Tiene que ver con la deteccion de movimientos

        // Agregue los Capturadores para capturar los eventos
        // Capturar las prensas de mouse de componentes
        component.addMouseListener(this);
        // Capturar eventos clave de componentes
        component.setFocusable(true);
        component.requestFocus();
        component.addKeyListener(this);
        // Capturar la rueda del mouse Componente
        component.addMouseWheelListener(this);
        // Capturar el componente
        component.addComponentListener(this);
        // Capturar el enfoque del componente cambiado
        component.addFocusListener(this);
        // capturar el movimiento del mouse componente
        component.addMouseMotionListener(this);

    }

    /**
     * Inicie un canvas.
     */
    public void initCanvas() {
        canvas = new Canvas() {
            private static final long serialVersionUID = 1L;

            public void addNotify() {
                super.addNotify();
                canvas.createBufferStrategy(2);
                bs = canvas.getBufferStrategy();
            }
        };
        canvas.setIgnoreRepaint(true);
        try { // sleep para dar tiempo para que se haga la estrategia de búfer.
            Thread.sleep(1000); // 1 sec
        } catch (InterruptedException ex) {
        }
        component = canvas;
    }

    /**
     * Init a JFrame.
     */
    public void initJFrame() {
        initCanvas();

        window = new JFrame("CounterStrike 1.6");
        window.addWindowFocusListener(this);
        window.addWindowListener(this);
        window.addWindowStateListener(this);
        window.setIgnoreRepaint(true);
        window.getContentPane().add(canvas);
    }

    /*
     * public void cerrar(){
     * window.dip
     * }
     */

    /**
     * Init a JPanel.
     */
    public void initJPanel() {
        panel = new JPanel() {
            private static final long serialVersionUID = 1L;

            public void addNotify() {
                super.addNotify();

            }
        };
        panel.setIgnoreRepaint(true);

        component = panel;
    }

    // Fin de los metodos de Pantalla de juego

    /**
     * Inicializa y comienza el hilo con el bucle del juego.
     */
    public void startGame() {
        if (animator != null || !running) {
            animator = new Thread(this);
            animator.start();
        }
    }

    /*
     * Juego de bucle: actualizar, renderizar, dormir.
     * Controla el FPS y los UPS con un
     * System.nanoTime();
     */

    public void run() throws NullPointerException {
        if (bs == null) {
            throw new NullPointerException("BufferStragegy es null.");
        }
        long beforeTime, afterTime, diff, sleepTime;
        long extraSleepTime = 0L, excessTime = 0L;
        int noSleeps = 0;

        try {
            initStage();
        } catch (Exception e) {

            e.printStackTrace();
        }
        initTime = playedTime = System.nanoTime();

        running = true;
        while (running) {
            beforeTime = System.nanoTime();

            /*
             * Si la animación de cuadro está tomando demasiado tiempo, actualice
             * El estado del juego sin representarlo, para obtener el
             * Ups más cerca del FPS requerido.
             */
            int skips = 0;
            while (skips < MAX_FRAME_SKIPS && excessTime > periodo) {
                excessTime -= periodo;
                updateStage(); // solo actualización, no renderizado
                skips++;
                totalUpdates++;
            }

            // LOOP game
            updateStage(); // Actualizar la etapa del juego
            updateScreen(); // Renderizar la etapa en un búfern búfer
                            // y mostrarlo en la pantalla

            afterTime = System.nanoTime();
            diff = afterTime - beforeTime;
            sleepTime = (periodo - diff) - extraSleepTime;

            if (sleepTime > 0) {
                totalTiempoEspera += sleepTime;
                try {
                    // nanoS -> miliS
                    Thread.sleep(sleepTime / 1000000L);
                } catch (InterruptedException e) {
                    System.err.println(e.getMessage());
                }
                extraSleepTime = System.nanoTime() - afterTime - sleepTime;
            } else {
                // sleepTime<=0 el marco tarda más que el período
                excessTime -= sleepTime; // Almacena el valor de exceso de tiempo
                extraSleepTime = 0L;
                if (++noSleeps >= NO_SLEEPS_FOR_YIELD) {
                    // Dale a otro hilo la oportunidad de correr
                    Thread.yield();
                    noSleeps = 0;
                }
            }
        }

    }

    /**
     * Repinte la pantalla si el búfer no pierde el contenido.
     */

    public void updateScreen() {
        ++totalFrames;
        ++totalUpdates;
        try {
            Graphics g = bs.getDrawGraphics();
            renderStage(g);

            if (bs.contentsLost()) {
                System.err.println("Contenidos del el buffer estan perdidos.");
            } else {
                bs.show();
            }
            g.dispose();
            // sincronizar la pantalla en algunos sistemas
            Toolkit.getDefaultToolkit().sync();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Te permite visualizar las Stats del juego

    public static void showPerformance() {
        double totalTime = (System.nanoTime() - initTime) / 1000000000.0;
        System.out.println("------------------------------");
        System.out.println("Total de Tiempo(Seg): "
                + totalTime + "\nFPS: " + FPS + "  Period: " + periodo
                + "\nTotal frames: " + totalFrames + "\nPromedio FPS: "
                + ((float) totalFrames / totalTime)
                + "\nTotal actualizacione: " + totalUpdates + "\n"
                + "Promedio UPS: " + ((float) totalUpdates / totalTime)
                + "\nTiempo de espera: " + totalTiempoEspera
                + "\nPromedio de tiempo de espera: " + ((float) totalTiempoEspera / periodo));
    }

    // Metodos abstractos para las clases hijas

    public abstract void updateStage();

    public abstract void renderStage(Graphics g);

    public abstract void initStage();

    // Indica si el juego a terminado
    public void gameOver() {
        gameOver = true;
    }

    public void exit() {
        running = false;
    }

    public void updateSize() {
        Stage.WIDTH = component.getWidth();
        Stage.HEIGHT = component.getHeight();
    }
    // ------------------------------------------------------------------------------------------Metodos
    // Get---------------------------------------------------------------------------

    public ImagesLoader getImagesLoader() {
        return imgLoader;
    }

    public ImagesEffects getImagesEffects() {
        return imgEffects;
    }

    public SoundsLoader getSoundsLoader() {
        return soundsLoader;
    }

    public JFrame getWindow() {
        return window;
    }

    public long getTimeRunning() {
        return System.nanoTime() - initTime;
    }

    public long getTimePlayed() {
        return System.nanoTime() - playedTime;
    }

    public Component getComponent() {
        return component;
    }

    public int getFPS() {
        return FPS;
    }

    public long getTotalUpdates() {
        return totalUpdates;
    }

    public long getTotalFrames() {
        return totalFrames;
    }

    public int getWidth() {
        return WIDTH;
    }

    public int getHeight() {
        return HEIGHT;
    }

    public boolean isGameOver() {
        return gameOver;
    }

    public boolean isRunning() {
        return running;
    }

    // Comprueba si el DisplayMode es compatible.

    public boolean isDisplayModeAvailable(DisplayMode d) {

        DisplayMode[] dm = screenDevice.getDisplayModes();
        for (int i = 0; i < dm.length; i++) {
            if (dm[i].getWidth() == d.getWidth() && dm[i].getHeight() == d.getHeight()
                    && dm[i].getBitDepth() == d.getBitDepth() && dm[i].getRefreshRate() == d.getRefreshRate()) {
                return true;
            }
        }
        return false;
    }
    // ---------------------------------------------------------------------------Metodos
    // Set---------------------------------------------------------------------------------------------------------------------------------------------------------

    public void setWindowVisible(boolean v) {
        if (window != null) {
            window.setVisible(v);
        }
    }

    public void setImagesLoader(ImagesLoader il) {
        this.imgLoader = il;
        this.imgEffects = new ImagesEffects(il);
    }

    public void setSoundsLoader(SoundsLoader sl) {
        this.soundsLoader = sl;
    }

    public void setFPS(int fps) {
        Stage.FPS = fps;
        periodo = 1000000000L / FPS;
    }

    public void setSize(int w, int h) {
        component.setPreferredSize(new Dimension(w, h));
        if (mode == JFRAME) {
            window.pack();
        } else {
            updateSize();
        }
    }

    // ------------------------------------------------------------------------Metodos
    // de
    // implementacion--------------------------------------------------------------------------------------

    // Implementa todos los eventos que puede sobrescribir en una subclase
    // De ser necesario
    public void keyPressed(KeyEvent e) {
    }

    public void keyReleased(KeyEvent e) {
        int keyCode = e.getKeyCode();
        if ((keyCode == KeyEvent.VK_ESCAPE) || (keyCode == KeyEvent.VK_END)
                || ((keyCode == KeyEvent.VK_C) && e.isControlDown())) {
            exit();
        }
    }

    public void keyTyped(KeyEvent e) {
    }

    public void mouseClicked(MouseEvent e) {
    }

    public void mouseEntered(MouseEvent e) {
    }

    public void mouseExited(MouseEvent e) {
    }

    public void mousePressed(MouseEvent e) {
    }

    public void mouseReleased(MouseEvent e) {
    }

    public void mouseDragged(MouseEvent e) {
    }

    public void mouseMoved(MouseEvent e) {
    }

    public void mouseWheelMoved(MouseWheelEvent e) {
    }

    public void focusGained(FocusEvent e) {
    }

    public void focusLost(FocusEvent e) {
    }

    public void componentHidden(ComponentEvent e) {
    }

    public void componentMoved(ComponentEvent e) {
        if (mode == JFRAME && e.getComponent() instanceof JFrame) {
            window.setLocation(0, 0);
        }
    }

    public void componentResized(ComponentEvent e) {
        updateSize();
    }

    public void componentShown(ComponentEvent e) {
    }

    public void windowActivated(WindowEvent e) {
    }

    public void windowClosed(WindowEvent e) {
    }

    public void windowClosing(WindowEvent e) {
        exit();
    }

    public void windowDeactivated(WindowEvent e) {
    }

    public void windowDeiconified(WindowEvent e) {
    }

    public void windowIconified(WindowEvent e) {
    }

    public void windowOpened(WindowEvent e) {
    }

    public void windowGainedFocus(WindowEvent e) {
    }

    public void windowLostFocus(WindowEvent e) {
    }

    public void windowStateChanged(WindowEvent e) {
    }

    public void closeFrame() {
        window.dispose();
    }

    public JFrame getTopLevelAncestor() {
        return null;
    }

    public static void dispose() {
    }
}
