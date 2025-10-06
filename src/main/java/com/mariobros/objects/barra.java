package com.mariobros.objects;

import com.mariobros.menudeayuda.presentacion;

public class barra {

    public static void main(String[] args) {
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
}
