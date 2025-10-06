package com.mariobros.objects;

/**
 * Representa un vector matematico sobre los ejes X e Y (2 dimensiones). Es de gran utilidad para representar el vector velocidad de objetos o personajes en toda clase de juegos. 
 * Hay que tener en cuenta la diferencia que existe entre los ejes matematicos y los ejes graficos de la pantalla. El eje X es igual para los dos, pero, en el eje Y hay diferencias.
 *  En el caso matematico el eje Y aumenta conforme subimos en la grafica, es decir todo lo contrario a el eje grafico de la pantalla. El Vector2D utiliza un eje matematico.</ br>
 * Por ejemplo, un vector con las coordenadas (2,3) se encuentra en el 1er cuadrante, por lo que deberia ir arriba a la derecha, pero sobre la pantalla
 * ese vector se encuentra en el 4to cuadrante, abajo a la izquierda.
 */

 /*
  * Vector2D.java Class
  */

public class Vector2D {

    //----------------------------------------------------------------Variables de la clase-------------------------------------------------------------------------------
    protected double x, y, module, angulo;


    //-----------------------------------------------------------------Constructores de la clase-----------------------------------------------------------------------------------------
    
    /*
     * Constructor vacio con todos sus parametros en 0
     * Module = 0;</ br>
     * angulo = 0;</ br>
     * X = 0;</ br>
     * Y = 0;</ br>
     */
    public Vector2D() {
        this(0, 0);
    }


    /*
     * Vector2D con un modulo y un angulo especificado.
     * Las coordenadas son obtenidas por trigonometria.
     * X = cos(angulo)*modulo;</ br>
     * Y = sen(angulo)*modulo;</ br>
     */
    public Vector2D(double module, double angulo) {
        this.setModule(module);
        this.setangulo(angulo);
    }

    /*
     * Crea un nuevo Vector2D a partir de uno ya existente. Se puede decir que
     * crea una copia de este.
     */
    public Vector2D(Vector2D vector) {
        this.module = vector.getModule();
        this.angulo = vector.getangulo();
        this.x = vector.getAccurateX();
        this.y = vector.getAccurateY();
    }

//--------------------------------------------------------------------------Metodos de la clase---------------------------------------------------------------------------------------------------------------------
    /*
     * Invierte el eje X y realiza los cambios oportunos en el �ngulo.
     */
    public void invertX() {
        if (Math.abs(angulo) > Math.PI / 2) {
            angulo -= Math.PI / 2;
        } else {
            angulo += Math.PI / 2;
        }
        x = -x;
    }

    /*
     * Invierte el eje Y, por lo tanto tambien se invierte en angulo.
     */
    public void invertY() {
        angulo = -angulo;
        y = -y;
    }
    

    /*
     * Actualiza el eje X y el eje  Y trigonometrico.
	 * Usado cuando el modulo o el angulo fueron cambiados.
     */
    private void updateXY() {
        x = Math.cos(angulo) * module;
        y = Math.sin(angulo) * module;
    }

    /* Actualiza el modulo por el teorema de pitagoras.
	 * 
     */
    private void updateModule() {
        //Pitagoras
        module = Math.sqrt(x * x + y * y);
    }

    /* 
     *Actualiza el angulo usando trogonometria.
	 *Se usa cuando X o Y on cambiados por setX(double) o setY(double).
     */
    private void updateangulo() {
        // trigonometria
        if (x == 0 && y == 0) {
            angulo = 0;
        } else if (x == 0) {
            angulo = (y > 0) ? Math.PI / 2 : -Math.PI / 2;
        } else if (y == 0) {
            angulo = (x > 0) ? 0 : Math.PI;
        } else {
            angulo = Math.atan(y / x);
        }
    }


    //--------------------------------------------------------------------Metodos Set---------------------------------------------------------------------------------------------------------
    public void setModule(double module) {
        this.module = module;
        updateXY();
    }

    /**
     * Cambia el angulo del vector y actualiza las coordenadas para que el
     * modulo siga siendo el mismo.
     *
     * @param angulo El angulo en radianes
     */
    public void setangulo(double angulo) {
        this.angulo = angulo % (2 * Math.PI);
        if (this.angulo > Math.PI) {
            this.angulo = -angulo % Math.PI;
        } else if (this.angulo < -Math.PI) {
            this.angulo = 2 * Math.PI + angulo;
        } else if (this.angulo < 0) {
            this.angulo = angulo % Math.PI;
        }
        updateXY();
    }

    /**
     * Cambia el angulo utilizando grados.
     *
     * @param angulo El angulo en grados.
     */
    public void setangulo(int angulo) {
        this.setangulo(Math.toRadians(angulo));
    }

    public void setX(double x) {
        this.x = x;
        updateModule();
        updateangulo();
    }

    public void setY(double y) {
        this.y = y;
        updateModule();
        updateangulo();
    }
    //--------------------------------------------------------------------Metodos get----------------------------------------------------------------------------------------------------------
    public int getX() {
        return ((int) x);
    }

    public int getY() {
        return ((int) y);
    }

    public double getAccurateX() {
        return x;
    }

    public double getAccurateY() {
        return y;
    }

    public double getangulo() {
        return angulo;
    }

    public double getModule() {
        return module;
    }
    
}  


