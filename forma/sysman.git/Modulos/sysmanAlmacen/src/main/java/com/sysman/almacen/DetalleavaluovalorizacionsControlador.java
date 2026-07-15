package com.sysman.almacen;

import com.sysman.almacen.enums.DetalleavaluovalorizacionsControladorUrlEnum;
import com.sysman.beanbase.BeanBaseContinuoAcmeImpl;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.enums.GenericUrlEnum;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.util.SysmanFunciones;

import java.util.Date;
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
 * @version 1, 04/12/2015
 * 
 * @author jlramirez
 * @version 2, 26/04/2017, se realizo refactoring
 */
@ManagedBean
@ViewScoped
public class DetalleavaluovalorizacionsControlador
                extends BeanBaseContinuoAcmeImpl {

    private final String compania;
    /**
     * Constante que identifica el nombre del campo NOMELEMENTO
     */
    private final String campoNomElemento;
    /**
     * Constante que identifica el nombre del campo NOMSERIE
     */
    private final String campoNomSerie;
    private String elemento;
    private String serie;
    private RegistroDataModelImpl listaElemento;
    private RegistroDataModelImpl listaSerie;
    private String auxiliar;
    private String nomElemento;
    private String nomSerie;

    /**
     * Creates a new instance of DetalleavaluovalorizacionsControlador
     */
    public DetalleavaluovalorizacionsControlador() {
        super();
        numFormulario = GeneralCodigoFormaEnum.DETALLEAVALUOVALORIZACIONS_CONTROLADOR
                        .getCodigo();
        compania = SessionUtil.getCompania();
        campoNomElemento = "NOMELEMENTO";
        campoNomSerie = "NOMSERIE";
        try {
            validarPermisos();
        }
        catch (Exception ex) {
            Logger.getLogger(DetalleavaluovalorizacionsControlador.class
                            .getName()).log(Level.SEVERE, null, ex);
            SessionUtil.redireccionarMenuPermisos();
        }
    }

    @PostConstruct
    public void inicializar() {
        enumBase = GenericUrlEnum.VALORIZACIONDEVOLUTIVO;
        buscarLlave();
        registro = new Registro(new HashMap<String, Object>());
        cargarListaElemento();
        cargarListaSerie();
        abrirFormulario();
        registro.getCampos().put("PERIODO", new Date());
        registro.getCampos().put("FECHAVALORIZACION", new Date());
        registro.getCampos().put("FECHAAVALUO", new Date());
    }

    @Override
    public void reasignarOrigen() {
        buscarUrls();
        parametrosListado.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
        parametrosListado.put(GeneralParameterEnum.ELEMENTO.getName(),
                        elemento);
        parametrosListado.put(GeneralParameterEnum.SERIE.getName(), serie);
    }

    public RegistroDataModelImpl getListaElemento() {
        return listaElemento;
    }

    public void setListaELEMENTO(RegistroDataModelImpl listaElemento) {
        this.listaElemento = listaElemento;
    }

    public RegistroDataModelImpl getListaSerie() {
        return listaSerie;
    }

    public void setListaSerie(RegistroDataModelImpl listaSerie) {
        this.listaSerie = listaSerie;
    }

    public String getAuxiliar() {
        return auxiliar;
    }

    public void setAuxiliar(String auxiliar) {
        this.auxiliar = auxiliar;
    }

    public String getElemento() {
        return elemento;
    }

    public void setElemento(String elemento) {
        this.elemento = elemento;
    }

    public String getSerie() {
        return serie;
    }

    public void setSerie(String serie) {
        this.serie = serie;
    }

    public String getNomElemento() {
        return nomElemento;
    }

    public void setNomElemento(String nomElemento) {
        this.nomElemento = nomElemento;
    }

    public String getNomSerie() {
        return nomSerie;
    }

    public void setNomSerie(String nomSerie) {
        this.nomSerie = nomSerie;
    }

    public void cargarListaElemento() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        DetalleavaluovalorizacionsControladorUrlEnum.URL5063
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        listaElemento = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, GeneralParameterEnum.ELEMENTO.getName());
    }

    public void cargarListaSerie() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        DetalleavaluovalorizacionsControladorUrlEnum.URL6151
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.ELEMENTO.getName(), elemento);

        listaSerie = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, GeneralParameterEnum.SERIE.getName());
    }

    public void cambiarNombreElemento() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    public void seleccionarFilaElemento(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        elemento = SysmanFunciones
                        .nvl(registroAux.getCampos()
                                        .get(GeneralParameterEnum.ELEMENTO
                                                        .getName()),
                                        "")
                        .toString();
        nomElemento = SysmanFunciones
                        .nvl(registroAux.getCampos().get(campoNomElemento), "")
                        .toString();
        registro.getCampos().put(campoNomElemento, nomElemento);
        serie = null;
        nomSerie = "";
        registro.getCampos().put(campoNomSerie, "");
        listaInicial = null;
        cargarListaSerie();
    }

    public void seleccionarFilaSerie(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        serie = SysmanFunciones.nvl(
                        registroAux.getCampos().get(
                                        GeneralParameterEnum.SERIE.getName()),
                        "")
                        .toString();
        nomSerie = SysmanFunciones
                        .nvl(registroAux.getCampos().get(campoNomSerie), "")
                        .toString();
        registro.getCampos().put(campoNomSerie, nomSerie);
        cargado = false;
        reasignarOrigen();

    }

    @Override
    public void abrirFormulario() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    @Override
    public void cancelarEdicion(RowEditEvent event) {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    @Override
    public boolean insertarAntes() {
        if (SysmanFunciones.validarVariableVacio(elemento)
            || SysmanFunciones.validarVariableVacio(serie)) {
            JsfUtil.agregarMensajeAlerta(
                            idioma.getString("TB_TB2011"));
            return false;
        }
        registro.getCampos().put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
        registro.getCampos().put(GeneralParameterEnum.ELEMENTO.getName(),
                        elemento);
        registro.getCampos().put(GeneralParameterEnum.SERIE.getName(), serie);
        registro.getCampos().remove(campoNomElemento);
        registro.getCampos().remove(campoNomSerie);
        return true;
    }

    @Override
    public boolean insertarDespues() {
        registro.getCampos().put(campoNomElemento, nomElemento);
        registro.getCampos().put(campoNomSerie, nomSerie);
        return true;
    }

    @Override
    public boolean actualizarAntes() {
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
        registro.getCampos().remove(GeneralParameterEnum.COMPANIA.getName());
        registro.getCampos().remove(GeneralParameterEnum.ELEMENTO.getName());
        registro.getCampos().remove(GeneralParameterEnum.SERIE.getName());
    }

    @Override
    public void asignarValoresRegistro() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }
}
