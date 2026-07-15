/*-
 * SaldocreditosControlador.java
 *
 * 1.0
 *
 * 20/09/2016
 *
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */
package com.sysman.serviciospublicos;

import com.sysman.beanbase.BeanBaseContinuoAcmeImpl;
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
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.serviciospublicos.enums.SaldocreditosControladorUrlEnum;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.event.RowEditEvent;
import org.primefaces.event.SelectEvent;

/**
 * Controlador del formulario SaldoCredito, llamado desde la ruta
 * Facturacion\Novedades\Saldo credito
 *
 * @version 1.0, 20/09/2016
 * @author dmaldonado
 *
 * @author eamaya
 * @version 3.0, 13/06/2017 Se cambió el llamado del codigo del
 * formulario
 *
 * -- Modificado por lcortes 16/06/2017. Refactorizacion de las
 * consultas de las listas para usar dss y eliminacion de conexiones a
 * la base de datos.
 */
@ManagedBean
@ViewScoped
public class SaldocreditosControlador extends BeanBaseContinuoAcmeImpl {
    /**
     * Atributo de la clase que almacena la compania.
     */
    private final String compania;
    // <DECLARAR_ATRIBUTOS>
    /**
     * Atributo de la clase que almacena el ciclo seleccionado.
     */
    private String ciclo;
    /**
     * Atributo de la clase que almacena el codigo de ruta
     * seleccionado.
     */
    private String codigoRuta;
    /**
     * Atributo de la clase que almacena el nombre del usuario
     * perteneciente al codigo de ruta seleccionado.
     */
    private String nombreUsuario;
    // </DECLARAR_ATRIBUTOS>
    // <DECLARAR_PARAMETROS>
    // </DECLARAR_PARAMETROS>
    // <DECLARAR_LISTAS>
    /**
     * Atributo de la clase que almacena la lista del combo Ciclo.
     */
    private List<Registro> listaCiclo;
    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>
    /**
     * Atributo de la clase que almacena la lista del combo
     * CodigoRuta.
     */
    private RegistroDataModelImpl listaCodigoRuta;
    /**
     * Atributo de la clase para asignar los valores que llegan al
     * seleccionar el valor de un combo grande.
     */
    private String auxiliar;

    // </DECLARAR_LISTAS_COMBO_GRANDE>
    /**
     * Creates a new instance of SaldocreditosControlador
     */
    public SaldocreditosControlador() {
        super();
        compania = SessionUtil.getCompania();
        try {
            numFormulario = GeneralCodigoFormaEnum.SALDOCREDITOS_CONTROLADOR
                            .getCodigo();

            validarPermisos();
            // <INI_ADICIONAL>
            // </INI_ADICIONAL>
        }
        catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
            SessionUtil.redireccionarMenuPermisos();
        }
    }

    @PostConstruct
    public void inicializar() {
        tabla = GenericUrlEnum.SP_BANCOS.getTable();
        registro = new Registro();
        // <CARGAR_LISTA>
        cargarListaCiclo();
        // </CARGAR_LISTA>
        // <CARGAR_LISTA_COMBO_GRANDE>
        // </CARGAR_LISTA_COMBO_GRANDE>
        abrirFormulario();
    }

    @Override
    public void reasignarOrigen() {

        parametrosListado.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
        parametrosListado.put(GeneralParameterEnum.CICLO.getName(), ciclo);
        parametrosListado.put(GeneralParameterEnum.CODIGORUTA.getName(),
                        codigoRuta);

        urlListado = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        SaldocreditosControladorUrlEnum.URL0001
                                                        .getValue());

    }

    // <METODOS_CARGAR_LISTA>
    public void cargarListaCiclo() {
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        try {
            listaCiclo = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            SaldocreditosControladorUrlEnum.URL4950
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    public void cargarListaCodigoRuta() {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        SaldocreditosControladorUrlEnum.URL5229
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.CICLO.getName(), ciclo);

        listaCodigoRuta = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, GeneralParameterEnum.CODIGORUTA.getName());
    }

    // </METODOS_CARGAR_LISTA>
    // <METODOS_BOTONES>
    // </METODOS_BOTONES>
    // <METODOS_CAMBIAR>
    public void cambiarCiclo() {
        // <CODIGO_DESARROLLADO>
        if (ciclo == null) {
            ciclo = "''";
        }
        cargarListaCodigoRuta();
        codigoRuta = null;
        nombreUsuario = "";
        reasignarOrigen();
        // </CODIGO_DESARROLLADO>
    }

    // </METODOS_CAMBIAR>
    // <METODOS_COMBOS_GRANDES>
    public void seleccionarFilaCodigoRuta(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        codigoRuta = registroAux.getCampos()
                        .get(GeneralParameterEnum.CODIGORUTA.getName())
                        .toString();
        nombreUsuario = registroAux.getCampos()
                        .get(GeneralParameterEnum.NOMBRE.getName()).toString();
        reasignarOrigen();

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
        // </CODIGO_DESARROLLADO>
    }

    @Override
    public void asignarValoresRegistro() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    // <SET_GET_ATRIBUTOS>
    public String getCiclo() {
        return ciclo;
    }

    public void setCiclo(String ciclo) {
        this.ciclo = ciclo;
    }

    public String getCodigoRuta() {
        return codigoRuta;
    }

    public void setCodigoRuta(String codigoRuta) {
        this.codigoRuta = codigoRuta;
    }

    public String getNombreUsuario() {
        return nombreUsuario;
    }

    public void setNombreUsuario(String nombreUsuario) {
        this.nombreUsuario = nombreUsuario;
    }

    // </SET_GET_ATRIBUTOS>
    // <SET_GET_PARAMETROS>
    // </SET_GET_PARAMETROS>
    // <SET_GET_LISTAS>
    public List<Registro> getListaCiclo() {
        return listaCiclo;
    }

    public void setListaCiclo(List<Registro> listaCiclo) {
        this.listaCiclo = listaCiclo;
    }

    // </SET_GET_LISTAS>
    // <SET_GET_LISTAS_COMBO_GRANDE>
    public RegistroDataModelImpl getListaCodigoRuta() {
        return listaCodigoRuta;
    }

    public void setListaCodigoRuta(RegistroDataModelImpl listaCodigoRuta) {
        this.listaCodigoRuta = listaCodigoRuta;
    }

    public String getAuxiliar() {
        return auxiliar;
    }

    public void setAuxiliar(String auxiliar) {
        this.auxiliar = auxiliar;
    }
    // </SET_GET_LISTAS_COMBO_GRANDE>
}
