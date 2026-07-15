/*-
 * ActadesuspensionControlador.java
 *
 * 1.0
 * 
 * 09/02/2017
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */
package com.sysman.serviciospublicos;

import com.sysman.beanbase.BeanBaseDatosAcmeImpl;
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
import com.sysman.serviciospublicos.ejb.EjbServiciosPublicosDosRemote;
import com.sysman.serviciospublicos.enums.ActadesuspensionControladorEnum;
import com.sysman.serviciospublicos.enums.ActadesuspensionControladorUrlEnum;
import com.sysman.util.SysmanFunciones;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.SQLException;
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
import javax.naming.NamingException;

import org.primefaces.event.SelectEvent;
import org.primefaces.model.StreamedContent;

import net.sf.jasperreports.engine.JRException;

/**
 * Controlador de la vista {@code actadesuspension} utilizada para
 * generar las actas de suspension del modulo de servicios publicos.
 *
 * @version 1.0, 09/02/2017
 * @author pespitia
 * @version 2, 16/05/2017 - spina se refactoriza para dss, depuracion
 * sonar y ejb
 * 
 * @version 3.0, 13/06/2017, <strong>pespitia</strong>:<br>
 * Reemplazar numero del formulario por enumerado.<br>
 * Reemplazar los llamados a ConectorPool por el esquema
 * ConectorPool.ESQUEMA_SYSMAN.
 * 
 */
@ManagedBean
@ViewScoped
public class ActadesuspensionControlador extends BeanBaseDatosAcmeImpl {
    /**
     * Constante a nivel de clase que almacena el codigo de la
     * compania en la cual inicio sesion el usuario, el valor de esta
     * constante es asignado en el constructor a la variable de sesion
     * correspondiente
     */
    private final String compania;

    /**
     * Constante a nivel de clase que almacena el codigo del usuario
     * con el que se inicio sesion, el valor de esta constante es
     * asignado en el constructor a la variable de sesion
     * correspondiente
     */
    private final String usuario;

    /**
     * Constante a nivel de clase que almacena el codigo del modulo en
     * el cual inicio session el usuario. el valor de esta constante
     * es asignado en el constructor a la variable de session
     * correspondiente
     */
    private final String modulo;

    /**
     * Constante a nivel de clase que aloja el valor
     * {@code CODIGORUTA}
     */
    private final String cCodigoRuta;

    /**
     * Constante a nivel de clase que aloja el valor {@code CODIGO}
     */
    private final String cCodigo;

    // <DECLARAR_ATRIBUTOS>

    /**
     * Atributo a nivel de clase que almacena el valor del check
     * {@code Consecutivo Automatico}
     */
    private boolean consecutivoAutomatico;

    /**
     * Atributo a nivel de clase que almacena el valor del check
     * 'Incluir Usuarios con Abonos'
     */
    private boolean ckAbonos;

    /**
     * Atributo que contiene el valor seleccionado del combo 'Codigo
     * Inicial'
     */
    private String codigoInicial;

    /**
     * Atributo que contiene el valor seleccionado del combo 'Codigo
     * Final'
     */
    private String codigoFinal;

    /**
     * Atributo que contiene el valor seleccionado del combo 'Ciclo'
     */
    private String ciclo;

    /**
     * Atributo que contiene el valor seleccionado del combo 'Formato'
     */
    private String formato;

    /**
     * Atributo que contiene el valor seleccionado del combo 'Estado'
     */
    private String estado;

    /**
     * Atributo que contiene el valor seleccionado del combo 'Informes
     * Formateados'
     */
    private String formateado;

    /**
     * Atributo que contiene el valor seleccionado del combo
     * 'Chapetas'
     */
    private String cbChapetas;

    /** Atributo que contiene el valor del combo 'PQR' seleccionado */
    private String pqr;

    /**
     * Atributo que contiene el valor seleccionado del combo 'Abonos'
     */
    private String abonos;

    /**
     * Atributo que contiene el valor del combo 'Super Intendencia'
     * seleccionado
     */
    private String intendencia;

    /**
     * Atributo que contiene el valor del campo 'Iniciar Consecutivo
     * en' asignado
     */
    private String consecutivo;

    /** Atributo que contiene el valor asignado en el campo 'Fecha' */
    private Date fechaEmision;

    /**
     * Atributo que contiene el valor asignado en el campo 'Periodos
     * Atraso > a'. Hace referencia al limite inferior.
     */
    private String periodoAtrasoMenor;

    /**
     * Atributo que contiene el valor asignado en el campo 'Deuda
     * Inicial'
     */
    private String deudaInicial;

    /**
     * Atributo que contiene el valor asignado en el campo 'Deuda
     * Final'
     */
    private String deudaFinal;

    /**
     * Atributo que contiene el valor asignado en el campo 'Periodos
     * Atraso < a'. Hace referencia al limite superior.
     */
    private String periodoAtrasoMayor;

    /**
     * Atributo que contiene el valor asignado en el campo 'Motivo'
     */
    private String motivo;

    /**
     * Atributo a nivel de clase que aloja el valor del parametro
     * 'PERIODOS DE ATRASO MODIFICA TIT. ACTA SUSPENSION'
     */
    private String parPeriodosAtraso;

    /**
     * Atributo a nivel de clase que aloja el valor del parametro
     * 'TITULO ACTA DE SUSPENSION'
     */
    private String parTituloActa;

    /**
     * Atributo a nivel de clase que aloja el valor del parametro
     * 'NOTA INVALIDAR ACTAS SUSPENSION ANTERIORES'
     */
    private String parNotaInvalidar;

    /**
     * Atributo a nivel de clase que aloja el valor del parametro
     * 'ACTAS DE SUSPENSION SIN FACTURADO ACTUAL'
     */
    private String parActaSuspension;

    /**
     * Almacena el valor del campo fecha del registro seleccionado en
     * el combo 'Formato'
     */
    private String fechaFormato;

    /**
     * Atributo que controla la visibilidad del boton 'Postergado
     * Vencido'
     */
    private boolean visiblePostergado;

    /**
     * Controla la visibilidad de los controles asociados al parametro
     * EXCLUIR PQR Y FINANCIABLES DEUDA PAGAS
     */
    private boolean visibleExcluirPqr;

    /**
     * Controla la visibilidad de los controles asociados al parametro
     * EXCLUIR PQR TRASLADADAS A SUPERINTENDENCIA
     */
    private boolean visibleExcluirPqrTS;

    /** Controla la visibilidad del combo 'Superintendencia'. */
    private boolean visibleSuperintendencia;

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
    /** Lista que contiene los items del combo 'Codigo Inicial' */
    private RegistroDataModelImpl listaCodigoInicial;

    /** Lista que contiene los items del combo 'Codigo Final' */
    private RegistroDataModelImpl listaCodigoFinal;

    /** Lista que contiene los items del combo 'Ciclo' */
    private RegistroDataModelImpl listaCiclo;

    /**
     * Lista que contiene los items del combo 'Informes Formateados'
     */
    private RegistroDataModelImpl listaFormateado;

    /** Lista que contiene los items del combo 'Formato' */
    private RegistroDataModelImpl listaFormato;

    @EJB
    private EjbServiciosPublicosDosRemote ejbServiciosPublicosDos;

    @EJB
    private EjbSysmanUtilRemote ejbSysmanUtil;

    // </DECLARAR_LISTAS_COMBO_GRANDE>

    /**
     * Crea una nueva instancia de ActadesuspensionControlador
     */
    public ActadesuspensionControlador() {
        super();
        compania = SessionUtil.getCompania();
        modulo = SessionUtil.getModulo();
        usuario = SessionUtil.getUser().getCodigo();

        cCodigoRuta = "CODIGORUTA";
        cCodigo = "CODIGO";

        try {
            // 1288
            numFormulario = GeneralCodigoFormaEnum.ACTADESUSPENSION_CONTROLADOR
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
     * En este metodo se hace la invocacion de lo metodos de carga de
     * todas las listas, menos las que son de subformularios
     */
    @Override
    public void iniciarListas() {
        // <CARGAR_LISTA_COMBO_GRANDE>
        // </CARGAR_LISTA_COMBO_GRANDE>
        // <CARGAR_LISTA>
        // </CARGAR_LISTA>
    }

    /**
     * En este metodo se hace la invocacion de lo metodos de carga de
     * todas las listas que son de subformularios
     */
    @Override
    public void iniciarListasSub() {
        // <CARGAR_LISTAS_SUBFORM>
        // </CARGAR_LISTAS_SUBFORM>
        // <CREAR_ARBOLES>
        // </CREAR_ARBOLES>
    }

    /**
     * En este metodo se iguala a null todas las listas de los
     * subformularios
     */
    @Override
    public void iniciarListasSubNulo() {
        // <CARGAR_LISTAS_SUBFORM_NULL>
        // </CARGAR_LISTAS_SUBFORM_NULL>
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
        fechaEmision = new Date();

        asignarMotivo();
        controlarVisibilidadInicial();
        cargarParametros();

        periodoAtrasoMenor = "0";
        periodoAtrasoMayor = "99999";
        deudaInicial = "0";
        deudaFinal = "999999999.99";
        abonos = "-1";
        cbChapetas = "3";
        pqr = "-1";
        intendencia = "S";
        ckAbonos = consecutivoAutomatico = true;
        estado = "A";
        tabla = "";

        asignarConsecutivo();
        cargarListaCiclo();
        cargarListaFormato();

        asignarOrigenDatos();
    }

    /**
     * Se realiza la asignacion de la variable origenDatos por la
     * consulta correspondiente del formulario
     */
    @Override
    public void asignarOrigenDatos() {
        origenDatos = "";
    }

    // <METODOS_CARGAR_LISTA>
    /** Carga la lista del combo 'Codigo Inicial' */
    public void cargarListaCodigoInicial() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        ActadesuspensionControladorUrlEnum.URL12920
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.CICLO.getName(), ciclo);

        listaCodigoInicial = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, cCodigoRuta);
    }

    /** Carga la lista del combo 'Codigo Final' */
    public void cargarListaCodigoFinal() {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        ActadesuspensionControladorUrlEnum.URL12921
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.CICLO.getName(), ciclo);
        param.put(ActadesuspensionControladorEnum.CODIGOINICIAL.getValue(),
                        codigoInicial);

        listaCodigoFinal = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, cCodigoRuta);
    }

    /** Carga la lista del combo 'Ciclo' */
    public void cargarListaCiclo() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        ActadesuspensionControladorUrlEnum.URL12263
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        listaCiclo = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, "NUMERO");
    }

    /**
     * Carga la lista del combo 'Formato'. Tener presente que apunta
     * al esquema SYSMANK
     */
    public void cargarListaFormato() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        ActadesuspensionControladorUrlEnum.URL12924
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(ActadesuspensionControladorEnum.TIPO.getValue(), 28);

        listaFormato = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, cCodigo);
    }

    // </METODOS_CARGAR_LISTA>
    // <METODOS_CAMBIAR>
    /**
     * Metodo ejecutado al cambiar el control Pqr. Gestiona los
     * eventos del combo PQR
     */
    public void cambiarPqr() {
        // <CODIGO_DESARROLLADO>
        visibleSuperintendencia = visibleExcluirPqrTS && "0".equals(pqr);
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado al cambiar el control 'ConsecutivoAutomatico'
     */
    public void cambiarConsecutivoAutomatico() {
        // <CODIGO_DESARROLLADO>
        asignarConsecutivo();
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Busca el valor del ultimo consecutivo y le adiciona una unidad
     */
    private void asignarConsecutivo() {
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        try {
            List<Registro> reg = RegistroConverter
                            .toListRegistro(requestManager.getList(
                                            UrlServiceUtil.getInstance()
                                                            .getUrlServiceByUrlByEnumID(
                                                                            ActadesuspensionControladorUrlEnum.URL12925
                                                                                            .getValue())
                                                            .getUrl(),
                                            param));
            if (reg != null && reg.get(0) != null) {
                consecutivo = reg.get(0).getCampos().isEmpty() ? "0000000001"
                    : reg.get(0).getCampos().get("MAXI1").toString();
            }
        }
        catch (SystemException e) {
            Logger.getLogger(ActadesuspensionControlador.class.getName())
                            .log(Level.SEVERE, null, e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    // </METODOS_CAMBIAR>
    // <METODOS_COMBOS_GRANDES>
    /**
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaCodigoInicial asociada al combo 'Codigo Inicial'.
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaCodigoInicial(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        codigoInicial = registroAux.getCampos().get(cCodigoRuta).toString();

        cargarListaCodigoFinal();
    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaCodigoFinal asociada al combo 'Codigo Final'.
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaCodigoFinal(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        codigoFinal = registroAux.getCampos().get(cCodigoRuta).toString();
    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista listaCiclo
     * asociada al combo 'Ciclo'.
     * 
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaCiclo(SelectEvent event) {

        Registro registroAux = (Registro) event.getObject();
        ciclo = registroAux.getCampos().get("NUMERO").toString();

        codigoInicial = registroAux.getCampos().get("CODIGOINICIAL").toString();
        codigoFinal = registroAux.getCampos().get("CODIGOFINAL").toString();

        cargarListaCodigoInicial();
        cargarListaCodigoFinal();
        controlarVisibilidadInicial();
        cargarParametros();
    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaFormato asociada al combo 'Formato'.
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaFormato(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        formato = registroAux.getCampos().get(cCodigo).toString();
        fechaFormato = registroAux.getCampos().get("FECHA").toString();
    }

    // </METODOS_COMBOS_GRANDES>
    // <METODOS_ARBOL>
    // </METODOS_ARBOL>
    // <METODOS_BOTONES>
    /** Metodo ejecutado al oprimir el boton Imprimir en la vista */
    public void oprimirImprimir() {
        // <CODIGO_DESARROLLADO>
        asignarConsecutivo();

        try {
            if (registrarActa()) {
                generarPlantilla();
            }
        }
        catch (NamingException | SQLException | IllegalAccessException
                        | InstantiationException | ClassNotFoundException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
        // </CODIGO_DESARROLLADO>
    }

    private boolean registrarActa()
                    throws IllegalAccessException, InstantiationException,
                    ClassNotFoundException, SQLException, NamingException {
        boolean rta = false;
        try {
            rta = ejbServiciosPublicosDos.registrarActa(compania, consecutivo,
                            Integer.parseInt(ciclo), codigoInicial, codigoFinal,
                            Integer.parseInt(periodoAtrasoMenor),
                            Integer.parseInt(periodoAtrasoMayor),
                            new BigDecimal(deudaInicial),
                            new BigDecimal(deudaFinal), estado,
                            ckAbonos, Integer.parseInt(abonos),
                            Integer.parseInt(cbChapetas), Integer.parseInt(pqr),
                            SysmanFunciones
                                            .convertirAFechaCadena(
                                                            fechaEmision),
                            intendencia,
                            usuario);
        }
        catch (NumberFormatException | SystemException | ParseException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
        return rta;
    }

    /** Metodo ejecutado al oprimir el boton Postergado en la vista */
    public void oprimirPostergado() {
        // <CODIGO_DESARROLLADO>
        archivoDescarga = null;

        generarReporte(FORMATOS.PDF);
        // </CODIGO_DESARROLLADO>
    }

    // </METODOS_BOTONES>
    // <METODOS_SUBFORM>
    // </METODOS_SUBFORM>
    // <METODOS_ADICIONALES>

    private void generarReporte(FORMATOS auxFormato) {
        HashMap<String, Object> reemplazar = new HashMap<>();
        Map<String, Object> parametros = new HashMap<>();

        String reporte = "001418LFechaSuspension";

        // <REEMPLAZAR VARIABLES EN CONSULTA>
        reemplazar.put("fecha", formatearFechaEmision());
        reemplazar.put("ciclo", ciclo);
        reemplazar.put("codigoInicial", "'" + codigoInicial + "'");
        reemplazar.put("codigoFinal", "'" + codigoFinal + "'");
        // </REEMPLAZAR VARIABLES EN CONSULTA>

        // <ENVIAR PARAMETROS AL REPORTE>
        parametros.put("PR_CICLO", ciclo);

        try {
            parametros.put("PR_FECHAEMISION", SysmanFunciones
                            .convertirAFechaCadena(fechaEmision));
        }
        catch (ParseException e1) {
            logger.error(e1.getMessage(), e1);
            JsfUtil.agregarMensajeError(e1.getMessage());
        }

        parametros.put("PR_USER", SessionUtil.getUser().getCodigo());
        // </ENVIAR PARAMETROS AL REPORTE>

        Reporteador.resuelveConsulta(reporte, Integer.parseInt(modulo),
                        reemplazar, parametros);

        try {
            archivoDescarga = JsfUtil.exportarStreamed(reporte, parametros,
                            ConectorPool.ESQUEMA_SYSMAN, auxFormato);
        }
        catch (OutOfMemoryError | JRException | IOException
                        | SysmanException e) {
            logger.error(e.getMessage(), e);

            JsfUtil.agregarMensajeInformativo(
                            idioma.getString("MSM_TRANS_INTERRUMPIDA") + " "
                                + idioma.getString("MSM_INFORME_NO_EXISTE")
                                + " " + e.getMessage());
        }
    }

    /**
     * Filtra por el estado si el valor del mismo es diferente a T
     * (TODOS).
     * 
     * @return La condicion del estado.
     */
    private String condicionarEstado() {
        return "T".equals(estado) ? "'A','S','C','R'"
            : " '" + estado + "' ";
    }

    /**
     * Realiza el casting para que la fechaFormato incluya el TO_DATE.
     * 
     * @return La fechaFormato con el TO_DATE
     */
    private String formatearFechaFormato() {
        Date miFecha = null;

        try {
            miFecha = SysmanFunciones.convertirAFecha(fechaFormato);
        }
        catch (ParseException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

        return SysmanFunciones.formatearFecha(miFecha);
    }

    /** Genera un informe con una determinada plantilla. */
    private void generarPlantilla() {
        if (formato != null) {
            String[] campos = new String[3];
            campos[0] = "codigoPlantilla";
            campos[1] = "fechaPlantilla";
            campos[2] = "nombreDocDescarga";

            String[] valores = new String[3];
            valores[0] = formato;
            valores[1] = formatearFechaFormato();
            valores[2] = idioma.getString("TB_TB2950").replace("#NUMACTA#",
                            consecutivo);

            HashMap<String, Object> reemplazar = new HashMap<>();
            reemplazar.put("s$compania$s", "'" + compania + "'");
            reemplazar.put("s$ciclo$s", ciclo);
            reemplazar.put("s$codigoInicial$s", "'" + codigoInicial + "'");
            reemplazar.put("s$codigoFinal$s", "'" + codigoFinal + "'");
            reemplazar.put("s$perAtrasoInicial$s", periodoAtrasoMenor);
            reemplazar.put("s$perAtrasoFinal$s", periodoAtrasoMayor);
            reemplazar.put("s$periodosAtraso$s", parPeriodosAtraso);
            reemplazar.put("s$tituloActa$s", "'" + parTituloActa + "'");
            reemplazar.put("s$notaInvalidar$s", "'" + parNotaInvalidar + "'");
            reemplazar.put("s$actaSuspencion$s", "'" + parActaSuspension + "'");
            reemplazar.put("s$deudaInicial$s", deudaInicial);
            reemplazar.put("s$deudaFinal$s", deudaFinal);
            reemplazar.put("s$condEstado$s", condicionarEstado());
            reemplazar.put("s$motivo$s", "'" + motivo + "'");

            /* Reemplazos de la consulta asociada a la plantilla */
            SessionUtil.setSessionVar("variablesConsultaWord",
                            reemplazar);

            SessionUtil.cargarModalDatosFlash(Integer
                            .toString(GeneralCodigoFormaEnum.IMPRIMIRWORDS_CONTROLADOR
                                            .getCodigo()),
                            SessionUtil.getModulo(), campos,
                            valores);
        }
    }

    /**
     * Hace el casting para que la fecha de emision incluya el TO_DATE
     * 
     * @return La fecha de emision formateada.
     */
    private String formatearFechaEmision() {
        return SysmanFunciones.formatearFecha(fechaEmision);
    }

    /** Metodo que asigna valor al campo 'Motivo' en la vista. */
    private void asignarMotivo() {
        try {
            motivo = ejbSysmanUtil.consultarParametro(compania,
                            "MOTIVO ACTA DE SUSPENSION", modulo, new Date(),
                            true);
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    /**
     * Gestiona los controles que se deben cargar y dependen de un
     * parametro.
     */
    public void controlarVisibilidadInicial() {
        try {

            String par = ejbSysmanUtil.consultarParametro(compania,
                            "MANEJA AMPLIA PLAZO DE SUSPENSION", modulo,
                            new Date(), true);

            verificarParametro(par, "MANEJA AMPLIA PLAZO DE SUSPENSION");

            /*- Controla la visibilidad del boton 'PostergadoVencido'.*/
            visiblePostergado = "SI".equals(SysmanFunciones.nvlStr(par, "NO"));

            par = ejbSysmanUtil.consultarParametro(compania,
                            "EXCLUIR PQR Y FINANCIABLES DEUDA PAGAS", modulo,
                            new Date(), true);

            verificarParametro(par, "EXCLUIR PQR Y FINANCIABLES DEUDA PAGAS");

            visibleExcluirPqr = "SI".equals(SysmanFunciones.nvlStr(par, "NO"));
            par = ejbSysmanUtil.consultarParametro(compania,
                            "EXCLUIR PQR TRASLADADAS A SUPERINTENDENCIA",
                            modulo, new Date(), true);

            verificarParametro(par,
                            "EXCLUIR PQR TRASLADADAS A SUPERINTENDENCIA");

            visibleExcluirPqrTS = "SI"
                            .equals(SysmanFunciones.nvlStr(par, "NO"));

        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    private void verificarParametro(String parametro, String nomPar) {
        if (SysmanFunciones.validarVariableVacio(parametro)) {
            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB2814")
                            .replace("#parameter#", nomPar));
        }
    }

    /**
     * Asigna a los atributos con prefijo 'par' el valor de su
     * respectivo parametro.
     */
    private void cargarParametros() {
        try {
            parPeriodosAtraso = SysmanFunciones
                            .nvlStr(ejbSysmanUtil.consultarParametro(compania,
                                            "PERIODOS DE ATRASO MODIFICA TIT. ACTA SUSPENSION",
                                            modulo,
                                            new Date(), true), "0");

            verificarParametro(parPeriodosAtraso,
                            "PERIODOS DE ATRASO MODIFICA TIT. ACTA SUSPENSION");

            parTituloActa = ejbSysmanUtil.consultarParametro(compania,
                            "TITULO ACTA DE SUSPENSION", modulo,
                            new Date(), true);

            verificarParametro(parTituloActa, "TITULO ACTA DE SUSPENSION");

            parNotaInvalidar = ejbSysmanUtil.consultarParametro(compania,
                            "NOTA INVALIDAR ACTAS SUSPENSION ANTERIORES",
                            modulo,
                            new Date(), true);

            verificarParametro(parNotaInvalidar,
                            "NOTA INVALIDAR ACTAS SUSPENSION ANTERIORES");

            parNotaInvalidar = SysmanFunciones
                            .nvlStr(ejbSysmanUtil.consultarParametro(compania,
                                            "ACTAS DE SUSPENSION SIN FACTURADO ACTUAL",
                                            modulo,
                                            new Date(), true), "NO");

            verificarParametro(parNotaInvalidar,
                            "ACTAS DE SUSPENSION SIN FACTURADO ACTUAL");
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    // </METODOS_ADICIONALES>
    /**
     * Este metodo es invocado el metodo inicializar, se ejecutan las
     * acciones a tener en cuenta en el momento de apertura del
     * formulario
     */
    @Override
    public void abrirFormulario() {
        // <CODIGO_DESARROLLADO>
        fechaEmision = new Date();

        asignarMotivo();

        periodoAtrasoMenor = "0";
        periodoAtrasoMayor = "99999";
        deudaInicial = "0";
        deudaFinal = "999999999.99";
        abonos = "-1";
        cbChapetas = "3";
        pqr = "-1";
        intendencia = "S";
        ckAbonos = consecutivoAutomatico = true;
        estado = "A";
        consecutivo = "0";
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado en el momento despues de cargar el registro
     */
    @Override
    public void cargarRegistro() {
        // <CODIGO_DESARROLLADO>
        precargarRegistro();
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado antes de realizar la insercion del registro
     * 
     * @return
     */
    @Override
    public boolean insertarAntes() {
        // <CODIGO_DESARROLLADO>
        registro.getCampos().put("COMPANIA", compania);
        // </CODIGO_DESARROLLADO>
        return true;
    }

    /**
     * Metodo ejecutado despues de realizar la insercion del registro
     * 
     * @return
     */
    @Override
    public boolean insertarDespues() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
        return true;
    }

    /**
     * Metodo ejecutado antes de realizar la insercion y actualizacion
     * del registro
     * 
     * 
     * @return
     */
    @Override
    public boolean actualizarAntes() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
        return true;
    }

    /**
     * Metodo ejecutado despues de realizar la insercion y
     * actualizacion del registro
     * 
     * 
     * @return
     */
    @Override
    public boolean actualizarDespues() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
        return true;
    }

    /**
     * Metodo ejecutado antes de realizar la eliminacion del registro
     * 
     * 
     * @return
     */
    @Override
    public boolean eliminarAntes() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
        return true;
    }

    /**
     * Metodo ejecutado despues de realizar la eliminacion del
     * registro
     * 
     * @return
     */
    @Override
    public boolean eliminarDespues() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
        return true;
    }

    // <SET_GET_ATRIBUTOS>
    public boolean isConsecutivoAutomatico() {
        return consecutivoAutomatico;
    }

    public void setConsecutivoAutomatico(boolean consecutivoAutomatico) {
        this.consecutivoAutomatico = consecutivoAutomatico;
    }

    public boolean isCkAbonos() {
        return ckAbonos;
    }

    public void setCkAbonos(boolean ckAbonos) {
        this.ckAbonos = ckAbonos;
    }

    /**
     * Retorna la variable codigoInicial
     * 
     * @return codigoInicial
     */
    public String getCodigoInicial() {
        return codigoInicial;
    }

    /**
     * Asigna la variable codigoInicial
     * 
     * @param codigoInicial
     * Variable a asignar en codigoInicial
     */
    public void setCodigoInicial(String codigoInicial) {
        this.codigoInicial = codigoInicial;
    }

    /**
     * Retorna la variable codigoFinal
     * 
     * @return codigoFinal
     */
    public String getCodigoFinal() {
        return codigoFinal;
    }

    /**
     * Asigna la variable codigoFinal
     * 
     * @param codigoFinal
     * Variable a asignar en codigoFinal
     */
    public void setCodigoFinal(String codigoFinal) {
        this.codigoFinal = codigoFinal;
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
     * Retorna la variable formato
     * 
     * @return formato
     */
    public String getFormato() {
        return formato;
    }

    /**
     * Asigna la variable formato
     * 
     * @param formato
     * Variable a asignar en formato
     */
    public void setFormato(String formato) {
        this.formato = formato;
    }

    /**
     * Retorna la variable estado
     * 
     * @return estado
     */
    public String getEstado() {
        return estado;
    }

    /**
     * Asigna la variable estado
     * 
     * @param estado
     * Variable a asignar en estado
     */
    public void setEstado(String estado) {
        this.estado = estado;
    }

    /**
     * Retorna la variable formateado
     * 
     * @return formateado
     */
    public String getFormateado() {
        return formateado;
    }

    /**
     * Asigna la variable formateado
     * 
     * @param formateado
     * Variable a asignar en formateado
     */
    public void setFormateado(String formateado) {
        this.formateado = formateado;
    }

    /**
     * Retorna la variable cbChapetas
     * 
     * @return cbChapetas
     */
    public String getCbChapetas() {
        return cbChapetas;
    }

    /**
     * Asigna la variable cbChapetas
     * 
     * @param cbChapetas
     * Variable a asignar en cbChapetas
     */
    public void setCbChapetas(String cbChapetas) {
        this.cbChapetas = cbChapetas;
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
     * Retorna la variable intendencia
     * 
     * @return intendencia
     */
    public String getIntendencia() {
        return intendencia;
    }

    /**
     * Asigna la variable intendencia
     * 
     * @param intendencia
     * Variable a asignar en intendencia
     */
    public void setIntendencia(String intendencia) {
        this.intendencia = intendencia;
    }

    /**
     * Retorna la variable consecutivo
     * 
     * @return consecutivo
     */
    public String getConsecutivo() {
        return consecutivo;
    }

    /**
     * Asigna la variable consecutivo
     * 
     * @param consecutivo
     * Variable a asignar en consecutivo
     */
    public void setConsecutivo(String consecutivo) {
        this.consecutivo = consecutivo;
    }

    public Date getFechaEmision() {
        return fechaEmision;
    }

    public void setFechaEmision(Date fechaEmision) {
        this.fechaEmision = fechaEmision;
    }

    /**
     * Retorna la variable periodoAtrasoMenor
     * 
     * @return periodoAtrasoMenor
     */
    public String getPeriodoAtrasoMenor() {
        return periodoAtrasoMenor;
    }

    /**
     * Asigna la variable periodoAtrasoMenor
     * 
     * @param periodoAtrasoMenor
     * Variable a asignar en periodoAtrasoMenor
     */
    public void setPeriodoAtrasoMenor(String periodoAtrasoMenor) {
        this.periodoAtrasoMenor = periodoAtrasoMenor;
    }

    /**
     * Retorna la variable deudaInicial
     * 
     * @return deudaInicial
     */
    public String getDeudaInicial() {
        return deudaInicial;
    }

    /**
     * Asigna la variable deudaInicial
     * 
     * @param deudaInicial
     * Variable a asignar en deudaInicial
     */
    public void setDeudaInicial(String deudaInicial) {
        this.deudaInicial = deudaInicial;
    }

    public String getDeudaFinal() {
        return deudaFinal;
    }

    public void setDeudaFinal(String deudaFinal) {
        this.deudaFinal = deudaFinal;
    }

    /**
     * Retorna la variable periodoAtrasoMayor
     * 
     * @return periodoAtrasoMayor
     */
    public String getPeriodoAtrasoMayor() {
        return periodoAtrasoMayor;
    }

    /**
     * Asigna la variable periodoAtrasoMayor
     * 
     * @param periodoAtrasoMayor
     * Variable a asignar en periodoAtrasoMayor
     */
    public void setPeriodoAtrasoMayor(String periodoAtrasoMayor) {
        this.periodoAtrasoMayor = periodoAtrasoMayor;
    }

    /**
     * Retorna la variable motivo
     * 
     * @return motivo
     */
    public String getMotivo() {
        return motivo;
    }

    /**
     * Asigna la variable motivo
     * 
     * @param motivo
     * Variable a asignar en motivo
     */
    public void setMotivo(String motivo) {
        this.motivo = motivo;
    }

    public boolean isVisiblePostergado() {
        return visiblePostergado;
    }

    public void setVisiblePostergado(boolean visiblePostergado) {
        this.visiblePostergado = visiblePostergado;
    }

    public boolean isVisibleExcluirPqr() {
        return visibleExcluirPqr;
    }

    public void setVisibleExcluirPqr(boolean visibleExcluirPqr) {
        this.visibleExcluirPqr = visibleExcluirPqr;
    }

    public boolean isVisibleExcluirPqrTS() {
        return visibleExcluirPqrTS;
    }

    public void setVisibleExcluirPqrTS(boolean visibleExcluirPqrTS) {
        this.visibleExcluirPqrTS = visibleExcluirPqrTS;
    }

    public boolean isVisibleSuperintendencia() {
        return visibleSuperintendencia;
    }

    public void setVisibleSuperintendencia(boolean visibleSuperintendencia) {
        this.visibleSuperintendencia = visibleSuperintendencia;
    }

    public StreamedContent getArchivoDescarga() {
        return archivoDescarga;
    }

    public void setArchivoDescarga(StreamedContent archivoDescarga) {
        this.archivoDescarga = archivoDescarga;
    }

    // </SET_GET_ATRIBUTOS>
    // <SET_GET_LISTAS>
    // </SET_GET_LISTAS>
    // <SET_GET_LISTAS_COMBO_GRANDE>
    /**
     * Retorna la lista listaCodigoInicial
     * 
     * @return listaCodigoInicial
     */
    public RegistroDataModelImpl getListaCodigoInicial() {
        return listaCodigoInicial;
    }

    /**
     * Asigna la lista listaCodigoInicial
     * 
     * @param listaCodigoInicial
     * Variable a asignar en listaCodigoInicial
     */
    public void setListaCodigoInicial(
        RegistroDataModelImpl listaCodigoInicial) {
        this.listaCodigoInicial = listaCodigoInicial;
    }

    /**
     * Retorna la lista listaCodigoFinal
     * 
     * @return listaCodigoFinal
     */
    public RegistroDataModelImpl getListaCodigoFinal() {
        return listaCodigoFinal;
    }

    /**
     * Asigna la lista listaCodigoFinal
     * 
     * @param listaCodigoFinal
     * Variable a asignar en listaCodigoFinal
     */
    public void setListaCodigoFinal(RegistroDataModelImpl listaCodigoFinal) {
        this.listaCodigoFinal = listaCodigoFinal;
    }

    /**
     * Retorna la lista listaCiclo
     * 
     * @return listaCiclo
     */
    public RegistroDataModelImpl getListaCiclo() {
        return listaCiclo;
    }

    /**
     * Asigna la lista listaCiclo
     * 
     * @param listaCiclo
     * Variable a asignar en listaCiclo
     */
    public void setListaCiclo(RegistroDataModelImpl listaCiclo) {
        this.listaCiclo = listaCiclo;
    }

    /**
     * Retorna la lista listaFormateado
     * 
     * @return listaFormateado
     */
    public RegistroDataModelImpl getListaFormateado() {
        return listaFormateado;
    }

    /**
     * Asigna la lista listaFormateado
     * 
     * @param listaFormateado
     * Variable a asignar en listaFormateado
     */
    public void setListaFormateado(RegistroDataModelImpl listaFormateado) {
        this.listaFormateado = listaFormateado;
    }

    public RegistroDataModelImpl getListaFormato() {
        return listaFormato;
    }

    public void setListaFormato(RegistroDataModelImpl listaFormato) {
        this.listaFormato = listaFormato;
    }
    // </SET_GET_LISTAS_COMBO_GRANDE>
    // <SET_GET_LISTAS_SUBFORM>
    // </SET_GET_LISTAS_SUBFORM>
    // <SET_GET_PARAMETROS>
    // </SET_GET_PARAMETROS>
    // <SET_GET_ADICIONALES>
    // </SET_GET_ADICIONALES>
}
