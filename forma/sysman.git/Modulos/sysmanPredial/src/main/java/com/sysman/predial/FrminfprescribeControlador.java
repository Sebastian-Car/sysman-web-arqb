package com.sysman.predial;

import com.sysman.beanbase.BeanBaseModal;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.ReportesBean;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.persistencia.ConectorPool;
import com.sysman.predial.ejb.EjbPredialCeroRemote;
import com.sysman.predial.enums.FrminfprescribeControladorEnum;
import com.sysman.predial.enums.FrminfprescribeControladorUrlEnum;
import com.sysman.reportes.Reporteador;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.util.SysmanConstantes;
import com.sysman.util.SysmanFunciones;

import java.io.IOException;
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

import net.sf.jasperreports.engine.JRException;

/**
 *
 * @author lcortes
 * @version 1, 14/06/2016
 *
 * @version 2, 13/06/2017 jrodriguezr Se refactoriza el c�digo: Se
 * pasa el numero del formulario al enumerado, se eliminan conexiones
 * y se ajustan metodos de generacion de reportes.
 * 
 * @modifier amonroy
 * @version 3, 04/07/2017 Se realiza el Proceso de Refactoring e
 * implementacion de EJBs para las funciones y procedimientos que son
 * llamadas en el controlador
 */
@ManagedBean
@ViewScoped
public class FrminfprescribeControlador extends BeanBaseModal {
    private final String compania;

    /**
     * Constante a nivel de clase que aloja el NUMERO_ORDEN_PREDIAL
     */
    private final String numOrdenPrevio;

    /**
     * Constante que identifica el nombre del campo CODIGO
     */
    private final String campoCodigo;
    /**
     * Constante que identifica el nombre del campo NOMBRE
     */
    private final String campoNombre;
    // <DECLARAR_ATRIBUTOS>
    private String codInicial;
    private String codFinal;
    private Date fechaInicial;
    private Date fechaFinal;
    private String propietarioInicial;
    private String propietarioFinal;
    private StreamedContent archivoDescarga;
    /**
     * Implementacion del EJB de EjbPredialCeroRemote para hacer el
     * llamado a las funciones que se invocan dentro del Controlador y
     * se encuentran almacenadas en el paquete PCK_PREDIAL
     */
    @EJB
    private EjbPredialCeroRemote ejbPredialCero;
    // </DECLARAR_ATRIBUTOS>
    // <DECLARAR_PARAMETROS>
    // </DECLARAR_PARAMETROS>
    // <DECLARAR_LISTAS>
    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>
    private RegistroDataModelImpl listaCodInicial;
    private RegistroDataModelImpl listaCodFinal;
    // </DECLARAR_LISTAS_COMBO_GRANDE>

    /**
     * Creates a new instance of FrminfprescribeControlador
     */
    public FrminfprescribeControlador() {
        super();
        compania = SessionUtil.getCompania();
        numOrdenPrevio = SysmanConstantes.NUMERO_ORDEN_PREDIAL;
        campoCodigo = "CODIGO";
        campoNombre = "NOMBRE";
        try {
            numFormulario = GeneralCodigoFormaEnum.FRMINFPRESCRIBE_CONTROLADOR
                            .getCodigo();
            validarPermisos();
            // <INI_ADICIONAL>
            // </INI_ADICIONAL>
        }
        catch (Exception ex) {
            Logger.getLogger(FrminfprescribeControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            SessionUtil.redireccionarMenuPermisos();
        }
    }

    @PostConstruct
    public void inicializar() {
        // <CARGAR_LISTA>
        // </CARGAR_LISTA>
        // <CARGAR_LISTA_COMBO_GRANDE>
        cargarListaCodInicial();
        // </CARGAR_LISTA_COMBO_GRANDE>
        abrirFormulario();
    }

    @Override
    public void abrirFormulario() {
        // <CODIGO_DESARROLLADO>
        fechaInicial = new Date();
        fechaFinal = new Date();
        // </CODIGO_DESARROLLADO>
    }

    // <METODOS_CARGAR_LISTA>
    /**
     * Carga la lista listaCodInicial asociada al combo
     * 'CodigoInicial'
     */
    public void cargarListaCodInicial() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        FrminfprescribeControladorUrlEnum.URL4409
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.NUMERO_ORDEN.getName(), numOrdenPrevio);

        listaCodInicial = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, campoCodigo);
    }

    /**
     * Carga la lista listaCodInicial asociada al combo 'CodigoFinal'
     */
    public void cargarListaCodFinal() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        FrminfprescribeControladorUrlEnum.URL5371
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.NUMERO_ORDEN.getName(), numOrdenPrevio);
        param.put(FrminfprescribeControladorEnum.CODIGO_INICIAL.getValue(),
                        codInicial);

        listaCodFinal = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, campoCodigo);
    }

    // </METODOS_CARGAR_LISTA>
    // <METODOS_BOTONES>
    public void oprimirCmdExcel() {
        // <CODIGO_DESARROLLADO>
        archivoDescarga = null;
        if (!validarVacios()) {
            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB623"));
            return;
        }
        if (fechaInicial.after(fechaFinal)) {
            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB624"));
            return;
        }
        generarInforme(ReportesBean.FORMATOS.EXCEL97);
        // </CODIGO_DESARROLLADO>
    }

    public void oprimirCmdImprimir() {
        // <CODIGO_DESARROLLADO>
        archivoDescarga = null;
        if (!validarVacios()) {
            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB623"));
            return;
        }
        if (fechaInicial.after(fechaFinal)) {
            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB624"));
            return;
        }
        generarInforme(ReportesBean.FORMATOS.PDF);
        // </CODIGO_DESARROLLADO>
    }

    private boolean validarVacios() {
        if ((fechaInicial == null) || (fechaFinal == null)
            || SysmanFunciones.validarVariableVacio(codInicial)) {
            return false;
        }
        if (SysmanFunciones.validarVariableVacio(codFinal)) {
            return false;
        }
        return true;
    }

    public void generarInforme(ReportesBean.FORMATOS formato) {

        String encabezado = "";
        HashMap<String, Object> reemplazos = new HashMap<>();
        Map<String, Object> parametros = new HashMap<>();

        // Reemplazos valores consulta reporte
        reemplazos.put("codInicial", codInicial);
        reemplazos.put("codFinal", codFinal);
        reemplazos.put("fechaIni", castingFecha(fechaInicial));
        reemplazos.put("fechaFin", castingFecha(fechaFinal));
        reemplazos.put("numeroOrden", numOrdenPrevio);
        try {
            // Inicio Parametros Reporte
            parametros.put("PR_CODINICIAL", codInicial);
            parametros.put("PR_CODFINAL", codFinal);
            parametros.put("PR_FECHAINI", SysmanFunciones
                            .convertirAFechaCadena(fechaInicial));
            parametros.put("PR_FECHAFIN",
                            SysmanFunciones.convertirAFechaCadena(fechaFinal));
            for (int i = 14; i <= 20; i++) {
                encabezado = encabezadoColumna(i);
                parametros.put("PR_ENCABEZADOCOLUMNC" + i, encabezado);
            }
            Reporteador.resuelveConsulta("000907INFPRESCRIBE",
                            Integer.parseInt(
                                            SessionUtil.getModulo()),
                            reemplazos,
                            parametros);

            archivoDescarga = JsfUtil.exportarStreamed("000907INFPRESCRIBE",
                            parametros, ConectorPool.ESQUEMA_SYSMAN, formato);
        }
        catch (JRException | IOException | SysmanException | ParseException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    /**
     * Hace el casting a la fecha, equivalente a utilizar un TO_DATE
     * en oracle.
     *
     * @param fecha
     * @return La fecha concatenada en el TO_DATE
     */
    private String castingFecha(Date fecha) {
        return SysmanFunciones.formatearFecha(fecha);
    }

    /**
     * Obtiene el nombre del concepto que se visualizara en el reporte
     * 
     * @param concepto
     * Numero del concepto a consultar
     * @return Valor del encabezado para el concepto a consultar
     */
    public String encabezadoColumna(int concepto) {
        String encabezado = "";
        try {
            encabezado = ejbPredialCero.consultarEncabezadoDeColumna(compania,
                            concepto);
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

        return encabezado;
    }

    // </METODOS_BOTONES>
    // <METODOS_CAMBIAR>
    // </METODOS_CAMBIAR>
    // <METODOS_COMBOS_GRANDES>
    public void seleccionarFilaCodInicial(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        codInicial = SysmanFunciones
                        .nvl(registroAux.getCampos().get(campoCodigo), "")
                        .toString();
        propietarioInicial = SysmanFunciones
                        .nvl(registroAux.getCampos().get(campoNombre), "")
                        .toString();
        propietarioFinal = codFinal = null;
        cargarListaCodFinal();
    }

    public void seleccionarFilaCodFinal(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        codFinal = SysmanFunciones
                        .nvl(registroAux.getCampos().get(campoCodigo), "")
                        .toString();
        propietarioFinal = SysmanFunciones
                        .nvl(registroAux.getCampos().get(campoNombre), "")
                        .toString();
    }

    // </METODOS_COMBOS_GRANDES>

    // <SET_GET_ATRIBUTOS>
    public String getCodInicial() {
        return codInicial;
    }

    public void setCodInicial(String codInicial) {
        this.codInicial = codInicial;
    }

    public String getCodFinal() {
        return codFinal;
    }

    public void setCodFinal(String codFinal) {
        this.codFinal = codFinal;
    }

    public Date getFechaInicial() {
        return fechaInicial;
    }

    public void setFechaInicial(Date fechaInicial) {
        this.fechaInicial = fechaInicial;
    }

    public Date getFechaFinal() {
        return fechaFinal;
    }

    public void setFechaFinal(Date fechaFinal) {
        this.fechaFinal = fechaFinal;
    }

    public String getPropietarioInicial() {
        return propietarioInicial;
    }

    public void setPropietarioInicial(String propietarioInicial) {
        this.propietarioInicial = propietarioInicial;
    }

    public String getPropietarioFinal() {
        return propietarioFinal;
    }

    public void setPropietarioFinal(String propietarioFinal) {
        this.propietarioFinal = propietarioFinal;
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
    public RegistroDataModelImpl getListaCodInicial() {
        return listaCodInicial;
    }

    public void setListaCodInicial(RegistroDataModelImpl listaCodInicial) {
        this.listaCodInicial = listaCodInicial;
    }

    public RegistroDataModelImpl getListaCodFinal() {
        return listaCodFinal;
    }

    public void setListaCodFinal(RegistroDataModelImpl listaCodFinal) {
        this.listaCodFinal = listaCodFinal;
    }
    // </SET_GET_LISTAS_COMBO_GRANDE>
}
