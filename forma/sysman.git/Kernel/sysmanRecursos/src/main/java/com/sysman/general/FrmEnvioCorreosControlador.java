/*-
 * FrmEnvioCorreosControlador.java
 *
 * 1.0
 * 
 * 11/06/2018
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */
package com.sysman.general;

import com.sysman.beanbase.BeanBaseModal;
import com.sysman.cache.enums.UrlServiceCache;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.email.ApiRestClient;
import com.sysman.email.EmailPojo;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException;
import com.sysman.general.enums.FrmEnvioCorreosControladorUrlEnum;
import com.sysman.jsfutil.CacheUtil;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.util.ContenedorArchivo;
import com.sysman.util.SysmanFunciones;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.event.SelectEvent;

/**
 * Este controlador puede ser llamado desde cualquier lugar, sirve para enviar correos de un correo origen a multiples destinos.
 *
 * @version 1.0, 11/06/2018
 * @author mvenegas
 */
@ManagedBean
@ViewScoped
public class FrmEnvioCorreosControlador extends BeanBaseModal {
    /**
     * Constante a nivel de clase que almacena el codigo de la compania en la cual inicio sesion el usuario, el valor de esta constante es asignado en el constructor a la variable de sesion
     * correspondiente
     */
    private final String compania;
    // <DECLARAR_ATRIBUTOS>
    /**
     * Este atributo se usa como auxiliar del componente selector de archivos txtAdjunto y funciona como contenedor del archivo que se debe guardar
     */
    private ContenedorArchivo contArchivotxtAdjunto;
    /**
     * Variable que almacena los datos de listacmbCorreo
     */
    private RegistroDataModelImpl listacmbCorreo;
    /**
     * Variable que almacena los datos de listalistaDestinos (LISTA MULTIPLE)
     */
    private RegistroDataModelImpl listalistaDestinos;
    /**
     * Constante que almacena la palabra PLANTILLA
     */
    private String cPlantilla;
    /**
     * Variable que almacena el valor del correo que se desea enviar
     */
    private String comboCorreos;
    /**
     * Variable que almacena el asunto del correo seleccionado
     */
    private String asunto;
    /**
     * Variable que almacena el proceso desde el cual se esta llamando el formulario
     */
    private String proceso;
    /**
     * Variable que almacena el origen del correo
     */
    private String origen;
    /**
     * Variable que almacena la descripcion del correo
     */
    private String descripcion;
    /**
     * Variable que indica el lugar desde donse se carga la lista 1: inicilizar, 2: selecciobar codigo
     */
    private int indicadorCargaLista;
    /**
     * Esta lista va a almacenar los correos destino seleccionados
     */
    private List<Registro> listaSeleccionados;
    /**
     * parametros enviados desde el controlador que se llama este formulario
     */
    private Map<String, Object> parametrosRecibidos;

    // </DECLARAR_LISTAS_COMBO_GRANDE>
    // <DECLARAR_LISTAS_SUBFORM>
    // </DECLARAR_LISTAS_SUBFORM>
    // <DECLARAR_PARAMETROS>
    // </DECLARAR_PARAMETROS>
    // <DECLARAR_ADICIONALES>
    // </DECLARAR_ADICIONALES>
    /**
     * Crea una nueva instancia de FrmEnvioCorreosControlador
     */
    public FrmEnvioCorreosControlador() {
        super();
        compania = SessionUtil.getCompania();
        parametrosRecibidos = SessionUtil.getFlash();
        indicadorCargaLista = 0;
        cPlantilla = "PLANTILLA";
        listaSeleccionados = null;
        try {
            numFormulario = GeneralCodigoFormaEnum.FRM_ENVIO_CORREOS
                            .getCodigo();
            validarPermisos();
            contArchivotxtAdjunto = new ContenedorArchivo();
            // <INI_ADICIONAL>
            // </INI_ADICIONAL>
        }
        catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
            SessionUtil.redireccionarMenuPermisos();
        }

    }

    /**
     * En este metodo se hace la invocacion de lo metodos de carga de todas las listas, menos las que son de subformularios
     */
    @PostConstruct
    public void inicializar() {
        // <CARGAR_LISTA_COMBO_GRANDE>
        indicadorCargaLista = 1;
        cargarListacmbCorreo();
        cargarListalistaDestinos();
        abrirFormulario();
        // </CARGAR_LISTA_COMBO_GRANDE>
        // <CARGAR_LISTA>
        // </CARGAR_LISTA>
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
     * Carga la lista listacmbCorreo
     *
     */
    public void cargarListacmbCorreo() {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        FrmEnvioCorreosControladorUrlEnum.URL001
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put("PROCESO", parametrosRecibidos.get("procesoCorreo").toString());

        listacmbCorreo = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, GeneralParameterEnum.CONSECUTIVO.getName());
    }

    /**
     * 
     * Carga la lista listalistaDestinos
     *
     */
    public void cargarListalistaDestinos() {

        try {
            UrlBean urlBean = UrlServiceUtil.getInstance()
                            .getUrlServiceByUrlByEnumID(
                                            FrmEnvioCorreosControladorUrlEnum.URL002
                                                            .getValue());

            Map<String, Object> param = new TreeMap<>();
            param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

            if (indicadorCargaLista == 1) {
                param.put(cPlantilla, "-1");
            }
            else if (indicadorCargaLista == 0) {
                limpiarListaMultiple();
                listalistaDestinos = new RegistroDataModelImpl();
                param.put(cPlantilla, "-1");
            }
            else {
                limpiarListaMultiple();
                listalistaDestinos = new RegistroDataModelImpl();
                param.put(cPlantilla, comboCorreos);

            }

            listalistaDestinos = new RegistroDataModelImpl(urlBean.getUrl(),
                            urlBean.getUrlConteo().getUrl(),
                            param,
                            false,
                            CacheUtil.getLlaveServicio(UrlServiceCache.SYSMANIRISST,
                                            "EMAIL_DESTINO"),
                            true);
        }
        catch (SysmanException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    // </METODOS_CARGAR_LISTA>
    // <METODOS_CAMBIAR>
    // </METODOS_CAMBIAR>
    // <METODOS_COMBOS_GRANDES>
    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista listacmbCorreo
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilacmbCorreo(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        comboCorreos = retornarString(registroAux, "CONSECUTIVO");
        proceso = retornarString(registroAux, "NOMBRE_PROCESO");
        origen = retornarString(registroAux, "ORIGEN");
        asunto = retornarString(registroAux, "ASUNTO");
        descripcion = retornarString(registroAux, "DESCRIPCION");

        indicadorCargaLista = SysmanFunciones.toString(comboCorreos) == null || SysmanFunciones.toString(comboCorreos).isEmpty() ? 0 : 2;
        cargarListalistaDestinos();

    }

    private String retornarString(Registro reg, String campo) {
        return SysmanFunciones.validarCampoVacio(reg.getCampos(), campo) ? ""
                        : reg.getCampos().get(campo).toString();
    }

    private String correosDestino() {
        if (!listalistaDestinos.getSeleccionados().isEmpty()) {
            listaSeleccionados = listalistaDestinos.getSeleccionados();
            String salida = "";

            for (Registro reg : listaSeleccionados) {
                salida = SysmanFunciones.concatenar(salida, SysmanFunciones.toString(reg.getCampos().get("CORREO_DESTINATARIO")), ",");
            }

            return salida.substring(0, (salida.length() - 1));
        }
        else {
            JsfUtil.agregarMensajeError("No ha seleccionado ningun correo destino.");

            return "";
        }

    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista listalistaDestinos
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilalistaDestinos(SelectEvent event) {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    // </METODOS_COMBOS_GRANDES>
    // <METODOS_ARBOL>
    // </METODOS_ARBOL>
    // <METODOS_BOTONES>
    /**
     * 
     * Metodo ejecutado al oprimir el boton btnEnviar en la vista
     *
     *
     */
    public void oprimirbtnEnviar() {
        // <CODIGO_DESARROLLADO>
        /**
         * En este caso se debe personalizar que cuando el consecutivo es 4 ("SOLICITUD DE DISPONIBILIDAD RECHAZADA") el correo destino debe ser la persona que hizo la solicitud
         */
        String correosDestino = "";
        if (comboCorreos.equals("4")) {
            correosDestino = parametrosRecibidos.get("correoRechazada").toString();
        }
        else {
            correosDestino = correosDestino();
        }

        if ((!correosDestino.equals("")) && (!descripcion.isEmpty())) {
            @SuppressWarnings("unchecked")
            String descripcionFinal = remplazarVariable(descripcion, (Map<String, Object>) parametrosRecibidos.get("remplazos"));

            EmailPojo email = new EmailPojo();
            email.setFrom(origen);
            email.setTo(correosDestino);
            email.setSubject(asunto);
            email.setBody(descripcionFinal);

            if (contArchivotxtAdjunto.getArchivo() != null) {
                String rutaArchivo = contArchivotxtAdjunto.getArchivo().getPath();
                email.setRuta(rutaArchivo);
            }

            ApiRestClient client = new ApiRestClient();

            try {
                client.postClient(email);
                JsfUtil.agregarMensajeInformativo("Alerta de email enviada correctamente.");
            }
            catch (IOException e) {
                logger.error(e.getMessage(), e);
                JsfUtil.agregarMensajeError(e.getMessage());
            }

        }
        else {
            JsfUtil.agregarMensajeError("No se pudo realizar el envio de la alerta.");
        }
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo que remplaza las variables de la descripcion del correo
     */
    public String remplazarVariable(String descripcion, Map<String, Object> parametros) {
        String salida = descripcion;

        for (Map.Entry<String, Object> entry : parametros.entrySet()) {
            salida = salida.replace(SysmanFunciones.concatenar("s$", entry.getKey(), "$s"), SysmanFunciones.toString(entry.getValue()));
        }

        return salida;
    }

    /**
     * 
     * Metodo ejecutado al oprimir el boton Quitar en la vista
     *
     *
     */
    public void oprimirQuitar() {
        // <CODIGO_DESARROLLADO>
        limpiarListaMultiple();
        // </CODIGO_DESARROLLADO>
    }

    public void limpiarListaMultiple() {
        if (!listalistaDestinos.getSeleccionados().isEmpty()) {
            listalistaDestinos.getSeleccionados().clear();
            listalistaDestinos.getLlavesSeleccionadas().clear();
            listalistaDestinos.getFiltradoMultiple().clear();
        }
    }

    // <SET_GET_ATRIBUTOS>
    /**
     * Retorna el objeto contArchivotxtAdjunto
     * 
     * @return contArchivotxtAdjunto
     */
    public ContenedorArchivo getContArchivotxtAdjunto() {
        return contArchivotxtAdjunto;
    }

    /**
     * Asigna el objeto contArchivotxtAdjunto
     * 
     * @param contArchivotxtAdjunto
     * Variable a asignar en contArchivotxtAdjunto
     */
    public void setContArchivotxtAdjunto(
                    ContenedorArchivo contArchivotxtAdjunto) {
        this.contArchivotxtAdjunto = contArchivotxtAdjunto;
    }

    // </SET_GET_ATRIBUTOS>
    // <SET_GET_LISTAS>
    // </SET_GET_LISTAS>
    // <SET_GET_LISTAS_COMBO_GRANDE>
    /**
     * Retorna la lista listacmbCorreo
     * 
     * @return listacmbCorreo
     */
    public RegistroDataModelImpl getListacmbCorreo() {
        return listacmbCorreo;
    }

    /**
     * Asigna la lista listacmbCorreo
     * 
     * @param listacmbCorreo
     * Variable a asignar en listacmbCorreo
     */
    public void setListacmbCorreo(RegistroDataModelImpl listacmbCorreo) {
        this.listacmbCorreo = listacmbCorreo;
    }

    /**
     * Retorna la lista listalistaDestinos
     * 
     * @return listalistaDestinos
     */
    public RegistroDataModelImpl getListalistaDestinos() {
        return listalistaDestinos;
    }

    /**
     * Asigna la lista listalistaDestinos
     * 
     * @param listalistaDestinos
     * Variable a asignar en listalistaDestinos
     */
    public void setListalistaDestinos(RegistroDataModelImpl listalistaDestinos) {
        this.listalistaDestinos = listalistaDestinos;
    }
    // </SET_GET_LISTAS_COMBO_GRANDE>
    // <SET_GET_LISTAS_SUBFORM>
    // </SET_GET_LISTAS_SUBFORM>
    // <SET_GET_PARAMETROS>
    // </SET_GET_PARAMETROS>
    // <SET_GET_ADICIONALES>
    // </SET_GET_ADICIONALES>

    public String getComboCorreos() {
        return comboCorreos;
    }

    public void setComboCorreos(String comboCorreos) {
        this.comboCorreos = comboCorreos;
    }

    public String getAsunto() {
        return asunto;
    }

    public void setAsunto(String asunto) {
        this.asunto = asunto;
    }

    public String getProceso() {
        return proceso;
    }

    public void setProceso(String proceso) {
        this.proceso = proceso;
    }

    public String getOrigen() {
        return origen;
    }

    public void setOrigen(String origen) {
        this.origen = origen;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public Map<String, Object> getParametrosRecibidos() {
        return parametrosRecibidos;
    }

    public void setParametrosRecibidos(Map<String, Object> parametrosRecibidos) {
        this.parametrosRecibidos = parametrosRecibidos;
    }
}
