/*-
 * ConfiguracionRubrosPptalesControlador.java
 *
 * 1.0
 * 
 * 05/07/2018
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */
package com.sysman.general;

import com.sysman.beanbase.BeanBaseContinuoAcmeImpl;
import com.sysman.circularunica.ejb.EjbCircularUnicaCeroGeneralRemote;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.exception.SystemException;
import com.sysman.general.enums.ConfiguracionRubrosPptalesControladorEnum;
import com.sysman.general.enums.ConfiguracionRubrosPptalesControladorUrlEnum;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.recursos.ejb.EjbSysmanUtilRemote;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.util.SysmanFunciones;
import com.sysman.util.SysmanConstantes;


import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.event.RowEditEvent;
import org.primefaces.event.SelectEvent;

import org.primefaces.model.StreamedContent;
import com.sysman.util.ContenedorArchivo;

import java.io.ByteArrayOutputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.math.BigDecimal;

import net.sf.jasperreports.engine.JRException;

import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Name;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.primefaces.event.RowEditEvent;
import org.primefaces.model.StreamedContent;

/**
 * TODO Ingrese una descripcion para la clase.
 *
 * @version 1.0, 05/07/2018
 * @author jmalaver
 */
@ManagedBean
@ViewScoped
public class ConfiguracionRubrosPptalesControlador
                extends BeanBaseContinuoAcmeImpl {
    /**
     * Constante a nivel de clase que almacena el codigo de la
     * compania en la cual inicio sesion el usuario, el valor de esta
     * constante es asignado en el constructor a la variable de sesion
     * correspondiente
     */
    private final String compania;
    // <DECLARAR_ATRIBUTOS>
    // </DECLARAR_ATRIBUTOS>
    // <DECLARAR_PARAMETROS>
    // </DECLARAR_PARAMETROS>
    // <DECLARAR_LISTAS>
    /**
     * Atributo auxliar el cual es asiganado en el momento que se
     * activa la edicion de un registro. Toma el valor del indice
     * dentro de la grilla del registro seleccionado para editar
     */
    private int indice;
    private String naturaleza;

    /**
     * variable que almacena la lista del combo listaCbAnio
     */
    private List<Registro> listaCbAnio;
    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>
    /**
     * variable que almacena la lista del combo
     * listaCbCodigoSupersalud
     */
    private RegistroDataModelImpl listaCbCodigoSupersalud;
    /**
     * variable que almacena la lista del combo
     * listaCbCodigoSupersaludE
     */
    private RegistroDataModelImpl listaCbCodigoSupersaludE;
    /**
     * variable que almacena la lista del combo listaCbCodigoSIA
     */
    private RegistroDataModelImpl listaCbCodigoSIA;
    /**
     * variable que almacena la lista del combo listaCbCodigoSIAE
     */
    private RegistroDataModelImpl listaCbCodigoSIAE;

    /**
     * Esta variable se usa como auxiliar para subformularios y en
     * esta se alamcena el identificador del registro que se
     * selecciono
     */
    private String auxiliar;
    private int ano;
    private int estado;
    private String tituloBtnCuentas;
    private final String modulo;
    private String manejaAuxiliarFuente;
    private boolean bloqueadoSIA;
    private boolean bloqueadoCircUnica;
    private boolean cargarplantilla = false;
    /**
  	 * Atributo usado para descargar contenidos de archivos desde la
  	 * vista
  	 */
  	private StreamedContent archivoDescarga;
  	private ContenedorArchivo contArchivocargarExcel = new ContenedorArchivo();
  	private String cadena;
  	private int contador;
  	private final String HOJA_CONFIG = "Configuración";

	@EJB
    private EjbSysmanUtilRemote ejbSysmanUtil;
	
	@EJB
    private EjbCircularUnicaCeroGeneralRemote ejbCircularUnicaCeroGeneralRemote;

    // </DECLARAR_LISTAS_COMBO_GRANDE>
    /**
     * Crea una nueva instancia de
     * ConfiguracionRubrosPptalesControlador
     */
    public ConfiguracionRubrosPptalesControlador() {
        super();
        compania = SessionUtil.getCompania();
        ano = SysmanFunciones.ano(new Date());
        modulo = SessionUtil.getModulo();
        estado = 0;
        tituloBtnCuentas = idioma.getString("TB_TB4149");
        try {
            numFormulario = 1851;
            validarPermisos();
            // <INI_ADICIONAL>
            // </INI_ADICIONAL>
        }
        catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
            SessionUtil.redireccionarMenuPermisos();
        }
    }

    /**
     * Este metodo se ejecuta justo despues de que el objeto de la
     * clase del Bean ha sido creado, en este se realizan las
     * asignaciones iniciales necesarias para la visualizacion del
     * formulario, como son tablas, origenes de datos, inicializacion
     * de listas y demas necesarios
     */
    @PostConstruct
    public void inicializar() {
        tabla = ConfiguracionRubrosPptalesControladorEnum.PLAN_PPTAL_CONFIG
                        .getValue();
        registro = new Registro();
        reasignarOrigen();
        buscarLlave();
        // <CARGAR_LISTA>
        cargarListaCbAnio();
        // </CARGAR_LISTA>
        // <CARGAR_LISTA_COMBO_GRANDE>
        // </CARGAR_LISTA_COMBO_GRANDE>
        abrirFormulario();
    }

    /**
     * En este metodo se asigna al atributo origenDatos del bean base
     * el valor de la consulta del formulario. Tambien carga la lista
     * del formulario por primera vez
     */
    @Override
    public void reasignarOrigen() {

        if ("99040102".equals(SessionUtil.getMenuActual())) {
            bloqueadoSIA = false;
            bloqueadoCircUnica = true;
            cargarplantilla = true;
            cargarListaCbCodigoSupersalud();
            cargarListaCbCodigoSupersaludE();

            urlListado = UrlServiceUtil.getInstance()
                            .getUrlServiceByUrlByEnumID(
                                            ConfiguracionRubrosPptalesControladorUrlEnum.URL001
                                                            .getValue());

            urlActualizacion = UrlServiceUtil.getInstance()
                            .getUrlServiceByUrlByEnumID(
                                            ConfiguracionRubrosPptalesControladorUrlEnum.URL003
                                                            .getValue());
        }

        else {
            bloqueadoSIA = true;
            bloqueadoCircUnica = false;
            cargarListaCbCodigoSIA();
            cargarListaCbCodigoSIAE();

            urlListado = UrlServiceUtil.getInstance()
                            .getUrlServiceByUrlByEnumID(
                                            ConfiguracionRubrosPptalesControladorUrlEnum.URL004
                                                            .getValue());

            urlActualizacion = UrlServiceUtil.getInstance()
                            .getUrlServiceByUrlByEnumID(
                                            ConfiguracionRubrosPptalesControladorUrlEnum.URL005
                                                            .getValue());
        }

        parametrosListado.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
        parametrosListado.put(GeneralParameterEnum.ANO.getName(), ano);
        parametrosListado.put(GeneralParameterEnum.ESTADO.getName(), estado);

        try {
            manejaAuxiliarFuente = ejbSysmanUtil.consultarParametro(
                            compania,
                            "MANEJA AUXILIAR POR FUENTE EN PRESUPUESTO",
                            SessionUtil.getModulo(),
                            new Date(), true);
            manejaAuxiliarFuente = manejaAuxiliarFuente == null ? "NO"
                : manejaAuxiliarFuente;
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

        parametrosListado.put("MANEJA_AUXILIARFUENTE", manejaAuxiliarFuente);
    }

    // <METODOS_CARGAR_LISTA>

    /**
     * 
     * Carga la lista listaCbAnio
     */
    public void cargarListaCbAnio() {
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(),
                        String.valueOf(compania));

        try {
            listaCbAnio = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            ConfiguracionRubrosPptalesControladorUrlEnum.URL002
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    /**
     * 
     * Carga la lista listaCpCodigoOpcion
     */
    public void cargarListaCbCodigoSupersalud() {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        ConfiguracionRubrosPptalesControladorUrlEnum.URL006
                                                        .getValue());

        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        listaCbCodigoSupersalud = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, GeneralParameterEnum.CODIGO.getName());

    }

    /**
     * 
     * Carga la lista listaCpCodigoOpcion
     */
    public void cargarListaCbCodigoSupersaludE() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        ConfiguracionRubrosPptalesControladorUrlEnum.URL006
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        listaCbCodigoSupersaludE = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, GeneralParameterEnum.CODIGO.getName());
    }

    /**
     * 
     * Carga la lista listaCpCodigoOpcion
     */
    public void cargarListaCbCodigoSIA() {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        ConfiguracionRubrosPptalesControladorUrlEnum.URL007
                                                        .getValue());

        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.ANO.getName(), ano);

        listaCbCodigoSIA = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, GeneralParameterEnum.CODIGO.getName());

    }

    /**
     * 
     * Carga la lista listaCpCodigoOpcion
     */
    public void cargarListaCbCodigoSIAE() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        ConfiguracionRubrosPptalesControladorUrlEnum.URL007
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.ANO.getName(), ano);
        param.put(GeneralParameterEnum.CLASE.getName(), naturaleza);

        listaCbCodigoSIAE = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, GeneralParameterEnum.CODIGO.getName());
    }

    // </METODOS_CARGAR_LISTA>
    // <METODOS_BOTONES>
    /**
     * 
     * Metodo ejecutado al oprimir el boton BtVerificarCtasSinConf en
     * la vista
     *
     */
    public void oprimirBtVerificarCtasSinConf() {
        // <CODIGO_DESARROLLADO>
        if (estado == 1) {
            estado = 0;
            tituloBtnCuentas = idioma.getString("TB_TB4149");
        }
        else {
            estado = 1;
            tituloBtnCuentas = idioma.getString("TB_TB4150");
        }
        reasignarOrigen();
        // </CODIGO_DESARROLLADO>
    }

    /**
     * 
     * Metodo ejecutado al oprimir el boton BtActualizarVigencia en la
     * vista
     *
     */
    public void oprimirBtActualizarVigencia() {
        // <CODIGO_DESARROLLADO>
        String[] campos = { "anioInicial" };
        String[] valores = { String.valueOf(ano) };
        SessionUtil.cargarModalDatosFlash(String.valueOf(
                        GeneralCodigoFormaEnum.FRM_PREPARAR_CODIGOS_ANIO
                                        .getCodigo()),
                        modulo, campos, valores);
        // </CODIGO_DESARROLLADO>
    }
    


    /**
     * Metodo ejecutado cuando se activa la edicion de un registro del
     * formulario
     * 
     *
     * @param registro
     * registro del cual se activo la edicion
     */
    public void activarEdicion(Registro registro) {
        indice = listaInicial.getRowIndex();
        naturaleza = listaInicial.getDatasource().get(indice).getCampos()
                        .get("NATURALEZA").toString();
        cargarListaCbCodigoSIAE();

    }
    
	public void oprimirbtnDescargarPlantilla()  {
		//<CODIGO_DESARROLLADO>
		archivoDescarga=null;  
		HSSFWorkbook workbook = new HSSFWorkbook();
		try (ByteArrayOutputStream out = new ByteArrayOutputStream();) {
		
			CellStyle style = crearEstiloEncabezado(workbook);
			crearHojaConfiguracion(workbook, style);
			
			workbook.write(out);
			workbook.close();

			archivoDescarga = JsfUtil.getArchivoDescarga(new ByteArrayInputStream(out.toByteArray()),
					"Plantilla De Configuración Presupuestal.xls");

			JsfUtil.agregarMensajeInformativo(idioma.getString("MSM_PROCESO_EJECUTADO"));
		} catch (IOException | JRException  e) {
			logger.error(e.getMessage(),e);
			JsfUtil.agregarMensajeError(e.getMessage());
		
		} 


		//</CODIGO_DESARROLLADO>
	}

	/**
	 * 
	 * metodo para  estilo de encabezado en archivo excel - generico -
	 * 
	 */
	private CellStyle crearEstiloEncabezado(HSSFWorkbook workbook) {

		Font font = workbook.createFont();
		font.setFontName("Arial");
		font.setBold(true);
		font.setFontHeightInPoints((short) 10);

		CellStyle style = workbook.createCellStyle();
		style.setFont(font);

		//		groundColor(IndexedColors.GREY_25_PERCENT.getIndex());
		//		style.setFillPattern(FillPatternType.SOLID_FOREGROUND);

		return style;
	}
	
	
	/**
	 * 
	 * metodo para enviar fuente de recurso
	 * 
	 */
	private void crearHojaConfiguracion(HSSFWorkbook workbook, CellStyle style) {

		HSSFSheet sheet = workbook.createSheet(HOJA_CONFIG);

		String[] columnas = {
				"CODIGO RUBRO",
				"NOMBRE RUBRO",
				"NATURALEZA",
				"MOVIMIENTO",
				"FUENTE RECURSO",
				"CODIGO SUPERSALUD",
				"CENTRO COSTO",
				"AUXILIAR GENERAL",
				"REFERENCIA"
		};

		crearEncabezados(sheet, columnas, style);
		
	}
	
	/**
	 * 
	 * metodo para crear titulos encabezado generico
	 * 
	 */
	private void crearEncabezados(HSSFSheet sheet, String[] columnas, CellStyle style) {

		Row row = sheet.createRow(0);

		for (int i = 0; i < columnas.length; i++) {
			Cell cell = row.createCell(i);
			cell.setCellValue(columnas[i]);
			cell.setCellStyle(style);
			sheet.autoSizeColumn(i);
		}
	}
	
	public void oprimirbtnCargarPlantilla() {
		//<CODIGO_DESARROLLADO>
		archivoDescarga=null;  
		Workbook workbook = null;
		cadena = "TO_CLOB('";
		contador = 0;
		
		if (!validarArchivo()) {
			return;
		}
		File archivo = contArchivocargarExcel.getArchivo();
		try (FileInputStream file = new FileInputStream(archivo)) {
			String rutaArchivo = contArchivocargarExcel.getArchivo().getPath();
			String extension = rutaArchivo.substring(rutaArchivo.lastIndexOf('.') + 1);
			Sheet sheet = null;
			String tablas = null;
			//Row header = null;
			int column = 0;
			int registrosProcesados = 0;

			if ("xls".equalsIgnoreCase(extension)) {
				workbook = new HSSFWorkbook(file);
			} else {
				workbook = new XSSFWorkbook(file);
			}

			sheet = workbook.getSheet(HOJA_CONFIG);    	
			column = 9;
			tablas = "plan_pptal_config";
			  
			 
			for (Row row : sheet) {

					if (row.getRowNum() == 0) {
						continue;
					}
					if (filaVacia(row)) {
						continue;
					}
					capturaDatosExcelConfiguracionPPTAL(row, column);
					registrosProcesados ++;
			}

			cadena = cadena + "')";
				
			if (registrosProcesados == 0) {
				JsfUtil.agregarMensajeAlerta("La hoja " + sheet.getSheetName() + " no tiene información.");
				return;
			}
				
				
			String clobErrores = ejbCircularUnicaCeroGeneralRemote.cargarConfiguracionPptal(tablas, cadena, SessionUtil.getUser().getCodigo(),compania,ano);
						
			if (clobErrores != null && !clobErrores.trim().isEmpty()) {
			
				ByteArrayInputStream streamTexto = JsfUtil.serializarPlano(clobErrores);
				archivoDescarga = JsfUtil.getArchivoDescarga(streamTexto, "LOG.txt");
			}
			
			
			
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
			JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB4504"));
		} finally {
			try {
				if (workbook != null) {
					workbook.close();
				}
			} catch (IOException e) {
				logger.error(e.getMessage(), e);
			}
		}	
	}
	
	public boolean validarArchivo() {

		if (contArchivocargarExcel == null || contArchivocargarExcel.getArchivo() == null) {
			JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB4001"));
			return false;
		} else {
			return true;
		}
	}
	
//Método para validar si una fila está vacía
		private boolean filaVacia(Row row) {

		    if (row == null) {
		        return true;
		    }

		    DataFormatter formatter = new DataFormatter();

		    for (int c = 0; c < 7; c++) { 
		        Cell cell = row.getCell(c);

		        if (cell != null) {
		            String val = formatter.formatCellValue(cell);
		            if (val != null && !val.trim().isEmpty()) {
		                return false;
		            }
		        }
		    }
		    return true;
		}
		private void capturaDatosExcelConfiguracionPPTAL(Row row, int column) {

		    for (int i = 0; i < column; i++) {

		        Cell cell = row.getCell(i);
		        String val = "";

		        if (cell != null) {

		            int cellType = cell.getCellType();

		            if (cellType == Cell.CELL_TYPE_NUMERIC) {

		                BigDecimal bd = BigDecimal.valueOf(cell.getNumericCellValue());
		                bd = bd.stripTrailingZeros();
		                //val = bd.toPlainString().replace('.', ',');
		                val = bd.toPlainString();

		            } else if (cellType == Cell.CELL_TYPE_STRING) {

		                val = cell.getStringCellValue().trim();
		                val = normalizarTexto(val);

		            } else {
		                val = "";
		            }
		        }

		        if (val == null || val.isEmpty() || "null".equalsIgnoreCase(val)) {
		            val = "NoDato";
		        }

		        contador += val.length();

		        if (contador >= 3000) {
		            cadena = cadena + "') || TO_CLOB('";
		            contador = 0;
		        }

		        cadena = cadena + val + SysmanConstantes.SEPARADOR_COL;
		    }

		    cadena = cadena.substring(0, cadena.length() - SysmanConstantes.SEPARADOR_COL.length());
		    cadena = cadena + SysmanConstantes.SEPARADOR_REG;
		}
		private String normalizarTexto(String texto) {

		    if (texto == null) {
		        return "";
		    }

		    // Reemplazar saltos de línea por espacio
		    texto = texto.replaceAll("\\r\\n|\\r|\\n", " ");

		    // Reemplazar tabulaciones
		    texto = texto.replaceAll("\\t", " ");

		    // Quitar espacios múltiples
		    texto = texto.replaceAll(" +", " ");

		    // 4. Trim final
		    return texto.trim();
	}
    // </METODOS_BOTONES>
    // <METODOS_CAMBIAR>
    /**
     * Metodo ejecutado al cambiar el control CbAnio
     * 
     */
    public void cambiarCbAnio() {
        // <CODIGO_DESARROLLADO>
        reasignarOrigen();
        // </CODIGO_DESARROLLADO>
    }

    // </METODOS_CAMBIAR>
    // <METODOS_COMBOS_GRANDES>
    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaCbCodigoSupersalud
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaCbCodigoSupersalud(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        registro.getCampos().put("CODIGO_SUPERSALUD",
                        registroAux.getCampos().get(
                                        GeneralParameterEnum.CODIGO.getName()));
    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaCbCodigoSupersalud
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaCbCodigoSupersaludE(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        auxiliar = SysmanFunciones.nvl(
                        registroAux.getCampos().get(
                                        GeneralParameterEnum.CODIGO.getName()),
                        "").toString();
    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaCbCodigoSIA
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaCbCodigoSIA(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();

        auxiliar = SysmanFunciones.nvl(
                        registroAux.getCampos().get(
                                        GeneralParameterEnum.CODIGO
                                                        .getName()),
                        "").toString();

    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaCbCodigoSIA
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaCbCodigoSIAE(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();

        auxiliar = SysmanFunciones.nvl(
                        registroAux.getCampos().get(
                                        GeneralParameterEnum.CODIGO
                                                        .getName()),
                        "").toString();

    }
    // </METODOS_COMBOS_GRANDES>

    /**
     * Este metodo es invocado el metodo inicializar, se ejecutan las
     * acciones a tener en cuenta en el momento de apertura del
     * formulario
     */
    @Override
    public void abrirFormulario() {
        // <CODIGO_DESARROLLADO>

        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado cuando se cancela la edicion del registro
     */
    @Override
    public void cancelarEdicion(RowEditEvent event) {

    }

    /**
     * Metodo ejecutado antes de realizar la insercion del registro
     * 
     * @return true
     */
    @Override
    public boolean insertarAntes() {
        // <CODIGO_DESARROLLADO>
        registro.getCampos().put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
        registro.getCampos().put(GeneralParameterEnum.ANO.getName(), ano);
        // </CODIGO_DESARROLLADO>
        return true;
    }

    /**
     * Metodo ejecutado despues de realizar la insercion del registro
     * 
     * @return true
     */
    @Override
    public boolean insertarDespues() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
        return true;
    }

    /**
     * Metodo ejecutado antes de realizar la insercion y actualizacion
     * del registro
     * 
     * @return true
     */
    @Override
    public boolean actualizarAntes() {
        // <CODIGO_DESARROLLADO>
        registro.getCampos().put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
        registro.getCampos().put(GeneralParameterEnum.ANO.getName(), ano);
        registro.getCampos().remove(GeneralParameterEnum.NOMBRE.getName());
        registro.getCampos().remove(GeneralParameterEnum.MOVIMIENTO.getName());
        // </CODIGO_DESARROLLADO>
        return true;
    }

    /**
     * Metodo ejecutado despues de realizar la insercion y
     * actualizacion del registro
     * 
     * @return true
     */
    @Override
    public boolean actualizarDespues() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
        return true;
    }

    /**
     * Metodo ejecutado antes de realizar la eliminacion del registro
     * 
     * @return true
     */
    @Override
    public boolean eliminarAntes() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
        return true;
    }

    /**
     * Metodo ejecutado despues de realizar la eliminacion del
     * registro
     * 
     * @return true
     */
    @Override
    public boolean eliminarDespues() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
        return true;
    }

    /**
     * Este metodo se ejecuta antes enviar la accion de actualizacion,
     * en el se pueden remover valores auxiliares que no se desee o se
     * deban enviar en el registro
     */
    @Override
    public void removerCombos() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Este metodo es ejecutado despues de finalizar la insercion y
     * edicion del registro se usa cuando se desean agregar valores al
     * registro despues de dichas acciones
     */
    @Override
    public void asignarValoresRegistro() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    // <SET_GET_ATRIBUTOS>

    /**
     * @return the ano
     */
    public int getAno() {
        return ano;
    }

    /**
     * @param ano
     * the ano to set
     */
    public void setAno(int ano) {
        this.ano = ano;
    }

    // </SET_GET_ATRIBUTOS>
    // <SET_GET_PARAMETROS>
    // </SET_GET_PARAMETROS>
    // <SET_GET_LISTAS>
    /**
     * Retorna la lista listaCbAnio
     * 
     * @return listaCbAnio
     */
    public List<Registro> getListaCbAnio() {
        return listaCbAnio;
    }

    /**
     * Asigna la lista listaCbAnio
     * 
     * @param listaCbAnio
     * Variable a asignar en listaCbAnio
     */
    public void setListaCbAnio(List<Registro> listaCbAnio) {
        this.listaCbAnio = listaCbAnio;
    }

    // </SET_GET_LISTAS>
    // <SET_GET_LISTAS_COMBO_GRANDE>

    /**
     * Retorna la variable auxiliar
     * 
     * @return auxiliar
     */
    public String getAuxiliar() {
        return auxiliar;
    }

    /**
     * @return the listaCbCodigoSupersalud
     */
    public RegistroDataModelImpl getListaCbCodigoSupersalud() {
        return listaCbCodigoSupersalud;
    }

    /**
     * @param listaCbCodigoSupersalud
     * the listaCbCodigoSupersalud to set
     */
    public void setListaCbCodigoSupersalud(
        RegistroDataModelImpl listaCbCodigoSupersalud) {
        this.listaCbCodigoSupersalud = listaCbCodigoSupersalud;
    }

    /**
     * @return the listaCbCodigoSupersaludE
     */
    public RegistroDataModelImpl getListaCbCodigoSupersaludE() {
        return listaCbCodigoSupersaludE;
    }

    /**
     * @param listaCbCodigoSupersaludE
     * the listaCbCodigoSupersaludE to set
     */
    public void setListaCbCodigoSupersaludE(
        RegistroDataModelImpl listaCbCodigoSupersaludE) {
        this.listaCbCodigoSupersaludE = listaCbCodigoSupersaludE;
    }

    /**
     * @return the listaCbCodigoSIA
     */
    public RegistroDataModelImpl getListaCbCodigoSIA() {
        return listaCbCodigoSIA;
    }

    /**
     * @param listaCbCodigoSIA
     * the listaCbCodigoSIA to set
     */
    public void setListaCbCodigoSIA(RegistroDataModelImpl listaCbCodigoSIA) {
        this.listaCbCodigoSIA = listaCbCodigoSIA;
    }

    /**
     * @return the listaCbCodigoSIAE
     */
    public RegistroDataModelImpl getListaCbCodigoSIAE() {
        return listaCbCodigoSIAE;
    }

    /**
     * @param listaCbCodigoSIAE
     * the listaCbCodigoSIAE to set
     */
    public void setListaCbCodigoSIAE(RegistroDataModelImpl listaCbCodigoSIAE) {
        this.listaCbCodigoSIAE = listaCbCodigoSIAE;
    }

    /**
     * Asigna la variable auxiliar
     * 
     * @param auxiliar
     * Variable a asignar en auxiliar
     */
    public void setAuxiliar(String auxiliar) {
        this.auxiliar = auxiliar;
    }

    /**
     * @return the estado
     */
    public int getEstado() {
        return estado;
    }

    /**
     * @param estado
     * the estado to set
     */
    public void setEstado(int estado) {
        this.estado = estado;
    }

    /**
     * @return the tituloBtnCuentas
     */
    public String getTituloBtnCuentas() {
        return tituloBtnCuentas;
    }

    /**
     * @param tituloBtnCuentas
     * the tituloBtnCuentas to set
     */
    public void setTituloBtnCuentas(String tituloBtnCuentas) {
        this.tituloBtnCuentas = tituloBtnCuentas;
    }

    /**
     * @return the bloqueadoSIA
     */
    public boolean isBloqueadoSIA() {
        return bloqueadoSIA;
    }

    /**
     * @param bloqueadoSIA
     * the bloqueadoSIA to set
     */
    public void setBloqueadoSIA(boolean bloqueadoSIA) {
        this.bloqueadoSIA = bloqueadoSIA;
    }

    /**
     * @return the bloqueadoCircUnica
     */
    public boolean isBloqueadoCircUnica() {
        return bloqueadoCircUnica;
    }

    /**
     * @param bloqueadoCircUnica
     * the bloqueadoCircUnica to set
     */
    public void setBloqueadoCircUnica(boolean bloqueadoCircUnica) {
        this.bloqueadoCircUnica = bloqueadoCircUnica;
    }

    /**
     * Retorna la variable indice
     * 
     * @return indice
     */
    public int getIndice() {
        return indice;
    }

    /**
     * @param indice
     * the indice to set
     */
    public void setIndice(int indice) {
        this.indice = indice;
    }
    
    public boolean isCargarplantilla() {
		return cargarplantilla;
	}

	public void setCargarplantilla(boolean cargarplantilla) {
		this.cargarplantilla = cargarplantilla;
	}

	public StreamedContent getArchivoDescarga() {
		return archivoDescarga;
	}

	public void setArchivoDescarga(StreamedContent archivoDescarga) {
		this.archivoDescarga = archivoDescarga;
	}

	public ContenedorArchivo getContArchivocargarExcel() {
		return contArchivocargarExcel;
	}

	public void setContArchivocargarExcel(ContenedorArchivo contArchivocargarExcel) {
		this.contArchivocargarExcel = contArchivocargarExcel;
	}

	public String getCadena() {
		return cadena;
	}

	public void setCadena(String cadena) {
		this.cadena = cadena;
	}

	public String getHOJA_CONFIG() {
		return HOJA_CONFIG;
	}

	public int getContador() {
		return contador;
	}

	public void setContador(int contador) {
		this.contador = contador;
	}

	
    // </SET_GET_LISTAS_COMBO_GRANDE>
}
