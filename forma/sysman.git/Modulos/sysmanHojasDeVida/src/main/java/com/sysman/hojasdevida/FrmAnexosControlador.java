/*-
 * FrmAnexosControlador.java
 *
 * 1.0
 * 
 * 21 ago. 2019
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */

package com.sysman.hojasdevida;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.event.RowEditEvent;
import org.primefaces.model.StreamedContent;
import org.primefaces.model.UploadedFile;

import com.sysman.beanbase.BeanBaseContinuoAcmeImpl;
import com.sysman.componentes.Direccionador;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.enums.GenericUrlEnum;
import com.sysman.exception.SystemException;
import com.sysman.hojasdevida.ejb.EjbHojasDeVidaCeroRemote;
import com.sysman.hojasdevida.enums.FrmAnexosControladorUrlEnum;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.kernel.api.clientwso2.beans.Parameter;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.recursos.ejb.EjbSysmanUtilRemote;
import com.sysman.util.SysmanConstantes;
import com.sysman.util.SysmanFunciones;

import net.sf.jasperreports.engine.JRException;

/**
 * Formulario que permite realizar la carga de archivos de anexos para cada
 * empleado
 *
 * @version 1.0, 21/08/2019
 * @author eamaya
 */
@ManagedBean
@ViewScoped
public class FrmAnexosControlador extends BeanBaseContinuoAcmeImpl {
    /**
     * Constante a nivel de clase que almacena el codigo de la compania en la cual
     * inicio sesion el usuario, el valor de esta constante es asignado en el
     * constructor a la variable de sesion correspondiente
     */
    private final String compania;

    /**
     * Variable que almacena el modulo
     */
    private final String modulo;

    /**
     * Usuario que ingresa a la aplicaciï¿½n
     */
    private final String usuario;
    // <DECLARAR_ATRIBUTOS>
    /**
     * Atributo usado para descargar contenidos de archivos desde la vista
     */
    private StreamedContent archivoDescarga;

    /**
     * Este atributo se usa como auxiliar del componente referencia de archivos
     * Adjuntar y funciona como contenedor del archivo que se desea cargar
     */
    private UploadedFile archivoCargaAdjuntar;

    private String numeroDocumento;
    private String sucursal;
    private boolean permiteEliminar;
    private String rutaEliminar;
    private Registro registroEliminar;

    private Map<String, Object> ridDatosPersonales;

    // </DECLARAR_ATRIBUTOS>
    // <DECLARAR_PARAMETROS>
    // </DECLARAR_PARAMETROS>
    // <DECLARAR_LISTAS>
    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>
    // </DECLARAR_LISTAS_COMBO_GRANDE>

    @EJB
    private EjbHojasDeVidaCeroRemote ejbHojasDeVidaCero;

    @EJB
    private EjbSysmanUtilRemote ejbSysmanUtil;

    /**
     * Crea una nueva instancia de FrmAnexosControlador
     */
    @SuppressWarnings("unchecked")
    public FrmAnexosControlador() {
        super();
        compania = SessionUtil.getCompania();
        modulo = SessionUtil.getModulo();
        usuario = SessionUtil.getUser().getCodigo();
        
        try {
            // 2101
            numFormulario = GeneralCodigoFormaEnum.FRM_ANEXOS_CONTROLADOR
                    .getCodigo();
            validarPermisos();
            // <INI_ADICIONAL>
            Map<String, Object> parametrosEntrada = SessionUtil.getFlash();
            if (parametrosEntrada != null) {
                ridDatosPersonales = (Map<String, Object>) parametrosEntrada
                        .get("rid");

                numeroDocumento = SysmanFunciones
                        .nvl(parametrosEntrada.get("dp_numedocu"), "")
                        .toString();
                sucursal = SysmanFunciones
                        .nvl(parametrosEntrada.get("sucursal"), "")
                        .toString();
            }

            // </INI_ADICIONAL>
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
            SessionUtil.redireccionarMenuPermisos();
        }
    }

    /**
     * Este metodo se ejecuta justo despues de que el objeto de la clase del Bean ha
     * sido creado, en este se realizan las asignaciones iniciales necesarias para
     * la visualizacion del formulario, como son tablas, origenes de datos,
     * inicializacion de listas y demas necesarios
     */
    @PostConstruct
    public void inicializar() {
        enumBase = GenericUrlEnum.CONFIGURACION_ANEXOS;
        crearJerarquiaCarpetas();
        reasignarOrigen();
        buscarLlave();
        registro = new Registro();
        // <CARGAR_LISTA>
        // </CARGAR_LISTA>
        // <CARGAR_LISTA_COMBO_GRANDE>
        // </CARGAR_LISTA_COMBO_GRANDE>
        abrirFormulario();
    }

    private void crearJerarquiaCarpetas() {

        Map<String, Object> param = new TreeMap<>();

        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.CODIGO.getName(), numeroDocumento);

        Parameter parameter = new Parameter();
        parameter.setFields(param);

        UrlBean urlBean = UrlServiceUtil.getUrlBeanById(
                FrmAnexosControladorUrlEnum.URL8858.getValue());

        try {
            requestManager.save(urlBean.getUrl(), urlBean.getMetodo(),
                    parameter);
        } catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    /**
     * En este metodo se asigna al atributo origenDatos del bean base el valor de la
     * consulta del formulario. Tambien carga la lista del formulario por primera
     * vez
     */
    @Override
    public void reasignarOrigen() {

        parametrosListado.put(GeneralParameterEnum.COMPANIA.getName(),
                compania);

        parametrosListado.put("MODULO", modulo);

        parametrosListado.put("REFERENCIA",
                numeroDocumento);

        urlListado = UrlServiceUtil.getInstance()
                .getUrlServiceByUrlByEnumID(
                        FrmAnexosControladorUrlEnum.URL7559
                                .getValue());

    }

    // <METODOS_CARGAR_LISTA>
    // </METODOS_CARGAR_LISTA>
    // <METODOS_BOTONES>
    
    /**
     * Método ejecutado al oprimir el botón Guardar en la interfaz.
     *
     * Este método valida el archivo a cargar y gestiona su subida al servidor. 
     * Se verifica que el archivo no esté vacío y, si el código del registro es 
     * "20", se restringe la carga a imágenes (jpg, png, jpeg, gif).
     *
     * Se determina la ruta de almacenamiento del archivo y se comprueba si 
     * ya existe una imagen previa en esa ruta. Si todo es correcto, 
     * se sube el archivo y se actualiza la ruta en el registro.
     *
     * @param reg    Registro en el cual se encuentra el botón oprimido dentro de la grilla.
     * @param indice Índice en el que se ubica el botón oprimido dentro de la grilla.
     */
    public void oprimirGuardar(Registro reg, int indice) {
        String ruta;
        String archivoOriginal = archivoCargaAdjuntar.getFileName(); // Archivo original

        // Obtener el código del registro
        String codigoRegistro = reg.getCampos().get(GeneralParameterEnum.CODIGO.getName()).toString();

        // Validar que el archivo no esté vacío
        if (SysmanFunciones.validarVariableVacio(archivoOriginal)) {
            JsfUtil.agregarMensajeError(idioma.getString("TB_TB3979")); // Mensaje de archivo vacío
            return;
        }

        // Si el código es "20", solo permitir la carga de imágenes
        if ("20".equals(codigoRegistro)) {
            // Verificar si el archivo tiene una extensión de imagen
            if (!archivoOriginal.toLowerCase().endsWith(".jpg") && !archivoOriginal.toLowerCase().endsWith(".png")
                    && !archivoOriginal.toLowerCase().endsWith(".jpeg") && !archivoOriginal.toLowerCase().endsWith(".gif")) {
                JsfUtil.agregarMensajeError("Solo se permiten archivos de imagen (jpg, png, jpeg) para el código 20.");
                return;
            }
        }

        try {
            // Obtener la ruta del servidor para los anexos
            ruta = ejbSysmanUtil.consultarParametro(compania,
                    "RUTA SERVIDOR DE ANEXOS",
                    Integer.toString(SysmanConstantes.CODIGO_APLICACION_GENERAL),
                    new Date(),
                    false);

            // Crear la ruta del anexo con base en la compañía, módulo y número de documento
            ruta = ruta + ejbHojasDeVidaCero.crearRutaAnexos(compania,
                    Integer.parseInt(modulo),
                    numeroDocumento,
                    codigoRegistro);

            // Llama a verificarRuta pasando codigoRegistro para ajustar el nombre si es "20"
            ruta = verificarRuta(ruta, codigoRegistro);

            // Subir el archivo a la ruta generada con el nombre ajustado
            JsfUtil.upload(archivoCargaAdjuntar.getInputstream(), ruta);

            // Actualizar la ruta del archivo adjunto en el registro
            acturalizarRutaAdjunto(ruta, reg);

            reasignarOrigen();

            // Mensaje de éxito
            JsfUtil.agregarMensajeInformativo(idioma.getString("TB_TB4083"));

        } catch (NumberFormatException | SystemException | IOException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    private void acturalizarRutaAdjunto(String ruta, Registro reg) {

        Map<String, Object> param = new TreeMap<>();

        try {
            param.put("RUTA", ruta);
            param.put("KEY_COMPANIA", compania);
            param.put("KEY_MODULO", modulo);
            param.put("KEY_NIVEL_AGRUPAMIENTO",
                    reg.getCampos().get(GeneralParameterEnum.CODIGO
                            .getName()).toString());
            param.put("KEY_CODIGO", numeroDocumento);

            param.put(GeneralParameterEnum.MODIFIED_BY.getName(), usuario);
            param.put(GeneralParameterEnum.DATE_MODIFIED.getName(), new Date());

            Parameter parameter = new Parameter();
            parameter.setFields(param);

            UrlBean urlBean = UrlServiceUtil.getUrlBeanById(
                    FrmAnexosControladorUrlEnum.URL7859.getValue());

            requestManager.update(urlBean.getUrl(), urlBean.getMetodo(),
                    parameter);
        } catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    /**
     * Metodo que Verifica si una ruta de directorio existe y crea el directorio si no es así.
     * Genera un nombre de archivo basado en el código de registro y la carga de archivo actual.
     *
     * @param ruta          La ruta del directorio donde se desea guardar el archivo.
     * @param codigoRegistro El código que determina el formato del nombre del archivo.
     *                      Si el código es "20", se usará un nombre personalizado.
     * @return La ruta completa del archivo con el nombre adecuado.
     */
    private String verificarRuta(String ruta, String codigoRegistro) {
        File verificar = new File(ruta);
        if (!verificar.isDirectory()) {
            verificar.mkdirs();
        }

        // Verificar si el código de registro es "20"
        String nombreArchivo;
        if ("20".equals(codigoRegistro)) {
            // Usar el nombre personalizado "numeroDocumento_FOTO_PERFIL"
            String extensionArchivo = archivoCargaAdjuntar.getFileName().substring(archivoCargaAdjuntar.getFileName().lastIndexOf('.'));
            nombreArchivo = numeroDocumento + "_FOTO_PERFIL" + extensionArchivo;
        } else {
            // Usar el nombre original del archivo
            nombreArchivo = numeroDocumento + "_" + archivoCargaAdjuntar.getFileName();
        }

        // Concatenar el nombre de archivo adecuado a la ruta
        ruta = ruta + "/" + nombreArchivo;

        return ruta;
    }

    /**
     * Metodo ejecutado al oprimir el boton Eliminar
     * 
     * 
     * @param reg    registro en el cual esta ubicado el boton oprimido dentro de la
     *               grilla
     * @param indice indice en el cual esta ubicado el boton oprimido dentro de la
     *               grilla
     */
    public void oprimirEliminar(Registro reg, int indice) {

        permiteEliminar = true;
        
        
       
        rutaEliminar = SysmanFunciones.nvl(reg.getCampos()
                .get("RUTA"), "")
                .toString();
        
       registroEliminar = reg;

        
     
    }

    /**
     * Metodo ejecutado al oprimir el boton Descargar
     * 
     * 
     * @param reg    registro en el cual esta ubicado el boton oprimido dentro de la
     *               grilla
     * @param indice indice en el cual esta ubicado el boton oprimido dentro de la
     *               grilla
     */
    public void oprimirDescargar(Registro reg, int indice) {

        archivoDescarga = null;

        String ruta = SysmanFunciones.nvl(reg.getCampos()
                .get("RUTA"), "")
                .toString();

        if (SysmanFunciones.validarVariableVacio(ruta)) {
            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB4332"));
            return;
        }

        File adjunto = new File(ruta);

        try (InputStream inputStream = new FileInputStream(adjunto)) {
            byte[] vec = new byte[(int) adjunto.length()];

            inputStream.read(vec, 0, vec.length);

            archivoDescarga = JsfUtil.getArchivoDescarga(
                    new ByteArrayInputStream(vec), adjunto.getName());
        } catch (IOException | JRException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    // </METODOS_BOTONES>
    // <METODOS_CAMBIAR>

    /**
     * Metodo ejecutado al cambiar el control permiteEliminar
     * 
     * 
     */
    public void cambiarpermiteEliminar() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    /**
     * 
     * Metodo ejecutado al oprimir el boton Cancelar del dialogo permiteEliminar en
     * la vista
     *
     *
     */
    public void cancelarpermiteEliminar() {
        // <CODIGO_DESARROLLADO>
        permiteEliminar = false;
        // </CODIGO_DESARROLLADO>
    }

    /**
     * 
     * Metodo ejecutado al oprimir el boton Aceptar del dialogo permiteEliminar en
     * la vista
     *
     *
     */
    public void aceptarpermiteEliminar() {
        // <CODIGO_DESARROLLADO>
         
         if (SysmanFunciones.validarVariableVacio(rutaEliminar)) {
         JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB4332")); return; }
          
          File adjunto = new File(rutaEliminar);
          
          adjunto.delete(); 
          
          acturalizarRutaAdjunto("", registroEliminar);
          
          JsfUtil.agregarMensajeInformativo(idioma.getString("TB_TB2490"));
         
    	permiteEliminar = false;
    	
        // </CODIGO_DESARROLLADO>
    }

    // </METODOS_CAMBIAR>
    // <METODOS_COMBOS_GRANDES>
    // </METODOS_COMBOS_GRANDES>
    /**
     * Este metodo es invocado el metodo inicializar, se ejecutan las acciones a
     * tener en cuenta en el momento de apertura del formulario
     */
    @Override
    public void abrirFormulario() {
        // <CODIGO_DESARROLLADO>

        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado cuando se cancela la edicion del registro seleccionado
     */
    @Override
    public void cancelarEdicion(RowEditEvent event) {
        getListaInicial().load();
    }

    /**
     * Metodo ejecutado antes de realizar la insercion del registro
     * 
     */
    @Override
    public boolean insertarAntes() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
        return true;
    }

    /**
     * Metodo ejecutado despues de realizar la insercion del registro
     * 
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
     * 
     */
    @Override
    public boolean actualizarAntes() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
        return true;
    }

    /**
     * Metodo ejecutado despues de realizar la insercion y actualizacion del
     * registro
     * 
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
     * 
     */
    @Override
    public boolean eliminarDespues() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
        return true;
    }

    /**
     * Este metodo se ejecuta antes enviar la accion de actualizacion, en el se
     * pueden remover valores auxiliares que no se desee o se deban enviar en el
     * registro
     */
    @Override
    public void removerCombos() {
        // METODO_NO_IMPLEMENTADO
    }

    /**
     * Metodo ejecutado desde un comando remoto cuando se cierra el formulario
     * 
     */
    public void ejecutarrcCerrar() {
        Map<String, Object> parametros = new HashMap<>();
        parametros.put("rid", ridDatosPersonales);
        parametros.put("numeroDcto", numeroDocumento);
        parametros.put("sucursal", sucursal);

        Direccionador direccionador = new Direccionador();
        direccionador.setNumForm(Integer
                .toString(GeneralCodigoFormaEnum.NAT_DATOS_PERSONALES_CONTROLADOR
                        .getCodigo()));
        direccionador.setParametros(parametros);
        SessionUtil.redireccionarForma(direccionador, SessionUtil.getModulo());
    }

    /**
     * Este metodo es ejecutado despues de finalizar la insercion y edicion del
     * registro se usa cuando se desean agregar valores al registro despues de
     * dichas acciones
     */
    @Override
    public void asignarValoresRegistro() {
        // METODO_NO_IMPLEMENTADO
    }

    // <SET_GET_ATRIBUTOS>
    /**
     * Atributo usado para descargar contenidos de archivos desde la vista
     */
    public StreamedContent getArchivoDescarga() {
        return archivoDescarga;
    }

    /**
     * Retorna el objeto contArchivoAdjuntar
     * 
     * @return contArchivoAdjuntar
     */
    public UploadedFile getArchivoCargaAdjuntar() {
        return archivoCargaAdjuntar;
    }

    /**
     * Asigna el objeto contArchivoAdjuntar
     * 
     * @param contArchivoAdjuntar Variable a asignar en contArchivoAdjuntar
     */
    public void setArchivoCargaAdjuntar(UploadedFile archivoCargaAdjuntar) {
        this.archivoCargaAdjuntar = archivoCargaAdjuntar;
    }

    public boolean isPermiteEliminar() {
        return permiteEliminar;
    }

    public void setPermiteEliminar(boolean permiteEliminar) {
        this.permiteEliminar = permiteEliminar;
    }

    // </SET_GET_ATRIBUTOS>
    // <SET_GET_PARAMETROS>
    // </SET_GET_PARAMETROS>
    // <SET_GET_LISTAS>
    // </SET_GET_LISTAS>
    // <SET_GET_LISTAS_COMBO_GRANDE>
    // </SET_GET_LISTAS_COMBO_GRANDE>
}
