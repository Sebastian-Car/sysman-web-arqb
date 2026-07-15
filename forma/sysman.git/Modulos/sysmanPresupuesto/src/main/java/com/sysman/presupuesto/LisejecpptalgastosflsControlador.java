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
import com.sysman.jsfutil.ReportesBean.FORMATOS;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.persistencia.ConectorPool;
import com.sysman.presupuesto.enums.LisejecpptalgastosflsControladorEnum;
import com.sysman.presupuesto.enums.LisejecpptalgastosflsControladorUrlEnum;
import com.sysman.recursos.ejb.EjbSysmanUtilRemote;
import com.sysman.reportes.Reporteador;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.util.SysmanConstantes;
import com.sysman.util.SysmanFunciones;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.sql.SQLException;
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

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.primefaces.event.SelectEvent;
import org.primefaces.model.StreamedContent;

import net.sf.dynamicreports.report.exception.DRException;
import net.sf.jasperreports.engine.JRException;

/**
 *
 * @author ybecerra
 * @version 1, 27/06/2016
 * @version 2, 19/04/2017 jrodriguezr Se refactoriza el codigo SQL de
 * las listas para utilizar dss.
 * @version 3, 24/04/2017 jrodriguezr Se refactoriza el codigo
 * ajustando los llamados a funciones, procedimientos y metodos de la
 * clase Acciones a llamados a EJB.
 *
 * @author eamaya
 * @version 4.0, 13/06/2017 Se cambió el llamado del código del
 * formulario y actualización de ConnectorPool
 *
 */
@ManagedBean
@ViewScoped
public class LisejecpptalgastosflsControlador extends BeanBaseModal {
    private final String compania;
    private final String modulo;

    /**
     * Constante que almacenara la cadena "CODIGO"
     */
    private final String codigoC;

    /**
     * Constante que almacenara la cadena "PR_FORMATO"
     */
    private final String prFormatoC;

    /**
     * Constante que almacenara la cadena "000948LisEjecPptalGastosFL"
     */
    private String nombreReportC;
    // <DECLARAR_ATRIBUTOS>
    private boolean especial;
    private boolean agrupado;
    private boolean fuente;
    private boolean resumido;
    private boolean conId;
    private boolean entreMeses;
    private boolean excelPlano;
    private String cuentaInicial;
    private String cuentaFinal;
    private String digitos;
    private String fuenteInicial;
    private String fuenteFinal;
    private int ano;
    private int mes;
    private int mesFinal;
    private int nivel;
    private boolean fuenteVisible;
    private boolean presentarVisible;
    private StreamedContent archivoDescarga;
    // </DECLARAR_ATRIBUTOS>
    // <DECLARAR_PARAMETROS>
    // </DECLARAR_PARAMETROS>
    // <DECLARAR_LISTAS>
    private List<Registro> listaAno;
    private List<Registro> listaMes;
    private List<Registro> listaMesFinal;
    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>
    private RegistroDataModelImpl listaCuentaInicial;
    private RegistroDataModelImpl listaCuentaFinal;
    private RegistroDataModelImpl listaFuenteInicial;
    private RegistroDataModelImpl listaFuenteFinal;
    // </DECLARAR_LISTAS_COMBO_GRANDE>

    @EJB
    private EjbSysmanUtilRemote ejbSysmanUtil;

    /**
     * Creates a new instance of LisejecpptalgastosflsControlador
     */
    public LisejecpptalgastosflsControlador() {
        super();
        compania = SessionUtil.getCompania();
        modulo = SessionUtil.getModulo();
        codigoC = "CODIGO";
        prFormatoC = "PR_FORMATO";
        nombreReportC = "000948LisEjecPptalGastosFL";
        cuentaInicial = SysmanConstantes.DEFECTOINICIAL_STRING;
        cuentaFinal = SysmanConstantes.DEFECTOFINAL_STRING;
        fuenteInicial = SysmanConstantes.DEFECTOINICIAL_STRING;
        fuenteFinal = SysmanConstantes.DEFECTOFINAL_STRING;

        try {
            numFormulario = GeneralCodigoFormaEnum.LISEJECPPTALGASTOSFLS_CONTROLADOR
                            .getCodigo();

            validarPermisos();
            // <INI_ADICIONAL>
            // </INI_ADICIONAL>
        }
        catch (Exception ex) {
            Logger.getLogger(LisejecpptalgastosflsControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            SessionUtil.redireccionarMenuPermisos();
        }
    }

    @PostConstruct
    public void inicializar() {
        abrirFormulario();
        // <CARGAR_LISTA>
        cargarListaAno();
        cargarListaMes();
        cargarListaMesFinal();
        // </CARGAR_LISTA>
        // <CARGAR_LISTA_COMBO_GRANDE>
        cargarListaCuentaInicial();
        cargarListaCuentaFinal();
        /*
         * Se agrega este llamado para que apenas se abra el
         * formulario se cargue la lista del combo "fuente inicial"
         */
        cargarListaFuenteInicial();
        // </CARGAR_LISTA_COMBO_GRANDE>

    }

    @Override
    public void abrirFormulario() {

        ano = SysmanFunciones
                        .ano(new Date());
        mes = SysmanFunciones
                        .mes(new Date());
        mesFinal = SysmanFunciones
                		.mes(new Date());
        nivel = 99;
        digitos = "2";
        presentarVisible = true;
        fuenteVisible = false;
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
                                                            LisejecpptalgastosflsControladorUrlEnum.URL4777
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    public void cargarListaMes() {
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(LisejecpptalgastosflsControladorEnum.ANIO.getValue(), ano);
        try {
            listaMes = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            LisejecpptalgastosflsControladorUrlEnum.URL5230
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }
    
    public void cargarListaMesFinal() {
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(LisejecpptalgastosflsControladorEnum.ANIO.getValue(), ano);
        try {
            listaMesFinal = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            LisejecpptalgastosflsControladorUrlEnum.URL5230
                                                                            .getValue())
                                            .getUrl(), param));
        } catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    public void cargarListaCuentaInicial() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        LisejecpptalgastosflsControladorUrlEnum.URL5961
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.ANO.getName(), ano);
        listaCuentaInicial = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, codigoC);
    }

    public void cargarListaCuentaFinal() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        LisejecpptalgastosflsControladorUrlEnum.URL6925
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.ANO.getName(), ano);
        param.put(GeneralParameterEnum.NATURALEZA.getName(), "D");
        param.put(LisejecpptalgastosflsControladorEnum.CUENTAINICIAL.getValue(),
                        cuentaInicial);
        listaCuentaFinal = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, codigoC);
    }

    public void cargarListaFuenteInicial() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        LisejecpptalgastosflsControladorUrlEnum.URL7986
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.ANO.getName(), ano);
        listaFuenteInicial = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, codigoC);
    }

    public void cargarListaFuenteFinal() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        LisejecpptalgastosflsControladorUrlEnum.URL8754
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.ANO.getName(), ano);
        param.put(LisejecpptalgastosflsControladorEnum.AUXILIARINICIAL
                        .getValue(),
                        fuenteInicial);
        listaFuenteFinal = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, codigoC);
    }

    // </METODOS_CARGAR_LISTA>
    // <METODOS_BOTONES>
    public void oprimirPresentar() {
        // <CODIGO_DESARROLLADO>
        archivoDescarga = null;
        generarInforme(FORMATOS.PDF);
        // </CODIGO_DESARROLLADO>
    }

    public void oprimirExcel() {
        // <CODIGO_DESARROLLADO>
        archivoDescarga = null;
        generarInforme(FORMATOS.EXCEL);
        // </CODIGO_DESARROLLADO>
    }

    // </METODOS_BOTONES>
    public void validarFuente(String fuenteInicial, String fuenteFinal) {
        if (((fuenteInicial == null) || fuenteInicial.isEmpty())
            || ((fuenteFinal == null) || fuenteFinal.isEmpty())) {

            JsfUtil.agregarMensajeAlerta(
                            idioma.getString("TB_TB639"));
            return;
        }

    }

    private void generarInforme(ReportesBean.FORMATOS formato) {

        String parReporte = "";
        int resVisible = 0;
        int cueVisible = 0;
        ByteArrayOutputStream out;
        Workbook workbook;
        out = new ByteArrayOutputStream();
        nombreReportC = "000948LisEjecPptalGastosFL";
        String titulo = "";
        String parametro;
        String consultaBase = "";
        try {
            parametro = ejbSysmanUtil.consultarParametro(
                            compania,
                            "EJECUCION PRESUPUESTAL CON CUENTAS POR PAGAR",
                            modulo, new Date(), true);

            if (SysmanFunciones.validarVariableVacio(digitos)) {
                digitos = "2";
            }

            HashMap<String, Object> reemplazar = new HashMap<>();
            reemplazar.put("ano", ano);
            reemplazar.put("mes", mes);
            reemplazar.put("cuentaInicial", "'" + cuentaInicial + "'");
            reemplazar.put("cuentaFinal", "'" + cuentaFinal + "'");
            reemplazar.put("fuenteInicial", "'" + fuenteInicial + "'");
            reemplazar.put("fuenteFinal", "'" + fuenteFinal + "'");
            reemplazar.put("digitos", digitos);
            reemplazar.put("nivel", nivel);
            reemplazar.put("mesFinal", mesFinal);
            reemplazar.put("conId", conId ? "S" : "N");


            String firmaUno = ejbSysmanUtil.consultarParametro(
                            compania, "FIRMA EJECUCION 1", modulo, new Date(),
                            true);
            String firmaDos = ejbSysmanUtil.consultarParametro(
                            compania, "FIRMA EJECUCION 2", modulo, new Date(),
                            true);
            String firmaTres = SysmanFunciones
                            .nvl(ejbSysmanUtil.consultarParametro(compania,
                                            "FIRMA EJECUCION 3", modulo,
                                            new Date(), true), "")
                            .toString();
            String cargoUno = ejbSysmanUtil.consultarParametro(
                            compania, "CARGO EJECUCION 1", modulo, new Date(),
                            true);
            String cargoDos = ejbSysmanUtil.consultarParametro(
                            compania, "CARGO EJECUCION 2", modulo, new Date(),
                            true);
            String cargoTres = SysmanFunciones
                            .nvl(ejbSysmanUtil.consultarParametro(
                                            compania,
                                            "CARGO EJECUCION 3", modulo,
                                            new Date(), true), "")
                            .toString();
            String representanteLegal = SysmanFunciones.nvl(ejbSysmanUtil.consultarParametro(compania,
							"NOMBRE REPRESENTANTE LEGAL",
							SessionUtil.getModulo(),new Date(), true)," ").toString();

            String firmaRepLegal = SysmanFunciones.nvl(ejbSysmanUtil.consultarParametro(compania,
							"FIRMA REPRESENTANTE LEGAL",
							SessionUtil.getModulo(),new Date(), true)," ").toString();

            String secretariaHacienda = SysmanFunciones.nvl(ejbSysmanUtil.consultarParametro(compania,
						"NOMBRE DE SECRETARIA DE HACIENDA",
						SessionUtil.getModulo(),new Date(), true)," ").toString();

            Map<String, Object> parametros = new HashMap<>();
            parametros.put("PR_FIRMAUNO", firmaUno);
            parametros.put("PR_CARGOUNO", cargoUno);
            parametros.put("PR_FIRMADOS", firmaDos);
            parametros.put("PR_CARGODOS", cargoDos);
            parametros.put("PR_FIRMATRES", firmaTres);
            parametros.put("PR_CARGOTRES", cargoTres);
            
            if(entreMeses) {
	            parametros.put("PR_ENCABEZADO","DESDE "
			   			+SysmanConstantes.NOMBRE_MESES_CONTABILIDAD[(mes)].toUpperCase()
			            +" HASTA " +SysmanConstantes.NOMBRE_MESES_CONTABILIDAD[(mesFinal)].toUpperCase() 
			            +" DE " +ano);
            } else {
            	parametros.put("PR_ENCABEZADO",
                        idioma.getString("TB_TB633") + " "
                            + SysmanConstantes.NOMBRE_MESES_CONTABILIDAD[mes]
                                            .toUpperCase()
                            + " DE " + ano);
            }
            
            if (mes == 12) {
                resVisible = 1;
                titulo = idioma.getString("TB_TB635");
            }

            parametros.put("PR_RESVISIBLE", resVisible);

            if ((mes == 12) && "SI".equals(parametro)) {

                cueVisible = 1;
                titulo = idioma.getString("TB_TB638");
            }
            parametros.put("PR_CUEVISIBLE", cueVisible);
            parametros.put("PR_TITULO", titulo);
            parametros.put("PR_NITCOMPANIA",SessionUtil.getCompaniaIngreso().getNit());
            parametros.put("PR_NOMBRECOMPANIA",SessionUtil.getCompaniaIngreso().getNombre());
            parametros.put("PR_NOMBRE_REPRESENTANTE_LEGAL",representanteLegal);
            parametros.put("PR_FIRMA_REPRESENTANTE_LEGAL",firmaRepLegal);
            parametros.put("PR_NOMBRE_DE_SECRETARIA_DE_HACIENDA",secretariaHacienda);
            
            switch (digitos) {

            case "0":
                parametros.put(prFormatoC, "#,#00;(#,#00)");
                break;
            case "1":
                parametros.put(prFormatoC, "#,#00.0;(#,#00.0)");
                break;
            case "2":
                parametros.put(prFormatoC, "#,#00.00;(#,#00.00)");
                break;

            default:
                parametros.put(prFormatoC, "#,##0");
                break;

            }

            if (especial) {
                if (fuente) {
                    parReporte = "000953LisEjecPptalGastosSO";
                }
                else {
                    parReporte = "000955LisEjecPptalGastosSOF";
                }
                Reporteador.resuelveConsulta(nombreReportC,
                                Integer.parseInt(modulo), reemplazar,
                                parametros);
            }
            else {
                if (fuente) {
                    parReporte = nombreReportC;
                    Reporteador.resuelveConsulta(nombreReportC,
                                    Integer.parseInt(modulo), reemplazar,
                                    parametros);
                }
                else if (agrupado) {
                    validarFuente(fuenteInicial, fuenteFinal);
                    parReporte = "000949LisEjecPptalGastosFLYOPAL";
                    Reporteador.resuelveConsulta(
                                    "000949LisEjecPptalGastosFLYOPAL",
                                    Integer.parseInt(modulo), reemplazar,
                                    parametros);
                }
                else {

                    if (resumido) {
                        nombreReportC = "003006LisEjecPptalGastosFLResumido";
                    }

                    parReporte = "000950LisEjecPptalGastosFLFLO";

                    Reporteador.resuelveConsulta(nombreReportC,
                                    Integer.parseInt(modulo), reemplazar,
                                    parametros);
                }
            }
            if(entreMeses) {
            	nombreReportC = "002770LisEjecPptalGastosEntreMeses";
            	parReporte = nombreReportC;

            	Reporteador.resuelveConsulta(nombreReportC,
                            		Integer.parseInt(modulo),reemplazar,
                            		parametros);
            }
            if (formato.equals(ReportesBean.FORMATOS.EXCEL)
                || formato.equals(ReportesBean.FORMATOS.EXCEL97)) {
                reemplazar.put("consultaBase", Reporteador.resuelveConsulta(
                                nombreReportC,
                                Integer.parseInt(SessionUtil.getModulo()),
                                reemplazar));
                String salida = "";
                if ((agrupado) && (fuente)) {
                    salida = Reporteador.resuelveConsulta(
                                    "000948LisEjecPptalGastosEspecial_Excel",
                                    Integer.parseInt(SessionUtil.getModulo()),
                                    reemplazar);                	
                } else if (excelPlano && entreMeses) {
                    salida = Reporteador.resuelveConsulta(
                            "8000579Rpt_Ejecucion_Presupuestal_Gastos",
                            Integer.parseInt(SessionUtil.getModulo()),
                            reemplazar);    

                    
                } else if(entreMeses) {
                	salida = Reporteador.resuelveConsulta(
                            "002770LisEjecPptalGastosEntreMeses_Excel",
                            Integer.parseInt(SessionUtil.getModulo()),
                            reemplazar);           	
                }else {
                    salida = Reporteador.resuelveConsulta(
                                    "000948LisEjecPptalGastosFL_Excel",
                                    Integer.parseInt(SessionUtil.getModulo()),
                                    reemplazar);
                }
                
                
                /*archivoDescarga = JsfUtil.exportarHojaDatosStreamed(salida,
                                ConectorPool.ESQUEMA_SYSMAN, formato,
                                nombreReportC); */
                /*-*/
                
                formato = FORMATOS.EXCEL;
        		archivoDescarga = JsfUtil.exportarHojaDatosStreamed(salida,
                        ConectorPool.ESQUEMA_SYSMAN, formato, nombreReportC);
        		
        		 workbook = new XSSFWorkbook(
        				JsfUtil.exportarHojaDatosStreamed(salida,
        						ConectorPool.ESQUEMA_SYSMAN,
        						formato).getStream());

        		Sheet sheet = workbook.getSheetAt(0);
        		
        		sheet.shiftRows(0, sheet.getLastRowNum(),2);

        		sheet.createFreezePane(0,3);

        		Font font2 = workbook.createFont();
        		font2.setFontName("SansSerif");
        		font2.setFontHeightInPoints((short)12);
        		font2.setBold(true);
        		 
        		String encabezadoExcel = "";
        		
        		if(entreMeses) {
        			encabezadoExcel = "EJECUCIÓN PRESUPUESTAL DE GASTOS \n" + idioma.getString("TB_TB4474") + " "
										+SysmanConstantes.NOMBRE_MESES_CONTABILIDAD[(mes)].toUpperCase()+" A "
										+SysmanConstantes.NOMBRE_MESES_CONTABILIDAD[(mesFinal)].toUpperCase()+" DE "+ano;
        		} else {
        			encabezadoExcel = "EJECUCION PRESUPUESTAL DE GASTOS \n" + "CORRESPONDIENTE AL "+SysmanConstantes.NOMBRE_MESES_CONTABILIDAD[(mes)].toUpperCase()+" DE  "+ano;
        		}
        			Row r = sheet.createRow(0);
        			Cell cell1 = r.createCell(0);
        			CellStyle style = r.getSheet().getWorkbook().createCellStyle();
        			cell1 = r.createCell(0);
        			cell1.setCellValue(encabezadoExcel);
        			style.setAlignment(CellStyle.ALIGN_CENTER);
        			style.setFont(font2);
        			cell1.setCellStyle(style);
     
        			sheet.addMergedRegion(new CellRangeAddress(0,0,0,4));
        			workbook.write(out);

            		archivoDescarga = JsfUtil.getArchivoDescarga(
            				new ByteArrayInputStream(out.toByteArray()), nombreReportC+".xlsx");
            		workbook.close(); /*-*/
            }
            else {

                archivoDescarga = JsfUtil.exportarStreamed(parReporte,
                                parametros,
                                ConectorPool.ESQUEMA_SYSMAN, formato);
            }
        }
        catch (JRException | IOException | SysmanException
                        | SystemException | SQLException | DRException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    
    public void cambiarCheckExcelPlano() {

    }
    
    // <METODOS_CAMBIAR>
    public void cambiarAno() {

        cuentaInicial = "0";
        cuentaFinal = "9999999999999999";
        fuenteInicial = "0";
        fuenteFinal = SysmanConstantes.CONS_FUENTE;
        cargarListaCuentaInicial();
        cargarListaFuenteInicial();
        cargarListaMes();

    }

    public void cambiarEspecial() {
        if (especial) {
            agrupado = false;
            fuenteVisible = false;
            fuenteInicial = null;
            fuenteFinal = null;
            presentarVisible = false;
        }
        else {
            presentarVisible = true;
        }
    }

    public void cambiarFuente() {

        if (fuente) {
            // agrupado = false;
            fuenteVisible = false;
            fuenteInicial = null;
            fuenteFinal = null;
        }
    }
    
    public void cambiarEntreMeses() {}

    public void cambiaragrupado() {

        if (agrupado) {
            fuenteVisible = true;
            // fuente = false;
            especial = false;
            presentarVisible = true;
        }
        else {
            fuenteVisible = false;
        }
    }

    // </METODOS_CAMBIAR>
    // <METODOS_COMBOS_GRANDES>
    public void seleccionarFilaCuentaInicial(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        cuentaInicial = SysmanFunciones
                        .nvl(registroAux.getCampos().get(codigoC), "")
                        .toString();
        cuentaFinal = null;
        cargarListaCuentaFinal();
    }

    public void seleccionarFilaCuentaFinal(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        cuentaFinal = SysmanFunciones
                        .nvl(registroAux.getCampos().get(codigoC), "")
                        .toString();

    }

    public void seleccionarFilaFuenteInicial(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        fuenteInicial = SysmanFunciones
                        .nvl(registroAux.getCampos().get(codigoC), "")
                        .toString();
        fuenteFinal = null;
        cargarListaFuenteFinal();
    }

    public void seleccionarFilaFuenteFinal(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        fuenteFinal = SysmanFunciones
                        .nvl(registroAux.getCampos().get(codigoC), "")
                        .toString();
    }

    // </METODOS_COMBOS_GRANDES>

    // <SET_GET_ATRIBUTOS>
    public boolean getEspecial() {
        return especial;
    }

    public void setEspecial(boolean especial) {
        this.especial = especial;
    }

    public boolean getAgrupado() {
        return agrupado;
    }

    public void setAgrupado(boolean agrupado) {
        this.agrupado = agrupado;
    }

    public boolean getFuente() {
        return fuente;
    }

    public void setFuente(boolean fuente) {
        this.fuente = fuente;
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

    public String getDigitos() {
        return digitos;
    }

    public void setDigitos(String digitos) {
        this.digitos = digitos;
    }

    public String getFuenteInicial() {
        return fuenteInicial;
    }

    public void setFuenteInicial(String fuenteInicial) {
        this.fuenteInicial = fuenteInicial;
    }

    public String getFuenteFinal() {
        return fuenteFinal;
    }

    public void setFuenteFinal(String fuenteFinal) {
        this.fuenteFinal = fuenteFinal;
    }

    public int getAno() {
        return ano;
    }

    public void setAno(int ano) {
        this.ano = ano;
    }

    public int getMes() {
        return mes;
    }

    public void setMes(int mes) {
        this.mes = mes;
    }
    
    public int getMesFinal() {
        return mesFinal;
    }
    
    public void setMesFinal(int mesFinal) {
        this.mesFinal = mesFinal;
    }

    public int getNivel() {
        return nivel;
    }

    public void setNivel(int nivel) {
        this.nivel = nivel;
    }

    public StreamedContent getArchivoDescarga() {
        return archivoDescarga;
    }

    public boolean isFuenteVisible() {
        return fuenteVisible;
    }

    public void setFuenteVisible(boolean fuenteVisible) {
        this.fuenteVisible = fuenteVisible;
    }

    public boolean isPresentarVisible() {
        return presentarVisible;
    }

    public void setPresentarVisible(boolean presentarVisible) {
        this.presentarVisible = presentarVisible;
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

    public List<Registro> getListaMes() {
        return listaMes;
    }

    public void setListaMes(List<Registro> listaMes) {
        this.listaMes = listaMes;
    }
    
    public List<Registro> getListaMesFinal() {
        return listaMesFinal;
    }
    
    public void setListaMesFinal(List<Registro> listaMesFinal) {
        this.listaMesFinal = listaMesFinal;
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

    public RegistroDataModelImpl getListaFuenteInicial() {
        return listaFuenteInicial;
    }

    public void setListaFuenteInicial(
        RegistroDataModelImpl listaFuenteInicial) {
        this.listaFuenteInicial = listaFuenteInicial;
    }

    public RegistroDataModelImpl getListaFuenteFinal() {
        return listaFuenteFinal;
    }

    public void setListaFuenteFinal(RegistroDataModelImpl listaFuenteFinal) {
        this.listaFuenteFinal = listaFuenteFinal;
    }

    public boolean isResumido() {
        return resumido;
    }

    public void setResumido(boolean resumido) {
        this.resumido = resumido;
    }
    
    public boolean isConId() {
        return conId;
    }

    public void setConId(boolean conId) {
        this.conId = conId;
    }
    
    public boolean isEntreMeses() {
        return entreMeses;
    }

    public void setEntreMeses(boolean entreMeses) {
        this.entreMeses = entreMeses;
    }

	/**
	 * @return the excelPlano
	 */
	public boolean isExcelPlano() {
		return excelPlano;
	}

	/**
	 * @param excelPlano the excelPlano to set
	 */
	public void setExcelPlano(boolean excelPlano) {
		this.excelPlano = excelPlano;
	}

    // </SET_GET_LISTAS_COMBO_GRANDE>
    
}
