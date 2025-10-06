package com.mariobros.objects;

import java.awt.event.*;
import java.awt.*;

/**
 *Mario.java class
 */
public class Mario extends Player {

    //----------------------------------------------------------------------Constructor de la clase------------------------------------------------------------------
    public Mario(Stage s) {
        super(s);
        // Tamaño predeterminado del jugador.
        setPreferredSize(map.tileXSize, map.tileYSize);
        // Rectangulo para las colisiones
        bounds.add(new Rectangle(6, 1, width - 12, height - 1));
    }

    //-----------------------------------------------------------------------Metodos de la clase----------------------------------------------------------------------------

    
	/* Asignacion de las teclas que controlan al jugador. */
    public void createKeys() {
        keyLeft = KeyEvent.VK_LEFT;
        keyRight = KeyEvent.VK_RIGHT;
        keyRun = KeyEvent.VK_CONTROL;
        keyJump = KeyEvent.VK_UP;
        keyCrouch = KeyEvent.VK_DOWN;
    }

    /* Asignacion de las imagenes que representan al jugador. */
    public void createImgs() {
       
        imgLStop ="marioLStop";
        imgRStop = "marioRStop";
        imgLJump = "marioLJump";
        imgRJump = "marioRJump";
        imgLFall = "marioLJump";
        imgRFall = "marioRJump";
        imgRight = "marioRWalk*";
        imgLeft = "marioLWalk*";
        imgRSlip = "marioRSlip";
        imgLSlip = "marioLSlip";
        imgRCrouch = "marioRCrouch";
        imgLCrouch = "marioLCrouch";
    }

}  


