/*-
 * LCortesSuspensionesControlador.java
 *
 * 1.0
 *
 * 11/11/2016
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
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.persistencia.ConectorPool;
import com.sysman.recursos.ejb.EjbSysmanUtilRemote;
import com.sysman.reportes.Reporteador;
import com.sysman.serviciospublicos.ejb.EjbServiciosPublicosOchoRemote;
import com.sysman.serviciospublicos.ejb.EjbServiciosPublicosTresRemote;
import com.sysman.serviciospublicos.enums.LCortesSuspensionesControladorEnum;
import com.sysman.serviciospublicos.enums.LCortesSuspensionesControladorUrlEnum;

import java.io.IOException;
import java.math.BigDecimal;
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
 * Esta clase es el controlador para el formulario que permite generar
 * el informe de Usuarios para corte y suspension, en Access
 * "LCortesSuspensiones", el cual es llamado desde
 * Facturacion\Informes\Facturación y cartera\Listado para Cortes y
 * Suspensiones Abonos
 *
 * @author amonroy
 * @version 1.0, 11/11/2016
 * @version 2.0, 14/06/2017 - Se eliminan los metodos
 * prepararConsulta, prepararConsultaDos, prepararConsultaTres y
 * asignarOrdenamiento, los cuales se usaban para construir la
 * consulta que genera el reporte, se unifican estos metodos en la
 * funcion PCK_SERVICIOS_PUBLICOS_COM8.FC_PREPARARINFORMESUSPENSIONES
 * <br>
 * - Se realiza el Proceso de Refactoring e implementacion de EJBs
 * para las funciones y procedimientos que son llamadas en el
 * controlador
 */
@ManagedBean
@ViewScoped
public class LCortesSuspensionesControlador extends BeanBaseModal {
    /**
     * Constante a nivel de clase que almacena el codigo de la
     * compania en la cual inicio sesion el usuario, el valor de esta
     * constante es asignado en el constructor a la variable de sesion
     * correspondiente
     */
    private final String compania;
    /**
     * Constante que almacena el codigo del modulo actual
     */
    private final String modulo;
    // <DECLARAR_ATRIBUTOS>
    /**
     * Atributo que almacena pate de la consulta que se arma para
     * generar el informe
     */
    private String condicion;
    /**
     * Atributo que almacena la parte de ordenamioento en la consulta
     * del informe
     */
    private String ordenadoPor;
    /**
     * Atributo que almacena el valor del ciclo inicial seleccionado
     * en el formulario
     */
    private String cicloInicial;
    /**
     * Atributo que almacena el valor del ciclo final seleccionado en
     * el formulario
     */
    private String cicloFinal;
    /**
     * Indicador que permite definir el filtro de chapetas para
     * generar el informe
     */
    private String chapetas;
    /**
     * Indicador que permite definir el filtro de PQR para generar el
     * informe
     */
    private String pqr;
    /**
     * Indicador que permite definir el filtro de Abonos para generar
     * el informe
     */
    private String abonos;
    /**
     * Atributo que almacena el valor del periodo inicial de atraso
     * seleccionado en el formulario
     */
    private String periodoAtrasoInicial;
    /**
     * Atributo que almacena el valor definido en el campo
     * "valor superior a", usado para definir la consulta que genera
     * el reporte
     */
    private String superior;
    /**
     * Atributo que almacena el valor del periodo final de atraso
     * seleccionado en el formulario
     */
    private String periodoAtrasoFinal;
    /**
     * Permite definir la consulta "UsuariosSinAbonos" que se usa para
     * armar la consulta del reporte
     */
    private String usuariosSinAbonos;
    /**
     * Atributo usado para descargar contenidos de archivos desde la
     * vista
     */
    private StreamedContent archivoDescarga;
    /**
     * Atributo auxiliar para armar la consulta del reporte
     */
    private String parCondicion;
    /**
     * Almacena el nombre del ciclo inicial que se selecciona en el
     * formulario
     */
    private String nombrePeriodo;
    /**
     * Implementacion del EJB de SysmanUtil para hacer el llamado a la
     * funcion FC_PAR
     */
    @EJB
    private EjbSysmanUtilRemote ejbSysmanUtil;
    /**
     * Implementacion del EJB de EjbServiciosPublicosTresRemote para
     * hacer el llamado a las funciones que se invocan dentro del
     * Controlador y se encuentran almacenadas en el paquete
     * PCK_SERVICIOS_PUBLICOS_COM3
     */
    @EJB
    private EjbServiciosPublicosTresRemote ejbServiciosPublicosTres;
    /**
     * Implementacion del EJB de EjbServiciosPublicosOchoRemote para
     * hacer el llamado a las funciones que se invocan dentro del
     * Controlador y se encuentran almacenadas en el paquete
     * PCK_SERVICIOS_PUBLICOS_COM8
     */
    @EJB
    private EjbServiciosPublicosOchoRemote ejbServiciosPublicosOcho;
    // </DECLARAR_ATRIBUTOS>
    // <DECLARAR_PARAMETROS>
    // </DECLARAR_PARAMETROS>
    // <DECLARAR_LISTAS>
    /**
     * Listado de registros para el comboBox de ciclo Inicial
     */
    private List<Registro> listaCicloInicial;
    /**
     * Listado de registros para el comboBox de cilo final
     */
    private List<Registro> listaCicloFinal;

    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>
    // </DECLARAR_LISTAS_COMBO_GRANDE>
    /**
     * Crea una nueva instancia de LCortesSuspensionesControlador
     */
    public LCortesSuspensionesControlador() {
        super();
        compania = SessionUtil.getCompania();
        modulo = SessionUtil.getModulo();
        try {
            numFormulario = GeneralCodigoFormaEnum.L_CORTES_SUSPENSIONES_CONTROLADOR
                            .getCodigo();
            validarPermisos();
            // <INI_ADICIONAL>
            periodoAtrasoInicial = "0";
            periodoAtrasoFinal = "999";
            chapetas = "3";
            pqr = "-1";
            abonos = "-1";
            condicion = "1";
            ordenadoPor = "1";
            superior = "0";
            usuariosSinAbonos = "";
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
        cargarListaCicloInicial();
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
     * Carga la lista listaCicloInicial
     *
     */
    public void cargarListaCicloInicial() {
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        try {
            listaCicloInicial = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            LCortesSuspensionesControladorUrlEnum.URL8750
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
     * Carga la lista listaCicloFinal
     *
     */
    public void cargarListaCicloFinal() {
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(LCortesSuspensionesControladorEnum.CICLOINICIAL.getValue(),
                        cicloInicial);

        try {
            listaCicloFinal = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            LCortesSuspensionesControladorUrlEnum.URL9478
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
     * Metodo ejecutado al oprimir el boton BtnPdf en la vista
     *
     * Hace el llamado al metodo "generarInforme" indicando el formato
     * con el que se desea generar
     *
     */
    public void oprimirBtnPdf() {
        // <CODIGO_DESARROLLADO>
        if (!validarPeriodosAtraso()) {
            archivoDescarga = null;
            generarInforme(FORMATOS.PDF);
        }
        else {
            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB2731"));
        }
        // </CODIGO_DESARROLLADO>
    }

    /**
     *
     * Metodo ejecutado al oprimir el boton BtnExcel en la vista
     *
     * Hace el llamado al metodo "generarInforme" indicando el formato
     * con el que se desea
     *
     */
    public void oprimirBtnExcel() {
        // <CODIGO_DESARROLLADO>
        if (!validarPeriodosAtraso()) {
            archivoDescarga = null;
            generarInforme(FORMATOS.EXCEL);
        }
        else {
            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB2731"));
        }
        // </CODIGO_DESARROLLADO>
    }

    // </METODOS_BOTONES>
    // <METODOS_CAMBIAR>
    /**
     * Actualiza el listado de registros que se carga en el combo para
     * seleccionar el ciclo final
     */
    public void cambiarCicloInicial() {
        cicloFinal = cicloInicial;
        cargarListaCicloFinal();
    }

    // </METODOS_CAMBIAR>
    // <METODOS_COMBOS_GRANDES>
    // </METODOS_COMBOS_GRANDES>
    // <METODOS_ARBOL>
    /**
     * Verifica que el periodo inicial que fue digitado en el
     * formulario sea menor que el periodo
     *
     * @return el resultado de la validacion verdadero o falso
     */
    public boolean validarPeriodosAtraso() {
        return Integer.parseInt(periodoAtrasoInicial) > Integer
                        .parseInt(periodoAtrasoFinal);
    }

    /**
     * Permite definir el formato del reporte que se va a generar
     * dependiendo del valor del parametro
     *
     * @return
     */
    public String obtenerInforme() {
        String strNombreReporte = "";
        try {
            strNombreReporte = ejbSysmanUtil.consultarParametro(
                            compania,
                            "FORMATO REPORTE DE CORTES Y SUSPENSION",
                            modulo, new Date(),
                            true);

            if ((strNombreReporte == null) || strNombreReporte.isEmpty()) {
                JsfUtil.agregarMensajeError(idioma.getString("TB_TB2726"));
            }

        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

        return strNombreReporte;
    }

    /**
     * Arma el subtitulo que se visualizará en el reporte y define
     * algunos parámetros del informe
     *
     * @return Subtitulo a enviar en los parametros del informe
     */
    public String generarSubtitulo() {
        parCondicion = "1".equals(condicion) ? "y"
            : idioma.getString("TB_TB2717");
        nombrePeriodo = service.buscarEnLista(cicloInicial,
                        "NUMERO", "NOMPERIODO", listaCicloInicial);

        String strChapetas = "-1".equals(chapetas) ? "Con Chapetas"
            : ("0".equals(chapetas) ? "Sin Chapetas" : "");
        String strPqr = "-1".equals(pqr) ? "Sin PQR"
            : ("0".equals(pqr)
                ? "Con PQR" : "");
        String strAbonos = "-1".equals(abonos)
            ? "Sin Abonos" : ("0".equals(abonos) ? "Con Abonos" : "");

        return asignarSubtitulo(strChapetas, strAbonos, strPqr);
    }

    /**
     * Metodo auxiliar para armar el subtitulo que se envia al reporte
     * 
     * @param strChapetas
     * Cadena asignada a la opcion seleccionada en Chapeta
     * @param strAbonos
     * Cadena asignada a la opcion seleccionada en Abonos
     * @param strPqr
     * Cadena asignada a la opcion seleccionada en PQR
     * @return La cadena con el subtitulo definitivo
     */
    private String asignarSubtitulo(String strChapetas, String strAbonos,
        String strPqr) {
        String subtitulo;
        if (strChapetas.isEmpty()) {
            subtitulo = strPqr.isEmpty() ? strAbonos
                : (strAbonos.isEmpty() ? strPqr : strPqr + ", " + strAbonos);
        }
        else {
            subtitulo = strChapetas
                + (strPqr.isEmpty() ? ", " + strAbonos
                    : (strAbonos.isEmpty() ? ", " + strPqr
                        : ", " + strPqr + ", " + strAbonos));
        }
        return subtitulo;
    }

    /**
     * Define las acciones necesarias para generar el informe realiza
     * el reemplazo de valores en la consulta del informe y envia los
     * parametros definidos
     *
     * @param formato
     * Formato seleccionado por el usuario para generar el informe
     */
    public void generarInforme(FORMATOS formato) {
        String nombreReporte = obtenerInforme();
        String subtitulo = generarSubtitulo();

        try {
            ejbServiciosPublicosTres.preparaExcluirCartera(compania,
                            Integer.parseInt(cicloInicial),
                            Integer.parseInt(cicloFinal),
                            SessionUtil.getUser().getCodigo());

            // Obtiene el reemplazo para enviar a la consulta del
            // informe 001270LCortesSuspensiones
            usuariosSinAbonos = ejbServiciosPublicosOcho
                            .prepararInformeSuspensiones(compania,
                                            Integer.parseInt(cicloInicial),
                                            Integer.parseInt(cicloFinal),
                                            Integer.parseInt(abonos),
                                            Integer.parseInt(chapetas),
                                            Integer.parseInt(pqr),
                                            Integer.parseInt(
                                                            periodoAtrasoInicial),
                                            Integer.parseInt(
                                                            periodoAtrasoFinal),
                                            Integer.parseInt(condicion),
                                            BigDecimal.valueOf(Double
                                                            .parseDouble(superior)),
                                            Integer.parseInt(ordenadoPor));

            // HashMap reemplazar envia reemplazos a la consulta
            // almacenada
            HashMap<String, Object> reemplazar = new HashMap<>();
            reemplazar.put("usuariosSinAbonos", usuariosSinAbonos);

            // MANEJO DE PARAMETROS DE REEMPLAZO
            Map<String, Object> parametros = new HashMap<>();
            parametros.put("PR_PERIODOATRASOINICIAL", periodoAtrasoInicial);
            parametros.put("PR_PERIODOATRASOFINAL", periodoAtrasoFinal);
            parametros.put("PR_CONDICION", parCondicion);
            parametros.put("PR_SUPERIOR", superior);
            parametros.put("PR_CICLOINICIAL", cicloInicial);
            parametros.put("PR_CICLOFINAL", cicloFinal);
            parametros.put("PR_NOMBREPERIODO", nombrePeriodo);
            parametros.put("PR_SUBTITULO", subtitulo);
            parametros.put("PR_EXCEL",
                            formato.equals(FORMATOS.EXCEL) ? true : false);
            parametros.put("PR_VIGILADOPOR", SessionUtil.getCompaniaIngreso()
                            .getRutaVigiladoPor());

            Reporteador.resuelveConsulta(nombreReporte,
                            Integer.parseInt(modulo), reemplazar,
                            parametros);

            archivoDescarga = JsfUtil.exportarStreamed(
                            nombreReporte,
                            parametros,
                            ConectorPool.ESQUEMA_SYSMAN, formato);
        }
        catch (JRException | IOException | SysmanException
                        | NumberFormatException | SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

        JsfUtil.agregarMensajeError(idioma.getString("TB_TB1750"));
    }

    // </METODOS_ARBOL>
    // <SET_GET_ATRIBUTOS>
    /**
     * Retorna la variable condicion
     *
     * @return condicion
     */
    public String getCondicion() {
        return condicion;
    }

    /**
     * Asigna la variable condicion
     *
     * @param condicion
     * Variable a asignar en condicion
     */
    public void setCondicion(String condicion) {
        this.condicion = condicion;
    }

    /**
     * Retorna la variable ordenadoPor
     *
     * @return ordenadoPor
     */
    public String getOrdenadoPor() {
        return ordenadoPor;
    }

    /**
     * Asigna la variable ordenadoPor
     *
     * @param ordenadoPor
     * Variable a asignar en ordenadoPor
     */
    public void setOrdenadoPor(String ordenadoPor) {
        this.ordenadoPor = ordenadoPor;
    }

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
     * Retorna la variable chapetas
     *
     * @return chapetas
     */
    public String getChapetas() {
        return chapetas;
    }

    /**
     * Asigna la variable chapetas
     *
     * @param chapetas
     * Variable a asignar en chapetas
     */
    public void setChapetas(String chapetas) {
        this.chapetas = chapetas;
    }

    /**
     * Retorna la variable pqr
     *
     * @return pqr
     */
    public String getPqr() {
        return pqr;
    }

    /**
     * Asigna la variable pqr
     *
     * @param pqr
     * Variable a asignar en pqr
     */
    public void setPqr(String pqr) {
        this.pqr = pqr;
    }

    /**
     * Retorna la variable abonos
     *
     * @return abonos
     */
    public String getAbonos() {
        return abonos;
    }

    /**
     * Asigna la variable abonos
     *
     * @param abonos
     * Variable a asignar en abonos
     */
    public void setAbonos(String abonos) {
        this.abonos = abonos;
    }

    /**
     * Retorna la variable periodoAtrasoInicial
     *
     * @return periodoAtrasoInicial
     */
    public String getPeriodoAtrasoInicial() {
        return periodoAtrasoInicial;
    }

    /**
     * Asigna la variable periodoAtrasoInicial
     *
     * @param periodoAtrasoInicial
     * Variable a asignar en periodoAtrasoInicial
     */
    public void setPeriodoAtrasoInicial(String periodoAtrasoInicial) {
        this.periodoAtrasoInicial = periodoAtrasoInicial;
    }

    /**
     * Retorna la variable superior
     *
     * @return superior
     */
    public String getSuperior() {
        return superior;
    }

    /**
     * Asigna la variable superior
     *
     * @param superior
     * Variable a asignar en superior
     */
    public void setSuperior(String superior) {
        this.superior = superior;
    }

    /**
     * Retorna la variable periodoAtrasoFinal
     *
     * @return periodoAtrasoFinal
     */
    public String getPeriodoAtrasoFinal() {
        return periodoAtrasoFinal;
    }

    /**
     * Asigna la variable periodoAtrasoFinal
     *
     * @param periodoAtrasoFinal
     * Variable a asignar en periodoAtrasoFinal
     */
    public void setPeriodoAtrasoFinal(String periodoAtrasoFinal) {
        this.periodoAtrasoFinal = periodoAtrasoFinal;
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
     * Retorna la lista listaCicloInicial
     *
     * @return listaCicloInicial
     */
    public List<Registro> getListaCicloInicial() {
        return listaCicloInicial;
    }

    /**
     * Asigna la lista listaCicloInicial
     *
     * @param listaCicloInicial
     * Variable a asignar en listaCicloInicial
     */
    public void setListaCicloInicial(List<Registro> listaCicloInicial) {
        this.listaCicloInicial = listaCicloInicial;
    }

    /**
     * Retorna la lista listaCicloFinal
     *
     * @return listaCicloFinal
     */
    public List<Registro> getListaCicloFinal() {
        return listaCicloFinal;
    }

    /**
     * Asigna la lista listaCicloFinal
     *
     * @param listaCicloFinal
     * Variable a asignar en listaCicloFinal
     */
    public void setListaCicloFinal(List<Registro> listaCicloFinal) {
        this.listaCicloFinal = listaCicloFinal;
    }
    // </SET_GET_LISTAS>
    // <SET_GET_LISTAS_COMBO_GRANDE>
    // </SET_GET_LISTAS_COMBO_GRANDE>
}
