/*-
 * InformeUsuariosMedidores.java
 *
 * 1.0
 *
 * 28/10/2016
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
import com.sysman.serviciospublicos.enums.InformeUsuariosMedidoresEnum;
import com.sysman.serviciospublicos.enums.InformeUsuariosMedidoresUrlEnum;
import com.sysman.util.SysmanFunciones;

import java.io.IOException;
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
 * Clase encargada de generar informes teniendo encuenta los
 * historicos de facturacion
 *
 * @version 1.0, 28/10/2016
 * @author jguerrero
 *
 * @version 2, 05/06/2017
 * @author jreina se realizaron los cambios de refactoring en cada uno
 * de los combos.
 *
 * @version 3, 12/06/2017 jrodriguezr Se refactoriza el cï¿½digo: Se
 * pasa el numero del formulario al enumerado, se eliminan conexiones
 * y se ajustan metodos de generacion de reportes.
 */

@ManagedBean
@ViewScoped
public class InformeUsuariosMedidores extends BeanBaseModal {
    /**
     * Constante a nivel de clase que almacena el codigo de la
     * compania en la cual inicio sesion el usuario, el valor de esta
     * constante es asignado en el constructor a la variable de sesion
     * correspondiente
     */
    private final String compania;
    /**
     * Constante encargada de almancera la cadena de caracteres
     * "VARIABLECICLO"
     */
    private final String variableCicloCons;

    // <DECLARAR_ATRIBUTOS>
    /**
     * Variable que se encarga de almanecar temporalmente lo
     * seleccionado por el radio booton del formulario
     */

    private String tipoInforme;
    /**
     * Variable que se encarga de almanecar temporalmente el resultado
     * de la subconsulta de los informes
     */

    private String consultaPrincipal;
    /**
     * Variable que se encarga de almanecar temporalmente el titulo de
     * los informes
     */
    private String strTitulo;

    /*
     ** Variable que se encarga de almanecar temporalmente el ciclo
     * seleccionado desde el formulario.
     */
    private String ciclo;
    /**
     * Variable que se encarga de almanecar temporalmente el
     * periodoInicial seleccionado desde el formulario.
     */
    private String periodoInicial;
    /**
     * Variable que se encarga de almanecar temporalmente el
     * periodoFinal seleccionado desde el formulario.
     */
    private String periodoFinal;
    /**
     * Variable que se encarga de almanecar temporalmente el el nombre
     * del reporte a generar
     */
    private String reporte;
    /**
     * Variable que se encarga de mostrar o no los campos de
     * periodoInicia y peridoFinal en el formulario
     *
     */
    private boolean cargarPeriodos;
    /**
     * Variable que se encarga de de descargar los archivos de los
     * informes
     *
     */
    private StreamedContent archivoDescarga;

    // </DECLARAR_ATRIBUTOS>
    // <DECLARAR_PARAMETROS>
    // </DECLARAR_PARAMETROS>
    // <DECLARAR_LISTAS>
    /**
     * Lista que se encarga de almacenar la lista cargada por el combo
     * ciclo.
     */
    private List<Registro> listaCiclo;
    /**
     * Lista que se encarga de almacenar la lista cargada por el combo
     * periodoInicial.
     */
    private List<Registro> listacmbPeriodoInicial;
    /**
     * Lista que se encarga de almacenar la lista cargada por el combo
     * periodoFinal
     */
    private List<Registro> listacmbPeriodoFinal;

    @EJB
    private EjbSysmanUtilRemote ejbSysmanUtilRemote;

    @EJB
    private EjbServiciosPublicosCeroRemote ejbServiciosPublicosCeroRemote;

    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>
    // </DECLARAR_LISTAS_COMBO_GRANDE>
    /**
     * Crea una nueva instancia de InformeUsuariosMedidores
     */
    public InformeUsuariosMedidores() {
        super();
        compania = SessionUtil.getCompania();
        variableCicloCons = "variableCiclo";
        tipoInforme = "1";
        ciclo = "T";

        try {
            numFormulario = GeneralCodigoFormaEnum.INFORME_USUARIOS_MEDIDORES
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
        cargarListacmbPeriodoInicial();

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
        cargarPeriodos();
        // </CODIGO_DESARROLLADO>
    }

    // <METODOS_CARGAR_LISTA>
    /**
     *
     * Carga la lista listaCiclo
     *
     * Metodo encargado De acceder a la base de datos y cargar en
     * ListaCiclo los datos de la base de datos
     */
    public void cargarListaCiclo() {
        try {
            Map<String, Object> param = new TreeMap<>();
            param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
            listaCiclo = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            InformeUsuariosMedidoresUrlEnum.URL7140
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
     * Carga la lista listacmbPeriodoInicial * Metodo encargado De
     * acceder a la base de datos y cargar en listacmbPeriodoInicial
     * los datos de la base de datos
     */
    public void cargarListacmbPeriodoInicial() {
        try {
            Map<String, Object> param = new TreeMap<>();
            param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
            listacmbPeriodoInicial = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            InformeUsuariosMedidoresUrlEnum.URL7839
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
     * Carga la lista listacmbPeriodoFinal * Metodo encargado De
     * acceder a la base de datos y cargar en listacmbPeriodoFinal los
     * datos de la base de datos
     */
    public void cargarListacmbPeriodoFinal() {
        try {
            Map<String, Object> param = new TreeMap<>();
            param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
            param.put(InformeUsuariosMedidoresEnum.PARAM0.getValue(),
                            periodoInicial);
            listacmbPeriodoFinal = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            InformeUsuariosMedidoresUrlEnum.URL7840
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
     * Metodo ejecutado al oprimir el boton Impresora en la vista
     *
     * Metodo encargado del procedimiento de los metodos
     * reporteSeleccionado que se encarga de generar el nombre del
     * reporte segun las opciones del formulario, ademas ejecuta el
     * metodo preprararDatosusuarioMedidiores en el cual arma la
     * subconsulta de todos los infomres teniendo en cuenta el
     * parametro de historicos y por ultimo ejecuta el metodo
     * genInforme que es el encargado de crear todos los informes *
     */
    public void oprimirImpresora() {
        // <CODIGO_DESARROLLADO>
        archivoDescarga = null;
        reporteSeleccionado();
        prepararDatosUsuarioMedidores();
        genInforme(ReportesBean.FORMATOS.PDF);
        // </CODIGO_DESARROLLADO>
    }

    /**
     *
     * Metodo ejecutado al oprimir el boton Comando48 en la vista
     *
     *
     * Metodo encargado del procedimiento de los metodos
     * reporteSeleccionado que se encarga de generar el nombre del
     * reporte segun las opciones del formulario, ademas ejecuta el
     * metodo preprararDatosusuarioMedidiores en el cual arma la
     * subconsulta de todos los infomres teniendo en cuenta el
     * parametro de historicos y por ultimo ejecuta el metodo
     * genInforme que es el encargado de crear todos los informes
     *
     */
    public void oprimirComando48() {
        // <CODIGO_DESARROLLADO>
        archivoDescarga = null;
        reporteSeleccionado();
        prepararDatosUsuarioMedidores();
        genInforme(ReportesBean.FORMATOS.EXCEL);
        // </CODIGO_DESARROLLADO>
    }

    // </METODOS_BOTONES>
    // <METODOS_CAMBIAR>
    /**
     * Metodo ejecutado al cambiar el control cmbPeriodoInicial
     *
     * Metodo que se encarga de cargar la lista del peridoFinaal.
     *
     */
    public void cambiarcmbPeriodoInicial() {
        // <CODIGO_DESARROLLADO>
        periodoFinal = null;
        cargarListacmbPeriodoFinal();
        // </CODIGO_DESARROLLADO>
    }

    // </METODOS_CAMBIAR>
    // <METODOS_COMBOS_GRANDES>
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

    public String getTipoInforme() {
        return tipoInforme;
    }

    public boolean isCargarPeriodos() {
        return cargarPeriodos;
    }

    public void setCargarPeriodos(boolean cargarPeriodos) {
        this.cargarPeriodos = cargarPeriodos;
    }

    public void setTipoInforme(String tipoInforme) {
        this.tipoInforme = tipoInforme;
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

    public StreamedContent getArchivoDescarga() {
        return archivoDescarga;
    }

    public void setArchivoDescarga(StreamedContent archivoDescarga) {
        this.archivoDescarga = archivoDescarga;
    }

    /**
     * Retorna la lista listacmbPeriodoInicial
     *
     * @return listacmbPeriodoInicial
     */
    public List<Registro> getListacmbPeriodoInicial() {
        return listacmbPeriodoInicial;
    }

    /**
     * Asigna la lista listacmbPeriodoInicial
     *
     * @param listacmbPeriodoInicial
     * Variable a asignar en listacmbPeriodoInicial
     */
    public void setListacmbPeriodoInicial(
        List<Registro> listacmbPeriodoInicial) {
        this.listacmbPeriodoInicial = listacmbPeriodoInicial;
    }

    /**
     * Retorna la lista listacmbPeriodoFinal
     *
     * @return listacmbPeriodoFinal
     */
    public List<Registro> getListacmbPeriodoFinal() {
        return listacmbPeriodoFinal;
    }

    /**
     * Asigna la lista listacmbPeriodoFinal
     *
     * @param listacmbPeriodoFinal
     * Variable a asignar en listacmbPeriodoFinal
     */
    public void setListacmbPeriodoFinal(List<Registro> listacmbPeriodoFinal) {
        this.listacmbPeriodoFinal = listacmbPeriodoFinal;
    }
    // </SET_GET_LISTAS>
    // <SET_GET_LISTAS_COMBO_GRANDE>
    // </SET_GET_LISTAS_COMBO_GRANDE>

    private void prepararDatosUsuarioMedidores() {

        String consultaUsuario = "";
        String consultaHistoricos = "";

        HashMap<String, Object> reemplazarUsuario = new HashMap<>();
        HashMap<String, Object> reemplazarHistoricos = new HashMap<>();

        try {

            boolean conHistoria = "SI".equals(SysmanFunciones
                            .nvl(ejbSysmanUtilRemote.consultarParametro(
                                            compania,
                                            "GUARDA HISTORICOS DE FACTURA",
                                            SessionUtil.getModulo(), new Date(),
                                            false),
                                            "NO"));
            if ("3".equals(tipoInforme)) {

                reemplazarUsuario.put("complementoUsuario",
                                ", SP_USUARIO.CODIGORUTA, SP_USUARIO.CODIGOINTERNO");
                reemplazarHistoricos.put("complementoHistorico",
                                ", SP_HISTORICOFACTURA.CODIGORUTA, SP_HISTORICOFACTURA.CODIGOINTERNO");
            }
            else {
                reemplazarUsuario.put("complementoUsuario", "");
                reemplazarHistoricos.put("complementoHistorico", "");
            }

            if (!"T".equals(ciclo)) {
                strTitulo = "Ciclo: " + ciclo + "\n";
                reemplazarUsuario.put(variableCicloCons,
                                " AND SP_USUARIO.CICLO=" + ciclo);
                reemplazarHistoricos.put("variableCiclo",
                                " AND SP_HISTORICOFACTURA.CICLO=" + ciclo);

            }
            else {
                strTitulo = "Ciclo: Todos \n";
                reemplazarUsuario.put(variableCicloCons,
                                "");
                reemplazarHistoricos.put(variableCicloCons,
                                "");
            }

            reemplazarUsuario.put("variableHistoico", conHistoria
                ? "AND SP_USUARIO.ANO||SP_USUARIO.PERIODO BETWEEN '"
                    + periodoInicial + "' AND '" + periodoFinal + "' "
                    + ""
                : "");

            reemplazarHistoricos.put("variableHistoico", conHistoria
                ? "AND SP_HISTORICOFACTURA.ANO||SP_HISTORICOFACTURA.PERIODO BETWEEN '"
                    + periodoInicial + "' AND '" + periodoFinal + "' "
                    + ""
                : "");

            if (conHistoria) {
                String periodoA = periodoInicial.substring(4,
                                periodoInicial.length());
                String anoA = periodoInicial.substring(0, 4);
                String periodoB = periodoFinal.substring(4,
                                periodoFinal.length());
                String anoB = periodoFinal.substring(0, 4);
                strTitulo = strTitulo + " Entre el periodo "
                    + ejbServiciosPublicosCeroRemote.asignarNombrePeriodo(
                                    compania, Integer.parseInt(anoA), periodoA,
                                    "")
                    + " y "
                    + ejbServiciosPublicosCeroRemote.asignarNombrePeriodo(
                                    compania, Integer.parseInt(anoB), periodoB,
                                    "");

                consultaUsuario = Reporteador.resuelveConsulta(
                                "800064CoberturaMicroMedicionSinHistorial",
                                Integer.valueOf(SessionUtil.getModulo()),
                                reemplazarUsuario);

                consultaHistoricos = Reporteador.resuelveConsulta(
                                "800065CoberturaMicroMedicionConHistorial",
                                Integer.valueOf(SessionUtil.getModulo()),
                                reemplazarHistoricos);

                consultaPrincipal = consultaUsuario + " UNION ALL "
                    + consultaHistoricos;
            }
            else {
                consultaPrincipal = Reporteador.resuelveConsulta(
                                "800064CoberturaMicroMedicionSinHistorial",
                                Integer.valueOf(SessionUtil.getModulo()),
                                reemplazarUsuario);
            }
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    private void reporteSeleccionado() {

        if ("1".equals(tipoInforme)) {
            reporte = "001188RPTCOBERTURAMICRO";
        }
        if ("2".equals(tipoInforme)) {
            reporte = "001186RPTCOBERTURAMICROUSO";

        }
        else if ("3".equals(tipoInforme)) {
            reporte = "001190RPTCOBERTURAMICROPORUSUARIO";
            strTitulo = "INFORME DE COBERTURA DE MICROMEDICIÓN POR USUARIO";
        }

    }

    public void genInforme(ReportesBean.FORMATOS formato) {
        HashMap<String, Object> reemplazar = new HashMap<>();
        Map<String, Object> parametros = new HashMap<>();
        reemplazar.put("subConsulta", consultaPrincipal);
        parametros.put("PR_TITULO", (strTitulo != null) ? strTitulo : "");
        Reporteador.resuelveConsulta(reporte,
                        Integer.parseInt(SessionUtil.getModulo()),
                        reemplazar, parametros);
        try {
            archivoDescarga = JsfUtil.exportarStreamed(reporte, parametros,
                            ConectorPool.ESQUEMA_SYSMAN, formato);
        }
        catch (JRException | IOException | SysmanException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    private void cargarPeriodos() {
        try {
            if ("SI".equals(SysmanFunciones
                            .nvl(ejbSysmanUtilRemote.consultarParametro(
                                            compania,
                                            "GUARDA HISTORICOS DE FACTURA",
                                            SessionUtil.getModulo(), new Date(),
                                            false),
                                            "NO"))) {
                cargarPeriodos = true;
            }
            else {
                cargarPeriodos = false;
            }
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

}
