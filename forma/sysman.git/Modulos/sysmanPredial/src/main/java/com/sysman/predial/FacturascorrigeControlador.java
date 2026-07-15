package com.sysman.predial;

import com.sysman.beanbase.BeanBaseContinuoAcmeImpl;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.predial.enums.FacturascorrigeControladorUrlEnum;
import com.sysman.services.RegistroDataModel;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.util.SysmanConstantes;
import com.sysman.util.SysmanFunciones;

import java.util.Calendar;
import java.util.Date;
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
 * @version 1, 09/06/2016
 *
 * @author spina - refactorizo conexiones
 * @version 2, 13/06/2017
 *
 * @author spina
 * @version 3, 28/06/2017 - refactoriza dss, depuracion sonar y ejbs
 */
@ManagedBean
@ViewScoped
public class FacturascorrigeControlador extends BeanBaseContinuoAcmeImpl {
    private final String compania;
    private final String nOrden;
    // <DECLARAR_ATRIBUTOS>
    private String codigo;
    private String nombre;
    private String nFactura;
    private String numeroOrden;
    // </DECLARAR_ATRIBUTOS>
    // <DECLARAR_PARAMETROS>
    private Map<String, Object> rid;
    // </DECLARAR_PARAMETROS>
    // <DECLARAR_LISTAS>
    private List<Registro> listaPagBan;
    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>
    private RegistroDataModelImpl listaCodigo;
    private RegistroDataModel listaCodigoE;
    private String auxiliar;
    // </DECLARAR_LISTAS_COMBO_GRANDE>

    /**
     * Creates a new instance of FacturascorrigeControlador
     */
    @SuppressWarnings("unchecked")
    public FacturascorrigeControlador() {
        super();
        compania = SessionUtil.getCompania();
        nOrden = SysmanConstantes.NUMERO_ORDEN_PREDIAL;
        try {
            numFormulario = GeneralCodigoFormaEnum.FACTURASCORRIGE_CONTROLADOR
                            .getCodigo();
            validarPermisos();
            Map<String, Object> parametrosEntrada = SessionUtil.getFlash();
            if (parametrosEntrada != null) {
                rid = (Map<String, Object>) parametrosEntrada.get("rid");
            }
            // <INI_ADICIONAL>
            // </INI_ADICIONAL>
        }
        catch (Exception ex) {
            Logger.getLogger(FacturascorrigeControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            SessionUtil.redireccionarMenuPermisos();
        }
        finally {
            SessionUtil.cleanFlash();
        }
    }

    @PostConstruct
    public void inicializar() {

        tabla = "IP_RECIBOS_DE_PAGO";
        buscarLlave();
        registro = new Registro();
        reasignarOrigen();
        // <CARGAR_LISTA>
        cargarListaPagBan();
        // </CARGAR_LISTA>
        // <CARGAR_LISTA_COMBO_GRANDE>
        cargarListaCodigo();
        // </CARGAR_LISTA_COMBO_GRANDE>
        abrirFormulario();
    }

    @Override
    public void reasignarOrigen() {
        urlListado = UrlServiceUtil.getUrlBeanById(
                        FacturascorrigeControladorUrlEnum.URL3664
                                        .getValue());
        parametrosListado.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
        parametrosListado.put(GeneralParameterEnum.CODIGO.getName(), codigo);
    }

    // <METODOS_CARGAR_LISTA>
    public void cargarListaPagBan() {
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        try {
            listaPagBan = RegistroConverter
                            .toListRegistro(requestManager.getList(
                                            UrlServiceUtil.getInstance()
                                                            .getUrlServiceByUrlByEnumID(
                                                                            FacturascorrigeControladorUrlEnum.URL3665
                                                                                            .getValue())
                                                            .getUrl(),
                                            param));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    public void cargarListaCodigo() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        FacturascorrigeControladorUrlEnum.URL3666
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.NUMERO.getName(), nOrden);
        listaCodigo = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param, true,
                        GeneralParameterEnum.CODIGO.getName());
    }

    // </METODOS_CARGAR_LISTA>
    // <METODOS_BOTONES>
    // </METODOS_BOTONES>
    // <METODOS_CAMBIAR>
    // </METODOS_CAMBIAR>
    // <METODOS_COMBOS_GRANDES>
    public void seleccionarFilaCodigo(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        codigo = String.valueOf(registroAux.getCampos()
                        .get(GeneralParameterEnum.CODIGO
                                        .getName()));
        nFactura = String
                        .valueOf(SysmanFunciones.nvl(registroAux.getCampos()
                                        .get("NUMERO_FACTURA"), ""));
        numeroOrden = String
                        .valueOf(SysmanFunciones.nvl(registroAux.getCampos()
                                        .get("NUMERO_ORDEN"), ""));
        nombre = String.valueOf(SysmanFunciones
                        .nvl(registroAux.getCampos().get("NOMBRE"), ""));
        reasignarOrigen();
    }

    // </METODOS_COMBOS_GRANDES>
    @Override
    public void abrirFormulario() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    @Override
    public void cancelarEdicion(RowEditEvent event) {
        getListaInicial().load();
    }

    @Override
    public boolean insertarAntes() {
        // <CODIGO_DESARROLLADO>
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
        if ((boolean) registro.getCampos().get("PAGO")
            || (registro.getCampos().get("PREFECPAG") != null)) {
            JsfUtil.agregarMensajeError(idioma.getString("TB_TB206"));
            return false;
        }

        if (!registro.getCampos().get("PREANO").equals(
                        String.valueOf(SysmanFunciones.getParteFecha(new Date(),
                                        Calendar.YEAR)))) {
            JsfUtil.agregarMensajeError(idioma.getString("TB_TB207"));
            return false;
        }

        if (registro.getCampos().get("DOCNUM").toString().equals(nFactura)) {
            JsfUtil.agregarMensajeError(idioma.getString("TB_TB208"));
            return false;
        }
        // </CODIGO_DESARROLLADO>
        return true;
    }

    @Override
    public boolean eliminarDespues() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
        return true;
    }

    @Override
    public void removerCombos() {
        // NO SE IMPLEMENTA
    }

    @Override
    public void asignarValoresRegistro() {
        // NO SE IMPLEMENTA
    }

    // <SET_GET_ATRIBUTOS>

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getnFactura() {
        return nFactura;
    }

    public void setnFactura(String nFactura) {
        this.nFactura = nFactura;
    }

    public String getNumeroOrden() {
        return numeroOrden;
    }

    public void setNumeroOrden(String numeroOrden) {
        this.numeroOrden = numeroOrden;
    }

    // </SET_GET_ATRIBUTOS>
    // <SET_GET_PARAMETROS>
    public Map<String, Object> getRid() {
        return rid;
    }

    public void setRid(Map<String, Object> rid) {
        this.rid = rid;
    }

    // </SET_GET_PARAMETROS>
    // <SET_GET_LISTAS>
    public List<Registro> getListaPagBan() {
        return listaPagBan;
    }

    public void setListaPagBan(List<Registro> listaPagBan) {
        this.listaPagBan = listaPagBan;
    }

    // </SET_GET_LISTAS>
    // <SET_GET_LISTAS_COMBO_GRANDE>
    public RegistroDataModelImpl getListaCodigo() {
        return listaCodigo;
    }

    public void setListaCodigo(RegistroDataModelImpl listaCodigo) {
        this.listaCodigo = listaCodigo;
    }

    public RegistroDataModel getListaCodigoE() {
        return listaCodigoE;
    }

    public void setListaCodigoE(RegistroDataModel listaCodigoE) {
        this.listaCodigoE = listaCodigoE;
    }

    public String getAuxiliar() {
        return auxiliar;
    }

    public void setAuxiliar(String auxiliar) {
        this.auxiliar = auxiliar;
    }
    // </SET_GET_LISTAS_COMBO_GRANDE>

    public void ejecutarrcCerrar() {
        // <CODIGO_DESARROLLADO>
        String[] campos = { "rid" };
        Object[] valores = { rid };
        SessionUtil.redireccionar("/predialregispagban.sysman", campos,
                        valores);
        // </CODIGO_DESARROLLADO>
    }
}
