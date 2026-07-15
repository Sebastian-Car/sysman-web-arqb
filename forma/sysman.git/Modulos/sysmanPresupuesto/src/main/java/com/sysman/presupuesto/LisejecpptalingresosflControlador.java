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
import com.sysman.presupuesto.enums.LisejecpptalingresosflControladorEnum;
import com.sysman.presupuesto.enums.LisejecpptalingresosflControladorUrlEnum;
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
 * @author dsuesca
 * @version 1, 01/07/2016
 *
 * @author eamaya
 * @version 2, 19/04/2017, Proceso de Refactoring
 *
 */
@ManagedBean
@ViewScoped

public class LisejecpptalingresosflControlador extends BeanBaseModal {
    private final String compania;

    private final String strCodigo;

    // <DECLARAR_ATRIBUTOS>
    private Boolean agrupado;
    private String cuentaInicial;
    private String cuentaFinal;
    private String fuenteInicial;
    private String fuenteFinal;
    private String anio;
    private String mes;
    private String mesFinal;
    private StreamedContent archivoDescarga;
    // </DECLARAR_ATRIBUTOS>
    // <DECLARAR_PARAMETROS>
    private String firmaEjecucion1;
    private String firmaEjecucion2;
    private String firmaEjecucion3;
    private String cargoEjecucion1;
    private String cargoEjecucion2;
    private String cargoEjecucion3;
    private boolean manejaAuxiliar;
    private boolean resumido;
    private boolean entreMeses;
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

    private EjbSysmanUtilRemote ejbParametro;

    /**
     * Creates a new instance of LisejecpptalingresosflControlador
     */
    public LisejecpptalingresosflControlador() {
        super();
        compania = SessionUtil.getCompania();

        strCodigo = GeneralParameterEnum.CODIGO.getName();

        try {
            numFormulario = GeneralCodigoFormaEnum.LISEJECPPTALINGRESOSFL_CONTROLADOR
                            .getCodigo();
            validarPermisos();
            // <INI_ADICIONAL>
            anio = String.valueOf(SysmanFunciones.ano(new Date()));
            mes = String.valueOf(SysmanFunciones.mes(new Date()));
            mesFinal = String.valueOf(SysmanFunciones.mes(new Date()));
            iniciarDefecto();
            agrupado = false;
            // </INI_ADICIONAL>
        }
        catch (Exception ex) {
            Logger.getLogger(LisejecpptalingresosflControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            SessionUtil.redireccionarMenuPermisos();
        }

    }

    private void iniciarDefecto() {
        cuentaInicial = SysmanConstantes.DEFECTOINICIAL_STRING;
        cuentaFinal = SysmanConstantes.DEFECTOFINAL_STRING;
        fuenteInicial = SysmanConstantes.DEFECTOINICIAL_STRING;
        fuenteFinal = SysmanConstantes.DEFECTOFINAL_STRING;
    }

    /**
     * Realiza la consulta de los parametros que definen los valores a
     * visualizar en las firmas ubicadas en el pie del informe
     * 000970LisEjecPptalIngresosFL
     */
    public void cargarParametros() {
        try {
            firmaEjecucion1 = SysmanFunciones
                            .nvl(ejbParametro.consultarParametro(compania,
                                            "FIRMA EJECUCION 1",
                                            SessionUtil.getModulo(), new Date(),
                                            false),
                                            "")
                            .toString();
            firmaEjecucion2 = SysmanFunciones
                            .nvl(ejbParametro.consultarParametro(compania,
                                            "FIRMA EJECUCION 2",
                                            SessionUtil.getModulo(), new Date(),
                                            false),
                                            "")
                            .toString();
            firmaEjecucion3 = SysmanFunciones
                            .nvl(ejbParametro.consultarParametro(compania,
                                            "FIRMA EJECUCION 3",
                                            SessionUtil.getModulo(), new Date(),
                                            false),
                                            "")
                            .toString();
            cargoEjecucion1 = SysmanFunciones
                            .nvl(ejbParametro.consultarParametro(compania,
                                            "CARGO EJECUCION 1",
                                            SessionUtil.getModulo(), new Date(),
                                            false),
                                            "")
                            .toString();
            cargoEjecucion2 = SysmanFunciones
                            .nvl(ejbParametro.consultarParametro(compania,
                                            "CARGO EJECUCION 2",
                                            SessionUtil.getModulo(), new Date(),
                                            false),
                                            "")
                            .toString();

            cargoEjecucion3 = SysmanFunciones
                            .nvl(ejbParametro.consultarParametro(compania,
                                            "CARGO EJECUCION 3",
                                            SessionUtil.getModulo(), new Date(),
                                            false),
                                            "")
                            .toString();
            manejaAuxiliar = "SI".equals(SysmanFunciones
                            .nvl(ejbParametro.consultarParametro(compania,
                                            "MANEJA AUXILIAR POR FUENTE EN PRESUPUESTO",
                                            SessionUtil.getModulo(),
                                            new Date(), true), "NO"));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    @PostConstruct
    public void inicializar() {
        // <CARGAR_LISTA>
        cargarListaAno();
        cargarListaMes();
        cargarListaMesFinal();
        // </CARGAR_LISTA>
        // <CARGAR_LISTA_COMBO_GRANDE>
        cargarListaCuentaInicial();
        cargarListaCuentaFinal();
        cargarListaFuenteInicial();
        cargarListaFuenteFinal();
        // </CARGAR_LISTA_COMBO_GRANDE>
        abrirFormulario();
    }

    @Override
    public void abrirFormulario() {
        // <CODIGO_DESARROLLADO>
        /*
         * FR967-AL_ABRIR Private Sub Form_Open(Cancel As Integer)
         * DoCmd.Restore VarExcel2 = False End Sub
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
                                                            LisejecpptalingresosflControladorUrlEnum.URL7664
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    public void cargarListaMes() {

        try {

            Map<String, Object> param = new TreeMap<>();
            param.put(GeneralParameterEnum.COMPANIA.getName(),
                            compania);
            param.put(GeneralParameterEnum.ANO.getName(),
                            anio);

            listaMes = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            LisejecpptalingresosflControladorUrlEnum.URL9268
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }
    
    public void cargarListaMesFinal() {
        try {
            Map<String, Object> param = new TreeMap<>();
            param.put(GeneralParameterEnum.COMPANIA.getName(),compania);
            param.put(GeneralParameterEnum.ANO.getName(),anio);

            listaMesFinal = RegistroConverter.toListRegistro(
                            	 requestManager.getList(UrlServiceUtil.getInstance()
                                                 .getUrlServiceByUrlByEnumID(
                                                                 LisejecpptalingresosflControladorUrlEnum.URL9268.getValue())
                                                 .getUrl(), param));
        } catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    public void cargarListaCuentaInicial() {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        LisejecpptalingresosflControladorUrlEnum.URL9889
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
        param.put(GeneralParameterEnum.ANO.getName(),
                        anio);
        param.put(LisejecpptalingresosflControladorEnum.PARAM1.getValue(),
                        "C");

        listaCuentaInicial = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, "ID");
    }

    public void cargarListaCuentaFinal() {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        LisejecpptalingresosflControladorUrlEnum.URL10828
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
        param.put(LisejecpptalingresosflControladorEnum.PARAM3.getValue(),
                        cuentaInicial);
        param.put(GeneralParameterEnum.ANO.getName(),
                        anio);
        param.put(LisejecpptalingresosflControladorEnum.PARAM1.getValue(),
                        "C");

        listaCuentaFinal = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, "ID");

    }

    public void cargarListaFuenteInicial() {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        LisejecpptalingresosflControladorUrlEnum.URL11906
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
        param.put(GeneralParameterEnum.ANO.getName(),
                        anio);

        listaFuenteInicial = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, strCodigo);
    }

    public void cargarListaFuenteFinal() {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        LisejecpptalingresosflControladorUrlEnum.URL12612
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
        param.put(LisejecpptalingresosflControladorEnum.PARAM8.getValue(),
                        fuenteInicial);
        param.put(GeneralParameterEnum.ANO.getName(),
                        anio);

        listaFuenteFinal = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, strCodigo);
    }

    // </METODOS_CARGAR_LISTA>
    // <METODOS_BOTONES>
    public void oprimirPDF() {
        // <CODIGO_DESARROLLADO>
        archivoDescarga = null;
        if (validarCampos()) {

            getInforme(FORMATOS.PDF);
        }
        else {
            JsfUtil.agregarMensajeInformativo(idioma.getString("TB_TB201"));
        }
        // </CODIGO_DESARROLLADO>
    }

    public void oprimirExcel() {
        // <CODIGO_DESARROLLADO>
        archivoDescarga = null;
        if (validarCampos()) {

            getInforme(FORMATOS.EXCEL97);
        }
        else {
            JsfUtil.agregarMensajeInformativo(idioma.getString("TB_TB201"));
        }

        // </CODIGO_DESARROLLADO>
    }

    public void getInforme(FORMATOS formato) {

        try {
            HashMap<String, Object> reemplazar = new HashMap<>();
            Workbook workbook;
            String consultaexcel;
            String nombreexcel;
            ByteArrayOutputStream out;
            out = new ByteArrayOutputStream();
            cargarParametros();
            String reporte = "000970LisEjecPptalIngresosFL";
            String consulta;
            consulta = reporte;
            String encabezado = "";
            String representanteLegal = "";
            String firmaRepLegal = "";
            String secretariaHacienda = "";
            if (manejaAuxiliar) {
                reporte = "000970LisEjecPptalIngresosFLFuente";
            } 
            if (resumido) {
                consulta = "003007LisEjecPptalIngresosFLResumido";
            }
            if (entreMeses) {
            	reporte = "002781EjecPptalIngrEntreMeses";
            	consulta = reporte;
            	encabezado = "DESDE "
		   					 + SysmanConstantes.NOMBRE_MESES_CONTABILIDAD[Integer.parseInt(mes)].toUpperCase()
		                     + " HASTA " + SysmanConstantes.NOMBRE_MESES_CONTABILIDAD[Integer.parseInt(mesFinal)].toUpperCase() 
		                     + " DE " + anio;
            }            
            
            reemplazar.put("anio", anio);
            reemplazar.put("cuentaInicial", cuentaInicial);
            reemplazar.put("cuentaFinal", cuentaFinal);
            reemplazar.put("fuenteInicial", fuenteInicial);
            reemplazar.put("fuenteFinal", fuenteFinal);
            reemplazar.put("mes", mes);
            reemplazar.put("mesFinal",mesFinal);
            // MANEJO DE PARAMETROS DE REEMPLAZO
            Map<String, Object> parametros = new HashMap<>();

            // MANEJO DE PARAMETROS DEL REPORTE
            if(formato.equals(FORMATOS.PDF)) {
            	 Reporteador.resuelveConsulta(consulta,
                         Integer.parseInt(SessionUtil.getModulo()),
                         reemplazar, parametros);
            }else {
            	formato = FORMATOS.EXCEL;
            	  
            	consultaexcel = "000970LisEjecPptalIngresosFL";
            	nombreexcel = "000970LisEjecPptalIngresosFL";
            	 if (manejaAuxiliar) {
            		 consultaexcel = "000970LisEjecPptalIngresosFL_Excel";
            		 nombreexcel = "000970LisEjecPptalIngresosFLFuente";
                 } 
                 if (resumido) {
                	 consultaexcel = "800631LisEjecPptalIngresosFL_Excel_Resumido";
                	 nombreexcel = "003007LisEjecPptalIngresosFLResumido";
                 }
                 if (entreMeses) {
                	 consultaexcel = "800715EjecPptalIngrEntreMeses";
                	 nombreexcel = "800715EjecPptalIngrEntreMeses";
                 }
            	reemplazar.put("compania", "'"+compania+"'");
            	reemplazar.put("consultaBase",
            			Reporteador.resuelveConsulta("002781EjecPptalIngrEntreMeses",
            					Integer.parseInt(SessionUtil.getModulo()),
            					reemplazar));
            	
            String sql = Reporteador.resuelveConsulta(consultaexcel,reemplazar);
            
    		archivoDescarga = JsfUtil.exportarHojaDatosStreamed(sql,
                    ConectorPool.ESQUEMA_SYSMAN, formato, consultaexcel);
    		
    		 workbook = new XSSFWorkbook(
    				JsfUtil.exportarHojaDatosStreamed(sql,
    						ConectorPool.ESQUEMA_SYSMAN,
    						formato).getStream());

    		Sheet sheet = workbook.getSheetAt(0);
    		
    		sheet.shiftRows(0, sheet.getLastRowNum(),2);

    		sheet.createFreezePane(0,3);

    		Font font2 = workbook.createFont();
    		font2.setFontName("SansSerif");
    		font2.setFontHeightInPoints((short)12);
    		font2.setBold(true);
    		 
    			Row r = sheet.createRow(0);
    			Cell cell1 = r.createCell(0);
    			CellStyle style = r.getSheet().getWorkbook().createCellStyle();
    			cell1 = r.createCell(0);
    			if(entreMeses) {
    				cell1.setCellValue("EJECUCION PRESUPUESTAL DE INGRESOS \n" + encabezado);
    			} else {
    				cell1.setCellValue("EJECUCION PRESUPUESTAL DE INGRESOS\n" + "CORRESPONDIENTE AL "+SysmanConstantes.NOMBRE_MESES_CONTABILIDAD[Integer.parseInt(mes)].toUpperCase()+" DE  "+anio);
    			}
    			style.setAlignment(CellStyle.ALIGN_CENTER);
    			style.setFont(font2);
    			cell1.setCellStyle(style);
 
    			sheet.addMergedRegion(new CellRangeAddress(0,0,0,2));
    			workbook.write(out);

        		archivoDescarga = JsfUtil.getArchivoDescarga(
        				new ByteArrayInputStream(out.toByteArray()), nombreexcel+".xlsx");
        		workbook.close(); 
            }
			try {
				representanteLegal = SysmanFunciones.nvl(ejbParametro.consultarParametro(compania,
										"NOMBRE REPRESENTANTE LEGAL",
										SessionUtil.getModulo(),new Date(), true)," ").toString();
				
				firmaRepLegal = SysmanFunciones.nvl(ejbParametro.consultarParametro(compania,
										"FIRMA REPRESENTANTE LEGAL",
										SessionUtil.getModulo(),new Date(), true)," ").toString();
				
				secretariaHacienda = SysmanFunciones.nvl(ejbParametro.consultarParametro(compania,
										"NOMBRE DE SECRETARIA DE HACIENDA",
										SessionUtil.getModulo(),new Date(), true)," ").toString();
			} catch(SystemException e) {
				e.printStackTrace();
			}
			
            parametros.put("PR_NOMBREDEMES",
                            SysmanConstantes.NOMBRE_MESES_CONTABILIDAD[Integer
                                            .parseInt(mes)].toUpperCase());
            parametros.put("PR_ANO", anio);
            parametros.put("PR_CARGO_EJECUCION_3", cargoEjecucion3);
            parametros.put("PR_CARGO_EJECUCION_1", cargoEjecucion1);
            parametros.put("PR_FIRMA_EJECUCION_2", firmaEjecucion2);
            parametros.put("PR_CARGO_EJECUCION_2", cargoEjecucion2);
            parametros.put("PR_FIRMA_EJECUCION_3", firmaEjecucion3);
            parametros.put("PR_FIRMA_EJECUCION_1", firmaEjecucion1);
            parametros.put("PR_NITCOMPANIA",SessionUtil.getCompaniaIngreso().getNit());
            parametros.put("PR_NOMBRECOMPANIA",SessionUtil.getCompaniaIngreso().getNombre());
            parametros.put("PR_ENCABEZADO",encabezado);
            parametros.put("PR_NOMBRE_REPRESENTANTE_LEGAL",representanteLegal);
            parametros.put("PR_FIRMA_REPRESENTANTE_LEGAL",firmaRepLegal);
            parametros.put("PR_NOMBRE_DE_SECRETARIA_DE_HACIENDA",secretariaHacienda);
            
            if(formato.equals(FORMATOS.PDF)) {
            	archivoDescarga = JsfUtil.exportarStreamed(reporte, parametros,
                        ConectorPool.ESQUEMA_SYSMAN, formato);
            }
            

        }
        catch (JRException | IOException | SysmanException | SQLException | DRException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    public boolean validarCampos() {

        boolean antes = SysmanFunciones.validarVariableVacio(fuenteInicial)
            || SysmanFunciones.validarVariableVacio(fuenteFinal);

        return !(agrupado && antes);
    }

    // </METODOS_BOTONES>
    // <METODOS_CAMBIAR>
    public void cambiarAno() {
        cargarListaMes();
        cargarListaMesFinal();
        cargarListaFuenteInicial();
        cargarListaFuenteFinal();
        cargarListaCuentaInicial();
        cargarListaCuentaFinal();
        iniciarDefecto();
    }

    public void cambiarEntreMeses() {}
    
    public void cambiaragrupado() {
        // <CODIGO_DESARROLLADO>

        // </CODIGO_DESARROLLADO>
    }

    // </METODOS_CAMBIAR>
    // <METODOS_COMBOS_GRANDES>
    public void seleccionarFilaCuentaInicial(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        cuentaInicial = SysmanFunciones
                        .nvl(registroAux.getCampos().get("ID"), " ").toString();
        cuentaFinal = null;
        cargarListaCuentaFinal();
    }

    public void seleccionarFilaCuentaFinal(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        cuentaFinal = SysmanFunciones
                        .nvl(registroAux.getCampos().get("ID"), " ").toString();
    }

    public void seleccionarFilaFuenteInicial(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        fuenteInicial = SysmanFunciones
                        .nvl(registroAux.getCampos().get(strCodigo), " ")
                        .toString();
        fuenteFinal = null;
        cargarListaFuenteFinal();
    }

    public void seleccionarFilaFuenteFinal(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        fuenteFinal = SysmanFunciones
                        .nvl(registroAux.getCampos().get(strCodigo), " ")
                        .toString();
    }

    // </METODOS_COMBOS_GRANDES>

    // <SET_GET_ATRIBUTOS>
    public Boolean getAgrupado() {
        return agrupado;
    }

    public void setAgrupado(Boolean agrupado) {
        this.agrupado = agrupado;
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

    public String getAnio() {
        return anio;
    }

    public void setAnio(String anio) {
        this.anio = anio;
    }

    public String getMes() {
        return mes;
    }

    public void setMes(String mes) {
        this.mes = mes;
    }
    
    public String getMesFinal() {
        return mesFinal;
    }
    
    public void setMesFinal(String mesFinal) {
        this.mesFinal = mesFinal;
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
    
    public boolean isEntreMeses() {
        return entreMeses;
    }

    public void setEntreMeses(boolean entreMeses) {
        this.entreMeses = entreMeses;
    }

    // </SET_GET_LISTAS_COMBO_GRANDE>
}
