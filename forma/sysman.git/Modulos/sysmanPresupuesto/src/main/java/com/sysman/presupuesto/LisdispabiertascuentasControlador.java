package com.sysman.presupuesto;

import com.sysman.beanbase.BeanBaseModal;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.ReportesBean.FORMATOS;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.persistencia.ConectorPool;
import com.sysman.presupuesto.enums.LisdispabiertascuentasControladorEnum;
import com.sysman.presupuesto.enums.LisdispabiertascuentasControladorUrlEnum;
import com.sysman.recursos.ejb.EjbSysmanUtilRemote;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.util.SysmanConstantes;
import com.sysman.util.SysmanFunciones;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.Date;
import java.util.HashMap;
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

import net.sf.dynamicreports.report.exception.DRException;
import net.sf.jasperreports.engine.JRException;

/**
 * Permite generar un reporte con el listado de disponibilidades
 * abiertas por cuentas en el m�dulo de presupuesto.
 * 
 * @author acaceres
 * @version 1, 07/07/2016
 * 
 * @author jrodrigueza
 * @version 2, 19/04/2017 Proceso de refactoring.
 * 
 * @author ybecerra
 * @version 3, 13/06/2017 Implementacion al llamado de
 * GeneralCodigoFormaEnum, para el codigo del formulario
 *
 * @author jgomez
 * @version 4, 9/08/2018 Se ajusta para que el reporte por excel salga
 * plano
 */
@ManagedBean
@ViewScoped
public class LisdispabiertascuentasControlador extends BeanBaseModal {
    /**
     * Constante a nivel de clase que almacena el codigo de la
     * compania en la cual inicio sesion el usuario, el valor de esta
     * constante es asignado en el constructor a la variable de sesion
     * correspondiente
     */
    private final String compania;
    /**
     * Constante a nivel de clase que almacena el codigo del mudulo
     * actual, el valor de esta constante es asignado en el
     * constructor a la variable de sesion correspondiente
     */
    private final String modulo;
    private final String cod;
    private final String nom;
    private final String consFormato;
    // <DECLARAR_ATRIBUTOS>
    private String tipoCuenta;
    private String cuentaInicial = SysmanConstantes.DEFECTOINICIAL_STRING;
    private String cuentaFinal = SysmanConstantes.DEFECTOFINAL_STRING;
    private Date fechaIInicial;
    private Date fechaFinal;
    private String nombreCuentaIni;
    private String nombreCuentaFin;
    private StreamedContent archivoDescarga;
    // </DECLARAR_ATRIBUTOS>
    // <DECLARAR_PARAMETROS>
    // </DECLARAR_PARAMETROS>
    // <DECLARAR_LISTAS>
    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>
    private RegistroDataModelImpl listaCuentaInicial;
    private RegistroDataModelImpl listaCuentaFinal;
    // </DECLARAR_LISTAS_COMBO_GRANDE>

    @EJB
    private EjbSysmanUtilRemote ejbSysmanUtil;

    /**
     * Creates a new instance of LisdispabiertascuentasControlador
     */
    public LisdispabiertascuentasControlador() {
        super();
        compania = SessionUtil.getCompania();
        modulo = SessionUtil.getModulo();
        cod = GeneralParameterEnum.CODIGO.getName();
        nom = GeneralParameterEnum.NOMBRE.getName();
        consFormato = "dd/MM/yyyy";
        try {
            numFormulario = GeneralCodigoFormaEnum.LISDISPABIERTASCUENTAS_CONTROLADOR
                            .getCodigo();
            validarPermisos();
            // <INI_ADICIONAL>
            // </INI_ADICIONAL>
        }
        catch (Exception ex) {
            Logger.getLogger(LisdispabiertascuentasControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            SessionUtil.redireccionarMenuPermisos();
        }
    }

    @PostConstruct
    public void inicializar() {
        // <CARGAR_LISTA>
        // </CARGAR_LISTA>
        // <CARGAR_LISTA_COMBO_GRANDE>
        abrirFormulario();
        cargarListaCuentaInicial();
        // </CARGAR_LISTA_COMBO_GRANDE>

    }

    @Override
    public void abrirFormulario() {
        // <CODIGO_DESARROLLADO>
        fechaIInicial = new Date();
        fechaFinal = new Date();
        cuentaInicial = "0";
        cuentaFinal = SysmanConstantes.CONS_MAX_ID;
        tipoCuenta = "1";
        // </CODIGO_DESARROLLADO>
    }

    // <METODOS_CARGAR_LISTA>
    public void cargarListaCuentaInicial() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        LisdispabiertascuentasControladorUrlEnum.URL4725
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(LisdispabiertascuentasControladorEnum.PARAM0.getValue(),
                        compania);
        param.put(LisdispabiertascuentasControladorEnum.PARAM1.getValue(),
                        SysmanFunciones.ano(fechaIInicial));

        listaCuentaInicial = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, cod);
    }

    public void cargarListaCuentaFinal() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        LisdispabiertascuentasControladorUrlEnum.URL5957
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(LisdispabiertascuentasControladorEnum.PARAM0.getValue(),
                        compania);
        param.put(LisdispabiertascuentasControladorEnum.PARAM1.getValue(),
                        SysmanFunciones.ano(fechaIInicial));
        param.put(LisdispabiertascuentasControladorEnum.PARAM2.getValue(),
                        cuentaInicial);
        param.put(LisdispabiertascuentasControladorEnum.PARAM3.getValue(),
                        SysmanConstantes.CONS_MAX_ID);

        listaCuentaFinal = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, cod);
    }

    // </METODOS_CARGAR_LISTA>

    public void obtenerReporte(FORMATOS formatos) {
        String reporte = null;
        String excelSalida = null;

        try {
            HashMap<String, Object> reemplazar = new HashMap<>();
            reemplazar.put("tipoCuenta", tipoCuenta);
            reemplazar.put("cuentaInicial", cuentaInicial);
            reemplazar.put("cuentaFinal", cuentaFinal);
            reemplazar.put("fechaInicial", SysmanFunciones
                            .convertirAFechaCadena(fechaIInicial, consFormato));
            reemplazar.put("fechaFinal", SysmanFunciones
                            .convertirAFechaCadena(fechaFinal, consFormato));

            // MANEJO DE PARAMETROS DE REEMPLAZO
            Map<String, Object> parametros = new HashMap<>();
            // MANEJO DE PARAMETROS DEL REPORTE
            String entreFechas = "Disponibilidades abiertas por cuentas entre "
                + SysmanFunciones.convertirAFechaCadena(fechaIInicial,
                                "dd/MM/yyyy")
                + " y "
                + SysmanFunciones.convertirAFechaCadena(fechaFinal,
                                "dd/MM/yyyy")
                + "";
            parametros.put("PR_ENTREFECHAS", entreFechas);

            if ("SI".equals(obtenerParametro(
                            "MANEJA AUXILIAR POR FUENTE EN PRESUPUESTO",
                            "NO"))) {
                reporte = "001815LisDispAbiertasCuentasF";
                excelSalida = "001814LisDispAbiertasF_Excel";
            }
            else {
                reporte = "000987LisDispAbiertasCuentas";
                excelSalida = "000985LisDispAbiertas_EXCEL";
            }
            archivoDescarga = JsfUtil.exportarExcelPlano(reporte, excelSalida,
                            ConectorPool.ESQUEMA_SYSMAN, formatos, reemplazar,
                            parametros, Integer.valueOf(modulo));

        }
        catch (FileNotFoundException e) {
            JsfUtil.agregarMensajeError(
                            idioma.getString("MSM_INFORME_VAR_NO_EXISTE")
                                            .replace("s$reporte$s", reporte)
                                + e.getMessage());
            logger.error(e.getMessage(), e);
        }

        catch (JRException | IOException | SysmanException | ParseException e) {
            JsfUtil.agregarMensajeError(e.getMessage());
            logger.error(e.getMessage(), e);

        }
        catch (SQLException e) {
            JsfUtil.agregarMensajeError(e.getMessage());
            logger.error(e.getMessage(), e);
        }
        catch (DRException e) {
            JsfUtil.agregarMensajeError(e.getMessage());
            logger.error(e.getMessage(), e);
        }

    }

    /**
     * Obtiene el valor almacenado en la base de datos para el
     * parametro ingresado.
     *
     * @param nombreParametro
     * Nombre del parametro a consultar en la base de datos.
     * @param valorDefault
     * Valor por omision en caso de nulo.
     * @return valor asignado al parametro
     */
    private String obtenerParametro(String nombreParametro,
        String valorDefault) {
        String parametro = null;
        try {
            parametro = ejbSysmanUtil.consultarParametro(
                            SessionUtil.getCompania(),
                            nombreParametro, SessionUtil.getModulo(),
                            new Date(), true);
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
        return parametro != null ? parametro : valorDefault;
    }

    // <METODOS_BOTONES>
    public void oprimirPdf() {
        // <CODIGO_DESARROLLADO>
        archivoDescarga = null;
        obtenerReporte(FORMATOS.PDF);
        // </CODIGO_DESARROLLADO>
    }

    public void oprimirexcel() {
        // <CODIGO_DESARROLLADO>
        archivoDescarga = null;
        obtenerReporte(FORMATOS.EXCEL97);
        // </CODIGO_DESARROLLADO>
    }

    // </METODOS_BOTONES>
    // <METODOS_CAMBIAR>
    public void cambiarFechaInicial() {
        // <CODIGO_DESARROLLADO>
        cuentaInicial = SysmanConstantes.DEFECTOINICIAL_STRING;
        nombreCuentaIni = "";
        cuentaFinal = SysmanConstantes.DEFECTOFINAL_STRING;
        nombreCuentaFin = "";
        cargarListaCuentaInicial();

        // </CODIGO_DESARROLLADO>
    }

    public void cambiarFechaFinal() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    // </METODOS_CAMBIAR>
    // <METODOS_COMBOS_GRANDES>
    public void seleccionarFilaCuentaInicial(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        cuentaInicial = registroAux.getCampos().get(cod).toString();
        nombreCuentaIni = registroAux.getCampos().get(nom).toString();
        cuentaFinal = SysmanConstantes.DEFECTOFINAL_STRING;
        ;
        nombreCuentaFin = "";
        cargarListaCuentaFinal();
    }

    public void seleccionarFilaCuentaFinal(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        cuentaFinal = registroAux.getCampos().get(cod).toString();
        nombreCuentaFin = registroAux.getCampos().get(nom).toString();
    }

    // </METODOS_COMBOS_GRANDES>

    // <SET_GET_ATRIBUTOS>
    public String getTipoCuenta() {
        return tipoCuenta;
    }

    public void setTipoCuenta(String tipoCuenta) {
        this.tipoCuenta = tipoCuenta;
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

    public Date getFechaIInicial() {
        return fechaIInicial;
    }

    public void setFechaIInicial(Date fechaIInicial) {
        this.fechaIInicial = fechaIInicial;
    }

    public Date getFechaFinal() {
        return fechaFinal;
    }

    public void setFechaFinal(Date fechaFinal) {
        this.fechaFinal = fechaFinal;
    }

    public void setArchivoDescarga(StreamedContent archivoDescarga) {
        this.archivoDescarga = archivoDescarga;
    }

    public String getNombreCuentaIni() {
        return nombreCuentaIni;
    }

    public void setNombreCuentaIni(String nombreCuentaIni) {
        this.nombreCuentaIni = nombreCuentaIni;
    }

    public String getNombreCuentaFin() {
        return nombreCuentaFin;
    }

    public void setNombreCuentaFin(String nombreCuentaFin) {
        this.nombreCuentaFin = nombreCuentaFin;
    }

    public StreamedContent getArchivoDescarga() {
        return archivoDescarga;
    }

    // </SET_GET_ATRIBUTOS>
    // <SET_GET_PARAMETROS>
    // </SET_GET_PARAMETROS>
    // <SET_GET_LISTAS>
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

    // </SET_GET_LISTAS_COMBO_GRANDE>
}
