/*-
 * PagosespecialesControlador.java
 *
 * 1.0
 *
 * 28/03/2019
 *
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */
package com.sysman.nomina;

import com.sysman.beanbase.BeanBaseDatosAcmeImpl;
import com.sysman.cache.enums.UrlServiceCache;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.enums.GenericUrlEnum;
import com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException;
import com.sysman.exception.SystemException;
import com.sysman.general.EscalafonsControlador;
import com.sysman.jsfutil.CacheUtil;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.nomina.enums.NovedadesControladorEnum;
import com.sysman.nomina.enums.PagosespecialesControladorControladorEnum;
import com.sysman.nomina.enums.PagosespecialesControladorUrlEnum;
import com.sysman.recursos.ejb.EjbSysmanUtilRemote;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.util.SysmanFunciones;

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

import org.primefaces.event.RowEditEvent;
import org.primefaces.event.SelectEvent;

/**
 * Controlador pde
 *
 * @version 1.0, 28/03/2019
 * @author mzanguna
 */
@ManagedBean
@ViewScoped
public class PagosespecialesControlador extends BeanBaseDatosAcmeImpl
{
    /**
     * Constante a nivel de clase que almacena el codigo de la compania en la cual inicio sesion el usuario, el valor de esta constante es asignado en el constructor a la variable de sesion
     * correspondiente
     */
    private final String compania;
    // <DECLARAR_ATRIBUTOS>
    // </DECLARAR_ATRIBUTOS>
    // <DECLARAR_LISTAS>
    /**
    
     */
    private List<Registro> listaCbAno;
    private List<Registro> listaCbMes;
    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>
    /**
    
     */
    private RegistroDataModelImpl listaCbCategoria;
    /**
    
     */
    private RegistroDataModelImpl listaCbCategoriaE;
    /**
     * Esta variable se usa como auxiliar para subformularios y en esta se alamcena el identificador del registro que se selecciono
     */
    private String auxiliar;

    private String escalafon;
    /**
    
     */
    private RegistroDataModelImpl listaCbConcepto;
    // </DECLARAR_LISTAS_COMBO_GRANDE>
    // <DECLARAR_LISTAS_SUBFORM>
    /**
    
     */
    private RegistroDataModelImpl listaSfdetalle;
    // </DECLARAR_LISTAS_SUBFORM>
    // <DECLARAR_PARAMETROS>
    // </DECLARAR_PARAMETROS>
    // <DECLARAR_ADICIONALES>
    /**
     * Atributo de referencia para el subformulario
     */
    private Registro registroSub;

    @EJB
    private EjbSysmanUtilRemote ejbSysmanUtil;

    private int anoDetallePago;

    private int indiceSfdetalle;

    // </DECLARAR_ADICIONALES>
    /**
     * Crea una nueva instancia de PagosespecialesControlador
     */
    public PagosespecialesControlador()
    {
        super();
        compania = SessionUtil.getCompania();
        try
        {
            numFormulario = 2050;
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
     * En este metodo se hace la invocacion de lo metodos de carga de todas las listas, menos las que son de subformularios
     */
    @Override
    public void iniciarListas()
    {
        cargarListaCbConcepto();
        cargarListaCbMes();
    }

    /**
     * En este metodo se hace la invocacion de lo metodos de carga de todas las listas que son de subformularios
     */
    @Override
    public void iniciarListasSub()
    {
        cargarListaSfdetalle();
        cargarListaCbAno();
        cargarListaCbCategoria();
        cargarListaCbCategoriaE();
    }

    /**
     * En este metodo se iguala a null todas las listas de los subformularios
     */
    @Override
    public void iniciarListasSubNulo()
    {
        // <CARGAR_LISTAS_SUBFORM_NULL>
        listaSfdetalle = null;
        // </CARGAR_LISTAS_SUBFORM_NULL>
    }

    /**
     * Este metodo se ejecuta justo despues de que el objeto de la clase del Bean ha sido creado, en este se realizan las asignaciones iniciales necesarias para la visualizacion del formulario, como
     * son tablas, origenes de datos, inicializacion de listas y demas necesarios
     */
    @PostConstruct
    public void inicializar()
    {
        enumBase = GenericUrlEnum.PAGOESPECIAL;
        buscarLlave();
        asignarOrigenDatos();
        abrirFormulario();
    }

    /**
     * Se realiza la asignacion de la variable origenDatos por la consulta correspondiente del formulario
     *
     *
     *
     */
    @Override
    public void asignarOrigenDatos()
    {
        buscarUrls();
        parametrosListado.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
    }

    /**
     *
     * Carga la lista listaSfdetalle
     *
     *
     */
    public void cargarListaSfdetalle()
    {
        try
        {
            UrlBean urlBean = UrlServiceUtil.getInstance()
                            .getUrlServiceByUrlByEnumID(
                                            PagosespecialesControladorUrlEnum.URL0004
                                                            .getValue());
            Map<String, Object> param = new TreeMap<>();
            param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
            param.put(PagosespecialesControladorControladorEnum.CODPAGO.getValue(), registro
                            .getCampos().get(GeneralParameterEnum.CODIGO.getName()));

            listaSfdetalle = new RegistroDataModelImpl(urlBean.getUrl(),
                            urlBean.getUrlConteo().getUrl(), param,
                            CacheUtil.getLlaveServicio(
                                            UrlServiceCache.SYSMANDSUNIST,
                                            PagosespecialesControladorControladorEnum.DETALLEPAGOESPECIAL.getValue()));

        }
        catch (SysmanException e)
        {
            Logger.getLogger(PagosespecialesControlador.class.getName())
                            .log(Level.SEVERE, null, e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    // <METODOS_CARGAR_LISTA>
    /**
     *
     * Carga la lista listaCbAno
     *
     */
    public void cargarListaCbAno()
    {
        Map<String, Object> param = new HashMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        try
        {
            listaCbAno = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            PagosespecialesControladorUrlEnum.URL0001
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
     * Carga la lista listaCbCategoria
     *
     *
     */
    public void cargarListaCbCategoria()
    {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        PagosespecialesControladorUrlEnum.URL0003
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(PagosespecialesControladorControladorEnum.ANO.getValue(), anoDetallePago);

        listaCbCategoria = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, PagosespecialesControladorControladorEnum.CATEGORIA.getValue());
    }

    /**
     *
     * Carga la lista listaCbCategoria
     *
     *
     */
    public void cargarListaCbCategoriaE()
    {
        listaCbCategoriaE = listaCbCategoria;
    }

    /**
     *
     * Carga la lista listaCbConcepto
     *
     *
     */
    public void cargarListaCbConcepto()
    {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        PagosespecialesControladorUrlEnum.URL0002
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        listaCbConcepto = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param, true,
                        NovedadesControladorEnum.ID_DE_CONCEPTO.getValue());
    }

    public void cargarListaCbMes()
    {
        try
        {
            Map<String, Object> param = new TreeMap<>();
            param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
            param.put(GeneralParameterEnum.ANO.getName(), Integer.parseInt(SessionUtil.getSessionVar("anioNomina").toString()));

            listaCbMes = RegistroConverter.toListRegistro(requestManager.getList(
                            UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            PagosespecialesControladorUrlEnum.URL0019
                                                                            .getValue())
                                            .getUrl(),
                            param));
        }
        catch (SystemException e)
        {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    // </METODOS_CARGAR_LISTA>
    // <METODOS_CAMBIAR>
    public void cambiarCbAno()
    {
        anoDetallePago = Integer.parseInt(registroSub.getCampos().get("ANO").toString());
        cargarListaCbCategoria();
        // </CODIGO_DESARROLLADO>
    }

    public void cambiarCbAnoC(int rowNum)
    {
        anoDetallePago = Integer.parseInt(listaSfdetalle.getDatasource().get(rowNum % 10).getCampos().get("ANO").toString());
        cargarListaCbCategoria();
        cargarListaCbCategoriaE();
    }

    public void cambiarCkPrimaAnual()
    {
        //
    }

    // </METODOS_CAMBIAR>
    // <METODOS_COMBOS_GRANDES>
    /**
     *
     * Metodo ejecutado al seleccionar una fila de la lista listaCbCategoria
     *
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaCbCategoria(SelectEvent event)
    {
        Registro registroAux = (Registro) event.getObject();
        registroSub.getCampos().put("CATEGORIAREF", registroAux.getCampos().get("ID_DE_CATEGORIA"));
        registroSub.getCampos().put(PagosespecialesControladorControladorEnum.CATESCALAFON.getValue(),
                        registroAux.getCampos().get("ESCALAFON"));
    }

    /**
     *
     * Metodo ejecutado al seleccionar una fila de la lista listaCbCategoria
     *
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaCbCategoriaE(SelectEvent event)
    {
        Registro registroAux = (Registro) event.getObject();
        auxiliar = (String) registroAux.getCampos().get("ID_DE_CATEGORIA");
        escalafon = (String) registroAux.getCampos().get("ESCALAFON");
    }

    /**
     *
     * Metodo ejecutado al seleccionar una fila de la lista listaCbConcepto
     *
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaCbConcepto(SelectEvent event)
    {
        Registro registroAux = (Registro) event.getObject();
        registro.getCampos().put("ID_DE_CONCEPTO", registroAux.getCampos().get("ID_DE_CONCEPTO"));
    }

    // </METODOS_COMBOS_GRANDES>
    // <METODOS_ARBOL>
    // </METODOS_ARBOL>
    // <METODOS_BOTONES>
    // </METODOS_BOTONES>
    // <METODOS_SUBFORM>
    /**
     * Metodo de insercion del formulario Sfdetalle
     *
     *
     */
    public void agregarRegistroSubSfdetalle()
    {
        try
        {
            UrlBean urlBean = UrlServiceUtil.getInstance()
                            .getUrlServiceByUrlByEnumID(
                                            PagosespecialesControladorUrlEnum.URL0005
                                                            .getValue());

            registroSub.getCampos().put("COMPANIA", compania);
            registroSub.getCampos().put("CODIGOPAGOESP", registro.getCampos().get(GeneralParameterEnum.CODIGO.getName()));
            registroSub.getCampos().put(GeneralParameterEnum.CONSECUTIVO.getName(),

                            ejbSysmanUtil.generarSiguienteConsecutivo("DETALLEPAGOESPECIAL",
                                            "COMPANIA = " + compania + "AND CODIGOPAGOESP = " + registro.getCampos().get("CODIGO"),
                                            GeneralParameterEnum.CONSECUTIVO.getName()));

            registroSub.getCampos().put(
                            GeneralParameterEnum.DATE_CREATED.getName(),
                            new Date());
            registroSub.getCampos().put(
                            GeneralParameterEnum.CREATED_BY.getName(),
                            SessionUtil.getUser().getCodigo());

            requestManager.save(urlBean.getUrl(), urlBean.getMetodo(),
                            registroSub.getCampos());

            JsfUtil.agregarMensajeInformativo(
                            idioma.getString("TB_TB1945"));

            cargarListaSfdetalle();
        }
        catch (SystemException ex)
        {
            Logger.getLogger(PagosespecialesControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            JsfUtil.agregarMensajeError(ex.getMessage());
        }
        finally
        {
            registroSub.getCampos().clear();
        }
    }

    public void activarEdicionSfdetalle(Registro reg)
    {
        indiceSfdetalle = listaSfdetalle.getRowIndex();
        anoDetallePago = Integer.parseInt(reg.getCampos().get("ANO").toString());
        cargarListaCbCategoria();
        cargarListaCbCategoriaE();
    }

    /**
     * Metodo de edicion del formulario Sfdetalle
     *
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void editarRegSubSfdetalle(RowEditEvent event)
    {
        Registro reg = (Registro) event.getObject();
        try
        {
            UrlBean urlBean = UrlServiceUtil.getInstance()
                            .getUrlServiceByUrlByEnumID(
                                            PagosespecialesControladorUrlEnum.URL0007
                                                            .getValue());

            reg.getCampos().put(GeneralParameterEnum.MODIFIED_BY.getName(),
                            SessionUtil.getUser().getCodigo());
            reg.getCampos().put(GeneralParameterEnum.DATE_MODIFIED.getName(), new Date());

            reg.getCampos().remove(GeneralParameterEnum.COMPANIA.getName());
            reg.getCampos().remove("CODIGOPAGOESP");
            reg.getCampos().remove(GeneralParameterEnum.CONSECUTIVO.getName());
            reg.getCampos().remove("NOMBRE_CATEGORIA");

            if (auxiliar != null)
            {
                reg.getCampos().put("CATEGORIAREF", auxiliar);
                reg.getCampos().put(PagosespecialesControladorControladorEnum.CATESCALAFON.getValue(), escalafon);
            }

            requestManager.update(urlBean.getUrl(), urlBean.getMetodo(),
                            reg.getCampos(), reg.getLlave());
            JsfUtil.agregarMensajeInformativo(
                            idioma.getString("MSM_REGISTRO_MODIFICADO"));
        }
        catch (SystemException ex)
        {
            Logger.getLogger(EscalafonsControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            JsfUtil.agregarMensajeError(SysmanFunciones.concatenar(
                            idioma.getString(
                                            PagosespecialesControladorControladorEnum.MSM_TRANS_INTERRUMPIDA
                                                            .getValue()),
                            ex.getMessage()));
        }
        finally
        {
            cargarListaSfdetalle();
        }
    }

    /**
     * Metodo de eliminacion del formulario Sfdetalle
     *
     *
     *
     * @param reg
     * registro seleccionado en el subformulario
     */
    public void eliminarRegSubSfdetalle(Registro reg)
    {
        try
        {
            UrlBean urlBean = UrlServiceUtil.getInstance()
                            .getUrlServiceByUrlByEnumID(
                                            PagosespecialesControladorUrlEnum.URL0006
                                                            .getValue());
            requestManager.delete(urlBean.getUrl(), reg.getLlave());

            JsfUtil.agregarMensajeInformativo(
                            idioma.getString("MSM_REGISTRO_ELIMINADO"));
            cargarListaSfdetalle();
        }
        catch (SystemException ex)
        {
            logger.error(ex.getMessage(), ex);
            JsfUtil.agregarMensajeError(ex.getMessage());
        }
    }

    /**
     * Metodo ejecutado cuando se cancela la edicion del registro seleccionado para el subformulario Sfdetalle
     *
     *
     */
    public void cancelarEdicionSfdetalle()
    {
        cargarListaSfdetalle();
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
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado en el momento despues de cargar el registro
     *
     *
     */
    @Override
    public void cargarRegistro()
    {
        // <CODIGO_DESARROLLADO>
        precargarRegistro();
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado antes de realizar la insercion del registro
     *
     *
     */
    @Override
    public boolean insertarAntes()
    {
        // <CODIGO_DESARROLLADO>
        registro.getCampos().put("COMPANIA", compania);
        try
        {
            registro.getCampos().put(GeneralParameterEnum.CODIGO.getName(),
                            ejbSysmanUtil.generarSiguienteConsecutivo("PAGOESPECIAL", "COMPANIA = " + compania,
                                            GeneralParameterEnum.CODIGO.getName()));

            if (registro.getCampos().get(GeneralParameterEnum.MES.getName()) == null)
            {
                registro.getCampos().put(GeneralParameterEnum.MES.getName(), 0);
            }
        }
        catch (SystemException e)
        {
            Logger.getLogger(PagosespecialesControlador.class.getName())
                            .log(Level.SEVERE, null, e);
            JsfUtil.agregarMensajeError(e.getMessage());

        }
        // </CODIGO_DESARROLLADO>
        return true;
    }

    /**
     * Metodo ejecutado despues de realizar la insercion del registro
     *
     *
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
     *
     *
     *
     */
    @Override
    public boolean actualizarAntes()
    {
        // <CODIGO_DESARROLLADO>
        if (registro.getCampos().get(GeneralParameterEnum.MES.getName()) == null)
        {
            registro.getCampos().put(GeneralParameterEnum.MES.getName(), 0);
        }
        // </CODIGO_DESARROLLADO>
        return true;
    }

    /**
     * Metodo ejecutado despues de realizar la insercion y actualizacion del registro
     *
     *
     *
     *
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
     *
     *
     *
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
     *
     *
     *
     */
    @Override
    public boolean eliminarDespues()
    {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
        return true;
    }

    // <SET_GET_ATRIBUTOS>
    // </SET_GET_ATRIBUTOS>
    // <SET_GET_LISTAS>
    /**
     * Retorna la lista listaCbAno
     *
     * @return listaCbAno
     */
    public List<Registro> getListaCbAno()
    {
        return listaCbAno;
    }

    /**
     * Asigna la lista listaCbAno
     *
     * @param listaCbAno
     * Variable a asignar en listaCbAno
     */
    public void setListaCbAno(List<Registro> listaCbAno)
    {
        this.listaCbAno = listaCbAno;
    }

    // </SET_GET_LISTAS>
    // <SET_GET_LISTAS_COMBO_GRANDE>
    /**
     * Retorna la lista listaCbCategoria
     *
     * @return listaCbCategoria
     */
    public RegistroDataModelImpl getListaCbCategoria()
    {
        return listaCbCategoria;
    }

    /**
     * Asigna la lista listaCbCategoria
     *
     * @param listaCbCategoria
     * Variable a asignar en listaCbCategoria
     */
    public void setListaCbCategoria(RegistroDataModelImpl listaCbCategoria)
    {
        this.listaCbCategoria = listaCbCategoria;
    }

    /**
     * Retorna la lista listaCbCategoria
     *
     * @return listaCbCategoria
     */
    public RegistroDataModelImpl getListaCbCategoriaE()
    {
        return listaCbCategoriaE;
    }

    /**
     * Asigna la lista listaCbCategoria
     *
     * @param listaCbCategoria
     * Variable a asignar en listaCbCategoria
     */
    public void setListaCbCategoriaE(RegistroDataModelImpl listaCbCategoriaE)
    {
        this.listaCbCategoriaE = listaCbCategoriaE;
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

    /**
     * Retorna la lista listaCbConcepto
     *
     * @return listaCbConcepto
     */
    public RegistroDataModelImpl getListaCbConcepto()
    {
        return listaCbConcepto;
    }

    /**
     * Asigna la lista listaCbConcepto
     *
     * @param listaCbConcepto
     * Variable a asignar en listaCbConcepto
     */
    public void setListaCbConcepto(RegistroDataModelImpl listaCbConcepto)
    {
        this.listaCbConcepto = listaCbConcepto;
    }

    // </SET_GET_LISTAS_COMBO_GRANDE>
    // <SET_GET_LISTAS_SUBFORM>
    /**
     * Retorna la lista listaSfdetalle
     *
     * @return listaSfdetalle
     */
    public RegistroDataModelImpl getListaSfdetalle()
    {
        return listaSfdetalle;
    }

    /**
     * Asigna la lista listaSfdetalle
     *
     * @param listaSfdetalle
     * Variable a asignar en listaSfdetalle
     */
    public void setListaSfdetalle(RegistroDataModelImpl listaSfdetalle)
    {
        this.listaSfdetalle = listaSfdetalle;
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

    public int getIndiceSfdetalle()
    {
        return indiceSfdetalle;
    }

    public void setIndiceSfdetalle(int indiceSfdetalle)
    {
        this.indiceSfdetalle = indiceSfdetalle;
    }

    public List<Registro> getListaCbMes()
    {
        return listaCbMes;
    }

    public void setListaCbMes(List<Registro> listaCbMes)
    {
        this.listaCbMes = listaCbMes;
    }

    // </SET_GET_ADICIONALES>
}
