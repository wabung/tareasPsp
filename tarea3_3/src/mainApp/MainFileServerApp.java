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
import workers.LogicaServer;

/**
 * Aplicación del servidor principal.
 */
public class MainFileServerApp {

	public static void main(String[] args) throws Exception {
		int puerto = 4321;
		
		if (args.length == 1) {
			puerto = Integer.parseInt(args[0]);
		}
		
		//Puerto por el que escucha el servidor: 4321 por defecto
		ServerSocket serverSocket = new ServerSocket(puerto);
		
		Thread[] logica = new Thread[1];
		
		String cadena;
		
		Connection conexion = null;

		while ((conexion = new Connection(serverSocket.accept())) != null) {
			System.out.println("Obteniendo datagrama");
			
            //Aceptar conexión TCP
            Socket clientSocket = serverSocket.accept();
            
            InetSocketAddress remote = (InetSocketAddress) clientSocket.getRemoteSocketAddress();
            
            System.out.println("\tOrigen: " + remote.getAddress() + ":" + remote.getPort());
            
            BufferedReader lector = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

            cadena = lector.readLine();

			System.out.println("\tMensaje recibido: " + cadena.trim()+"\n");
			
			//Iniciar los hilos
			for(int i = 0; i < 1; i++) {
				logica[i] = new Thread(new LogicaServer(conexion));
				logica[i].start();
			}
			
		}
	}

}
