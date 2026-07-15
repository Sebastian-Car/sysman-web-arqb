package com.sysman.kernel.api.clientwso2.dbs;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.InputStream;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Carga en memoria todos los archivos .dbs encontrados bajo un
 * directorio externo del filesystem, y expone un indice por:
 * - id de query -&gt; DbsQuery
 * - CODIGO (el mismo que GenericUrlEnum: readKey, createKey,
 *   updateKey, deleteKey, gridKey) -&gt; DbsResource
 *
 * No modifica ni reescribe los .dbs: los lee y ejecuta la misma
 * consulta SQL localmente, sin pasar por WSO2 DSS.
 *
 * Esquema esperado de <resource>:
 *
 * <resource codigo="320001">
 *     <call-query href="spEmpresasConvenioQuery"/>
 *     <call-query-conteo href="spEmpresasConvenioConteoQuery"/> <!-- opcional -->
 * </resource>
 */
public class DbsRegistry implements DbsRegistryProvider {

    private static final Logger LOG = Logger.getLogger(DbsRegistry.class.getName());

    private final Map<String, DbsQuery> queriesById = new HashMap<>();
    private final Map<String, DbsResource> resourcesPorCodigo = new HashMap<>();

    /**
     * Lee los .dbs desde un directorio real del filesystem (fuera del
     * .war), por ejemplo /opt/sysman/servicios. Permite agregar o
     * modificar .dbs sin recompilar ni redesplegar la aplicacion.
     */
    public static DbsRegistry cargarDesdeDirectorio(Path directorio) {
        DbsRegistry registry = new DbsRegistry();
        if (directorio == null || !Files.isDirectory(directorio)) {
            LOG.warning("Directorio de .dbs externo no valido: " + directorio);
            return registry;
        }
        for (Path ruta : listarArchivosDbs(directorio)) {
            try (InputStream in = Files.newInputStream(ruta)) {
                registry.parsearArchivo(in, ruta.toString());
            }
            catch (Exception e) {
                LOG.log(Level.SEVERE, "Error parseando .dbs: " + ruta, e);
            }
        }
        LOG.info("DbsRegistry cargado desde " + directorio + ": " + registry.queriesById.size()
            + " queries, " + registry.resourcesPorCodigo.size() + " recursos.");
        return registry;
    }

    private static Set<Path> listarArchivosDbs(Path directorio) {
        Set<Path> encontrados = new HashSet<>();
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(directorio)) {
            for (Path entrada : stream) {
                if (Files.isDirectory(entrada)) {
                    encontrados.addAll(listarArchivosDbs(entrada));
                }
                else if (entrada.toString().toLowerCase().endsWith(".dbs")) {
                    encontrados.add(entrada);
                }
            }
        }
        catch (Exception e) {
            LOG.log(Level.SEVERE, "Error listando .dbs en " + directorio, e);
        }
        return encontrados;
    }

    private void parsearArchivo(InputStream in, String rutaOrigen) throws Exception {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document doc = builder.parse(in);
        doc.getDocumentElement().normalize();

        Element root = doc.getDocumentElement(); // <data>

        // 1) Config: id logico -> propiedad carbon_datasource_name
        Map<String, String> dataSourcePorConfigId = new HashMap<>();
        NodeList configs = root.getElementsByTagName("config");
        for (int i = 0; i < configs.getLength(); i++) {
            Element config = (Element) configs.item(i);
            String configId = config.getAttribute("id");
            String dsName = configId;
            NodeList props = config.getElementsByTagName("property");
            for (int j = 0; j < props.getLength(); j++) {
                Element prop = (Element) props.item(j);
                if ("carbon_datasource_name".equals(prop.getAttribute("name"))) {
                    dsName = textoDe(prop);
                }
            }
            dataSourcePorConfigId.put(configId, dsName);
        }

        // 2) Queries
        NodeList queries = root.getElementsByTagName("query");
        for (int i = 0; i < queries.getLength(); i++) {
            Element q = (Element) queries.item(i);
            String id = q.getAttribute("id");
            String useConfig = q.getAttribute("useConfig");
            String dsName = dataSourcePorConfigId.getOrDefault(useConfig, useConfig);

            String sql = null;
            NodeList sqlNodes = q.getElementsByTagName("sql");
            if (sqlNodes.getLength() > 0) {
                sql = textoDe((Element) sqlNodes.item(0));
            }

            DbsQuery dbsQuery = new DbsQuery(id, sql, dsName);

            NodeList params = q.getElementsByTagName("param");
            for (int j = 0; j < params.getLength(); j++) {
                Element p = (Element) params.item(j);
                dbsQuery.addParam(new DbsParam(p.getAttribute("name"), p.getAttribute("sqlType")));
            }

            queriesById.put(id, dbsQuery);
        }

        // 3) Resources, indexados por CODIGO
        NodeList resources = root.getElementsByTagName("resource");
        for (int i = 0; i < resources.getLength(); i++) {
            Element r = (Element) resources.item(i);
            String codigo = r.getAttribute("codigo");
            if (codigo == null || codigo.trim().isEmpty()) {
                LOG.warning("Elemento <resource> sin 'codigo' en " + rutaOrigen + ", se omite.");
                continue;
            }

            String queryId = primerHrefDe(r, "call-query");
            if (queryId == null) {
                LOG.warning("<resource codigo=\"" + codigo + "\"> sin <call-query href=\"...\"/> en "
                    + rutaOrigen + ", se omite.");
                continue;
            }
            String queryIdConteo = primerHrefDe(r, "call-query-conteo"); // opcional

            DbsResource resource = new DbsResource(codigo, queryId, queryIdConteo);
            resourcesPorCodigo.put(resource.getCodigo(), resource);
        }
    }

    private static String primerHrefDe(Element resourceEl, String nombreTag) {
        NodeList nodos = resourceEl.getElementsByTagName(nombreTag);
        if (nodos.getLength() == 0) {
            return null;
        }
        String href = ((Element) nodos.item(0)).getAttribute("href");
        return href == null || href.trim().isEmpty() ? null : href;
    }

    private static String textoDe(Element el) {
        StringBuilder sb = new StringBuilder();
        NodeList hijos = el.getChildNodes();
        for (int i = 0; i < hijos.getLength(); i++) {
            Node n = hijos.item(i);
            if (n.getNodeType() == Node.TEXT_NODE || n.getNodeType() == Node.CDATA_SECTION_NODE) {
                sb.append(n.getNodeValue());
            }
        }
        return sb.toString().trim();
    }

    @Override
    public DbsQuery getQuery(String queryId) {
        return queriesById.get(queryId);
    }

    @Override
    public DbsResource getResource(String codigo) {
        return resourcesPorCodigo.get(codigo == null ? null : codigo.trim());
    }
}
