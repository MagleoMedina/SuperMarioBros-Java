package com.mariobros.objectsloaders;

import java.applet.*;
import java.net.*;
import java.io.*;
import java.util.*;

/**
 * SoundsLoader.java class
 */
public class SoundsLoader extends Loader {

//-------------------------------------------------------------------------Variables de la clase-----------------------------------------------------------------------------------------------

//	  Almacena todos los Clips de audio que se están reproduciendo
    public ArrayList<String> reproducir;


//----------------------------------------------------------------------------Constructor de la clase------------------------------------------------------------------------------------------
     public SoundsLoader(String path, String loader) {
        super(path, loader);
        loaded = new HashMap<String, Object>();
        reproducir = new ArrayList<String>();
    }

    //----------------------------------------------------------------------Metodos de la clase-----------------------------------------------------------------------------------------------
   /*
    * Este método carga un archivo de audio y lo guarda en un mapa con un nombre especificado. 
    */
   
    @Override

    public boolean load(File f, String name, boolean sobreescritura) {
        if (name == null) {
            name = f.getName();
        }
        if (!sobreescritura && loaded.containsKey(name)) {
            return false;
        }
        try {
            URL url = f.toURI().toURL();
            @SuppressWarnings({ "removal" })
			AudioClip a = Applet.newAudioClip(url);
            loaded.put(name, a);
        } catch (Exception e) {
            System.err.println("Error cargando sonido "+ name + " desde " + f.getPath());
            e.printStackTrace();
            return false;
        }
      
        return true;
    }

    /**
     * Reproduce un sonido cargado previamente.
     * 
     */
   
	@SuppressWarnings({ "removal" })
	public void play(String name, boolean loop) {
        if (loop) {
        	 
            ((AudioClip) loaded.get(name)).loop();
            reproducir.add(name);
            return;
        }
        ((AudioClip) loaded.get(name)).play();
    }




    /**
     * Retorna una imagen si <code>load</code> es <code>true</code> y si es
     * necesario es cargado.
     */
    @SuppressWarnings("removal")
	public AudioClip returnAudio(String name, boolean load, boolean sobreescritura) {
        Object o = super.getObject(name, load, sobreescritura);
        if (o == null) {
            return null;
        }
        return (AudioClip) o;
    }

    //Detiene el clip de audio
    @SuppressWarnings({  "removal" })
	public void stop() {
    for (String name : reproducir) {
        ((AudioClip) loaded.get(name)).stop();
    }
    reproducir.clear();
}
    //Metodo get
    
    @SuppressWarnings("removal")
	public AudioClip getAudio(String name) {
        return (AudioClip) loaded.get(name);
    }


}  

