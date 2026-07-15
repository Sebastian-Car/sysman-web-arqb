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
import com.sysman.presupuesto.enums.EjecucionMensualIngresosControladorEnum;
import com.sysman.presupuesto.enums.EjecucionMensualIngresosControladorUrlEnum;
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
 * @version 1, 08/07/2016 12:54:46 -- Modificado por jrodriguezr
 * 
 * @version 2, 18/04/2017, pespitia :<br>
 * Se aplicaron las recomendaciones de SonarLint.<br>
 * Se aplico refactoring.<br>
 * Manejo de EJBs.
 * 
 * @author eamaya
 * @version 3.0, 13/06/2017 Se cambi� el llamado del c�digo del
 * formulario y actualizaci�n de ConnectorPool
 * 
 */
@ManagedBean
@ViewScoped
public class EjecucionMensualIngresosControlador extends BeanBaseModal {
    private final String compania;

    /**
     * Constante a nivel de clase que aloja el modulo con el que el
     * usuario esta interactuando.
     */
    private final String modulo;

    /**
     * Constante a nivel de clase que aloja el nombre del enumerado
     * <code>GeneralParameterEnum.CODIGO</code>
     */
    private final String cCodigo;

    // <DECLARAR_ATRIBUTOS>
    private String anio;
    private String mesInicial;
    private String cuentaInicial;
    private String cuentaFinal;
    private String nmes1;
    private StreamedContent archivoDescarga;
    // </DECLARAR_ATRIBUTOS>
    // <DECLARAR_PARAMETROS>
    // </DECLARAR_PARAMETROS>
    // <DECLARAR_LISTAS>
    private List<Registro> listaanio;
    private List<Registro> listaMesInicial;
    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>
    private RegistroDataModelImpl listaCuentaInicial;
    private RegistroDataModelImpl listaCuentaFinal;
    // </DECLARAR_LISTAS_COMBO_GRANDE>

    // </DECLARAR_EJBs>
    @EJB
    private EjbSysmanUtilRemote ejbSysmanUtil;
    // </DECLARAR_EJBs>

    /**
     * Creates a new instance of EjecucionMensualIngresosControlador
     */
    public EjecucionMensualIngresosControlador() {
        super();

        compania = SessionUtil.getCompania();
        modulo = SessionUtil.getModulo();

        cCodigo = GeneralParameterEnum.CODIGO.getName();

        try {
            numFormulario = GeneralCodigoFormaEnum.EJECUCION_MENSUAL_INGRESOS_CONTROLADOR
                            .getCodigo();
            validarPermisos();
            // <INI_ADICIONAL>
            // </INI_ADICIONAL>
        }
        catch (Exception ex) {
            Logger.getLogger(
                            EjecucionMensualIngresosControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            SessionUtil.redireccionarMenuPermisos();
        }
    }

    @PostConstruct
    public void inicializar() {
        // <CARGAR_LISTA>
        cargarListaanio();
        anio = String.valueOf(SysmanFunciones.ano(new Date()));
        cargarListaMesInicial();
        mesInicial = String.valueOf(SysmanFunciones.mes(new Date()));
        nmes1 = SysmanConstantes.NOMBRE_MESES_CONTABILIDAD[Integer
                        .parseInt(mesInicial)];
        // </CARGAR_LISTA>
        // <CARGAR_LISTA_COMBO_GRANDE>
        cargarListaCuentaInicial();
        // </CARGAR_LISTA_COMBO_GRANDE>
        abrirFormulario();
    }

    @Override
    public void abrirFormulario() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    // <METODOS_CARGAR_LISTA>
    public void cargarListaanio() {
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        try {
            listaanio = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            EjecucionMensualIngresosControladorUrlEnum.URL4048
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    public void cargarListaMesInicial() {
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.ANO.getName(), anio);

        try {
            listaMesInicial = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            EjecucionMensualIngresosControladorUrlEnum.URL4385
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    public void cargarListaCuentaInicial() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        EjecucionMensualIngresosControladorUrlEnum.URL4813
                                                        .getValue());

        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.ANO.getName(), anio);

        listaCuentaInicial = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, cCodigo);
    }

    public void cargarListaCuentaFinal() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        EjecucionMensualIngresosControladorUrlEnum.URL5730
                                                        .getValue());

        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.ANO.getName(), anio);
        param.put(EjecucionMensualIngresosControladorEnum.CUENTAINICIAL
                        .getValue(), cuentaInicial);

        listaCuentaFinal = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, cCodigo);
    }

    // </METODOS_CARGAR_LISTA>
    // <METODOS_BOTONES>
    public void oprimirPdf() {
        // <CODIGO_DESARROLLADO>
        archivoDescarga = null;
        generaReporte(FORMATOS.PDF);
        // </CODIGO_DESARROLLADO>
    }

    public void oprimirExcel() {
        // <CODIGO_DESARROLLADO>
        archivoDescarga = null;
        generaReporte(FORMATOS.EXCEL97);
        // </CODIGO_DESARROLLADO>
    }

    public boolean validarVacios() {
        if (SysmanFunciones.validarVariableVacio(cuentaInicial)) {
            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB296"));
            return false;
        }
        if (SysmanFunciones.validarVariableVacio(cuentaFinal)) {
            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB297"));
            return false;
        }
        if (SysmanFunciones.validarVariableVacio(mesInicial)) {
            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB298"));
            return false;
        }
        if (SysmanFunciones.validarVariableVacio(anio)) {
            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB299"));
            return false;
        }

        return true;
    }

    private void generaReporte(FORMATOS formato) {

        if (!validarVacios()) {
            return;
        }
        try {
            HashMap<String, Object> reemplazar = new HashMap<>();
            Map<String, Object> parametros = new HashMap<>();
            String reporte = "000966LisEjecucionIngresosCTRECE";
            reemplazar.put("cuentaInicial", cuentaInicial);
            reemplazar.put("cuentaFinal", cuentaFinal);
            reemplazar.put("mesInicial", mesInicial);
            reemplazar.put("anio", anio);

            Reporteador.resuelveConsulta(reporte,
                            Integer.parseInt(SessionUtil.getModulo()),
                            reemplazar, parametros);

            String cargoPresupuesto = ejbSysmanUtil.consultarParametro(compania,
                            "CARGO PRESUPUESTO", modulo, new Date(), false);

            String nombreJefePresupuesto = ejbSysmanUtil.consultarParametro(
                            compania, "NOMBRE DE JEFE DE PRESUPUESTO",
                            modulo,
                            new Date(), false);

            parametros.put("PR_CARGO_PRESUPUESTO", cargoPresupuesto);
            parametros.put("PR_NOMBRE_DE_JEFE_DE_PRESUPUESTO",
                            nombreJefePresupuesto);
            parametros.put("PR_MES", mesInicial);
            parametros.put("PR_ANO", anio);
            parametros.put("PR_NMES", nmes1);
            parametros.put("PR_NOMBRECOMPANIA",
                            SessionUtil.getCompaniaIngreso().getNombre());
            parametros.put("PR_NITCOMPANIA",
                            SessionUtil.getCompaniaIngreso().getNit());

            archivoDescarga = JsfUtil.exportarStreamed(reporte, parametros,
                            ConectorPool.ESQUEMA_SYSMAN, formato);
        }
        catch (JRException | IOException | SysmanException
                        | SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    // </METODOS_BOTONES>
    // <METODOS_CAMBIAR>
    public void cambiaranio() {
        // <CODIGO_DESARROLLADO>
        mesInicial = nmes1 = cuentaInicial = cuentaFinal = null;
        cargarListaMesInicial();
        cargarListaCuentaInicial();
        // </CODIGO_DESARROLLADO>
    }

    public void cambiarMesInicial() {
        // <CODIGO_DESARROLLADO>
        nmes1 = SysmanConstantes.NOMBRE_MESES_CONTABILIDAD[Integer
                        .parseInt(mesInicial)];
        // </CODIGO_DESARROLLADO>
    }

    // </METODOS_CAMBIAR>
    // <METODOS_COMBOS_GRANDES>
    public void seleccionarFilaCuentaInicial(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        cuentaInicial = registroAux.getCampos().get(cCodigo).toString();
        cargarListaCuentaFinal();
        cuentaFinal = null;
    }

    public void seleccionarFilaCuentaFinal(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        cuentaFinal = registroAux.getCampos().get(cCodigo).toString();
    }

    // </METODOS_COMBOS_GRANDES>

    // <SET_GET_ATRIBUTOS>
    public String getAnio() {
        return anio;
    }

    public void setAnio(String anio) {
        this.anio = anio;
    }

    public String getMesInicial() {
        return mesInicial;
    }

    public void setMesInicial(String mesInicial) {
        this.mesInicial = mesInicial;
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

    public String getNmes1() {
        return nmes1;
    }

    public void setNmes1(String nmes1) {
        this.nmes1 = nmes1;
    }

    public StreamedContent getArchivoDescarga() {
        return archivoDescarga;
    }

    // </SET_GET_ATRIBUTOS>
    // <SET_GET_PARAMETROS>
    // </SET_GET_PARAMETROS>
    // <SET_GET_LISTAS>
    public List<Registro> getListaanio() {
        return listaanio;
    }

    public void setListaanio(List<Registro> listaanio) {
        this.listaanio = listaanio;
    }

    public List<Registro> getListaMesInicial() {
        return listaMesInicial;
    }

    public void setListaMesInicial(List<Registro> listaMesInicial) {
        this.listaMesInicial = listaMesInicial;
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
    // </SET_GET_LISTAS_COMBO_GRANDE>
}
