/*-
 * InformeAbonosControlador.java
 *
 * 1.0
 * 
 * 09/11/2016
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */
package com.sysman.serviciospublicos;

import com.sysman.beanbase.BeanBaseModal;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.jsfutil.ReportesBean;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.persistencia.ConectorPool;
import com.sysman.recursos.ejb.EjbSysmanUtilRemote;
import com.sysman.reportes.Reporteador;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.serviciospublicos.enums.InformeAbonosControladorUrlEnum;
import com.sysman.util.SysmanFunciones;

import java.io.IOException;
import java.text.ParseException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.event.SelectEvent;
import org.primefaces.model.StreamedContent;

import net.sf.jasperreports.engine.JRException;

/**
 * Clase engargada de generar 3 tipos de informe en los que se puede
 * apreciar el total de consumo de los servicios de acueducto,
 * alcantarillado y aseo.
 *
 * @version 1.0, 09/11/2016
 * @author jguerrero
 * @version 2.0, 02/06/2017
 * @author spina - se refactoriza para dss, depuracion sonar y ejb
 */
@ManagedBean
@ViewScoped
public class InformeAbonosControlador extends BeanBaseModal {
    /**
     * Constante a nivel de clase que almacena el codigo de la
     * compania en la cual inicio sesion el usuario, el valor de esta
     * constante es asignado en el constructor a la variable de sesion
     * correspondiente
     */
    private final String compania;
    // <DECLARAR_ATRIBUTOS>
    /**
     * Variable encargada de almacenar temporalmente lo seleccionado
     * del combo ciclo en el formulario
     */
    private String ciclo;
    /**
     * Variable encargada de almacenar temporalmente lo seleccionado
     * del combo codigoRuta inicial en el formulario
     */
    private String codigoInicial;
    /**
     * Variable encargada de almacenar temporalmente lo seleccionado
     * del combo codigoRuta final en el formulario
     */
    private String codigoFinal;
    /**
     * Variable encargada de almacenar temporalmente lo seleccionado
     * del campo fecha Inicial en el formulario
     */
    private Date fechaInicial;
    /**
     * Variable encargada de almacenar temporalmente lo seleccionado
     * del campo fechaFinal en el formulario
     */
    private Date fechaFinal;
    /**
     * Atributo usado para descargar contenidos de archivos desde la
     * vista
     */

    private String tipoFormato;

    /**
     * Variable encargada de almacenar temporalmente lo seleccionado
     * del combo tipoFormato en el formulario
     */
    private String tipoReporte;

    /**
     * Variable encargada de almacenar temporalmente el titulo del
     * informe;
     */
    private String titulo;

    /**
     * Variable encargada de mostar los combos de codigoRuta
     * dependiendo del combo ciclo.
     */
    private boolean cargarCodigosRutas;

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
     * Variable encaqrgada de almacenar temporalmente el resultado de
     * la culsta a la base de datos y que es mostrado en el combo
     * ciclo
     */
    private List<Registro> listaCiclo;
    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>
    /**
     * Variable encaqrgada de almacenar temporalmente el resultado de
     * la culsta a la base de datos y que es mostrado en el combo
     * codigoRutaInicial
     */
    private RegistroDataModelImpl listacmbCodigoInicial;
    /**
     * Variable encaqrgada de almacenar temporalmente el resultado de
     * la culsta a la base de datos y que es mostrado en el combo
     * codigoRutaFinal
     */
    private RegistroDataModelImpl listaCmbCodigoFinal;

    // </DECLARAR_LISTAS_COMBO_GRANDE>
    /**
     * Crea una nueva instancia de InformeAbonosControlador
     */

    private final String codigoRutaCons;

    @EJB
    private EjbSysmanUtilRemote ejbSysmanUtil;

    public InformeAbonosControlador() {
        super();
        compania = SessionUtil.getCompania();
        cargarCodigosRutas = true;
        codigoRutaCons = "CODIGORUTA";
        ciclo = "T";
        try {
            numFormulario = GeneralCodigoFormaEnum.INFORME_ABONOS_CONTROLADOR
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
     * Este metodo se ejecuta justo despues de que el objeto de la
     * clase del Bean ha sido creado, en este se realizan las
     * asignaciones iniciales necesarias para la visualizacion del
     * formulario, como son tablas, origenes de datos, inicializacion
     * de listas y demas necesarios
     */
    @PostConstruct
    public void inicializar() {
        // <CARGAR_LISTA>
        cargarListaCiclo();
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
     * Carga la lista listaCiclo
     *
     * Metodo encargado en hacer la llamada a la base de datos y
     * almancera en la listaCiclo el resultado de la misma
     */
    public void cargarListaCiclo() {
        Map<String, Object> param = new HashMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        try {
            listaCiclo = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            InformeAbonosControladorUrlEnum.URL0001
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e) {
            Logger.getLogger(InformeAbonosControlador.class.getName())
                            .log(Level.SEVERE, null, e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    /**
     * 
     * Carga la lista listacmbCodigoInicial
     *
     * Metodo encargado en hacer la llamada a la base de datos y
     * almancera en la listacmbCodigoInicial el resultado de la misma
     */
    public void cargarListacmbCodigoInicial() {
        Map<String, Object> param = new HashMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.CICLO.getName(), ciclo);
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        InformeAbonosControladorUrlEnum.URL0002
                                                        .getValue());
        listacmbCodigoInicial = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, codigoRutaCons);

    }

    /**
     * 
     * Carga la lista listaCmbCodigoFinal
     *
     * Metodo encargado en hacer la llamada a la base de datos y
     * almancera en la listaCmbCodigoFinal el resultado de la misma
     */
    public void cargarListaCmbCodigoFinal() {
        Map<String, Object> param = new HashMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.CICLO.getName(), ciclo);
        param.put("CODIGOINICIAL", codigoInicial);

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        InformeAbonosControladorUrlEnum.URL0003
                                                        .getValue());
        listaCmbCodigoFinal = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, codigoRutaCons);

    }

    // </METODOS_CARGAR_LISTA>
    // <METODOS_BOTONES>
    /**
     * 
     * Metodo ejecutado al oprimir el boton pdf en la vista
     *
     * Metodo ejecutado cuando se oprime el boton de pdf en el
     * formulario
     *
     */
    public void oprimirpdf() {
        // <CODIGO_DESARROLLADO>
        archivoDescarga = null;
        tipoReporte();
        genInforme(ReportesBean.FORMATOS.PDF);
        // </CODIGO_DESARROLLADO>
    }

    /**
     * 
     * Metodo ejecutado al oprimir el boton exel en la vista
     *
     * Metodo ejecutado cuando se oprime el boton de excel en el
     * formulario
     *
     */
    public void oprimirexel() {
        // <CODIGO_DESARROLLADO>
        archivoDescarga = null;
        tipoReporte();
        genInforme(ReportesBean.FORMATOS.EXCEL);
        // </CODIGO_DESARROLLADO>
    }

    // </METODOS_BOTONES>
    // <METODOS_CAMBIAR>
    /**
     * Metodo ejecutado al cambiar el control Ciclo
     * 
     * Metodo ejecutado al cambiar de opcion en el combo ciclo del
     * formulario en cual oculta o muestra los combos de codigoRuta
     * con la variable cargarCodigosRutas.
     * 
     */
    public void cambiarCiclo() {
        // <CODIGO_DESARROLLADO>
        codigoInicial = null;
        codigoFinal = null;
        if ("T".equals(ciclo) || (ciclo == null)) {
            cargarCodigosRutas = true;
        }
        else {
            cargarCodigosRutas = false;
            cargarListacmbCodigoInicial();
        }

        // </CODIGO_DESARROLLADO>
    }

    // </METODOS_CAMBIAR>
    // <METODOS_COMBOS_GRANDES>
    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listacmbCodigoInicial
     *
     * Metodo ejecutado al cambiar de seleccion en el combo de
     * codigoRuta inicial en el formulario ademas almancera lo
     * seleccionado en la varible codigo Inicial y carga la Lista del
     * codidgoRuta Final
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilacmbCodigoInicial(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        codigoInicial = registroAux.getCampos().get(codigoRutaCons).toString();
        codigoFinal = null;
        cargarListaCmbCodigoFinal();
    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaCmbCodigoFinal
     *
     * Metodo ejecutado al cambiar de seleccion en el combo de
     * codigoRuta inicial en el formulario ademas almancera lo
     * seleccionado en la varible codigo FInal
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaCmbCodigoFinal(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        codigoFinal = registroAux.getCampos().get(codigoRutaCons).toString();
    }

    // </METODOS_COMBOS_GRANDES>
    // <METODOS_ARBOL>
    // </METODOS_ARBOL>
    // <SET_GET_ATRIBUTOS>
    /**
     * Retorna la variable ciclo
     * 
     * @return ciclo
     */
    public String getCiclo() {
        return ciclo;
    }

    /**
     * Asigna la variable ciclo
     * 
     * @param ciclo
     * Variable a asignar en ciclo
     */
    public void setCiclo(String ciclo) {
        this.ciclo = ciclo;
    }

    /**
     * Retorna la variable codigoInicial
     * 
     * @return codigoInicial
     */
    public String getCodigoInicial() {
        return codigoInicial;
    }

    /**
     * Asigna la variable codigoInicial
     * 
     * @param codigoInicial
     * Variable a asignar en codigoInicial
     */
    public void setCodigoInicial(String codigoInicial) {
        this.codigoInicial = codigoInicial;
    }

    /**
     * Retorna la variable codigoFinal
     * 
     * @return codigoFinal
     */
    public String getCodigoFinal() {
        return codigoFinal;
    }

    /**
     * Asigna la variable codigoFinal
     * 
     * @param codigoFinal
     * Variable a asignar en codigoFinal
     */
    public void setCodigoFinal(String codigoFinal) {
        this.codigoFinal = codigoFinal;
    }

    /**
     * Retorna la variable fechaInicial
     * 
     * @return fechaInicial
     */

    /**
     * Atributo usado para descargar contenidos de archivos desde la
     * vista
     */
    public StreamedContent getArchivoDescarga() {
        return archivoDescarga;
    }

    public Date getFechaInicial() {
        return fechaInicial;
    }

    public void setFechaInicial(Date fechaInicial) {
        this.fechaInicial = fechaInicial;
    }

    public Date getFechaFinal() {
        return fechaFinal;
    }

    public void setFechaFinal(Date fechaFinal) {
        this.fechaFinal = fechaFinal;
    }

    public String getTipoFormato() {
        return tipoFormato;
    }

    public void setTipoFormato(String tipoFormato) {
        this.tipoFormato = tipoFormato;
    }

    public String getTipoReporte() {
        return tipoReporte;
    }

    public void setTipoReporte(String tipoReporte) {
        this.tipoReporte = tipoReporte;
    }

    public boolean isCargarCodigosRutas() {
        return cargarCodigosRutas;
    }

    public void setCargarCodigosRutas(boolean cargarCodigosRutas) {
        this.cargarCodigosRutas = cargarCodigosRutas;
    }

    // </SET_GET_ATRIBUTOS>
    // <SET_GET_PARAMETROS>
    // </SET_GET_PARAMETROS>
    // <SET_GET_LISTAS>
    /**
     * Retorna la lista listaCiclo
     * 
     * @return listaCiclo
     */
    public List<Registro> getListaCiclo() {
        return listaCiclo;
    }

    /**
     * Asigna la lista listaCiclo
     * 
     * @param listaCiclo
     * Variable a asignar en listaCiclo
     */
    public void setListaCiclo(List<Registro> listaCiclo) {
        this.listaCiclo = listaCiclo;
    }

    // </SET_GET_LISTAS>
    // <SET_GET_LISTAS_COMBO_GRANDE>
    /**
     * Retorna la lista listacmbCodigoInicial
     * 
     * @return listacmbCodigoInicial
     */
    public RegistroDataModelImpl getListacmbCodigoInicial() {
        return listacmbCodigoInicial;
    }

    /**
     * Asigna la lista listacmbCodigoInicial
     * 
     * @param listacmbCodigoInicial
     * Variable a asignar en listacmbCodigoInicial
     */
    public void setListacmbCodigoInicial(
        RegistroDataModelImpl listacmbCodigoInicial) {
        this.listacmbCodigoInicial = listacmbCodigoInicial;
    }

    /**
     * Retorna la lista listaCmbCodigoFinal
     * 
     * @return listaCmbCodigoFinal
     */
    public RegistroDataModelImpl getListaCmbCodigoFinal() {
        return listaCmbCodigoFinal;
    }

    /**
     * Asigna la lista listaCmbCodigoFinal
     * 
     * @param listaCmbCodigoFinal
     * Variable a asignar en listaCmbCodigoFinal
     */
    public void setListaCmbCodigoFinal(
        RegistroDataModelImpl listaCmbCodigoFinal) {
        this.listaCmbCodigoFinal = listaCmbCodigoFinal;
    }
    // </SET_GET_LISTAS_COMBO_GRANDE>

    public void tipoReporte() {
        try {
            if ("1".equals(tipoFormato)) {
                tipoReporte = "001233RptInformeAbonos";

            }
            else {
                if ("2".equals(tipoFormato)) {
                    tipoReporte = SysmanFunciones
                                    .nvl(ejbSysmanUtil.consultarParametro(
                                                    compania,
                                                    "FORMATO INFORME ABONOS DEUDA POR BANCO",
                                                    SessionUtil.getModulo(),
                                                    new Date(), true), "")
                                    .toString();

                }
            }
            if ("3".equals(tipoFormato)) {
                tipoReporte = "001252rptInformeAbonosFecha";
            }

            titulo = SysmanFunciones.concatenar("Informe de abonos \n Ciclo ",
                            "T".equals(ciclo) ? "Todos" : ciclo,
                            (codigoInicial != null) && (codigoFinal != null)
                                ? SysmanFunciones.concatenar(" códigos ",
                                                codigoInicial, " al ",
                                                codigoFinal)
                                : "",
                            " \n fechas ", SysmanFunciones
                                            .convertirAFechaCadena(
                                                            fechaInicial),
                            " a ", SysmanFunciones
                                            .convertirAFechaCadena(fechaFinal));

        }
        catch (ParseException | SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    public void genInforme(ReportesBean.FORMATOS formato) {
        if (!validarCodigosRutas()) {
            return;
        }
        archivoDescarga = null;
        try {
            HashMap<String, Object> reemplazar = new HashMap<>();
            reemplazar.put("ciclo", "T".equals(ciclo) ? ""
                : "AND ABONOS.CICLO= " + ciclo);
            reemplazar.put("codigo",
                            "T".equals(ciclo) ? ""
                                : " AND USUARIO.CODIGORUTA BETWEEN '"
                                    + codigoInicial
                                    + "' AND '" + codigoFinal + "'");

            reemplazar.put("fechaInicial", SysmanFunciones
                            .convertirAFechaCadena(fechaInicial));
            reemplazar.put("fechaFinal", SysmanFunciones
                            .convertirAFechaCadena(fechaFinal));

            Map<String, Object> parametros = new HashMap<>();
            parametros.put("PR_TITULO", titulo);
            Reporteador.resuelveConsulta(tipoReporte,
                            Integer.parseInt(SessionUtil.getModulo()),
                            reemplazar, parametros);
            archivoDescarga = JsfUtil.exportarStreamed(tipoReporte, parametros,
                            ConectorPool.ESQUEMA_SYSMAN, formato);
        }
        catch (JRException | IOException | SysmanException | ParseException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(SysmanFunciones.concatenar(
                            idioma.getString("MSM_TRANS_INTERRUMPIDA"), "<br>",
                            e.getMessage()));
        }
    }

    private boolean validarCodigosRutas() {
        boolean rta = true;
        if (!cargarCodigosRutas
            && (SysmanFunciones.validarVariableVacio(codigoInicial))) {
            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB3088"));
            rta = false;
        }
        if (!cargarCodigosRutas
            && (SysmanFunciones.validarVariableVacio(codigoFinal))) {
            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB3089"));
            rta = false;
        }

        if (fechaFinal.before(fechaInicial)) {
            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB75"));
            rta = false;

        }

        return rta;
    }

}
