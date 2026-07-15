/*-
 * DetalleDocumentoAnexosControlador.java
 *
 * 1.0
 * 
 * 16/02/2018
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */
package com.sysman.hojasdevida;

import com.sysman.beanbase.BeanBaseContinuoAcmeImpl;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.enums.GenericUrlEnum;
import com.sysman.general.PlantillaswordsControlador;
import com.sysman.hojasdevida.enums.DetalleDocumentoAnexosControladorEnum;
import com.sysman.hojasdevida.enums.DetalleDocumentoAnexosControladorUrlEnum;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.util.SysmanConstantes;
import com.sysman.util.SysmanFunciones;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.apache.commons.io.FilenameUtils;
import org.primefaces.context.RequestContext;
import org.primefaces.event.RowEditEvent;
import org.primefaces.event.SelectEvent;
import org.primefaces.model.StreamedContent;
import org.primefaces.model.UploadedFile;

import net.sf.jasperreports.engine.JRException;

/**
 * Clase migrada para administrar los documentos a anexar
 *
 * @version 1.0, 16/02/2018
 * @author ybecerra
 */
@ManagedBean
@ViewScoped
public class DetalleDocumentoAnexosControlador
                extends BeanBaseContinuoAcmeImpl {
    /**
     * Constante a nivel de clase que almacena el codigo de la
     * compania en la cual inicio sesion el usuario, el valor de esta
     * constante es asignado en el constructor a la variable de sesion
     * correspondiente
     */
    private final String compania;
    // <DECLARAR_ATRIBUTOS>
    /**
     * Atributo que almacena el codigo del tipo de archivo
     * seleccionado
     */
    private String tipoArchivo;
    /**
     * Atributo que almacena la observacion digitada en el formulario
     */
    private String observaciones;
    /**
     * Variable encargada de almacenar lo obtenido por el flash y que
     * hace referencia al formulario tipotransaccion
     */
    private String tipoTransaccion;
    /**
     * Atributo que almacena el nombre de la transaccion que se esta
     * trabajando, valor que ha sido enviado por parametro
     */
    private String nombreTipoTransaccion;
    /**
     * Atributo que almacena el consecutivo de la transaccion
     * seleccionada, valor que ha sido enviado por parametro
     */
    private String consecutivoTransaccion;
    /**
     * Este atributo se usa como auxiliar del componente referencia de
     * archivos nuevoArchivo y funciona como contenedor del archivo
     * que se desea cargar
     */
    private UploadedFile archivoCarganuevoArchivo;
    /**
     * Atributo que se usa para almacenar la ruta a la cual debe ir el
     * archivo seleccionado
     */
    private String ruta;
    /**
     * Atributo que almacena el titulo del informe ANEXOS + nombre de
     * la transaccion
     */
    private String nombreTransaccion;

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
    /**
     * Lista de registros de los documentos
     */
    private RegistroDataModelImpl listaDocumento;
    /**
     * Lista de registros de los documentos
     */
    private RegistroDataModelImpl listaDocumentoE;
    /**
     * Esta variable se usa como auxiliar para subformularios y en
     * esta se alamcena el identificador del registro que se
     * selecciono
     */
    private String auxiliar;

    // </DECLARAR_LISTAS_COMBO_GRANDE>
    /**
     * Crea una nueva instancia de DetalleDocumentoAnexosControlador
     */
    public DetalleDocumentoAnexosControlador() {
        super();
        compania = SessionUtil.getCompania();
        try {
            // 1700
            numFormulario = GeneralCodigoFormaEnum.DETALLE_DOCUMENTO_ANEXO_CONTROLADOR
                            .getCodigo();
            validarPermisos();
            // <INI_ADICIONAL>
            Map<String, Object> parametrosEntrada = SessionUtil.getFlash();
            if (parametrosEntrada != null) {

                tipoTransaccion = parametrosEntrada.get("tipoTransaccion")
                                .toString();

                nombreTipoTransaccion = parametrosEntrada
                                .get("nombreTransaccion")
                                .toString();

                consecutivoTransaccion = parametrosEntrada
                                .get("consecutivoTransaccion").toString();

            }
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

        enumBase = GenericUrlEnum.SST_DETALLE_DOCUMENTO_ANEXO;
        reasignarOrigen();
        buscarLlave();
        registro = new Registro();
        // <CARGAR_LISTA>
        // </CARGAR_LISTA>
        // <CARGAR_LISTA_COMBO_GRANDE>
        cargarListaDocumento();
        cargarListaDocumentoE();
        // </CARGAR_LISTA_COMBO_GRANDE>
        abrirFormulario();

    }

    /**
     * En este metodo se asigna al atributo origenDatos del bean base
     * el valor de la consulta del formulario. Tambien carga la lista
     * del formulario por primera vez
     */
    @Override
    public void reasignarOrigen() {
        buscarUrls();
        parametrosListado.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
        parametrosListado
                        .put(DetalleDocumentoAnexosControladorEnum.TIPO_TRANSACCION
                                        .getValue(), tipoTransaccion);
        parametrosListado
                        .put(DetalleDocumentoAnexosControladorEnum.NUMERO_TRANSACCION
                                        .getValue(), consecutivoTransaccion);
    }

    // <METODOS_CARGAR_LISTA>
    /**
     * 
     * Carga la lista listaDocumento
     *
     */
    public void cargarListaDocumento() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        DetalleDocumentoAnexosControladorUrlEnum.URL174
                                                        .getValue());

        Map<String, Object> param = new TreeMap<>();

        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(DetalleDocumentoAnexosControladorEnum.TIPO_TRANSACCION
                        .getValue(), tipoTransaccion);

        listaDocumento = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param, true,
                        DetalleDocumentoAnexosControladorEnum.TIPO_DOCUMENTO
                                        .getValue());
    }

    /**
     * 
     * Carga la lista listaDocumento
     *
     */
    public void cargarListaDocumentoE() {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        DetalleDocumentoAnexosControladorUrlEnum.URL174
                                                        .getValue());

        Map<String, Object> param = new TreeMap<>();

        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        listaDocumentoE = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param, true,
                        DetalleDocumentoAnexosControladorEnum.TIPO_DOCUMENTO
                                        .getValue());

    }

    // </METODOS_CARGAR_LISTA>
    // <METODOS_BOTONES>
    /**
     * 
     * Metodo ejecutado al oprimir el boton Guardar en la vista
     *
     *
     */
    public void oprimirAdjuntar() {
        // <CODIGO_DESARROLLADO>

        // Nombre del archivo seleccionado
        String archivo = archivoCarganuevoArchivo.getFileName();
        if (SysmanFunciones.validarVariableVacio(tipoArchivo)) {
            JsfUtil.agregarMensajeError(idioma.getString("TB_TB3980"));
            return;
        }
        else if (SysmanFunciones.validarVariableVacio(archivo)) {
            JsfUtil.agregarMensajeError(idioma.getString("TB_TB3979"));
            return;
        }
        Map<String, Object> llaveAnexo = new HashMap<>();

        llaveAnexo.put("KEY_COMPANIA", compania);
        llaveAnexo.put("KEY_TIPO_TRANSACCION", tipoTransaccion);
        llaveAnexo.put("KEY_NUMERO_TRANSACCION", consecutivoTransaccion);
        llaveAnexo.put("KEY_TIPO_DOCUMENTO", tipoArchivo);
        // Tipo de extension del archivo seleccionado
        String extension = FilenameUtils.getExtension(archivo);

        ruta = JsfUtil.generarNombreArchivo(SessionUtil.getModulo(),
                        llaveAnexo,
                        SysmanConstantes.RUTA_DOCUMENTOS_TRANSACCIONES,
                        extension,
                        "");
        try {
            JsfUtil.upload(archivoCarganuevoArchivo.getInputstream(), ruta);
            agregarRegistroNuevo(null);
            reasignarOrigen();
        }
        catch (IOException e) {

            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado al oprimir el boton Descargar
     * 
     * 
     * @param reg
     * registro en el cual esta ubicado el boton oprimido dentro de la
     * grilla
     * @param indice
     * indice en el cual esta ubicado el boton oprimido dentro de la
     * grilla
     */
    public void oprimirDescargar(Registro reg, int indice) {
        // <CODIGO_DESARROLLADO>
        archivoDescarga = null;
        String nombreArchivo = String
                        .valueOf(reg.getCampos().get("NOMBRE_ARCHIVO"));

        String rutaDes = JsfUtil.generarRuta(SessionUtil.getModulo(),
                        "",
                        SysmanConstantes.RUTA_DOCUMENTOS_TRANSACCIONES,
                        nombreArchivo);

        File anexo = new File(rutaDes);
        try (InputStream fis = new FileInputStream(anexo)) {

            byte[] vec = new byte[(int) anexo.length()];
            fis.read(vec, 0, vec.length);
            archivoDescarga = JsfUtil.getArchivoDescarga(
                            new ByteArrayInputStream(vec), anexo.getName());
        }
        catch (FileNotFoundException ex) {
            Logger.getLogger(PlantillaswordsControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            JsfUtil.agregarMensajeAlerta("El archivo a descargar no existe");
        }
        catch (JRException | IOException ex) {
            Logger.getLogger(PlantillaswordsControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
        }
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado al oprimir el boton Eliminar
     * 
     * 
     * @param reg
     * registro en el cual esta ubicado el boton oprimido dentro de la
     * grilla
     * @param indice
     * indice en el cual esta ubicado el boton oprimido dentro de la
     * grilla
     */
    public void oprimirEliminar(Registro reg, int indice) {
        // <CODIGO_DESARROLLADO>
        eliminarReg(reg);
        // </CODIGO_DESARROLLADO>
    }

    // </METODOS_BOTONES>
    // <METODOS_CAMBIAR>
    // </METODOS_CAMBIAR>
    // <METODOS_COMBOS_GRANDES>
    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaDocumento
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaDocumento(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        tipoArchivo = SysmanFunciones
                        .nvl(registroAux.getCampos().get("TIPO_DOCUMENTO"), "")
                        .toString();
    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaDocumento
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaDocumentoE(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        auxiliar = SysmanFunciones
                        .nvl(registroAux.getCampos().get("TIPO_DOCUMENTO"), "")
                        .toString();
    }

    // </METODOS_COMBOS_GRANDES>
    /**
     * Este metodo es invocado el metodo inicializar, se ejecutan las
     * acciones a tener en cuenta en el momento de apertura del
     * formulario
     */
    @Override
    public void abrirFormulario() {
        // <CODIGO_DESARROLLADO>
        nombreTransaccion = SysmanFunciones.concatenar("ANEXOS DE", " ",
                        nombreTipoTransaccion);
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado cuando se cancela la edicion del registro
     * seleccionado
     */
    @Override
    public void cancelarEdicion(RowEditEvent event) {
        getListaInicial().load();
    }

    /**
     * Metodo ejecutado antes de realizar la insercion del registro
     * 
     * @return true
     */
    @Override
    public boolean insertarAntes() {
        // <CODIGO_DESARROLLADO>
        registro.getCampos().put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
        registro.getCampos()
                        .put(DetalleDocumentoAnexosControladorEnum.TIPO_TRANSACCION
                                        .getValue(), tipoTransaccion);
        registro.getCampos()
                        .put(DetalleDocumentoAnexosControladorEnum.NUMERO_TRANSACCION
                                        .getValue(), consecutivoTransaccion);
        registro.getCampos()
                        .put(DetalleDocumentoAnexosControladorEnum.TIPO_DOCUMENTO
                                        .getValue(), tipoArchivo);
        registro.getCampos().put(GeneralParameterEnum.OBSERVACION.getName(),
                        observaciones);
        registro.getCampos().remove("NOMBRETIPODOCUMENTO");
        File archivoTemp = new File(ruta);
        registro.getCampos().put("NOMBRE_ARCHIVO",
                        archivoTemp.getName());
        // </CODIGO_DESARROLLADO>
        return true;
    }

    /**
     * Metodo ejecutado despues de realizar la insercion del registro
     * 
     * @return true
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
     * @return true
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
     * @return true
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
     * @return true
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
     * 
     * @return true
     */
    @Override
    public boolean eliminarDespues() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
        return true;
    }

    /**
     * Este metodo se ejecuta antes enviar la accion de actualizacion,
     * en el se pueden remover valores auxiliares que no se desee o se
     * deban enviar en el registro
     */
    @Override
    public void removerCombos() {
        // <CODIGO_DESARROLLADO>
        registro.getCampos().remove(GeneralParameterEnum.COMPANIA.getName());
        registro.getCampos()
                        .remove(DetalleDocumentoAnexosControladorEnum.TIPO_TRANSACCION
                                        .getValue());
        registro.getCampos()
                        .remove(DetalleDocumentoAnexosControladorEnum.NUMERO_TRANSACCION
                                        .getValue());
        registro.getCampos().remove("NOMBRETIPODOCUMENTO");

        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado cuando se cierra el formulario
     * 
     */
    public void cerrarFormulario() {
        RequestContext.getCurrentInstance().closeDialog(null);
    }

    /**
     * Metodo ejecutado desde un comando remoto cuando se cierra el
     * formulario
     * 
     */
    public void ejecutarrcCerrar() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Este metodo es ejecutado despues de finalizar la insercion y
     * edicion del registro se usa cuando se desean agregar valores al
     * registro despues de dichas acciones
     */
    @Override
    public void asignarValoresRegistro() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    // <SET_GET_ATRIBUTOS>
    /**
     * Retorna la variable tipoArchivo
     * 
     * @return tipoArchivo
     */
    public String getTipoArchivo() {
        return tipoArchivo;
    }

    /**
     * Asigna la variable tipoArchivo
     * 
     * @param tipoArchivo
     * Variable a asignar en tipoArchivo
     */
    public void setTipoArchivo(String tipoArchivo) {
        this.tipoArchivo = tipoArchivo;
    }

    /**
     * Retorna la variable observaciones
     * 
     * @return observaciones
     */
    public String getObservaciones() {
        return observaciones;
    }

    /**
     * Asigna la variable observaciones
     * 
     * @param observaciones
     * Variable a asignar en observaciones
     */
    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }

    /**
     * Retorna la variable nombreTransaccion
     * 
     * @return nombreTransaccion
     */
    public String getNombreTransaccion() {
        return nombreTransaccion;
    }

    /**
     * Asigna la variable nombreTransaccion
     * 
     * @param nombreTransaccion
     * Variable a asignar en nombreTransaccion
     */
    public void setNombreTransaccion(String nombreTransaccion) {
        this.nombreTransaccion = nombreTransaccion;
    }

    /**
     * Atributo usado para descargar contenidos de archivos desde la
     * vista
     */
    public StreamedContent getArchivoDescarga() {
        return archivoDescarga;
    }

    /**
     * Retorna el objeto contArchivonuevoArchivo
     * 
     * @return contArchivonuevoArchivo
     */
    public UploadedFile getArchivoCarganuevoArchivo() {
        return archivoCarganuevoArchivo;
    }

    /**
     * Asigna el objeto contArchivonuevoArchivo
     * 
     * @param contArchivonuevoArchivo
     * Variable a asignar en contArchivonuevoArchivo
     */
    public void setArchivoCarganuevoArchivo(
        UploadedFile archivoCarganuevoArchivo) {
        this.archivoCarganuevoArchivo = archivoCarganuevoArchivo;
    }

    // </SET_GET_ATRIBUTOS>
    // <SET_GET_PARAMETROS>
    // </SET_GET_PARAMETROS>
    // <SET_GET_LISTAS>
    // </SET_GET_LISTAS>
    // <SET_GET_LISTAS_COMBO_GRANDE>
    /**
     * Retorna la lista listaDocumento
     * 
     * @return listaDocumento
     */
    public RegistroDataModelImpl getListaDocumento() {
        return listaDocumento;
    }

    /**
     * Asigna la lista listaDocumento
     * 
     * @param listaDocumento
     * Variable a asignar en listaDocumento
     */
    public void setListaDocumento(RegistroDataModelImpl listaDocumento) {
        this.listaDocumento = listaDocumento;
    }

    /**
     * Retorna la lista listaDocumento
     * 
     * @return listaDocumento
     */
    public RegistroDataModelImpl getListaDocumentoE() {
        return listaDocumentoE;
    }

    /**
     * Asigna la lista listaDocumento
     * 
     * @param listaDocumento
     * Variable a asignar en listaDocumento
     */
    public void setListaDocumentoE(RegistroDataModelImpl listaDocumentoE) {
        this.listaDocumentoE = listaDocumentoE;
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
}
