package com.sysman.contabilidad;

import com.sysman.beanbase.BeanBaseContinuoAcmeImpl;
import com.sysman.contabilidad.enums.ChequesAnuladosControladorEnum;
import com.sysman.contabilidad.enums.ChequesAnuladosControladorUrlEnum;
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
import com.sysman.recursos.ejb.EjbSysmanUtilRemote;
import com.sysman.services.RegistroDataModelImpl;
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

import org.primefaces.context.RequestContext;
import org.primefaces.event.RowEditEvent;
import org.primefaces.event.SelectEvent;

/**
 *
 * @author otorres
 * @version 1, 07/03/2016
 *
 * Revision Sonar y Refactoring
 * @author ybecerra
 * @version 2, 07/04/2017
 */
@ManagedBean
@ViewScoped

public class ChequesAnuladosControlador extends BeanBaseContinuoAcmeImpl {

    private final String compania;
    /**
     * Constante definida para almacenar el valor de
     * GeneralParameterEnum.CUENTA.getName();
     */
    private final String cCuenta;
    /**
     * Constante definida para almacenar el valor de
     * ChequesAnuladosControladorEnum.PARAM0.getValue();
     */
    private final String cNumAnular;
    /**
     * Constante definida para almacenar el valor de
     * ChequesAnuladosControladorEnum.PARAM1.getValue();
     */
    private final String cNumChequera;
    // <DECLARAR_ATRIBUTOS>
    private String ano;
    // </DECLARAR_ATRIBUTOS>
    // <DECLARAR_PARAMETROS>
    // </DECLARAR_PARAMETROS>
    // <DECLARAR_LISTAS>
    private List<Registro> listaano;
    private List<Registro> listacuenta;
    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>
    private RegistroDataModelImpl listaNumChequera;
    private RegistroDataModelImpl listaNumChequeraE;
    private String auxiliar;
    // </DECLARAR_LISTAS_COMBO_GRANDE>

    private String cuenta;
    private int chequeraIni;
    private int chequeraFin;
    @EJB
    private EjbSysmanUtilRemote ejbContabilidadCero;

    /**
     * Creates a new instance of ChequesAnuladosControlador
     */
    public ChequesAnuladosControlador() {
        super();
        compania = SessionUtil.getCompania();
        cCuenta = GeneralParameterEnum.CUENTA.getName();
        cNumAnular = ChequesAnuladosControladorEnum.PARAM0.getValue();
        cNumChequera = ChequesAnuladosControladorEnum.PARAM1.getValue();

        try {
            numFormulario = GeneralCodigoFormaEnum.CHEQUES_ANULADOS_CONTROLADOR
                            .getCodigo();
            validarPermisos();
            // <INI_ADICIONAL>
            // </INI_ADICIONAL>
        }
        catch (Exception ex) {
            Logger.getLogger(ChequesAnuladosControlador.class.getName())
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
    public void inicializar() {
        enumBase = GenericUrlEnum.CHEQUES_ANULADOS;
        buscarLlave();

        Map<String, Object> parametrosEntrada = SessionUtil.getFlash();
        if (parametrosEntrada != null) {
            cuenta = (String) parametrosEntrada.get(cCuenta);
        }
        SessionUtil.cleanFlash();
        abrirFormulario();
        reasignarOrigen();
        registro = new Registro();
        // <CARGAR_LISTA>

        cargarListaano();
        cargarListacuenta();
        // </CARGAR_LISTA>
        // <CARGAR_LISTA_COMBO_GRANDE>
        cargarListaNumChequera();
        cargarListaNumChequeraE();
        // </CARGAR_LISTA_COMBO_GRANDE>

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
        parametrosListado.put(GeneralParameterEnum.ANO.getName(), ano);
    }

    // <METODOS_CARGAR_LISTA>
    /**
     *
     * Carga la lista listaano
     *
     */
    public void cargarListaano() {
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        try {
            listaano = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            ChequesAnuladosControladorUrlEnum.URL5478
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    /**
     *
     * Carga la lista listaNumChequera
     *
     */
    public void cargarListaNumChequera() {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        ChequesAnuladosControladorUrlEnum.URL5858
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.ANO.getName(), ano);
        param.put(GeneralParameterEnum.CUENTA.getName(), cuenta);

        listaNumChequera = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, cNumChequera);

    }

    /**
     *
     * Carga la lista listaNumChequera
     *
     */
    public void cargarListaNumChequeraE() {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        ChequesAnuladosControladorUrlEnum.URL6561
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.ANO.getName(), ano);
        param.put(GeneralParameterEnum.CUENTA.getName(), cuenta);

        listaNumChequeraE = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, cNumChequera);
    }

    /**
     *
     * Carga la lista listacuenta
     *
     */
    public void cargarListacuenta() {
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.ANO.getName(), ano);

        try {
            listacuenta = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            ChequesAnuladosControladorUrlEnum.URL7157
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }
    // </METODOS_CARGAR_LISTA>
    // <METODOS_BOTONES>
    // </METODOS_BOTONES>
    // <METODOS_CAMBIAR>

    public void cambiarano() {
        // <CODIGO_DESARROLLADO>
        if (ano != null) {
            reasignarOrigen();
            cargarListaNumChequera();
            cargarListaNumChequeraE();
            cargarListacuenta();
        }
        // </CODIGO_DESARROLLADO>
    }

    public void cambiarcuenta() {
        // <CODIGO_DESARROLLADO>
        cuenta = registro.getCampos().get(cCuenta).toString();
        registro.getCampos().remove(cNumChequera);
        cargarListaNumChequera();

        // </CODIGO_DESARROLLADO>
    }

    // </METODOS_CAMBIAR>
    // <METODOS_COMBOS_GRANDES>
    public void seleccionarFilaNumChequera(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        registro.getCampos().put(cNumChequera,
                        registroAux.getCampos().get(cNumChequera));
        chequeraIni = Integer.parseInt(
                        registroAux.getCampos().get("NUMINICIAL") == null ? "0"
                            : registroAux.getCampos().get("NUMINICIAL")
                                            .toString());
        chequeraFin = Integer.parseInt(
                        registroAux.getCampos().get("NUMFINAL") == null ? "0"
                            : registroAux.getCampos().get("NUMFINAL")
                                            .toString());
    }

    public void seleccionarFilaNumChequeraE(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        auxiliar = registroAux.getCampos().get(cNumChequera).toString();
    }

    // </METODOS_COMBOS_GRANDES>
    @Override
    public void abrirFormulario() {
        // <CODIGO_DESARROLLADO>
        ano = String.valueOf(SysmanFunciones
                        .ano(new Date()));
        /*
         * FR559-AL_ABRIR Private Sub Form_Open(Cancel As Integer)
         * formularioAbrir 3, Me.Name Me.AllowDeletions = False
         * Me.AllowEdits = False DoCmd.Restore Me.Requery End Sub
         */
        // </CODIGO_DESARROLLADO>
    }

    @Override
    public void cancelarEdicion(RowEditEvent event) {
        getListaInicial().load();
    }

    @Override
    public boolean insertarAntes() {
        // <CODIGO_DESARROLLADO>

        if ((Integer.parseInt(registro.getCampos().get(cNumAnular)
                        .toString()) < chequeraIni)
            || (Integer.parseInt(registro.getCampos().get(cNumAnular)
                            .toString()) > chequeraFin)) {
            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB626"));
            return false;
        }

        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(cNumAnular, registro.getCampos().get(cNumAnular).toString());
        param.put(GeneralParameterEnum.ANO.getName(), ano);
        param.put(GeneralParameterEnum.CUENTA.getName(), cuenta);
        param.put(cNumChequera,
                        registro.getCampos().get(cNumChequera).toString());

        Registro rs;
        try {
            rs = RegistroConverter.toRegistro(
                            requestManager.get(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            ChequesAnuladosControladorUrlEnum.URL334
                                                                            .getValue())
                                            .getUrl(), param));
            if (!"0".equals(rs.getCampos().get("CANT").toString())) {
                JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB627"));
                return false;
            }
        }
        catch (SystemException e) {

            Logger.getLogger(ChequesAnuladosControlador.class.getName())
                            .log(Level.SEVERE, null, e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

        actualizarAntes();
        // </CODIGO_DESARROLLADO>
        return true;
    }

    @Override
    public boolean insertarDespues() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
        return true;
    }

    @Override
    public boolean actualizarAntes() {
        // <CODIGO_DESARROLLADO>
        registro.getCampos().put("ANO", ano);
        registro.getCampos().put(cCuenta, cuenta);
        registro.getCampos().put("COMPANIA", compania);
        try {
            registro.getCampos().put("CONSECUTIVO", ejbContabilidadCero
                            .generarConsecutivoConValorInicial(tabla,
                                            "COMPANIA = ''" + compania
                                                + "'' AND ANO = " + ano,
                                            "CONSECUTIVO", "1"));

        }
        catch (SystemException ex) {
            Logger.getLogger(ChequesAnuladosControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
        }
        // </CODIGO_DESARROLLADO>
        return true;
    }

    @Override
    public boolean actualizarDespues() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
        return true;
    }

    @Override
    public boolean eliminarAntes() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
        return true;
    }

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
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado cuando se cierra el formulario
     *
     */
    public void cerrarFormulario() {
        RequestContext.getCurrentInstance().closeDialog(null);
    }

    /**
     * Este metodo es ejecutado despues de finalizar la insercion y
     * edicion del registro se usa cuando se desean agregar valores al
     * registro despues de dichas acciones
     */
    @Override
    public void asignarValoresRegistro() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    // <SET_GET_ATRIBUTOS>
    /**
     * Retorna la variable ano
     *
     * @return ano
     */
    public String getAno() {
        return ano;
    }

    /**
     * Asigna la variable ano
     *
     * @param ano
     * Variable a asignar en ano
     */
    public void setAno(String ano) {
        this.ano = ano;
    }

    // </SET_GET_ATRIBUTOS>
    // <SET_GET_PARAMETROS>
    // </SET_GET_PARAMETROS>
    // <SET_GET_LISTAS>
    /**
     * Retorna la lista listaANO
     *
     * @return listaANO
     */
    public List<Registro> getListaano() {
        return listaano;
    }

    /**
     * Asigna la lista listaANO
     *
     * @param listaANO
     * Variable a asignar en listaANO
     */
    public void setListaano(List<Registro> listaano) {
        this.listaano = listaano;
    }

    /**
     * Retorna la lista listacuenta
     *
     * @return listacuenta
     */
    public List<Registro> getListacuenta() {
        return listacuenta;
    }

    /**
     * Asigna la lista listacuenta
     *
     * @param listacuenta
     * Variable a asignar en listacuenta
     */
    public void setListacuenta(List<Registro> listacuenta) {
        this.listacuenta = listacuenta;
    }

    // </SET_GET_LISTAS>
    // <SET_GET_LISTAS_COMBO_GRANDE>
    /**
     * Retorna la lista listaNumChequera
     *
     * @return listaNumChequera
     */
    public RegistroDataModelImpl getListaNumChequera() {
        return listaNumChequera;
    }

    /**
     * Asigna la lista listaNumChequera
     *
     * @param listaNumChequera
     * Variable a asignar en listaNumChequera
     */
    public void setListaNumChequera(RegistroDataModelImpl listaNumChequera) {
        this.listaNumChequera = listaNumChequera;
    }

    /**
     * Retorna la lista listaNumChequera
     *
     * @return listaNumChequera
     */
    public RegistroDataModelImpl getListaNumChequeraE() {
        return listaNumChequeraE;
    }

    /**
     * Asigna la lista listaNumChequera
     *
     * @param listaNumChequera
     * Variable a asignar en listaNumChequera
     */
    public void setListaNumChequeraE(RegistroDataModelImpl listaNumChequeraE) {
        this.listaNumChequeraE = listaNumChequeraE;
    }

    /**
     * Retorna la variable auxiliar
     *
     * @return auxiliar
     */
    public String getAuxiliar() {
        return auxiliar;
    }

    /**
     * Asigna la variable auxiliar
     *
     * @param auxiliar
     * Variable a asignar en auxiliar
     */
    public void setAuxiliar(String auxiliar) {
        this.auxiliar = auxiliar;
    }
    // </SET_GET_LISTAS_COMBO_GRANDE>
}
