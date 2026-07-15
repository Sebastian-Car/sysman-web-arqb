package com.sysman.precontractual;

import com.sysman.beanbase.BeanBaseContinuoAcmeImpl;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.enums.GenericUrlEnum;
import com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.precontractual.enums.EscontratosControladorEnum;
import com.sysman.precontractual.enums.EscontratosControladorUrlEnum;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.util.SysmanFunciones;

import java.util.HashMap;
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
 * @version 1, 23/11/2015
 * 
 * @version 2, 23/08/2017, <strong>pespitia</strong>:<br>
 * Reemplazo del numero del formulario por enumerado.<br>
 * Refactoring de senetencias SQL.<br>
 * Se aplicaron las recomendaciones de SonarLint.
 */
@ManagedBean
@ViewScoped
public class EscontratosControlador extends BeanBaseContinuoAcmeImpl {

    private final String compania;

    /**
     * Constante a nivel de clase que aloja la cadena:
     * <code>COMPANIA</code>
     */
    private final String cCompania;

    /**
     * Constante a nivel de clase que aloja la cadena:
     * <code>TIPO</code>
     */
    private final String cTipo;

    /**
     * Constante a nivel de clase que aloja la cadena:
     * <code>COD_T_CONTRATO</code>
     */
    private final String cCodTContrato;

    private String cmbTipoContrato;
    private String codigoContratos;
    private RegistroDataModelImpl listaCmbTipoContrato;
    private RegistroDataModelImpl listaCmbTipoContratoE;
    private String auxiliar;

    /**
     * Creates a new instance of EscontratosControlador
     */
    public EscontratosControlador() {
        super();

        compania = SessionUtil.getCompania();
        cCompania = GeneralParameterEnum.COMPANIA.getName();
        cTipo = EscontratosControladorEnum.TIPO.getValue();
        cCodTContrato = EscontratosControladorEnum.COD_T_CONTRATO.getValue();

        try {
            // 361
            numFormulario = GeneralCodigoFormaEnum.ESCONTRATOS_CONTROLADOR
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
        enumBase = GenericUrlEnum.ES_CONTRATO;
        registro = new Registro(new HashMap<String, Object>());

        buscarLlave();
        reasignarOrigen();
        cargarListaCmbTipoContrato();
        cargarListaCmbTipoContratoE();
        abrirFormulario();
    }

    public void cargarListaCmbTipoContrato() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        EscontratosControladorUrlEnum.URL2061
                                                        .getValue());

        Map<String, Object> param = new TreeMap<>();
        param.put(cCompania, compania);

        listaCmbTipoContrato = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, cCodTContrato);

    }

    public void cargarListaCmbTipoContratoE() {
        listaCmbTipoContratoE = listaCmbTipoContrato;
    }

    public void seleccionarFilaCmbTipoContrato(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        cmbTipoContrato = SysmanFunciones
                        .nvl(registroAux.getCampos().get("NOMBRE"), "")
                        .toString();

        codigoContratos = SysmanFunciones
                        .nvl(registroAux.getCampos().get(cCodTContrato), "")
                        .toString();

        reasignarOrigen();
    }

    @Override
    public void abrirFormulario() {
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
        registro.getCampos().put(cCodTContrato, codigoContratos);
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
        registro.getCampos().remove(cCompania);
        registro.getCampos().remove(cCodTContrato);
        // </CODIGO_DESARROLLADO>
    }

    @Override
    public void reasignarOrigen() {
        buscarUrls();
        parametrosListado.put(cCompania, compania);
        parametrosListado.put(cTipo, codigoContratos);
    }

    @Override
    public void asignarValoresRegistro() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    public RegistroDataModelImpl getListaCmbTipoContrato() {
        return listaCmbTipoContrato;
    }

    public void setListaCmbTipoContrato(
        RegistroDataModelImpl listaCmbTipoContrato) {
        this.listaCmbTipoContrato = listaCmbTipoContrato;
    }

    public RegistroDataModelImpl getListaCmbTipoContratoE() {
        return listaCmbTipoContratoE;
    }

    public void setListaCmbTipoContratoE(
        RegistroDataModelImpl listaCmbTipoContratoE) {
        this.listaCmbTipoContratoE = listaCmbTipoContratoE;
    }

    public String getAuxiliar() {
        return auxiliar;
    }

    public void setAuxiliar(String auxiliar) {
        this.auxiliar = auxiliar;
    }

    public String getCmbTipoContrato() {
        return cmbTipoContrato;
    }

    public void setCmbTipoContrato(String cmbTipoContrato) {
        this.cmbTipoContrato = cmbTipoContrato;
    }
}
