/*-
 * FrmdetimpfacuotasControlador.java
 *
 * 1.0
 * 
 * 14/12/2018
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */
package com.sysman.facturaciongeneral;

import com.sysman.beanbase.BeanBaseContinuoAcmeImpl;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException;
import com.sysman.exception.SystemException;
import com.sysman.facturaciongeneral.enums.FrmdetimpfacuotasControladorUrlEnum;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.ReportesBean;
import com.sysman.jsfutil.ReportesBean.FORMATOS;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.persistencia.ConectorPool;
import com.sysman.recursos.ejb.EjbSysmanUtilRemote;
import com.sysman.reportes.Reporteador;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.session.utl.ConstantesFacturacionGenEnum;
import com.sysman.util.SysmanFunciones;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.event.RowEditEvent;
import org.primefaces.event.SelectEvent;
import org.primefaces.model.StreamedContent;

import net.sf.jasperreports.engine.JRException;

/**
 * TODO Ingrese una descripcion para la clase.
 *
 * @version 1.0, 14/12/2018
 * @author jrojas
 */
@ManagedBean
@ViewScoped
public class FrmdetimpfacuotasControlador extends BeanBaseContinuoAcmeImpl {
    /**
     * Constante a nivel de clase que almacena el codigo de la
     * compania en la cual inicio sesion el usuario, el valor de esta
     * constante es asignado en el constructor a la variable de sesion
     * correspondiente
     */
    private final String compania;
    private final String modulo;
    // <DECLARAR_ATRIBUTOS>
    /**
     */
    private String tercero;
    private String terceroNombre;
    private String ano;
    private String tipoCobro;
    private String numero;
    private String cuota;
    private String codigoEan;
    // </DECLARAR_ATRIBUTOS>
    // <DECLARAR_PARAMETROS>
    // </DECLARAR_PARAMETROS>
    /**
     * Atributo usado para descargar contenidos de archivos desde la
     * vista
     */
    private StreamedContent archivoDescarga;
    // <DECLARAR_LISTAS>
    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>

    private RegistroDataModelImpl listaTercero; // ´poner el Impl
    private RegistroDataModelImpl listaTerceroE; // ´poner el Impl
    /**
     * Esta variable se usa como auxiliar para subformularios y en
     * esta se alamcena el identificador del registro que se
     * selecciono
     */
    private String auxiliar;

    @EJB
    private EjbSysmanUtilRemote ejbSysmanUtil;

    // </DECLARAR_LISTAS_COMBO_GRANDE>
    /**
     * Crea una nueva instancia de FrmdetimpfacuotasControlador
     */
    public FrmdetimpfacuotasControlador() {
        super();
        compania = SessionUtil.getCompania();
        modulo = SessionUtil.getModulo();
        tipoCobro = (String) SessionUtil.getSessionVar(
                        ConstantesFacturacionGenEnum.TIPOCOBRO.getValue());
        ano = (String) SessionUtil.getSessionVar(
                        ConstantesFacturacionGenEnum.ANIO.getValue());
        try {
            numFormulario = 2007;
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

        tabla = "SF_FACTURA";
        // reasignarOrigen();
        buscarLlave();
        registro = new Registro();
        // <CARGAR_LISTA>
        // </CARGAR_LISTA>
        // <CARGAR_LISTA_COMBO_GRANDE>
        cargarListaTercero();
        cargarListaTerceroE();
        // </CARGAR_LISTA_COMBO_GRANDE>
        abrirFormulario();

    }

    /**
     * En este metodo se asigna al atributo origenDatos del bean base
     * el valor de la consulta del formulario. Tambien carga la lista
     * del formulario por primera vez
     */
    @Override
    public void reasignarOrigen() {
        parametrosListado.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
        parametrosListado.put(GeneralParameterEnum.TERCERO.getName(), tercero);

        urlListado = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        FrmdetimpfacuotasControladorUrlEnum.URL661045
                                                        .getValue());
    }

    // <METODOS_CARGAR_LISTA>
    /**
     * 
     * Carga la lista listaTercero
     *
     */
    public void cargarListaTercero() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        FrmdetimpfacuotasControladorUrlEnum.URL14180
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        listaTercero = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param, true, "NIT");
    }

    /**
     * 
     * Carga la lista listaTercero
     *
     */
    public void cargarListaTerceroE() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        FrmdetimpfacuotasControladorUrlEnum.URL14180
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        listaTerceroE = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param, true,
                        "NIT");
    }

    // </METODOS_CARGAR_LISTA>
    // <METODOS_BOTONES>
    /**
     * Metodo ejecutado al oprimir el boton CmbImprimir
     * 
     * 
     * @param reg
     * registro en el cual esta ubicado el boton oprimido dentro de la
     * grilla
     * @param indice
     * indice en el cual esta ubicado el boton oprimido dentro de la
     * grilla
     */
    public void oprimirCmbImprimir(Registro reg, int indice) {

        archivoDescarga = null;
        generarInforme(FORMATOS.PDF, reg);

    }

    public void generarInforme(ReportesBean.FORMATOS formato, Registro reg) {
        try {
            // <CODIGO_DESARROLLADO>

            numero = reg.getCampos().get("NUMERO_FACTURA").toString();
            cuota = reg.getCampos().get("CUOTA").toString();
            String nombreReporte = ejbSysmanUtil.consultarParametro(
                    compania,
                    "SF FORMATO FACTURA DIFERIDAS",
                    SessionUtil.getModulo(),
                    new Date(), false);
            
            Map<String, Object> parametros = new HashMap<>();
            HashMap<String, Object> reemplazar = new HashMap<>();

            codigoEan = SysmanFunciones.nvl(
                            ejbSysmanUtil.consultarParametro(compania,
                                            "SF CODIGO EAN",
                                            SessionUtil.getModulo(),
                                            new Date(), false),
                            "").toString();

            reemplazar.put("ano", ano);
            reemplazar.put("tipoCobro", tipoCobro);
            reemplazar.put("cuota", cuota);
            reemplazar.put("numero", numero);
            reemplazar.put("tercero", tercero);
            reemplazar.put("codigoEan", codigoEan);

            String informe;

            informe = ejbSysmanUtil.consultarParametro(
                            compania,
                            "SF FORMATO FACTURA DIFERIDAS",
                            SessionUtil.getModulo(),
                            new Date(), false);

            if (nombreReporte.equals(informe)) {

                parametros.put("PR_NOMBRECOMPANIA",
                                SessionUtil.getCompaniaIngreso().getNombre());

                parametros.put("PR_NITCOMPANIA",
                                SessionUtil.getCompaniaIngreso().getNit());

                Reporteador.resuelveConsulta(nombreReporte,
                                Integer.parseInt(modulo), reemplazar,
                                parametros);

                archivoDescarga = JsfUtil.exportarStreamed(nombreReporte,
                                parametros, ConectorPool.ESQUEMA_SYSMAN,
                                formato);
            }
            else {

                Reporteador.resuelveConsulta(informe,
                                Integer.parseInt(modulo), reemplazar,
                                parametros);

                archivoDescarga = JsfUtil.exportarStreamed(informe,
                                parametros, ConectorPool.ESQUEMA_SYSMAN,
                                formato);

            }

        }
        catch (SystemException | JRException | IOException
                        | SysmanException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    // </METODOS_BOTONES>
    // <METODOS_CAMBIAR>
    // </METODOS_CAMBIAR>
    // <METODOS_COMBOS_GRANDES>
    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaTercero
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaTercero(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        tercero = registroAux.getCampos().get("NIT").toString();
        terceroNombre = registroAux.getCampos().get("NOMBRE").toString();
        reasignarOrigen();
    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaTercero
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaTerceroE(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        auxiliar = registroAux.getCampos().get("NIT").toString();
        reasignarOrigen();
    }

    // </METODOS_COMBOS_GRANDES>
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

    /**
     * Metodo ejecutado cuando se cancela la edicion del registro
     * seleccionado
     */
    @Override
    public void cancelarEdicion(RowEditEvent event) {
        getListaInicial().load();
    }

    /**
     * Metodo ejecutado antes de realizar la insercion del registro
     */
    @Override
    public boolean insertarAntes() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
        return true;
    }

    /**
     * Metodo ejecutado despues de realizar la insercion del registro
     */
    @Override
    public boolean insertarDespues() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
        return true;
    }

    /**
     * Metodo ejecutado antes de realizar la insercion y actualizacion
     * del registro
     * 
     */
    @Override
    public boolean actualizarAntes() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
        return true;
    }

    /**
     * Metodo ejecutado despues de realizar la insercion y
     * actualizacion del registro
     * 
     */
    @Override
    public boolean actualizarDespues() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
        return true;
    }

    /**
     * Metodo ejecutado antes de realizar la eliminacion del registro
     * 
     */
    @Override
    public boolean eliminarAntes() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
        return true;
    }

    /**
     * Metodo ejecutado despues de realizar la eliminacion del
     * registro
     * 
     * 
     */
    @Override
    public boolean eliminarDespues() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
        return true;
    }

    /**
     * Este metodo se ejecuta antes enviar la accion de actualizacion,
     * en el se pueden remover valores auxiliares que no se desee o se
     * deban enviar en el registro
     */
    @Override
    public void removerCombos() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Este metodo es ejecutado despues de finalizar la insercion y
     * edicion del registro se usa cuando se desean agregar valores al
     * registro despues de dichas acciones
     */
    @Override
    public void asignarValoresRegistro() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    // <SET_GET_ATRIBUTOS>
    /**
     * Retorna la variable tercero
     * 
     * @return tercero
     */
    public String getTercero() {
        return tercero;
    }

    /**
     * Asigna la variable tercero
     * 
     * @param tercero
     * Variable a asignar en tercero
     */
    public void setTercero(String tercero) {
        this.tercero = tercero;
    }

    // </SET_GET_ATRIBUTOS>
    // <SET_GET_PARAMETROS>
    // </SET_GET_PARAMETROS>
    // <SET_GET_LISTAS>
    // </SET_GET_LISTAS>
    // <SET_GET_LISTAS_COMBO_GRANDE>
    /**
     * Retorna la lista listaTercero
     * 
     * @return listaTercero
     */
    public RegistroDataModelImpl getListaTercero() {
        return listaTercero;
    }

    /**
     * Asigna la lista listaTercero
     * 
     * @param listaTercero
     * Variable a asignar en listaTercero
     */
    public void setListaTercero(RegistroDataModelImpl listaTercero) {
        this.listaTercero = listaTercero;
    }

    /**
     * Retorna la lista listaTercero
     * 
     * @return listaTercero
     */
    public RegistroDataModelImpl getListaTerceroE() {
        return listaTerceroE;
    }

    /**
     * Asigna la lista listaTercero
     * 
     * @param listaTercero
     * Variable a asignar en listaTercero
     */
    public void setListaTerceroE(RegistroDataModelImpl listaTerceroE) {
        this.listaTerceroE = listaTerceroE;
    }

    /**
     * Retorna la variable auxiliar
     * 
     * @return auxiliar
     */
    public String getAuxiliar() {
        return auxiliar;
    }

    /**
     * Asigna la variable auxiliar
     * 
     * @param auxiliar
     * Variable a asignar en auxiliar
     */
    public void setAuxiliar(String auxiliar) {
        this.auxiliar = auxiliar;
    }
    // </SET_GET_LISTAS_COMBO_GRANDE>

    public String getTerceroNombre() {
        return terceroNombre;
    }

    public void setTerceroNombre(String terceroNombre) {
        this.terceroNombre = terceroNombre;
    }

    /**
     * @return the ano
     */
    public String getAno() {
        return ano;
    }

    /**
     * @param ano
     * the ano to set
     */
    public void setAno(String ano) {
        this.ano = ano;
    }

    /**
     * @return the tipoCobro
     */
    public String getTipoCobro() {
        return tipoCobro;
    }

    /**
     * @param tipoCobro
     * the tipoCobro to set
     */
    public void setTipoCobro(String tipoCobro) {
        this.tipoCobro = tipoCobro;
    }

    /**
     * @return the codigoEan
     */
    public String getCodigoEan() {
        return codigoEan;
    }

    /**
     * @param codigoEan
     * the codigoEan to set
     */
    public void setCodigoEan(String codigoEan) {
        this.codigoEan = codigoEan;
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
