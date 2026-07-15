package com.sysman.kernel.api.clientwso2.dbs;

import javax.naming.InitialContext;
import javax.sql.DataSource;
import java.io.InputStream;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

/**
 * Traduce el "carbon_datasource_name" declarado en un .dbs (ej.
 * SYSMANIRISST) al recurso JNDI real configurado en WildFly, y
 * cachea las DataSource ya resueltas.
 *
 * El mapeo vive en config/dbs-datasource.properties, para no tocar
 * los .dbs ni hardcodear nombres JNDI en el codigo.
 */
public class DataSourceResolver {

    private static final Logger LOG = Logger.getLogger(DataSourceResolver.class.getName());

    private final Properties mapeo = new Properties();
    private final ConcurrentHashMap<String, DataSource> cache = new ConcurrentHashMap<>();

    public DataSourceResolver() {
        try (InputStream in = getClass().getClassLoader()
            .getResourceAsStream("config/dbs-datasource.properties")) {
            if (in != null) {
                mapeo.load(in);
            }
        }
        catch (Exception e) {
            LOG.warning("No se pudo cargar dbs-datasource.properties: " + e.getMessage());
        }
    }

    public DataSource resolve(String dataSourceConfigId) throws Exception {
        return cache.computeIfAbsent(dataSourceConfigId, id -> {
            try {
                String jndiName = mapeo.getProperty(id, mapeo.getProperty("default"));
                if (jndiName == null) {
                    throw new IllegalStateException(
                        "No hay JNDI mapeado para el datasource logico '" + id
                            + "'. Revisa config/dbs-datasource.properties");
                }
                InitialContext ic = new InitialContext();
                return (DataSource) ic.lookup(jndiName);
            }
            catch (Exception e) {
                throw new RuntimeException(
                    "No fue posible resolver el datasource '" + id + "'", e);
            }
        });
    }
}
