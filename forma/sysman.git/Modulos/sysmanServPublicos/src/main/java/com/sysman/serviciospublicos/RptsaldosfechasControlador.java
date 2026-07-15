/*-
 * RptsaldosfechasControlador.java
 *
 * 1.0
 * 
 * 15/11/2016
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
import com.sysman.recursos.ejb.EjbSysmanUtilRemote;
import com.sysman.reportes.Reporteador;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.serviciospublicos.enums.RptsaldosfechasControladorEnum;
import com.sysman.serviciospublicos.enums.RptsaldosfechasControladorUrlEnum;
import com.sysman.util.SysmanFunciones;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.ParseException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
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
 * Controlador del formulario Rptsaldosfechas
 *
 * @version 1.0, 15/11/2016
 * @author cperez
 * 
 * @author eamaya
 * @version 2.0, 15/06/2017 Proceso de Refactoring y Manejo de EJBs
 * 
 */
@ManagedBean
@ViewScoped

public class RptsaldosfechasControlador extends BeanBaseModal {
    /**
     * Constante a nivel de clase que almacena el codigo de la
     * compania en la cual inicio sesion el usuario, el valor de esta
     * constante es asignado en el constructor a la variable de sesion
     * correspondiente
     */
    private final String compania;
    /**
     * Constante a nivel de clase que almacena el numero del modulo en
     * el cual inicio sesion el usuario, el valor de esta constante es
     * asignado en el constructor a la variable de sesion
     * correspondiente
     */

    private final String modulo;
    // <DECLARAR_ATRIBUTOS>
    /**
     * Obtiene el estado del chec consolidado
     */
    private boolean checkConsolidado;
    /**
     * Obtiene el ciclo de la consulta
     */
    private String ciclo;
    /*
     * variable para dejar visible o no los campos del parametro
     * "INFORME SALDOS CREDITO POR BANCO"
     */
    private boolean informeSaldosCreditoVisible;
    /*
     * variable para dejar visible o no los campos del parametro
     * "MOSTRAR FILTROS ENTRE  BANCOS SALDOS CREDITOS"
     */
    private boolean mostrarFiltrosVisible;
    /*
     * variable para asiganar el nombre del tipo de error
     * msm_Trans_Interrumpida
     */
    private String msmTransInterrumpida;

    /**
     * Obtiene el Banco Inicial de la consulta
     */
    private String bancoInicial;
    /**
     * Obtiene el Banco Final de la consulta
     */

    private String bancoFinal;
    /**
     * Obtiene la fecha Inicial de la consulta
     */
    private Date fechaInicial;
    /**
     * Obtiene el fecha Inicial de la consulta
     */
    private Date fechaFinal;

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
    /**
     * Necesario para obtener y mandar la lista del Banco Inicial
     */
    private RegistroDataModelImpl listaBancoFinal;
    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>
    /**
     * Necesario para obtener y mandar la lista del Banco Inicial
     */
    private RegistroDataModelImpl listaBancoInicial;

    // </DECLARAR_LISTAS_COMBO_GRANDE>
    /**
     * Crea una nueva instancia de RptsaldosfechasControlador
     */

    @EJB
    private EjbSysmanUtilRemote ejbSysmanUtil;

    public RptsaldosfechasControlador() {
        super();
        compania = SessionUtil.getCompania();
        modulo = SessionUtil.getModulo();
        try {
            numFormulario = GeneralCodigoFormaEnum.RPTSALDOSFECHAS_CONTROLADOR
                            .getCodigo();
            msmTransInterrumpida = "MSM_TRANS_INTERRUMPIDA";

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
        cargarListaBancoInicial();
        // </CARGAR_LISTA_COMBO_GRANDE>
        // <CREAR_ARBOLES>
        // </CREAR_ARBOLES>
        abrirFormulario();
    }

    /*
     * Para saber si el parámetro “INFORME SALDOS CREDITO POR BANCO”
     * es igual a si o a no
     * 
     * @return parametroInformesaldos
     */
    public String parametroInformeSaldos() {
        String parametroInformesaldos = null;
        try {
            parametroInformesaldos = ejbSysmanUtil.consultarParametro(
                            compania, "INFORME SALDOS CREDITO POR BANCO",
                            modulo, new Date(),
                            false);

        }
        catch (SystemException ex) {
            JsfUtil.agregarMensajeError(idioma.getString(msmTransInterrumpida)
                + ex.getMessage());
            Logger.getLogger(RptsaldosfechasControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
        }
        return parametroInformesaldos;
    }

    /*
     * Para saber si el parámetro “MOSTRAR FILTROS ENTRE BANCOS SALDOS
     * CREDITOS” es igual a si o a no
     * 
     * @return parametroMostrarFiltros
     */
    public String parametroMostrarFiltros() {
        String parametroMostrarFiltros = null;
        try {
            parametroMostrarFiltros = ejbSysmanUtil.consultarParametro(
                            compania,
                            "MOSTRAR FILTROS ENTRE BANCOS SALDOS CREDITOS",
                            modulo, new Date(), false);

        }
        catch (SystemException ex) {
            JsfUtil.agregarMensajeError(idioma.getString(msmTransInterrumpida)
                + ex.getMessage());
            Logger.getLogger(RptsaldosfechasControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
        }
        return parametroMostrarFiltros;
    }

    /**
     * Este metodo es invocado el metodo inicializar, se ejecutan las
     * acciones a tener en cuenta en el momento de apertura del
     * formulario si el parametro parametroMostrarFiltros es igual a
     * si entonces hace visibles los siguientes objetos /*
     * Me!cmbBancoInicial.visible = True Me!cmbBancoFinal.visible =
     * True Me!Etiqueta25.visible = True Me!Etiqueta23.visible = True
     * si el parametro parametroInformeSaldos es igual a si hace
     * vicible los siguientes objetos Me!lbConsolidado.visible = True
     * Me!checkConsolidado.visible = True
     */
    @Override
    public void abrirFormulario() {
        // <CODIGO_DESARROLLADO>
        fechaInicial = new Date();
        fechaFinal = new Date();
        if ("SI".equals(parametroInformeSaldos())) {
            informeSaldosCreditoVisible = true;
        }
        else {
            informeSaldosCreditoVisible = false;
        }
        if ("SI".equals(parametroMostrarFiltros())) {
            mostrarFiltrosVisible = true;
        }
        else {
            mostrarFiltrosVisible = false;
        }
        // </CODIGO_DESARROLLADO>
    }

    // <METODOS_CARGAR_LISTA>
    /**
     * 
     * Carga la lista listaCiclo
     */
    public void cargarListaCiclo() {

        try {
            Map<String, Object> param = new TreeMap<>();
            param.put(GeneralParameterEnum.COMPANIA.getName(),
                            compania);

            listaCiclo = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            RptsaldosfechasControladorUrlEnum.URL9549
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
     * Carga la lista listaBancoFinal
     */
    public void cargarListaBancoFinal() {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        RptsaldosfechasControladorUrlEnum.URL10192
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);

        param.put(RptsaldosfechasControladorEnum.BANCOINICIAL.getValue(),
                        bancoInicial);

        listaBancoFinal = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, GeneralParameterEnum.CODIGO.getName());
    }

    /**
     * 
     * Carga la lista listaBancoInicial
     */
    public void cargarListaBancoInicial() {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        RptsaldosfechasControladorUrlEnum.URL10879
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);

        listaBancoInicial = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, GeneralParameterEnum.CODIGO.getName());

    }

    // </METODOS_CARGAR_LISTA>
    // <METODOS_BOTONES>
    /*
     * muesta el mensaje de error del campo Banco
     */
    public void mensajeBanco() {
        if (bancoInicial == null || "".equals(bancoInicial)) {
            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB1871"));
            return;
        }
        else if (bancoFinal == null || "".equals(bancoFinal)) {
            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB1872"));
            return;
        }
        long bancoIni = Long.parseLong(bancoInicial);
        long bancoFin = Long.parseLong(bancoFinal);
        if (bancoIni > bancoFin) {
            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB1822"));
            return;
        }
    }

    /*
     * valida si el vanco Inicial es menor que el banco Final
     * 
     * @return compribarBanco
     */
    public String validarBanco() {
        long bancoIni = Long.parseLong(bancoInicial);
        long bancoFin = Long.parseLong(bancoFinal);
        if (bancoIni > bancoFin) {
            return "";
        }
        else {
            return "1";
        }
    }

    /*
     * Retorna la variable compribarBanco
     * 
     * @return compribarBanco
     */
    public String comprobarBanco() {

        if ("SI".equals(parametroMostrarFiltros())) {

            if ("".equals(bancoInicial) || "".equals(bancoFinal)) {
                return "0";
            }
            else {
                return validarBanco();
            }
        }
        else {
            return "1";
        }
    }

    /**
     * Método que envia el reporte que se va a generar
     * 
     * @param formato
     */
    public void decision(FORMATOS formato) {
        archivoDescarga = null;
        if ("SI".equals(parametroInformeSaldos())) {
            if (!checkConsolidado) {
                genInforme(formato, "001249RptSaldosCreditoFechasPorBanco");
            }
            else {
                genInforme(formato,
                                "001248rptSaldosCreditoConsolidadoPorBanco");

            }
        }
        else {
            genInforme(formato, "001251RptSaldosCreditoFechas");
        }
    }

    /**
     * 
     * Metodo ejecutado al oprimir el boton Pdf en la vista
     *
     */
    public void oprimirPdf() {
        // <CODIGO_DESARROLLADO>

        archivoDescarga = null;
        if ("0".equals(comprobarBanco()) || "".equals(comprobarBanco())) {
            mensajeBanco();
        }
        else if ("1".equals(comprobarBanco())) {
            decision(FORMATOS.PDF);
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
        if ("0".equals(comprobarBanco()) || "".equals(comprobarBanco())) {
            mensajeBanco();
        }
        else if ("1".equals(comprobarBanco())) {
            decision(FORMATOS.EXCEL);
        }
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Genera el reporte de Excel y de Pdf
     */
    public void genInforme(ReportesBean.FORMATOS formato, String reporte) {
        try {

            String nombreCiclo;
            String condicionCiclo;
            String bancoInicialFinal;
            String bancoFinalFinal;
            if ("T".equals(ciclo)) {
                nombreCiclo = "Todos";
                condicionCiclo = "";
            }
            else {
                nombreCiclo = ciclo;
                condicionCiclo = "AND SP_USUARIO.CICLO=" + ciclo;
            }
            if ("SI".equals(parametroMostrarFiltros())) {
                bancoInicialFinal = bancoInicial;
                bancoFinalFinal = bancoFinal;
            }
            else {
                bancoInicialFinal = "0";
                bancoFinalFinal = "99999999999";
            }

            HashMap<String, Object> reemplazar = new HashMap<>();

            reemplazar.put("compania", compania);
            reemplazar.put("concicionCiclo", condicionCiclo);
            reemplazar.put("bancoInicial", bancoInicialFinal);
            reemplazar.put("bancoFinal", bancoFinalFinal);
            reemplazar.put("fechaInicial", SysmanFunciones
                            .convertirAFechaCadena(fechaInicial));
            reemplazar.put("fechaFinal",
                            SysmanFunciones.convertirAFechaCadena(fechaFinal));
            Map<String, Object> parametros = new HashMap<>();
            parametros.put("PR_FORMS_RPTSALDOSFECHAS_CICLO", nombreCiclo);
            parametros.put("PR_FORMS_RPTSALDOSFECHAS_FECHAINICIAL",
                            SysmanFunciones.convertirAFechaCadena(
                                            fechaInicial));
            parametros.put("PR_FORMS_RPTSALDOSFECHAS_FECHAFINAL",
                            SysmanFunciones.convertirAFechaCadena(fechaFinal));

            Reporteador.resuelveConsulta(reporte,
                            Integer.parseInt(SessionUtil.getModulo()),
                            reemplazar, parametros);
            archivoDescarga = JsfUtil.exportarStreamed(reporte, parametros,
                            ConectorPool.ESQUEMA_SYSMAN, formato);
        }

        catch (FileNotFoundException ex) {
            JsfUtil.agregarMensajeInformativo(ex.getMessage());
            Logger.getLogger(RptsaldosfechasControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
        }
        catch (JRException | IOException | ParseException
                        | SysmanException ex) {
            JsfUtil.agregarMensajeError(ex.getMessage());
            Logger.getLogger(RptsaldosfechasControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
        }

    }

    // </METODOS_BOTONES>
    // <METODOS_CAMBIAR>
    /**
     * Metodo ejecutado al cambiar el control fechainicial
     * 
     */
    public void cambiarfechainicial() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    // </METODOS_CAMBIAR>
    // <METODOS_COMBOS_GRANDES>
    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaBancoInicial
     * 
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaBancoInicial(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        bancoInicial = registroAux.getCampos()
                        .get(GeneralParameterEnum.CODIGO.getName()).toString();
        cargarListaBancoFinal();

    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaBancoFinal
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaBancoFinal(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        bancoFinal = registroAux.getCampos()
                        .get(GeneralParameterEnum.CODIGO.getName()).toString();
    }

    // </METODOS_COMBOS_GRANDES>
    // <METODOS_ARBOL>
    // </METODOS_ARBOL>
    // <SET_GET_ATRIBUTOS>
    /**
     * Retorna la variable checkConsolidado
     * 
     * @return checkConsolidado
     */
    public boolean getCheckConsolidado() {
        return checkConsolidado;
    }

    /**
     * Asigna la variable checkConsolidado
     * 
     * @param checkConsolidado
     * Variable a asignar en checkConsolidado
     */
    public void setCheckConsolidado(boolean checkConsolidado) {
        this.checkConsolidado = checkConsolidado;
    }

    /*
     * Asigna el estado de la variable informeSaldosCreditoVisible
     * 
     * @return informeSaldosCreditoVisible
     */
    public boolean isInformeSaldosCreditoVisible() {
        return informeSaldosCreditoVisible;
    }

    /*
     * Asigna el estado de la variable informeSaldosCreditoVisible
     */
    public void setInformeSaldosCreditoVisible(
        boolean informeSaldosCreditoVisible) {
        this.informeSaldosCreditoVisible = informeSaldosCreditoVisible;
    }

    /*
     * Asigna el estado de la variable mostrarFiltrosVisible *
     * 
     * @return mostrarFiltrosVisible
     */
    public boolean isMostrarFiltrosVisible() {
        return mostrarFiltrosVisible;
    }

    /*
     * Asigna el estado de la variable mostrarFiltrosVisible
     */
    public void setMostrarFiltrosVisible(boolean mostrarFiltrosVisible) {
        this.mostrarFiltrosVisible = mostrarFiltrosVisible;
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
     * Retorna la variable bancoInicial
     * 
     * @return bancoInicial
     */
    public String getBancoInicial() {
        return bancoInicial;
    }

    /**
     * Asigna la variable bancoInicial
     * 
     * @param bancoInicial
     * Variable a asignar en bancoInicial
     */
    public void setBancoInicial(String bancoInicial) {
        this.bancoInicial = bancoInicial;
    }

    /**
     * Retorna la variable bancoFinal
     * 
     * @return bancoFinal
     */
    public String getBancoFinal() {
        return bancoFinal;
    }

    /**
     * Asigna la variable bancoFinal
     * 
     * @param bancoFinal
     * Variable a asignar en bancoFinal
     */
    public void setBancoFinal(String bancoFinal) {
        this.bancoFinal = bancoFinal;
    }

    /**
     * Retorna la variable fechainicial
     * 
     * @return fechainicial
     */
    public Date getFechaInicial() {
        return fechaInicial;
    }

    /**
     * Asigna la variable fechainicial
     * 
     * @param fechainicial
     * Variable a asignar en fechainicial
     */
    public void setFechaInicial(Date fechaInicial) {
        this.fechaInicial = fechaInicial;
    }

    /**
     * Retorna la variable fechafinal
     * 
     * @return fechafinal
     */
    public Date getFechaFinal() {
        return fechaFinal;
    }

    /**
     * Asigna la variable fechafinal
     * 
     * @param fechafinal
     * Variable a asignar en fechafinal
     */
    public void setFechaFinal(Date fechaFinal) {
        this.fechaFinal = fechaFinal;
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
    /**
     * Retorna la lista listaBancoInicial
     * 
     * @return listaBancoInicial
     */
    public RegistroDataModelImpl getListaBancoInicial() {
        return listaBancoInicial;
    }

    /**
     * Asigna la lista listaBancoInicial
     * 
     * @param listaBancoInicial
     * Variable a asignar en listaBancoInicial
     */
    public void setListaBancoInicial(RegistroDataModelImpl listaBancoInicial) {
        this.listaBancoInicial = listaBancoInicial;
    }

    /**
     * Retorna la lista listaBancoFinal
     * 
     * @return listaBancoFinal
     */
    public RegistroDataModelImpl getListaBancoFinal() {
        return listaBancoFinal;
    }

    /**
     * Asigna la lista listaBancoFinal
     * 
     * @param listaBancoFinal
     * Variable a asignar en listaBancoFinal
     */
    public void setListaBancoFinal(RegistroDataModelImpl listaBancoFinal) {
        this.listaBancoFinal = listaBancoFinal;
    }
    // </SET_GET_LISTAS_COMBO_GRANDE>
}
