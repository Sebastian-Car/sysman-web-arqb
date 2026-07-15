/*-
 * FrmbajasmesControlador.java
 *
 * 1.0
 * 
 * 08/08/2018
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */
package com.sysman.almacen;

import com.sysman.almacen.enums.FrmbajasmesControladorControladorUrlEnum;
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
import com.sysman.persistencia.ConectorPool;
import com.sysman.recursos.ejb.impl.EjbSysmanUtil;
import com.sysman.reportes.Reporteador;
import com.sysman.util.SysmanFunciones;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.model.StreamedContent;

import net.sf.jasperreports.engine.JRException;

/**
 * Formulario modal que permite la generación del reporte
 * "001843BAJASMES"
 *
 * @version 1.0, 08/08/2018
 * @author jmalaver
 */
@ManagedBean
@ViewScoped
public class FrmbajasmesControlador extends BeanBaseModal {
    /**
     * Constante a nivel de clase que almacena el codigo de la
     * compania en la cual inicio sesion el usuario, el valor de esta
     * constante es asignado en el constructor a la variable de sesion
     * correspondiente
     */
    private final String compania;

    // <DECLARAR_ATRIBUTOS>

    /**
     * Variable de clase que almacena el valor del año
     */
    private int ano;

    /**
     * Variable de clase que almacena el valor del mes inicial
     */
    private String mesInicial;

    /**
     * Variable de clase que almacena el valor del mes final
     */
    private String mesFinal;

    /**
     * Atributo usado para descargar contenidos de archivos desde la
     * vista
     */
    private StreamedContent archivoDescarga;

    @EJB
    EjbSysmanUtil ejbSysmanUtil;

    // </DECLARAR_ATRIBUTOS>
    // <DECLARAR_PARAMETROS>
    // </DECLARAR_PARAMETROS>
    // <DECLARAR_LISTAS>
    /**
     * Lista de registros para el combo CbAno
     */
    private List<Registro> listaCbAno;

    /**
     * Lista de registros para el combo CbMesInicial
     */
    private List<Registro> listacbMesInicial;

    /**
     * Lista de registros para el combo CbMesFinal
     */
    private List<Registro> listacbMesFinal;

    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>
    // </DECLARAR_LISTAS_COMBO_GRANDE>
    /**
     * Crea una nueva instancia de FrmbajasmesControlador
     */
    public FrmbajasmesControlador() {
        super();
        compania = SessionUtil.getCompania();
        ano = SysmanFunciones.ano(new Date());

        try {
            numFormulario = GeneralCodigoFormaEnum.FRM_BAJAS_MES_CONTROLADOR
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
        cargarListaCbAno();
        cargarListacbMesInicial();

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
     * Carga la lista listaCbAno
     *
     */
    public void cargarListaCbAno() {
        Map<String, Object> parametros = new HashMap<>();

        parametros.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        try {
            listaCbAno = RegistroConverter
                            .toListRegistro(requestManager.getList(
                                            UrlServiceUtil.getInstance()
                                                            .getUrlServiceByUrlByEnumID(
                                                                            FrmbajasmesControladorControladorUrlEnum.URL001
                                                                                            .getValue())
                                                            .getUrl(),
                                            parametros));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    /**
     * 
     * Carga la lista listacbMesInicial
     *
     */
    public void cargarListacbMesInicial() {
        Map<String, Object> parametros = new HashMap<>();

        parametros.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        parametros.put(GeneralParameterEnum.ANO.getName(), ano);

        try {
            listacbMesInicial = RegistroConverter
                            .toListRegistro(requestManager.getList(
                                            UrlServiceUtil.getInstance()
                                                            .getUrlServiceByUrlByEnumID(
                                                                            FrmbajasmesControladorControladorUrlEnum.URL002
                                                                                            .getValue())
                                                            .getUrl(),
                                            parametros));

        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    /**
     * 
     * Carga la lista listacbMesFinal
     *
     */
    public void cargarListacbMesFinal() {
        Map<String, Object> parametros = new HashMap<>();

        parametros.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
        parametros.put(GeneralParameterEnum.ANO.getName(), ano);
        parametros.put("NUMERO", mesInicial);

        try {
            listacbMesFinal = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            FrmbajasmesControladorControladorUrlEnum.URL003
                                                                            .getValue())
                                            .getUrl(),
                                            parametros));
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
     * Metodo ejecutado al oprimir el boton BtnExcel en la vista
     *
     */
    public void oprimirBtnExcel() {
        // <CODIGO_DESARROLLADO>
        archivoDescarga = null;
        generaReporte(FORMATOS.EXCEL);
        // </CODIGO_DESARROLLADO>
    }

    /**
     * 
     * Metodo ejecutado al oprimir el boton BtnPdf en la vista
     *
     */
    public void oprimirBtnPdf() {
        // <CODIGO_DESARROLLADO>
        archivoDescarga = null;
        generaReporte(FORMATOS.PDF);
        // </CODIGO_DESARROLLADO>
    }

    // </METODOS_BOTONES>
    // <METODOS_CAMBIAR>
    /**
     * Metodo ejecutado al cambiar el control cbMesInicial
     * 
     */
    public void cambiarcbMesInicial() {
        // <CODIGO_DESARROLLADO>
        cargarListacbMesFinal();
        // </CODIGO_DESARROLLADO>
    }

    // </METODOS_CAMBIAR>
    // <METODOS_COMBOS_GRANDES>
    // </METODOS_COMBOS_GRANDES>
    // <METODOS_ARBOL>
    private void generaReporte(FORMATOS formato) {
        try {
            String reporte = "001843BAJASMES";

            String nombreMesInicial = ejbSysmanUtil
                            .mostrarNombreDeMes(Integer.parseInt(mesInicial))
                            .toUpperCase();

            String nombreMesFinal = ejbSysmanUtil
                            .mostrarNombreDeMes(Integer.parseInt(mesFinal))
                            .toUpperCase();

            // PARAMETROS DE REEMPLAZO EN LA CONSULTA
            HashMap<String, Object> reemplazar = new HashMap<>();
            reemplazar.put("compania", compania);
            reemplazar.put("mesInicial", mesInicial);
            reemplazar.put("mesFinal", mesFinal);
            reemplazar.put("ano", ano);

            // MANEJO DE PARAMETROS DEL REPORTE
            Map<String, Object> parametros = new HashMap<>();
            parametros.put("PR_ANO", SysmanFunciones.toString(ano));
            parametros.put("PR_MESINICIAL", nombreMesInicial);
            parametros.put("PR_MESFINAL", nombreMesFinal);

            Reporteador.resuelveConsulta(reporte,
                            Integer.parseInt(SessionUtil.getModulo()),
                            reemplazar,
                            parametros);

            archivoDescarga = JsfUtil.exportarStreamed(reporte, parametros,
                            ConectorPool.ESQUEMA_SYSMAN, formato);
        }
        catch (JRException | IOException | SysmanException
                        | NumberFormatException | SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    // </METODOS_ARBOL>
    // <SET_GET_ATRIBUTOS>
    /**
     * Retorna la variable ano
     * 
     * @return ano
     */
    public int getAno() {
        return ano;
    }

    /**
     * Asigna la variable ano
     * 
     * @param ano
     * Variable a asignar en ano
     */
    public void setAno(int ano) {
        this.ano = ano;
    }

    /**
     * Retorna la variable mesInicial
     * 
     * @return mesInicial
     */
    public String getMesInicial() {
        return mesInicial;
    }

    /**
     * Asigna la variable mesInicial
     * 
     * @param mesInicial
     * Variable a asignar en mesInicial
     */
    public void setMesInicial(String mesInicial) {
        this.mesInicial = mesInicial;
    }

    /**
     * Retorna la variable MesFinal
     * 
     * @return MesFinal
     */
    public String getMesFinal() {
        return mesFinal;
    }

    /**
     * Asigna la variable MesFinal
     * 
     * @param MesFinal
     * Variable a asignar en MesFinal
     */
    public void setMesFinal(String mesFinal) {
        this.mesFinal = mesFinal;
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
     * Retorna la lista listaCbAno
     * 
     * @return listaCbAno
     */
    public List<Registro> getListaCbAno() {
        return listaCbAno;
    }

    /**
     * Asigna la lista listaCbAno
     * 
     * @param listaCbAno
     * Variable a asignar en listaCbAno
     */
    public void setListaCbAno(List<Registro> listaCbAno) {
        this.listaCbAno = listaCbAno;
    }

    /**
     * Retorna la lista listacbMesInicial
     * 
     * @return listacbMesInicial
     */
    public List<Registro> getListacbMesInicial() {
        return listacbMesInicial;
    }

    /**
     * Asigna la lista listacbMesInicial
     * 
     * @param listacbMesInicial
     * Variable a asignar en listacbMesInicial
     */
    public void setListacbMesInicial(List<Registro> listacbMesInicial) {
        this.listacbMesInicial = listacbMesInicial;
    }

    /**
     * Retorna la lista listacbMesFinal
     * 
     * @return listacbMesFinal
     */
    public List<Registro> getListacbMesFinal() {
        return listacbMesFinal;
    }

    /**
     * Asigna la lista listacbMesFinal
     * 
     * @param listacbMesFinal
     * Variable a asignar en listacbMesFinal
     */
    public void setListacbMesFinal(List<Registro> listacbMesFinal) {
        this.listacbMesFinal = listacbMesFinal;
    }
    // </SET_GET_LISTAS>
    // <SET_GET_LISTAS_COMBO_GRANDE>
    // </SET_GET_LISTAS_COMBO_GRANDE>

}
