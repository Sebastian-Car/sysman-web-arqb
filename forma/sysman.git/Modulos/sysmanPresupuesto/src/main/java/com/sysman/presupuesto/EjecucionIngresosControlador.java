package com.sysman.presupuesto;

import com.sysman.beanbase.BeanBaseModal;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.jsfutil.ReportesBean.FORMATOS;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.persistencia.ConectorPool;
import com.sysman.presupuesto.enums.EjecucionIngresosControladorEnum;
import com.sysman.presupuesto.enums.EjecucionIngresosControladorUrlEnum;
import com.sysman.recursos.ejb.EjbSysmanUtilRemote;
import com.sysman.reportes.Reporteador;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.util.SysmanConstantes;
import com.sysman.util.SysmanFunciones;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.event.SelectEvent;
import org.primefaces.model.StreamedContent;

import net.sf.jasperreports.engine.JRException;

/**
 *
 * @author jrodriguezr
 * @version 1, 20/06/2016
 * @modified jsforero
 * @version 2. 18/04/2017 Se realizo el refactory.
 * 
 * @author jlramirez
 * @version 3, 24/04/2017, Manejo de EJBs
 * 
 * @author eamaya
 * @version 3.0, 12/06/2017 Cambio cï¿½digo formulario y
 * actualizaciï¿½n de ConnectorPool
 * 
 * @author jgomez
 * @version 4, 22/08/2018 Se ajsuta para que las ejecuciones mayoricen
 * 
 * @author dcastiblanco
 * @version 5, 12/07/2021 Se añade validacion de parametro para suma a un digito
 */
@ManagedBean
@ViewScoped
public class EjecucionIngresosControlador extends BeanBaseModal {
    private final String compania;

    /**
     * Constante a nivel de clase que aloja el codigo del modulo desde
     * el cual el usuario inicio sesion
     */
    private final String modulo;

    private final String strCodigo;

    // <DECLARAR_ATRIBUTOS>
    private boolean enMiles;
    private String cuentaInicial = SysmanConstantes.DEFECTOINICIAL_STRING;
    private String cuentaFinal = SysmanConstantes.DEFECTOFINAL_STRING;
    private String mesInicial;
    private String mesFinal;
    private String centroInicial = SysmanConstantes.DEFECTOINICIAL_STRING;
    private String centroFinal = SysmanConstantes.DEFECTOFINAL_STRING;
    private String fuenteInicial = SysmanConstantes.DEFECTOINICIAL_STRING;
    private String fuenteFinal = SysmanConstantes.DEFECTOFINAL_STRING;
    private String anio;
    private String nmes1;
    private String nmes2;
    private StreamedContent archivoDescarga;
    @EJB
    private EjbSysmanUtilRemote sysmanUtil;
    // </DECLARAR_ATRIBUTOS>
    // <DECLARAR_PARAMETROS>

    /**
     * Atributo que indica si el parametro:
     * <code>MANEJA AUXILIAR POR FUENTE EN PRESUPUESTO</code> tiene el
     * valor definido en <code>SI</code>
     */
    private boolean parManejaAuxiliar;
    /**
     * Atributo que indica si el parametro:
     * <code>MANEJA SUMAR RUBROS A UN DIGITO</code> tiene el
     * valor definido en <code>SI</code>
     */
    private boolean parmanejavalorRubro;

    // </DECLARAR_PARAMETROS>
    // <DECLARAR_LISTAS>
    private List<Registro> listaMesInicial;
    private List<Registro> listaMesFinal;
    private List<Registro> listaano;
    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>
    private RegistroDataModelImpl listaCuentaInicial;
    private RegistroDataModelImpl listaCuentaFinal;
    private RegistroDataModelImpl listacentrocostoInicial;
    private RegistroDataModelImpl listacentrocostoFinal;
    private RegistroDataModelImpl listaFuenteInicial;
    private RegistroDataModelImpl listaFuenteFinal;
    // </DECLARAR_LISTAS_COMBO_GRANDE>

    private String observaciones;

    /**
     * Creates a new instance of EjecucionIngresosControlador
     */
    public EjecucionIngresosControlador() {
        super();
        compania = SessionUtil.getCompania();
        modulo = SessionUtil.getModulo();

        strCodigo = "CODIGO";

        try {
            numFormulario = GeneralCodigoFormaEnum.EJECUCION_INGRESOS_CONTROLADOR
                            .getCodigo();

            validarPermisos();
            // <INI_ADICIONAL>
            // </INI_ADICIONAL>
        } catch (Exception ex) {
            Logger.getLogger(EjecucionIngresosControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            SessionUtil.redireccionarMenuPermisos();
        }

    }

    @PostConstruct
    public void inicializar() {
        // <CARGAR_LISTA>
        cargarListaano();
        anio = String.valueOf(SysmanFunciones.ano(new Date()));
        cargarListaMesInicial();
        mesInicial = "1";
        cargarListaMesFinal();
        mesFinal = "1";
        nmes1 = SysmanConstantes.NOMBRE_MESES_CONTABILIDAD[Integer
                        .parseInt(mesInicial)];
        nmes2 = SysmanConstantes.NOMBRE_MESES_CONTABILIDAD[Integer
                        .parseInt(mesInicial)];
        // </CARGAR_LISTA>
        // <CARGAR_LISTA_COMBO_GRANDE>
        cargarListaCuentaInicial();

        cargarListacentrocostoInicial();

        cargarListaFuenteInicial();

        // </CARGAR_LISTA_COMBO_GRANDE>
        abrirFormulario();
    }

    @Override
    public void abrirFormulario() {
        // <CODIGO_DESARROLLADO>
        cargarParametros();
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Util para cargar las variables que dependen de parametros al
     * abrir el formulario
     */
    private void cargarParametros() {
        try {
            String valorPar = recuperarValorPar(
                            "MANEJA AUXILIAR POR FUENTE EN PRESUPUESTO");
            
                   
            parManejaAuxiliar = validarParametro(
                            "MANEJA AUXILIAR POR FUENTE EN PRESUPUESTO",
                            valorPar) ? "SI".equals(valorPar) : false;
                            
              String  valorRubro = recuperarValorPar(
                                    "MANEJA SUMAR RUBROS A UN DIGITO");          
            parmanejavalorRubro = validarParametro(
                                    "MANEJA SUMAR RUBROS A UN DIGITO",
                                    valorRubro) ? "SI".equals(valorRubro) : false;
        } catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    /**
     * Consulta y retorna el valor asignado al parametro segun la base
     * de datos.
     * 
     * @param nombrePar
     * Nombre asignado al parametro
     * @return El valor del parametro asignado en la base de datos.
     * @throws SystemException
     */
    private String recuperarValorPar(String nombrePar) throws SystemException {
        return sysmanUtil.consultarParametro(compania, nombrePar, modulo,
                        new Date(), false);
    }

    /**
     * Util para verificar que el parametro {@code nomPar} existe en
     * la base de datos. De lo contrario muestra un mensaje
     * informativo.
     * 
     * @param nomPar
     * Nombre del parametro.
     * @param valor
     * Valor asignado al parametro en la base de datos.
     * @return {@code true}: si el parametro existe y tiene valor
     * diferente a nulo.
     */
    private boolean validarParametro(String nomPar, String valor) {
        if (SysmanFunciones.validarVariableVacio(valor)) {
            JsfUtil.agregarMensajeInformativo(idioma.getString("TB_TB655")
                            .replace("#PAR#", nomPar));
            return false;
        }

        return true;
    }

    // <METODOS_CARGAR_LISTA>
    public void cargarListaMesInicial() {

        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.name(), compania);
        param.put(GeneralParameterEnum.ANO.name(), anio);

        try {
            UrlBean urlListMeI = UrlServiceUtil.getInstance()
                            .getUrlServiceByUrlByEnumID(
                                            EjecucionIngresosControladorUrlEnum.URL4379
                                                            .getValue());
            listaMesInicial = RegistroConverter.toListRegistro(
                            requestManager.getList(urlListMeI.getUrl(), param));
        } catch (SystemException ex) {
            Logger.getLogger(EjecucionIngresosControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            JsfUtil.agregarMensajeError(ex.getMessage());
        }

    }

    public void cargarListaMesFinal() {

        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.name(), compania);
        param.put(GeneralParameterEnum.ANO.name(), anio);
        param.put(GeneralParameterEnum.NUMERO.name(), mesInicial);

        try {
            UrlBean urlListMeF = UrlServiceUtil.getInstance()
                            .getUrlServiceByUrlByEnumID(
                                            EjecucionIngresosControladorUrlEnum.URL4804
                                                            .getValue());
            listaMesFinal = RegistroConverter.toListRegistro(
                            requestManager.getList(urlListMeF.getUrl(), param));
        } catch (SystemException ex) {
            Logger.getLogger(EjecucionIngresosControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            JsfUtil.agregarMensajeError(ex.getMessage());
        }

    }

    public void cargarListaano() {

        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.name(), compania);

        try {
            listaano = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            EjecucionIngresosControladorUrlEnum.URL5292
                                                                            .getValue())
                                            .getUrl(), param));
        } catch (SystemException ex) {
            Logger.getLogger(EjecucionIngresosControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            JsfUtil.agregarMensajeError(ex.getMessage());
        }

    }

    public void cargarListaCuentaInicial() {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        EjecucionIngresosControladorUrlEnum.URL5631
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.name(), compania);
        param.put(GeneralParameterEnum.ANO.name(), anio);

        listaCuentaInicial = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, strCodigo);
    }

    public void cargarListaCuentaFinal() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        EjecucionIngresosControladorUrlEnum.URL6605
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.name(), compania);
        param.put(GeneralParameterEnum.ANO.name(), anio);
        param.put(EjecucionIngresosControladorEnum.CUENTAINICIAL.getValue(),
                        cuentaInicial);

        listaCuentaFinal = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, strCodigo);
    }

    public void cargarListacentrocostoInicial() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        EjecucionIngresosControladorUrlEnum.URL7713
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.name(), compania);
        param.put(GeneralParameterEnum.ANO.name(), anio);

        listacentrocostoInicial = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, strCodigo);
    }

    public void cargarListacentrocostoFinal() {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        EjecucionIngresosControladorUrlEnum.URL8426
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.name(), compania);
        param.put(GeneralParameterEnum.ANO.name(), anio);
        param.put(EjecucionIngresosControladorEnum.CENTRO_COSTO.getValue(),
                        centroInicial);

        listacentrocostoFinal = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, strCodigo);
    }

    public void cargarListaFuenteInicial() {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        EjecucionIngresosControladorUrlEnum.URL9145
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.name(), compania);
        param.put(GeneralParameterEnum.ANO.name(), anio);

        listaFuenteInicial = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, strCodigo);
    }

    public void cargarListaFuenteFinal() {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        EjecucionIngresosControladorUrlEnum.URL9774
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.name(), compania);
        param.put(GeneralParameterEnum.ANO.name(), anio);
        param.put(EjecucionIngresosControladorEnum.AUXILIARINICIAL.getValue(),
                        fuenteInicial);

        listaFuenteFinal = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, strCodigo);
    }

    // </METODOS_CARGAR_LISTA>
    // <METODOS_BOTONES>
    public void oprimirPdf() {
        archivoDescarga = null;
        generaReporte(FORMATOS.PDF);
    }

    public void oprimirExcel() {
        archivoDescarga = null;
        generaReporte(FORMATOS.EXCEL97);
    }

    private boolean agregarMensaje(String campo, String mensaje) {
        if (SysmanFunciones.validarVariableVacio(campo)) {
            JsfUtil.agregarMensajeAlerta(mensaje);
            return true;
        }
        return false;
    }

    private void generaReporte(FORMATOS formato) {
        Map<String, Object> reemplazar = new HashMap<>();
        Map<String, Object> parametros = new HashMap<>();

        try {
            String nombreRpteLegal;
            String firmaRpteLegal;

            if (validaSalir()) {
                return;
            }

            String reporte = parManejaAuxiliar
                ? "000938FCRptEjecIngContCundFuente"
                : "000937FCRptEjecIngContCund";
            
            if (parmanejavalorRubro) {
            reporte = parmanejavalorRubro
                    ? "002284FCRptEjecIngContCundDuitama"
                    : "000937FCRptEjecIngContCund";
            }

            nombreRpteLegal = sysmanUtil.consultarParametro(compania,
                            "NOMBRE REPRESENTANTE LEGAL",
                            SessionUtil.getModulo(), new Date(), true);

            firmaRpteLegal = sysmanUtil.consultarParametro(
                            compania, "FIRMA REPRESENTANTE LEGAL",
                            SessionUtil.getModulo(), new Date(), true);

            // <REEMPLAZAR VARIABLES EN CONSULTA>
            reemplazar.put("anio", anio);
            reemplazar.put("mesIniAnt", Integer.parseInt(mesInicial) - 1);
            reemplazar.put("miles", enMiles ? "-1" : "0");
            reemplazar.put("mesInicial", mesInicial);
            reemplazar.put("mesFinal", mesFinal);

            reemplazar.put("centroInicial",
                            SysmanFunciones.colocarComillasTexto(
                                            centroInicial));

            reemplazar.put("centroFinal",
                            SysmanFunciones.colocarComillasTexto(centroFinal));

            if (parManejaAuxiliar) {
                reemplazar.put("fuenteInicial",
                                SysmanFunciones.colocarComillasTexto(
                                                fuenteInicial));

                reemplazar.put("fuenteFinal",
                                SysmanFunciones.colocarComillasTexto(
                                                fuenteFinal));
            }

            reemplazar.put("cuentaInicial",
                            SysmanFunciones.colocarComillasTexto(
                                            cuentaInicial));

            reemplazar.put("cuentaFinal",
                            SysmanFunciones.colocarComillasTexto(cuentaFinal));

            // </REEMPLAZAR VARIABLES EN CONSULTA>

            Reporteador.resuelveConsulta(reporte, Integer.parseInt(modulo),
                            reemplazar, parametros);

            agregaParametros(parametros, nombreRpteLegal, firmaRpteLegal);

            archivoDescarga = JsfUtil.exportarStreamed(reporte, parametros,
                            ConectorPool.ESQUEMA_SYSMAN, formato);
        } catch (JRException | IOException | SysmanException
                        | SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    private void agregaParametros(Map<String, Object> parametros,
        String nombreRpteLegal, String firmaRpteLegal) {
        parametros.put("PR_FORMATO",
                        enMiles ? "#,#00;(#,#00)" : "#,#00.00;(#,#00.00)");
        parametros.put("PR_ANO", anio);
        parametros.put("PR_NMES2", nmes2.toUpperCase());
        parametros.put("PR_NMES1", nmes1.toUpperCase());
        parametros.put("PR_MESINICIAL", mesInicial);
        parametros.put("PR_MESFINAL", mesFinal);
        parametros.put("PR_NOMBRE_REPRESENTANTE_LEGAL", nombreRpteLegal);
        parametros.put("PR_NOMBRE_REPRESENTANTE_LEGAL", firmaRpteLegal);
        parametros.put("PR_OBSERVACIONES", observaciones);
        parametros.put("PR_MILES",
                        enMiles ? "VALOR EN MILES DE PESOS" : "");
        parametros.put("PR_NOMBRECOMPANIA",
                        SessionUtil.getCompaniaIngreso().getNombre());
        parametros.put("PR_NITCOMPANIA",
                        SessionUtil.getCompaniaIngreso().getNit());

    }

    private boolean validaSalir() {
        boolean salir = false;
        if (agregarMensaje(centroInicial, idioma.getString("TB_TB640"))
            || agregarMensaje(centroFinal, idioma.getString("TB_TB641"))
            || agregarMensaje(cuentaInicial,
                            idioma.getString("TB_TB642"))) {
            salir = true;
        }

        if (agregarMensaje(cuentaFinal, idioma.getString("TB_TB643"))) {
            salir = true;
        }

        if (agregarMensaje(anio, idioma.getString("TB_TB648"))
            || agregarMensaje(mesInicial, idioma.getString("TB_TB646"))
            || agregarMensaje(mesFinal, idioma.getString("TB_TB647"))) {
            salir = true;
        }

        return salir;
    }

    // </METODOS_BOTONES>
    // <METODOS_CAMBIAR>
    public void cambiarMesInicial() {
        nmes1 = SysmanConstantes.NOMBRE_MESES_CONTABILIDAD[Integer
                        .parseInt(mesInicial)];

        cargarListaMesFinal();

        mesFinal = nmes2 = "";
    }

    public void cambiarMesFinal() {
        // <CODIGO_DESARROLLADO>
        nmes2 = SysmanConstantes.NOMBRE_MESES_CONTABILIDAD[Integer
                        .parseInt(mesFinal)];

        // </CODIGO_DESARROLLADO>
    }

    public void cambiarano() {
        mesInicial = mesFinal = nmes1 = nmes2;
        cuentaInicial = SysmanConstantes.DEFECTOINICIAL_STRING;
        cuentaFinal = SysmanConstantes.DEFECTOFINAL_STRING;
        centroInicial = SysmanConstantes.DEFECTOINICIAL_STRING;
        centroFinal = SysmanConstantes.DEFECTOFINAL_STRING;
        fuenteInicial = SysmanConstantes.DEFECTOINICIAL_STRING;
        fuenteFinal = SysmanConstantes.DEFECTOFINAL_STRING;
        cargarListaMesInicial();
        cargarListaCuentaInicial();
        cargarListaFuenteInicial();
        cargarListacentrocostoInicial();
    }

    public void seleccionarFilaCuentaInicial(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        cuentaInicial = asignarValorCampo(registroAux, strCodigo);
        cuentaFinal = SysmanConstantes.DEFECTOFINAL_STRING;

        cargarListaCuentaFinal();
    }

    public void seleccionarFilaCuentaFinal(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        cuentaFinal = asignarValorCampo(registroAux, strCodigo);
    }

    public void seleccionarFilacentrocostoInicial(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        centroInicial = asignarValorCampo(registroAux, strCodigo);
        centroFinal = SysmanConstantes.DEFECTOFINAL_STRING;
        cargarListacentrocostoFinal();
    }

    public void seleccionarFilacentrocostoFinal(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        centroFinal = asignarValorCampo(registroAux, strCodigo);
    }

    public void seleccionarFilaFuenteInicial(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        fuenteInicial = asignarValorCampo(registroAux, strCodigo);
        fuenteFinal = SysmanConstantes.DEFECTOFINAL_STRING;
        cargarListaFuenteFinal();
    }

    public void seleccionarFilaFuenteFinal(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        fuenteFinal = asignarValorCampo(registroAux, strCodigo);
    }

    /**
     * Verifica que el registro <code>reg</code> tenga una coleccion
     * de campos que no sea nula.
     * 
     * @param reg
     * @param campo
     * El campo a evaluar en la coleccion.
     * @return El valor del campo segun la coleccion.
     */
    private String asignarValorCampo(Registro reg, String campo) {
        return reg.getCampos().isEmpty() ? ""
            : SysmanFunciones.validarCampoVacio(reg.getCampos(), campo) ? ""
                : reg.getCampos().get(campo).toString();
    }

    // </METODOS_COMBOS_GRANDES>

    // <SET_GET_ATRIBUTOS>
    public boolean getEnMiles() {
        return enMiles;
    }

    public void setEnMiles(boolean enMiles) {
        this.enMiles = enMiles;
    }

    public String getCuentaInicial() {
        return cuentaInicial;
    }

    public void setCuentaInicial(String cuentaInicial) {
        this.cuentaInicial = cuentaInicial;
    }

    public String getCuentaFinal() {
        return cuentaFinal;
    }

    public void setCuentaFinal(String cuentaFinal) {
        this.cuentaFinal = cuentaFinal;
    }

    public String getMesInicial() {
        return mesInicial;
    }

    public void setMesInicial(String mesInicial) {
        this.mesInicial = mesInicial;
    }

    public String getMesFinal() {
        return mesFinal;
    }

    public void setMesFinal(String mesFinal) {
        this.mesFinal = mesFinal;
    }

    public String getCentroInicial() {
        return centroInicial;
    }

    public void setCentroInicial(String centroInicial) {
        this.centroInicial = centroInicial;
    }

    public String getCentroFinal() {
        return centroFinal;
    }

    public void setCentroFinal(String centroFinal) {
        this.centroFinal = centroFinal;
    }

    public String getFuenteInicial() {
        return fuenteInicial;
    }

    public void setFuenteInicial(String fuenteInicial) {
        this.fuenteInicial = fuenteInicial;
    }

    public String getFuenteFinal() {
        return fuenteFinal;
    }

    public void setFuenteFinal(String fuenteFinal) {
        this.fuenteFinal = fuenteFinal;
    }

    public String getAnio() {
        return anio;
    }

    public void setAnio(String anio) {
        this.anio = anio;
    }

    public String getNmes1() {
        return nmes1;
    }

    public void setNmes1(String nmes1) {
        this.nmes1 = nmes1;
    }

    public String getNmes2() {
        return nmes2;
    }

    public void setNmes2(String nmes2) {
        this.nmes2 = nmes2;
    }

    public StreamedContent getArchivoDescarga() {
        return archivoDescarga;
    }

    public String getObservaciones() {
        return observaciones;
    }

    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }

    // </SET_GET_ATRIBUTOS>
    // <SET_GET_PARAMETROS>
    public boolean isParManejaAuxiliar() {
        return parManejaAuxiliar;
    }

    public void setParManejaAuxiliar(boolean parManejaAuxiliar) {
        this.parManejaAuxiliar = parManejaAuxiliar;
    }

    // </SET_GET_PARAMETROS>
    // <SET_GET_LISTAS>
    public List<Registro> getListaMesInicial() {
        return listaMesInicial;
    }

    public void setListaMesInicial(List<Registro> listaMesInicial) {
        this.listaMesInicial = listaMesInicial;
    }

    public List<Registro> getListaMesFinal() {
        return listaMesFinal;
    }

    public void setListaMesFinal(List<Registro> listaMesFinal) {
        this.listaMesFinal = listaMesFinal;
    }

    public List<Registro> getListaano() {
        return listaano;
    }

    public void setListaano(List<Registro> listaano) {
        this.listaano = listaano;
    }

    // </SET_GET_LISTAS>
    // <SET_GET_LISTAS_COMBO_GRANDE>
    public RegistroDataModelImpl getListaCuentaInicial() {
        return listaCuentaInicial;
    }

    public void setListaCuentaInicial(
        RegistroDataModelImpl listaCuentaInicial) {
        this.listaCuentaInicial = listaCuentaInicial;
    }

    public RegistroDataModelImpl getListaCuentaFinal() {
        return listaCuentaFinal;
    }

    public void setListaCuentaFinal(RegistroDataModelImpl listaCuentaFinal) {
        this.listaCuentaFinal = listaCuentaFinal;
    }

    public RegistroDataModelImpl getListacentrocostoInicial() {
        return listacentrocostoInicial;
    }

    public void setListacentrocostoInicial(
        RegistroDataModelImpl listacentrocostoInicial) {
        this.listacentrocostoInicial = listacentrocostoInicial;
    }

    public RegistroDataModelImpl getListacentrocostoFinal() {
        return listacentrocostoFinal;
    }

    public void setListacentrocostoFinal(
        RegistroDataModelImpl listacentrocostoFinal) {
        this.listacentrocostoFinal = listacentrocostoFinal;
    }

    public RegistroDataModelImpl getListaFuenteInicial() {
        return listaFuenteInicial;
    }

    public void setListaFuenteInicial(
        RegistroDataModelImpl listaFuenteInicial) {
        this.listaFuenteInicial = listaFuenteInicial;
    }

    public RegistroDataModelImpl getListaFuenteFinal() {
        return listaFuenteFinal;
    }

    public void setListaFuenteFinal(RegistroDataModelImpl listaFuenteFinal) {
        this.listaFuenteFinal = listaFuenteFinal;
    }
    // </SET_GET_LISTAS_COMBO_GRANDE>
}
