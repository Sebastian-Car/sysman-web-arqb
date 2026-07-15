package com.sysman.precontractual;

import com.sysman.beanbase.BeanBaseContinuoAcmeImpl;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.enums.GenericUrlEnum;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.precontractual.enums.FrmestpreviounspscsControladorEnum;
import com.sysman.precontractual.enums.FrmestpreviounspscsControladorUrlEnum;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.util.SysmanFunciones;

import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.context.RequestContext;
import org.primefaces.event.RowEditEvent;
import org.primefaces.event.SelectEvent;

/**
 *
 * @author vmolano
 * @version 1, 24/08/2016
 * @modified jguerrero
 * @version 2. 25/08/2017 Se realizo el refactory de las consultas sql
 * en el controlador. Además se ajustaron los errores del sonar
 */
@ManagedBean
@ViewScoped

public class FrmestpreviounspscsControlador extends BeanBaseContinuoAcmeImpl {
    private final String compania;

    /** Constante a nivel de clase que aloja el valor NOMBRE */
    private final String cNombre;

    /** Constante a nivel de clase que aloja el valor CODIGO */
    private final String cCodigo;

    private String codigoEstudio;
    private String titulo;
    private String auxiliar;
    private String nombreCodigo;

    /**
     * Identificador que gestiona la visibilidad de los controles para
     * crear, actualizar y eliminar
     */
    private boolean visualizar;

    private RegistroDataModelImpl listatxtCodigo;
    private RegistroDataModelImpl listatxtCodigoE;

    /**
     * Creates a new instance of FrmestpreviounspscsControlador
     */
    public FrmestpreviounspscsControlador() {
        super();
        compania = SessionUtil.getCompania();
        cNombre = GeneralParameterEnum.NOMBRE.getName();
        cCodigo = GeneralParameterEnum.CODIGO.getName();

        try {
            numFormulario = GeneralCodigoFormaEnum.FRMESTPREVIOUNSPSCS_CONTROLADOR
                            .getCodigo();
            validarPermisos();

            Map<String, Object> parametrosEntrada = SessionUtil.getFlash();

            if (parametrosEntrada != null) {
                codigoEstudio = (String) parametrosEntrada
                                .get(FrmestpreviounspscsControladorEnum.TXTCODESTUDIOLOWER
                                                .getValue());
                titulo = (String) parametrosEntrada
                                .get(FrmestpreviounspscsControladorEnum.TITULOLWER
                                                .getValue());
                visualizar = Boolean.parseBoolean(
                                parametrosEntrada
                                                .get(FrmestpreviounspscsControladorEnum.VISUALIZAR
                                                                .getValue())
                                                .toString());
            }

        }
        catch (Exception ex) {
            Logger.getLogger(FrmestpreviounspscsControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            SessionUtil.redireccionarMenuPermisos();
        }
    }

    @PostConstruct
    public void inicializar() {

        enumBase = GenericUrlEnum.ES_ESTPREVIO_UNSPSC;
        buscarLlave();
        reasignarOrigen();

        registro = new Registro();
        cargarListatxtCodigo();
        cargarListatxtCodigoE();

        abrirFormulario();

    }

    @Override
    public void reasignarOrigen() {
        buscarUrls();
        parametrosListado.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
        parametrosListado.put(GeneralParameterEnum.COD_ESTUDIO.getName(),
                        codigoEstudio);

    }

    public RegistroDataModelImpl getListatxtCodigo() {
        return listatxtCodigo;
    }

    public void setListatxtCodigo(RegistroDataModelImpl listatxtCodigo) {
        this.listatxtCodigo = listatxtCodigo;
    }

    public void cargarListatxtCodigo() {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        FrmestpreviounspscsControladorUrlEnum.URL4029
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        listatxtCodigo = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true,
                        cCodigo);

    }

    public void cargarListatxtCodigoE() {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        FrmestpreviounspscsControladorUrlEnum.URL4029
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        listatxtCodigoE = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true,
                        cCodigo);
    }

    public void seleccionarFilatxtCodigo(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        registro.getCampos().put("COD_UNSPSC",
                        registroAux.getCampos().get(cCodigo));
        registro.getCampos().put(cNombre,
                        registroAux.getCampos().get(cNombre));
    }

    public void seleccionarFilatxtCodigoE(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        auxiliar = retornarString(registroAux, cCodigo);
        nombreCodigo = retornarString(registroAux, cNombre);
    }

    public void cambiartxtCodigo() {
        // <CODIGO_DESARROLLADO>

        // </CODIGO_DESARROLLADO>
    }

    public void cambiartxtNombre() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    public void cambiartxtCodigoC(int rowNum) {
        // <CODIGO_DESARROLLADO>
        listaInicial.getDatasource().get(rowNum % 10).getCampos()
                        .put(cNombre, nombreCodigo);
        // </CODIGO_DESARROLLADO>
    }

    public void cambiartxtNombreC(int rowNum) {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    @Override
    public void abrirFormulario() {
        // <CODIGO_DESARROLLADO>
        /*
         * FR1065-AL_ABRIR Private Sub Form_Open(Cancel As Integer)
         * DoCmd.Restore formularioAbrir 74, Me.Name End Sub
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
        registro.getCampos().remove(cCodigo);
        registro.getCampos().put(GeneralParameterEnum.COD_ESTUDIO.getName(),
                        Integer.parseInt(codigoEstudio));

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

        registro.getCampos().remove(cCodigo);
        registro.getCampos().remove(cNombre);

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

    public String getCodigoEstudio() {
        return codigoEstudio;
    }

    public void setCodigoEstudio(String codigoEstudio) {
        this.codigoEstudio = codigoEstudio;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
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

    public String getNombreCodigo() {
        return nombreCodigo;
    }

    public void setNombreCodigo(String nombreCodigo) {
        this.nombreCodigo = nombreCodigo;
    }

    public RegistroDataModelImpl getListatxtCodigoE() {
        return listatxtCodigoE;
    }

    public void setListatxtCodigoE(RegistroDataModelImpl listatxtCodigoE) {
        this.listatxtCodigoE = listatxtCodigoE;
    }

    public String getAuxiliar() {
        return auxiliar;
    }

    public void setAuxiliar(String auxiliar) {
        this.auxiliar = auxiliar;
    }

    public boolean isVisualizar() {
        return visualizar;
    }

    public void setVisualizar(boolean visualizar) {
        this.visualizar = visualizar;
    }

    private String retornarString(Registro reg, String campo) {
        return SysmanFunciones.validarCampoVacio(reg.getCampos(), campo) ? ""
            : reg.getCampos().get(campo).toString();
    }
}