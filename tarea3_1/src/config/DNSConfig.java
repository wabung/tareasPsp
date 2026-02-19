package config;

import java.io.FileInputStream;
import java.util.Properties;

/**
 * Objeto para la configuración de las propiedades.
 */
public class DNSConfig {
	//Iniciar las propiedades
	private Properties propiedades = new Properties();
	
	/**
	 * Método para cargar las propiedades del archivo DNS.
	 * @throws Exception
	 */
	public void cargar() throws Exception{
		//Obtener entrada del archivo
		FileInputStream lector = new FileInputStream("dns.properties");
		//Cargar las propiedades del archivo
		propiedades.load(lector);
	}

	/**
	 * Método para obtener la ip del dominio que se introduzca.
	 * @param dominio
	 * @return
	 */
    public String getIp(String dominio) {
        return propiedades.getProperty(dominio);
    }
}
