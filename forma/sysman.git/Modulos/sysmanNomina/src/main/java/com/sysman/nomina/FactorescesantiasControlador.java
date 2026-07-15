package com.sysman.nomina;

import com.sysman.beanbase.BeanBaseModal;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.Constantes;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.jsfutil.ReportesBean;
import com.sysman.jsfutil.ReportesBean.FORMATOS;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.nomina.enums.FactorescesantiasControladorEnum;
import com.sysman.nomina.enums.FactorescesantiasControladorUrlEnum;
import com.sysman.persistencia.ConectorPool;
import com.sysman.recursos.ejb.EjbSysmanUtilRemote;
import com.sysman.reportes.Reporteador;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.util.SysmanFunciones;

import java.io.IOException;
import java.sql.SQLException;
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

import org.primefaces.event.SelectEvent;
import org.primefaces.model.StreamedContent;

import net.sf.dynamicreports.report.exception.DRException;
import net.sf.jasperreports.engine.JRException;

/**
 *
 * @author sdaza
 * @version 1, 29/07/2015
 *
 * @author eamaya
 * @version 2.0, 05/10/2017, Proceso de Refactoring DSS , Manejo de
 * EJBs y cambio de numero de formulario por enum
 */
@ManagedBean
@ViewScoped
public class FactorescesantiasControlador extends BeanBaseModal {
    /**
     * variable que almacena la compania
     */
    private final String compania;
    /**
     * variable que almacena el modulo
     */
    private final String modulo = SessionUtil.getModulo();
    /*
     * variable que almacena el proceso de nomina
     */
    private final String procesoNomina;
    /**
     * variable que almacena el anio de la nomina
     */
    private final String anoNomina;
    /**
     * variable que almacena el mes de la nomina
     */
    private final String mesNomina;
    /**
     * variable que almacena el periodo de nomina
     */
    private final String periodoNomina;
    /**
     * vector de informes
     */
    @SuppressWarnings("rawtypes")
    private Map[] parInformes;
    /**
     * variable que alamcena el estado del chech parciales
     */
    private boolean indParciales;
    /**
     * variable que almacena el anio
     */
    private String ano;
    /**
     * vairable que almacena el mes
     */
    private String mes;
    /**
     * variable que almacena el periodo
     */
    private String periodo;
    /**
     * variable que almacena el id del empleado
     */
    private String idEmpleado;
    /**
     * variable que almacena el formato del reporte
     */
    private String formatoRep;
    /**
     * variable que almacena el nombre del empleado
     */
    private String nombreEmpleado;
    /**
     * variable que almacena la lista de anios
     */
    private List<Registro> listaAno1;
    /**
     * variable que almacena la lista de mes
     */
    private List<Registro> listaMes1;
    /**
     * variable que almacena la lista del periodo
     */
    private List<Registro> listaPeriodo1;
    /**
     * lista los empleados
     */
    private RegistroDataModelImpl listaEmpleado;
    /**
     * variable utilizada para que almacene el archivo de descarga
     */
    private StreamedContent archivoDescarga;
    /**
     * variables estaticas un valor para posteriormente se llame una
     * vez
     */
    private static final String PR_STRSQL = "PR_STRSQL";
    private static final String PR_NOMBREEMPRESA = "PR_NOMBREEMPRESA";
    private static final String PR_CARGO_DEL_GERENTE = "PR_CARGO_DEL_GERENTE";
    private static final String PR_NOMBRE_DEL_GERENTE = "PR_NOMBRE_DEL_GERENTE";
    private static final String CPTODOCEAVASFNA = "cptoDoceavasFNA";
    private static final String CARGO_DEL_GERENTE = "CARGO DEL GERENTE";
    private static final String CARGO_JEFE_RECURSOS_HUMANOS = "CARGO JEFE RECURSOS HUMANOS";
    private static final String PR_CARGO_JEFE_RECURSOS_HUMANOS = "PR_CARGO_JEFE_RECURSOS_HUMANOS";
    private static final String PR_NOMBRE_DE_QUIEN_AUTORIZA_NOMINA = "PR_NOMBRE_DE_QUIEN_AUTORIZA_NOMINA";
    private static final String PR_CARGO_DE_QUIEN_AUTORIZA_NOMINA = "PR_CARGO_DE_QUIEN_AUTORIZA_NOMINA";

    @EJB
    private EjbSysmanUtilRemote ejbSysmanUtl;

    /**
     * Creates a new instance of FactorescesantiasControlador
     */
    public FactorescesantiasControlador() {
        super();
        compania = SessionUtil.getCompania();
        numFormulario = GeneralCodigoFormaEnum.FACTORESCESANTIAS_CONTROLADOR
                        .getCodigo();
        procesoNomina = SessionUtil.getSessionVar("procesoNomina").toString();
        anoNomina = SessionUtil.getSessionVar("anioNomina").toString();
        mesNomina = SessionUtil.getSessionVar("mesNomina").toString();
        periodoNomina = SessionUtil.getSessionVar("periodoNomina").toString();
        try {

            validarPermisos();
        }
        catch (Exception ex) {
            Logger.getLogger(FactorescesantiasControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            SessionUtil.redireccionarMenuPermisos();
        }

    }

    @PostConstruct
    public void inicializar() {

        cargarListaAno1();
        cargarListaEmpleado();
        abrirFormulario();
    }

    public void cargarListaAno1() {

        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);

        try {
            listaAno1 = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            FactorescesantiasControladorUrlEnum.URL5622
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    public void cargarListaMes1() {

        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
        param.put(FactorescesantiasControladorEnum.PROCESO.getValue(),
                        procesoNomina);
        param.put(GeneralParameterEnum.ANO.getName(),
                        ano);

        try {
            listaMes1 = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            FactorescesantiasControladorUrlEnum.URL6202
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    public void cargarListaPeriodo1() {

        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
        param.put(FactorescesantiasControladorEnum.PROCESO.getValue(),
                        procesoNomina);
        param.put(GeneralParameterEnum.ANO.getName(),
                        ano);
        param.put(GeneralParameterEnum.MES.getName(),
                        mes);

        try {
            listaPeriodo1 = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            FactorescesantiasControladorUrlEnum.URL7286
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    public void cargarListaEmpleado() {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        FactorescesantiasControladorUrlEnum.URL8072
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);

        listaEmpleado = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, "ID_DE_EMPLEADO");
    }

    public void oprimirVistaPreliminar() {

        genInforme(ReportesBean.FORMATOS.PDF);
    }

    public void oprimircmdExcel() {

        genInforme(ReportesBean.FORMATOS.EXCEL97);
    }

    private boolean mensajeInforme() {
        if (SysmanFunciones.validarVariableVacio(ano)) {
            JsfUtil.agregarMensajeInformativo(idioma.getString("TB_TB2543"));
            return true;
        }
        if (SysmanFunciones.validarVariableVacio(mes)) {
            JsfUtil.agregarMensajeInformativo(idioma.getString("TB_TB2571"));
            return true;
        }
        if (SysmanFunciones.validarVariableVacio(periodo)) {
            JsfUtil.agregarMensajeInformativo(idioma.getString("TB_TB2572"));
            return true;
        }
        return false;
    }

    @SuppressWarnings("unchecked")
    private void genInforme(FORMATOS formato) {
        archivoDescarga = null;

        if (mensajeInforme()) {
            return;
        }
        try {
            String[] listInformes = new String[6];
            setParInformes(new HashMap[6]);
            String parcialCesantias;
            HashMap<String, Object> parametros = new HashMap<>();
//            HashMap<String, Object> parametros1 = new HashMap<>();
//            HashMap<String, Object> parametros2 = new HashMap<>();
//            HashMap<String, Object> parametros3 = new HashMap<>();
            HashMap<String, Object> parametros4 = new HashMap<>();
//            HashMap<String, Object> parametros5 = new HashMap<>();
            HashMap<String, Object> reemplaza = new HashMap<>();
//            HashMap<String, Object> reemplaza1 = new HashMap<>();
//            HashMap<String, Object> reemplaza2 = new HashMap<>();
//            HashMap<String, Object> reemplaza3 = new HashMap<>();
            HashMap<String, Object> reemplaza4 = new HashMap<>();
//            HashMap<String, Object> reemplaza5 = new HashMap<>();
            String sql;
            Date fechaParametros = SysmanFunciones.ultimoDiaDate(SysmanFunciones
                            .convertirAFecha("01/" + mes + "/"
                                + ano));

            String formatoCesantiasFNA = ejbSysmanUtl.consultarParametro(
                            compania, "FORMATO FACTORES CESANTIAS FNA", modulo,
                            new Date(), false);
            
            
            if ((formatoCesantiasFNA != null) && indParciales) {

                if (validarEmpleado()) {
                    return;
                }

                parcialCesantias = ejbSysmanUtl.consultarParametro(compania,
                                "FORMATO FACTORES CESANTIAS PARCIALES",
                                modulo, fechaParametros, false);

                String pagoInteresParcial = ejbSysmanUtl.consultarParametro(
                                compania,
                                "PAGAR INTERESES PARCIALES EN NOMINA MENSUAL",
                                modulo, fechaParametros, false);

                reemplaza.put("pagoInteresParcial", pagoInteresParcial);
                reemplaza.put("proceso", procesoNomina);
                reemplaza.put("ano", ano);
                reemplaza.put("mes", mes);
                reemplaza.put("periodo", periodo);
                reemplaza.put("IdEmpleado", idEmpleado);
                sql = Reporteador.resuelveConsulta(parcialCesantias,
                                Integer.parseInt(modulo), reemplaza);
                if(sql.contains("''")) {
                	sql = sql.replaceAll("''","'");
                }
                parametros.put(PR_STRSQL, sql);
                String cargoRH = ejbSysmanUtl.consultarParametro(compania,
                                CARGO_JEFE_RECURSOS_HUMANOS, modulo,
                                fechaParametros, false);

                parametros.put(PR_CARGO_JEFE_RECURSOS_HUMANOS,
                                cargoRH);
                String jefeRH = ejbSysmanUtl.consultarParametro(compania,
                                "NOMBRE JEFE RECURSOS HUMANOS", modulo,
                                fechaParametros, false);

                parametros.put("PR_NOMBRE_JEFE_RECURSOS_HUMANOS",
                                jefeRH);
                parametros.put("PR_ELABORADO_POR",
                                SessionUtil.getUser().getCodigo());

                String cargoGerente = ejbSysmanUtl.consultarParametro(compania,
                                CARGO_DEL_GERENTE, modulo, fechaParametros,
                                false);

                parametros.put(PR_CARGO_DEL_GERENTE, cargoGerente);
                parametros.put(PR_NOMBRE_DEL_GERENTE, cargoGerente);
                String parametroCesanAuto = SysmanFunciones.nvlStr(ejbSysmanUtl.consultarParametro(
                        compania, "MIGRAR A CESANTIAS AUTO", modulo,
                        new Date(), false), "NO");
                if(parametroCesanAuto.equals("SI")) {
                	parametros.put("PR_INTERESES_CESANTIAS_MOSTRAR", parametroCesanAuto);
                }
                listInformes[0] = parcialCesantias;
                getParInformes()[0] = parametros;
                archivoDescarga = JsfUtil.exportarStreamed(parcialCesantias,
                                parametros, ConectorPool.ESQUEMA_SYSMAN,
                                formato);
                return;
            }
            else {
//                String cptoDoceavasFNA = ejbSysmanUtl.consultarParametro(
//                                compania,
//                                "CONCEPTO DOCEAVAS FNA", modulo,
//                                fechaParametros, false);
//
//                String cargoGerente = ejbSysmanUtl.consultarParametro(compania,
//                                CARGO_DEL_GERENTE, modulo, fechaParametros,
//                                false);
//
//                String nombreGerente = ejbSysmanUtl.consultarParametro(compania,
//                                "NOMBRE DEL GERENTE", modulo, fechaParametros,
//                                false);
//
//                String nombreCargoTesoreroPagador = ejbSysmanUtl
//                                .consultarParametro(compania,
//                                                "NOMBRE DEL CARGO TESORERO PAGADOR",
//                                                modulo, new Date(),
//                                                false);
//
//                String cargoTesoreroPagador = ejbSysmanUtl.consultarParametro(
//                                compania,
//                                "CARGO DEL TESORERO PAGADOR", modulo,
//                                new Date(),
//                                false);
//
//                String nombreAutNomina = ejbSysmanUtl.consultarParametro(
//                                compania, "NOMBRE DE QUIEN AUTORIZA NOMINA",
//                                modulo, fechaParametros, false);
//
//                String cargoAutNomina = ejbSysmanUtl.consultarParametro(
//                                compania, "CARGO DE QUIEN AUTORIZA NOMINA",
//                                modulo, fechaParametros, false);
//
//                // FORMATO FACTORES CESANTIAS FNA
//                // MANEJO DE PARAMETROS DEL REPORTE
//                reemplaza1.put(CPTODOCEAVASFNA, cptoDoceavasFNA);
//                reemplaza1.put("ano", ano);
//                reemplaza1.put("mes", mes);
//
//                sql = Reporteador.resuelveConsulta(formatoCesantiasFNA,
//                                Integer.parseInt(modulo), reemplaza1);
//
//                parametros1.put(PR_STRSQL, sql);
//                parametros1.put(PR_NOMBREEMPRESA, SessionUtil
//                                .getCompaniaIngreso().getNombre());
//                parametros1.put(PR_CARGO_DEL_GERENTE, cargoGerente);
//                parametros1.put(PR_NOMBRE_DEL_GERENTE, nombreGerente);
//
//                parametros1.put("PR_NOMBRE_DEL_CARGO_TESORERO_PAGADOR",
//                                nombreCargoTesoreroPagador);
//                parametros1.put("PR_CARGO_DEL_TESORERO_PAGADOR",
//                                cargoTesoreroPagador);
//
//                parametros1.put(PR_NOMBRE_DE_QUIEN_AUTORIZA_NOMINA,
//                                nombreAutNomina);
//                parametros1.put(PR_CARGO_DE_QUIEN_AUTORIZA_NOMINA,
//                                cargoAutNomina);
//
//                listInformes[1] = formatoCesantiasFNA;
//                getParInformes()[1] = parametros1;
//
//                // MANEJO DE PARAMETROS DEL REPORTE
//                reemplaza2.put(CPTODOCEAVASFNA, cptoDoceavasFNA);
//                reemplaza2.put("ano", ano);
//                reemplaza2.put("mes", mes);
//                sql = Reporteador.resuelveConsulta(
//                                "000121FACTORESCESANTIASFNASTRMES",
//                                Integer.parseInt(modulo), reemplaza2);
//                parametros2.put(PR_STRSQL, sql);
//                parametros2.put(PR_NOMBREEMPRESA, SessionUtil
//                                .getCompaniaIngreso().getNombre());
//                listInformes[2] = "000121FACTORESCESANTIASFNASTRMES";
//                getParInformes()[2] = parametros2;
//
//                // MANEJO DE PARAMETROS DEL REPORTE
//                reemplaza3.put(CPTODOCEAVASFNA, cptoDoceavasFNA);
//                reemplaza3.put("ano", ano);
//                reemplaza3.put("mes", mes);
//                sql = Reporteador.resuelveConsulta(
//                                "000149FACTORESCESANTIASFNASTRMESCVALLE",
//                                Integer.parseInt(modulo), reemplaza3);
//                parametros3.put(PR_STRSQL, sql);
//                String liquidador = ejbSysmanUtl.consultarParametro(compania,
//                                "NOMBRE DE QUIEN LIQUIDA NOMINA", modulo,
//                                fechaParametros, false);
//
//                parametros3.put("PR_NOMBRE_DE_QUIEN_LIQUIDA_NOMINA",
//                                liquidador);
//
//                parametros3.put(PR_CARGO_DE_QUIEN_AUTORIZA_NOMINA,
//                                cargoAutNomina);
//
//                String nombreRevNomina = ejbSysmanUtl.consultarParametro(
//                                compania, "NOMBRE DE QUIEN REVISA NOMINA",
//                                modulo, fechaParametros, false);
//
//                parametros3.put("PR_NOMBRE_DE_QUIEN_REVISA_NOMINA",
//                                nombreRevNomina);
//
//                parametros3.put(PR_NOMBRE_DE_QUIEN_AUTORIZA_NOMINA,
//                                nombreAutNomina);
//                String cargoLiquidador = ejbSysmanUtl.consultarParametro(
//                                compania,
//                                "CARGO DE QUIEN LIQUIDA NOMINA", modulo,
//                                fechaParametros, false);
//
//                parametros3.put("PR_CARGO_DE_QUIEN_LIQUIDA_NOMINA",
//                                cargoLiquidador);
//
//                parametros3.put(PR_CARGO_DEL_GERENTE, cargoGerente);
//
//                parametros3.put(PR_NOMBRE_DEL_GERENTE, nombreGerente);
//
//                String cargoGteAdmon = ejbSysmanUtl.consultarParametro(compania,
//                                "CARGO GERENTE ADMINISTRATIVO",
//                                modulo, fechaParametros, false);
//
//                parametros3.put("PR_CARGO_GERENTE_ADMINISTRATIVO",
//                                cargoGteAdmon);
//                String nombreGteAdmon = ejbSysmanUtl.consultarParametro(
//                                compania, "NOMBRE GERENTE ADMINISTRATIVO",
//                                modulo, fechaParametros, false);
//
//                parametros3.put("PR_NOMBRE_GERENTE_ADMINISTRATIVO",
//                                nombreGteAdmon);
//                parametros3.put(PR_NOMBREEMPRESA, SessionUtil
//                                .getCompaniaIngreso().getNombre());
//                String cargoRevisa = ejbSysmanUtl.consultarParametro(compania,
//                                "CARGO DE QUIEN REVISA NOMINA", modulo,
//                                fechaParametros, false);
//
//                parametros3.put("PR_CARGO_DE_QUIEN_REVISA_NOMINA",
//                                cargoRevisa);
//                listInformes[3] = "000149FACTORESCESANTIASFNASTRMESCVALLE";
//                getParInformes()[3] = parametros3;
//
//            }
            String formatoCesantias = ejbSysmanUtl.consultarParametro(compania,
                            "FORMATO FACTORES CESANTIAS", modulo,
                            fechaParametros, false);

            if (!("".equals(formatoCesantias))) {
                // MANEJO DE PARAMETROS DEL REPORTE
                reemplaza4.put("ano", ano);
                reemplaza4.put("mes", mes);
                reemplaza4.put("periodo", periodo);
                reemplaza4.put("idProceso", procesoNomina);
                sql = Reporteador.resuelveConsulta(formatoCesantias,
                                Integer.parseInt(modulo), reemplaza4);
                parametros4.put(PR_STRSQL, sql);
                parametros4.put(PR_NOMBREEMPRESA,
                                SessionUtil.getCompaniaIngreso().getNombre());
                String cargoGerente = ejbSysmanUtl.consultarParametro(compania,
                                CARGO_DEL_GERENTE, modulo, fechaParametros,
                                false);
                parametros4.put(PR_CARGO_DEL_GERENTE, cargoGerente);
                parametros4.put(PR_NOMBRE_DEL_GERENTE, cargoGerente);
                String cargoRH = ejbSysmanUtl.consultarParametro(compania,
                                CARGO_JEFE_RECURSOS_HUMANOS, modulo,
                                fechaParametros, false);
                parametros4.put(PR_CARGO_JEFE_RECURSOS_HUMANOS, cargoRH);
                
                String nombreDesarrolloH = ejbSysmanUtl.consultarParametro(compania,
                                "NOMBRE JEFE DESARROLLO HUMANO", modulo,
                                fechaParametros, false);
                parametros4.put("PR_NOMBRE_JEFE_DESARROLLO_HUMANO", 
                		        nombreDesarrolloH);
                
                String cargoFirmaN = ejbSysmanUtl.consultarParametro(compania,
                                 "CARGO DE QUIEN FIRMA NOMINA VB1", modulo,
                                  fechaParametros, false);
                parametros4.put("PR_CARGO_DE_QUIEN_FIRMA_NOMINA", 
        		                cargoFirmaN);
                
                String nombreJefeN = ejbSysmanUtl.consultarParametro(compania,
                        "NOMBRE JEFE NOMINA", modulo,
                         fechaParametros, false);
                 parametros4.put("PR_NOMBRE_JEFE_NOMINA", 
    		            nombreJefeN);

                parametros4.put(PR_CARGO_JEFE_RECURSOS_HUMANOS, cargoRH);
                String jefeRH = ejbSysmanUtl.consultarParametro(compania,
                                "NOMBRE JEFE RECURSOS HUMANOS", modulo,
                                fechaParametros, false);

                parametros4.put("PR_NOMBRE_JEFE_RECURSOS_HUMANOS", jefeRH);

                String quienLiquida = ejbSysmanUtl.consultarParametro(compania,
                                "NOMBRE DE QUIEN LIQUIDA NOMINA", modulo,
                                fechaParametros, false);

                parametros4.put("PR_NOMBRE_DE_QUIEN_LIQUIDA_NOMINA",
                                quienLiquida);
                String cargoLiquida = ejbSysmanUtl.consultarParametro(compania,
                                "CARGO DE QUIEN LIQUIDA NOMINA", modulo,
                                fechaParametros, false);

                parametros4.put("PR_CARGO_DE_QUIEN_LIQUIDA_NOMINA",
                                cargoLiquida);

                String nombreAutoriza = ejbSysmanUtl.consultarParametro(
                                compania, "NOMBRE DE QUIEN AUTORIZA NOMINA",
                                modulo, fechaParametros,
                                false);

                parametros4.put("PR_NOMBREDEQUIENAUTORIZA",
                                nombreAutoriza);

                String cargoAutoriza = ejbSysmanUtl.consultarParametro(compania,
                                "CARGO DE QUIEN AUTORIZA NOMINA", modulo,
                                fechaParametros, false);

                parametros4.put("PR_CARGODEQUIENAUTORIZA",
                                cargoAutoriza);

                String nombreJefeRH = ejbSysmanUtl.consultarParametro(compania,
                                "NOMBRE DEL JEFE DE RECURSOS HUMANOS", modulo,
                                fechaParametros,
                                false);

                parametros4.put("PR_NOMBREDELJEFEDERECURSOSHUMANOS",
                                nombreJefeRH);

                String cargoJefeRH = ejbSysmanUtl.consultarParametro(compania,
                                CARGO_JEFE_RECURSOS_HUMANOS, modulo,
                                fechaParametros, false);

                parametros4.put("PR_CARGOJEFERECURSOSHUMANOS",
                                cargoJefeRH);

                String nombreRevNomina = ejbSysmanUtl.consultarParametro(
                                compania, "NOMBRE DE QUIEN REVISA NOMINA",
                                modulo, fechaParametros, false);

                parametros4.put("PR_NOMBREDEQUIENREVISA",
                                nombreRevNomina);
                String cargoRevisa = ejbSysmanUtl.consultarParametro(compania,
                                "CARGO DE QUIEN REVISA NOMINA", modulo,
                                fechaParametros, false);


                parametros4.put("PR_CARGODEQUIENREVISA",
                                cargoRevisa);
                //Inicio dcastiblanco
                
                String jefeNomina = ejbSysmanUtl.consultarParametro(compania,
                                "NOMBRE JEFE NOMINA", modulo,
                                fechaParametros, false);

                parametros4.put("PR_NOMBRE_JEFE_NOMINA",
                                jefeNomina);
                String cargoResponsableNomina = ejbSysmanUtl.consultarParametro(compania,
                                "CARGO RESPONSABLE DE NOMINA", modulo,
                                fechaParametros, false);

                parametros4.put("PR_CARGO_RESPONSABLE_DE_NOMINA",
                                cargoResponsableNomina);
                String jefeDH = ejbSysmanUtl.consultarParametro(compania,
                                "NOMBRE JEFE DESARROLLO HUMANO", modulo,
                                fechaParametros, false);

                parametros4.put("PR_NOMBRE_JEFE_DESARROLLO_HUMANO",
                                jefeDH);
                String cargoJefeDesarrolloHumano = ejbSysmanUtl.consultarParametro(compania,
                                "CARGO JEFE DESARROLLO HUMANO", modulo,
                                fechaParametros, false);

                parametros4.put("PR_CARGO_JEFE_DESARROLLO_HUMANO",
                                cargoJefeDesarrolloHumano);
                //Fin dcastiblanco
                
                listInformes[4] = formatoCesantias;
                getParInformes()[4] = parametros4;
            }
//            if ("8".equals(periodo)) {
//
//                // MANEJO DE PARAMETROS DEL REPORTE
//                reemplaza5.put("ano", ano);
//                reemplaza5.put("mes", mes);
//                sql = Reporteador.resuelveConsulta(
//                                "000181FACTORESCESANTIASCORPORINOQUIAFNA",
//                                Integer.parseInt(modulo), reemplaza5);
//                parametros5.put(PR_STRSQL, sql);
//                String nombreJefeRH = ejbSysmanUtl.consultarParametro(compania,
//                                "NOMBRE DEL JEFE DE RECURSOS HUMANOS", modulo,
//                                fechaParametros,
//                                false);
//
//                parametros5.put("PR_NOMBRE_DEL_JEFE_DE_RECURSOS_HUMANOS",
//                                nombreJefeRH);
//                String cargoJefeRH = ejbSysmanUtl.consultarParametro(compania,
//                                CARGO_JEFE_RECURSOS_HUMANOS, modulo,
//                                fechaParametros, false);
//
//                parametros5.put(PR_CARGO_JEFE_RECURSOS_HUMANOS, cargoJefeRH);
//                String cargoAutoriza = ejbSysmanUtl.consultarParametro(compania,
//                                "CARGO DE QUIEN AUTORIZA NOMINA", modulo,
//                                fechaParametros, false);
//
//                parametros5.put(PR_CARGO_DE_QUIEN_AUTORIZA_NOMINA,
//                                cargoAutoriza);
//                parametros5.put(PR_NOMBREEMPRESA,
//                                SessionUtil.getCompaniaIngreso().getNombre());
//                String nombreAutoriza = ejbSysmanUtl.consultarParametro(
//                                compania, "NOMBRE DE QUIEN AUTORIZA NOMINA",
//                                modulo, fechaParametros,
//                                false);
//
//                parametros5.put(PR_NOMBRE_DE_QUIEN_AUTORIZA_NOMINA,
//                                nombreAutoriza);
//                listInformes[5] = "000181FACTORESCESANTIASCORPORINOQUIAFNA";
//                getParInformes()[5] = parametros5;
//            }

            archivoDescarga = JsfUtil
                            .exportarComprimidoReportesStreamed(
                                            listInformes,
                                            getParInformes(),
                                            ConectorPool.ESQUEMA_SYSMAN,
                                            formato);
        }
	}

        catch (JRException | IOException | SQLException | DRException
                        | SysmanException | ParseException
                        | SystemException | NullPointerException ex) {
            logger.error(ex.getMessage(), ex);
            JsfUtil.agregarMensajeError(
                            SysmanFunciones.concatenar(idioma.getString(
                                            Constantes.MSM_TRANS_INTERRUMPIDA),
                                            " - ",
                                            ex.getMessage()));
        }

    }


    private boolean validarEmpleado() {
        if ("0".equals(idEmpleado)
            || SysmanFunciones.validarVariableVacio(idEmpleado)) {
            JsfUtil.agregarMensajeInformativo(
                            idioma.getString("TB_TB2598"));
            return true;
        }
        return false;
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

    public void cambiarPARCIALES() {
        // <CODIGO_DESARROLLADO>
        if ((indParciales)
            && SysmanFunciones.validarVariableVacio(idEmpleado)) {
            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB2603"));
        }
        else if ((indParciales) && ("0".equals(idEmpleado))) {
            indParciales = false;
            formatoRep = "2";
        }
        else if (!indParciales) {
            formatoRep = "2";
        }

        // </CODIGO_DESARROLLADO>
    }

    public void cambiarFormato() {
        // <CODIGO_DESARROLLADO>
        if (indParciales) {
            formatoRep = "1";
        }
        // </CODIGO_DESARROLLADO>
    }

    public void seleccionarFilaEmpleado(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        idEmpleado = SysmanFunciones
                        .nvl(registroAux.getCampos().get("ID_DE_EMPLEADO"), "")
                        .toString();
        nombreEmpleado = SysmanFunciones
                        .nvl(registroAux.getCampos().get("NOMBRECOMPLETO"), "")
                        .toString();
        if ("0".equals(idEmpleado)) {
            indParciales = false;
        }
        else {
            indParciales = true;
        }
        formatoRep = "1";
    }

    public boolean getIndParciales() {
        return indParciales;
    }

    public void setIndParciales(boolean indParciales) {
        this.indParciales = indParciales;
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

    public String getIdEmpleado() {
        return idEmpleado;
    }

    public void setIdEmpleado(String idEmpleado) {
        this.idEmpleado = idEmpleado;
    }

    public String getFormatoRep() {
        return formatoRep;
    }

    public void setFormatoRep(String formatoRep) {
        this.formatoRep = formatoRep;
    }

    public String getNombreEmpleado() {
        return nombreEmpleado;
    }

    public void setNombreEmpleado(String nombreEmpleado) {
        this.nombreEmpleado = nombreEmpleado;
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

    public RegistroDataModelImpl getListaEmpleado() {
        return listaEmpleado;
    }

    public void setListaEmpleado(RegistroDataModelImpl listaEmpleado) {
        this.listaEmpleado = listaEmpleado;
    }

    @Override
    public void abrirFormulario() {
        // <CODIGO_DESARROLLADO>
        formatoRep = "1";
        ano = anoNomina;
        cargarListaMes1();
        mes = mesNomina;
        cargarListaPeriodo1();
        periodo = periodoNomina;
        // </CODIGO_DESARROLLADO>
    }

    public StreamedContent getArchivoDescarga() {
        return archivoDescarga;
    }

    public void setArchivoDescarga(StreamedContent archivoDescarga) {
        this.archivoDescarga = archivoDescarga;
    }

    @SuppressWarnings("rawtypes")
    public Map[] getParInformes() {
        return parInformes;
    }

    @SuppressWarnings("rawtypes")
    public void setParInformes(Map[] parInformes) {
        this.parInformes = parInformes;
    }

}
