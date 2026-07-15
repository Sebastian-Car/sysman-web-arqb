/*-
 * DiscoBancoBogotaControlador.java
 *
 * 1.0
 * 
 * 15/05/2019
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */
package com.sysman.nomina;

import com.sysman.beanbase.BeanBaseModal;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.ArchivosBean;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.nomina.ejb.EjbNominaBancosRemote;
import com.sysman.nomina.enums.DiscoBancoBogotaControladorUrlEnum;
import com.sysman.recursos.ejb.EjbSysmanUtilRemote;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.util.SysmanFunciones;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.event.SelectEvent;
/**
 * Clase que permite generar plano de Banco de Bogota
 *
 * @version 1.0, 15/05/2019
 * @author asana
 */
import org.primefaces.model.StreamedContent;

@ManagedBean
@ViewScoped
public class DiscoBancoBogotaControlador extends BeanBaseModal {
    /**
     * Constante a nivel de clase que almacena el codigo de la
     * compania en la cual inicio sesion el usuario, el valor de esta
     * constante es asignado en el constructor a la variable de sesion
     * correspondiente
     */
    private String compania;
    // <DECLARAR_ATRIBUTOS>
    /**
     * Opcion del cuadro de centro de costo
     */
    private String opcionCentroCosto;
    /**
     * Opcion del cuadro banco
     */
    private String opcionBanco;
    private String ano;
    private String mes;
    private String periodo;
    private String banco;
    private String establecimiento;
    /**
     * Codigo del centro de costo seleccionado
     */
    private String centroCosto;
    private Date fecha;
    private String codigoCiudad;
    private String totalGirar;
    private String conceptoPago;
    private String nomina;
    private String parametroCodigo;
    private StreamedContent archivoDescarga;
    // </DECLARAR_ATRIBUTOS>
    // <DECLARAR_PARAMETROS>
    // </DECLARAR_PARAMETROS>
    // <DECLARAR_LISTAS>
    private List<Registro> listaAno;
    private List<Registro> listaMes;
    private List<Registro> listaPeriodo;
    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>
    private RegistroDataModelImpl listaBanco;
    private RegistroDataModelImpl listaEstablecimiento;
    /**
     * Lista que carga los centros de costo
     */
    private RegistroDataModelImpl listaCentroCosto;
    @EJB
    private EjbNominaBancosRemote ejbNominaBancos;
    @EJB
    private EjbSysmanUtilRemote ejbSysmanUtil;

    // </DECLARAR_LISTAS_COMBO_GRANDE>
    /**
     * Crea una nueva instancia de DiscoBancoBogotaControlador
     */
    public DiscoBancoBogotaControlador() {
        super();
        try {
            compania = SessionUtil.getCompania();
            opcionCentroCosto = "1";
            opcionBanco = "1";
            // 2074
            numFormulario = GeneralCodigoFormaEnum.DISCOBANCOBOGOTA_CONTROLADOR
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

    /**
     * Este metodo se ejecuta justo despues de que el objeto de la
     * clase del Bean ha sido creado, en este se realizan las
     * asignaciones iniciales necesarias para la visualizacion del
     * formulario, como son tablas, origenes de datos, inicializacion
     * de listas y demas necesarios
     */
    @PostConstruct
    public void inicializar() {
        // <CARGAR_LISTA>
        ano = validaVacio(SessionUtil.getSessionVar("anioNomina"));
        mes = validaVacio(SessionUtil.getSessionVar("mesNomina"));
        periodo = validaVacio(SessionUtil.getSessionVar("periodoNomina"));
        cargarListaAno();
        cargarListaMes();
        cargarListaPeriodo();
        // </CARGAR_LISTA>
        // <CARGAR_LISTA_COMBO_GRANDE>
        cargarListaBanco();
        cargarListaEstablecimiento();
        cargarListaCentroCosto();
        // </CARGAR_LISTA_COMBO_GRANDE>
        // <CREAR_ARBOLES>

        try {
            parametroCodigo = ejbSysmanUtil.consultarParametro(compania,
                            "CODIGO CIUDAD CUENTA BANCO BOGOTA ENTIDAD",
                            SessionUtil.getModulo(), new Date(), false);
        }

        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
        // </CREAR_ARBOLES>
        abrirFormulario();
    }

    /**
     * Este metodo es invocado el metodo inicializar, se ejecutan las
     * acciones a tener en cuenta en el momento de apertura del
     * formulario
     */
    @Override
    public void abrirFormulario() {
        // <CODIGO_DESARROLLADO>
        codigoCiudad = parametroCodigo;

        // </CODIGO_DESARROLLADO>
    }

    // <METODOS_CARGAR_LISTA>
    /**
     * 
     * Carga la lista listaAno
     */
    public void cargarListaAno() {

        Map<String, Object> parametros = new HashMap<>();
        parametros.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        try {
            listaAno = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            DiscoBancoBogotaControladorUrlEnum.URL0001
                                                                            .getValue())
                                            .getUrl(), parametros));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    /**
     * 
     * Carga la lista listaMes
     */
    public void cargarListaMes() {

        Map<String, Object> parametros = new HashMap<>();
        parametros.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        parametros.put(GeneralParameterEnum.ANO.getName(), ano);

        try {
            listaMes = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            DiscoBancoBogotaControladorUrlEnum.URL0002
                                                                            .getValue())
                                            .getUrl(), parametros));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    /**
     * 
     * Carga la lista listaPeriodo
     */
    public void cargarListaPeriodo() {
        Map<String, Object> parametros = new HashMap<>();
        parametros.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        parametros.put(GeneralParameterEnum.ANO.getName(), ano);
        parametros.put(GeneralParameterEnum.MES.getName(), mes);

        try {
            listaPeriodo = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            DiscoBancoBogotaControladorUrlEnum.URL0003
                                                                            .getValue())
                                            .getUrl(), parametros));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    /**
     * 
     * Carga la lista listaBanco
     */
    public void cargarListaBanco() {
        Map<String, Object> parametros = new HashMap<>();
        parametros.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        String urlEnumId = DiscoBancoBogotaControladorUrlEnum.URL0004
                        .getValue();
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(urlEnumId);
        listaBanco = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), parametros,
                        true, GeneralParameterEnum.BANCO.getName());
    }

    /**
     * 
     * Carga la lista listaEstablecimiento
     */
    public void cargarListaEstablecimiento() {

        Map<String, Object> parametros = new HashMap<>();
        parametros.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        String urlEnumId = DiscoBancoBogotaControladorUrlEnum.URL0005
                        .getValue();
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(urlEnumId);
        listaEstablecimiento = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), parametros,
                        true, GeneralParameterEnum.CODIGO.getName());

    }

    /**
     * 
     * Carga la lista listaCentroCosto
     *
     */
    public void cargarListaCentroCosto() {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        DiscoBancoBogotaControladorUrlEnum.URL0006
                                                        .getValue());

        Map<String, Object> parametros = new HashMap<>();
        parametros.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        parametros.put(GeneralParameterEnum.ANO.getName(), ano);

        listaCentroCosto = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), parametros,
                        true, GeneralParameterEnum.CODIGO.getName());
    }

    // </METODOS_CARGAR_LISTA>
    // <METODOS_BOTONES>
    /**
     * 
     * Metodo ejecutado al oprimir el boton GenerarDisco en la vista
     *
     */
    public void oprimirGenerarDisco() {
        // <CODIGO_DESARROLLADO>

        String salida = null;

        if (validarCentroCosto() && validarBanco()) {
            try {
                salida = ejbNominaBancos.generarPlanoBBogota(compania,
                                Integer.parseInt(ano),
                                Integer.parseInt(mes),
                                periodo,
                                fecha,
                                banco,
                                codigoCiudad,
                                conceptoPago,
                                centroCosto);

                ArchivosBean.generarPlano("NO" + ano + mes + ".txt", salida);
            }
            catch (NumberFormatException | SystemException | IOException e) {
                logger.error(e.getMessage(), e);
                JsfUtil.agregarMensajeError(e.getMessage());
            }

        }

        // </CODIGO_DESARROLLADO>
    }

    private boolean validarCentroCosto() {
        if ("2".equals(opcionCentroCosto)
            && SysmanFunciones.validarVariableVacio(centroCosto)) {
            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB3251"));

            return false;
        }
        return true;
    }

    private boolean validarBanco() {
        if ("2".equals(opcionBanco)
            && SysmanFunciones.validarVariableVacio(banco)) {
            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB3815"));

            return false;
        }
        return true;
    }

    /**
     * 
     * Metodo ejecutado al oprimir el boton GenerarNuevoFormato en la
     * vista
     *
     */
    public void oprimirGenerarNuevoFormato() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    /**
     * 
     * Metodo ejecutado al oprimir el boton Carta en la vista
     *
     */
    public void oprimirCarta() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    // </METODOS_BOTONES>
    // <METODOS_CAMBIAR>
    /**
     * Metodo ejecutado al cambiar el control Ano
     * 
     */
    public void cambiarAno() {
        // <CODIGO_DESARROLLADO>
        mes = null;
        periodo = null;
        cargarListaMes();
        centroCosto = null;
        cargarListaCentroCosto();
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado al cambiar el control Mes
     * 
     */
    public void cambiarMes() {
        // <CODIGO_DESARROLLADO>
        periodo = null;
        cargarListaPeriodo();
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado al cambiar el control CentroCosto
     * 
     */
    public void cambiarCentroCosto() {
        centroCosto = null;
    }

    /**
     * Metodo ejecutado al cambiar el control Banco
     * 
     */
    public void cambiarBanco() {

        banco = null;

        cargarListaBanco();

    }

    // </METODOS_CAMBIAR>
    // <METODOS_COMBOS_GRANDES>
    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista listaBanco
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaBanco(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        banco = SysmanFunciones.nvl(registroAux.getCampos().get("BANCO"), "")
                        .toString();

    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaEstablecimiento
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaEstablecimiento(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        establecimiento = registroAux != null
            ? registroAux.getCampos().get(GeneralParameterEnum.CODIGO.getName())
                            .toString()
            : null;
    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaCentroCosto
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaCentroCosto(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        centroCosto = SysmanFunciones.nvl(
                        registroAux.getCampos().get(
                                        GeneralParameterEnum.CODIGO.getName()),
                        "").toString();
    }

    // </METODOS_COMBOS_GRANDES>
    // <METODOS_ARBOL>
    public String validaVacio(Object parametro) {
        return parametro != null ? parametro.toString() : null;
    }

    // </METODOS_ARBOL>
    // <SET_GET_ATRIBUTOS>
    /**
     * Retorna la variable opcionCentroCosto
     * 
     * @return opcionCentroCosto
     */
    public String getOpcionCentroCosto() {
        return opcionCentroCosto;
    }

    /**
     * Asigna la variable opcionCentroCosto
     * 
     * @param opcionCentroCosto
     * Variable a asignar en opcionCentroCosto
     */
    public void setOpcionCentroCosto(String opcionCentroCosto) {
        this.opcionCentroCosto = opcionCentroCosto;
    }

    /**
     * Retorna la variable opcionBanco
     * 
     * @return opcionBanco
     */
    public String getOpcionBanco() {
        return opcionBanco;
    }

    /**
     * Asigna la variable opcionBanco
     * 
     * @param opcionBanco
     * Variable a asignar en opcionBanco
     */
    public void setOpcionBanco(String opcionBanco) {
        this.opcionBanco = opcionBanco;
    }

    /**
     * Retorna la variable ano
     * 
     * @return ano
     */
    public String getAno() {
        return ano;
    }

    /**
     * Asigna la variable ano
     * 
     * @param ano
     * Variable a asignar en ano
     */
    public void setAno(String ano) {
        this.ano = ano;
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

    /**
     * Retorna la variable periodo
     * 
     * @return periodo
     */
    public String getPeriodo() {
        return periodo;
    }

    /**
     * Asigna la variable periodo
     * 
     * @param periodo
     * Variable a asignar en periodo
     */
    public void setPeriodo(String periodo) {
        this.periodo = periodo;
    }

    /**
     * Retorna la variable banco
     * 
     * @return banco
     */
    public String getBanco() {
        return banco;
    }

    /**
     * Asigna la variable banco
     * 
     * @param banco
     * Variable a asignar en banco
     */
    public void setBanco(String banco) {
        this.banco = banco;
    }

    /**
     * Retorna la variable establecimiento
     * 
     * @return establecimiento
     */
    public String getEstablecimiento() {
        return establecimiento;
    }

    /**
     * Asigna la variable establecimiento
     * 
     * @param establecimiento
     * Variable a asignar en establecimiento
     */
    public void setEstablecimiento(String establecimiento) {
        this.establecimiento = establecimiento;
    }

    /**
     * Retorna la variable centroCosto
     * 
     * @return centroCosto
     */
    public String getCentroCosto() {
        return centroCosto;
    }

    /**
     * Asigna la variable centroCosto
     * 
     * @param centroCosto
     * Variable a asignar en centroCosto
     */
    public void setCentroCosto(String centroCosto) {
        this.centroCosto = centroCosto;
    }

    /**
     * Retorna la variable fecha
     * 
     * @return fecha
     */
    public Date getFecha() {
        return fecha;
    }

    /**
     * Asigna la variable fecha
     * 
     * @param fecha
     * Variable a asignar en fecha
     */
    public void setFecha(Date fecha) {
        this.fecha = fecha;
    }

    /**
     * Retorna la variable codigoCiudad
     * 
     * @return codigoCiudad
     */
    public String getCodigoCiudad() {
        return codigoCiudad;
    }

    /**
     * Asigna la variable codigoCiudad
     * 
     * @param codigoCiudad
     * Variable a asignar en codigoCiudad
     */
    public void setCodigoCiudad(String codigoCiudad) {
        this.codigoCiudad = codigoCiudad;
    }

    /**
     * Retorna la variable totalGirar
     * 
     * @return totalGirar
     */
    public String getTotalGirar() {
        return totalGirar;
    }

    /**
     * Asigna la variable totalGirar
     * 
     * @param totalGirar
     * Variable a asignar en totalGirar
     */
    public void setTotalGirar(String totalGirar) {
        this.totalGirar = totalGirar;
    }

    /**
     * Retorna la variable conceptoPago
     * 
     * @return conceptoPago
     */
    public String getConceptoPago() {
        return conceptoPago;
    }

    /**
     * Asigna la variable conceptoPago
     * 
     * @param conceptoPago
     * Variable a asignar en conceptoPago
     */
    public void setConceptoPago(String conceptoPago) {
        this.conceptoPago = conceptoPago;
    }

    /**
     * Retorna la variable nomina
     * 
     * @return nomina
     */
    public String getNomina() {
        return nomina;
    }

    /**
     * Asigna la variable nomina
     * 
     * @param nomina
     * Variable a asignar en nomina
     */
    public void setNomina(String nomina) {
        this.nomina = nomina;
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

    /**
     * Retorna la lista listaMes
     * 
     * @return listaMes
     */
    public List<Registro> getListaMes() {
        return listaMes;
    }

    /**
     * Asigna la lista listaMes
     * 
     * @param listaMes
     * Variable a asignar en listaMes
     */
    public void setListaMes(List<Registro> listaMes) {
        this.listaMes = listaMes;
    }

    /**
     * Retorna la lista listaPeriodo
     * 
     * @return listaPeriodo
     */
    public List<Registro> getListaPeriodo() {
        return listaPeriodo;
    }

    /**
     * Asigna la lista listaPeriodo
     * 
     * @param listaPeriodo
     * Variable a asignar en listaPeriodo
     */
    public void setListaPeriodo(List<Registro> listaPeriodo) {
        this.listaPeriodo = listaPeriodo;
    }

    // </SET_GET_LISTAS>
    // <SET_GET_LISTAS_COMBO_GRANDE>
    /**
     * Retorna la lista listaBanco
     * 
     * @return listaBanco
     */
    public RegistroDataModelImpl getListaBanco() {
        return listaBanco;
    }

    /**
     * Asigna la lista listaBanco
     * 
     * @param listaBanco
     * Variable a asignar en listaBanco
     */
    public void setListaBanco(RegistroDataModelImpl listaBanco) {
        this.listaBanco = listaBanco;
    }

    /**
     * Retorna la lista listaEstablecimiento
     * 
     * @return listaEstablecimiento
     */
    public RegistroDataModelImpl getListaEstablecimiento() {
        return listaEstablecimiento;
    }

    /**
     * Asigna la lista listaEstablecimiento
     * 
     * @param listaEstablecimiento
     * Variable a asignar en listaEstablecimiento
     */
    public void setListaEstablecimiento(
        RegistroDataModelImpl listaEstablecimiento) {
        this.listaEstablecimiento = listaEstablecimiento;
    }

    /**
     * Retorna la lista listaCentroCosto
     * 
     * @return listaCentroCosto
     */
    public RegistroDataModelImpl getListaCentroCosto() {
        return listaCentroCosto;
    }

    /**
     * Asigna la lista listaCentroCosto
     * 
     * @param listaCentroCosto
     * Variable a asignar en listaCentroCosto
     */
    public void setListaCentroCosto(RegistroDataModelImpl listaCentroCosto) {
        this.listaCentroCosto = listaCentroCosto;
    }

    public StreamedContent getArchivoDescarga() {
        return archivoDescarga;
    }

    public void setArchivoDescarga(StreamedContent archivoDescarga) {
        this.archivoDescarga = archivoDescarga;
    }

    // </SET_GET_LISTAS_COMBO_GRANDE>
}
