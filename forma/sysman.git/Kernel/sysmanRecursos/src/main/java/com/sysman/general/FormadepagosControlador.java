package com.sysman.general;

import com.sysman.beanbase.BeanBaseContinuoAcmeImpl;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.enums.GenericUrlEnum;
import com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException;
import com.sysman.exception.SystemException;
import com.sysman.general.enums.FormadepagosControladorEnum;
import com.sysman.general.enums.FormadepagosControladorUrlEnum;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.kernel.api.clientwso2.beans.Parameter;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.recursos.ejb.impl.EjbSysmanUtil;
import com.sysman.util.SysmanFunciones;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
 * @author jrodriguezr
 * @version 1, 09/12/2015
 * 
 * @author jsforero
 * @version 2, 05/04/2017 Se realizo el refactory. Ademas se hicieron
 * las respectivas Correcciones del sonar.
 * 
 * @author jrodrigueza
 * @version 3, Validación de parametros nulos al cargar totales.
 * 
 * @author spina - refactorizo conexiones
 * @version 4, 12/06/2017
 */
@ManagedBean
@ViewScoped
public class FormadepagosControlador extends BeanBaseContinuoAcmeImpl {

    private final String compania;
    private final String codigoCons;
    private final String descripcionCons;
    private final String porcentajeCons;
    private final String valorCons;
    private final String fechaPlazaCons;

    private boolean muestraDialogo = false;
    private String tipoContrato;
    private String numeroContrato;
    private String valorTotalContrato;
    private String totalPorcentaje;
    private String valorTotal;
    private boolean visibleActualizarContrato;
    private String tituloCodigo;
    private String tituloValor;
    private Long consecutivo;
    private Double porcentaje;
    private Double valor;
    private Double porcentajeAnterior;
    private Double porcentajeEditado;
    private Double porcentajeAcumulado;
    private Double porcentajeTotal;

    /**
     * Atributo auxliar el cual es asiganado en el momento que se
     * activa la edicion de un registro. Toma el valor del indice
     * dentro de la grilla del registro seleccionado para editar
     */
    private int indice;

    @EJB
    private EjbSysmanUtil ejbSysmanUtil;

    /**
     * Creates a new instance of FormadepagosControlador
     */
    public FormadepagosControlador() {
        super();
        compania = SessionUtil.getCompania();
        codigoCons = "CODIGO";
        descripcionCons = "DESCRIPCION";
        porcentajeCons = "PORCENTAJE";
        valorCons = "VALOR";
        fechaPlazaCons = "FECHAPLAZO";
        try {
            visibleActualizarContrato = !"90203".equals(SessionUtil.getMenu());
            numFormulario = GeneralCodigoFormaEnum.FORMADEPAGOS_CONTROLADOR
                            .getCodigo();
            cargarFlash();
            validarPermisos();
        }
        catch (SysmanException ex) {
            logger.error(ex.getMessage(), ex);
            SessionUtil.redireccionarMenuPermisos();
        }
        finally {
            SessionUtil.getFlash().clear();
        }
    }

    private void cargarFlash() {
        Map<String, Object> parametrosEntrada = SessionUtil.getFlash();
        if (parametrosEntrada != null) {
            if (SessionUtil.getModulo().equals("19")) {
                valorTotalContrato = parametrosEntrada
                                .get("valorTotalEstudio").toString();
                numeroContrato = parametrosEntrada.get("codigoEstudio")
                                .toString();
            }

            else {
                tipoContrato = parametrosEntrada.get("tipoContrato").toString();
                valorTotalContrato = parametrosEntrada
                                .get("valorTotalContrato").toString();
                numeroContrato = parametrosEntrada.get("numeroContrato")
                                .toString();
            }
        }
    }

    @PostConstruct
    public void inicializar() {

        if (SessionUtil.getModulo().equals("19")) {
            enumBase = GenericUrlEnum.ES_PROGPAGOS_ESTPREVIO;
            visibleActualizarContrato = false;
            tituloCodigo = idioma.getString("TB_TB4177");
            tituloValor = idioma.getString("TB_TB4176");
        }

        else {
            enumBase = GenericUrlEnum.PAGOPROGRAMADO;
            tituloCodigo = idioma.getString("TG_VALOR_DEL_CONTRATO");
            tituloValor = idioma.getString("TB_TB4175");
        }

        buscarLlave();
        reasignarOrigen();
        registro = new Registro(new HashMap<String, Object>());

        cargarTotales();
        abrirFormulario();
    }

    public void cargarTotales() {
        String urlEnumId = null;
        Map<String, Object> parametros = new HashMap<>();
        parametros.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        parametros.put(FormadepagosControladorEnum.NUMEROCONTRATO
                        .getValue(),
                        numeroContrato);

        if (!"19".equals(SessionUtil.getModulo())) {
            parametros.put(GeneralParameterEnum.TIPOCONTRATO.getName(),
                            tipoContrato);
        }

        HashMap<String, Object> valorTotalReg = new HashMap<>();

        try {
            if ("19".equals(SessionUtil.getModulo())) {
                urlEnumId = FormadepagosControladorUrlEnum.URL001
                                .getValue();
            }

            else {
                urlEnumId = FormadepagosControladorUrlEnum.URL7918
                                .getValue();
            }
            UrlBean urlReg = UrlServiceUtil.getInstance()
                            .getUrlServiceByUrlByEnumID(urlEnumId);
            Parameter parameter = requestManager.get(urlReg.getUrl(),
                            parametros);
            if (parameter != null) {
                valorTotalReg = (HashMap<String, Object>) parameter
                                .getFields();
            }
        }
        catch (SystemException e1) {
            logger.error(e1.getMessage(), e1);
            JsfUtil.agregarMensajeError(e1.getMessage());
        }

        if (valorTotalReg.get("VALORT") == null
            || valorTotalReg.isEmpty()) {
            valorTotal = "0";
            totalPorcentaje = "0";
        }

        else {
            valorTotal = valorTotalReg.get("VALORT").toString();
            DecimalFormat df = new DecimalFormat("#,###.00");
            valorTotal = df.format(Double.parseDouble(valorTotal));
            totalPorcentaje = valorTotalReg.get("PORC").toString();
            totalPorcentaje = String
                            .valueOf(Double.parseDouble(totalPorcentaje))
                + "%";
        }

    }

    public boolean validarTotales() {
        String urlEnumId = null;
        Map<String, Object> parametros = new HashMap<>();
        parametros.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);

        parametros.put(FormadepagosControladorEnum.NUMEROCONTRATO
                        .getValue(),
                        numeroContrato);

        if (!"19".equals(SessionUtil.getModulo())) {
            parametros.put(GeneralParameterEnum.TIPOCONTRATO.getName(),
                            tipoContrato);
        }

        HashMap<String, Object> valorTotalReg = new HashMap<>();

        try {
            if ("19".equals(SessionUtil.getModulo())) {
                urlEnumId = FormadepagosControladorUrlEnum.URL001
                                .getValue();
            }

            else {
                urlEnumId = FormadepagosControladorUrlEnum.URL7918
                                .getValue();
            }
            UrlBean urlReg = UrlServiceUtil.getInstance()
                            .getUrlServiceByUrlByEnumID(urlEnumId);
            Parameter parameter = requestManager.get(urlReg.getUrl(),
                            parametros);
            if (parameter != null) {
                valorTotalReg = (HashMap<String, Object>) parameter
                                .getFields();
            }
        }
        catch (SystemException e1) {
            logger.error(e1.getMessage(), e1);
            JsfUtil.agregarMensajeError(e1.getMessage());
        }

        if (valorTotalReg.get("VALORT") == null
            || valorTotalReg.isEmpty()) {
            valorTotal = "0";
            totalPorcentaje = "0";
        }

        porcentajeTotal = Double
                        .parseDouble(valorTotalReg.get("PORC").toString());
        porcentajeEditado = Double.parseDouble(registro.getCampos()
                        .get(porcentajeCons).toString());

        porcentajeAcumulado = porcentajeTotal - porcentajeAnterior
            + porcentajeEditado;

        if (porcentajeAcumulado > 100) {
            JsfUtil.agregarMensajeAlerta(
                            "El valor no puede exceder el valor del estudio previo");
            return false;
        }

        else {
            valorTotal = valorTotalReg.get("VALORT").toString();
            DecimalFormat df = new DecimalFormat("#,###.00");
            valorTotal = df.format(Double.parseDouble(valorTotal));
            totalPorcentaje = valorTotalReg.get("PORC").toString();
            totalPorcentaje = String
                            .valueOf(Double.parseDouble(totalPorcentaje))
                + "%";

            return true;
        }

    }

    public void oprimirActualizaContrato() {
        // <CODIGO_DESARROLLADO>
        muestraDialogo = true;
        // </CODIGO_DESARROLLADO>
    }

    public void aceptarConfirmaActualizar() {
        // <CODIGO_DESARROLLADO>
        StringBuilder cadena = new StringBuilder();

        Map<String, Object> parametros = new HashMap<>();
        parametros.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        parametros.put(GeneralParameterEnum.TIPOCONTRATO.getName(),
                        tipoContrato);
        parametros.put(FormadepagosControladorEnum.NUMEROCONTRATO.getValue(),
                        numeroContrato);

        UrlBean urlList = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        FormadepagosControladorUrlEnum.URL6228
                                                        .getValue());

        List<Registro> registroDetalle;
        try {
            registroDetalle = RegistroConverter.toListRegistro(requestManager
                            .getList(urlList.getUrl(), parametros));

            for (int i = 0; i < registroDetalle.size(); i++) {
                String codigo = (registroDetalle.get(i).getCampos()
                                .get(codigoCons)).toString();
                String descripcion = registroDetalle.get(i).getCampos()
                                .get(descripcionCons).toString();
                descripcion = SysmanFunciones.validarVariableVacio(descripcion)
                    ? "ND"
                    : descripcion;
                String porcentaje = (registroDetalle.get(i).getCampos()
                                .get(porcentajeCons))
                                                .toString();
                String valor = (registroDetalle.get(i).getCampos()
                                .get(valorCons)).toString();
                String fechaPlazo = (registroDetalle.get(i).getCampos()
                                .get(fechaPlazaCons))
                                                .toString();

                cadena = cadena.append("Pago N°").append(codigo).append(" _ ")
                                .append(descripcion).append(" _ ")
                                .append("Equivale al ").append(porcentaje)
                                .append("% _ Por un valor de ").append(valor)
                                .append(" _ ").append("Para pagar en la fecha ")
                                .append(fechaPlazo).append("----------");
            }
        }
        catch (SystemException ex) {
            Logger.getLogger(FormadepagosControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            JsfUtil.agregarMensajeError(
                            idioma.getString("MSM_TRANS_INTERRUMPIDA")
                                + ex.getMessage());
        }

        if ("".equals(cadena.toString())) {
            return;
        }

        try {

            HashMap<String, Object> param = new HashMap<>();
            param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
            param.put(GeneralParameterEnum.TIPOCONTRATO.getName(),
                            tipoContrato);
            param.put(FormadepagosControladorEnum.NUMEROCONTRATO.getValue(),
                            numeroContrato);
            param.put(GeneralParameterEnum.FORMA_PAGO.name(),
                            cadena.toString());
            Parameter parameter = new Parameter();

            parameter.setFields(param);
            UrlBean urlUpdate = UrlServiceUtil.getInstance()
                            .getUrlServiceByUrlByEnumID(
                                            FormadepagosControladorUrlEnum.URL12241
                                                            .getValue());
            requestManager.update(urlUpdate.getUrl(), urlUpdate.getMetodo(),
                            parameter);

        }
        catch (SystemException ex) {
            Logger.getLogger(FormadepagosControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
        }
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado al cambiar el control PORCENTAJE
     * 
     * 
     */
    public void cambiarPORCENTAJE() {
        // <CODIGO_DESARROLLADO>
        porcentaje = Double.parseDouble(
                        registro.getCampos().get(porcentajeCons).toString());

        porcentaje = porcentaje == null ? 0
            : porcentaje;

        valor = (Double.parseDouble(valorTotalContrato) *
            porcentaje) / 100;

        registro.getCampos().put("VALOR", valor);

        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado al cambiar el control PORCENTAJE en la fila
     * seleccionada dentro de la grilla
     * 
     * @param rowNum
     * indice de la fila seleccionada
     */
    public void cambiarPORCENTAJEC(int rowNum) {
        // <CODIGO_DESARROLLADO>
        porcentaje = Double
                        .parseDouble(listaInicial.getDatasource().get(rowNum %
                            10).getCampos().get(porcentajeCons).toString());

        valor = (Double.parseDouble(valorTotalContrato) *
            porcentaje) / 100;

        listaInicial.getDatasource().get(rowNum %
            10).getCampos().put("VALOR", valor);

        // </CODIGO_DESARROLLADO>
    }
    // </METODOS_CAMBIAR>

    @Override
    public void abrirFormulario() {

        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    @Override
    public boolean insertarAntes() {
        // <CODIGO_DESARROLLADO>

        porcentajeAnterior = 0.0;

        if (SessionUtil.getModulo().equals("19")) {
            registro.getCampos().put(GeneralParameterEnum.COMPANIA.name(),
                            compania);
            registro.getCampos().put(
                            "CODIGO_ESTPREVIO",
                            numeroContrato);
            registro.getCampos().put("CODIGO", codigoDePago());
            registro.getCampos().put("VALOR_ESTPREVIO", valorTotalContrato);

        }

        else {
            registro.getCampos().put(codigoCons, codigoDePago());
            registro.getCampos().put(GeneralParameterEnum.COMPANIA.name(),
                            compania);
            registro.getCampos().put(
                            FormadepagosControladorEnum.ORDENDECOMPRA
                                            .getValue(),
                            numeroContrato);
            registro.getCampos().put(GeneralParameterEnum.CLASEORDEN.name(),
                            tipoContrato);
            registro.getCampos().put(
                            FormadepagosControladorEnum.VALORCONTRATO
                                            .getValue(),
                            valorTotalContrato);
        }

        // </CODIGO_DESARROLLADO>
        return validarTotales();
    }

    @Override
    public boolean actualizarAntes() {
        // <CODIGO_DESARROLLADO>
        if ((registro.getCampos().get(descripcionCons) == null)
            || (registro.getCampos().get(porcentajeCons) == null)
            || (registro.getCampos().get(valorCons) == null)) {
            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB201"));
            return false;
        }

        // </CODIGO_DESARROLLADO>
        return validarTotales();
    }

    @Override
    public boolean actualizarDespues() {
        // <CODIGO_DESARROLLADO>
        cargarTotales();
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

        if (!SessionUtil.getModulo().equals("19")) {
            registro.getCampos().remove(GeneralParameterEnum.COMPANIA.name());
            registro.getCampos()
                            .remove(GeneralParameterEnum.DATE_CREATED.name());
            registro.getCampos().remove(GeneralParameterEnum.CREATED_BY.name());
            registro.getCampos().remove(
                            FormadepagosControladorEnum.ORDENDECOMPRA
                                            .getValue());
            registro.getCampos().remove(GeneralParameterEnum.FECHA.name());
            registro.getCampos().remove(
                            FormadepagosControladorEnum.VALORCONTRATO
                                            .getValue());
            registro.getCampos().remove(GeneralParameterEnum.CLASEORDEN.name());
            registro.getCampos().remove(GeneralParameterEnum.CODIGO.name());

        }

        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado cuando se activa la edicion de un registro del
     * formulario
     *
     * @param registro
     * registro del cual se activo la edicion
     */
    public void activarEdicion(Registro registro) {
        setIndice(listaInicial.getRowIndex());

        porcentajeAnterior = Double
                        .parseDouble(registro.getCampos().get(porcentajeCons)
                                        .toString());
    }

    @Override
    public void reasignarOrigen() {

        buscarUrls();

        parametrosListado.put(GeneralParameterEnum.COMPANIA.name(), compania);
        parametrosListado.put(
                        FormadepagosControladorEnum.NUMEROCONTRATO.getValue(),
                        numeroContrato);

        if (!SessionUtil.getModulo().equals("19")) {
            parametrosListado.put(GeneralParameterEnum.TIPOCONTRATO.name(),
                            tipoContrato);
        }

    }

    @Override
    public void asignarValoresRegistro() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    @Override
    public void cancelarEdicion(RowEditEvent event) {
        listaInicial.load();
    }

    @Override
    public boolean insertarDespues() {
        // <CODIGO_DESARROLLADO>
        cargarTotales();

        // </CODIGO_DESARROLLADO>
        return true;
    }

    public long codigoDePago() {
        long codigodepago;
        Map<String, Object> parametros = new HashMap<>();
        if (!"19".equals(SessionUtil.getModulo())) {
            parametros.put(GeneralParameterEnum.TIPOCONTRATO.getName(),
                            tipoContrato);
        }

        parametros.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        parametros.put(FormadepagosControladorEnum.NUMEROCONTRATO.getValue(),
                        numeroContrato);

        if ("19".equals(SessionUtil.getModulo())) {
            try {
                consecutivo = ejbSysmanUtil.generarSiguienteConsecutivo(
                                "ES_PROGPAGOS_ESTPREVIO",
                                SysmanFunciones.concatenar(" COMPANIA = ''",
                                                compania,
                                                "'' AND CODIGO_ESTPREVIO = ''",
                                                numeroContrato, "''  "),
                                "CODIGO");

            }
            catch (SystemException e) {
                logger.error(e.getMessage(), e);
                JsfUtil.agregarMensajeError(e.getMessage());
            }

            return consecutivo;
        }

        else {

            HashMap<String, Object> codigoMax = new HashMap<>();

            try {

                UrlBean urlReg = UrlServiceUtil.getInstance()
                                .getUrlServiceByUrlByEnumID(
                                                FormadepagosControladorUrlEnum.URL18625
                                                                .getValue());
                codigoMax = (HashMap<String, Object>) requestManager
                                .get(urlReg.getUrl(), parametros).getFields();
            }
            catch (SystemException e1) {
                logger.error(e1.getMessage(), e1);
                JsfUtil.agregarMensajeError(e1.getMessage());
            }
            if (codigoMax.get("MAXIMO") != null) {
                codigodepago = Long
                                .parseLong(codigoMax.get("MAXIMO").toString())
                    + 1;
            }
            else {
                codigodepago = 1;
            }
            return codigodepago;
        }
    }

    public void cerrarFormulario() {
        RequestContext.getCurrentInstance().closeDialog(null);
    }

    public boolean isVisibleActualizarContrato() {
        return visibleActualizarContrato;
    }

    public void setVisibleActualizarContrato(
        boolean visibleActualizarContrato) {
        this.visibleActualizarContrato = visibleActualizarContrato;
    }

    public boolean isMuestraDialogo() {
        return muestraDialogo;
    }

    public void setMuestraDialogo(boolean muestraDialogo) {
        this.muestraDialogo = muestraDialogo;
    }

    public String getTipoContrato() {
        return tipoContrato;
    }

    public void setTipoContrato(String tipoContrato) {
        this.tipoContrato = tipoContrato;
    }

    public String getNumeroContrato() {
        return numeroContrato;
    }

    public void setNumeroContrato(String numeroContrato) {
        this.numeroContrato = numeroContrato;
    }

    public String getValorTotalContrato() {
        return valorTotalContrato;
    }

    public void setValorTotalContrato(String valorTotalContrato) {
        this.valorTotalContrato = valorTotalContrato;
    }

    public String getTotalPorcentaje() {
        return totalPorcentaje;
    }

    public void setTotalPorcentaje(String totalPorcentaje) {
        this.totalPorcentaje = totalPorcentaje;
    }

    public String getValorTotal() {
        return valorTotal;
    }

    public void setValorTotal(String valorTotal) {
        this.valorTotal = valorTotal;
    }

    /**
     * @return the tituloCodigo
     */
    public String getTituloCodigo() {
        return tituloCodigo;
    }

    /**
     * @param tituloCodigo
     * the tituloCodigo to set
     */
    public void setTituloCodigo(String tituloCodigo) {
        this.tituloCodigo = tituloCodigo;
    }

    /**
     * @return the tituloValor
     */
    public String getTituloValor() {
        return tituloValor;
    }

    /**
     * @param tituloValor
     * the tituloValor to set
     */
    public void setTituloValor(String tituloValor) {
        this.tituloValor = tituloValor;
    }

    /**
     * @return the indice
     */
    public int getIndice() {
        return indice;
    }

    /**
     * @param indice
     * the indice to set
     */
    public void setIndice(int indice) {
        this.indice = indice;
    }

}
