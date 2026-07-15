/*-
 * Aperturainscritos1sControlador.java
 *
 * 1.0
 * 
 * 30/01/2018
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */
package com.sysman.hojasdevida;

import com.sysman.beanbase.BeanBaseContinuoAcmeImpl;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.email.ApiRestClient;
import com.sysman.email.EmailPojo;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.enums.GenericUrlEnum;
import com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException;
import com.sysman.exception.SystemException;
import com.sysman.hojasdevida.ejb.EjbHojasDeVidaCeroRemote;
import com.sysman.hojasdevida.enums.Aperturainscritos1sControladorEnum;
import com.sysman.hojasdevida.enums.Aperturainscritos1sControladorUrlEnum;
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
import com.sysman.util.SysmanConstantes;
import com.sysman.util.SysmanFunciones;

import java.io.IOException;
import java.text.ParseException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.event.RowEditEvent;
import org.primefaces.event.SelectEvent;
import org.primefaces.model.StreamedContent;

import net.sf.jasperreports.engine.JRException;

/**
 * Clase encargada de gestionar la apertura de inscritos en la seleccion del personal
 *
 * @version 1.0, 30/01/2018
 * @author jeguerrero
 * @version 2, 21/08/2018 jgomezp Se agrega validacion de registro de duplicidad
 */
@ManagedBean
@ViewScoped
public class Aperturainscritos1sControlador extends BeanBaseContinuoAcmeImpl {
    /**
     * Constante a nivel de clase que almacena el codigo de la compania en la cual inicio sesion el usuario, el valor de esta constante es asignado en el constructor a la variable de sesion
     * correspondiente
     */
    private final String compania;
    // <DECLARAR_ATRIBUTOS>
    /**
     * variable encargada de almacenar temporalmente el numero de convatoria seleccioanda en la interfaz grafica .
     */
    private String convocatoria;
    /**
     * variable encargada de almacenar temporalmente la fecha de convatoria seleccioanda en la interfaz grafica .
     */
    private String fechaConv;
    /**
     * variable encargada de almacenar temporalmente el nombre del cargo de la convatoria seleccioanda en la interfaz grafica .
     */
    private String nombreCargo;
    /**
     * variable encargada de almacenar temporalmente el nombre del tercero seleccioanda en la interfaz grafica .
     */

    private String nombreTer;
    /**
     * variable encargada de almacenar temporalmente el telefono del tercero seleccioanda en la interfaz grafica .
     */
    private String telefono;
    /**
     * variable encargada de almacenar temporalmente la direccion del tercero seleccioanda en la interfaz grafica .
     */
    private String direccion;

    /**
     * Atributo usado para descargar contenidos de archivos desde la vista
     */
    private StreamedContent archivoDescarga;
    // </DECLARAR_ATRIBUTOS>
    // <DECLARAR_PARAMETROS>
    // </DECLARAR_PARAMETROS>
    // <DECLARAR_LISTAS>
    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>
    /**
     * Lista encagada de almacenar los datos de respuesta de la base de datos a la tabla NAT_APERTURA
     */
    private RegistroDataModelImpl listaConvocatoria;

    /**
     * Lista encagada de almacenar los datos de respuesta de la base de datos a la tabla TERCERO
     */
    private RegistroDataModelImpl listaNumeroDcto;
    /**
     * Lista encagada de almacenar los datos de respuesta de la base de datos a la tabla TERCERO
     */
    private RegistroDataModelImpl listaNumeroDctoE;
    /**
     * Esta variable se usa como auxiliar para subformularios y en esta se alamcena el identificador del registro que se selecciono
     */
    private String auxiliar;

    /**
     * Atributo que administra el bloqueo de los campos de nombre,direccion y telefono del tercero
     */
    private boolean bloqueaTecero;
    /**
     * Variable que bloquea el boton de envio de correo
     */
    private boolean enviarInactivo;

    /**
     * variable que almacena la fecha de envio del correo
     */
    private String fechaEnvioCorreo;
    /**
     * Mensaje enviado correctamente
     */
    private String mensajeCorrecto;

    private boolean verNuevo;

    /**
     * Mensaje NO enviado
     */
    private String mensajeIncorrecto;

    @EJB
    private EjbSysmanUtilRemote ejbSysmanUtil;
    @EJB
    private EjbHojasDeVidaCeroRemote ejbHojasDeVida;
    private String tercero;

    // </DECLARAR_LISTAS_COMBO_GRANDE>
    /**
     * Crea una nueva instancia de Aperturainscritos1sControlador
     */
    public Aperturainscritos1sControlador() {
        super();
        compania = SessionUtil.getCompania();
        enviarInactivo = true;
        mensajeCorrecto = "Alerta de email enviada correctamente.";
        mensajeIncorrecto = "No se pudo enviar la alerta de email.";
        try {
            numFormulario = GeneralCodigoFormaEnum.APERTURAINSCRITOS1S_CONTROLADOR
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
     * Este metodo se ejecuta justo despues de que el objeto de la clase del Bean ha sido creado, en este se realizan las asignaciones iniciales necesarias para la visualizacion del formulario, como
     * son tablas, origenes de datos, inicializacion de listas y demas necesarios
     */
    @PostConstruct
    public void inicializar() {

        enumBase = GenericUrlEnum.NAT_APERTURA_INSCRITOS;
        reasignarOrigen();
        buscarLlave();
        bloqueaTecero = true;
        registro = new Registro();
        // <CARGAR_LISTA>
        // </CARGAR_LISTA>
        // <CARGAR_LISTA_COMBO_GRANDE>
        cargarListaConvocatoria();

        cargarListaNumeroDcto();
        cargarListaNumeroDctoE();
        // </CARGAR_LISTA_COMBO_GRANDE>
        abrirFormulario();

    }

    /**
     * En este metodo se asigna al atributo origenDatos del bean base el valor de la consulta del formulario. Tambien carga la lista del formulario por primera vez
     */
    @Override
    public void reasignarOrigen() {
        buscarUrls();
        parametrosListado.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
        parametrosListado.put("CONVOCATORIA", convocatoria);
    }

    // <METODOS_CARGAR_LISTA>
    /**
     * 
     * Carga la lista listaConvocatoria
     *
     * Metodo encargado de hacer el llamado a la base de datos y almacenar su respuesta en la lista listaConvocatoria
     */
    public void cargarListaConvocatoria() {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        Aperturainscritos1sControladorUrlEnum.URL0004
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        listaConvocatoria = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true,
                        Aperturainscritos1sControladorEnum.NRO_CONVOCATORIA
                                        .getValue());

    }

    /**
     * 
     * Carga la lista listaConvocatoria
     *
     * Metodo encargado de hacer el llamado a la base de datos y almacenar su respuesta en la lista listaNumeroDcto
     */
    public void cargarListaNumeroDcto() {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        Aperturainscritos1sControladorUrlEnum.URL6231
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        listaNumeroDcto = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, "NIT");

    }

    /**
     * 
     * Carga la lista listaConvocatoria
     *
     * Metodo encargado de hacer el llamado a la base de datos y almacenar su respuesta en la lista listaNumeroDctoE
     */
    public void cargarListaNumeroDctoE() {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        Aperturainscritos1sControladorUrlEnum.URL6230
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        listaNumeroDctoE = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, "NIT");

    }

    // </METODOS_CARGAR_LISTA>
    // <METODOS_BOTONES>
    /**
     * Metodo ejecutado al oprimir el boton Pdf
     * 
     * 
     * @param reg
     * registro en el cual esta ubicado el boton oprimido dentro de la grilla
     * @param indice
     * indice en el cual esta ubicado el boton oprimido dentro de la grilla
     */
    public void oprimirPdf(Registro reg, int indice) {
        // <CODIGO_DESARROLLADO>
        archivoDescarga = null;
        genInforme(FORMATOS.PDF, retornarString(reg,
                        GeneralParameterEnum.NUMERO_DCTO.getName()));
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Evento de envio de correo
     */
    public void oprimirbtnEnviar() {
        try {

            Map<String, Object> remplazosDescripcion = new TreeMap<>();
            remplazosDescripcion.put("nombreConvocatoria",
                            obtenerNombreConvocatoria(convocatoria));
            remplazosDescripcion.put("nombreCargo", nombreCargo);

            Map<String, Object> paramEnvio = new TreeMap<>();
            paramEnvio.put(GeneralParameterEnum.COMPANIA.getName(), compania);
            paramEnvio.put(GeneralParameterEnum.CONSECUTIVO.getName(), "12");

            StringBuilder correosDestino = obtenerCorreosInscritos();

            Registro rsEmail = RegistroConverter.toRegistro(
                            requestManager.get(UrlServiceUtil
                                            .getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            "1663003")
                                            .getUrl(),
                                            paramEnvio));

            if (rsEmail != null) {

                String descripcionFinal = remplazarVariable(
                                rsEmail.getCampos().get(
                                                GeneralParameterEnum.DESCRIPCION
                                                                .getName())
                                                .toString(),
                                remplazosDescripcion);

                EmailPojo email = new EmailPojo();
                email.setFrom(rsEmail.getCampos().get("ORIGEN")
                                .toString());
                email.setTo(correosDestino.toString());
                email.setSubject(rsEmail.getCampos().get("ASUNTO").toString());
                email.setBody(descripcionFinal);

                ApiRestClient client = new ApiRestClient();
                client.postClient(email);

                JsfUtil.agregarMensajeInformativo(
                                mensajeCorrecto);
                fechaEnvioCorreo = SysmanFunciones
                                .convertirAFechaCadena(new Date());
                ejbHojasDeVida.actualizarEnvioCorreos(compania, convocatoria,
                                0);
            }
            else {
                JsfUtil.agregarMensajeError(
                                mensajeIncorrecto);
            }

        }
        catch (SystemException | ParseException | IOException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    /**
     * Metodo que remplaza las variables de la descripcion del correo
     */
    public String remplazarVariable(String descripcion,
                    Map<String, Object> parametros) {
        String salida = descripcion;

        for (Map.Entry<String, Object> entry : parametros.entrySet()) {
            salida = salida.replace(
                            SysmanFunciones.concatenar("s$", entry.getKey(),
                                            "$s"),
                            SysmanFunciones.toString(entry.getValue()));
        }

        return salida;
    }

    public String obtenerNombreConvocatoria(String numeroConvocatoria) {
        String salida = "";

        if (numeroConvocatoria == null) {
            return "";
        }
        Map<String, Object> parametrosConvocatoria = new TreeMap<>();

        parametrosConvocatoria.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
        parametrosConvocatoria.put("NRO_CONVOCATORIA",
                        numeroConvocatoria);

        Registro rsClaseConvocatoria;
        try {
            rsClaseConvocatoria = RegistroConverter.toRegistro(
                            requestManager.get(UrlServiceUtil
                                            .getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            Aperturainscritos1sControladorUrlEnum.URL0002
                                                                            .getValue())
                                            .getUrl(),
                                            parametrosConvocatoria));
            salida = SysmanFunciones.toString(
                            rsClaseConvocatoria.getCampos()
                                            .get("NOMBRE_CLASE"));
        }
        catch (Exception e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

        if (salida == null || salida.isEmpty()) {
            return "";
        }
        else {
            return salida;
        }

    }

    /**
     * Metodo ejecutado al oprimir el boton Pdf
     * 
     * 
     * @param reg
     * registro en el cual esta ubicado el boton oprimido dentro de la grilla
     * @param indice
     * indice en el cual esta ubicado el boton oprimido dentro de la grilla
     */
    public void oprimirExcel(Registro reg, int indice) {
        // <CODIGO_DESARROLLADO>
        archivoDescarga = null;
        genInforme(FORMATOS.EXCEL, retornarString(reg,
                        GeneralParameterEnum.NUMERO_DCTO.getName()));
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado al cambiar el control Cedula
     * 
     */
    public void cambiarCedula() {
        bloqueaTecero = true;
        tercero = registro.getCampos().get(GeneralParameterEnum.NUMERO_DCTO.getName()).toString();

        Map<String, Object> param = new TreeMap<>();

        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        param.put("NIT", registro.getCampos().get(GeneralParameterEnum.NUMERO_DCTO.getName()));

        try {
            Registro regTercero = RegistroConverter.toRegistro(
                            requestManager.get(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            Aperturainscritos1sControladorUrlEnum.URL5959
                                                                            .getValue())
                                            .getUrl(), param));

            if (regTercero != null) {

                registro.getCampos().put(
                                GeneralParameterEnum.SUCURSAL.getName(),
                                regTercero.getCampos()
                                                .get(GeneralParameterEnum.SUCURSAL
                                                                .getName()));

                registro.getCampos().put(GeneralParameterEnum.NOMBRE.getName(),
                                regTercero.getCampos()
                                                .get(GeneralParameterEnum.NOMBRE
                                                                .getName()));

                registro.getCampos().put(
                                GeneralParameterEnum.TELEFONO.getName(),
                                regTercero.getCampos().get(
                                                Aperturainscritos1sControladorEnum.TELEFONOS
                                                                .getValue()));

                registro.getCampos().put(
                                GeneralParameterEnum.DIRECCION.getName(),
                                regTercero.getCampos()
                                                .get(GeneralParameterEnum.DIRECCION
                                                                .getName()));

                registro.getCampos().put(
                                Aperturainscritos1sControladorEnum.DIRECCIONEMAIL
                                                .getValue(),
                                regTercero.getCampos().get(
                                                Aperturainscritos1sControladorEnum.DIRECCIONEMAIL
                                                                .getValue()));

            }
            else {

                registro.getCampos().put(
                                GeneralParameterEnum.SUCURSAL.getName(),
                                SysmanConstantes.CONS_SUCURSAL);

                bloqueaTecero = false;
            }

        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    // </METODOS_BOTONES>
    // <METODOS_CAMBIAR>
    /**
     * Metodo ejecutado al cambiar el control NumeroDcto en la fila seleccionada dentro de la grilla
     * 
     * 
     * @param rowNum
     * indice de la fila seleccionada
     */
    public void cambiarNumeroDctoC(int rowNum) {
        // Para el cambio en una fila selecciona (PARA FORMULARIOS

        listaInicial.getDatasource().get(rowNum % 10).getCampos().put(
                        GeneralParameterEnum.NOMBRES.getName(),
                        nombreTer);
        listaInicial.getDatasource().get(rowNum % 10).getCampos()
                        .put(GeneralParameterEnum.DIRECCION.getName(),
                                        direccion);
        listaInicial.getDatasource().get(rowNum % 10).getCampos()
                        .put(GeneralParameterEnum.TELEFONO.getName(), telefono);

        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado al cambiar el control Cedula en la fila seleccionada dentro de la grilla
     * 
     * @param rowNum
     * indice de la fila seleccionada
     */
    public void cambiarCedulaC(int rowNum) {

        bloqueaTecero = true;

        tercero = listaInicial.getDatasource().get(rowNum %
                        10).getCampos().get("NUMERO_DCTO").toString();

        Map<String, Object> param = new TreeMap<>();

        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        param.put("NIT", listaInicial.getDatasource().get(rowNum %
                        10).getCampos().get("NUMERO_DCTO"));

        Registro regTercero;
        try {
            regTercero = RegistroConverter.toRegistro(
                            requestManager.get(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            Aperturainscritos1sControladorUrlEnum.URL5959
                                                                            .getValue())
                                            .getUrl(), param));

            if (regTercero != null) {

                listaInicial.getDatasource().get(rowNum %
                                10).getCampos().put(
                                                GeneralParameterEnum.SUCURSAL.getName(),
                                                regTercero.getCampos()
                                                                .get(GeneralParameterEnum.SUCURSAL
                                                                                .getName()));

                listaInicial.getDatasource().get(rowNum %
                                10).getCampos().put(GeneralParameterEnum.NOMBRE.getName(),
                                                regTercero.getCampos()
                                                                .get(GeneralParameterEnum.NOMBRE
                                                                                .getName()));

                listaInicial.getDatasource().get(rowNum %
                                10).getCampos().put(
                                                GeneralParameterEnum.TELEFONO.getName(),
                                                regTercero.getCampos().get(
                                                                Aperturainscritos1sControladorEnum.TELEFONOS
                                                                                .getValue()));

                listaInicial.getDatasource().get(rowNum %
                                10).getCampos().put(
                                                GeneralParameterEnum.DIRECCION.getName(),
                                                regTercero.getCampos()
                                                                .get(GeneralParameterEnum.DIRECCION
                                                                                .getName()));

                listaInicial.getDatasource().get(rowNum %
                                10).getCampos().put(
                                                Aperturainscritos1sControladorEnum.DIRECCIONEMAIL
                                                                .getValue(),
                                                regTercero.getCampos()
                                                                .get(Aperturainscritos1sControladorEnum.DIRECCIONEMAIL
                                                                                .getValue()));

            }
            else {
                bloqueaTecero = false;
            }
        }
        catch (SystemException e) {

            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    // </METODOS_CAMBIAR>
    // <METODOS_COMBOS_GRANDES>
    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista listaConvocatoria
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaConvocatoria(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();

        verNuevo = (boolean) registroAux.getCampos().get("CERRADA");

        convocatoria = registroAux.getCampos()
                        .get(Aperturainscritos1sControladorEnum.NRO_CONVOCATORIA
                                        .getValue())
                        .toString();

        fechaConv = registroAux.getCampos().get("FECHA_CONVOCATORIA")
                        .toString();

        nombreCargo = registroAux.getCampos().get("DENOMINACION").toString();
        reasignarOrigen();

        if (convocatoria != null) {
            enviarInactivo = false;

            Map<String, Object> parametrosConvocatoria = new TreeMap<>();
            parametrosConvocatoria.put(GeneralParameterEnum.COMPANIA.getName(),
                            compania);
            parametrosConvocatoria.put(
                            Aperturainscritos1sControladorEnum.NRO_CONVOCATORIA
                                            .getValue(),
                            convocatoria);

            Registro rsClaseConvocatoria;
            try {
                rsClaseConvocatoria = RegistroConverter.toRegistro(
                                requestManager.get(UrlServiceUtil
                                                .getInstance()
                                                .getUrlServiceByUrlByEnumID(
                                                                Aperturainscritos1sControladorUrlEnum.URL0001
                                                                                .getValue())
                                                .getUrl(),
                                                parametrosConvocatoria));

                if (rsClaseConvocatoria != null) {
                    fechaEnvioCorreo = SysmanFunciones.toString(
                                    rsClaseConvocatoria.getCampos()
                                                    .get("FECHA_ENVIO_CORREO"));
                }
                else {
                    fechaEnvioCorreo = " ";
                }

            }
            catch (SystemException e) {
                logger.error(e.getMessage(), e);
                JsfUtil.agregarMensajeError(e.getMessage());
            }

        }

        if (verNuevo) {
            enviarInactivo = true;
        }

    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista listaNumeroDcto
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaNumeroDcto(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        registro.getCampos().put(GeneralParameterEnum.NUMERO_DCTO.getName(),
                        registroAux.getCampos().get("NIT"));

        registro.getCampos().put(GeneralParameterEnum.SUCURSAL.getName(),
                        registroAux.getCampos()
                                        .get(GeneralParameterEnum.SUCURSAL
                                                        .getName()));

        registro.getCampos().put(GeneralParameterEnum.NOMBRES.getName(),
                        registroAux.getCampos().get(GeneralParameterEnum.NOMBRES
                                        .getName()));

        registro.getCampos().put(GeneralParameterEnum.TELEFONO.getName(),
                        registroAux.getCampos().get(
                                        Aperturainscritos1sControladorEnum.TELEFONOS
                                                        .getValue()));
        registro.getCampos().put(GeneralParameterEnum.DIRECCION.getName(),
                        registroAux.getCampos()
                                        .get(GeneralParameterEnum.DIRECCION
                                                        .getName()));

        registro.getCampos().put(
                        Aperturainscritos1sControladorEnum.DIRECCIONEMAIL
                                        .getValue(),
                        registroAux.getCampos().get(
                                        Aperturainscritos1sControladorEnum.DIRECCIONEMAIL
                                                        .getValue()));

    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista listaNumeroDcto
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaNumeroDctoE(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        auxiliar = retornarString(registroAux, "NIT");
        nombreTer = retornarString(registroAux,
                        GeneralParameterEnum.NOMBRES.getName());
        direccion = retornarString(registroAux,
                        GeneralParameterEnum.DIRECCION.getName());
        telefono = retornarString(registroAux,
                        Aperturainscritos1sControladorEnum.TELEFONOS
                                        .getValue());
    }

    // </METODOS_COMBOS_GRANDES>
    /**
     * Este metodo es invocado el metodo inicializar, se ejecutan las acciones a tener en cuenta en el momento de apertura del formulario
     */
    @Override
    public void abrirFormulario() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado cuando se cancela la edicion del registro
     */
    @Override
    public void cancelarEdicion(RowEditEvent event) {
        getListaInicial().load();
    }

    public void genInforme(ReportesBean.FORMATOS formato, String documento) {

        try {

            Map<String, Object> reemplazar = new HashMap<>();
            reemplazar.put("nroDocumento", documento);

            Map<String, Object> parametros = new HashMap<>();
            parametros.put("PR_NOMBRE_COMPANIA",
                            SessionUtil.getCompaniaIngreso().getNombre());
            Reporteador.resuelveConsulta("001677AperturaInscritosComprobante",
                            Integer.parseInt(SessionUtil.getModulo()),
                            reemplazar, parametros);
            archivoDescarga = JsfUtil.exportarStreamed(
                            "001677AperturaInscritosComprobante", parametros,
                            ConectorPool.ESQUEMA_SYSMAN, formato);
        }
        catch (JRException | IOException | SysmanException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    /**
     * Metodo ejecutado antes de realizar la insercion del registro
     */
    @Override
    public boolean insertarAntes() {
        // <CODIGO_DESARROLLADO>
        registro.getCampos().put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
        registro.getCampos()
                        .put(Aperturainscritos1sControladorEnum.NRO_CONVOCATORIA
                                        .getValue(), convocatoria);

        registro.getCampos().put(GeneralParameterEnum.CONSECUTIVO.getName(),
                        generarConsecutivo());
        Registro reg = null;
        HashMap<String, Object> param = new HashMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(Aperturainscritos1sControladorEnum.NRO_CONVOCATORIA
                        .getValue(), convocatoria);
        param.put(GeneralParameterEnum.NUMERO_DCTO.getName(), tercero);
        registro.getCampos();
        try {
            reg = RegistroConverter.toRegistro(requestManager.get(
                            UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            Aperturainscritos1sControladorUrlEnum.URL4970
                                                                            .getValue())
                                            .getUrl(),
                            param));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

        if (reg != null) {
            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB4191"));
            return false;
        }
        else {

            registro.getCampos()
                            .put(Aperturainscritos1sControladorEnum.NRO_CONVOCATORIA
                                            .getValue(), convocatoria);
            registro.getCampos().remove("CERRADA");

            // </CODIGO_DESARROLLADO>
            return true;
        }
        // </CODIGO_DESARROLLADO>
    }

    private Object generarConsecutivo() {
        long consecutivo = 0;

        try {
            consecutivo = ejbSysmanUtil.generarSiguienteConsecutivo(
                            "NAT_APERTURA_INSCRITOS",
                            "COMPANIA = " + compania + "AND NRO_CONVOCATORIA= "
                                            + convocatoria,
                            GeneralParameterEnum.CONSECUTIVO.getName());
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

        return consecutivo;
    }

    /**
     * Metodo ejecutado despues de realizar la insercion del registro
     */
    @Override
    public boolean insertarDespues() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
        return true;
    }

    /**
     * Metodo ejecutado antes de realizar la insercion y actualizacion del registro
     * 
     */
    @Override
    public boolean actualizarAntes() {
        // <CODIGO_DESARROLLADO>
        HashMap<String, Object> param = new HashMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(Aperturainscritos1sControladorEnum.NRO_CONVOCATORIA
                        .getValue(), convocatoria);
        param.put(GeneralParameterEnum.NUMERO_DCTO.getName(), tercero);
        registro.getCampos();

        Registro reg = null;
        try {
            reg = RegistroConverter.toRegistro(requestManager.get(
                            UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            Aperturainscritos1sControladorUrlEnum.URL4970
                                                                            .getValue())
                                            .getUrl(),
                            param));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

        if (reg != null) {
            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB4191"));
            return false;
        }
        else {

            registro.getCampos()
                            .put(Aperturainscritos1sControladorEnum.NRO_CONVOCATORIA
                                            .getValue(), convocatoria);
            registro.getCampos().remove("CERRADA");

            // </CODIGO_DESARROLLADO>
            return true;
        }
    }

    /**
     * Metodo ejecutado despues de realizar la insercion y actualizacion del registro
     * 
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
     */
    @Override
    public boolean eliminarAntes() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
        return true;
    }

    /**
     * Metodo ejecutado despues de realizar la eliminacion del registro
     * 
     */
    @Override
    public boolean eliminarDespues() {
        // <CODIGO_DESARROLLADO>
        listaInicial.load();
        // </CODIGO_DESARROLLADO>
        return true;
    }

    /**
     * Este metodo se ejecuta antes enviar la accion de actualizacion, en el se pueden remover valores auxiliares que no se desee o se deban enviar en el registro
     */
    @Override
    public void removerCombos() {
        registro.getCampos().remove(GeneralParameterEnum.COMPANIA.getName());
    }

    public StringBuilder obtenerCorreosInscritos() {

        StringBuilder salida = new StringBuilder();

        Map<String, Object> parametrosCorreos = new TreeMap<>();
        parametrosCorreos.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);

        parametrosCorreos.put(
                        Aperturainscritos1sControladorEnum.NRO_CONVOCATORIA
                                        .getValue(),
                        convocatoria);

        try {
            Registro rsListadoCorreos = RegistroConverter.toRegistro(
                            requestManager.get(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            Aperturainscritos1sControladorUrlEnum.URL0003
                                                                            .getValue())
                                            .getUrl(), parametrosCorreos));

            if (rsListadoCorreos != null) {
                salida.append(rsListadoCorreos.getCampos()
                                .get("CORREOS_DESTINO"));
            }
            else {
                salida.append("");
            }

        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

        return salida;

    }

    /**
     * Este metodo es ejecutado despues de finalizar la insercion y edicion del registro se usa cuando se desean agregar valores al registro despues de dichas acciones
     */
    @Override
    public void asignarValoresRegistro() {
        //
    }

    // <SET_GET_ATRIBUTOS>
    /**
     * Retorna la variable convocatoria
     * 
     * @return convocatoria
     */
    public String getConvocatoria() {
        return convocatoria;
    }

    /**
     * Asigna la variable convocatoria
     * 
     * @param convocatoria
     * Variable a asignar en convocatoria
     */
    public void setConvocatoria(String convocatoria) {
        this.convocatoria = convocatoria;
    }

    /**
     * Retorna la variable fechaConv
     * 
     * @return fechaConv
     */
    public String getFechaConv() {
        return fechaConv;
    }

    /**
     * Asigna la variable fechaConv
     * 
     * @param fechaConv
     * Variable a asignar en fechaConv
     */
    public void setFechaConv(String fechaConv) {
        this.fechaConv = fechaConv;
    }

    /**
     * Retorna la variable nombreCargo
     * 
     * @return nombreCargo
     */
    public String getNombreCargo() {
        return nombreCargo;
    }

    /**
     * Asigna la variable nombreCargo
     * 
     * @param nombreCargo
     * Variable a asignar en nombreCargo
     */
    public void setNombreCargo(String nombreCargo) {
        this.nombreCargo = nombreCargo;
    }

    /**
     * Atributo usado para descargar contenidos de archivos desde la vista
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
     * Retorna la lista listaConvocatoria
     * 
     * @return listaConvocatoria
     */
    public RegistroDataModelImpl getListaConvocatoria() {
        return listaConvocatoria;
    }

    /**
     * Asigna la lista listaConvocatoria
     * 
     * @param listaConvocatoria
     * Variable a asignar en listaConvocatoria
     */
    public void setListaConvocatoria(RegistroDataModelImpl listaConvocatoria) {
        this.listaConvocatoria = listaConvocatoria;
    }

    /**
     * Retorna la lista listaNumeroDcto
     * 
     * @return listaNumeroDcto
     */
    public RegistroDataModelImpl getListaNumeroDcto() {
        return listaNumeroDcto;
    }

    /**
     * Asigna la lista listaNumeroDcto
     * 
     * @param listaNumeroDcto
     * Variable a asignar en listaNumeroDcto
     */
    public void setListaNumeroDcto(RegistroDataModelImpl listaNumeroDcto) {
        this.listaNumeroDcto = listaNumeroDcto;
    }

    /**
     * Retorna la lista listaNumeroDcto
     * 
     * @return listaNumeroDcto
     */
    public RegistroDataModelImpl getListaNumeroDctoE() {
        return listaNumeroDctoE;
    }

    /**
     * Asigna la lista listaNumeroDcto
     * 
     * @param listaNumeroDcto
     * Variable a asignar en listaNumeroDcto
     */
    public void setListaNumeroDctoE(RegistroDataModelImpl listaNumeroDctoE) {
        this.listaNumeroDctoE = listaNumeroDctoE;
    }

    /**
     * Retorna la variable auxiliar
     * 
     * @return auxiliar
     */
    public String getAuxiliar() {
        return auxiliar;
    }

    /**
     * Asigna la variable auxiliar
     * 
     * @param auxiliar
     * Variable a asignar en auxiliar
     */
    public void setAuxiliar(String auxiliar) {
        this.auxiliar = auxiliar;
    }
    // </SET_GET_LISTAS_COMBO_GRANDE>

    private String retornarString(Registro reg, String campo) {
        return SysmanFunciones.validarCampoVacio(reg.getCampos(), campo) ? ""
                        : reg.getCampos().get(campo).toString();

    }

    public boolean isBloqueaTecero() {
        return bloqueaTecero;
    }

    public void setBloqueaTecero(boolean bloqueaTecero) {
        this.bloqueaTecero = bloqueaTecero;
    }

    public boolean isEnviarInactivo() {
        return enviarInactivo;
    }

    public void setEnviarInactivo(boolean enviarInactivo) {
        this.enviarInactivo = enviarInactivo;
    }

    public String getFechaEnvioCorreo() {
        return fechaEnvioCorreo;
    }

    public void setFechaEnvioCorreo(String fechaEnvioCorreo) {
        this.fechaEnvioCorreo = fechaEnvioCorreo;
    }

    /**
     * @return the verNuevo
     */
    public boolean isVerNuevo() {
        return verNuevo;
    }

    /**
     * @param verNuevo
     * the verNuevo to set
     */
    public void setVerNuevo(boolean verNuevo) {
        this.verNuevo = verNuevo;
    }

}
