package workers;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

import conexion.Connection;

public class LogicaServer implements Runnable {
	private Connection conexion;

	/**
	 * Constructor del hilo
	 * 
	 * @param conexion
	 */
	public LogicaServer(Connection conexion) {
		super();
		this.conexion = conexion;
	}

	/**
	 * Método runnable, obtendra la conexión del parámetro introducido y enviará al
	 * cliente un mensaje.
	 */
	@Override
	public void run() {
		byte[] buffer = new byte[4096];
		int bytesRecibidos = conexion.recibir(buffer);
		FileInputStream entrada = null;

		if (bytesRecibidos > 0) {
			try {
				String ruta = new String(buffer, 0, bytesRecibidos);
				entrada = new FileInputStream(ruta);

				byte[] respuesta = new String("OK\n\rDATOS").getBytes();
				conexion.enviar(respuesta, respuesta.length);

				while ((bytesRecibidos = entrada.read(buffer)) > 0) {
					conexion.enviar(buffer, bytesRecibidos);
				}
			} catch (Exception e) {
				byte[] respuesta = new String("KO\n\r").getBytes();
				conexion.enviar(respuesta, respuesta.length);
			} finally {
				try {
					entrada.close();
				} catch (IOException ignored) {
				}
				conexion.cerrar();
			}
		}

	}
}
