
package com.mariobros.objects;

import java.applet.*;



/**
 *Coin.java class
 */
public class Coin extends WorldObject {

    //----------------------------------------------------------------------------Atributos de la clase--------------------------------------------------------------------------------------------
    
    /* Permite que todos los objetos de esta misma clase
	 * puedan pasar de una imagen a otra todos sincronizados.
     */
    protected static int indexClass = 0;

    /* Indica que es necesario actualizar el indexClass. */
    protected static boolean changeImg = false;

    /* Numero de imagenes que representan al sprite. */
    public final static int LENGHT_IMAGES = 4;

      /* Cuenta el numero de monedas que se han creado. */
    public static int N_COINS = 0;

    /* Cuenta las monedas cogidas. */
    public static int COINS_CATCHED = 0;

    public String imgNormal = "coin*_0";

    public String imgAnimation = "coinEfect*_0";

    // Indica si la moneda esta reproduciendo
    // el efecto que le va a hacer desaparecer.
    public boolean effect = false;

    //Variables de audio de la coin
    @SuppressWarnings("removal")
	public static AudioClip[] audio;
    public static int indexAudio;
    public static boolean first = true;

    //----------------------------------------------------------------------------------------Constructor de la clase------------------------------------------------------------------------
    @SuppressWarnings("removal")
	public Coin(Stage s) {
        super(s);
        setPreferredSize(map.tileXSize, map.tileYSize);
        setImages(imgNormal, 0, LENGHT_IMAGES);
        // Rectangulo para las colisiones
        bounds.add(new java.awt.Rectangle(2, 2, width - 4, height - 4));
        N_COINS++;
        if (first) {
            first = false;
            indexAudio = 0;
            audio = new AudioClip[5];
            for (int i = 0; i < audio.length; i++) {
                audio[i] = stage.getSoundsLoader().returnAudio("coin.wav", true, true);
            }
        }
    }

    //------------------------------------------------------------------------------------Metodos de la clase-----------------------------------------------------------------------------

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
     * El método llama al método move para mover el objeto y luego verifica el valor de la variable effect. 
     * Si effect es verdadero, el método calcula la frecuencia de fotogramas y verifica si es cero o si el número total de actualizaciones del 
     * escenario es divisible por la frecuencia de fotogramas. Si se cumple alguna de estas condiciones, se llama al método nextImg y, si devuelve verdadero, 
     * se establece la variable delete en verdadero y se incrementa el contador de monedas recolectadas. Si effect es falso, el método calcula la frecuencia de fotogramas 
     * y verifica si es cero o si el número total de actualizaciones del escenario es divisible por la frecuencia de fotogramas. Si se cumple alguna de estas condiciones, 
     * se llama al método setImage para establecer la imagen del objeto y se establece la variable changeImg en verdadero.
     */
    @Override
    public void act() {
        move();
        if (effect) {
            int frameFrec = (int) (stage.getFPS() / 20);
            if (frameFrec == 0 || stage.getTotalUpdates() % frameFrec == 0) {
                if (nextImg()) {
                    delete = true;
                    COINS_CATCHED++;
                }
            }
        } else {
            int frameFrec = (int) (stage.getFPS() / 10);
            if (frameFrec == 0 || stage.getTotalUpdates() % frameFrec == 0) {
                setImage(indexClass);
                changeImg = true;
            }
        }
    }
  
    /*
     * Maneja las colisiones entre un objeto y otro objeto Sprite. 
     * El método está anotado con @Override, lo que indica que sobrescribe un método de una clase padre.
     * El método verifica si la variable effect es falsa y, de ser así, llama al método play del objeto SoundsLoader del escenario para reproducir un sonido. 
     * Luego, el método reproduce un audio de la matriz audio y incrementa el valor del índice de audio en uno. 
     * Después, el método llama al método setImages para establecer las imágenes del objeto y establece la variable effect en verdadero. 
     * Finalmente, el método establece la velocidad en el eje Y del objeto en 2.
     */
      @SuppressWarnings("removal")
      
	@Override
    public void collision(Sprite s) {
        // prueba la diferencia entre
        // utilizar un mismo sonido
        // y utilizar copias de ese sonido
        if (!effect) {
            stage.getSoundsLoader().play("coin.wav", false);
            audio[indexAudio].play();
            indexAudio = (indexAudio + 1) % audio.length;
            setImages(imgAnimation, 0, 7);
            effect = true;
            speed.setY(2);
        }
    }
   
}  


