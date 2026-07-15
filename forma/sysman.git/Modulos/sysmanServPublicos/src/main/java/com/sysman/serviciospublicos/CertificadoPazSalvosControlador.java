/*-
 * CertificadoPazSalvosControlador.java
 *
 * 1.0
 * 
 * 02/02/2017
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
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.recursos.ejb.EjbSysmanUtilRemote;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.serviciospublicos.ejb.EjbServiciosPublicosTresRemote;
import com.sysman.serviciospublicos.enums.CertificadoPazSalvosControladorUrlEnum;
import com.sysman.util.SysmanFunciones;

import java.math.BigDecimal;
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

/**
 * Esta clase es el controlador para el formulario que genera los
 * Certificados de Paz y Salvo en Access "PazYSalvo", el cual es
 * llamado desde Facturacion\Suscriptores\Correspondencia\Paz Y Salvo
 *
 * @version 1.0, 02/02/2017
 * @author amonroy
 * @modified jguerrero
 * @version 2. 25/05/2017 Se realizo el refactory de las consultas sql
 * en el controlador. Además se ajustaron los errores del sonar
 */
@ManagedBean
@ViewScoped

public class CertificadoPazSalvosControlador extends BeanBaseDatosAcmeImpl {
    /**
     * Constante a nivel de clase que almacena el codigo de la
     * compania en la cual inicio sesion el usuario, el valor de esta
     * constante es asignado en el constructor a la variable de sesion
     * correspondiente
     */
    private final String compania;
    /**
     * Constante definida por el numero de veces que se realiza el
     * llamado al campo "CODIGORUTA" en el formulario, almacena el
     * texto CODIGORUTA
     */
    private final String cCodigoRuta;
    /**
     * Constante definida por el numero de veces que se realiza el
     * llamado al campo "IDACTA" en el formulario, almacena el texto
     * IDACTA
     */
    private final String cIdActa;
    // <DECLARAR_ATRIBUTOS>
    /**
     * Atributo que almacena el nombre del usuario que pertenece al
     * codigo de ruta selecciondo
     */
    private String nombre;
    /**
     * Atributo que almacena el código del formato seleccionado en el
     * formulario
     */
    private String codigoFormato;
    // </DECLARAR_ATRIBUTOS>
    // <DECLARAR_LISTAS>
    /**
     * Listado de registros para el comboBox en el que se selecciona
     * el formato del documento a generar
     */
    private List<Registro> listaDocumento;
    /**
     * Listado de registros para el comboBox en el que se selecciona
     * el ciclo
     */
    private List<Registro> listaCiclo;

    private boolean mostrarUpdateDelete;

    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>
    /**
     * Listado de registros para el comboBox de en el que seselecciona
     * el código de Ruta del usuario a generar el paz y salvo
     */
    private RegistroDataModelImpl listaCodigoRuta;

    @EJB
    private EjbSysmanUtilRemote ejbSysmanUtil;

    @EJB
    private EjbServiciosPublicosTresRemote ejbServPublTres;
    private final String nombreCons;

    // </DECLARAR_LISTAS_COMBO_GRANDE>
    // <DECLARAR_LISTAS_SUBFORM>
    // </DECLARAR_LISTAS_SUBFORM>
    // <DECLARAR_PARAMETROS>
    // </DECLARAR_PARAMETROS>
    // <DECLARAR_ADICIONALES>
    // </DECLARAR_ADICIONALES>
    /**
     * Crea una nueva instancia de CertificadoPazSalvosControlador
     */
    public CertificadoPazSalvosControlador() {
        super();
        compania = SessionUtil.getCompania();
        cCodigoRuta = "CODIGORUTA";
        cIdActa = "IDACTA";
        nombreCons = "NOMBRE";
        try {
            numFormulario = GeneralCodigoFormaEnum.CERTIFICADO_PAZ_SALVOS_CONTROLADOR
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
        cargarListaCiclo();

        // </CARGAR_LISTA_COMBO_GRANDE>
        // <CARGAR_LISTA>
        cargarListaDocumento();

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
        tabla = "SP_CERTIFICADOSESTRATIFICACION";
        buscarLlave();
        asignarOrigenDatos();

    }

    /**
     * Se realiza la asignacion de la variable origenDatos por la
     * consulta correspondiente del formulario
     * 
     */
    @Override
    public void asignarOrigenDatos() {

        parametrosListado.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);

        urlListado = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        CertificadoPazSalvosControladorUrlEnum.URL18557
                                                        .getValue());

        urlCreacion = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        CertificadoPazSalvosControladorUrlEnum.URL18554
                                                        .getValue());
        urlActualizacion = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        CertificadoPazSalvosControladorUrlEnum.URL18555
                                                        .getValue());
        urlEliminacion = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        CertificadoPazSalvosControladorUrlEnum.URL18556
                                                        .getValue());
        urlLectura = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        CertificadoPazSalvosControladorUrlEnum.URL18558
                                                        .getValue());

    }

    // <METODOS_CARGAR_LISTA>
    /**
     * 
     * Carga la lista listaCiclo, que contiene los cicos diponibles
     *
     */
    public void cargarListaCiclo() {

        // 214026
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        try {
            listaCiclo = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            CertificadoPazSalvosControladorUrlEnum.URL7846
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
     * Carga la lista listaDocumento, que define los formatos
     * disponibles para la generacion del documento
     */
    public void cargarListaDocumento() {
        // 104021

        Map<String, Object> param = new TreeMap<>();
        param.put("TIPO", 29);
        try {
            listaDocumento = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            CertificadoPazSalvosControladorUrlEnum.URL8507
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
     * Carga la lista listaCodigoRuta, que permite la visualización de
     * los usuarios registrados
     *
     */
    public void cargarListaCodigoRuta() {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        CertificadoPazSalvosControladorUrlEnum.URL18559
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.CICLO.getName(),
                        registro.getCampos().get("CICLO"));

        listaCodigoRuta = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, cCodigoRuta);

    }

    // </METODOS_CARGAR_LISTA>
    // <METODOS_CAMBIAR>
    /**
     * Metodo ejecutado al cambiar el control Ciclo Reinicia el valor
     * de los campos Codigoruta, Valido hasta y Nombre, en el que se
     * visualiza el nombre del codigo ruta seleccionado.
     * 
     */
    public void cambiarCiclo() {
        // <CODIGO_DESARROLLADO>
        cargarListaCodigoRuta();

        registro.getCampos().put(nombreCons, null);
        registro.getCampos().put(cCodigoRuta, null);
        registro.getCampos().put("VALIDOHASTA", null);
        // </CODIGO_DESARROLLADO>
    }

    // </METODOS_CAMBIAR>
    // <METODOS_COMBOS_GRANDES>
    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaCodigoRuta
     *
     * Actualiza el valor del campo Nombre, adicionalmente calcula el
     * valor del campo válido hasta
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaCodigoRuta(SelectEvent event)
                    throws IllegalAccessException {
        try {
            Registro registroAux = (Registro) event.getObject();
            registro.getCampos().put(cCodigoRuta,
                            registroAux.getCampos().get(cCodigoRuta));

            registro.getCampos().put(nombreCons,
                            registroAux.getCampos().get(nombreCons));

            String fecha = SysmanFunciones.formatearFecha(
                            (Date) registroAux.getCampos().get("FECHAPYS"));

            Date validoHasta;
            if (fecha == null) {
                validoHasta = SysmanFunciones.convertirAFecha("01/01/1900");
            }
            else {
                validoHasta = ejbServPublTres.ultimoDia(
                                (Date) registroAux.getCampos().get("FECHAPYS"));

            }
            registro.getCampos().put("VALIDOHASTA", validoHasta);
        }
        catch (ParseException | SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    // </METODOS_COMBOS_GRANDES>
    // <METODOS_ARBOL>
    /**
     * Valida que no se haya generado previamente el certificado para
     * un codigo de ruta especificado
     * 
     * @param codigoruta
     * codigo que se desea consultar
     */
    private boolean verificarExistencia(String codigoruta) {

        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.CODIGORUTA.getName(), codigoruta);

        try {
            Registro rs = RegistroConverter.toRegistro(
                            requestManager.get(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            CertificadoPazSalvosControladorUrlEnum.URL18560
                                                                            .getValue())
                                            .getUrl(), param));

            if (Integer.parseInt(
                            rs.getCampos().get("CANTIDAD").toString()) > 0) {
                JsfUtil.agregarMensajeError(idioma.getString("TB_TB2791"));
                return true;
            }

        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
        return false;
    }

    /**
     * Realiza el paso de un numero que ingresa por parametro a letras
     * 
     * @param dia
     * día que se desea convertir
     * @return el numero ingresado en letras
     */
    private String convertirDias(int dia) {
        String diaEnLetras = "";
        try {

            diaEnLetras = ejbSysmanUtil.convetirValorEnLetras(
                            BigDecimal.valueOf(dia), false);
            diaEnLetras = diaEnLetras.replace(" PESOS MC.", "");
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

        return diaEnLetras;
    }

    // </METODOS_ARBOL>
    // <METODOS_BOTONES>
    /**
     * 
     * Metodo ejecutado al oprimir el boton CmdPreview en la vista
     *
     * Si existe el mismo formato con fechas diferentes, selecciona el
     * de la fecha más reciente, partiendo de la fecha definida en el
     * formulario
     * 
     * Define el nombre del archivo y envía los valores de reemplazo
     * para la consulta
     *
     */
    public void oprimirCmdPreview() {
        // <CODIGO_DESARROLLADO>
        try

        {
            if (codigoFormato == null) {
                JsfUtil.agregarMensajeError(idioma.getString("TB_TB1884"));
                return;
            }

            if ("i".equals(accion) && SysmanFunciones
                            .validarCampoVacio(registro.getCampos(), cIdActa)) {
                JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB2934"));
                return;
            }

            // Selecciona el formato mas reciente
            Date fechaGeneracion = (Date) registro.getCampos().get("FECHAACTA");

            Map<String, Object> param = new TreeMap<>();
            param.put("CODIGOFORMATO", codigoFormato);
            param.put("FECHACREACION", fechaGeneracion);

            Registro rs = RegistroConverter.toRegistro(
                            requestManager.get(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            CertificadoPazSalvosControladorUrlEnum.URL18561
                                                                            .getValue())
                                            .getUrl(), param));

            if (rs == null) {
                JsfUtil.agregarMensajeError(idioma.getString("TB_TB2794"));
                return;
            }

            Date fecha = (Date) rs.getCampos().get("FECHA");
            String strNombreDocumento = idioma.getString("TB_TB2792");
            strNombreDocumento = strNombreDocumento.replace("s$codigoruta$s",
                            registro.getCampos().get(cCodigoRuta).toString());

            strNombreDocumento = strNombreDocumento.replace("s$fecha$s",
                            SysmanFunciones
                                            .convertirAFechaCadena(new Date())
                                            .replace("/", ""));
            strNombreDocumento = strNombreDocumento.replace("s$hora$s",
                            SysmanFunciones
                                            .convertirAHoraCadena(new Date())
                                            .replace(":", ""));
            String dia = convertirDias(SysmanFunciones.dia(fechaGeneracion));

            String[] campos = new String[3];
            String[] valores = new String[3];
            campos[0] = "codigoPlantilla";
            campos[1] = "fechaPlantilla";
            campos[2] = "nombreDocDescarga";

            valores[0] = codigoFormato;
            valores[1] = SysmanFunciones.formatearFecha(fecha);
            valores[2] = strNombreDocumento;

            HashMap<String, String> variablesConsultaW = new HashMap<>();
            variablesConsultaW.put("s$compania$s", "'" + compania + "'");
            variablesConsultaW.put("s$ciclo$s",
                            "'" + registro.getCampos().get("CICLO") + "'");
            variablesConsultaW.put("s$codigoRuta$s",
                            "'" + registro.getCampos().get(cCodigoRuta) + "'");
            variablesConsultaW.put("s$idacta$s",
                            "'" + registro.getCampos().get(cIdActa) + "'");
            variablesConsultaW.put("s$diaLetras$s", "'" + dia + "'");

            // variables por parametro para documento word
            SessionUtil.setSessionVar("variablesConsultaWord",
                            variablesConsultaW);

            SessionUtil.cargarModalDatosFlash("281", SessionUtil.getModulo(),
                            campos,
                            valores);

            registro.getCampos().put("IMPRESO", true);
            agregarRegistroNuevo(false);

            cargarRegistro(registro.getLlave(), accion, registro.getIndice());

            accion = ACCION_VER;

        }
        catch (

                        ParseException | SystemException e)

        {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

        // </CODIGO_DESARROLLADO>
    }

    // </METODOS_BOTONES>
    // <METODOS_SUBFORM>
    // </METODOS_SUBFORM>
    // <METODOS_ADICIONALES>
    // </METODOS_ADICIONALES>
    /**
     * Este metodo es invocado el metodo inicializar, se ejecutan las
     * acciones a tener en cuenta en el momento de apertura del
     * formulario
     */
    @Override
    public void abrirFormulario() {
        // <CODIGO_DESARROLLADO>
        /*
         * FR1283-AL_ABRIR Private Sub Form_Open(Cancel As Integer)
         * DoCmd.Restore formularioAbrir 74, Me.Name End Sub
         */
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado en el momento despues de cargar el registro
     * 
     * Realiza la consulta del nombre de usuario al código ruta que ha
     * sido registrado previamente
     */
    @Override
    public void cargarRegistro() {
        // <CODIGO_DESARROLLADO>

        precargarRegistro();
        cargarListaCodigoRuta();
    }

    /**
     * Metodo ejecutado antes de realizar la insercion del registro
     * Calcula el numero consecutivo para el acta
     * 
     * @return la confirmación para realizar la inserción
     */
    @Override
    public boolean insertarAntes() {
        // <CODIGO_DESARROLLADO>
        registro.getCampos().put("COMPANIA", compania);

        if (verificarExistencia(
                        registro.getCampos().get(cCodigoRuta).toString())) {
            return false;
        }

        long idActa = 1;
        try {

            String condicion = " COMPANIA = ''" + compania + "''" +
                " AND CLASE      = ''29''";
            idActa = ejbSysmanUtil.generarConsecutivoConValorInicial(
                            "SP_CERTIFICADOSESTRATIFICACION", condicion,
                            cIdActa, "1");

            registro.getCampos().put(cIdActa, idActa);
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

        registro.getCampos().remove(nombreCons);

        // </CODIGO_DESARROLLADO>
        return true;
    }

    /**
     * Metodo ejecutado despues de realizar la insercion del registro
     * 
     * @return un boolean de acuerdo a las acciones definidas dentro
     * del metodo
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
     * Asigna el valor del campo clase en 29, que identifica la clase
     * "Paz y Salvo"
     * 
     * @return un boolean de acuerdo a las acciones definidas dentro
     * del metodo
     */
    @Override
    public boolean actualizarAntes() {
        // <CODIGO_DESARROLLADO>
        registro.getCampos().put("CLASE", "29");

        if (accion.equals(ACCION_MODIFICAR)) {
            registro.getCampos()
                            .remove(GeneralParameterEnum.COMPANIA.getName());
            registro.getCampos().remove("IDACTA");
            registro.getCampos().remove("CLASE");
            registro.getCampos().remove(nombreCons);
        }

        // </CODIGO_DESARROLLADO>
        return true;
    }

    /**
     * Metodo ejecutado despues de realizar la insercion y
     * actualizacion del registro
     * 
     * 
     * @return un boolean de acuerdo a las acciones definidas dentro
     * del metodo
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
     * @return un boolean de acuerdo a las acciones definidas dentro
     * del metodo
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
     * @return un boolean de acuerdo a las acciones definidas dentro
     * del metodo
     */
    @Override
    public boolean eliminarDespues() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
        return true;
    }

    // <SET_GET_ATRIBUTOS>
    /**
     * Retorna el valor del atributo nombre
     * 
     * @return nombre del usuario
     */
    public String getNombre() {
        return nombre;
    }

    /**
     * Asigna el valor al atributo Nombre
     * 
     * @param nombre
     * que se desea almacenar
     */
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    /**
     * Retorna el valor del atributo codigoFormato
     * 
     * @return codigoFormato
     */
    public String getCodigoFormato() {
        return codigoFormato;
    }

    /**
     * Asigna el valor al atributo codigoFormato
     * 
     * @param codigoFormato
     */
    public void setCodigoFormato(String codigoFormato) {
        this.codigoFormato = codigoFormato;
    }

    // </SET_GET_ATRIBUTOS>
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
     * Retorna la lista listaDocumento
     * 
     * @return listaDocumento
     */
    public List<Registro> getListaDocumento() {
        return listaDocumento;
    }

    /**
     * Asigna la lista listaDocumento
     * 
     * @param listaDocumento
     * Variable a asignar en listaDocumento
     */
    public void setListaDocumento(List<Registro> listaDocumento) {
        this.listaDocumento = listaDocumento;
    }

    // </SET_GET_LISTAS>
    // <SET_GET_LISTAS_COMBO_GRANDE>
    /**
     * Retorna la lista listaCodigoRuta
     * 
     * @return listaCodigoRuta
     */
    public RegistroDataModelImpl getListaCodigoRuta() {
        return listaCodigoRuta;
    }

    /**
     * Asigna la lista listaCodigoRuta
     * 
     * @param listaCodigoRuta
     * Variable a asignar en listaCodigoRuta
     */
    public void setListaCodigoRuta(RegistroDataModelImpl listaCodigoRuta) {
        this.listaCodigoRuta = listaCodigoRuta;
    }

    public boolean isMostrarUpdateDelete() {
        return mostrarUpdateDelete;
    }

    public void setMostrarUpdateDelete(boolean mostrarUpdateDelete) {
        this.mostrarUpdateDelete = mostrarUpdateDelete;
    }

    // </SET_GET_LISTAS_COMBO_GRANDE>
    // <SET_GET_LISTAS_SUBFORM>
    // </SET_GET_LISTAS_SUBFORM>
    // <SET_GET_PARAMETROS>
    // </SET_GET_PARAMETROS>
    // <SET_GET_ADICIONALES>
    // </SET_GET_ADICIONALES>
}