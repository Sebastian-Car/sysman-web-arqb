package com.sysman.almacen;

import com.sysman.almacen.enums.AfectacioncontratosControladorEnum;
import com.sysman.almacen.enums.AfectacioncontratosControladorUrlEnum;
import com.sysman.beanbase.BeanBaseContinuoAcmeImpl;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.services.RegistroDataModelImpl;

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
 * @version 1, 01/12/2015
 * @author yrojas
 * @version 2, 26/04/2017 Se cambiaron las consultas por la invocacion
 * de los DSS. Se cambio controlador segun especificaciones del
 * SonarLint.
 *
 * Modificado por lcortes 13/06/2017. Se reemplaza el valor del
 * atributo numFormulario por el enumerado correspondiente.
 */
@ManagedBean
@ViewScoped
public class AfectacioncontratosControlador extends BeanBaseContinuoAcmeImpl {

    private final String compania;
    private RegistroDataModelImpl listatipo;
    private RegistroDataModelImpl listatipoE;
    private String auxiliar;
    private String codigo;

    /**
     * Creates a new instance of AfectacioncontratosControlador
     */
    public AfectacioncontratosControlador() {
        super();
        compania = SessionUtil.getCompania();
        try {
            numFormulario = GeneralCodigoFormaEnum.AFECTACIONCONTRATOS_CONTROLADOR
                            .getCodigo();
            validarPermisos();
        }
        catch (SysmanException ex) {
            Logger.getLogger(AfectacioncontratosControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            SessionUtil.redireccionarMenuPermisos();
        }
    }

    @PostConstruct
    public void inicializar() {
        tabla = AfectacioncontratosControladorEnum.PARAM0.getValue();
        buscarLlave();
        reasignarOrigen();
        registro = new Registro(new HashMap<String, Object>());
        cargarListatipo();
        cargarListatipoE();
        abrirFormulario();
    }

    @Override
    public void reasignarOrigen() {
        urlListado = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        AfectacioncontratosControladorUrlEnum.URL11959
                                                        .getValue());
        urlActualizacion = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        AfectacioncontratosControladorUrlEnum.URL12000
                                                        .getValue());
        urlEliminacion = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        AfectacioncontratosControladorUrlEnum.URL12012
                                                        .getValue());
        parametrosListado.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
        parametrosListado.put(GeneralParameterEnum.CODIGO.getName(), codigo);
    }

    public void cargarListatipo() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        AfectacioncontratosControladorUrlEnum.URL1812
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        listatipo = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param, true,
                        GeneralParameterEnum.CODIGO.getName());
    }

    public void cargarListatipoE() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        AfectacioncontratosControladorUrlEnum.URL2143
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        listatipoE = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param, true,
                        GeneralParameterEnum.CODIGO.getName());
    }

    public void cambiartipoC(int rowNum) {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    public void seleccionarFilatipo(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        codigo = registroAux.getCampos()
                        .get(GeneralParameterEnum.CODIGO.getName()).toString();
        registro.getCampos().put(GeneralParameterEnum.CODIGO.getName(), codigo);
        registro.getCampos().put(GeneralParameterEnum.NOMBRE.getName(),
                        registroAux.getCampos().get(
                                        GeneralParameterEnum.NOMBRE.getName()));
        reasignarOrigen();
    }

    public void onRowSelecttipoE(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        auxiliar = registroAux.getCampos()
                        .get(GeneralParameterEnum.CODIGO.getName()).toString();
    }

    @Override
    public void abrirFormulario() {
        // <CODIGO_DESARROLLADO>
        registro.getCampos().put(GeneralParameterEnum.CODIGO.getName(), codigo);
        registro.getCampos().put(GeneralParameterEnum.NOMBRE.getName(),
                        registro.getCampos().get(
                                        GeneralParameterEnum.NOMBRE.getName()));
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
        registro.getCampos().remove("VACIALB");
        registro.getCampos().remove("NOMBRETERCERO");
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
        registro.getCampos().remove("VACIALB");
        registro.getCampos().remove("NOMBRETERCERO");
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

    @Override
    public void asignarValoresRegistro() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    public RegistroDataModelImpl getListatipo() {
        return listatipo;
    }

    public void setListatipo(RegistroDataModelImpl listatipo) {
        this.listatipo = listatipo;
    }

    public RegistroDataModelImpl getListatipoE() {
        return listatipoE;
    }

    public void setListatipoE(RegistroDataModelImpl listatipoE) {
        this.listatipoE = listatipoE;
    }

    public String getAuxiliar() {
        return auxiliar;
    }

    public void setAuxiliar(String auxiliar) {
        this.auxiliar = auxiliar;
    }
}
