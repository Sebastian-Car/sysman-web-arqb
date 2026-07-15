/*-
 * LestadisticasConsumoAseoControlador.java
 *
 * 1.0
 * 
 * 17/01/2017
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
import com.sysman.recursos.ejb.EjbSysmanUtilRemote;
import com.sysman.reportes.Reporteador;
import com.sysman.serviciospublicos.ejb.EjbServiciosPublicosCeroRemote;
import com.sysman.serviciospublicos.enums.LestadisticasConsumoAseoControladorUrlEnum;
import com.sysman.util.SysmanFunciones;

import java.io.IOException;
import java.util.ArrayList;
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
 * Clase que genera 4 tipos de reportes en los que se muestra toda la
 * informacion estadistica relevante del pe�o o gran productor.
 *
 * @version 1.0, 17/01/2017
 * @author jguerrero
 * 
 * @author eamaya
 * @version 2.0 ,05/06/2017 Proceso de Refactoring y Manejo de EJBs
 * 
 */
@ManagedBean
@ViewScoped

public class LestadisticasConsumoAseoControlador extends BeanBaseModal {
    /**
     * Constante a nivel de clase que almacena el codigo de la
     * compania en la cual inicio sesion el usuario, el valor de esta
     * constante es asignado en el constructor a la variable de sesion
     * correspondiente
     */
    private final String compania;
    /**
     * Constante a nivel de clase que almacena el estado false, el
     * valor de esta constante es asignado en el constructor
     */
    private final String falseCons;
    /**
     * Constante a nivel de clase que almacena la cadena de caracteres
     * tipoProductor1, el valor de esta constante es asignado en el
     * constructor
     */

    private final String tipoProductor1Cons;
    /**
     * Constante a nivel de clase que almacena la cadena de caracteres
     * tipoProductor1, el valor de esta constante es asignado en el
     * constructor
     */

    private final String tipoProductor2Cons;
    /**
     * Constante a nivel de clase que almacena la cadena de caracteres
     * tipoProductor2, el valor de esta constante es asignado en el
     * constructor
     */

    private final String granProductorCons;

    // <DECLARAR_ATRIBUTOS>
    /**
     * Variable encargada de almacenar temporalmente lo seleccionado
     * del check peque�os productores del formulario
     */
    private String checkPeq;
    /**
     * Variable encargada de almacenar temporalmente lo seleccionado
     * del check grandes productores del formulario
     */
    private String checkGra;
    /**
     * Variable encargada de almacenar temporalmente lo seleccionado
     * del combo de ciclo en la parte grafica
     */
    private String ciclo;
    /**
     * Variable encargada de almacenar temporalmente lo seleccionado
     * del combo a�o inicial en la parte grafica.
     */
    private String anoInicial;
    /**
     * Variable encargada de almacenar temporalmente lo seleccionado
     * del combo a�o Inicial en la parte grafica.
     */
    private String periodoInicial;
    /**
     * Variable encargada de almacenar temporalmente lo seleccionado
     * del combo periodo final en la parte grafica.
     */
    private String anoFinal;
    /**
     * Variable encargada de almacenar temporalmente lo seleccionado
     * del combo a�o final en la parte grafica.
     */
    private String periodoFinal;
    /**
     * Atributo usado para descargar contenidos de archivos desde la
     * vista
     */
    private List<String> nombreConceptos;
    /**
     * Atributo usado para almacenar el resultado del paramentro
     * MANEJA MULTIUSUARIOS el cual es un llamado a la base de datos a
     * la tabla parametro.
     */
    private boolean manejaMultiUsuarios;
    private HashMap<String, Object> reemplazosTipoProductor;
    private StreamedContent archivoDescarga;
    // </DECLARAR_ATRIBUTOS>
    // <DECLARAR_PARAMETROS>
    // </DECLARAR_PARAMETROS>
    // <DECLARAR_LISTAS>

    /**
     * Lista encargada de almancenar temporalmente la respuesta de la
     * base de datos y que posteriormente mostrara la informacion en
     * el combo ciclo
     */
    private List<Registro> listaCiclo;
    /**
     * Lista encargada de almancenar temporalmente la respuesta de la
     * base de datos y que posteriormente mostrara la informacion en
     * el combo A�o Inicial
     */
    private List<Registro> listaAnoInicial;
    /**
     * Lista encargada de almancenar temporalmente la respuesta de la
     * base de datos y que posteriormente mostrara la informacion en
     * el combo Periodo Inicial
     */
    private List<Registro> listaPeriodoInicial;
    /**
     * Lista encargada de almancenar temporalmente la respuesta de la
     * base de datos y que posteriormente mostrara la informacion en
     * el combo ano Final
     */
    private List<Registro> listaAnoFinal;
    /**
     * Lista encargada de almancenar temporalmente la respuesta de la
     * base de datos y que posteriormente mostrara la informacion en
     * el combo Periodo final.
     */
    private List<Registro> listaPeriodoFinal;

    @EJB
    private EjbSysmanUtilRemote ejbSysmanUtil;

    @EJB
    private EjbServiciosPublicosCeroRemote ejbSpCero;

    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>
    // </DECLARAR_LISTAS_COMBO_GRANDE>
    /**
     * Crea una nueva instancia de LestadisticasConsumoAseoControlador
     */
    public LestadisticasConsumoAseoControlador() {
        super();
        compania = SessionUtil.getCompania();
        manejaMultiUsuarios = false;
        falseCons = "false";
        tipoProductor1Cons = "tipoProductor1";
        tipoProductor2Cons = "tipoProductor2";
        granProductorCons = "'Gran Productor'";
        numFormulario = GeneralCodigoFormaEnum.LESTADISTICAS_CONSUMO_ASEO_CONTROLADOR
                        .getCodigo();
        try {
            validarPermisos();
        }
        catch (SysmanException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
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
        cargarListaAnoInicial();
        cargarListaPeriodoInicial();
        cargarListaAnoFinal();
        cargarListaPeriodoFinal();
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
        try {

            if ("SI".equals(SysmanFunciones
                            .nvl(ejbSysmanUtil.consultarParametro(compania,
                                            "MANEJA MULTIUSUARIOS",
                                            SessionUtil.getModulo(), new Date(),
                                            false), "NO"))) {
                manejaMultiUsuarios = true;

            }

            // <INI_ADICIONAL>
            // </INI_ADICIONAL>
        }
        catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
            SessionUtil.redireccionarMenuPermisos();
        }

        // </CODIGO_DESARROLLADO>
    }

    // <METODOS_CARGAR_LISTA>

    public void cargarListaCiclo() {
        try {
            Map<String, Object> param = new TreeMap<>();
            param.put(GeneralParameterEnum.COMPANIA.getName(),
                            compania);

            listaCiclo = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            LestadisticasConsumoAseoControladorUrlEnum.URL8947
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
     * Carga la lista listaAnoInicial
     *
     * Metodo encargado de hacer el llamado a la base de datos y
     * guardar la respuesta en la listaanoIncial .
     */
    public void cargarListaAnoInicial() {
        try {

            Map<String, Object> param = new TreeMap<>();
            param.put(GeneralParameterEnum.COMPANIA.getName(),
                            compania);

            listaAnoInicial = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            LestadisticasConsumoAseoControladorUrlEnum.URL9627
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
     * Carga la lista listaPeriodoInicial
     *
     * Metodo encargado de hacer el llamado a la base de datos y
     * guardar la respuesta en la listaPeriodoIncial
     */
    public void cargarListaPeriodoInicial() {
        try {

            Map<String, Object> param = new TreeMap<>();
            param.put(GeneralParameterEnum.COMPANIA.getName(),
                            compania);
            param.put(GeneralParameterEnum.ANO.getName(),
                            anoInicial);

            listaPeriodoInicial = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            LestadisticasConsumoAseoControladorUrlEnum.URL10378
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
     * Carga la lista listaAnoFinal
     *
     * 
     * Metodo encargado de hacer el llamado a la base de datos y
     * guardar la respuesta en la listaAnoFinal
     */
    public void cargarListaAnoFinal() {
        try {

            Map<String, Object> param = new TreeMap<>();
            param.put(GeneralParameterEnum.COMPANIA.getName(),
                            compania);
            param.put(GeneralParameterEnum.ANO.getName(),
                            anoInicial);

            listaAnoFinal = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            LestadisticasConsumoAseoControladorUrlEnum.URL11173
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
     * Carga la lista listaPeriodoFinal
     *
     *
     * Metodo encargado de hacer el llamado a la base de datos y
     * guardar la respuesta en la listaPeriodoFinal
     */
    public void cargarListaPeriodoFinal() {
        try {
            Map<String, Object> param = new TreeMap<>();

            param.put(GeneralParameterEnum.COMPANIA.getName(),
                            compania);
            param.put(GeneralParameterEnum.ANO.getName(),
                            anoFinal);

            listaPeriodoFinal = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            LestadisticasConsumoAseoControladorUrlEnum.URL12023
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
     * Metodo ejecutado al oprimir el boton ImprimirPdf en la vista
     * ademas ejecuta el metodo genInforme el cual exporta el informe
     * con formatpo PDF
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
     * ademas ejecuta el metodo genInforme el cual exporta el informe
     * con formatpo Excel
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
    public void cambiarAnoInicial() {
        // <CODIGO_DESARROLLADO>
        periodoInicial = null;
        anoFinal = null;
        cargarListaPeriodoInicial();
        cargarListaAnoFinal();
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado al cambiar el control AnoFinal Ademas de esto,
     * el metodo es el encargado de cargar la lista del periodo final
     * ya que esta se actualiza con el combo del ano final previamente
     * seleccionado
     * 
     */
    public void cambiarAnoFinal() {
        // <CODIGO_DESARROLLADO>
        periodoFinal = null;
        cargarListaPeriodoFinal();
        // </CODIGO_DESARROLLADO>
    }

    public void cambiarchekpeq() {
        // <CODIGO_DESARROLLADO>
        if ("true".equals(checkPeq)) {
            checkGra = falseCons;

        }
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado al cambiar el control chekgra
     * 
     * Metodo encargado de cambiar el estado del check peque�os
     * productores en la forma
     * 
     */
    public void cambiarchekgra() {
        // <CODIGO_DESARROLLADO>
        if ("true".equals(checkGra)) {
            checkPeq = falseCons;

        }
        // </CODIGO_DESARROLLADO>
    }

    // </METODOS_CAMBIAR>
    // <METODOS_COMBOS_GRANDES>
    // </METODOS_COMBOS_GRANDES>
    // <METODOS_ARBOL>
    // </METODOS_ARBOL>
    // <SET_GET_ATRIBUTOS>
    /**
     * Retorna la variable checkPeq
     * 
     * @return checkPeq
     */
    public String getCheckPeq() {
        return checkPeq;
    }

    /**
     * Asigna la variable checkPeq
     * 
     * @param checkPeq
     * Variable a asignar en checkPeq
     */
    public void setCheckPeq(String checkPeq) {
        this.checkPeq = checkPeq;
    }

    /**
     * Retorna la variable checkGra
     * 
     * @return checkGra
     */
    public String getCheckGra() {
        return checkGra;
    }

    /**
     * Asigna la variable checkGra
     * 
     * @param checkGra
     * Variable a asignar en checkGra
     */
    public void setCheckGra(String checkGra) {
        this.checkGra = checkGra;
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
     * Retorna la lista listaAnoInicial
     * 
     * @return listaAnoInicial
     */
    public List<Registro> getListaAnoInicial() {
        return listaAnoInicial;
    }

    /**
     * Asigna la lista listaAnoInicial
     * 
     * @param listaAnoInicial
     * Variable a asignar en listaAnoInicial
     */
    public void setListaAnoInicial(List<Registro> listaAnoInicial) {
        this.listaAnoInicial = listaAnoInicial;
    }

    /**
     * Retorna la lista listaPeriodoInicial
     * 
     * @return listaPeriodoInicial
     */
    public List<Registro> getListaPeriodoInicial() {
        return listaPeriodoInicial;
    }

    /**
     * Asigna la lista listaPeriodoInicial
     * 
     * @param listaPeriodoInicial
     * Variable a asignar en listaPeriodoInicial
     */
    public void setListaPeriodoInicial(List<Registro> listaPeriodoInicial) {
        this.listaPeriodoInicial = listaPeriodoInicial;
    }

    /**
     * Retorna la lista listaAnoFinal
     * 
     * @return listaAnoFinal
     */
    public List<Registro> getListaAnoFinal() {
        return listaAnoFinal;
    }

    /**
     * Asigna la lista listaAnoFinal
     * 
     * @param listaAnoFinal
     * Variable a asignar en listaAnoFinal
     */
    public void setListaAnoFinal(List<Registro> listaAnoFinal) {
        this.listaAnoFinal = listaAnoFinal;
    }

    /**
     * Retorna la lista listaPeriodoFinal
     * 
     * @return listaPeriodoFinal
     */
    public List<Registro> getListaPeriodoFinal() {
        return listaPeriodoFinal;
    }

    /**
     * Asigna la lista listaPeriodoFinal
     * 
     * @param listaPeriodoFinal
     * Variable a asignar en listaPeriodoFinal
     */
    public void setListaPeriodoFinal(List<Registro> listaPeriodoFinal) {
        this.listaPeriodoFinal = listaPeriodoFinal;
    }

    public List<Registro> getListaCiclo() {
        return listaCiclo;
    }

    public void setListaCiclo(List<Registro> listaCiclo) {
        this.listaCiclo = listaCiclo;
    }

    public void genInforme(ReportesBean.FORMATOS formato) {
        archivoDescarga = null;
        String reporte = nombreInformeTipoProductor();
        nombreConceptos();
        try {
            HashMap<String, Object> reemplazar = new HashMap<>();
            reemplazar.put("cicloCondicion", "T".equals(ciclo) ? ""
                : " AND Qry_PesoAseoEstTot.Ciclo=" + ciclo);

            reemplazar.put("anoInicial", anoInicial);

            reemplazar.put("anoFinal", anoFinal);

            reemplazar.put("periodoInicial", periodoInicial);

            reemplazar.put("periodoFinal", periodoFinal);

            reemplazar.put(tipoProductor1Cons,
                            reemplazosTipoProductor.get(tipoProductor1Cons));
            reemplazar.put(tipoProductor2Cons,
                            reemplazosTipoProductor.get(tipoProductor2Cons));

            Map<String, Object> parametros = new HashMap<>();

            parametros.put("PR_TITULO", "T".equals(ciclo) ? "Ciclo: Todos"
                : "Ciclo: " + ciclo);

            parametros.put("PR_PRIMER_PERIODO",
                            ejbSpCero.asignarNombrePeriodo(compania,
                                            Integer.parseInt(anoInicial),
                                            periodoInicial, null));

            parametros.put("PR_SEG_PERIODO",
                            ejbSpCero.asignarNombrePeriodo(compania,
                                            Integer.parseInt(anoFinal),
                                            periodoFinal, null));

            parametros.put("PR_CONCEPTO3", nombreConceptos.get(0));
            parametros.put("PR_CONCEPTO20", nombreConceptos.get(1));
            parametros.put("PR_CONCEPTO21", nombreConceptos.get(2));
            parametros.put("PR_CONCEPTO22", nombreConceptos.get(3));
            parametros.put("PR_CONCEPTO48", nombreConceptos.get(4));

            Reporteador.resuelveConsulta(reporte,
                            Integer.parseInt(SessionUtil.getModulo()),
                            reemplazar, parametros);

            archivoDescarga = JsfUtil.exportarStreamed(reporte, parametros,
                            ConectorPool.ESQUEMA_SYSMAN, formato);
        }
        catch (JRException | IOException | SysmanException
                        | NumberFormatException | SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    public String nombreInformeTipoProductor() {
        String nombre = "";
        reemplazosTipoProductor = new HashMap<>();
        if (manejaMultiUsuarios) {

            if ("true".equals(checkPeq)) {
                nombre = "001360EstadisticaConsumoAsRangosPeqMult";
                reemplazosTipoProductor.put(tipoProductor1Cons,
                                granProductorCons);
                reemplazosTipoProductor.put(tipoProductor2Cons,
                                "'Peque�o Productor'");

            }
            if ("true".equals(checkGra)) {
                nombre = "001360EstadisticaConsumoAsRangosPeqMult";
                reemplazosTipoProductor.put(tipoProductor1Cons,
                                granProductorCons);
                reemplazosTipoProductor.put(tipoProductor2Cons,
                                granProductorCons);

            }

            if (falseCons.equals(checkPeq) && falseCons.equals(checkGra)) {
                nombre = "001364EstadisticaConsumoAsRangosMult";
                reemplazosTipoProductor.put(tipoProductor1Cons,
                                "");
                reemplazosTipoProductor.put(tipoProductor2Cons,
                                "");
            }

        }
        else {
            if ("true".equals(checkPeq)) {
                nombre = "001361EstadisticaConsumoAsRangosPeq";
                reemplazosTipoProductor.put(tipoProductor1Cons,
                                "'Peque�o Productor'");
                reemplazosTipoProductor.put(tipoProductor2Cons,
                                "");

            }
            if ("true".equals(checkGra)) {
                nombre = "001361EstadisticaConsumoAsRangosPeq";
                reemplazosTipoProductor.put(tipoProductor1Cons,
                                granProductorCons);
                reemplazosTipoProductor.put(tipoProductor2Cons,
                                "");

            }
            if (falseCons.equals(checkPeq) && falseCons.equals(checkGra)) {
                nombre = "001365EstadisticaConsumoAsRangos";
                reemplazosTipoProductor.put(tipoProductor1Cons,
                                granProductorCons);
                reemplazosTipoProductor.put(tipoProductor2Cons,
                                "");
            }

        }

        return nombre;

    }

    private void nombreConceptos() {
        nombreConceptos = new ArrayList<>();
        int[] numeroConcepto = { 3, 20, 21, 22, 48 };
        for (int i = 0; i < numeroConcepto.length; i++) {

            HashMap<String, Object> param = new HashMap<>();

            param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

            param.put(GeneralParameterEnum.CODIGO.getName(), numeroConcepto[i]);

            Registro registro;

            try {
                registro = RegistroConverter
                                .toRegistro(requestManager.get(UrlServiceUtil
                                                .getInstance()
                                                .getUrlServiceByUrlByEnumID(
                                                                LestadisticasConsumoAseoControladorUrlEnum.URL3604
                                                                                .getValue())
                                                .getUrl(), param));

                if ((registro != null)
                    && (registro.getCampos().get("NOMBRE") != null)) {
                    nombreConceptos.add(
                                    registro.getCampos().get("NOMBRE")
                                                    .toString());
                }
                else {
                    nombreConceptos.add("");
                }
            }
            catch (SystemException e) {
                logger.error(e.getMessage(), e);
                JsfUtil.agregarMensajeError(e.getMessage());
            }

        }

    }

    // </SET_GET_LISTAS>
    // <SET_GET_LISTAS_COMBO_GRANDE>
    // </SET_GET_LISTAS_COMBO_GRANDE>
}
