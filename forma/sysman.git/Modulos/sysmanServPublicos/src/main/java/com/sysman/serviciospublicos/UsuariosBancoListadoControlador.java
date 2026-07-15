/*-
 * UsuariosBancoListadoControlador.java
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
import com.sysman.jsfutil.ReportesBean.FORMATOS;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.persistencia.ConectorPool;
import com.sysman.recursos.ejb.EjbSysmanUtilRemote;
import com.sysman.reportes.Reporteador;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.serviciospublicos.enums.UsuariosBancoListadoControladorUrlEnum;
import com.sysman.util.SysmanFunciones;

import java.io.IOException;
import java.text.ParseException;
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

import net.sf.jasperreports.engine.JRException;

/**
 * Clase que permite generar el reporte USUARIOS QUE PAGARON EN UN
 * BANCO
 *
 * @version 1.0, 02/11/2016
 * @author jrodriguezr
 * 
 * @version 2, 20/06/2017
 * @author jreina se realizaron los cambios de refactoring en cada uno de los combos.
 */

@ManagedBean
@ViewScoped
public class UsuariosBancoListadoControlador extends BeanBaseModal {

    /**
     * Constante a nivel de clase que almacena el codigo de la
     * compania en la cual inicio sesion el usuario, el valor de esta
     * constante es asignado en el constructor a la variable de sesion
     * correspondiente
     */
    private final String compania;
    
    private final String modulo;
    // <DECLARAR_ATRIBUTOS>
    /**
     * Valor del atributo correspondiente a ciclo.
     */
    private String ciclo;
    /**
     * Valor del atributo correspondiente a banco.
     */
    private String banco;
    /**
     * Valor del atributo correspondiente a numeroPaquete.
     */
    private String numeroPaquete;
    /**
     * Valor del atributo correspondiente a fechaInicial.
     */
    private Date fechaInicial;
    /**
     * Valor del atributo correspondiente a nombreBanco.
     */
    private String nombreBanco;

    private final String consTodos;

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
     * Lista de objetos pertenecientes al combo paquete
     */
    private List<Registro> listaPaquete;
    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>
    /**
     * Lista de objetos pertenecientes al combo ciclo.
     */
    private RegistroDataModelImpl listaCiclo;
    /**
     * Lista de objetos pertenecientes al combo banco.
     */
    private RegistroDataModelImpl listaBanco;

    // </DECLARAR_LISTAS_COMBO_GRANDE>
    
    
    @EJB
    private EjbSysmanUtilRemote ejbSysmanUtilRemote;
    /**
     * Crea una nueva instancia de UsuariosBancoListadoControlador
     */
    public UsuariosBancoListadoControlador() {
        super();
        compania = SessionUtil.getCompania();
        modulo=SessionUtil.getModulo();
        consTodos = "TODOS";
        try {
            numFormulario = GeneralCodigoFormaEnum.USUARIOS_BANCO_LISTADO_CONTROLADOR
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
        cargarListaPaquete();
        // </CARGAR_LISTA>
        // <CARGAR_LISTA_COMBO_GRANDE>
        fechaInicial = new Date();
        cargarListaCiclo();
        cargarListaBanco();
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
     * Carga la lista listaPaquete
     */
    public void cargarListaPaquete() {
        try {
        Map<String,Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(),compania);
        param.put(GeneralParameterEnum.BANCO.getName(),banco);
        param.put(GeneralParameterEnum.FECHA.getName(),SysmanFunciones.convertirAFechaCadena(fechaInicial));

            listaPaquete = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            UsuariosBancoListadoControladorUrlEnum.URL5182
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException | ParseException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    /**
     *
     * Carga la lista listaCiclo
     */
    public void cargarListaCiclo() {
        UrlBean urlBean = UrlServiceUtil.getInstance().getUrlServiceByUrlByEnumID(UsuariosBancoListadoControladorUrlEnum.URL5802.getValue());   
        Map<String,Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(),compania);

        listaCiclo = new RegistroDataModelImpl(urlBean.getUrl(),urlBean.getUrlConteo().getUrl(),param,
                        true, "NUMERO");
    }

    /**
     *
     * Carga la lista listaBanco
     *
     */
    public void cargarListaBanco() {
        try {
            UrlBean urlBean = UrlServiceUtil.getInstance().getUrlServiceByUrlByEnumID(UsuariosBancoListadoControladorUrlEnum.URL6944.getValue());   
            Map<String,Object> param = new TreeMap<>();
            param.put(GeneralParameterEnum.COMPANIA.getName(),compania);

            param.put(GeneralParameterEnum.FECHA.getName(),SysmanFunciones.convertirAFechaCadena(fechaInicial));
            listaBanco = new RegistroDataModelImpl(urlBean.getUrl(),urlBean.getUrlConteo().getUrl(),param,
                            true, "BANCO");
        }
        catch (ParseException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
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
        archivoDescarga = null;
        generaInforme(FORMATOS.PDF);
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
        generaInforme(FORMATOS.EXCEL97);
        // </CODIGO_DESARROLLADO>
    }

    private String nombrePeriodo() {
        Registro regPeriodo = null;
        try {
            Map<String, Object> param = new TreeMap<>();
            param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
            param.put(GeneralParameterEnum.CICLO.getName(), ciclo);

            regPeriodo = RegistroConverter.toRegistro(
                            requestManager.get(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            UsuariosBancoListadoControladorUrlEnum.URL5832
                                                                            .getValue())
                                            .getUrl(), param));

        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

        return (regPeriodo == null) ? ""
            : regPeriodo.getCampos().get("NOMBRE").toString();

    }

    /**
     * Metodo que permite generar el reporte con los filtros y
     * formatos seleccionados.
     *
     * @param formato
     * Formato en el cual se genera el reporte.
     */
    private void generaInforme(FORMATOS formato) {
        try {
            HashMap<String, Object> reemplazar = new HashMap<>();
            Map<String, Object> parametros = new HashMap<>();

            String manejaInformeRecaudoExterno = ejbSysmanUtilRemote
                            .consultarParametro(compania,
                                            "MANEJA INFORME CON RECAUDOS EXTERNOS",
                                            modulo, new Date(), true);
            String informeRecaudoOrdenadoPorFechayHora = ejbSysmanUtilRemote
                            .consultarParametro(compania,
                                            "INFORMES DE RECAUDO ORDENADOS POR FECHA Y HORA",
                                            modulo, new Date(), true);
                 
            reemplazar.put("banco", banco);
            reemplazar.put("paquete", numeroPaquete);
            reemplazar.put("fecha", SysmanFunciones
                            .convertirAFechaCadena(fechaInicial));
            reemplazar.put("condConsulta",
                            "888".equals(numeroPaquete) ? "1" : "0");
            String reporte;
            if ("SI".equals(manejaInformeRecaudoExterno)) {
                reporte = "001349UsuariosPagaronBanco2";
            }
            else if ("SI".equals(informeRecaudoOrdenadoPorFechayHora)) {
                reporte = "001350UsuariosPagaronBancoFECHA";
            }
            else {
                reporte = "001220UsuariosPagaronBanco";
            }
            reemplazar.put("condCiclo", consTodos.equals(ciclo) ? ""
                : " AND PAGO.CICLO = '" + ciclo + "'");

            Reporteador.resuelveConsulta(reporte,
                            Integer.parseInt(SessionUtil.getModulo()),
                            reemplazar, parametros);

            parametros.put("PR_CICLO",
                            consTodos.equals(ciclo) ? consTodos : ciclo);
            parametros.put("PR_FECHAINICIAL", SysmanFunciones
                            .convertirAFechaCadena(fechaInicial));
            parametros.put("PR_PAQUETE", numeroPaquete);
            parametros.put("PR_NOMBREBANCO", nombreBanco);
            parametros.put("PR_VIGILADOPOR", SessionUtil.getCompaniaIngreso()
                            .getRutaVigiladoPor());
            parametros.put("PR_NOMBREPERIODO", nombrePeriodo());

            archivoDescarga = JsfUtil.exportarStreamed(reporte, parametros,
                            ConectorPool.ESQUEMA_SYSMAN, formato);
        }
        catch (JRException | IOException | SysmanException |ParseException | SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    // </METODOS_BOTONES>
    // <METODOS_CAMBIAR>
    /**
     * Metodo ejecutado al cambiar el control FechaInicial
     *
     */
    public void cambiarFechaInicial() {
        // <CODIGO_DESARROLLADO>
        nombreBanco = banco = numeroPaquete = null;
        cargarListaBanco();
        cargarListaPaquete();
        // </CODIGO_DESARROLLADO>
    }

    // </METODOS_CAMBIAR>
    // <METODOS_COMBOS_GRANDES>
    /**
     *
     * Metodo ejecutado al seleccionar una fila de la lista listaCiclo
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaCiclo(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        ciclo = registroAux.getCampos().get("NUMERO").toString();
        banco = nombreBanco = numeroPaquete = null;
    }

    /**
     *
     * Metodo ejecutado al seleccionar una fila de la lista listaBanco
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaBanco(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        banco = registroAux.getCampos().get("BANCO").toString();
        nombreBanco = registroAux.getCampos().get("NOMBRE").toString();
        numeroPaquete = null;
        cargarListaPaquete();
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
     * Retorna la variable banco
     *
     * @return banco
     */
    public String getBanco() {
        return banco;
    }

    /**
     * Asigna la variable banco
     *
     * @param banco
     * Variable a asignar en banco
     */
    public void setBanco(String banco) {
        this.banco = banco;
    }

    /**
     * Retorna la variable numeroPaquete
     *
     * @return numeroPaquete
     */
    public String getNumeroPaquete() {
        return numeroPaquete;
    }

    /**
     * Asigna la variable numeroPaquete
     *
     * @param numeroPaquete
     * Variable a asignar en numeroPaquete
     */
    public void setNumeroPaquete(String numeroPaquete) {
        this.numeroPaquete = numeroPaquete;
    }

    /**
     * Retorna la variable fechaInicial
     *
     * @return fechaInicial
     */
    public Date getFechaInicial() {
        return fechaInicial;
    }

    /**
     * Asigna la variable fechaInicial
     *
     * @param fechaInicial
     * Variable a asignar en fechaInicial
     */
    public void setFechaInicial(Date fechaInicial) {
        this.fechaInicial = fechaInicial;
    }

    /**
     * Retorna la variable nombreBanco
     *
     * @return nombreBanco
     */
    public String getNombreBanco() {
        return nombreBanco;
    }

    /**
     * Asigna la variable nombreBanco
     *
     * @param nombreBanco
     * Variable a asignar en nombreBanco
     */
    public void setNombreBanco(String nombreBanco) {
        this.nombreBanco = nombreBanco;
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
     * Retorna la lista listaPaquete
     *
     * @return listaPaquete
     */
    public List<Registro> getListaPaquete() {
        return listaPaquete;
    }

    /**
     * Asigna la lista listaPaquete
     *
     * @param listaPaquete
     * Variable a asignar en listaPaquete
     */
    public void setListaPaquete(List<Registro> listaPaquete) {
        this.listaPaquete = listaPaquete;
    }

    // </SET_GET_LISTAS>
    // <SET_GET_LISTAS_COMBO_GRANDE>
   

   

    public RegistroDataModelImpl getListaCiclo() {
        return listaCiclo;
    }

    public void setListaCiclo(RegistroDataModelImpl listaCiclo) {
        this.listaCiclo = listaCiclo;
    }

    public RegistroDataModelImpl getListaBanco() {
        return listaBanco;
    }

    public void setListaBanco(RegistroDataModelImpl listaBanco) {
        this.listaBanco = listaBanco;
    }
    
    
    // </SET_GET_LISTAS_COMBO_GRANDE>
}
