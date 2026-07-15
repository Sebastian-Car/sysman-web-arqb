/*-
 * FrmactdatospersonaldetalladosControlador.java
 *
 * 1.0
 *
 * 23/01/2018
 *
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */
package com.sysman.hojasdevida;

import com.sysman.beanbase.BeanBaseDatosAcmeImpl;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.enums.GenericUrlEnum;
import com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException;
import com.sysman.exception.SystemException;
import com.sysman.general.PlantillaswordsControlador;
import com.sysman.hojasdevida.enums.FrmactdatospersonaldetalladosControladorUrlEnum;
import com.sysman.hojasdevida.enums.frmactdatospersonalsControladorEnum;
import com.sysman.jsfutil.CacheUtil;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.kernel.api.clientwso2.beans.Parameter;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.persistencia.Acciones;
import com.sysman.persistencia.ConectorPool;
import com.sysman.recursos.ejb.impl.EjbSysmanUtil;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.util.SysmanConstantes;
import com.sysman.util.SysmanFunciones;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.naming.NamingException;

import org.apache.commons.io.FilenameUtils;
import org.primefaces.context.RequestContext;
import org.primefaces.event.RowEditEvent;
import org.primefaces.event.SelectEvent;
import org.primefaces.model.StreamedContent;
import org.primefaces.model.UploadedFile;

import net.sf.jasperreports.engine.JRException;

/**
 * Permite registrar y ver datos personales
 *
 * @version 1.0, 23/01/2018
 * @author vmolano
 */
@ManagedBean
@ViewScoped
public class FrmactdatospersonaldetalladosControlador
                extends BeanBaseDatosAcmeImpl
{
    /**
     * Constante a nivel de clase que almacena el codigo de la compania en la cual inicio sesion el usuario, el valor de esta constante es asignado en el constructor a la variable de sesion
     * correspondiente
     */
    private final String compania;

    private String consecutivo;
    private String idEmpleado;
    private String nombreEmpleado;
    private String cedula;

    private boolean enviado;
    private boolean actualizado;
    private String tipoS;
    private String sucursal;

    private String observaciones;

    private boolean estadoInterfaz;
    private boolean bloqueaGuardar;
    private boolean bloquearArchivos;

    /**
     * Atributo usado para descargar contenidos de archivos desde la vista
     */
    private StreamedContent archivoDescarga;

    private int tipoArchivo;

    private String ruta;

    private UploadedFile archivoCarganuevoArchivo;
    // <DECLARAR_ATRIBUTOS>
    // </DECLARAR_ATRIBUTOS>
    // <DECLARAR_LISTAS>
    /**
     * Listado de paises posibles para asignar a un empleado
     */
    private List<Registro> listacmbPaisHab;
    /**
     * Listado de departamentos posibles para asignar a un empleado
     */
    private List<Registro> listacmbDptoHab;
    /**
     * Listado de ciudades posibles para asignar a un empleado.
     */
    private List<Registro> listacmbCiudadHab;
    private RegistroDataModelImpl listacmbTipoArchivo;
    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>
    // </DECLARAR_LISTAS_COMBO_GRANDE>
    // <DECLARAR_LISTAS_SUBFORM>
    /**
     * TODO DOCUMENTACION NECESARIA
     */
    private List<Registro> listaFrmarchivos;
    // </DECLARAR_LISTAS_SUBFORM>
    // <DECLARAR_PARAMETROS>
    // </DECLARAR_PARAMETROS>
    // <DECLARAR_ADICIONALES>
    /**
     * Atributo de referencia para el subformulario
     */
    private Registro registroSub; // <DECLARAR_ADICIONALES>
    // </DECLARAR_ADICIONALES>

    @EJB
    EjbSysmanUtil ejbSysmanUtil;

    /**
     * Crea una nueva instancia de FrmactdatospersonaldetalladosControlador
     */
    public FrmactdatospersonaldetalladosControlador()
    {
        super();
        compania = SessionUtil.getCompania();
        try
        {
            numFormulario = GeneralCodigoFormaEnum.FRM_ACTDATOSPERSONALES_DETALLADO_CONTROLADOR
                            .getCodigo();
            validarPermisos();
            registro = new Registro();
            Map<String, Object> parametrosEntrada = SessionUtil.getFlash();
            if (parametrosEntrada != null)
            {

                accion = parametrosEntrada.get("ACCION").toString();
                if (ACCION_MODIFICAR.equals(accion))
                {
                    consecutivo = parametrosEntrada.get("CONSECUTIVO")
                                    .toString();
                    actualizado = (boolean) parametrosEntrada
                                    .get("ACTUALIZADO");
                    enviado = (boolean) parametrosEntrada.get("ENVIADO");
                    tipoS = parametrosEntrada.get("TIPO").toString();

                }

                idEmpleado = parametrosEntrada.get("ID_DE_EMPLEADO")
                                .toString();

                nombreEmpleado = SysmanFunciones.nvl(
                                parametrosEntrada.get("NOMBREEMPLEADO"), "")
                                .toString();

                sucursal = parametrosEntrada.get("SUCURSAL").toString();

                cedula = SysmanFunciones
                                .nvl(parametrosEntrada.get("CEDULA"), "")
                                .toString();

                validarCampos();
            }

            // <INI_ADICIONAL>
            registroSub = new Registro(new HashMap<String, Object>());
            iniciarListasSub();
            // </INI_ADICIONAL>
        }
        catch (Exception ex)
        {
            logger.error(ex.getMessage(), ex);
            SessionUtil.redireccionarMenuPermisos();
        }

    }

    public void validarCampos()
    {

        if ("S".equals(tipoS))
        {
            estadoInterfaz = false;
            bloqueaGuardar = false;
            if (enviado)
            {
                estadoInterfaz = true;
                // Desactivar boton imprimir
                bloqueaGuardar = true;

                // Desactivar pestaña archivos
                bloquearArchivos = false;
            }
        }
        else if ("A".equals(tipoS))
        {
            estadoInterfaz = true;
            // desactivar boton imprimir
            bloqueaGuardar = true;

            // desactivar pestaña archivos
            bloquearArchivos = false;
        }
        else if ("T".equals(tipoS) && consecutivo != null)
        {

            if (enviado)
            {
                // desbloquear ESTADO
            }
            else
            {
                // bloquear ESTADO
            }
            estadoInterfaz = true;
            // desactivar boton imprimir
            bloqueaGuardar = true;

            // desactivar pestaña archivos
            bloquearArchivos = false;
        }

        if (!actualizado && consecutivo != null)
        {
            estadoInterfaz = true;
            // desactivar boton imprimir
            bloqueaGuardar = true;

            // desactivar pestaña archivos
            bloquearArchivos = false;

            if (!enviado)
            {
                estadoInterfaz = false;
                // Activar boton imprimir
                bloqueaGuardar = false;

                // Activar pestaña archivos
                bloquearArchivos = true;
            }
        }

        if (consecutivo == null && !actualizado)
        { // Es nuevo
            estadoInterfaz = false;
            // Activar boton imprimir
            bloqueaGuardar = false;

            // Activar pestaña archivos
            bloquearArchivos = true;
        }

        if (consecutivo == null)
        {
            bloquearArchivos = false;
        }

    }

    /**
     * En este metodo se hace la invocacion de lo metodos de carga de todas las listas, menos las que son de subformularios
     */
    @Override
    public void iniciarListas()
    {
        // <CARGAR_LISTA_COMBO_GRANDE>
        // </CARGAR_LISTA_COMBO_GRANDE>
        // <CARGAR_LISTA>
        cargarListacmbPaisHab();
        cargarListacmbDptoHab();
        cargarListacmbCiudadHab();
        cargarListacmbTipoArchivo();
        // </CARGAR_LISTA>
    }

    /**
     * En este metodo se hace la invocacion de lo metodos de carga de todas las listas que son de subformularios
     */
    @Override
    public void iniciarListasSub()
    {
        // <CARGAR_LISTAS_SUBFORM>
        cargarListaFrmarchivos();
        // </CARGAR_LISTAS_SUBFORM>
        // <CREAR_ARBOLES>
        // </CREAR_ARBOLES>
    }

    /**
     * En este metodo se iguala a null todas las listas de los subformularios
     */
    @Override
    public void iniciarListasSubNulo()
    {
        // <CARGAR_LISTAS_SUBFORM_NULL>
        listaFrmarchivos = null;
        // </CARGAR_LISTAS_SUBFORM_NULL>
    }

    /**
     * Este metodo se ejecuta justo despues de que el objeto de la clase del Bean ha sido creado, en este se realizan las asignaciones iniciales necesarias para la visualizacion del formulario, como
     * son tablas, origenes de datos, inicializacion de listas y demas necesarios
     */
    @PostConstruct
    public void inicializar()
    {
        // tabla = "AUT_PERSONAL";
        enumBase = GenericUrlEnum.AUT_PERSONAL;
        buscarLlave();
        asignarOrigenDatos();
        // iniciarListas();
        // reasignarOrigenGrilla();

        // String archivo =
        // JsfUtil.generarNombreArchivo(SessionUtil.getModulo(),
        // registroSub,
        // SysmanConstantes.RUTA_DOCUMENTOS_PERSONALES, ".xls");
        //
        // String ruta = JsfUtil.generarRuta(SessionUtil.getModulo(),
        // "123456",
        // SysmanConstantes.RUTA_DOCUMENTOS_PERSONALES,
        // "001_25_365_abc_.xls");
        abrirFormulario();
    }

    /**
     * Se realiza la asignacion de la variable origenDatos por la consulta correspondiente del formulario
     *
     *
     */
    @Override
    public void asignarOrigenDatos()
    {
        buscarUrls();
    }

    /**
     *
     * Carga la lista listaFrmarchivos
     *
     */
    public void cargarListaFrmarchivos()
    {

        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put("AUTORIZACION", consecutivo);
        param.put("IDEMPLEADO", idEmpleado);

        try
        {
            listaFrmarchivos = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            "989001")
                                            .getUrl(),
                                            param),
                            CacheUtil.getLlaveServicio(urlConexionCache,
                                            "AUTPERSONAL_DOCUMENTO"));
        }
        catch (SystemException | SysmanException e)
        {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    // <METODOS_CARGAR_LISTA>
    /**
     *
     * Carga la lista listacmbPaisHab
     *
     */
    public void cargarListacmbPaisHab()
    {
        try
        {
            listacmbPaisHab = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            FrmactdatospersonaldetalladosControladorUrlEnum.URL002
                                                                            .getValue())
                                            .getUrl(), null));
        }
        catch (SystemException e)
        {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    /**
     *
     * Carga la lista listacmbDptoHab
     *
     */
    public void cargarListacmbDptoHab()
    {
        try
        {
            Map<String, Object> param = new TreeMap<>();
            param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
            param.put(frmactdatospersonalsControladorEnum.PAIS.getValue(),
                            SysmanFunciones.nvl(registro.getCampos()
                                            .get("PAIS_HAB"), "").toString());
            listacmbDptoHab = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            FrmactdatospersonaldetalladosControladorUrlEnum.URL003
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e)
        {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    /**
     *
     * Carga la lista listacmbCiudadHab
     *
     */
    public void cargarListacmbCiudadHab()
    {
        try
        {
            Map<String, Object> param = new TreeMap<>();
            param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
            param.put(frmactdatospersonalsControladorEnum.PAIS.getValue(),
                            SysmanFunciones.nvl(registro.getCampos()
                                            .get("PAIS_HAB"), "").toString());
            param.put(frmactdatospersonalsControladorEnum.DEPARTAMENTO
                            .getValue(),
                            SysmanFunciones.nvl(
                                            registro.getCampos()
                                                            .get("DEPARTAMENTO_HAB"),
                                            "").toString());

            listacmbCiudadHab = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            FrmactdatospersonaldetalladosControladorUrlEnum.URL004
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e)
        {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    /**
     *
     * Carga la lista listacmbTipoArchivo
     *
     */
    public void cargarListacmbTipoArchivo()
    {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        FrmactdatospersonaldetalladosControladorUrlEnum.URL001
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        listacmbTipoArchivo = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, GeneralParameterEnum.CODIGO.getName());

    }

    // </METODOS_CARGAR_LISTA>
    /**
     * Metodo ejecutado al cambiar el control cmbPaisHab
     *
     *
     */
    public void cambiarcmbPaisHab()
    {
        // <CODIGO_DESARROLLADO>
        registro.getCampos().put("DEPARTAMENTO_HAB", "");
        registro.getCampos().put("CIUDAD_HAB", "");
        cargarListacmbDptoHab();
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado al cambiar el control cmbDptoHab
     *
     */
    public void cambiarcmbDptoHab()
    {
        // <CODIGO_DESARROLLADO>
        registro.getCampos().put("CIUDAD_HAB", "");
        cargarListacmbCiudadHab();
        // </CODIGO_DESARROLLADO>
    }

    // <METODOS_CAMBIAR>
    // </METODOS_CAMBIAR>
    // <METODOS_COMBOS_GRANDES>
    /**
     *
     * Metodo ejecutado al seleccionar una fila de la lista listacmbTipoArchivo
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilacmbTipoArchivo(SelectEvent event)
    {
        Registro registroAux = (Registro) event.getObject();
        tipoArchivo = (int) registroAux.getCampos()
                        .get(GeneralParameterEnum.CODIGO.getName());
    }

    // </METODOS_COMBOS_GRANDES>
    // <METODOS_ARBOL>
    // </METODOS_ARBOL>
    // <METODOS_BOTONES>
    /**
     *
     * Metodo ejecutado al oprimir el boton Imprimir en la vista
     *
     */
    public void oprimirImprimir()
    {
        // <CODIGO_DESARROLLADO>
        // accion = ACCION_MODIFICAR;
        bloquearArchivos = true;
        if (!"0".equals(consecutivo) && !enviado)
        {
            estadoInterfaz = true;
            registro.getCampos().put("ENVIADO", -1);
        }
        agregarRegistroNuevo(false);

        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado al oprimir el boton Comando50
     *
     * @param reg
     * registro en el cual esta ubicado el boton oprimido dentro de la grilla
     * @param indice
     * indice en el cual esta ubicado el boton oprimido dentro de la grilla
     */
    public void oprimirComando50(Registro reg, int indice)
    {
        // <CODIGO_DESARROLLADO>
        archivoDescarga = null;

        String nombreArchivo = String
                        .valueOf(reg.getCampos().get("NOMBRE_ARCHIVO"));

        String rutaDes = JsfUtil.generarRuta(SessionUtil.getModulo(),
                        SessionUtil.getUser().getCedula(),
                        SysmanConstantes.RUTA_DOCUMENTOS_PERSONALES,
                        nombreArchivo);

        File anexo = new File(rutaDes);
        try (InputStream fis = new FileInputStream(anexo))
        {

            byte[] vec = new byte[(int) anexo.length()];
            fis.read(vec, 0, vec.length);
            archivoDescarga = JsfUtil.getArchivoDescarga(
                            new ByteArrayInputStream(vec), anexo.getName());
        }
        catch (FileNotFoundException ex)
        {
            Logger.getLogger(PlantillaswordsControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            JsfUtil.agregarMensajeAlerta("El archivo a descargar no existe");
        }
        catch (JRException | IOException ex)
        {
            Logger.getLogger(PlantillaswordsControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
        }
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado al oprimir el boton Comando2
     *
     * @param reg
     * registro en el cual esta ubicado el boton oprimido dentro de la grilla
     * @param indice
     * indice en el cual esta ubicado el boton oprimido dentro de la grilla
     */
    public void oprimirComando2(Registro reg, int indice)
    {
        // <CODIGO_DESARROLLADO>
        eliminarRegSubFrmarchivos(reg);

        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado al oprimir el boton btAdjuntar en la vista
     */
    public void oprimirbtAdjuntar()
    {
        // <CODIGO_DESARROLLADO>

        if (consecutivo != null)
        {

            if (tipoArchivo != 0)
            {

                Map<String, Object> llaveAct = new HashMap<>();

                llaveAct.put("KEY_COMPANIA", compania);
                llaveAct.put("KEY_CODIGO_DOCUMENTO", tipoArchivo);
                llaveAct.put("KEY_AUTORIZACION", consecutivo);
                llaveAct.put("KEY_ID_DE_EMPLEADO", idEmpleado);

                String archivo = archivoCarganuevoArchivo.getFileName();

                if (!SysmanFunciones.validarVariableVacio(archivo))
                {
                    String extension = FilenameUtils.getExtension(archivo);

                    ruta = JsfUtil.generarNombreArchivo(SessionUtil.getModulo(),
                                    llaveAct,
                                    SysmanConstantes.RUTA_DOCUMENTOS_PERSONALES,
                                    extension,
                                    SessionUtil.getUser().getCedula());

                    try
                    {
                        JsfUtil.upload(archivoCarganuevoArchivo
                                        .getInputstream(),
                                        ruta);
                        agregarRegistroSubFrmarchivos();
                        cargarListaFrmarchivos();
                    }
                    catch (IOException e)
                    {
                        logger.error(e.getMessage(), e);
                        JsfUtil.agregarMensajeError(e.getMessage());
                    }
                }
                else
                {
                    JsfUtil.agregarMensajeError(
                                    "No se ha seleccionado ningún archivo");
                }
            }
            else
            {
                JsfUtil.agregarMensajeError(
                                "No se ha seleccionado el tipo de archivo");
            }
        }
        else
        {
            JsfUtil.agregarMensajeError(
                            "Primero debe guardar el registro principal en la pestaña de Información");
        }

        // </CODIGO_DESARROLLADO>
    }

    // </METODOS_BOTONES>
    // <METODOS_SUBFORM>
    /**
     * Metodo de insercion del formulario Frmarchivos
     *
     */
    public void agregarRegistroSubFrmarchivos()
    {
        try
        {

            File archivoTemp = new File(ruta);

            registroSub.getCampos().put("COMPANIA", compania);
            registroSub.getCampos().put("CODIGO_DOCUMENTO", tipoArchivo);
            registroSub.getCampos().put("AUTORIZACION", consecutivo);
            registroSub.getCampos().put("ID_DE_EMPLEADO", idEmpleado);
            registroSub.getCampos().put("OBSERVACION", observaciones);
            registroSub.getCampos().put("NOMBRE_ARCHIVO",
                            archivoTemp.getName());
            registroSub.getCampos().put("CREATED_BY",
                            SessionUtil.getUser().getCodigo());
            registroSub.getCampos().put("DATE_CREATED", new Date());

            Parameter params = new Parameter();
            params.setFields(registroSub.getCampos());

            UrlBean urlCreate = UrlServiceUtil.getInstance()
                            .getUrlServiceByUrlByEnumID(
                                            FrmactdatospersonaldetalladosControladorUrlEnum.URL005
                                                            .getValue());

            requestManager.save(urlCreate.getUrl(),
                            urlCreate.getMetodo(),
                            params);
        }
        catch (SystemException e)
        {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    /**
     * Metodo de edicion del formulario Frmarchivos
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void editarRegSubFrmarchivos(RowEditEvent event)
    {
        Registro reg = (Registro) event.getObject();
        try
        {
            int conteo;
            conteo = Acciones.actualizar(ConectorPool.ESQUEMA_SYSMAN,
                            "AUTPERSONAL_DOCUMENTO", reg.getCampos(),
                            reg.getLlave());
            if (conteo > 0)
            {
                JsfUtil.agregarMensajeInformativo(
                                idioma.getString("MSM_REGISTRO_MODIFICADO"));
            }
        }
        catch (IllegalAccessException | InstantiationException
                        | ClassNotFoundException | SQLException
                        | NamingException ex)
        {
            logger.error(ex.getMessage(), ex);
            JsfUtil.agregarMensajeError(ex.getMessage());
        }
        finally
        {
            cargarListaFrmarchivos();
        }
    }

    /**
     * Metodo de eliminacion del formulario Frmarchivos
     *
     * @param reg
     * registro seleccionado en el subformulario
     */
    public void eliminarRegSubFrmarchivos(Registro reg)
    {

        try
        {
            Map<String, Object> llaveAct = new HashMap<>();

            llaveAct.put("KEY_COMPANIA", compania);
            llaveAct.put("KEY_CODIGO_DOCUMENTO", String
                            .valueOf(reg.getCampos().get("CODIGO_DOCUMENTO")));
            llaveAct.put("KEY_AUTORIZACION", consecutivo);
            llaveAct.put("KEY_ID_DE_EMPLEADO", idEmpleado);

            Parameter params = new Parameter();
            params.setFields(registroSub.getCampos());

            UrlBean urlEliminacion = UrlServiceUtil.getInstance()
                            .getUrlServiceByUrlByEnumID(
                                            FrmactdatospersonaldetalladosControladorUrlEnum.URL006
                                                            .getValue());

            requestManager.delete(urlEliminacion.getUrl(), llaveAct);
            cargarListaFrmarchivos();
        }
        catch (SystemException e)
        {

            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    /**
     * Metodo ejecutado cuando se cancela la edicion del registro seleccionado para el subformulario Frmarchivos
     *
     */
    public void cancelarEdicionFrmarchivos()
    {
        cargarListaFrmarchivos();
    }

    // </METODOS_SUBFORM>
    // <METODOS_ADICIONALES>
    // </METODOS_ADICIONALES>
    /**
     * Este metodo es invocado el metodo inicializar, se ejecutan las acciones a tener en cuenta en el momento de apertura del formulario
     */
    @Override
    public void abrirFormulario()
    {

        Map<String, Object> llaveActual = new HashMap<>();
        if (ACCION_MODIFICAR.equals(accion))
        {
            llaveActual.put("KEY_COMPANIA", compania);
            llaveActual.put("KEY_CONSECUTIVO", consecutivo);
            llaveActual.put("KEY_ID_DE_EMPLEADO", idEmpleado);
            cargarRegistro(llaveActual, accion);
        }
        else
        {
            cargarRegistroNuevo();
        }

        iniciarListas();
        // <CODIGO_DESARROLLADO>
        /*
         * FR1636-AL_ABRIR Private Sub Form_Open(Cancel As Integer) Dim Estado As Boolean On Error Resume Next If Forms!FRM_ACTDATOSPERSONAL!TIPOS = "S" Then Estado = False Me!Imprimir.Enabled = True
         * If Not Me!Enviado = 0 Then Estado = True Me!Imprimir.Enabled = False Me!btn_Ruta.Enabled = False End If ElseIf Forms!FRM_ACTDATOSPERSONAL!TIPOS = "A" Then Estado = True Me!Imprimir.Enabled
         * = False Me!btn_Ruta.Enabled = False ElseIf Forms!FRM_ACTDATOSPERSONAL!TIPOS = "T" And Not (Forms!Identificacion!N_Solicitud = 0) Then Estado = False If Not Me!Enviado = 0 Then
         * Me.Estado.Locked = Estado Else Me.Estado.Locked = True End If Estado = True Me!Imprimir.Enabled = False Me!btn_Ruta.Enabled = False End If If Forms!FRM_ACTDATOSPERSONAL!ACTUALIZADO = 0 And
         * Not (Forms!Identificacion!N_Solicitud = 0) Then Estado = True Me!Imprimir.Enabled = False Me!btn_Ruta.Enabled = False If Me!Enviado = 0 Then Estado = False Me!Imprimir.Enabled = True
         * Me!btn_Ruta.Enabled = True End If End If If Forms!Identificacion!N_Solicitud = 0 And Forms!FRM_ACTDATOSPERSONAL!ACTUALIZADO = 0 Then 'NUEVO Estado = False Forms!Identificacion!N_Solicitud =
         * 0 Me.Undo Me.Requery Me!Imprimir.Enabled = True Me!btn_Ruta.Enabled = True End If Me.txtEmailPersonal.Locked = Estado Me.txtDireccion.Locked = Estado Me.txtTelefono.Locked = Estado
         * Me.cmbPaisHab.Locked = Estado Me.cmbDptoHab.Locked = Estado Me.cmbCiudadHab.Locked = Estado Me.txtTallaPantalon.Locked = Estado Me.txtTallaCamisa.Locked = Estado Me.txtTallaCalzado.Locked =
         * Estado Me.txtTallaChaqueta.Locked = Estado ' Me.txtRuta.Locked = Estado End Sub
         */
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado en el momento despues de cargar el registro
     *
     */
    @Override
    public void cargarRegistro()
    {
        // <CODIGO_DESARROLLADO>
        precargarRegistro();
        if (ACCION_INSERTAR.equals(accion))
        {
            try
            {
                String criterio = "COMPANIA = ''" + compania
                    + "'' AND ID_DE_EMPLEADO = ''" + idEmpleado + "''";

                consecutivo = String.valueOf(ejbSysmanUtil
                                .generarConsecutivoConValorInicial(
                                                enumBase.getTable(),
                                                criterio, "CONSECUTIVO", "1"));

                registro.getCampos().put("COMPANIA", compania);
                registro.getCampos().put("CONSECUTIVO", consecutivo);
                registro.getCampos().put("ID_DE_EMPLEADO", idEmpleado);
                registro.getCampos().put("NUMERO_DCTO", cedula);
                registro.getCampos().put("NOMBREEMPLEADO", nombreEmpleado);
                registro.getCampos().put("ESTADO", "T");
                registro.getCampos().put("ENVIADO", false);
                registro.getCampos().put("SUCURSAL", sucursal);
            }
            catch (SystemException e)
            {
                logger.error(e.getMessage(), e);
                JsfUtil.agregarMensajeError(e.getMessage());
            }
        }
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado antes de realizar la insercion del registro
     *
     * @return
     */
    @Override
    public boolean insertarAntes()
    {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
        return true;
    }

    /**
     * Metodo ejecutado despues de realizar la insercion del registro
     *
     * @return
     */
    @Override
    public boolean insertarDespues()
    {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
        return true;
    }

    /**
     * Metodo ejecutado antes de realizar la insercion y actualizacion del registro
     *
     * @return
     */
    @Override
    public boolean actualizarAntes()
    {
        // <CODIGO_DESARROLLADO>
        if (enviado)
        {
            JsfUtil.agregarMensajeAlerta("La solicitud ya fue enviada.");
            return false;
        }
        registro.getCampos().remove("NOMBREEMPLEADO");
        if (ACCION_MODIFICAR.equals(accion))
        {
            registro.getCampos()
                            .remove(GeneralParameterEnum.COMPANIA.getName());
            registro.getCampos()
                            .remove(GeneralParameterEnum.CONSECUTIVO.getName());
            registro.getCampos()
                            .remove(GeneralParameterEnum.ID_DE_EMPLEADO
                                            .getName());
            registro.getCampos()
                            .remove(GeneralParameterEnum.COMPANIA.getName());
            registro.getCampos()
                            .remove(GeneralParameterEnum.CONSECUTIVO.getName());
            registro.getCampos().remove(
                            GeneralParameterEnum.ID_DE_EMPLEADO.getName());
            registro.getCampos().remove("APELLIDO1_A");
            registro.getCampos().remove("APELLIDO2_A");
            registro.getCampos().remove(GeneralParameterEnum.NOMBRES.getName());
            registro.getCampos().remove("APELLIDO1");

        }

        if (enviado)
        {
            estadoInterfaz = true;
        }
        bloquearArchivos = true;
        // registro.getCampos().put("ENVIADO",
        // ((boolean) registro.getCampos().get("ENVIADO")) ? -1
        // : 0);
        // String estado = SysmanFunciones
        // .nvl(registro.getCampos().get("ENVIADO"), "")
        // .toString();
        // </CODIGO_DESARROLLADO>
        return true;
    }

    /**
     * Metodo ejecutado despues de realizar la insercion y actualizacion del registro
     *
     * @return
     */
    @Override
    public boolean actualizarDespues()
    {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
        return true;
    }

    /**
     * Metodo ejecutado antes de realizar la eliminacion del registro
     *
     * @return
     */
    @Override
    public boolean eliminarAntes()
    {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
        return true;
    }

    /**
     * Metodo ejecutado despues de realizar la eliminacion del registro
     *
     * @return
     */
    @Override
    public boolean eliminarDespues()
    {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
        return true;
    }

    /**
     * Metodo ejecutado cuando se cierra el formulario
     */
    public void cerrarFormulario()
    {
        RequestContext.getCurrentInstance().closeDialog(null);
    }

    // <SET_GET_ATRIBUTOS>

    /**
     * Atributo usado para descargar contenidos de archivos desde la vista
     */
    public StreamedContent getArchivoDescarga()
    {
        return archivoDescarga;
    }

    /**
     * Retorna el objeto contArchivonuevoArchivo
     *
     * @return contArchivonuevoArchivo
     */
    public UploadedFile getArchivoCarganuevoArchivo()
    {
        return archivoCarganuevoArchivo;
    }

    /**
     * Asigna el objeto contArchivonuevoArchivo
     *
     * @param contArchivonuevoArchivo
     * Variable a asignar en contArchivonuevoArchivo
     */
    public void setArchivoCarganuevoArchivo(
        UploadedFile archivoCarganuevoArchivo)
    {
        this.archivoCarganuevoArchivo = archivoCarganuevoArchivo;
    }

    // </SET_GET_ATRIBUTOS>
    // <SET_GET_LISTAS>
    /**
     * Retorna la lista listacmbPaisHab
     *
     * @return listacmbPaisHab
     */
    public List<Registro> getListacmbPaisHab()
    {
        return listacmbPaisHab;
    }

    /**
     * Asigna la lista listacmbPaisHab
     *
     * @param listacmbPaisHab
     * Variable a asignar en listacmbPaisHab
     */
    public void setListacmbPaisHab(List<Registro> listacmbPaisHab)
    {
        this.listacmbPaisHab = listacmbPaisHab;
    }

    /**
     * Retorna la lista listacmbDptoHab
     *
     * @return listacmbDptoHab
     */
    public List<Registro> getListacmbDptoHab()
    {
        return listacmbDptoHab;
    }

    /**
     * Asigna la lista listacmbDptoHab
     *
     * @param listacmbDptoHab
     * Variable a asignar en listacmbDptoHab
     */
    public void setListacmbDptoHab(List<Registro> listacmbDptoHab)
    {
        this.listacmbDptoHab = listacmbDptoHab;
    }

    /**
     * Retorna la lista listacmbCiudadHab
     *
     * @return listacmbCiudadHab
     */
    public List<Registro> getListacmbCiudadHab()
    {
        return listacmbCiudadHab;
    }

    /**
     * Asigna la lista listacmbCiudadHab
     *
     * @param listacmbCiudadHab
     * Variable a asignar en listacmbCiudadHab
     */
    public void setListacmbCiudadHab(List<Registro> listacmbCiudadHab)
    {
        this.listacmbCiudadHab = listacmbCiudadHab;
    }

    /**
     * Retorna la lista listacmbTipoArchivo
     *
     * @return listacmbTipoArchivo
     */
    public RegistroDataModelImpl getListacmbTipoArchivo()
    {
        return listacmbTipoArchivo;
    }

    /**
     * Asigna la lista listacmbTipoArchivo
     *
     * @param listacmbTipoArchivo
     * Variable a asignar en listacmbTipoArchivo
     */
    public void setListacmbTipoArchivo(
        RegistroDataModelImpl listacmbTipoArchivo)
    {
        this.listacmbTipoArchivo = listacmbTipoArchivo;
    }

    // </SET_GET_LISTAS>
    // <SET_GET_LISTAS_COMBO_GRANDE>
    // </SET_GET_LISTAS_COMBO_GRANDE>
    // <SET_GET_LISTAS_SUBFORM>
    /**
     * Retorna la lista listaFrmarchivos
     *
     * @return listaFrmarchivos
     */
    public List<Registro> getListaFrmarchivos()
    {
        return listaFrmarchivos;
    }

    /**
     * Asigna la lista listaFrmarchivos
     *
     * @param listaFrmarchivos
     * Variable a asignar en listaFrmarchivos
     */
    public void setListaFrmarchivos(List<Registro> listaFrmarchivos)
    {
        this.listaFrmarchivos = listaFrmarchivos;
    }

    public String getObservaciones()
    {
        return observaciones;
    }

    public void setObservaciones(String observaciones)
    {
        this.observaciones = observaciones;
    }

    public int getTipoArchivo()
    {
        return tipoArchivo;
    }

    public void setTipoArchivo(int tipoArchivo)
    {
        this.tipoArchivo = tipoArchivo;
    }

    public Registro getRegistroSub()
    {
        return registroSub;
    }

    public void setRegistroSub(Registro registroSub)
    {
        this.registroSub = registroSub;
    }

    public String getConsecutivo()
    {
        return consecutivo;
    }

    public void setConsecutivo(String consecutivo)
    {
        this.consecutivo = consecutivo;
    }

    public String getIdEmpleado()
    {
        return idEmpleado;
    }

    public void setIdEmpleado(String idEmpleado)
    {
        this.idEmpleado = idEmpleado;
    }

    public boolean getActualizado()
    {
        return actualizado;
    }

    public void setActualizado(boolean actualizado)
    {
        this.actualizado = actualizado;
    }

    public String getTipoS()
    {
        return tipoS;
    }

    public void setTipoS(String tipoS)
    {
        this.tipoS = tipoS;
    }

    public boolean isEstadoInterfaz()
    {
        return estadoInterfaz;
    }

    public void setEstadoInterfaz(boolean estadoInterfaz)
    {
        this.estadoInterfaz = estadoInterfaz;
    }

    public String getRuta()
    {
        return ruta;
    }

    public void setRuta(String ruta)
    {
        this.ruta = ruta;
    }

    public boolean isBloqueaGuardar()
    {
        return bloqueaGuardar;
    }

    public void setBloqueaGuardar(boolean bloqueaGuardar)
    {
        this.bloqueaGuardar = bloqueaGuardar;
    }

    public boolean isBloquearArchivos()
    {
        return bloquearArchivos;
    }

    public void setBloquearArchivos(boolean bloquearArchivos)
    {
        this.bloquearArchivos = bloquearArchivos;
    }

    public String getNombreEmpleado()
    {
        return nombreEmpleado;
    }

    public void setNombreEmpleado(String nombreEmpleado)
    {
        this.nombreEmpleado = nombreEmpleado;
    }

    // </SET_GET_LISTAS_SUBFORM>
    // <SET_GET_PARAMETROS>
    // </SET_GET_PARAMETROS>
    // <SET_GET_ADICIONALES>
    // </SET_GET_ADICIONALES>
}
