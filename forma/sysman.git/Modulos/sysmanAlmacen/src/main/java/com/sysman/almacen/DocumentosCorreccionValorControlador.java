/*-
 * DocumentosCorreccionValorControlador.java
 *
 * 1.0
 * 
 * 18/10/2018
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */
package com.sysman.almacen;
import com.sysman.almacen.enums.DocumentosCorreccionValorControladorUrlEnum;
import com.sysman.beanbase.BeanBaseModal;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.enums.GenericUrlEnum;
import com.sysman.exception.SystemException;
import com.sysman.general.PlantillaswordsControlador;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.kernel.api.clientwso2.beans.Parameter;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.recursos.ejb.impl.EjbSysmanUtil;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.util.ContenedorArchivo;
import com.sysman.util.SysmanFunciones;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.apache.jasper.tagplugins.jstl.core.Url;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.event.SelectEvent;
import org.primefaces.model.StreamedContent;

import net.sf.jasperreports.engine.JRException;
/**
 * Formulario que permite administrar los archivos adjuntos en una correccion de valor.
 *
 * @version 1.0, 18/10/2018
 * @author asana
 */
@ManagedBean
@ViewScoped
public class  DocumentosCorreccionValorControlador extends BeanBaseModal{
    /**
     * Constante a nivel de clase que almacena el codigo de la
     * compania en la cual inicio sesion el usuario, el valor de esta
     * constante es asignado en el constructor a la variable de sesion
     * correspondiente
     */
    private final String compania ;
    //<DECLARAR_ATRIBUTOS>
    private String tipo;
    private String movimiento;
    private Map<String, Object> parametroMovimiento;
    private String nombreArchivo;
    private String rutaDocumentos;
    private String nombre;
    private long consecutivo;
    private String extension;
    private InputStream is;
    private StreamedContent archivoDescarga;
    /**
     * Este atributo se usa como auxiliar del componente selector de
     * archivos subir y funciona como contenedor del archivo que se
     * debe guardar
     */
    private ContenedorArchivo contArchivosubir;
    //</DECLARAR_ATRIBUTOS>
    //<DECLARAR_PARAMETROS>
    //</DECLARAR_PARAMETROS>
    //<DECLARAR_LISTAS>
    //</DECLARAR_LISTAS>
    //<DECLARAR_LISTAS_COMBO_GRANDE>

    private RegistroDataModelImpl listanombre;
    
    @EJB
    private EjbSysmanUtil ejbSysmanUtil;
    //</DECLARAR_LISTAS_COMBO_GRANDE>
    /**
     * Crea una nueva instancia de DocumentosCorreccionValorControlador
     */
    public DocumentosCorreccionValorControlador() {
        super();
        compania = SessionUtil.getCompania();
        try {
            //1967
            numFormulario = GeneralCodigoFormaEnum.DOCUMENTOSCORRECCIONVALOR_CONTROLADOR.getCodigo();
            parametroMovimiento = SessionUtil.getFlash();
            
            if(parametroMovimiento != null) {
                tipo = (String) parametroMovimiento.get("tipo");
                movimiento = (String) parametroMovimiento.get("movimiento");
            }
            
            validarPermisos();
            //<INI_ADICIONAL>
            //</INI_ADICIONAL>
        } catch (Exception ex) {
            logger.error(ex.getMessage(),ex);
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
    public void inicializar(){
        //<CARGAR_LISTA>
        //</CARGAR_LISTA>
        //<CARGAR_LISTA_COMBO_GRANDE>
        cargarListanombre();
        //</CARGAR_LISTA_COMBO_GRANDE>
        //<CREAR_ARBOLES>
        //</CREAR_ARBOLES>
        abrirFormulario();
    }
    /**
     * Este metodo es invocado el metodo inicializar, se ejecutan las
     * acciones a tener en cuenta en el momento de apertura del
     * formulario
     */
    @Override
    public void abrirFormulario(){
        //<CODIGO_DESARROLLADO>
        rutaDocumentos = SysmanFunciones.concatenar(SessionUtil.getRuta(10), "Documentos", "/" );
        //</CODIGO_DESARROLLADO>
    }
    //<METODOS_CARGAR_LISTA>
    /**
     * 
     * Carga la lista listanombre
     *
     */
    public void cargarListanombre(){
        
        Map<String, Object> parametro = new HashMap<>();
        parametro.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        parametro.put(GeneralParameterEnum.TIPOMOVIMIENTO.getName(), tipo);
        parametro.put(GeneralParameterEnum.MOVIMIENTO.getName(), movimiento);
        
        UrlBean urlBean = UrlServiceUtil.getInstance().getUrlServiceByUrlByEnumID(DocumentosCorreccionValorControladorUrlEnum.URL3217.getValue());

        listanombre = new RegistroDataModelImpl(urlBean.getUrl(), urlBean.getUrlConteo().getUrl(), parametro, true,
                        GeneralParameterEnum.CONSECUTIVO.getName());
        
    }
    //</METODOS_CARGAR_LISTA>
    //<METODOS_BOTONES>
    /**
     * 
     * Metodo ejecutado al oprimir el boton descargar
     * en la vista
     *
     *
     */
    public void oprimirdescargar() {
        //<CODIGO_DESARROLLADO>
        archivoDescarga  = null;
        File plantilla = new File(rutaDocumentos
                        + nombreArchivo);
        try (InputStream fis = new FileInputStream(plantilla)) {

            byte[] vec = new byte[(int) plantilla.length()];
            fis.read(vec, 0, vec.length);
            archivoDescarga = JsfUtil.getArchivoDescarga(
                            new ByteArrayInputStream(vec), nombre);// plantilla.getName());
        }
        catch (FileNotFoundException ex) {
            Logger.getLogger(PlantillaswordsControlador.class.getName())
            .log(Level.SEVERE, null, ex);
            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB570"));
        }
        catch (JRException | IOException ex) {
            Logger.getLogger(PlantillaswordsControlador.class.getName())
            .log(Level.SEVERE, null, ex);
        }
        //</CODIGO_DESARROLLADO>
    }
    
    public void oprimireliminar() {
        
            Map<String, Object> parametro = new HashMap<>();
            parametro.put(GeneralParameterEnum.COMPANIA.getName(), compania);
            parametro.put(GeneralParameterEnum.TIPOMOVIMIENTO.getName(), tipo);
            parametro.put(GeneralParameterEnum.MOVIMIENTO.getName(), movimiento);
            parametro.put(GeneralParameterEnum.CONSECUTIVO.getName(), consecutivo);
            
            UrlBean urlDelete = UrlServiceUtil.getInstance()
                            .getUrlServiceByUrlByEnumID(DocumentosCorreccionValorControladorUrlEnum.URL3218.getValue());
            
            try {
                requestManager.delete(urlDelete.getUrl(), parametro);
                
                JsfUtil.agregarMensajeInformativo(
                                idioma.getString("MSM_REGISTRO_ELIMINADO"));
            }
            catch (SystemException e) {
                logger.error(e.getMessage(), e);
                JsfUtil.agregarMensajeError(e.getMessage());
            }
            
            nombre = null;
            consecutivo = 0;
            cargarListanombre();
        }
    
    public void cargarArchivosubir(FileUploadEvent event) {
        
        try {
            String condicion; 
            
            condicion = SysmanFunciones.concatenar(" COMPANIA = ''", compania, "'' AND TIPOMOVIMIENTO = ''", tipo, 
                                                    "'' AND MOVIMIENTO = ", movimiento );
            consecutivo = ejbSysmanUtil.generarConsecutivoConValorInicial("DOCUMENTO_CORRECCIONVALOR", condicion, GeneralParameterEnum.CONSECUTIVO.getName(), "1");
            
            is = event.getFile().getInputstream();
            nombreArchivo = event.getFile().getFileName();
            nombreArchivo = nombreArchivo.replace(" ", "_");
            if (tieneExtensionValida(nombreArchivo))
            {

                File directorioCatalogo = new File(rutaDocumentos);

                if (!directorioCatalogo.isDirectory())
                {
                    directorioCatalogo.mkdir();
                }
                
                JsfUtil.upload(is, SysmanFunciones.concatenar(String.valueOf(consecutivo),asignarNombre(nombreArchivo)), rutaDocumentos);
                
                Map<String, Object> parametros = new HashMap<>();
                
                parametros.put(GeneralParameterEnum.COMPANIA.getName(), compania);
                parametros.put(GeneralParameterEnum.CONSECUTIVO.getName(), consecutivo);
                parametros.put(GeneralParameterEnum.TIPOMOVIMIENTO.getName(), tipo);
                parametros.put(GeneralParameterEnum.MOVIMIENTO.getName(), movimiento);
                parametros.put("RUTA", asignarNombre(nombreArchivo));
                parametros.put(GeneralParameterEnum.DATE_CREATED.getName(), new Date());
                parametros.put(GeneralParameterEnum.CREATED_BY.getName(), SessionUtil.getUser().toString());
                Parameter parameter = new Parameter();

                parameter.setFields(parametros);
                UrlBean urlInsertSelect = UrlServiceUtil.getInstance()
                                .getUrlServiceByUrlByEnumID(
                                                GenericUrlEnum.DOCUMENTO_CORRECCIONVALOR.getCreateKey());
                
                requestManager.save(urlInsertSelect.getUrl(),
                                urlInsertSelect.getMetodo(), parameter);
                
                JsfUtil.agregarMensajeInformativo(idioma.getString("MSM_REGISTRO_INGRESADO"));
                
                cargarListanombre();
                
            }
            else
            {
                JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB4100"));
            }
        }
        catch (IOException | SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }
    
    private boolean tieneExtensionValida(String nombreArchivo)
    {
        String regex = "([^w]+(\\.(?i)(pdf|doc|docx))$)";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(nombreArchivo);
        return matcher.matches();
    }
    
    public String asignarNombre(String nombreArchivoR)
    {
        Pattern pattern = Pattern.compile(SysmanFunciones.concatenar("\\w\\.", "(\\.doc|docx|pdf)?"));
        Matcher matcher = pattern.matcher(nombreArchivoR);
        extension = matcher.find() ? matcher.group(1) : "";
        
        Pattern patternE = Pattern.compile(SysmanFunciones.concatenar("([a-zA-Z0-9_-]+)\\.", extension));
        Matcher matcherE = patternE.matcher(nombreArchivoR);
        String nombreF = matcherE.find() ? matcherE.group(1) : "";
        
        return nombreArchivoR.length() == SysmanFunciones.concatenar(nombreF,".",extension).length() ? SysmanFunciones.concatenar(nombreF,".",extension) : null;
        
    }
    
    
    //</METODOS_BOTONES>
    //<METODOS_CAMBIAR>
    //</METODOS_CAMBIAR>
    //<METODOS_COMBOS_GRANDES>
    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listanombre
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilanombre(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        consecutivo = new Long(registroAux.getCampos().get("CONSECUTIVO").toString());
        nombreArchivo = SysmanFunciones.concatenar(registroAux.getCampos().get("CONSECUTIVO").toString(),
                        registroAux.getCampos().get("RUTA").toString());
        nombre = registroAux.getCampos().get("RUTA").toString();
    }
    //</METODOS_COMBOS_GRANDES>
    //<METODOS_ARBOL>
    //</METODOS_ARBOL>
    //<SET_GET_ATRIBUTOS>
    /**
     * Retorna la variable nombre
     * 
     * @return  nombre
     */
    public String getNombre() {
        return nombre;
    }
    /**
     * Asigna la variable  nombre
     * 
     * @param  nombre
     * Variable a asignar en  nombre
     */
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }
    /**
     * Retorna el objeto contArchivosubir
     * 
     * @return contArchivosubir
     */
    public ContenedorArchivo getContArchivosubir() {
        return contArchivosubir;
    }
    /**
     * Asigna el objeto contArchivosubir
     * 
     * @param contArchivosubir
     * Variable a asignar en contArchivosubir
     */
    public void setContArchivosubir(ContenedorArchivo contArchivosubir) {
        this.contArchivosubir = contArchivosubir;
    }
    //</SET_GET_ATRIBUTOS>
    //<SET_GET_PARAMETROS>
    //</SET_GET_PARAMETROS>
    //<SET_GET_LISTAS>
    //</SET_GET_LISTAS>
    //<SET_GET_LISTAS_COMBO_GRANDE>	
    /**
     * Retorna la lista listanombre
     * 
     * @return listanombre
     */
    public RegistroDataModelImpl getListanombre() {
        return listanombre;
    }
    /**
     * Asigna la lista listanombre
     * 
     * @param listanombre
     * Variable a asignar en  listanombre
     */
    public void setListanombre(RegistroDataModelImpl listanombre) {
        this.listanombre = listanombre;
    }
    public StreamedContent getArchivoDescarga() {
        return archivoDescarga;
    }
    public void setArchivoDescarga(StreamedContent archivoDescarga) {
        this.archivoDescarga = archivoDescarga;
    }
    
    
    //</SET_GET_LISTAS_COMBO_GRANDE>
}
