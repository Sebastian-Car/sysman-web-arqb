package com.sysman.recursos.ejb.impl;

import com.sysman.enums.ParametrosConstantes;
import com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException;
import com.sysman.exception.SystemException;
import com.sysman.kernel.api.clientwso2.beans.Parameter;
import com.sysman.kernel.api.clientwso2.connectors.RequestManager;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.logica.Aplicacion;
import com.sysman.logica.Formulario;
import com.sysman.logica.Grupo;
import com.sysman.logica.Usuario;
import com.sysman.recursos.auth.util.ValidadorMenus;
import com.sysman.recursos.ejb.EjbAutorizacionLocal;
import com.sysman.recursos.ejb.EjbAutorizacionRemote;
import com.sysman.recursos.ejb.EjbMenukRemote;
import com.sysman.recursos.ejb.EjbSysmanUtilRemote;
import com.sysman.util.SysmanFunciones;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;

/**
 * Session Bean implementation class EjbAutorizacion Centraliza los
 * procesos necesarios para la autorizacion dentro de la aplicacion,
 * como son obtencion de grupos, menus y permisos
 */
@Stateless
@LocalBean
public class EjbAutorizacion
                implements EjbAutorizacionRemote, EjbAutorizacionLocal {

    /**
     * Codigo del servicio para la obtencion de grupos a los que
     * pertenece el usuario logeado
     */
    private static final String CODIGO_URL_GRUPOS = "47006";

    /**
     * Codigo del servicio para la obtencion de permisos a los que
     * pertenece el usuario logeado
     */
    private static final String CODIGO_URL_PERMISOS = "56001";

    /**
     * Objeto implementacion de EjbMenu el cual admiistra la obtencion
     * de las opciones de menu para el usuario bloqueado
     */
    @EJB
    private EjbMenukRemote ejbMenu;

    /**
     * Objeto implementacion de EjbSysmnUtil utilizado en la obtencion
     * de parametros y demas funcionalidades generales
     */
    @EJB
    private EjbSysmanUtilRemote ejbSysmanUtil;

    /**
     * Default constructor.
     */
    public EjbAutorizacion() {
        //
    }

    @Override
    public Map<String, Grupo> getGruposAutorizacion(String usuario,
        String compania) throws SysmanException {
        RequestManager rq = new RequestManager();
        Map<String, Object> par = new HashMap<>();
        par.put(ParametrosConstantes.CODIGO.getValue(), usuario);
        par.put(ParametrosConstantes.COMPANIA.getValue(), compania);
        List<Parameter> parGrupos;
        try {
            parGrupos = rq.getList(UrlServiceUtil
                            .getUrlBeanById(CODIGO_URL_GRUPOS).getUrl(), par);
        }
        catch (SystemException e) {
            throw new SysmanException(e, e.getMessage());
        }
        Map<String, Grupo> grupos = new HashMap<>();
        for (Parameter parameter : parGrupos) {

            Aplicacion aplicacion = new Aplicacion((int) parameter
                            .getFields()
                            .get(ParametrosConstantes.APLICACION
                                            .getValue()),
                            SysmanFunciones.toString(
                                            parameter.getFields()
                                                            .get("NOMBRE_APP")),
                            SysmanFunciones.toString(
                                            parameter.getFields()
                                                            .get("AREA")),
                            (int) parameter.getFields()
                                            .get("DIASHABILES"),
                            SysmanFunciones.toString(
                                            parameter
                                                            .getFields()
                                                            .get("RUTA_ARCHIVOS")),
                            SysmanFunciones.toString(
                                            parameter
                                                            .getFields()
                                                            .get("RUTA_DOCUMENTOS")));

            Grupo grupo = new Grupo(SysmanFunciones.toString(parameter
                            .getFields().get("CODIGO")),
                            (int) parameter.getFields()
                                            .get(ParametrosConstantes.APLICACION
                                                            .getValue()),
                            SysmanFunciones.nvlZero(parameter.getFields()
                                            .get("NIVEL_GRUPO")),
                            SysmanFunciones.nvlZero(parameter.getFields()
                                            .get("NIVEL_USUARIO")),
                            aplicacion, (boolean) parameter
                                            .getFields()
                                            .get("MOD_COMPROBANTE"),
                            (boolean) parameter.getFields()
                                            .get("ES_ADMINISTRADOR"));

            grupos.put(Integer.toString(grupo.getAplicacion()), grupo);
        }
        return grupos;
    }

    @Override
    public Object[] getXMLMenus(String compania,
        Usuario usuario)
                    throws SysmanException {
        try {
            Object[] rta = new Object[2];
            List<String> excluidos = new ArrayList<>();
            rta[0] = ValidadorMenus.getXmlMenus(
                            ejbMenu.retornarXMLMenus(compania,
                                            usuario.getCodigo()),
                            compania, excluidos, usuario, ejbSysmanUtil);
            rta[1] = excluidos;
            return rta;
        }
        catch (SystemException e) {
            throw new SysmanException(e, e.getMessage());
        }
    }

    @Override
    public Map<String, Formulario> getPermisos(String compania, String usuario)
                    throws SysmanException {

        RequestManager rq = new RequestManager();
        Map<String, Object> par = new HashMap<>();
        par.put(ParametrosConstantes.CODIGO.getValue(), usuario);
        par.put(ParametrosConstantes.COMPANIA.getValue(), compania);
        List<Parameter> parPermisos;
        try {
            parPermisos = rq.getList(UrlServiceUtil
                            .getUrlBeanById(CODIGO_URL_PERMISOS).getUrl(), par);
        }
        catch (SystemException e) {
            throw new SysmanException(e, e.getMessage());
        }
        Map<String, Formulario> permisos = new HashMap<>();

        int ancho;
        int alto;

        for (Parameter parameter : parPermisos) {
            ancho = (int) parameter.getFields().get("ANCHO_MODAL");
            alto = (int) parameter.getFields().get("ALTO_MODAL");

            Formulario form = new Formulario(SysmanFunciones
                            .toString(parameter.getFields()
                                            .get("RUTA")),
                            (boolean) parameter.getFields()
                                            .get("MODAL"),
                            new boolean[] { (boolean) parameter
                                            .getFields()
                                            .get(
                                                            "ADICIONAR"),
                                            (boolean) parameter
                                                            .getFields()
                                                            .get("BORRAR"),
                                            (boolean) parameter
                                                            .getFields()
                                                            .get("MODIFICAR"),
                                            (boolean) parameter
                                                            .getFields()
                                                            .get("CONSULTAR"),
                                            (boolean) parameter
                                                            .getFields()
                                                            .get("EXPORTAR") });
            if (ancho != 0) {
                if (alto == 0) {
                    form.setAnchoModal(ancho);
                }
                else {
                    form.setAnchoModal(ancho);
                    form.setAltoModal(alto);
                }
            }
            permisos.put((int) parameter.getFields().get("FORMULARIO")
                + ","
                + SysmanFunciones.toString(parameter.getFields()
                                .get("APLICACION")),
                            form);
        }

        return permisos;
    }

}
