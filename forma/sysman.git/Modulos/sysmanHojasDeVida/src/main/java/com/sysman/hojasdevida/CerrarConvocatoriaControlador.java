/*-
 * CerrarConvocatoriaControlador.java
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

import com.sysman.beanbase.BeanBaseModal;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.email.ApiRestClient;
import com.sysman.email.EmailPojo;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.exception.SystemException;
import com.sysman.hojasdevida.ejb.EjbHojasDeVidaCeroRemote;
import com.sysman.hojasdevida.enums.Aperturainscritos1sControladorEnum;
import com.sysman.hojasdevida.enums.CerrarConvocatoriaControladorEnum;
import com.sysman.hojasdevida.enums.CerrarConvocatoriaControladorUrlEnum;
import com.sysman.hojasdevida.enums.EvaluaciondocumentossubsControladorEnum;
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

import org.primefaces.event.SelectEvent;

/**
 * Clase encargada del formulario cerrar convocatoria
 *
 * @version 1.0, 29/01/2018
 * @author spina
 */
@ManagedBean
@ViewScoped
public class CerrarConvocatoriaControlador extends BeanBaseModal {

    // <DECLARAR_ATRIBUTOS>
    /**
     * Constante a nivel de clase que almacena el codigo de la compania en la cual inicio sesion el usuario, el valor de esta constante es asignado en el constructor a la variable de sesion
     * correspondiente
     */
    private final String compania;
    /**
     * Variable que almacena la convocatoria seleccionada en el combo
     */
    private String noConvocatoria;
    // </DECLARAR_ATRIBUTOS>
    // <DECLARAR_PARAMETROS>
    // </DECLARAR_PARAMETROS>
    // <DECLARAR_LISTAS>
    /**
     * lista que muestra las convocatorias de la compania
     */
    private RegistroDataModelImpl listaCmbConvocatoria;

    /**
     * Variable que bloquea el boton de envio de correo
     */
    private boolean enviarInactivo;
    /**
     * variable que almacena la fecha de envio del correo
     */
    private String fechaAceptados;
    /**
     * variable que almacena la fecha de envio del correo
     */
    private String fechaRechazados;
    /**
     * Mensaje enviado correctamente
     */
    private String mensajeCorrecto;

    /**
     * Mensaje NO enviado
     */
    private String mensajeIncorrecto;
    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>
    // </DECLARAR_LISTAS_COMBO_GRANDE>
    @EJB
    private EjbHojasDeVidaCeroRemote ejbHojasDeVidaCero;
    @EJB
    private EjbHojasDeVidaCeroRemote ejbHojasDeVida;

    /**
     * Crea una nueva instancia de CerrarConvocatoriaControlador
     */
    public CerrarConvocatoriaControlador() {
        super();
        compania = SessionUtil.getCompania();
        enviarInactivo = true;
        mensajeCorrecto = "Alerta de email enviada correctamente.";
        mensajeIncorrecto = "No se pudo enviar la alerta de email.";
        try {
            numFormulario = GeneralCodigoFormaEnum.CERRARCONVOCATORIA_CONTROLADOR
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
        // <CARGAR_LISTA>
        cargarListaCmbConvocatoria();
        // </CARGAR_LISTA>
        // <CARGAR_LISTA_COMBO_GRANDE>
        // </CARGAR_LISTA_COMBO_GRANDE>
        // <CREAR_ARBOLES>
        // </CREAR_ARBOLES>
        abrirFormulario();
    }

    /**
     * Este metodo es invocado el metodo inicializar, se ejecutan las acciones a tener en cuenta en el momento de apertura del formulario
     */
    @Override
    public void abrirFormulario() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    // <METODOS_CARGAR_LISTA>
    /**
     *
     * Carga la lista listaCmbConvocatoria
     *
     */
    public void cargarListaCmbConvocatoria() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        CerrarConvocatoriaControladorUrlEnum.URL007
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        listaCmbConvocatoria = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param, true,
                        CerrarConvocatoriaControladorEnum.NRO_CONVOCATORIA
                                        .getValue());
    }

    public void seleccionarFilaCmbConvocatoria(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        noConvocatoria = SysmanFunciones.nvl(registroAux.getCampos().get(
                        CerrarConvocatoriaControladorEnum.NRO_CONVOCATORIA
                                        .getValue()),
                        "").toString();

        if (noConvocatoria != null) {
            enviarInactivo = false;

            Map<String, Object> parametrosConvocatoria = new TreeMap<>();
            parametrosConvocatoria.put(GeneralParameterEnum.COMPANIA.getName(),
                            compania);
            parametrosConvocatoria.put(
                            CerrarConvocatoriaControladorEnum.NRO_CONVOCATORIA
                                            .getValue(),
                            noConvocatoria);

            Registro rsClaseConvocatoria;
            try {
                rsClaseConvocatoria = RegistroConverter.toRegistro(
                                requestManager.get(UrlServiceUtil
                                                .getInstance()
                                                .getUrlServiceByUrlByEnumID(
                                                                CerrarConvocatoriaControladorUrlEnum.URL004
                                                                                .getValue())
                                                .getUrl(),
                                                parametrosConvocatoria));

                if (rsClaseConvocatoria != null) {

                    fechaAceptados = SysmanFunciones.toString(
                                    rsClaseConvocatoria.getCampos()
                                                    .get("FECHA_ENVIO_ACP"));

                    fechaRechazados = SysmanFunciones.toString(
                                    rsClaseConvocatoria.getCampos()
                                                    .get("FECHA_ENVIO_RCZ"));
                }
                else {
                    fechaAceptados = " ";

                    fechaRechazados = " ";
                }

            }
            catch (SystemException e) {
                logger.error(e.getMessage(), e);
                JsfUtil.agregarMensajeError(e.getMessage());
            }

        }
    }

    // </METODOS_CARGAR_LISTA>
    // <METODOS_BOTONES>
    /**
     *
     * Metodo ejecutado al oprimir el boton Comando6 en la vista
     *
     */
    public void oprimirComando6() {
        // <CODIGO_DESARROLLADO>
        try {

            String mensaje = ejbHojasDeVidaCero.cerrarConvocatoria(compania, noConvocatoria,
                            SessionUtil.getUser().getCodigo());
            if (mensaje.isEmpty()) {
                JsfUtil.agregarMensajeInformativo(
                                idioma.getString("MSM_PROCESO_EJECUTADO"));
            }
            else {

                JsfUtil.agregarMensajeInformativo(
                                idioma.getString("TB_TB4225").replace("s$inscritos$s ", mensaje.substring(1, mensaje.length()))
                                                .replace("s$convocatoria$s", noConvocatoria));

            }

        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
        JsfUtil.agregarMensajeInformativo(
                        idioma.getString("MSM_PROCESO_EJECUTADO"));
        // </CODIGO_DESARROLLADO>
    }

    public void oprimirbtnEnviaeAceptados() {

        try {

            Map<String, Object> parametrosConvocatoria = new TreeMap<>();

            parametrosConvocatoria.put(GeneralParameterEnum.COMPANIA.getName(),
                            compania);
            parametrosConvocatoria.put(
                            EvaluaciondocumentossubsControladorEnum.NRO_CONVOCATORIA
                                            .getValue(),
                            noConvocatoria);

            Registro rsClaseConvocatoria;
            rsClaseConvocatoria = RegistroConverter.toRegistro(
                            requestManager.get(UrlServiceUtil
                                            .getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            CerrarConvocatoriaControladorUrlEnum.URL002
                                                                            .getValue())
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
                fechaAceptados = SysmanFunciones
                                .convertirAFechaCadena(new Date());
                ejbHojasDeVida.actualizarEnvioCorreos(compania,
                                noConvocatoria, 2);
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

    public void oprimirbtnEnviarRechazados() {

        try {

            Map<String, Object> parametrosCargo = new TreeMap<>();

            parametrosCargo.put(GeneralParameterEnum.COMPANIA.getName(),
                            compania);
            parametrosCargo.put("NRO_CONVOCATORIA", noConvocatoria);

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
                fechaRechazados = SysmanFunciones
                                .convertirAFechaCadena(new Date());
                ejbHojasDeVida.actualizarEnvioCorreos(compania,
                                noConvocatoria, 3);
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

        parametrosCorreos
                        .put(Aperturainscritos1sControladorEnum.NRO_CONVOCATORIA
                                        .getValue(), noConvocatoria);

        try {
            String servicio;
            if (opcion == 1) {
                /**
                 * Aprobados
                 */
                servicio = CerrarConvocatoriaControladorUrlEnum.URL005
                                .getValue();
            }
            else {
                /**
                 * No aprobados
                 */
                servicio = CerrarConvocatoriaControladorUrlEnum.URL006
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

    // </METODOS_BOTONES>
    // <METODOS_CAMBIAR>
    // </METODOS_CAMBIAR>
    // <METODOS_COMBOS_GRANDES>
    // </METODOS_COMBOS_GRANDES>
    // <METODOS_ARBOL>
    // </METODOS_ARBOL>
    // <SET_GET_ATRIBUTOS>
    // </SET_GET_ATRIBUTOS>
    // <SET_GET_PARAMETROS>
    // </SET_GET_PARAMETROS>
    // <SET_GET_LISTAS>
    /**
     * Retorna la lista listaCmbConvocatoria
     *
     * @return listaCmbConvocatoria
     */
    public RegistroDataModelImpl getListaCmbConvocatoria() {
        return listaCmbConvocatoria;
    }

    /**
     * Asigna la lista listaCmbConvocatoria
     *
     * @param listaCmbConvocatoria
     * Variable a asignar en listaCmbConvocatoria
     */
    public void setListaCmbConvocatoria(
                    RegistroDataModelImpl listaCmbConvocatoria) {
        this.listaCmbConvocatoria = listaCmbConvocatoria;
    }

    public String getNoConvocatoria() {
        return noConvocatoria;
    }

    public void setNoConvocatoria(String noConvocatoria) {
        this.noConvocatoria = noConvocatoria;
    }

    public boolean isEnviarInactivo() {
        return enviarInactivo;
    }

    public void setEnviarInactivo(boolean enviarInactivo) {
        this.enviarInactivo = enviarInactivo;
    }

    public String getFechaAceptados() {
        return fechaAceptados;
    }

    public void setFechaAceptados(String fechaAceptados) {
        this.fechaAceptados = fechaAceptados;
    }

    public String getFechaRechazados() {
        return fechaRechazados;
    }

    public void setFechaRechazados(String fechaRechazados) {
        this.fechaRechazados = fechaRechazados;
    }
    // </SET_GET_LISTAS>
    // <SET_GET_LISTAS_COMBO_GRANDE>
    // </SET_GET_LISTAS_COMBO_GRANDE>
}
