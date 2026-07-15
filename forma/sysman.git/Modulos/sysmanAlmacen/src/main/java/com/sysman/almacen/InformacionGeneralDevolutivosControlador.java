/*-
 * InformacionGeneralDevolutivosControlador.java
 *
 * 1.0
 * 
 * 20/06/2018
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */
package com.sysman.almacen;

import com.ibm.icu.text.SimpleDateFormat;
import com.sysman.almacen.enums.InformacionGeneralDevolutivosControladorEnum;
import com.sysman.almacen.enums.InformacionGeneralDevolutivosControladorUrlEnum;
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
import com.sysman.recursos.ejb.EjbSysmanUtilRemote;
import com.sysman.reportes.Reporteador;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.util.SysmanFunciones;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.primefaces.event.SelectEvent;
import org.primefaces.model.StreamedContent;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.DataFormat;
import org.apache.poi.ss.usermodel.Row;

import net.sf.dynamicreports.report.exception.DRException;
import net.sf.jasperreports.engine.JRException;

/**
 * Esta clase es el controlador para el formulario INFORMACION GENERAL
 * DEVOLUTIVO en Access "INFORMACION_GENERAL_DEVOLUTIVO", el cual es
 * llamado desde Almacen\Informes\Generales\Informe General Devolutios
 *
 * 
 * @version 1.0, 20/06/2018
 * @author amonroy
 * 
 * @modified dcastiblanco
 * @version  2. Se añade la validacion del archivo plano
 */
@ManagedBean
@ViewScoped
public class InformacionGeneralDevolutivosControlador extends BeanBaseModal {
    /**
     * Constante a nivel de clase que almacena el codigo de la
     * compania en la cual inicio sesion el usuario, el valor de esta
     * constante es asignado en el constructor a la variable de sesion
     * correspondiente
     */
    private final String compania;
    /**
     * Constante a nivel de clase que almacena el codigo del modulo en
     * el cual esta trabajando el usuario, el valor de esta constante
     * es asignado en el constructor a la variable de sesion
     * correspondiente
     */
    private final String modulo;
    /**
     * Constante definida por el numero de veces que se realiza el
     * llamado al campo CODIGOELEMENTO en el formulario, almacena el
     * texto CODIGOELEMENTO
     */
    private final String cCodigoElemento;

    // <DECLARAR_ATRIBUTOS>
    /**
     * Atributo que almacena el codigo del elemento inicial
     * seleccionado en el formulario
     */
    private String centroCostoInicial;
    /**
     * Atributo que almacena el codigo del centro costo Final
     * seleccionado en el formulario
     */
    private String centroCostoFinal;
    /**
     * Atributo que almacena el nombre del centro costo inicial
     * seleccionado en el formulario
     */
    private String nombreCenCostoI;
    /**
     * Atributo que almacena el nombre del centro costo inicial
     * seleccionado en el formulario
     */
    private String nombreCenCostoF;
    
    private String elementoDesde;
    /**
     * Atributo que almacena el codigo del elemento final seleccionado
     * en el formulario
     */
    private String elementoHasta;
    /**
     * Atributo que almacena el nombre del elemento inicial
     * seleccionado en el formulario
     */
    private String nombreElementoDesde;
    /**
     * Atributo que almacena el codigo del elemento final seleccionado
     * en el formulario
     */
    private String nombreElementoHasta;

    /**
     * Atributo que almacena la fecha que ha sido seleccionada en el
     * formulario
     */
    private Date fecha;
    
    /**
     * Atributo que almacena la fecha que tiene formato de fecha y hora 
     * 
     */ 
     private String fechaAux;
    
    /**
     * Atributo usado para descargar contenidos de archivos desde la
     * vista
     */
    private StreamedContent archivoDescarga;
    // </DECLARAR_ATRIBUTOS>
    // <DECLARAR_PARAMETROS>
    // </DECLARAR_PARAMETROS>
    // <DECLARAR_LISTAS>
    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>
    /**
     * Listado de registros para el combo de Elemento Inicial
     */
    private RegistroDataModelImpl listaElementoDesde;
    /**
     * Listado de registros para el combo de Elemento Final
     */
    private RegistroDataModelImpl listaElementoHasta;
    /**
     * Listado de registros para el combo de Centro Costo Inicial y Final
     */
    private RegistroDataModelImpl listacbCentroCostoI;
    private RegistroDataModelImpl listaCbCentroCostoF;
    
    private boolean ckExcelplano;
    private boolean ckCentroCosto;
    private boolean ckInfEspecialDev;
    private boolean verCkInfEspecialDev = false;

    // </DECLARAR_LISTAS_COMBO_GRANDE>
    @EJB
    private EjbSysmanUtilRemote ejbSysmanUtil;
    /**
     * Crea una nueva instancia de
     * InformacionGeneralDevolutivosControlador
     */
    public InformacionGeneralDevolutivosControlador() {
        super();
        compania = SessionUtil.getCompania();
        modulo = SessionUtil.getModulo();
        cCodigoElemento = InformacionGeneralDevolutivosControladorEnum.CODIGOELEMENTO
                        .getValue();
        try {
            numFormulario = GeneralCodigoFormaEnum.INFORMACION_GENERAL_DEVOLUTIVOS_CONTROLADOR
                            .getCodigo();// 1833
            validarPermisos();
            // <INI_ADICIONAL>
            fecha = new Date();
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
        // <CARGAR_LISTA>
        // </CARGAR_LISTA>
        // <CARGAR_LISTA_COMBO_GRANDE>
        cargarListaElementoDesde();
        cargarListaElementoHasta();
        cargarlistaCentroCostoInicial();
        // </CARGAR_LISTA_COMBO_GRANDE>
        // <CREAR_ARBOLES>
        // </CREAR_ARBOLES>
        abrirFormulario();
    }

    /**
     * Este metodo es invocado el metodo inicializar, se ejecutan las
     * acciones a tener en cuenta en el momento de apertura del
     * formulario
     */
    @Override
    public void abrirFormulario() {
        String infEspecial = "";
    	try {
    		infEspecial = SysmanFunciones.nvl(ejbSysmanUtil.consultarParametro(compania,
    					  "USUARIOS QUE GENERAN INFORME GENERAL DE DEVOLUTIVOS ESPECIAL",
    					  modulo,new Date(),true),",").toString();
		} catch (SystemException e) {
			e.printStackTrace();
		}
    	String[] arrayUsuarios = infEspecial.split(",");
    	
    	if (Arrays.asList(arrayUsuarios).contains(SessionUtil.getUser().getCodigo())) {
    		verCkInfEspecialDev = true;
    	} else {
    		verCkInfEspecialDev = false;
    	}
        // </CODIGO_DESARROLLADO>
    }

    // <METODOS_CARGAR_LISTA>
    /**
     * 
     * Carga la lista listaElementoDesde
     *
     */
    public void cargarListaElementoDesde() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        InformacionGeneralDevolutivosControladorUrlEnum.URL4740
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        listaElementoDesde = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, cCodigoElemento);
    }
    /**
     * Carga la lista de centro costo en `listacbCentroCostoI`,  
     * obteniendo los datos desde un servicio basado en la URL correspondiente.  
     *  
     * Este método configura los parámetros necesarios, incluyendo la compañía  
     * y el año derivado de la fecha actual, para realizar la consulta  
     * y obtener la información de los centros de costo iniciales.  
     */
    public void cargarlistaCentroCostoInicial() {

                
                        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        InformacionGeneralDevolutivosControladorUrlEnum.URL3900
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.ANO.getName(), SysmanFunciones.ano(fecha));

        
        listacbCentroCostoI = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, GeneralParameterEnum.CODIGO.getName());
            }
    /**
     * Carga la lista de centro costo en `listacbCentroCostoF`,  
     * obteniendo los datos desde un servicio basado en la URL correspondiente.  
     *  
     * Este método configura los parámetros necesarios, incluyendo la compañía  
     * y el año derivado de la fecha actual, para realizar la consulta  
     * y obtener la información de los centros de costo Final.  
     */
    public void cargarlistaCentroCostoFinal() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        InformacionGeneralDevolutivosControladorUrlEnum.URL4523
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.ANO.getName(), SysmanFunciones.ano(fecha));
        param.put(GeneralParameterEnum.CENTRO_COSTO.getName(), centroCostoInicial);


        listaCbCentroCostoF =new RegistroDataModelImpl(urlBean.getUrl(),
                urlBean.getUrlConteo().getUrl(), param,
                true, GeneralParameterEnum.CODIGO.getName());
    }

    /**
     * 
     * Carga la lista listaElementoHasta
     *
     */
    public void cargarListaElementoHasta() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        InformacionGeneralDevolutivosControladorUrlEnum.URL5626
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(InformacionGeneralDevolutivosControladorEnum.ELEMENTOINICIAL
                        .getValue(), elementoDesde);

        listaElementoHasta = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, cCodigoElemento);
    }

    // </METODOS_CARGAR_LISTA>
    // <METODOS_BOTONES>
    /**
     * 
     * Metodo ejecutado al oprimir el boton BtnPdf en la vista
     *
     * Hace el llamado al metodo "generarInforme" indicando el formato
     * con el que se desea generar el informe
     *
     */
    public void oprimirBtnPdf() {
        // <CODIGO_DESARROLLADO>
        archivoDescarga = null;
        generarInforme(FORMATOS.PDF);
        // </CODIGO_DESARROLLADO>
    }

    /**
     * 
     * Metodo ejecutado al oprimir el boton BtnExcel en la vista
     *
     * Hace el llamado al metodo "generarInforme" indicando el formato
     * con el que se desea generar el informe
     *
     */
    public void oprimirBtnExcel() {
        // <CODIGO_DESARROLLADO>
        archivoDescarga = null;
        generarInforme(FORMATOS.EXCEL);
        // </CODIGO_DESARROLLADO>
    }

    // </METODOS_BOTONES>
    // <METODOS_CAMBIAR>
    /**
     * Metodo ejecutado al cambiar el control ckCentroCosto
     * 
     * 
     */
    public void cambiarckCentroCosto() {
    }
    // </METODOS_CAMBIAR>
    // <METODOS_COMBOS_GRANDES>
    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaElementoDesde
     *
     * Asigna valor al atributo "nombreElementoDesde" y recarga la
     * lista "listaElementoHasta"
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaElementoDesde(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        elementoDesde = retornarString(registroAux, cCodigoElemento);
        nombreElementoDesde = retornarString(registroAux, "NOMBRELARGO");
        elementoHasta = nombreElementoHasta = null;
        cargarListaElementoHasta();
    }
    /**
     * Maneja la selección de una fila en la lista de centros de costo iniciales.  
     *  
     * Al seleccionar un centro de costo inicial, este método extrae su código  
     * y nombre desde el evento, los asigna a las variables correspondientes  
     * y reinicia los valores del centro de costo final.  
     * Luego, carga la lista de centros de costo finales con base en la selección.  
     *
     * @param event Evento de selección que contiene la fila seleccionada.
     */
    public void seleccionarFilacbCentroCostoI(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        
        centroCostoInicial = SysmanFunciones.toString(
                registroAux.getCampos().get(
                                GeneralParameterEnum.CODIGO.getName()));
        nombreCenCostoI = SysmanFunciones.toString(
                registroAux.getCampos().get(
                                GeneralParameterEnum.NOMBRE.getName()));
        
        centroCostoFinal = nombreCenCostoF = null;
        cargarlistaCentroCostoFinal();
    }
    
    /**
     * Maneja la selección de una fila en la lista de centros de costo finales.  
     *  
     * Al seleccionar un centro de costo final, este método extrae su código  
     * y nombre desde el evento y los asigna a las variables correspondientes.  
     *
     * @param event Evento de selección que contiene la fila seleccionada.
     */
    public void seleccionarFilaCbCentroCostoF(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
 
        centroCostoFinal = SysmanFunciones.toString(
                registroAux.getCampos().get(
                                GeneralParameterEnum.CODIGO.getName()));
        nombreCenCostoF = SysmanFunciones.toString(
                registroAux.getCampos().get(
                                GeneralParameterEnum.NOMBRE.getName()));

    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaElementoHasta
     *
     * Asigna valor al atributo "nombreElementoHasta"
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaElementoHasta(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        elementoHasta = retornarString(registroAux, cCodigoElemento);
        nombreElementoHasta = retornarString(registroAux, "NOMBRELARGO");
    }

    // </METODOS_COMBOS_GRANDES>
    // <METODOS_ARBOL>
    /**
     * Define las acciones necesarias para generar el informe realiza
     * el reemplazo de valores en la consulta del informe y envía los
     * parámetros definidos
     * 
     * @param formato
     * Formato seleccionado por el usuario para generar el informe
     */
    private void generarInforme(FORMATOS formato) {

        try {
        	String informe = "";
        	boolean informeTel = "SI".equals(SysmanFunciones.nvl(ejbSysmanUtil.consultarParametro(compania,
        							"MANEJA DEPENDENCIA-UBICACION",modulo,new Date(),true),"NO"));
        	
        	boolean informeResumen = "SI".equals(SysmanFunciones.nvlStr(ejbSysmanUtil.consultarParametro(compania,
    									"MANEJA INFORME RESUMEN EN ALMACEN", modulo, new Date(), true),"NO"));

        	if(informeTel && formato.equals(FORMATOS.EXCEL)) {
        		informe = "002691InventarioDevolutivoTel";
        	} else {
        		informe = "001812InventarioDevolutivo";
        	}
        	if (ckCentroCosto) {
                informe = "002755INVENTARIODEVOLUTIVONIIFCC"; 
            }
        	if(ckInfEspecialDev) {
        		if(formato.equals(FORMATOS.EXCEL)) {
        			informe = "002723InventarioDevolutivoEspEx";
        		} else {
        			informe = "002722InventarioDevolutivoEsp";
        		}
        	}
            //Se deja el formato de la fecha con la ultima hora del dia para que se tenga en cuenta todos los movimientos de la fecha de corte
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy 23:59:59");
            fechaAux = sdf.format(fecha);            
            String fechaCadena = SysmanFunciones.convertirAFechaCadena(fecha);

            // PARAMETROS DE REEMPLAZO EN LA CONSULTA
            Map<String, Object> reemplazar = new HashMap<>();
            reemplazar.put("ultimoDiaFecha", fechaAux);
            reemplazar.put("compania", compania);
            reemplazar.put("elementoInicial", elementoDesde);
            reemplazar.put("elementoFinal", elementoHasta);
            reemplazar.put("fechaCorte", fechaCadena);
            if(ckCentroCosto) {
                reemplazar.put("centroCostoIni", centroCostoInicial);
                reemplazar.put("centroCostoFin", centroCostoFinal);
            }
            // PARAMETROS PARA GENERACION DE INFORME
            Map<String, Object> parametros = new HashMap<>();
            
            String formatosUspec = SysmanFunciones.nvl(ejbSysmanUtil.consultarParametro(compania,
            					"FORMATOS UNICOS USPEC", modulo, new Date(), false), "NO").toString();

            parametros.put("PR_FECHAS", fechaCadena);
            
            if ("SI".equals(formatosUspec)) {
            	parametros.put("PR_FORMATOS_USPEC", true);
            } else {
            	parametros.put("PR_FORMATOS_USPEC", false);
            }
            
            if(ckExcelplano == true) 
            {
            	if(informeTel) {
            		informe = "800685InventarioDevolutivoTel";
            	} else {
            		informe = "800418InventarioDevolutivo";
            	}
            	if(ckInfEspecialDev) {
            		informe = "800696InventarioDevolutivoEsp";
            	}
            	
            	try {
            		
            		String datosExcel = Reporteador.resuelveConsulta(informe, 
                			Integer.parseInt(modulo),
                			reemplazar);
            	
	            	if(informeResumen) {
	            		
	            		archivoDescarga = generarReporteConsolidadoResumen(informe, datosExcel, reemplazar, compania, modulo);
	            		return;
	                    
	            	} else {
	            	
	            		archivoDescarga = JsfUtil.exportarHojaDatosStreamed(datosExcel, 
	            				ConectorPool.ESQUEMA_SYSMAN,
	            				FORMATOS.EXCEL,informe);
	            	}

            	} catch (SQLException | DRException e) {
            		((Throwable) e).printStackTrace();
            	} catch (Exception e) {
                    logger.error(e.getMessage(), e);
                    JsfUtil.agregarMensajeError(e.getMessage());
                }

            }
            

            Reporteador.resuelveConsulta(informe,
                            Integer.parseInt(modulo), reemplazar,
                            parametros);

            archivoDescarga = JsfUtil.exportarStreamed(
                            informe,
                            parametros,
                            ConectorPool.ESQUEMA_SYSMAN,
                            formato);
        }
        catch (JRException | IOException | SysmanException | ParseException | SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }
    
    private StreamedContent generarReporteConsolidadoResumen(String nombreReporte, String strSql, Map<String, Object> reemplazar, String compania, String modulo) throws Exception {
        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            
            Workbook workbook = new XSSFWorkbook(JsfUtil.exportarHojaDatosStreamed(strSql, ConectorPool.ESQUEMA_SYSMAN, FORMATOS.EXCEL).getStream());
            Sheet sheet = workbook.getSheetAt(0);

            CellStyle contabilidadStyle = workbook.createCellStyle();
            DataFormat df = workbook.createDataFormat();
            contabilidadStyle.setDataFormat(df.getFormat("#,##0.00"));

            Row r4 = sheet.createRow(sheet.getLastRowNum() + 1);
            Cell cellT = r4.createCell(4);
            cellT.setCellValue("TOTAL GENERAL");

            String[] columnas = { "F", "G", "H", "I"};
            for (int i = 0; i < columnas.length; i++) {
                Cell cell = r4.createCell(i + 5);
                cell.setCellFormula("SUM(" + columnas[i] + "2:" + columnas[i] + (sheet.getLastRowNum()) + ")");
                cell.setCellStyle(contabilidadStyle);
            }
            
            String fechaCadena = SysmanFunciones.convertirAFechaCadena(fecha);
            Map<String, Object> param = new HashMap<>();
            param.put("COMPANIA", compania);
            param.put("FECHACORTE", fechaCadena);
            param.put("ULTIMODIA", fechaAux);
            param.put("ELEMENTOINICIAL", elementoDesde);
            param.put("ELEMENTOFINAL", elementoHasta);

            List<Registro> listaReporte2 = RegistroConverter.toListRegistro(
                requestManager.getList(
                    UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(InformacionGeneralDevolutivosControladorUrlEnum.URL179006.getValue())
                        .getUrl(),
                    param));

            int filaTotalGeneral = sheet.getLastRowNum();
            int filaEncabezado = filaTotalGeneral + 2;
            Row headerRow = sheet.createRow(filaEncabezado);

            String[] encabezados = {
                "CUENTA_CONTABLE", "VALOR_HISTORICO", "VALOR_LIBROS",
                "DEPRECIACION_MENSUAL", "DEPRECIACION_ACUMULADA"};

            int col = 4;
            for (String encabezado : encabezados) {
                Cell cell = headerRow.createCell(col++);
                cell.setCellValue(encabezado);
            }

            int filaInicioDatos = filaEncabezado + 1;
            int filaDatos = filaInicioDatos;
            for (Registro reg : listaReporte2) {
                Row dataRow = sheet.createRow(filaDatos++);

                dataRow.createCell(4).setCellValue(String.valueOf(reg.getCampos().get("CUENTA_CONTABLE")));

                Cell cell2 = dataRow.createCell(5);
                cell2.setCellValue(parseDouble(reg.getCampos().get("VALOR_HISTORICO")));
                cell2.setCellStyle(contabilidadStyle);

                Cell cell3 = dataRow.createCell(6);
                cell3.setCellValue(parseDouble(reg.getCampos().get("VALOR_LIBROS")));
                cell3.setCellStyle(contabilidadStyle);

                Cell cell4 = dataRow.createCell(7);
                cell4.setCellValue(parseDouble(reg.getCampos().get("DEPRECIACION_MENSUAL")));
                cell4.setCellStyle(contabilidadStyle);

                Cell cell5 = dataRow.createCell(8);
                cell5.setCellValue(parseDouble(reg.getCampos().get("DEPRECIACION_ACUMULADA")));
                cell5.setCellStyle(contabilidadStyle);
            }
            
            Row rt = sheet.createRow(sheet.getLastRowNum() + 1);
            Cell cellT2 = rt.createCell(4);
            cellT2.setCellValue("TOTAL GENERAL");

            String[] columnasT = { "F", "G", "H", "I"};
            for (int i = 0; i < columnasT.length; i++) {
            	Cell cell = rt.createCell(i + 5);
                cell.setCellFormula("SUM(" + columnasT[i] + filaInicioDatos + ":" + columnasT[i] + sheet.getLastRowNum() + ")");
                cell.setCellStyle(contabilidadStyle);
            }

            workbook.write(out);
            workbook.close();
            
            return JsfUtil.getArchivoDescarga(new ByteArrayInputStream(out.toByteArray()), nombreReporte + ".xlsx");
        }
    }
    
    private double parseDouble(Object val) {
        try {
            return Double.parseDouble(String.valueOf(val));
        } catch (Exception e) {
            return 0.0;
        }
    }

    /**
     * Evalua si el campo ingresado por parametro se encuentra nulo
     * dentro del registro que tambien ha sido ingresado por parametro
     * 
     * @param reg
     * Registro en el que se desea evaluar el campo
     * @param campo
     * Campo que se desea consultar
     * @return Cadena vacia o el valor del campo
     */
    private String retornarString(Registro reg, String campo) {
        return SysmanFunciones.validarCampoVacio(reg.getCampos(), campo) ? ""
            : reg.getCampos().get(campo).toString();
    }

    // </METODOS_ARBOL>
    // <SET_GET_ATRIBUTOS>
    /**
     * Retorna la variable elementoDesde
     * 
     * @return elementoDesde
     */
    public String getElementoDesde() {
        return elementoDesde;
    }

    /**
     * Asigna la variable elementoDesde
     * 
     * @param elementoDesde
     * Variable a asignar en elementoDesde
     */
    public void setElementoDesde(String elementoDesde) {
        this.elementoDesde = elementoDesde;
    }

    /**
     * Retorna la variable elementoHasta
     * 
     * @return elementoHasta
     */
    public String getElementoHasta() {
        return elementoHasta;
    }

    /**
     * Asigna la variable elementoHasta
     * 
     * @param elementoHasta
     * Variable a asignar en elementoHasta
     */
    public void setElementoHasta(String elementoHasta) {
        this.elementoHasta = elementoHasta;
    }

    /**
     * Retorna la variable fecha
     * 
     * @return fecha
     */
    public Date getFecha() {
        return fecha;
    }

    /**
     * Asigna la variable fecha
     * 
     * @param fecha
     * Variable a asignar en fecha
     */
    public void setFecha(Date fecha) {
        this.fecha = fecha;
    }

    /**
     * Atributo usado para descargar contenidos de archivos desde la
     * vista
     */
    public StreamedContent getArchivoDescarga() {
        return archivoDescarga;
    }

    // </SET_GET_ATRIBUTOS>
    // <SET_GET_PARAMETROS>
    // </SET_GET_PARAMETROS>
    // <SET_GET_LISTAS>
    // </SET_GET_LISTAS>
    // <SET_GET_LISTAS_COMBO_GRANDE>
    /**
     * Retorna la lista listaElementoDesde
     * 
     * @return listaElementoDesde
     */
    public RegistroDataModelImpl getListaElementoDesde() {
        return listaElementoDesde;
    }

    /**
     * Asigna la lista listaElementoDesde
     * 
     * @param listaElementoDesde
     * Variable a asignar en listaElementoDesde
     */
    public void setListaElementoDesde(
        RegistroDataModelImpl listaElementoDesde) {
        this.listaElementoDesde = listaElementoDesde;
    }

    /**
     * Retorna la lista listaElementoHasta
     * 
     * @return listaElementoHasta
     */
    public RegistroDataModelImpl getListaElementoHasta() {
        return listaElementoHasta;
    }

    /**
     * Asigna la lista listaElementoHasta
     * 
     * @param listaElementoHasta
     * Variable a asignar en listaElementoHasta
     */
    public void setListaElementoHasta(
        RegistroDataModelImpl listaElementoHasta) {
        this.listaElementoHasta = listaElementoHasta;
    }
    // </SET_GET_LISTAS_COMBO_GRANDE>

    public String getNombreElementoDesde() {
        return nombreElementoDesde;
    }

    public void setNombreElementoDesde(String nombreElementoDesde) {
        this.nombreElementoDesde = nombreElementoDesde;
    }

    public String getNombreElementoHasta() {
        return nombreElementoHasta;
    }

    public void setNombreElementoHasta(String nombreElementoHasta) {
        this.nombreElementoHasta = nombreElementoHasta;
    }

	/**
	 * @return the ckExcelplano
	 */
	public boolean isCkExcelplano() {
		return ckExcelplano;
	}

	/**
	 * @param ckExcelplano the ckExcelplano to set
	 */
	public void setCkExcelplano(boolean ckExcelplano) {
		this.ckExcelplano = ckExcelplano;
	}
	
	/**
	 * @return the ckInfEspecialDev
	 */
	public boolean isCkInfEspecialDev() {
		return ckInfEspecialDev;
	}

	/**
	 * @param ckInfEspecialDev the ckInfEspecialDev to set
	 */
	public void setCkInfEspecialDev(boolean ckInfEspecialDev) {
		this.ckInfEspecialDev = ckInfEspecialDev;
	}
	
	/**
	 * @return the verCkInfEspecialDev
	 */
	public boolean isVerCkInfEspecialDev() {
		return verCkInfEspecialDev;
	}

	/**
	 * @param verCkInfEspecialDev the verCkInfEspecialDev to set
	 */
	public void setVerCkInfEspecialDev(boolean verCkInfEspecialDev) {
		this.verCkInfEspecialDev = verCkInfEspecialDev;
	}
	
    /**
     * @return the ckCentroCosto
     */
    public boolean isCkCentroCosto() {
            return ckCentroCosto;
    }

    /**
     * @param ckCentroCosto the ckCentroCosto to set
     */
    public void setCkCentroCosto(boolean ckCentroCosto) {
            this.ckCentroCosto = ckCentroCosto;
    }

    /**
     * @return the centroCostoInicial
     */
    public String getCentroCostoInicial() {
            return centroCostoInicial;
    }

    /**
     * @param centroCostoInicial the centroCostoInicial to set
     */
    public void setCentroCostoInicial(String centroCostoInicial) {
            this.centroCostoInicial = centroCostoInicial;
    }

    /**
     * @return the centroCostoFinal
     */
    public String getCentroCostoFinal() {
            return centroCostoFinal;
    }

    /**
     * @param centroCostoFinal the centroCostoFinal to set
     */
    public void setCentroCostoFinal(String centroCostoFinal) {
            this.centroCostoFinal = centroCostoFinal;
    }

    /**
     * @return the nombreCenCostoI
     */
    public String getNombreCenCostoI() {
            return nombreCenCostoI;
    }

    /**
     * @param nombreCenCostoI the nombreCenCostoI to set
     */
    public void setNombreCenCostoI(String nombreCenCostoI) {
            this.nombreCenCostoI = nombreCenCostoI;
    }

    /**
     * @return the nombreCenCostoF
     */
    public String getNombreCenCostoF() {
            return nombreCenCostoF;
    }

    /**
     * @param nombreCenCostoF the nombreCenCostoF to set
     */
    public void setNombreCenCostoF(String nombreCenCostoF) {
            this.nombreCenCostoF = nombreCenCostoF;
    }

    /**
     * @return the listacbCentroCostoI
     */
    public RegistroDataModelImpl getListacbCentroCostoI() {
            return listacbCentroCostoI;
    }

    /**
     * @param listacbCentroCostoI the listacbCentroCostoI to set
     */
    public void setListacbCentroCostoI(RegistroDataModelImpl listacbCentroCostoI) {
            this.listacbCentroCostoI = listacbCentroCostoI;
    }

    /**
     * @return the listaCbCentroCostoF
     */
    public RegistroDataModelImpl getListaCbCentroCostoF() {
            return listaCbCentroCostoF;
    }

    /**
     * @param listaCbCentroCostoF the listaCbCentroCostoF to set
     */
    public void setListaCbCentroCostoF(RegistroDataModelImpl listaCbCentroCostoF) {
            this.listaCbCentroCostoF = listaCbCentroCostoF;
    }
            

}
