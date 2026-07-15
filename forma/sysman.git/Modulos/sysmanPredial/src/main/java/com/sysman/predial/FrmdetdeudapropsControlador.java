package com.sysman.predial;

import com.sysman.beanbase.BeanBaseContinuoAcmeImpl;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.Constantes;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.predial.ejb.impl.EjbPredialCero;
import com.sysman.predial.enums.FrmdetdeudapropsControladorEnum;
import com.sysman.predial.enums.FrmdetdeudapropsControladorUrlEnum;
import com.sysman.util.SysmanConstantes;
import com.sysman.util.SysmanFunciones;

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
 * @author sdaza
 * @version 1, 29/07/2016
 *
 * @author spina - refactorizo conexiones
 * @version 2, 13/06/2017
 *
 * @author asana - Se refactoriza llamado a registros
 * @version 3, 30/06/2017
 */
@ManagedBean
@ViewScoped
public class FrmdetdeudapropsControlador extends BeanBaseContinuoAcmeImpl {
    private final String compania;
    // <DECLARAR_ATRIBUTOS>
    private String codPredio;
    private String nomC1;
    private String nomC2;
    private String nomC3;
    private String nomC4;
    private String vlrC1;
    private String vlrC2;
    private String vlrC3;
    private String vlrC4;
    private String vlrDsc;
    private String vlrOtros;
    private String vlrTotal;
    private Map<String, Object> rid;
    private String codigoPredio;
    private String nitPropietario;
    private String nomPropietario;
    // </DECLARAR_ATRIBUTOS>
    // <DECLARAR_PARAMETROS>
    // </DECLARAR_PARAMETROS>
    // <DECLARAR_LISTAS>
    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>
    // </DECLARAR_LISTAS_COMBO_GRANDE>

    private Map<String, Object> parametrosEntrada;
    @EJB
    EjbPredialCero ejbpredialcero;

    /**
     * Creates a new instance of FrmdetdeudapropsControlador
     */
    @SuppressWarnings("unchecked")
    public FrmdetdeudapropsControlador() {
        super();
        compania = SessionUtil.getCompania();
        try {
            numFormulario = GeneralCodigoFormaEnum.FRMDETDEUDAPROPS_CONTROLADOR
                            .getCodigo();
            validarPermisos();
            parametrosEntrada = SessionUtil.getFlash();

            if (parametrosEntrada != null) {
                codPredio = parametrosEntrada.get("codPredio").toString();
                setRid((Map<String, Object>) parametrosEntrada.get("rid"));
                setCodigoPredio(parametrosEntrada.get("codigoPredio")
                                .toString());
                setNitPropietario(parametrosEntrada.get("nitPropietario")
                                .toString());
                setNomPropietario(parametrosEntrada.get("nomPropietario")
                                .toString());
            }
            else {
                SessionUtil.redireccionarMenuPermisos();
                return;
            }
            // <INI_ADICIONAL>
            // </INI_ADICIONAL>
        }
        catch (Exception ex) {
            Logger.getLogger(FrmdetdeudapropsControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            SessionUtil.redireccionarMenuPermisos();
        }
        finally {
            SessionUtil.cleanFlash();
        }
    }

    @PostConstruct
    public void init() {
        tabla = FrmdetdeudapropsControladorEnum.PARAM0.getValue();
        buscarLlave();
        reasignarOrigen();
        registro = new Registro();
        // <CARGAR_LISTA>
        // </CARGAR_LISTA>
        // <CARGAR_LISTA_COMBO_GRANDE>
        // </CARGAR_LISTA_COMBO_GRANDE>
        abrirFormulario();
        actualizarTotales();
    }

    @Override
    public void reasignarOrigen() {

        parametrosListado.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
        parametrosListado.put(FrmdetdeudapropsControladorEnum.PARAM1.getValue(),
                        codPredio);
        parametrosListado.put(FrmdetdeudapropsControladorEnum.PARAM2.getValue(),
                        SysmanConstantes.NUMERO_ORDEN_PREDIAL);

        urlListado = UrlServiceUtil.getInstance().getUrlServiceByUrlByEnumID(
                        FrmdetdeudapropsControladorUrlEnum.URL7406
                                        .getValue());
    }

    // <METODOS_CARGAR_LISTA>
    // </METODOS_CARGAR_LISTA>
    // <METODOS_BOTONES>
    // </METODOS_BOTONES>
    // <METODOS_CAMBIAR>
    // </METODOS_CAMBIAR>
    // <METODOS_COMBOS_GRANDES>
    // </METODOS_COMBOS_GRANDES>
    @Override
    public void abrirFormulario() {
        // <CODIGO_DESARROLLADO>
        try {
            nomC1 = ejbpredialcero.consultarEncabezadoDeColumna(compania, 1);
            nomC2 = ejbpredialcero.consultarEncabezadoDeColumna(compania, 2);
            nomC3 = ejbpredialcero.consultarEncabezadoDeColumna(compania, 3);
            nomC4 = ejbpredialcero.consultarEncabezadoDeColumna(compania, 4);
        }
        catch (SystemException e) {

            JsfUtil.agregarMensajeError(
                            idioma.getString(Constantes.MSM_TRANS_INTERRUMPIDA)
                                            + " "
                                            + e.getMessage());
            Logger.getLogger(FrmdetdeudapropsControlador.class.getName())
                            .log(Level.SEVERE, null, e);
        }

        // </CODIGO_DESARROLLADO>
    }

    public void actualizarTotales() {

        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.CODIGO.getName(), codPredio);
        param.put(GeneralParameterEnum.NUMERO_ORDEN.getName(),
                        SysmanConstantes.NUMERO_ORDEN_PREDIAL);

        Registro regAux;
        try {
            regAux = RegistroConverter.toRegistro(
                            requestManager.get(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            FrmdetdeudapropsControladorUrlEnum.URL7407
                                                                            .getValue())
                                            .getUrl(), param));
            vlrC1 = SysmanFunciones.nvl(
                            regAux.getCampos().get("TIMPUESTO"),
                            "0.00").toString();
            vlrC2 = SysmanFunciones.nvl(
                            regAux.getCampos().get("TINTERES"), "0.00")
                            .toString();
            vlrC3 = SysmanFunciones.nvl(
                            regAux.getCampos().get("TCAR"), "0.00").toString();
            vlrC4 = SysmanFunciones.nvl(
                            regAux.getCampos().get("TCARINT"), "0.00")
                            .toString();
            vlrDsc = SysmanFunciones.nvl(
                            regAux.getCampos().get("TDESC"), "0.00").toString();
            vlrOtros = SysmanFunciones.nvl(
                            regAux.getCampos().get("TOTROS"), "0.00")
                            .toString();
            vlrTotal = SysmanFunciones.nvl(
                            regAux.getCampos().get("TDEUDA"), "0.00")
                            .toString();
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
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    public void cerrarFormulario() {
        RequestContext.getCurrentInstance().closeDialog(null);
    }

    @Override
    public void asignarValoresRegistro() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    // <SET_GET_ATRIBUTOS>
    public String getCodPredio() {
        return codPredio;
    }

    public void setCodPredio(String codPredio) {
        this.codPredio = codPredio;
    }

    public String getNomC1() {
        return nomC1;
    }

    public void setNomC1(String nomC1) {
        this.nomC1 = nomC1;
    }

    public String getNomC2() {
        return nomC2;
    }

    public void setNomC2(String nomC2) {
        this.nomC2 = nomC2;
    }

    public String getNomC3() {
        return nomC3;
    }

    public void setNomC3(String nomC3) {
        this.nomC3 = nomC3;
    }

    public String getNomC4() {
        return nomC4;
    }

    public void setNomC4(String nomC4) {
        this.nomC4 = nomC4;
    }

    public String getVlrC1() {
        return vlrC1;
    }

    public void setVlrC1(String vlrC1) {
        this.vlrC1 = vlrC1;
    }

    public String getVlrC2() {
        return vlrC2;
    }

    public void setVlrC2(String vlrC2) {
        this.vlrC2 = vlrC2;
    }

    public String getVlrC3() {
        return vlrC3;
    }

    public void setVlrC3(String vlrC3) {
        this.vlrC3 = vlrC3;
    }

    public String getVlrC4() {
        return vlrC4;
    }

    public void setVlrC4(String vlrC4) {
        this.vlrC4 = vlrC4;
    }

    public String getVlrDsc() {
        return vlrDsc;
    }

    public void setVlrDsc(String vlrDsc) {
        this.vlrDsc = vlrDsc;
    }

    public String getVlrOtros() {
        return vlrOtros;
    }

    public void setVlrOtros(String vlrOtros) {
        this.vlrOtros = vlrOtros;
    }

    public String getVlrTotal() {
        return vlrTotal;
    }

    public void setVlrTotal(String vlrTotal) {
        this.vlrTotal = vlrTotal;
    }

    public String getNomPropietario() {
        return nomPropietario;
    }

    public void setNomPropietario(String nomPropietario) {
        this.nomPropietario = nomPropietario;
    }

    public Map<String, Object> getRid() {
        return rid;
    }

    public void setRid(Map<String, Object> rid) {
        this.rid = rid;
    }

    public String getCodigoPredio() {
        return codigoPredio;
    }

    public void setCodigoPredio(String codigoPredio) {
        this.codigoPredio = codigoPredio;
    }

    public String getNitPropietario() {
        return nitPropietario;
    }

    public void setNitPropietario(String nitPropietario) {
        this.nitPropietario = nitPropietario;
    }

    // </SET_GET_ATRIBUTOS>
    // <SET_GET_PARAMETROS>
    // </SET_GET_PARAMETROS>
    // <SET_GET_LISTAS>
    // </SET_GET_LISTAS>
    // <SET_GET_LISTAS_COMBO_GRANDE>
    // </SET_GET_LISTAS_COMBO_GRANDE>
}
