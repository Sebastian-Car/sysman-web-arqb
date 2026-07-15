/*-
 * LresolucionControlador.java
 *
 * 1.0
 * 
 * 02/11/2016
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
import com.sysman.jsfutil.ReportesBean.FORMATOS;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.persistencia.ConectorPool;
import com.sysman.reportes.Reporteador;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.serviciospublicos.enums.LresolucionControladorUrlEnum;
import com.sysman.util.SysmanFunciones;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.event.SelectEvent;
import org.primefaces.model.StreamedContent;

import net.sf.jasperreports.engine.JRException;

/**
 * Clase que sirve de controlador para el formulario
 * LresolucionControlador
 *
 * @version 1.0, 02/11/2016
 * @author cperez
 * 
 * -- Modificado por asana 07/06/2017. Refactorizacion de codigo de
 * las listas para utilizar dss. Reemplazo de llamados a la clase
 * Acciones por ejb respectivo.
 */
@ManagedBean
@ViewScoped
public class LresolucionControlador extends BeanBaseModal {
    /**
     * Constante a nivel de clase que almacena el codigo de la
     * compania en la cual inicio sesion el usuario, el valor de esta
     * constante es asignado en el constructor a la variable de sesion
     * correspondiente
     */
    private final String compania;
    // <DECLARAR_ATRIBUTOS>
    /**
     * Obtiene el ciclo de la consulta
     */
    private String ciclo;
    /**
     * Obtiene el periodo Inicial de la consulta
     */
    private String periodoInicial;
    /**
     * Obtiene el id del periodo Inicial de la consulta
     */
    private String periodoFinalId;
    /**
     * Obtiene el id del periodo Inicial de la consulta
     */
    private String periodoInicialId;
    /**
     * Obtiene el periodo final de la consulta
     */
    private String periodoFinal;
    /**
     * Obtiene el mes del periodo inicial de la consulta
     */
    private String mesPeriodoIni;
    /**
     * Obtiene el mes del periodo final de la consulta
     */
    private String mesPeriodoFin;
    /**
     * Obtiene el nombre con el (-) del mes mes del periodo inicial de
     * la consulta
     */
    private String mesPeriodoInicial;
    /**
     * Obtiene el nombre del (-) mes mes del periodo final de la
     * consulta
     */
    private String mesPeriodoFinal;
    /**
     * variable para el nombre FILTRO
     */
    private String filtroConstante;
    /**
     * Obtiene el a�o Inicial de la consulta
     */
    private String anoPeriodoInicial;
    /**
     * Obtiene el ano final de la consulta
     */
    private String anoPeriodoFinal;
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
     * Necesario para obtener y mandar la lista del Ciclo
     */
    private List<Registro> listaCiclo;
    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>
    /**
     * Necesario para obtener mandar la lista del periodo Inicial
     */
    private RegistroDataModelImpl listaPERIODO1;
    /**
     * Necesario para obtener y mandar la lista del a�o final
     */
    private RegistroDataModelImpl listaPERIODO2;

    // </DECLARAR_LISTAS_COMBO_GRANDE>
    /**
     * Crea una nueva instancia de LresolucionControlador
     */
    public LresolucionControlador() {
        super();
        compania = SessionUtil.getCompania();
        filtroConstante = "FILTRO";
        try {
            numFormulario = GeneralCodigoFormaEnum.LRESOLUCION_CONTROLADOR
                            .getCodigo();
            validarPermisos();
            // <INI_ADICIONAL>
            // </INI_ADICIONAL>
        }
        catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
            SessionUtil.redireccionarMenuPermisos();
        }
        finally {
            SessionUtil.cleanFlash();
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
        cargarListaPERIODO1();
        cargarListaPERIODO2();
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
     */
    public void cargarListaCiclo() {

        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        try {
            listaCiclo = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            LresolucionControladorUrlEnum.URL6065
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
     * Carga la lista listaPERIODO1
     */
    public void cargarListaPERIODO1() {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        LresolucionControladorUrlEnum.URL6705
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        listaPERIODO1 = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param, true,
                        filtroConstante);
    }

    /**
     * 
     * Carga la lista listaPERIODO2
     */
    public void cargarListaPERIODO2() {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        LresolucionControladorUrlEnum.URL7588
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.PERIODO.getName(), periodoInicialId);

        listaPERIODO2 = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param, true,
                        filtroConstante);
    }

    // </METODOS_CARGAR_LISTA>
    // <METODOS_BOTONES>
    /**
     * 
     * Metodo ejecutado al oprimir el boton Pdf en la vista
     *
     */
    public void oprimirPdf() {
        // <CODIGO_DESARROLLADO>
        long periodoFin = Long.parseLong(periodoFinalId);
        long periodoIni = Long.parseLong(periodoInicialId);
        if (periodoIni > periodoFin) {
            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB1786"));
        }
        else {
            genInforme(FORMATOS.PDF, "001208InformeResolucion15085");
        }
        // </CODIGO_DESARROLLADO>
    }

    /**
     * 
     * Metodo ejecutado al oprimir el boton Excel en la vista
     *
     */
    public void oprimirExcel() {
        // <CODIGO_DESARROLLADO>
        archivoDescarga = null;
        long periodoFin = Long.parseLong(periodoFinalId);
        long periodoIni = Long.parseLong(periodoInicialId);
        if (periodoIni > periodoFin) {
            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB1786"));
        }
        else {
            genInforme(FORMATOS.EXCEL, "001208InformeResolucion15085");
        }
        // </CODIGO_DESARROLLADO>
    }

    public void genInforme(ReportesBean.FORMATOS formato, String reporte) {
        archivoDescarga = null;
        try {
            HashMap<String, Object> reemplazar = new HashMap<>();
            String periodoInicialCompleto;
            String periodoFinalCompleto;
            String periodoFinalCompletoConsumo;
            String periodoFinalCompletoFacturacion;
            String nombreCiclo;
            periodoInicialCompleto = anoPeriodoInicial + mesPeriodoIni;
            periodoFinalCompleto = anoPeriodoFinal + mesPeriodoFin;

            if ("T".equals(ciclo)) {
                nombreCiclo = "Todos";
                periodoFinalCompletoConsumo = "";
                periodoFinalCompletoFacturacion = "";
                periodoFinalCompletoConsumo = periodoFinalCompleto + " "
                    + periodoFinalCompletoConsumo;
                periodoFinalCompletoFacturacion = periodoFinalCompleto + " "
                    + periodoFinalCompletoFacturacion;

            }
            else {
                nombreCiclo = ciclo;
                periodoFinalCompletoConsumo = "AND SP_ESTADISTICAS_CONSUMO.CICLO = "
                    + ciclo;
                periodoFinalCompletoFacturacion = "AND SP_ESTADISTICAS_FACTURACION.CICLO = "
                    + ciclo;
                periodoFinalCompletoConsumo = periodoFinalCompleto + " "
                    + periodoFinalCompletoConsumo;
                periodoFinalCompletoFacturacion = periodoFinalCompleto + " "
                    + periodoFinalCompletoFacturacion;
            }
            reemplazar.put("compania", compania);
            reemplazar.put("periodoInicialCompleto", periodoInicialCompleto);
            reemplazar.put("periodoFinalCompleto", periodoFinalCompletoConsumo);
            reemplazar.put("periodoFnalCompleto",
                            periodoFinalCompletoFacturacion);
            Map<String, Object> parametros = new HashMap<>();
            Reporteador.resuelveConsulta(reporte,
                            Integer.parseInt(SessionUtil.getModulo()),
                            reemplazar, parametros);
            parametros.put("PR_FORMS_LRESOLUCION15085_N_PERIODO1",
                            mesPeriodoInicial);
            parametros.put("PR_FORMS_LRESOLUCION15085_N_PERIODO2",
                            mesPeriodoFinal);
            parametros.put("PR_FORMS_LRESOLUCION15085_CICLO", nombreCiclo);
            archivoDescarga = JsfUtil.exportarStreamed(reporte, parametros,
                            ConectorPool.ESQUEMA_SYSMAN, formato);
        }
        catch (JRException | IOException | SysmanException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(SysmanFunciones.concatenar(
                            idioma.getString("MSM_TRANS_INTERRUMPIDA"), " ",
                            e.getMessage()));
        }
    }

    // </METODOS_BOTONES>
    // <METODOS_CAMBIAR>
    // </METODOS_CAMBIAR>
    // <METODOS_COMBOS_GRANDES>
    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaPERIODO1
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaPERIODO1(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();

        periodoInicial = registroAux.getCampos().get("PER").toString();
        periodoInicialId = registroAux.getCampos().get(filtroConstante)
                        .toString();
        mesPeriodoIni = registroAux.getCampos().get("MES").toString();
        String mesPeriodoInicialNombre;
        mesPeriodoInicialNombre = registroAux.getCampos().get("MESNOMBRE")
                        .toString();
        anoPeriodoInicial = registroAux.getCampos().get("ANO").toString();
        mesPeriodoInicial = mesPeriodoInicialNombre + "/" + anoPeriodoInicial;
        cargarListaPERIODO2();
        periodoFinalId = "";
        periodoFinal="";

    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaPERIODO2
     * 
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaPERIODO2(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        periodoFinal = registroAux.getCampos().get("PER").toString();
        periodoFinalId = registroAux.getCampos().get(filtroConstante)
                        .toString();
        mesPeriodoFin = registroAux.getCampos().get("MES").toString();
        String mesPeriodoFinalNombre;
        mesPeriodoFinalNombre = registroAux.getCampos().get("MESNOMBRE")
                        .toString();
        anoPeriodoFinal = registroAux.getCampos().get("ANO").toString();
        mesPeriodoFinal = mesPeriodoFinalNombre + "/" + anoPeriodoFinal;
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
     * Retorna la variable mesPeriodoInicial
     * 
     * @return mesPeriodoInicial
     */
    public String getMesPeriodoInicial() {
        return mesPeriodoInicial;
    }

    /**
     * Asigna la variable mesPeriodoInicial
     * 
     * @param mesPeriodoInicial
     * Variable a asignar en mesPeriodoInicial
     */
    public void setMesPeriodoInicial(String mesPeriodoInicial) {
        this.mesPeriodoInicial = mesPeriodoInicial;
    }

    /**
     * Retorna la variable mesPeriodoFinal
     * 
     * @return mesPeriodoFinal
     */
    public String getMesPeriodoFinal() {
        return mesPeriodoFinal;
    }

    /**
     * Asigna la variable mesPeriodoFinal
     * 
     * @param mesPeriodoFinal
     * Variable a asignar en mesPeriodoFinal
     */
    public void setMesPeriodoFinal(String mesPeriodoFinal) {
        this.mesPeriodoFinal = mesPeriodoFinal;
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

    public RegistroDataModelImpl getListaPERIODO1() {
        return listaPERIODO1;
    }

    public RegistroDataModelImpl getListaPERIODO2() {
        return listaPERIODO2;
    }

    public void setListaPERIODO2(RegistroDataModelImpl listaPERIODO2) {
        this.listaPERIODO2 = listaPERIODO2;
    }

    public void setListaPERIODO1(RegistroDataModelImpl listaPERIODO1) {
        this.listaPERIODO1 = listaPERIODO1;
    }

    // </SET_GET_LISTAS_COMBO_GRANDE>
}
