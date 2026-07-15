package com.sysman.predial;

import com.sysman.beanbase.BeanBaseModal;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.ReportesBean;
import com.sysman.jsfutil.ReportesBean.FORMATOS;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.persistencia.ConectorPool;
import com.sysman.predial.ejb.EjbPredialDosRemote;
import com.sysman.predial.enums.PredialestadocuentasControladorUrlEnum;
import com.sysman.recursos.ejb.EjbSysmanUtilRemote;
import com.sysman.reportes.Reporteador;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.util.SysmanConstantes;
import com.sysman.util.SysmanFunciones;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.ParseException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.event.SelectEvent;
import org.primefaces.model.StreamedContent;

import net.sf.jasperreports.engine.JRException;

/**
 *
 * @author ybecerra
 * @version 1, 13/06/2016
 * 
 * @author spina - refactorizo conexiones
 * @version 2, 13/06/2017
 * 
 * @author ybecerra
 * @version 3, 14/07/2017, proceso de Refactoring
 * 
 */
@ManagedBean
@ViewScoped

public class PredialestadocuentasControlador extends BeanBaseModal
{
    /**
     * Constante a nivel de clase que almacena el codigo de la
     * compania en la cual inicio sesion el usuario, el valor de esta
     * constante es asignado en el constructor a la variable de sesion
     * correspondiente
     */
    private final String compania;
    /**
     * Constante definida para almacenar el codigo del modulo por el
     * cual se ingresa en la aplicacion
     */
    private final String modulo;
    private final String codigoCons;
    private final String nombreCons;

    // <DECLARAR_ATRIBUTOS>
    private boolean porUsuario;
    private boolean porPredio;
    private String codigo;
    private String usuario;
    private String nombre;
    private String nit;
    private boolean codigoVisible;
    private boolean usuarioVisible;
    private StreamedContent archivoDescarga;
    private String condicion;
    private String numeroOrden;

    String codigoUsuario;
    // </DECLARAR_ATRIBUTOS>
    // <DECLARAR_PARAMETROS>
    // </DECLARAR_PARAMETROS>
    // <DECLARAR_LISTAS>
    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>
    private RegistroDataModelImpl listaCodigo;
    private RegistroDataModelImpl listaUsuario;
    // </DECLARAR_LISTAS_COMBO_GRANDE>
    @EJB
    private EjbSysmanUtilRemote ejbSysmanUtil;
    @EJB
    private EjbPredialDosRemote ejbPredialDos;

    /**
     * Atributo usado para descargar contenidos de archivos desde la
     * vista
     */
    public PredialestadocuentasControlador()
    {
        super();
        compania = SessionUtil.getCompania();
        modulo = SessionUtil.getModulo();
        codigoCons = GeneralParameterEnum.CODIGO.getName();
        nombreCons = GeneralParameterEnum.NOMBRE.getName();
        condicion = "";
        try
        {
            numFormulario = GeneralCodigoFormaEnum.PREDIALESTADOCUENTAS_CONTROLADOR
                            .getCodigo();
            validarPermisos();
            // <INI_ADICIONAL>
            // </INI_ADICIONAL>
        }
        catch (Exception ex)
        {
            Logger.getLogger(PredialestadocuentasControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
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
    public void inicializar()
    {
        // <CARGAR_LISTA>
        // </CARGAR_LISTA>
        // <CARGAR_LISTA_COMBO_GRANDE>
        cargarListaCodigo();
        cargarListaUsuario();
        // </CARGAR_LISTA_COMBO_GRANDE>
        abrirFormulario();
    }

    /**
     * Este metodo es invocado el metodo inicializar, se ejecutan las
     * acciones a tener en cuenta en el momento de apertura del
     * formulario
     */
    @Override
    public void abrirFormulario()
    {

        porPredio = true;
        codigoVisible = true;
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    // <METODOS_CARGAR_LISTA>
    /**
     * 
     * Carga la lista listaCodigo
     */
    public void cargarListaCodigo()
    {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        PredialestadocuentasControladorUrlEnum.URL4045
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.NUMERO_ORDEN.getName(),
                        SysmanConstantes.NUMERO_ORDEN_PREDIAL);

        listaCodigo = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, codigoCons);
    }

    /**
     * 
     * Carga la lista listaUsuario
     */
    public void cargarListaUsuario()
    {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        PredialestadocuentasControladorUrlEnum.URL5227
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
        param.put(GeneralParameterEnum.NUMERO_ORDEN.getName(),
                        SysmanConstantes.NUMERO_ORDEN_PREDIAL);

        listaUsuario = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, "NIT");

    }

    // </METODOS_CARGAR_LISTA>
    // <METODOS_BOTONES>
    /**
     * 
     * Metodo ejecutado al oprimir el boton Presentar en la vista
     *
     */
    public void oprimirPresentar()
    {
        // <CODIGO_DESARROLLADO>
        archivoDescarga = null;
        generarInforme(FORMATOS.PDF);
        // </CODIGO_DESARROLLADO>
    }

    /**
     * 
     * Metodo ejecutado al oprimir el boton Excel en la vista
     *
     */
    public void oprimirExcel()
    {
        // <CODIGO_DESARROLLADO>
        archivoDescarga = null;
        generarInforme(FORMATOS.EXCEL97);
        // </CODIGO_DESARROLLADO>
    }

    // </METODOS_BOTONES>

    /**
     * Metodo ejecutado al oprimir los botones PDF o excel del
     * formulario
     * 
     * @param formato
     */
    public void generarInforme(ReportesBean.FORMATOS formato)
    {
        String parReporte;
        String codPredio;
        if ("ALCALDIA DE FUSAGASUGA".equals(
                        SessionUtil.getCompaniaIngreso().getNombre()))
        {
            parReporte = "000911PREDIALESTADOCUENTA159USUARIO";

        }
        else
        {
            parReporte = "000903ESTADOCUENTASTDUSUARIO";
        }

        if (porUsuario)
        {
            codPredio = codigoUsuario;
        }
        else
        {
            codPredio = codigo;
        }

        try
        {
            condicion = ejbPredialDos.armaConsultaEstadoCuenta(compania, nit,
                            codPredio, usuario, porUsuario, porPredio,
                            SysmanConstantes.NUMERO_ORDEN_PREDIAL,
                            SessionUtil.getCompaniaIngreso().getNombre());
            String parametro = ejbSysmanUtil.consultarParametro(compania,
                            "VER COPROPIETARIOS EN ESTADO DE CUENTA", modulo,
                            new Date(), true);
            int sub;

            HashMap<String, Object> reemplazar = new HashMap<>();

            reemplazar.put("condicion", condicion);
            reemplazar.put("numeroOrden", "'" + numeroOrden + "'");
            reemplazar.put("ano", SysmanFunciones.ano(
                            new Date()));

            Map<String, Object> parametros = new HashMap<>();

            sub = "SI".equals(parametro) ? 1 : 0;
            parametros.put("PR_NOMBRECOMPANIA", SessionUtil.getCompaniaIngreso()
                            .getNombre().toUpperCase());
            parametros.put("PR_NITCOMPANIA",
                            SessionUtil.getCompaniaIngreso().getNit());
            parametros.put("PR_ESTADO", "ESTADO DE CUENTA A " + SysmanFunciones
                            .convertirAFechaCadena(new Date()));
            parametros.put("PR_SUBREPORTE", sub);

            Reporteador.resuelveConsulta(parReporte, Integer.parseInt(modulo),
                            reemplazar, parametros);
            archivoDescarga = JsfUtil.exportarStreamed(parReporte, parametros,
                            ConectorPool.ESQUEMA_SYSMAN, formato);
        }
        catch (FileNotFoundException e)
        {
            JsfUtil.agregarMensajeError(
                            idioma.getString("MSM_INFORME_VAR_NO_EXISTE")
                                            .replace("s$reporte$s", parReporte)
                                + e.getMessage());
            logger.error(e.getMessage(), e);
        }
        catch (ParseException | IOException | JRException
                        | SysmanException | SystemException ex)
        {
            JsfUtil.agregarMensajeError(ex.getMessage());
            logger.error(ex.getMessage(), ex);
        }

    }

    // <METODOS_CAMBIAR>
    /**
     * Metodo ejecutado al cambiar el control PorUsuario
     * 
     */
    public void cambiarPorPredio()
    {

        if (porPredio)
        {
            codigoVisible = true;
            porUsuario = false;
            usuarioVisible = false;
            nit = null;
            usuario = null;

        }
        else
        {

            porPredio = true;

        }
    }

    /**
     * Metodo ejecutado al cambiar el control PorPredio
     * 
     * 
     */
    public void cambiarPorUsuario()
    {

        if (porUsuario)
        {
            usuarioVisible = true;
            porPredio = false;
            codigoVisible = false;
            codigo = null;
            nombre = null;

        }
        else
        {

            porUsuario = true;

        }
    }

    // </METODOS_CAMBIAR>
    // <METODOS_COMBOS_GRANDES>
    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaCodigo
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaCodigo(SelectEvent event)
    {
        Registro registroAux = (Registro) event.getObject();
        codigo = registroAux.getCampos().get(codigoCons).toString();
        nombre = registroAux.getCampos().get(nombreCons).toString();
    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaUsuario
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaUsuario(SelectEvent event)
    {
        Registro registroAux = (Registro) event.getObject();
        usuario = registroAux.getCampos().get(nombreCons).toString();
        nit = registroAux.getCampos().get("NIT").toString();
        codigoUsuario = registroAux.getCampos().get(codigoCons).toString();
    }

    // </METODOS_COMBOS_GRANDES>

    // <SET_GET_ATRIBUTOS>
    /**
     * Retorna la variable porUsuario
     * 
     * @return porUsuario
     */
    public boolean getPorUsuario()
    {
        return porUsuario;
    }

    /**
     * Asigna la variable porUsuario
     * 
     * @param porUsuario
     * Variable a asignar en porUsuario
     */
    public void setPorUsuario(boolean porUsuario)
    {
        this.porUsuario = porUsuario;
    }

    /**
     * Retorna la variable porPredio
     * 
     * @return porPredio
     */
    public boolean getPorPredio()
    {
        return porPredio;
    }

    /**
     * Asigna la variable porPredio
     * 
     * @param porPredio
     * Variable a asignar en porPredio
     */
    public void setPorPredio(boolean porPredio)
    {
        this.porPredio = porPredio;
    }

    /**
     * Retorna la variable codigo
     * 
     * @return codigo
     */
    public String getCodigo()
    {
        return codigo;
    }

    /**
     * Asigna la variable codigo
     * 
     * @param codigo
     * Variable a asignar en codigo
     */
    public void setCodigo(String codigo)
    {
        this.codigo = codigo;
    }

    /**
     * Retorna la variable usuario
     * 
     * @return usuario
     */
    public String getUsuario()
    {
        return usuario;
    }

    /**
     * Asigna la variable nombre
     * 
     * @param nombre
     * Variable a asignar en nombre
     */
    public void setUsuario(String usuario)
    {
        this.usuario = usuario;
    }

    /**
     * Retorna la variable nombre
     * 
     * @return nombre
     */
    public String getNombre()
    {
        return nombre;
    }

    /**
     * Asigna la variable nombre
     * 
     * @param nombre
     * Variable a asignar en nombre
     */
    public void setNombre(String nombre)
    {
        this.nombre = nombre;
    }

    /**
     * Atributo usado para descargar contenidos de archivos desde la
     * vista
     */
    public StreamedContent getArchivoDescarga()
    {
        return archivoDescarga;
    }

    /**
     * Retorna la variable codigoVisible
     * 
     * @return codigoVisible
     */
    public boolean isCodigoVisible()
    {
        return codigoVisible;
    }

    /**
     * Asigna la variable codigoVisible
     * 
     * @param codigoVisible
     * Variable a asignar en codigoVisible
     */
    public void setCodigoVisible(boolean codigoVisible)
    {
        this.codigoVisible = codigoVisible;
    }

    /**
     * Retorna la variable usuarioVisible
     * 
     * @return usuarioVisible
     */
    public boolean isUsuarioVisible()
    {
        return usuarioVisible;
    }

    /**
     * Asigna la variable usuarioVisible
     * 
     * @param usuarioVisible
     * Variable a asignar en usuarioVisible
     */
    public void setUsuarioVisible(boolean usuarioVisible)
    {
        this.usuarioVisible = usuarioVisible;
    }

    /**
     * Retorna la variable nit
     * 
     * @return nit
     */
    public String getNit()
    {
        return nit;
    }

    /**
     * Asigna la variable nit
     * 
     * @param nit
     * Variable a asignar en nit
     */
    public void setNit(String nit)
    {
        this.nit = nit;
    }

    // </SET_GET_ATRIBUTOS>
    // <SET_GET_PARAMETROS>
    // </SET_GET_PARAMETROS>
    // <SET_GET_LISTAS>
    // </SET_GET_LISTAS>
    // <SET_GET_LISTAS_COMBO_GRANDE>
    /**
     * Retorna la lista listaCodigo
     * 
     * @return listaCodigo
     */
    public RegistroDataModelImpl getListaCodigo()
    {
        return listaCodigo;
    }

    /**
     * Asigna la lista listaCodigo
     * 
     * @param listaCodigo
     * Variable a asignar en listaCodigo
     */
    public void setListaCodigo(RegistroDataModelImpl listaCodigo)
    {
        this.listaCodigo = listaCodigo;
    }

    /**
     * Retorna la lista listaUsuario
     * 
     * @return listaUsuario
     */
    public RegistroDataModelImpl getListaUsuario()
    {
        return listaUsuario;
    }

    /**
     * Asigna la lista listaUsuario
     * 
     * @param listaUsuario
     * Variable a asignar en listaUsuario
     */
    public void setListaUsuario(RegistroDataModelImpl listaUsuario)
    {
        this.listaUsuario = listaUsuario;
    }
    // </SET_GET_LISTAS_COMBO_GRANDE>
}
