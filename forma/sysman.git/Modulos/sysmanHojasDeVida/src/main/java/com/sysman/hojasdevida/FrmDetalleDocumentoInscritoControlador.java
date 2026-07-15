/*-
 * FrmDetalleDocumentoInscritoControlador.java
 *
 * 1.0
 * 
 * 20/06/2018
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
import com.sysman.exception.SystemException;
import com.sysman.general.PlantillaswordsControlador;
import com.sysman.hojasdevida.enums.FrmDetalleAnexoControladorEnum;
import com.sysman.hojasdevida.enums.FrmDetalleDocumentoInscritoControladorEnum;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.recursos.ejb.EjbSysmanUtilRemote;
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
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.apache.commons.io.FilenameUtils;
import org.primefaces.context.RequestContext;
import org.primefaces.event.RowEditEvent;
import org.primefaces.model.StreamedContent;
import org.primefaces.model.UploadedFile;

import net.sf.jasperreports.engine.JRException;

/**
 * Clase migrada para administrar los documentos a anexar
 *
 * @version 1.0, 20/06/2018
 * @author lbotia
 */
@ManagedBean
@ViewScoped
public class FrmDetalleDocumentoInscritoControlador
                extends BeanBaseContinuoAcmeImpl {
    /**
     * Constante a nivel de clase que almacena el codigo de la
     * compania en la cual inicio sesion el usuario, el valor de esta
     * constante es asignado en el constructor a la variable de sesion
     * correspondiente
     */
    private final String compania;

    private String idEvento;

    private String tipoEvento;

    private String numeroDcto;

    private String sucursal;

    /**
     * Atributo que se usa para almacenar la ruta a la cual debe ir el
     * archivo seleccionado
     */
    private String ruta;
    // <DECLARAR_ATRIBUTOS>
    /**
     * Atributo usado para descargar contenidos de archivos desde la
     * vista
     */
    private StreamedContent archivoDescarga;
    /**
     * Este atributo se usa como auxiliar del componente referencia de
     * archivos nuevoArchivo y funciona como contenedor del archivo
     * que se desea cargar
     */
    private UploadedFile archivoCarganuevoArchivo;

    @EJB
    private EjbSysmanUtilRemote ejbSymanUtl;

    // </DECLARAR_ATRIBUTOS>
    // <DECLARAR_PARAMETROS>
    // </DECLARAR_PARAMETROS>
    // <DECLARAR_LISTAS>
    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>
    // </DECLARAR_LISTAS_COMBO_GRANDE>
    /**
     * Crea una nueva instancia de
     * FrmDetalleDocumentoInscritoControlador
     */
    public FrmDetalleDocumentoInscritoControlador() {
        super();
        compania = SessionUtil.getCompania();
        try {
            // 1834
            numFormulario = GeneralCodigoFormaEnum.FRM_DETALLE_DOCUMENTO_INSCRITO_CONTROLADOR
                            .getCodigo();
            validarPermisos();

            Map<String, Object> parametrosEntrada = SessionUtil.getFlash();
            if (parametrosEntrada != null) {

                idEvento = parametrosEntrada
                                .get("idevento")
                                .toString();

                tipoEvento = parametrosEntrada
                                .get("tipoevento")
                                .toString();

                numeroDcto = parametrosEntrada.get("numerodcto")
                                .toString();

                sucursal = parametrosEntrada.get("sucursal")
                                .toString();

            }

            // <INI_ADICIONAL>
            // </INI_ADICIONAL>
        }
        catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
            SessionUtil.redireccionarMenuPermisos();
        }
        finally {
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
        enumBase = GenericUrlEnum.SST_DOCUMENTO_INSCRITO;
        buscarLlave();
        reasignarOrigen();
        registro = new Registro();
        // <CARGAR_LISTA>
        // </CARGAR_LISTA>
        // <CARGAR_LISTA_COMBO_GRANDE>
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
                        .put(FrmDetalleDocumentoInscritoControladorEnum.IDEVENTO
                                        .getValue(), idEvento);
        parametrosListado.put(
                        FrmDetalleDocumentoInscritoControladorEnum.TIPOEVENTO
                                        .getValue(),
                        tipoEvento);
        parametrosListado.put(
                        FrmDetalleDocumentoInscritoControladorEnum.NUMERO_DCTO
                                        .getValue(),
                        numeroDcto);
        parametrosListado
                        .put(FrmDetalleDocumentoInscritoControladorEnum.SUCURSAL
                                        .getValue(), sucursal);
    }

    // <METODOS_CARGAR_LISTA>
    // </METODOS_CARGAR_LISTA>
    // <METODOS_BOTONES>
    /**
     * 
     * Metodo ejecutado al oprimir el boton Adjuntar en la vista
     *
     * Metodo que permite adjuntar archivos al oprimir el botón
     *
     */
    public void oprimirAdjuntar() {
        // <CODIGO_DESARROLLADO>
        String archivo = archivoCarganuevoArchivo.getFileName();
        if (SysmanFunciones.validarVariableVacio(archivo)) {
            JsfUtil.agregarMensajeError(idioma.getString("TB_TB3979"));
            return;
        }

        Map<String, Object> llaveAnexo = new HashMap<>();

        llaveAnexo.put("KEY_COMPANIA", compania);
        llaveAnexo.put("KEY_IDEVENTO", idEvento);
        llaveAnexo.put("KEY_TIPOEVENTO", tipoEvento);
        llaveAnexo.put("KEY_NUMERO_DCTO", numeroDcto);
        llaveAnexo.put("KEY_SUCURSAL", sucursal);
        llaveAnexo.put("KEY_ARCHIVO_NOM", FilenameUtils.getName(archivo));
        // Tipo de extension del archivo seleccionado
        String extension = FilenameUtils.getExtension(archivo);

        ruta = JsfUtil.generarNombreArchivo(SessionUtil.getModulo(),
                        llaveAnexo,
                        SysmanConstantes.RUTA_DOCUMENTOS_INSCRITOS,
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
     * Genera la ruta donde se ubicaran los documentos asociados a las
     * actividades inscritas . Toma la longitud maxima que puede tomar
     * la compania, el id evento, tipo evento, numero documento y la
     * sucursal; y crea una ruta espeficica para el nodo.
     */

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

    /**
     * Metodo ejecutado al oprimir el boton Descargar
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
                        SysmanConstantes.RUTA_DOCUMENTOS_INSCRITOS,
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

    // </METODOS_BOTONES>
    // <METODOS_CAMBIAR>
    // </METODOS_CAMBIAR>
    // <METODOS_COMBOS_GRANDES>
    // </METODOS_COMBOS_GRANDES>
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
        try {
            // <CODIGO_DESARROLLADO>
            registro.getCampos().put(GeneralParameterEnum.COMPANIA.getName(),
                            compania);
            registro.getCampos()
                            .put(FrmDetalleDocumentoInscritoControladorEnum.IDEVENTO
                                            .getValue(),
                                            idEvento);
            registro.getCampos()
                            .put(FrmDetalleDocumentoInscritoControladorEnum.TIPOEVENTO
                                            .getValue(),
                                            tipoEvento);
            registro.getCampos()
                            .put(FrmDetalleDocumentoInscritoControladorEnum.NUMERO_DCTO
                                            .getValue(), numeroDcto);
            registro.getCampos()
                            .put(FrmDetalleDocumentoInscritoControladorEnum.SUCURSAL
                                            .getValue(), sucursal);

            String condicion = SysmanFunciones.concatenar("COMPANIA = ''",
                            compania, "''AND IDEVENTO = ''", idEvento,
                            "''AND TIPOEVENTO = ''",
                            tipoEvento, "''AND NUMERO_DCTO = ''", numeroDcto,
                            "''AND SUCURSAL = ''", sucursal, "''");

            long consecutivo = ejbSymanUtl.generarConsecutivoConValorInicial(
                            "SST_DOCUMENTO_INSCRITO", condicion, "CONSECUTIVO",
                            "1");

            registro.getCampos()
                            .put(FrmDetalleAnexoControladorEnum.CONSECUTIVO
                                            .getValue(), consecutivo);

            File archivoTemp = new File(ruta);
            registro.getCampos().put("NOMBRE_ARCHIVO",
                            archivoTemp.getName());
            // </CODIGO_DESARROLLADO>

        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
        return true;
    }

    /**
     * Metodo ejecutado despues de realizar la insercion del registro
     * 
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
        // Auto-generated method stub
    }

    // <SET_GET_ATRIBUTOS>
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
    // </SET_GET_LISTAS_COMBO_GRANDE>

    /**
     * @return the archivoDescarga
     */
    public StreamedContent getArchivoDescarga() {
        return archivoDescarga;
    }

    /**
     * @param archivoDescarga
     * the archivoDescarga to set
     */
    public void setArchivoDescarga(StreamedContent archivoDescarga) {
        this.archivoDescarga = archivoDescarga;
    }
}
