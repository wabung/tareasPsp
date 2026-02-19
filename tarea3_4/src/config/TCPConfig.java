package config;

import java.io.FileInputStream;
import java.util.Properties;

public class TCPConfig {
	
	//Iniciar las propiedades
	private Properties propiedades = new Properties();
	
	/**
	 * Método para cargar las propiedades del archivo propiedades.
	 * @throws Exception
	 */
	public void cargar() throws Exception{
		//Obtener entrada del archivo
		FileInputStream lector = new FileInputStream("server.properties");
		//Cargar las propiedades del archivo
		propiedades.load(lector);
	}

	/**
	 * Método para obtener el puerto.
	 * @param clave
	 * @return
	 */
    public int getPort(String clave) {
        return Integer.parseInt(propiedades.getProperty(clave));
    }

}
