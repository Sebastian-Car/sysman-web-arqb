package com.sysman.kernel.api.clientwso2.dbs;

/**
 * Contrato comun para obtener queries y recursos .dbs por CODIGO
 * (el mismo identificador que ya usa GenericUrlEnum/UrlServiceUtil),
 * sin importar si vienen de un snapshot fijo (DbsRegistry, cargado
 * una sola vez) o de un registro "vivo" que se recarga
 * automaticamente cuando cambian los archivos en un directorio
 * externo (DbsRegistryHolder).
 */
public interface DbsRegistryProvider {

    DbsQuery getQuery(String queryId);

    DbsResource getResource(String codigo);
}
