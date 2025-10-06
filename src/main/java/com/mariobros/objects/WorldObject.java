package com.mariobros.objects;

/*
 * WorldObject.java Class
 */
public class WorldObject extends Sprite {

    //--------------------------------------------------------------------------------Variables de la clase-------------------------------------------------------------------------------

    // Indica que no hay suelo.
    public static final int NOT_FLOOR = 1000000000;

    /* Indica si es capaz de sostener a un jugador
	 * encima del Sprite.
     */
    protected boolean supportsPlayer;

    /* Referencia a el mapa a el cual pertenece el
	 * objeto. */
    protected Map map;

    //------------------------------------------------------------------------------Constructores de la clase------------------------------------------------------------------- 
  
   
    public WorldObject(Stage s) {
        super(s);
        map = ((MainStage) s).getCurrentMap();
        supportsPlayer = false;
    }

    // --------------------------------------------------------------------------Metodo Get ----------------------------------------------------------------------------------
    public int getFloor() {
        if (supportsPlayer) {
            return (int) y;
        }
        return NOT_FLOOR;
    }
       public boolean isSupportsPlayer() {
        return supportsPlayer;
    }

    // -----------------------------------------------------------------------------Metodo Set -------------------------------------------------------------------------------
    public void setSupportPlayer(boolean support) {
        this.supportsPlayer = false;
    }


 

}  


