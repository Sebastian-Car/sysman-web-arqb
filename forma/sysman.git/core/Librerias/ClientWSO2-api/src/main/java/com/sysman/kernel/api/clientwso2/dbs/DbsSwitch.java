package com.sysman.kernel.api.clientwso2.dbs;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Decide, para un CODIGO dado (el mismo que GenericUrlEnum: readKey,
 * createKey, updateKey, deleteKey, gridKey), si debe resolverse local
 * (esta clase) o seguir yendo por WSO2, leyendo dbs-switch.properties.
 *
 * Esto es lo que permite migrar .dbs de a uno y hacer rollback
 * inmediato: basta con cambiar el valor y esperar a que se recargue
 * (o reiniciar el nodo si se cargo desde el classpath).
 */
public class DbsSwitch {

    private static final Logger LOG = Logger.getLogger(DbsSwitch.class.getName());

    private final Properties valores = new Properties();

    /** Carga dbs-switch.properties desde el classpath (config/). */
    public static DbsSwitch cargarDesdeClasspath() {
        DbsSwitch dbsSwitch = new DbsSwitch();
        try (InputStream in = DbsSwitch.class.getClassLoader()
            .getResourceAsStream("config/dbs-switch.properties")) {
            if (in != null) {
                dbsSwitch.valores.load(in);
            }
            else {
                LOG.warning("No se encontro config/dbs-switch.properties en el classpath; "
                    + "todo se resuelve remoto por defecto.");
            }
        }
        catch (Exception e) {
            LOG.log(Level.SEVERE, "No se pudo cargar dbs-switch.properties", e);
        }
        return dbsSwitch;
    }

    /**
     * Carga dbs-switch.properties desde un archivo en el mismo
     * directorio externo que los .dbs, para poder migrar sin tocar
     * el .war (ni siquiera reiniciar, si se recarga junto con el
     * resto del directorio).
     */
    public static DbsSwitch cargarDesdeArchivo(Path archivo) {
        DbsSwitch dbsSwitch = new DbsSwitch();
        if (archivo == null || !Files.isRegularFile(archivo)) {
            LOG.warning("No existe dbs-switch.properties en " + archivo
                + "; todo se resuelve remoto por defecto.");
            return dbsSwitch;
        }
        try (InputStream in = Files.newInputStream(archivo)) {
            dbsSwitch.valores.load(in);
        }
        catch (Exception e) {
            LOG.log(Level.SEVERE, "No se pudo cargar " + archivo, e);
        }
        return dbsSwitch;
    }

    public boolean esLocal(String codigo) {
        String valor = valores.getProperty(codigo, valores.getProperty("default", "remoto"));
        return "local".equalsIgnoreCase(valor);
    }
}
