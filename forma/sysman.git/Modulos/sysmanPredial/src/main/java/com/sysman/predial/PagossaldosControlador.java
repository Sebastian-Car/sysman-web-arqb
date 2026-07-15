package com.sysman.predial;

import com.sysman.beanbase.BeanBaseContinuoAcmeImpl;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.jsfutil.ReportesBean;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.persistencia.ConectorPool;
import com.sysman.predial.enums.PagossaldosControladorEnum;
import com.sysman.predial.enums.PagossaldosControladorUrlEnum;
import com.sysman.reportes.Reporteador;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.util.SysmanConstantes;
import com.sysman.util.SysmanFunciones;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;
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
import org.primefaces.model.StreamedContent;

import net.sf.jasperreports.engine.JRException;

/**
 *
 * @author NGOMEZ
 * @version 1, 20/05/2016
 *
 * @version 2, 10/07/2017
 * @author jreina se realizaron los cambios de refactoring en cada uno de los combos y en el origen de grilla.
 *
 * * @version 2, 31/07/2017
 * @author sdaza se elimina el envio de parametro USUARIO los DSS debido a que el campo se elimino de la tabla
 */

@ManagedBean
@ViewScoped
public class PagossaldosControlador extends BeanBaseContinuoAcmeImpl {
    private final String compania;
    private final String nOrden;
    private final String cCodigo;
    private final String cPreAno;
    private final String cPagoAnoAux;
    private final String cNumeroOrden;
    private final String cDescuento;
    private final String cPago;
    private final String cBanco;
    private final String cConcepto;
    private final String cPropietario;
    private final String cFecha;
    private final String cPagoAno;
    private final String cPreCod;
    private final String cFalse;

    private int indice;

    private boolean accion;
    // <DECLARAR_ATRIBUTOS>
    private StreamedContent archivoDescarga;
    private boolean conceptoBloqueado;
    private String propietarioAux;
    private String nOrdenAux;
    private String pagoAnioAux;
    private int anioAux;
    private boolean cuadroVisible;
    private String tituloCuadro;
    // </DECLARAR_ATRIBUTOS>
    // <DECLARAR_PARAMETROS>
    // </DECLARAR_PARAMETROS>
    // <DECLARAR_LISTAS>
    private List<Registro> listaTxtBanco;
    private List<Registro> listaPreAno;
    private List<Registro> listaCmbConcepto;
    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>
    private RegistroDataModelImpl listaPrecod;
    private RegistroDataModelImpl listaPrecodE;
    private String auxiliar;
    // </DECLARAR_LISTAS_COMBO_GRANDE>

    /**
     * Creates a new instance of PagossaldosControlador
     */
    public PagossaldosControlador() {
        super();
        compania = SessionUtil.getCompania();
        nOrden = SysmanConstantes.NUMERO_ORDEN_PREDIAL;
        cCodigo = "CODIGO";
        cPreAno = "PREANO";
        cPagoAnoAux = "PAGO_ANO_AUX";
        cNumeroOrden = "NUMERO_ORDEN";
        cDescuento = "DESCUENTO";
        cPago = "PAGO";
        cBanco = "BANCO";
        cConcepto = "CONCEPTO";
        cPropietario = "PROPIETARIO";
        cFecha = "FECHA";
        cPagoAno = "PAGO_ANO";
        cPreCod = "PRECOD";
        cFalse = "false";
        accion = false;
        try {
            numFormulario = GeneralCodigoFormaEnum.PAGOSSALDOS_CONTROLADOR
                            .getCodigo();

            validarPermisos();
            // <INI_ADICIONAL>
            // </INI_ADICIONAL>
        }
        catch (Exception ex) {
            Logger.getLogger(PagossaldosControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            SessionUtil.redireccionarMenuPermisos();
        }
    }

    @PostConstruct
    public void inicializar() {
        tabla = PagossaldosControladorEnum.TABLA.getValue();
        buscarLlave();
        reasignarOrigen();
        registro = new Registro();
        anioAux = SysmanFunciones.getParteFecha(
                        new Date(),
                        Calendar.YEAR);
        // <CARGAR_LISTA>
        cargarListaTxtBanco();
        cargarListaPreAno();
        cargarListaCmbConcepto();
        // </CARGAR_LISTA>
        // <CARGAR_LISTA_COMBO_GRANDE>
        cargarListaPrecod();
        cargarListaPrecodE();
        // </CARGAR_LISTA_COMBO_GRANDE>
        abrirFormulario();

    }

    @Override
    public void reasignarOrigen() {
        urlActualizacion = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        PagossaldosControladorUrlEnum.URL1978
                                                        .getValue());
        urlCreacion = UrlServiceUtil.getInstance().getUrlServiceByUrlByEnumID(
                        PagossaldosControladorUrlEnum.URL1979.getValue());
        urlEliminacion = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        PagossaldosControladorUrlEnum.URL1980
                                                        .getValue());
        urlListado = UrlServiceUtil.getInstance().getUrlServiceByUrlByEnumID(
                        PagossaldosControladorUrlEnum.URL1981.getValue());

        parametrosListado.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        parametrosListado.put(PagossaldosControladorEnum.PARAM0.getValue(), "S");
    }

    // <METODOS_CARGAR_LISTA>
    public void cargarListaCmbConcepto() {
        try {
            Map<String, Object> param = new TreeMap<>();
            param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

            listaCmbConcepto = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            PagossaldosControladorUrlEnum.URL7030
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    public void cargarListaTxtBanco() {
        try {
            Map<String, Object> param = new TreeMap<>();
            param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

            listaTxtBanco = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            PagossaldosControladorUrlEnum.URL7020
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    public void cargarListaPreAno() {
        try {
            Map<String, Object> param = new TreeMap<>();
            param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

            listaPreAno = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            PagossaldosControladorUrlEnum.URL7362
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    public void cargarListaPrecod() {
        UrlBean urlBean = UrlServiceUtil.getInstance().getUrlServiceByUrlByEnumID(PagossaldosControladorUrlEnum.URL7694.getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.NUMERO_ORDEN.getName(), nOrden);

        listaPrecod = new RegistroDataModelImpl(urlBean.getUrl(), urlBean.getUrlConteo().getUrl(), param,
                        true, cCodigo);
    }

    public void cargarListaPrecodE() {
        UrlBean urlBean = UrlServiceUtil.getInstance().getUrlServiceByUrlByEnumID(PagossaldosControladorUrlEnum.URL7694.getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.NUMERO_ORDEN.getName(), nOrden);

        listaPrecodE = new RegistroDataModelImpl(urlBean.getUrl(), urlBean.getUrlConteo().getUrl(), param,
                        true, cCodigo);
    }

    // </METODOS_CARGAR_LISTA>
    // <METODOS_BOTONES>
    public void oprimircmdCertificado(Registro reg, int indice) {
        /* indice viene de la forma */
        // <CODIGO_DESARROLLADO>
        archivoDescarga = null;
        if (reg.getCampos().get("PAGO").equals(true)) {
            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB1090"));
            return;
        }
        else {
            genInforme(ReportesBean.FORMATOS.PDF, reg);
        }
        // </CODIGO_DESARROLLADO>
    }

    // </METODOS_BOTONES>
    // <METODOS_CAMBIAR>
    public void cambiarChkDescuento() {
        // <CODIGO_DESARROLLADO>
        if (registro.getCampos().get(cDescuento).equals(false)) {
            conceptoBloqueado = true;
            registro.getCampos().put(cConcepto, null);
            registro.getCampos().put(cFecha, null);
            registro.getCampos().put(cBanco, null);
        }
        else {
            conceptoBloqueado = false;
            registro.getCampos().put(cFecha,
                            new Date());
        }
        // </CODIGO_DESARROLLADO>
    }

    public void aceptarCUADRO() {
        // <CODIGO_DESARROLLADO>
        registro.getCampos().put(cPreAno, Integer.parseInt(
                        registro.getCampos().get(cPagoAnoAux).toString())
                        + 1);
        anioAux = Integer.parseInt(
                        registro.getCampos().get(cPreAno).toString());
        registro.getCampos().put(cConcepto, null);
        cargarListaCmbConcepto();
        cuadroVisible = false;
        // </CODIGO_DESARROLLADO>
    }

    public void cancelarCUADRO() {
        // <CODIGO_DESARROLLADO>
        anioAux = Integer.parseInt(
                        registro.getCampos().get(cPreAno).toString());
        registro.getCampos().put(cConcepto, null);
        cargarListaCmbConcepto();
        cuadroVisible = false;
        // </CODIGO_DESARROLLADO>
    }

    public void cambiarPrecodC(int rowNum) {

        // <CODIGO_DESARROLLADO>
        listaInicial.getDatasource().get(rowNum % 10).getCampos()
                        .put(cPropietario, propietarioAux);
        listaInicial.getDatasource().get(rowNum % 10).getCampos()
                        .put(cNumeroOrden, nOrdenAux);
        listaInicial.getDatasource().get(rowNum % 10).getCampos()
                        .put(cPagoAnoAux, pagoAnioAux);
        // </CODIGO_DESARROLLADO>
    }

    public void cambiarChkDescuentoC(int rowNum) {
        if (!listaInicial.getDatasource().get(rowNum % 10).getCampos()
                        .get(cDescuento).equals(false)) {
            listaInicial.getDatasource().get(rowNum % 10).getCampos().put(
                            cFecha,
                            new Date());
        }
    }

    public void cambiarPreAno() {
        // <CODIGO_DESARROLLADO>
        if (registro.getCampos().get(cPagoAnoAux) != null) {
            if (Integer.parseInt(registro.getCampos().get(cPreAno)
                            .toString()) > (Integer.parseInt(registro
                                            .getCampos().get(cPagoAnoAux)
                                            .toString())
                                            + 1)) {
                tituloCuadro = idioma.getString("TB_TB156");
                cuadroVisible = true;
            }
            else if (Integer.parseInt(registro.getCampos().get(cPreAno)
                            .toString()) <= Integer
                                            .parseInt(registro.getCampos()
                                                            .get(cPagoAnoAux)
                                                            .toString())) {
                tituloCuadro = idioma.getString("TB_TB157");
                cuadroVisible = true;
            }
        }

        // </CODIGO_DESARROLLADO>
    }

    public void cambiarPreAnoC(int rowNum) {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    // </METODOS_CAMBIAR>
    // <METODOS_COMBOS_GRANDES>
    public void seleccionarFilaPrecod(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        registro.getCampos().put(cPreCod,
                        registroAux.getCampos().get(cCodigo));
        registro.getCampos().put(cPropietario,
                        registroAux.getCampos().get("NOMBRE"));
        registro.getCampos().put(cNumeroOrden,
                        registroAux.getCampos().get(cNumeroOrden));
        registro.getCampos().put(cPagoAnoAux,
                        registroAux.getCampos().get(cPagoAno));
    }

    public void seleccionarFilaPrecodE(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        auxiliar = (String) registroAux.getCampos().get(cCodigo);
        propietarioAux = (String) registroAux.getCampos().get("NOMBRE");
        nOrdenAux = (String) registroAux.getCampos().get(cNumeroOrden);
        pagoAnioAux = (String) registroAux.getCampos().get(cPagoAno);
    }

    public void onRowSelectCmbConcepto(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        registro.getCampos().put(cConcepto,
                        registroAux.getCampos().get(cCodigo));
    }

    public void onRowSelectCmbConceptoE(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        auxiliar = ((BigDecimal) registroAux.getCampos().get(cCodigo))
                        .toString();
    }

    // </METODOS_COMBOS_GRANDES>
    @Override
    public void abrirFormulario() {
        // <CODIGO_DESARROLLADO>

        conceptoBloqueado = true;

        // </CODIGO_DESARROLLADO>
    }

    public void genInforme(ReportesBean.FORMATOS formato, Registro reg) {
        archivoDescarga = null;
        try {
            HashMap<String, Object> reemplazar = new HashMap<>();
            reemplazar.put("preCod", reg.getCampos().get(cPreCod));
            reemplazar.put("nOrden", nOrden);
            Map<String, Object> parametros = new HashMap<>();
            parametros.put("PR_NOMBRECOMPANIA",
                            SessionUtil.getCompaniaIngreso().getNombre());
            parametros.put("PR_NOMBRE_USUARIO",
                            SysmanFunciones.nvl(
                                            SessionUtil.getUser().getNombre1(),
                                            "")
                                            + " "
                                            + SysmanFunciones.nvl(
                                                            SessionUtil.getUser()
                                                                            .getApellido1(),
                                                            "")
                                            + " "
                                            + SysmanFunciones.nvl(
                                                            SessionUtil.getUser()
                                                                            .getApellido2(),
                                                            ""));

            String reporte = "000799INFCERTIFICADOSALFAVOR";
            Reporteador.resuelveConsulta(reporte,
                            Integer.parseInt(SessionUtil.getModulo()),
                            reemplazar, parametros);

            archivoDescarga = JsfUtil.exportarStreamed(reporte, parametros,
                            ConectorPool.ESQUEMA_SYSMAN, formato);
        }
        catch (JRException | IOException | SysmanException e) {
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
        if ((registro.getCampos().get(cPreAno) == null)
                        || registro.getCampos().get(cPreAno).toString().isEmpty()) {
            registro.getCampos().put(cPreAno,
                            SysmanFunciones.getParteFecha(
                                            new Date(),
                                            Calendar.YEAR)
                                            + 1);
        }

        if ((registro.getCampos().get(cFecha) == null)
                        || registro.getCampos().get(cFecha).toString().isEmpty()) {
            registro.getCampos().put(cFecha,
                            new Date());
        }
        accion = true;
        // </CODIGO_DESARROLLADO>
        return true;
    }

    @Override
    public boolean insertarDespues() {
        // <CODIGO_DESARROLLADO>
        conceptoBloqueado = true;
        // </CODIGO_DESARROLLADO>
        return true;
    }

    @Override
    public boolean actualizarAntes() {
        // <CODIGO_DESARROLLADO>
        if ("0".equals(SysmanFunciones
                        .nvl(registro.getCampos().get("VALOR"), "0"))) {
            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB158"));
            registro.getCampos().put(cDescuento, cFalse);
            conceptoBloqueado = true;

            return false;
        }

        if (!accion && !validaRegistro()) {
            return false;
        }
        if (!accion) {
            registro.getCampos().remove(GeneralParameterEnum.COMPANIA.getName());
            registro.getCampos().remove("ANULADOPOR");
            registro.getCampos().remove(GeneralParameterEnum.DATE_CREATED.getName());
            registro.getCampos().remove(GeneralParameterEnum.CREATED_BY.getName());
            registro.getCampos().remove(GeneralParameterEnum.NUMERO_ORDEN.getName());
            registro.getCampos().remove(PagossaldosControladorEnum.PARAM0.getValue());
            registro.getCampos().remove("GPAGODOBLE");
            registro.getCampos().remove("ANO_EXCEDENTE");
        }
        else {
            registro.getCampos().put(GeneralParameterEnum.COMPANIA.getName(),
                            compania);
            registro.getCampos().put(PagossaldosControladorEnum.PARAM0.getValue(),
                            "S");
        }
        registro.getCampos().remove(cPagoAnoAux);
        registro.getCampos().remove(cPropietario);
        accion = false;

        // </CODIGO_DESARROLLADO>
        return true;
    }

    private boolean validaRegistro() {
        try {
            Map<String, Object> param = new TreeMap<>();
            param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
            param.put(GeneralParameterEnum.CODIGO.getName(),
                            registro.getCampos().get(cPreCod));
            param.put(GeneralParameterEnum.NUMERO_ORDEN.getName(),
                            registro.getCampos().get(cNumeroOrden));

            Registro regAux0 = RegistroConverter.toRegistro(
                            requestManager.get(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            PagossaldosControladorUrlEnum.URL8678
                                                                            .getValue())
                                            .getUrl(), param));
            if (!SysmanFunciones.validarCampoVacio(regAux0.getCampos(),
                            cPagoAno) && (Integer.parseInt(
                                            registro.getCampos().get(cPreAno)
                                                            .toString()) < Integer
                                                                            .parseInt(SysmanFunciones
                                                                                            .nvl(
                                                                                                            registro.getCampos()
                                                                                                                            .get(cPagoAnoAux),
                                                                                                            regAux0.getCampos()
                                                                                                                            .get(cPagoAno))
                                                                                            .toString()))) {
                JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB159"));
                registro.getCampos().put(cDescuento, cFalse);
                conceptoBloqueado = true;
                return false;
            }

            if (SysmanFunciones.nvl(registro.getCampos().get("PAGO"), false)
                            .equals(true)) {
                JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB160"));
                registro.getCampos().put(cDescuento, cFalse);
                conceptoBloqueado = true;

                return false;
            }

            param.put(GeneralParameterEnum.PREANO.getName(),
                            registro.getCampos().get(cPreAno));

            Registro regAux = RegistroConverter.toRegistro(
                            requestManager.get(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            PagossaldosControladorUrlEnum.URL8690
                                                                            .getValue())
                                            .getUrl(), param));

            if (regAux != null) {
                if ((boolean) regAux.getCampos().get("PAGADO")) {
                    JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB161"));
                    registro.getCampos().put(cDescuento, cFalse);
                    conceptoBloqueado = true;

                    return false;
                }
            }
            else if (Integer.parseInt(registro.getCampos().get(cPreAno)
                            .toString()) <= SysmanFunciones
                                            .getParteFecha(new Date(),
                                                            Calendar.YEAR)) {
                JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB162"));
                registro.getCampos().put(cDescuento, cFalse);
                conceptoBloqueado = true;

                return false;
            }
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
        return true;
    }

    @Override
    public boolean actualizarDespues() {
        // <CODIGO_DESARROLLADO>
        conceptoBloqueado = true;

        // </CODIGO_DESARROLLADO>
        return true;
    }

    @Override
    public boolean eliminarAntes() {
        // <CODIGO_DESARROLLADO>
        if (registro.getCampos().get("PAGO").equals(true)) {
            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB163"));
            return false;
        }

        if (Double.parseDouble((String) SysmanFunciones.nvl(
                        registro.getCampos().get("VALOR").toString(),
                        "0")) > 0) {
            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB164"));
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
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    public void activarEdicion(Registro registro) {
        indice = listaInicial.getRowIndex();
        conceptoBloqueado = registro.getCampos().get(cDescuento).equals(true)
                        ? false : true;

        anioAux = Integer.parseInt(listaInicial.getDatasource().get(indice % 10)
                        .getCampos().get(cPreAno).toString());
        cargarListaCmbConcepto();
    }

    @Override
    public void asignarValoresRegistro() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    // <SET_GET_ATRIBUTOS>
    public StreamedContent getArchivoDescarga() {
        return archivoDescarga;
    }

    public boolean isConceptoBloqueado() {
        return conceptoBloqueado;
    }

    public void setConceptoBloqueado(boolean conceptoBloqueado) {
        this.conceptoBloqueado = conceptoBloqueado;
    }

    public int getIndice() {
        return indice;
    }

    public void setIndice(int indice) {
        this.indice = indice;
    }

    public String getPropietarioAux() {
        return propietarioAux;
    }

    public void setPropietarioAux(String propietarioAux) {
        this.propietarioAux = propietarioAux;
    }

    public String getnOrdenAux() {
        return nOrdenAux;
    }

    public void setnOrdenAux(String nOrdenAux) {
        this.nOrdenAux = nOrdenAux;
    }

    public String getPagoAnioAux() {
        return pagoAnioAux;
    }

    public void setPagoAnioAux(String pagoAnioAux) {
        this.pagoAnioAux = pagoAnioAux;
    }

    public int getAnioAux() {
        return anioAux;
    }

    public void setAnioAux(int anioAux) {
        this.anioAux = anioAux;
    }

    public boolean isCuadroVisible() {
        return cuadroVisible;
    }

    public void setCuadroVisible(boolean cuadroVisible) {
        this.cuadroVisible = cuadroVisible;
    }

    public String getTituloCuadro() {
        return tituloCuadro;
    }

    public void setTituloCuadro(String tituloCuadro) {
        this.tituloCuadro = tituloCuadro;
    }

    // </SET_GET_ATRIBUTOS>
    // <SET_GET_PARAMETROS>
    // </SET_GET_PARAMETROS>
    // <SET_GET_LISTAS>
    public List<Registro> getListaTxtBanco() {
        return listaTxtBanco;
    }

    public void setListaTxtBanco(List<Registro> listaTxtBanco) {
        this.listaTxtBanco = listaTxtBanco;
    }

    public List<Registro> getListaPreAno() {
        return listaPreAno;
    }

    public void setListaPreAno(List<Registro> listaPreAno) {
        this.listaPreAno = listaPreAno;
    }

    public List<Registro> getListaCmbConcepto() {
        return listaCmbConcepto;
    }

    public void setListaCmbConcepto(List<Registro> listaCmbConcepto) {
        this.listaCmbConcepto = listaCmbConcepto;
    }

    // </SET_GET_LISTAS>
    // <SET_GET_LISTAS_COMBO_GRANDE>

    public RegistroDataModelImpl getListaPrecod() {
        return listaPrecod;
    }

    public void setListaPrecod(RegistroDataModelImpl listaPrecod) {
        this.listaPrecod = listaPrecod;
    }

    public RegistroDataModelImpl getListaPrecodE() {
        return listaPrecodE;
    }

    public void setListaPrecodE(RegistroDataModelImpl listaPrecodE) {
        this.listaPrecodE = listaPrecodE;
    }

    public String getAuxiliar() {
        return auxiliar;
    }

    public void setAuxiliar(String auxiliar) {
        this.auxiliar = auxiliar;
    }

    public String getcPago() {
        return cPago;
    }

}
