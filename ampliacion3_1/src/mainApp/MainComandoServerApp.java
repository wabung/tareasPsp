package mainApp;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;

import conexion.Connection;
import config.TCPConfig;
import workers.LogicaServer;
import java.time.LocalDate;

/**
 * Aplicación del servidor principal.
 */
public class MainComandoServerApp {

	public static void main(String[] args) throws Exception {
		
		//Iniciar la configuración
		TCPConfig configuracion = new TCPConfig();

		int puerto;
		
		try {
			//Cargar la configuración
			configuracion.cargar();
			
			puerto = configuracion.getPort("puerto");
		}catch(Exception e) {
			System.out.println("El archivo no es accesible.");
			puerto = 2121;
			
		}
		
		//Puerto por el que escucha el servidor: 2121 por defecto
		ServerSocket serverSocket = new ServerSocket(puerto);
		System.out.println("Servidor iniciado en puerto " + puerto);
	
	Connection conexion = null;

		while ((conexion = new Connection(serverSocket.accept())) != null) {
			System.out.println("Conexión aceptada");
			
            InetSocketAddress remote = (InetSocketAddress) conexion.getSocket().getRemoteSocketAddress();
			
			//Iniciar un hilo por cada cliente
			Thread hilo = new Thread(new LogicaServer(conexion));
			hilo.start();
		}
	}

}
