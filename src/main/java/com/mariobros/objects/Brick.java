package com.mariobros.objects;

import java.applet.*;
import java.awt.*;

/**
 *Brick.java Class
 */
public class Brick extends WorldObject {

    //-------------------------------------------------------------------------------------Variables de la clase-----------------------------------------------------------------
    
    /* Permite que todos los objetos de esta misma clase
	 * puedan pasar de una imagen a otra todos sincronizados.
     */
    protected static int indexClass = 0;

    /* Indica que es necesario actualizar el indexClass. */
    protected static boolean changeImg = false;

    /* Numero de imagenes que representan al sprite. */
    public final static int LENGHT_IMAGES = 4;

    /* Permite actualizar las imagenes para todos los objetos
	 * de esa clase. */

    //Variables que representan el sonido del bloque
    @SuppressWarnings("removal")
	public static AudioClip[] audio;
    public static int indexAudio;
    public static boolean first = true;

    protected String imgNormal = "brick*_0";

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


    //--------------------------------------------------------------------------------Constructor de la clase------------------------------------------------------------------------------------------
    @SuppressWarnings("removal")
	public Brick(Stage s) {
        super(s);
        supportsPlayer = true;
        setPreferredSize(Map.TILE_X_SIZE, Map.TILE_Y_SIZE);
        setImages(imgNormal, 0, 4);
        // Rectangulo para las colisiones
        bounds.add(new Rectangle(-1, -1, width + 1, height + 1));
        if (first) {
            first = false;
            indexAudio = 0;
            audio = new AudioClip[5];
            for (int i = 0; i < audio.length; i++) {
                audio[i] = stage.getSoundsLoader().returnAudio("block-hit.wav", true, true); // Nombre de los sonidos
            }
        }
    }
    //---------------------------------------------------------------------Metodos de la Clase----------------------------------------------------------------------------------------

    /*
     * Actualiza el estado de la clase. 
     * El método verifica si la variable estática changeImg es verdadera y, de ser así, 
     * incrementa el valor de la variable estática indexClass en uno y lo divide por el valor de la constante LENGHT_IMAGES para obtener el resto. 
     * Luego, se establece la variable changeImg en falso.
     */
        public static void actClass() {
        if (changeImg) {
            indexClass = (indexClass + 1) % LENGHT_IMAGES;
            changeImg = false;
        }
    }

    /*
     * Actualiza el estado de un objeto. 
     * El método está anotado con @Override, lo que indica que sobrescribe un método de una clase padre. 
     * Si la variable moving es verdadera, el método llama al método move para mover el objeto y ajusta su velocidad en el eje Y en función de la gravedad del escenario. 
     * Si la posición y del objeto es mayor o igual a su posición inicial, se establece su velocidad en el eje Y en cero, se establece su posición y en su posición inicial y 
     * se establece la variable moving en falso. Luego, el método calcula la frecuencia de fotogramas y verifica si es cero o si el número total de actualizaciones del 
     * escenario es divisible por la frecuencia de fotogramas. Si se cumple alguna de estas condiciones, se llama al método setImage para establecer la imagen del objeto y 
     * se establece la variable changeImg en verdadero.
     */
    @Override
    public void act() {
        if (moving) {
            move();
            speed.setY(speed.getAccurateY() - ((MainStage) stage).getGravity());
            if (y >= initY) {
                speed.setY(0);
                moving = false;
            }
        }
        int frameFrec = (int) (stage.getFPS() / 10);
        if (frameFrec == 0
                || stage.getTotalUpdates() % frameFrec == 0) {
            setImage(indexClass);
            changeImg = true;
        }
    }


    /*
    Toma como parámetro un objeto de la clase Sprite. El método verifica si el objeto Sprite pasado como parámetro es una instancia de la clase Player y 
    si la variable de instancia supportsPlayer es verdadera. Si ambas condiciones son verdaderas, el método realiza una serie de verificaciones para detectar 
    colisiones entre el objeto Player y otro objeto en los ejes X e Y.
    En el eje X, el método verifica si el lado izquierdo del objeto actual (this) está en intersección con el lado derecho del objeto Player y si la velocidad en X del 
    objeto Player es mayor que 0. Si ambas condiciones son verdaderas, se establece la velocidad en X del objeto Player en 0 y se llama al método setLeftWall del objeto Player, 
    pasando como parámetro la coordenada X del objeto actual.

    Si no se cumple la primera condición, el método verifica si el lado derecho del objeto actual está en intersección con el lado izquierdo del objeto Player y 
    si la velocidad en X del objeto Player es menor que 0. Si ambas condiciones son verdaderas, se establece la velocidad en X del objeto Player en 0 y se llama al método 
    setRightWall del objeto Player, pasando como parámetro la coordenada X del objeto actual más su ancho.

    En el eje Y, el método verifica si la cabeza del objeto Player está en intersección con el pie del objeto actual y si el objeto Player está subiendo. 
    Si ambas condiciones son verdaderas, se verifica si la variable de instancia moving es falsa. Si es así, se establece en verdadera, 
    se reproduce el sonido “blockHit” y se establece la velocidad en Y del objeto actual en el valor de la variable de instancia movingSpeed. 
    Además, si la velocidad en Y del objeto pasado como parámetro es mayor que movingSpeed, se establece su velocidad en Y en el valor de movingSpeed. 
    Finalmente, se establece la coordenada Y del objeto pasado como parámetro en la coordenada Y del objeto actual más su altura.
    Si no se cumple la primera condición, el método verifica si el pie del objeto Player está en intersección con la cabeza del objeto actual. 
    Si esta condición es verdadera y si la variable de instancia moving es falsa, se llama al método setFloor del objeto Player, pasando como parámetro 
    la coordenada Y del objeto actual.
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


    //Metodo set
    @Override

    public void setY(float yPos) {
        super.setY(yPos);
        initY = yPos;
    }

}
