package com.mariobros.objectsloaders;

import java.util.*;
import java.io.*;

// Loader.java class

public abstract class Loader {

    //------------------------------------------------------------------------------------Atributos de la clase--------------------------------------------------------------------

//	 Lista de extensiones de archivo válidas que se pueden cargar.
// 	 Las cadenas no distinguen entre mayúsculas y minúsculas. El punto '.' 
//	 No es permitido. La cadena vacía "" permite cualquier extensión de archivo.
    public static ArrayList<String> extensionesValidas;

    //Ruta del directorio del paquete.
    private String packageDirectory;

//	Camino donde están los recursos.
//	Relativo al directorio del paquete.
    private String relativePath;

//	 Archivo que contiene los archivos a cargar.
    private File loader;

//	Almacena todos los Objetos cargados.
    protected HashMap<String, Object> loaded;


//----------------------------------------------------------------------------------Constructor De la  Clase------------------------------------------------------------------------------------

    public Loader(String path, String loader) {
        extensionesValidas = new ArrayList<String>();
        extensionesValidas.add("");

        // Obtenemos el directorio de una forma enrevesada
        // para poder utilizar el cargador en Apples.
        packageDirectory = getClass().getClassLoader().getResource("").getPath();
        int binIndex = packageDirectory.lastIndexOf("bin/");
        if (binIndex != -1) {
            packageDirectory = packageDirectory.substring(0, binIndex);
        }
        System.out.println(packageDirectory);
        setPath(path);
        setLoader(loader);


    } 

    //------------------------------------------------------------------------------Metodos de la clase----------------------------------------------------------------------------------

    public boolean startLoader() {
        if (loader != null && loader.exists()) {
            if (loader.isDirectory()) {
//            Carga todas las imagenes validas en el directorio.
                return loadDirectory(loader);
            } else if (loader.isFile()) {
                if (loader.canRead()) {
                    return readLoaderFile();
                }
            }
        }
        return false;
    }  

    /*
     * Este método lee el contenido de un archivo y procesa cada línea utilizando el método loadLine
     */
    private boolean readLoaderFile() {
        BufferedReader br;
        try {

            InputStream is = new FileInputStream(loader);
            InputStreamReader isr = new InputStreamReader(is);
            br = new BufferedReader(isr);
            String line;
            while ((line = br.readLine()) != null) {
                line = line.trim();
//               En caso de que sea una linea o comentario vacio
                if (line.length() == 0 || line.startsWith("//")) {
                    continue;
                } // else... analiza la linea y la carga
                loadLine(line);
            }
            br.close();
        } catch (IOException e) {
            System.err.println("Error al leer el archivo loader:\n"+ loader.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return true;
    }

//		Analiza una linea de texto que contiene una informacion cargada
//	  	@param .línea Información para cargar una o mas  imagenes

    public void loadLine(String line) {
        if (line.startsWith("1 ")) {
            loadSingleFile(line);
        } else if (line.startsWith("2 ")) {
            loadNumeratedFiles(line);
        }
    }

    /*
     * Este método carga un archivo a partir de una línea de texto con información sobre su nombre y ubicación. 
     */
    private void loadSingleFile(String line) {
        int equals = line.indexOf("=");
        if (equals == -1) {
            load(line.substring(2));
        } else {
            load(line.substring(equals + 1),line.substring(2, equals));
        }
    }

    /*
     * Este método carga varios archivos a partir de una línea de texto con información sobre sus nombres y ubicación.
     */
    private void loadNumeratedFiles(String line) {
        boolean error = false;
        StringTokenizer st = new StringTokenizer(line, " ");
        int tokens = st.countTokens();
        st.nextToken();

        String fileName = st.nextToken();
        String loadedName = null;
        int equals = fileName.indexOf("=");
        if (equals != -1) {
            loadedName = fileName.substring(0, equals);
            fileName = fileName.substring(equals + 1);
        }
        int wildcard = fileName.indexOf("*");
        if (wildcard != -1) {
            int i = 0;
// 			almacena los diferentes nombres enumerados
            String fullName;
// 			para diferente número de argumentos
            if (tokens == 2) {
                do {
                    fullName = fileName.substring(0, wildcard) + (i++)  + fileName.substring(wildcard + 1);
                } while (load(fullName, loadedName + (i - 1)));
            } else if (tokens >= 3) {
                int numFiles = 0;
                try {
                    numFiles = Integer.parseInt(st.nextToken());
                    if (tokens == 4) {
                        i = Integer.parseInt(st.nextToken());
                        numFiles += i;
                    }
                } catch (NumberFormatException e) {
                    error = true;
                    e.printStackTrace();
                }
                while (i < numFiles) {
                    fullName
                            = fileName.substring(0, wildcard) + (i++)
                            + fileName.substring(wildcard + 1);
                    load(fullName, loadedName);
                    if (!load(fullName)) break;
                }
            }
        } else {
            error = true;
        }
        if (error) {
            System.err.println("Error de formato en la linea: " + line);
        }
    }

//	Carga de  todas las imágenes del directorio.
//
//	@param d representa el Directorio con las imágenes que deben
//	ser cargadas.
    public boolean loadDirectory(File d) {
        if (!d.isDirectory()) {
            return false;
        }

        File[] f = d.listFiles();
        for (File file : f) {
            if (file.isFile()
                    && hasValidExtension(file)) {
                load(file);
            }
        }
        return true;
    }  //  fin de  loadDirectory(File);


    public boolean load(File f) {
        return load(f, f.getName(), false);
    }  

        public boolean load(String n) {
        return load(n, n);
    }  

//		Debe implementar este método si  se desea cargar
//		cualquier Objeto. Para ello se agrega el objeto en el
//		<code>cargado:ArrayList</code>.En el caso contrario
//		No cargue nada si devuelve <code>false</code>.
    public abstract boolean load(File f, String name, boolean rewrite);


    public boolean load(String fileName, String name) {
        File f = new File(getPath() + fileName);
        if (f.exists()) {
            if (f.isDirectory()) {
                loadDirectory(f);
            } else if (f.isFile() && hasValidExtension(f)) {
                return load(f, name, false);
            }
        } else {
            System.err.println("No Encontrado: " + f.getPath());
        }
        return false;
    }  





//	  Retira o elimina la imagen del objeto cargada previamente.
    public void removeObject(String name) {
        loaded.remove(name);
    }

//	 Retira o remueve todas las imagenes de los objetos cargados.
    public void removeAllObjects() {
        loaded.clear();
    }

        public void changeName(String name, String newName) {
        loaded.put(newName, loaded.get(name));
        removeObject(name);
    }

    public void putObject(String name, Object object) {
        loaded.put(name, object);
    }


//  Compruebe si el nombre del <code>f</code> tiene
//	cualquiera de las extensiones indicadas en el <code>ArrayList
//	Extensiones válidas</code>.
//
//	@paramametro f representa el Archivo para comprobar.
//	@return <code>true</code> si encuentra una extensión
//	o <code>false</code> si no.
    public boolean hasValidExtension(File f) {
        for (int i = 0; i < extensionesValidas.size(); i++) {
            String ext = "." + extensionesValidas.get(i).toLowerCase();
            if (ext.equals(".")) {
                return true;
            }
            if (f.getName().toLowerCase().lastIndexOf(ext)
                    + ext.length() == f.getName().length()) {
                // la extension del archivo es permitida
                return true;
            }
        }
        return false;
    }  // fin de hasValidExtension(File);

//	Comprueba si existe el archivo o directorio del cargador.
//
//	@return <code>true</code> si existe
//	o <code>false</code> si no.
    public boolean existsLoader() {
        if (loader != null) {
            return loader.exists();
        }
        return false;
    }
//---------------------------------------------------------------------------------Metodos Get---------------------------------------------------------------------------------------------------------
    
public File getFile(String name) {
        try {
            File file = new File(packageDirectory + relativePath + name);
            return file;
        } catch (Exception e) {
            System.err.println("Error cargando un archivo: " + name);
            e.printStackTrace();
        }
        return null;
    }

    public String getRelativePath() {
        return relativePath;
    }

    public String getPath() {
        return packageDirectory + relativePath;
    }

    public String getPackagePath() {
        return packageDirectory;
    }

    public File getLoader() {
        return loader;
    }

//	  Retorna/devuelve objetos
  public Object getObject(String name, boolean load, boolean rewrite) {
      Object aux = loaded.get(name);
      if ((load && aux == null) || rewrite) {
          File f = new File(getPath() + name);
          if (f.exists() && f.isFile() && hasValidExtension(f)) {
              load(f, name, rewrite);
          }
      }
      return loaded.get(name);
  }
  
  //---------------------------------------------------------------------------------Metodos set---------------------------------------------------------------------------------------------------------------

//	Establece la ruta de los directorios de archivos o
//	el archivo del loader.<br />
//	La <code>ruta</code> es relativa a la principal
//	directorio del paquete.<br />
//	La ruta predeterminada es <código>"."</código>
//	(directorio del paquete).<br />
//	No usar <código>".."</código>
public void setPath(String path) {
  try {

      path = path.trim();
      StringBuffer s = new StringBuffer(path);
      for (int i = 0; i < s.length(); i++) {
          if (s.charAt(i) == '\\') {
              s.replace(i, i + 1, "/");
          }
      }

      path = s.toString();
      if (path.charAt(path.length() - 1) != '/') {
          path += "/";
      }
      this.relativePath = path;
  } catch (Exception e) {
      System.err.println("\n"+ "Error al configurar el directorio de recursos.");
      e.printStackTrace();
  }
}  

//Un loader es una forma cómoda de cargar archivos. Un
//loader puede ser un archivo (si <code>name</code> es un
//nombre de archivo válido) o directorio de la <code>ruta</code>
//(si <código>nombre</código> es igual a "").
//
//@param nombre Nombre de archivo válido o <code>""</code> (todos
//imágenes del directorio <code>path</code>)
//
//@return <code>true</code> si el archivo o directorio
//existe o <code>false</code> si no.

public boolean setLoader(String name) {
  if (name == null) {
      return false;
  }
  loader = getFile(name);
  if (loader == null) {
      return false;
  }
  return loader.exists();
} 

//Carga todos los archivos del loader
//
//@return <code>true</code> si el cargador (archivo o
//directorio) existe y si no se producen errores.<br />
//En caso contrario <code>false</code>.
} 

