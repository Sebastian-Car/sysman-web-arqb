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
import com.sysman.precontractual.enums.MresolucionsControladorEnum;
import com.sysman.precontractual.enums.MresolucionsControladorUrlEnum;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.util.SysmanFunciones;

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

/**
 *
 * @author NGOMEZ
 * @version 1, 26/02/2016
 * @modified jguerrero
 * @version 2. 01/09/2017 Se realizo el refactory de las consultas sql
 * en el controlador. Además se ajustaron los errores del sonar
 *
 * -- Modificado por lcortes 15/02/2017 -- Se
 */
@ManagedBean
@ViewScoped

public class MresolucionsControlador extends BeanBaseContinuoAcmeImpl {

    private final String compania;

    private RegistroDataModelImpl listaModeloPlantilla;
    private RegistroDataModelImpl listaModeloPlantillaE;
    private String auxiliar;
    private List<Registro> listatipoContrato;
    private String nombreAux;

    /**
     * Creates a new instance of MresolucionsControlador
     */
    public MresolucionsControlador() {
        super();
        compania = SessionUtil.getCompania();

        try {
            numFormulario = GeneralCodigoFormaEnum.MRESOLUCIONS_CONTROLADOR
                            .getCodigo();

            validarPermisos();
        }
        catch (Exception ex) {
            Logger.getLogger(MresolucionsControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            SessionUtil.redireccionarMenuPermisos();
        }

    }

    @PostConstruct
    public void inicializar() {
        enumBase = GenericUrlEnum.ES_MODELO;
        buscarLlave();
        reasignarOrigen();
        registro = new Registro(new HashMap<String, Object>());
        cargarlistatipoContrato();
        cargarlistaModeloPlantilla();
        cargarlistaModeloPlantillaE();
        abrirFormulario();
    }

    @Override
    public void reasignarOrigen() {
        buscarUrls();
        parametrosListado.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);

    }

    public void cargarlistatipoContrato() {

        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        try {
            listatipoContrato = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            MresolucionsControladorUrlEnum.URL3357
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

        // 111014
    }

    public void cargarlistaModeloPlantilla() {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        MresolucionsControladorUrlEnum.URL2854
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(MresolucionsControladorEnum.MODULO.getValue(),
                        SessionUtil.getModulo());

        listaModeloPlantilla = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, GeneralParameterEnum.CODIGO.getName());

    }

    public void cargarlistaModeloPlantillaE() {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        MresolucionsControladorUrlEnum.URL2854
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(MresolucionsControladorEnum.MODULO.getValue(),
                        SessionUtil.getModulo());

        listaModeloPlantillaE = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, GeneralParameterEnum.CODIGO.getName());

    }

    public void oprimirDisenarModelo() {
        // <CODIGO_DESARROLLADO>
        Map<String, Object> parametrosPlantW = new HashMap<>();
        parametrosPlantW.put(
                        MresolucionsControladorEnum.FORM_ORIGENLOWER.getValue(),
                        MresolucionsControladorEnum.MRESOLUCIONLOWER
                                        .getValue());
        parametrosPlantW.put(
                        MresolucionsControladorEnum.COD_FORM_ORIGENLOWER
                                        .getValue(),
                        String.valueOf(GeneralCodigoFormaEnum.MRESOLUCIONS_CONTROLADOR
                                        .getCodigo()));

        Direccionador direccionador = new Direccionador();
        direccionador.setNumForm(Integer
                        .toString(GeneralCodigoFormaEnum.PLANTILLASWORDS_CONTROLADOR
                                        .getCodigo()));
        direccionador.setParametros(parametrosPlantW);
        SessionUtil.redireccionarForma(direccionador, SessionUtil.getModulo());

        // </CODIGO_DESARROLLADO>
    }

    public void cambiarModeloPlantillaC(int rowNum) {
        // Para el cambio en una fila selecciona (PARA FORMULARIOS
        // CONTINUOS) se realiza como lo muestra la siguiente linea
        listaInicial.getDatasource().get(rowNum % 10).getCampos().put(
                        GeneralParameterEnum.NOMBRE.getName(),
                        nombreAux);
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    public void seleccionarFilaModeloPlantilla(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        registro.getCampos().put(GeneralParameterEnum.MODELO.getName(),
                        registroAux.getCampos().get(
                                        GeneralParameterEnum.CODIGO.getName()));
        registro.getCampos().put(GeneralParameterEnum.NOMBRE.getName(),
                        registroAux.getCampos().get(
                                        GeneralParameterEnum.NOMBRE.getName()));
    }

    public void seleccionarFilaModeloPlantillaE(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        auxiliar = retornarString(registroAux,
                        GeneralParameterEnum.CODIGO.getName());
        nombreAux = retornarString(registroAux,
                        GeneralParameterEnum.NOMBRE.getName());
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
        registro.getCampos().put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
        registro.getCampos()
                        .remove(MresolucionsControladorEnum.CODCONTRATONOMBRE
                                        .getValue());
        registro.getCampos().remove(
                        MresolucionsControladorEnum.TIPO_ESTUDIO2.getValue());
        // </CODIGO_DESARROLLADO>
        return true;
    }

    @Override
    public boolean insertarDespues() {
        // <CODIGO_DESARROLLADO>
        registro.getCampos().put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
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
        registro.getCampos()
                        .remove(MresolucionsControladorEnum.CODCONTRATONOMBRE
                                        .getValue());
        registro.getCampos().remove(
                        MresolucionsControladorEnum.TIPO_ESTUDIO2.getValue());

    }

    @Override
    public void asignarValoresRegistro() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    public RegistroDataModelImpl getListaModeloPlantilla() {
        return listaModeloPlantilla;
    }

    public void setListaModeloPlantilla(
        RegistroDataModelImpl listaModeloPlantilla) {
        this.listaModeloPlantilla = listaModeloPlantilla;
    }

    public RegistroDataModelImpl getListaModeloPlantillaE() {
        return listaModeloPlantillaE;
    }

    public void setListaModeloPlantillaE(
        RegistroDataModelImpl listaModeloPlantillaE) {
        this.listaModeloPlantillaE = listaModeloPlantillaE;
    }

    public List<Registro> getListatipoContrato() {
        return listatipoContrato;
    }

    public void setListatipoContrato(List<Registro> listatipoContrato) {
        this.listatipoContrato = listatipoContrato;
    }

    public String getNombreAux() {
        return nombreAux;
    }

    public void setNombreAux(String nombreAux) {
        this.nombreAux = nombreAux;
    }

    public String getAuxiliar() {
        return auxiliar;
    }

    public void setAuxiliar(String auxiliar) {
        this.auxiliar = auxiliar;
    }

    private String retornarString(Registro reg, String campo) {
        return SysmanFunciones.validarCampoVacio(reg.getCampos(), campo) ? ""
            : reg.getCampos().get(campo).toString();
    }

}
