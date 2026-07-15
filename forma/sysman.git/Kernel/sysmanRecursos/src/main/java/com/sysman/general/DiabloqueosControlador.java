/*-
 * DiabloqueosControlador.java
 *
 * 1.0
 *
 * 09/05/2017
 *
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */
package com.sysman.general;

import com.sysman.beanbase.BeanBaseContinuoAcmeImpl;
import com.sysman.componentes.Direccionador;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.enums.GenericUrlEnum;
import com.sysman.exception.SystemException;
import com.sysman.general.enums.DiabloqueosControladorEnum;
import com.sysman.general.enums.DiabloqueosControladorUrlEnum;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.recursos.ejb.EjbSysmanUtilRemote;
import com.sysman.util.SysmanConstantes;
import com.sysman.util.SysmanFunciones;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.context.RequestContext;
import org.primefaces.event.RowEditEvent;

/**
 * Formulario por el cual se puede acceder desde las siguientes
 * opciones
 *
 * Almacen\Archivos\Períodos
 *
 * Contabilidad\Archivos\Períodos y paag Panel
 *
 * Principal\Presupuesto\Archivos\Periodos y Paag
 *
 * @version 1.0, 09/05/2017
 * @author ybecerra
 *
 * -- Modificado por lcortes 12/06/2017. Se reemplaza el valor de la
 * variable numero de formulario por el enumerado correspondiente y se
 * reemplaza el metodo redireccionar por el metodo redireccionarForma
 * para manejar el numero de formulario con el enumerado
 * correspondiente.
 */
@ManagedBean
@ViewScoped

public class DiabloqueosControlador extends BeanBaseContinuoAcmeImpl
{
    /**
     * Constante a nivel de clase que almacena el codigo de la
     * compania en la cual inicio sesion el usuario, el valor de esta
     * constante es asignado en el constructor a la variable de sesion
     * correspondiente
     */
    private final String compania;
    /**
     * Constante definida para almacenar el codigo del modulo por la
     * cual se ingreso a la aplicacion
     */
    private final String modulo;
    // <DECLARAR_ATRIBUTOS>
    /**
     * Atributo que almacena el numero de ano seleccionado en el combo
     * ano del formulario
     */
    private String anoFiltro;
    /**
     * Atributo que almacena el numero de mes seleccionado en el combo
     * mes del formulario
     */
    private String mesFiltro;
    /**
     * Atributo que almacena el valor que retorna el llamado del ejb.
     */
    private String estadoAno;

    /**
     * Atributo que almacena el valor que retona el llamado del ejb.
     */
    private String estadoMes;

    /**
     * Atributo que almacena el codigo seleccionado en el combo
     * proceso del formulario
     */
    private String procesoFiltro;

    /**
     * Atributo que cambiara el texto del dialogo dependiendo el cambo
     * seleccionado
     */
    private String mensajeCambiarEstado;
    /**
     * Atributo que valida si el dialogo se hace visible o no
     */
    private boolean cambiarEstadoAnoMes;
    /**
     * Atributo que valida cual funcion se debe ejcutar si por ano o
     * por mes
     */
    String estadoActual;

    // </DECLARAR_ATRIBUTOS>
    // <DECLARAR_PARAMETROS>
    // </DECLARAR_PARAMETROS>
    // <DECLARAR_LISTAS>
    /**
     * Lista que contiene los detalles del combo ano
     */
    private List<Registro> listaAnoFiltro;
    /**
     * Lista de contiene los detalles del combo mes
     */
    private List<Registro> listaMesFiltro;

    /**
     * Lista que contiene los detalles del proceso
     */
    private List<Registro> listaprocesoFiltro;

    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>
    // </DECLARAR_LISTAS_COMBO_GRANDE>

    @EJB
    private EjbSysmanUtilRemote ejbSysmanUtl;

    /**
     * Crea una nueva instancia de DiabloqueosControlador
     */
    public DiabloqueosControlador()
    {
        super();
        compania = SessionUtil.getCompania();
        modulo = SessionUtil.getModulo();
        try
        {
            numFormulario = GeneralCodigoFormaEnum.DIABLOQUEOS_CONTROLADOR
                            .getCodigo();
            validarPermisos();
            // <INI_ADICIONAL>
            // </INI_ADICIONAL>
        }
        catch (Exception ex)
        {
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
    public void inicializar()
    {

        enumBase = GenericUrlEnum.DIA_BLOQUEO;
        buscarLlave();

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
    public void reasignarOrigen()
    {
        buscarUrls();
        obtenerParametros();

    }

    // <METODOS_CARGAR_LISTA>
    /**
     *
     * Carga la lista listaAnoFiltro
     *
     */
    public void cargarListaAnoFiltro()
    {

        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);

        try
        {
            listaAnoFiltro = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            DiabloqueosControladorUrlEnum.URL5180
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
     * Carga la lista listaMesFiltro
     *
     */
    public void cargarListaMesFiltro()
    {

        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
        param.put(GeneralParameterEnum.ANO.getName(),
                        anoFiltro);

        try
        {
            listaMesFiltro = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            DiabloqueosControladorUrlEnum.URL202
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
     * Carga la lista listaprocesoFiltro
     *
     */
    public void cargarListaprocesoFiltro()
    {

        Map<String, Object> param = new TreeMap<>();
        param.put(DiabloqueosControladorEnum.PARAM0.getValue(),
                        modulo);

        try
        {
            listaprocesoFiltro = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            DiabloqueosControladorUrlEnum.URL248
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e)
        {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    // </METODOS_CARGAR_LISTA>
    // <METODOS_BOTONES>
    /**
     *
     * Metodo ejecutado al oprimir el boton AnoEtiqueta en la vista
     *
     *
     */
    public void oprimirAnoEtiqueta()
    {
        // <CODIGO_DESARROLLADO>
        Direccionador direccionador = new Direccionador();
        direccionador.setNumForm(Integer
                        .toString(GeneralCodigoFormaEnum.ANOSPERIODOS_CONTROLADOR
                                        .getCodigo()));
        SessionUtil.redireccionarDeModalAModal(direccionador, modulo);
        // </CODIGO_DESARROLLADO>
    }

    // </METODOS_BOTONES>
    // <METODOS_CAMBIAR>
    /**
     * Metodo ejecutado al cambiar el control AnoFiltro
     *
     *
     */
    public void cambiarAnoFiltro()
    {

        mesFiltro = estadoAno = estadoMes = procesoFiltro = null;
        obtenerParametros();
        cargarListaMesFiltro();
        insertaMeses();

    }

    /**
     * Metodo ejecutado al cambiar el control MesFiltro
     *
     *
     */
    public void cambiarMesFiltro()
    {
        // <CODIGO_DESARROLLADO>
        estadoAno = estadoMes = procesoFiltro = null;
        obtenerParametros();
        insertaDias();
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado al cambiar el control procesoFiltro
     *
     *
     */
    public void cambiarprocesoFiltro()
    {

        reasignarOrigen();

        estadoAno = verificarEstadoAno();
        estadoMes = verificarEstadoMes();

    }

    /**
     *
     * Metodo ejecutado al cambiar el control estadoAno
     *
     *
     */
    public void cambiarestadoAno()
    {

        if (validarCombos())
        {
            estadoActual = "A";
            mensajeCambiarEstado = idioma.getString("TB_TB3136")
                            .replace("s$periodo$s", anoFiltro);

            cambiarEstadoAnoMes = true; // aceptarcambiarEstado

        }
        else
        {
            estadoAno = null;
            estadoMes = null;
            return;
        }

    }

    /**
     * Metodo ejecutado al cambiar el control estadoMes
     *
     *
     */
    public void cambiarestadoMes()
    {
        if (validarCombos())
        {
            estadoActual = "M";
            mensajeCambiarEstado = idioma.getString("TB_TB3137")
                            .replace("s$periodo$s",
                                            SysmanConstantes.NOMBRE_MESES_CONTABILIDAD[Integer
                                                            .valueOf(mesFiltro)]);

            cambiarEstadoAnoMes = true; // aceptarcambiarEstado
        }
        else
        {
            estadoAno = null;
            estadoMes = null;
            return;
        }

    }

    /**
     * Metodo ejecutado al cambiar el control estado
     *
     *
     */
    public void cambiarestadoC(int rowNun)
    {
        if (listaInicial.getDatasource().get(rowNun % 10).getCampos()
                        .get("ESTADO") == null)
        {
            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB3141"));
            return;
        }

    }

    /**
     * Al cambiar los estados de ano y mes, se ejecuta este metodo
     * para validar que el ano mes y proceso no esten vacios
     *
     * @return
     */
    private boolean validarCombos()
    {
        if (anoFiltro == null)
        {
            JsfUtil.agregarMensajeInformativo(idioma.getString("MSM_DEBE_ANO"));
            return false;

        }
        else if (mesFiltro == null)
        {
            JsfUtil.agregarMensajeInformativo(idioma.getString("MSM_DEBE_MES"));
            return false;
        }
        else if (procesoFiltro == null)
        {
            JsfUtil.agregarMensajeInformativo(idioma.getString("TB_TB3153"));
            return false;
        }
        else
        {
            return true;
        }

    }

    // </METODOS_CAMBIAR>
    // <METODOS_COMBOS_GRANDES>
    // </METODOS_COMBOS_GRANDES>
    /**
     * Este metodo es invocado el metodo inicializar, se ejecutan las
     * acciones a tener en cuenta en el momento de apertura del
     * formulario
     */
    @Override
    public void abrirFormulario()
    {
        // <CODIGO_DESARROLLADO>
        anoFiltro = String.valueOf(SysmanFunciones.ano(new Date()));
        mesFiltro = String.valueOf(SysmanFunciones.mes(new Date()));
        procesoFiltro = "1";
        cargarListaAnoFiltro();
        cargarListaMesFiltro();
        cargarListaprocesoFiltro();
        insertaMeses();
        insertaDias();
        reasignarOrigen();
        estadoAno = verificarEstadoAno();
        estadoMes = verificarEstadoMes();
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo que toma el valor que retorna la funcion
     * verificarEstadoPeriodoAnual
     *
     * @return estadoA
     */
    private String verificarEstadoAno()
    {
        String estadoA = "";
        try
        {
            /* estadoA = ejbSysmanUtl.verificarEstadoPeriodoAnual(compania,
                            Integer.parseInt(anoFiltro),
                            Integer.parseInt(modulo),
                            Integer.parseInt(procesoFiltro)); */
        	
        	//fix Jm 09-04-2025
        	
        	estadoA = ejbSysmanUtl.verificarEstadoPeriodoAnual(compania,
                    anoFiltro,
                    modulo,
                    procesoFiltro);
        }
        catch (NumberFormatException | SystemException e)
        {

            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
        return estadoA;
    }

    /**
     *
     * Metodo que toma el valor que retorna la funcion
     * verificarEstadoPeriodoMensual
     *
     * @return estadoM
     */
    private String verificarEstadoMes()
    {
        String estadoM = "";
        try
        {
            estadoM = ejbSysmanUtl.verificarEstadoPeriodoMensual(compania,
                            Integer.parseInt(anoFiltro),
                            Integer.parseInt(mesFiltro),
                            Integer.parseInt(modulo),
                            Integer.parseInt(procesoFiltro));
        }
        catch (NumberFormatException | SystemException e)
        {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
        return estadoM;
    }

    /**
     * Metodo ejecutado al cambiar el ano, inserta los meses a la
     * tabla mes, si no existen para el ano seleccionado
     */
    private void insertaMeses()
    {

        try
        {
            ejbSysmanUtl.insertarMesesdelAno(compania,
                            Integer.parseInt(anoFiltro),
                            SessionUtil.getUser().getCodigo());
        }
        catch (NumberFormatException | SystemException e)
        {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    /**
     * Metodo ejecutado al cambiar el mes, inserta lso dias en la
     * tabla dia y dia_bloqueo si no existen datos con el mes
     * seleccionado
     */
    private void insertaDias()
    {

        try
        {
            ejbSysmanUtl.insertarDiasdelMes(compania,
                            Integer.parseInt(anoFiltro),
                            Integer.parseInt(mesFiltro),
                            SessionUtil.getUser().getCodigo());
        }
        catch (Exception e)
        {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    /**
     * Metodo ejecutado en el reasignarOrigen cambiarAno y cambiarMes
     */
    private void obtenerParametros()
    {
        parametrosListado.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
        parametrosListado.put(GeneralParameterEnum.ANO.getName(), anoFiltro);
        parametrosListado.put(GeneralParameterEnum.MES.getName(), mesFiltro);
        parametrosListado.put(DiabloqueosControladorEnum.PARAM0.getValue(),
                        modulo);
        parametrosListado.put(DiabloqueosControladorEnum.PARAM2.getValue(),
                        procesoFiltro);
    }

    /**
     * Metodo llamado al ejecutar el evento de cambiarAno o cambiarMes
     */
    public void aceptarcambiarEstado()
    {
        try
        {
            if ("A".equals(estadoActual))
            {

                ejbSysmanUtl.cambiarEstadoAno(compania,
                                Integer.parseInt(anoFiltro),
                                Integer.parseInt(modulo),
                                Integer.parseInt(procesoFiltro), estadoAno,
                                SessionUtil.getUser().getCodigo());

            }
            else
            {
                ejbSysmanUtl.cambiarEstadoMes(compania,
                                Integer.parseInt(anoFiltro),
                                Integer.parseInt(mesFiltro),
                                Integer.parseInt(modulo),
                                Integer.parseInt(procesoFiltro), estadoMes,
                                SessionUtil.getUser().getCodigo());
            }
        }
        catch (NumberFormatException | SystemException e)
        {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
        estadoAno = verificarEstadoAno();
        estadoMes = verificarEstadoMes();
        reasignarOrigen();
        cambiarEstadoAnoMes = false;

    }

    /**
     * Metodo llamado al ejecutar el evento de cambiarAno o cambiarMes
     */
    public void cancelarcambiarEstado()
    {
        cambiarEstadoAnoMes = false;
        estadoAno = verificarEstadoAno();
        estadoMes = verificarEstadoMes();
    }

    public void cerrarFormulario()
    {
        RequestContext.getCurrentInstance().closeDialog(null);
    }

    /**
     * Metodo ejecutado cuando se cancela la edicion del registro
     * seleccionado
     */
    @Override
    public void cancelarEdicion(RowEditEvent event)
    {
        getListaInicial().load();
    }

    /**
     * Metodo ejecutado antes de realizar la insercion del registro
     *
     * @return true
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
     * @return true
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
     *
     * @return true
     */
    @Override
    public boolean actualizarAntes()
    {
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
    public boolean actualizarDespues()
    {
        // <CODIGO_DESARROLLADO>
        estadoAno = verificarEstadoAno();
        estadoMes = verificarEstadoMes();
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
    public boolean eliminarAntes()
    {
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
    public boolean eliminarDespues()
    {
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
    public void removerCombos()
    {
        // <CODIGO_DESARROLLADO>
        registro.getCampos().remove(GeneralParameterEnum.COMPANIA.getName());
        registro.getCampos().remove(GeneralParameterEnum.ANO.getName());
        registro.getCampos().remove(GeneralParameterEnum.MES.getName());
        registro.getCampos()
                        .remove(DiabloqueosControladorEnum.PARAM1.getValue());
        registro.getCampos()
                        .remove(DiabloqueosControladorEnum.PARAM0.getValue());
        registro.getCampos()
                        .remove(DiabloqueosControladorEnum.PARAM2.getValue());
        registro.getCampos()
                        .remove(DiabloqueosControladorEnum.PARAM3.getValue());
        registro.getCampos()
                        .remove(DiabloqueosControladorEnum.PARAM4.getValue());

        // </CODIGO_DESARROLLADO>
    }

    /**
     * Este metodo es ejecutado despues de finalizar la insercion y
     * edicion del registro se usa cuando se desean agregar valores al
     * registro despues de dichas acciones
     */
    @Override
    public void asignarValoresRegistro()
    {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    // <SET_GET_ATRIBUTOS>
    /**
     * Retorna el numero de ano
     *
     * @return anoFiltro
     */
    public String getAnoFiltro()
    {
        return anoFiltro;
    }

    /**
     * Asigna el numero de ano
     *
     * @param anoFiltro
     */
    public void setAnoFiltro(String anoFiltro)
    {
        this.anoFiltro = anoFiltro;
    }

    /**
     * Retorna el numero de mes
     *
     * @return mesFiltro
     */
    public String getMesFiltro()
    {
        return mesFiltro;
    }

    /**
     * Asigna el numero de mes
     *
     * @param mesFiltro
     */
    public void setMesFiltro(String mesFiltro)
    {
        this.mesFiltro = mesFiltro;
    }

    /**
     * Retorna el estado del ano
     *
     * @return estadoAno
     */
    public String getEstadoAno()
    {
        return estadoAno;
    }

    /**
     * Asigna el estado del ano
     *
     * @param estadoAno
     */
    public void setEstadoAno(String estadoAno)
    {
        this.estadoAno = estadoAno;
    }

    /**
     * Retorna el estado del mes
     *
     * @return estadoMes
     */
    public String getEstadoMes()
    {
        return estadoMes;
    }

    /**
     * Asigna el estado del mes
     *
     * @param estadoMes
     */
    public void setEstadoMes(String estadoMes)
    {
        this.estadoMes = estadoMes;
    }

    /**
     * retorna el codigo el proceso
     *
     * @return procesoFiltro
     */
    public String getProcesoFiltro()
    {
        return procesoFiltro;
    }

    /**
     * Asigna el codigo del proceso
     *
     * @param procesoFiltro
     */
    public void setProcesoFiltro(String procesoFiltro)
    {
        this.procesoFiltro = procesoFiltro;
    }

    /**
     * Retorna el mensaje del dialogo
     *
     * @return mensajeCambiarEstado
     */
    public String getMensajeCambiarEstado()
    {
        return mensajeCambiarEstado;
    }

    /**
     * Asigna el mensaje del dialogo
     *
     * @param mensajeCambiarEstado
     */
    public void setMensajeCambiarEstado(String mensajeCambiarEstado)
    {
        this.mensajeCambiarEstado = mensajeCambiarEstado;
    }

    /**
     * Retorna el true o false
     *
     * @return cambiarEstadoAnoMes
     */
    public boolean isCambiarEstadoAnoMes()
    {
        return cambiarEstadoAnoMes;
    }

    /**
     * Asigna cambiarEstadoAnoMes
     *
     * @param cambiarEstadoAnoMes
     */
    public void setCambiarEstadoAnoMes(boolean cambiarEstadoAnoMes)
    {
        this.cambiarEstadoAnoMes = cambiarEstadoAnoMes;
    }

    // </SET_GET_ATRIBUTOS>
    // <SET_GET_PARAMETROS>
    // </SET_GET_PARAMETROS>
    // <SET_GET_LISTAS>
    /**
     * Retorna la lista listaAnoFiltro
     *
     * @return listaAnoFiltro
     */
    public List<Registro> getListaAnoFiltro()
    {
        return listaAnoFiltro;
    }

    /**
     * Asigna la lista listaAnoFiltro
     *
     * @param listaAnoFiltro
     * Variable a asignar en listaAnoFiltro
     */
    public void setListaAnoFiltro(List<Registro> listaAnoFiltro)
    {
        this.listaAnoFiltro = listaAnoFiltro;
    }

    /**
     * Retorna la lista listaMesFiltro
     *
     * @return listaMesFiltro
     */
    public List<Registro> getListaMesFiltro()
    {
        return listaMesFiltro;
    }

    /**
     * Asigna la lista listaMesFiltro
     *
     * @param listaMesFiltro
     * Variable a asignar en listaMesFiltro
     */
    public void setListaMesFiltro(List<Registro> listaMesFiltro)
    {
        this.listaMesFiltro = listaMesFiltro;
    }

    /**
     * Retorna la lista listaprocesoFiltro
     *
     * @return listaprocesoFiltro
     */
    public List<Registro> getListaprocesoFiltro()
    {
        return listaprocesoFiltro;
    }

    /**
     * Asigna la lista listaprocesoFiltro
     *
     * @param listaprocesoFiltro
     * Variable a asignar en listaprocesoFiltro
     */
    public void setListaprocesoFiltro(List<Registro> listaprocesoFiltro)
    {
        this.listaprocesoFiltro = listaprocesoFiltro;
    }
    // </SET_GET_LISTAS>
    // <SET_GET_LISTAS_COMBO_GRANDE>
    // </SET_GET_LISTAS_COMBO_GRANDE>
}
