package clientes;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Scanner;

import conexion.Connection;

/**
 * Aplicación de la terminal del cliente.
 */
public class MainFileClientApp {

	public static void main(String[] args) throws Exception {
		Connection conexion;
		// Dirección del servidor
		String host = "localhost";
		int puerto = 4321;
		if (args.length == 1) {
			host = args[0];
		} else if (args.length == 2) {
			host = args[0];
			puerto = Integer.parseInt(args[1]);
		}
		
		FileOutputStream salida = null;

		// Crear el scanner
		Scanner sc = new Scanner(System.in);
		// Obtener la linea escrita por el cliente
		String ruta = sc.nextLine();

		try {
			// Enviar la solicitud al servidor
			conexion = new Connection(new Socket(host, puerto));
			
			byte[] rutaBytes = ruta.getBytes();
			conexion.enviar(rutaBytes, rutaBytes.length);
			
			// Leer respuesta del servidor
			byte[] respuestaBytes = new byte[4];
			int longitudRespuesta = conexion.recibir(respuestaBytes);
			
			String respuesta = new String(respuestaBytes, 0, longitudRespuesta);
			
			// Si la respuesta es igual a la del error
			if (respuesta.equals("KO\n\r")) {
				System.err.println("Error accediendo al archivo.");
			}
			else {
				
				// Leer la ruta del fichero
				String rutaArchivo = new File(ruta).getName();
				salida = new FileOutputStream(rutaArchivo);
				
				byte[] buffer = new byte[4096];
				
				// Enviar el resto del mensaje
				while ((longitudRespuesta = conexion.recibir(buffer)) != -1) {
					salida.write(buffer, 0, longitudRespuesta);
				}
				
			}


	}
	catch (Exception e) {
		System.err.println("Error en la conexión.");
	}
		
		sc.close();
			
		}
	}

