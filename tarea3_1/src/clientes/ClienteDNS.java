package clientes;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Scanner;

/**
 * Aplicación de la terminal del cliente.
 */
public class ClienteDNS {

	public static void main(String[] args) throws Exception {
        //Dominio a resolver
        String cadena;
        byte[] enviados;
        byte[] recibidos = new byte[1024];

        //Dirección del servidor
        InetAddress servidor = InetAddress.getByName("localhost");
        int puertoServidor = 2222;

        //Crear socket del cliente
        DatagramSocket socket = new DatagramSocket();

        //Crear el scanner
        Scanner sc = new Scanner(System.in);
		while (true) {
			//Obtener la linea escrita por el cliente
	        cadena = sc.nextLine().trim();
	        
	        //Si la linea está vacia, salir del bucle.
            if (cadena.equals("")) {
                break;
            }
            
            enviados = cadena.getBytes();
	        //Enviar petición
	        DatagramPacket paqEnviado = new DatagramPacket(enviados, enviados.length, servidor, puertoServidor);
	        socket.send(paqEnviado);

	        //Esperar respuesta
	        DatagramPacket paqRecibido = new DatagramPacket(recibidos, recibidos.length);
	        socket.receive(paqRecibido);

	        String respuesta = new String(paqRecibido.getData()).trim();
	        System.out.println("Respuesta del servidor: " + respuesta);
		}
		//Cerrar el cliente
        socket.close();
        sc.close();
    }

}
