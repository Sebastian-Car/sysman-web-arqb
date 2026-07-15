/*-
 * SpempresasControlador.java
 *
 * 1.0
 *
 * 27/07/2017
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
import com.sysman.enums.GenericUrlEnum;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.recursos.ejb.EjbSysmanUtilRemote;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.serviciospublicos.enums.SpempresasControladorUrlEnum;
import com.sysman.util.SysmanFunciones;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.Map;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.apache.poi.util.IOUtils;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.event.SelectEvent;
import org.primefaces.model.UploadedFile;

/**
 * Clase que permite el registro de las empresas externas de aseo
 *
 * @version 1.0, 27/07/2017
 * @author jrodriguezr
 */
@ManagedBean
@ViewScoped
public class SpempresasControlador extends BeanBaseDatosAcmeImpl {
    /**
     * Constante a nivel de clase que almacena el codigo de la
     * compania en la cual inicio sesion el usuario, el valor de esta
     * constante es asignado en el constructor a la variable de sesion
     * correspondiente
     */
    private final String compania;

    // <DECLARAR_ATRIBUTOS>
    @EJB
    private EjbSysmanUtilRemote ejbSysmanUtil;

    private RegistroDataModelImpl listaNIT;

    /**
     * Arreglo de bytes que contiene la imagen de la compañia que se
     * carga desde el componente de Primefaces.
     */
    private byte[] imagenBytes;

    /**
     * Valor del atributo correspondiente a nombre Icono Compania
     */
    private String nombreIconoCompania;
    /**
     * Valor del atributo correspondiente a la ruta del icono
     */
    private String iconoCompania;
    /**
     * Valor del atributo correspondiente a directorio donde se
     * encuentra el icono
     */
    private String directorio;
    /**
     * Valor del atributo correspondiente al stream de la imagen a
     * cargar.
     */
    private InputStream inputStreamImagen;

    // </DECLARAR_ATRIBUTOS>
    // <DECLARAR_LISTAS>
    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>
    // </DECLARAR_LISTAS_COMBO_GRANDE>
    // <DECLARAR_LISTAS_SUBFORM>
    // </DECLARAR_LISTAS_SUBFORM>
    // <DECLARAR_PARAMETROS>
    // </DECLARAR_PARAMETROS>
    // <DECLARAR_ADICIONALES>
    // </DECLARAR_ADICIONALES>
    /**
     * Crea una nueva instancia de SpempresasControlador
     */
    public SpempresasControlador() {
        super();
        compania = SessionUtil.getCompania();
        try {
            numFormulario = GeneralCodigoFormaEnum.SP_EMPRESAS_CONTROLADOR
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
        cargarListaNIT();

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
        cargarImagen();
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
        enumBase = GenericUrlEnum.SP_EMPRESAS_TERCERIZA;
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
        buscarUrls();
    }

    // <METODOS_CARGAR_LISTA>
    /**
     * Carga la lista listaNIT
     */
    public void cargarListaNIT() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        SpempresasControladorUrlEnum.URL0001
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        listaNIT = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, "NIT");
    }

    // </METODOS_CARGAR_LISTA>
    // <METODOS_CAMBIAR>
    /**
     * Carga el archivo de la imagen en el sistema
     *
     * @param event
     * archivo a subir
     */
    public void cargarArchivoLogo(FileUploadEvent event) {
        UploadedFile archivoImagen = event.getFile();
        nombreIconoCompania = archivoImagen.getFileName();
        imagenBytes = getFileContent(archivoImagen);
        iconoCompania = JsfUtil.encodeImage(imagenBytes);

    }

    /**
     * Actualiza la imagen seleccionada o que ya se encuentra
     * guardada.
     */
    private void cargarImagen() {
        iconoCompania = null;
        String rutaImagen = SysmanFunciones.nvl(
                        registro.getCampos().get("LOGO"), "").toString();
        File file = new File(rutaImagen);
        if (!rutaImagen.isEmpty()) {
            try (FileInputStream inputStream = new FileInputStream(file)) {
                nombreIconoCompania = rutaImagen.substring(
                                rutaImagen.lastIndexOf(File.separator) + 1,
                                rutaImagen.length());

                directorio = SysmanFunciones
                                .concatenar(rutaImagen.substring(0,
                                                rutaImagen.lastIndexOf(
                                                                File.separator)),
                                                File.separator);
                iconoCompania = JsfUtil.encodeImage(rutaImagen);
                inputStreamImagen = inputStream;
            }
            catch (FileNotFoundException ex) {
                logger.error(ex.getMessage(), ex);
                JsfUtil.agregarMensajeAlerta(SysmanFunciones.concatenar(
                                idioma.getString("TB_TB125"), directorio));
            }
            catch (Exception ex) {
                logger.error(ex.getMessage(), ex);
                JsfUtil.agregarMensajeFatal(SysmanFunciones.concatenar(
                                idioma.getString("TB_TB2840"), " ", rutaImagen,
                                "<br>", ex.getMessage()));
            }
            finally {
                if (inputStreamImagen != null) {
                    try {
                        inputStreamImagen.close();
                    }
                    catch (IOException e) {
                        logger.error(e.getMessage(), e);
                        JsfUtil.agregarMensajeError(e.getMessage());
                    }
                }
            }
        }
        JsfUtil.ejecutarJavaScript(
                        "cargarImagen('FR1405_nuevo:TS60:IM1408')");
    }

    /**
     * Recibe el archivo que se cargo en el selector de imagen y lo
     * comnvierte a un arreglo de bytes.
     *
     * @param file
     * Archivo subido por medio del componente <i>fileUpload</i> de
     * Primefaces.
     * @return Archivo como arreglo de bytes.
     */
    private byte[] getFileContent(UploadedFile file) {
        byte[] bytes = new byte[0];
        try (InputStream stream = file.getInputstream();) {
            bytes = IOUtils.toByteArray(stream);
            JsfUtil.agregarMensajeInformativo(idioma.getString("TB_TB2839"));
        }
        catch (IOException ex) {
            logger.error(ex.getMessage(), ex);
            JsfUtil.agregarMensajeError(SysmanFunciones.concatenar(
                            idioma.getString("MSM_TRANS_INTERRUMPIDA"), "<br>",
                            ex.getMessage()));
        }
        return bytes;
    }

    // </METODOS_CAMBIAR>
    // <METODOS_COMBOS_GRANDES>
    /**
     *
     * Metodo ejecutado al seleccionar una fila de la lista listaNIT
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaNIT(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        registro.getCampos().put("NIT", registroAux.getCampos().get("NIT"));
        registro.getCampos().put(GeneralParameterEnum.NOMBRE.getName(),
                        registroAux.getCampos().get(
                                        GeneralParameterEnum.NOMBRE.getName()));
        registro.getCampos().put(GeneralParameterEnum.SUCURSAL.getName(),
                        registroAux.getCampos()
                                        .get(GeneralParameterEnum.SUCURSAL
                                                        .getName()));
        try {
            registro.getCampos()
                            .put("DV", ejbSysmanUtil
                                            .generarDigitoDeVerificacion(
                                                            registro.getCampos()
                                                                            .get("NIT")
                                                                            .toString()));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    // </METODOS_COMBOS_GRANDES>
    // <METODOS_ARBOL>
    // </METODOS_ARBOL>
    // <METODOS_BOTONES>
    /**
     * Metodo que valida el archivo al momento de subirlo en el
     * sistema, no se puede bajar la complejidad
     *
     * @return true si es un archivo valido
     */
    private boolean validarArchivoImagen() {
        if (!SysmanFunciones.validarVariableVacio(iconoCompania)) {
            try {
                File ficheroImagen = new File(directorio);
                String subExtImagen = nombreIconoCompania.substring(
                                nombreIconoCompania.lastIndexOf('.'),
                                nombreIconoCompania.length());

                if (SysmanFunciones.validarVariableVacio(directorio)) {
                    JsfUtil.agregarMensajeError(idioma.getString("TB_TB96"));
                    return false;
                }

                if (!tieneExtensionValida(nombreIconoCompania)) {
                    JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB97"));
                    return false;
                }
                String prefijo = idioma.getString("TB_TB99");
                String codigoCompania = registro.getCampos()
                                .get("ID").toString();
                nombreIconoCompania = SysmanFunciones.concatenar(prefijo,
                                codigoCompania, subExtImagen);
                if (!directorio.endsWith("\\")) {
                    directorio = SysmanFunciones.concatenar(directorio, "\\");
                }
                String ruta = SysmanFunciones.concatenar(directorio,
                                nombreIconoCompania);
                registro.getCampos().put("LOGO", ruta);

                if (ficheroImagen.exists()) {
                    return true;
                }
                else if (ficheroImagen.isDirectory()) {
                    ficheroImagen.mkdir();
                    return true;
                }
                else {
                    JsfUtil.agregarMensajeAlertaVentana(
                                    idioma.getString("TB_TB103"));
                    return false;
                }
            }
            catch (NullPointerException e) {
                logger.error(e.getMessage(), e);
                JsfUtil.agregarMensajeError(idioma.getString("TB_TB104"));
                return false;
            }
        }

        return true;
    }

    /**
     * Este metodo permite validar la extension del archivo
     * seleccionado
     *
     * @param nombreArchivo
     * @return true si es una extension valida
     */
    private boolean tieneExtensionValida(String nombreArchivo) {
        String regex = "([^\\s]+(\\.(?i)(jpg|png|gif|bmp))$)";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(nombreArchivo);
        return matcher.matches();
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
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado en el momento despues de cargar el registro
     */
    @Override
    public void cargarRegistro() {
        // <CODIGO_DESARROLLADO>
        precargarRegistro();
        if (accion.equals(ACCION_INSERTAR)) {
            directorio = null;
            inputStreamImagen = null;
            imagenBytes = null;
            nombreIconoCompania = null;
            cargarImagen();
        }
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado antes de realizar la insercion del registro
     *
     * @return VARIABLE
     */
    @Override
    public boolean insertarAntes() {
        // <CODIGO_DESARROLLADO>
        if (ACCION_INSERTAR.equals(accion)) {
            registro.getCampos().put(GeneralParameterEnum.COMPANIA.getName(),
                            compania);
            registro.getCampos().put(GeneralParameterEnum.ANO.getName(),
                            SysmanFunciones.ano(new Date()));
        }

        // </CODIGO_DESARROLLADO>
        return true;
    }

    /**
     * Metodo ejecutado despues de realizar la insercion del registro
     *
     * @return VARIABLE
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
     * @return VARIABLE
     */
    @Override
    public boolean actualizarAntes() {
        // <CODIGO_DESARROLLADO>
        try {
            if (validarArchivoImagen()) {
                if (accion.equals(ACCION_MODIFICAR)) {
                    registro.getCampos().remove(
                                    GeneralParameterEnum.COMPANIA.getName());
                    registro.getCampos().remove("ID");
                    registro.getCampos()
                                    .remove(GeneralParameterEnum.ANO.getName());
                }
                return true;
            }
            else {
                return false;
            }
        }
        catch (NullPointerException e) {
            logger.error(e.getMessage(), e);
            return false;
        }
    }

    /**
     * Metodo ejecutado despues de realizar la insercion y
     * actualizacion del registro
     *
     * @return VARIABLE
     */
    @Override
    public boolean actualizarDespues() {
        // <CODIGO_DESARROLLADO>
        if ((imagenBytes != null) && (directorio != null)
            && (nombreIconoCompania != null)) {
            JsfUtil.upload(imagenBytes, directorio, nombreIconoCompania);
        }
        JsfUtil.ejecutarJavaScript("cargarImagen('FR1405_nuevo:TS60:IM1408')");
        // </CODIGO_DESARROLLADO>
        return true;
    }

    /**
     * Metodo ejecutado antes de realizar la eliminacion del registro
     *
     * @return VARIABLE
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
     * @return VARIABLE
     */
    @Override
    public boolean eliminarDespues() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
        return true;
    }
    // <SET_GET_ATRIBUTOS>

    // </SET_GET_ATRIBUTOS>
    // <SET_GET_LISTAS>

    public RegistroDataModelImpl getListaNIT() {
        return listaNIT;
    }

    public String getNombreIconoCompania() {
        return nombreIconoCompania;
    }

    public void setNombreIconoCompania(String nombreIconoCompania) {
        this.nombreIconoCompania = nombreIconoCompania;
    }

    public String getIconoCompania() {
        return iconoCompania;
    }

    public void setIconoCompania(String iconoCompania) {
        this.iconoCompania = iconoCompania;
    }

    public byte[] getImagenBytes() {
        return imagenBytes;
    }

    public void setImagenBytes(byte[] imagenBytes) {
        this.imagenBytes = imagenBytes;
    }

    public String getDirectorio() {
        return directorio;
    }

    public void setDirectorio(String directorio) {
        this.directorio = directorio;
    }

    public InputStream getInputStreamImagen() {
        return inputStreamImagen;
    }

    public void setInputStreamImagen(InputStream inputStreamImagen) {
        this.inputStreamImagen = inputStreamImagen;
    }

    public void setListaNIT(RegistroDataModelImpl listaNIT) {
        this.listaNIT = listaNIT;
    }

    // </SET_GET_LISTAS>
    // <SET_GET_LISTAS_COMBO_GRANDE>
    // </SET_GET_LISTAS_COMBO_GRANDE>
    // <SET_GET_LISTAS_SUBFORM>
    // </SET_GET_LISTAS_SUBFORM>
    // <SET_GET_PARAMETROS>
    // </SET_GET_PARAMETROS>
    // <SET_GET_ADICIONALES>
    // </SET_GET_ADICIONALES>
}
