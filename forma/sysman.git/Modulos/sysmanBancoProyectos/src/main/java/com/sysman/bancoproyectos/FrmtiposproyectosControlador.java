
package com.sysman.bancoproyectos;

import com.sysman.bancoproyectos.enums.FrmtiposproyectosControladorEnum;
import com.sysman.bancoproyectos.enums.FrmtiposproyectosControladorUrlEnum;
import com.sysman.beanbase.BeanBaseContinuoAcmeImpl;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.enums.GenericUrlEnum;
import com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.services.RegistroDataModelImpl;

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
 * @author dmaldonado
 * @version 1, 13/08/2015
 *
 * @modified lcortes
 * @version 2, 22/09/2017. Refactorización de codigo a las consultas
 * del origen de datos y de las listas para usar dss y revision de
 * observaciones de la herramienta SonarLint.
 */
@ManagedBean
@ViewScoped
public class FrmtiposproyectosControlador extends BeanBaseContinuoAcmeImpl {

    private final String compania;
    private final String unidadCons;
    private String nombreUnidad;
    private RegistroDataModelImpl listaUnidad;
    private RegistroDataModelImpl listaUnidadE;
    private String auxiliar;

    /**
     * Creates a new instance of FrmtiposproyectosControlador
     */
    public FrmtiposproyectosControlador() {
        super();
        compania = SessionUtil.getCompania();
        unidadCons = GeneralParameterEnum.UNIDAD.getName();

        try {
            numFormulario = GeneralCodigoFormaEnum.FRMTIPOSPROYECTOS_CONTROLADOR
                            .getCodigo();
            validarPermisos();
        }
        catch (SysmanException ex) {
            Logger.getLogger(FrmtiposproyectosControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            SessionUtil.redireccionarMenuPermisos();
        }
    }

    @PostConstruct
    public void inicializar() {
        enumBase = GenericUrlEnum.TIPOSPROYECTO;
        buscarLlave();
        reasignarOrigen();
        registro = new Registro();
        cargarListaUnidad();
        cargarListaUnidadE();
        abrirFormulario();
    }

    @Override
    public void reasignarOrigen() {
        buscarUrls();
        parametrosListado.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);

    }

    public RegistroDataModelImpl getListaUnidad() {
        return listaUnidad;
    }

    public void setListaUnidad(RegistroDataModelImpl listaUnidad) {
        this.listaUnidad = listaUnidad;
    }

    public RegistroDataModelImpl getListaUnidadE() {
        return listaUnidadE;
    }

    public void setListaUnidadE(RegistroDataModelImpl listaUnidadE) {
        this.listaUnidadE = listaUnidadE;
    }

    public String getAuxiliar() {
        return auxiliar;
    }

    public void setAuxiliar(String auxiliar) {
        this.auxiliar = auxiliar;
    }

    public String getNombreUnidad() {
        return nombreUnidad;
    }

    public void setNombreUnidad(String nombreUnidad) {
        this.nombreUnidad = nombreUnidad;
    }

    public void cargarListaUnidad() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        FrmtiposproyectosControladorUrlEnum.URL3414
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        listaUnidad = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, unidadCons);
    }

    public void cargarListaUnidadE() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        FrmtiposproyectosControladorUrlEnum.URL3934
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        listaUnidadE = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, unidadCons);
    }

    public void seleccionarFilaUnidad(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        registro.getCampos().put(unidadCons,
                        registroAux.getCampos().get(unidadCons));
        nombreUnidad = (String) registroAux.getCampos()
                        .get(GeneralParameterEnum.NOMBRE.getName());
    }

    public void seleccionarFilaUnidadE(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        auxiliar = (String) registroAux.getCampos().get(unidadCons);
    }

    @Override
    public void abrirFormulario() {
        // NO ESTA IMPLEMENTADO
    }

    @Override
    public void cancelarEdicion(RowEditEvent event) {
        listaInicial.load();
    }

    @Override
    public boolean insertarAntes() {
        registro.getCampos().put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
        return true;
    }

    @Override
    public boolean insertarDespues() {
        return true;
    }

    @Override
    public boolean actualizarAntes() {
        registro.getCampos()
                        .remove(FrmtiposproyectosControladorEnum.NOMBRE_UNIDAD
                                        .getValue());
        return true;
    }

    @Override
    public boolean actualizarDespues() {
        return true;
    }

    @Override
    public boolean eliminarAntes() {
        return true;
    }

    @Override
    public boolean eliminarDespues() {
        return true;
    }

    @Override
    public void removerCombos() {
        // NO ESTA IMPLEMENTADO
        registro.getCampos().remove(GeneralParameterEnum.COMPANIA.getName());
    }

    @Override
    public void asignarValoresRegistro() {
        // NO ESTA IMPLEMENTADO
    }
}