package clientes;

import java.net.Socket;
import java.util.Scanner;

import conexion.Connection;

/**
 * Aplicación de la terminal del cliente.
 */
public class MainComandoClientApp {

	public static void main(String[] args) {
		//Dirección del servidor
		String host = "localhost";
		int puerto = 2121;
		if (args.length == 1) {
			host = args[0];
		} else if (args.length == 2) {
			host = args[0];
			puerto = Integer.parseInt(args[1]);
		}

		//Crear el scanner
		Scanner sc = new Scanner(System.in);

		try {
			//Establecer conexión
			Connection conexion = new Connection(new Socket(host, puerto));
			System.out.println("Conectado a " + host + ":" + puerto);
			
			while (true) {
				//Obtener la linea escrita por el cliente
				System.out.print("Comando: ");
				String comando = sc.nextLine();
				
				// Enviar la solicitud al servidor
				conexion.enviarCadena(comando);
				
				//Leer respuesta del servidor
				String respuesta = conexion.recibirCadena();
				if (respuesta != null) {
					System.out.print(respuesta);
				}
				
				//Salir si el comando es quit
				if(comando.trim().equals("quit")) {
					System.out.println("Cerrando la conexión");
					conexion.cerrar();
					break;
				}
			}
		} catch (Exception e) {
			System.err.println("Error en la conexión: " + e.getMessage());
		}
		
		sc.close();
	}
}

