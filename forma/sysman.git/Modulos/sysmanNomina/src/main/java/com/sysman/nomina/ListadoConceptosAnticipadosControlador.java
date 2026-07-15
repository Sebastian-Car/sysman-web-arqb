/*-
 * ListadoConceptosAnticipadosControlador.java
 *
 * 1.0
 * 
 * 10/01/2018
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */
package com.sysman.nomina;

import com.sysman.beanbase.BeanBaseModal;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.jsfutil.ReportesBean.FORMATOS;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.nomina.enums.ListadoConceptosAnticipadosControladorUrlEnum;
import com.sysman.persistencia.ConectorPool;
import com.sysman.recursos.ejb.EjbSysmanUtilRemote;
import com.sysman.reportes.Reporteador;
import com.sysman.sesion.SessionBean;
import com.sysman.util.SysmanFunciones;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.model.StreamedContent;

import net.sf.jasperreports.engine.JRException;

/**
 * Esta clase es el controlador para el formulario
 * "Listado Descuentos Anticipados" en Access
 * "Listado_conceptos_anticipados", el cual es llamado desde
 * Nomina\Procesos\Nomina\Conceptos Anticipados
 *
 * @version 1.0, 10/01/2018
 * @author amonroy
 * 
 * @version 2.0,22/02/2018, Se adiciono el indicador consolidado para
 * generar el reporte 001696CONSOLIDADOSDESCUENTOSANE y se modifico el
 * metodo generarInforme()
 * @author eamaya
 */
@ManagedBean
@ViewScoped
public class ListadoConceptosAnticipadosControlador extends BeanBaseModal {
    /**
     * Constante a nivel de clase que almacena el codigo de la
     * compania en la cual inicio sesion el usuario, el valor de esta
     * constante es asignado en el constructor a la variable de sesion
     * correspondiente
     */
    private final String compania;
    /**
     * Constante que almacena el codigo del modulo al que ha ingresado
     * el usuario
     */
    private final String modulo;

    // <DECLARAR_ATRIBUTOS>
    /**
     * Atributo que almacena el anio de trabajo que ha sido
     * seleccionado al ingresar al modulo de Nomina
     */
    private String anio;
    /**
     * Atributo que almacena el mes de trabajo que ha sido
     * seleccionado al ingresar al modulo de Nomina
     */
    private String mes;
    /**
     * Atributo que almacena el periodo de trabajo que ha sido
     * seleccionado al ingresar al modulo de Nomina
     */
    private String periodo;
    /**
     * Atributo que almacena el codigo del proceso que ha sido
     * seleccionado al ingresar al modulo de Nomina
     */
    private String idProceso;

    /**
     * Indicador para generar el reporte de consolidados
     */
    private boolean consolidado;

    /**
     * Indicador para generar el reporte detallado
     */

    private boolean detallado;

    /**
     * Indicador para generar el reporte detallado por entidades
     */
    private boolean detalladoEntidad;

    /**
     * Atributo usado para descargar contenidos de archivos desde la
     * vista
     */
    private StreamedContent archivoDescarga;
    // </DECLARAR_ATRIBUTOS>
    // <DECLARAR_PARAMETROS>
    // </DECLARAR_PARAMETROS>
    // <DECLARAR_LISTAS>
    /**
     * Listado de registros para el combo de anio
     */
    private List<Registro> listaAnio;
    /**
     * Listado de registros para el combo de Mes
     */
    private List<Registro> listaMes;
    /**
     * Listado de registros para el combo de Periodo
     */
    private List<Registro> listaPeriodo;

    /**
     * Atributo utilizado apra guardar la opcion de reporte a imprimir
     */
    private int opcionReporte;

    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>
    // </DECLARAR_LISTAS_COMBO_GRANDE>

    @EJB
    EjbSysmanUtilRemote ejbSysmanUtil;

    /**
     * Crea una nueva instancia de
     * ListadoConceptosAnticipadosControlador
     */
    public ListadoConceptosAnticipadosControlador() {
        super();
        compania = SessionUtil.getCompania();
        modulo = SessionUtil.getModulo();
        consolidado = false;
        detallado = false;
        detalladoEntidad = false;

        try {
            numFormulario = GeneralCodigoFormaEnum.LISTADO_CONCEPTOS_ANTICIPADOS_CONTROLADOR
                            .getCodigo();// 1585
            validarPermisos();
            // <INI_ADICIONAL>
            anio = SysmanFunciones
                            .nvl(SessionUtil.getSessionVar("anioNomina"), "")
                            .toString();

            mes = SysmanFunciones
                            .nvl(SessionUtil.getSessionVar("mesNomina"), "")
                            .toString();

            periodo = SysmanFunciones
                            .nvl(SessionUtil.getSessionVar("periodoNomina"), "")
                            .toString();

            idProceso = SysmanFunciones
                            .nvl(SessionUtil.getSessionVar("procesoNomina"), "")
                            .toString();
            // </INI_ADICIONAL>
        }
        catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
            SessionUtil.redireccionarMenuPermisos();
        }
    }

    /**
     * Este metodo se ejecuta justo despues de que el objeto de la
     * clase del Bean ha sido creado, en este se realizan las
     * asignaciones iniciales necesarias para la visualizacion del
     * formulario, como son tablas, origenes de datos, inicializacion
     * de listas y demas necesarios
     */
    @PostConstruct
    public void inicializar() {
        // <CARGAR_LISTA>
        cargarListaAnio();
        cargarListaMes();
        cargarListaPeriodo();
        // </CARGAR_LISTA>
        // <CARGAR_LISTA_COMBO_GRANDE>
        // </CARGAR_LISTA_COMBO_GRANDE>
        // <CREAR_ARBOLES>
        // </CREAR_ARBOLES>
        abrirFormulario();
    }

    /**
     * Este metodo es invocado el metodo inicializar, se ejecutan las
     * acciones a tener en cuenta en el momento de apertura del
     * formulario
     */
    @Override
    public void abrirFormulario() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    // <METODOS_CARGAR_LISTA>
    /**
     * 
     * Carga la lista listaAnio
     *
     */
    public void cargarListaAnio() {
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.ID_DE_PROCESO.getName(), idProceso);

        try {
            listaAnio = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            ListadoConceptosAnticipadosControladorUrlEnum.URL4910
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    /**
     * 
     * Carga la lista listaMes
     *
     */
    public void cargarListaMes() {
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.ANO.getName(), anio);
        param.put(GeneralParameterEnum.ID_DE_PROCESO.getName(), idProceso);

        try {
            listaMes = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            ListadoConceptosAnticipadosControladorUrlEnum.URL5768
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    /**
     * 
     * Carga la lista listaPeriodo
     *
     */
    public void cargarListaPeriodo() {
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.ID_DE_PROCESO.getName(), idProceso);
        param.put(GeneralParameterEnum.ANO.getName(), anio);
        param.put(GeneralParameterEnum.MES.getName(), mes);

        try {
            listaPeriodo = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            ListadoConceptosAnticipadosControladorUrlEnum.URL6444
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    // </METODOS_CARGAR_LISTA>
    // <METODOS_BOTONES>
    /**
     * 
     * Metodo ejecutado al oprimir el boton BtnPdf en la vista
     *
     * Hace el llamado al metodo "generarInforme" indicando el formato
     * con el que se desea generar el informe
     * 
     */
    public void oprimirBtnPdf() {
        // <CODIGO_DESARROLLADO>
        archivoDescarga = null;
        generarInforme(FORMATOS.PDF);
        // </CODIGO_DESARROLLADO>
    }

    /**
     * 
     * Metodo ejecutado al oprimir el boton BtnExcel en la vista
     *
     * Hace el llamado al metodo "generarInforme" indicando el formato
     * con el que se desea generar el informe
     *
     */
    public void oprimirBtnExcel() {
        // <CODIGO_DESARROLLADO>
        archivoDescarga = null;
        generarInforme(FORMATOS.EXCEL);
        // </CODIGO_DESARROLLADO>
    }

    // </METODOS_BOTONES>
    // <METODOS_CAMBIAR>
    /**
     * Metodo ejecutado al cambiar el control Anio
     * 
     * Actualiza los atributos mes y periodo, adicionalmente carga el
     * listado para wl combo de Mes
     * 
     */
    public void cambiarAnio() {
        // <CODIGO_DESARROLLADO>
        mes = periodo = null;
        cargarListaMes();
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado al cambiar el control Mes
     * 
     * Actualiza el valor del atributo periodo y recarga el listado de
     * registros para el combo de Periodo
     * 
     */
    public void cambiarMes() {
        // <CODIGO_DESARROLLADO>
        periodo = null;
        cargarListaPeriodo();
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado al cambiar el control Consolidado
     * 
     */
    public void cambiarConsolidado() {
        detallado = false;
        detalladoEntidad = false;
        opcionReporte = 1;
        if (!consolidado) {
            opcionReporte = 4;
        }
    }

    /**
     * Metodo ejecutado al cambiar el control Detallado
     * 
     */
    public void cambiarDetallado() {
        consolidado = false;
        detalladoEntidad = false;
        opcionReporte = 2;
        if (!detallado) {
            opcionReporte = 4;
        }

    }

    /**
     * Metodo ejecutado al cambiar el control DetalladoEntidad
     * 
     */
    public void cambiarDetalladoEntidad() {
        consolidado = false;
        detallado = false;
        opcionReporte = 3;
        if (!detalladoEntidad) {
            opcionReporte = 4;
        }
    }

    // </METODOS_CAMBIAR>
    // <METODOS_COMBOS_GRANDES>
    // </METODOS_COMBOS_GRANDES>
    // <METODOS_ARBOL>
    /**
     * Define las acciones necesarias para generar el informe realiza
     * el reemplazo de valores en la consulta del informe y envía los
     * parámetros definidos
     * 
     * @param formato
     * Formato seleccionado por el usuario para generar el informe
     */
    private void generarInforme(FORMATOS formato) {
        Map<String, Object> parametros = new HashMap<>();
        Map<String, Object> reemplazar = new HashMap<>();
        String informe;

        switch (opcionReporte) {
        case 1:
            informe = "001696CONSOLIDADOSDESCUENTOSANE";
            generarConsolidado(parametros, reemplazar, formato, informe);

            break;
        case 2:
            informe = "001753CONSOLIDADOSDESCUENTOSANEDETALLADO";
            generarConsolidado(parametros, reemplazar, formato, informe);
            break;
        case 3:
            informe = "001754CONSOLIDADOSDESCUENTOSANEDETALLADOENTIDAD";
            generarConsolidado(parametros, reemplazar, formato, informe);
            break;

        default:
            informe = "001626ListadoConceptosAnticipados";
            generarAnticipados(parametros, reemplazar, formato, informe);
            break;
        }

    }

    private void generarAnticipados(Map<String, Object> parametros,
        Map<String, Object> reemplazar, FORMATOS formato, String informe) {

        try {
            reemplazar.put("proceso", idProceso);
            reemplazar.put("anio", anio);
            reemplazar.put("mes", mes);
            reemplazar.put("periodo", periodo);
            // PARAMETROS PARA GENERACION DE INFORME

            parametros.put("PR_NOMBREEMPRESA",
                            SessionUtil.getCompaniaIngreso().getNombre());

            Reporteador.resuelveConsulta(informe,
                            Integer.parseInt(modulo), reemplazar,
                            parametros);

            archivoDescarga = JsfUtil.exportarStreamed(
                            informe,
                            parametros,
                            ConectorPool.ESQUEMA_SYSMAN,
                            formato);
        }
        catch (JRException | IOException | SysmanException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    private void generarConsolidado(Map<String, Object> parametros,
        Map<String, Object> reemplazar, FORMATOS formato, String informe) {

        try {
            reemplazar.put("proceso", idProceso);
            reemplazar.put("anio", anio);
            reemplazar.put("mes", mes);
            reemplazar.put("periodo", periodo);

            reemplazar.put("parametroRubroSIIFPensionPublico",
                            SysmanFunciones.nvl(
                                            ejbSysmanUtil.consultarParametro(
                                                            compania,
                                                            "RUBRO SIIF DESCUENTO PENSION PUBLICO",
                                                            modulo, new Date(),
                                                            false),
                                            ""));

            reemplazar.put("nit",
                            SessionUtil.getCompaniaIngreso().getNit());

            parametros.put("PR_NOMBREEMPRESA",
                            SessionUtil.getCompaniaIngreso().getNombre());

            parametros.put("PR_MES", ejbSysmanUtil
                            .mostrarNombreDeMes(Integer.parseInt(mes)));
            parametros.put("PR_PERIODO", periodo);
            parametros.put("PR_ANO", anio);

            parametros.put("PR_NOMBRE_DEL_GERENTE",
                            ejbSysmanUtil.consultarParametro(compania,
                                            "NOMBRE DEL GERENTE", modulo,
                                            new Date(), false));
            parametros.put("PR_CARGO_DEL_GERENTE",
                            ejbSysmanUtil.consultarParametro(compania,
                                            "CARGO DEL GERENTE", modulo,
                                            new Date(), false));

            parametros.put("PR_NOMBRE_DEL_CARGO_TESORERO_PAGADOR",
                            ejbSysmanUtil.consultarParametro(compania,
                                            "NOMBRE DEL CARGO TESORERO PAGADOR",
                                            modulo,
                                            new Date(), false));
            parametros.put("PR_CARGO_DEL_TESORERO_PAGADOR",
                            ejbSysmanUtil.consultarParametro(compania,
                                            "CARGO DEL TESORERO PAGADOR",
                                            modulo,
                                            new Date(), false));

            parametros.put("PR_NOMBRE_DE_QUIEN_AUTORIZA_NOMINA",
                            ejbSysmanUtil.consultarParametro(compania,
                                            "NOMBRE DE QUIEN AUTORIZA NOMINA",
                                            modulo,
                                            new Date(), false));
            parametros.put("PR_CARGO_DE_QUIEN_AUTORIZA_NOMINA",
                            ejbSysmanUtil.consultarParametro(compania,
                                            "CARGO DE QUIEN AUTORIZA NOMINA",
                                            modulo,
                                            new Date(), false));
            
            // IMPLEMETACION PARAMETROS MARCA_BLANCA - ljdiaz (Luis Jacobo Diaz muñoz)
            parametros.put("PR_EMPRESAPARAMETRIZADA", SessionBean.getImpresoPorEmpresaParamterizada());
            // FIN IMPLEMENTACION MARCA_BLANCA
            
            Reporteador.resuelveConsulta(informe,
                            Integer.parseInt(modulo), reemplazar,
                            parametros);

            archivoDescarga = JsfUtil.exportarStreamed(
                            informe,
                            parametros,
                            ConectorPool.ESQUEMA_SYSMAN,
                            formato);

        }
        catch (SystemException | JRException | IOException
                        | SysmanException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());

        }
    }

    // </METODOS_ARBOL>
    // <SET_GET_ATRIBUTOS>
    /**
     * Retorna la variable anio
     * 
     * @return anio
     */
    public String getAnio() {
        return anio;
    }

    /**
     * Asigna la variable anio
     * 
     * @param anio
     * Variable a asignar en anio
     */
    public void setAnio(String anio) {
        this.anio = anio;
    }

    /**
     * Retorna la variable mes
     * 
     * @return mes
     */
    public String getMes() {
        return mes;
    }

    /**
     * Asigna la variable mes
     * 
     * @param mes
     * Variable a asignar en mes
     */
    public void setMes(String mes) {
        this.mes = mes;
    }

    /**
     * Retorna la variable periodo
     * 
     * @return periodo
     */
    public String getPeriodo() {
        return periodo;
    }

    /**
     * Asigna la variable periodo
     * 
     * @param periodo
     * Variable a asignar en periodo
     */
    public void setPeriodo(String periodo) {
        this.periodo = periodo;
    }

    /**
     * Atributo usado para descargar contenidos de archivos desde la
     * vista
     */
    public StreamedContent getArchivoDescarga() {
        return archivoDescarga;
    }

    // </SET_GET_ATRIBUTOS>
    // <SET_GET_PARAMETROS>
    // </SET_GET_PARAMETROS>
    // <SET_GET_LISTAS>
    /**
     * Retorna la lista listaAnio
     * 
     * @return listaAnio
     */
    public List<Registro> getListaAnio() {
        return listaAnio;
    }

    /**
     * Asigna la lista listaAnio
     * 
     * @param listaAnio
     * Variable a asignar en listaAnio
     */
    public void setListaAnio(List<Registro> listaAnio) {
        this.listaAnio = listaAnio;
    }

    /**
     * Retorna la lista listaMes
     * 
     * @return listaMes
     */
    public List<Registro> getListaMes() {
        return listaMes;
    }

    /**
     * Asigna la lista listaMes
     * 
     * @param listaMes
     * Variable a asignar en listaMes
     */
    public void setListaMes(List<Registro> listaMes) {
        this.listaMes = listaMes;
    }

    /**
     * Retorna la lista listaPeriodo
     * 
     * @return listaPeriodo
     */
    public List<Registro> getListaPeriodo() {
        return listaPeriodo;
    }

    /**
     * Asigna la lista listaPeriodo
     * 
     * @param listaPeriodo
     * Variable a asignar en listaPeriodo
     */
    public void setListaPeriodo(List<Registro> listaPeriodo) {
        this.listaPeriodo = listaPeriodo;
    }

    public boolean isConsolidado() {
        return consolidado;
    }

    public void setConsolidado(boolean consolidado) {
        this.consolidado = consolidado;
    }

    public boolean isDetallado() {
        return detallado;
    }

    public void setDetallado(boolean detallado) {
        this.detallado = detallado;
    }

    public boolean isDetalladoEntidad() {
        return detalladoEntidad;
    }

    public void setDetalladoEntidad(boolean detalladoEntidad) {
        this.detalladoEntidad = detalladoEntidad;
    }

    // </SET_GET_LISTAS>
    // <SET_GET_LISTAS_COMBO_GRANDE>
    // </SET_GET_LISTAS_COMBO_GRANDE>
}
