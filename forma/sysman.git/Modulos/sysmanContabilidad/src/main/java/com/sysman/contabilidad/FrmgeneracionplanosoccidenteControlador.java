/*-
 * FrmgeneracionplanosoccidenteControlador.java
 *
 * 1.0
 * 
 * 12/07/2025
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */
package com.sysman.contabilidad;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
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

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.primefaces.event.SelectEvent;
import com.sysman.beanbase.BeanBaseModal;
import com.sysman.contabilidad.enums.GeneracionplanosbancosControladorUrlEnum;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.jsfutil.ReportesBean;
import com.sysman.jsfutil.ReportesBean.FORMATOS;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.persistencia.ConectorPool;
import com.sysman.reportes.Reporteador;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.util.ContenedorArchivo;
import com.sysman.util.SysmanConstantes;
import com.sysman.util.SysmanFunciones;

import net.sf.jasperreports.engine.JRException;

import org.primefaces.model.StreamedContent;

/**
 * TODO Ingrese una descripcion para la clase.
 *
 * @version 1.0, 12/07/2025
 * @author SYSMAN
 */
@ManagedBean
@ViewScoped
public class FrmgeneracionplanosoccidenteControlador extends BeanBaseModal {
	/**
	 * Constante a nivel de clase que almacena el codigo de la compania en la cual
	 * inicio sesion el usuario, el valor de esta constante es asignado en el
	 * constructor a la variable de sesion correspondiente
	 */
	private final String compania;
//<DECLARAR_ATRIBUTOS>
	/**
	 * TODO DOCUMENTACION NECESARIA
	 */
	private String numeroBanco;
	/**
	 * TODO DOCUMENTACION NECESARIA
	 */
	private String ano;
	/**
	 * TODO DOCUMENTACION NECESARIA
	 */
	private String tipoEgreso;
	/**
	 * TODO DOCUMENTACION NECESARIA
	 */
	private String egresoInicial;
	/**
	 * TODO DOCUMENTACION NECESARIA
	 */
	private String egresoFinal;
	/**
	 * TODO DOCUMENTACION NECESARIA
	 */
	private String terceroInicial;
	/**
	 * TODO DOCUMENTACION NECESARIA
	 */
	private String terceroFinal;
	/**
	 * TODO DOCUMENTACION NECESARIA
	 */
	private String cuentaInicial;
	/**
	 * TODO DOCUMENTACION NECESARIA
	 */
	private String cuentaFinal;
	/**
	 * TODO DOCUMENTACION NECESARIA
	 */
	private Date fechaPago;	
	/**
     * Variable que almacena el valor de cuentaClienteDebitar
     */
    private String cuentaClienteDebitar;
	/**
	 * Atributo usado para descargar contenidos de archivos desde la vista
	 */
	private StreamedContent archivoDescarga;
	private ContenedorArchivo contArchivocargarArchivoPagos;
//</DECLARAR_ATRIBUTOS>
//<DECLARAR_PARAMETROS>
//</DECLARAR_PARAMETROS>
//<DECLARAR_LISTAS>
	/**
	 * TODO DOCUMENTACION NECESARIA
	 */
	private List<Registro> listacmbAno;
//</DECLARAR_LISTAS>
//<DECLARAR_LISTAS_COMBO_GRANDE>
	/**
	 * TODO DOCUMENTACION NECESARIA
	 */
	private RegistroDataModelImpl listacmbNumeroBanco;
	/**
	 * TODO DOCUMENTACION NECESARIA
	 */
	private RegistroDataModelImpl listacmbTipo;
	/**
	 * TODO DOCUMENTACION NECESARIA
	 */
	private RegistroDataModelImpl listacmbNumeroInicial;
	/**
	 * TODO DOCUMENTACION NECESARIA
	 */
	private RegistroDataModelImpl listacmbNumeroFinal;
	/**
	 * TODO DOCUMENTACION NECESARIA
	 */
	private RegistroDataModelImpl listacmbTerceroInicial;
	/**
	 * TODO DOCUMENTACION NECESARIA
	 */
	private RegistroDataModelImpl listacmbTerceroFinal;
	/**
	 * TODO DOCUMENTACION NECESARIA
	 */
	private RegistroDataModelImpl listacmbCuentaInicial;
	/**
	 * TODO DOCUMENTACION NECESARIA
	 */
	private RegistroDataModelImpl listacmbCuentaFinal;

//</DECLARAR_LISTAS_COMBO_GRANDE>
	/**
	 * Crea una nueva instancia de FrmgeneracionplanosoccidenteControlador
	 */
	public FrmgeneracionplanosoccidenteControlador() {
		super();
		compania = SessionUtil.getCompania();
		numeroBanco = "23";
		tipoEgreso = "EGR";
		ano = SysmanFunciones.toString(SysmanFunciones.ano(new Date()));
        fechaPago = new Date();
        egresoInicial = "1";
        egresoFinal = SysmanConstantes.CONS_REFERENCIA;
        cuentaInicial = "1";
        cuentaFinal = SysmanConstantes.CONS_MAX_ID;
        terceroInicial = "1";
        terceroFinal = SysmanConstantes.CONS_REFERENCIA;
		try {
			contArchivocargarArchivoPagos = new ContenedorArchivo();
			numFormulario = GeneralCodigoFormaEnum.FRM_GENERACION_PLANOS_OCCIDENTE_CONTROLADOR.getCodigo();
			validarPermisos();
//<INI_ADICIONAL>
//</INI_ADICIONAL>
		} catch (Exception ex) {
			logger.error(ex.getMessage(), ex);
			SessionUtil.redireccionarMenuPermisos();
		} finally {
		}
	}

	/**
	 * Este metodo se ejecuta justo despues de que el objeto de la clase del Bean ha
	 * sido creado, en este se realizan las asignaciones iniciales necesarias para
	 * la visualizacion del formulario, como son tablas, origenes de datos,
	 * inicializacion de listas y demas necesarios
	 */
	@PostConstruct
	public void inicializar() {
//<CARGAR_LISTA>
		cargarListacmbAno();
//</CARGAR_LISTA>
//<CARGAR_LISTA_COMBO_GRANDE>
		cargarListacmbNumeroBanco();
		cargarListacmbTipo();
		cargarListacmbNumeroInicial();
		cargarListacmbNumeroFinal();
		cargarListacmbTerceroInicial();
		cargarListacmbTerceroFinal();
		cargarListacmbCuentaInicial();
		cargarListacmbCuentaFinal();
//</CARGAR_LISTA_COMBO_GRANDE>
//<CREAR_ARBOLES>
//</CREAR_ARBOLES>
		abrirFormulario();
	}

	/**
	 * Este metodo es invocado el metodo inicializar, se ejecutan las acciones a
	 * tener en cuenta en el momento de apertura del formulario
	 */
	@Override
	public void abrirFormulario() {
		// <CODIGO_DESARROLLADO>
		// </CODIGO_DESARROLLADO>
	}

//<METODOS_CARGAR_LISTA>
	/**
	 * 
	 * Carga la lista listacmbAno
	 *
	 * TODO DOCUMENTACION ADICIONAL
	 */
	public void cargarListacmbAno() {
		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

		try {
			listacmbAno = RegistroConverter
					.toListRegistro(requestManager.getList(
							UrlServiceUtil.getInstance()
									.getUrlServiceByUrlByEnumID(
											GeneracionplanosbancosControladorUrlEnum.URL002.getValue())
									.getUrl(),
							param));
		} catch (SystemException e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}
	}

	/**
	 * 
	 * Carga la lista listacmbNumeroBanco
	 *
	 * TODO DOCUMENTACION ADICIONAL
	 */
	public void cargarListacmbNumeroBanco() {
		UrlBean urlBean = UrlServiceUtil.getInstance()
                .getUrlServiceByUrlByEnumID(GeneracionplanosbancosControladorUrlEnum.URL001.getValue());
		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		
		listacmbNumeroBanco = new RegistroDataModelImpl(
		                urlBean.getUrl(),
		                urlBean.getUrlConteo().getUrl(), param,
		                true,
		                GeneralParameterEnum.BANCO.getName());
	}

	/**
	 * 
	 * Carga la lista listacmbTipo
	 *
	 * TODO DOCUMENTACION ADICIONAL
	 */
	public void cargarListacmbTipo() {
		UrlBean urlBean = UrlServiceUtil.getInstance()
                .getUrlServiceByUrlByEnumID(GeneracionplanosbancosControladorUrlEnum.URL005.getValue());
		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		
		listacmbTipo = new RegistroDataModelImpl(
		                urlBean.getUrl(),
		                urlBean.getUrlConteo().getUrl(), param,
		                true,
		                GeneralParameterEnum.CODIGO.getName());
	}

	/**
	 * 
	 * Carga la lista listacmbNumeroInicial
	 *
	 * TODO DOCUMENTACION ADICIONAL
	 */
	public void cargarListacmbNumeroInicial() {
		UrlBean urlBean = UrlServiceUtil.getInstance()
                .getUrlServiceByUrlByEnumID(GeneracionplanosbancosControladorUrlEnum.URL003.getValue());
		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		param.put(GeneralParameterEnum.ANO.getName(), ano);
		param.put(GeneralParameterEnum.TIPOEGRESO.getName(), tipoEgreso);
		
		listacmbNumeroInicial = new RegistroDataModelImpl(
		                urlBean.getUrl(),
		                urlBean.getUrlConteo().getUrl(), param,
		                true,
		                GeneralParameterEnum.NUMERO.getName());
	}

	/**
	 * 
	 * Carga la lista listacmbNumeroFinal
	 *
	 * TODO DOCUMENTACION ADICIONAL
	 */
	public void cargarListacmbNumeroFinal() {
		UrlBean urlBean = UrlServiceUtil.getInstance()
                .getUrlServiceByUrlByEnumID(GeneracionplanosbancosControladorUrlEnum.URL004.getValue());
		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		param.put(GeneralParameterEnum.ANO.getName(), ano);
		param.put(GeneralParameterEnum.TIPOEGRESO.getName(), tipoEgreso);
		param.put(GeneralParameterEnum.NUMEROINICIAL.getName(), egresoInicial);
		
		listacmbNumeroFinal = new RegistroDataModelImpl(
		                urlBean.getUrl(),
		                urlBean.getUrlConteo().getUrl(), param,
		                true,
		                GeneralParameterEnum.NUMERO.getName());
	}

	/**
	 * 
	 * Carga la lista listacmbTerceroInicial
	 *
	 * TODO DOCUMENTACION ADICIONAL
	 */
	public void cargarListacmbTerceroInicial() {
		UrlBean urlBean = UrlServiceUtil.getInstance()
                .getUrlServiceByUrlByEnumID(GeneracionplanosbancosControladorUrlEnum.URL006.getValue());
		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		
		listacmbTerceroInicial = new RegistroDataModelImpl(
		                urlBean.getUrl(),
		                urlBean.getUrlConteo().getUrl(), param,
		                true, GeneralParameterEnum.NIT.getName());
	}

	/**
	 * 
	 * Carga la lista listacmbTerceroFinal
	 *
	 * TODO DOCUMENTACION ADICIONAL
	 */
	public void cargarListacmbTerceroFinal() {
		UrlBean urlBean = UrlServiceUtil.getInstance()
                .getUrlServiceByUrlByEnumID(GeneracionplanosbancosControladorUrlEnum.URL007.getValue());
		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		param.put(GeneralParameterEnum.TERCEROINICIAL.getName(), terceroInicial);
		
		listacmbTerceroFinal = new RegistroDataModelImpl(
		                urlBean.getUrl(),
		                urlBean.getUrlConteo().getUrl(), param,
		                true, GeneralParameterEnum.NIT.getName());
	}

	/**
	 * 
	 * Carga la lista listacmbCuentaInicial
	 *
	 * TODO DOCUMENTACION ADICIONAL
	 */
	public void cargarListacmbCuentaInicial() {
		UrlBean urlBean = UrlServiceUtil.getInstance()
                .getUrlServiceByUrlByEnumID(GeneracionplanosbancosControladorUrlEnum.URL008.getValue());
		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		param.put(GeneralParameterEnum.ANO.getName(), ano);
		
		listacmbCuentaInicial = new RegistroDataModelImpl(
		                urlBean.getUrl(),
		                urlBean.getUrlConteo().getUrl(), param,
		                true, GeneralParameterEnum.CODIGO.getName());
	}

	/**
	 * 
	 * Carga la lista listacmbCuentaFinal
	 *
	 * TODO DOCUMENTACION ADICIONAL
	 */
	public void cargarListacmbCuentaFinal() {
		UrlBean urlBean = UrlServiceUtil.getInstance()
                .getUrlServiceByUrlByEnumID(GeneracionplanosbancosControladorUrlEnum.URL009.getValue());
		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		param.put(GeneralParameterEnum.ANO.getName(), ano);
		param.put(GeneralParameterEnum.CUENTAINICIAL.getName(), cuentaInicial);
		
		listacmbCuentaFinal = new RegistroDataModelImpl(
		                urlBean.getUrl(),
		                urlBean.getUrlConteo().getUrl(), param,
		                true, GeneralParameterEnum.CODIGO.getName());
	}

//</METODOS_CARGAR_LISTA>
//<METODOS_BOTONES>
	/**
	 * 
	 * Metodo ejecutado al oprimir el boton btnGenerar en la vista
	 *
	 * TODO DOCUMENTACION ADICIONAL
	 *
	 */
	public void oprimirbtnGenerar() {
		// <CODIGO_DESARROLLADO>
		archivoDescarga = null;
		if (contArchivocargarArchivoPagos.getArchivo() == null) {
            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB470"));
            return;
        }
		try (Workbook workbook = new HSSFWorkbook(new FileInputStream(
                contArchivocargarArchivoPagos.getArchivo()))) 
		{

		    Sheet sheet = workbook.getSheet("Pagos");
		    CellStyle baseStyle = workbook.createCellStyle();	
		    baseStyle.setBorderTop(CellStyle.BORDER_THIN);
		    baseStyle.setTopBorderColor(IndexedColors.BLACK.getIndex());
		    baseStyle.setBorderLeft(CellStyle.BORDER_THIN);
		    baseStyle.setLeftBorderColor(IndexedColors.BLACK.getIndex());
		    baseStyle.setBorderRight(CellStyle.BORDER_THIN);
		    baseStyle.setRightBorderColor(IndexedColors.BLACK.getIndex());
		    baseStyle.setAlignment(CellStyle.ALIGN_RIGHT);
		    baseStyle.setBorderBottom(CellStyle.BORDER_THIN);
		    baseStyle.setBottomBorderColor(IndexedColors.BLACK.getIndex());
		    Font font = workbook.createFont();
		    font.setFontHeightInPoints((short) 10);
		    font.setFontName("Arial");
		    baseStyle.setFont(font);
		    
		    CellStyle style = workbook.createCellStyle();
		    style.cloneStyleFrom(baseStyle);
		    style.setAlignment(HorizontalAlignment.RIGHT);
		    
		    CellStyle style1 = workbook.createCellStyle();
		    style1.cloneStyleFrom(baseStyle);
		    style1.setAlignment(HorizontalAlignment.LEFT);
		    
		    CellStyle style2 = workbook.createCellStyle();
		    style2.cloneStyleFrom(baseStyle);
		    style2.setAlignment(HorizontalAlignment.CENTER);
		    
		    CellStyle dateStyle = workbook.createCellStyle();
		    dateStyle.cloneStyleFrom(baseStyle);
		    dateStyle.setAlignment(HorizontalAlignment.CENTER);
		    dateStyle.setDataFormat(workbook.createDataFormat().getFormat("00"));
			
		    CellStyle numericStyle = workbook.createCellStyle();
			numericStyle.cloneStyleFrom(baseStyle);
			numericStyle.setAlignment(HorizontalAlignment.RIGHT);
			numericStyle.setDataFormat(workbook.createDataFormat().getFormat("###,##0.00#"));

			CellStyle formatStyle = workbook.createCellStyle();
			formatStyle.cloneStyleFrom(baseStyle);
			formatStyle.setAlignment(HorizontalAlignment.CENTER);
			formatStyle.setDataFormat(workbook.createDataFormat().getFormat("000-00000-0"));
		
		    if (sheet == null) {
		        JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB857"));
		        return;
		    }         
		                
		    Cell cellAnio;
		    cellAnio = sheet.getRow(12).createCell(1);
            cellAnio.setCellValue(LocalDate.now().getYear());
            
            Cell cellMes;
            String mesActual = String.format("%02d" ,LocalDate.now().getMonthValue());			
            cellMes = sheet.getRow(12).createCell(2);            
            cellMes.setCellValue(mesActual);
            
            Cell cellDia;
            String diaActual = String.format("%02d" ,LocalDate.now().getDayOfMonth());
            cellDia = sheet.getRow(12).createCell(3);
            cellDia.setCellValue(diaActual);
            
            UrlBean urlLista = UrlServiceUtil.getInstance()
                    .getUrlServiceByUrlByEnumID(GeneracionplanosbancosControladorUrlEnum.URL010.getValue());
            
            SimpleDateFormat formatFecha = new SimpleDateFormat("dd/MM/yyyy");
            Map<String, Object> param = new TreeMap<>();
            param.put(GeneralParameterEnum.COMPANIA.name(), compania);
            param.put(GeneralParameterEnum.ANO.name(), ano);
            param.put(GeneralParameterEnum.TIPOEGRESO.name(), tipoEgreso);
            param.put(GeneralParameterEnum.CUENTAINICIAL.name(), cuentaInicial);
            param.put(GeneralParameterEnum.CUENTAFINAL.name(), cuentaFinal);
            param.put(GeneralParameterEnum.EGRESOINICIAL.name(), egresoInicial);
            param.put(GeneralParameterEnum.EGRESOFINAL.name(), egresoFinal);
            param.put(GeneralParameterEnum.TERCEROINICIAL.name(), terceroInicial);
            param.put(GeneralParameterEnum.TERCEROFINAL.name(), terceroFinal);
            param.put(GeneralParameterEnum.FECHA.name(), formatFecha.format(fechaPago));
            
            List<Registro> rs = RegistroConverter.toListRegistro(
                                    requestManager.getList(urlLista.getUrl(), param));
            if (rs.isEmpty()) 
            {
            	 JsfUtil.agregarMensajeInformativo(idioma.getString("TB_TB4255"));
            	 return;
            }
                        
            int contFila = 20;
            for (Registro registro : rs) 
            {            	
            	Cell cellCtaOrigen;
            	cellCtaOrigen = sheet.getRow(contFila).createCell(0);
            	String ctaOrigen = SysmanFunciones.nvl(registro.getCampos().get(GeneralParameterEnum.CUENTAORIGEN.name()).toString(),"0");
            	if(ctaOrigen != null)
            	{
            		ctaOrigen = ctaOrigen.replace("-", "");
            	} 
            	if(ctaOrigen == null || ctaOrigen.isEmpty())
            	{
            		ctaOrigen = "";
            	}
            	cellCtaOrigen.setCellValue(Integer.parseInt(ctaOrigen));
            	cellCtaOrigen.setCellStyle(formatStyle);
            	
            	Cell cellAnoP;
            	cellAnoP = sheet.getRow(contFila).createCell(1);
            	cellAnoP.setCellValue(String.valueOf(registro.getCampos().get(GeneralParameterEnum.ANOPAGO.name())));
            	cellAnoP.setCellStyle(style2);
            	
            	Cell cellMesP;
            	cellMesP = sheet.getRow(contFila).createCell(2);
            	cellMesP.setCellValue(Integer.parseInt(registro.getCampos().get(GeneralParameterEnum.MESPAGO.name()).toString()));
            	cellMesP.setCellStyle(dateStyle);
            
            	Cell cellDiaP;
            	cellDiaP = sheet.getRow(contFila).createCell(3);
            	cellDiaP.setCellValue(Integer.parseInt(registro.getCampos().get(GeneralParameterEnum.DIAPAGO.name()).toString()));
            	cellDiaP.setCellStyle(dateStyle);
            	
            	Cell cellBeneficiario;
            	cellBeneficiario = sheet.getRow(contFila).createCell(4);
            	cellBeneficiario.setCellValue(String.valueOf(registro.getCampos().get(GeneralParameterEnum.NOMBREBENEFICIARIO.name())));
            	cellBeneficiario.setCellStyle(style1);
            	
            	Cell cellNit;
            	cellNit = sheet.getRow(contFila).createCell(5);
            	cellNit.setCellValue(String.valueOf(registro.getCampos().get(GeneralParameterEnum.NIT.name())));
            	cellNit.setCellStyle(style);
            	
            	Cell cellComprobante;
            	cellComprobante = sheet.getRow(contFila).createCell(6);
            	cellComprobante.setCellValue(String.valueOf(registro.getCampos().get(GeneralParameterEnum.COMPROBANTE.name())));
            	cellComprobante.setCellStyle(style);
            	
            	Cell cellFormaPago;
            	cellFormaPago = sheet.getRow(contFila).createCell(7);
            	cellFormaPago.setCellValue(String.valueOf(registro.getCampos().get(GeneralParameterEnum.FORMA_PAGO.name())));
            	cellFormaPago.setCellStyle(style2);
            	
            	Cell cellValorPagar;
            	cellValorPagar = sheet.getRow(contFila).createCell(8);
            	cellValorPagar.setCellValue(Double.valueOf(registro.getCampos().get(GeneralParameterEnum.VALORTRANSACCION.name()).toString()));
            	cellValorPagar.setCellStyle(numericStyle);
            	
            	Cell cellInformacion;
            	cellInformacion = sheet.getRow(contFila).createCell(9);
            	cellInformacion.setCellValue(String.valueOf(registro.getCampos().get(GeneralParameterEnum.TEXTO.name())));
            	cellInformacion.setCellStyle(style);
            	
            	Cell cellEntidadDes;
            	cellEntidadDes = sheet.getRow(contFila).createCell(10);
            	cellEntidadDes.setCellValue(String.valueOf(registro.getCampos().get(GeneralParameterEnum.BANCODESTINO.name())));
            	cellEntidadDes.setCellStyle(style2);
            	
            	Cell cellCtaDestino;
            	cellCtaDestino = sheet.getRow(contFila).createCell(11);
            	cellCtaDestino.setCellValue(String.valueOf(registro.getCampos().get(GeneralParameterEnum.CUENTADESTINO.name())));
            	cellCtaDestino.setCellStyle(style1);
            	
            	Cell cellTipoCtaDes;   
            	cellTipoCtaDes = sheet.getRow(contFila).createCell(12);
            	cellTipoCtaDes.setCellValue(String.valueOf(registro.getCampos().get(GeneralParameterEnum.TIPO_CUENTA.name())));
            	cellTipoCtaDes.setCellStyle(style2);
            	
            	contFila++;            	
            }
            
		    workbook.setForceFormulaRecalculation(true);
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            workbook.write(out);
            out.close();
            archivoDescarga = JsfUtil.getArchivoDescarga(
                            new ByteArrayInputStream(out.toByteArray()),
                            "PAGOS.xls");
        }
        catch (IOException | JRException | SystemException e) {
            Logger.getLogger(FormatoDIANRetControlador.class.getName())
                            .log(Level.SEVERE, null, e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }		    
	}
	/**
	 * 
	 * Metodo ejecutado al oprimir el boton btnImpirmir en la vista
	 *
	 * TODO DOCUMENTACION ADICIONAL
	 *
	 */
	public void oprimirbtnImpirmir() {
		// <CODIGO_DESARROLLADO>
		archivoDescarga = null;
		imprimirReporte(ReportesBean.FORMATOS.EXCEL97);
		// </CODIGO_DESARROLLADO>
	}
	
	private void imprimirReporte(FORMATOS formato) {
		try {
			HashMap<String, Object> reemplazar = new HashMap<>();
			String reporte = "002809CONSOLIDADOBANCODEOCCIDENTE";
			reemplazar.put("compania", compania);
			reemplazar.put("ano",Integer.parseInt(ano));
			reemplazar.put("tipoEgreso", tipoEgreso);
			reemplazar.put("cuentaInicial", cuentaInicial);
			reemplazar.put("cuentaFinal", cuentaFinal);
			reemplazar.put("egresoInicial", egresoInicial);
			reemplazar.put("egresoFinal", egresoFinal);
			reemplazar.put("terceroInicial", terceroInicial);
			reemplazar.put("terceroFinal", terceroFinal);
			Map<String, Object> parametros = new HashMap<>();
			Reporteador.resuelveConsulta(reporte, Integer.parseInt(SessionUtil.getModulo()), reemplazar, parametros);
			parametros.put("PR_NOMBRECOMPANIA", SessionUtil.getCompaniaIngreso().getNombre());
			archivoDescarga = JsfUtil.exportarStreamed(reporte, parametros, ConectorPool.ESQUEMA_SYSMAN, formato);
		} catch (JRException | IOException | com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException   e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}
	}

//</METODOS_BOTONES>
//<METODOS_CAMBIAR>
	/**
	 * Metodo ejecutado al cambiar el control cmbAno
	 * 
	 * TODO DOCUMENTACION ADICIONAL
	 * 
	 */
	public void cambiarcmbAno() {
		listacmbNumeroInicial = null;
        egresoInicial = "";
        listacmbNumeroFinal = null;
        egresoFinal = null;
        listacmbCuentaInicial = null;
        cuentaInicial = "";
        listacmbCuentaFinal = null;
        cuentaFinal = "";
        cargarListacmbCuentaInicial();
        cargarListacmbNumeroInicial();
	}

//</METODOS_CAMBIAR>
//<METODOS_COMBOS_GRANDES>
	/**
	 * 
	 * Metodo ejecutado al seleccionar una fila de la lista listacmbNumeroBanco
	 *
	 * TODO DOCUMENTACION ADICIONAL
	 *
	 * @param event objeto que encapsula la accion proveniente de la vista
	 */
	public void seleccionarFilacmbNumeroBanco(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		numeroBanco = SysmanFunciones.toString(registroAux.getCampos().get(GeneralParameterEnum.BANCO.getName()));
	}

	/**
	 * 
	 * Metodo ejecutado al seleccionar una fila de la lista listacmbTipo
	 *
	 * TODO DOCUMENTACION ADICIONAL
	 *
	 * @param event objeto que encapsula la accion proveniente de la vista
	 */
	public void seleccionarFilacmbTipo(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		tipoEgreso = SysmanFunciones.toString(registroAux.getCampos().get(GeneralParameterEnum.CODIGO.getName()));
		listacmbNumeroInicial = null;
		egresoInicial = "";
		listacmbNumeroFinal = null;
		egresoFinal = "";
		cargarListacmbNumeroInicial();
	}

	/**
	 * 
	 * Metodo ejecutado al seleccionar una fila de la lista listacmbNumeroInicial
	 *
	 * TODO DOCUMENTACION ADICIONAL
	 *
	 * @param event objeto que encapsula la accion proveniente de la vista
	 */
	public void seleccionarFilacmbNumeroInicial(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		egresoInicial = SysmanFunciones.toString(registroAux.getCampos().get(GeneralParameterEnum.NUMERO.getName()));
		listacmbNumeroFinal = null;
		egresoFinal = "";
		cargarListacmbNumeroFinal();
	}

	/**
	 * 
	 * Metodo ejecutado al seleccionar una fila de la lista listacmbNumeroFinal
	 *
	 * TODO DOCUMENTACION ADICIONAL
	 *
	 * @param event objeto que encapsula la accion proveniente de la vista
	 */
	public void seleccionarFilacmbNumeroFinal(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		egresoFinal = SysmanFunciones.toString(registroAux.getCampos().get(GeneralParameterEnum.NUMERO.getName()));
	}

	/**
	 * 
	 * Metodo ejecutado al seleccionar una fila de la lista listacmbTerceroInicial
	 *
	 * TODO DOCUMENTACION ADICIONAL
	 *
	 * @param event objeto que encapsula la accion proveniente de la vista
	 */
	public void seleccionarFilacmbTerceroInicial(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		terceroInicial = SysmanFunciones.toString(registroAux.getCampos().get(GeneralParameterEnum.NIT.getName()));
		listacmbTerceroFinal = null;
		terceroFinal = "";
		cargarListacmbTerceroFinal();
	}

	/**
	 * 
	 * Metodo ejecutado al seleccionar una fila de la lista listacmbTerceroFinal
	 *
	 * TODO DOCUMENTACION ADICIONAL
	 *
	 * @param event objeto que encapsula la accion proveniente de la vista
	 */
	public void seleccionarFilacmbTerceroFinal(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		terceroFinal = SysmanFunciones.toString(registroAux.getCampos().get(GeneralParameterEnum.NIT.getName()));
	}

	/**
	 * 
	 * Metodo ejecutado al seleccionar una fila de la lista listacmbCuentaInicial
	 *
	 * TODO DOCUMENTACION ADICIONAL
	 *
	 * @param event objeto que encapsula la accion proveniente de la vista
	 */
	public void seleccionarFilacmbCuentaInicial(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		cuentaInicial = SysmanFunciones.toString(registroAux.getCampos().get(GeneralParameterEnum.CODIGO.getName()));
		listacmbCuentaFinal = null;
		cuentaFinal = null;
		cargarListacmbCuentaFinal();
	}

	/**
	 * 
	 * Metodo ejecutado al seleccionar una fila de la lista listacmbCuentaFinal
	 *
	 * TODO DOCUMENTACION ADICIONAL
	 *
	 * @param event objeto que encapsula la accion proveniente de la vista
	 */
	public void seleccionarFilacmbCuentaFinal(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		cuentaFinal = SysmanFunciones.toString(registroAux.getCampos().get(GeneralParameterEnum.CODIGO.getName()));
	}
	
//</METODOS_COMBOS_GRANDES>
//<METODOS_ARBOL>
//</METODOS_ARBOL>
//<SET_GET_ATRIBUTOS>
	/**
	 * Retorna la variable numeroBanco
	 * 
	 * @return numeroBanco
	 */
	public String getNumeroBanco() {
		return numeroBanco;
	}

	/**
	 * Asigna la variable numeroBanco
	 * 
	 * @param numeroBanco Variable a asignar en numeroBanco
	 */
	public void setNumeroBanco(String numeroBanco) {
		this.numeroBanco = numeroBanco;
	}

	/**
	 * Retorna la variable ano
	 * 
	 * @return ano
	 */
	public String getAno() {
		return ano;
	}

	/**
	 * Asigna la variable ano
	 * 
	 * @param ano Variable a asignar en ano
	 */
	public void setAno(String ano) {
		this.ano = ano;
	}

	/**
	 * Retorna la variable tipoEgreso
	 * 
	 * @return tipoEgreso
	 */
	public String getTipoEgreso() {
		return tipoEgreso;
	}

	/**
	 * Asigna la variable tipoEgreso
	 * 
	 * @param tipoEgreso Variable a asignar en tipoEgreso
	 */
	public void setTipoEgreso(String tipoEgreso) {
		this.tipoEgreso = tipoEgreso;
	}

	/**
	 * Retorna la variable egresoInicial
	 * 
	 * @return egresoInicial
	 */
	public String getEgresoInicial() {
		return egresoInicial;
	}

	/**
	 * Asigna la variable egresoInicial
	 * 
	 * @param egresoInicial Variable a asignar en egresoInicial
	 */
	public void setEgresoInicial(String egresoInicial) {
		this.egresoInicial = egresoInicial;
	}

	/**
	 * Retorna la variable egresoFinal
	 * 
	 * @return egresoFinal
	 */
	public String getEgresoFinal() {
		return egresoFinal;
	}

	/**
	 * Asigna la variable egresoFinal
	 * 
	 * @param egresoFinal Variable a asignar en egresoFinal
	 */
	public void setEgresoFinal(String egresoFinal) {
		this.egresoFinal = egresoFinal;
	}

	/**
	 * Retorna la variable terceroInicial
	 * 
	 * @return terceroInicial
	 */
	public String getTerceroInicial() {
		return terceroInicial;
	}

	/**
	 * Asigna la variable terceroInicial
	 * 
	 * @param terceroInicial Variable a asignar en terceroInicial
	 */
	public void setTerceroInicial(String terceroInicial) {
		this.terceroInicial = terceroInicial;
	}

	/**
	 * Retorna la variable terceroFinal
	 * 
	 * @return terceroFinal
	 */
	public String getTerceroFinal() {
		return terceroFinal;
	}

	/**
	 * Asigna la variable terceroFinal
	 * 
	 * @param terceroFinal Variable a asignar en terceroFinal
	 */
	public void setTerceroFinal(String terceroFinal) {
		this.terceroFinal = terceroFinal;
	}

	/**
	 * Retorna la variable cuentaInicial
	 * 
	 * @return cuentaInicial
	 */
	public String getCuentaInicial() {
		return cuentaInicial;
	}

	/**
	 * Asigna la variable cuentaInicial
	 * 
	 * @param cuentaInicial Variable a asignar en cuentaInicial
	 */
	public void setCuentaInicial(String cuentaInicial) {
		this.cuentaInicial = cuentaInicial;
	}

	/**
	 * Retorna la variable cuentaFinal
	 * 
	 * @return cuentaFinal
	 */
	public String getCuentaFinal() {
		return cuentaFinal;
	}

	/**
	 * Asigna la variable cuentaFinal
	 * 
	 * @param cuentaFinal Variable a asignar en cuentaFinal
	 */
	public void setCuentaFinal(String cuentaFinal) {
		this.cuentaFinal = cuentaFinal;
	}

	/**
	 * Retorna la variable fechaPago
	 * 
	 * @return fechaPago
	 */
	public Date getFechaPago() {
		return fechaPago;
	}

	/**
	 * Asigna la variable fechaPago
	 * 
	 * @param fechaPago Variable a asignar en fechaPago
	 */
	public void setFechaPago(Date fechaPago) {
		this.fechaPago = fechaPago;
	}

	/**
	 * Atributo usado para descargar contenidos de archivos desde la vista
	 */
	public StreamedContent getArchivoDescarga() {
		return archivoDescarga;
	}

//</SET_GET_ATRIBUTOS>
//<SET_GET_PARAMETROS>
//</SET_GET_PARAMETROS>
//<SET_GET_LISTAS>
	/**
	 * Retorna la lista listacmbAno
	 * 
	 * @return listacmbAno
	 */
	public List<Registro> getListacmbAno() {
		return listacmbAno;
	}

	/**
	 * Asigna la lista listacmbAno
	 * 
	 * @param listacmbAno Variable a asignar en listacmbAno
	 */
	public void setListacmbAno(List<Registro> listacmbAno) {
		this.listacmbAno = listacmbAno;
	}

//</SET_GET_LISTAS>
//<SET_GET_LISTAS_COMBO_GRANDE>	
	/**
	 * Retorna la lista listacmbNumeroBanco
	 * 
	 * @return listacmbNumeroBanco
	 */
	public RegistroDataModelImpl getListacmbNumeroBanco() {
		return listacmbNumeroBanco;
	}

	/**
	 * Asigna la lista listacmbNumeroBanco
	 * 
	 * @param listacmbNumeroBanco Variable a asignar en listacmbNumeroBanco
	 */
	public void setListacmbNumeroBanco(RegistroDataModelImpl listacmbNumeroBanco) {
		this.listacmbNumeroBanco = listacmbNumeroBanco;
	}

	/**
	 * Retorna la lista listacmbTipo
	 * 
	 * @return listacmbTipo
	 */
	public RegistroDataModelImpl getListacmbTipo() {
		return listacmbTipo;
	}

	/**
	 * Asigna la lista listacmbTipo
	 * 
	 * @param listacmbTipo Variable a asignar en listacmbTipo
	 */
	public void setListacmbTipo(RegistroDataModelImpl listacmbTipo) {
		this.listacmbTipo = listacmbTipo;
	}

	/**
	 * Retorna la lista listacmbNumeroInicial
	 * 
	 * @return listacmbNumeroInicial
	 */
	public RegistroDataModelImpl getListacmbNumeroInicial() {
		return listacmbNumeroInicial;
	}

	/**
	 * Asigna la lista listacmbNumeroInicial
	 * 
	 * @param listacmbNumeroInicial Variable a asignar en listacmbNumeroInicial
	 */
	public void setListacmbNumeroInicial(RegistroDataModelImpl listacmbNumeroInicial) {
		this.listacmbNumeroInicial = listacmbNumeroInicial;
	}

	/**
	 * Retorna la lista listacmbNumeroFinal
	 * 
	 * @return listacmbNumeroFinal
	 */
	public RegistroDataModelImpl getListacmbNumeroFinal() {
		return listacmbNumeroFinal;
	}

	/**
	 * Asigna la lista listacmbNumeroFinal
	 * 
	 * @param listacmbNumeroFinal Variable a asignar en listacmbNumeroFinal
	 */
	public void setListacmbNumeroFinal(RegistroDataModelImpl listacmbNumeroFinal) {
		this.listacmbNumeroFinal = listacmbNumeroFinal;
	}

	/**
	 * Retorna la lista listacmbTerceroInicial
	 * 
	 * @return listacmbTerceroInicial
	 */
	public RegistroDataModelImpl getListacmbTerceroInicial() {
		return listacmbTerceroInicial;
	}

	/**
	 * Asigna la lista listacmbTerceroInicial
	 * 
	 * @param listacmbTerceroInicial Variable a asignar en listacmbTerceroInicial
	 */
	public void setListacmbTerceroInicial(RegistroDataModelImpl listacmbTerceroInicial) {
		this.listacmbTerceroInicial = listacmbTerceroInicial;
	}

	/**
	 * Retorna la lista listacmbTerceroFinal
	 * 
	 * @return listacmbTerceroFinal
	 */
	public RegistroDataModelImpl getListacmbTerceroFinal() {
		return listacmbTerceroFinal;
	}

	/**
	 * Asigna la lista listacmbTerceroFinal
	 * 
	 * @param listacmbTerceroFinal Variable a asignar en listacmbTerceroFinal
	 */
	public void setListacmbTerceroFinal(RegistroDataModelImpl listacmbTerceroFinal) {
		this.listacmbTerceroFinal = listacmbTerceroFinal;
	}

	/**
	 * Retorna la lista listacmbCuentaInicial
	 * 
	 * @return listacmbCuentaInicial
	 */
	public RegistroDataModelImpl getListacmbCuentaInicial() {
		return listacmbCuentaInicial;
	}

	/**
	 * Asigna la lista listacmbCuentaInicial
	 * 
	 * @param listacmbCuentaInicial Variable a asignar en listacmbCuentaInicial
	 */
	public void setListacmbCuentaInicial(RegistroDataModelImpl listacmbCuentaInicial) {
		this.listacmbCuentaInicial = listacmbCuentaInicial;
	}

	/**
	 * Retorna la lista listacmbCuentaFinal
	 * 
	 * @return listacmbCuentaFinal
	 */
	public RegistroDataModelImpl getListacmbCuentaFinal() {
		return listacmbCuentaFinal;
	}

	/**
	 * Asigna la lista listacmbCuentaFinal
	 * 
	 * @param listacmbCuentaFinal Variable a asignar en listacmbCuentaFinal
	 */
	public void setListacmbCuentaFinal(RegistroDataModelImpl listacmbCuentaFinal) {
		this.listacmbCuentaFinal = listacmbCuentaFinal;
	}
//</SET_GET_LISTAS_COMBO_GRANDE>

	public String getCuentaClienteDebitar() {
		return cuentaClienteDebitar;
	}

	public void setCuentaClienteDebitar(String cuentaClienteDebitar) {
		this.cuentaClienteDebitar = cuentaClienteDebitar;
	}

	public ContenedorArchivo getContArchivocargarArchivoPagos() {
		return contArchivocargarArchivoPagos;
	}

	public void setContArchivocargarArchivoPagos(ContenedorArchivo contArchivocargarArchivoPagos) {
		this.contArchivocargarArchivoPagos = contArchivocargarArchivoPagos;
	}
}
