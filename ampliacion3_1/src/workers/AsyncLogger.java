package workers;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Logger asíncrono para registro concurrente de eventos del servidor.
 * Usa una cola bloqueante (BlockingQueue) para procesar logs sin bloquear los hilos de trabajo.
 */
public class AsyncLogger {
    private static AsyncLogger instance;
    private final BlockingQueue<String> logQueue;
    private final ExecutorService executor;
    private final DateTimeFormatter formatter;
    private volatile boolean running;
    private PrintWriter fileWriter;

    /**
     * Constructor privado para patrón Singleton
     */
    private AsyncLogger() {
        this.logQueue = new LinkedBlockingQueue<>();
        this.executor = Executors.newSingleThreadExecutor();
        this.formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        this.running = true;
        
        try {
            // Abrir archivo en modo append (agregar al final)
            this.fileWriter = new PrintWriter(new FileWriter("server.log", true), true);
        } catch (IOException e) {
            System.err.println("Error al abrir archivo de log: " + e.getMessage());
            this.fileWriter = null;
        }
        
        // Iniciar el hilo que procesa logs de forma asíncrona
        executor.submit(this::processLogs);
    }

    /**
     * Obtiene la instancia única del logger (patrón Singleton)
     */
    public static synchronized AsyncLogger getInstance() {
        if (instance == null) {
            instance = new AsyncLogger();
        }
        return instance;
    }

    /**
     * Registra un evento de forma asíncrona
     * @param ip Dirección IP del cliente
     * @param command Comando recibido
     * @param response Respuesta enviada
     */
    public void log(String ip, String command, String response) {
        String timestamp = LocalDateTime.now().format(formatter);
        String logMessage = String.format("[%s] [%s] RECV: %s -> RESP: %s", 
            timestamp, ip, command, response);
        
        // Encolar el mensaje sin bloquear (non-blocking)
        logQueue.offer(logMessage);
    }


    /**
     * Procesa los logs de forma asíncrona desde la cola
     */
    private void processLogs() {
        while (running) {
            try {
                // Tomar un mensaje de la cola (bloquea si está vacía)
                String logMessage = logQueue.take();
                
                // Escribir al console
                System.out.println(logMessage);
                
                // Escribir al archivo si está disponible
                if (fileWriter != null) {
                    fileWriter.println(logMessage);
                    // flush() automático por auto-flush en constructor
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
    }

    /**
     * Detiene el logger de forma ordenada
     */
    public void shutdown() {
        running = false;
        executor.shutdown();
        
        // Cerrar el archivo
        if (fileWriter != null) {
            fileWriter.close();
        }
    }
}
