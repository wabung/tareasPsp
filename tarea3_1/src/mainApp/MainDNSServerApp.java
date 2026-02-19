package mainApp;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

import workers.LogicaServer;
import config.DNSConfig;

/**
 * Aplicación del servidor principal.
 */
public class MainDNSServerApp {

	public static void main(String[] args) throws Exception {
		//Iniciar la configuración
		DNSConfig configuracion = new DNSConfig();

		//Cargar la configuración
		configuracion.cargar();

		//Puerto por el que escucha el servidor: 2222
		DatagramSocket serverSocket = new DatagramSocket(2222);
		byte[] recibidos = new byte[1024];
		byte[] enviados = new byte[1024];
		
		Thread[] logica = new Thread[1];
		
		String cadena;

		while (true) {
			System.out.println("Obteniendo datagrama");
			
			//Recibir datagrama
			recibidos = new byte[1024];
			DatagramPacket paqRecibido = new DatagramPacket(recibidos, recibidos.length);
			serverSocket.receive(paqRecibido);
			cadena = new String(paqRecibido.getData()).trim();
			
			//Dirección origen
			InetAddress IPOrigen = paqRecibido.getAddress();
			int puerto = paqRecibido.getPort();
			System.out.println("\tOrigen: " + IPOrigen + ":" + puerto);
			System.out.println("\tMensaje recibido: " + cadena.trim()+"\n");
			
			for(int i = 0; i < 1; i++) {
				logica[i] = new Thread(new LogicaServer(cadena));
				logica[i].start();
			}
			
			//Enviar la ip
			enviados = ip.getBytes();
			
			//Enviar datagrama al cliente
			DatagramPacket paqEnviado = new DatagramPacket(enviados, enviados.length, IPOrigen, puerto);
			serverSocket.send(paqEnviado);
			
		}
	}

}
