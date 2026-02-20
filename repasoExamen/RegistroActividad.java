import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class RegistroActividad {
    private static final DateTimeFormatter FORMATO = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private static PrintWriter escritor;
    private static final Object candado = new Object();

    static {
        try {
            escritor = new PrintWriter(new FileWriter("server.log", true), true);
        } catch (IOException e) {
            escritor = null;
        }
    }

    public static void registrar(String ipCliente, String evento) {
        String linea = "[" + LocalDateTime.now().format(FORMATO) + "] [" + ipCliente + "] " + evento;
        synchronized (candado) {
            System.out.println(linea);
            if (escritor != null) {
                escritor.println(linea);
            }
        }
    }
}