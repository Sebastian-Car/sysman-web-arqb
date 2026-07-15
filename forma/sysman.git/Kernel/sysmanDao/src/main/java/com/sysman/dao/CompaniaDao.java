/*
 * To change this license header, choose License Headers in Project
 * Properties. To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sysman.dao;

import com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException;
import com.sysman.exception.SystemException;
import com.sysman.kernel.api.clientwso2.beans.Parameter;
import com.sysman.kernel.api.clientwso2.connectors.RequestManager;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.logica.Compania;
import com.sysman.util.SysmanFunciones;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author cmanrique
 */
public class CompaniaDao {

    private static final String CODIGO_URL_DATOS_AUTENTICACION = "59011";

    public Compania validarCompania(String codigo)
                    throws SysmanException {
        Compania compania = null;

        RequestManager rq = new RequestManager();
        Map<String, Object> par = new HashMap<>();
        par.put("CODIGO", codigo);
        try {
            Parameter parCompania = rq.get(UrlServiceUtil
                            .getUrlBeanById(CODIGO_URL_DATOS_AUTENTICACION)
                            .getUrl(),
                            par);

            if (parCompania != null) {
                compania = new Compania();
                compania.setCodigo(SysmanFunciones.toString(
                                parCompania.getFields().get("CODIGO")));
                compania.setNombre(SysmanFunciones.toString(
                                parCompania.getFields().get("NOMBRE")));
                compania.setRutaImagen(
                                SysmanFunciones.toString(parCompania.getFields()
                                                .get("RUTA_IMAGEN")));
                compania.setRutaVigiladoPor(
                                SysmanFunciones.toString(parCompania.getFields()
                                                .get("RUTA_VIGILADO_POR")));
                compania.setSigla(SysmanFunciones.toString(
                                parCompania.getFields().get("SIGLACOMPANIA")));
                compania.setPais(SysmanFunciones.toString(
                                parCompania.getFields().get("NOMBRE_PAIS")));
                compania.setDepartamento(
                                SysmanFunciones.toString(parCompania.getFields()
                                                .get("NOMBRE_DEPARTAMENTO")));
                compania.setCiudad(SysmanFunciones.toString(
                                SysmanFunciones.toString(parCompania.getFields()
                                                .get("NOMBRE_CIUDAD"))));
                compania.setCodigoPais(SysmanFunciones.toString(
                                parCompania.getFields().get("PAIS")));
                compania.setCodigoDepartamento(
                                SysmanFunciones.toString(parCompania.getFields()
                                                .get("DEPARTAMENTO")));
                compania.setCodigoCiudad(SysmanFunciones.toString(
                                parCompania.getFields().get("CIUDAD")));
                compania.setMoneda(SysmanFunciones.toString(
                                parCompania.getFields().get("MONEDA")));
                compania.setNit(SysmanFunciones.toString(
                                parCompania.getFields().get("NITCOMPANIA")));
                compania.setDireccion(SysmanFunciones.toString(
                                parCompania.getFields().get("DIRECCION")));
                compania.setTelefono(SysmanFunciones.toString(
                                parCompania.getFields().get("TELEFONO")));
                compania.setRetieneFuente(
                                (boolean) parCompania.getFields()
                                                .get("RETENEDOR_FTE"));
                compania.setRetieneIva(
                                (boolean) parCompania.getFields()
                                                .get("RETENEDOR_IVA"));
                compania.setRetieneIca(
                                (boolean) parCompania.getFields()
                                                .get("RETENEDOR_ICA"));
                compania.setRetieneTimbre(
                                (boolean) parCompania.getFields()
                                                .get("RETENEDOR_TBRE"));
                compania.setConsolidada(
                                (boolean) parCompania.getFields()
                                                .get("CONSOLIDADA"));
                compania.setFax(SysmanFunciones
                                .toString(parCompania.getFields().get("FAX")));
                compania.setCodigoDane(SysmanFunciones.toString(
                                parCompania.getFields().get("CODIGODANE")));
                compania.setMision(SysmanFunciones.toString(
                                parCompania.getFields().get("MISION")));
                compania.setVision(SysmanFunciones.toString(
                                parCompania.getFields().get("VISION")));
                compania.setEmail(SysmanFunciones.toString(
                                parCompania.getFields().get("DIRECCIONEMAIL")));
                compania.setPaginaWeb(SysmanFunciones.toString(
                                parCompania.getFields().get("PAGINAWEB")));
                compania.setTipoEntidad(parCompania.getFields()
                                .get("TIPOENTIDAD") != null
                                    ? (int) parCompania.getFields()
                                                    .get("TIPOENTIDAD")
                                    : 0);
                compania.setCodigosChip(
                                SysmanFunciones.toString(parCompania.getFields()
                                                .get("CODIGOSCHIP")));
                compania.setFirmaFactura(
                                SysmanFunciones.toString(parCompania.getFields()
                                                .get("FIRMAFACTURA")));
                compania.setCodigoWeb(SysmanFunciones.toString(
                                parCompania.getFields().get("CODIGO_WEB")));
                compania.setContacto(SysmanFunciones.toString(
                                parCompania.getFields().get("CONTACTO")));
                compania.setRutaSticker(
                                SysmanFunciones.toString(parCompania.getFields()
                                                .get("RUTA_STICKER")));
                compania.setNuir(SysmanFunciones.toString(
                                parCompania.getFields().get("NUIR")));
                compania.setCodigoContaduria(SysmanFunciones.toString(
                                parCompania.getFields().get("CODIGO_CONTADURIA")));
            }
        }
        catch (SystemException e) {
            throw new SysmanException(e, e.getMessage());
        }

        return compania;
    }
}
