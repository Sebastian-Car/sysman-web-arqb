package com.sysman.precontractual;

import com.sysman.beanbase.BeanBaseContinuoAcmeImpl;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.enums.GenericUrlEnum;
import com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.precontractual.enums.EpetapaControladorEnum;
import com.sysman.precontractual.enums.EpetapaControladorUrlEnum;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.util.SysmanFunciones;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.event.RowEditEvent;
import org.primefaces.event.SelectEvent;

/**
 *
 * @author acaceres
 * @version 1, 24/11/2015
 * 
 * @version 2, 23/08/2017, <strong>pespitia</strong>:<br>
 * Se reemplazo numero del formulario por enumerado
 * {@code GeneralCodigoFormaEnum}.<br>
 * Refactoring de sentencias SQL.<br>
 * Se aplicaron las recomendaciones de SonarLint.
 */
@ManagedBean
@ViewScoped
public class EpetapaControlador extends BeanBaseContinuoAcmeImpl {

    private final String compania;

    /**
     * Constante a nivel de clase que aloja la cadena
     * <code>COMPANIA</code>
     */
    private final String cCompania;

    /**
     * Constante a nivel de clase que aloja la cadena
     * <code>ETAPA</code>
     */
    private final String cEtapa;

    /**
     * Constante a nivel de clase que aloja la cadena
     * <code>TIPOCONTRATO</code>
     */
    private final String cTipoContrato;

    /**
     * Constante a nivel de clase que aloja la cadena
     * <code>COD_ETAPA</code>
     */
    private final String cCodEtapa;

    /**
     * Constante a nivel de clase que aloja la cadena
     * <code>COD_T_CONTRATO</code>
     */
    private final String cCodTContrato;

    private String codnombre;
    private String tipoContrato;
    private RegistroDataModelImpl listaNOMBRE;
    private RegistroDataModelImpl listaNOMBREE;
    private String auxiliar;
    private String cmbTipoContratos;
    private BigInteger codigoEtapa;
    private List<Registro> listaCmbTipoContrato;

    /**
     * Creates a new instance of EpetapaControlador
     */
    public EpetapaControlador() {
        super();

        compania = SessionUtil.getCompania();
        cCompania = GeneralParameterEnum.COMPANIA.getName();
        cEtapa = EpetapaControladorEnum.ETAPA.getValue();
        cTipoContrato = GeneralParameterEnum.TIPOCONTRATO.getName();
        cCodEtapa = EpetapaControladorEnum.COD_ETAPA.getValue();
        cCodTContrato = EpetapaControladorEnum.COD_T_CONTRATO.getValue();

        try {
            // 367
            numFormulario = GeneralCodigoFormaEnum.EPETAPA_CONTROLADOR
                            .getCodigo();

            validarPermisos();
        }
        catch (SysmanException ex) {
            logger.error(ex.getMessage(), ex);
            SessionUtil.redireccionarMenuPermisos();
        }
    }

    @PostConstruct
    public void inicializar() {
        enumBase = GenericUrlEnum.ES_D_ETAPA;
        registro = new Registro(new HashMap<String, Object>());

        buscarLlave();
        reasignarOrigen();
        cargarListaCmbTipoContrato();
        cargarListaNOMBRE();
        cargarListaNOMBREE();
        abrirFormulario();
    }

    public void cargarListaCmbTipoContrato() {
        Map<String, Object> param = new TreeMap<>();
        param.put(cCompania, compania);

        try {
            listaCmbTipoContrato = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            EpetapaControladorUrlEnum.URL2464
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    public void cargarListaNOMBRE() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        EpetapaControladorUrlEnum.URL2925
                                                        .getValue());

        Map<String, Object> param = new TreeMap<>();
        param.put(cCompania, compania);
        param.put(GeneralParameterEnum.TIPOCONTRATO.getName(), tipoContrato);

        listaNOMBRE = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param, true,
                        cCodEtapa);

    }

    public void cargarListaNOMBREE() {
        listaNOMBREE = listaNOMBRE;
    }

    public void seleccionarFilaNOMBRE(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        codnombre = SysmanFunciones
                        .nvl(registroAux.getCampos().get("NOMBRE"), "")
                        .toString();

        codigoEtapa = registroAux.getCampos().get(cCodEtapa) == null ? null
            : new BigInteger(registroAux.getCampos().get(cCodEtapa)
                            .toString());

        cargado = false;

        reasignarOrigen();
    }

    @Override
    public void abrirFormulario() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    public void cambiarCmbTipoContrato() {
        // <CODIGO_DESARROLLADO>
        codnombre = null;
        codigoEtapa = BigInteger.ZERO;
        tipoContrato = cmbTipoContratos;

        cargarListaNOMBRE();
        reasignarOrigen();
        // </CODIGO_DESARROLLADO>
    }

    public void cambiarNOMBRE() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    @Override
    public void cancelarEdicion(RowEditEvent event) {
        listaInicial.load();
    }

    @Override
    public boolean insertarAntes() {
        // <CODIGO_DESARROLLADO>
        registro.getCampos().put(cCompania, compania);
        registro.getCampos().put(cCodEtapa, codigoEtapa);
        registro.getCampos().put(cCodTContrato, tipoContrato);
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
        if (SysmanFunciones.validarVariableVacio(tipoContrato)) {
            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB3484"));
            return false;
        }

        if (codigoEtapa == null) {
            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB3485"));
            return false;
        }
        // </CODIGO_DESARROLLADO>
        return true;
    }

    @Override
    public boolean actualizarDespues() {
        // <CODIGO_DESARROLLADO>
        return true;
        // </CODIGO_DESARROLLADO>
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
        return true;
        // </CODIGO_DESARROLLADO>
    }

    @Override
    public void removerCombos() {
        // <CODIGO_DESARROLLADO>
        registro.getCampos().remove(cCompania);
        registro.getCampos().remove(cCodEtapa);
        registro.getCampos().remove(cCodTContrato);
        // </CODIGO_DESARROLLADO>
    }

    @Override
    public void reasignarOrigen() {
        buscarUrls();

        parametrosListado.put(cCompania, compania);
        parametrosListado.put(cTipoContrato, tipoContrato);
        parametrosListado.put(cEtapa, codigoEtapa);

    }

    @Override
    public void asignarValoresRegistro() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    public BigInteger getCodigoContrato() {
        return codigoEtapa;
    }

    public void setCodigoContrato(BigInteger codigoContrato) {
        this.codigoEtapa = codigoContrato;
    }

    public String getTipoContrato() {
        return tipoContrato;
    }

    public void setTipoContrato(String tipoContrato) {
        this.tipoContrato = tipoContrato;
    }

    public String getCmbTipoContratos() {
        return cmbTipoContratos;
    }

    public void setCmbTipoContratos(String cmbTipoContratos) {
        this.cmbTipoContratos = cmbTipoContratos;
    }

    public List<Registro> getListaCmbTipoContrato() {
        return listaCmbTipoContrato;
    }

    public void setListaCmbTipoContrato(List<Registro> listaCmbTipoContrato) {
        this.listaCmbTipoContrato = listaCmbTipoContrato;
    }

    public RegistroDataModelImpl getListaNOMBRE() {
        return listaNOMBRE;
    }

    public void setListaNOMBRE(RegistroDataModelImpl listaNOMBRE) {
        this.listaNOMBRE = listaNOMBRE;
    }

    public RegistroDataModelImpl getListaNOMBREE() {
        return listaNOMBREE;
    }

    public void setListaNOMBREE(RegistroDataModelImpl listaNOMBREE) {
        this.listaNOMBREE = listaNOMBREE;
    }

    public String getAuxiliar() {
        return auxiliar;
    }

    public void setAuxiliar(String auxiliar) {
        this.auxiliar = auxiliar;
    }

    public String getCodnombre() {
        return codnombre;
    }

    public void setCodnombre(String codnombre) {
        this.codnombre = codnombre;
    }
}
