/*-
 * ImpresionPorLotesAlmacenControlador.java
 *
 * 1.0
 * 
 * 23/08/2018
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */
package com.sysman.almacen;

import com.sysman.almacen.enums.ImpresionPorLotesAlmacenControladorEnum;
import com.sysman.almacen.enums.ImpresionPorLotesAlmacenControladorUrlEnum;
import com.sysman.beanbase.BeanBaseModal;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.ReportesBean;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.persistencia.ConectorPool;
import com.sysman.recursos.ejb.impl.EjbSysmanUtil;
import com.sysman.reportes.Reporteador;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.util.SysmanFunciones;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.event.SelectEvent;
import org.primefaces.model.StreamedContent;

import net.sf.jasperreports.engine.JRException;

/**
 *
 * @version 1.0, 23/08/2018
 * @author jrojas
 */
@ManagedBean
@ViewScoped
public class ImpresionPorLotesAlmacenControlador extends BeanBaseModal {
    /**
     * Constante a nivel de clase que almacena el codigo de la
     * compania en la cual inicio sesion el usuario, el valor de esta
     * constante es asignado en el constructor a la variable de sesion
     * correspondiente
     */
    private final String compania;
    // <DECLARAR_ATRIBUTOS>
    private String numeroInicial;
    private String numeroFinal;
    private String tipo;
    private String modulo;

    @EJB
    EjbSysmanUtil ejbSysmanUtil;
    private String digitosAgrupacionInventario;
    private String formatoReportes;
    /**
     * Atributo usado para descargar contenidos de archivos desde la
     * vista
     */
    private StreamedContent archivoDescarga;

    // </DECLARAR_ATRIBUTOS>
    // <DECLARAR_PARAMETROS>
    // </DECLARAR_PARAMETROS>
    // <DECLARAR_LISTAS>
    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>

    private RegistroDataModelImpl listanumeroInicial;
    private RegistroDataModelImpl listanumeroFinal;
    private RegistroDataModelImpl listaTipo;

    // </DECLARAR_LISTAS_COMBO_GRANDE>
    /**
     * Crea una nueva instancia de ImpresionPorLotesAlmacenControlador
     */
    public ImpresionPorLotesAlmacenControlador() {
        super();
        compania = SessionUtil.getCompania();
        modulo = SessionUtil.getModulo();
        try {
            // 1899
            numFormulario = GeneralCodigoFormaEnum.IMPRESIONPORLOTESALMACENCONTROLADOR
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
        abrirFormulario();
        // <CARGAR_LISTA>
        cargarListaTipo();
        // </CARGAR_LISTA>
        // <CARGAR_LISTA_COMBO_GRANDE>

        // </CARGAR_LISTA_COMBO_GRANDE>
        // <CREAR_ARBOLES>
        // </CREAR_ARBOLES>

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
            digitosAgrupacionInventario = SysmanFunciones
                            .nvlStr(ejbSysmanUtil.consultarParametro(compania,
                                            "DIGITOS AGRUPACION INVENTARIO",
                                            modulo, new Date(), true), "");
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
        }

        // </CODIGO_DESARROLLADO>
    }

    // <METODOS_CARGAR_LISTA>
    /**
     * 
     * Carga la lista listanumeroInicial
     *
     */
    public void cargarListanumeroInicial() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        ImpresionPorLotesAlmacenControladorUrlEnum.URL1718
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(ImpresionPorLotesAlmacenControladorEnum.TIPOMOVIMIENTO
                        .getValue(), tipo);

        listanumeroInicial = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param, true,
                        GeneralParameterEnum.NUMERO.getName());
    }

    /**
     * 
     * Carga la lista listanumeroFinal
     *
     */
    public void cargarListanumeroFinal() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        ImpresionPorLotesAlmacenControladorUrlEnum.URL1719
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(ImpresionPorLotesAlmacenControladorEnum.TIPOMOVIMIENTO
                        .getValue(), tipo);
        param.put(ImpresionPorLotesAlmacenControladorEnum.NUMEROINICIAL
                        .getValue(), numeroInicial);

        listanumeroFinal = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param, true,
                        GeneralParameterEnum.NUMERO.getName());

    }

    /**
     * 
     * Carga la lista listaTipo
     *
     */
    public void cargarListaTipo() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        ImpresionPorLotesAlmacenControladorUrlEnum.URL1717
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        listaTipo = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param, true,
                        GeneralParameterEnum.CODIGO.getName());
    }

    // </METODOS_CARGAR_LISTA>
    // <METODOS_BOTONES>
    /**
     * 
     * Metodo ejecutado al oprimir el boton GeneraExcel en la vista
     *
     *
     */
    public void oprimirGeneraExcel() {
        // <CODIGO_DESARROLLADO>
        archivoDescarga = null;
        generarReporte(ReportesBean.FORMATOS.EXCEL);
        // </CODIGO_DESARROLLADO>
    }

    /**
     * 
     * Metodo ejecutado al oprimir el boton GeneraPdf en la vista
     *
     *
     */
    public void oprimirGeneraPdf() {
        // <CODIGO_DESARROLLADO>
        archivoDescarga = null;
        generarReporte(ReportesBean.FORMATOS.PDF);
        // </CODIGO_DESARROLLADO>
    }
    // </METODOS_BOTONES>
    // <METODOS_CAMBIAR>
    // </METODOS_CAMBIAR>
    // <METODOS_COMBOS_GRANDES>

    private void generarReporte(ReportesBean.FORMATOS formato) {
        HashMap<String, Object> reemplazar = new HashMap<>();
        HashMap<String, Object> parametros = new HashMap<>();
        String reporte = null;
        
       
        // Codigo del reporte
        // reporte = formatoReportes;
        // <REEMPLAZAR VARIABLES EN CONSULTA>
        try {
            boolean menu = false;
            reemplazar.put(GeneralParameterEnum.COMPANIA.getName(), compania);
            reemplazar.put(ImpresionPorLotesAlmacenControladorEnum.tipoMovimiento
                            .getValue(), tipo);
            reemplazar.put(ImpresionPorLotesAlmacenControladorEnum.movimientoInicial
                            .getValue(), numeroInicial);
            reemplazar.put(ImpresionPorLotesAlmacenControladorEnum.movimientoFinal
                            .getValue(), numeroFinal);
            reemplazar.put(ImpresionPorLotesAlmacenControladorEnum.nivelGrupo
                            .getValue(), digitosAgrupacionInventario);
            parametros.put("PR_CIUDADCOMPANIA",
                            SessionUtil.getCompaniaIngreso().getCiudad());
            parametros.put("PR_MENU", menu);
            
            //JM 05/08/2024 INI 7750212 
            String cargoSecAlm = ejbSysmanUtil.consultarParametro(compania,"CARGO SECRETARIA ALMACEN",modulo, new Date(),true);
            parametros.put("PR_CARGO_SECRETARIA_ALMACEN", cargoSecAlm);
            //JM 05/08/2024 FIN 7750212 
            
            String firmaAlmacenistaGeneral = ejbSysmanUtil.consultarParametro(compania,
                    "FIRMA ALMACENISTA GENERAL",modulo, new Date(), false);
            
            parametros.put("PR_FIRMA_ALMACENISTA_GENERAL", firmaAlmacenistaGeneral);
            
            
            
            String Almacenista = ejbSysmanUtil.consultarParametro(compania,
                    "ALMACENISTA",modulo, new Date(), false);
            
            parametros.put("PR_ALMACENISTA", Almacenista);
            
            
            String CargoAlmacenista = ejbSysmanUtil.consultarParametro(compania,
                    "CARGO ALMACENISTA",modulo, new Date(), false);
            
            parametros.put("PR_CARGO_ALMACENISTA", CargoAlmacenista);
            parametros.put("PR_REVISOR_ALMACEN", ejbSysmanUtil.consultarParametro(compania,"REVISOR ALMACEN",modulo, new Date(),true));
            parametros.put("PR_CARGO_REVISOR_ALMACEN", ejbSysmanUtil.consultarParametro(compania,"CARGO REVISOR ALMACEN",modulo, new Date(),true));
            parametros.put("PR_COORDINADOR_ALMACEN", ejbSysmanUtil.consultarParametro(compania,"COORDINADOR ALMACEN",modulo, new Date(),true));
            parametros.put("PR_CARGO_COORDINADOR_ALMACEN", ejbSysmanUtil.consultarParametro(compania,"CARGO COORDINADOR ALMACEN",modulo, new Date(),true));
            parametros.put("PR_ORDENADOR_ALMACEN", ejbSysmanUtil.consultarParametro(compania,"ORDENADOR ALMACEN",modulo, new Date(),true));
            parametros.put("PR_CARGO_ORDENADOR_ALMACEN", ejbSysmanUtil.consultarParametro(compania,"CARGO ORDENADOR ALMACEN",modulo, new Date(),true));
            

            // </REEMPLAZAR VARIABLES EN CONSULTA

            // <ENVIAR PARAMETROS AL REPORTE>
            // </ENVIAR PARAMETROS AL REPORTE>
            if (formatoReportes.equals("001986MOIDIUPC")) {

                reporte = "000528MOIDITUN";

            }
            else {

                reporte = formatoReportes;

            }
            Reporteador.resuelveConsulta(reporte, Integer.parseInt(modulo),
                            reemplazar, parametros);

            /*-aqui reporte hace referencia al nombre del reporte*/
            archivoDescarga = JsfUtil.exportarStreamed(formatoReportes,
                            parametros,
                            ConectorPool.ESQUEMA_SYSMAN, formato);
        }

        catch (JRException | IOException
                        | com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException | SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listanumeroInicial
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilanumeroInicial(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        numeroInicial = registroAux.getCampos().get("NUMERO").toString();
        cargarListanumeroFinal();
    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listanumeroFinal
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilanumeroFinal(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        numeroFinal = registroAux.getCampos().get("NUMERO").toString();

    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista listaTipo
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaTipo(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        tipo = registroAux.getCampos().get("CODIGO").toString();
        formatoReportes = registroAux.getCampos().get("TIPOFORMATO").toString();
        registroAux.getCampos().put(numeroInicial, null);
        registroAux.getCampos().put(numeroFinal, null);
        cargarListanumeroInicial();
        cargarListanumeroFinal();
    }

    // </METODOS_COMBOS_GRANDES>
    // <METODOS_ARBOL>
    // </METODOS_ARBOL>
    // <SET_GET_ATRIBUTOS>
    /**
     * Retorna la variable numeroInicial
     * 
     * @return numeroInicial
     */
    public String getNumeroInicial() {
        return numeroInicial;
    }

    /**
     * Asigna la variable numeroInicial
     * 
     * @param numeroInicial
     * Variable a asignar en numeroInicial
     */
    public void setNumeroInicial(String numeroInicial) {
        this.numeroInicial = numeroInicial;
    }

    /**
     * Retorna la variable numeroFinal
     * 
     * @return numeroFinal
     */
    public String getNumeroFinal() {
        return numeroFinal;
    }

    /**
     * Asigna la variable numeroFinal
     * 
     * @param numeroFinal
     * Variable a asignar en numeroFinal
     */
    public void setNumeroFinal(String numeroFinal) {
        this.numeroFinal = numeroFinal;
    }

    /**
     * Retorna la variable tipo
     * 
     * @return tipo
     */
    public String getTipo() {
        return tipo;
    }

    /**
     * Asigna la variable tipo
     * 
     * @param tipo
     * Variable a asignar en tipo
     */
    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    /**
     * Retorna la variable formatoreporte
     * 
     * @return formatoreporte
     */

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
    // </SET_GET_LISTAS>
    // <SET_GET_LISTAS_COMBO_GRANDE>
    /**
     * Retorna la lista listanumeroInicial
     * 
     * @return listanumeroInicial
     */
    public RegistroDataModelImpl getListaTipo() {
        return listaTipo;
    }

    public void setListaTipo(RegistroDataModelImpl listaTipo) {
        this.listaTipo = listaTipo;
    }

    public RegistroDataModelImpl getListanumeroFinal() {
        return listanumeroFinal;
    }

    public void setListanumeroFinal(RegistroDataModelImpl listanumeroFinal) {
        this.listanumeroFinal = listanumeroFinal;
    }

    public RegistroDataModelImpl getListanumeroInicial() {
        return listanumeroInicial;
    }

    public void setListanumeroInicial(
        RegistroDataModelImpl listanumeroInicial) {
        this.listanumeroInicial = listanumeroInicial;
    }

    public String getFormatoReportes() {
        return formatoReportes;
    }

    public void setFormatoReportes(String formatoReportes) {
        this.formatoReportes = formatoReportes;
    }

    public String getDigitosAgrupacionInventario() {
        return digitosAgrupacionInventario;
    }

    public void setDigitosAgrupacionInventario(
        String digitosAgrupacionInventario) {
        this.digitosAgrupacionInventario = digitosAgrupacionInventario;
    }

    // </SET_GET_LISTAS_COMBO_GRANDE>
}
