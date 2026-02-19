package conexion;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

/**
 * Clase usada para las conexiones del cliente y servidor.
 */
public class Connection {

	// Socket usado para la conexión
	private Socket socket;

	public Connection(Socket socket) {
		super();
		this.socket = socket;
	}
	
	/**
	 * Envia datos entre conexión
	 * @param datos
	 * @param longitud
	 */
	public void enviar(byte[] datos, int longitud) {
		//Si el socket esta conectado
		if (socket.isConnected()) {
			try {
				// Intenta enviar los datos
				OutputStream salida = socket.getOutputStream();
				salida.write(datos, 0, longitud);
				// Vacía la salida
				salida.flush();
						
			} catch (Exception e) {
				// TODO: handle exception
			}
		}
		else {
			System.err.println("Socket no conectado");
		}
	}
	
	/**
	 * Recibe datos entre conexión
	 * @param buffer
	 * @return
	 */
	public int recibir(byte[] buffer) {
		//Si el socket esta conectado
		if (socket.isConnected()) {
			try {
				// Intenta recibir los datos
				InputStream entrada = socket.getInputStream();
				return entrada.read(buffer);
			} catch (Exception e) {
				System.err.println("Error en la conexion al recibir datos.");
				return -1;
			}
			
		}
		else {
			System.err.println("Socket no conectado");
			return -1;
		}
	}
	
	/**
	 * Cierra la conexión
	 */
	public void cerrar() {
		try {
			socket.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
