package com.sysman.presupuesto;

import com.sysman.beanbase.BeanBaseModal;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.ReportesBean.FORMATOS;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.persistencia.ConectorPool;
import com.sysman.presupuesto.enums.ListadoTercerosControladorEnum;
import com.sysman.presupuesto.enums.ListadoTercerosControladorUrlEnum;
import com.sysman.reportes.Reporteador;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.util.SysmanFunciones;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.event.SelectEvent;
import org.primefaces.model.StreamedContent;

import net.sf.jasperreports.engine.JRException;

/**
 *
 * @author jrodriguezr
 * @version 1, 19/07/2016
 * @version 2, 19/04/2017 jrodriguezr Se refactoriza el codigo SQL de
 * las listas para utilizar dss.
 * @version 3, 24/04/2017 jrodriguezr Se refactoriza el codigo
 * ajustando los llamados a funciones, procedimientos y metodos de la
 * clase Acciones a llamados a EJB.
 * 
 * @author eamaya
 * @version 4.0, 13/06/2017 Se cambi� el llamado del c�digo del
 * formulario y actualizaci�n de ConnectorPool
 * 
 */
@ManagedBean
@ViewScoped
public class ListadoTercerosControlador extends BeanBaseModal {
    private final String compania;

    private final String strNombre;

    // <DECLARAR_ATRIBUTOS>
    private boolean porNombre;
    private boolean porCodigo;
    private String terceroNitInicial;
    private String terceroNitFinal;
    private String terceroNombreIni;
    private String terceroNombreFin;
    private StreamedContent archivoDescarga;
    // </DECLARAR_ATRIBUTOS>
    // <DECLARAR_PARAMETROS>
    // </DECLARAR_PARAMETROS>
    // <DECLARAR_LISTAS>
    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>
    private RegistroDataModelImpl listaTerceroNitInicial;
    private RegistroDataModelImpl listaTerceroNitFinal;
    private RegistroDataModelImpl listaterceroNombreI;
    private RegistroDataModelImpl listaterceroNombreF;
    // </DECLARAR_LISTAS_COMBO_GRANDE>

    /**
     * Creates a new instance of ListadoTercerosControlador
     */
    public ListadoTercerosControlador() {
        super();
        compania = SessionUtil.getCompania();
        strNombre = "NOMBRE";

        try {
            numFormulario = GeneralCodigoFormaEnum.LISTADO_TERCEROS_CONTROLADOR
                            .getCodigo();
            validarPermisos();
            // <INI_ADICIONAL>
            // </INI_ADICIONAL>
        }
        catch (Exception ex) {
            Logger.getLogger(ListadoTercerosControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            SessionUtil.redireccionarMenuPermisos();
        }

    }

    @PostConstruct
    public void inicializar() {
        // <CARGAR_LISTA>
        // </CARGAR_LISTA>
        // <CARGAR_LISTA_COMBO_GRANDE>
        porCodigo = true;
        cargarListaTerceroNitInicial();
        // </CARGAR_LISTA_COMBO_GRANDE>
        abrirFormulario();
    }

    @Override
    public void abrirFormulario() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    // <METODOS_CARGAR_LISTA>
    public void cargarListaTerceroNitInicial() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        ListadoTercerosControladorUrlEnum.URL3411
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        listaTerceroNitInicial = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, "NIT");
    }

    public void cargarListaTerceroNitFinal() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        ListadoTercerosControladorUrlEnum.URL4091
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(ListadoTercerosControladorEnum.NITINICIAL.getValue(),
                        terceroNitInicial);
        listaTerceroNitFinal = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, "NIT");
    }

    public void cargarListaterceroNombreI() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        ListadoTercerosControladorUrlEnum.URL4848
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        listaterceroNombreI = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, strNombre);
    }

    public void cargarListaterceroNombreF() {
        //
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        ListadoTercerosControladorUrlEnum.URL5477
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(ListadoTercerosControladorEnum.TERCEROINICIAL.getValue(),
                        terceroNombreIni);
        listaterceroNombreF = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, strNombre);
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
        generaReporte(FORMATOS.EXCEL);
        // </CODIGO_DESARROLLADO>
    }

    private void generaReporte(FORMATOS formato) {

        if (!validarVaciosPorCodigo()) {
            return;
        }
        try {
            HashMap<String, Object> reemplazar = new HashMap<>();
            Map<String, Object> parametros = new HashMap<>();
            String reporte = "000355LisTerceros";

            String strOrderBy = porNombre ? "WHERE TERCERO.COMPANIA = '"
                + compania + "' AND TRIM(UPPER(TERCERO.NOMBRE)) BETWEEN '"
                + terceroNombreIni + "' AND '" + terceroNombreFin
                + "' \n"
                + " ORDER BY TERCERO.NOMBRE"
                : "WHERE  TERCERO.COMPANIA = '" + compania
                    + "' AND  TERCERO.NIT BETWEEN '"
                    + terceroNitInicial + "' AND '"
                    + terceroNitFinal + "' \n"
                    + " ORDER BY TRIM(UPPER(TERCERO.NIT))";
            String tercero = "Listado entre terceros " + (porNombre
                ? terceroNombreIni + " y " + terceroNombreFin + "."
                : terceroNitInicial + " y " + terceroNitFinal
                    + ".");

            reemplazar.put("strOrderBy", strOrderBy);

            Reporteador.resuelveConsulta(reporte,
                            Integer.parseInt(SessionUtil.getModulo()),
                            reemplazar, parametros);

            parametros.put("PR_NOMBRECOMPANIA",
                            SessionUtil.getCompaniaIngreso().getNombre());
            parametros.put("PR_TERCERO", tercero);

            archivoDescarga = JsfUtil.exportarStreamed(reporte, parametros,
                            ConectorPool.ESQUEMA_SYSMAN, formato);
        }
        catch (JRException | IOException | SysmanException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    private boolean validarVaciosPorCodigo() {
        if (porCodigo) {
            if (SysmanFunciones.validarVariableVacio(terceroNitInicial)) {
                JsfUtil.agregarMensajeAlerta(
                                idioma.getString("MSM_DEBE_TERCERO_INI"));
                return false;
            }
            if (SysmanFunciones.validarVariableVacio(terceroNitFinal)) {
                JsfUtil.agregarMensajeAlerta(
                                idioma.getString("MSM_DEBE_TERCERO_FIN"));
                return false;
            }
        }
        else {
            if (SysmanFunciones.validarVariableVacio(terceroNombreIni)) {
                JsfUtil.agregarMensajeAlerta(
                                idioma.getString("MSM_DEBE_TERCERO_INI"));
                return false;
            }
            else if (SysmanFunciones.validarVariableVacio(terceroNombreFin)) {
                JsfUtil.agregarMensajeAlerta(
                                idioma.getString("MSM_DEBE_TERCERO_FIN"));
                return false;
            }
        }

        return true;
    }

    // </METODOS_BOTONES>
    // <METODOS_CAMBIAR>
    public void cambiarNombre() {
        // <CODIGO_DESARROLLADO>
        if (porNombre) {
            porCodigo = false;
            cargarListaterceroNombreI();
            terceroNombreIni = terceroNombreFin = terceroNitInicial = terceroNitFinal = null;
        }
        else {
            porNombre = true;
        }

        // </CODIGO_DESARROLLADO>
    }

    public void cambiarCodigo() {
        // <CODIGO_DESARROLLADO>
        if (porCodigo) {
            porNombre = false;
            cargarListaTerceroNitInicial();
            terceroNombreIni = terceroNombreFin = terceroNitInicial = terceroNitFinal = null;
        }
        else {
            porCodigo = true;
        }
        // </CODIGO_DESARROLLADO>
    }

    // </METODOS_CAMBIAR>
    // <METODOS_COMBOS_GRANDES>
    public void onRowSelectTerceroNitInicial(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        terceroNitInicial = SysmanFunciones
                        .nvl(registroAux.getCampos().get("NIT"), "").toString();
        cargarListaTerceroNitFinal();
        terceroNitFinal = null;
    }

    public void onRowSelectTerceroNitFinal(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        terceroNitFinal = SysmanFunciones
                        .nvl(registroAux.getCampos().get("NIT"), "").toString();
    }

    public void onRowSelectterceroNombreI(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        terceroNombreIni = SysmanFunciones
                        .nvl(registroAux.getCampos().get(strNombre), "")
                        .toString();
        cargarListaterceroNombreF();
        terceroNombreFin = null;
    }

    public void onRowSelectterceroNombreF(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        terceroNombreFin = SysmanFunciones
                        .nvl(registroAux.getCampos().get(strNombre), "")
                        .toString();
    }

    // </METODOS_COMBOS_GRANDES>

    // <SET_GET_ATRIBUTOS>
    public boolean getPorNombre() {
        return porNombre;
    }

    public void setPorNombre(boolean porNombre) {
        this.porNombre = porNombre;
    }

    public boolean getPorCodigo() {
        return porCodigo;
    }

    public void setPorCodigo(boolean porCodigo) {
        this.porCodigo = porCodigo;
    }

    public String getTerceroNitInicial() {
        return terceroNitInicial;
    }

    public void setTerceroNitInicial(String terceroNitInicial) {
        this.terceroNitInicial = terceroNitInicial;
    }

    public String getTerceroNitFinal() {
        return terceroNitFinal;
    }

    public void setTerceroNitFinal(String terceroNitFinal) {
        this.terceroNitFinal = terceroNitFinal;
    }

    public String getTerceroNombreIni() {
        return terceroNombreIni;
    }

    public void setTerceroNombreIni(String terceroNombreIni) {
        this.terceroNombreIni = terceroNombreIni;
    }

    public String getTerceroNombreFin() {
        return terceroNombreFin;
    }

    public void setTerceroNombreFin(String terceroNombreFin) {
        this.terceroNombreFin = terceroNombreFin;
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
    public RegistroDataModelImpl getListaTerceroNitInicial() {
        return listaTerceroNitInicial;
    }

    public void setListaTerceroNitInicial(
        RegistroDataModelImpl listaTerceroNitInicial) {
        this.listaTerceroNitInicial = listaTerceroNitInicial;
    }

    public RegistroDataModelImpl getListaTerceroNitFinal() {
        return listaTerceroNitFinal;
    }

    public void setListaTerceroNitFinal(
        RegistroDataModelImpl listaTerceroNitFinal) {
        this.listaTerceroNitFinal = listaTerceroNitFinal;
    }

    public RegistroDataModelImpl getListaterceroNombreI() {
        return listaterceroNombreI;
    }

    public void setListaterceroNombreI(
        RegistroDataModelImpl listaterceroNombreI) {
        this.listaterceroNombreI = listaterceroNombreI;
    }

    public RegistroDataModelImpl getListaterceroNombreF() {
        return listaterceroNombreF;
    }

    public void setListaterceroNombreF(
        RegistroDataModelImpl listaterceroNombreF) {
        this.listaterceroNombreF = listaterceroNombreF;
    }
    // </SET_GET_LISTAS_COMBO_GRANDE>
}
