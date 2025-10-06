package com.mariobros.objectsloaders;

import java.awt.*;
import java.awt.image.*;
import java.util.*;
import java.io.*;
import javax.imageio.*;

/*
 * ImagesLoader.java class
 */
public class ImagesLoader extends Loader {

    //-------------------------------------------------------------------Variable de la clase--------------------------------------------------------------------------------

    //Variable para crear imagenes compatibles con el entorno de desarrollo
    private GraphicsConfiguration gc;


    //------------------------------------------------------------------------------Constructor de la clase-----------------------------------------------------------------------------------------

    public ImagesLoader(String path, String loader) {

        super(path, loader);
        loaded = new HashMap<String, Object>();
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        gc = ge.getDefaultScreenDevice().getDefaultConfiguration();
    }  
   //-----------------------------------------------------------------------------Metodos de la clase --------------------------------------------------------------------------------------------


//	 Analiza linea por linea de texto que contiene informacion a cargar.
    @Override

    public void loadLine(String line) {

        super.loadLine(line);
        if (line.startsWith("3 ")) {
            loadSprites(line);
        }
    }

    /*
    *Toma como parámetro un String que representa una línea de texto con información sobre los sprites a cargar.
    *El método comienza creando un objeto StringTokenizer para dividir la línea de texto en tokens utilizando el espacio como delimitador. 
    *Luego, verifica si hay al menos 4 tokens en la línea de texto. Si es así, el método ignora el primer token y obtiene el segundo token, 
    *que representa el nombre del sprite o el nombre del archivo del sprite.
    
    *A continuación, el método busca el carácter = en el nombre del sprite o del archivo. 
    *Si no encuentra el carácter =, asume que el nombre del sprite es igual al nombre del archivo y llama al método load de la clase padre pasándole como parámetro el nombre del sprite. 
    *Si encuentra el carácter =, separa el nombre del sprite y el nombre del archivo utilizando el carácter = como delimitador y llama al método load de la clase padre pasándole como 
    *parámetros el nombre del archivo y el nombre del sprite.

    *Después, el método obtiene la imagen del sprite utilizando el método getImage y calcula el ancho y alto de cada cuadro del sprite dividiendo el ancho y alto de la imagen entre el número 
    *de columnas y filas especificadas en la línea de texto. También obtiene la transparencia de la imagen utilizando el método getTransparency del modelo de color de la imagen.
    *A continuación, el método recorre cada fila y columna del sprite y crea una nueva imagen compatible con la transparencia especificada utilizando el método createCompatibleImage del objeto gc. 
    *Luego, obtiene un objeto Graphics a partir de la nueva imagen y utiliza este objeto para dibujar una parte de la imagen original en la nueva imagen.
    *Después, verifica si hay más tokens en la línea de texto y si alguno de ellos comienza con las coordenadas actuales (i, j). 
    
    *Si es así, guarda la nueva imagen en un mapa utilizando como clave el nombre especificado después del carácter = en el token. Si no hay más tokens o ninguno comienza con 
    las coordenadas actuales, guarda la nueva imagen en un mapa utilizando como clave el nombre del sprite seguido de las coordenadas actuales (i, j).
    *Finalmente, libera los recursos del objeto Graphics utilizando el método dispose y elimina la imagen original del mapa utilizando el método removeObject
     */
    private boolean loadSprites(String line) {

        boolean error = false;
        StringTokenizer st = new StringTokenizer(line, " ");

        if (st.countTokens() >= 4) {
            st.nextToken();
            String name = st.nextToken();
            String nameFile = "";
            int equals = name.indexOf("=");
            int row = 0, col = 0;

            try {
                row = Integer.parseInt(st.nextToken());
                col = Integer.parseInt(st.nextToken());
            } catch (NumberFormatException e) {
                error = true;
                e.printStackTrace();
            }

            if (!error) {
                if (equals == -1) { 
                    if (!super.load(name)) {
                        return false;
                    }
                } else { 
                    nameFile = name.substring(equals + 1);
                    name = name.substring(0, equals);
                    if (!super.load(nameFile, name)) {
                        return false;
                    }
                }
                BufferedImage bi = getImage(name);
                int w = (int) bi.getWidth() / col;
                int h = bi.getHeight() / row;
                int transparency = bi.getColorModel().getTransparency();

                BufferedImage img;
                Graphics g;
                String nextName = null;
                equals = -1;

                for (int i = 0; i < row; i++) {
                    for (int j = 0; j < col; j++) {
                        img = gc.createCompatibleImage(w, h, transparency);
                        g = img.getGraphics();
                        g.drawImage(bi, 0, 0, w, h,j * w, i * h, (j * w) + w, (i * h) + h, null);
                        if (nextName == null) {
                            nextName = "";
                            while (st.hasMoreTokens()) {
                                nextName = st.nextToken();
                                equals = nextName.indexOf("=");
                                if (equals != -1) {
                                    break;
                                }
                            }
                        }
                        if (nextName.startsWith(i + "_" + j)) {
                            loaded.put(nextName.substring(equals + 1), img);
                           nextName = null;
                        } else {
                            loaded.put(name + i + "_" + j, img);
                        }
                        g.dispose();
                
                    }
                }
                removeObject(name);
            }
        } else {
            // Si no hay argumentos
            error = true;
        }
        if (error) {
            System.err.println("Error en la linea de formato " + line);
        }
        return error;
    }  


    /*
     * Este método carga una imagen desde un archivo y la guarda en un mapa con un nombre especificado.
     */
    @Override

    public boolean load(File f, String name, boolean rewrite) {

        if (name == null) {
            name = f.getName();
        }
        if (!rewrite && loaded.containsKey(name)) {
            return false;
        }

        try {
            BufferedImage bi = ImageIO.read(f);
            int transparency = bi.getColorModel().getTransparency();
            BufferedImage img = gc.createCompatibleImage(bi.getWidth(), bi.getHeight(), transparency);

            Graphics2D g = img.createGraphics();
            g.drawImage(bi, 0, 0, null);
            g.dispose();
            loaded.put(name, img);
        } catch (IOException e) {
            System.err.println("Error cargando imagen "+ f.getPath());
            e.printStackTrace();
            return false;
        }
       
        return true;
    }  

    //-----------------------------------------Metodos Get-----------------------------------------------------

//	  Retorna una previa imagen cargada.
    public BufferedImage getImage(String name) {
        return (BufferedImage) loaded.get(name);
    }

//	  Devuelve una imagen. Si <code>load</code> es
//	  <code>true</code> y si es necesario se carga.
    public BufferedImage getImage(String name, boolean load, boolean rewrite) {
        Object o = super.getObject(name, load, rewrite);
        if (o == null) {
            return null;
        }
        return (BufferedImage) o;
    }  
    
//	  Retorna el valor por default de GraphicsConfiguration
//	  Para crear imagenes compatibles.
  public GraphicsConfiguration getGraphicsConfiguration() {

      return gc;
  }

}  

