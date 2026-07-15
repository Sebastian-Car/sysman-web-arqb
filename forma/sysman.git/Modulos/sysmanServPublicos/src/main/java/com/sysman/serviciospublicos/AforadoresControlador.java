package com.sysman.serviciospublicos;

import com.sysman.beanbase.BeanBaseContinuoAcmeImpl;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.enums.GenericUrlEnum;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.serviciospublicos.enums.AforadoresControladorUrlEnum;

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
 * @author jguerrero
 * @version 1, 01/08/2016
 *
 * -- Modificado por lcortes 15/05/2017 11:40. Refactorizacion de
 * codigo de las listas para utilizar dss.
 */
@ManagedBean
@ViewScoped
public class AforadoresControlador extends BeanBaseContinuoAcmeImpl {
    private final String compania;
    private final String cApellido1;
    private final String cApellido2;
    private final String cNombres;
    private final String cSucursal;
    // <DECLARAR_ATRIBUTOS>
    private String apellido1;
    private String apellido2;
    private String nombres;
    private String sucursal;
    // </DECLARAR_ATRIBUTOS>
    // <DECLARAR_PARAMETROS>
    // </DECLARAR_PARAMETROS>
    // <DECLARAR_LISTAS>
    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>
    private RegistroDataModelImpl listaNit;
    private RegistroDataModelImpl listaNitE;
    private String auxiliar;

    // </DECLARAR_LISTAS_COMBO_GRANDE>

    /**
     * Creates a new instance of AforadoresControlador
     */
    public AforadoresControlador() {
        super();
        compania = SessionUtil.getCompania();
        cApellido1 = "APELLIDO1";
        cApellido2 = "APELLIDO2";
        cNombres = GeneralParameterEnum.NOMBRES.getName();
        cSucursal = GeneralParameterEnum.SUCURSAL.getName();
        try {
            numFormulario = GeneralCodigoFormaEnum.AFORADORES_CONTROLADOR.getCodigo();
            validarPermisos();      
        }
        catch (Exception ex) {
            Logger.getLogger(AforadoresControlador.class.getName())
            .log(Level.SEVERE, null, ex);
            SessionUtil.redireccionarMenuPermisos();
        }
    }

    @PostConstruct
    public void inicializar() {

        enumBase = GenericUrlEnum.SP_AFORADORES;
        buscarLlave();
        reasignarOrigen();     
        registro = new Registro();     
        cargarListaNit();
        cargarListaNitE();         
        abrirFormulario();
    }

    @Override
    public void reasignarOrigen() {
        buscarUrls();
        parametrosListado.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
    }

    // <METODOS_CARGAR_LISTA>
    public void cargarListaNit() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        AforadoresControladorUrlEnum.URL3973
                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        listaNit = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, "NIT");
    }

    public void cargarListaNitE() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        AforadoresControladorUrlEnum.URL4740
                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        listaNitE = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, "NIT");
    }

    // </METODOS_CARGAR_LISTA>
    // <METODOS_BOTONES>
    // </METODOS_BOTONES>
    // <METODOS_CAMBIAR>
    public void cambiarNitC(int rowNum) {
        // <CODIGO_DESARROLLADO>
        listaInicial.getDatasource().get(rowNum % 10).getCampos().put(cNombres,
                        nombres);
        listaInicial.getDatasource().get(rowNum % 10).getCampos()
        .put(cApellido1, apellido1);
        listaInicial.getDatasource().get(rowNum % 10).getCampos()
        .put(cApellido2, apellido2);
        listaInicial.getDatasource().get(rowNum % 10).getCampos()
        .put(cSucursal, sucursal);
        // </CODIGO_DESARROLLADO>
    }

    // </METODOS_CAMBIAR>
    // <METODOS_COMBOS_GRANDES>
    public void onRowSelectNit(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        registro.getCampos().put("NIT", registroAux.getCampos().get("NIT"));
        registro.getCampos().put(cNombres,
                        registroAux.getCampos().get(cNombres));
        registro.getCampos().put(cApellido1,
                        registroAux.getCampos().get(cApellido1));
        registro.getCampos().put(cApellido2,
                        registroAux.getCampos().get(cApellido2));
        registro.getCampos().put(cSucursal,
                        registroAux.getCampos().get(cSucursal));

    }

    public void onRowSelectNitE(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        auxiliar = registroAux.getCampos().get("NIT").toString();
        nombres = registroAux.getCampos().get(cNombres).toString();
        apellido1 = registroAux.getCampos().get(cApellido1).toString();
        apellido2 = registroAux.getCampos().get(cApellido2).toString();
        sucursal = registroAux.getCampos().get(cSucursal).toString();
    }

    // </METODOS_COMBOS_GRANDES>
    @Override
    public void abrirFormulario() {
        // <CODIGO_DESARROLLADO>
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
        registro.getCampos().remove(cNombres);
        registro.getCampos().remove(cApellido1);
        registro.getCampos().remove(cApellido2);
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
        registro.getCampos().remove(cNombres);
        registro.getCampos().remove(cApellido1);
        registro.getCampos().remove(cApellido2);
        registro.getCampos().remove(GeneralParameterEnum.COMPANIA.getName());
        registro.getCampos().remove(GeneralParameterEnum.CREATED_BY.getName());
        registro.getCampos()
        .remove(GeneralParameterEnum.DATE_CREATED.getName());
        // </CODIGO_DESARROLLADO>
    }

    @Override
    public void asignarValoresRegistro() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    // <SET_GET_ATRIBUTOS>
    public String getApellido1() {
        return apellido1;
    }

    public void setApellido1(String apellido1) {
        this.apellido1 = apellido1;
    }

    public String getApellido2() {
        return apellido2;
    }

    public void setApellido2(String apellido2) {
        this.apellido2 = apellido2;
    }

    public String getNombres() {
        return nombres;
    }

    public void setNombres(String nombres) {
        this.nombres = nombres;
    }

    public String getSucursal() {
        return sucursal;
    }

    public void setSucursal(String sucursal) {
        this.sucursal = sucursal;
    }

    public RegistroDataModelImpl getListaNit() {
        return listaNit;
    }

    public void setListaNit(RegistroDataModelImpl listaNit) {
        this.listaNit = listaNit;
    }

    public RegistroDataModelImpl getListaNitE() {
        return listaNitE;
    }

    public void setListaNitE(RegistroDataModelImpl listaNitE) {
        this.listaNitE = listaNitE;
    }

    public String getAuxiliar() {
        return auxiliar;
    }

    public void setAuxiliar(String auxiliar) {
        this.auxiliar = auxiliar;
    }
}
