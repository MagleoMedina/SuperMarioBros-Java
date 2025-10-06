package com.mariobros.objects;

import java.awt.*;
import java.io.*;
import java.util.*;

import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;

import com.mariobros.creditos.*;

/**
 * Map.java class
 */
public class Map {

    // ------------------------------------------------------------------------Variables
    // de la
    // clase-----------------------------------------------------------------------------

    public static final String MAP_PATH = "res/maps/";// Ruta a la cual estan asignados los mapas
    public static final String FILE_PREFIX = "level*_*";// Identificador del nivel que se esta jugando segun la ruta de
                                                        // los mapas

    public static final String TAG_BACKGROUND = "back:";// carga del BackGround
    public static final String TAG_FRONTGROUND = "front:";// Carga del FrontGround
    public static final String TAG_MUSIC = "music:";// Carga de la musica del juego
    public static final String TAG_TIME = "time:";// Tiempo que tarda el jugador en completar el nivel o niveles(Se
                                                  // muestra por consola)

    public static final int TILE_X_SIZE = 32;
    public static final int TILE_Y_SIZE = 32;

    public static final int MAX_SIZE_X = 5000;
    public static final int MAX_SIZE_Y = 5000;

    // Posicion inicial de la camara al iniciar un nuevo mapa
    public static final int DISPLAY_X = 30;
    public static final int DISPLAY_Y = 20;

    // Declaracion de las variables en 0/false
    public float xMap = 0, yMap = 0;
    public double xSpeed = 0, ySpeed = 0;
    public int tileXSize = TILE_X_SIZE, tileYSize = TILE_Y_SIZE;
    public int sizeX = 0, sizeY = 0;
    public int displayX = DISPLAY_X, displayY = DISPLAY_Y;
    public int tileX = 0, tileY = 0, accurateX = 0, accurateY = 0;
    public boolean movingX = false, movingY = false;

    // Variables enteras que representan el numero del mundo y el nivel del juego
    public int world, level;
    public final int LAST_LEVEL = 4;

    // Variable que registra y lee el nombre del archivo del mapa de jeugo
    public String fileName;

    // Matriz De Objetos que se agg a la matriz de mapa
    public WorldObject[][] spriteMap;

    public StringBuffer[][] stringMap;

    // Declaracion de ArrayList de objetos del mapa

    public ArrayList<Background> backs;
    public ArrayList<Background> fronts;
    public ArrayList<String> music;
    public ArrayList<Player> players;

    // Lugares desde los cuales es posible que empieze
    // un jugador. El numero de puntos marca el maximo
    // de jugadores que pueden jugar este nivel.
    // Por lo tanto, como minimo, debe haber uno.
    public ArrayList<Point> startingPlaces;

    // Representa el escenario que se esta jugando
    public Stage stage;

    // ------------------------------------------------------------ Constructores de
    // la
    // clase------------------------------------------------------------------------------------------------

    public Map(Stage s, int world, int level) {
        StringBuffer sb = new StringBuffer(FILE_PREFIX);
        int first = sb.indexOf("*");
        int last = sb.lastIndexOf("*");
        if (first != last) {
            sb.replace(first, first + 1, "" + world);
            sb.replace(last, last + 1, "" + level);
            this.stage = s;
            this.fileName = sb.toString();
            this.world = world;
            this.level = level;
        } else {
            System.err.println("FILE_PREFIX es incorrecto.");
            System.exit(-1);
        }
    }

    // --------------------------------------------------------------------------------Metodos
    // de la
    // clase--------------------------------------------------------------------
    /*
     * Lee un archivo de mapa y lo procesa.
     * El método utiliza un BufferedReader para leer el archivo línea por línea. Si
     * la línea es un comentario o está vacía, se ignora.
     * Si la línea comienza con una etiqueta específica (como TAG_BACKGROUND,
     * TAG_FRONTGROUND o TAG_MUSIC), se procesa en consecuencia.
     * Si no, se llama al método readMapLine para analizar la línea. El método
     * también mantiene un registro del tamaño del mapa en las variables sizeX y
     * sizeY.
     * Finalmente, el método cierra el BufferedReader y devuelve verdadero
     */
    private boolean readMapFile() {
        BufferedReader br = null;
        try {
            String packageDirectory = getClass().getClassLoader().getResource("").getPath();
            int binIndex = packageDirectory.lastIndexOf("bin/");
            if (binIndex != -1) {
                packageDirectory = packageDirectory.substring(0, binIndex);
            }
            // Remove trailing slash if present
            if (packageDirectory.endsWith("/") || packageDirectory.endsWith("\\")) {
                packageDirectory = packageDirectory.substring(0, packageDirectory.length() - 1);
            }
            InputStream is = new FileInputStream(packageDirectory + "/" + MAP_PATH + "/" + fileName);
            InputStreamReader isr = new InputStreamReader(is);
            br = new BufferedReader(isr);

            String line;
            while ((line = br.readLine()) != null) {

                // Si es una linea vacio un comentario...
                if (line.length() == 0 || line.startsWith("//")) {
                    continue;

                } else if (line.startsWith(TAG_BACKGROUND)) {
                    addBackground(createBackground(line.substring(TAG_BACKGROUND.length(), line.length())));
                    continue;

                } else if (line.startsWith(TAG_FRONTGROUND)) {
                    addFrontground(createBackground(line.substring(TAG_FRONTGROUND.length(), line.length())));
                    continue;

                } else if (line.startsWith(TAG_MUSIC)) {
                    addMusic(line.substring(TAG_MUSIC.length(), line.length()));
                    continue;

                } // si no... analizamos la linea.
                if (sizeX < line.length()) {
                    sizeX = line.length();
                }
                readMapLine(line, sizeY);
                sizeY++;
            }
            br.close();

        } catch (IOException e) {
            System.out.println(e.getMessage());
            //System.err.println("Error cargando el archivo del mapa");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return true;
    }

    /*
     * inicializa un mapa.
     * El método crea una matriz bidimensional de objetos WorldObject llamada
     * spriteMap,
     * así como varias listas para almacenar objetos de fondo, objetos de primer
     * plano, música, jugadores y lugares de inicio.
     * Luego, se agregan dos jugadores (Mario y Luigi) a la lista de jugadores y se
     * llama al método readMapFile para leer el archivo de mapa.
     * Después, el método crea una nueva matriz bidimensional de objetos WorldObject
     * llamada s y copia los valores de spriteMap en ella.
     * Finalmente, se asigna la matriz s a spriteMap y se llama al método startMusic
     */
    public void initMap() {
        spriteMap = new WorldObject[MAX_SIZE_Y][MAX_SIZE_X];
        backs = new ArrayList<Background>();
        fronts = new ArrayList<Background>();
        music = new ArrayList<String>();
        players = new ArrayList<Player>();
        startingPlaces = new ArrayList<Point>();
        // Only add Mario and Luigi if there are enough starting places after reading the map
        readMapFile();
        if (startingPlaces.size() > 0) {
            Player mario = new Mario(stage);
            mario.setPosition(startingPlaces.get(0));
            players.add(mario);
        }
        if (startingPlaces.size() > 1) {
            Player luigi = new Luigi(stage);
            luigi.setPosition(startingPlaces.get(1));
            players.add(luigi);
        }
        // Defensive: set sizeY/sizeX to at least 1 to avoid zero-size arrays
        int safeSizeY = Math.max(sizeY, 1);
        int safeSizeX = Math.max(sizeX, 1);
        WorldObject[][] s = new WorldObject[safeSizeY][safeSizeX];
        for (int i = 0; i < sizeY; i++) {
            for (int j = 0; j < sizeX; j++) {
                s[i][j] = spriteMap[i][j];
            }
        }
        spriteMap = s;
        startMusic();
    }

    /*
     * lee una línea del archivo de mapa y la procesa.
     * El método toma como parámetros una cadena line que representa la línea a leer
     * y un entero y que representa la posición vertical de la línea en el mapa.
     * El método recorre cada carácter de la línea y llama al método readChar para
     * procesar cada carácter individualmente,
     * pasando el carácter, su posición horizontal en la línea y su posición
     * vertical en el mapa como parámetros
     */
    private void readMapLine(String line, int y) {
        for (int i = 0; i < line.length(); i++) {
            readChar(line.charAt(i), i, y);
        }
    }

    /*
     * lee un carácter del archivo de mapa y lo procesa.
     * El método toma como parámetros un carácter c que representa el carácter a
     * leer,
     * un entero x que representa la posición horizontal del carácter en el mapa y
     * un entero y que representa la posición vertical del carácter en el mapa.
     * El método utiliza una instrucción switch para verificar el valor del carácter
     * y crear un objeto WorldObject apropiado en consecuencia
     * (como un ladrillo, una moneda o una caja de monedas). Si el carácter es un
     * asterisco o una barra vertical,
     * se agrega un nuevo punto a la lista de lugares de inicio. Finalmente, si se
     * creó un objeto WorldObject,
     * se establecen sus coordenadas y se agrega a la matriz spriteMap. Si no se
     * creó ningún objeto, se asigna el valor nulo a la posición correspondiente en
     * spriteMap
     */
    private void readChar(char c, int x, int y) {
        WorldObject o = null;
        switch (c) {
            case 'B':
                o = new Brick(stage);
                break;
            case 'b':
                o = new Brick(stage); // Creamos bloques falsos para el jugador
                o.setSupportPlayer(false); // De momento no los utilizamos
                break;
            case 'c':
                o = new Coin(stage);
                break;
            case '?':
                o = new CoinBox(stage);
            case '#':
                o = new CoinBoxCoin(stage);

                break;
            case '*':
                startingPlaces.add(new Point(x * tileXSize, y * tileYSize));// Representando a mario
                break;
            case '|':
                startingPlaces.add(new Point(x * tileXSize, y * tileYSize));// representando a luigi
                break;
        }
        if (o != null) {
            o.setX(x * tileXSize);
            o.setY(y * tileYSize);
            spriteMap[y][x] = o;
        } else {
            spriteMap[y][x] = null;
        }
    }

    public boolean endlevel = false;
    public Object showCoinCount;

    /*
     * Carga el siguiente nivel del juego.
     * El método establece la variable endlevel en verdadero y luego espera medio
     * segundo.
     * Después, el método crea un objeto StringBuffer a partir del nombre del
     * archivo de mapa actual
     * y utiliza el método replace para incrementar el número de nivel en el nombre
     * del archivo.
     * Luego, se asigna el valor del objeto StringBuffer a la variable fileName y se
     * llama al método initMap para inicializar el nuevo mapa.
     * Finalmente, se establece la variable endlevel en falso.
     */
    public void nextLevel() {
        endlevel = true;

        if (checkLastLevel()) {
            // Si es el último nivel, cerrar el JFrame

            stage.window.setVisible(false); // Ocultar la ventana del juego
            Stage.showPerformance();// mostrar las stats antes de cerrar el Jframe
            creditos(); // se llaman a los creditos los cuales cerraran el Jframe

        } else {

            try {
                Thread.sleep(1000);

            } catch (Exception e) {
            }

            StringBuffer s = new StringBuffer(fileName);
            s.replace(fileName.lastIndexOf("" + level), fileName.lastIndexOf("" + level) + 1, "" + (++level));
            fileName = s.toString();
            initMap();
            endlevel = false;
        }
    }

    /*
     * Crea un objeto Background a partir de una cadena de información.
     * El método utiliza un objeto StringTokenizer para dividir la cadena en tokens.
     * Si hay solo un token, se crea un nuevo objeto Background utilizando el token
     * como nombre y se devuelve.
     * Si hay más de un token pero no hay exactamente 8, se muestra un mensaje de
     * error y se devuelve nulo.
     * Si hay exactamente 8 tokens, se procesan en consecuencia para obtener el
     * nombre, el factor de velocidad, la velocidad predeterminada, las coordenadas
     * x e y, el ancho,
     * el alto y el valor alfa del fondo. Luego, se crea un nuevo objeto Background
     * con estos valores y se establecen sus coordenadas x e y.
     * Finalmente, se devuelve el objeto Background creado.
     */
    public Background createBackground(String info) {
        StringTokenizer st = new StringTokenizer(info);
        if (st.countTokens() == 1) {
            Background b = new Background(stage, info);
            return b;

        } else if (st.countTokens() != 8) {
            System.err.println("Error al crear el BackGround.\n");
            return null;
        }

        String name = st.nextToken();
        double speedFactor = 0, defSpeed = 0;
        int x = 0, y = 0, width = 0, height = 0;
        float alpha = 0;

        try {

            speedFactor = Double.parseDouble(st.nextToken());
            defSpeed = Double.parseDouble(st.nextToken());
            String s = st.nextToken();
            x = Integer.parseInt(s);
            s = st.nextToken();
            y = Integer.parseInt(s);
            s = st.nextToken();

            if (s.startsWith("-")) {
                width = Background.IMG_DIMENSIONS;

            } else if (s.toLowerCase().startsWith("w")) {
                width = getDisplayableWidth();

            } else {
                width = Integer.parseInt(s);
            }
            s = st.nextToken();

            if (s.startsWith("-")) {
                height = Background.IMG_DIMENSIONS;

            } else if (s.toLowerCase().startsWith("h")) {
                height = getDisplayableHeight();

            } else {
                height = Integer.parseInt(s);
            }
            s = st.nextToken();

            if (s.startsWith("-")) {
                alpha = 1.0F;

            } else {
                alpha = Float.parseFloat(s);
            }

        } catch (NumberFormatException e) {
            System.err.println("Error al agregar el FrontGround.");
            e.printStackTrace();
            return null;
        }
        Background b = new Background(stage, name, speedFactor, defSpeed, width, height, alpha);
        b.setX(x);
        b.setY(y);
        return b;
    }

    public void addBackground(Background b) {
        backs.add(b);
    }

    public void addFrontground(Background f) {
        fronts.add(f);
    }

    // Valida el numero de jugadores permitido en un mapa

    public void addPlayer(Player player) {
        if (players.size() >= startingPlaces.size()) {
            System.out.println(" Solamente se permiten  " + players.size() + " jugadores en este mapa.");
        } else {
            Point point = startingPlaces.get(players.size());
            player.setPosition(point);
            players.add(player);

            // hacer que el mapa muestre al jugador
            xMap = Float.parseFloat("" + point.getX());
            yMap = Float.parseFloat("" + point.getY()) - getDisplayableHeight() + 64;
        }
    }

    public void addMusic(String name) {
        music.add(name);
    }

    /*
     * reproduce la música del mapa. El método recorre la lista de música y, para
     * cada elemento, verifica si es nulo.
     * Si no es nulo, se llama al método play del objeto SoundsLoader del escenario
     * para reproducir la canción.
     * El segundo parámetro del método play se establece en true, lo que indica que
     * la canción se repetirá en bucle.
     */

    public void startMusic() {

        for (int i = 0; i < music.size(); i++) {
            String songName = music.get(i);
            if (songName != null) {
                stage.getSoundsLoader().play(music.get(i), true);
            }
        }
    }

    // Detiene la musica
    public void stopMusic() {

        stage.getSoundsLoader().stop(); // Detiene la reproducción de la música
    }

    /// Metodos que verifican si el mapa está listo para moverse hacia los ejes
    public boolean readyRightXMap(Player p) {
        return !movingX && (p.getX() - xMap >= getDisplayableWidth() / 2)
                && (xMap < getWidth() - getDisplayableWidth());
    }

    public boolean readyLeftXMap(Player p) {
        return !movingX && (p.getX() - xMap <= getDisplayableWidth() / 2) && (xMap > 0);
    }

    public boolean readyUpYMap(Player p) {
        return !movingY && (p.getY() - yMap <= getDisplayableHeight() / 4) && (yMap > 0);
    }

    public boolean readyDownYMap(Player p) {
        return !movingY && (p.getY() - yMap >= getDisplayableHeight() / 2)
                && (yMap < getHeight() - getDisplayableHeight());
    }

    /*
     * actualiza el estado del juego.
     * El método verifica si el nivel ha terminado y, de ser así, retorna sin hacer
     * nada. Luego,
     * el método llama a los métodos actClass de varias clases para actualizar su
     * estado.
     * Después, el método mueve el mapa en los ejes X e Y y verifica si se ha salido
     * de los límites del mapa.
     * Si es así, ajusta la posición y la velocidad en consecuencia.
     * Luego, el método mueve los objetos de fondo y primer plano y ajusta su
     * velocidad en consecuencia.
     * Después, el método verifica si el mapa está en movimiento y, de no ser así,
     * establece la velocidad en cero.
     * Luego, el método actualiza las coordenadas de los sprites estáticos y
     * verifica si hay colisiones entre ellos.
     * Finalmente, el método actualiza el estado de los jugadores y verifica si hay
     * colisiones con otros objetos.
     */
    public void act() {
        if (endlevel) {
            return;
        }
        // Defensive: skip act if spriteMap is not properly initialized
        if (spriteMap == null || sizeX <= 0 || sizeY <= 0) {
            return;
        }
        // Actualizamos las clases de manera estatica.
        // De esta forma hacemos que las imagenes sean
        // las mismas para todos los WorldObjects(Sprites).
        Coin.actClass();
        Brick.actClass();
        CoinBox.actClass();
        CoinBoxCoin.actClass();

        // Movemos el mapa de ser necesario.
        xMap += xSpeed;
        if (xMap < 0) {
            xSpeed = xSpeed - xMap;
            xMap = 0;
        } else if (xMap > getWidth() - getDisplayableWidth()) {
            xMap = getWidth() - getDisplayableWidth();
        }
        yMap += ySpeed;
        if (yMap < 0) {
            ySpeed = ySpeed - yMap;
            yMap = 0;
        } else if (yMap > getHeight() - getDisplayableHeight()) {
            yMap = getHeight() - getDisplayableHeight();
        }

        // Movemos los fondos.
        for (int i = 0; i < backs.size(); i++) {
            Background b = backs.get(i);
            b.setSpeedX(-xSpeed);
            // b.setSpeedY(-ySpeed);
            b.act();
        }
        for (int i = 0; i < fronts.size(); i++) {
            Background b = fronts.get(i);
            b.setSpeedX(-xSpeed);
            // b.setSpeedY(-ySpeed);
            b.act();
        }
        // Quitamos la posible velocidad si no es constante.
        if (!movingX) {
            xSpeed = 0;
        }
        if (!movingY) {
            ySpeed = 0;
        }
        // Actualizamos los sprites estaticos.

        tileX = (int) xMap / tileXSize;
        tileY = (int) yMap / tileYSize;
        accurateX = -(int) xMap % tileXSize;
        accurateY = -(int) yMap % tileYSize;
        for (int i = tileX; i < tileX + displayX + 1 && i < sizeX; i++) {
            for (int j = tileY; j < tileY + displayY + 1 && j < sizeY; j++) {
                // Defensive: check bounds
                if (j < 0 || j >= spriteMap.length || i < 0 || i >= spriteMap[j].length) continue;
                Sprite s = spriteMap[j][i];
                if (s != null) {
                    s.act();

                    // Colisones con los objetos de al lado
                    if (inMap(i + 1, j) && spriteMap[j][i + 1] != null) {
                        if (s.collidesWith(spriteMap[j][i + 1], false)) {
                            s.collision(spriteMap[j][i + 1]);
                            spriteMap[j][i + 1].collision(s);
                        }
                    }
                    if (inMap(i, j + 1) && spriteMap[j + 1][i] != null) {
                        if (s.collidesWith(spriteMap[j + 1][i], false)) {
                            s.collision(spriteMap[j + 1][i]);
                            spriteMap[j + 1][i].collision(s);
                        }
                    }
                    if (inMap(i + 1, j + 1) && spriteMap[j + 1][i + 1] != null) {
                        if (s.collidesWith(spriteMap[j + 1][i + 1], false)) {
                            s.collision(spriteMap[j + 1][i + 1]);
                            spriteMap[j + 1][i + 1].collision(s);
                        }
                    }
                    if (s.isToDelete()) {
                        spriteMap[j][i] = null;
                    }
                }
            }
        }

        // Actualizamos los jugadores y detectamos colisiones.
        for (int i = 0; i < players.size(); i++) {
            players.get(i).act();
            checkCollisions(players.get(i));
        }
    }

    /*
     * Dibuja el estado actual del juego en un objeto Graphics.
     * El método verifica si el nivel ha terminado y, de ser así, retorna sin hacer
     * nada.
     * Luego, el método recorre la lista de objetos de fondo y llama al método paint
     * de cada uno para dibujarlos en el objeto Graphics.
     * Después, el método recorre la matriz de sprites estáticos y llama al método
     * paint de cada uno para dibujarlos en el objeto Graphics,
     * ajustando sus coordenadas en consecuencia. Luego, el método recorre la lista
     * de jugadores y llama al método paint de cada uno para dibujarlos en el objeto
     * Graphics,
     * ajustando sus coordenadas en consecuencia.
     * Finalmente, el método recorre la lista de objetos de primer plano y llama al
     * método paint de cada uno para dibujarlos en el objeto Graphics
     */

    public void paint(Graphics g) {
        if (endlevel) {
            return;
        }
        // Pintamos los fondos.
        for (int i = 0; i < backs.size(); i++) {
            Background b = backs.get(i);
            b.paint(g);
        }
        // Pintamos los sprites estaticos.

        for (int i = tileX; i < tileX + displayX + 1 && i < sizeX; i++) {
            for (int j = tileY; j < tileY + displayY + 1 && j < sizeY; j++) {
                Sprite s = spriteMap[j][i];
                if (s != null) {
                    s.paint(g, s.getX() - xMap, s.getY() - yMap, tileXSize, tileYSize);
                }
            }
        }
        // Pintamos los jugadores.

        for (int i = 0; i < players.size(); i++) {
            Player p = players.get(i);
            p.paint(g, p.getX() - xMap, p.getY() - yMap, tileXSize, tileYSize);
        }
        // Pintamos los frontground

        for (int i = 0; i < fronts.size(); i++) {
            Background b = fronts.get(i);
            b.paint(g);
        }
    }

    /*
     * Verifica si un jugador ha colisionado con algún objeto en el mapa.
     * El método toma como parámetro un objeto Player y calcula su columna y fila en
     * el mapa dividiendo sus coordenadas x e y por el tamaño de los tiles en los
     * ejes X e Y,
     * respectivamente. Luego, el método llama al método checkTile cuatro veces,
     * pasando como parámetros el piso y el techo de la columna y la fila del
     * jugador, así como el objeto Player en sí.
     * Esto verifica si el jugador ha colisionado con algún objeto en las cuatro
     * tiles adyacentes a su posición actual.
     */
    public void checkCollisions(Player p) {
        double colP = p.getX() / tileXSize;
        double filP = p.getY() / tileYSize;
        checkTile(Math.floor(colP), Math.floor(filP), p);
        checkTile(Math.ceil(colP), Math.ceil(filP), p);
        checkTile(Math.ceil(colP), Math.floor(filP), p);
        checkTile(Math.floor(colP), Math.ceil(filP), p);
    }

    /*
     * Verifica si un jugador ha colisionado con un objeto en una tile específica
     * del mapa.
     * El método toma como parámetros dos valores x e y que representan la columna y
     * la fila de la tile a verificar, así como un objeto Player.
     * El método verifica si la tile está dentro de los límites del mapa y, de ser
     * así, obtiene el objeto WorldObject en esa posición de la matriz spriteMap.
     * Luego, el método verifica si el objeto WorldObject colisiona con el jugador
     * y, de ser así, llama a los métodos collision de ambos objetos para manejar la
     * colisión.
     * Finalmente, el método devuelve verdadero si hubo una colisión o falso en caso
     * contrario.
     */
    public boolean checkTile(double x, double y, Player p) {
        if (inMap(x, y)) {
            WorldObject o = spriteMap[(int) y][(int) x];
            if (o.collidesWith(p, false)) {
                o.collision(p);
                p.collision(o);
                return true;
            }
        }
        return false;
    }

    /*
     * Verifica si una posición está dentro de los límites del mapa y si hay un
     * objeto en esa posición.
     * El método toma como parámetros dos valores x e y que representan la columna y
     * la fila de la posición a verificar.
     * El método devuelve verdadero si x e y están dentro de los límites del mapa y
     * si hay un objeto en la posición correspondiente de la matriz spriteMap.
     * De lo contrario, devuelve falso.
     */
    public boolean inMap(double x, double y) {
        return (x >= 0 && y >= 0 && x < sizeX && y < sizeY) && (spriteMap[(int) y][(int) x] != null);
    }

    // --------------------------------------------------------------------------------------
    // Metodos Get ----------------------------------------------------------------
    public int getWidth() {
        return sizeX * tileXSize;
    }

    public int getDisplayableWidth() {
        return displayX * tileXSize;
    }

    public int getHeight() {
        return sizeY * tileYSize;
    }

    public int getDisplayableHeight() {
        return displayY * tileYSize;
    }

    // ------------------------------------------------------------------------Metodos
    // Set------------------------------------------------------------------------
    /*
     * Establece la velocidad en el eje X del objeto.
     * El método toma como parámetro un valor s que representa la nueva velocidad en
     * el eje X.
     * Si s es negativo y su valor absoluto es mayor que el valor absoluto de la
     * velocidad actual en el eje X, se asigna el valor de s a la variable xSpeed.
     * Si s es positivo y es mayor que la velocidad actual en el eje X, se asigna el
     * valor de s a la variable xSpeed
     */
    public void setSpeedX(double s) {
        if (s < 0 && -xSpeed > s) {
            xSpeed = s;

        } else if (s > 0 && xSpeed < s) {
            xSpeed = s;
        }
    }

    /*
     * establece la velocidad en el eje Y del objeto. El método toma como parámetro
     * un valor s que representa la nueva velocidad en el eje Y.
     * Si s es negativo y su valor absoluto es mayor que el valor absoluto de la
     * velocidad actual en el eje Y,
     * se asigna el valor de s a la variable ySpeed.
     * Si s es positivo y es mayor que la velocidad actual en el eje Y, se asigna el
     * valor de s a la variable ySpeed
     */
    public void setSpeedY(double s) {
        if (s < 0 && -ySpeed > s) {
            ySpeed = s;

        } else if (s > 0 && ySpeed < s) {
            ySpeed = s;
        }
    }

    public boolean checkLastLevel() {
        return level == LAST_LEVEL;
    }

    // Jframe de los creditos del juego
    public static void creditos() {

        Pantalla panta = new Pantalla();
        panta.setSize(810, 508); // Establece las dimensiones deseadas
        panta.setVisible(true);
        panta.setLocationRelativeTo(null);

        // Ruta del archivo de la canción
        String rutaCancion = "src/main/java/com/mariobros/creditos/YOASOBI Seventeen Instrumental.wav";

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

            System.exit(0);

            // Detener y cerrar el clip después de 30 segundos
            clip.stop();
            clip.close();
        } catch (IOException | LineUnavailableException | UnsupportedAudioFileException | InterruptedException e) {
            e.printStackTrace();
        }
    }

}
