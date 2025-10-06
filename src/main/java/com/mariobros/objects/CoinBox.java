package com.mariobros.objects;

import java.applet.*;
import java.awt.*;

/**
 *CoinBox.java class
 */
public class CoinBox extends WorldObject  {

    //----------------------------------------------------------------------Atributos de la clase------------------------------------------------------------------------------------------

    /* Permite que todos los objetos de esta misma clase
	 * puedan pasar de una imagen a otra todos sincronizados.
     */
    protected static int indexClass = 0;

    /* Indica que es necesario actualizar el indexClass. */
    protected static boolean changeImg = false;

    /* Numero de imagenes que representan al sprite. */
    public final static int LENGHT_IMAGES = 4;

    //Variables de audio para bloque CoinBox
    @SuppressWarnings("removal")
	public static AudioClip[] audio;
    public static int indexAudio;
    public static boolean first = true;

    protected String imgNormal = "box*_0";

    // Indica si el ladrillo ha sido golpeado cuando
    // el jugador no tiene la capacidad de romperlos,
    // por lo tanto indica que el ladrillo se esta moviendo.
    protected boolean moving = false;

    // Velocidad del movimiento.
    protected float movingSpeed = 1.5F;

    // Representa la posicion inicial en la que se encuentra
    // y el la cual se quedara quieto el ladrillo cuando
    // realize se movimiento al ser golpeado
    
    protected float initY = 0;

    //-------------------------------------------------------------------------------Constructor de la Clase--------------------------------------------------------------------------------
    @SuppressWarnings("removal")
	public CoinBox(Stage s) {
        super(s);
        supportsPlayer = true;
        setPreferredSize(map.tileXSize, map.tileYSize);
        setImages(imgNormal, 0, 4);
        // Rectangulo para las colisiones
        bounds.add(new Rectangle(-1, -1, width + 1, height + 1));
        if (first) {
            first = false;
            indexAudio = 0;
            audio = new AudioClip[5];
            for (int i = 0; i < audio.length; i++) {
                audio[i] = stage.getSoundsLoader().returnAudio("coin.wav", true, true); 
            }
        }
    }

//---------------------------------------------------------------------------------Metodos de la clase---------------------------------------------------------------------------------
    
/* Permite actualizar las imagenes para todos los objetos
	 * de esa clase. */
    public static void actClass() {
        if (changeImg) {
            indexClass = (indexClass + 1) % LENGHT_IMAGES;
            changeImg = false;
        }
    }

    /*
     *Actualiza el estado de un objeto. 
     * El método está anotado con @Override, lo que indica que sobrescribe un método de una clase padre.
     * Si la variable moving es verdadera,
     * el método llama al método move para mover el objeto y ajusta su velocidad en el eje Y en función de la gravedad del escenario. 
     * Si la posición y del objeto es mayor o igual a su posición inicial, 
     * se establece su velocidad en el eje Y en cero, se establece su posición y en su posición inicial y se establece la variable moving en falso. Luego, 
     * el método calcula la frecuencia de fotogramas y verifica si es cero o si el número total de actualizaciones del escenario es divisible por la frecuencia de fotogramas. 
     * Si se cumple alguna de estas condiciones, se llama al método setImage para establecer la imagen del objeto y se establece la variable changeImg en verdadero.
     */
    @Override
    public void act() {
        if (moving) {
            move();
            speed.setY(speed.getAccurateY() - ((MainStage) stage).getGravity());
            if (y >= initY) {
                speed.setY(0);
                y = initY;
                moving = false;
            }
        }
        int frameFrec = (int) (stage.getFPS() / 10);
        if (frameFrec == 0 || stage.getTotalUpdates() % frameFrec == 0) {
            setImage(indexClass);
            changeImg = true;
        }
    }
    /*
     * Maneja las colisiones entre un objeto y otro objeto Sprite. 
     * El método toma como parámetro un objeto Sprite y verifica si es una instancia de la clase Player y si el objeto actual soporta al jugador. 
     * Si ambas condiciones se cumplen, el método verifica si hay colisiones en el eje X y ajusta la velocidad y la posición del jugador en consecuencia. 
     * Luego, el método verifica si hay colisiones en el eje Y y ajusta la velocidad y la posición del jugador en consecuencia. 
     * Si el jugador está subiendo y su cabeza colisiona con el pie del objeto, se reproduce un sonido y se establece la variable moving en verdadero. 
     * Si el pie del jugador colisiona con la cabeza del objeto, se establece el piso del jugador en la posición y del objeto
     */
    @Override
    
    public void collision(Sprite s) {
        if (s instanceof Player && supportsPlayer) {
            Player p = (Player) s;
            // Colisiones del eje X
            if (getLeft().intersects(p.getRight()) && p.getSpeed().getAccurateX() > 0) {
                p.getSpeed().setX(0);
                p.setLeftWall((int) x);

            } else if (getRight().intersects(p.getLeft()) && p.getSpeed().getAccurateX() < 0) {
                p.getSpeed().setX(0);
                p.setRightWall((int) x + width);

            } else // Colisiones del eje Y
            if (p.getHead().intersects(getFoot()) && p.isRising()) {
                if (!moving) {
                    moving = true;
                    ((MainStage) stage).getSoundsLoader().play("blockHit", false);
                    speed.setY(movingSpeed);
                    if (s.getSpeed().getAccurateY() > movingSpeed) {
                        s.getSpeed().setY(movingSpeed);
                    }
                    s.setY(y + height);
                } else {
                    s.getSpeed().setY(speed.getAccurateY());
                }
            } else if (p.getFoot().intersects(getHead())) {
                if (!moving) {
                    p.setFloor((int) y);
                }
            }
        }
    }

    @Override
    //Metodo Set
    public void setY(float yPos) {
        super.setY(yPos);
        initY = yPos;
    }

}
