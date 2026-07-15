package com.sysman.predial;

import com.sysman.beanbase.BeanBaseModal;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.predial.ejb.EjbPredialCuatroRemote;
import com.sysman.predial.enums.PredialregispagocuotaanterControladorUrlEnum;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.util.SysmanConstantes;
import com.sysman.util.SysmanFunciones;

import java.math.BigDecimal;
import java.text.ParseException;
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

import org.primefaces.event.SelectEvent;

/**
 *
 * @author NGOMEZ
 * @version 1, 07/06/2016
 *
 * @version 2, 13/06/2017 jrodriguezr Se refactoriza el c�digo: Se
 * pasa el numero del formulario al enumerado, se eliminan conexiones
 * y se ajustan metodos de generacion de reportes.
 * 
 * @author eamaya
 * @version 3.0, 12/07/2017. Proceso de Refactoring DSS ,Manejo de
 * EJBs y cambio de metodos onRow
 * 
 * @modified jguerrero
 * @version 4. 25/07/2017 Se agrega validacion para que se cargue el
 * valor de la cuota en el campo total pagado
 * 
 */
@ManagedBean
@ViewScoped

public class PredialregispagocuotaanterControlador extends BeanBaseModal {
    private final String compania;
    private final String nOrden;

    // <DECLARAR_ATRIBUTOS>
    private String codBanco;
    private String codPredio;
    private String nroAcuerdo;
    private String nroCuota;
    private Date fechaCorte;
    private String nombrePredio;
    private String nombreBanco;
    private String nroRecibo;
    private String totalPagado;
    private String observaciones;
    private String trpCod;
    private boolean muestraDialogo;

    private String obsPagos;

    // </DECLARAR_ATRIBUTOS>
    // <DECLARAR_PARAMETROS>
    // </DECLARAR_PARAMETROS>
    // <DECLARAR_LISTAS>
    private List<Registro> listanroAcuerdo;
    private List<Registro> listanroCuota;
    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>
    private RegistroDataModelImpl listaCodigobanco;
    private RegistroDataModelImpl listaCodPredio;
    // </DECLARAR_LISTAS_COMBO_GRANDE>

    private Date fecPag;
    private String mensaje;

    @EJB
    private EjbPredialCuatroRemote ejbPredialCuatro;

    /**
     * Creates a new instance of PredialregispagocuotaanterControlador
     */
    public PredialregispagocuotaanterControlador() {
        super();
        compania = SessionUtil.getCompania();
        nOrden = SysmanConstantes.NUMERO_ORDEN_PREDIAL;

        try {
            numFormulario = GeneralCodigoFormaEnum.PREDIALREGISPAGOCUOTAANTER_CONTROLADOR
                            .getCodigo();
            validarPermisos();
            // <INI_ADICIONAL>
            // </INI_ADICIONAL>
        }
        catch (Exception ex) {
            Logger.getLogger(PredialregispagocuotaanterControlador.class
                            .getName()).log(Level.SEVERE, null, ex);
            SessionUtil.redireccionarMenuPermisos();
        }
    }

    @PostConstruct
    public void inicializar() {
        // <CARGAR_LISTA>
        cargarListanroAcuerdo();
        cargarListanroCuota();
        // </CARGAR_LISTA>
        // <CARGAR_LISTA_COMBO_GRANDE>
        cargarListaCodigobanco();
        cargarListaCodPredio();
        // </CARGAR_LISTA_COMBO_GRANDE>
        abrirFormulario();
    }

    @Override
    public void abrirFormulario() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    // <METODOS_CARGAR_LISTA>
    public void cargarListanroAcuerdo() {

        try {
            Map<String, Object> param = new TreeMap<>();
            param.put(GeneralParameterEnum.COMPANIA.getName(),
                            compania);
            param.put(GeneralParameterEnum.PREDIO.getName(),
                            codPredio);

            listanroAcuerdo = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            PredialregispagocuotaanterControladorUrlEnum.URL4256
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    public void cargarListanroCuota() {

        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
        param.put(GeneralParameterEnum.CODIGOACUERDO.getName(),
                        nroAcuerdo);

        try {
            listanroCuota = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            PredialregispagocuotaanterControladorUrlEnum.URL4841
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    public void cargarListaCodigobanco() {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        PredialregispagocuotaanterControladorUrlEnum.URL5381
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);

        listaCodigobanco = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, "CODIGOBANCO");
    }

    public void cargarListaCodPredio() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        PredialregispagocuotaanterControladorUrlEnum.URL5858
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
        param.put(GeneralParameterEnum.NUMERO_ORDEN.getName(),
                        nOrden);

        listaCodPredio = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, GeneralParameterEnum.CODIGO.getName());
    }

    // </METODOS_CARGAR_LISTA>
    // <METODOS_BOTONES>

    public void oprimirRegistrar() {

        String resultado = null;
        try {
            resultado = ejbPredialCuatro.getRegistroPagoCuotaInicial(compania,
                            SysmanConstantes.NUMERO_ORDEN_PREDIAL,
                            SessionUtil.getUser().getCodigo(), observaciones,
                            fechaCorte, codPredio, codBanco, nroAcuerdo,
                            nroRecibo, new BigDecimal(totalPagado),
                            Integer.parseInt(nroCuota), trpCod);

        }
        catch (NumberFormatException | SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
        String[] resultados = null;
        if (resultado != null) {
            resultados = resultado.split(",");
        }

        if ("DIALOGO".equals(resultados[0])) {
            try {
                if (!SysmanFunciones.validarVariableVacio(resultados[1])) {
                    fecPag = SysmanFunciones.convertirAFecha(resultados[1]);
                }
            }
            catch (ParseException e) {
                logger.error(e.getMessage(), e);
                JsfUtil.agregarMensajeError(e.getMessage());
            }
            obsPagos = resultados[2];
            mensaje = resultados[3];
            muestraDialogo = true;
        }
        else {
            JsfUtil.agregarMensajeInformativo(resultado);
        }
    }
    // </METODOS_BOTONES>
    // <METODOS_CAMBIAR>

    public void cambiarnroAcuerdo() {
        // <CODIGO_DESARROLLADO>
        nroCuota = null;
        cargarListanroCuota();
        // </CODIGO_DESARROLLADO>
    }

    public void cambiarnroCuota() {
        try {
            Map<String, Object> param = new TreeMap<>();
            param.put(GeneralParameterEnum.COMPANIA.getName(),
                            compania);
            param.put(GeneralParameterEnum.CODIGOACUERDO.getName(),
                            nroAcuerdo);

            param.put(GeneralParameterEnum.CUOTA.getName(), nroCuota);

            Registro reg = RegistroConverter.toRegistro(
                            requestManager.get(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            PredialregispagocuotaanterControladorUrlEnum.URL1346
                                                                            .getValue())
                                            .getUrl(), param));

            if (reg.getCampos().get(
                            GeneralParameterEnum.TOTAL.getName()) != null) {
                totalPagado = reg.getCampos()
                                .get(GeneralParameterEnum.TOTAL.getName())
                                .toString();
            }

        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    public void cambiardialogoPago() {
        // <CODIGO_DESARROLLADO>

        // </CODIGO_DESARROLLADO>
    }

    public void aceptardialogoPago() {

        String resultado = null;
        try {
            resultado = ejbPredialCuatro.getRegistroPagoCuotaInicial(compania,
                            nroRecibo,
                            codBanco,
                            fechaCorte,
                            SessionUtil.getUser().getCodigo(),
                            nroAcuerdo,
                            codPredio,
                            Integer.parseInt(nroCuota),
                            new BigDecimal(totalPagado),
                            obsPagos);

        }
        catch (NumberFormatException | SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

        if (resultado != null) {
            JsfUtil.agregarMensajeInformativo(
                            idioma.getString("MSM_PROCESO_EJECUTADO"));
        }
        muestraDialogo = false;
    }

    // </METODOS_CAMBIAR>
    // <METODOS_COMBOS_GRANDES>
    public void seleccionarFilaCodigobanco(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        codBanco = SysmanFunciones
                        .nvl(registroAux.getCampos().get("CODIGOBANCO"), "")
                        .toString();
        nombreBanco = SysmanFunciones
                        .nvl(registroAux.getCampos().get("NOMBREBANCO"), "")
                        .toString();
    }

    public void seleccionarFilaCodPredio(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();

        if (registroAux.getCampos().get(GeneralParameterEnum.CODIGO.getName())
                        .toString().length() > 15) {
            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB712"));
            return;
        }

        Map<String, Object> param = new TreeMap<>();

        param.put(GeneralParameterEnum.PREDIO.getName(), registroAux.getCampos()
                        .get(GeneralParameterEnum.CODIGO
                                        .getName()));

        Registro regAux;
        try {
            regAux = RegistroConverter.toRegistro(
                            requestManager.get(
                                            UrlServiceUtil.getInstance()
                                                            .getUrlServiceByUrlByEnumID(
                                                                            PredialregispagocuotaanterControladorUrlEnum.URL1345
                                                                                            .getValue())
                                                            .getUrl(),
                                            param));

            if ("0".equals(regAux.getCampos().get("CUENTA").toString())) {
                JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB713"));
                codPredio = null;
                return;
            }

            codPredio = SysmanFunciones
                            .nvl(registroAux.getCampos().get(
                                            GeneralParameterEnum.CODIGO
                                                            .getName()),
                                            "")
                            .toString();
            nombrePredio = SysmanFunciones
                            .nvl(registroAux.getCampos().get("NOMBRE"), "")
                            .toString();
            trpCod = SysmanFunciones
                            .nvl(registroAux.getCampos().get("TRPCOD"), "")
                            .toString();
            nroAcuerdo = null;
            nroCuota = null;
            totalPagado = null;
            cargarListanroAcuerdo();
            cargarListanroCuota();

        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    // </METODOS_COMBOS_GRANDES>

    // <SET_GET_ATRIBUTOS>
    public String getCodBanco() {
        return codBanco;
    }

    public void setCodBanco(String codBanco) {
        this.codBanco = codBanco;
    }

    public String getCodPredio() {
        return codPredio;
    }

    public void setCodPredio(String codPredio) {
        this.codPredio = codPredio;
    }

    public String getNroAcuerdo() {
        return nroAcuerdo;
    }

    public void setNroAcuerdo(String nroAcuerdo) {
        this.nroAcuerdo = nroAcuerdo;
    }

    public String getNroCuota() {
        return nroCuota;
    }

    public void setNroCuota(String nroCuota) {
        this.nroCuota = nroCuota;
    }

    public Date getFechaCorte() {
        return fechaCorte;
    }

    public void setFechaCorte(Date fechaCorte) {
        this.fechaCorte = fechaCorte;
    }

    public String getNombrePredio() {
        return nombrePredio;
    }

    public void setNombrePredio(String nombrePredio) {
        this.nombrePredio = nombrePredio;
    }

    public String getNombreBanco() {
        return nombreBanco;
    }

    public void setNombreBanco(String nombreBanco) {
        this.nombreBanco = nombreBanco;
    }

    public String getNroRecibo() {
        return nroRecibo;
    }

    public void setNroRecibo(String nroRecibo) {
        this.nroRecibo = nroRecibo;
    }

    public String getTotalPagado() {
        return totalPagado;
    }

    public void setTotalPagado(String totalPagado) {
        this.totalPagado = totalPagado;
    }

    public String getObservaciones() {
        return observaciones;
    }

    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }

    public boolean isMuestraDialogo() {
        return muestraDialogo;
    }

    public void setMuestraDialogo(boolean muestraDialogo) {
        this.muestraDialogo = muestraDialogo;
    }

    // </SET_GET_ATRIBUTOS>
    // <SET_GET_PARAMETROS>
    // </SET_GET_PARAMETROS>
    // <SET_GET_LISTAS>
    public List<Registro> getListanroAcuerdo() {
        return listanroAcuerdo;
    }

    public void setListanroAcuerdo(List<Registro> listanroAcuerdo) {
        this.listanroAcuerdo = listanroAcuerdo;
    }

    public List<Registro> getListanroCuota() {
        return listanroCuota;
    }

    public void setListanroCuota(List<Registro> listanroCuota) {
        this.listanroCuota = listanroCuota;
    }

    // </SET_GET_LISTAS>
    // <SET_GET_LISTAS_COMBO_GRANDE>
    public RegistroDataModelImpl getListaCodigobanco() {
        return listaCodigobanco;
    }

    public void setListaCodigobanco(RegistroDataModelImpl listaCodigobanco) {
        this.listaCodigobanco = listaCodigobanco;
    }

    public RegistroDataModelImpl getListaCodPredio() {
        return listaCodPredio;
    }

    public void setListaCodPredio(RegistroDataModelImpl listaCodPredio) {
        this.listaCodPredio = listaCodPredio;
    }
    // </SET_GET_LISTAS_COMBO_GRANDE>

    public Date getFecPag() {
        return fecPag;
    }

    public void setFecPag(Date fecPag) {
        this.fecPag = fecPag;
    }

    public String getMensaje() {
        return mensaje;
    }

    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }

}
