package com.mariobros.objects;

import java.awt.Rectangle;
import java.awt.event.KeyEvent;

//Luigi.java class

public class Luigi extends Player {

    //-----------------------------------------------------------------Constructor de la Clase------------------------------------------------------------------------------------------
    public Luigi(Stage s) {
        super(s);
        // Tamaño predeterminado del jugador.
        setPreferredSize(map.tileXSize, map.tileYSize);
        // Rectangulo para las colisiones
        bounds.add(new Rectangle(6, 1, width - 12, height - 1));
    }

    //-------------------------------------------------------------------------------Metodos de la clase------------------------------------------------------------------------------------------------------------
    /* Asignacion de las teclas que controlan al jugador. */
    public void createKeys() {
        keyLeft = KeyEvent.VK_A;
        keyRight = KeyEvent.VK_D;
        keyRun = KeyEvent.VK_SHIFT;
        keyJump = KeyEvent.VK_W;
        keyCrouch = KeyEvent.VK_S;
    }


    /* Asignacion de las imagenes que representan al jugador. */
    public void createImgs() {

        imgLStop = "luigiLStop";
        imgRStop = "luigiRStop";
        imgLJump = "luigiLJump";
        imgRJump = "luigiRJump";
        imgLFall = "luigiLJump";
        imgRFall = "luigiRJump";
        imgRight = "luigiRWalk*";
        imgLeft = "luigiLWalk*";
        imgRSlip = "luigiRSlip";
        imgLSlip = "luigiLSlip";
        imgRCrouch = "luigiRCrouch";
        imgLCrouch = "luigiLCrouch";
    }

}
