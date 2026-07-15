/*-
 * FrmconsultaabonosControlador.java
 *
 * 1.0
 * 
 * 27/02/2018
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */
package com.sysman.facturaciongeneral;

import com.sysman.beanbase.BeanBaseDatosAcmeImpl;
import com.sysman.componentes.Direccionador;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.enums.GenericUrlEnum;
import com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException;
import com.sysman.exception.SystemException;
import com.sysman.facturaciongeneral.enums.FrmDiferirFacturaControladorEnum;
import com.sysman.facturaciongeneral.enums.FrmconsultaabonosControladorUrlEnum;
import com.sysman.jsfutil.CacheUtil;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.jsfutil.ReportesBean;
import com.sysman.jsfutil.ReportesBean.FORMATOS;
import com.sysman.kernel.api.clientwso2.beans.Parameter;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.persistencia.ConectorPool;
import com.sysman.recursos.ejb.impl.EjbSysmanUtil;
import com.sysman.reportes.Reporteador;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.session.utl.ConstantesFacturacionGenEnum;
import com.sysman.util.SysmanFunciones;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
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
 * Con
 *
 * @version 1.0, 27/02/2018
 * @author jeguerrero
 */
@ManagedBean
@ViewScoped
public class FrmconsultaabonosControlador extends BeanBaseDatosAcmeImpl {
	
	 private final String modulo;
    /**
     * Constante a nivel de clase que almacena el codigo de la
     * compania en la cual inicio sesion el usuario, el valor de esta
     * constante es asignado en el constructor a la variable de sesion
     * correspondiente
     */
    private final String compania;
    /**
     * Variable encargada de generar el titulo vacio en la grilla de
     * los botones
     */
    private String tituloVacioGrilla;
    // <DECLARAR_ATRIBUTOS>
    // </DECLARAR_ATRIBUTOS>
    // <DECLARAR_LISTAS>
    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>
    /**
     * Lista encargada de almacenar temporalmente los datos de
     * respuesta a la base de datos.
     */
    private RegistroDataModelImpl listaCodigoRuta;
    // </DECLARAR_LISTAS_COMBO_GRANDE>
    // <DECLARAR_LISTAS_SUBFORM>
    /**
     * Lista encargada de gestionar los datos del subformulario
     * ConsultaAbono
     */
    private List<Registro> listaSubfrmconsultaabono;
    // </DECLARAR_LISTAS_SUBFORM>
    // <DECLARAR_PARAMETROS>
    // </DECLARAR_PARAMETROS>
    // <DECLARAR_ADICIONALES>
    /**
     * Constante encargada de almacenar el String
     */
    private final String formatoMonedaCons;

    /**
     * Atributo de referencia para el subformulario
     */
    private Registro registroSub;
    /**
     * Variable encargada de almacenar temporalmente el tipo de cobro
     * con el que se accesde al modulo de facturacion general
     */
    private String tipo;
    /**
     * Variable encargada de almacenar temporalmente el ano de cobro
     * con el que se accesde al modulo de facturacion general
     */
    private String ano;
    /**
     * Varible encargada de mostrar el total del capital en el pie de
     * pagina
     */
    private String totalCapital;
    /**
     * Varible encargada de mostrar el total del capital en el pie de
     * pagina
     */
    private String totalInteres;
    /**
     * Varible encargada de mostrar el total del interes en el pie de
     * pagina
     */

    private String totalIntFinanciacion;
    /**
     * Varible encargada de mostrar el total del fincacioncion en el
     * pie de pagina
     */
    private String totalIntRecargo;
    /**
     * Varible encargada de mostrar el total del recargo en el pie de
     * pagina
     */
    private String totalCuota;
    /**
     * Varible encargada de mostrar el total del cuota en el pie de
     * pagina
     */
    private boolean visibleCodRuta;
    /**
     * Atributo usado para descargar contenidos de archivos desde la
     * vista
     */
    private StreamedContent archivoDescarga;

    @EJB
    private EjbSysmanUtil ejbSysmanUtl;

    // </DECLARAR_ADICIONALES>
    /**
     * Crea una nueva instancia de FrmconsultaabonosControlador
     */
    @SuppressWarnings("unchecked")
    public FrmconsultaabonosControlador() {
        super();
        
        Map<String, Object> parametros = SessionUtil.getFlash();
        if (parametros != null) {
            rid = (Map<String, Object>) parametros.get("rid");
        }
        modulo = SessionUtil.getModulo();
        compania = SessionUtil.getCompania();
        tituloVacioGrilla = "";
        formatoMonedaCons = "$ #,##0.00";
        ano = SysmanFunciones.nvl(SessionUtil.getSessionVar(
                        ConstantesFacturacionGenEnum.ANIO.getValue()), "")
                        .toString();
        tipo = SysmanFunciones.nvl(SessionUtil.getSessionVar(
                        ConstantesFacturacionGenEnum.TIPOCOBRO.getValue()), "")
                        .toString();

        try {
            numFormulario = GeneralCodigoFormaEnum.FRMCONSULTAABONOS_CONTROLADOR
                            .getCodigo();
            validarPermisos();
            // <INI_ADICIONAL>
            registroSub = new Registro(new HashMap<String, Object>());
            // </INI_ADICIONAL>
        }
        catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
            SessionUtil.redireccionarMenuPermisos();
        }

    }

    /**
     * En este metodo se hace la invocacion de lo metodos de carga de
     * todas las listas, menos las que son de subformularios
     */
    @Override
    public void iniciarListas() {
        // <CARGAR_LISTA_COMBO_GRANDE>

        cargarListaCodigoRuta();
        // </CARGAR_LISTA_COMBO_GRANDE>
        // <CARGAR_LISTA>
        // </CARGAR_LISTA>
    }

    /**
     * En este metodo se hace la invocacion de lo metodos de carga de
     * todas las listas que son de subformularios
     */
    @Override
    public void iniciarListasSub() {
        // <CARGAR_LISTAS_SUBFORM>
        cargarListaSubfrmconsultaabono();
        generarTotalesSub();
        // </CARGAR_LISTAS_SUBFORM>
        // <CREAR_ARBOLES>
        // </CREAR_ARBOLES>
    }

    /**
     * En este metodo se iguala a null todas las listas de los
     * subformularios
     */
    @Override
    public void iniciarListasSubNulo() {
        // <CARGAR_LISTAS_SUBFORM_NULL>
        listaSubfrmconsultaabono = null;
        // </CARGAR_LISTAS_SUBFORM_NULL>
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
        enumBase = GenericUrlEnum.SF_ABONOS;
        buscarLlave();
        asignarOrigenDatos();

    }

    /**
     * Se realiza la asignacion de la variable origenDatos por la
     * consulta correspondiente del formulario
     * 
     * 
     * 
     */
    @Override
    public void asignarOrigenDatos() {

        buscarUrls();

        parametrosListado.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
        parametrosListado.put("TIPO", tipo);
        parametrosListado.put(GeneralParameterEnum.ANO.getName(), ano);

    }

    /**
     * 
     * Carga la lista listaSubfrmconsultaabono
     *
     * Metodo encargado de hacer el llamado a la base de datos y
     * almacenar la respuesta en la lista listaSubFrmConsultaabono .
     */
    public void cargarListaSubfrmconsultaabono() {
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put("TIPO", tipo);
        param.put(GeneralParameterEnum.CODIGO.getName(), registro.getCampos()
                        .get(GeneralParameterEnum.CODIGO.getName()));

        try {
            listaSubfrmconsultaabono = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            FrmconsultaabonosControladorUrlEnum.URL17101
                                                                            .getValue())
                                            .getUrl(), param),
                            CacheUtil.getLlaveServicio(urlConexionCache,
                                            "SF_DETALLE_ABONO"));
        }
        catch (SystemException | SysmanException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    // <METODOS_CARGAR_LISTA>

    /**
     * 
     * Carga la lista listaCODIGORUTA
     *
     * Metodo encargado de hacer el llamado a la base de datos y
     * almacenar el resultado en la lista listaCodigoRuta
     */
    public void cargarListaCodigoRuta() {

        // URL15596

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        FrmconsultaabonosControladorUrlEnum.URL15596
                                                        .getValue());

        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        listaCodigoRuta = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param, true,
                        GeneralParameterEnum.CODIGORUTA.getName());

    }

    // </METODOS_CARGAR_LISTA>
    // <METODOS_CAMBIAR>
    // </METODOS_CAMBIAR>
    // <METODOS_COMBOS_GRANDES>
    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaCodigoRuta
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaCodigoRuta(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        registro.getCampos().put(GeneralParameterEnum.CODIGORUTA.getName(),
                        registroAux.getCampos()
                                        .get(GeneralParameterEnum.CODIGORUTA
                                                        .getName()));

        registro.getCampos().put("NOMBRE_CODRUTA",
                        registroAux.getCampos().get(
                                        GeneralParameterEnum.NOMBRE.getName()));
        actualizarCodRuta();

    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listacentroCosto
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilacentroCosto(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        registro.getCampos().put("CENTRO_COSTO",
                        registroAux.getCampos().get(
                                        GeneralParameterEnum.CODIGO.getName()));
    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaauxiliar
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaauxiliar(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        registro.getCampos().put(GeneralParameterEnum.AUXILIAR.getName(),
                        registroAux.getCampos()
                                        .get(GeneralParameterEnum.CODIGORUTA
                                                        .getName()));
    }

    // </METODOS_COMBOS_GRANDES>
    // <METODOS_ARBOL>
    // </METODOS_ARBOL>
    // <METODOS_BOTONES>
    /**
     * Metodo ejecutado al oprimir el boton CmdDistribucion
     * 
     * 
     * @param reg
     * registro en el cual esta ubicado el boton oprimido dentro de la
     * grilla
     * @param indice
     * indice en el cual esta ubicado el boton oprimido dentro de la
     * grilla
     */
    public void oprimirCmdDistribucion(Registro reg, int indice) {
        // <CODIGO_DESARROLLADO>

        Map<String, Object> parametros = new HashMap<>();
        parametros.put("ano", ano);
        parametros.put("tipo", tipo);
        parametros.put("codigo", retornarString(registro,
                        GeneralParameterEnum.CODIGO.getName()));
        parametros.put("cuota", retornarString(reg,
                        GeneralParameterEnum.CUOTA.getName()));
        parametros.put("rid", css);

        Direccionador direccionador = new Direccionador();
        direccionador.setNumForm(
                        String.valueOf(GeneralCodigoFormaEnum.SUBFRMCONSULTACUOTAABS_CONTROLADOR
                                        .getCodigo()));
        direccionador.setParametros(parametros);

        SessionUtil.redireccionarForma(direccionador, SessionUtil.getModulo());

        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado al oprimir el boton Factura
     * 
     * 
     * @param reg
     * registro en el cual esta ubicado el boton oprimido dentro de la
     * grilla
     * @param indice
     * indice en el cual esta ubicado el boton oprimido dentro de la
     * grilla
     */
    public void oprimirFactura(Registro reg, int indice) {
        // <CODIGO_DESARROLLADO>
        archivoDescarga = null;
        String cuota = retornarString(reg,
                        GeneralParameterEnum.CUOTA.getName());

        genInforme(FORMATOS.PDF, cuota);
        // </CODIGO_DESARROLLADO>
    }

    // </METODOS_BOTONES>
    // <METODOS_SUBFORM>
    /**
     * Metodo de insercion del formulario Subfrmconsultaabono
     * 
     */
    public void agregarRegistroSubSubfrmconsultaabono() {
        //
    }

    /**
     * Metodo de edicion del formulario Subfrmconsultaabono
     * 
     * 
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void editarRegSubSubfrmconsultaabono(RowEditEvent event) {
        //
    }

    /**
     * Metodo de eliminacion del formulario Subfrmconsultaabono
     * 
     * 
     * @param reg
     * registro seleccionado en el subformulario
     */
    public void eliminarRegSubSubfrmconsultaabono(Registro reg) {
        //
    }

    /**
     * Metodo ejecutado cuando se cancela la edicion del registro
     * seleccionado para el subformulario Subfrmconsultaabono
     *
     */
    public void cancelarEdicionSubfrmconsultaabono() {
        cargarListaSubfrmconsultaabono();
    }

    // </METODOS_SUBFORM>
    // <METODOS_ADICIONALES>
    // </METODOS_ADICIONALES>
    /**
     * Este metodo es invocado el metodo inicializar, se ejecutan las
     * acciones a tener en cuenta en el momento de apertura del
     * formulario
     */
    @Override
    public void abrirFormulario() {
        // <CODIGO_DESARROLLADO>
        validarParametroCodRuta();
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado en el momento despues de cargar el registro
     * 
     */
    @Override
    public void cargarRegistro() {
        // <CODIGO_DESARROLLADO>
        precargarRegistro();
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado antes de realizar la insercion del registro
     */
    @Override
    public boolean insertarAntes() {
        // <CODIGO_DESARROLLADO>
        registro.getCampos().put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
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
        registro.getCampos().remove(GeneralParameterEnum.COMPANIA.getName());
        registro.getCampos().remove(GeneralParameterEnum.TIPO.getName());
        registro.getCampos().remove("TERCERO_NOMBRE");
        registro.getCampos().remove("CENTROC_NOMBRE");
        registro.getCampos().remove("AUXILIAR_NOMBRE");
        registro.getCampos().remove("NOMBRE_CODRUTA");

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
     */
    @Override
    public boolean eliminarDespues() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
        return true;
    }

    // <SET_GET_ATRIBUTOS>
    // </SET_GET_ATRIBUTOS>
    // <SET_GET_LISTAS>
    // </SET_GET_LISTAS>
    // <SET_GET_LISTAS_COMBO_GRANDE>

    // </SET_GET_LISTAS_COMBO_GRANDE>
    // <SET_GET_LISTAS_SUBFORM>
    /**
     * Retorna la lista listaSubfrmconsultaabono
     * 
     * @return listaSubfrmconsultaabono
     */
    public List<Registro> getListaSubfrmconsultaabono() {
        return listaSubfrmconsultaabono;
    }

    public RegistroDataModelImpl getListaCodigoRuta() {
        return listaCodigoRuta;
    }

    public void setListaCodigoRuta(RegistroDataModelImpl listaCodigoRuta) {
        this.listaCodigoRuta = listaCodigoRuta;
    }

    /**
     * Asigna la lista listaSubfrmconsultaabono
     * 
     * @param listaSubfrmconsultaabono
     * Variable a asignar en listaSubfrmconsultaabono
     */
    public void setListaSubfrmconsultaabono(
        List<Registro> listaSubfrmconsultaabono) {
        this.listaSubfrmconsultaabono = listaSubfrmconsultaabono;
    }

    // </SET_GET_LISTAS_SUBFORM>
    // <SET_GET_PARAMETROS>
    // </SET_GET_PARAMETROS>
    // <SET_GET_ADICIONALES>
    /**
     * Retorna el objeto registroSub
     * 
     * @return registroSub
     */
    public Registro getRegistroSub() {
        return registroSub;
    }

    /**
     * Asigna el objeto registroSub
     * 
     * @param registroSub
     * Variable a asignar en registroSub
     */
    public void setRegistroSub(Registro registroSub) {
        this.registroSub = registroSub;
    }

    public String getTituloVacioGrilla() {
        return tituloVacioGrilla;
    }

    public void setTituloVacioGrilla(String tituloVacioGrilla) {
        this.tituloVacioGrilla = tituloVacioGrilla;
    }

    public String getTotalCapital() {
        return totalCapital;
    }

    public void setTotalCapital(String totalCapital) {
        this.totalCapital = totalCapital;
    }

    public String getTotalInteres() {
        return totalInteres;
    }

    public void setTotalInteres(String totalInteres) {
        this.totalInteres = totalInteres;
    }

    public String getTotalIntFinanciacion() {
        return totalIntFinanciacion;
    }

    public void setTotalIntFinanciacion(String totalIntFinanciacion) {
        this.totalIntFinanciacion = totalIntFinanciacion;
    }

    public String getTotalIntRecargo() {
        return totalIntRecargo;
    }

    public void setTotalIntRecargo(String totalIntRecargo) {
        this.totalIntRecargo = totalIntRecargo;
    }

    public String getTotalCuota() {
        return totalCuota;
    }

    public void setTotalCuota(String totalCuota) {
        this.totalCuota = totalCuota;
    }

    public boolean isVisibleCodRuta() {
        return visibleCodRuta;
    }

    public void setVisibleCodRuta(boolean visibleCodRuta) {
        this.visibleCodRuta = visibleCodRuta;
    }

    public StreamedContent getArchivoDescarga() {
        return archivoDescarga;
    }

    public void setArchivoDescarga(StreamedContent archivoDescarga) {
        this.archivoDescarga = archivoDescarga;
    }

    private void generarTotalesSub() {
        try {
            Map<String, Object> param = new TreeMap<>();
            param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
            param.put("CODIGOCOBRO",
                            registro.getCampos()
                                            .get(GeneralParameterEnum.CODIGO
                                                            .getName()));
            param.put("TIPOCOBRO", tipo);
            param.put(GeneralParameterEnum.ANO.getName(), ano);

            Registro regTotales = RegistroConverter.toRegistro(
                            requestManager.get(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            FrmconsultaabonosControladorUrlEnum.URL23432
                                                                            .getValue())
                                            .getUrl(), param));

            totalCapital = new java.text.DecimalFormat(formatoMonedaCons)
                            .format(
                                            retornarDouble(regTotales,
                                                            "CAPITAL"));
            totalInteres = new java.text.DecimalFormat(formatoMonedaCons)
                            .format(retornarDouble(regTotales, "INTERES"));
            totalIntRecargo = new java.text.DecimalFormat(formatoMonedaCons)
                            .format(retornarDouble(regTotales, "INT_RECARGO"));
            totalIntFinanciacion = new java.text.DecimalFormat(
                            formatoMonedaCons)
                                            .format(retornarDouble(regTotales,
                                                            "INT_FINANCIACION"));
            totalCuota = new java.text.DecimalFormat(formatoMonedaCons)
                            .format(retornarDouble(regTotales, "TOTAL_CUOTA"));

        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    private double retornarDouble(Registro reg, String campo) {
        return Double.parseDouble(SysmanFunciones
                        .nvl(reg.getCampos().get(campo), "0").toString());
    }

    private void validarParametroCodRuta() {

        try {
            visibleCodRuta = "SI".equals(SysmanFunciones
                            .nvl(ejbSysmanUtl.consultarParametro(compania,
                                            "SF MANEJA FINANCIABLES A SERVICIOS PUBLICOS",
                                            SessionUtil.getModulo(), new Date(),
                                            false), "NO"));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    private void actualizarCodRuta() {
        UrlBean urlUpdate = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        FrmconsultaabonosControladorUrlEnum.URL9923
                                                        .getValue());
        Map<String, Object> fields = new TreeMap<>();
        fields.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        fields.put("TIPO", tipo);
        fields.put(GeneralParameterEnum.CODIGO.getName(), registro.getCampos()
                        .get(GeneralParameterEnum.CODIGO.getName()));
        fields.put(GeneralParameterEnum.CODIGORUTA.getName(), registro
                        .getCampos()
                        .get(GeneralParameterEnum.CODIGORUTA.getName()));

        fields.put(GeneralParameterEnum.MODIFIED_BY.getName(),
                        SessionUtil.getUser().getCodigo());
        fields.put(GeneralParameterEnum.DATE_MODIFIED.getName(), new Date());

        Parameter parameter = new Parameter();
        parameter.setFields(fields);

        try {
            requestManager.update(urlUpdate.getUrl(),
                            urlUpdate.getMetodo(),
                            parameter);
            JsfUtil.agregarMensajeInformativo(
                            idioma.getString("MSM_REGISTRO_MODIFICADO"));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    private void genInforme(ReportesBean.FORMATOS formato, String cuota) {

        try {
        	//770618 MROSERO
        	String nombreReporte = ejbSysmanUtl.consultarParametro(
                    compania,
                    "SF FORMATO RECIBO ABONO",
                    SessionUtil.getModulo(),
                    new Date(), false);
            
            Map<String, Object> reemplazar = new HashMap<>();
            reemplazar.put("tipo", tipo);
            reemplazar.put("nroFactura", registro.getCampos()
                            .get(GeneralParameterEnum.CODIGO.getName()));
            reemplazar.put("nroCuota", cuota);

            Map<String, Object> parametros = new HashMap<>();
          
            String informe;

            informe = ejbSysmanUtl.consultarParametro(
                            compania,
                            "SF FORMATO RECIBO ABONO",
                            SessionUtil.getModulo(),
                            new Date(), false);
            if (nombreReporte.equals(informe)) {

            
            parametros.put("PR_NOMBRECOMPANIA",
                            SessionUtil.getCompaniaIngreso().getNombre());
            parametros.put("PR_NITCOMPANIA",
                            SessionUtil.getCompaniaIngreso().getNit());
            parametros.put("PR_CIUDADCOMPANIA",
                            SessionUtil.getCompaniaIngreso().getCiudad());

            parametros.put("PR_GETUSER", SessionUtil.getUser().getCodigo());
            parametros.put("PR_DIRECCIONCOMPANIA",
                            SessionUtil.getCompaniaIngreso().getDireccion());
            parametros.put("PR_SF_ESLOGAN_TITULO",
                            obtenerParametro("SF ESLOGAN TITULO"));
            parametros.put("PR_SF_ESLOGAN_PIE",
                            obtenerParametro("SF ESLOGAN PIE"));
            
            parametros.put("PR_SF_ESLOGAN_PIE",
                    obtenerParametro("SF ESLOGAN PIE"));
            
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
            
//            Reporteador.resuelveConsulta("001705INFRECSTD02",
//                            Integer.parseInt(SessionUtil.getModulo()),
//                            reemplazar, parametros);
//            
//            archivoDescarga = JsfUtil.exportarStreamed(
//                            "001705INFRECSTD02", parametros,
//                            ConectorPool.ESQUEMA_SYSMAN, formato);
            
        }
      
        catch (SystemException | JRException | IOException
                | SysmanException e) {
    logger.error(e.getMessage(), e);
    JsfUtil.agregarMensajeError(e.getMessage());
}

    }

    private String obtenerParametro(String nombreParam) {

        String parametro = null;

        try {
            parametro = ejbSysmanUtl.consultarParametro(compania, nombreParam,
                            SessionUtil.getModulo(), new Date(), true);
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

        return SysmanFunciones.validarVariableVacio(parametro) ? "" : parametro;

    }

    private String retornarString(Registro reg, String campo) {
        return SysmanFunciones.validarCampoVacio(reg.getCampos(), campo) ? ""
            : reg.getCampos().get(campo).toString();

    }

    // </SET_GET_ADICIONALES>
}
