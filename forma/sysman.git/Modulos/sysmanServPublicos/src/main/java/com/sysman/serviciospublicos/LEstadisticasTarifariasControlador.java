/*-
 * LEstadisticasTarifariasControlador.java
 *
 * 1.0
 * 
 * 27/01/2017
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
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.persistencia.ConectorPool;
import com.sysman.reportes.Reporteador;
import com.sysman.serviciospublicos.ejb.EjbServiciosPublicosCeroRemote;
import com.sysman.serviciospublicos.enums.LEstadisticasTarifariasControladorUrlEnum;

import java.io.IOException;
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
 * Clase del modulo de Servicios Publicos, Es la engargada de exportar
 * informes estadisticos de acueducto y alcantarillado
 *
 * @version 1.0, 27/01/2017
 * @author jguerrero
 * @modified jguerrero
 * @version 2. 05/06/2017 Se realizo el refactory de las consultas sql
 * en el controlador. Además se ajustaron los errores del sonar
 */
@ManagedBean
@ViewScoped

public class LEstadisticasTarifariasControlador extends BeanBaseModal {
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
     * en tipo de servicio en el formulario
     */
    private String tipoDeInforme;
    /**
     * Variable encargada de almacenar temporalmente lo seleccionado
     * en el combo de ciclo del formulario
     */
    private String ciclo;
    /**
     * Variable encargada de almacenar temporalmente el año inicial
     * seleccionado en el combo de ciclo del formulario
     */
    private String anoInicial;
    /**
     * Variable encargada de almacenar temporalmente el periodo
     * inicial seleccionado en el combo de ciclo del formulario
     */
    private String periodoInicial;
    /**
     * Variable encargada de almacenar temporalmente el ano final
     * seleccionado en el combo de ciclo del formulario
     */
    private String anoFinal;
    /**
     * Variable encargada de almacenar temporalmente el periodo Final
     * seleccionado en el combo de ciclo del formulario
     */
    private String periodoFinal;
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
     * Lista encargada de almacenar los datos correspondientes a los
     * ciclos de la compañia. Es la respuesta al llamado de la base de
     * datos
     */
    private List<Registro> listaciclo;
    /**
     * Lista encargada de almacenar los datos correspondientes a los
     * años de la compañia. Es la respuesta al llamado de la base de
     * datos
     */
    private List<Registro> listaanoInicial;
    /**
     * Lista encargada de almacenar los datos correspondientes a los
     * periodos de la compañia. Es la respuesta al llamado de la base
     * de datos
     */
    private List<Registro> listaperiodoInicial;
    /**
     * Lista encargada de almacenar los datos correspondientes a los
     * años de la compañia. Es la respuesta al llamado de la base de
     * datos
     */
    private List<Registro> listaanoFinal;
    /**
     * Lista encargada de almacenar los datos correspondientes a los
     * periodos de la compañia. Es la respuesta al llamado de la base
     * de datos
     */
    private List<Registro> listaperiodoFinal;
    @EJB
    private EjbServiciosPublicosCeroRemote EjbServPubCero;

    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>
    // </DECLARAR_LISTAS_COMBO_GRANDE>
    /**
     * Crea una nueva instancia de LEstadisticasTarifariasControlador
     */
    public LEstadisticasTarifariasControlador() {
        super();
        compania = SessionUtil.getCompania();
        tipoDeInforme = "1";
        try {
            numFormulario = GeneralCodigoFormaEnum.L_ESTADISTICAS_TARIFARIAS_CONTROLADOR
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
        cargarListaciclo();
        cargarListaanoInicial();
        cargarListaperiodoInicial();
        cargarListaanoFinal();
        cargarListaperiodoFinal();
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
     * Carga la lista listaciclo
     *
     * Metodo encargado de hacer la llamada a la base de datos y
     * almacenar la respesta temporalmente en la listaCiclo
     */
    public void cargarListaciclo() {

        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        try {
            listaciclo = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            LEstadisticasTarifariasControladorUrlEnum.URL6848
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
        // 214029
    }

    /**
     * 
     * Carga la lista listaanoInicial
     *
     * Metodo encargado de hacer la llamada a la base de datos y
     * almacenar la respesta temporalmente en la listaAnoinicial
     */
    public void cargarListaanoInicial() {

        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        try {
            listaanoInicial = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            LEstadisticasTarifariasControladorUrlEnum.URL7523
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
        // 227029
    }

    /**
     * 
     * Carga la lista listaperiodoInicial
     *
     * Metodo encargado de hacer la llamada a la base de datos y
     * almacenar la respesta temporalmente en la listaperiodoInicial
     */
    public void cargarListaperiodoInicial() {

        Map<String, Object> param = new TreeMap<>();

        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        try {
            listaperiodoInicial = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            LEstadisticasTarifariasControladorUrlEnum.URL8288
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
     * Carga la lista listaanoFinal
     *
     * Metodo encargado de hacer la llamada a la base de datos y
     * almacenar la respesta temporalmente en la listAnoFinal
     */
    public void cargarListaanoFinal() {

        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.ANO.getName(), anoInicial);

        try {
            listaanoFinal = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            LEstadisticasTarifariasControladorUrlEnum.URL9147
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
        // 227030
    }

    /**
     * 
     * Carga la lista listaperiodoFinal
     *
     * Metodo encargado de hacer la llamada a la base de datos y
     * almacenar la respesta temporalmente en la listaPeriodoFinal
     */
    public void cargarListaperiodoFinal() {

        Map<String, Object> param = new TreeMap<>();

        param.put("MESINICIAL", periodoInicial);
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        try {
            listaperiodoFinal = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            LEstadisticasTarifariasControladorUrlEnum.URL9972
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
        // 227032

    }

    // </METODOS_CARGAR_LISTA>
    // <METODOS_BOTONES>
    /**
     * 
     * Metodo ejecutado al oprimir el boton ImprimirPdf en la vista y
     * exporta el informe en formato Pdf
     *
     *
     */
    public void oprimirImprimirPdf() {
        // <CODIGO_DESARROLLADO>
        archivoDescarga = null;
        genInforme(ReportesBean.FORMATOS.PDF);
        // </CODIGO_DESARROLLADO>
    }

    /**
     * 
     * Metodo ejecutado al oprimir el boton ImprimirExcel en la vista
     *
     * Metodo ejecutado al oprimir el boton ImprimirPdf en la vista y
     * exporta el informe en formato Excel
     *
     */
    public void oprimirImprimirExcel() {
        // <CODIGO_DESARROLLADO>
        archivoDescarga = null;
        genInforme(ReportesBean.FORMATOS.EXCEL);
        // </CODIGO_DESARROLLADO>
    }

    // </METODOS_BOTONES>
    // <METODOS_CAMBIAR>
    /**
     * Metodo ejecutado al cambiar el control anoInicial Además, Es el
     * encargado de cargar la lista anoFinal y la lista PeriodoInicial
     * 
     */
    public void cambiaranoInicial() {
        // <CODIGO_DESARROLLADO>
        anoFinal = null;
        cargarListaanoFinal();
        periodoInicial = null;
        cargarListaperiodoInicial();
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado al cambiar el control periodoInicial
     * 
     * 
     */
    public void cambiarperiodoInicial() {
        // <CODIGO_DESARROLLADO>

        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado al cambiar el control anoFinal
     * 
     * Además, Es el encargado de cargar la listaperiodoFinal
     * 
     */
    public void cambiaranoFinal() {
        // <CODIGO_DESARROLLADO>
        periodoFinal = null;
        cargarListaperiodoFinal();
        // </CODIGO_DESARROLLADO>
    }

    // </METODOS_CAMBIAR>
    // <METODOS_COMBOS_GRANDES>
    // </METODOS_COMBOS_GRANDES>
    // <METODOS_ARBOL>
    // </METODOS_ARBOL>
    // <SET_GET_ATRIBUTOS>
    /**
     * Retorna la variable tipoDeInforme
     * 
     * @return tipoDeInforme
     */
    public String getTipoDeInforme() {
        return tipoDeInforme;
    }

    /**
     * Asigna la variable tipoDeInforme
     * 
     * @param tipoDeInforme
     * Variable a asignar en tipoDeInforme
     */
    public void setTipoDeInforme(String tipoDeInforme) {
        this.tipoDeInforme = tipoDeInforme;
    }

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
     * Retorna la variable anoInicial
     * 
     * @return anoInicial
     */
    public String getAnoInicial() {
        return anoInicial;
    }

    /**
     * Asigna la variable anoInicial
     * 
     * @param anoInicial
     * Variable a asignar en anoInicial
     */
    public void setAnoInicial(String anoInicial) {
        this.anoInicial = anoInicial;
    }

    /**
     * Retorna la variable periodoInicial
     * 
     * @return periodoInicial
     */
    public String getPeriodoInicial() {
        return periodoInicial;
    }

    /**
     * Asigna la variable periodoInicial
     * 
     * @param periodoInicial
     * Variable a asignar en periodoInicial
     */
    public void setPeriodoInicial(String periodoInicial) {
        this.periodoInicial = periodoInicial;
    }

    /**
     * Retorna la variable anoFinal
     * 
     * @return anoFinal
     */
    public String getAnoFinal() {
        return anoFinal;
    }

    /**
     * Asigna la variable anoFinal
     * 
     * @param anoFinal
     * Variable a asignar en anoFinal
     */
    public void setAnoFinal(String anoFinal) {
        this.anoFinal = anoFinal;
    }

    /**
     * Retorna la variable periodoFinal
     * 
     * @return periodoFinal
     */
    public String getPeriodoFinal() {
        return periodoFinal;
    }

    /**
     * Asigna la variable periodoFinal
     * 
     * @param periodoFinal
     * Variable a asignar en periodoFinal
     */
    public void setPeriodoFinal(String periodoFinal) {
        this.periodoFinal = periodoFinal;
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
     * Retorna la lista listaciclo
     * 
     * @return listaciclo
     */
    public List<Registro> getListaciclo() {
        return listaciclo;
    }

    /**
     * Asigna la lista listaciclo
     * 
     * @param listaciclo
     * Variable a asignar en listaciclo
     */
    public void setListaciclo(List<Registro> listaciclo) {
        this.listaciclo = listaciclo;
    }

    /**
     * Retorna la lista listaanoInicial
     * 
     * @return listaanoInicial
     */
    public List<Registro> getListaanoInicial() {
        return listaanoInicial;
    }

    /**
     * Asigna la lista listaanoInicial
     * 
     * @param listaanoInicial
     * Variable a asignar en listaanoInicial
     */
    public void setListaanoInicial(List<Registro> listaanoInicial) {
        this.listaanoInicial = listaanoInicial;
    }

    /**
     * Retorna la lista listaperiodoInicial
     * 
     * @return listaperiodoInicial
     */
    public List<Registro> getListaperiodoInicial() {
        return listaperiodoInicial;
    }

    /**
     * Asigna la lista listaperiodoInicial
     * 
     * @param listaperiodoInicial
     * Variable a asignar en listaperiodoInicial
     */
    public void setListaperiodoInicial(List<Registro> listaperiodoInicial) {
        this.listaperiodoInicial = listaperiodoInicial;
    }

    /**
     * Retorna la lista listaanoFinal
     * 
     * @return listaanoFinal
     */
    public List<Registro> getListaanoFinal() {
        return listaanoFinal;
    }

    /**
     * Asigna la lista listaanoFinal
     * 
     * @param listaanoFinal
     * Variable a asignar en listaanoFinal
     */
    public void setListaanoFinal(List<Registro> listaanoFinal) {
        this.listaanoFinal = listaanoFinal;
    }

    /**
     * Retorna la lista listaperiodoFinal
     * 
     * @return listaperiodoFinal
     */
    public List<Registro> getListaperiodoFinal() {
        return listaperiodoFinal;
    }

    /**
     * Asigna la lista listaperiodoFinal
     * 
     * @param listaperiodoFinal
     * Variable a asignar en listaperiodoFinal
     */
    public void setListaperiodoFinal(List<Registro> listaperiodoFinal) {
        this.listaperiodoFinal = listaperiodoFinal;
    }
    // </SET_GET_LISTAS>
    // <SET_GET_LISTAS_COMBO_GRANDE>
    // </SET_GET_LISTAS_COMBO_GRANDE>

    public void genInforme(ReportesBean.FORMATOS formato) {
        archivoDescarga = null;
        try {

            String reporte = "1".equals(tipoDeInforme)
                ? "001380EstadisticaTarifariaAc"
                : "001387EstadisticaTarifariaAlc";

            HashMap<String, Object> reemplazar = new HashMap<>();
            reemplazar.put("anoInicial", anoInicial);
            reemplazar.put("periodoInicial", periodoInicial);
            reemplazar.put("anoFinal", anoFinal);
            reemplazar.put("periodoFinal", periodoFinal);
            reemplazar.put("cicloCond", "T".equals(ciclo) ? ""
                : " AND ESTADISTICAS_CONSUMO.CICLO= " + ciclo);

            Map<String, Object> parametros = new HashMap<>();

            parametros.put("PR_PRIMER_PERIODO",
                            EjbServPubCero.asignarNombrePeriodo(compania,
                                            Integer.parseInt(anoInicial),
                                            periodoInicial, null));
            parametros.put("PR_SEG_PERIODO",
                            EjbServPubCero.asignarNombrePeriodo(compania,
                                            Integer.parseInt(anoFinal),
                                            periodoFinal, null));

            parametros.put("PR_CICLO_TITULO", "T".equals(ciclo)
                ? "Ciclo:  Todos" : "Ciclo: " + ciclo);
            Reporteador.resuelveConsulta(reporte,
                            Integer.parseInt(SessionUtil.getModulo()),
                            reemplazar, parametros);
            archivoDescarga = JsfUtil.exportarStreamed(reporte, parametros,
                            ConectorPool.ESQUEMA_SYSMAN, formato);
        }
        catch (JRException | IOException | SysmanException
                        | SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

}
