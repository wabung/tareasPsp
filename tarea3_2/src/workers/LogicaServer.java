package workers;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.ServerSocket;

import config.DNSConfig;

public class LogicaServer implements Runnable{
	//Iniciar la configuración
	DNSConfig configuracion;
	String cadena;
	byte[] enviados = new byte[1024];
	DatagramSocket serverSocket;
	InetAddress ipOrigen;
	int puerto;

	/**
	 * Constructor del hilo
	 * @param cadena
	 * @param serverSocket
	 * @param ipOrigen
	 * @param puerto
	 * @throws Exception
	 */
	public LogicaServer(String cadena, DatagramSocket serverSocket, InetAddress ipOrigen, int puerto) throws Exception {
		this.configuracion = new DNSConfig();
		this.cadena = cadena;
		this.serverSocket = serverSocket;
		this.ipOrigen = ipOrigen;
		this.puerto = puerto;
		
		//Cargar la configuración
		configuracion.cargar();
	}

	/**
	 * Método runnable, obtendra la ip del parámetro introducido y lo enviará al cliente.
	 */
	@Override
	public void run() {
		String ip = configuracion.getIp(cadena);
		
		//Si la ip es nula, devolver un error al cliente.
		if (ip == null) {
		    ip = "El nombre no se encuentra";
		}
		
		System.out.println(ip);
		
		//Enviar la ip
		enviados = ip.getBytes();
		
		//Enviar datagrama al cliente
		DatagramPacket paqEnviado = new DatagramPacket(enviados, enviados.length, ipOrigen, puerto);
		try {
			serverSocket.send(paqEnviado);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
}
