package com.mariobros.creditos;

import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import java.io.File;
import java.awt.Font;
import java.awt.FontFormatException;
import java.io.IOException;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;

public class PruebaCapas {
    public static void main(String[] args) {
        Pantalla panta = new Pantalla();
        panta.setSize(810, 508); // Establece las dimensiones deseadas
        panta.setVisible(true);
        panta.setLocationRelativeTo(null);

        // Ruta del archivo de la canción
        String rutaCancion = "YOASOBI Seventeen Instrumental.wav";

        // Ruta del archivo de la fuente
        String rutaFuente = "Minecraft.ttf";

        try {
            // Cargar el archivo de la canción
            File archivoCancion = new File(rutaCancion);
            Clip clip = AudioSystem.getClip();
            clip.open(AudioSystem.getAudioInputStream(archivoCancion));

            // Reproducir la canción
            clip.start();

            // Temporizador de 30 segundos
            Thread.sleep(30000);

            // Cierra automáticamente después de 30 segundos
            panta.dispose();

            // Detener y cerrar el clip después de 30 segundos
            clip.stop();
            clip.close();

            // Cargar la fuente desde el archivo
            Font font = Font.createFont(Font.TRUETYPE_FONT, new File(rutaFuente));

            // Establecer la fuente a utilizar en la Pantalla
            panta.setFont(font);

        } catch (IOException | LineUnavailableException | UnsupportedAudioFileException | FontFormatException
                | InterruptedException e) {
            e.printStackTrace();
        }
    }
}
