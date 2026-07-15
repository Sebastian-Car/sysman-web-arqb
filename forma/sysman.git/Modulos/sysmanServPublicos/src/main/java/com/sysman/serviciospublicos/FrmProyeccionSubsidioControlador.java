/*-
 * FrmProyeccionSubsidio.java
 *
 * 1.0
 *
 * 18/01/2017
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
import com.sysman.serviciospublicos.enums.FrmProyeccionSubsidioControladorEnum;
import com.sysman.serviciospublicos.enums.FrmProyeccionSubsidioControladorUrlEnum;
import com.sysman.util.SysmanFunciones;

import java.io.IOException;
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

import org.primefaces.model.StreamedContent;

import net.sf.jasperreports.engine.JRException;

/**
 * Controlador de la forma frmproyeccionsubsidio asociada al
 * formulario Proyección de subsidio por estrato. Que permite generar
 * un informe de los subsidios por estrato, teniendo en cuenta el tipo
 * de servicio prestado (Acueducto, Alcantarillado, Aseo o Alumbrado)
 * y un porcentaje de proyección, en un periodo definido entre un año
 * inicial y un año final.
 *
 *
 * @version 1.0, 18/01/2017
 * @author jlramirez
 *
 * -- Modificado por lcortes 06,07/06/2017. Refactorizacion de codigo
 * de las listas para utilizar dss. Reemplazo de llamados a la clase
 * Acciones por ejb respectivo y revision de alertas de la herramienta
 * SonarLint.
 */
@ManagedBean
@ViewScoped
public class FrmProyeccionSubsidioControlador extends BeanBaseModal {
    /**
     * Constante a nivel de clase que almacena el código de la
     * compania en la cual inicio sesión el usuario, el valor de esta
     * constante es asignado en el constructor a la variable de sesión
     * correspondiente
     */
    private final String compania;
    // <DECLARAR_ATRIBUTOS>
    /**
     * Atributo que contiene el valor asignado al control de tipo de
     * servicio del que se quiere generar el informe.
     */
    private String tipoServicio;
    /**
     * Atributo que almacena el codigo seleccionado en el combo ciclo
     * del formulario.
     */
    private String ciclo;
    /**
     * Atributo que almacena el año seleccionado en el combo de año
     * inicial en el formulario.
     */
    private int anioInicial;
    /**
     * Atributo que almacena el periodo seleccionado en el combo de
     * periodo inicial en el formulario.
     */
    private String periodoInicial;
    /**
     * Atributo que almacena el año seleccionado en el combo de año
     * final en el formulario.
     */
    private int anioFinal;
    /**
     * Atributo que almacena el periodo seleccionado en el combo de
     * periodo final en el formulario.
     */
    private String periodoFinal;
    /**
     * Atributo que almacena el porcentaje de proyección ingresado en
     * el formulario.
     */
    private String porcentaje;
    /**
     * Atributo usado para descargar contenidos de archivos desde la
     * vista
     */
    private StreamedContent archivoDescarga;
    /**
     * Atributo que contiene el valor asignado al parametro nombre
     * reemplazo alumbrado.
     */
    private String nombreReemplazoAlumbrado;
    /**
     * Atributo que contiene el valor asignado al parametro cambiar
     * nombre alumbrado.
     */
    private boolean cambiarNombreAlumbrado;
    /**
     * Atributo que contiene el valor asignado al nombre para el
     * servicio de Alumbrado que se debe imprimir en los reportes
     */
    private String nombreServicioAlumbrado;
    /**
     * Atributo que contiene el valor asignado al parametro nombre
     * reemplazo acueducto.
     */
    private String nombreReemplazoAcueducto;
    /**
     * Atributo que contiene el valor asignado al parametro cambiar
     * nombre acueducto.
     */
    private boolean cambiarNombreAcueducto;
    /**
     * Atributo que contiene el valor asignado al nombre para el
     * servicio de Acueducto que se debe imprimir en los reportes
     */
    private String nombreServicioAcueducto;
    /**
     * Atributo que contiene el valor asignado al parametro nombre
     * reemplazo alcantarillado.
     */
    private String nombreReemplazoAlcantarillado;
    /**
     * Atributo que contiene el valor asignado al parametro cambiar
     * nombre alcantarillado.
     */
    private boolean cambiarNombreAlcantarillado;
    /**
     * Atributo que contiene el valor asignado al nombre para el
     * servicio de Alcantarillado que se debe imprimir en los reportes
     */
    private String nombreServicioAlcantarillado;
    /**
     * Atributo que contiene el valor asignado al parametro nombre
     * reemplazo aseo.
     */
    private String nombreReemplazoAseo;
    /**
     * Atributo que contiene el valor asignado al parametro cambiar
     * nombre aseo.
     */
    private boolean cambiarNombreAseo;
    /**
     * Atributo que contiene el valor asignado al nombre para el
     * servicio de Aseo que se debe imprimir en los reportes
     */
    private String nombreServicioAseo;
    // </DECLARAR_ATRIBUTOS>
    // <DECLARAR_PARAMETROS>
    // </DECLARAR_PARAMETROS>
    // <DECLARAR_LISTAS>
    /**
     * Atributo de la clase que almacena la lista de los ciclos del
     * formulario
     */
    private List<Registro> listaCiclo;
    /**
     * Atributo de la clase que almacena la lista de los los registros
     * del combo año inicial del formulario.
     */
    private List<Registro> listaAnoInicial;
    /**
     * Atributo de la clase que almacena la lista de los los registros
     * del combo periodo inicial del formulario.
     */
    private List<Registro> listaPeriodoInicial;
    /**
     * Atributo de la clase que almacena la lista de los los registros
     * del combo año final del formulario.
     */
    private List<Registro> listaAnoFinal;
    /**
     * Atributo de la clase que almacena la lista de los los registros
     * del combo periodo final del formulario.
     */
    private List<Registro> listaPeriodoFinal;

    // </DECLARAR_LISTAS>

    @EJB
    private EjbSysmanUtilRemote ejbSysmanUtil;

    // <DECLARAR_LISTAS_COMBO_GRANDE>
    // </DECLARAR_LISTAS_COMBO_GRANDE>
    /**
     * Crea una nueva instancia de FrmProyeccionSubsidio
     */
    public FrmProyeccionSubsidioControlador() {
        super();
        compania = SessionUtil.getCompania();
        try {
            numFormulario = GeneralCodigoFormaEnum.FRM_PROYECCION_SUBSIDIO_CONTROLADOR
                            .getCodigo();
            validarPermisos();
            // <INI_ADICIONAL>
            // </INI_ADICIONAL>
        }
        catch (Exception ex) {
            Logger.getLogger(FrmProyeccionSubsidioControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            JsfUtil.agregarMensajeError(ex.getMessage());
        }
    }

    /**
     * Este método se ejecuta justo después de que el objeto de la
     * clase del Bean ha sido creado, en este se realizan las
     * asignaciones iniciales necesarias para la visualización del
     * formulario, como son tablas, origenes de datos, inicialización
     * de listas y demás necesarios
     */
    @PostConstruct
    public void inicializar() {
        ciclo = "T";
        anioInicial = SysmanFunciones.ano(new Date());
        anioFinal = SysmanFunciones.ano(new Date());
        periodoInicial = "01";
        periodoFinal = "01";
        tipoServicio = "1";
        porcentaje = "0";
        // <CARGAR_LISTA>
        // </CARGAR_LISTA>
        // <CARGAR_LISTA_COMBO_GRANDE>
        // </CARGAR_LISTA_COMBO_GRANDE>
        // <CREAR_ARBOLES>
        // </CREAR_ARBOLES>
        abrirFormulario();
    }

    /**
     * Este método es invocado el metodo inicializar, se ejecutan las
     * acciones a tener en cuenta en el momento de apertura del
     * formulario
     */
    @Override
    public void abrirFormulario() {
        // <CODIGO_DESARROLLADO>
        cargarParametros();
        reemplazarNombre();
        cargarListaCiclo();
        cargarListaAnoInicial();
        cargarListaAnoFinal();
        cargarListaPeriodoInicial();
        cargarListaPeriodoFinal();
        // </CODIGO_DESARROLLADO>
    }

    // <METODOS_CARGAR_LISTA>
    /**
     * Carga la lista listaCiclo
     */
    public void cargarListaCiclo() {

        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        try {
            listaCiclo = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            FrmProyeccionSubsidioControladorUrlEnum.URL8839
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    /**
     * Carga la lista listaAnoInicial
     */
    public void cargarListaAnoInicial() {

        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        try {
            listaAnoInicial = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            FrmProyeccionSubsidioControladorUrlEnum.URL9335
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    /**
     * Carga la lista listaPeriodoInicial
     */
    public void cargarListaPeriodoInicial() {

        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        try {
            listaPeriodoInicial = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            FrmProyeccionSubsidioControladorUrlEnum.URL9962
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    /**
     * Carga la lista listaAnoFinal
     */
    public void cargarListaAnoFinal() {
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.ANO.getName(), anioInicial);

        try {
            listaAnoFinal = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            FrmProyeccionSubsidioControladorUrlEnum.URL10675
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    /**
     * Carga la lista listaPeriodoFinal
     */
    public void cargarListaPeriodoFinal() {

        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(FrmProyeccionSubsidioControladorEnum.PARAM0.getValue(),
                        String.valueOf(periodoInicial));

        try {
            listaPeriodoFinal = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            FrmProyeccionSubsidioControladorUrlEnum.URL11365
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
     * Método en el que se obtiene el valor de determinados parámetros
     * para su posterior evaluación
     */
    public void cargarParametros() {
        try {
            String parCambiarNombreAlumbrado = ejbSysmanUtil.consultarParametro(
                            compania, "CAMBIAR NOMBRE SERVICIO ALUMBRADO",
                            SessionUtil.getModulo(), new Date(), true);

            cambiarNombreAlumbrado = ("SI").equals(parCambiarNombreAlumbrado);

            nombreReemplazoAlumbrado = ejbSysmanUtil.consultarParametro(
                            compania, "NOMBRE SERVICIO A REMPLAZAR ALUMBRADO",
                            SessionUtil.getModulo(), new Date(), true);

            String parCambiarNombreAcueducto = ejbSysmanUtil.consultarParametro(
                            compania, "CAMBIAR NOMBRE SERVICIO ACUEDUCTO",
                            SessionUtil.getModulo(), new Date(), true);

            cambiarNombreAcueducto = ("SI").equals(parCambiarNombreAcueducto);

            nombreReemplazoAcueducto = ejbSysmanUtil.consultarParametro(
                            compania, "NOMBRE SERVICIO A REMPLAZAR ACUEDUCTO",
                            SessionUtil.getModulo(), new Date(), true);

            String parCambiarNombreAlcantarillado = ejbSysmanUtil
                            .consultarParametro(compania,
                                            "CAMBIAR NOMBRE SERVICIO ALCANTARILLADO",
                                            SessionUtil.getModulo(), new Date(),
                                            true);
            cambiarNombreAlcantarillado = ("SI")
                            .equals(parCambiarNombreAlcantarillado);

            nombreReemplazoAlcantarillado = ejbSysmanUtil.consultarParametro(
                            compania,
                            "NOMBRE SERVICIO A REMPLAZAR ALCANTARILLADO",
                            SessionUtil.getModulo(), new Date(), true);

            String parCambiarNombreAseo = ejbSysmanUtil.consultarParametro(
                            compania, "CAMBIAR NOMBRE SERVICIO ASEO",
                            SessionUtil.getModulo(), new Date(), true);
            cambiarNombreAseo = ("SI").equals(parCambiarNombreAseo);

            nombreReemplazoAseo = ejbSysmanUtil.consultarParametro(compania,
                            "NOMBRE SERVICIO A REMPLAZAR ASEO",
                            SessionUtil.getModulo(), new Date(), true);
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    /**
     * Método en el que se reemplaza el nombre de un determinado
     * servicio que será mostrado en el formulario, según los
     * parámetros configurados previamente
     */
    public void reemplazarNombre() {
        if (cambiarNombreAlumbrado) {
            nombreServicioAlumbrado = nombreReemplazoAlumbrado;
        }
        else {
            nombreServicioAlumbrado = "Alumbrado";
        }
        if (cambiarNombreAcueducto) {
            nombreServicioAcueducto = nombreReemplazoAcueducto;
        }
        else {
            nombreServicioAcueducto = "Acueducto";
        }
        if (cambiarNombreAlcantarillado) {
            nombreServicioAlcantarillado = nombreReemplazoAlcantarillado;
        }
        else {
            nombreServicioAlcantarillado = "Alcantarillado";
        }
        if (cambiarNombreAseo) {
            nombreServicioAseo = nombreReemplazoAseo;
        }
        else {
            nombreServicioAseo = "Aseo";
        }
    }

    /**
     * Método que genera un reporte con el formato y tipo de servicio
     * público seleccionado por el usuario.
     *
     * @param formato
     * Extensión o tipo de reporte a generar.
     */

    public void generarReporte(FORMATOS formato) {
        String cond = "condicion";
        String reporte = "";
        try {
            HashMap<String, Object> reemplazar = new HashMap<>();
            Map<String, Object> parametros = new HashMap<>();
            reporte = "001363RptProyeccionSubsidios";

            // <REEMPLAZAR VARIABLES EN CONSULTA>
            reemplazar.put("anioInicial", anioInicial);
            reemplazar.put("anioFinal", anioFinal);
            reemplazar.put("periodoInicial", periodoInicial);
            reemplazar.put("periodoFinal", periodoFinal);
            reemplazar.put("porcentaje", porcentaje);
            if ("T".equals(ciclo)) {
                reemplazar.put("ciclo", "");
            }
            else {
                reemplazar.put("ciclo", "AND EC.CICLO IN (" + ciclo + ")");
            }

            switch (tipoServicio) {
            case "2":
                reemplazar.put(cond, "AND EC.SERVICIO IN('01')");
                break;
            case "3":
                reemplazar.put(cond, "AND EC.SERVICIO IN('02')");
                break;
            case "4":
                reemplazar.put(cond, "AND EC.SERVICIO IN('03')");
                break;
            case "5":
                reemplazar.put(cond, "AND EC.SERVICIO IN('04')");
                break;
            default:
                reemplazar.put(cond, "");
                break;
            }
            // </REEMPLAZAR VARIABLES EN CONSULTA>

            // <ENVIAR PARAMETROS AL REPORTE>
            parametros.put("PR_ANOINICIAL", anioInicial);
            parametros.put("PR_ANOFINAL", anioFinal);
            parametros.put("PR_PERIODOINICIAL", periodoInicial);
            parametros.put("PR_PERIODOFINAL", periodoFinal);
            parametros.put("PR_CICLO", ciclo);
            parametros.put("PR_PORCE", porcentaje);
            // </ENVIAR PARAMETROS AL REPORTE>
            Reporteador.resuelveConsulta(reporte,
                            Integer.parseInt(SessionUtil.getModulo()),
                            reemplazar, parametros);

            archivoDescarga = JsfUtil.exportarStreamed(reporte, parametros,
                            ConectorPool.ESQUEMA_SYSMAN, formato);
        }
        catch (JRException | IOException | SysmanException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    /**
     * Método ejecutado al oprimir el botón Pdf en la vista
     */
    public void oprimirPdf() {
        // <CODIGO_DESARROLLADO>
        archivoDescarga = null;
        generarReporte(FORMATOS.PDF);
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Método ejecutado al oprimir el botón Excel en la vista
     */
    public void oprimirExcel() {
        // <CODIGO_DESARROLLADO>
        archivoDescarga = null;
        generarReporte(FORMATOS.EXCEL97);
        // </CODIGO_DESARROLLADO>
    }

    // </METODOS_BOTONES>
    // <METODOS_CAMBIAR>
    /**
     * Método ejecutado al cambiar el control AnoInicial
     */
    public void cambiarAnoInicial() {
        // <CODIGO_DESARROLLADO>
        anioFinal = anioInicial;
        cargarListaAnoFinal();
        // </CODIGO_DESARROLLADO>
    }

    // </METODOS_CAMBIAR>
    // <METODOS_COMBOS_GRANDES>
    // </METODOS_COMBOS_GRANDES>
    // <METODOS_ARBOL>
    // </METODOS_ARBOL>
    // <SET_GET_ATRIBUTOS>
    /**
     * Retorna la variable tipoServicio
     *
     * @return tipoServicio
     */
    public String getTipoServicio() {
        return tipoServicio;
    }

    /**
     * Asigna la variable tipoServicio
     *
     * @param tipoServicio
     * Variable a asignar en tipoServicio
     */
    public void setTipoServicio(String tipoServicio) {
        this.tipoServicio = tipoServicio;
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
     * Retorna la variable anioInicial
     *
     * @return anioInicial
     */
    public int getAnioInicial() {
        return anioInicial;
    }

    /**
     * Asigna la variable anioInicial
     *
     * @param anioInicial
     * Variable a asignar en anioInicial
     */
    public void setAnioInicial(int anioInicial) {
        this.anioInicial = anioInicial;
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
     * Retorna la variable anioFinal
     *
     * @return anioFinal
     */
    public int getAnioFinal() {
        return anioFinal;
    }

    /**
     * Asigna la variable anioFinal
     *
     * @param anioFinal
     * Variable a asignar en anioFinal
     */
    public void setAnioFinal(int anioFinal) {
        this.anioFinal = anioFinal;
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
     * Retorna la variable porcentaje
     *
     * @return porcentaje
     */
    public String getPorcentaje() {
        return porcentaje;
    }

    /**
     * Asigna la variable porcentaje
     *
     * @param porcentaje
     * Variable a asignar en porcentaje
     */
    public void setPorcentaje(String porcentaje) {
        this.porcentaje = porcentaje;
    }

    /**
     * Retorna el atributo archivoDescarga
     *
     * @return archivoDescarga
     */
    public StreamedContent getArchivoDescarga() {
        return archivoDescarga;
    }

    /**
     * Retorna la variable nombreServicioAlumbrado
     *
     * @return nombreServicioAlumbrado
     */
    public String getNombreServicioAlumbrado() {
        return nombreServicioAlumbrado;
    }

    /**
     * Asigna la variable nombreServicioAlumbrado
     *
     * @param nombreServicioAlumbrado
     * Variable a asignar en nombreServicioAlumbrado
     */
    public void setNombreServicioAlumbrado(String nombreServicioAlumbrado) {
        this.nombreServicioAlumbrado = nombreServicioAlumbrado;
    }

    /**
     * Retorna la variable nombreServicioAcueducto
     *
     * @return nombreServicioAcueducto
     */
    public String getNombreServicioAcueducto() {
        return nombreServicioAcueducto;
    }

    /**
     * Asigna la variable nombreServicioAcueducto
     *
     * @param nombreServicioAcueducto
     * Variable a asignar en nombreServicioAcueducto
     */
    public void setNombreServicioAcueducto(String nombreServicioAcueducto) {
        this.nombreServicioAcueducto = nombreServicioAcueducto;
    }

    /**
     * Retorna la variable nombreServicioAlcantarillado
     *
     * @return nombreServicioAlcantarillado
     */
    public String getNombreServicioAlcantarillado() {
        return nombreServicioAlcantarillado;
    }

    /**
     * Asigna la variable nombreServicioAlcantarillado
     *
     * @param nombreServicioAlcantarillado
     * Variable a asignar en nombreServicioAlcantarillado
     */
    public void setNombreServicioAlcantarillado(
        String nombreServicioAlcantarillado) {
        this.nombreServicioAlcantarillado = nombreServicioAlcantarillado;
    }

    /**
     * Retorna la variable nombreServicioAseo
     *
     * @return nombreServicioAseo
     */
    public String getNombreServicioAseo() {
        return nombreServicioAseo;
    }

    /**
     * Asigna la variable nombreServicioAseo
     *
     * @param nombreServicioAseo
     * Variable a asignar en nombreServicioAseo
     */
    public void setNombreServicioAseo(String nombreServicioAseo) {
        this.nombreServicioAseo = nombreServicioAseo;
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
     * Retorna la lista listaAnoInicial
     *
     * @return listaAnoInicial
     */
    public List<Registro> getListaAnoInicial() {
        return listaAnoInicial;
    }

    /**
     * Asigna la lista listaAnoInicial
     *
     * @param listaAnoInicial
     * Variable a asignar en listaAnoInicial
     */
    public void setListaAnoInicial(List<Registro> listaAnoInicial) {
        this.listaAnoInicial = listaAnoInicial;
    }

    /**
     * Retorna la lista listaPeriodoInicial
     *
     * @return listaPeriodoInicial
     */
    public List<Registro> getListaPeriodoInicial() {
        return listaPeriodoInicial;
    }

    /**
     * Asigna la lista listaPeriodoInicial
     *
     * @param listaPeriodoInicial
     * Variable a asignar en listaPeriodoInicial
     */
    public void setListaPeriodoInicial(List<Registro> listaPeriodoInicial) {
        this.listaPeriodoInicial = listaPeriodoInicial;
    }

    /**
     * Retorna la lista listaAnoFinal
     *
     * @return listaAnoFinal
     */
    public List<Registro> getListaAnoFinal() {
        return listaAnoFinal;
    }

    /**
     * Asigna la lista listaAnoFinal
     *
     * @param listaAnoFinal
     * Variable a asignar en listaAnoFinal
     */
    public void setListaAnoFinal(List<Registro> listaAnoFinal) {
        this.listaAnoFinal = listaAnoFinal;
    }

    /**
     * Retorna la lista listaPeriodoFinal
     *
     * @return listaPeriodoFinal
     */
    public List<Registro> getListaPeriodoFinal() {
        return listaPeriodoFinal;
    }

    /**
     * Asigna la lista listaPeriodoFinal
     *
     * @param listaPeriodoFinal
     * Variable a asignar en listaPeriodoFinal
     */
    public void setListaPeriodoFinal(List<Registro> listaPeriodoFinal) {
        this.listaPeriodoFinal = listaPeriodoFinal;
    }
    // </SET_GET_LISTAS>
    // <SET_GET_LISTAS_COMBO_GRANDE>
    // </SET_GET_LISTAS_COMBO_GRANDE>
}
