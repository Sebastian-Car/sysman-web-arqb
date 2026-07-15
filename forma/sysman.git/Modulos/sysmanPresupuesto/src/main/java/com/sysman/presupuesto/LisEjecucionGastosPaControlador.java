/*-
 * LisEjecucionGastosPa.java
 *
 * 1.0
 * 
 * 07/12/2017
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */
package com.sysman.presupuesto;

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
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.persistencia.ConectorPool;
import com.sysman.presupuesto.enums.LisEjecucionGastosPaControladorEnum;
import com.sysman.presupuesto.enums.LisEjecucionGastosPaControladorUrlEnum;
import com.sysman.recursos.ejb.EjbSysmanUtilRemote;
import com.sysman.reportes.Reporteador;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.util.SysmanConstantes;
import com.sysman.util.SysmanFunciones;

import java.io.IOException;
import java.sql.SQLException;
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
 * Formulario que permite generar informes de la ejecucion de gastos
 * presupuestales
 *
 * @version 1.0, 07/12/2017
 * @author jreina
 */

@ManagedBean
@ViewScoped
public class LisEjecucionGastosPaControlador extends BeanBaseModal {
    /**
     * Constante a nivel de clase que almacena el codigo de la
     * compania en la cual inicio sesion el usuario, el valor de esta
     * constante es asignado en el constructor a la variable de sesion
     * correspondiente
     */
    private final String compania;
    /**
     * Constante que almacena el modulo en el que se encuentra el
     * usuario.
     */
    private final String modulo;
    // <DECLARAR_ATRIBUTOS>
    /**
     * Atributo que contiene el valor del check especial
     */
    private boolean ckEspecial;
    /**
     * Atributo que contiene el valor del check auxiliar
     */
    private boolean ckAuxiliar;
    /**
     * Atributo que contiene el valor del check especial excel
     */
    private boolean ckEspecialExcel;
    /**
     * Atributo que contiene el valor de la cuenta inicial del
     * formulario
     */
    private String cuentaInicial;
    /**
     * Atributo que contiene el valor de la cuenta final del
     * formulario
     */
    private String cuentaFinal;
    /**
     * Atributo que contiene el valor del anio del formulario
     */
    private String anio;
    /**
     * Atributo que contiene el valor del mes del formulario
     */
    private String mes;
    /**
     * Atributo usado para descargar contenidos de archivos desde la
     * vista
     */
    private StreamedContent archivoDescarga;

    @EJB
    private EjbSysmanUtilRemote ejbSysmanUtil;
    // </DECLARAR_ATRIBUTOS>
    // <DECLARAR_PARAMETROS>
    // </DECLARAR_PARAMETROS>
    // <DECLARAR_LISTAS>
    /** Lista que contiene los detalles del campo ano. */
    private List<Registro> listaAno;
    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>
    /**
     * Lista que contiene los detalles del combo de cuenta inicial.
     */
    private RegistroDataModelImpl listaCuentaInicial;

    /** Lista que contiene los detalles del combo de cuenta final. */
    private RegistroDataModelImpl listaCuentaFinal;

    // </DECLARAR_LISTAS_COMBO_GRANDE>
    /**
     * Crea una nueva instancia de LisEjecucionGastosPa
     */
    public LisEjecucionGastosPaControlador() {
        super();
        compania = SessionUtil.getCompania();
        modulo = SessionUtil.getModulo();

        anio = Integer.toString(SysmanFunciones.ano(new Date()));
        mes = Integer.toString(SysmanFunciones.mes(new Date()));
        cuentaInicial = SysmanConstantes.DEFECTOINICIAL_STRING;
        cuentaFinal = SysmanConstantes.DEFECTOFINAL_STRING;

        try {
            numFormulario = GeneralCodigoFormaEnum.LIS_EJECUCION_GASTOSPA_CONTROLADOR
                            .getCodigo();
            validarPermisos();
            // <INI_ADICIONAL>
            // </INI_ADICIONAL>
        } catch (Exception ex) {
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
        cargarListaAno();
        // </CARGAR_LISTA>
        // <CARGAR_LISTA_COMBO_GRANDE>
        cargarListaCuentaInicial();
        cargarListaCuentaFinal();
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
     * Carga la lista listaAno
     *
     */
    public void cargarListaAno() {
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        try {
            listaAno = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            LisEjecucionGastosPaControladorUrlEnum.URL12003
                                                                            .getValue())
                                            .getUrl(), param));
        } catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    /**
     * 
     * Carga la lista listaCuentaInicial
     *
     */
    public void cargarListaCuentaInicial() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        LisEjecucionGastosPaControladorUrlEnum.URL11959
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.ANO.getName(), anio);
        param.put(GeneralParameterEnum.NATURALEZA.getName(), "D");

        listaCuentaInicial = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param, true,
                        "ID");
    }

    /**
     * 
     * Carga la lista listaCuentaFinal
     *
     */
    public void cargarListaCuentaFinal() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        LisEjecucionGastosPaControladorUrlEnum.URL12000
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.ANO.getName(), anio);
        param.put(LisEjecucionGastosPaControladorEnum.PARAM0.getValue(),
                        cuentaInicial);
        param.put(GeneralParameterEnum.NATURALEZA.getName(), "D");

        listaCuentaFinal = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param, true,
                        "ID");
    }

    // </METODOS_CARGAR_LISTA>
    // <METODOS_BOTONES>
    /**
     * 
     * Metodo ejecutado al oprimir el boton Imprimir en la vista
     *
     *
     */
    public void oprimirImprimir() {
        // <CODIGO_DESARROLLADO>
        generarInforme(FORMATOS.PDF);
        // </CODIGO_DESARROLLADO>
    }

    /**
     * 
     * Metodo ejecutado al oprimir el boton excel en la vista
     *
     *
     */
    public void oprimirexcel() {
        // <CODIGO_DESARROLLADO>
        generarInforme(FORMATOS.EXCEL);
        // </CODIGO_DESARROLLADO>
    }

    public void generarInforme(FORMATOS formato) {
        try {
            archivoDescarga = null;
            String reporte;

            HashMap<String, Object> reemplazar = new HashMap<>();
            reemplazar.put("compania", compania);
            reemplazar.put("anio", anio);
            reemplazar.put("mes", mes);
            reemplazar.put("cuentaInicial", cuentaInicial);
            reemplazar.put("cuentaFinal", cuentaFinal);

            Map<String, Object> parametros = new HashMap<>();
            parametros.put("PR_MES", ejbSysmanUtil
                            .mostrarNombreDeMes(Integer.parseInt(mes)));
            parametros.put("PR_FIRMA_REPRESENTANTE_LEGAL",
                            consultarParametro("FIRMA REPRESENTANTE LEGAL"));
            parametros.put("PR_NOMBRE_DE_SECRETARIA_DE_HACIENDA",
                            consultarParametro(
                                            "NOMBRE DE SECRETARIA DE HACIENDA"));
            parametros.put("PR_FIRMA_EJECUCION_1",
                            consultarParametro("FIRMA EJECUCION 1"));
            parametros.put("PR_FIRMA_EJECUCION_2",
                            consultarParametro("FIRMA EJECUCION 2"));
            parametros.put("PR_FIRMA_EJECUCION_3",
                            consultarParametro("FIRMA EJECUCION 3"));
            parametros.put("PR_CARGO_EJECUCION_1",
                            consultarParametro("CARGO EJECUCION 1"));
            parametros.put("PR_CARGO_EJECUCION_2",
                            consultarParametro("CARGO EJECUCION 2"));
            parametros.put("PR_CARGO_EJECUCION_3",
                            consultarParametro("CARGO EJECUCION 3"));

            parametros.put("PR_NOMBRECOMPANIA",
                            SessionUtil.getCompaniaIngreso().getNombre());
            parametros.put("PR_NOMBRE_REPRESENTANTE_LEGAL",
                            consultarParametro("NOMBRE REPRESENTANTE LEGAL"));
            parametros.put("PR_NITCOMPANIA",
                            SessionUtil.getCompaniaIngreso().getNit());
            parametros.put("PR_ANO", anio);

            if ("SI".equals(ejbSysmanUtil.consultarParametro(compania,
                            "FORMATO ESPECIAL EJECUCION DE GASTOS TRINIDAD",
                            modulo, new Date(), true))) {
                reporte = "001536CCEJECUCIONGASTOSTRINI";
            } else if (ckAuxiliar) {
                reporte = "001537LisEjecucionGastosPAUXILIARES";
            } else if (ckEspecial) {
                reporte = "001538LisEjecucionGastosPAES";
            } else {
                reporte = "001539LisEjecucionGastosPA";
            }
            if ("001539LisEjecucionGastosPA".equals(reporte)
                && FORMATOS.EXCEL.equals(formato)) {
                String strsql = Reporteador.resuelveConsulta(reporte,
                                Integer.parseInt(modulo), reemplazar);
                archivoDescarga = JsfUtil.exportarHojaDatosStreamed(strsql,
                                ConectorPool.ESQUEMA_SYSMAN, FORMATOS.EXCEL,
                                reporte);
            } else {
                Reporteador.resuelveConsulta(reporte,
                                Integer.parseInt(SessionUtil.getModulo()),
                                reemplazar, parametros);
                archivoDescarga = JsfUtil.exportarStreamed(reporte, parametros,
                                ConectorPool.ESQUEMA_SYSMAN, formato);
            }

        } catch (SystemException | JRException | IOException
                        | SysmanException | SQLException | DRException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    public String consultarParametro(String nombre) throws SystemException {
        return ejbSysmanUtil.consultarParametro(compania,
                        nombre,
                        modulo, new Date(), false);
    }

    // </METODOS_BOTONES>
    // <METODOS_CAMBIAR>
    public void cambiarAno() {
        cuentaInicial = cuentaFinal = null;
        cargarListaCuentaInicial();
        cargarListaCuentaFinal();
    }

    // </METODOS_CAMBIAR>
    // <METODOS_COMBOS_GRANDES>
    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaCuentaInicial
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaCuentaInicial(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        cuentaInicial = registroAux.getCampos().get("ID").toString();
        cargarListaCuentaFinal();
        cuentaFinal = null;
    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaCuentaFinal
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaCuentaFinal(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        cuentaFinal = registroAux.getCampos().get("ID").toString();
    }

    // </METODOS_COMBOS_GRANDES>
    // <METODOS_ARBOL>
    // </METODOS_ARBOL>
    // <SET_GET_ATRIBUTOS>

    /**
     * Retorna la variable cuentaInicial
     * 
     * @return cuentaInicial
     */
    public String getCuentaInicial() {
        return cuentaInicial;
    }

    public boolean isCkEspecial() {
        return ckEspecial;
    }

    public void setCkEspecial(boolean ckEspecial) {
        this.ckEspecial = ckEspecial;
    }

    public boolean isCkAuxiliar() {
        return ckAuxiliar;
    }

    public void setCkAuxiliar(boolean ckAuxiliar) {
        this.ckAuxiliar = ckAuxiliar;
    }

    /**
     * Asigna la variable cuentaInicial
     * 
     * @param cuentaInicial
     * Variable a asignar en cuentaInicial
     */
    public void setCuentaInicial(String cuentaInicial) {
        this.cuentaInicial = cuentaInicial;
    }

    /**
     * Retorna la variable cuentaFinal
     * 
     * @return cuentaFinal
     */
    public String getCuentaFinal() {
        return cuentaFinal;
    }

    /**
     * Asigna la variable cuentaFinal
     * 
     * @param cuentaFinal
     * Variable a asignar en cuentaFinal
     */
    public void setCuentaFinal(String cuentaFinal) {
        this.cuentaFinal = cuentaFinal;
    }

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
     * Retorna la lista listaAno
     * 
     * @return listaAno
     */
    public List<Registro> getListaAno() {
        return listaAno;
    }

    /**
     * Asigna la lista listaAno
     * 
     * @param listaAno
     * Variable a asignar en listaAno
     */
    public void setListaAno(List<Registro> listaAno) {
        this.listaAno = listaAno;
    }

    // </SET_GET_LISTAS>
    // <SET_GET_LISTAS_COMBO_GRANDE>
    /**
     * Retorna la lista listaCuentaInicial
     * 
     * @return listaCuentaInicial
     */
    public RegistroDataModelImpl getListaCuentaInicial() {
        return listaCuentaInicial;
    }

    /**
     * Asigna la lista listaCuentaInicial
     * 
     * @param listaCuentaInicial
     * Variable a asignar en listaCuentaInicial
     */
    public void setListaCuentaInicial(
        RegistroDataModelImpl listaCuentaInicial) {
        this.listaCuentaInicial = listaCuentaInicial;
    }

    /**
     * Retorna la lista listaCuentaFinal
     * 
     * @return listaCuentaFinal
     */
    public RegistroDataModelImpl getListaCuentaFinal() {
        return listaCuentaFinal;
    }

    /**
     * Asigna la lista listaCuentaFinal
     * 
     * @param listaCuentaFinal
     * Variable a asignar en listaCuentaFinal
     */
    public void setListaCuentaFinal(RegistroDataModelImpl listaCuentaFinal) {
        this.listaCuentaFinal = listaCuentaFinal;
    }

    public boolean isCkEspecialExcel() {
        return ckEspecialExcel;
    }

    public void setCkEspecialExcel(boolean ckEspecialExcel) {
        this.ckEspecialExcel = ckEspecialExcel;
    }

    // </SET_GET_LISTAS_COMBO_GRANDE>
}
