/*-
 * RecaudosControlador.java
 *
 * 1.0
 *
 * 04/11/2016
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
import com.sysman.serviciospublicos.enums.RecaudosControladorEnum;
import com.sysman.serviciospublicos.enums.RecaudosControladorUrlEnum;
import com.sysman.util.SysmanFunciones;

import java.io.FileNotFoundException;
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
 * Esta clase es el controlador para los formularios en Access
 * "frmRecaudosUsuarios", "frmRecaudos_tasasAmb"
 *
 * @version 1.0, 04/11/2016
 * @author ybecerra
 * 
 * @version 2, 14/06/2017
 * @author jreina se realizaron los cambios de refactoring
 *  en cada uno de los combos.
 */

@ManagedBean
@ViewScoped
public class RecaudosControlador extends BeanBaseModal
{
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
    /**
     * Constante definida que almacena la cadena "CODIGO", se llama en
     * los metodos cargarListaBancoInicial, cargarListaBancoFinal,
     * seleccionarFilaBancoInicial, seleccionarFilaBancoFinal
     */
    private final String cod;
    
    private final String consRelacion;
    private final String consPagos;
    // <DECLARAR_ATRIBUTOS>
    /**
     * Atributo que almacena el un valor true o false si la casilla de
     * verificacion esta seleccionada o no
     */
    
    
    private boolean reporte;
    /**
     * Atributo que almacena el codigo seleccionado en el combo ciclo
     * del formulario
     */
    private String ciclo;
    /**
     * Atributo que almacena el codigo seleccionado en el combo banco
     * inicial del formulario
     */
    private String bancoInicial;
    /**
     * Atributo que almacena el codigo seleccionado en el combo banco
     * final del formulario
     */
    private String bancoFinal;
    /**
     * Atributo que almacena la fecha inicial seleccionada en el
     * formulario
     */
    private Date fechaInicial;
    /**
     * Atributo que almacena la fecha final seleccionada en el
     * formulario.
     */
    private Date fechaFinal;
    /**
     * Atributo que almacena el nombre del titulo del
     * formulario,cambia dependiendo la opcion de menu
     */
    private String titulo;

    /**
     * Atributo que almacena el nombre del encabezado del
     * formulario,cambia dependiendo de la opcion de menu
     */
    private String mostrarTitulo;

    /**
     * Atributo de la clase que valida si la casilla de verificacion
     * Energia y Acumulado del formulario se hace visible o no
     */
    private boolean checkVisible = false;
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
     * Atributo de la clase que almacena la lista de los ciclos del
     * formulario
     */
    private List<Registro> listaCiclo;
    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>
    /**
     * Atributo de la clase que almacena los registros del combo banco
     * inicial del formulario
     */
    private RegistroDataModelImpl listaBancoInicial;
    /**
     * Atributo de la clase que almacena los registros del combo banco
     * final del formulario
     */
    private RegistroDataModelImpl listaBancoFinal;

    // </DECLARAR_LISTAS_COMBO_GRANDE>
    
    
    @EJB
    private EjbSysmanUtilRemote ejbSysmanUtilRemote;
    /**
     * Crea una nueva instancia de RecaudosControlador
     */
    public RecaudosControlador()
    {
        super();
        compania = SessionUtil.getCompania();
        modulo = SessionUtil.getModulo();
        cod = "CODIGO";
        consRelacion="relacion";
        consPagos="pagos";

        try
        {
            numFormulario = GeneralCodigoFormaEnum.RECAUDOS_CONTROLADOR
                            .getCodigo();
            validarPermisos();
            // <INI_ADICIONAL>
            // </INI_ADICIONAL>
        }
        catch (Exception ex)
        {
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
    public void inicializar()
    {
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

    /**
     * Este metodo es invocado el metodo inicializar, se ejecutan las
     * acciones a tener en cuenta en el momento de apertura del
     * formulario
     */
    @Override
    public void abrirFormulario() {
        // <CODIGO_DESARROLLADO>
        titulo = idioma.getString("TB_TB1795");
        fechaInicial = new Date();
        fechaFinal = new Date();
        if ("74070411".equals(SessionUtil.getMenuActual())) {
            if ("002".equals(compania)) {
                checkVisible = true;
            }
            mostrarTitulo = idioma.getString("TB_TB1796");
        }
        else {
            mostrarTitulo = idioma.getString("TB_TB1799");
        }
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
            param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
            
            listaCiclo = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            RecaudosControladorUrlEnum.URL8130
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
     * Carga la lista listaBancoInicial
     *
     */
    public void cargarListaBancoInicial()
    {   
        UrlBean urlBean = UrlServiceUtil.getInstance().getUrlServiceByUrlByEnumID(RecaudosControladorUrlEnum.URL8822.getValue());       
        Map<String,Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(),compania);

        listaBancoInicial = new RegistroDataModelImpl(urlBean.getUrl(),urlBean.getUrlConteo().getUrl(),param,
                        true, cod);
        
        
    }

    /**
     *
     * Carga la lista listaBancoFinal
     *
     */
    public void cargarListaBancoFinal()
    {
        UrlBean urlBean = UrlServiceUtil.getInstance().getUrlServiceByUrlByEnumID(RecaudosControladorUrlEnum.URL9452.getValue());       
        Map<String,Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(),compania);
        param.put(RecaudosControladorEnum.PARAM0.getValue(),bancoInicial);

        listaBancoFinal = new RegistroDataModelImpl(urlBean.getUrl(),urlBean.getUrlConteo().getUrl(),param,
                        true, cod);
    }

    // </METODOS_CARGAR_LISTA>
    // <METODOS_BOTONES>
    /**
     *
     * Metodo ejecutado al oprimir el boton Presentar en la vista
     *
     *
     */
    public void oprimirPresentar()
    {
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
    public void oprimirExcel()
    {
        // <CODIGO_DESARROLLADO>
        archivoDescarga = null;
        generarInforme(FORMATOS.EXCEL);
        // </CODIGO_DESARROLLADO>
    }

    // </METODOS_BOTONES>

    public void generarInforme(ReportesBean.FORMATOS formato) {
        String parReporte = "";
        String reporteConsulta = "";
        try {
            HashMap<String, Object> reemplazar = new HashMap<>();
            if ("74070411".equals(SessionUtil.getMenuActual())) {
                if (!reporte) {
                    reporteConsulta = "001217LRecaudosUsuarioFECHA";
                    String param;
                    param = ejbSysmanUtilRemote.consultarParametro(compania,
                                    "INFORMES DE RECAUDO ORDENADOS POR FECHA Y HORA",
                                    modulo, new Date(), true);
                    if (param == null) {
                        JsfUtil.agregarMensajeError(
                                        idioma.getString("TB_TB1797"));
                        return;
                    }
                    else if ("SI".equals(param)) {
                        parReporte = "001217LRecaudosUsuarioFECHA";
                        reemplazar.put("pago", " SP_PAGO.HORA,");
                        reemplazar.put(consRelacion, "LEFT JOIN SP_PAGO \n"
                            + " ON  SP_USUARIO.COMPANIA   = SP_PAGO.COMPANIA\n"
                            + " AND SP_USUARIO.CICLO      = SP_PAGO.CICLO \n"
                            + " AND SP_USUARIO.CODIGORUTA = SP_PAGO.CODIGORUTA \n"
                            + " AND SP_USUARIO.ANO        = SP_PAGO.ANO\n"
                            + " AND SP_USUARIO.PERIODO    = SP_PAGO.PERIODO\n"
                            + " AND SP_USUARIO.FECHAPAGOPERPROCESO = SP_PAGO.FECHA\n"
                            + " AND SP_USUARIO.BANCOPERPROCESO     = SP_PAGO.BANCO\n"
                            + " AND SP_USUARIO.PAQUETEPAGOPERPROCESO  = SP_PAGO.NUMEROPAQUETE");
                        reemplazar.put(consPagos, "ORDER BY SP_PAGO.HORA");
                    }
                    else {
                        parReporte = "001222LRecaudosUsuario";
                        reemplazar.put("pago", "");
                        reemplazar.put(consRelacion, "");
                        reemplazar.put(consPagos, "");
                    }

                }
                else {
                    parReporte = "001229LRecaudosUsuarioEneAlu";
                    reemplazar.put("pago", "");
                    reemplazar.put(consRelacion, "");
                    reemplazar.put(consPagos, "");
                }
            }
            else {
                parReporte = "001230LRecaudossobretasaAmb";
                reporteConsulta = parReporte;
            }

            if ("T".equals(ciclo)) {
                reemplazar.put("condicionCiclo", "");
            }
            else {
                reemplazar.put("condicionCiclo",
                                " AND SP_USUARIO.CICLO = '" + ciclo + "'");
            }

            reemplazar.put("fechaInicial",
                            SysmanFunciones.formatearFecha(fechaInicial));
            reemplazar.put("fechaFinal",
                            SysmanFunciones.formatearFecha(fechaFinal));
            reemplazar.put("bancoInicial", "'" + bancoInicial + "'");
            reemplazar.put("bancoFinal", "'" + bancoFinal + "'");

            Map<String, Object> parametros = new HashMap<>();
            parametros.put("PR_CICLO", ciclo);
            parametros.put("PR_FECHAINICIAL", SysmanFunciones
                            .convertirAFechaCadena(fechaInicial));
            parametros.put("PR_FECHAFINAL",
                            SysmanFunciones.convertirAFechaCadena(fechaFinal));
            parametros.put("PR_BANCOINICIAL", bancoInicial);
            parametros.put("PR_BANCOFINAL", bancoFinal);

            Reporteador.resuelveConsulta(reporteConsulta,
                            Integer.parseInt(modulo), reemplazar, parametros);
            archivoDescarga = JsfUtil.exportarStreamed(parReporte, parametros,
                            ConectorPool.ESQUEMA_SYSMAN, formato);
        }
        catch (FileNotFoundException ex) {
            String msj = idioma.getString("MSM_INFORME_VAR_NO_EXISTE");
            msj = msj.replace("s$reporte$s", parReporte);
            JsfUtil.agregarMensajeError(
                            idioma.getString("MSM_TRANS_INTERRUMPIDA") + msj);
            logger.error(ex);
        }
        catch (JRException | IOException | ParseException | SysmanException
                        | SystemException ex) {
            JsfUtil.agregarMensajeError(ex.getMessage());
            logger.error(ex);

        }

    }

    // <METODOS_CAMBIAR>
    // </METODOS_CAMBIAR>
    // <METODOS_COMBOS_GRANDES>
    /**
     *
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaBancoInicial
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaBancoInicial(SelectEvent event)
    {
        Registro registroAux = (Registro) event.getObject();
        bancoInicial = registroAux.getCampos().get(cod).toString();
        bancoFinal = null;
        cargarListaBancoFinal();
    }

    /**
     *
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaBancoFinal
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaBancoFinal(SelectEvent event)
    {
        Registro registroAux = (Registro) event.getObject();
        bancoFinal = registroAux.getCampos().get(cod).toString();
    }

    // </METODOS_COMBOS_GRANDES>
    // <METODOS_ARBOL>
    // </METODOS_ARBOL>
    // <SET_GET_ATRIBUTOS>
    /**
     * Retorna la variable reporte
     *
     * @return reporte
     */
    public boolean getReporte()
    {
        return reporte;
    }

    /**
     * Asigna la variable reporte
     *
     * @param reporte
     * Variable a asignar en reporte
     */
    public void setReporte(boolean reporte)
    {
        this.reporte = reporte;
    }

    /**
     * Retorna la variable ciclo
     *
     * @return ciclo
     */
    public String getCiclo()
    {
        return ciclo;
    }

    /**
     * Asigna la variable ciclo
     *
     * @param ciclo
     * Variable a asignar en ciclo
     */
    public void setCiclo(String ciclo)
    {
        this.ciclo = ciclo;
    }

    /**
     * Retorna la variable bancoInicial
     *
     * @return bancoInicial
     */
    public String getBancoInicial()
    {
        return bancoInicial;
    }

    /**
     * Asigna la variable bancoInicial
     *
     * @param bancoInicial
     * Variable a asignar en bancoInicial
     */
    public void setBancoInicial(String bancoInicial)
    {
        this.bancoInicial = bancoInicial;
    }

    /**
     * Retorna la variable bancoFinal
     *
     * @return bancoFinal
     */
    public String getBancoFinal()
    {
        return bancoFinal;
    }

    /**
     * Asigna la variable bancoFinal
     *
     * @param bancoFinal
     * Variable a asignar en bancoFinal
     */
    public void setBancoFinal(String bancoFinal)
    {
        this.bancoFinal = bancoFinal;
    }

    /**
     * Retorna la variable fechaInicial
     *
     * @return fechaInicial
     */
    public Date getFechaInicial()
    {
        return fechaInicial;
    }

    /**
     * Asigna la variable fechaInicial
     *
     * @param fechaInicial
     * Variable a asignar en fechaInicial
     */
    public void setFechaInicial(Date fechaInicial)
    {
        this.fechaInicial = fechaInicial;
    }

    /**
     * Retorna la variable fechaFinal
     *
     * @return fechaFinal
     */
    public Date getFechaFinal()
    {
        return fechaFinal;
    }

    /**
     * Asigna la variable fechaFinal
     *
     * @param fechaFinal
     * Variable a asignar en fechaFinal
     */
    public void setFechaFinal(Date fechaFinal)
    {
        this.fechaFinal = fechaFinal;
    }

    /**
     * Retorna la variable titulo
     *
     * @return titulo
     */
    public String getTitulo()
    {
        return titulo;
    }

    /**
     * Asigna la variable setTitulo
     *
     * @param titulo
     * Variable a asignar en titulo
     */
    public void setTitulo(String titulo)
    {
        this.titulo = titulo;
    }

    /**
     * Retorna la variable mostrarTitulo
     *
     * @return mostrarTitulo
     */
    public String getMostrarTitulo()
    {
        return mostrarTitulo;
    }

    /**
     * Asigna la variable setMostrarTitulo
     *
     * @param mostrarTitulo
     * Variable a asignar en mostrarTitulo
     */
    public void setMostrarTitulo(String mostrarTitulo)
    {
        this.mostrarTitulo = mostrarTitulo;
    }

    public boolean isCheckVisible()
    {
        return checkVisible;
    }

    public void setCheckVisible(boolean checkVisible)
    {
        this.checkVisible = checkVisible;
    }

    /**
     * Atributo usado para descargar contenidos de archivos desde la
     * vista
     */
    public StreamedContent getArchivoDescarga()
    {
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
    public List<Registro> getListaCiclo()
    {
        return listaCiclo;
    }

    /**
     * Asigna la lista listaCiclo
     *
     * @param listaCiclo
     * Variable a asignar en listaCiclo
     */
    public void setListaCiclo(List<Registro> listaCiclo)
    {
        this.listaCiclo = listaCiclo;
    }

    // </SET_GET_LISTAS>
    // <SET_GET_LISTAS_COMBO_GRANDE>
    
    

    public RegistroDataModelImpl getListaBancoInicial() {
        return listaBancoInicial;
    }

    public void setListaBancoInicial(RegistroDataModelImpl listaBancoInicial) {
        this.listaBancoInicial = listaBancoInicial;
    }

    public RegistroDataModelImpl getListaBancoFinal() {
        return listaBancoFinal;
    }

    public void setListaBancoFinal(RegistroDataModelImpl listaBancoFinal) {
        this.listaBancoFinal = listaBancoFinal;
    }
    
    
    // </SET_GET_LISTAS_COMBO_GRANDE>
}
