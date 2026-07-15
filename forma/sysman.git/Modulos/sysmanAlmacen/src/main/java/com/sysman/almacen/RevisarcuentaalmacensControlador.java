package com.sysman.almacen;

import com.sysman.almacen.enums.RevisarcuentaalmacensControladorEnum;
import com.sysman.almacen.enums.RevisarcuentaalmacensControladorUrlEnum;
import com.sysman.beanbase.BeanBaseContinuoAcmeImpl;
import com.sysman.componentes.Direccionador;
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
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.util.SysmanFunciones;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.event.RowEditEvent;
import org.primefaces.event.SelectEvent;

/**
 *
 * @author NGOMEZ
 * @version 1, 15/04/2016
 * @version 2, 09/05/2017 spina - se realiza la refactorizacion para
 * dss y depuracion sonar
 * 
 * @version 3.0, 13/06/2017, <strong>pespitia</strong>:<br>
 * Reemplazar numero del formulario por enumerado.<br>
 * Reemplazar el redireccionar por el redireccionarForma de
 * SessionUtil.
 */
@ManagedBean
@ViewScoped
public class RevisarcuentaalmacensControlador extends BeanBaseContinuoAcmeImpl {
    private final String compania;

    /**
     * Constante a nival de clase que aloja el codigo del modulo desde
     * el cual el usuario abre el formyulario
     */
    private final String modulo;

    private String companiaSel;
    private String elemento;
    private String serie;
    private String nombreElemento;
    private String saldoAnteriorTotal;
    private String entradasTotal;
    private String salidasTotal;
    private String ajustesDebitoTotal;
    private String ajustesCreditoTotal;
    private String saldoFinalTotal;
    private String csaldoAnteriorTotal;
    private String valorEntradasTotal;
    private String valorSalidasTotal;
    private String saldoNuevoTotal;
    private String cajustesDebitoTotal;
    private String cajustesCreditoTotal;
    private String fechaInicial;
    private String fechaFinal;
    private String codigoCompaniaP;
    private String grupoP;
    private String opcionP;
    private String nombreComP;
    private RegistroDataModelImpl listaElementoInv;
    private String auxiliar;
    private List<Registro> listaCmbCompania;
    private List<Registro> listaSerieInv;

    /**
     * Creates a new instance of RevisarcuentaalmacensControlador
     */
    public RevisarcuentaalmacensControlador() {
        super();

        compania = SessionUtil.getCompania();
        modulo = SessionUtil.getModulo();

        try {
            // 635
            numFormulario = GeneralCodigoFormaEnum.REVISARCUENTAALMACENS_CONTROLADOR
                            .getCodigo();

            validarPermisos();
            Map<String, Object> parametrosEntrada = SessionUtil.getFlash();
            if (parametrosEntrada != null) {
                fechaInicial = SysmanFunciones
                                .nvl(parametrosEntrada.get("fechaInicial"), "")
                                .toString();
                fechaFinal = SysmanFunciones
                                .nvl(parametrosEntrada.get("fechaFinal"), "")
                                .toString();
                codigoCompaniaP = SysmanFunciones
                                .nvl(parametrosEntrada.get("codigoCompaniaP"),
                                                "")
                                .toString();
                opcionP = SysmanFunciones
                                .nvl(parametrosEntrada.get("opcionP"), "")
                                .toString();
                nombreComP = SysmanFunciones
                                .nvl(parametrosEntrada.get("nombreComP"), "")
                                .toString();
                grupoP = SysmanFunciones
                                .nvl(parametrosEntrada.get("grupoP"), "")
                                .toString();
            }
        }
        catch (Exception ex) {
            Logger.getLogger(RevisarcuentaalmacensControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            SessionUtil.redireccionarMenuPermisos();
        }
        finally {
            SessionUtil.cleanFlash();
        }
    }

    @PostConstruct
    public void inicializar() {
        enumBase = GenericUrlEnum.V_BASE_CUENTAALMACEN;
        reasignarOrigen();
        registro = new Registro();
        abrirFormulario();

    }

    @Override
    public void reasignarOrigen() {
        buscarUrls();
        parametrosListado.put(GeneralParameterEnum.COMPANIA.getName(),
                        companiaSel);
        parametrosListado.put(GeneralParameterEnum.FECHAINICIAL.getName(),
                        fechaInicial);
        parametrosListado.put(GeneralParameterEnum.FECHAFINAL.getName(),
                        fechaFinal);
        parametrosListado.put("NOELEMENTO", 
                        SysmanFunciones.esBdSqlServer()
                        ? SysmanFunciones.nvl(elemento, 0)
                        : elemento);
        parametrosListado.put("NOSERIE", SysmanFunciones.esBdSqlServer()
                        ? SysmanFunciones.nvl(serie, 0)
                        : serie);
    }

    public void cargarListaCmbCompania() {
        try {
            listaCmbCompania = RegistroConverter
                            .toListRegistro(requestManager.getList(
                                            UrlServiceUtil.getInstance()
                                                            .getUrlServiceByUrlByEnumID(
                                                                            RevisarcuentaalmacensControladorUrlEnum.URL4369
                                                                                            .getValue())
                                                            .getUrl(),
                                            null));
        }
        catch (SystemException e) {
            Logger.getLogger(RevisarcuentaalmacensControlador.class.getName())
                            .log(Level.SEVERE, null, e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    public void cargarListaElementoInv() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        RevisarcuentaalmacensControladorUrlEnum.URL4905
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(RevisarcuentaalmacensControladorEnum.COMPANIA.getValue(),
                        companiaSel);

        listaElementoInv = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param, true,
                        "CODIGOELEMENTO");
    }

    public void cargarListaSerieInv() {
        Map<String, Object> param = new TreeMap<>();
        param.put(RevisarcuentaalmacensControladorEnum.COMPANIA.getValue(),
                        companiaSel);
        param.put(RevisarcuentaalmacensControladorEnum.ELEMENTO.getValue(),
                        elemento);
        try {
            listaSerieInv = RegistroConverter
                            .toListRegistro(requestManager.getList(
                                            UrlServiceUtil.getInstance()
                                                            .getUrlServiceByUrlByEnumID(
                                                                            RevisarcuentaalmacensControladorUrlEnum.URL4589
                                                                                            .getValue())
                                                            .getUrl(),
                                            param));
        }
        catch (SystemException e) {
            Logger.getLogger(RevisarcuentaalmacensControlador.class.getName())
                            .log(Level.SEVERE, null, e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    public void cambiarCmbCompania() {
        elemento = null;
        nombreElemento = null;
        serie = null;
        cargarListaElementoInv();
        cargarListaSerieInv();
        reasignarOrigen();
        actualizarTotales();
    }

    public void cambiarSerieInv() {
        reasignarOrigen();
        actualizarTotales();
    }

    public void seleccionarFilaElementoInv(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        elemento = SysmanFunciones
                        .nvl(registroAux.getCampos().get("CODIGOELEMENTO"), "")
                        .toString();
        nombreElemento = SysmanFunciones
                        .nvl(registroAux.getCampos().get("NOMBRECORTO"), "")
                        .toString();
        serie = null;
        cargarListaSerieInv();
        reasignarOrigen();
        actualizarTotales();
    }

    @Override
    public void abrirFormulario() {
        companiaSel = compania;
        cargarListaCmbCompania();
        cargarListaElementoInv();
        cargarListaSerieInv();
        actualizarTotales();
    }

    @Override
    public void cancelarEdicion(RowEditEvent event) {
        getListaInicial().load();
    }

    public void actualizarTotales() {
        Map<String, Object> param = new TreeMap<>();
        param.put(RevisarcuentaalmacensControladorEnum.COMPANIA.getValue(),
                        companiaSel);
        param.put(GeneralParameterEnum.FECHAINICIAL.getName(), fechaInicial);
        param.put(GeneralParameterEnum.FECHAFINAL.getName(), fechaFinal);
        param.put("NOELEMENTO", SysmanFunciones.esBdSqlServer()
                        ? SysmanFunciones.nvl(elemento, 0)
                            : elemento);
        param.put("NOSERIE", SysmanFunciones.esBdSqlServer()
                        ? SysmanFunciones.nvl(serie, 0)
                        : serie);
        Registro regAux = null;
        try {
            regAux = RegistroConverter
                            .toListRegistro(requestManager.getList(
                                            UrlServiceUtil.getInstance()
                                                            .getUrlServiceByUrlByEnumID(
                                                                            RevisarcuentaalmacensControladorUrlEnum.URL7157
                                                                                            .getValue())
                                                            .getUrl(),
                                            param))
                            .get(0);
            saldoAnteriorTotal = regAux.getCampos().get("SALDOANTERIORTOTAL")
                            .toString();
            entradasTotal = regAux.getCampos().get("ENTRADASTOTAL").toString();
            salidasTotal = regAux.getCampos().get("SALIDASTOTAL").toString();
            ajustesDebitoTotal = regAux.getCampos().get("AJUSTESDEBITOTOTAL")
                            .toString();
            ajustesCreditoTotal = regAux.getCampos().get("AJUSTESCREDITOTOTAL")
                            .toString();
            saldoFinalTotal = regAux.getCampos().get("SALDOFINALTOTAL")
                            .toString();
            csaldoAnteriorTotal = regAux.getCampos().get("CSALDOANTERIORTOTAL")
                            .toString();
            valorEntradasTotal = regAux.getCampos().get("VALORENTRADASTOTAL")
                            .toString();
            valorSalidasTotal = regAux.getCampos().get("VALORSALIDASTOTAL")
                            .toString();
            saldoNuevoTotal = regAux.getCampos().get("SALDONUEVOTOTAL")
                            .toString();
            cajustesDebitoTotal = regAux.getCampos().get("CAJUSTESDEBITOTOTAL")
                            .toString();
            cajustesCreditoTotal = regAux.getCampos()
                            .get("CAJUSTESCREDITOTOTAL").toString();
        }
        catch (SystemException e) {
            Logger.getLogger(RevisarcuentaalmacensControlador.class.getName())
                            .log(Level.SEVERE, null, e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    public void ejecutarrcCerrar() {
        Map<String, Object> param = new HashMap<>();
        param.put("fechaInicialP", fechaInicial);
        param.put("fechaFinalP", fechaFinal);
        param.put("codigoCompaniaP", codigoCompaniaP);
        param.put("grupoP", grupoP);
        param.put("nombreComP", nombreComP);
        param.put("opcionP", opcionP);

        Direccionador dir = new Direccionador();
        dir.setNumForm(Integer
                        .toString(GeneralCodigoFormaEnum.FCUENTAALMACEN_CONTROLADOR
                                        .getCodigo()));

        dir.setParametros(param);

        SessionUtil.redireccionarForma(dir, modulo);
    }

    public List<Registro> getListaCmbCompania() {
        return listaCmbCompania;
    }

    public void setListaCmbCompania(List<Registro> listaCmbCompania) {
        this.listaCmbCompania = listaCmbCompania;
    }

    public List<Registro> getListaSerieInv() {
        return listaSerieInv;
    }

    public void setListaSerieInv(List<Registro> listaSerieInv) {
        this.listaSerieInv = listaSerieInv;
    }

    public RegistroDataModelImpl getListaElementoInv() {
        return listaElementoInv;
    }

    public void setListaElementoInv(RegistroDataModelImpl listaElementoInv) {
        this.listaElementoInv = listaElementoInv;
    }

    public String getAuxiliar() {
        return auxiliar;
    }

    public void setAuxiliar(String auxiliar) {
        this.auxiliar = auxiliar;
    }

    public String getCompaniaSel() {
        return companiaSel;
    }

    public void setCompaniaSel(String companiaSel) {
        this.companiaSel = companiaSel;
    }

    public String getElemento() {
        return elemento;
    }

    public void setElemento(String elemento) {
        this.elemento = elemento;
    }

    public String getSerie() {
        return serie;
    }

    public void setSerie(String serie) {
        this.serie = serie;
    }

    public String getNombreElemento() {
        return nombreElemento;
    }

    public void setNombreElemento(String nombreElemento) {
        this.nombreElemento = nombreElemento;
    }

    public String getSaldoAnteriorTotal() {
        return saldoAnteriorTotal;
    }

    public void setSaldoAnteriorTotal(String saldoAnteriorTotal) {
        this.saldoAnteriorTotal = saldoAnteriorTotal;
    }

    public String getEntradasTotal() {
        return entradasTotal;
    }

    public void setEntradasTotal(String entradasTotal) {
        this.entradasTotal = entradasTotal;
    }

    public String getSalidasTotal() {
        return salidasTotal;
    }

    public void setSalidasTotal(String salidasTotal) {
        this.salidasTotal = salidasTotal;
    }

    public String getAjustesDebitoTotal() {
        return ajustesDebitoTotal;
    }

    public void setAjustesDebitoTotal(String ajustesDebitoTotal) {
        this.ajustesDebitoTotal = ajustesDebitoTotal;
    }

    public String getAjustesCreditoTotal() {
        return ajustesCreditoTotal;
    }

    public void setAjustesCreditoTotal(String ajustesCreditoTotal) {
        this.ajustesCreditoTotal = ajustesCreditoTotal;
    }

    public String getSaldoFinalTotal() {
        return saldoFinalTotal;
    }

    public void setSaldoFinalTotal(String saldoFinalTotal) {
        this.saldoFinalTotal = saldoFinalTotal;
    }

    public String getCsaldoAnteriorTotal() {
        return csaldoAnteriorTotal;
    }

    public void setCsaldoAnteriorTotal(String csaldoAnteriorTotal) {
        this.csaldoAnteriorTotal = csaldoAnteriorTotal;
    }

    public String getValorEntradasTotal() {
        return valorEntradasTotal;
    }

    public void setValorEntradasTotal(String valorEntradasTotal) {
        this.valorEntradasTotal = valorEntradasTotal;
    }

    public String getValorSalidasTotal() {
        return valorSalidasTotal;
    }

    public void setValorSalidasTotal(String valorSalidasTotal) {
        this.valorSalidasTotal = valorSalidasTotal;
    }

    public String getSaldoNuevoTotal() {
        return saldoNuevoTotal;
    }

    public void setSaldoNuevoTotal(String saldoNuevoTotal) {
        this.saldoNuevoTotal = saldoNuevoTotal;
    }

    public String getCajustesDebitoTotal() {
        return cajustesDebitoTotal;
    }

    public void setCajustesDebitoTotal(String cajustesDebitoTotal) {
        this.cajustesDebitoTotal = cajustesDebitoTotal;
    }

    public String getCajustesCreditoTotal() {
        return cajustesCreditoTotal;
    }

    public void setCajustesCreditoTotal(String cajustesCreditoTotal) {
        this.cajustesCreditoTotal = cajustesCreditoTotal;
    }

    public String getCompania() {
        return compania;
    }

    public String getFecha() {
        return fechaInicial;
    }

    public void setFecha(String fecha) {
        this.fechaInicial = fecha;
    }

    public String getFechaInicial() {
        return fechaInicial;
    }

    public void setFechaInicial(String fechaInicial) {
        this.fechaInicial = fechaInicial;
    }

    public String getFechaFinal() {
        return fechaFinal;
    }

    public void setFechaFinal(String fechaFinal) {
        this.fechaFinal = fechaFinal;
    }

    public String getCodigoCompaniaP() {
        return codigoCompaniaP;
    }

    public void setCodigoCompaniaP(String codigoCompaniaP) {
        this.codigoCompaniaP = codigoCompaniaP;
    }

    public String getGrupoP() {
        return grupoP;
    }

    public void setGrupoP(String grupoP) {
        this.grupoP = grupoP;
    }

    public String getOpcionP() {
        return opcionP;
    }

    public void setOpcionP(String opcionP) {
        this.opcionP = opcionP;
    }

    public String getNombreComP() {
        return nombreComP;
    }

    public void setNombreComP(String nombreComP) {
        this.nombreComP = nombreComP;
    }

    @Override
    public boolean insertarAntes() {
        return true;
    }

    @Override
    public boolean insertarDespues() {
        return true;
    }

    @Override
    public boolean actualizarAntes() {
        return true;
    }

    @Override
    public boolean actualizarDespues() {
        return true;
    }

    @Override
    public boolean eliminarAntes() {
        return true;
    }

    @Override
    public boolean eliminarDespues() {
        return true;
    }

    @Override
    public void removerCombos() {
        // METODO NO IMPLEMENTADO
    }

    @Override
    public void asignarValoresRegistro() {
        // METODO NO IMPLEMENTADO
    }

}
