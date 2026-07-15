/*-
 * AuxiliarrecaudosconceptosControlador.java
 *
 * 1.0
 *
 * 10/11/2016
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
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.persistencia.ConectorPool;
import com.sysman.reportes.Reporteador;
import com.sysman.serviciospublicos.enums.AuxiliarrecaudosconceptosControladorEnum;
import com.sysman.serviciospublicos.enums.AuxiliarrecaudosconceptosControladorUrlEnum;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.model.StreamedContent;

import net.sf.jasperreports.engine.JRException;

/**
 * Clase migrada para el formulario Auxiliar de cartera por concepto,
 * es llamado desde Facturacion\Informes\Facturaci�n y
 * cartera\Auxiliar de cartera por concepto, genera un reporte con el
 * listado de los auxiliares de cartera por concepto
 *
 * @version 1.0, 10/11/2016
 * @author ybecerra
 * 
 * @author eamaya
 * @version 2, 16/05/2017 Proceso de Refactoring
 */
@ManagedBean
@ViewScoped

public class AuxiliarrecaudosconceptosControlador extends BeanBaseModal {
    /**
     * Constante a nivel de clase que almacena el codigo de la
     * compania en la cual inicio sesion el usuario, el valor de esta
     * constante es asignado en el constructor a la variable de sesion
     * correspondiente
     */
    private final String compania;
    /**
     * Constante definida para almacenar el codigo del modulo por la
     * cual se ingresa a la aplicacion
     */
    private final String modulo;
    // <DECLARAR_ATRIBUTOS>
    /**
     * Atributo que almacena el codigo seleccionado del combo ciclo
     * inicial del formulario
     */
    private String cicloInicial;
    /**
     * Atributo que almacena el codigo seleccionado del combo ciclo
     * final del formulario
     */
    private String cicloFinal;
    /**
     * Atributo que almacena el valor ingresado en el campo concepto
     * inicial del formulario
     */
    private String conceptoInicial;
    /**
     * Atributo que almacena el valor ingresado en el campo concepto
     * final del formulario
     */
    private String conceptoFinal;
    /**
     * Atributo que almacena el valor ingresado en el campo atrasos
     * mayores del formulario
     */
    private String atrasos;
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
     * Listado de registros para el combo de ciclo inicial
     */
    private List<Registro> listaCiclo;
    /**
     * Listado de registros para el combo de ciclo final
     */
    private List<Registro> listacmbCicloF;

    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>
    // </DECLARAR_LISTAS_COMBO_GRANDE>
    /**
     * Crea una nueva instancia de
     * AuxiliarrecaudosconceptosControlador
     */
    public AuxiliarrecaudosconceptosControlador() {
        super();
        compania = SessionUtil.getCompania();
        modulo = SessionUtil.getModulo();
        try {
            numFormulario = GeneralCodigoFormaEnum.AUXILIARRECAUDOSCONCEPTOS_CONTROLADOR
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

        conceptoInicial = "0";
        conceptoFinal = "999";
        atrasos = "0";

        // <CODIGO_DESARROLLADO>
        /*
         * FR1194-AL_ABRIR Private Sub Form_Open(Cancel As Integer)
         * DoCmd.Restore formularioAbrir 74, Me.Name End Sub
         */
        // </CODIGO_DESARROLLADO>
    }

    // <METODOS_CARGAR_LISTA>
    /**
     *
     * Carga la lista listaCiclo
     *
     */
    public void cargarListaCiclo() {
        try {

            Map<String, Object> param = new TreeMap<>();
            param.put(GeneralParameterEnum.COMPANIA.getName(),
                            compania);

            listaCiclo = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            AuxiliarrecaudosconceptosControladorUrlEnum.URL5493
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
     * Carga la lista listacmbCicloF
     *
     */
    public void cargarListacmbCicloF() {
        try {

            Map<String, Object> param = new TreeMap<>();
            param.put(GeneralParameterEnum.COMPANIA.getName(),
                            compania);
            param.put(AuxiliarrecaudosconceptosControladorEnum.PARAM0
                            .getValue(),
                            String.valueOf(cicloInicial));

            listacmbCicloF = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            AuxiliarrecaudosconceptosControladorUrlEnum.URL5946
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
     * Metodo ejecutado al oprimir el boton Presentar en la vista
     *
     *
     */
    public void oprimirPresentar() {
        // <CODIGO_DESARROLLADO>
        archivoDescarga = null;
        generarInforme(FORMATOS.PDF);
        // </CODIGO_DESARROLLADO>
    }

    /**
     *
     * Metodo ejecutado al oprimir el boton Excel en la vista
     *
     *
     */
    public void oprimirExcel() {
        // <CODIGO_DESARROLLADO>
        archivoDescarga = null;
        generarInforme(FORMATOS.EXCEL);
        // </CODIGO_DESARROLLADO>
    }

    // </METODOS_BOTONES>
    /**
     * Metodo que se ejecuta al darle clic al boton presentar o excel
     * del formulario
     *
     * @param formato
     */
    public void generarInforme(ReportesBean.FORMATOS formato) {
        try {
            String parReporte = "001240LAuxiliarRecaudosConcepto";

            HashMap<String, Object> reemplazar = new HashMap<>();
            reemplazar.put("cicloInicial", "'" + cicloInicial + "'");
            reemplazar.put("cicloFinal", "'" + cicloFinal + "'");
            reemplazar.put("conceptoInicial", conceptoInicial);
            reemplazar.put("conceptoFinal", conceptoFinal);
            reemplazar.put("atraso", atrasos);

            Map<String, Object> parametros = new HashMap<>();
            parametros.put("PR_CICLOINICIAL", cicloInicial);
            parametros.put("PR_CICLOFINAL", cicloFinal);

            Reporteador.resuelveConsulta(parReporte, Integer.parseInt(modulo),
                            reemplazar, parametros);
            archivoDescarga = JsfUtil.exportarStreamed(parReporte, parametros,
                            ConectorPool.ESQUEMA_SYSMAN, formato);
        }
        catch (JRException | IOException | SysmanException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }
    // <METODOS_CAMBIAR>

    public void cambiarCiclo() {
        cicloFinal = null;
        cargarListacmbCicloF();
    }

    // </METODOS_CAMBIAR>
    // <METODOS_COMBOS_GRANDES>
    // </METODOS_COMBOS_GRANDES>
    // <METODOS_ARBOL>
    // </METODOS_ARBOL>
    // <SET_GET_ATRIBUTOS>
    /**
     * Retorna la variable cicloInicial
     *
     * @return cicloInicial
     */
    public String getCicloInicial() {
        return cicloInicial;
    }

    /**
     * Asigna la variable cicloInicial
     *
     * @param cicloInicial
     * Variable a asignar en cicloInicial
     */
    public void setCicloInicial(String cicloInicial) {
        this.cicloInicial = cicloInicial;
    }

    /**
     * Retorna la variable cicloFinal
     *
     * @return cicloFinal
     */
    public String getCicloFinal() {
        return cicloFinal;
    }

    /**
     * Asigna la variable cicloFinal
     *
     * @param cicloFinal
     * Variable a asignar en cicloFinal
     */
    public void setCicloFinal(String cicloFinal) {
        this.cicloFinal = cicloFinal;
    }

    /**
     * Retorna la variable conceptoInicial
     *
     * @return conceptoInicial
     */
    public String getConceptoInicial() {
        return conceptoInicial;
    }

    /**
     * Asigna la variable conceptoInicial
     *
     * @param conceptoInicial
     * Variable a asignar en conceptoInicial
     */
    public void setConceptoInicial(String conceptoInicial) {
        this.conceptoInicial = conceptoInicial;
    }

    /**
     * Retorna la variable conceptoFinal
     *
     * @return conceptoFinal
     */
    public String getConceptoFinal() {
        return conceptoFinal;
    }

    /**
     * Asigna la variable conceptoFinal
     *
     * @param conceptoFinal
     * Variable a asignar en conceptoFinal
     */
    public void setConceptoFinal(String conceptoFinal) {
        this.conceptoFinal = conceptoFinal;
    }

    /**
     * Retorna la variable atrasos
     *
     * @return atrasos
     */
    public String getAtrasos() {
        return atrasos;
    }

    /**
     * Asigna la variable atrasos
     *
     * @param atrasos
     * Variable a asignar en atrasos
     */
    public void setAtrasos(String atrasos) {
        this.atrasos = atrasos;
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

    /**
     * Retorna la lista listacmbCicloF
     *
     * @return listacmbCicloF
     */
    public List<Registro> getListacmbCicloF() {
        return listacmbCicloF;
    }

    /**
     * Asigna la lista listacmbCicloF
     *
     * @param listacmbCicloF
     * Variable a asignar en listacmbCicloF
     */
    public void setListacmbCicloF(List<Registro> listacmbCicloF) {
        this.listacmbCicloF = listacmbCicloF;
    }
    // </SET_GET_LISTAS>
    // <SET_GET_LISTAS_COMBO_GRANDE>
    // </SET_GET_LISTAS_COMBO_GRANDE>
}
