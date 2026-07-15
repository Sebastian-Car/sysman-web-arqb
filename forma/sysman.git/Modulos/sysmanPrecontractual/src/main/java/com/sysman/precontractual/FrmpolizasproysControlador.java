package com.sysman.precontractual;

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
import com.sysman.precontractual.enums.FrmpolizasproysControladorEnum;
import com.sysman.precontractual.enums.FrmpolizasproysControladorUrlEnum;
import com.sysman.recursos.ejb.EjbSysmanUtilRemote;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.util.SysmanFunciones;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.event.RowEditEvent;
import org.primefaces.event.SelectEvent;

/**
 *
 * @author acaceres
 * @version 1, 04/04/2016
 * @author jcrodriguez,Refactoring y Depuracion
 * @version 2, 29/08/2017
 */
@ManagedBean
@ViewScoped
public class FrmpolizasproysControlador extends BeanBaseContinuoAcmeImpl {

    private final String compania;
    private RegistroDataModelImpl listaTIPOPOLIZA;
    private RegistroDataModelImpl listaTIPOPOLIZAE;

    /**
     * TODO DOCUMENTACION NECESARIA
     */
    private List<Registro> listaCbUnidadMedida;

    private String auxiliar;
    private int codigoPoliza;
    private String tipoPoliza;
    @EJB
    private EjbSysmanUtilRemote ejbSysmanUtil;

    /*
     * Parametros que se reciben desde el formulario principal
     */
    private HashMap<String, Object> ridP;
    private String txtCodEstudio;
    private boolean esCreador;
    private String vigencia;

    /**
     * Creates a new instance of FrmpolizasproysControlador
     */
    public FrmpolizasproysControlador() {
        super();
        compania = SessionUtil.getCompania();
        try {
            numFormulario = GeneralCodigoFormaEnum.FRMPOLIZASPROYS_CONTROLADOR
                            .getCodigo();

            Map<String, Object> parametrosEntrada = SessionUtil.getFlash();
            if (parametrosEntrada != null) {
                ridP = (HashMap<String, Object>) parametrosEntrada.get("rid");
                txtCodEstudio = validarCadena(parametrosEntrada, "codEstudio");
                esCreador = Boolean.parseBoolean(
                                validarCadena(parametrosEntrada, "esCreador"));
                vigencia = parametrosEntrada.get("vigenciaPeriodo").toString();

            }
            validarPermisos();
        }
        catch (Exception ex) {
            Logger.getLogger(FrmpolizasproysControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            SessionUtil.redireccionarMenuPermisos();
        }
    }

    @PostConstruct
    public void inicializar() {

        enumBase = GenericUrlEnum.ES_POLIZA_EST;
        buscarLlave();
        reasignarOrigen();
        registro = new Registro();
        cargarListaTIPOPOLIZA();
        cargarListaTIPOPOLIZAE();
        cargarListaCbUnidadMedida();
        abrirFormulario();
    }

    @Override
    public void reasignarOrigen() {
        buscarUrls();
        parametrosListado.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
        parametrosListado.put(GeneralParameterEnum.COD_ESTUDIO.getName(),
                        txtCodEstudio);

    }

    public void cargarListaTIPOPOLIZA() {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        FrmpolizasproysControladorUrlEnum.URL3825
                                                        .getValue());
        listaTIPOPOLIZA = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), null,
                        true, GeneralParameterEnum.CODIGO.getName());
    }

    public void cargarListaTIPOPOLIZAE() {
        listaTIPOPOLIZAE = listaTIPOPOLIZA;
    }

    /**
     * 
     * Carga la lista listaCbUnidadMedida
     *
     * TODO DOCUMENTACION ADICIONAL
     */
    public void cargarListaCbUnidadMedida() {
        Map<String, Object> param = new TreeMap<>();
        param.put("CATEGORIA", 12);

        try {
            listaCbUnidadMedida = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            FrmpolizasproysControladorUrlEnum.URL001
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    public void seleccionarFilaTIPOPOLIZA(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        registro.getCampos().put(
                        FrmpolizasproysControladorEnum.TIPOPOLIZA.getValue(),
                        registroAux.getCampos().get(
                                        GeneralParameterEnum.CODIGO.getName()));
        registro.getCampos().put(GeneralParameterEnum.DESCRIPCION.getName(),
                        registroAux.getCampos()
                                        .get(GeneralParameterEnum.DESCRIPCION
                                                        .getName()));
        codigoPoliza = Integer.parseInt(validarCadena(registroAux.getCampos(),
                        GeneralParameterEnum.CODIGO.getName()));
    }

    private String validarCadena(Map<String, Object> campos, String var) {
        return SysmanFunciones.validarCampoVacio(campos, var) ? ""
            : campos.get(var).toString();
    }

    public void seleccionarFilaTIPOPOLIZAE(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        auxiliar = validarCadena(registroAux.getCampos(),
                        GeneralParameterEnum.DESCRIPCION.getName());
        tipoPoliza = validarCadena(registroAux.getCampos(),
                        GeneralParameterEnum.CODIGO.getName());
    }

    public void cambiarTIPOPOLIZAC(int rowNum) {
        listaInicial.getDatasource().get(rowNum % 10).getCampos().put(
                        FrmpolizasproysControladorEnum.TIPOPOLIZA.getValue(),
                        tipoPoliza);

    }

    @Override
    public void abrirFormulario() {
        // Heredado del bean base
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
        StringBuilder criterio = new StringBuilder();
        criterio.append("ES_POLIZA_EST.COMPANIA  = ''");
        criterio.append(compania);
        criterio.append("''");
        criterio.append(" AND ES_POLIZA_EST.COD_ESTUDIO = ''");
        criterio.append(txtCodEstudio);
        criterio.append("''");
        try {
            registro.getCampos().put(
                            FrmpolizasproysControladorEnum.COD_POLIZA
                                            .getValue(),
                            (int) ejbSysmanUtil.generarSiguienteConsecutivo(
                                            FrmpolizasproysControladorEnum.ES_POLIZA_EST
                                                            .getValue(),
                                            criterio.toString(),
                                            FrmpolizasproysControladorEnum.COD_POLIZA
                                                            .getValue()));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
        registro.getCampos().put(GeneralParameterEnum.COD_ESTUDIO.getName(),
                        txtCodEstudio);
        registro.getCampos().remove(GeneralParameterEnum.DESCRIPCION.getName());
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
        registro.getCampos().remove(GeneralParameterEnum.DESCRIPCION.getName());
    }

    @Override
    public void asignarValoresRegistro() {
        // heredado del bean bases
    }

    public void ejecutarrcCerrar() {
        // <CODIGO_DESARROLLADO>

        Map<String, Object> parametros = new TreeMap<>();

        parametros.put("ridEstPrevios", ridP);
        parametros.put("vigenciaPeriodo", vigencia);
        Direccionador direccionador = new Direccionador();
        direccionador.setNumForm(Integer
                        .toString(GeneralCodigoFormaEnum.FRMESTPREVIOPROYS_CONTROLADOR
                                        .getCodigo()));
        direccionador.setParametros(parametros);

        SessionUtil.redireccionarForma(direccionador, SessionUtil.getModulo());
        // </CODIGO_DESARROLLADO>
    }

    public RegistroDataModelImpl getListaTIPOPOLIZA() {
        return listaTIPOPOLIZA;
    }

    public void setListaTIPOPOLIZA(RegistroDataModelImpl listaTIPOPOLIZA) {
        this.listaTIPOPOLIZA = listaTIPOPOLIZA;
    }

    public RegistroDataModelImpl getListaTIPOPOLIZAE() {
        return listaTIPOPOLIZAE;
    }

    public void setListaTIPOPOLIZAE(RegistroDataModelImpl listaTIPOPOLIZAE) {
        this.listaTIPOPOLIZAE = listaTIPOPOLIZAE;
    }

    /**
     * Retorna la lista listaCbUnidadMedida
     * 
     * @return listaCbUnidadMedida
     */
    public List<Registro> getListaCbUnidadMedida() {
        return listaCbUnidadMedida;
    }

    /**
     * Asigna la lista listaCbUnidadMedida
     * 
     * @param listaCbUnidadMedida
     * Variable a asignar en listaCbUnidadMedida
     */
    public void setListaCbUnidadMedida(List<Registro> listaCbUnidadMedida) {
        this.listaCbUnidadMedida = listaCbUnidadMedida;
    }

    public String getAuxiliar() {
        return auxiliar;
    }

    public void setAuxiliar(String auxiliar) {
        this.auxiliar = auxiliar;
    }

    public boolean isEsCreador() {
        return esCreador;
    }

    public void setEsCreador(boolean esCreador) {
        this.esCreador = esCreador;
    }

    public String getTipoPoliza() {
        return tipoPoliza;
    }

    public void setTipoPoliza(String tipoPoliza) {
        this.tipoPoliza = tipoPoliza;
    }

    public int getCodigoPoliza() {
        return codigoPoliza;
    }

    public void setCodigoPoliza(int codigoPoliza) {
        this.codigoPoliza = codigoPoliza;
    }

}
