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
import com.sysman.nomina.ejb.EjbNominaUnoRemote;
import com.sysman.nomina.enums.PlanillaCesantiasConsignarControladorUrlEnum;
import com.sysman.persistencia.ConectorPool;
import com.sysman.recursos.ejb.EjbSysmanUtilRemote;
import com.sysman.reportes.Reporteador;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.sesion.SessionBean;
import com.sysman.util.SysmanConstantes;
import com.sysman.util.SysmanFunciones;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
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
import javax.faces.context.FacesContext;

import org.primefaces.event.SelectEvent;
import org.primefaces.model.StreamedContent;

import net.sf.dynamicreports.report.exception.DRException;
import net.sf.jasperreports.engine.JRException;

/**
 *
 * @author acaceres
 * @version 1, 22/07/2015
 * 
 * Se pasan las consultas del metodo getConsulta() a la tabla consulta
 * y se realizan los ajustes necesarios, para su llamado. Se
 * modificï¿½ para que muestre cual informe se debe generar en caso de
 * que no exista. Se elimino el metodo oprimirMiPlanilla(), ya que no
 * se estaba utilizando. Se crearon texto bean de algunos textos que
 * estaban quemados.
 * @author jlramirez
 * @version 2, 22/03/2017
 * 
 * @author eamaya
 * @version 2.11, 18/10/2017, Proceso de Refactoring DSS y cambio de
 * numero de formulario por enum
 * 
 */
@ManagedBean
@ViewScoped

public class PlanillaCesantiasConsignarControlador extends BeanBaseModal {

    private final String compania;
    private final String cInforme000067;
    private final String cInforme001757;
    private final String cInforme001828;
    private final String cInforme002760;

    private String ano;
    private String mes;
    private List<Registro> listaAno1;
    private List<Registro> listaMes1;
    private String fondo;
    private String nombreFondo;
    private boolean acumulado;
    private boolean manejaPlantillaCesantiasStr;
    /**
     * Atributo usado para indicar que informe descargar
     * segun el valor del parametro manejaPlantillaCesantiasStr
     */
    String informeSecundario;

    /**
     */

    private RegistroDataModelImpl listaFondoCesantias;
    /**
     * Atributo usado para descargar contenidos de archivos desde la
     * vista
     */
    private StreamedContent archivoDescarga;
    private FacesContext context;
    private final String ces99;
    /**
     * Atributo que almacena el proceso de Nomina con el que se esta
     * trabajando
     */
    private String proceso;
    /**
     * Atributo que almacena el periodo de Nomina seleccionado una vez
     * se ingresa al modulo
     */
    private String periodo;
    /**
     * Atributo que almacena el valor del parï¿½metro <b>FORMATO
     * PLANILLA CESANTIAS A CONSIGNAR</b>
     */
    private String formatoPlantillaCesantias;
    private String formatoPlantillaCesantiasAcum;

    Map<String, Object> reemplazosInformes;
    Map<String, Object> parametrosInformes;

    @EJB
    private EjbSysmanUtilRemote ejbSysmanUtilRemote;
    @EJB
    private EjbNominaUnoRemote ejbNominaUno;

    private final String modulo = SessionUtil.getModulo();

    /**
     * Creates a new instance of PlanillaCesantiasConsignarControlador
     */
    public PlanillaCesantiasConsignarControlador() {
        super();
        compania = SessionUtil.getCompania();
        ces99 = "CES99";
        cInforme000067 = "000067PlanillacesantiasSTRCC";
        cInforme001757 = "001757PlanillacesantiasANE";
        cInforme001828 = "001828PlanillaCesantiasFnaAcum2ANE";
        cInforme002760 = "002760Planilla_cesantias_STR";

        try {
            numFormulario = GeneralCodigoFormaEnum.PLANILLA_CESANTIAS_CONSIGNAR_CONTROLADOR
                            .getCodigo();
            validarPermisos();
            nombreFondo = "NINGUNO";
            fondo = ces99;
            ano = String.valueOf(SysmanFunciones.ano(new Date()));
            mes = String.valueOf(SysmanFunciones.mes(new Date()));
            proceso = SysmanFunciones
                            .nvl(SessionUtil.getSessionVar("procesoNomina"), "")
                            .toString();
            periodo = SysmanFunciones
                            .nvl(SessionUtil.getSessionVar("periodoNomina"), "")
                            .toString();

        }
        catch (Exception ex) {
            Logger.getLogger(PlanillaCesantiasConsignarControlador.class
                            .getName()).log(Level.SEVERE, null, ex);
            SessionUtil.redireccionarMenuPermisos();
        }
    }

    @PostConstruct
    public void inicializar() {
        try {
            cargarListaAno1();
            cargarListaMes1();
            cargarListaFondoCesantias();
            //abrirFormulario();
            abrirFormulario();
                formatoPlantillaCesantias = SysmanFunciones.nvlStr(
                        ejbSysmanUtilRemote.consultarParametro(compania,
                                        "FORMATO PLANILLA CESANTIAS A CONSIGNAR",
                                        modulo,
                                        new Date(), false),
                        cInforme000067);
            	
            	
            
            abrirFormulario();


        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
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
                                                            PlanillaCesantiasConsignarControladorUrlEnum.URL3033
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
                        ano);

        try {
            listaMes1 = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            PlanillaCesantiasConsignarControladorUrlEnum.URL3467
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    public void cargarListaFondoCesantias() {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        PlanillaCesantiasConsignarControladorUrlEnum.URL3034
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        listaFondoCesantias = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, GeneralParameterEnum.CODIGO.getName());

    }

    public void oprimirImprimirBancos() {
        // <CODIGO_DESARROLLADO>
        archivoDescarga = null;
        generarInforme(FORMATOS.EXCEL);
        // </CODIGO_DESARROLLADO>
    }

    public void oprimirPreliminarBancos() {
        // <CODIGO_DESARROLLADO>
        archivoDescarga = null;
        generarInforme(FORMATOS.PDF);
        // </CODIGO_DESARROLLADO>
    }

    public void oprimirgenerarPlarno() {
        archivoDescarga = null;
        generarExcel();
    }
    
    public void oprimirArcAportesLinea() {
        archivoDescarga = null;
        generarExcelAportesEnLinea();
    }
    
    public void oprimirconsultaFactores() {
    	archivoDescarga = null;    
    	try 
    	{
			String pivot = ejbNominaUno.getPreparaPivotConsFact(compania,
																Integer.parseInt(ano),
																Integer.parseInt(mes));
			
			if (pivot == null) {
                JsfUtil.agregarMensajeAlerta(
                                idioma.getString("TG_NO_EXISTE_INFORMACION_CON_LOS_PARAMETROS_SUMINISTRADOS"));
                return;
            }
			
			HashMap<String, Object> reemplazar = new HashMap<>();
			reemplazar.put("compania", compania);
			reemplazar.put("anio", ano);
			reemplazar.put("mes", mes);
			reemplazar.put("pivot", pivot);
			
			String strSql = Reporteador.resuelveConsulta(
                    "800561ConsultaFactoresCesantias",
                    Integer.parseInt(modulo), reemplazar);
			
			archivoDescarga = JsfUtil.exportarHojaDatosStreamed(strSql,
                    ConectorPool.ESQUEMA_SYSMAN,
                    ReportesBean.FORMATOS.EXCEL);
			
		} catch (JRException | IOException | SQLException | DRException | SysmanException | SystemException e) {
			e.printStackTrace();
		}
    }

    public void cambiarAno1() {
        // <CODIGO_DESARROLLADO>
        mes = null;
        cargarListaMes1();
        // </CODIGO_DESARROLLADO>
    }

    public void cambiarMes1() {
        // <CODIGO_DESARROLLADO>
        mes = null;
        cargarListaMes1();
        // </CODIGO_DESARROLLADO>
    }

    public void seleccionarFilaFondoCesantias(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        fondo = retornarString(registroAux,
                        GeneralParameterEnum.CODIGO.getName());
        nombreFondo = retornarString(registroAux,
                        GeneralParameterEnum.NOMBRE.getName());
    }

    /**
     * Mediante esta opcion se pueden generar dos reportes, en este
     * mï¿½todo se evalï¿½a si los dos informes poseen informaciï¿½n y de
     * acuerdo a esto se genera un informe de forma normal o dos de
     * forma comprimida
     * 
     * @param formato
     * Formato seleccionado por el usuario para generar el informe
     */
    private void generarInforme(FORMATOS formato) {
        try {
            archivoDescarga = null;
            if (acumulado) {
                formatoPlantillaCesantias = SysmanFunciones.nvlStr(
                                ejbSysmanUtilRemote.consultarParametro(compania,
                                                "FORMATO PLANILLA CESANTIAS ACUMULADAS A CONSIGNAR",
                                                modulo,
                                                new Date(), false),
                                cInforme000067);
                }
                else {
                    formatoPlantillaCesantias = SysmanFunciones.nvlStr(
                            ejbSysmanUtilRemote.consultarParametro(compania,
                                            "FORMATO PLANILLA CESANTIAS A CONSIGNAR",
                                            modulo,
                                            new Date(), false),
                            cInforme000067);
                	
                	}

            // Definicion de la consulta del primer informe
            String consultaInforme1;
            if (cInforme000067
                            .equals(formatoPlantillaCesantias)) {
                consultaInforme1 = acumulado ? "800096cesantiasaconsignar"
                    : "800097cesantiasaconsignarSinAcum";
            }
            else if (cInforme001757
                            .equals(formatoPlantillaCesantias)) {
                consultaInforme1 = cInforme001757;
            }
            else {
                consultaInforme1 = formatoPlantillaCesantias;
            }
            // Se consulta el parámetro y se decide si usar el informe alternativo

            manejaPlantillaCesantiasStr = "SI".equals(SysmanFunciones
                    .nvl(ejbSysmanUtilRemote.consultarParametro(compania, "MANEJA PLANILLA CESANTIAS STR",
                        "-1", new Date(), true), "NO"));
            
            
            // Según el valor del parámetro, se usa cInforme002760 o cInforme001828
             informeSecundario = manejaPlantillaCesantiasStr ? cInforme002760 : cInforme001828;

            generarReemplazosParametros();

            ArrayList<Long> totalRegistros = new ArrayList<>();
            totalRegistros.add(service
                            .getConteoConsulta(Reporteador.resuelveConsulta(
                                            consultaInforme1,
                                            Integer.valueOf(SessionUtil
                                                            .getModulo()),
                                            reemplazosInformes)));
            totalRegistros.add(service.getConteoConsulta(
                    Reporteador.resuelveConsulta(informeSecundario,
                        Integer.valueOf(SessionUtil.getModulo()),
                        reemplazosInformes)));

            // Evalua si solo uno de los reportes a generar trae
            // informacion
            boolean unInforme = totalRegistros.contains((long) 0);
            int posAux = unInforme ? totalRegistros.indexOf((long) 0) : -1;

            if (unInforme) {
            	generarInformeSencillo(formato,
                        posAux != 0 ? formatoPlantillaCesantias : informeSecundario,
                        posAux != 0 ? consultaInforme1 : informeSecundario);
            }
            else {
            	generarComprimido(formatoPlantillaCesantias,
                        consultaInforme1,
                        informeSecundario,
                        informeSecundario,
                        formato);
            }

        }
        catch (NumberFormatException | SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    /**
     * Realiza la generacion de un reporte en especifico
     * 
     * @param formato
     * El formato en el que se desea generar el informe
     * @param informe
     * Nombre del archivo jasper a generar
     * @param consulta
     * Nombre de la consulta a ejecutar para obtener la informacion
     * del reporte
     */
    private void generarInformeSencillo(FORMATOS formato, String informe,
        String consulta) {
        try {

            Reporteador.resuelveConsulta(consulta,
                            Integer.parseInt(modulo),
                            reemplazosInformes,
                            parametrosInformes);

            archivoDescarga = JsfUtil.exportarStreamed(
                            informe,
                            parametrosInformes,
                            ConectorPool.ESQUEMA_SYSMAN,
                            formato);
        }
        catch (JRException | IOException | SysmanException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    /**
     * Arma las estructuras que se envï¿½an para la generaciï¿½n de los
     * reportes, las cuales incluyen los reemplazos para las consultas
     * y los parï¿½metros requeridos por los mismos
     */
    private void generarReemplazosParametros() {
        try {
            // MANEJO DE REEMPLAZOS DEL REPORTE

            reemplazosInformes = new HashMap<>();
            reemplazosInformes.put("anio", ano);
            reemplazosInformes.put("ano", ano);
            reemplazosInformes.put("mes", mes);
            reemplazosInformes.put("proceso", proceso);
            reemplazosInformes.put("periodo", periodo);
            reemplazosInformes.put("fondo",fondo);

            if (cInforme000067
                            .equals(formatoPlantillaCesantias)) {
                String condicionFondo = idioma.getString("TB_TB3924");
                condicionFondo = condicionFondo.replace("s$fondo$s",
                                fondo);
                reemplazosInformes.put("fondo",
                                ces99.equals(fondo) ? "" : condicionFondo);

            }
            else if (cInforme001757
                            .equals(formatoPlantillaCesantias)) {

                reemplazosInformes.put("acumulado", acumulado ? 1 : 0);
                reemplazosInformes.put("fondoCesantias",
                                SysmanFunciones.concatenar("'", fondo, "'"));
            }

            // MANEJO DE PARAMETROS DEL REPORTE

            parametrosInformes = new HashMap<>();
            parametrosInformes.put("PR_ANO", ano);

            parametrosInformes.put("PR_PERIODO", periodo);

            parametrosInformes.put("PR_NOMBREEMPRESA",
                            SessionUtil.getCompaniaIngreso().getNombre());

            String parametroSubtitulo = idioma.getString("TB_TB2994").replace(
                            "#$mes$#",
                            SysmanConstantes.NOMBRE_MESES_CONTABILIDAD[Integer
                                            .parseInt(mes)])
                            .replace("#$anio$#", ano);

            parametrosInformes.put("PR_SUBTITULO", parametroSubtitulo);

            String mesNombre = SysmanFunciones.toString(
                            SysmanConstantes.NOMBRE_MESES_CONTABILIDAD[Integer
                                            .parseInt(mes)]);
            parametrosInformes.put("PR_MES", mesNombre);

            String nombreGerente = SysmanFunciones.nvlStr(
                            ejbSysmanUtilRemote.consultarParametro(compania,
                                            "NOMBRE DEL GERENTE",
                                            modulo,
                                            new Date(), false),
                            " ");

            parametrosInformes.put("PR_NOMBRE_DEL_GERENTE", nombreGerente);

            String cargoGerente = SysmanFunciones.nvlStr(
                            ejbSysmanUtilRemote.consultarParametro(compania,
                                            "CARGO DEL GERENTE",
                                            modulo,
                                            new Date(), false),
                            " ");

            parametrosInformes.put("PR_CARGO_DEL_GERENTE", cargoGerente);

            String cargoTesorero = SysmanFunciones.nvlStr(
                            ejbSysmanUtilRemote.consultarParametro(compania,
                                            "NOMBRE DEL CARGO TESORERO PAGADOR",
                                            modulo,
                                            new Date(), false),
                            " ");

            parametrosInformes.put("PR_NOMBRE_DEL_CARGO_TESORERO_PAGADOR",
                            cargoTesorero);

            String tesoreroPagador = SysmanFunciones.nvlStr(
                            ejbSysmanUtilRemote.consultarParametro(compania,
                                            "CARGO DEL TESORERO PAGADOR",
                                            modulo,
                                            new Date(), false),
                            " ");

            parametrosInformes.put("PR_CARGO_DEL_TESORERO_PAGADOR",
                            tesoreroPagador);

            String nombreAutoriza = SysmanFunciones.nvlStr(
                            ejbSysmanUtilRemote.consultarParametro(compania,
                                            "NOMBRE DE QUIEN AUTORIZA NOMINA",
                                            modulo,
                                            new Date(), false),
                            " ");

            parametrosInformes.put("PR_NOMBRE_DE_QUIEN_AUTORIZA_NOMINA",
                            nombreAutoriza);

            String cargoAutoriza = SysmanFunciones.nvlStr(
                            ejbSysmanUtilRemote.consultarParametro(compania,
                                            "CARGO DE QUIEN AUTORIZA NOMINA",
                                            modulo,
                                            new Date(), false),
                            " ");
            parametrosInformes.put("PR_CARGO_DE_QUIEN_AUTORIZA_NOMINA",
                            cargoAutoriza);
            
            // IMPLEMETACION PARAMETROS MARCA_BLANCA - ljdiaz (Luis Jacobo Diaz muï¿½oz)
            parametrosInformes.put("PR_EMPRESAPARAMETRIZADA", SessionBean.getImpresoPorEmpresaParamterizada());
            // FIN IMPLEMENTACION MARCA_BLANCA
            
            String nombreJefePresu = SysmanFunciones.nvlStr(
                    ejbSysmanUtilRemote.consultarParametro(compania,
                                    "NOMBRE DE JEFE DE PRESUPUESTO",
                                    modulo,
                                    new Date(), false),
                    " ");
            parametrosInformes.put("PR_NOMBRE_DE_JEFE_DE_PRESUPUESTO",
                    nombreJefePresu);
            
            
            String cargoJefePresu= SysmanFunciones.nvlStr(
                    ejbSysmanUtilRemote.consultarParametro(compania,
                                    "CARGO DEL JEFE DE PRESUPUESTO",
                                    modulo,
                                    new Date(), false),
                    " ");
            parametrosInformes.put("PR_CARGO_DEL_JEFE_DE_PRESUPUESTO",
                    cargoJefePresu);
            
            String nombreQuienRevisa= SysmanFunciones.nvlStr(
                    ejbSysmanUtilRemote.consultarParametro(compania,
                                    "NOMBRE DE QUIEN REVISA NOMINA",
                                    modulo,
                                    new Date(), false),
                    " ");
            parametrosInformes.put("PR_NOMBRE_DE_QUIEN_REVISA_NOMINA",
                    nombreQuienRevisa);
            
            
            String cargoQuienRevisa= SysmanFunciones.nvlStr(
                    ejbSysmanUtilRemote.consultarParametro(compania,
                                    "CARGO DE QUIEN REVISA NOMINA",
                                    modulo,
                                    new Date(), false),
                    " ");
            parametrosInformes.put("PR_CARGO_DE_QUIEN_REVISA_NOMINA",
                    cargoQuienRevisa);

        }
        catch (SystemException e1) {
            logger.error(e1.getMessage(), e1);
            JsfUtil.agregarMensajeError(e1.getMessage());
        }

    }

    /**
     * Arma un archivo comprimido incluyendo los dos informes a
     * generar
     * 
     * @param informeUno
     * Primer informe a incluir en el comprimido
     * @param informeDos
     * Segundo informe a incluir en el comprimido
     * @param reemplazar
     * Valores de reemplazo para las consultas de ambos reportes
     * @param parametros
     * Parametros que se envian a los reportes
     * @param formato
     * Formato en el que se desean generar los informes que se
     * encuentan en el comprimido
     */
    public void generarComprimido(String informeUno, String consultaInformeUno,
        String informeDos, String consultaInformeDos,
        ReportesBean.FORMATOS formato) {
        String[] informe = new String[2];
        String[] nombresArchivos = new String[2];
        ByteArrayInputStream[] salidas = new ByteArrayInputStream[2];

        informe[0] = informeUno;
        informe[1] = informeDos;

        try {
            Reporteador.resuelveConsulta(consultaInformeUno,
                            Integer.valueOf(SessionUtil.getModulo()),
                            reemplazosInformes, parametrosInformes);
            if (FORMATOS.PDF.equals(formato)) {

                salidas[0] = JsfUtil.serializarReporte(informe[0],
                                parametrosInformes,
                                ConectorPool.ESQUEMA_SYSMAN, FORMATOS.PDF);

                Reporteador.resuelveConsulta(consultaInformeDos,
                                Integer.valueOf(SessionUtil.getModulo()),
                                reemplazosInformes, parametrosInformes);

                salidas[1] = JsfUtil.serializarReporte(informe[1],
                                parametrosInformes,
                                ConectorPool.ESQUEMA_SYSMAN, FORMATOS.PDF);

                nombresArchivos[0] = SysmanFunciones.concatenar(informeUno,
                                ".pdf");
                nombresArchivos[1] = SysmanFunciones.concatenar(informeDos,
                                ".pdf");
            }
            else {
                salidas[0] = JsfUtil.serializarReporte(informe[0],
                                parametrosInformes,
                                ConectorPool.ESQUEMA_SYSMAN, FORMATOS.EXCEL);

                Reporteador.resuelveConsulta(consultaInformeDos,
                                Integer.valueOf(SessionUtil.getModulo()),
                                reemplazosInformes, parametrosInformes);

                salidas[1] = JsfUtil.serializarReporte(informe[1],
                                parametrosInformes,
                                ConectorPool.ESQUEMA_SYSMAN, FORMATOS.EXCEL);

                nombresArchivos[0] = SysmanFunciones.concatenar(informeUno,
                                ".xlsx");
                nombresArchivos[1] = SysmanFunciones.concatenar(informeDos,
                                ".xlsx");
            }

            archivoDescarga = JsfUtil.exportarComprimidoGeneralStreamed(
                            salidas, nombresArchivos);
        }
        catch (JRException | IOException | SysmanException | DRException
                        | SQLException | NumberFormatException e) {
            JsfUtil.agregarMensajeError(e.getMessage());
            logger.error(e.getMessage(), e);

        }

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

    public boolean isAcumulado() {
        return acumulado;
    }

    public void setAcumulado(boolean acumulado) {
        this.acumulado = acumulado;
    }

    @Override
    public void abrirFormulario() {
        // <CODIGO_DESARROLLADO>

        // </CODIGO_DESARROLLADO>
    }

    /**
     * Atributo usado para descargar contenidos de archivos desde la
     * vista
     */
    public StreamedContent getArchivoDescarga() {
        return archivoDescarga;
    }

    public FacesContext getContext() {
        return context;
    }

    public void setContext(FacesContext context) {
        this.context = context;
    }

    public String getFondo() {
        return fondo;
    }

    public void setFondo(String fondo) {
        this.fondo = fondo;
    }

    public RegistroDataModelImpl getListaFondoCesantias() {
        return listaFondoCesantias;
    }

    public void setListaFondoCesantias(
        RegistroDataModelImpl listaFondoCesantias) {
        this.listaFondoCesantias = listaFondoCesantias;
    }

    public String getNombreFondo() {
        return nombreFondo;
    }

    public void setNombreFondo(String nombreFondo) {
        this.nombreFondo = nombreFondo;
    }

    private void generarExcel() {

        Map<String, Object> reemplazar = new HashMap<>();
        String condicionFondo = idioma.getString("TB_TB3923");
        condicionFondo = condicionFondo.replace("s$fondo$s", fondo);
        reemplazar.put("ano", ano);
        reemplazar.put("mes", mes);
        reemplazar.put("fondo", ces99.equals(fondo) ? "" : condicionFondo);

        String strSql = Reporteador
                        .resuelveConsulta(
                                        "800098CONSIGNACION_CESANTIAS_MIPLANILLA_PILA",
                                        Integer.parseInt(
                                                        SessionUtil.getModulo()),
                                        reemplazar);

        try {
            archivoDescarga = JsfUtil.exportarHojaDatosStreamed(strSql,
                            ConectorPool.ESQUEMA_SYSMAN, FORMATOS.EXCEL97,
                            "CONSIGNACION_CESANTIAS_MIPLANILLA_PILA");
        }
        catch (JRException | IOException | SQLException | DRException
                        | SysmanException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }
    
    private void generarExcelAportesEnLinea() {

        Map<String, Object> reemplazar = new HashMap<>();
        String condicionFondo = idioma.getString("TB_TB3923");
        condicionFondo = condicionFondo.replace("s$fondo$s", fondo);
        reemplazar.put("ano", ano);
        reemplazar.put("mes", mes);
       
        String strSql = Reporteador
                        .resuelveConsulta(
                                        "800610CesantiasAportesEnLinea",
                                        Integer.parseInt(
                                                        SessionUtil.getModulo()),
                                        reemplazar);

        try {
            archivoDescarga = JsfUtil.exportarHojaDatosStreamed(strSql,
                            ConectorPool.ESQUEMA_SYSMAN, FORMATOS.EXCEL97,
                            "CESANTIAS_APORTES_EN_LINEA_" + ano);
        }
        catch (JRException | IOException | SQLException | DRException
                        | SysmanException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }
    
    

    private String retornarString(Registro reg, String campo) {
        return SysmanFunciones.validarCampoVacio(reg.getCampos(), campo) ? ""
            : reg.getCampos().get(campo).toString();
    }

	public String getFormatoPlantillaCesantiasAcum() {
		return formatoPlantillaCesantiasAcum;
	}

	public void setFormatoPlantillaCesantiasAcum(String formatoPlantillaCesantiasAcum) {
		this.formatoPlantillaCesantiasAcum = formatoPlantillaCesantiasAcum;
	}

}
