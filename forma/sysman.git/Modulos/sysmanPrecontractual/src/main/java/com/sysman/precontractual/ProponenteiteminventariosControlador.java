package com.sysman.precontractual;

import com.sysman.beanbase.BeanBaseContinuoAcmeImpl;
import com.sysman.componentes.Direccionador;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.kernel.api.clientwso2.beans.Parameter;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.precontractual.enums.ProponenteiteminventariosControladorEnum;
import com.sysman.precontractual.enums.ProponenteiteminventariosControladorUrlEnum;
import com.sysman.services.RegistroDataModel;

import java.util.HashMap;
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
 * @author lcortes
 * @version 1, 23/03/2016
 *
 * @author lcortes
 * @version 2, 01,04,05/09/2017. Refactorizacion de codigo, reemplazo
 * de llamados a la clase Acciones y revision de observaciones de la
 * herramienta SonarLint
 * @modified lcortes. 21,22/09/2017. Cambio en el metodo
 * oprimirCotizaInventario para usar el metodo redireccionarForma en
 * lugar de cargarModalDatosFlash, se agregan parametros necesarios
 * para redireccionar al formulario Proponente etapa. Se modifica el
 * metodo actualizarValorProponente para cambiar el llamado de la Url.
 *
 * @modified lcortes 11/10/2017. Se ajusta el calculo del valor del
 * total en el metodo calcularValores.
 *
 */
@ManagedBean
@ViewScoped
public class ProponenteiteminventariosControlador
                extends BeanBaseContinuoAcmeImpl {

    private final String compania;
    private final String cIdElemento;
    private RegistroDataModel listaidItem;
    private RegistroDataModel listaidItemE;
    private String auxiliar;
    private String tituloEtapa;
    private String tituloProponente;
    private String tipoContrato;
    private String transaccion;
    private String consecutivo;
    private String proponente;
    private String nombreProponente;
    private String sucursal;
    private String idEtapa;
    private String nombreEtapa;
    private String totalValorUDi;
    private String totalValorIva;
    private String totalValorDesc;
    private String valorTotal;
    private String estadoEtapa;
    private String estadoProponente;
    private boolean cotizaInventario;
    private boolean modificar;
    private String redonValorUnitarioIVA;
    private String digRedoValorUnitarioIVA;

    private HashMap<String, Object> rid;
    private String anio;
    private String condicion;
    private String estadoVigencia;
    private String estadoProceso;
    private String desdeMonitor;

    /**
     * Creates a new instance of ProponenteiteminventariosControlador
     */
    public ProponenteiteminventariosControlador() {
        super();
        compania = SessionUtil.getCompania();
        cIdElemento = ProponenteiteminventariosControladorEnum.IDELEMENTO
                        .getValue();
        try {
            Map<String, Object> parametrosEntrada = SessionUtil.getFlash();
            if (parametrosEntrada != null) {
                tipoContrato = (String) parametrosEntrada.get("tipoContrato");
                transaccion = (String) parametrosEntrada
                                .get("consecutivoTransaccion");
                consecutivo = (String) parametrosEntrada
                                .get("consecutivoDetalle");
                proponente = (String) parametrosEntrada.get("proponente");
                nombreProponente = (String) parametrosEntrada
                                .get("nombreProponente");
                sucursal = (String) parametrosEntrada.get("sucursal");
                idEtapa = (String) parametrosEntrada.get("idEtapa");
                nombreEtapa = (String) parametrosEntrada.get("nombreEtapa");
                estadoEtapa = (String) parametrosEntrada.get("estadoEtapa");
                estadoProponente = (String) parametrosEntrada
                                .get("estadoProponente");
                redonValorUnitarioIVA = (String) parametrosEntrada
                                .get("redonValorUnitarioIVA");
                digRedoValorUnitarioIVA = (String) parametrosEntrada
                                .get("digRedoValorUnitarioIVA");
                tituloEtapa = "ETAPA " + idEtapa + "- "
                    + nombreEtapa.toUpperCase();
                StringBuilder titulo = new StringBuilder();
                titulo.append("PROPONENTE: ").append(proponente).append(" - ")
                                .append(nombreProponente);
                tituloProponente = titulo.toString();
                estadoEtapa = estadoEtapa == null ? "NEE" : estadoEtapa; // NEE
                                                                         // no
                                                                         // tiene
                                                                         // ninguna
                                                                         // equivalencia
                                                                         // para
                                                                         // el
                                                                         // estado
                estadoProponente = estadoProponente == null ? "NEP"
                    : estadoProponente; // NEP no tiene ninguna
                                        // equivalencia para el
                                        // estado
                modificar = Boolean.valueOf(
                                parametrosEntrada.get("modificar").toString());
                cotizaInventario = Boolean.valueOf(parametrosEntrada
                                .get("cotizaInventario").toString());
                rid = (HashMap<String, Object>) parametrosEntrada
                                .get("rid");
                anio = (String) parametrosEntrada.get("anio");
                condicion = (String) parametrosEntrada.get("condicion");
                estadoVigencia = (String) parametrosEntrada
                                .get("estadoVigencia");
                estadoProceso = (String) parametrosEntrada.get("estadoProceso");
                desdeMonitor = (String) parametrosEntrada.get("desdeMonitor");

            }
            else {
                SessionUtil.redireccionarMenu();
            }
            if (modificar) {
                if (("A").equals(estadoEtapa)
                    || !("RE").equals(estadoProponente)) {
                    modificar = true;
                }
                else {
                    modificar = false;
                }
            }

            numFormulario = GeneralCodigoFormaEnum.PROPONENTEITEMINVENTARIOS_CONTROLADOR
                            .getCodigo();
            validarPermisos();
        }
        catch (Exception ex) {
            Logger.getLogger(ProponenteiteminventariosControlador.class
                            .getName()).log(Level.SEVERE, null, ex);
            SessionUtil.redireccionarMenuPermisos();
        }
        finally {
            SessionUtil.cleanFlash();
        }
    }

    @PostConstruct
    public void init() {
        tabla = ProponenteiteminventariosControladorEnum.PROPONENTE_ITEMINVENTARIO
                        .getValue();
        buscarLlave();
        reasignarOrigen();
        registro = new Registro();
        abrirFormulario();
    }

    @Override
    public void reasignarOrigen() {

        parametrosListado.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
        parametrosListado
                        .put(ProponenteiteminventariosControladorEnum.TIPOCONTRATO
                                        .getValue(), tipoContrato);
        parametrosListado
                        .put(ProponenteiteminventariosControladorEnum.TRANSACCION
                                        .getValue(), transaccion);
        parametrosListado.put(GeneralParameterEnum.CONSECUTIVO.getName(),
                        consecutivo);
        parametrosListado
                        .put(ProponenteiteminventariosControladorEnum.PROPONENTE
                                        .getValue(), proponente);

        urlListado = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        ProponenteiteminventariosControladorUrlEnum.URL8648
                                                        .getValue());

        urlActualizacion = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        ProponenteiteminventariosControladorUrlEnum.URL9602
                                                        .getValue());
    }

    public void onRowSelectidItem(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        registro.getCampos().put(cIdElemento,
                        registroAux.getCampos().get(cIdElemento));
    }

    public void onRowSelectidItemE(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        auxiliar = registroAux.getCampos().get(cIdElemento).toString();
    }

    public void calcularTotales() {
        try {
            totalValorUDi = "";
            totalValorIva = "";
            totalValorDesc = "";
            valorTotal = "";

            Map<String, Object> param = new TreeMap<>();
            param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
            param.put(ProponenteiteminventariosControladorEnum.TIPOCONTRATO
                            .getValue(), tipoContrato);
            param.put(GeneralParameterEnum.CONSECUTIVO.getName(), consecutivo);
            param.put(ProponenteiteminventariosControladorEnum.PROPONENTE
                            .getValue(), proponente);
            Registro regAux = RegistroConverter.toRegistro(
                            requestManager.get(
                                            UrlServiceUtil.getInstance()
                                                            .getUrlServiceByUrlByEnumID(
                                                                            ProponenteiteminventariosControladorUrlEnum.URL12833
                                                                                            .getValue())
                                                            .getUrl(),
                                            param));

            totalValorUDi = regAux.getCampos()
                            .get(ProponenteiteminventariosControladorEnum.VALUNITARIODITOTAL
                                            .getValue())
                            .toString();
            totalValorIva = regAux.getCampos()
                            .get(ProponenteiteminventariosControladorEnum.VALIVATOTAL
                                            .getValue())
                            .toString();
            totalValorDesc = regAux.getCampos()
                            .get(ProponenteiteminventariosControladorEnum.VALDESCTOTAL
                                            .getValue())
                            .toString();
            valorTotal = regAux.getCampos()
                            .get(ProponenteiteminventariosControladorEnum.VALTOTAL
                                            .getValue())
                            .toString();
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    public void actualizarValorProponente() {
        UrlBean urlUpdate = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        ProponenteiteminventariosControladorUrlEnum.URL17652
                                                        .getValue());

        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(ProponenteiteminventariosControladorEnum.TIPOCONTRATO
                        .getValue(), tipoContrato);
        param.put(ProponenteiteminventariosControladorEnum.TRANSACCION
                        .getValue(), transaccion);
        param.put(ProponenteiteminventariosControladorEnum.PROPONENTE
                        .getValue(), proponente);
        param.put(ProponenteiteminventariosControladorEnum.VLRTOTAL.getValue(),
                        registro.getCampos()
                                        .get(ProponenteiteminventariosControladorEnum.VLRTOTAL
                                                        .getValue()));

        Parameter parameter = new Parameter();
        parameter.setFields(param);

        try {
            requestManager.update(urlUpdate.getUrl(),
                            urlUpdate.getMetodo(),
                            parameter);
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    public void calcularValores() {
        Double cantidad = Double.parseDouble(registro.getCampos()
                        .get(GeneralParameterEnum.CANTIDAD.getName())
                        .toString());
        Double valorUnitario = Double
                        .parseDouble(registro.getCampos()
                                        .get(ProponenteiteminventariosControladorEnum.VALORUNITARIO
                                                        .getValue())
                                        .toString());
        Double porcDesc = Double
                        .parseDouble(registro.getCampos()
                                        .get(ProponenteiteminventariosControladorEnum.PORCDESC
                                                        .getValue())
                                        .toString());
        Double porcIva = Double
                        .parseDouble(registro.getCampos()
                                        .get(ProponenteiteminventariosControladorEnum.PORCIVA
                                                        .getValue())
                                        .toString());
        Double total;
        Double descuento;
        Double iva;
        Double valorUnitDI;
        total = valorUnitario * cantidad;
        descuento = (total * porcDesc) / 100;
        descuento = Math.rint(descuento * 100) / 100;
        iva = valorUnitario * (porcIva / 100);
        iva = Math.rint(iva * 100) / 100;
        iva = iva * cantidad;
        total = (total + iva) - descuento;
        if (cantidad > 0) {
            if (("SI").equalsIgnoreCase(redonValorUnitarioIVA)) {
                Double digitos = Math.pow(10,
                                Double.parseDouble(digRedoValorUnitarioIVA));
                valorUnitDI = total / cantidad;
                valorUnitDI = Math.rint(valorUnitDI * digitos) / digitos;

            }
            else {
                valorUnitDI = total / cantidad;
            }
        }
        else {
            valorUnitDI = 0.0;
        }
        registro.getCampos()
                        .put(ProponenteiteminventariosControladorEnum.VALORUNITARIODI
                                        .getValue(),
                                        String.valueOf(valorUnitDI));
        registro.getCampos().put(ProponenteiteminventariosControladorEnum.VLRIVA
                        .getValue(), String.valueOf(iva));
        registro.getCampos()
                        .put(ProponenteiteminventariosControladorEnum.VLRDESCUENTO
                                        .getValue(), String.valueOf(descuento));
        registro.getCampos()
                        .put(ProponenteiteminventariosControladorEnum.VLRTOTAL
                                        .getValue(), String.valueOf(total));

    }

    @Override
    public void abrirFormulario() {
        try {

            UrlBean urlCreate = UrlServiceUtil.getInstance()
                            .getUrlServiceByUrlByEnumID(
                                            ProponenteiteminventariosControladorUrlEnum.URL17653
                                                            .getValue());

            Map<String, Object> param = new TreeMap<>();
            param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
            param.put(ProponenteiteminventariosControladorEnum.TIPOCONTRATO
                            .getValue(), tipoContrato);
            param.put(ProponenteiteminventariosControladorEnum.TRANSACCION
                            .getValue(), transaccion);
            param.put(ProponenteiteminventariosControladorEnum.PROPONENTE
                            .getValue(), proponente);
            param.put(GeneralParameterEnum.CONSECUTIVO.getName(), consecutivo);
            param.put(GeneralParameterEnum.SUCURSAL.getName(), sucursal);

            int res = requestManager.saveCount(urlCreate.getUrl(),
                            urlCreate.getMetodo(), param);
            if (res != 0) {
                reasignarOrigen();
                calcularTotales();
            }
            else {

                calcularTotales();
            }
        }
        catch (SystemException e) {
            Logger.getLogger(ProponenteiteminventariosControlador.class
                            .getName()).log(Level.SEVERE, null, e);

            JsfUtil.agregarMensajeError(e.getMessage());

        }
        // <CODIGO_DESARROLLADO>
        /*
         * FR589-AL_ABRIR Private Sub Form_Open(Cancel As Integer)
         * DoCmd.Maximize 'Tener encuenta el estado del proponente
         * AC;Aceptado;RE;Rechazado;EV;En Evaluacion
         * BloqueaFormPorEtapa Me, True If BlnBloqueTrans Then
         * Me.AllowAdditions = False Me.AllowDeletions = False
         * Me.AllowEdits = False End If End Sub
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
        registro.getCampos().put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
        registro.getCampos()
                        .put(ProponenteiteminventariosControladorEnum.TIPOCONTRATO
                                        .getValue(), tipoContrato);
        registro.getCampos()
                        .put(ProponenteiteminventariosControladorEnum.TRANSACCION
                                        .getValue(), transaccion);
        registro.getCampos()
                        .put(ProponenteiteminventariosControladorEnum.CONSECUTIVODETALLE
                                        .getValue(), consecutivo);
        registro.getCampos()
                        .put(ProponenteiteminventariosControladorEnum.PROPONENTE
                                        .getValue(), proponente);
        registro.getCampos().put(GeneralParameterEnum.SUCURSAL.getName(),
                        sucursal);
        calcularValores();
        // </CODIGO_DESARROLLADO>
        return true;
    }

    @Override
    public boolean insertarDespues() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
        actualizarValorProponente();
        calcularTotales();
        return true;
    }

    @Override
    public boolean actualizarAntes() {
        calcularValores();
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
        return true;
    }

    @Override
    public boolean actualizarDespues() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
        actualizarValorProponente();
        calcularTotales();
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
        calcularTotales();
        return true;
    }

    @Override
    public void removerCombos() {
        // <CODIGO_DESARROLLADO>
        registro.getCampos().remove(GeneralParameterEnum.COMPANIA.getName());
        registro.getCampos()
                        .remove(ProponenteiteminventariosControladorEnum.TIPOCONTRATO
                                        .getValue());
        registro.getCampos()
                        .remove(ProponenteiteminventariosControladorEnum.TRANSACCION
                                        .getValue());
        registro.getCampos()
                        .remove(ProponenteiteminventariosControladorEnum.CONSECUTIVODETALLE
                                        .getValue());
        registro.getCampos()
                        .remove(ProponenteiteminventariosControladorEnum.PROPONENTE
                                        .getValue());
        registro.getCampos().remove(GeneralParameterEnum.SUCURSAL.getName());
        registro.getCampos().remove(cIdElemento);
        registro.getCampos().remove(GeneralParameterEnum.DESCRIPCION.getName());
        // </CODIGO_DESARROLLADO>
    }

    public void ejecutarrcCerrar() {

        Map<String, Object> param = new HashMap<>();
        param.put("tipoContrato", tipoContrato);
        param.put("consecutivoTransaccion", transaccion);
        param.put("consecutivoDetalle", consecutivo);
        param.put("idEtapa", idEtapa);
        param.put("nombreEtapa", nombreEtapa);
        param.put("estadoEtapa", estadoEtapa);
        param.put("cotizaInventario", cotizaInventario ? "true" : "false");
        param.put("rid", rid);
        param.put("anio", anio);
        param.put("condicion", condicion);
        param.put("estadoVigencia", estadoVigencia);
        param.put("estadoProceso", estadoProceso);
        param.put("desdeMonitor", desdeMonitor);

        Direccionador dir = new Direccionador();
        dir.setParametros(param);
        dir.setNumForm(Integer
                        .toString(GeneralCodigoFormaEnum.PROPONENTEETAPAS_CONTROLADOR
                                        .getCodigo()));

        SessionUtil.redireccionarForma(dir, SessionUtil.getModulo());
    }

    @Override
    public void asignarValoresRegistro() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>

    }

    public RegistroDataModel getListaidItem() {
        return listaidItem;
    }

    public void setListaidItem(RegistroDataModel listaidItem) {
        this.listaidItem = listaidItem;
    }

    public RegistroDataModel getListaidItemE() {
        return listaidItemE;
    }

    public void setListaidItemE(RegistroDataModel listaidItemE) {
        this.listaidItemE = listaidItemE;
    }

    public String getAuxiliar() {
        return auxiliar;
    }

    public void setAuxiliar(String auxiliar) {
        this.auxiliar = auxiliar;
    }

    public String getTituloEtapa() {
        return tituloEtapa;
    }

    public void setTituloEtapa(String tituloEtapa) {
        this.tituloEtapa = tituloEtapa;
    }

    public String getTituloProponente() {
        return tituloProponente;
    }

    public void setTituloProponente(String tituloProponente) {
        this.tituloProponente = tituloProponente;
    }

    public String getTotalValorUDi() {
        return totalValorUDi;
    }

    public void setTotalValorUDi(String totalValorUDi) {
        this.totalValorUDi = totalValorUDi;
    }

    public String getTotalValorIva() {
        return totalValorIva;
    }

    public void setTotalValorIva(String totalValorIva) {
        this.totalValorIva = totalValorIva;
    }

    public String getTotalValorDesc() {
        return totalValorDesc;
    }

    public void setTotalValorDesc(String totalValorDesc) {
        this.totalValorDesc = totalValorDesc;
    }

    public String getValorTotal() {
        return valorTotal;
    }

    public void setValorTotal(String valorTotal) {
        this.valorTotal = valorTotal;
    }

    public boolean isModificar() {
        return modificar;
    }

    public void setModificar(boolean modificar) {
        this.modificar = modificar;
    }

}
