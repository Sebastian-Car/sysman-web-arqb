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
import com.sysman.jsfutil.ReportesBean.FORMATOS;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.nomina.ejb.EjbNominaSeisRemote;
import com.sysman.nomina.ejb.EjbNominaUnoRemote;
import com.sysman.nomina.enums.ListadosRtefteControladorEnum;
import com.sysman.nomina.enums.ListadosRtefteControladorUrlEnum;
import com.sysman.persistencia.ConectorPool;
import com.sysman.recursos.ejb.EjbSysmanUtilRemote;
import com.sysman.reportes.Reporteador;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.sesion.SessionBean;
import com.sysman.util.SysmanConstantes;
import com.sysman.util.SysmanFunciones;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
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
 * @author dsuesca
 * @version 1, 24/07/2015
 *
 * Revision Sonar
 * @author ybecerra
 * @version 2, 16/03/2017
 *
 * Se eliminaron parametros de ActionEvent ac llamados en los metodos de los botones
 * @author ybecerra
 * @version 3, 23/03/2017
 *
 *
 * -- Modificado por ybecerra 23/03/2017
 * 
 * @author eamaya
 * @version 3.1, Proceso de Refactoring DSS,Manejo de EJBs,correcciones SonarLint y cambio de numero de formulario por enum
 * 
 */
@ManagedBean
@ViewScoped

public class ListadosRtefteControlador extends BeanBaseModal {
    /**
     * Constante a nivel de clase que almacena el codigo de la compania en la cual inicio sesion el usuario, el valor de esta constante es asignado en el constructor a la variable de sesion
     * correspondiente
     */
    private final String compania;

    /**
     * Constante que almacena el nombre de usuario que inicio sesion
     */
    private final String usuario;

    /**
     * Constante que almacena el numero de modulo
     */
    private final String modulo;
    /**
     * Constante definida para almacenar la cadena "MSM_TRANS_INTERRUMPIDA"
     */
    private final String cInterrumpida;
    /**
     * Constante definida para almacenar la cadena "PR_NOMBREEMPRESA"
     */
    private final String cNombreEmpresa;

    /**
     * Constante definida para almacenar la cadena "periodo1"
     */
    private final String cPeriodoUno;
    /**
     * Constante definida para almacenar la cadena "proceso"
     */
    private final String cProceso;

    private final String procesoSesion;
    private final String anioSesion;
    private final String mesSesion;
    private final String periodoSesion;
    private final String nombreCompania;
    // <DECLARAR_ATRIBUTOS>
    private boolean todos;
    private boolean todos1;
    private String ano1;
    private String mes1;
    private String periodo1;
    private String proceso;
    private String empleado;
    private String idEmpleado;
    private String codEmpleado;
    private String formatoD;
    private String formatoIDSN;
    private boolean cuadreSaldosVisible;
    private boolean porPeriodo;
    // </DECLARAR_ATRIBUTOS>
    // <DECLARAR_LISTAS>
    private List<Registro> listaAno1;
    private List<Registro> listaMes1;
    private List<Registro> listaPeriodo1;
    private List<Registro> listaProceso;
    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>
    private RegistroDataModelImpl listaEmpleado;
    // </DECLARAR_LISTAS_COMBO_GRANDE>
    private StreamedContent archivoDescarga;

    @EJB
    private EjbSysmanUtilRemote ejbSysmanUtil;

    @EJB
    private EjbNominaUnoRemote ejbNominaUno;
    
    @EJB
    private EjbNominaSeisRemote ejbNominaSeis;

    /**
     * Creates a new instance of ListadosRtefteControlador
     */
    public ListadosRtefteControlador() {
        super();
        numFormulario = GeneralCodigoFormaEnum.LISTADOS_RTEFTE_CONTROLADOR
                        .getCodigo();
        compania = SessionUtil.getCompania();
        usuario = SessionUtil.getUser().getCodigo();
        modulo = SessionUtil.getModulo();
        procesoSesion = (String) SessionUtil.getSessionVar("procesoNomina");
        anioSesion = (String) SessionUtil.getSessionVar("anioNomina");
        mesSesion = (String) SessionUtil.getSessionVar("mesNomina");
        periodoSesion = (String) SessionUtil.getSessionVar("periodoNomina");
        nombreCompania = SessionUtil.getCompaniaIngreso().getNombre();
        proceso = procesoSesion;
        ano1 = anioSesion;
        mes1 = mesSesion;
        periodo1 = periodoSesion;
        cInterrumpida = "MSM_TRANS_INTERRUMPIDA";
        cNombreEmpresa = "PR_NOMBREEMPRESA";
        cPeriodoUno = "periodo1";
        cProceso = "proceso";
        formatoIDSN = "001829ListadoRteFte_IDSN";
        try {
            validarPermisos();
        }
        catch (Exception ex) {
            SessionUtil.redireccionarMenuPermisos();
            Logger.getLogger(ListadosRtefteControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
        }

    }

    @PostConstruct
    public void inicializar() {
        cargarListaAno1();
        cargarListaMes1();
        cargarListaPeriodo1();
        cargarListaProceso();
        cargarListaEmpleado();
        abrirFormulario();
        
        if(ano1.equals("2023")) {
			cuadreSaldosVisible = true;
		} else {
			cuadreSaldosVisible = false;
		}
    }

    public void cargarListaAno1() {

        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);

        try {
            listaAno1 = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            ListadosRtefteControladorUrlEnum.URL5407
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
        param.put(GeneralParameterEnum.ANO.getName(),
                        ano1);

        try {
            listaMes1 = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            ListadosRtefteControladorUrlEnum.URL5840
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
        param.put(GeneralParameterEnum.ANO.getName(),
                        ano1);

        param.put(GeneralParameterEnum.MES.getName(), mes1);

        param.put(ListadosRtefteControladorEnum.PROCESO.getValue(), proceso);

        try {
            listaPeriodo1 = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            ListadosRtefteControladorUrlEnum.URL6969
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    public void cargarListaProceso() {

        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);

        try {
            listaProceso = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            ListadosRtefteControladorUrlEnum.URL7449
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
                                        ListadosRtefteControladorUrlEnum.URL8146
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
        param.put(ListadosRtefteControladorEnum.PROCESO.getValue(),
                        proceso);
        param.put(GeneralParameterEnum.ANO.getName(),
                        ano1);
        param.put(GeneralParameterEnum.MES.getName(),
                        mes1);
        param.put(GeneralParameterEnum.PERIODO.getName(),
                        periodo1);

        listaEmpleado = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, "NUMERO_DCTO");
    }
    
    public void oprimirCuadreSaldos() {
    	try {
    		if(codEmpleado == null || codEmpleado.isEmpty()) {
    			codEmpleado = "0";
    		}
    		
			ejbNominaSeis.revisaLey2277_2022_2(compania, Integer.parseInt(proceso), 
											   Integer.parseInt(ano1), 
											   Integer.parseInt(mes1), 
											   Integer.parseInt(periodo1), 
											   Integer.parseInt(codEmpleado), todos1, usuario);
			
			JsfUtil.agregarMensajeInformativo(idioma.getString("MSM_PROCESO_EJECUTADO"));
			
		} catch (NumberFormatException | SystemException e) {
			e.printStackTrace();
		}
    }

    public void oprimirPreliminarBancos() {

        getPreliminar(ReportesBean.FORMATOS.PDF);
    }

    public void oprimirComando22() {

        getPreliminar(ReportesBean.FORMATOS.EXCEL97);

    }
    
 
    

    public void getPreliminar(FORMATOS formato) {
        archivoDescarga = null;
        String infPorPeriodo = "000099ListadoRteFte";
        try {

            ejbNominaUno.putActualizarRetefuente(compania,
                            Integer.parseInt(ano1), Integer.parseInt(mes1),
                            Integer.parseInt(periodo1),
                            usuario);

            formatoD = SysmanFunciones.nvl(ejbSysmanUtil.consultarParametro(
                            compania,
                            "FORMATO LISTADO RETE FUENTE",
                            modulo,
                            new Date(), false), "").toString();
            
            if (porPeriodo) {
            	infPorPeriodo = "002594ListadoRteFte";
            }

            if (formatoIDSN.equalsIgnoreCase(formatoD)) {
                Map<String, Object> parametros = new HashMap<>();

                parametros.put(cNombreEmpresa, nombreCompania);
                String nMes = SysmanConstantes.NOMBRE_MESES_CONTABILIDAD[Integer
                                .parseInt(mes1)];
                parametros.put("PR_NOMBREMES", nMes);

                String nombreJefeRH = ejbSysmanUtil.consultarParametro(compania,
                                "NOMBRE JEFE RECURSOS HUMANOS", modulo,
                                new Date(),
                                false);

                String cargoJefeRH = ejbSysmanUtil.consultarParametro(compania,
                                "CARGO JEFE RECURSOS HUMANOS", modulo,
                                new Date(),
                                false);

                String nombreLiquida = ejbSysmanUtil.consultarParametro(
                                compania,
                                "NOMBRE DE QUIEN AUTORIZA NOMINA", modulo,
                                new Date(),
                                false);

                String cargoLiquida = ejbSysmanUtil.consultarParametro(compania,
                                "CARGO DE QUIEN AUTORIZA NOMINA", modulo,
                                new Date(), false);

                String nombreGerente = ejbSysmanUtil.consultarParametro(
                                compania,
                                "NOMBRE DE QUIEN REVISA NOMINA", modulo,
                                new Date(), false);

                String cargoGerente = ejbSysmanUtil.consultarParametro(compania,
                                "CARGO DE QUIEN REVISA NOMINA", modulo,
                                new Date(), false);

                parametros.put("PR_NOMBRE_JEFE_RECURSOS_HUMANOS", nombreJefeRH);
                parametros.put("PR_CARGO_JEFE_RECURSOS_HUMANOS", cargoJefeRH);
                parametros.put("PR_NOMBRE_DE_QUIEN_AUTORIZA_NOMINA",
                                nombreLiquida);
                parametros.put("PR_CARGO_DE_QUIEN_AUTORIZA_NOMINA",
                                cargoLiquida);
                parametros.put("PR_NOMBRE_DE_QUIEN_REVISA_NOMINA",
                                nombreGerente);
                parametros.put("PR_CARGO_DE_QUIEN_REVISA_NOMINA", cargoGerente);
                
                // IMPLEMETACION PARAMETROS MARCA_BLANCA - ljdiaz (Luis Jacobo Diaz muñoz)
                parametros.put("PR_EMPRESAPARAMETRIZADA", SessionBean.getImpresoPorEmpresaParamterizada());
                // FIN IMPLEMENTACION MARCA_BLANCA

                HashMap<String, Object> reemplazar = new HashMap<>();
                reemplazar.put(cProceso, proceso);
                reemplazar.put("ano1", ano1);
                reemplazar.put("mes1", mes1);
                reemplazar.put(cPeriodoUno, periodo1);
                Reporteador.resuelveConsulta(infPorPeriodo,
                                Integer.parseInt(SessionUtil.getModulo()),
                                reemplazar, parametros);

                archivoDescarga = JsfUtil.exportarStreamed(porPeriodo?infPorPeriodo:formatoD,
                                parametros, ConectorPool.ESQUEMA_SYSMAN,
                                formato);
            }
            else {

                Map<String, Object> parametros = new HashMap<>();

                parametros.put(cNombreEmpresa, nombreCompania);
                String nMes = SysmanConstantes.NOMBRE_MESES_CONTABILIDAD[Integer
                                .parseInt(mes1)];
                parametros.put("PR_NOMBREMES", nMes);

                parametros.put("PR_NOMBRE_DEL_GERENTE",
                                retornarValorParametro("NOMBRE DEL GERENTE"));
                parametros.put("PR_CARGO_DEL_GERENTE",
                                retornarValorParametro("CARGO DEL GERENTE"));
                parametros.put("PR_NOMBRE_DEL_CARGO_TESORERO_PAGADOR",
                                retornarValorParametro(
                                                "NOMBRE DEL CARGO TESORERO PAGADOR"));
                parametros.put("PR_CARGO_DEL_TESORERO_PAGADOR",
                                retornarValorParametro(
                                                "CARGO DEL TESORERO PAGADOR"));
                parametros.put("PR_CARGO_DE_QUIEN_AUTORIZA_NOMINA",
                                retornarValorParametro(
                                                "CARGO DE QUIEN AUTORIZA NOMINA"));
                parametros.put("PR_NOMBRE_DE_QUIEN_AUTORIZA_NOMINA",
                                retornarValorParametro(
                                                "NOMBRE DE QUIEN AUTORIZA NOMINA"));

                parametros.put("PR_NOMBRE_DE_QUIEN_REVISA_NOMINA",
                                retornarValorParametro(
                                                "NOMBRE DE QUIEN REVISA NOMINA"));
                parametros.put("PR_CARGO_DE_QUIEN_REVISA_NOMINA",
                                retornarValorParametro(
                                                "CARGO DE QUIEN REVISA NOMINA"));

                parametros.put("PR_NOMBRE_JEFE_RECURSOS_HUMANOS",
                                retornarValorParametro(
                                                "NOMBRE JEFE RECURSOS HUMANOS"));
                parametros.put("PR_CARGO_JEFE_RECURSOS_HUMANOS",
                                retornarValorParametro(
                                                "CARGO JEFE RECURSOS HUMANOS"));
                parametros.put("PR_MOSTAR",
                                "900.334.265-3".equals(SessionUtil
                                                .getCompaniaIngreso().getNit())
                                                                ? false
                                                                : true);
                // IMPLEMETACION PARAMETROS MARCA_BLANCA - ljdiaz (Luis Jacobo Diaz muñoz)
                parametros.put("PR_EMPRESAPARAMETRIZADA", SessionBean.getImpresoPorEmpresaParamterizada());
                // FIN IMPLEMENTACION MARCA_BLANCA
                //7750292 - ljdiaz - cuarta firma
                String mostrarCuartaFirma = SysmanFunciones.nvlStr(ejbSysmanUtil.consultarParametro(compania, "ACTIVAR CUARTA FIRMA", modulo,
        					new Date(), false), "NO");
        		if("SI".equals(mostrarCuartaFirma)) {
        			String nombreCuartaFirma = SysmanFunciones.nvlStr(ejbSysmanUtil.consultarParametro(compania, "NOMBRE CUARTA FIRMA", modulo,
        					new Date(), false), "NO");
        			String cargoCuartaFirma = SysmanFunciones.nvlStr(ejbSysmanUtil.consultarParametro(compania, "CARGO CUARTA FIRMA", modulo,
        					new Date(), false), "NO");
        			
        			parametros.put("PR_MOSTRAR_CUARTA_FIRMA", mostrarCuartaFirma);
        			parametros.put("PR_NOMBRE_CUARTA_FIRMA", nombreCuartaFirma);
        			parametros.put("PR_CARGO_CUARTA_FIRMA", cargoCuartaFirma);
        		}
                HashMap<String, Object> reemplazar = new HashMap<>();
                reemplazar.put(cProceso, proceso);
                reemplazar.put("ano1", ano1);
                reemplazar.put("mes1", mes1);
                reemplazar.put(cPeriodoUno, periodo1);
                Reporteador.resuelveConsulta(infPorPeriodo,
                                Integer.parseInt(SessionUtil.getModulo()),
                                reemplazar, parametros);

                archivoDescarga = JsfUtil.exportarStreamed(
                				infPorPeriodo,
                                parametros, ConectorPool.ESQUEMA_SYSMAN,
                                formato);

            }

        }

        catch (JRException | IOException | SysmanException
                        | NumberFormatException | SystemException ex) {
            JsfUtil.agregarMensajeError(ex.getMessage());
            Logger.getLogger(ListadosRtefteControlador.class.getName())
                            .log(Level.SEVERE, null, ex);

        }

    }
    
    public void getPreliminarRmensual(FORMATOS formato) {
    	// <CODIGO_DESARROLLADO> BOTONBT176 ----------- INFORME DIAN
        // ------------------------------
        archivoDescarga = null;
        String nombreReporte;

        if (formato == FORMATOS.EXCEL97) {
            nombreReporte = "000115ListadoRteFteINFORMEDIANEXC";
        } else {
            nombreReporte = "000115ListadoRteFteINFORMEDIAN";
        }
        String condTodos;
        if (todos) {
            condTodos = "";
        }
        else {
            condTodos = "AND RETEFUENTE_CALCULOS.RETENCION_APLICADA NOT IN (0) ";
        }
        try {

            Map<String, Object> parametros = new HashMap<>();

            parametros.put(cNombreEmpresa, nombreCompania);
            String nMes = SysmanConstantes.NOMBRE_MESES_CONTABILIDAD[Integer
                            .parseInt(mes1)];
            parametros.put("PR_NOMBREMES", nMes);

            String nombreJefeRH = ejbSysmanUtil.consultarParametro(compania,
                            "NOMBRE DEL GERENTE", modulo, new Date(),
                            false);

            String cargoJefeRH = ejbSysmanUtil.consultarParametro(compania,
                            "CARGO DEL GERENTE", modulo, new Date(),
                            false);

            String nombreLiquida = ejbSysmanUtil.consultarParametro(compania,
                            "NOMBRE DEL CARGO TESORERO PAGADOR", modulo,
                            new Date(),
                            false);

            String cargoLiquida = ejbSysmanUtil.consultarParametro(compania,
                            "CARGO DEL TESORERO PAGADOR", modulo,
                            new Date(), false);

            String nombreGerente = ejbSysmanUtil.consultarParametro(compania,
                            "NOMBRE DE QUIEN AUTORIZA NOMINA", modulo,
                            new Date(), false);

            String cargoGerente = ejbSysmanUtil.consultarParametro(compania,
                            "CARGO DE QUIEN AUTORIZA NOMINA", modulo,
                            new Date(), false);

            parametros.put("PR_NOMBRE_DE_QUIEN_LIQUIDA_NOMINA", nombreJefeRH);
            parametros.put("PR_CARGO_PROFESIONAL_UNIVERSITARIO", cargoJefeRH);
            parametros.put("PR_NOMBRE_DE_QUIEN_APRUEBA_NOMINA", nombreLiquida);
            parametros.put("PR_CARGO_DE_QUIEN_APRUEBA_NOMINA", cargoLiquida);
            parametros.put("PR_NOMBRE_DE_QUIEN_AUTORIZA", nombreGerente);
            parametros.put("PR_CARGO_DE_QUIEN_AUTORIZA", cargoGerente);
            
            // IMPLEMETACION PARAMETROS MARCA_BLANCA - ljdiaz (Luis Jacobo Diaz muñoz)
            parametros.put("PR_EMPRESAPARAMETRIZADA", SessionBean.getImpresoPorEmpresaParamterizada());
            // FIN IMPLEMENTACION MARCA_BLANCA

            HashMap<String, Object> reemplazar = new HashMap<>();

            reemplazar.put(cProceso, proceso);
            reemplazar.put("ano1", ano1);
            reemplazar.put("mes1", mes1);
            reemplazar.put(cPeriodoUno, periodo1);
            reemplazar.put("CONDTODOS", condTodos);

            Reporteador.resuelveConsulta("000115ListadoRteFteINFORMEDIAN",
                            Integer.parseInt(SessionUtil.getModulo()),
                            reemplazar, parametros);

            archivoDescarga = JsfUtil.exportarStreamed(
            		nombreReporte , parametros,
                            ConectorPool.ESQUEMA_SYSMAN,
                            formato);

        }

        catch (JRException | IOException | SysmanException
                        | SystemException ex) {
            JsfUtil.agregarMensajeError(ex.getMessage());
            Logger.getLogger(ListadosRtefteControlador.class.getName())
                            .log(Level.SEVERE, null, ex);

        }
    }

    public void oprimirexcel2() {

    	getPreliminarRmensual(ReportesBean.FORMATOS.EXCEL97);

    }
    

    
    
    public void oprimirComando63() {
        // <CODIGO_DESARROLLADO> BOTON 177
        // <CODIGO_DESARROLLADO>
        archivoDescarga = null;
        String condTodos;
        if (empleado.isEmpty()) {

            if (todos1) {
                condTodos = "";
            }
            else {
                condTodos = "AND rc.RETENCION_APLICADA NOT IN (0) ";
            }
        }
        else {
            /**
             * Se agregan comillas al id del empleado dado que en SQL SERVER un varchar no se puede eviar sin comilla simple
             */
            condTodos = "AND PERSONAL.NUMERO_DCTO = '" + idEmpleado + "' ";
        }
        try {

            Map<String, Object> parametros = new HashMap<>();

            parametros.put(cNombreEmpresa, nombreCompania);

            java.text.NumberFormat.getCurrencyInstance(new Locale("en", "US"))
                            .setMaximumFractionDigits(0);

            String prDeducibles = ejbSysmanUtil.consultarParametro(compania,
                            "VALOR UVT MAXIMOS DEDUCIBLES VIVIENDA",
                            modulo, new Date(), false);

            String prMedicina = ejbSysmanUtil.consultarParametro(compania,
                            "VALOR UVT MAXIMOS DEDUCIBLES MEDICINA PREPAGADAS",
                            modulo, new Date(), false);

            String prPersonal = ejbSysmanUtil.consultarParametro(compania,
                            "VALOR UVT MAXIMOS PERSONAL A CARGO", modulo,
                            new Date(), false);

            String prRenta = ejbSysmanUtil.consultarParametro(compania,
                            "VALOR UVT MAXIMOS RENTA EXCENTA", modulo,
                            new Date(), false);

            parametros.put("PR_VALOR_UVT_MAXIMOS_DEDUCIBLES_VIVIENDA",
                            prDeducibles);
            parametros.put("PR_VALOR_UVT_MAXIMOS_DEDUCIBLES_MEDICINA_PREPAGADAS",
                            prMedicina);
            parametros.put("PR_VALOR_UVT_MAXIMOS_PERSONAL_A_CARGO", prPersonal);
            parametros.put("PR_VALOR_UVT_MAXIMOS_RENTA_EXCENTA", prRenta);
            
            // IMPLEMETACION PARAMETROS MARCA_BLANCA - ljdiaz (Luis Jacobo Diaz muñoz)
            parametros.put("PR_EMPRESAPARAMETRIZADA", SessionBean.getImpresoPorEmpresaParamterizada());
            // FIN IMPLEMENTACION MARCA_BLANCA

            HashMap<String, Object> reemplazar = new HashMap<>();

            reemplazar.put(cProceso, proceso);
            reemplazar.put("ano1", ano1);
            reemplazar.put("mes1", mes1);
            reemplazar.put(cPeriodoUno, periodo1);
            reemplazar.put("CONDTODOS", condTodos);
            reemplazar.put("p1", prDeducibles);
            reemplazar.put("p2", prMedicina);
            reemplazar.put("p3", prPersonal);
            reemplazar.put("p4", prRenta);

            Reporteador.resuelveConsulta("000106ListadoRteFteINFORMEPERIODO",
                            Integer.parseInt(SessionUtil.getModulo()),
                            reemplazar, parametros);

            archivoDescarga = JsfUtil.exportarStreamed(
                            "000106ListadoRteFteINFORMEPERIODO", parametros,
                            ConectorPool.ESQUEMA_SYSMAN,
                            ReportesBean.FORMATOS.PDF);

        }

        catch (JRException | IOException | SysmanException
                        | SystemException ex) {
            JsfUtil.agregarMensajeError(
                            idioma.getString(cInterrumpida)
                                            + ex.getMessage());
            Logger.getLogger(ListadosRtefteControlador.class.getName())
                            .log(Level.SEVERE, null, ex);

        }

    }

    public void oprimirComando57() {
    	getPreliminarRmensual(ReportesBean.FORMATOS.PDF);

    }

    private String retornarValorParametro(String parametro) {
        String rta = "";
        try {
            rta = SysmanFunciones.nvlStr(
                            ejbSysmanUtil.consultarParametro(compania,
                                            parametro,
                                            SessionUtil.getModulo(), new Date(),
                                            false),
                            "");
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
        return rta;

    }

    public void cambiarAno1() {
        // <CODIGO_DESARROLLADO>
    	if(ano1.equals("2023")) {
			cuadreSaldosVisible = true;
		} else {
			cuadreSaldosVisible = false;
		}
    	
        cargarListaPeriodo1();
        cargarListaMes1();
        empleado = null;
        cargarListaEmpleado();
        // </CODIGO_DESARROLLADO>
    }

    public void cambiarMes1() {
        // <CODIGO_DESARROLLADO>
        cargarListaPeriodo1();
        empleado = null;
        cargarListaEmpleado();
        // </CODIGO_DESARROLLADO>
    }

    public void cambiarPeriodo1() {
        // <CODIGO_DESARROLLADO>
        empleado = null;
        cargarListaEmpleado();
        // </CODIGO_DESARROLLADO>
    }

    public void cambiarProceso() {
        // <CODIGO_DESARROLLADO>
        empleado = null;
        cargarListaEmpleado();
        cargarListaPeriodo1();
        // </CODIGO_DESARROLLADO>
    }

    public void seleccionarFilaEmpleado(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        empleado = SysmanFunciones
                        .nvl(registroAux.getCampos().get("NOMBRECOMPLETO"), "")
                        .toString();
        idEmpleado = SysmanFunciones
                        .nvl(registroAux.getCampos().get("NUMERO_DCTO"), "")
                        .toString();
        codEmpleado = SysmanFunciones
                		.nvl(registroAux.getCampos().get("ID_DE_EMPLEADO"), "")
                		.toString();
    }

    public boolean getTodos() {
        return todos;
    }

    public void setTodos(boolean todos) {
        this.todos = todos;
    }

    public boolean getTodos1() {
        return todos1;
    }

    public void setTodos1(boolean todos1) {
        this.todos1 = todos1;
    }

    public String getAno1() {
        return ano1;
    }

    public void setAno1(String ano1) {
        this.ano1 = ano1;
    }

    public String getMes1() {
        return mes1;
    }

    public void setMes1(String mes1) {
        this.mes1 = mes1;
    }

    public String getPeriodo1() {
        return periodo1;
    }

    public void setPeriodo1(String periodo1) {
        this.periodo1 = periodo1;
    }

    public String getProceso() {
        return proceso;
    }

    public void setProceso(String proceso) {
        this.proceso = proceso;
    }

    public String getEmpleado() {
        return empleado;
    }

    public void setEmpleado(String empleado) {
        this.empleado = empleado;
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

    public List<Registro> getListaProceso() {
        return listaProceso;
    }

    public void setListaProceso(List<Registro> listaProceso) {
        this.listaProceso = listaProceso;
    }

    public RegistroDataModelImpl getListaEmpleado() {
        return listaEmpleado;
    }

    public void setListaEmpleado(RegistroDataModelImpl listaEmpleado) {
        this.listaEmpleado = listaEmpleado;
    }

    public String getIdEmpleado() {
        return idEmpleado;
    }

    public void setIdEmpleado(String idEmpleado) {
        this.idEmpleado = idEmpleado;
    }
    
    public String getCodEmpleado() {
        return codEmpleado;
    }

    public void setCodEmpleado(String codEmpleado) {
        this.codEmpleado = codEmpleado;
    }
    
    public boolean isCuadreSaldosVisible() {
		return cuadreSaldosVisible;
	}

	public void setCuadreSaldosVisible(boolean cuadreSaldosVisible) {
		this.cuadreSaldosVisible = cuadreSaldosVisible;
	}
	
	public boolean getPorPeriodo() {
        return porPeriodo;
    }

    public void setPorPeriodo(boolean porPeriodo) {
        this.porPeriodo = porPeriodo;
    }

    @Override
    public void abrirFormulario() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    public StreamedContent getArchivoDescarga() {
        return archivoDescarga;
    }

    public void setArchivoDescarga(StreamedContent archivoDescarga) {
        this.archivoDescarga = archivoDescarga;
    }

}
