import java.io.*;
import java.net.*;
import java.util.Scanner;

public class TerminalRemota {
    private static final String SERVIDOR = "localhost";
    private static final int PUERTO = 5000;

    public static void main(String[] args) {
        try (
            Socket enlace = new Socket(SERVIDOR, PUERTO);
            BufferedReader receptor = new BufferedReader(new InputStreamReader(enlace.getInputStream()));
            InputStream flujoBruto = enlace.getInputStream();
            PrintWriter emisor = new PrintWriter(enlace.getOutputStream(), true);
            Scanner teclado = new Scanner(System.in);
        ) {
            System.out.println("Conectado al servidor " + SERVIDOR + ":" + PUERTO);
            System.out.println("Comandos: list, mkdir, info, head, find, descarga, delete, quit, exit");

            while (true) {
                System.out.print("> ");
                if (!teclado.hasNextLine()) break;
                String instruccion = teclado.nextLine().trim();
                if (instruccion.isEmpty()) continue;

                emisor.println(instruccion);

                String[] partes = instruccion.split("\\s+", 2);
                String comando = partes[0].toLowerCase();

                if (comando.equals("quit") || comando.equals("exit")) {
                    String respuesta = receptor.readLine();
                    System.out.println("Servidor: " + respuesta);
                    System.out.println("Sesión finalizada.");
                    break;
                }

                String respuesta = receptor.readLine();
                if (respuesta == null) {
                    System.out.println("El servidor cerró la conexión.");
                    break;
                }
                System.out.println("Servidor: " + respuesta);

                if (respuesta.equals("CORRECTO")) {
                    switch (comando) {
                        case "list":
                        case "head":
                            String linea;
                            while ((linea = receptor.readLine()) != null) {
                                if (linea.isEmpty()) break;
                                System.out.println("  " + linea);
                            }
                            break;

                        case "info":
                            String metadatos = receptor.readLine();
                            if (metadatos != null) {
                                System.out.println("  Metadatos: " + metadatos);
                            }
                            break;

                        case "find":
                            String resultado = receptor.readLine();
                            if (resultado != null) {
                                System.out.println("  Resultado: " + resultado);
                            }
                            break;

                        case "descarga":
                            String tamLinea = receptor.readLine();
                            if (tamLinea != null) {
                                int tamano = Integer.parseInt(tamLinea.trim());
                                System.out.println("  Tamaño del archivo: " + tamano + " bytes");
                                String nombreArchivo = partes.length > 1 ? partes[1] : "descarga";
                                File archivoDestino = new File("descargas_" + new File(nombreArchivo).getName());
                                byte[] buffer = new byte[tamano];
                                int leidos = 0;
                                while (leidos < tamano) {
                                    int r = flujoBruto.read(buffer, leidos, tamano - leidos);
                                    if (r == -1) break;
                                    leidos += r;
                                }
                                try (FileOutputStream fos = new FileOutputStream(archivoDestino)) {
                                    fos.write(buffer, 0, leidos);
                                }
                                System.out.println("  Archivo guardado como: " + archivoDestino.getName());
                            }
                            break;

                        default:
                            break;
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("Error de conexión: " + e.getMessage());
        }
    }
}