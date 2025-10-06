package com.mariobros;

import com.mariobros.login.login;
import com.mariobros.menudeayuda.presentacion;
import com.mariobros.objects.MainStage;

public class App {
  private static boolean loginClosed = true;

    //Inicializa el Login para despues activarlo
    public static boolean loginGame() {
        login frame = new login();
        frame.setVisible(true);

        // Ruta del archivo de la canción "Theme_Wii_Mario.wav"
        String rutaCancion = "src/main/java/com/mariobros/login/Theme Wii Mario.wav";

        // Reproducir la canción desde el main
        login.playBackgroundMusic(rutaCancion,35000);

        // Agregar WindowListener para detener la música al cerrar la ventana de login
        frame.addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosed(java.awt.event.WindowEvent windowEvent) {
                loginClosed = true;
                
               
            }
            
        });
        // Esperar a que se cierre la ventana de login
        while (frame.isVisible()) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        // Devolver true si se cerró la ventana de login
        return !frame.isVisible();
    }

//Inicializa el menu de ayuda
    public static void menuDeAyuda() {
        presentacion pre = new presentacion();
        pre.setVisible(true);

        try {
            for (int i = 0; i <= 100; i++) {
                Thread.sleep(200);
                presentacion.barra.setValue(i);
                pre.repaint(); // Actualiza el frame para mostrar los cambios inmediatamente
            }
        } catch (Exception e) {
        }
        pre.dispose();
    }

    //Inicia el juego como tal
    public static void startMarioGame() {
        MainStage p = new MainStage();
        p.getWindow().setVisible(true);
        p.startGame();
    }

      //Arranca el Login
    public static void  intro(){
        loginClosed = loginGame();

        // Verificar si la ventana de login se cerró correctamente
        if (loginClosed) {
            // Se Agrega un separador de 3 segundos antes de mostrar el menú de ayuda
            try {
                Thread.sleep(3000);
                menuDeAyuda();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            
        }
        

    }

    public static void main(String[] args) {
      
       //intro();
        //YSURI
        startMarioGame();
    }
}
