package com.sysman.nomina;

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
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.nomina.enums.FactoresPrimaJunControladorUrlEnum;
import com.sysman.persistencia.ConectorPool;
import com.sysman.recursos.ejb.EjbSysmanUtilRemote;
import com.sysman.reportes.Reporteador;
import com.sysman.sesion.SessionBean;
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
 * @author sdaza
 * @version 1, 31/08/2015
 * 
 * @version 2, 05/10/2017
 * @author jreina se realizaron los cambios de refactoring en cada uno
 * de los combos.
 * 
 * @author fperez
 * @version 3, 12/04/2018. Se cambiï¿½ el anterior reporte,
 * 000206PlanillaPrimaJunioACACIAS, por el
 * 001760PlanillaPrimaJunioANE.
 * 
 * @author bcardenas
 * @version 4, 18/06/2018. Se realiza metodo para generar reporte,
 * 001800PLANILLAPRIMAJUNIOIDSN
 * 
 */

@ManagedBean
@ViewScoped
public class FactoresPrimaJunControlador extends BeanBaseModal {
    /**
     * variable que almacena la compania
     */
    private final String compania;
    /**
     * variable que almacena el modulo
     */
    private final String modulo;
    /**
     * variable que almacena el proceso de la nomina
     */
    private final String procesoNomina;
    /***
     * variable que almacena el anio de la nomina
     */
    private final String anoNomina;
    /**
     * variable que almacena el mes de la nomina
     */
    private final String mesNomina;
    /**
     * variable que almacena el periodo de la nomina
     */
    private final String periodoNomina;
    /***
     * variable que almacena los registros
     */
    private Registro registro;
    /**
     * variable que almacena el origen de datos
     */
    private String origenDatos;
    /**
     * variable que almacena la lista de anios
     */
    private List<Registro> listaAno1;
    /**
     * variable que almacena la lista de mes
     */
    private List<Registro> listaMes1;
    /**
     * variable que almacena la lista de periodos
     */
    private List<Registro> listaPeriodo1;
    /**
     * variable que almacena el anio
     */
    private String ano;
    /**
     * variable que almacena el mes
     */
    private String mes;
    /**
     * variable que almacena el periodo
     */
    private String periodo;
    /**
     * variable que almacena la obserbacioes
     */
    private String observacion;

    // private String planillaGOBCAQUETA;
    // /**
    // * variable que almacena la planillaAne
    // */
    // private String planillaAne;

    /**
     * variable de descarga
     */
    private StreamedContent archivoDescarga;

    @EJB
    private EjbSysmanUtilRemote ejbSysmanUtil;

    /**
     * Creates a new instance of FactoresliqfinalControlador
     */
    public FactoresPrimaJunControlador() {
        super();
        compania = SessionUtil.getCompania();
        modulo = SessionUtil.getModulo();
        procesoNomina = (String) SessionUtil.getSessionVar("procesoNomina");
        anoNomina = (String) SessionUtil.getSessionVar("anioNomina");
        mesNomina = (String) SessionUtil.getSessionVar("mesNomina");
        periodoNomina = (String) SessionUtil.getSessionVar("periodoNomina");
        // planillaAne = "001760PlanillaPrimaJunioANE";
        // planillaGOBCAQUETA = "001822PlanillaPrimaJunioGOBCAQUETA";
        try {
            numFormulario = GeneralCodigoFormaEnum.FACTORES_PRIMA_JUN_CONTROLADOR
                            .getCodigo();
            validarPermisos();
        }
        catch (Exception ex) {
            Logger.getLogger(FactoresPrimaJunControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            SessionUtil.redireccionarMenuPermisos();
        }

    }

    @PostConstruct
    public void inicializar() {
        origenDatos = "";
        cargarListaAno1();
        cargarListaMes1();
        cargarListaPeriodo1();
        abrirFormulario();
    }

    public void cargarListaAno1() {
        try {
            Map<String, Object> param = new TreeMap<>();
            param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
            param.put(GeneralParameterEnum.ID_DE_PROCESO.getName(),
                            procesoNomina);

            listaAno1 = RegistroConverter
                            .toListRegistro(
                                            requestManager.getList(
                                                            UrlServiceUtil.getInstance()
                                                                            .getUrlServiceByUrlByEnumID(
                                                                                            FactoresPrimaJunControladorUrlEnum.URL4149
                                                                                                            .getValue())
                                                                            .getUrl(),
                                                            param));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    public void cargarListaMes1() {
        try {
            Map<String, Object> param = new TreeMap<>();
            param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
            param.put(GeneralParameterEnum.ID_DE_PROCESO.getName(),
                            procesoNomina);
            param.put(GeneralParameterEnum.ANO.getName(), ano);

            listaMes1 = RegistroConverter
                            .toListRegistro(
                                            requestManager.getList(
                                                            UrlServiceUtil.getInstance()
                                                                            .getUrlServiceByUrlByEnumID(
                                                                                            FactoresPrimaJunControladorUrlEnum.URL4850
                                                                                                            .getValue())
                                                                            .getUrl(),
                                                            param));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    public void cargarListaPeriodo1() {
        try {
            Map<String, Object> param = new TreeMap<>();
            param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
            param.put(GeneralParameterEnum.ID_DE_PROCESO.getName(),
                            procesoNomina);
            param.put(GeneralParameterEnum.ANO.getName(), ano);
            param.put(GeneralParameterEnum.MES.getName(), mes);

            listaPeriodo1 = RegistroConverter
                            .toListRegistro(
                                            requestManager.getList(
                                                            UrlServiceUtil.getInstance()
                                                                            .getUrlServiceByUrlByEnumID(
                                                                                            FactoresPrimaJunControladorUrlEnum.URL6147
                                                                                                            .getValue())
                                                                            .getUrl(),
                                                            param));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    public void oprimirPreliminarBancos() {
        // <CODIGO_DESARROLLADO>
        archivoDescarga = null;
        genInforme(ReportesBean.FORMATOS.PDF);
        // </CODIGO_DESARROLLADO>
    }

    public void oprimircmdExcel() {
        // <CODIGO_DESARROLLADO>
        archivoDescarga = null;
        genInforme(ReportesBean.FORMATOS.EXCEL97);
        // </CODIGO_DESARROLLADO>
    }

    // Se crea metodo para generar reporte para IDSN y ANE

    private void genInforme(ReportesBean.FORMATOS formato) {

        try {

            HashMap<String, Object> reemplaza = new HashMap<>();
            Map<String, Object> parametros = new HashMap<>();
            reemplaza.put("idProceso", procesoNomina);
            reemplaza.put("ano", ano);
            reemplaza.put("mes", mes);
            reemplaza.put("periodo", periodo);

            String formatoI = ejbSysmanUtil.consultarParametro(
                            compania,
                            "FORMATO FACTORES PRIMA JUNIO",
                            SessionUtil.getModulo(),
                            new Date(), false);

            if (!formatoI.equals("001833PLANILLAPRIMAJUNIOGOBCAQUETA")) {
                cargarParametro(parametros);
                String sql = Reporteador.resuelveConsulta(formatoI,
                                Integer.parseInt(modulo), reemplaza);

                parametros.put("PR_STRSQL", sql);

                archivoDescarga = JsfUtil.exportarStreamed(formatoI,
                                parametros, ConectorPool.ESQUEMA_SYSMAN,
                                formato);
            }
            else if (("001833PLANILLAPRIMAJUNIOGOBCAQUETA".equals(formatoI)
                && periodo.equals("4"))
                && (mes.equalsIgnoreCase("6")
                    || mes.equalsIgnoreCase("7"))) {
                cargarParametro(parametros);
                String sql = Reporteador.resuelveConsulta(formatoI,
                                Integer.parseInt(modulo), reemplaza);

                parametros.put("PR_STRSQL", sql);

                archivoDescarga = JsfUtil.exportarStreamed(formatoI,
                                parametros, ConectorPool.ESQUEMA_SYSMAN,
                                formato);
            }
            else {
                JsfUtil.agregarMensajeError(idioma.getString("TB_TB4255"));
            }
        }

        catch (SystemException | JRException | IOException ex) {
            logger.error(ex.getMessage(), ex);
            JsfUtil.agregarMensajeError(
                            idioma.getString("MSM_TRANS_INTERRUMPIDA")
                                + ex.getMessage());
        }
        catch (SysmanException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    private void cargarParametro(Map<String, Object> parametros) {
        try {
            Date fechaParametros = SysmanFunciones.ultimoDiaDate(SysmanFunciones
                            .convertirAFecha("01/" + mes + "/"
                                + ano));
            parametros.put("PR_ANO", ano);

            String elaboradoPor = consultarParameto("ELABORADO POR",
                            fechaParametros);
            parametros.put("PR_ELABORADO_POR", elaboradoPor);

            String nombreRevisor = ejbSysmanUtil.consultarParametro(
                            compania, "NOMBRE DE QUIEN REVISA NOMINA",
                            modulo, new Date(), true);
            parametros.put("PR_NOMBRE_DE_QUIEN_REVISA_NOMINA",
                            nombreRevisor);

            String cargoRevisor = consultarParameto(
                            "CARGO DE QUIEN REVISA NOMINA",
                            fechaParametros);
            parametros.put("PR_CARGO_DE_QUIEN_REVISA_NOMINA",
                            cargoRevisor);

            String nombreJefeRH = consultarParameto(
                            "NOMBRE JEFE RECURSOS HUMANOS",
                            fechaParametros);
            parametros.put("PR_NOMBRE_JEFE_RECURSOS_HUMANOS",
                            nombreJefeRH);

            String cargoJefeRH = consultarParameto(
                            "CARGO JEFE RECURSOS HUMANOS", fechaParametros);
            parametros.put("PR_CARGO_JEFE_RECURSOS_HUMANOS",
                            cargoJefeRH);

            String nombreAutoriza = consultarParameto(
                            "NOMBRE DE QUIEN AUTORIZA NOMINA",
                            fechaParametros);
            parametros.put("PR_NOMBRE_DE_QUIEN_AUTORIZA_NOMINA",
                            nombreAutoriza);

            String cargoAutoriza = consultarParameto(
                            "CARGO DE QUIEN AUTORIZA NOMINA",
                            fechaParametros);
            parametros.put("PR_CARGO_DE_QUIEN_AUTORIZA_NOMINA",
                            cargoAutoriza);
            parametros.put("PR_NOMBREEMPRESA",
                            SessionUtil.getCompaniaIngreso().getNombre());

            String nombreGerente = ejbSysmanUtil.consultarParametro(
                            compania,
                            "NOMBRE DEL GERENTE", modulo, new Date(), true);
            parametros.put("PR_NOMBRE_DEL_GERENTE", nombreGerente);

            String cargoGerente = consultarParameto("CARGO DEL GERENTE",
                            fechaParametros);
            parametros.put("PR_CARGO_DEL_GERENTE", cargoGerente);

            String cargoTesorero = consultarParameto(
                            "CARGO DEL TESORERO PAGADOR",
                            fechaParametros);
            parametros.put("PR_CARGO_DEL_TESORERO_PAGADOR",
                            cargoTesorero);

            String nombreTesorero = ejbSysmanUtil.consultarParametro(
                            compania,
                            "NOMBRE DEL CARGO TESORERO PAGADOR", modulo,
                            new Date(), true);
            parametros.put("PR_NOMBRE_DEL_CARGO_TESORERO_PAGADOR",
                            nombreTesorero);

            String nombrePresupuesto = ejbSysmanUtil.consultarParametro(
                            compania,
                            "NOMBRE DE JEFE DE PRESUPUESTO", modulo,
                            new Date(), true);
            parametros.put("PR_NOMBRE_DE_JEFE_DE_PRESUPUESTO",
                            nombrePresupuesto);

            String cargoPresupuesto = consultarParameto(
                            "CARGO DEL JEFE DE PRESUPUESTO",
                            fechaParametros);
            parametros.put("PR_CARGO_DEL_JEFE_DE_PRESUPUESTO",
                            cargoPresupuesto);
            /*
             * Se crean parametros para el reporte
             * 002074PlanillaPrimaJunioSTR segun TAR 1000092305
             */
            String nombreElabora = ejbSysmanUtil.consultarParametro(
                            compania,
                            "NOMBRE DE QUIEN LIQUIDA NOMINA", modulo,
                            new Date(), true);
            parametros.put("PR_NOMBRE_DE_QUIEN_LIQUIDA_NOMINA",
                            nombreElabora);

            String cargoeElabora = consultarParameto(
                            "CARGO DE QUIEN LIQUIDA NOMINA",
                            fechaParametros);
            parametros.put("PR_CARGO_DE_QUIEN_LIQUIDA_NOMINA",
                            cargoeElabora);

            String nombreAnalista = ejbSysmanUtil.consultarParametro(
                            compania,
                            "NOMBRE ANALISTA DE NOMINAS", modulo,
                            new Date(), true);
            parametros.put("PR_NOMBRE_ANALISTA_DE_NOMINAS",
                            nombreAnalista);

            String cargoAnaliza = consultarParameto(
                            "CARGO ANALISTA DE NOMINAS",
                            fechaParametros);
            parametros.put("PR_CARGO_ANALISTA_DE_NOMINAS",
                            cargoAnaliza);
            
            // IMPLEMETACION PARAMETROS MARCA_BLANCA - ljdiaz (Luis Jacobo Diaz muñoz)
            parametros.put("PR_EMPRESAPARAMETRIZADA", SessionBean.getImpresoPorEmpresaParamterizada());
            // FIN IMPLEMENTACION MARCA_BLANCA
        }
        catch (NumberFormatException | SystemException | ParseException e) {
            JsfUtil.agregarMensajeError(e.getMessage());
            logger.error(e.getMessage(), e);

        }
    }

    public String consultarParameto(String nombre, Date fecha)
                    throws SystemException {
        return ejbSysmanUtil.consultarParametro(compania, nombre, modulo, fecha,
                        false);
    }

    public void cambiarAno1() {
        // <CODIGO_DESARROLLADO>
        mes = null;
        periodo = null;
        cargarListaMes1();
        cargarListaPeriodo1();
        // </CODIGO_DESARROLLADO>
    }

    public void cambiarMes1() {
        // <CODIGO_DESARROLLADO>
        periodo = null;
        cargarListaPeriodo1();
        // </CODIGO_DESARROLLADO>
    }

    @Override
    public void abrirFormulario() {
        ano = anoNomina;
        mes = mesNomina;
        periodo = periodoNomina;
        cargarListaMes1();
        cargarListaPeriodo1();
    }

    public Registro getRegistro() {
        return registro;
    }

    public void setRegistro(Registro registro) {
        this.registro = registro;
    }

    public List<Registro> getListaAno1() {
        return listaAno1;
    }

    public void setListaAno1(List<Registro> listaAno1) {
        this.listaAno1 = listaAno1;
    }

    public List<Registro> getListaMes1() {
        return listaMes1;
    }

    public void setListaMes1(List<Registro> listaMes1) {
        this.listaMes1 = listaMes1;
    }

    public List<Registro> getListaPeriodo1() {
        return listaPeriodo1;
    }

    public void setListaPeriodo1(List<Registro> listaPeriodo1) {
        this.listaPeriodo1 = listaPeriodo1;
    }

    public String getAno() {
        return ano;
    }

    public void setAno(String ano) {
        this.ano = ano;
    }

    public String getMes() {
        return mes;
    }

    public void setMes(String mes) {
        this.mes = mes;
    }

    public String getPeriodo() {
        return periodo;
    }

    public void setPeriodo(String periodo) {
        this.periodo = periodo;
    }

    public String getObservacion() {
        return observacion;
    }

    public void setObservacion(String observacion) {
        this.observacion = observacion;
    }

    public void cargarRegistro(String condicion) {
        registro = service.getRegistro(ConectorPool.ESQUEMA_SYSMAN,
                        origenDatos + condicion);
    }

    public StreamedContent getArchivoDescarga() {
        return archivoDescarga;
    }

    public void setArchivoDescarga(StreamedContent archivoDescarga) {
        this.archivoDescarga = archivoDescarga;
    }

}
