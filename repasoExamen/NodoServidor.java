import java.io.*;
import java.net.*;

public class NodoServidor {
    private static final int PUERTO = 5000;

    public static void main(String[] args) {
        File dirBase = new File("servidor_raiz");
        if (!dirBase.exists()) {
            dirBase.mkdirs();
        }

        try (ServerSocket socketServidor = new ServerSocket(PUERTO)) {
            System.out.println("Servidor iniciado en el puerto " + PUERTO);
            while (true) {
                Socket conexion = socketServidor.accept();
                GestorConexion gestor = new GestorConexion(conexion, dirBase);
                gestor.start();
            }
        } catch (IOException e) {
            System.err.println("Error al iniciar el servidor: " + e.getMessage());
        }
    }
}