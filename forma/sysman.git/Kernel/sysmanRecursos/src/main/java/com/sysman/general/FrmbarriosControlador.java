package com.sysman.general;

import com.sysman.beanbase.BeanBaseContinuoAcmeImpl;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.enums.GenericUrlEnum;
import com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException;
import com.sysman.exception.SystemException;
import com.sysman.general.enums.FrmbarriosControladorEnum;
import com.sysman.general.enums.FrmbarriosControladorUrlEnum;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.event.RowEditEvent;

/**
 *
 * @author lcortes
 * @version 1, 17/09/2015
 * 
 * @author jlramirez
 * @version 2, 04/04/2017, proceso de Refactoring, cambio de texto
 * quemado por texto en bean y modificaciones según especificaciones
 * de SONARLINT
 */
@ManagedBean
@ViewScoped
public class FrmbarriosControlador extends BeanBaseContinuoAcmeImpl {

    private String pais;
    private String departamento;
    private String ciudad;
    private List<Registro> listaPais;
    private List<Registro> listaDepartamento;
    private List<Registro> listaCiudad;
    private List<Registro> listaTipoDivision;
    private List<Registro> listaCodigoAsociacion;

    /**
     * Creates a new instance of FrmbarriosControlador
     */
    public FrmbarriosControlador() {
        super();
        try {
            numFormulario = GeneralCodigoFormaEnum.FRMBARRIOS_CONTROLADOR.getCodigo();
            validarPermisos();
        }
        catch (SysmanException ex) {
            Logger.getLogger(FrmbarriosControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            SessionUtil.redireccionarMenuPermisos();
        }
    }

    @PostConstruct
    public void inicializar() {
        enumBase = GenericUrlEnum.BARRIOS;
        buscarLlave();
        registro = new Registro();
        reasignarOrigen();
        cargarListaPais();
        abrirFormulario();
    }

    @Override
    public void reasignarOrigen() {
        buscarUrls();
        parametrosListado.put(FrmbarriosControladorEnum.PARAM0.getValue(),
                        pais);
        parametrosListado.put(GeneralParameterEnum.DEPARTAMENTO.getName(),
                        departamento);
        parametrosListado.put(GeneralParameterEnum.CIUDAD.getName(), ciudad);
    }

    public List<Registro> getListaPais() {
        return listaPais;
    }

    public void setListaPais(List<Registro> listaPais) {
        this.listaPais = listaPais;
    }

    public List<Registro> getListaDepartamento() {
        return listaDepartamento;
    }

    public void setListaDepartamento(List<Registro> listaDepartamento) {
        this.listaDepartamento = listaDepartamento;
    }

    public List<Registro> getListaCiudad() {
        return listaCiudad;
    }

    public void setListaCiudad(List<Registro> listaCiudad) {
        this.listaCiudad = listaCiudad;
    }

    public List<Registro> getListaTipoDivision() {
        return listaTipoDivision;
    }

    public void setListaTipoDivision(List<Registro> listaTipoDivision) {
        this.listaTipoDivision = listaTipoDivision;
    }

    public List<Registro> getListaCodigoAsociacion() {
        return listaCodigoAsociacion;
    }

    public void setListaCodigoAsociacion(List<Registro> listaCodigoAsociacion) {
        this.listaCodigoAsociacion = listaCodigoAsociacion;
    }

    public String getPais() {
        return pais;
    }

    public void setPais(String pais) {
        this.pais = pais;
    }

    public String getDepartamento() {
        return departamento;
    }

    public void setDepartamento(String departamento) {
        this.departamento = departamento;
    }

    public String getCiudad() {
        return ciudad;
    }

    public void setCiudad(String ciudad) {
        this.ciudad = ciudad;
    }

    public void cargarListaPais() {
        try {
            listaPais = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            FrmbarriosControladorUrlEnum.URL4261
                                                                            .getValue())
                                            .getUrl(), null));
        }
        catch (SystemException e) {
            Logger.getLogger(FrmbarriosControlador.class.getName())
                            .log(Level.SEVERE, null, e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    public void cargarListaDepartamento() {
        Map<String, Object> param = new TreeMap<>();
        param.put(FrmbarriosControladorEnum.PARAM0.getValue(),
                        pais);
        try {
            listaDepartamento = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            FrmbarriosControladorUrlEnum.URL4717
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e) {
            Logger.getLogger(FrmbarriosControlador.class.getName())
                            .log(Level.SEVERE, null, e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    public void cargarListaCiudad() {
        Map<String, Object> param = new TreeMap<>();
        param.put(FrmbarriosControladorEnum.PARAM0.getValue(),
                        pais);
        param.put(GeneralParameterEnum.DEPARTAMENTO.getName(),
                        departamento);

        try {
            listaCiudad = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            FrmbarriosControladorUrlEnum.URL5288
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e) {
            Logger.getLogger(FrmbarriosControlador.class.getName())
                            .log(Level.SEVERE, null, e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    public void cargarListaTipoDivision() {
        try {
            listaTipoDivision = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            FrmbarriosControladorUrlEnum.URL5988
                                                                            .getValue())
                                            .getUrl(), null));
        }
        catch (SystemException e) {
            Logger.getLogger(FrmbarriosControlador.class.getName())
                            .log(Level.SEVERE, null, e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    public void cargarListaCodigoAsociacion() {
        try {
            listaCodigoAsociacion = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            FrmbarriosControladorUrlEnum.URL6466
                                                                            .getValue())
                                            .getUrl(), null));
        }
        catch (SystemException e) {
            Logger.getLogger(FrmbarriosControlador.class.getName())
                            .log(Level.SEVERE, null, e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    public void cambiarCiudad() {
        cargarListaTipoDivision();
        cargarListaCodigoAsociacion();
        reasignarOrigen();
    }

    public void oprimirBTPais() {
        SessionUtil.cargarModal("167", SessionUtil.getModulo());
    }

    public void cambiarDepartamento() {
        cargarListaCiudad();
        cargarListaTipoDivision();
        cargarListaCodigoAsociacion();
        registro.getCampos().put("CODIGO", " ");
        reasignarOrigen();

    }

    public void cambiarPais() {
        departamento = " ";
        ciudad = " ";
        cargarListaDepartamento();
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
        registro.getCampos().put(FrmbarriosControladorEnum.PARAM0.getValue(),
                        pais);
        registro.getCampos().put(GeneralParameterEnum.DEPARTAMENTO.getName(),
                        departamento);
        registro.getCampos().put("CIUDAD", ciudad);
        registro.getCampos().remove("NOMBREDIVISION");
        registro.getCampos().remove("NOMBREASOCIACION");
        registro.getCampos().remove("SECTORLB");

        if ((" ").equals(registro.getCampos().put("CIUDAD", ciudad))) {
            JsfUtil.agregarMensajeAlerta(
                            idioma.getString("TB_TB3043"));
            return false;
        }
        else {
            return true;
        }

        // </CODIGO_DESARROLLADO>
    }

    @Override
    public boolean insertarDespues() {
        // <CODIGO_DESARROLLADO>
        return true;
        // </CODIGO_DESARROLLADO>
    }

    @Override
    public boolean actualizarAntes() {
        // <CODIGO_DESARROLLADO>
        registro.getCampos().remove("NOMBREDIVISION");
        registro.getCampos().remove("NOMBREASOCIACION");
        registro.getCampos().remove("SECTORLB");
        return true;
        // </CODIGO_DESARROLLADO>
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
        return true;
        // </CODIGO_DESARROLLADO>
    }

    @Override
    public boolean eliminarDespues() {
        // <CODIGO_DESARROLLADO>
        return true;
        // </CODIGO_DESARROLLADO>
    }

    @Override
    public void removerCombos() {
        // NO SE IMPLEMENTA
    }

    @Override
    public void asignarValoresRegistro() {
        // NO SE IMPLEMENTA

    }
}
