package com.sysman.planeacion;

import com.sysman.beanbase.BeanBaseContinuoAcmeImpl;
import com.sysman.componentes.Direccionador;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.planeacion.enums.SubpropuestadetallesControladorEnum;
import com.sysman.planeacion.enums.SubpropuestadetallesControladorUrlEnum;
import com.sysman.services.FormContinuoService;
import com.sysman.util.SysmanFunciones;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.event.RowEditEvent;

/**
 *
 * @author ngomez
 * @version 1, 17/12/2015
 * 
 * @author asana
 * @version 2, 11/09/2017 Se realiza proceso de refactoring.
 */
@ManagedBean
@ViewScoped
public class SubpropuestadetallesControlador extends BeanBaseContinuoAcmeImpl {

    private final String compania;
    private Map<String, Object> rid;
    private String codReq;
    private String codPro;
    private String valorUnitario;
    private String cantidad;
    private String valorTotal;
    private String porcIva;
    private String porDescuento;

    /**
     * Creates a new instance of SubpropuestadetallesControlador
     */
    @SuppressWarnings("unchecked")
    public SubpropuestadetallesControlador() {
        super();
        compania = SessionUtil.getCompania();
        valorUnitario = "VALORUNITARIO";
        cantidad = "CANTIDAD";
        valorTotal = "VLRTOTAL";
        porcIva = "PORCIVA";
        porDescuento = "PORCDESC";
        numFormulario = GeneralCodigoFormaEnum.SUBPROPUESTADETALLES_CONTROLADOR
                        .getCodigo();
        Map<String, Object> parametrosEntrada = SessionUtil.getFlash();
        if (parametrosEntrada != null) {
            rid = (Map<String, Object>) parametrosEntrada.get("rid");
            codReq = parametrosEntrada.get("codReq").toString();
            codPro = parametrosEntrada.get("codPro").toString();
        }
        SessionUtil.cleanFlash();
        try {
            validarPermisos();
        }
        catch (SysmanException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    @PostConstruct
    public void init() {
        tabla = SubpropuestadetallesControladorEnum.TABLA.getValue();
        buscarLlave();
        reasignarOrigen();
        registro = new Registro(new HashMap<String, Object>());
        abrirFormulario();
    }

    @Override
    public FormContinuoService getService() {
        return service;
    }

    @Override
    public void setService(FormContinuoService service) {
        this.service = service;
    }

    public Map<String, Object> getRid() {
        return rid;
    }

    public void setRid(Map<String, Object> rid) {
        this.rid = rid;
    }

    public String getCodReq() {
        return codReq;
    }

    public void setCodReq(String codReq) {
        this.codReq = codReq;
    }

    public String getCodPro() {
        return codPro;
    }

    public void setCodPro(String codPro) {
        this.codPro = codPro;
    }

    @Override
    public void abrirFormulario() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    public void onRowCancel() {
        listaInicial.load();
    }

    @Override
    public boolean insertarAntes() {
        // <CODIGO_DESARROLLADO>
        registro.getCampos().remove("NOMBRELARGO");
        registro.getCampos().put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
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
        registro.getCampos().remove(
                        SubpropuestadetallesControladorEnum.NOMBRE.getValue());
        registro.getCampos().remove("COD_DETALLE");
        registro.getCampos().remove("ORDENDESUMINISTRO");
        registro.getCampos().remove("COD_PROPUESTA");
        registro.getCampos().remove("CENTRODECOSTO");
        registro.getCampos().remove("SALDOCANT");
        registro.getCampos().remove("COMPANIA");
        registro.getCampos().remove("DEPENDENCIA");
        registro.getCampos().remove("REQUISICION");
        registro.getCampos().remove("ELEMENTO");
        registro.getCampos().remove("COD_REQUISICION");

        registro.getCampos().put(porcIva, SysmanFunciones
                        .nvl(registro.getCampos().get(porcIva), "0"));
        registro.getCampos().put(cantidad, SysmanFunciones
                        .nvl(registro.getCampos().get(cantidad), "0"));
        registro.getCampos().put(valorUnitario, SysmanFunciones
                        .nvl(registro.getCampos().get(valorUnitario), "0"));
        registro.getCampos().put(porDescuento, SysmanFunciones
                        .nvl(registro.getCampos().get(porDescuento), "0"));

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

    public void cambiarPocIVA() {
        // <CODIGO_DESARROLLADO>
        if (registro.getCampos()
                        .get(porcIva) == null) {
            registro.getCampos()
                            .put(porcIva, 0);
        }
        registro.getCampos().put(
                        valorTotal, valorTotal());

        // </CODIGO_DESARROLLADO>
    }

    public void cambiarPocIVAC(int rowNum) {
        // <CODIGO_DESARROLLADO>
        if (listaInicial.getDatasource().get(rowNum % 10).getCampos()
                        .get(porcIva) == null) {
            listaInicial.getDatasource().get(rowNum % 10).getCampos()
                            .put(porcIva, 0);
        }

        listaInicial.getDatasource().get(rowNum % 10).getCampos().put(
                        valorTotal, valorTotal(rowNum));
        // </CODIGO_DESARROLLADO>
    }

    public void cambiarCantidad() {
        // <CODIGO_DESARROLLADO>
        if (registro.getCampos().get(cantidad) == null) {
            registro.getCampos().put(cantidad, 0);
        }
        registro.getCampos().put(valorTotal, valorTotal());
        // </CODIGO_DESARROLLADO>
    }

    public void cambiarCantidadC(int rowNum) {
        // <CODIGO_DESARROLLADO>
        if (listaInicial.getDatasource().get(rowNum % 10).getCampos()
                        .get(cantidad) == null) {

            listaInicial.getDatasource().get(rowNum % 10).getCampos()
                            .put(cantidad, 0);
        }

        listaInicial.getDatasource().get(rowNum % 10).getCampos()
                        .put(valorTotal, valorTotal(rowNum));

        // </CODIGO_DESARROLLADO>
    }

    public void cambiarValorUnitario() {
        // <CODIGO_DESARROLLADO>
        if (registro.getCampos().get(valorUnitario) == null) {
            registro.getCampos().put(valorUnitario, 0);
        }

        registro.getCampos().put(valorTotal, valorTotal());
        // </CODIGO_DESARROLLADO>
    }

    public void cambiarValorUnitarioC(int rowNum) {
        // <CODIGO_DESARROLLADO>
        if (listaInicial.getDatasource().get(rowNum % 10).getCampos()
                        .get(valorUnitario) == null) {
            listaInicial.getDatasource().get(rowNum % 10).getCampos()
                            .put(valorUnitario, 0);
        }

        listaInicial.getDatasource().get(rowNum % 10).getCampos()
                        .put(valorTotal, valorTotal(rowNum));
        // </CODIGO_DESARROLLADO>
    }

    public void cambiarPorDescuentoC(int rowNum) {
        if (listaInicial.getDatasource().get(rowNum % 10).getCampos()
                        .get(porDescuento) == null) {
            listaInicial.getDatasource().get(rowNum % 10).getCampos()
                            .put(porDescuento, 0);
        }

        listaInicial.getDatasource().get(rowNum % 10).getCampos().put(
                        valorTotal, valorTotal(rowNum));

    }

    public void cambiarPorDescuento() {
        if (registro.getCampos().get(porDescuento) == null) {
            registro.getCampos().put(porDescuento, 0);
        }

        registro.getCampos().put(valorTotal, valorTotal());
    }

    public Double valorTotal(int rowNum) {

        Double iva = (Double.parseDouble(
                        listaInicial.getDatasource()
                                        .get(rowNum % 10).getCampos()
                                        .get(valorUnitario).toString())

            *
            Double.parseDouble(
                            listaInicial.getDatasource().get(rowNum % 10)
                                            .getCampos().get(porcIva)
                                            .toString()))
            / 100;
        Double valorConIva = Double.parseDouble(listaInicial.getDatasource()
                        .get(rowNum % 10).getCampos()
                        .get(valorUnitario).toString())
            + iva;
        Double valorUniDesc = valorConIva
            * (Double.parseDouble(listaInicial.getDatasource()
                            .get(rowNum
                                % 10)
                            .getCampos()
                            .get(porDescuento)
                            .toString())
                / 100);

        return Double.parseDouble(
                        listaInicial.getDatasource()
                                        .get(rowNum
                                            % 10)
                                        .getCampos()
                                        .get(cantidad)
                                        .toString())
            * (valorConIva - valorUniDesc);

    }

    public Double valorTotal() {

        Double iva = (Double.parseDouble(
                        registro.getCampos()
                                        .get(valorUnitario).toString())

            *
            Double.parseDouble(
                            registro
                                            .getCampos().get(porcIva)
                                            .toString()))
            / 100;
        Double valorConIva = Double.parseDouble(registro.getCampos()
                        .get(valorUnitario).toString())
            + iva;
        Double valorUniDesc = valorConIva
            * (Double.parseDouble(registro
                            .getCampos()
                            .get(porDescuento)
                            .toString())
                / 100);

        return Double.parseDouble(
                        registro
                                        .getCampos()
                                        .get(cantidad)
                                        .toString())
            * (valorConIva - valorUniDesc);
    }

    public void ejecutarrcCerrar() {
        // <CODIGO_DESARROLLADO>
        Map<String, Object> parametros = new HashMap<>();
        parametros.put("rid", rid);
        Direccionador direccionador = new Direccionador();
        direccionador.setParametros(parametros);
        direccionador.setNumForm(Integer
                        .toString(GeneralCodigoFormaEnum.PROPUESTAREQS_CONTROLADOR
                                        .getCodigo()));
        SessionUtil.redireccionarForma(direccionador, SessionUtil.getModulo());
        // </CODIGO_DESARROLLADO>
    }

    @Override
    public void reasignarOrigen() {

        parametrosListado.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
        parametrosListado.put(SubpropuestadetallesControladorEnum.PROPUESTA
                        .getValue(),
                        codPro);
        parametrosListado.put(SubpropuestadetallesControladorEnum.REQUISICION
                        .getValue(), codReq);

        urlListado = UrlServiceUtil.getUrlBeanById(
                        SubpropuestadetallesControladorUrlEnum.URL3294
                                        .getValue());
        urlActualizacion = UrlServiceUtil.getUrlBeanById(
                        SubpropuestadetallesControladorUrlEnum.URL4772
                                        .getValue());
    }

    @Override
    public void removerCombos() {
        // <CODIGO DESARROLLADO>

        // </CODIGO DESARROLLADO>
    }

    @Override
    public void asignarValoresRegistro() {
        // <CODIGO DESARROLLADO>

        // </CODIGO DESARROLLADO>
    }

    @Override
    public void cancelarEdicion(RowEditEvent event) {
        // <CODIGO DESARROLLADO>

        // </CODIGO DESARROLLADO>
    }
}
