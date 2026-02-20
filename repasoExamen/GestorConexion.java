import java.io.*;
import java.net.*;
import java.nio.file.*;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class GestorConexion extends Thread {
    private Socket conexion;
    private File dirBase;
    private String ipCliente;

    public GestorConexion(Socket conexion, File dirBase) {
        this.conexion = conexion;
        this.dirBase = dirBase;
        this.ipCliente = conexion.getInetAddress().getHostAddress();
    }

    @Override
    public void run() {
        RegistroActividad.registrar(ipCliente, "NUEVA CONEXIÓN");

        try (
            BufferedReader entrada = new BufferedReader(new InputStreamReader(conexion.getInputStream()));
            OutputStream salidaRaw = conexion.getOutputStream();
            PrintWriter salida = new PrintWriter(salidaRaw, true);
        ) {
            String lineaComando;
            while ((lineaComando = entrada.readLine()) != null) {
                lineaComando = lineaComando.trim();
                if (lineaComando.isEmpty()) continue;

                String[] partes = lineaComando.split("\\s+", 2);
                String comando = partes[0].toLowerCase();
                String argumento = partes.length > 1 ? partes[1] : "";

                switch (comando) {
                    case "quit":
                    case "exit":
                        salida.println("CORRECTO");
                        RegistroActividad.registrar(ipCliente, "RECV: " + lineaComando + " -> RESP: CORRECTO");
                        RegistroActividad.registrar(ipCliente, "CLIENTE DESCONECTADO");
                        conexion.close();
                        return;

                    case "list":
                        procesarList(salida, argumento, lineaComando);
                        break;

                    case "mkdir":
                        procesarMkdir(salida, argumento, lineaComando);
                        break;

                    case "info":
                        procesarInfo(salida, argumento, lineaComando);
                        break;

                    case "head":
                        procesarHead(salida, argumento, lineaComando);
                        break;

                    case "find":
                        procesarFind(salida, argumento, lineaComando);
                        break;

                    case "descarga":
                        procesarDescarga(salida, salidaRaw, argumento, lineaComando);
                        break;

                    case "delete":
                        procesarDelete(salida, argumento, lineaComando);
                        break;

                    default:
                        salida.println("ERROR");
                        RegistroActividad.registrar(ipCliente, "RECV: " + lineaComando + " -> RESP: ERROR (Comando desconocido)");
                        break;
                }
            }
        } catch (IOException e) {
            RegistroActividad.registrar(ipCliente, "CLIENTE DESCONECTADO (cierre inesperado)");
        }
    }

    private File resolverRuta(String ruta) {
        File archivo = new File(dirBase, ruta);
        return archivo;
    }

    private void procesarList(PrintWriter salida, String argumento, String comandoOriginal) {
        File directorio = resolverRuta(argumento.isEmpty() ? "." : argumento);
        if (directorio.exists() && directorio.isDirectory()) {
            salida.println("CORRECTO");
            String[] contenido = directorio.list();
            if (contenido != null) {
                for (String nombre : contenido) {
                    salida.println(nombre);
                }
            }
            salida.println();
            RegistroActividad.registrar(ipCliente, "RECV: " + comandoOriginal + " -> RESP: CORRECTO");
        } else {
            salida.println("ERROR");
            RegistroActividad.registrar(ipCliente, "RECV: " + comandoOriginal + " -> RESP: ERROR (Directorio no encontrado)");
        }
    }

    private void procesarMkdir(PrintWriter salida, String argumento, String comandoOriginal) {
        if (argumento.isEmpty()) {
            salida.println("ERROR");
            RegistroActividad.registrar(ipCliente, "RECV: " + comandoOriginal + " -> RESP: ERROR (Sin argumento)");
            return;
        }
        File nuevaCarpeta = resolverRuta(argumento);
        if (nuevaCarpeta.exists()) {
            salida.println("ERROR");
            RegistroActividad.registrar(ipCliente, "RECV: " + comandoOriginal + " -> RESP: ERROR (La carpeta ya existe)");
        } else {
            if (nuevaCarpeta.mkdirs()) {
                salida.println("CORRECTO");
                RegistroActividad.registrar(ipCliente, "RECV: " + comandoOriginal + " -> RESP: CORRECTO");
            } else {
                salida.println("ERROR");
                RegistroActividad.registrar(ipCliente, "RECV: " + comandoOriginal + " -> RESP: ERROR (Permiso denegado)");
            }
        }
    }

    private void procesarInfo(PrintWriter salida, String argumento, String comandoOriginal) {
        if (argumento.isEmpty()) {
            salida.println("ERROR");
            RegistroActividad.registrar(ipCliente, "RECV: " + comandoOriginal + " -> RESP: ERROR (Sin argumento)");
            return;
        }
        File archivo = resolverRuta(argumento);
        if (archivo.exists()) {
            long ultimaModif = archivo.lastModified();
            LocalDateTime fechaModif = LocalDateTime.ofInstant(Instant.ofEpochMilli(ultimaModif), ZoneId.systemDefault());
            DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
            boolean escritura = archivo.canWrite();
            salida.println("CORRECTO");
            salida.println(fechaModif.format(fmt) + " " + escritura);
            RegistroActividad.registrar(ipCliente, "RECV: " + comandoOriginal + " -> RESP: CORRECTO");
        } else {
            salida.println("ERROR");
            RegistroActividad.registrar(ipCliente, "RECV: " + comandoOriginal + " -> RESP: ERROR (Archivo no encontrado)");
        }
    }

    private void procesarHead(PrintWriter salida, String argumento, String comandoOriginal) {
        if (argumento.isEmpty()) {
            salida.println("ERROR");
            RegistroActividad.registrar(ipCliente, "RECV: " + comandoOriginal + " -> RESP: ERROR (Sin argumento)");
            return;
        }
        File archivo = resolverRuta(argumento);
        if (archivo.exists() && archivo.isFile()) {
            try (BufferedReader lector = new BufferedReader(new FileReader(archivo))) {
                salida.println("CORRECTO");
                String linea;
                int contador = 0;
                while ((linea = lector.readLine()) != null && contador < 5) {
                    salida.println(linea);
                    contador++;
                }
                salida.println();
                RegistroActividad.registrar(ipCliente, "RECV: " + comandoOriginal + " -> RESP: CORRECTO");
            } catch (IOException e) {
                salida.println("ERROR");
                RegistroActividad.registrar(ipCliente, "RECV: " + comandoOriginal + " -> RESP: ERROR (Error de lectura)");
            }
        } else {
            salida.println("ERROR");
            RegistroActividad.registrar(ipCliente, "RECV: " + comandoOriginal + " -> RESP: ERROR (Archivo no encontrado)");
        }
    }

    private void procesarFind(PrintWriter salida, String argumento, String comandoOriginal) {
        if (argumento.isEmpty()) {
            salida.println("ERROR");
            RegistroActividad.registrar(ipCliente, "RECV: " + comandoOriginal + " -> RESP: ERROR (Sin argumento)");
            return;
        }
        File[] archivos = dirBase.listFiles();
        boolean encontrado = false;
        if (archivos != null) {
            for (File f : archivos) {
                if (f.getName().equals(argumento)) {
                    encontrado = true;
                    break;
                }
            }
        }
        salida.println("CORRECTO");
        salida.println(encontrado ? "FOUND" : "NOT_FOUND");
        RegistroActividad.registrar(ipCliente, "RECV: " + comandoOriginal + " -> RESP: CORRECTO (" + (encontrado ? "FOUND" : "NOT_FOUND") + ")");
    }

    private void procesarDescarga(PrintWriter salida, OutputStream salidaRaw, String argumento, String comandoOriginal) {
        if (argumento.isEmpty()) {
            salida.println("ERROR");
            RegistroActividad.registrar(ipCliente, "RECV: " + comandoOriginal + " -> RESP: ERROR (Sin argumento)");
            return;
        }
        File archivo = resolverRuta(argumento);
        if (archivo.exists() && archivo.isFile()) {
            try {
                byte[] contenido = Files.readAllBytes(archivo.toPath());
                salida.println("CORRECTO");
                salida.println(contenido.length);
                salida.flush();
                salidaRaw.write(contenido);
                salidaRaw.flush();
                RegistroActividad.registrar(ipCliente, "RECV: " + comandoOriginal + " -> RESP: CORRECTO");
            } catch (IOException e) {
                salida.println("ERROR");
                RegistroActividad.registrar(ipCliente, "RECV: " + comandoOriginal + " -> RESP: ERROR (Error de lectura)");
            }
        } else {
            salida.println("ERROR");
            RegistroActividad.registrar(ipCliente, "RECV: " + comandoOriginal + " -> RESP: ERROR (Archivo no encontrado)");
        }
    }

    private void procesarDelete(PrintWriter salida, String argumento, String comandoOriginal) {
        if (argumento.isEmpty()) {
            salida.println("ERROR");
            RegistroActividad.registrar(ipCliente, "RECV: " + comandoOriginal + " -> RESP: ERROR (Sin argumento)");
            return;
        }
        File archivo = resolverRuta(argumento);
        if (archivo.exists()) {
            if (archivo.delete()) {
                salida.println("CORRECTO");
                RegistroActividad.registrar(ipCliente, "RECV: " + comandoOriginal + " -> RESP: CORRECTO");
            } else {
                salida.println("ERROR");
                RegistroActividad.registrar(ipCliente, "RECV: " + comandoOriginal + " -> RESP: ERROR (Permiso denegado)");
            }
        } else {
            salida.println("ERROR");
            RegistroActividad.registrar(ipCliente, "RECV: " + comandoOriginal + " -> RESP: ERROR (Archivo no encontrado)");
        }
    }
}