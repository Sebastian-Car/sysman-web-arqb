/*-
 * EvaluaciondocumentossubsControlador.java
 *
 * 1.0
 * 
 * 29/01/2018
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
import com.sysman.exception.SystemException;
import com.sysman.hojasdevida.ejb.EjbHojasDeVidaCeroRemote;
import com.sysman.hojasdevida.enums.Aperturainscritos1sControladorEnum;
import com.sysman.hojasdevida.enums.Aperturainscritos1sControladorUrlEnum;
import com.sysman.hojasdevida.enums.CerrarConvocatoriaControladorUrlEnum;
import com.sysman.hojasdevida.enums.EvaluaciondocumentossubsControladorEnum;
import com.sysman.hojasdevida.enums.EvaluaciondocumentossubsControladorUrlEnum;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.util.SysmanFunciones;

import java.io.IOException;
import java.text.ParseException;
import java.util.Date;
import java.util.Map;
import java.util.TreeMap;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.event.RowEditEvent;
import org.primefaces.event.SelectEvent;

/**
 * Clase encargada de la gestion (CRUD) de los documentos adjuntados para la convocatoria
 *
 * @version 1.0, 29/01/2018
 * @author jeguerrero
 */
@ManagedBean
@ViewScoped
public class EvaluaciondocumentossubsControlador
                extends BeanBaseContinuoAcmeImpl {
    /**
     * Constante a nivel de clase que almacena el codigo de la compania en la cual inicio sesion el usuario, el valor de esta constante es asignado en el constructor a la variable de sesion
     * correspondiente
     */
    private final String compania;
    // <DECLARAR_ATRIBUTOS>
    /**
     * Variable encargada de almacenar la convocatoria seleccionada en el combo de la interfaz grafica.
     */
    private String convocatoria;
    /**
     * Variable encargada de almacenar temporalmente la fecha de la convocatoria seleccionada en el combo convocatoria de la interfaz grafica
     */
    private String fechaConv;
    /**
     * Variable encargada de almacenar temporalmente el nombre de la convocatoria seleccionada en el combo convocatoria de la interfaz grafica
     */
    private String nombreCargo;
    // </DECLARAR_ATRIBUTOS>
    // <DECLARAR_PARAMETROS>
    // </DECLARAR_PARAMETROS>
    // <DECLARAR_LISTAS>
    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>
    /**
     * Lista encargada de almacenar la respuesta de la base de datos y almacenarla en la listaConvocatoria.
     */
    private RegistroDataModelImpl listaconvocatoria;

    /**
     * Lista encargada de almacenar la respuesta de la base de datos y almacenarla en la listaNumeroDcto.
     */
    private RegistroDataModelImpl listaNumeroDcto;
    /**
     * Lista encargada de almacenar la respuesta de la base de datos y almacenarla en la listaNumeroDcto.
     */
    private RegistroDataModelImpl listaNumeroDctoE;
    /**
     * Esta variable se usa como auxiliar para subformularios y en esta se alamcena el identificador del registro que se selecciono
     */
    private String auxiliar;
    /**
     * Variable encargada de almacenar temporalmente el nombre del tercero seleccionado.
     */
    private String nombre;
    /**
     * Variable que bloquea el boton de envio de correo
     */
    private boolean enviarInactivo;

    /**
     * Mensaje de envio de alerta
     */
    private String mensajeEnvio;

    /**
     * variable que almacena la fecha de envio del correo
     */
    private String fechaEnvioCorreoAprobados;
    private String fechaEnvioCorreoRechazados;

    /**
     * Constante encargada de almacenar el String "NRO_CONVOCATORIA"
     */
    private final String nroConvocatoriaCons;
    private String sucursal;

    private boolean verEditar;

    /**
     * Mensaje enviado correctamente
     */
    private String mensajeCorrecto;

    /**
     * Mensaje NO enviado
     */
    private String mensajeIncorrecto;
    @EJB
    private EjbHojasDeVidaCeroRemote ejbHojasDeVida;

    // </DECLARAR_LISTAS_COMBO_GRANDE>
    /**
     * Crea una nueva instancia de EvaluaciondocumentossubsControlador
     */
    public EvaluaciondocumentossubsControlador() {
        super();
        compania = SessionUtil.getCompania();
        nroConvocatoriaCons = EvaluaciondocumentossubsControladorEnum.NRO_CONVOCATORIA
                        .getValue();

        enviarInactivo = true;
        mensajeCorrecto = "Alerta de email enviada correctamente.";
        mensajeIncorrecto = "No se pudo enviar la alerta de email.";
        try {
            numFormulario = GeneralCodigoFormaEnum.EVALUACIONDOCUMENTOSSUBS_CONTROLADOR
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

        tabla = EvaluaciondocumentossubsControladorEnum.NAT_APERTURA_INSCRITOS
                        .getValue();
        reasignarOrigen();
        buscarLlave();

        registro = new Registro();
        // <CARGAR_LISTA>
        // </CARGAR_LISTA>
        // <CARGAR_LISTA_COMBO_GRANDE>
        cargarListaconvocatoria();

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

        parametrosListado.put("KEY_COMPANIA", compania);
        parametrosListado.put("KEY_NRO_CONVOCATORIA", convocatoria);

        urlListado = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        EvaluaciondocumentossubsControladorUrlEnum.URL0002
                                                        .getValue());

        urlActualizacion = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        EvaluaciondocumentossubsControladorUrlEnum.URL5181
                                                        .getValue());
        urlEliminacion = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        EvaluaciondocumentossubsControladorUrlEnum.URL6228
                                                        .getValue());

    }

    // <METODOS_CARGAR_LISTA>
    /**
     * 
     * Carga la lista listaconvocatoria
     *
     * Metodo encargado de hacer el llamado a la base de datos y almancear la respuesta en la lista listaconvocatoria
     */
    public void cargarListaconvocatoria() {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        EvaluaciondocumentossubsControladorUrlEnum.URL6229
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        listaconvocatoria = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, nroConvocatoriaCons);

    }

    /**
     * 
     * Metodo encargado de hacer el llamado a la base de datos y almancear la respuesta en la lista listaNumeroDcto
     *
     */
    public void cargarListaNumeroDcto() {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        EvaluaciondocumentossubsControladorUrlEnum.URL6229
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        listaNumeroDcto = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, "NIT");

    }

    /**
     * 
     * Carga la lista listaNumeroDcto
     *
     * Metodo encargado de hacer el llamado a la base de datos y almancear la respuesta en la lista listaNumeroDctoE
     */
    public void cargarListaNumeroDctoE() {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        EvaluaciondocumentossubsControladorUrlEnum.URL6230
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        listaNumeroDctoE = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, "NIT");

    }

    // </METODOS_CARGAR_LISTA>
    // <METODOS_BOTONES>
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

        listaInicial.getDatasource().get(rowNum % 10).getCampos().put(
                        "NUMERO_DCTO",
                        auxiliar);
        listaInicial.getDatasource().get(rowNum % 10).getCampos().put(
                        GeneralParameterEnum.NOMBRES.getName(),
                        nombre);

        listaInicial.getDatasource().get(rowNum % 10).getCampos()
                        .put(GeneralParameterEnum.SUCURSAL.getName(), sucursal);

        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    // </METODOS_CAMBIAR>
    // <METODOS_COMBOS_GRANDES>
    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista listaconvocatoria
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaconvocatoria(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        verEditar = (boolean) registroAux.getCampos().get("CERRADA");

        if (verEditar) {
            verEditar = false;
        }
        else {
            verEditar = true;
        }

        convocatoria = retornarString(registroAux, nroConvocatoriaCons);
        fechaConv = retornarString(registroAux, "FECHA_CONVOCATORIA");
        nombreCargo = retornarString(registroAux, "DENOMINACION");

        reasignarOrigen();

        if (convocatoria != null) {
            enviarInactivo = false;

            Map<String, Object> parametrosConvocatoria = new TreeMap<>();
            parametrosConvocatoria.put(GeneralParameterEnum.COMPANIA.getName(),
                            compania);
            parametrosConvocatoria.put(
                            EvaluaciondocumentossubsControladorEnum.NRO_CONVOCATORIA
                                            .getValue(),
                            convocatoria);

            Registro rsClaseConvocatoria;
            try {
                rsClaseConvocatoria = RegistroConverter.toRegistro(
                                requestManager.get(UrlServiceUtil
                                                .getInstance()
                                                .getUrlServiceByUrlByEnumID(
                                                                EvaluaciondocumentossubsControladorUrlEnum.URL0001
                                                                                .getValue())
                                                .getUrl(),
                                                parametrosConvocatoria));

                if (rsClaseConvocatoria != null) {
                    fechaEnvioCorreoAprobados = SysmanFunciones.toString(
                                    rsClaseConvocatoria.getCampos()
                                                    .get("FECHA_ENVIO_CORREO_APROBADOS"));

                    fechaEnvioCorreoRechazados = SysmanFunciones.toString(
                                    rsClaseConvocatoria.getCampos()
                                                    .get("FECHA_ENVIO_CORREO_RECHAZADOS"));

                }
                else {
                    fechaEnvioCorreoAprobados = " ";
                    fechaEnvioCorreoRechazados = " ";
                }

            }
            catch (SystemException e) {
                logger.error(e.getMessage(), e);
                JsfUtil.agregarMensajeError(e.getMessage());
            }

        }

    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista listaconvocatoria
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaconvocatoriaE(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        auxiliar = retornarString(registroAux, nroConvocatoriaCons);

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
        registro.getCampos().put("NUMERO_DCTO",
                        registroAux.getCampos().get("NIT"));
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
        nombre = retornarString(registroAux,
                        GeneralParameterEnum.NOMBRES.getName());
        sucursal = retornarString(registroAux,
                        GeneralParameterEnum.SUCURSAL.getName());
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

    /**
     * Metodo ejecutado antes de realizar la insercion del registro
     */
    @Override
    public boolean insertarAntes() {
        // <CODIGO_DESARROLLADO>

        // </CODIGO_DESARROLLADO>
        return true;
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

        registro.getCampos().remove(GeneralParameterEnum.NOMBRES.getName());
        registro.getCampos().remove("CERRADA");

        registro.getCampos().remove("CANTIDAD_CAL");

        // </CODIGO_DESARROLLADO>
        return true;
    }

    /**
     * Metodo ejecutado despues de realizar la insercion y actualizacion del registro
     * 
     */
    @Override
    public boolean actualizarDespues() {
        // <CODIGO_DESARROLLADO>
        registro.getCampos().remove("CERRADA");
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
        // </CODIGO_DESARROLLADO>
        return true;
    }

    /**
     * Este metodo se ejecuta antes enviar la accion de actualizacion, en el se pueden remover valores auxiliares que no se desee o se deban enviar en el registro
     */
    @Override
    public void removerCombos() {
        registro.getCampos().remove("COMPANIA");
        registro.getCampos().remove("CONSECUTIVO");
        registro.getCampos().remove(
                        EvaluaciondocumentossubsControladorEnum.NRO_CONVOCATORIA
                                        .getValue());
        registro.getCampos().remove("SUCURSAL");

        //
    }

    /**
     * Este metodo es ejecutado despues de finalizar la insercion y edicion del registro se usa cuando se desean agregar valores al registro despues de dichas acciones
     */
    @Override
    public void asignarValoresRegistro() {
        //
    }

    /**
     * Evento de envio de correo
     */
    public void oprimirbtnEnviarAprobados() {

        try {

            Map<String, Object> parametrosConvocatoria = new TreeMap<>();

            parametrosConvocatoria.put(GeneralParameterEnum.COMPANIA.getName(),
                            compania);
            parametrosConvocatoria.put(
                            EvaluaciondocumentossubsControladorEnum.NRO_CONVOCATORIA
                                            .getValue(),
                            convocatoria);

            Registro rsClaseConvocatoria;
            rsClaseConvocatoria = RegistroConverter.toRegistro(
                            requestManager.get(UrlServiceUtil
                                            .getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            "463041")
                                            .getUrl(),
                                            parametrosConvocatoria));
            String nombreCargoEnvio = SysmanFunciones.toString(
                            rsClaseConvocatoria.getCampos()
                                            .get("NOMBRE_DEL_CARGO"));

            Registro rsNombreDependencia;
            rsNombreDependencia = RegistroConverter.toRegistro(
                            requestManager.get(UrlServiceUtil
                                            .getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            CerrarConvocatoriaControladorUrlEnum.URL003
                                                                            .getValue())
                                            .getUrl(),
                                            parametrosConvocatoria));
            String nombreDependencia = SysmanFunciones.toString(
                            rsNombreDependencia.getCampos()
                                            .get("NOMBRE_DEPENDENCIA"));

            StringBuilder correosDestino = obtenerCorreosDestinos(1);

            Map<String, Object> remplazosDescripcion = new TreeMap<>();
            remplazosDescripcion.put("nombreDependencia", nombreDependencia);
            remplazosDescripcion.put("nombreCargo", nombreCargoEnvio);

            Map<String, Object> paramEnvio = new TreeMap<>();
            paramEnvio.put(GeneralParameterEnum.COMPANIA.getName(), compania);
            paramEnvio.put(GeneralParameterEnum.CONSECUTIVO.getName(), "13");

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
                fechaEnvioCorreoAprobados = SysmanFunciones
                                .convertirAFechaCadena(new Date());
                ejbHojasDeVida.actualizarEnvioCorreos(compania, convocatoria,
                                1);
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

    /**
     * Evento de envio de correo oprimirbtnEnviarRechazados
     */
    public void oprimirbtnEnviarRechazados() {

        try {

            Map<String, Object> parametrosCargo = new TreeMap<>();

            parametrosCargo.put(GeneralParameterEnum.COMPANIA.getName(),
                            compania);
            parametrosCargo.put("NRO_CONVOCATORIA", convocatoria);

            Registro rsCargo = RegistroConverter.toRegistro(
                            requestManager.get(UrlServiceUtil
                                            .getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            "463041")
                                            .getUrl(),
                                            parametrosCargo));

            String nombreCargoEnvio = SysmanFunciones.toString(
                            rsCargo.getCampos().get("NOMBRE_DEL_CARGO"));

            StringBuilder correosDestino = obtenerCorreosDestinos(0);

            Map<String, Object> remplazosDescripcion = new TreeMap<>();
            remplazosDescripcion.put("nombreCargo", nombreCargoEnvio);

            Map<String, Object> paramEnvio = new TreeMap<>();
            paramEnvio.put(GeneralParameterEnum.COMPANIA.getName(), compania);
            paramEnvio.put(GeneralParameterEnum.CONSECUTIVO.getName(), "14");

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
                fechaEnvioCorreoRechazados = SysmanFunciones
                                .convertirAFechaCadena(new Date());
                ejbHojasDeVida.actualizarEnvioCorreos(compania, convocatoria,
                                5);
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
     * Este metodo se va a encargar de retornar los correos destino de las personas que aprobaron y de las personas que no aprobaron 1 = aprobados 0 = no aprobados
     * 
     * @param
     * @return
     */
    public StringBuilder obtenerCorreosDestinos(int opcion) {

        StringBuilder salida = new StringBuilder();

        Map<String, Object> parametrosCorreos = new TreeMap<>();
        parametrosCorreos.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);

        parametrosCorreos.put(
                        Aperturainscritos1sControladorEnum.NRO_CONVOCATORIA
                                        .getValue(),
                        convocatoria);

        try {
            String servicio;
            if (opcion == 1) {
                /**
                 * Aprobados
                 */
                servicio = EvaluaciondocumentossubsControladorUrlEnum.URL0003
                                .getValue();
            }
            else {
                /**
                 * No aprobados
                 */
                servicio = EvaluaciondocumentossubsControladorUrlEnum.URL0004
                                .getValue();

            }
            Registro rsListadoCorreos = RegistroConverter.toRegistro(
                            requestManager.get(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            servicio)
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

    public String getFechaConv() {
        return fechaConv;
    }

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

    // </SET_GET_ATRIBUTOS>
    // <SET_GET_PARAMETROS>
    // </SET_GET_PARAMETROS>
    // <SET_GET_LISTAS>
    // </SET_GET_LISTAS>
    // <SET_GET_LISTAS_COMBO_GRANDE>
    /**
     * Retorna la lista listaconvocatoria
     * 
     * @return listaconvocatoria
     */
    public RegistroDataModelImpl getListaconvocatoria() {
        return listaconvocatoria;
    }

    /**
     * Asigna la lista listaconvocatoria
     * 
     * @param listaconvocatoria
     * Variable a asignar en listaconvocatoria
     */
    public void setListaconvocatoria(RegistroDataModelImpl listaconvocatoria) {
        this.listaconvocatoria = listaconvocatoria;
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

    private String retornarString(Registro reg, String campo) {
        return SysmanFunciones.validarCampoVacio(reg.getCampos(), campo) ? ""
                        : reg.getCampos().get(campo).toString();
    }

    public String getFechaEnvioCorreoAprobados() {
        return fechaEnvioCorreoAprobados;
    }

    public void setFechaEnvioCorreoAprobados(String fechaEnvioCorreoAprobados) {
        this.fechaEnvioCorreoAprobados = fechaEnvioCorreoAprobados;
    }

    public String getFechaEnvioCorreoRechazados() {
        return fechaEnvioCorreoRechazados;
    }

    public void setFechaEnvioCorreoRechazados(
                    String fechaEnvioCorreoRechazados) {
        this.fechaEnvioCorreoRechazados = fechaEnvioCorreoRechazados;
    }

    public boolean isEnviarInactivo() {
        return enviarInactivo;
    }

    public void setEnviarInactivo(boolean enviarInactivo) {
        this.enviarInactivo = enviarInactivo;
    }

    /**
     * @return the verEditar
     */
    public boolean isVerEditar() {
        return verEditar;
    }

    /**
     * @param verEditar
     * the verEditar to set
     */
    public void setVerEditar(boolean verEditar) {
        this.verEditar = verEditar;
    }

    // </SET_GET_LISTAS_COMBO_GRANDE>
}
