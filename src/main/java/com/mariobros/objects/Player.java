package com.mariobros.objects;

import java.awt.event.*;
import java.applet.*;

/**
 *Player.java class
 */
public abstract class Player extends WorldObject {

    //---------------------------------------------------------------------Variables de la Clase----------------------------------------------------------------------------------------------

    /* Indican el estado en el que se encuentra
	 * el jugador con respecto a el eje Y. */
    public static final int NOT_JUMPING = 0;
    public static final int RISING = 1;
    public static final int FALLING = 2;

    /* Indican el estado en el que se encuentra
	 * el jugador con respecto a el eje Y. */
    public static final int STOPPED = 0;
    public static final int MOVING_RIGHT = 1;
    public static final int MOVING_LEFT = 2;

    /* Variables estaticas que ayudan a reproducir
	 * los sonidos de dos saltos seguidos sin
	 * interrupciones. */
   
	@SuppressWarnings("removal")
	public static AudioClip[] audio;
    public static int indexAudio;
    public static boolean first = true;

    /* Teclas que controlan al jugador. 
	 * Para definir estas teclas en la subclase
	 * simplemente habra que implementar el metodo
	 * createKeys(void) y en el cuerpo de la funcion
	 * realizar la asignacion de valores segun las teclas del teclado. */
    public abstract void createKeys();

    public int keyLeft,keyRight,
               keyRun,keyJump,keyCrouch;
    
    /* Variables booleanas que indican si las
	 * teclas anteriormente nombradas estan o
	 * no pulsadas. */

    public boolean keyLeftDown,keyRightDown,
                   keyRunDown,keyJumpDown,keyCrouchDown = false;
   

    /* Para las imagenes que representan el estado
	 * del sprite.
     * Para definir estas imagenes en la subclase
	 * simplemente habra que implementar el metodo
	 * createimgs(void) y en el cuerpo de la funcion
	 * realizar la asignacion de valores segun convenga. */
    protected abstract void createImgs();

    protected String imgLStop,imgRStop,imgLJump,
                     imgRJump,imgLFall,imgRFall,    
                     imgRight,imgLeft,imgRSlip,
                     imgLSlip,imgRCrouch,imgLCrouch;


    /* Indica el estado actual con respecto al eje Y.
	 * Posibles valores:
	 * NOT_JUMPING, RISING, or FALLING. */
    protected int yState;

    /* Indica el estado actual con respecto al eje X.
	 * Posibles valores:
	 * STOPPED, MOVING_RIGHT, or MOVING_LEFT. */
    protected int xState;

    /* Variable que controla la direccion que hay que
	 * tomar en caso de que se pulsen las dos teclas
	 * de movimiento a la vez. */
    protected int leftAndRight;

    // Para las velocidades y aceleraciones
    protected float acelX = 0.05F;//modificable
    protected float speedX = 2;
    protected float acelY = ((MainStage) stage).getGravity();
    protected float speedY = 10; // defecto 10
    protected float reward = 1; // aceleracion extra
    // que se puede conseguir en caso de que se salte
    // con velocidad en el eje X. Si running==true se
    // puede conseguir algo mas de la velocidad
    // marcaga por esta variable. 
    protected float acelRunningX = 0.1F;
    protected float speedRunningX = 4;

    // Representa la posicion del suelo mas cercano en el
    // cual se puede apoyar el jugador.
    protected int floor = WorldObject.NOT_FLOOR;
    protected int lastFloor = WorldObject.NOT_FLOOR;

    // Varables booleanas que representan el estado
    // del jugador.
    // Para el EJE X:
    protected volatile boolean lookingRight = false;
    protected volatile boolean lookingLeft = false;
    protected volatile boolean slipping = false;
    protected volatile boolean running = false;
    // Para otros estado no relacionados con el
    // movimiento:
    protected volatile boolean crouching = false;

    //----------------------------------------------------------------Constructor de la clase-------------------------------------------------------------------------------
    @SuppressWarnings("removal")
	public Player(Stage s) {
        super(s);
        // Multiples sonidos de saltos para evitar
        // interrupciones en la reproduccion.
        if (first) {
            first = false;
            indexAudio = 0;
            Player.audio = new AudioClip[2];
            for (int i = 0; i < audio.length; i++) {
                audio[i] = stage.getSoundsLoader().returnAudio("jump.wav", true, true);
            }
        }
        // Establece las teclas que controlan al jugador
        createKeys();
        // Establece las imagenes que representan al jugador.
        createImgs();
        // Player quieto y mirando hacia la derecha.
        xState = STOPPED;
        yState = NOT_JUMPING;
        leftAndRight = STOPPED;
        lookingRight = true;
        setImage(imgRStop);
    }
    //-------------------------------------------------------------------------Metodos de la Clase-----------------------------------------------------------------------------------------
    public void movePlayer() {
        // En primer lugar cambiamos las coordenadas del
        // jugador en el mapa.
        move();
        // Movemos la X del mapa si es necesario...
        if (map.readyRightXMap(this)&& speed.getAccurateX() > 0) {
            map.setSpeedX(speed.getAccurateX());

        } else if (map.readyLeftXMap(this)&& speed.getAccurateX() < 0) {
            map.setSpeedX(speed.getAccurateX());
        }
        // Movemos la Y del mapa si es necesario...
        if (map.readyUpYMap(this)&& speed.getAccurateY() > 0) {
            map.setSpeedY(-speed.getAccurateY());

        } else if (map.readyDownYMap(this)&& speed.getAccurateY() < 0) {
            map.setSpeedY(-speed.getAccurateY());
        }
    }

    /*
     * Actualiza la velocidad en el eje X de un objeto. 
     * El método verifica el estado del objeto (moviéndose hacia la derecha, moviéndose hacia la izquierda o detenido) y ajusta su velocidad en consecuencia.
     * También verifica si el objeto está corriendo o no y ajusta la aceleración y la velocidad límite en consecuencia. 
     * Además, el método verifica si el objeto se ha salido del mapa por los laterales y ajusta su posición y velocidad en consecuencia. 
     * 
     */
    public void speedXUpdate() {
        double s = 0;
        float acel = (keyRunDown) ? acelRunningX : acelX;
        float limitSpeed = (keyRunDown) ? speedRunningX : speedX;
        if (xState == MOVING_RIGHT) {
            if (speed.getX() > limitSpeed) {
                s = speed.getAccurateX() - acelX;
            } else {
                s = speed.getAccurateX() + acel;
                if (s > limitSpeed) {
                    s = limitSpeed;
                }
            }
        } else if (xState == MOVING_LEFT) {
            if (speed.getX() < -limitSpeed) {
                s = speed.getAccurateX() + acelX;
            } else {
                s = speed.getAccurateX() - acel;
                if (s < -limitSpeed) {
                    s = -limitSpeed;
                }
            }
        } else if (xState == STOPPED) {
            // La aceleracion de frenado es siempre
            // acelX, esto crea un mayor efecto de
            // deslizamiento en el momento de frenar
            // desde velocidades superiores a speedX.
            // Eso ocurre cuando running==true.
            if (slipping) {
                if (lookingLeft) {
                    s = speed.getAccurateX() - acelX;
                    if (s < 0) {
                        s = 0;
                    }
                } else if (lookingRight) {
                    s = speed.getAccurateX() + acelX;
                    if (s > 0) {
                        s = 0;
                    }
                }
            } else {
                if (lookingRight) {
                    s = speed.getAccurateX() - acelX;
                    if (s < 0) {
                        s = 0;
                    }
                } else if (lookingLeft) {
                    s = speed.getAccurateX() + acelX;
                    if (s > 0) {
                        s = 0;
                    }
                }
            }
        }
        speed.setX(s);
        // Si nos salimos del mapa por los
        // laterales...
         if (x <= 0 && s < 0) {
            x = 0;
            speed.setX(0);

        } else if (x >= map.getWidth() - width && s > 0) {
            x = map.getWidth() - width;
            speed.setX(0);
        }
    }

    public void speedYUpdate() {
        if (yState == NOT_JUMPING && floor == NOT_FLOOR) {
            fall();
        } else if (yState != NOT_JUMPING && floor != NOT_FLOOR && floor != lastFloor) {
            land();
            // Le sumamos 1 para que sigua en contacto con la
            // plataforma en la que se encuentra y de esta
            // manera sabemos que no debe de volver a caer.
            y = floor - height + 1;
            speed.setY(0);
        }
        lastFloor = floor;
        floor = NOT_FLOOR;
        // Si esta saltando hay que aplicar la velocidad
        // de subida y detectar cuando comienza a bajar.
        if (yState != NOT_JUMPING) {
            // Si acabamos de saltar establecemos una
            // velocidad para que se eleve.
            if (yState == RISING && speed.getAccurateY() == 0) {
                // La potencia de salto depende en parte
                // de la velocidad que se lleva en el
                // eje X. El impulso es mayor cuanto mayor
                // sea la carrerilla.
                if (xState == MOVING_RIGHT) {
                    speed.setY(speedY+ speed.getAccurateX() / speedX * reward);

                } else if (xState == MOVING_LEFT) {

                    speed.setY(speedY- speed.getAccurateX() / speedX * reward);
                } else {
                    // Si no se mueve.
                    speed.setY(speedY);
                }
            } else {
                // Si estamos subiendo pero soltamos
                // la tecla de salto empezamos a bajar.
                if (yState == RISING && !keyJumpDown) {
                    if (speed.getAccurateY() > 4) {
                        speed.setY(4);
                    }
                }
                // Aplicamos gravedad, es decir, la aceleracion
                // en el eje Y (acelY). Siempre sin superar la
                // velocidad maxima indicada por speedY.
                double s = speed.getAccurateY() - acelY;
                if (yState != FALLING && s <= 0) {
                    fall();
                }
                // Evitar superar la velocidad maxima.
                if (-s > speedY) {
                    s = -speedY;
                }
                // Establecemos la aceleracion.
                speed.setY(s);
            }
        }   
  
    }

    @Override
    
    public synchronized void act() {
        // Actualizamos las coordenadas
        // dependiendo de su vector velocidad.
        // Tambien actualiza el mapa.
        movePlayer();
        // Control de velocidades para el EJE X:
        speedXUpdate();
        // Control de velocidades para el EJE Y:
        speedYUpdate();
        // Comprobamos si es posible saltar.
        if (keyJumpDown && yState == NOT_JUMPING) {
            jump();
        }
        // Control de la direccion en caso de que se
        // hayan pulsado las dos teclas.
        if (keyRightDown && keyLeftDown) {
            if (leftAndRight == STOPPED) {
                // Si es se mueve hacia la derecha hay que
                // hacer que se mueva hacia la izquierda.
                leftAndRight = xState;
            }
        } else {
            leftAndRight = STOPPED;
        }
        // Comprobamos cambios en el comportamiento
        // sobre el eje X.
        if (keyRightDown && xState != MOVING_RIGHT && leftAndRight != MOVING_RIGHT) {
            moveRight();

        } else if (keyLeftDown && xState != MOVING_LEFT && leftAndRight != MOVING_LEFT) {
            moveLeft();

        } else if (!keyLeftDown && !keyRightDown && xState != STOPPED) {

            // Si no hay ninguna tecla de dirreccion
            // pulsada y el jugador no esta quieto...
            stop();
        }
        // Agachamos al jugador en caso de que sea
        // necesario.
        if (xState == STOPPED && yState == NOT_JUMPING) {
            // Si no esta agachado pero esta la
            // tecla de agacharse pulsada...
            if (keyCrouchDown && !crouching) {
                crouch();
            } else if (!keyCrouchDown && crouching) {
                // Si esta agachado y no esta la
                // tecla de agacharse pulsada...
                standUp();
            }
        }
        // Actualizamos los sprites que representan al
        // jugador.
        updateImg();
    }

    public void updateImg() {
        // Controlamos cuando deja de resbalar.
        if (slipping) {
            if (xState == MOVING_LEFT && speed.getAccurateX() < 0) {
                slipping = false;
                setImages(imgLeft, 1, 3);

            } else if (xState == MOVING_RIGHT && speed.getAccurateX() > 0) {
                slipping = false;
                setImages(imgRight, 1, 3);
            }
        }
        // Pasamos de una imagen a otra cuando se esta
        // moviendo para dar el efecto de que mueve las
        // piernas y los brazos. Dependiendo de si anda
        // o esta corriendo las imagenes van mas lentas
        // o mas rapidas.
        if (xState != STOPPED && yState == NOT_JUMPING && !slipping) {
            int num = 0;
            if (running) {
                num = (stage.getFPS() / 15);
            } else {
                num = (stage.getFPS() / 10);
            }
            if (num == 0 || stage.getTotalFrames() % num == 0) {
                nextImg();
            }
        }
    }

    public synchronized void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == keyJump) {
            keyJumpDown = true;
        } else if (e.getKeyCode() == keyRight) {
            keyRightDown = true;
        } else if (e.getKeyCode() == keyLeft) {
            keyLeftDown = true;
        } else if (e.getKeyCode() == keyRun) {
            keyRunDown = true;
        } else if (e.getKeyCode() == keyCrouch) {
            keyCrouchDown = true;
        }
    }

    public synchronized void keyReleased(KeyEvent e) {
        if (e.getKeyCode() == keyJump) {
            keyJumpDown = false;
        } else if (e.getKeyCode() == keyRight) {
            keyRightDown = false;
        } else if (e.getKeyCode() == keyLeft) {
            keyLeftDown = false;
        } else if (e.getKeyCode() == keyRun) {
            keyRunDown = false;
        } else if (e.getKeyCode() == keyCrouch) {
            keyCrouchDown = false;
        }
    }
    /*
     * Metodos que representan el movimiento del personaje hacia donde mira 
     */
    public void moveLeft() {
        xState = MOVING_LEFT;
        lookingLeft = true;
        lookingRight = false;
        crouching = false;
        if (yState != NOT_JUMPING) {
            setImage(imgLJump);
        } else if (speed.getAccurateX() > 0) {
            slipping = true;
            setImage(imgLSlip);
        } else {
            setImages(imgLeft, 1, 3);
        }
    }

    
    public void moveRight() {
        xState = MOVING_RIGHT;
        lookingRight = true;
        lookingLeft = false;
        crouching = false;
        if (yState != NOT_JUMPING) {
            setImage(imgRJump);
        } else if (speed.getAccurateX() < 0) {
            slipping = true;
            setImage(imgRSlip);
        } else {
            setImages(imgRight, 1, 3);
        }
    }

    public void stop() {
        xState = STOPPED;
        if (lookingRight) {
            if (yState != NOT_JUMPING) {
                setImage(imgRJump);
            } else {
                setImage(imgRStop);
            }
        } else if (lookingLeft) {
            if (yState != NOT_JUMPING) {
                setImage(imgLJump);
            } else {
                setImage(imgLStop);
            }
        }
    }

    @SuppressWarnings("removal")
	public void jump() {
        yState = RISING;
        // Reproducimos sonido de salto
        audio[indexAudio].play();
        indexAudio = (indexAudio + 1) % audio.length;
        // Dependiendo de a donde mire ponemos una
        // imagen u otra.
        if (lookingRight) {
            setImage(imgRJump);
        } else if (lookingLeft) {
            setImage(imgLJump);
        }
    }

    public void fall() {
        yState = FALLING;
        if (lookingRight) {
            setImage(imgRFall);

        } else if (lookingLeft) {
            setImage(imgLFall);
        }
    }

    public void crouch() {
        crouching = true;
        if (lookingRight) {
            setImage(imgRCrouch);

        } else if (lookingLeft) {
            setImage(imgLCrouch);
        }
    }

    public void standUp() {
        crouching = false;
        if (lookingRight) {
            setImage(imgRStop);

        } else if (lookingLeft) {
            setImage(imgLStop);
        }
    }

    public void land() {
        if (yState == NOT_JUMPING) {
            return;
        }
        yState = NOT_JUMPING;
        if (xState == MOVING_LEFT) {
            if (speed.getAccurateX() > 0) {
                slipping = true;
                setImage(imgLSlip);

            } else {
                setImages(imgLeft, 1, 3);
            }
        } else if (xState == MOVING_RIGHT) {
            if (speed.getAccurateX() < 0) {
                slipping = true;
                setImage(imgRSlip);

            } else {
                setImages(imgRight, 1, 3);
            }
        } else if (xState == STOPPED) {
            if (lookingRight) {
                if (crouching) {
                    setImage(imgRCrouch);

                } else {
                    setImage(imgRStop);
                }
            } else if (lookingLeft) {
                if (crouching) {
                    setImage(imgLCrouch);

                } else {
                    setImage(imgLStop);
                }
            }
        }
    }

    // --------------------------------------------------------------------------------------Metodos Set -----------------------------------------------------------------------------------
    public void setXStage(int s) {
        if (s == MOVING_RIGHT
                || s == MOVING_LEFT
                || s == STOPPED) {
            xState = s;
        }
    }

    public void setYState(int s) {
        if (s == NOT_JUMPING
                || s == RISING
                || s == FALLING) {
            yState = s;
        }
    }

    public void setFloor(int floor) {
        this.floor = floor;
    }

    public void setRightWall(int wall) {
        x = wall - (int) bounds.get(0).getX();
    }

    public void setLeftWall(int wall) {
        x = wall - width
                + (int) bounds.get(0).getX();
    }
   


    // -------------------------------------------------------------------Metodos Get (Booleans)--------------------------------------------------------------------------------
    public boolean isRising() {
        return yState == RISING;
    }

    public boolean isFalling() {
        return yState == FALLING;
    }

    public boolean isRunning() {
        return running;
    }

    public boolean isCrouching() {
        return crouching;
    }

    public boolean isWalkingRight() {
        return xState == MOVING_RIGHT;
    }

    public boolean isWalkingLeft() {
        return xState == MOVING_LEFT;
    }
    
}
