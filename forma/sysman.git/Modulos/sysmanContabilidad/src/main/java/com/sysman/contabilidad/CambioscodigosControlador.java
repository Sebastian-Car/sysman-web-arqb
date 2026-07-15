/*-
 * CambioscodigosControlador.java
 *
 * 1.0
 * 
 * 05/12/2016
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */
package com.sysman.contabilidad;

import com.sysman.beanbase.BeanBaseDatosAcmeImpl;
import com.sysman.contabilidad.ejb.EjbContabilidadTresRemote;
import com.sysman.contabilidad.enums.CambioscodigosControladorEnum;
import com.sysman.contabilidad.enums.CambioscodigosControladorUrlEnum;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.enums.GenericUrlEnum;
import com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.CacheUtil;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.recursos.ejb.EjbSysmanUtilRemote;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.util.SysmanFunciones;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.event.RowEditEvent;
import org.primefaces.event.SelectEvent;

/**
 * Esta clase es el controlador para el formulario Reclasificacion
 * NIIF en Access "CambioDecodigo" perteneciente al m�dulo Utilidades
 * MGC, el cual es llamado desde Contabilidad\Mantenimiento\Utilidades
 * MGC\Procesos\Reclasificacion de Cuentas Contables
 *
 * @version 1.0, 05/12/2016
 * @author amonroy
 * @version 2.0, 10/04/2017 modificado por jcrodriguez
 * descrpcion:--depuracion del controlador --se agregaron los
 * servicios para el formulario
 * 
 * @author ybecerra
 * @version 3, 12/06/2017 Implementacion al llamado de
 * GeneralCodigoFormaEnum, para el codigo del formulario
 * 
 */
@ManagedBean
@ViewScoped
public class CambioscodigosControlador extends BeanBaseDatosAcmeImpl
{
    /**
     * Constante a nivel de clase que almacena el codigo de la
     * compania en la cual inicio sesion el usuario, el valor de esta
     * constante es asignado en el constructor a la variable de sesion
     * correspondiente
     */
    private final String compania;
    /**
     * Constante definida por el numero de veces que se realiza el
     * llamado a la tabla "RECLASIFICAR_NIIF" en el formulario.
     */
    private final String strTabla;
    /**
     * Constante definida por el numero de veces que se realiza el
     * llamado al campo CODIGO en el formulario, almacena el texto
     * CODIGO el cual es un campo del registro
     */
    private final String strCodigo;
    /**
     * Constante definida por el numero de veces que se realiza el
     * llamado a la tabla "D_RECLASIFICAR_NIIF" en el formulario.
     */
    private final String strTablaDetalles;
    /**
     * Constante definida por el numero de veces que se realiza el
     * llamado al campo NOMBRE en el formulario.
     */
    private final String strNombre;
    /**
     * Constante definida por el numero de veces que se realiza el
     * llamado al campo NUMERO en el formulario.
     */
    private final String strNumero;
    /**
     * Atributo que almacena el anio del plan contable seleccionado en
     * el formulario
     */
    private String anio;
    /**
     * Listado de registros para el comboBox de anio
     */
    private List<Registro> listaAno;
    /**
     * Listado de registros para el comboBox de codigo anterior cuando
     * se va a realizar un nuevo registro en el subformulario
     */
    private RegistroDataModelImpl listaCodigoAnterior;
    /**
     * Listado de registros para el comboBox de codigo anterior cuando
     * se va a realizar la edici�n de un registro en el subformulario
     */
    private RegistroDataModelImpl listaCodigoAnteriorE;
    /**
     * Listado de registros para el comboBox de tipo comprobante
     * cuando se va a realizar un nuevo registro en el subformulario
     */
    private RegistroDataModelImpl listaTipo;
    /**
     * Listado de registros para el comboBox de tipo comprobante
     * cuando se va a realizar la edici�n de un registro en el
     * subformulario
     */
    private RegistroDataModelImpl listaTipoE;
    /**
     * Listado de registros para el comboBox de codigo nuevo cuando se
     * va a realizar un nuevo registro en el subformulario
     */
    private RegistroDataModelImpl listaCodigoNuevo;
    /**
     * Listado de registros para el comboBox de codigo nuevo cuando se
     * va a realizar la edici�n de un registro en el subformulario
     */
    private RegistroDataModelImpl listaCodigoNuevoE;
    /**
     * Esta variable se usa como auxiliar para subformularios y en
     * esta se almacena el identificador del registro que se
     * selecciono
     */
    private String auxiliar;
    /**
     * Este atributo se usa como auxiliar para subformularios y
     * almacena el nombre del codigo anterior que se selecciona
     */
    private String nombreAnterior;
    /**
     * Este atributo se usa como auxiliar para subformularios y
     * almacena el nombre del codigo nuevo que se selecciona
     */
    private String nombreNuevo;
    /**
     * Listado de registros del subformulario D_CambiosDeNombres
     */
    private List<Registro> listaCambiosdenombres;
    /**
     * Atributo de referencia para el subformulario
     */

    private Registro registroSub;
    /**
     * variable EJB
     */
    @EJB
    EjbSysmanUtilRemote ejbSysmanUtil;
    /**
     * variable EJB
     */
    @EJB
    EjbContabilidadTresRemote ejbContabilidadTres;

    /**
     * Crea una nueva instancia de CambioscodigosControlador
     */
    public CambioscodigosControlador()
    {
        super();
        compania = SessionUtil.getCompania();
        strTabla = CambioscodigosControladorEnum.RECLASIFICAR_NIIF.getValue();
        strTablaDetalles = CambioscodigosControladorEnum.D_RECLASIFICAR_NIIF
                        .getValue();
        strCodigo = GeneralParameterEnum.CODIGO.getName();
        strNombre = GeneralParameterEnum.NOMBRE.getName();
        strNumero = GeneralParameterEnum.NUMERO.getName();
        try
        {
            numFormulario = GeneralCodigoFormaEnum.CAMBIOSCODIGOS_CONTROLADOR
                            .getCodigo();
            validarPermisos();
            // <INI_ADICIONAL>
            registroSub = new Registro(new HashMap<String, Object>());

            // </INI_ADICIONAL>
        }
        catch (Exception ex)
        {
            logger.error(ex.getMessage(), ex);
            SessionUtil.redireccionarMenuPermisos();
        }
    }

    /**
     * En este metodo se hace la invocacion de lo metodos de carga de
     * todas las listas, menos las que son de subformularios
     */
    @Override
    public void iniciarListas()
    {
        cargarListaAno();
    }

    /**
     * En este metodo se hace la invocacion de lo metodos de carga de
     * todas las listas que son de subformularios
     */
    @Override
    public void iniciarListasSub()
    {
        anio = registro.getCampos().get(GeneralParameterEnum.ANO.getName())
                        .toString();
        cargarListaCambiosdenombres();
        cargarListaCodigoAnterior();
        cargarListaCodigoAnteriorE();
        cargarListaTipo();
        cargarListaTipoE();
        cargarListaCodigoNuevo();
        cargarListaCodigoNuevoE();
    }

    /**
     * En este metodo se iguala a null todas las listas de los
     * subformularios
     */
    @Override
    public void iniciarListasSubNulo()
    {
        listaCambiosdenombres = null;
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
        enumBase = GenericUrlEnum.RECLASIFICAR_NIIF;
        buscarLlave();
        asignarOrigenDatos();
    }

    /**
     * Se realiza la asignacion de la variable origenDatos por la
     * consulta correspondiente del formulario
     * 
     */
    @Override
    public void asignarOrigenDatos()
    {
        buscarUrls();
    }

    /**
     * 
     * Carga la lista listaD_cambiosdenombres, la cual almacena los
     * registros del suformulario
     */
    public void cargarListaCambiosdenombres()
    {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        GenericUrlEnum.D_RECLASIFICAR_NIIF
                                                        .getGridKey());
        try
        {
            Map<String, Object> param = new TreeMap<>();
            param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
            param.put(GeneralParameterEnum.ANO.getName(), registro.getCampos()
                            .get(GeneralParameterEnum.ANO.getName()));
            param.put(CambioscodigosControladorEnum.STRNUMERO.getValue(),
                            registro.getCampos().get(GeneralParameterEnum.NUMERO
                                            .getName()));
            listaCambiosdenombres = RegistroConverter.toListRegistro(
                            requestManager.getList(urlBean.getUrl(), param),
                            CacheUtil.getLlaveServicio(urlConexionCache,
                                            strTablaDetalles));

        }
        catch (SystemException | SysmanException e)
        {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    /**
     * 
     * Carga la lista de registros de anios
     */
    public void cargarListaAno()
    {
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        try
        {
            listaAno = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            CambioscodigosControladorUrlEnum.URL10958
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
     * Carga la lista listaCodigoAnterior
     */
    public void cargarListaCodigoAnterior()
    {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        CambioscodigosControladorUrlEnum.URL12212
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(CambioscodigosControladorEnum.ANIO.getValue(), anio);

        listaCodigoAnterior = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, strCodigo);
    }

    /**
     * 
     * Carga la lista listaCodigoAnterior
     */
    public void cargarListaCodigoAnteriorE()
    {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        CambioscodigosControladorUrlEnum.URL12212
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(CambioscodigosControladorEnum.ANIO.getValue(), anio);

        listaCodigoAnteriorE = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, strCodigo);
    }

    /**
     * 
     * Carga el listado con los tipos de comprobantes
     */
    public void cargarListaTipo()
    {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        CambioscodigosControladorUrlEnum.URL11528
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        listaTipo = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, strCodigo);

    }

    /**
     * 
     * Carga la lista listaTipo al realizar una edicion en el
     * subformulario
     */
    public void cargarListaTipoE()
    {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        CambioscodigosControladorUrlEnum.URL11528
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        listaTipoE = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, strCodigo);
    }

    /**
     * 
     * Carga la lista listaCodigoNuevo
     */
    public void cargarListaCodigoNuevo()
    {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        CambioscodigosControladorUrlEnum.URL12212
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(CambioscodigosControladorEnum.ANIO.getValue(), anio);

        listaCodigoNuevo = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, strCodigo);

    }

    /**
     * 
     * Carga la lista listaCodigoNuevo
     */
    public void cargarListaCodigoNuevoE()
    {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        CambioscodigosControladorUrlEnum.URL12212
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(CambioscodigosControladorEnum.ANIO.getValue(), anio);

        listaCodigoNuevoE = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, strCodigo);

    }

    // </METODOS_CARGAR_LISTA>
    // <METODOS_CAMBIAR>
    public void cambiarAno()
    {
        anio = registro.getCampos().get(GeneralParameterEnum.ANO.getName())
                        .toString();
        try
        {

            String[] parametros = { "COMPANIA = ''" + compania
                + "'' AND ANO = " + anio };
            registro.getCampos().put(strNumero, ejbSysmanUtil
                            .generarConsecutivoConValorInicial(strTabla,
                                            SysmanFunciones.concatenar(
                                                            parametros),
                                            strNumero, "1"));
        }
        catch (SystemException e)
        {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    /**
     * Metodo ejecutado al cambiar el control CodigoAnterior en la
     * fila seleccionada dentro de la grilla
     * 
     * 
     * @param rowNum
     * indice de la fila seleccionada
     */
    public void cambiarCodigoAnteriorC(int rowNum)
    {

        listaCambiosdenombres.get(rowNum).getCampos().put(
                        CambioscodigosControladorEnum.NOMBREANTERIOR.getValue(),
                        nombreAnterior);
    }

    /**
     * Metodo ejecutado al cambiar el control CodigoNuevo en la fila
     * seleccionada dentro de la grilla
     * 
     * @param rowNum
     * indice de la fila seleccionada
     */
    public void cambiarCodigoNuevoC(int rowNum)
    {
        listaCambiosdenombres.get(rowNum).getCampos().put(
                        CambioscodigosControladorEnum.NOMBRENUEVO.getValue(),
                        nombreNuevo);
    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaCodigoAnterior. Actualiza los valores de los campos
     * CODIGOANTERIOR y NOMBREANTERIOR en el registro, de acuerdo a
     * las opciones seleccionadas para realizar la insercion de un
     * registro en el subformulario
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaCodigoAnterior(SelectEvent event)
    {
        Registro registroAux = (Registro) event.getObject();
        registroSub.getCampos().put(
                        CambioscodigosControladorEnum.CODIGOANTERIOR.getValue(),
                        registroAux.getCampos().get(strCodigo));
        registroSub.getCampos().put(
                        CambioscodigosControladorEnum.NOMBREANTERIOR.getValue(),
                        registroAux.getCampos().get(strNombre));
    }

    /**
     * metodo que valida el casteo a toString
     * 
     * @param campos
     * @param var
     * @return
     */
    private String cadenaVacia(Registro campos, String var)
    {
        return SysmanFunciones.validarCampoVacio(campos.getCampos(), var) ? null
            : campos.getCampos().get(var).toString();
    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaCodigoAnterior.
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaCodigoAnteriorE(SelectEvent event)
    {
        Registro registroAux = (Registro) event.getObject();
        auxiliar = cadenaVacia(registroAux, strCodigo);
        nombreAnterior = cadenaVacia(registroAux, strNombre);
    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista listaTipo
     * *
     * 
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaTipo(SelectEvent event)
    {
        Registro registroAux = (Registro) event.getObject();
        registroSub.getCampos().put(
                        CambioscodigosControladorEnum.TIPO.getValue(),
                        registroAux.getCampos().get(strCodigo));
    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista listaTipo
     * cuandos se esta modificando un registro en el subformulario
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaTipoE(SelectEvent event)
    {
        Registro registroAux = (Registro) event.getObject();
        auxiliar = cadenaVacia(registroAux, strCodigo);
    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaCodigoNuevo. Actualiza los valores de los campos
     * CODIGONUEVO y NOMBRENUEVO en el registro, de acuerdo a las
     * opciones seleccionadas para realizar la insercion de un
     * registro en el subformulario
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaCodigoNuevo(SelectEvent event)
    {
        Registro registroAux = (Registro) event.getObject();
        registroSub.getCampos().put(
                        CambioscodigosControladorEnum.CODIGONUEVO.getValue(),
                        registroAux.getCampos().get(strCodigo));
        registroSub.getCampos().put(
                        CambioscodigosControladorEnum.NOMBRENUEVO.getValue(),
                        registroAux.getCampos().get(strNombre));
    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaCodigoNuevo
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaCodigoNuevoE(SelectEvent event)
    {
        Registro registroAux = (Registro) event.getObject();
        auxiliar = cadenaVacia(registroAux, strCodigo);
        nombreNuevo = cadenaVacia(registroAux, strNombre);
    }

    // </METODOS_COMBOS_GRANDES>
    // <METODOS_ARBOL>
    /**
     * Valida si una reclasificacion ha sido realizada o no mediante
     * el
     */
    public void validarRealizado()
    {

        if (css != null)
        {
            if ((Boolean) registro.getCampos().get(
                            CambioscodigosControladorEnum.REALIZADO.getValue()))
            {
                JsfUtil.agregarMensajeInformativo(
                                idioma.getString("TB_TB2686"));
            }
        }
        else
        {
            agregarRegistroNuevo(false);
        }

    }

    /**
     * 
     * Metodo ejecutado al oprimir el boton Cambio en la vista
     *
     * Crea la cuenta contable cuando no existe
     *
     */
    public void oprimirCambio()
    {
        // <CODIGO_DESARROLLADO>
        if (!SysmanFunciones.validarCampoVacio(registro.getCampos(),
                        GeneralParameterEnum.ANO.getName()))
        {
            validarRealizado();
            if (listaCambiosdenombres.isEmpty())
            {
                JsfUtil.agregarMensajeInformativo(
                                idioma.getString("TB_TB2711"));
            }
            else
            {
                procedimiento();
                cargarRegistro(registro.getLlave(), accion,
                                registro.getIndice());
                JsfUtil.agregarMensajeInformativo(
                                idioma.getString("TB_TB1637"));

            }
        }

    }

    private void procedimiento()
    {
        try
        {
            String ano = registro.getCampos()
                            .get(GeneralParameterEnum.ANO.getName()).toString();
            String numero = registro.getCampos().get(strNumero).toString();
            Date fecha = (Date) registro.getCampos()
                            .get(GeneralParameterEnum.FECHA.getName());
            String usuario = registro.getCampos()
                            .get(GeneralParameterEnum.USUARIO.getName())
                            .toString();

            for (int i = 0; i < listaCambiosdenombres.size(); i++)
            {

                String tipo = listaCambiosdenombres.get(i).getCampos()
                                .get("TIPO").toString();
                String consecutivo = listaCambiosdenombres.get(i)
                                .getCampos()
                                .get(CambioscodigosControladorEnum.CONSECUTIVO
                                                .getValue())
                                .toString();
                ejbContabilidadTres.reclasificarniif(compania,
                                Integer.parseInt(ano), tipo,
                                Integer.parseInt(numero),
                                Long.parseLong(consecutivo), fecha, usuario);

            }
        }
        catch (NumberFormatException | SystemException e)
        {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    /**
     * Metodo de insercion del subformulario D_cambiosdenombres
     * 
     */
    public void agregarRegistroSubCambiosdenombres()
    {
        try
        {
            registroSub.getCampos().put(GeneralParameterEnum.COMPANIA.getName(),
                            compania);
            registroSub.getCampos().put(GeneralParameterEnum.ANO.getName(),
                            registro.getCampos().get(GeneralParameterEnum.ANO
                                            .getName()));
            registroSub.getCampos().put(strNumero,
                            registro.getCampos().get(strNumero));
            registroSub.getCampos()
                            .put(CambioscodigosControladorEnum.CONSECUTIVO
                                            .getValue(),
                                            ejbSysmanUtil.generarConsecutivoConValorInicial(
                                                            strTablaDetalles,
                                                            "COMPANIA=''"
                                                                + compania
                                                                + "''",
                                                            CambioscodigosControladorEnum.CONSECUTIVO
                                                                            .getValue(),
                                                            "1"));

            registroSub.getCampos().put(
                            GeneralParameterEnum.CREATED_BY.getName(),
                            SessionUtil.getUser().getCodigo());
            registroSub.getCampos().put(
                            GeneralParameterEnum.DATE_CREATED.getName(),
                            new Date());
            registroSub.getCampos().remove(
                            GeneralParameterEnum.DATE_MODIFIED.getName());
            registroSub.getCampos()
                            .remove(GeneralParameterEnum.MODIFIED_BY.getName());

            UrlBean urlCreate = UrlServiceUtil.getInstance()
                            .getUrlServiceByUrlByEnumID(
                                            GenericUrlEnum.D_RECLASIFICAR_NIIF
                                                            .getCreateKey());

            requestManager.save(urlCreate.getUrl(), urlCreate.getMetodo(),
                            registroSub.getCampos());
            cargarListaCambiosdenombres();
            JsfUtil.agregarMensajeInformativo(
                            idioma.getString(
                                            CambioscodigosControladorEnum.MSM_REGISTRO_INGRESADO
                                                            .getValue()));

        }
        catch (SystemException ex)
        {
            logger.error(ex.getMessage(), ex);
            JsfUtil.agregarMensajeError(ex.getMessage());
        }
        finally
        {
            registroSub = new Registro(new HashMap<String, Object>());
        }
    }

    /**
     * Metodo de edicion del subformulario D_cambiosdenombres
     * 
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void editarRegSubCambiosdenombres(RowEditEvent event)
    {
        Registro reg = (Registro) event.getObject();
        try
        {
            reg.getCampos().put(GeneralParameterEnum.MODIFIED_BY.getName(),
                            SessionUtil.getUser().getCodigo());
            reg.getCampos().put(GeneralParameterEnum.DATE_MODIFIED.getName(),
                            new Date());
            reg.getCampos().remove(CambioscodigosControladorEnum.KEY_COMPANIA
                            .getValue());
            reg.getCampos().remove(
                            CambioscodigosControladorEnum.KEY_ANO.getValue());
            reg.getCampos().remove(CambioscodigosControladorEnum.KEY_NUMERO
                            .getValue());
            reg.getCampos().remove(CambioscodigosControladorEnum.KEY_CONSECUTIVO
                            .getValue());
            reg.getCampos().remove(
                            CambioscodigosControladorEnum.KEY_TIPO.getValue());
            reg.getCampos().remove(GeneralParameterEnum.COMPANIA.getName());
            reg.getCampos().remove(GeneralParameterEnum.ANO.getName());
            reg.getCampos().remove(GeneralParameterEnum.NUMERO.getName());
            reg.getCampos().remove(GeneralParameterEnum.DATE_CREATED.getName());
            reg.getCampos().remove(GeneralParameterEnum.CREATED_BY.getName());

            UrlBean urlUpdate = UrlServiceUtil.getInstance()
                            .getUrlServiceByUrlByEnumID(
                                            GenericUrlEnum.D_RECLASIFICAR_NIIF
                                                            .getUpdateKey());

            requestManager.update(urlUpdate.getUrl(), urlUpdate.getMetodo(),
                            reg.getCampos(),
                            reg.getLlave());

            JsfUtil.agregarMensajeInformativo(
                            idioma.getString(
                                            CambioscodigosControladorEnum.MSM_REGISTRO_MODIFICADO
                                                            .getValue()));

        }
        catch (SystemException ex)
        {
            logger.error(ex.getMessage(), ex);
            JsfUtil.agregarMensajeError(ex.getMessage());
        }
        finally
        {
            cargarListaCambiosdenombres();
        }
    }

    /**
     * Metodo de eliminacion del suformulario D_cambiosdenombres
     * 
     * @param reg
     * registro seleccionado en el subformulario
     */
    public void eliminarRegSubCambiosdenombres(Registro reg)
    {
        try
        {
            UrlBean urlDelete = UrlServiceUtil.getInstance()
                            .getUrlServiceByUrlByEnumID(
                                            GenericUrlEnum.D_RECLASIFICAR_NIIF
                                                            .getDeleteKey());
            requestManager.delete(urlDelete.getUrl(), reg.getLlave());

            JsfUtil.agregarMensajeInformativo(
                            idioma.getString(
                                            CambioscodigosControladorEnum.MSM_REGISTRO_ELIMINADO
                                                            .getValue()));

            cargarListaCambiosdenombres();
        }
        catch (SystemException ex)
        {
            logger.error(ex.getMessage(), ex);
            JsfUtil.agregarMensajeError(ex.getMessage());
        }
    }

    /**
     * Metodo ejecutado cuando se cancela la edicion del registro
     * seleccionado para el subformulario D_cambiosdenombres
     *
     */
    public void cancelarEdicionCambiosdenombres()
    {
        cargarListaCambiosdenombres();
    }

    // </METODOS_SUBFORM>
    // <METODOS_ADICIONALES>
    // </METODOS_ADICIONALES>
    /**
     * Este metodo es invocado el metodo inicializar, se ejecutan las
     * acciones a tener en cuenta en el momento de apertura del
     * formulario
     */
    @Override
    public void abrirFormulario()
    {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado en el momento despues de cargar el registro
     * Asigna valores por omision cuando se va a insetar un nuevo
     * registro
     */
    @Override
    public void cargarRegistro()
    {
        // <CODIGO_DESARROLLADO>

        precargarRegistro();
        if (css != null)
        {
            anio = registro.getCampos().get(GeneralParameterEnum.ANO.getName())
                            .toString();
        }
        else
        {
            try
            {
                if (anio == null) {
                    
                    anio = Integer.toString(SysmanFunciones.ano(new Date()));  
                }
                
                String[] parametros = { "COMPANIA = ''" + compania
                    + "'' AND ANO = " + anio };
                anio = Integer.toString(
                                Calendar.getInstance().get(Calendar.YEAR));
                registro.getCampos().put(GeneralParameterEnum.ANO.getName(),
                                anio);
                registro.getCampos().put(strNumero,
                                ejbSysmanUtil.generarConsecutivoConValorInicial(
                                                strTabla,
                                                SysmanFunciones.concatenar(
                                                                parametros),
                                                strNumero,
                                                "1"));
                registro.getCampos().put("FECHA", new Date());
                registro.getCampos().put(GeneralParameterEnum.USUARIO.getName(),
                                SessionUtil.getUser().getCodigo());
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
     * @return VARIABLE
     */
    @Override
    public boolean insertarAntes()
    {
        // <CODIGO_DESARROLLADO>
        if (ACCION_INSERTAR.equals(accion))
        {
            registro.getCampos().put(GeneralParameterEnum.COMPANIA.getName(),
                            compania);
            registro.getCampos()
                            .remove(GeneralParameterEnum.MODIFIED_BY.getName());
            registro.getCampos().remove(
                            GeneralParameterEnum.DATE_MODIFIED.getName());
            registro.getCampos().remove("KEY_COMPANIA");
            registro.getCampos().remove("KEY_NUMERO");
            registro.getCampos().remove("KEY_ANO");
            registro.getCampos().remove("REALIZADO");

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
    public boolean insertarDespues()
    {
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
    public boolean actualizarAntes()
    {
        if (ACCION_MODIFICAR.equals(accion))
        {
            registro.getCampos()
                            .remove(GeneralParameterEnum.CREATED_BY.getName());
            registro.getCampos().remove(
                            GeneralParameterEnum.DATE_CREATED.getName());
            registro.getCampos()
                            .remove(GeneralParameterEnum.COMPANIA.getName());
            registro.getCampos().remove(GeneralParameterEnum.ANO.getName());
            registro.getCampos().remove(GeneralParameterEnum.NUMERO.getName());
            registro.getCampos().remove("KEY_COMPANIA");
            registro.getCampos().remove("KEY_NUMERO");
            registro.getCampos().remove("KEY_ANO");
        }

        return true;
    }

    /**
     * Metodo ejecutado despues de realizar la insercion y
     * actualizacion del registro
     * 
     * @return VARIABLE
     */
    @Override
    public boolean actualizarDespues()
    {
        // heredado del bean base
        return true;
    }

    /**
     * Metodo ejecutado antes de realizar la eliminacion del registro
     * 
     * @return VARIABLE
     */
    @Override
    public boolean eliminarAntes()
    {
        // heredado del bean base
        return true;
    }

    /**
     * Metodo ejecutado despues de realizar la eliminacion del
     * registro
     * 
     * @return VARIABLE
     */
    @Override
    public boolean eliminarDespues()
    {
        // heredado del bean base
        return true;
    }

    // set y get
    public String getAnio()
    {
        return anio;
    }

    public void setAnio(String anio)
    {
        this.anio = anio;
    }

    /**
     * Retorna la lista listaAno
     * 
     * @return listaAno
     */
    public List<Registro> getListaAno()
    {
        return listaAno;
    }

    /**
     * Asigna la lista listaAno
     * 
     * @param listaAno
     * Variable a asignar en listaAno
     */
    public void setListaAno(List<Registro> listaAno)
    {
        this.listaAno = listaAno;
    }

    /**
     * Retorna la lista listaCodigoAnterior
     * 
     * @return listaCodigoAnterior
     */
    public RegistroDataModelImpl getListaCodigoAnterior()
    {
        return listaCodigoAnterior;
    }

    /**
     * Asigna la lista listaCodigoAnterior
     * 
     * @param listaCodigoAnterior
     * Variable a asignar en listaCodigoAnterior
     */
    public void setListaCodigoAnterior(
        RegistroDataModelImpl listaCodigoAnterior)
    {
        this.listaCodigoAnterior = listaCodigoAnterior;
    }

    /**
     * Retorna la lista listaCodigoAnterior
     * 
     * @return listaCodigoAnterior
     */
    public RegistroDataModelImpl getListaCodigoAnteriorE()
    {
        return listaCodigoAnteriorE;
    }

    /**
     * Asigna la lista listaCodigoAnterior
     * 
     * @param listaCodigoAnterior
     * Variable a asignar en listaCodigoAnterior
     */
    public void setListaCodigoAnteriorE(
        RegistroDataModelImpl listaCodigoAnteriorE)
    {
        this.listaCodigoAnteriorE = listaCodigoAnteriorE;
    }

    /**
     * Retorna la lista listaTipo
     * 
     * @return listaTipo
     */
    public RegistroDataModelImpl getListaTipo()
    {
        return listaTipo;
    }

    /**
     * Asigna la lista listaTipo
     * 
     * @param listaTipo
     * Variable a asignar en listaTipo
     */
    public void setListaTipo(RegistroDataModelImpl listaTipo)
    {
        this.listaTipo = listaTipo;
    }

    /**
     * Retorna la lista listaTipo
     * 
     * @return listaTipo
     */
    public RegistroDataModelImpl getListaTipoE()
    {
        return listaTipoE;
    }

    /**
     * Asigna la lista listaTipo
     * 
     * @param listaTipo
     * Variable a asignar en listaTipo
     */
    public void setListaTipoE(RegistroDataModelImpl listaTipoE)
    {
        this.listaTipoE = listaTipoE;
    }

    /**
     * Retorna la lista listaCodigoNuevo
     * 
     * @return listaCodigoNuevo
     */
    public RegistroDataModelImpl getListaCodigoNuevo()
    {
        return listaCodigoNuevo;
    }

    /**
     * Asigna la lista listaCodigoNuevo
     * 
     * @param listaCodigoNuevo
     * Variable a asignar en listaCodigoNuevo
     */
    public void setListaCodigoNuevo(RegistroDataModelImpl listaCodigoNuevo)
    {
        this.listaCodigoNuevo = listaCodigoNuevo;
    }

    /**
     * Retorna la lista listaCodigoNuevo
     * 
     * @return listaCodigoNuevo
     */
    public RegistroDataModelImpl getListaCodigoNuevoE()
    {
        return listaCodigoNuevoE;
    }

    /**
     * Asigna la lista listaCodigoNuevo
     * 
     * @param listaCodigoNuevo
     * Variable a asignar en listaCodigoNuevo
     */
    public void setListaCodigoNuevoE(RegistroDataModelImpl listaCodigoNuevoE)
    {
        this.listaCodigoNuevoE = listaCodigoNuevoE;
    }

    /**
     * Retorna la variable auxiliar
     * 
     * @return auxiliar
     */
    public String getAuxiliar()
    {
        return auxiliar;
    }

    /**
     * Asigna la variable auxiliar
     * 
     * @param auxiliar
     * Variable a asignar en auxiliar
     */
    public void setAuxiliar(String auxiliar)
    {
        this.auxiliar = auxiliar;
    }

    // </SET_GET_LISTAS_COMBO_GRANDE>
    // <SET_GET_LISTAS_SUBFORM>
    public List<Registro> getListaCambiosdenombres()
    {
        return listaCambiosdenombres;
    }

    public void setListaCambiosdenombres(List<Registro> listaCambiosdenombres)
    {
        this.listaCambiosdenombres = listaCambiosdenombres;
    }

    // </SET_GET_LISTAS_SUBFORM>
    // <SET_GET_PARAMETROS>
    // </SET_GET_PARAMETROS>
    // <SET_GET_ADICIONALES>
    /**
     * Retorna el objeto registroSub
     * 
     * @return registroSub
     */
    public Registro getRegistroSub()
    {
        return registroSub;
    }

    /**
     * Asigna el objeto registroSub
     * 
     * @param registroSub
     * Variable a asignar en registroSub
     */
    public void setRegistroSub(Registro registroSub)
    {
        this.registroSub = registroSub;
    }
    // </SET_GET_ADICIONALES>
}
