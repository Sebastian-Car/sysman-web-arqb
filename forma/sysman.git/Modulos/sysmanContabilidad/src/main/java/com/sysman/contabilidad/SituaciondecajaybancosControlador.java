package com.sysman.contabilidad;

import com.sysman.beanbase.BeanBaseModal;
import com.sysman.contabilidad.enums.SituaciondecajaybancosControladorUrlEnum;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.jsfutil.ReportesBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.persistencia.ConectorPool;
import com.sysman.recursos.ejb.EjbSysmanUtilRemote;
import com.sysman.reportes.Reporteador;
import com.sysman.util.SysmanConstantes;
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
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.model.StreamedContent;

import net.sf.jasperreports.engine.JRException;

/**
 *
 * @author lcortes
 * @version 1, 17/05/2016
 * 
 * @author eamaya
 * @version 2, 12/04/2017 Proceso de Refactoring y Correciones
 * SonarLint
 * 
 * @author jlramirez
 * @version 3, 20/04/2017, Manejo de EJBs
 * 
 * @version 4.0, 12/06/2017, <strong>pespitia</strong>:<br>
 * Reemplazar numero del formulario por enumerado.<br>
 * Reemplazar los llamados a ConectorPool por el esquema
 * ConectorPool.ESQUEMA_SYSMAN.
 */
@ManagedBean
@ViewScoped
public class SituaciondecajaybancosControlador extends BeanBaseModal {
    private final String compania;

    // <DECLARAR_ATRIBUTOS>
    private String informe;
    private String mes;
    private String anio;
    private StreamedContent archivoDescarga;
    @EJB
    private EjbSysmanUtilRemote sysmanUtil;
    // </DECLARAR_ATRIBUTOS>
    // <DECLARAR_PARAMETROS>
    // </DECLARAR_PARAMETROS>
    // <DECLARAR_LISTAS>
    private List<Registro> listaAno;
    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>
    // </DECLARAR_LISTAS_COMBO_GRANDE>

    /**
     * Creates a new instance of SituaciondecajaybancosControlador
     */
    public SituaciondecajaybancosControlador() {
        super();
        compania = SessionUtil.getCompania();
        try {
            // 715
            numFormulario = GeneralCodigoFormaEnum.SITUACIONDECAJAYBANCOS_CONTROLADOR
                            .getCodigo();

            validarPermisos();
            // <INI_ADICIONAL>
            // </INI_ADICIONAL>
        }
        catch (Exception ex) {
            Logger.getLogger(SituaciondecajaybancosControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            SessionUtil.redireccionarMenuPermisos();
        }
    }

    @PostConstruct
    public void init() {
        // <CARGAR_LISTA>
        cargarListaAno();
        // </CARGAR_LISTA>
        // <CARGAR_LISTA_COMBO_GRANDE>
        // </CARGAR_LISTA_COMBO_GRANDE>
        abrirFormulario();
    }

    @Override
    public void abrirFormulario() {
        anio = String.valueOf(SysmanFunciones.ano(new Date()));
        mes = String.valueOf(SysmanFunciones.mes(new Date()));
        // <CODIGO_DESARROLLADO>
        /*
         * FR715-AL_ABRIR Private Sub Form_Open(Cancel As Integer)
         * DoCmd.Restore End Sub
         */
        // </CODIGO_DESARROLLADO>
    }

    // <METODOS_CARGAR_LISTA>
    public void cargarListaAno() {
        try {

            Map<String, Object> param = new TreeMap<>();
            param.put(GeneralParameterEnum.COMPANIA.getName(),
                            compania);

            listaAno = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            SituaciondecajaybancosControladorUrlEnum.URL3382
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    // </METODOS_CARGAR_LISTA>
    // <METODOS_BOTONES>
    public void oprimirCancelar() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    public boolean validarVacios() {
        if (SysmanFunciones.validarVariableVacio(anio)
            || SysmanFunciones.validarVariableVacio(mes)
            || SysmanFunciones.validarVariableVacio(informe)) {
            JsfUtil.agregarMensajeAlerta(
                            idioma.getString("TB_TB122"));
            return true;
        }

        return false;
    }

    public void oprimirImprimir() {
        // <CODIGO_DESARROLLADO>
        archivoDescarga = null;
        if (validarVacios()) {
            return;
        }
        generarInforme(ReportesBean.FORMATOS.PDF);
        // </CODIGO_DESARROLLADO>
    }

    public void oprimirExcel() {
        // <CODIGO_DESARROLLADO>
        archivoDescarga = null;
        if (validarVacios()) {
            return;
        }
        generarInforme(ReportesBean.FORMATOS.EXCEL97);
        // </CODIGO_DESARROLLADO>
    }

    public void generarInforme(ReportesBean.FORMATOS formato) {
        try {
            HashMap<String, Object> reemplazos = new HashMap<>();
            Map<String, Object> parametros = new HashMap<>();
            String condicion;
            String tituloInforme = "";
            String rubroResponsabilidades = SysmanFunciones
                            .nvl(sysmanUtil.consultarParametro(compania,
                                            "RUBRO DE DISPONIBILIADES",
                                            SessionUtil.getModulo(), new Date(),
                                            true), " ")
                            .toString();
            rubroResponsabilidades = rubroResponsabilidades == null ? "N.A"
                : rubroResponsabilidades;
            String manejaFirmas = sysmanUtil.consultarParametro(compania,
                            "MANEJA FIRMAS ESPECIALES EN SITUACION CAJA Y B",
                            SessionUtil.getModulo(), new Date(), true);
            manejaFirmas = manejaFirmas == null ? "NO" : manejaFirmas;
            if ("RN".equals(informe)) {
                condicion = " AND PLAN_CONTABLE.ID = '" + rubroResponsabilidades
                    + "' ";
                tituloInforme = idioma.getString("OD_CB2490_0");
            }
            else if ("P".equals(informe)) {
                condicion = " AND PLAN_CONTABLE.ID NOT IN ('"
                    + rubroResponsabilidades + "') ";
                tituloInforme = idioma.getString("OD_CB2490_1");
            }
            else {
                condicion = "";
            }

            // Reemplazos valores consulta reporte
            reemplazos.put("mes", mes);
            reemplazos.put("mesInicial", Integer.parseInt(mes) + 1);
            reemplazos.put("anio", anio);
            reemplazos.put("condicion", condicion);
            reemplazos.put("informe", informe);
            // Inicio Par�metros Informe
            parametros.put("PR_MES",
                            SysmanConstantes.NOMBRE_MESES_CONTABILIDAD[Integer
                                            .parseInt(mes)]);
            parametros.put("PR_CIUDADCOMPANIA",
                            SessionUtil.getCompaniaIngreso().getCiudad() + ", "
                                + SysmanFunciones.convertirAFechaCadena(
                                                new Date(),
                                                "dd 'de' MMMMM 'de' YYYY"));
            parametros.put("PR_ANO", anio);
            parametros.put("PR_NOMBRECOMPANIA",
                            SessionUtil.getCompaniaIngreso().getNombre());
            parametros.put("PR_INFORME", tituloInforme);
            if ("SI".equals(manejaFirmas)) {
                parametros.put("PR_CARGO", SysmanFunciones
                                .nvl(sysmanUtil.consultarParametro(compania,
                                                "CARGO1 EN SITUACION CAJA Y BANCOS",
                                                SessionUtil.getModulo(),
                                                new Date(), true), " ")
                                .toString());
                parametros.put("PR_NOMBRE1", SysmanFunciones
                                .nvl(sysmanUtil.consultarParametro(compania,
                                                "NOMBRE1 EN SITUACION CAJA Y BANCOS",
                                                SessionUtil.getModulo(),
                                                new Date(), true), " ")
                                .toString());
                parametros.put("PR_CARGO2", SysmanFunciones
                                .nvl(sysmanUtil.consultarParametro(compania,
                                                "CARGO2 EN SITUACION CAJA Y BANCOS",
                                                SessionUtil.getModulo(),
                                                new Date(), true), " "))
                                .toString();
                parametros.put("PR_NOMBRE2", SysmanFunciones
                                .nvl(sysmanUtil.consultarParametro(compania,
                                                "NOMBRE2 EN SITUACION CAJA Y BANCOS",
                                                SessionUtil.getModulo(),
                                                new Date(), true), " ")
                                .toString());
            }
            else {
                parametros.put("PR_CARGO", SysmanFunciones
                                .nvl(sysmanUtil.consultarParametro(compania,
                                                "CARGO PRESUPUESTO",
                                                SessionUtil.getModulo(),
                                                new Date(), true), " ")
                                .toString());
                parametros.put("PR_NOMBRE1", SysmanFunciones
                                .nvl(sysmanUtil.consultarParametro(compania,
                                                "NOMBRE DE JEFE DE PRESUPUESTO",
                                                SessionUtil.getModulo(),
                                                new Date(), true), " ")
                                .toString());
                parametros.put("PR_CARGO2", SysmanFunciones
                                .nvl(sysmanUtil.consultarParametro(compania,
                                                "CARGO TESORERO",
                                                SessionUtil.getModulo(),
                                                new Date(), true), " ")
                                .toString());
                parametros.put("PR_NOMBRE2", SysmanFunciones
                                .nvl(sysmanUtil.consultarParametro(compania,
                                                "NOMBRE TESORERO",
                                                SessionUtil.getModulo(),
                                                new Date(), true), " ")
                                .toString());
            }

            Reporteador.resuelveConsulta("000782SituacionDeCajaYbanco",
                            Integer.parseInt(
                                            SessionUtil.getModulo()),
                            reemplazos,
                            parametros);

            archivoDescarga = JsfUtil.exportarStreamed(
                            "000782SituacionDeCajaYbanco", parametros,
                            ConectorPool.ESQUEMA_SYSMAN, formato);
        }
        catch (JRException | IOException | SysmanException | SystemException
                        | ParseException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    // </METODOS_BOTONES>
    // <METODOS_CAMBIAR>
    // </METODOS_CAMBIAR>
    // <METODOS_COMBOS_GRANDES>
    // </METODOS_COMBOS_GRANDES>

    // <SET_GET_ATRIBUTOS>
    public String getInforme() {
        return informe;
    }

    public void setInforme(String informe) {
        this.informe = informe;
    }

    public String getMes() {
        return mes;
    }

    public void setMes(String mes) {
        this.mes = mes;
    }

    public String getAnio() {
        return anio;
    }

    public void setAnio(String anio) {
        this.anio = anio;
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
    // </SET_GET_LISTAS_COMBO_GRANDE>
}
