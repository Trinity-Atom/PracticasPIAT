package piat.regExp;

import java.io.File;
import java.io.FilenameFilter;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Pattern;

/**
 * @SalvadorFernandezGarcia-Morales 53823973V
 *
 */
public class EstadisticasLog {

	private final static int numMaxMsg = 500;	// Nº máximo de mensajes enviados por un usuario para mostrarlo en las estadísticas
	
	// Expresiones regulares para la fecha y la hora
	private final static String FECHA = "[0-9]{4}-[0-9]{2}-[0-9]{2}";
	private final static String HORA = "[0-9]{2}:[0-9]{2}:[0-9]{2}";
	// Expresiones regulares para el nombre del servidor. El nombre del servidor está formado por el tipo + un número. Ejemplo smtp-in3
	private final static String TIPO_SERVIDOR = "[^0-9]+";
	private final static String NUMERO_SERVIDOR = "[0-9]+";
	
	// Patrón de una traza cualquiera correcta de la que podemos extraer, en el grupo 1, el nombre del servidor
	//DONE: Modificar este patrón para que tenga más grupos y así se pueda extraer más información y no solo el nombre del servidor
	private final static String patronTraza = "^("+FECHA+")\\s+("+HORA+")\\s+("+TIPO_SERVIDOR+")("+NUMERO_SERVIDOR+")+\\s+(\\[\\w+\\]):(.*)";	
	
	/* Patrones que se usan en las estadísticas agregadas

		msgIn: Mensajes en la bandeja de entrada
		msgOUT: Mensajes en la bandeja de salida
		msgINFECTED: Mensajes clasificados como infectados
		msgSPAM: Mensajes clasificados como spam
		code432: Mensajes con información de intentos de entrega con codigo 4.3.2 (overload)
		code511: Mensajes con información de intentos de entrega con código 5.1.1 (bad destination mailbox address)

	*/
	private final static String msgIn = FECHA+"\\s"+HORA+"\\ssmtp-in\\d\\s\\[[A-Z0-9]{8}\\]\\:\\smessage\\sfrom.*"; 	
	private final static String msgOUT = FECHA+"\\s"+HORA+"\\ssmtp-out\\d\\s\\[[A-Z0-9]{8}\\]\\:\\smessage\\sfrom.*";
	private final static String msgINFECTED = ".*security-antivirus: INFECTED.*";
	private final static String msgSPAM = ".*security-antispam: SPAM.*";
	private final static String code432 = ".*4\\.3\\.2\\s\\(overload\\).*";
	private final static String code511 = ".*5\\.1\\.1\\s\\(bad\\sdestination\\smailbox\\saddress\\).*";
	//DONE: Cambiar estos patrones por los que se piden en la práctica
	

	public static void main(String[] args) throws InterruptedException {
	    Thread.currentThread().setName("Principal");
	    // Verificar que se pasa como argumento el directorio con los logs y obtenerlo
	    File directorioFuente=validarArgumentos(args);
		
		// En este array bidimensional se almacenan los nombres de los estadísticos a obtener y el patrón para que luego sea mas fácil recorrerlo 
		// y meter sus valores en el mapa hmPatronesEstadisticasAgregadas 
		final String[][] patronesEstadisticasAgregadas= {
							{"msgIn",msgIn},
							{"msgOUT",msgOUT},
							{"msgSPAM",msgSPAM},	
							{"msgINFECTED",msgINFECTED},
							{"code432",code432},	
							{"code511",code511}					
						};
		//DONE: Cambiar estos patrones por los que se piden en la práctica
		
		// Mapa donde se guarda como clave el nombre de la estadística agregada, y como valor, el patrón para detectar el String de la traza que contiene ese estadístico
		final ConcurrentHashMap <String,Pattern> hmPatronesEstadisticasAgregadas = new ConcurrentHashMap <String,Pattern> ();
		
		// Guardar en el mapa hmPatronesEstadisticasAgregadas todos los patrones que se usarán para las estadísticas agregadas.
		// Los patrones y el nombre del estadístico están en el array bidimensional patronesEstadisticasAgregadas
		Pattern patron;
		String estadistico;		
		for (int i= 0; i < patronesEstadisticasAgregadas.length;i++) {
			estadistico=patronesEstadisticasAgregadas[i][0];
			patron = Pattern.compile(patronesEstadisticasAgregadas[i][1]);
			hmPatronesEstadisticasAgregadas.put(estadistico, patron);
		}		
		
		// Mapa donde recoger los contadores de cada estadísticas agregada.
		// La clave indica el servidor, la fecha y el estadístico, por lo que estará formado por una String con esta estructura: "<tipoServidor> <fecha> <estadístico>"
		// El valor es el contador de las veces que se encuentra el estadístico anterior
		final ConcurrentHashMap<String, AtomicInteger> hmEstadisticasAgregadas = new ConcurrentHashMap <String,AtomicInteger>();	

		// Mapa donde guardar el nombre del servidor y el tipo de servidor
		final ConcurrentHashMap<String, String> hmServidores = new ConcurrentHashMap <String,String>();
		
		// Mapa donde se guarda como clave el nombre de un usuario y como valor un contador, para poder guardar cuantos mensajes envían los usuarios del sistema
		final ConcurrentHashMap <String,AtomicInteger> hmUsuarios = new ConcurrentHashMap <String,AtomicInteger> ();	
		
		// Crear el patrón que se usará para analizar una traza, y del que, además de conocer si es una traza correcta, se pueden extraer ciertos valores mediante los grupos
		Pattern pTraza = Pattern.compile (patronTraza);

		/* Variables usadas para las estadísticas generales */
		int numeroFicherosProcesados;
		final AtomicInteger lineasCorrectas = new AtomicInteger(0);
		final AtomicInteger lineasIncorrectas = new AtomicInteger(0);
		
		/****** Comienzo del procesamiento ******/
		
		// Obtener los ficheros de log del directorio
		final File[] ficheros=obtenerFicheros(directorioFuente);
		
		final int numDeNucleos = Runtime.getRuntime().availableProcessors();
		System.out.print ("\nLanzando hilos en los " + numDeNucleos + " núcleos disponibles de este ordenador ");
		
		// Crear un pool donde ejecutar los hilos. El pool tendrá un tamaño del nº de núcleos del ordenador
		// por lo que nunca podrá haber más hilos que ese número en ejecución simultánea.
		// Si se quiere hacer pruebas con un solo trabajador en ejecución, poner como argumento un 1. Irá mucho más lenta
		final ExecutorService ejecutor = Executors.newFixedThreadPool(numDeNucleos);

	    final long tiempoComienzo = System.currentTimeMillis();
	    final AtomicInteger numTrabajadoresTerminados = new AtomicInteger(0);
	    int numTrabajadores=0;
	    numeroFicherosProcesados=0;
		for (File fichero: ficheros){
			System.out.print (".");
			ejecutor.execute(new Trabajador(fichero,  pTraza,  lineasCorrectas,  lineasIncorrectas, hmServidores,hmEstadisticasAgregadas, hmPatronesEstadisticasAgregadas,hmUsuarios,numTrabajadoresTerminados));
			numeroFicherosProcesados++;
			numTrabajadores++;
			//break; // Descomentando este break, solo se ejecuta el primer trabajador
		}
		System.out.print ("\nSe van a ejecutar "+numTrabajadores+" trabajadores en el pool. Esperar a que terminen ");
		
		// Esperar a que terminen todos los trabajadores
		ejecutor.shutdown();	// Cerrar el ejecutor cuando termine el último trabajador
		// Cada 10 segundos mostrar cuantos trabajadores se han ejecutado y los que quedan
		while (!ejecutor.awaitTermination(10, TimeUnit.SECONDS)) {
			final int terminados=numTrabajadoresTerminados.get();
			System.out.print("\nYa han terminado "+terminados+". Esperando a los "+(numTrabajadores-terminados)+" que quedan ");
		}
		// Mostrar todos los trabajadores que se han ejecutado. Debe coincidir con los creados
		System.out.println("\nYa han terminado los "+numTrabajadoresTerminados.get()+" trabajadores");
		
		/****** Mostrar las estadísticas ******/
		/* Estadísticas generales */
		System.out.println("\n\n  Estadísticas generales:");

		System.out.print("\t Servidores: ");
		// Crear un mapa temporal, llamado numServidores, que se usará para almacenar como clave el tipo de servidor y como valor un contador de cuantos hay de ese tipo en el mapa hmServidores
		HashMap<String, Integer> numServidores = new HashMap <String,Integer>();
		// Recorrer todas las entradas de hmServidores
		for (Map.Entry<String, String> entrada : hmServidores.entrySet()) {
			// Se intenta meter la clave de hmServidores en numServidores:
			// 		si no existe se añade con el contador 1
			// 		si ya existe se incrementa el contador en 1
			if(hmServidores.contains(entrada.getKey()))
				numServidores.entrySet();
			String nombreServidor = entrada.getValue();
			numServidores.put (nombreServidor, numServidores.get(nombreServidor) == null ? 1 : Integer.valueOf(numServidores.get(nombreServidor)+1));
		}
		// Mostrar por la salida estándar todas las entradas del mapa numServidores
		for (Map.Entry<String, Integer> entrada : numServidores.entrySet()) {
			System.out.print(entrada.getKey()+"="+entrada.getValue()+" ");
		}
		
		System.out.println("\n\t Numero de ficheros procesados: "+ numeroFicherosProcesados);
		System.out.println("\t Numero de trazas procesadas: "+ lineasCorrectas.get()+lineasIncorrectas.get());
		System.out.println("\t Numero de trazas incorrectas: "+ lineasIncorrectas.get());

		
		/* Estadísticas agregadas */
		System.out.println("\n\n  Estadísticas agregadas:");
		
		// Para mostrar el contenido del mapa hmEstadisticasAgregadas de forma ordenado, se copia a un TreeMap y se muestra el contenido de este, pues un TreeMap almacena la información ordenada por la clave
		Map <String, AtomicInteger> mapaOrdenado = new TreeMap<String ,AtomicInteger>(hmEstadisticasAgregadas);
		for (Map.Entry<String, AtomicInteger> entrada : mapaOrdenado.entrySet()) {
			//TODO: La siguiente instrucción es correcta, pero se puede cambiar para que salga mejor formateada
			System.out.println("\t"+entrada.getKey()+"aa = " +entrada.getValue().get());
	        }

		/* Estadísticas de usuarios */
		System.out.println("\n  Estadísticas de usuarios que han enviado más de "+numMaxMsg+" mensajes:");
		
		// Se reutiliza el TreeMap anterior para mostrar el mapa hmUsuarios ordenado
		mapaOrdenado.clear();
		mapaOrdenado = new TreeMap<String ,AtomicInteger>(hmUsuarios);
		for (Map.Entry<String, AtomicInteger> entrada : mapaOrdenado.entrySet()) {
			if (entrada.getValue().get()>=numMaxMsg) 
				System.out.printf("\t%-20s %5d %n", entrada.getKey()+":",entrada.getValue().get());
		}

		
		final long tiempoFinal = System.currentTimeMillis();
		System.out.println("\nTiempo de ejecución: "+ ((long)(tiempoFinal-tiempoComienzo)/1000) + " segundos");
		System.out.println("\n\n");
	}
	
	
	/* Métodos auxiliares */
	
	/**
	 * Devuelve un array de elementos File que se corresponden con los ficheros con extensión log encontrados en el directorio que se pasa como parámetro
	 *
	 * @param	directorioFuente	Directorio que contiene los ficheros de log
	 */
	private static File[] obtenerFicheros(File directorioFuente) {
	
		final File[] ficheros = directorioFuente.listFiles( new FilenameFilter() {
				@Override
				public boolean accept(File dir, String name) {
					if (name.endsWith(".log")){
						return true;
					}
					return false;
				}
			}
		);
		return ficheros;
	}
	
 
	/**
	 * Verifica que se ha pasado un argumento con el nombre del directorio y que este existe y se puede leer. En caso contrario aborta la aplicación.
	 *
	 * @param	args	Argumentos a analizar
	 */
	private static File validarArgumentos(String[] args) {
		// Validación de argumentos
		if (args.length != 1){
			mostrarUso("ERROR: faltan argumentos");
			System.exit(-1);
		}
	
		// Verificación de que el directorio existe y puede ser leído
		File directorioFuente = new File(args[0]);
		if (!directorioFuente.isDirectory() || !directorioFuente.canRead()){
			mostrarUso("ERROR: El directorio '" + args[0] + "' no existe o no puede ser leído");
			System.exit(-1);
		}
		return (directorioFuente);
	}
	
	
	/**
	 * Muestra mensaje de uso del programa.
	 *
	 * @param	mensaje	Mensaje adicional informativo en la línea inicial (null si no se desea)
	 */
	private static void mostrarUso(String mensaje){
		Class<? extends Object> thisClass = new Object(){}.getClass();
		
		if (mensaje != null)
			System.err.println(mensaje);
		System.err.println(
				"Uso: " + thisClass.getEnclosingClass().getCanonicalName() + " <directorio fuente>\n" +
				"\t\t donde <directorio fuente> es el path al directorio donde están los ficheros de log\n");
	}
}
