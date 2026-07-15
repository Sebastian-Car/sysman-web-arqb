package com.sysman.precontractual;

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
import com.sysman.precontractual.enums.FrmcotizacionesproysControladorEnum;
import com.sysman.precontractual.enums.FrmcotizacionesproysControladorUrlEnum;
import com.sysman.recursos.ejb.EjbSysmanUtilRemote;
import com.sysman.util.SysmanFunciones;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
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

/**
 *
 * @author ybecerra
 * @version 1, 01/04/2016
 * @author jcrodriguez
 * @version 2, 25/08/2017, Refactoring y depuracion
 */
@ManagedBean
@ViewScoped

public class FrmcotizacionesproysControlador extends BeanBaseContinuoAcmeImpl {

    private final String compania;

    private String codElemento;
    private String codEstudio;
    private String codItem;
    private String elemento;
    private String rid;
    private String valorTotal;
    /**
     * lISTADO DE LOS AÑOS
     */
    private List<Registro> listacmbAno;

    @EJB
    private EjbSysmanUtilRemote ejbSysmanUtil;

    /**
     * Creates a new instance of FrmcotizacionesproysControlador
     */
    public FrmcotizacionesproysControlador() {
        super();
        compania = SessionUtil.getCompania();

        try {
            numFormulario = GeneralCodigoFormaEnum.FRMCOTIZACIONESPROYS_CONTROLADOR.getCodigo();
            validarPermisos();
            Map<String, Object> parametrosEntrada = SessionUtil.getFlash();
            codEstudio = validarCadena(parametrosEntrada, FrmcotizacionesproysControladorEnum.CODESTUDIO.getValue().toLowerCase());
            codItem = validarCadena(parametrosEntrada, FrmcotizacionesproysControladorEnum.COD_ITEM.getValue().toLowerCase());
            elemento = validarCadena(parametrosEntrada, GeneralParameterEnum.ELEMENTO.getName().toLowerCase());
            rid = validarCadena(parametrosEntrada, FrmcotizacionesproysControladorEnum.RID.getValue().toLowerCase());

        }
        catch (Exception ex) {
            Logger.getLogger(FrmcotizacionesproysControlador.class.getName()).log(Level.SEVERE, null, ex);
            SessionUtil.redireccionarMenuPermisos();
        }
    }

    private String validarCadena(Map<String, Object> campos, String var) {
        return SysmanFunciones.validarCampoVacio(campos, var) ? "" : campos.get(var).toString();
    }

    @PostConstruct
    public void init() {

        enumBase = GenericUrlEnum.ES_COTIZACIONES;
        buscarLlave();
        reasignarOrigen();
        cargarListacmbAno();
        registro = new Registro();
        nombreElemento();
        obtenerValorTotal();
        abrirFormulario();
    }

    @Override
    public void reasignarOrigen() {
        buscarUrls();
        parametrosListado.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        parametrosListado.put(FrmcotizacionesproysControladorEnum.CODESTUDIO.getValue(), codEstudio);
        parametrosListado.put(FrmcotizacionesproysControladorEnum.COD_ITEM.getValue(), codItem);
        parametrosListado.put(GeneralParameterEnum.ELEMENTO.getName(), elemento);

    }

    @Override
    public void abrirFormulario() {
        // <CODIGO_DESARROLLADO>
        /*
         * FR604-AL_ABRIR Private Sub Form_Open(Cancel As Integer)
         * DoCmd.Restore End Sub
         */
        // </CODIGO_DESARROLLADO>
    }

    /**
     * 
     * Carga la lista listacmbAno
     *
     */
    public void cargarListacmbAno() {
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);

        try {
            listacmbAno = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            "4001")
                                            .getUrl(), param));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    @Override
    public void cancelarEdicion(RowEditEvent event) {
        getListaInicial().load();
    }

    @Override
    public boolean insertarAntes() {

        registro.getCampos().put(GeneralParameterEnum.COMPANIA.getName(), compania);
        registro.getCampos().put(FrmcotizacionesproysControladorEnum.COD_ESTUDIO.getValue(), codEstudio);
        registro.getCampos().put(FrmcotizacionesproysControladorEnum.COD_ITEM.getValue(), codItem);
        registro.getCampos().put(FrmcotizacionesproysControladorEnum.COD_ELEMENTO.getValue(), elemento);
        long cons = 0;
        try {
            // <CODIGO_DESARROLLADO>
            StringBuilder criterio = new StringBuilder();
            criterio.append("COMPANIA = ''");
            criterio.append(compania);
            criterio.append("'' AND COD_ESTUDIO = ''");
            criterio.append(codEstudio);
            criterio.append("'' AND COD_ITEM = ''");
            criterio.append(codItem);
            criterio.append("'' AND COD_ELEMENTO = ''");
            criterio.append(elemento);
            criterio.append("''");

            cons = ejbSysmanUtil.generarConsecutivoConValorInicial(FrmcotizacionesproysControladorEnum.ES_COTIZACIONES.getValue(),
                            criterio.toString(),
                            GeneralParameterEnum.CONSECUTIVO.getName(), "1");

            // </CODIGO_DESARROLLADO>
        }
        catch (SystemException ex) {
            Logger.getLogger(FrmcotizacionesproysControlador.class.getName()).log(Level.SEVERE, null, ex);
            JsfUtil.agregarMensajeError(ex.getMessage());
        }
        registro.getCampos().put(GeneralParameterEnum.CONSECUTIVO.getName(), cons);

        return true;
    }

    @Override
    public boolean insertarDespues() {

        obtenerValorTotal();
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
        obtenerValorTotal();
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
        obtenerValorTotal();
        return true;
    }

    public void nombreElemento() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        FrmcotizacionesproysControladorUrlEnum.URL2222
                                                        .getValue());
        Map<String, Object> params = new HashMap<>();
        params.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        params.put(GeneralParameterEnum.ELEMENTO.getName(), elemento);

        Registro reg;
        try {
            reg = RegistroConverter.toRegistro(requestManager.get(urlBean.getUrl(), params));
            if (reg != null) {
                codElemento = (String) reg.getCampos().get(FrmcotizacionesproysControladorEnum.NOMBRELARGO.getValue());
            }
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    @Override
    public void removerCombos() {
        // heredado del bean base
    }

    public void cerrarFormulario() {
        RequestContext.getCurrentInstance().closeDialog(null);
    }

    @Override
    public void asignarValoresRegistro() {
        // heredado del bean base
    }

    public void obtenerValorTotal() {
        Map<String, Object> params = new HashMap<>();
        params.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        params.put(FrmcotizacionesproysControladorEnum.CODESTUDIO.getValue(), codEstudio);
        params.put(FrmcotizacionesproysControladorEnum.COD_ITEM.getValue(), codItem);
        params.put(GeneralParameterEnum.ELEMENTO.getName(), elemento);
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        FrmcotizacionesproysControladorUrlEnum.URL2224
                                                        .getValue());
        Registro reg;

        try {
            reg = RegistroConverter.toRegistro(requestManager.get(urlBean.getUrl(), params));

            if (reg != null) {
                if (!SysmanFunciones.validarCampoVacio(reg.getCampos(), GeneralParameterEnum.VALOR.getName())) {
                    valorTotal = formato(Double.parseDouble(validarCadena(reg.getCampos(), GeneralParameterEnum.VALOR.getName())));
                }
                else {
                    valorTotal = "0";
                }

            }
            else {
                valorTotal = "0";
            }
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    private String formato(double total) {
        DecimalFormatSymbols dfs = new DecimalFormatSymbols(Locale.US);
        return new DecimalFormat("#,##0", dfs).format(total);
    }

    public void ejecutarrcCerrar() {
        // <CODIGO_DESARROLLADO>
        String[] campos = { "rid" };
        String[] valores = { rid };
        SessionUtil.redireccionar("/form/precontractual/epitemsestproy.sysman", campos, valores);
        // </CODIGO_DESARROLLADO>
    }

    public String getValorTotal() {
        return valorTotal;
    }

    public void setValorTotal(String valorTotal) {
        this.valorTotal = valorTotal;
    }

    public String getCodElemento() {
        return codElemento;
    }

    public void setCodElemento(String codElemento) {
        this.codElemento = codElemento;
    }

    /**
     * Retorna la lista listacmbAno
     * 
     * @return listacmbAno
     */
    public List<Registro> getListacmbAno() {
        return listacmbAno;
    }

    /**
     * Asigna la lista listacmbAno
     * 
     * @param listacmbAno
     * Variable a asignar en listacmbAno
     */
    public void setListacmbAno(List<Registro> listacmbAno) {
        this.listacmbAno = listacmbAno;
    }
}
