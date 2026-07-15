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
import com.sysman.jsfutil.ReportesBean;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.persistencia.ConectorPool;
import com.sysman.presupuesto.enums.InftrasldospacControladorEnum;
import com.sysman.presupuesto.enums.InftrasldospacControladorUrlEnum;
import com.sysman.reportes.Reporteador;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.util.SysmanFunciones;

import java.io.IOException;
import java.text.ParseException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
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
 * @author lcortes
 * @version 1, 08/07/2016
 * @version 2, 18/04/2017 jrodriguezr Se refactoriza el codigo SQL de
 * las listas para utilizar dss.
 * @version 3, 24/04/2017 jrodriguezr Se refactoriza el codigo
 * ajustando los llamados a funciones, procedimientos y metodos de la
 * clase Acciones a llamados a EJB.
 * 
 * @author eamaya
 * @version 3.0, 12/06/2017 Cambio c�digo formulario y actualizaci�n
 * de ConnectorPool
 */
@ManagedBean
@ViewScoped

public class InftrasldospacControlador extends BeanBaseModal {
    private final String compania;
    // <DECLARAR_ATRIBUTOS>
    private String cuentaInicial;
    private String cuentaFinal;
    private String anio;
    private Date fechaInicial;
    private Date fechaFinal;
    private String nomCuentaIni;
    private String nomCuentaFin;
    private StreamedContent archivoDescarga;
    // </DECLARAR_ATRIBUTOS>
    // <DECLARAR_PARAMETROS>
    // </DECLARAR_PARAMETROS>
    // <DECLARAR_LISTAS>
    private List<Registro> listaAno;
    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>
    private RegistroDataModelImpl listaCuentaInicial;
    private RegistroDataModelImpl listaCuentaFinal;
    // </DECLARAR_LISTAS_COMBO_GRANDE>

    /**
     * Creates a new instance of InftrasldospacControlador
     */
    public InftrasldospacControlador() {
        super();
        compania = SessionUtil.getCompania();
        try {
            numFormulario = GeneralCodigoFormaEnum.INFTRASLDOSPAC_CONTROLADOR
                            .getCodigo();

            validarPermisos();
            // <INI_ADICIONAL>
            // </INI_ADICIONAL>
        }
        catch (Exception ex) {
            Logger.getLogger(InftrasldospacControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            SessionUtil.redireccionarMenuPermisos();
        }
    }

    @PostConstruct
    public void inicializar() {
        // <CARGAR_LISTA>
        anio = String.valueOf(SysmanFunciones
                        .ano(new Date()));
        cargarListaAno();
        // </CARGAR_LISTA>
        // <CARGAR_LISTA_COMBO_GRANDE>
        cargarListaCuentaInicial();

        // </CARGAR_LISTA_COMBO_GRANDE>
        abrirFormulario();
    }

    @Override
    public void abrirFormulario() {
        fechaInicial = new Date();
        fechaFinal = new Date();
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    // <METODOS_CARGAR_LISTA>
    public void cargarListaAno() {
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        try {
            listaAno = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            InftrasldospacControladorUrlEnum.URL3628
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
                                        InftrasldospacControladorUrlEnum.URL4010
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.ANO.getName(), anio);
        listaCuentaInicial = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, GeneralParameterEnum.CODIGO.getName());
    }

    public void cargarListaCuentaFinal() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        InftrasldospacControladorUrlEnum.URL4736
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.ANO.getName(), anio);
        param.put(InftrasldospacControladorEnum.CUENTAINICIAL.getValue(),
                        cuentaInicial);

        listaCuentaFinal = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, GeneralParameterEnum.CODIGO.getName());
    }

    // </METODOS_CARGAR_LISTA>
    // <METODOS_BOTONES>
    public void oprimirImprimir() {
        if (!validarVacios()) {
            return;
        }
        if (fechaInicial.after(fechaFinal)) {
            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB957"));
            return;
        }
        if (SysmanFunciones.ano(fechaInicial) != Integer.valueOf(anio)) {
            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB958"));
            return;
        }
        if (SysmanFunciones.ano(fechaFinal) != Integer.valueOf(anio)) {
            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB959"));
            return;
        }
        generarInforme(ReportesBean.FORMATOS.PDF);
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    public void oprimirCmdExcel() {
        if (!validarVacios()) {
            return;
        }
        if (fechaInicial.after(fechaFinal)) {
            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB957"));
            return;
        }
        if (SysmanFunciones.ano(fechaInicial) != Integer.valueOf(anio)) {
            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB958"));
            return;
        }
        if (SysmanFunciones.ano(fechaFinal) != Integer.valueOf(anio)) {
            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB959"));
            return;
        }
        generarInforme(ReportesBean.FORMATOS.EXCEL97);
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    public void generarInforme(ReportesBean.FORMATOS formato) {
        archivoDescarga = null;
        try {
            HashMap<String, Object> reemplazos = new HashMap<>();
            Map<String, Object> parametros = new HashMap<>();
            // Reemplazos valores consulta reporte

            reemplazos.put("cuentaInicial", cuentaInicial);
            reemplazos.put("cuentaFinal", cuentaFinal);
            reemplazos.put("fechaInicial", SysmanFunciones
                            .convertirAFechaCadena(fechaInicial));
            reemplazos.put("fechaFinal",
                            SysmanFunciones.convertirAFechaCadena(fechaFinal));
            reemplazos.put("anio", anio);

            parametros.put("PR_NOMBRECOMPANIA",
                            SessionUtil.getCompaniaIngreso().getNombre());
            parametros.put("PR_NITCOMPANIA",
                            SessionUtil.getCompaniaIngreso().getNit());
            parametros.put("PR_ANO", anio);
            parametros.put("PR_FECHAINICIAL", SysmanFunciones
                            .convertirAFechaCadena(fechaInicial));
            parametros.put("PR_FECHAFINAL",
                            SysmanFunciones.convertirAFechaCadena(fechaFinal));

            Reporteador.resuelveConsulta("000991TrasladosPac",
                            Integer.parseInt(
                                            SessionUtil.getModulo()),
                            reemplazos,
                            parametros);

            archivoDescarga = JsfUtil.exportarStreamed("000991TrasladosPac",
                            parametros, ConectorPool.ESQUEMA_SYSMAN, formato);
        }
        catch (JRException | IOException | SysmanException | ParseException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    private boolean validarVacios() {
        anio = SysmanFunciones.nvlStr(anio, "");
        cuentaInicial = SysmanFunciones.nvlStr(cuentaInicial, "");
        cuentaFinal = SysmanFunciones.nvlStr(cuentaFinal, "");
        anio = SysmanFunciones.nvlStr(anio, "");
        anio = SysmanFunciones.nvlStr(anio, "");
        anio = SysmanFunciones.nvlStr(anio, "");
        if (anio.isEmpty() || cuentaInicial.isEmpty()
            || cuentaFinal.isEmpty()) {
            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB956"));
            return false;
        }
        if ((fechaInicial == null)
            || (fechaFinal == null)) {
            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB956"));
            return false;
        }
        return true;
    }

    // </METODOS_BOTONES>
    // <METODOS_CAMBIAR>
    public void cambiarAno() {
        cuentaInicial = "";
        nomCuentaIni = "";
        cuentaFinal = "";
        nomCuentaFin = "";
        cargarListaCuentaInicial();
        cargarListaCuentaFinal();
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    // </METODOS_CAMBIAR>
    // <METODOS_COMBOS_GRANDES>
    public void seleccionarFilaCuentaInicial(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        cuentaInicial = SysmanFunciones
                        .nvl(registroAux.getCampos()
                                        .get(GeneralParameterEnum.CODIGO
                                                        .getName()),
                                        "")
                        .toString();
        nomCuentaIni = SysmanFunciones
                        .nvl(registroAux.getCampos().get("NOMBRE"), "")
                        .toString();
        cargarListaCuentaFinal();
        cuentaFinal = nomCuentaFin = null;
    }

    public void seleccionarFilaCuentaFinal(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        cuentaFinal = SysmanFunciones.nvl(
                        registroAux.getCampos().get(
                                        GeneralParameterEnum.CODIGO.getName()),
                        "")
                        .toString();
        nomCuentaFin = SysmanFunciones
                        .nvl(registroAux.getCampos().get("NOMBRE"), "")
                        .toString();
    }

    // </METODOS_COMBOS_GRANDES>

    // <SET_GET_ATRIBUTOS>
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

    public String getAnio() {
        return anio;
    }

    public void setAnio(String anio) {
        this.anio = anio;
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

    public String getNomCuentaIni() {
        return nomCuentaIni;
    }

    public void setNomCuentaIni(String nomCuentaIni) {
        this.nomCuentaIni = nomCuentaIni;
    }

    public String getNomCuentaFin() {
        return nomCuentaFin;
    }

    public void setNomCuentaFin(String nomCuentaFin) {
        this.nomCuentaFin = nomCuentaFin;
    }

    public StreamedContent getArchivoDescarga() {
        return archivoDescarga;
    }

    // </SET_GET_ATRIBUTOS>
    // <SET_GET_PARAMETROS>
    // </SET_GET_PARAMETROS>
    // <SET_GET_LISTAS>
    public List<Registro> getListaAno() {
        return listaAno;
    }

    public void setListaAno(List<Registro> listaAno) {
        this.listaAno = listaAno;
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
