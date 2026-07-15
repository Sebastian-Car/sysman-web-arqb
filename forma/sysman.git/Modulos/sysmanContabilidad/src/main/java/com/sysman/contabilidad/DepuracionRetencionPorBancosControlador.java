/*-
 * DepuracionRetencionPorBancosControlador.java
 *
 * 1.0
 * 
 * 08/11/2018
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */
package com.sysman.contabilidad;

import com.sysman.bancoproyectos.ejb.EjbBancoProyectosCeroGeneralRemote;
import com.sysman.beanbase.BeanBaseContinuoAcmeImpl;
import com.sysman.contabilidad.ejb.EjbContabilidadSieteRemote;
import com.sysman.contabilidad.enums.DepuracionRetencionPorBancosControladorUrlEnum;
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
import com.sysman.session.utl.ConstantesFacturacionGenEnum;
import com.sysman.util.SysmanFunciones;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.event.RowEditEvent;
import org.primefaces.event.SelectEvent;
import org.primefaces.model.StreamedContent;

/**
 *
 * @version 1.0, 08/11/2018
 * @author bcardenas
 */
@ManagedBean
@ViewScoped
public class DepuracionRetencionPorBancosControlador
                extends BeanBaseContinuoAcmeImpl {
    /**
     * Constante a nivel de clase que almacena el codigo de la
     * compania en la cual inicio sesion el usuario, el valor de esta
     * constante es asignado en el constructor a la variable de sesion
     * correspondiente
     */

    private final String compania;
    private String anio;
    private String mes;
    private String auxiliar;
    private List<Registro> listaAno;
    private RegistroDataModelImpl listaBancoPagoRetencion;
    private RegistroDataModelImpl listaBancoPagoRetencionE;
    private String comprobante;
    private String tipoCte;
    private String auxNombre;
    private String tipoRetencion;
    private String comprobanteRetencion;
    private String anioRetencion;
    private String cuentaAfectada;
    private String fechaRetencion;
    private boolean indicadorPago;

    /**
     * Atributo auxliar el cual es asiganado en el momento que se
     * activa la edicion de un registro. Toma el valor del indice
     * dentro de la grilla del registro seleccionado para editar
     */
    private int indice;

    private StreamedContent archivoDescarga;

    @EJB
    private EjbBancoProyectosCeroGeneralRemote ejbBancoProyectosCeroGeneralRemote;

    @EJB
    private EjbContabilidadSieteRemote ejbContabilidadSieteRemote;

    /**
     * Crea una nueva instancia de
     * DepuracionRetencionPorBancosControlador
     */
    public DepuracionRetencionPorBancosControlador() {
        super();

        compania = SessionUtil.getCompania();
        try {
            numFormulario = GeneralCodigoFormaEnum.DEPURACION_RETENCION_POR_BANCOS_CONTROLADOR
                            .getCodigo();
            validarPermisos();
            // <INI_ADICIONAL>

            Map<String, Object> parametros = SessionUtil.getFlash();
            if (parametros != null) {
                anio = (String) parametros.get(
                                ConstantesFacturacionGenEnum.ANIO.getValue());
                mes = (String) parametros
                                .get(GeneralParameterEnum.MES.getName());
            }
            else {

                anio = String.valueOf(SysmanFunciones.ano(new Date()));
                mes = "1";
            }
            // </INI_ADICIONAL>
        }
        catch (Exception ex) {
            Logger.getLogger(DepuracionRetencionPorBancosControlador.class
                            .getName())
                            .log(Level.SEVERE, null, ex);
            SessionUtil.redireccionarMenuPermisos();
        }
    }

    /**
     * Este metodo se ejecuta justo despues de que el objeto de la
     * clase del Bean ha sido creado, en este se realizan las
     * asignaciones iniciales necesarias para la visualizacion del
     * formulario, como son tablas, origenes de datos, inicializacion
     * de listas y demas necesarios
     */
    @PostConstruct
    public void inicializar() {
        tabla = GenericUrlEnum.DETALLECOMPROBANTECNT.getTable();
        buscarLlave();
        reasignarOrigen();
        registro = new Registro();
        // <CARGAR_LISTA>
        cargarListaAno();

        // </CARGAR_LISTA>
        // <CARGAR_LISTA_COMBO_GRANDE>
        // </CARGAR_LISTA_COMBO_GRANDE>
        abrirFormulario();
    }

    /**
     * Metodo ejecutado cuando se activa la edicion de un registro del
     * formulario
     *
     *
     * @param registro
     * registro del cual se activo la edicion
     */
    public void activarEdicion(Registro registro) {
        indice = listaInicial.getRowIndex();
        comprobante = registro.getCampos()
                        .get(GeneralParameterEnum.COMPROBANTE.getName())
                        .toString();
        tipoCte = registro.getCampos()
                        .get(GeneralParameterEnum.TIPO_CPTE.getName())
                        .toString();
        cargarListaBancoPagoRetencionE();
    }

    @Override
    public void reasignarOrigen() {

        parametrosListado.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
        parametrosListado.put("ANIO",
                        anio);
        parametrosListado.put(GeneralParameterEnum.MES.getName(), mes);

        urlListado = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        DepuracionRetencionPorBancosControladorUrlEnum.URL0002
                                                        .getValue());

        urlActualizacion = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        DepuracionRetencionPorBancosControladorUrlEnum.URL0004
                                                        .getValue());

    }

    // <METODOS_CARGAR_LISTA>
    /**
     * 
     * Carga la lista listaAno
     * 
     */
    public void cargarListaAno() {
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        try {
            listaAno = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            DepuracionRetencionPorBancosControladorUrlEnum.URL0001
                                                                            .getValue())
                                            .getUrl(), param));

        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    /**
     * 
     * Carga la lista listaBancoPagoRetencion
     *
     */
    public void cargarListaBancoPagoRetencionE() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        DepuracionRetencionPorBancosControladorUrlEnum.URL0003
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();

        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.TIPO.getName(), tipoCte);
        param.put(GeneralParameterEnum.CODIGO.getName(), comprobante);

        listaBancoPagoRetencionE = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, "ID");

    }

    public void cambiarBancoPagoRetencionC(int rowNum) {

        // <CODIGO_DESARROLLADO>
        listaInicial.getDatasource().get(rowNum % 10).getCampos()
                        .put("TIPO_PAGO_RETENCION", tipoRetencion);

        listaInicial.getDatasource().get(rowNum % 10).getCampos()
                        .put("COMPROBANTE_PAGO_RETENCION",
                                        comprobanteRetencion);

        listaInicial.getDatasource().get(rowNum % 10).getCampos()
                        .put("ANO_RETENCION", anioRetencion);

        listaInicial.getDatasource().get(rowNum % 10).getCampos()
                        .put("BANCO_RETENCION", cuentaAfectada);

        listaInicial.getDatasource().get(rowNum % 10).getCampos()
                        .put("FECHA_PAGO_RETENCION", fechaRetencion);
        listaInicial.getDatasource().get(rowNum % 10).getCampos()
                        .put("PAGO_RETENCION", indicadorPago);

        // </CODIGO_DESARROLLADO>
    }

    // </METODOS_CAMBIAR>
    // <METODOS_COMBOS_GRANDES>
    // </METODOS_CAMBIAR>
    // <METODOS_COMBOS_GRANDES>
    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaBancoPagoRetencion
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaBancoPagoRetencion(SelectEvent event) {
        // </CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaBancoPagoRetencion
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaBancoPagoRetencionE(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();

        tipoRetencion = SysmanFunciones
                        .nvl(registroAux.getCampos()
                                        .get("TIPO_CPTE"),
                                        "")
                        .toString();

        registro.getCampos().put("TIPO_PAGO_RETENCION", tipoRetencion);

        comprobanteRetencion = SysmanFunciones
                        .nvl(registroAux.getCampos()
                                        .get("COMPROBANTE"),
                                        "")
                        .toString();

        registro.getCampos().put("COMPROBANTE_PAGO_RETENCION",
                        comprobanteRetencion);

        anioRetencion = SysmanFunciones
                        .nvl(registroAux.getCampos()
                                        .get("ANO"),
                                        "")
                        .toString();

        registro.getCampos().put("ANO_RETENCION", anioRetencion);

        cuentaAfectada = SysmanFunciones
                        .nvl(registroAux.getCampos().get("CUENTA"), "")
                        .toString();

        registro.getCampos().put("BANCO_RETENCION", cuentaAfectada);

        fechaRetencion = SysmanFunciones
                        .nvl(registroAux.getCampos().get("FECHA"), "")
                        .toString();

        registro.getCampos().put("FECHA_PAGO_RETENCION", fechaRetencion);

        if (tipoRetencion.isEmpty()) {
            indicadorPago = false;
        }
        else {
            indicadorPago = true;
        }

    }

    /**
     * 
     * Metodo ejecutado al oprimir el boton btnAceptar en la vista
     *
     *
     */
    public void oprimirbtnAceptar() {
        // <CODIGO_DESARROLLADO>
        // depurarPagoRetenciones
        try {
            ejbContabilidadSieteRemote.depurarPagoRetenciones(compania,
                            Integer.parseInt(anio), Integer.parseInt(mes));

            JsfUtil.agregarMensajeInformativo(
                            idioma.getString(
                                            "TB_TB4250"));
        }
        catch (NumberFormatException | SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado al cambiar el control Ano
     * 
     */
    public void cambiarAno() {
        // <CODIGO_DESARROLLADO>
        reasignarOrigen();
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado al cambiar el control Mes
     * 
     * 
     */
    public void cambiarMes() {
        // <CODIGO_DESARROLLADO>
        reasignarOrigen();
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Este metodo es invocado el metodo inicializar, se ejecutan las
     * acciones a tener en cuenta en el momento de apertura del
     * formulario
     */
    @Override
    public void abrirFormulario() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>

    }

    // <SET_GET_ATRIBUTOS>
    /**
     * Retorna la variable anio
     * 
     * @return anio
     */
    public String getAnio() {
        return anio;
    }

    /**
     * Asigna la variable anio
     * 
     * @param anio
     * Variable a asignar en anio
     */
    public void setAnio(String anio) {
        this.anio = anio;
    }

    /**
     * Retorna la variable mes
     * 
     * @return mes
     */
    public String getMes() {
        return mes;
    }

    /**
     * Asigna la variable mes
     * 
     * @param mes
     * Variable a asignar en mes
     */
    public void setMes(String mes) {
        this.mes = mes;
    }

    // </SET_GET_ATRIBUTOS>
    // <SET_GET_PARAMETROS>
    // </SET_GET_PARAMETROS>
    // <SET_GET_LISTAS>
    /**
     * Retorna la lista listaAno
     * 
     * @return listaAno
     */
    public List<Registro> getListaAno() {
        return listaAno;
    }

    /**
     * Asigna la lista listaAno
     * 
     * @param listaAno
     * Variable a asignar en listaAno
     */
    public void setListaAno(List<Registro> listaAno) {
        this.listaAno = listaAno;
    }
    // </SET_GET_LISTAS>
    // <SET_GET_LISTAS_COMBO_GRANDE>
    // </SET_GET_LISTAS_COMBO_GRANDE>

    @Override
    public void asignarValoresRegistro() {
        // </CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>

    }

    @Override
    public void removerCombos() {

        registro.getCampos().remove(GeneralParameterEnum.COMPANIA.getName());
        registro.getCampos().remove("ANO");
        registro.getCampos().remove("TIPO_CPTE");
        registro.getCampos().remove("CONSECUTIVO");
        registro.getCampos().remove("COMPROBANTE");
        registro.getCampos().remove("CUENTA");
        registro.getCampos().remove("VALOR_CREDITO");
        registro.getCampos().remove("FECHA");
        registro.getCampos().remove("DEPURADO");

    }

    @Override
    public void cancelarEdicion(RowEditEvent event) {

        // </CODIGO_DESARROLLADO>
        listaInicial.load();
        // </CODIGO_DESARROLLADO>

    }

    @Override
    public boolean insertarAntes() {
        // </CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
        return true;
    }

    @Override
    public boolean insertarDespues() {
        // </CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
        return true;
    }

    @Override
    public boolean actualizarAntes() {
        // </CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>

        return true;
    }

    @Override
    public boolean actualizarDespues() {
        // </CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
        return true;
    }

    @Override
    public boolean eliminarAntes() {
        // </CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
        return false;
    }

    @Override
    public boolean eliminarDespues() {
        // </CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
        return false;
    }

    /**
     * @return the auxiliar
     */
    public String getAuxiliar() {
        return auxiliar;
    }

    /**
     * @param auxiliar
     * the auxiliar to set
     */
    public void setAuxiliar(String auxiliar) {
        this.auxiliar = auxiliar;
    }

    /**
     * @return the listaBancoPagoRetencion
     */
    public RegistroDataModelImpl getListaBancoPagoRetencion() {
        return listaBancoPagoRetencion;
    }

    /**
     * @param listaBancoPagoRetencion
     * the listaBancoPagoRetencion to set
     */
    public void setListaBancoPagoRetencion(
        RegistroDataModelImpl listaBancoPagoRetencion) {
        this.listaBancoPagoRetencion = listaBancoPagoRetencion;
    }

    /**
     * @return the listaBancoPagoRetencionE
     */
    public RegistroDataModelImpl getListaBancoPagoRetencionE() {
        return listaBancoPagoRetencionE;
    }

    /**
     * @param listaBancoPagoRetencionE
     * the listaBancoPagoRetencionE to set
     */
    public void setListaBancoPagoRetencionE(
        RegistroDataModelImpl listaBancoPagoRetencionE) {
        this.listaBancoPagoRetencionE = listaBancoPagoRetencionE;
    }

    /**
     * @return the indice
     */
    public int getIndice() {
        return indice;
    }

    /**
     * @param indice
     * the indice to set
     */
    public void setIndice(int indice) {
        this.indice = indice;
    }

    /**
     * @return the comprobante
     */
    public String getComprobante() {
        return comprobante;
    }

    /**
     * @param comprobante
     * the comprobante to set
     */
    public void setComprobante(String comprobante) {
        this.comprobante = comprobante;
    }

    /**
     * @return the tipoCte
     */
    public String getTipoCte() {
        return tipoCte;
    }

    /**
     * @param tipoCte
     * the tipoCte to set
     */
    public void setTipoCte(String tipoCte) {
        this.tipoCte = tipoCte;
    }

    /**
     * @return the auxNombre
     */
    public String getAuxNombre() {
        return auxNombre;
    }

    /**
     * @param auxNombre
     * the auxNombre to set
     */
    public void setAuxNombre(String auxNombre) {
        this.auxNombre = auxNombre;
    }

    /**
     * @return the archivoDescarga
     */
    public StreamedContent getArchivoDescarga() {
        return archivoDescarga;
    }

    /**
     * @param archivoDescarga
     * the archivoDescarga to set
     */
    public void setArchivoDescarga(StreamedContent archivoDescarga) {
        this.archivoDescarga = archivoDescarga;
    }

}
