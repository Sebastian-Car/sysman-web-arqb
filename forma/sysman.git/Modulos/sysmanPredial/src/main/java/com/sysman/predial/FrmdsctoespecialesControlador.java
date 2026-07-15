package com.sysman.predial;

import com.sysman.beanbase.BeanBaseContinuoAcmeImpl;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.enums.GenericUrlEnum;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.predial.ejb.EjbPredialOchoRemote;
import com.sysman.predial.enums.FrmdsctoespecialesControladorEnum;
import com.sysman.predial.enums.FrmdsctoespecialesControladorUrlEnum;
import com.sysman.recursos.ejb.EjbSysmanUtilRemote;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.util.SysmanConstantes;
import com.sysman.util.SysmanFunciones;

import java.util.Date;
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
 *
 * @author acaceres
 * @version 1, 29/07/2016
 *
 * -- Modificado por lcortes 22,23/02/2017
 * @author jcrodriguez- Depuracion del controlador y Refactoring
 * @version 2, 30/06/2016
 */
@ManagedBean
@ViewScoped
public class FrmdsctoespecialesControlador extends BeanBaseContinuoAcmeImpl
{
    private final String compania;

    /**
     * Atributo auxliar el cual es asiganado en el momento que se
     * activa la edicion de un registro. Toma el valor del indice
     * dentro de la grilla del registro seleccionado para editar
     */
    private int indice;
    private String mesPreparar;
    private String anoPreparar;
    private String periodoBase;
    /**
     * Atributo que permite identificar el codigo de predio inicial
     * seleccionado en la lista.
     */
    private String codigoInicial;
    /**
     * Atributo que permite identificar el codigo de predio final
     * seleccionado en la lista.
     */
    private String codigoFinal;
    private List<Registro> listaMES;
    private List<Registro> listaANO;
    private List<Registro> listaMesPreparar;
    private List<Registro> listaAnoPreparar;
    private List<Registro> listaPeriodoBase;
    /**
     * Atributo que permite identificar la lista de codigos de predios
     * inicial.
     */
    private RegistroDataModelImpl listaCodigoInicial;
    /**
     * Atributo que permite identificar la lista de codigos de predios
     * inicial cuando se realice una edicion en el formulario..
     */
    private RegistroDataModelImpl listaCodigoInicialE;
    /**
     * Atributo que permite identificar la lista de codigos de predios
     * final.
     */
    private RegistroDataModelImpl listaCodigoFinal;
    /**
     * Atributo que permite identificar la lista de codigos de predios
     * final cuando se realice una edicion en el formulario.
     */
    private RegistroDataModelImpl listaCodigoFinalE;
    /**
     * Atributo que permite identificar la lista de clase de predios.
     */
    private RegistroDataModelImpl listaClasePredio;
    /**
     * Atributo que permite identificar la lista de clase de predios
     * cuando se realice una edicion en el formulario.
     */
    private RegistroDataModelImpl listaClasePredioE;
    /**
     * Esta variable se usa como auxiliar para subformularios y en
     * esta se alamcena el identificador del registro que se
     * selecciono
     */
    private String auxiliar;

    /**
     * Creates a new instance of FrmdsctoespecialesControlador
     */
    @EJB
    private EjbSysmanUtilRemote ejbSysmanUtilRemote;
    @EJB
    private EjbPredialOchoRemote ejbPredialOcho;

    public FrmdsctoespecialesControlador()
    {
        super();
        compania = SessionUtil.getCompania();

        try
        {
            numFormulario = GeneralCodigoFormaEnum.FRMDSCTOESPECIALES_CONTROLADOR.getCodigo();
            validarPermisos();
        }
        catch (Exception ex)
        {
            Logger.getLogger(FrmdsctoespecialesControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            SessionUtil.redireccionarMenuPermisos();
        }
    }

    @PostConstruct
    public void inicializar()
    {

        enumBase = GenericUrlEnum.IP_DESCUENTOS_ESPECIALES;
        buscarLlave();
        reasignarOrigen();
        registro = new Registro();
        cargarListaANO();
        cargarListaAnoPreparar();
        cargarListaPeriodoBase();
        cargarListaCodigoInicial();
        cargarListaCodigoInicialE();
        cargarListaClasePredio();
        cargarListaClasePredioE();
        abrirFormulario();
        cargarListaMesPreparar();
        cargarListaMES();
    }

    @Override
    public void reasignarOrigen()
    {

        buscarUrls();
        parametrosListado.put(GeneralParameterEnum.COMPANIA.getName(), compania);
    }

    // <METODOS_CARGAR_LISTA>
    public void cargarListaMES()
    {
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        try
        {
            listaMES = RegistroConverter.toListRegistro(requestManager.getList(UrlServiceUtil.getInstance()
                            .getUrlServiceByUrlByEnumID(FrmdsctoespecialesControladorUrlEnum.URL9074.getValue()).getUrl(), param));
        }
        catch (SystemException e)
        {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    public void cargarListaANO()
    {
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        try
        {
            listaANO = RegistroConverter.toListRegistro(requestManager.getList(UrlServiceUtil.getInstance()
                            .getUrlServiceByUrlByEnumID(FrmdsctoespecialesControladorUrlEnum.URL9556.getValue()).getUrl(), param));
        }
        catch (SystemException e)
        {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    public void cargarListaMesPreparar()
    {
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        try
        {
            listaMesPreparar = RegistroConverter.toListRegistro(requestManager.getList(UrlServiceUtil.getInstance()
                            .getUrlServiceByUrlByEnumID(FrmdsctoespecialesControladorUrlEnum.URL9074.getValue()).getUrl(), param));
        }
        catch (SystemException e)
        {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    public void cargarListaAnoPreparar()
    {
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        try
        {
            listaAnoPreparar = RegistroConverter
                            .toListRegistro(requestManager.getList(
                                            UrlServiceUtil.getInstance()
                                                            .getUrlServiceByUrlByEnumID(
                                                                            FrmdsctoespecialesControladorUrlEnum.URL10471.getValue())
                                                            .getUrl(),
                                            param));
        }
        catch (SystemException e)
        {

            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    public void cargarListaPeriodoBase()
    {
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        try
        {
            listaPeriodoBase = RegistroConverter
                            .toListRegistro(requestManager.getList(
                                            UrlServiceUtil.getInstance()
                                                            .getUrlServiceByUrlByEnumID(
                                                                            FrmdsctoespecialesControladorUrlEnum.URL14222.getValue())
                                                            .getUrl(),
                                            param));
        }
        catch (SystemException e)
        {

            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    /**
     *
     * Carga la lista listaCodigoInicial
     *
     */
    public void cargarListaCodigoInicial()
    {

        UrlBean urlBean = UrlServiceUtil.getInstance().getUrlServiceByUrlByEnumID(FrmdsctoespecialesControladorUrlEnum.URL11641.getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.NUMERO_ORDEN.getName(), SysmanConstantes.NUMERO_ORDEN_PREDIAL);
        listaCodigoInicial = new RegistroDataModelImpl(urlBean.getUrl(), urlBean.getUrlConteo().getUrl(), param,
                        true, GeneralParameterEnum.CODIGO.getName());
    }

    /**
     *
     * Carga la lista listaCodigoInicial
     *
     */
    public void cargarListaCodigoInicialE()
    {

        UrlBean urlBean = UrlServiceUtil.getInstance().getUrlServiceByUrlByEnumID(FrmdsctoespecialesControladorUrlEnum.URL11641.getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.NUMERO_ORDEN.getName(), SysmanConstantes.NUMERO_ORDEN_PREDIAL);
        listaCodigoInicialE = new RegistroDataModelImpl(urlBean.getUrl(), urlBean.getUrlConteo().getUrl(), param,
                        true, GeneralParameterEnum.CODIGO.getName());
    }

    /**
     *
     * Carga la lista listaCodigoFinal
     *
     */
    public void cargarListaCodigoFinal()
    {

        UrlBean urlBean = UrlServiceUtil.getInstance().getUrlServiceByUrlByEnumID(FrmdsctoespecialesControladorUrlEnum.URL13308.getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.NUMERO_ORDEN.getName(), SysmanConstantes.NUMERO_ORDEN_PREDIAL);
        param.put(FrmdsctoespecialesControladorEnum.CODIGO_INICIAL.getValue(), codigoInicial);

        listaCodigoFinal = new RegistroDataModelImpl(urlBean.getUrl(), urlBean.getUrlConteo().getUrl(), param,
                        true, GeneralParameterEnum.CODIGO.getName());

    }

    /**
     *
     * Carga la lista listaCodigoFinal
     *
     */
    public void cargarListaCodigoFinalE()
    {
        UrlBean urlBean = UrlServiceUtil.getInstance().getUrlServiceByUrlByEnumID(FrmdsctoespecialesControladorUrlEnum.URL13308.getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.NUMERO_ORDEN.getName(), SysmanConstantes.NUMERO_ORDEN_PREDIAL);
        param.put(FrmdsctoespecialesControladorEnum.CODIGO_INICIAL.getValue(), codigoInicial);

        listaCodigoFinalE = new RegistroDataModelImpl(urlBean.getUrl(), urlBean.getUrlConteo().getUrl(), param,
                        true, GeneralParameterEnum.CODIGO.getName());
    }

    /**
     *
     * Carga la lista listaClasePredio
     *
     */
    public void cargarListaClasePredio()
    {
        UrlBean urlBean = UrlServiceUtil.getInstance().getUrlServiceByUrlByEnumID(FrmdsctoespecialesControladorUrlEnum.URL15108.getValue());
        listaClasePredio = new RegistroDataModelImpl(urlBean.getUrl(), urlBean.getUrlConteo().getUrl(), null,
                        true, GeneralParameterEnum.CODIGO.getName());
    }

    /**
     *
     * Carga la lista listaClasePredio
     *
     */
    public void cargarListaClasePredioE()
    {
        UrlBean urlBean = UrlServiceUtil.getInstance().getUrlServiceByUrlByEnumID(FrmdsctoespecialesControladorUrlEnum.URL15108.getValue());
        listaClasePredioE = new RegistroDataModelImpl(urlBean.getUrl(), urlBean.getUrlConteo().getUrl(), null,
                        true, GeneralParameterEnum.CODIGO.getName());

    }

    // </METODOS_CARGAR_LISTA>
    // <METODOS_BOTONES>
    public void oprimirPrepararPeriodo()
    {
        // <CODIGO_DESARROLLADO>
        anoPreparar = SysmanFunciones.nvlStr(anoPreparar, "");
        mesPreparar = SysmanFunciones.nvlStr(mesPreparar, "");
        periodoBase = SysmanFunciones.nvlStr(periodoBase, "");
        if (anoPreparar.isEmpty())
        {
            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB1000"));
            return;
        }
        else if (mesPreparar.isEmpty())
        {
            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB1001"));
            return;
        }
        else if (periodoBase.isEmpty())
        {
            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB1021"));
            return;
        }
        else if (!preparaPeriodo())
        {
            return;
        }
        // </CODIGO_DESARROLLADO>
    }

    private boolean preparaPeriodo()
    {

        try
        {
            int num = ejbPredialOcho.prepararPeriodo(compania,
                            SessionUtil.getUser().getCodigo(),
                            Integer.parseInt(anoPreparar),
                            Integer.parseInt(mesPreparar),
                            periodoBase,
                            SysmanConstantes.NUMERO_ORDEN_PREDIAL);
            if (num > 0)
            {
                JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB1005")
                    + " " + anoPreparar + " - "
                    + " " + mesPreparar + " "
                    + idioma.getString("TB_TB1006"));

                reasignarOrigen();
                cargarListaPeriodoBase();
                return true;
            }
            else
            {
                JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB1003"));
                return false;
            }
        }
        catch (NumberFormatException | SystemException e1)
        {
            logger.error(e1.getMessage(), e1);
            JsfUtil.agregarMensajeError(e1.getMessage());
        }

        return false;
    }

    // </METODOS_BOTONES>
    // <METODOS_CAMBIAR>
    /**
     * Metodo ejecutado al cambiar el control CodigoInicial en la fila
     * seleccionada dentro de la grilla
     *
     * @param rowNum
     * indice de la fila seleccionada
     */
    public void cambiarCodigoInicialC(int rowNum)
    {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    public void cambiarCodigoFinalC(int rowNum)
    {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    // </METODOS_CAMBIAR>
    // <METODOS_COMBOS_GRANDES>
    /**
     *
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaCodigoInicial
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaCodigoInicial(SelectEvent event)
    {
        Registro registroAux = (Registro) event.getObject();
        codigoInicial = registroAux.getCampos().get(GeneralParameterEnum.CODIGO.getName()).toString();
        registro.getCampos().put(FrmdsctoespecialesControladorEnum.CODIGO_INICIAL.getValue(), codigoInicial);
        registro.getCampos().put(FrmdsctoespecialesControladorEnum.CODIGO_FINAL.getValue(), null);
        cargarListaCodigoFinal();
    }

    /**
     *
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaCodigoInicial
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaCodigoInicialE(SelectEvent event)
    {
        Registro registroAux = (Registro) event.getObject();
        auxiliar = registroAux.getCampos().get(GeneralParameterEnum.CODIGO.getName()).toString();
        codigoInicial = auxiliar;
        registro.getCampos().put(FrmdsctoespecialesControladorEnum.CODIGO_INICIAL.getValue(), codigoInicial);
        registro.getCampos().put(FrmdsctoespecialesControladorEnum.CODIGO_FINAL.getValue(), null);
        cargarListaCodigoFinalE();
    }

    /**
     *
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaCodigoFinal
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaCodigoFinal(SelectEvent event)
    {
        Registro registroAux = (Registro) event.getObject();
        codigoFinal = registroAux.getCampos().get(GeneralParameterEnum.CODIGO.getName()).toString();
        registro.getCampos().put(FrmdsctoespecialesControladorEnum.CODIGO_FINAL.getValue(), codigoFinal);
    }

    /**
     *
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaCodigoFinal
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaCodigoFinalE(SelectEvent event)
    {
        Registro registroAux = (Registro) event.getObject();
        auxiliar = registroAux.getCampos().get(GeneralParameterEnum.CODIGO.getName()).toString();
        codigoFinal = auxiliar;
        registro.getCampos().put(FrmdsctoespecialesControladorEnum.CODIGO_FINAL.getValue(), codigoFinal);
    }

    /**
     *
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaClasePredio
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaClasePredio(SelectEvent event)
    {
        Registro registroAux = (Registro) event.getObject();
        registro.getCampos().put("CLASE_PREDIO",
                        registroAux.getCampos().get(GeneralParameterEnum.CODIGO.getName()));
    }

    /**
     *
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaClasePredio
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaClasePredioE(SelectEvent event)
    {
        Registro registroAux = (Registro) event.getObject();
        auxiliar = registroAux.getCampos().get(GeneralParameterEnum.CODIGO.getName()).toString();
    }

    // </METODOS_COMBOS_GRANDES>
    @Override
    public void abrirFormulario()
    {
        // <CODIGO_DESARROLLADO>
        anoPreparar = String.valueOf(SysmanFunciones
                        .ano(new Date()));
        mesPreparar = String.valueOf(SysmanFunciones
                        .mes(new Date()));

        // </CODIGO_DESARROLLADO>
    }

    @Override
    public void cancelarEdicion(RowEditEvent event)
    {
        registro = new Registro();
        getListaInicial().load();

    }

    public String generarConsecutivo()
    {

        long consecutivo = 0;

        StringBuilder criterio = new StringBuilder("");
        criterio.append(" COMPANIA = ''");
        criterio.append(compania);
        criterio.append("'' ");
        criterio.append(" AND ANO = ");
        criterio.append(registro.getCampos().get(GeneralParameterEnum.ANO.getName()));
        criterio.append(" AND  MES = ");
        criterio.append(registro.getCampos().get(GeneralParameterEnum.MES.getName()));
        try
        {
            consecutivo = ejbSysmanUtilRemote.generarConsecutivoConValorInicial(
                            FrmdsctoespecialesControladorEnum.IP_DESCUENTOS_ESPECIALES.getValue(),
                            criterio.toString(),
                            GeneralParameterEnum.CONSECUTIVO.getName(),
                            "1");
        }
        catch (SystemException e)
        {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

        return String.valueOf(consecutivo);

    }

    @Override
    public boolean insertarAntes()
    {
        // <CODIGO_DESARROLLADO>
        registro.getCampos().remove(GeneralParameterEnum.MODIFIED_BY.getName());
        registro.getCampos().remove(GeneralParameterEnum.DATE_MODIFIED.getName());
        registro.getCampos().remove(GeneralParameterEnum.NOMBRE.getName());
        registro.getCampos().put(GeneralParameterEnum.COMPANIA.getName(), compania);
        registro.getCampos().remove(FrmdsctoespecialesControladorEnum.NOMBREMES.getValue());
        // </CODIGO_DESARROLLADO>
        return true;
    }

    @Override
    public boolean insertarDespues()
    {
        // <CODIGO_DESARROLLADO>
        cargarListaPeriodoBase();
        /*
         * FR1015-DESPUES_INSERTAR Private Sub Form_AfterInsert()
         * Me.PeriodoBase.Requery End Sub
         */
        // </CODIGO_DESARROLLADO>
        return true;
    }

    @Override
    public boolean actualizarAntes()
    {
        // <CODIGO_DESARROLLADO>
        registro.getCampos().remove("NOMBRE");
        registro.getCampos().put("COMPANIA", compania);
        registro.getCampos().put("NUMERO_ORDENI",
                        SysmanConstantes.NUMERO_ORDEN_PREDIAL);
        registro.getCampos().put("NUMERO_ORDENF",
                        SysmanConstantes.NUMERO_ORDEN_PREDIAL);
        if (validacionActollaves())
        {
            JsfUtil.agregarMensajeError(idioma.getString("TI_MS_ERROR_VALIDACION"));
            return false;
        }
        if (!validaCampos() || !validaCampos2() || !validaCampos3())
        {
            return false;
        }

        Date fechaLimite = (Date) registro.getCampos().get("FECHA_LIMITE");
        int anoFechaLimite = SysmanFunciones.ano(fechaLimite);
        String anoS = registro.getCampos().get(GeneralParameterEnum.ANO.getName()).toString();
        int ano = Integer.parseInt(anoS);
        int mesFechaLimite = SysmanFunciones.mes(fechaLimite);
        String mesS = registro.getCampos().get(GeneralParameterEnum.MES.getName()).toString();
        int mes = Integer.parseInt(mesS);

        if ((ano != anoFechaLimite) || (mes != mesFechaLimite))
        {
            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB996"));
            return false;
        }
        registro.getCampos().remove(FrmdsctoespecialesControladorEnum.NOMBREMES.getValue());
        // </CODIGO_DESARROLLADO>
        return true;
    }

    private boolean validaCampos3()
    {
        if (registro.getCampos().get(FrmdsctoespecialesControladorEnum.CODIGO_FINAL.getValue()) == null)
        {
            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB989"));
            return false;
        }

        if (registro.getCampos().get("IND_PRIORIDAD") == null)
        {
            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB993"));
            return false;
        }

        if (registro.getCampos().get("PORC_DSCTO") == null)
        {
            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB994"));
            return false;
        }

        if (registro.getCampos().get("FECHA_LIMITE") == null)
        {
            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB995"));
            return false;
        }
        return true;
    }

    private boolean validaCampos2()
    {
        if (registro.getCampos().get("INCREM_INICIAL") == null)
        {
            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB990"));
            return false;
        }

        if (registro.getCampos().get("INCREM_FINAL") == null)
        {
            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB991"));
            return false;
        }

        if (registro.getCampos().get("AREA_CONST_ANT") == null)
        {
            registro.getCampos().put("AREA_CONST_ANT", "NA");
        }

        if (registro.getCampos().get("AREA_CONST_ACTUAL") == null)
        {
            registro.getCampos().put("AREA_CONST_ACTUAL", "NA");
        }
        return true;
    }

    private boolean validaCampos()
    {
        if (registro.getCampos().get(GeneralParameterEnum.ANO.getName()) == null)
        {
            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB986"));
            return false;
        }

        if (registro.getCampos().get(GeneralParameterEnum.MES.getName()) == null)
        {
            JsfUtil.agregarMensajeAlerta(
                            idioma.getString(idioma.getString("TB_TB987")));
            return false;
        }

        if (registro.getCampos().get(GeneralParameterEnum.CONSECUTIVO.getName()) == null)
        {
            registro.getCampos().put(GeneralParameterEnum.CONSECUTIVO.getName(), generarConsecutivo());
        }

        if (registro.getCampos().get(FrmdsctoespecialesControladorEnum.CODIGO_INICIAL.getValue()) == null)
        {
            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB988"));
            return false;
        }
        return true;
    }

    private boolean validacionActollaves()
    {
        if (SysmanFunciones.validarCampoVacio(registro.getCampos(), GeneralParameterEnum.ANO.getName())
            || SysmanFunciones.validarCampoVacio(registro.getCampos(), GeneralParameterEnum.MES.getName()))
        {
            return true;
        }
        return false;
    }

    /**
     * Metodo ejecutado al cambiar el control AnoPreparar
     * 
     */
    public void cambiarAnoPreparar()
    {
        cargarListaMesPreparar();
    }

    /**
     * Metodo ejecutado al cambiar el control ANO
     * 
     * 
     */
    public void cambiarANO()
    {
        cargarListaMES();
    }

    /**
     * Metodo ejecutado al cambiar el control ANO en la fila
     * seleccionada dentro de la grilla
     * 
     * @param rowNum
     * indice de la fila seleccionada
     */
    public void cambiarANOC(int rowNum)
    {
        registro.getCampos().putAll(listaInicial.getDatasource()
                        .get(rowNum % 10).getCampos());
        cargarListaMES();
    }

    @Override
    public boolean actualizarDespues()
    {
        // <CODIGO_DESARROLLADO>
        cargarListaPeriodoBase();
        // </CODIGO_DESARROLLADO>
        return true;
    }

    @Override
    public boolean eliminarAntes()
    {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
        return true;
    }

    @Override
    public boolean eliminarDespues()
    {
        // <CODIGO_DESARROLLADO>
        cargarListaPeriodoBase();
        // </CODIGO_DESARROLLADO>
        return true;
    }

    @Override
    public void removerCombos()
    {
        registro.getCampos().remove(GeneralParameterEnum.CREATED_BY.getName());
        registro.getCampos().remove(GeneralParameterEnum.DATE_CREATED.getName());
    }

    /**
     * Metodo ejecutado cuando se activa la edicion de un registro del
     * formulario
     *
     * @param registro
     * registro del cual se activo la edicion
     */
    public void activarEdicion(Registro registro)
    {
        indice = listaInicial.getRowIndex();
        codigoInicial = registro.getCampos().get(FrmdsctoespecialesControladorEnum.CODIGO_INICIAL.getValue()).toString();
        cargarListaCodigoInicialE();
        cargarListaCodigoFinalE();

    }

    public void ejecutarrcCerrar()
    {
        // <CODIGO_DESARROLLADO>
        SessionUtil.redireccionar("/decuentoano.sysman");
        // </CODIGO_DESARROLLADO>
    }

    @Override
    public void asignarValoresRegistro()
    {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    // <SET_GET_ATRIBUTOS>
    /**
     * Retorna la variable indice
     *
     * @return indice
     */
    public int getIndice()
    {
        return indice;
    }

    public void setIndice(int indice)
    {
        this.indice = indice;
    }

    public String getMesPreparar()
    {
        return mesPreparar;
    }

    public void setMesPreparar(String mesPreparar)
    {
        this.mesPreparar = mesPreparar;
    }

    public String getAnoPreparar()
    {
        return anoPreparar;
    }

    public void setAnoPreparar(String anoPreparar)
    {
        this.anoPreparar = anoPreparar;
    }

    public String getPeriodoBase()
    {
        return periodoBase;
    }

    public void setPeriodoBase(String periodoBase)
    {
        this.periodoBase = periodoBase;
    }

    /**
     * Retorna la variable codigoInicial
     *
     * @return codigoInicial
     */
    public String getCodigoInicial()
    {
        return codigoInicial;
    }

    /**
     * Asigna la variable codigoInicial
     *
     * @param codigoInicial
     * Variable a asignar en codigoInicial
     */
    public void setCodigoInicial(String codigoInicial)
    {
        this.codigoInicial = codigoInicial;
    }

    /**
     * Retorna la variable codigoFinal
     *
     * @return codigoFinal
     */
    public String getCodigoFinal()
    {
        return codigoFinal;
    }

    /**
     * Asigna la variable codigoFinal
     *
     * @param codigoFinal
     * Variable a asignar en codigoFinal
     */
    public void setCodigoFinal(String codigoFinal)
    {
        this.codigoFinal = codigoFinal;
    }

    public List<Registro> getListaMES()
    {
        return listaMES;
    }

    public void setListaMES(List<Registro> listaMES)
    {
        this.listaMES = listaMES;
    }

    public List<Registro> getListaANO()
    {
        return listaANO;
    }

    public void setListaANO(List<Registro> listaANO)
    {
        this.listaANO = listaANO;
    }

    public List<Registro> getListaMesPreparar()
    {
        return listaMesPreparar;
    }

    public void setListaMesPreparar(List<Registro> listaMesPreparar)
    {
        this.listaMesPreparar = listaMesPreparar;
    }

    public List<Registro> getListaAnoPreparar()
    {
        return listaAnoPreparar;
    }

    public void setListaAnoPreparar(List<Registro> listaAnoPreparar)
    {
        this.listaAnoPreparar = listaAnoPreparar;
    }

    public List<Registro> getListaPeriodoBase()
    {
        return listaPeriodoBase;
    }

    public void setListaPeriodoBase(List<Registro> listaPeriodoBase)
    {
        this.listaPeriodoBase = listaPeriodoBase;
    }

    /**
     * Retorna la lista listaCodigoInicial
     *
     * @return listaCodigoInicial
     */
    public RegistroDataModelImpl getListaCodigoInicial()
    {
        return listaCodigoInicial;
    }

    /**
     * Asigna la lista listaCodigoInicial
     *
     * @param listaCodigoInicial
     * Variable a asignar en listaCodigoInicial
     */
    public void setListaCodigoInicial(RegistroDataModelImpl listaCodigoInicial)
    {
        this.listaCodigoInicial = listaCodigoInicial;
    }

    /**
     * Retorna la lista listaCodigoInicial
     *
     * @return listaCodigoInicial
     */
    public RegistroDataModelImpl getListaCodigoInicialE()
    {
        return listaCodigoInicialE;
    }

    /**
     * Asigna la lista listaCodigoInicial
     *
     * @param listaCodigoInicial
     * Variable a asignar en listaCodigoInicial
     */
    public void setListaCodigoInicialE(RegistroDataModelImpl listaCodigoInicialE)
    {
        this.listaCodigoInicialE = listaCodigoInicialE;
    }

    /**
     * Retorna la lista listaCodigoFinal
     *
     * @return listaCodigoFinal
     */
    public RegistroDataModelImpl getListaCodigoFinal()
    {
        return listaCodigoFinal;
    }

    /**
     * Asigna la lista listaCodigoFinal
     *
     * @param listaCodigoFinal
     * Variable a asignar en listaCodigoFinal
     */
    public void setListaCodigoFinal(RegistroDataModelImpl listaCodigoFinal)
    {
        this.listaCodigoFinal = listaCodigoFinal;
    }

    /**
     * Retorna la lista listaCodigoFinal
     *
     * @return listaCodigoFinal
     */
    public RegistroDataModelImpl getListaCodigoFinalE()
    {
        return listaCodigoFinalE;
    }

    /**
     * Asigna la lista listaCodigoFinal
     *
     * @param listaCodigoFinal
     * Variable a asignar en listaCodigoFinal
     */
    public void setListaCodigoFinalE(RegistroDataModelImpl listaCodigoFinalE)
    {
        this.listaCodigoFinalE = listaCodigoFinalE;
    }

    /**
     * Retorna la lista listaClasePredio
     *
     * @return listaClasePredio
     */
    public RegistroDataModelImpl getListaClasePredio()
    {
        return listaClasePredio;
    }

    /**
     * Asigna la lista listaClasePredio
     *
     * @param listaClasePredio
     * Variable a asignar en listaClasePredio
     */
    public void setListaClasePredio(RegistroDataModelImpl listaClasePredio)
    {
        this.listaClasePredio = listaClasePredio;
    }

    /**
     * Retorna la lista listaClasePredio
     *
     * @return listaClasePredio
     */
    public RegistroDataModelImpl getListaClasePredioE()
    {
        return listaClasePredioE;
    }

    /**
     * Asigna la lista listaClasePredio
     *
     * @param listaClasePredio
     * Variable a asignar en listaClasePredio
     */
    public void setListaClasePredioE(RegistroDataModelImpl listaClasePredioE)
    {
        this.listaClasePredioE = listaClasePredioE;
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
}
