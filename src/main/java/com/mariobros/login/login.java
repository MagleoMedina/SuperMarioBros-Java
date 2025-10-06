package com.mariobros.login;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.IOException;

public class login extends JFrame {

    private JTextField jTextField2;
    private JTextField jTextField3;
    private JPanel jPanel2;
    private JPanel jPanel3;
    private JLabel jLabel2;
    private JLabel jLabel3;

    public login() {
        initComponents();
    }

    private void initComponents() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);
        setTitle("Login");
        getContentPane().setLayout(null);

        jTextField2 = new JTextField("Escribe aqui");
        jTextField2.setBackground(new Color(153, 204, 255));
        jTextField2.setFont(new Font("Press Start 2P", Font.PLAIN, 12));
        jTextField2.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent evt) {
                jTextField2MousePressed(evt);
            }
        });
        jTextField2.setBounds(450, 350, 170, 40);
        getContentPane().add(jTextField2);

        jTextField3 = new JTextField("Escribe aqui");
        jTextField3.setBackground(new Color(153, 204, 255));
        jTextField3.setFont(new Font("Press Start 2P", Font.PLAIN, 12));
        jTextField3.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent evt) {
                jTextField3MousePressed(evt);
            }
        });
        jTextField3.setBounds(70, 350, 170, 40);
        getContentPane().add(jTextField3);

        jPanel2 = new JPanel();
        jPanel2.setBackground(new Color(255, 204, 102));
        jPanel2.setBounds(250, 450, 190, 40);

        jLabel2 = new JLabel("COMENZAR!");
        jLabel2.setFont(new Font("Press Start 2P", Font.PLAIN, 20));
        jLabel2.setCursor(new Cursor(Cursor.HAND_CURSOR));
        jLabel2.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent evt) {
                jLabel2MouseEntered(evt);
            }

            public void mouseExited(MouseEvent evt) {
                jLabel2MouseExited(evt);
            }

            public void mousePressed(MouseEvent evt) {
                jLabel2MousePressed(evt);
            }
        });
        jPanel2.add(jLabel2);
        getContentPane().add(jPanel2);

        jPanel3 = new JPanel();
        jPanel3.setBackground(new Color(153, 204, 255));
        jPanel3.setBounds(560, 490, 130, 30);
        jLabel3 = new JLabel("by: M.A.J.C");
        jLabel3.setFont(new Font("Press Start 2P", Font.PLAIN, 10));
        jLabel3.setForeground(new Color(153, 204, 255));
        jPanel3.add(jLabel3);
        jPanel3.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent evt) {
                jPanel3MouseEntered(evt);
            }

            public void mouseExited(MouseEvent evt) {
                jPanel3MouseExited(evt);
            }
        });
        getContentPane().add(jPanel3);

        // Cargar la imagen de fondo desde el recurso /ima/OG.png
        ImageIcon imageIcon = null;
        try {
            imageIcon = new ImageIcon(getClass().getResource("OG.png"));
        } catch (Exception e) {
            System.err.println("No se pudo cargar la imagen de fondo: OG.png");
        }
        JLabel backgroundLabel = new JLabel(imageIcon);
        backgroundLabel.setBounds(0, 0, 700, 533);
        getContentPane().add(backgroundLabel);

        // Establecer el tamaño de la ventana
        setSize(716, 572);
        // Centrar la ventana en la pantalla
        setLocationRelativeTo(null);
    }

    private void jLabel2MousePressed(MouseEvent evt) {
        dispose();
    }

    private void jLabel2MouseEntered(MouseEvent evt) {
        jPanel2.setBackground(Color.WHITE);
        jLabel2.setForeground(new Color(255, 204, 102));
    }

    private void jLabel2MouseExited(MouseEvent evt) {
        jPanel2.setBackground(new Color(255, 204, 102));
        jLabel2.setForeground(Color.BLACK);
    }

    private void jTextField3MousePressed(MouseEvent evt) {
        if (jTextField3.getText().equals("Escribe aqui")) {
            jTextField3.setText("");
        }
        if (String.valueOf(jTextField2.getText()).isEmpty()) {
            jTextField2.setText("Escribe aqui");
        }
    }

    private void jTextField2MousePressed(MouseEvent evt) {
        if (jTextField2.getText().equals("Escribe aqui")) {
            jTextField2.setText("");
        }
        if (jTextField3.getText().isEmpty()) {
            jTextField3.setText("Escribe aqui");
        }
    }

    private void jPanel3MouseEntered(MouseEvent evt) {
        jPanel3.setBackground(new Color(255, 204, 102));
        jLabel3.setForeground(Color.BLACK);
    }

    private void jPanel3MouseExited(MouseEvent evt) {
        jPanel3.setBackground(new Color(153, 204, 255));
        jLabel3.setForeground(new Color(153, 204, 255));
    }




    public static void playBackgroundMusic(String rutaCancion, int duracion) {
        try {
            // Obtener la cancion a reproducir
            File archivoCancion = new File(rutaCancion);
            if (!archivoCancion.exists()) {
                System.err.println("Archivo de audio no encontrado: " + rutaCancion);
                return;
            }
    
            // Abrir y reproducir el archivo de audio
            AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(archivoCancion);
            Clip clip = AudioSystem.getClip();
            clip.open(audioInputStream);
            clip.start();
    
            // Crear un hilo para controlar la reproducción
            Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        // Pausar la ejecución durante la duración especificada
                        Thread.sleep(duracion);
    
                        // Detener la reproducción
                        clip.stop();
                        clip.close();
                    } catch (InterruptedException e) {
                        System.err.println("Error al detener la reproducción: " + e.getMessage());
                    }
                }
            });
    
            // Iniciar el hilo
            thread.start();
    
        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
            System.err.println("Error al cargar o reproducir la canción: " + e.getMessage());
        }
    }
    
      
}
