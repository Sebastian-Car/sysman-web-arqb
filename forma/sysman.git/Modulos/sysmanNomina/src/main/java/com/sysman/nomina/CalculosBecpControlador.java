/*-
 * CalculosBecpControlador.java
 *
 * 1.0
 * 
 * 09/04/2026
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */
package com.sysman.nomina;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
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
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.primefaces.event.SelectEvent;
import org.primefaces.model.StreamedContent;
import com.sysman.beanbase.BeanBaseDatosAcmeImpl;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.nomina.ejb.EjbNominaDosRemote;
import com.sysman.recursos.ejb.EjbSysmanUtilRemote;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.util.ContenedorArchivo;
import com.sysman.util.SysmanFunciones;
import net.sf.jasperreports.engine.JRException;
/**
 * TODO Ingrese una descripcion para la clase.
 *
 * @version 1.0, 09/04/2026
 * @author jcrojas2
 */
@ManagedBean
@ViewScoped
public class CalculosBecpControlador extends BeanBaseDatosAcmeImpl{
    /**
     * Constante a nivel de clase que almacena el codigo de la
     * compania en la cual inicio sesion el usuario, el valor de esta
     * constante es asignado en el constructor a la variable de sesion
     * correspondiente
     */
    private final String compania ; 
    /**
     * Variable del indicador ingreso
     */
    private boolean ingreso = false; 
    /**
     * Variable que permite visualizar el BT4522 Y el LB58683
     */
    private boolean cargarIngreso;
    /**
     * Variable que almacena el codigo del proceso
     */
    private String proceso;
    /**
     * Variable que almacena el anio
     */
    private String anio;
    /**
     * Variable que almacena el mes
     */
    private String mes;
    /**
     * Vaiable que almacena el periodo
     */
    private String periodo;
    /**
     * Variable que almacena el id del empleado
     */
    private String idEmpleado;
    /**
     * Variable que almacena el nombre del archivo que se va a descargar
     */
    private String nombreArchivo;
    /**
     * Atributo usado para descargar contenidos de archivos desde la
     * vista
     */
    private StreamedContent archivoDescarga;
    /**
     * Este atributo se usa como auxiliar del componente selector de
     * archivos archivoModelo y funciona como contenedor del archivo que se
     * debe guardar
     */
    private ContenedorArchivo contArchivoarchivoModelo;
    /**
     * Lista del proceso
     */
    private List<Registro> listaproceso;
    /**
     * Lista del anio
     */
    private List<Registro> listaanio;
    /**
     * Lista del mes
     */
    private List<Registro> listaMes;
    /**
     * Lista del periodo
     */
    private List<Registro> listaperiodo;
    /**
     * Lista de empleados
     */
    private RegistroDataModelImpl listaidEmpleado;
    
    @EJB
    private EjbNominaDosRemote ejbNominaDos;
    @EJB
    private EjbSysmanUtilRemote ejbSysmanUtil;
    /**
     * Crea una nueva instancia de CalculosBecpControlador
     */
	public CalculosBecpControlador() {
		super();
	    compania = SessionUtil.getCompania();	    
	    try {
			numFormulario = GeneralCodigoFormaEnum.CALCULOS_BECP_CONTROLADOR.getCodigo();
			contArchivoarchivoModelo = new ContenedorArchivo();
			proceso = (String) SessionUtil.getSessionVar("procesoNomina");
			anio = (String) SessionUtil.getSessionVar("anioNomina");
			mes = (String) SessionUtil.getSessionVar("mesNomina");
			periodo = (String) SessionUtil.getSessionVar("periodoNomina");
			nombreArchivo = "B_E_C_P_" + anio + "_" + mes + "_" + periodo;
			cargarIngreso = ingreso;
			validarPermisos();
		} catch (Exception ex) {
			Logger.getLogger(CalculosBecpControlador.class.getName()).log(Level.SEVERE,null,ex);
			SessionUtil.redireccionarMenuPermisos();
        }
    }
    /**
     * En este metodo se hace la invocacion de lo metodos de carga de
     * todas las listas, menos las que son de subformularios
     */
	@Override
	public void iniciarListas() {
		cargarListaidEmpleado();
		cargarListaproceso();
		cargarListaanio();
		cargarListaMes();
		cargarListaperiodo();
	}
    /**
     * En este metodo se hace la invocacion de lo metodos de carga de
     * todas las listas que son de subformularios
     */
	@Override
	public void iniciarListasSub() {}
    /**
     * En este metodo se iguala a null todas las listas de los
     * subformularios
     */
	@Override
	public void iniciarListasSubNulo() {}
    /**
     * Este metodo se ejecuta justo despues de que el objeto de la
     * clase del Bean ha sido creado, en este se realizan las
     * asignaciones iniciales necesarias para la visualizacion del
     * formulario, como son tablas, origenes de datos, inicializacion
     * de listas y demas necesarios
     */
    @PostConstruct
    public void inicializar() {
    	tabla = "";
    	asignarOrigenDatos();
    	iniciarListas();
	}
    /**
     * Se realiza la asignacion de la variable origenDatos por la
     * consulta correspondiente del formulario
     * 
     * 
     */
    @Override
    public void asignarOrigenDatos() {}
    /**
     * 
     * Carga la lista listaproceso
     *
     */
    public void cargarListaproceso() {
    	Map<String,Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(),compania);
        try {
            listaproceso = RegistroConverter.toListRegistro(
            				requestManager.getList(UrlServiceUtil.getInstance()
            						.getUrlServiceByUrlByEnumID("537004").getUrl(),param));
        } catch (SystemException e) {
            JsfUtil.agregarMensajeError(e.getMessage());
            logger.error(e.getMessage(),e);
        }
    }
    /**
     * 
     * Carga la lista listaanio
     *
     */
    public void cargarListaanio() {
    	Map<String,Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(),compania);
        param.put(GeneralParameterEnum.ID_DE_PROCESO.getName(),proceso);
        try {
            listaanio = RegistroConverter.toListRegistro(
                         requestManager.getList(UrlServiceUtil.getInstance()
                                 .getUrlServiceByUrlByEnumID("471008").getUrl(),param));
        } catch (SystemException e) {
            JsfUtil.agregarMensajeError(e.getMessage());
            logger.error(e.getMessage(),e);
        }
    }
    /**
     * 
     * Carga la lista listaMes
     *
     */
    public void cargarListaMes() {
    	Map<String,Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(),compania);
        param.put(GeneralParameterEnum.ID_DE_PROCESO.getName(),proceso);
        param.put(GeneralParameterEnum.ANO.getName(),anio);
        try {
            listaMes = RegistroConverter.toListRegistro(
                        requestManager.getList(UrlServiceUtil.getInstance()
                                .getUrlServiceByUrlByEnumID("471049").getUrl(),param));
        } catch (SystemException e) {
            JsfUtil.agregarMensajeError(e.getMessage());
            logger.error(e.getMessage(),e);
        }
    }
    /**
     * 
     * Carga la lista listaperiodo
     *
     */
    public void cargarListaperiodo() {
    	Map<String,Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(),compania);
        param.put(GeneralParameterEnum.ID_DE_PROCESO.getName(),proceso);
        param.put(GeneralParameterEnum.ANO.getName(),anio);
        param.put(GeneralParameterEnum.MES.getName(),mes);
        try {
            listaperiodo = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                    .getUrlServiceByUrlByEnumID("471050").getUrl(),param));
        } catch (SystemException e) {
            JsfUtil.agregarMensajeError(e.getMessage());
            logger.error(e.getMessage(),e);
        }
    }
    /**
     * 
     * Carga la lista listaidEmpleado
     *
     */
    public void cargarListaidEmpleado() {
    	Map<String,Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(),compania);
		
    	UrlBean urlBean = UrlServiceUtil.getInstance().getUrlServiceByUrlByEnumID("210101");
		
		listaidEmpleado = new RegistroDataModelImpl(urlBean.getUrl(),
		                  urlBean.getUrlConteo().getUrl(),param,
		                  true,GeneralParameterEnum.ID_DE_EMPLEADO.getName());
    }
    /**
     * Metodo ejecutado al cambiar el control proceso
     * 
     * 
     */
    public void cambiarproceso() {
    	cargarListaanio();
        anio = null;
        mes = null;
        periodo = null;
        listaMes = null;
        listaperiodo = null;
        nombreArchivo = null;
        cambiaranio();
        cambiarMes();
    }
    /**
     * Metodo ejecutado al cambiar el control anio
     * 
     * 
     */
    public void cambiaranio() {
    	cargarListaMes();
        mes = null;
        periodo = null;
        listaperiodo = null;
        nombreArchivo = null;
        cambiarMes();
    }
    /**
     * Metodo ejecutado al cambiar el control Mes
     * 
     * 
     */
    public void cambiarMes() {
    	cargarListaperiodo();
        periodo = null;
        nombreArchivo = null;
    }
    /**
     * Metodo ejecutado al cambiar el control periodo
     * 
     * 
     */
    public void cambiarperiodo() {
    	nombreArchivo = "B_E_C_P_" + anio + "_" + mes + "_" + periodo;
    }
    /**
     * Metodo ejecutado al cambiar el control ingreso
     * 
     * 
     */
    public void cambiaringreso() {
    	cargarIngreso = ingreso;
    }	
    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaidEmpleado
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
	public void seleccionarFilaidEmpleado(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		idEmpleado = SysmanFunciones.nvl(registroAux.getCampos().get(GeneralParameterEnum.ID_DE_EMPLEADO.getName()),"").toString();
	}
	/**
	 * Valida que se suba un archivo
	 * 
	 * @return
	 */
	public boolean validarArchivo() {
		if (contArchivoarchivoModelo.getArchivo() == null) {
			JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB4001"));
			return false;
		} else {
			return true;
		}
	}

	private void setString(Sheet hoja,int fila,int columna,String valor) {
	    Row row = hoja.getRow(fila);
	    if (row == null) row = hoja.createRow(fila);

	    Cell cell = row.getCell(columna);
	    if (cell == null) cell = row.createCell(columna);

	    cell.setCellValue(valor);
	}
	
	private void setFecha(Sheet hoja,int fila,int columna,Date fecha,CellStyle style) {
	    Row row = hoja.getRow(fila);
	    if (row == null) row = hoja.createRow(fila);

	    Cell cell = row.getCell(columna);
	    if (cell == null) cell = row.createCell(columna);

	    cell.setCellValue(fecha);
	    cell.setCellStyle(style);
	}
	
	private void setFechaTexto(Sheet hoja,int fila,int columna,Date fecha) {
	    Row row = hoja.getRow(fila);
	    if (row == null) row = hoja.createRow(fila);

	    Cell cell = row.getCell(columna);
	    if (cell == null) cell = row.createCell(columna);

	    SimpleDateFormat sdf = new SimpleDateFormat("dd-MMM-yyyy",new Locale("es","ES"));

	    cell.setCellValue(sdf.format(fecha).toLowerCase());
	    cell.setCellType(CellType.STRING);
	}
	
	private void setNumero(Sheet hoja,int fila,int columna,double valor) {
	    Row row = hoja.getRow(fila);
	    if (row == null) row = hoja.createRow(fila);

	    Cell cell = row.getCell(columna);
	    if (cell == null) cell = row.createCell(columna);

	    cell.setCellValue(valor);
	}
	
	private void crearBecp() {
		int empl = 0;
        if (idEmpleado == null || idEmpleado.equals("")) {
        	empl = 0;
        } else {
        	empl = Integer.parseInt(idEmpleado);
        }
        try {
        	ejbNominaDos.insertBecp(compania,empl,Integer.parseInt(proceso),
        			Integer.parseInt(anio),Integer.parseInt(mes),Integer.parseInt(periodo),SessionUtil.getUser().getCodigo());
		} catch (NumberFormatException | SystemException e) {
			e.printStackTrace();
		}
    }
	
	private void limpiarFilas(Sheet hoja,int filaIni,int filaFin,int colIni,int colFin) {
	    for (int rowI = filaIni; rowI <= filaFin; rowI++) {
	        Row row = hoja.getRow(rowI);
	        if (row != null) {
	            for (int col = colIni; col <= colFin; col++) {
	                Cell cell = row.getCell(col);
	                if (cell != null) {
	                    cell.setCellValue(""); 
	                }
	            }
	        }
	    }
	}
    /**
     * 
     * Metodo ejecutado al oprimir el boton enviarExcel
     * en la vista
     *
     *
     */
	public void oprimirenviarExcel() {
		archivoDescarga = null;   
		String rutaArchivo = "";
		String extension = "";
		Workbook workbook = null;
		Date fechaIni = null;
		Date fechaFin = null;
		Date fechaFinAjustada = null;
		Date fechaInicioAno = null;
		Map<String,Object> param = new TreeMap<>();
		String procedimJunio = "";
		String sumarBasp2351 = "";
		if (!validarArchivo()) {
			return;
		}
		if (SysmanFunciones.validarVariableVacio(proceso)
	         || SysmanFunciones.validarVariableVacio(anio)
	         || SysmanFunciones.validarVariableVacio(mes)
	         || SysmanFunciones.validarVariableVacio(periodo)
	         || SysmanFunciones.validarVariableVacio(nombreArchivo)) {
	            JsfUtil.agregarMensajeError(idioma.getString("TB_TB1906"));
	            return;
	    }
		try (FileInputStream file = new FileInputStream(contArchivoarchivoModelo.getArchivo());) {
			procedimJunio = ejbSysmanUtil.consultarParametro(compania,
					"PROCEDIMIENTO CALCULO PRIMA DE JUNIO",SessionUtil.getModulo(),new Date(),false);
			sumarBasp2351 = ejbSysmanUtil.consultarParametro(compania,
                    "SUMAR BASP A PRIMA SERVICIOS DECRETO 2351",SessionUtil.getModulo(),new Date(),false);
			rutaArchivo = contArchivoarchivoModelo.getArchivo().getPath();
			extension = rutaArchivo.substring(rutaArchivo.indexOf('.'),rutaArchivo.length()).substring(1,
					rutaArchivo.substring(rutaArchivo.indexOf('.'),rutaArchivo.length()).length());

			if ("xls".equals(extension)) {
				workbook = new HSSFWorkbook(file);
			} else {
				workbook = new XSSFWorkbook(file);
			}				
			fechaIni = SysmanFunciones.convertirAFecha("01/" + mes + "/" + anio,"dd/MM/yyyy");
			fechaFin = SysmanFunciones.ultimoDiaDate(fechaIni);
			fechaFinAjustada = SysmanFunciones.sumarRestarDiasFecha(fechaFin,
					SysmanFunciones.dia(fechaFin)==31?-1:0);
			fechaInicioAno = SysmanFunciones.convertirAFecha("01/01/" + anio,"dd/MM/yyyy");
				
			CreationHelper helper = workbook.getCreationHelper();				
			CellStyle dateFormat = workbook.createCellStyle();
			dateFormat.setDataFormat(helper.createDataFormat().getFormat("dd/MM/yyyy"));				
				
			Sheet migrarCaus = workbook.getSheet("MIGRAR_CAUSACION");
	        Sheet basp = workbook.getSheet("B_A_S_P_");
	        Sheet serv = workbook.getSheet("PRIMA_DE_SERVICIOS");
	        Sheet vac = workbook.getSheet("VACACIONES_Y_PRIMA_VACACIONES");
	        Sheet nav = workbook.getSheet("PRIMA_NAVIDAD");
	        Sheet ces = workbook.getSheet("CESANTIAS_E_INTERESES");	            
	        crearBecp();
	            
	        List<Registro> listaBecp = RegistroConverter.toListRegistro(requestManager.getList(
	        		UrlServiceUtil.getInstance().getUrlServiceByUrlByEnumID("210171").getUrl(),param));
	            
	        //B_A_S_P_
	        setString(basp,2,1,"." + SessionUtil.getCompaniaIngreso().getNombre());
	        setString(basp,3,1,SysmanFunciones.convertirAFechaCadena(new Date()));
	        setString(basp,507,2,SessionUtil.getUser().getCodigo());
	        setFecha(basp,6,16,fechaIni,dateFormat);
	        setFecha(basp,7,16,fechaFinAjustada,dateFormat);
	            
	        //PRIMA_DE_SERVICIOS
	        setString(serv,2,1,"." + SessionUtil.getCompaniaIngreso().getNombre());
	        setString(serv,3,1,SysmanFunciones.convertirAFechaCadena(new Date()));
	        setString(serv,507,2,SessionUtil.getUser().getCodigo());
	        setFecha(serv,6,22,fechaIni,dateFormat);
	        setFecha(serv,7,22,fechaFinAjustada,dateFormat);
	            
	        //VACACIONES_Y_PRIMA_VACACIONES
	        setString(vac,2,1,"." + SessionUtil.getCompaniaIngreso().getNombre());
	        setString(vac,3,1,SysmanFunciones.convertirAFechaCadena(new Date()));
	        setString(vac,507,2,SessionUtil.getUser().getCodigo());
	        setFecha(vac,6,25,fechaIni,dateFormat);
	        setFecha(vac,7,25,fechaFinAjustada,dateFormat);
	            
	        //PRIMA_NAVIDAD
	        setString(nav,2,1,"." + SessionUtil.getCompaniaIngreso().getNombre());
	        setString(nav,3,1,SysmanFunciones.convertirAFechaCadena(new Date()));
	        setString(nav,507,2,SessionUtil.getUser().getCodigo());
	        setFecha(nav,6,21,fechaInicioAno,dateFormat);
	        setFecha(nav,7,21,fechaFinAjustada,dateFormat);
	            
	        //CESANTIAS_E_INTERESES
	        setString(ces,2,1,"." + SessionUtil.getCompaniaIngreso().getNombre());
	        setString(ces,3,1,SysmanFunciones.convertirAFechaCadena(new Date()));
	        setString(ces,507,2,SessionUtil.getUser().getCodigo());
	        setFechaTexto(ces,6,27,fechaInicioAno);
	        setFechaTexto(ces,7,27,fechaFinAjustada);	            
	            
	        int i = 6;
	        for (Registro infBecp : listaBecp) {
	        	String idEmp = infBecp.getCampos().get(GeneralParameterEnum.ID_DE_EMPLEADO.getName()).toString();
	            String numeroDcto = infBecp.getCampos().get(GeneralParameterEnum.NUMERO_DCTO.getName()).toString();
	            String nomCompleto = SysmanFunciones.nvl(infBecp.getCampos().get(GeneralParameterEnum.NOMBRECOMPLETO.getName()),"").toString();
	            String nomCargo = SysmanFunciones.nvl(infBecp.getCampos().get("NOMBRE_DEL_CARGO"),"").toString();
	            String fechaIng = SysmanFunciones.nvl(infBecp.getCampos().get("FECHA_DE_INGRESO"),"").toString();
	            double sbm = Double.parseDouble(infBecp.getCampos().get("SBM").toString());
	            double gr = Double.parseDouble(infBecp.getCampos().get("GR").toString());
	            double pt = Double.parseDouble(infBecp.getCampos().get("PT").toString());
	            	
	            String fIniPs = "";
	            if (infBecp.getCampos().get("FECHA_CUMPLIMIENTO_BONIFICACIO") == null) {
	            	fIniPs = fechaIng;
	            } else {
	            	fIniPs = SysmanFunciones.nvl(infBecp.getCampos().get("FECHA_CUMPLIMIENTO_BONIFICACIO"),"").toString();
	            }
	            double diasLnrBasp = Double.parseDouble(infBecp.getCampos().get("DIASLNRBASP").toString());
	            double porcBasp = Double.parseDouble(infBecp.getCampos().get("PORCBASP").toString());
	            double sobreSueldo = Double.parseDouble(infBecp.getCampos().get("SOBRESUELDO").toString());
	            double auxT = Double.parseDouble(infBecp.getCampos().get("AUXT").toString());
	            double auxA = Double.parseDouble(infBecp.getCampos().get("AUXA").toString());
	            double pa = Double.parseDouble(infBecp.getCampos().get("PA").toString());
	            double baspG = Double.parseDouble(infBecp.getCampos().get("BASP").toString());
	            String idTipo = SysmanFunciones.nvl(infBecp.getCampos().get("ID_DE_TIPO"),"").toString();
	            double baspS = 0;
	            if (procedimJunio.equals("calcularprimasemestral_STR2351") && sumarBasp2351.equals("NO")) {
	            	baspS = 0;
	            } else if (procedimJunio.equals("calcularprimasemestral_STR2351") && sumarBasp2351.equals("SI")) {
	            	baspS = baspG;
	            } else {
	            	baspS = SessionUtil.getCompaniaIngreso().getNit().equals("830.005.370-4") && !idTipo.equals("01")?0:baspG;
	            }
	            double extras = Double.parseDouble(infBecp.getCampos().get("EXTRAS").toString());
	            Date fechaInicioPs = (Date) SysmanFunciones.nvl(infBecp.getCampos().get("FECHAINIPS"),"");
	            double diasLnrPs = Double.parseDouble(infBecp.getCampos().get("DIASLNRPS").toString());
	            double ps = Double.parseDouble(infBecp.getCampos().get("PS").toString());
	            Date fechaIniPsV = (Date) SysmanFunciones.nvl(infBecp.getCampos().get("FECHAINIPSV"),"");
	            double diasLnrPV = Double.parseDouble(infBecp.getCampos().get("DIASLNRPV").toString());
	            double diasTotV = Double.parseDouble(infBecp.getCampos().get("DIASTOTV").toString());
	            double pv = Double.parseDouble(infBecp.getCampos().get("PV").toString());
	            Date fechaIniPsN = (Date) SysmanFunciones.nvl(infBecp.getCampos().get("FECHAINIPSN"),"");
	            double diasLnrN = Double.parseDouble(infBecp.getCampos().get("DIASLNRPN").toString());
	            String regimen = SysmanFunciones.nvl(infBecp.getCampos().get("REGIMEN"),"").toString();
	            double pn = Double.parseDouble(infBecp.getCampos().get("PN").toString());
	            Date fechaIniPsCes = (Date) SysmanFunciones.nvl(infBecp.getCampos().get("FECHAINIPSCES"),"");
	            double diasLnrCes = Double.parseDouble(infBecp.getCampos().get("DIASLNRCES").toString());
	            double anticipos = Double.parseDouble(infBecp.getCampos().get("ANTICIPOS").toString());
		            
	            //MIGRAR_CAUSACION
	            setString(migrarCaus,i,0,idEmp);
	            setString(migrarCaus,i,1,numeroDcto);
	            setString(migrarCaus,i,2,nomCompleto);
	            setString(migrarCaus,i,3,nomCargo.toLowerCase());
	            	
	            setString(basp,i,0,idEmp);
	            setString(basp,i,1,numeroDcto);
	            setString(basp,i,2,nomCompleto);
	            setString(basp,i,3,nomCargo.toLowerCase());
	            setString(basp,i,4,fechaIng);
	            setNumero(basp,i,5,sbm);
	            setNumero(basp,i,6,gr);
	            setNumero(basp,i,7,pt);
	            setString(basp,i,8,fIniPs);
	            setNumero(basp,i,10,diasLnrBasp);
	            setNumero(basp,i,13,porcBasp);
	            	
	            setString(serv,i,0,idEmp);
	            setString(serv,i,1,numeroDcto);
	            setString(serv,i,2,nomCompleto);
	            setString(serv,i,3,nomCargo.toLowerCase());
	            setString(serv,i,4,fechaIng);
	            setNumero(serv,i,5,sbm);
	            setNumero(serv,i,6,gr);
	            setNumero(serv,i,7,sobreSueldo);
	            setNumero(serv,i,8,auxT);
	            setNumero(serv,i,9,auxA);
	            setNumero(serv,i,10,pa);
	            setNumero(serv,i,11,pt);
	            setNumero(serv,i,12,baspS);
	            setNumero(serv,i,13,extras);
	            setString(serv,i,14,SysmanFunciones.convertirAFechaCadena(fechaInicioPs));
	            setNumero(serv,i,16,diasLnrPs);
	            	
	            setString(vac,i,0,idEmp);
	            setString(vac,i,1,numeroDcto);
	            setString(vac,i,2,nomCompleto);
	            setString(vac,i,3,nomCargo.toLowerCase());
	            setString(vac,i,4,fechaIng);
	            setNumero(vac,i,5,sbm);
	            setNumero(vac,i,6,gr);
	            setNumero(vac,i,7,sobreSueldo);
	            setNumero(vac,i,8,auxT);
	            setNumero(vac,i,9,auxA);
	            setNumero(vac,i,10,pa);
	            setNumero(vac,i,11,pt);
	            setNumero(vac,i,12,baspG);
	            setNumero(vac,i,13,ps);
	            setString(vac,i,14,SysmanFunciones.convertirAFechaCadena(fechaIniPsV));
	            setNumero(vac,i,16,diasLnrPV);
	            setNumero(vac,i,19,diasTotV);
	            	
	            setString(nav,i,0,idEmp);
	            setString(nav,i,1,numeroDcto);
	            setString(nav,i,2,nomCompleto);
	            setString(nav,i,3,nomCargo.toLowerCase());
	            setString(nav,i,4,fechaIng);
	            setNumero(nav,i,5,sbm);
	            setNumero(nav,i,6,gr);
	            setNumero(nav,i,7,sobreSueldo);
	            setNumero(nav,i,8,auxT);
	            setNumero(nav,i,9,auxA);
	            setNumero(nav,i,10,pa);
	            setNumero(nav,i,11,pt);
	            setNumero(nav,i,12,baspG);
	            setNumero(nav,i,13,ps);
	            setNumero(nav,i,14,pv);
	            setString(nav,i,15,SysmanFunciones.convertirAFechaCadena(fechaIniPsN));
	            setNumero(nav,i,17,diasLnrN);
	            	
	            setString(ces,i,0,idEmp);
	            setString(ces,i,1,numeroDcto);
	            setString(ces,i,2,nomCompleto);
	            setString(ces,i,3,nomCargo.toLowerCase());
	            setString(ces,i,4,fechaIng);
	            setString(ces,i,5,regimen);
	            setNumero(ces,i,6,sbm);
	            setNumero(ces,i,7,gr);
	            setNumero(ces,i,8,sobreSueldo);
	            setNumero(ces,i,9,auxT);
	            setNumero(ces,i,10,auxA);
	            setNumero(ces,i,11,pa);
	            setNumero(ces,i,12,pt);
	            setNumero(ces,i,13,extras);
	            setNumero(ces,i,14,baspG);
	            setNumero(ces,i,15,ps);
	            setNumero(ces,i,16,pv);
	            setNumero(ces,i,17,pn);
	            setString(ces,i,18,SysmanFunciones.convertirAFechaCadena(fechaIniPsCes));
	            setNumero(ces,i,20,diasLnrCes);
	            setNumero(ces,i,24,anticipos);
	            i++;
	        }
	        limpiarFilas(migrarCaus,i,505,0,0);
	        limpiarFilas(basp,i,505,0,0);
	        limpiarFilas(serv,i,505,0,0);
	        limpiarFilas(vac,i,505,0,0);
	        limpiarFilas(nav,i,505,0,0);
	        limpiarFilas(ces,i,505,0,0);
	        if (mes.equals("12")) {
	        	if (infDicPer8()) {
	        		workbook.setSheetHidden(workbook.getSheetIndex(nav.getSheetName()),1);
	        		workbook.setSheetHidden(workbook.getSheetIndex(ces.getSheetName()),1);
	        	}
	        }
	        ByteArrayOutputStream out = new ByteArrayOutputStream();
	        workbook.write(out);
	        workbook.close();
	            
	        archivoDescarga = JsfUtil.getArchivoDescarga(
	        		new ByteArrayInputStream(out.toByteArray()),nombreArchivo + "." + extension);
	        eliminarTmpBecp();
		} catch (IOException | ParseException | JRException | SystemException e) {
			logger.error(e.getMessage(),e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}
    }
	
	private void eliminarTmpBecp() {
		try {
			UrlBean urlDelete = UrlServiceUtil.getInstance().getUrlServiceByUrlByEnumID("210172");
			requestManager.delete(urlDelete.getUrl(),null);
		} catch (SystemException e) {
			logger.error(e.getMessage(),e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}
	}
	
	private boolean infDicPer8() {
		boolean existe = false;
		Registro rs = null;
		Map<String,Object> param = new HashMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(),compania);
        param.put(GeneralParameterEnum.ID_DE_PROCESO.getName(),proceso);
        param.put(GeneralParameterEnum.ANIO.getName(),anio);
        param.put(GeneralParameterEnum.MES.getName(),mes);
		try {
			rs = RegistroConverter.toRegistro(requestManager.get(
					UrlServiceUtil.getInstance().getUrlServiceByUrlByEnumID("620023").getUrl(),param));
		} catch (SystemException e) {
			e.printStackTrace();
		}        
        if (rs != null) {
            int reg = (int) SysmanFunciones.nvl(rs.getCampos().get("REG"),0);
            if (reg > 0) {
            	existe = true;
            } else {
            	existe = false;
            }
        }
        return existe;
	}
    /**
     * Este metodo es invocado el metodo inicializar, se ejecutan las
     * acciones a tener en cuenta en el momento de apertura del
     * formulario
     */
	  @Override
	  public void abrirFormulario() {}
    /**
     * Metodo ejecutado en el momento despues de cargar el registro
     * 
     */
    @Override
    public void cargarRegistro() {
    	precargarRegistro();
    }
    /**
     * Metodo ejecutado antes de realizar la insercion del registro
     * 
     * @return TODO VARIABLE
     */
    @Override
    public boolean insertarAntes() {
    	return true;
    }
    /**
     * Metodo ejecutado despues de realizar la insercion del registro
     * 
     * @return TODO VARIABLE
     */
    @Override
    public boolean insertarDespues() {
    	return true;
    }
    /**
     * Metodo ejecutado antes de realizar la insercion y actualizacion
     * del registro
     * 
     * 
     * @return TODO VARIABLE
     */
    @Override
    public boolean actualizarAntes() {
    	return true;
    }
    /**
     * Metodo ejecutado despues de realizar la insercion y actualizacion
     * del registro
     * 
     * 
     * @return TODO VARIABLE
     */
    @Override
    public boolean actualizarDespues() {
    	return true;
    }
    /**
     * Metodo ejecutado antes de realizar la eliminacion del
     * registro
     * 
     * 
     * @return TODO VARIABLE
     */
    @Override
    public boolean eliminarAntes() {
    	return true;
    }
    /**
     * Metodo ejecutado despues de realizar la eliminacion del
     * registro
     * 
     * 
     * @return TODO VARIABLE
     */
    @Override
    public boolean eliminarDespues() {
    	return true;
    }
    /**
     * Retorna la variable ingreso
     * 
     * @return  ingreso
     */
    public boolean getIngreso() {
        return ingreso;
    }
    /**
     * Asigna la variable  ingreso
     * 
     * @param  ingreso
     * Variable a asignar en  ingreso
     */
    public void setIngreso(boolean ingreso) {
        this.ingreso = ingreso;
    }
    /**
     * Retorna la variable cargarIngreso
     * 
     * @return  cargarIngreso
     */
    public boolean getCargarIngreso() {
        return cargarIngreso;
    }
    /**
     * Asigna la variable  cargarIngreso
     * 
     * @param  cargarIngreso
     * Variable a asignar en  cargarIngreso
     */
    public void setCargarIngreso(boolean cargarIngreso) {
        this.cargarIngreso = cargarIngreso;
    }
    /**
     * Retorna la variable proceso
     * 
     * @return  proceso
     */
    public String getProceso() {
        return proceso;
    }
    /**
     * Asigna la variable  proceso
     * 
     * @param  proceso
     * Variable a asignar en  proceso
     */
    public void setProceso(String proceso) {
        this.proceso = proceso;
    }
    /**
     * Retorna la variable anio
     * 
     * @return  anio
     */
    public String getAnio() {
        return anio;
    }
    /**
     * Asigna la variable  anio
     * 
     * @param  anio
     * Variable a asignar en  anio
     */
    public void setAnio(String anio) {
        this.anio = anio;
    }
    /**
     * Retorna la variable mes
     * 
     * @return  mes
     */
    public String getMes() {
        return mes;
    }
    /**
     * Asigna la variable  mes
     * 
     * @param  mes
     * Variable a asignar en  mes
     */
    public void setMes(String mes) {
        this.mes = mes;
    }
    /**
     * Retorna la variable periodo
     * 
     * @return  periodo
     */
    public String getPeriodo() {
        return periodo;
    }
    /**
     * Asigna la variable  periodo
     * 
     * @param  periodo
     * Variable a asignar en  periodo
     */
    public void setPeriodo(String periodo) {
        this.periodo = periodo;
    }
    /**
     * Retorna la variable idEmpleado
     * 
     * @return  idEmpleado
     */
    public String getIdEmpleado() {
        return idEmpleado;
    }
    /**
     * Asigna la variable  idEmpleado
     * 
     * @param  idEmpleado
     * Variable a asignar en  idEmpleado
     */
    public void setIdEmpleado(String idEmpleado) {
        this.idEmpleado = idEmpleado;
    }
    /**
     * Retorna la variable nombreArchivo
     * 
     * @return  nombreArchivo
     */
    public String getNombreArchivo() {
        return nombreArchivo;
    }
    /**
     * Asigna la variable  nombreArchivo
     * 
     * @param  nombreArchivo
     * Variable a asignar en  nombreArchivo
     */
    public void setNombreArchivo(String nombreArchivo) {
        this.nombreArchivo = nombreArchivo;
    }
    /**
     * Atributo usado para descargar contenidos de archivos desde la
     * vista
     */
    public StreamedContent getArchivoDescarga() {
        return archivoDescarga;
    }
    /**
     * Retorna el objeto contArchivoarchivoModelo
     * 
     * @return contArchivoarchivoModelo
     */
    public ContenedorArchivo getContArchivoarchivoModelo() {
        return contArchivoarchivoModelo;
    }
    /**
     * Asigna el objeto contArchivoarchivoModelo
     * 
     * @param contArchivoarchivoModelo
     * Variable a asignar en contArchivoarchivoModelo
     */
    public void setContArchivoarchivoModelo(ContenedorArchivo contArchivoarchivoModelo) {
        this.contArchivoarchivoModelo = contArchivoarchivoModelo;
    }
    /**
     * Retorna la lista listaproceso
     * 
     * @return listaproceso
     */
    public List<Registro> getListaproceso() {
        return listaproceso;
    }
    /**
     * Asigna la lista listaproceso
     * 
     * @param listaproceso
     * Variable a asignar en  listaproceso
     */
    public void setListaproceso(List<Registro> listaproceso) {
        this.listaproceso = listaproceso;
    }
    /**
     * Retorna la lista listaanio
     * 
     * @return listaanio
     */
    public List<Registro> getListaanio() {
        return listaanio;
    }
    /**
     * Asigna la lista listaanio
     * 
     * @param listaanio
     * Variable a asignar en  listaanio
     */
    public void setListaanio(List<Registro> listaanio) {
        this.listaanio = listaanio;
    }
    /**
     * Retorna la lista listaMes
     * 
     * @return listaMes
     */
    public List<Registro> getListaMes() {
        return listaMes;
    }
    /**
     * Asigna la lista listaMes
     * 
     * @param listaMes
     * Variable a asignar en  listaMes
     */
    public void setListaMes(List<Registro> listaMes) {
        this.listaMes = listaMes;
    }
    /**
     * Retorna la lista listaperiodo
     * 
     * @return listaperiodo
     */
    public List<Registro> getListaperiodo() {
        return listaperiodo;
    }
    /**
     * Asigna la lista listaperiodo
     * 
     * @param listaperiodo
     * Variable a asignar en  listaperiodo
     */
    public void setListaperiodo(List<Registro> listaperiodo) {
        this.listaperiodo = listaperiodo;
    }	
    /**
     * Retorna la lista listaidEmpleado
     * 
     * @return listaidEmpleado
     */
    public RegistroDataModelImpl getListaidEmpleado() {
        return listaidEmpleado;
    }
    /**
     * Asigna la lista listaidEmpleado
     * 
     * @param listaidEmpleado
     * Variable a asignar en  listaidEmpleado
     */
    public void setListaidEmpleado(RegistroDataModelImpl listaidEmpleado) {
        this.listaidEmpleado = listaidEmpleado;
    }
}
