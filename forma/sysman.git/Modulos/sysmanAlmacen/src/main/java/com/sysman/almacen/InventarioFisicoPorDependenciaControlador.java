/*-
 * InventarioFisicoPorDependenciaControlador.java
 *
 * 1.0
 *
 * 26/01/2017
 *
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */
package com.sysman.almacen;

import com.sysman.almacen.enums.InventarioFisicoPorDependenciaControladorEnum;
import com.sysman.almacen.enums.InventarioFisicoPorDependenciaControladorUrlEnum;
import com.sysman.beanbase.BeanBaseModal;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.ReportesBean.FORMATOS;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.persistencia.ConectorPool;
import com.sysman.recursos.ejb.EjbSysmanUtilRemote;
import com.sysman.reportes.Reporteador;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.util.SysmanFunciones;

import java.io.IOException;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.event.SelectEvent;
import org.primefaces.model.StreamedContent;

import net.sf.dynamicreports.report.exception.DRException;
import net.sf.jasperreports.engine.JRException;

/**
 * Clase que permite la generacion de informes del invemtario fisico por dependencia a traves de la seleccion de una serie de atributos
 *
 * @version 1.0, 26/01/2017
 * @author jreina
 *
 * @author eamaya
 * @version 2, 02/05/2017 Proceso de Refactoring, Manejo de EJBs y Correcciones SonarLint
 *
 * @version 3.0, 13/06/2017, <strong>pespitia</strong>:<br>
 * Reemplazar numero del formulario por enumerado.<br>
 * Reemplazar los llamados a ConectorPool por el esquema ConectorPool.ESQUEMA_SYSMAN.
 *
 * @author eamaya
 * @version 3.1, 11/07/2017, Invalidacion del método generarExcel()
 *
 */
@ManagedBean
@ViewScoped
public class InventarioFisicoPorDependenciaControlador extends BeanBaseModal {
    /**
     * Constante a nivel de clase que almacena el codigo de la compania en la cual inicio sesion el usuario, el valor de esta constante es asignado en el constructor a la variable de sesion
     * correspondiente
     */
    private final String compania;

    // <DECLARAR_ATRIBUTOS>
    /**
     * Atributo que contiene el valor asignado para generar el reprte con o sin firma del responsable
     */
    private String tipoFormato;
    /**
     * Atributo que contiene el valor asignado a la dependencia inicial de la forma del formulario
     */
    private String dependenciaInicial;
    /**
     * Atributo que contiene el valor asignado a la dependencia final de la forma del formulario
     */
    private String dependenciaFinal;
    /**
     * Atributo que contiene el valor asignado a la fecha inicial de la forma del formulario
     */
    private Date fechaInicial;

    /**
     * Atributo usado para descargar contenidos de archivos desde la vista
     */
    private StreamedContent archivoDescarga;

    /**
     * Atributo que contiene el valor asignado a la agrupacion de la forma del formulario
     */
    private String agrupacion;

    /**
     * Atributo que contiene el valor asignado al parametro DEFENSA CIVIL MANEJA NUEVAS COLUMNAS
     */
    private boolean manejaNuevasColumnas;
    /**
     * Atributo que contiene el valor asignado al parametro que permite la visualizacion de combo fechainicial
     */
    private boolean visibleNuevasColumnas;
    /**
     * Atributo que contiene el valor asignado al parametro RESPONSABLE ALMACEN
     */
    private String responsableAlmacen;
    // </DECLARAR_ATRIBUTOS>
    // <DECLARAR_PARAMETROS>
    // </DECLARAR_PARAMETROS>
    // <DECLARAR_LISTAS>
    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>
    /**
     * /** Lista que contiene los detalles del combo dependencia inicial.
     */
    private RegistroDataModelImpl listaDependenciaInicial;
    /**
     * /** Lista que contiene los detalles del combo dependencia final.
     */
    private RegistroDataModelImpl listaDependenciaFinal;

    @EJB

    private EjbSysmanUtilRemote ejbParametro;

    // </DECLARAR_LISTAS_COMBO_GRANDE>
    /**
     * Crea una nueva instancia de InventarioFisicoPorDependenciaControlador
     */
    public InventarioFisicoPorDependenciaControlador() {
        super();
        compania = SessionUtil.getCompania();
        try {
            // 1274
            numFormulario = GeneralCodigoFormaEnum.INVENTARIO_FISICO_POR_DEPENDENCIA_CONTROLADOR
                            .getCodigo();

            validarPermisos();
            // <INI_ADICIONAL>
            // </INI_ADICIONAL>
        }
        catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
            SessionUtil.redireccionarMenuPermisos();
        }
    }

    /**
     * Este metodo se ejecuta justo despues de que el objeto de la clase del Bean ha sido creado, en este se realizan las asignaciones iniciales necesarias para la visualizacion del formulario, como
     * son tablas, origenes de datos, inicializacion de listas y demas necesarios
     */
    @PostConstruct
    public void inicializar() {
        // <CARGAR_LISTA>
        // </CARGAR_LISTA>
        // <CARGAR_LISTA_COMBO_GRANDE>
        cargarListaDependenciaInicial();

        // </CARGAR_LISTA_COMBO_GRANDE>
        // <CREAR_ARBOLES>
        // </CREAR_ARBOLES>
        abrirFormulario();
    }

    /**
     * Este metodo es invocado el metodo inicializar, se ejecutan las acciones a tener en cuenta en el momento de apertura del formulario
     */
    @Override
    public void abrirFormulario() {
        // <CODIGO_DESARROLLADO>
        visibleNuevasColumnas = false;

        tipoFormato = "1";
        try {
            String parManejaNuevasColumnas = ejbParametro.consultarParametro(
                            compania, "MANEJA FECHA E INFORME EXCEL",
                            SessionUtil.getModulo(), new Date(), false);

            manejaNuevasColumnas = ("SI").equals(parManejaNuevasColumnas);

            responsableAlmacen = ejbParametro.consultarParametro(compania,
                            "RESPONSABLE ALMACEN", SessionUtil.getModulo(),
                            new Date(), false);

            agrupacion = (String) SysmanFunciones
                            .nvl(ejbParametro.consultarParametro(compania,
                                            "DIGITOS AGRUPACION INVENTARIO",
                                            SessionUtil.getModulo(), new Date(),
                                            false), "3");
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

        if (manejaNuevasColumnas) {
            visibleNuevasColumnas = true;
        }
        // </CODIGO_DESARROLLADO>
    }

    // <METODOS_CARGAR_LISTA>
    /**
     *
     * Carga la lista listaDependenciaInicial
     *
     */
    public void cargarListaDependenciaInicial() {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        InventarioFisicoPorDependenciaControladorUrlEnum.URL7530
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        listaDependenciaInicial = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, GeneralParameterEnum.CODIGO.getName());
    }

    /**
     *
     * Carga la lista listaDependenciaFinal
     *
     */
    public void cargarListaDependenciaFinal() {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        InventarioFisicoPorDependenciaControladorUrlEnum.URL8418
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
        param.put(InventarioFisicoPorDependenciaControladorEnum.PARAM2
                        .getValue(), dependenciaInicial);

        listaDependenciaFinal = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, GeneralParameterEnum.CODIGO.getName());
    }
    // </METODOS_CARGAR_LISTA>
    // <METODOS_BOTONES>

    private void generarReporte(FORMATOS formato) {
        try {
            Map<String, Object> reemplazar = new HashMap<>();
            Map<String, Object> parametros = new HashMap<>();
            String reporte;
            String consulta;
            String condicionFecha = " AND DEVOLUTIVO.FECHAADQUISICION <= "
                            + SysmanFunciones.formatearFecha(fechaInicial) + "  ";

            if (manejaNuevasColumnas) {
                if ("2".equals(tipoFormato)) {
                    reporte = "001377CInvIndivDevoluDepen1";
                    consulta = reporte;
                }
                else {
                    reporte = "001383CInvIndivDevoluDepenEspec1";
                    consulta = reporte;
                }
            }
            else {
                if ("2".equals(tipoFormato)) {
                    reporte = "001384CInvIndivDevoluDepen";
                    consulta = "001377CInvIndivDevoluDepen1";
                }
                else {
                    reporte = "001386CInvIndivDevoluDepenEspec";
                    consulta = "001383CInvIndivDevoluDepenEspec1";
                }
            }

            reemplazar.put("compania", "'" + compania + "'");
            reemplazar.put("dependenciaInicial",
                            "'" + dependenciaInicial + "'");
            reemplazar.put("dependenciaFinal", "'" + dependenciaFinal + "'");
            reemplazar.put("fechaInicial", manejaNuevasColumnas
                            ? condicionFecha : "");
            reemplazar.put("agrupacion", "'" + agrupacion + "'");
            //
            parametros.put("PR_RESPONSABLE_ALMACEN", responsableAlmacen);
            parametros.put("PR_FECHAINICIAL", SysmanFunciones
                            .convertirAFechaCadena(fechaInicial));

            Reporteador.resuelveConsulta(consulta,
                            Integer.parseInt(SessionUtil.getModulo()),
                            reemplazar,
                            parametros);

            archivoDescarga = JsfUtil.exportarStreamed(reporte, parametros,
                            ConectorPool.ESQUEMA_SYSMAN, formato);

        }
        catch (OutOfMemoryError | JRException | IOException | ParseException
                        | SysmanException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    /**
     * Este método anteriormente funcionaba para generar un único Excel cuando el nit de la compania es el de la Defensa Civil, se procedio a invalidarlo y porque a consulta de los reportes PDF
     * comparada con este es la misma. Este método era invocado desde el método oprimirComando22()
     */

    private void generarExcel() {
        // METODO NO IMPLEMENTADO
        String condicionFecha = " AND DEVOLUTIVO.FECHAADQUISICION <= "
                        + SysmanFunciones.formatearFecha(fechaInicial) + "  ";

        try {
            Map<String, Object> reemplazos = new HashMap<>();
            reemplazos.put("compania", "'" + compania + "'");
            reemplazos.put("dependenciaInicial",
                            "'" + dependenciaInicial + "'");
            reemplazos.put("dependenciaFinal", "'" + dependenciaFinal + "'");
            reemplazos.put("fechaInicial", manejaNuevasColumnas
                            ? condicionFecha : "");
            reemplazos.put("agrupacion", "'" + agrupacion + "'");
            String strSql = Reporteador.resuelveConsulta(
                            "800086InventarioPorDependencia",
                            Integer.parseInt(SessionUtil.getModulo()),
                            reemplazos);

            List<Registro> rs = service.getListado(
                            ConectorPool.ESQUEMA_SYSMAN,
                            strSql);

            if (!rs.isEmpty()) {
                archivoDescarga = JsfUtil.exportarHojaDatosStreamed(strSql,
                                ConectorPool.ESQUEMA_SYSMAN,
                                FORMATOS.EXCEL);
            }
            else {
                JsfUtil.agregarMensajeError(idioma.getString(
                                "TG_NO_EXISTE_INFORMACION_CON_LOS_PARAMETROS_SUMINISTRADOS"));
            }
        }
        catch (JRException | IOException | SQLException | DRException
                        | SysmanException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    /**
     *
     * Metodo ejecutado al oprimir el boton cmdPantalla en la vista
     *
     *
     */
    public void oprimircmdPantalla() {
        // <CODIGO_DESARROLLADO>
        archivoDescarga = null;
        generarReporte(FORMATOS.PDF);
        // </CODIGO_DESARROLLADO>
    }

    /**
     *
     * Metodo ejecutado al oprimir el boton Comando22 en la vista
     *
     *
     */
    public void oprimirComando22() {
        // <CODIGO_DESARROLLADO>
        archivoDescarga = null;
        generarReporte(FORMATOS.EXCEL97);
        // </CODIGO_DESARROLLADO>
    }

    // </METODOS_BOTONES>
    // <METODOS_CAMBIAR>
    // </METODOS_CAMBIAR>
    // <METODOS_COMBOS_GRANDES>
    /**
     *
     * Metodo ejecutado al seleccionar una fila de la lista listaDependenciaInicial
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaDependenciaInicial(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        dependenciaInicial = SysmanFunciones
                        .nvl(registroAux.getCampos()
                                        .get(GeneralParameterEnum.CODIGO
                                                        .getName()),
                                        "")
                        .toString();
        cargarListaDependenciaFinal();
    }

    /**
     *
     * Metodo ejecutado al seleccionar una fila de la lista listaDependenciaFinal
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaDependenciaFinal(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        dependenciaFinal = SysmanFunciones.nvl(registroAux.getCampos()
                        .get(GeneralParameterEnum.CODIGO.getName()), "")
                        .toString();
    }

    // </METODOS_COMBOS_GRANDES>
    // <METODOS_ARBOL>
    // </METODOS_ARBOL>
    // <SET_GET_ATRIBUTOS>
    /**
     * Retorna la variable tipoFormato
     *
     * @return tipoFormato
     */
    public String getTipoFormato() {
        return tipoFormato;
    }

    /**
     * Asigna la variable tipoFormato
     *
     * @param tipoFormato
     * Variable a asignar en tipoFormato
     */
    public void setTipoFormato(String tipoFormato) {
        this.tipoFormato = tipoFormato;
    }

    /**
     * Retorna la variable dependenciaInicial
     *
     * @return dependenciaInicial
     */
    public String getDependenciaInicial() {
        return dependenciaInicial;
    }

    /**
     * Asigna la variable dependenciaInicial
     *
     * @param dependenciaInicial
     * Variable a asignar en dependenciaInicial
     */
    public void setDependenciaInicial(String dependenciaInicial) {
        this.dependenciaInicial = dependenciaInicial;
    }

    /**
     * Retorna la variable dependenciaFinal
     *
     * @return dependenciaFinal
     */
    public String getDependenciaFinal() {
        return dependenciaFinal;
    }

    /**
     * Asigna la variable dependenciaFinal
     *
     * @param dependenciaFinal
     * Variable a asignar en dependenciaFinal
     */
    public void setDependenciaFinal(String dependenciaFinal) {
        this.dependenciaFinal = dependenciaFinal;
    }

    /**
     * Retorna la variable fechaInicial
     *
     * @return fechaInicial
     */
    public Date getFechaInicial() {
        return fechaInicial;
    }

    /**
     * Asigna la variable fechaInicial
     *
     * @param fechaInicial
     * Variable a asignar en fechaInicial
     */
    public void setFechaInicial(Date fechaInicial) {
        this.fechaInicial = fechaInicial;
    }

    /**
     * Retorna la variable visibleNuevasColumnas
     *
     * @return visibleNuevasColumnas
     */
    public boolean isVisibleNuevasColumnas() {
        return visibleNuevasColumnas;
    }

    /**
     * Asigna la variable visibleNuevasColumnas
     *
     * @param visibleNuevasColumnas
     * Variable a asignar en visibleNuevasColumnas
     */
    public void setVisibleNuevasColumnas(boolean visibleNuevasColumnas) {
        this.visibleNuevasColumnas = visibleNuevasColumnas;
    }

    /**
     * Retorna la variable archivoDescarga
     *
     * @return archivoDescarga
     */
    public StreamedContent getArchivoDescarga() {
        return archivoDescarga;
    }

    /**
     * Asigna la variable archivoDescarga
     *
     * @param archivoDescarga
     * Variable a asignar en archivoDescarga
     */
    public void setArchivoDescarga(StreamedContent archivoDescarga) {
        this.archivoDescarga = archivoDescarga;
    }

    // </SET_GET_ATRIBUTOS>
    // <SET_GET_PARAMETROS>
    // </SET_GET_PARAMETROS>
    // <SET_GET_LISTAS>
    // </SET_GET_LISTAS>
    // <SET_GET_LISTAS_COMBO_GRANDE>
    /**
     * Retorna la lista listaDependenciaInicial
     *
     * @return listaDependenciaInicial
     */
    public RegistroDataModelImpl getListaDependenciaInicial() {
        return listaDependenciaInicial;
    }

    /**
     * Asigna la lista listaDependenciaInicial
     *
     * @param listaDependenciaInicial
     * Variable a asignar en listaDependenciaInicial
     */
    public void setListaDependenciaInicial(
                    RegistroDataModelImpl listaDependenciaInicial) {
        this.listaDependenciaInicial = listaDependenciaInicial;
    }

    /**
     * Retorna la lista listaDependenciaFinal
     *
     * @return listaDependenciaFinal
     */
    public RegistroDataModelImpl getListaDependenciaFinal() {
        return listaDependenciaFinal;
    }

    /**
     * Asigna la lista listaDependenciaFinal
     *
     * @param listaDependenciaFinal
     * Variable a asignar en listaDependenciaFinal
     */
    public void setListaDependenciaFinal(
                    RegistroDataModelImpl listaDependenciaFinal) {
        this.listaDependenciaFinal = listaDependenciaFinal;
    }
    // </SET_GET_LISTAS_COMBO_GRANDE>
}
