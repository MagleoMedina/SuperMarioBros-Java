package com.mariobros.menudeayuda;
import java.awt.Dimension;
import javax.swing.BoundedRangeModel;
import javax.swing.DefaultBoundedRangeModel;

public class presentacion extends javax.swing.JFrame {

    private final BoundedRangeModel rangeModel;

    public presentacion() {
        initComponents();
        setWindowSize(810, 670);

        rangeModel = new DefaultBoundedRangeModel(0, 0, 0, 100);
        barra.setModel(rangeModel);
        barra.setStringPainted(true); // Mostrar el porcentaje en la barra de carga
        barra.setFont(barra.getFont().deriveFont(20f)); // Aumentar el tamaño de los numeros a 20

        setResizable(false); // Evitar que la ventana se pueda redimensionar
    }

    // Método para establecer el tamaño de la ventana
    private void setWindowSize(int width, int height) {
        this.setPreferredSize(new Dimension(width, height));
        this.pack();
        this.setLocationRelativeTo(null);
    }

    // Code>//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        barra = new javax.swing.JProgressBar();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jPanel1.setBackground(new java.awt.Color(51, 51, 255));
        jPanel1.setLayout(new javax.swing.BoxLayout(jPanel1, javax.swing.BoxLayout.PAGE_AXIS));

        jLabel2.setIcon(new javax.swing.ImageIcon(getClass().getResource("MENU DE AYUDA C3.png"))); // NOI18N
        jLabel2.setText("jLabel2");
        jLabel2.setAlignmentX(java.awt.Component.CENTER_ALIGNMENT);
        jPanel1.add(jLabel2);

        barra.setBackground(new java.awt.Color(255, 204, 0));
        barra.setForeground(new java.awt.Color(255, 51, 51));
        barra.setAlignmentX(java.awt.Component.CENTER_ALIGNMENT);
        jPanel1.add(barra);

        getContentPane().add(jPanel1, java.awt.BorderLayout.CENTER);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    /**
     * @param args the command line arguments
     */

    // Variables declaration - do not modify//GEN-BEGIN:variables
    public static javax.swing.JProgressBar barra;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JPanel jPanel1;
    // End of variables declaration//GEN-END:variables
}
