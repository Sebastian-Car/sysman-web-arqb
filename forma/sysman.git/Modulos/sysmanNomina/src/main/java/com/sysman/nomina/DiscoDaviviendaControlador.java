package com.sysman.nomina;

import com.sysman.beanbase.BeanBaseModal;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.ArchivosBean;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.jsfutil.ReportesBean;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.nomina.ejb.EjbNominaBancosRemote;
import com.sysman.nomina.ejb.EjbNominaCuatroRemote;
import com.sysman.nomina.enums.DiscoDaviviendaControladorEnum;
import com.sysman.nomina.enums.DiscoDaviviendaControladorUrlEnum;
import com.sysman.persistencia.ConectorPool;
import com.sysman.recursos.ejb.EjbSysmanUtilRemote;
import com.sysman.reportes.Reporteador;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.util.SysmanConstantes;
import com.sysman.util.SysmanFunciones;

import net.sf.dynamicreports.report.exception.DRException;
import net.sf.jasperreports.engine.JRException;

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
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.primefaces.event.SelectEvent;
import org.primefaces.model.StreamedContent;

/**
 *
 * @author esarmiento
 * @version 1, 13/08/2015
 * @author jcrodriguez,Refactoring y depuracion
 * @version 2, 03/10/2017
 */
@ManagedBean
@ViewScoped

public class DiscoDaviviendaControlador extends BeanBaseModal
{

    private final String compania;
    private final String proceso;
    private String anio;
    private String mes;
    private String periodo;
    private String banco;
    private Date fechaReporte;
    private String consecutivo;
    private String codigoOficina;
    private String nombreBanco;
    private List<Registro> listaAno1;
    private List<Registro> listaMes1;
    private List<Registro> listaPeriodo1;
    private RegistroDataModelImpl listaBanco1;
	private String modulo;
	/**
	 * Indica si se debe consultar teniendo en cuenta todos los bancos o un solo
	 * banco
	 */
	private boolean todosLosBancos;
    /**
     * Atributo usado para descargar contenidos de archivos desde la
     * vista
     */
	private StreamedContent archivoDescarga;
		
	@EJB
    private EjbSysmanUtilRemote ejbSysmanUtil;
    @EJB
    private EjbNominaBancosRemote ejbNominaBancos;
    @EJB
	private EjbNominaCuatroRemote ejbNominaCuatro;    

    /**
     * Creates a new instance of DiscoDaviviendaControlador
     */
    public DiscoDaviviendaControlador()
    {
        super();
        compania = SessionUtil.getCompania();
        proceso = validarCampoCadena(SessionUtil.getSessionVar("procesoNomina"));
        modulo = SessionUtil.getModulo();
        anio = validarCampoCadena(SessionUtil.getSessionVar("anioNomina"));
        mes = validarCampoCadena(SessionUtil.getSessionVar("mesNomina"));
        periodo = SessionUtil.getSessionVar("periodoNomina").toString();
        fechaReporte = new Date();
        codigoOficina = "01";
        consecutivo = "01";
        try
        {
            numFormulario = GeneralCodigoFormaEnum.DISCO_DAVIVIENDA_CONTROLADOR.getCodigo();
            validarPermisos();
        }
        catch (Exception ex)
        {
            Logger.getLogger(DiscoDaviviendaControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            SessionUtil.redireccionarMenuPermisos();
        }
    }

    private String validarCampoCadena(Object var)
    {
        return SysmanFunciones.validarVariableVacio(var.toString()) ? "" : var.toString();
    }

    @PostConstruct
    public void inicializar()
    {
        cargarListaAno1();
        cargarListaMes1();
        cargarListaPeriodo1();
        cargarListaBanco1();
        abrirFormulario();
    }

    @Override
    public void abrirFormulario()
    {
        // heredado del bean base
    }

    public void cargarListaAno1()
    {

        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        try
        {
            listaAno1 = RegistroConverter.toListRegistro(requestManager.getList(UrlServiceUtil.getInstance()
                            .getUrlServiceByUrlByEnumID(DiscoDaviviendaControladorUrlEnum.URL2889.getValue()).getUrl(), param));
        }
        catch (SystemException e)
        {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    public void cargarListaMes1()
    {
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.ANO.getName(), anio);
        param.put(DiscoDaviviendaControladorEnum.PROCESO.getValue(), proceso);

        try
        {
            listaMes1 = RegistroConverter.toListRegistro(requestManager.getList(UrlServiceUtil.getInstance()
                            .getUrlServiceByUrlByEnumID(DiscoDaviviendaControladorUrlEnum.URL3496.getValue()).getUrl(), param));
        }
        catch (SystemException e)
        {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    public void cargarListaPeriodo1()
    {
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.ANO.getName(), anio);
        param.put(GeneralParameterEnum.MES.getName(), mes);
        param.put(DiscoDaviviendaControladorEnum.PROCESO.getValue(), proceso);

        try
        {
            listaPeriodo1 = RegistroConverter.toListRegistro(requestManager.getList(UrlServiceUtil.getInstance()
                            .getUrlServiceByUrlByEnumID(DiscoDaviviendaControladorUrlEnum.URL3429.getValue()).getUrl(), param));
        }
        catch (SystemException e)
        {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    public void cargarListaBanco1()
    {
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        UrlBean urlBean = UrlServiceUtil.getInstance().getUrlServiceByUrlByEnumID(DiscoDaviviendaControladorUrlEnum.URL5148.getValue());
        listaBanco1 = new RegistroDataModelImpl(urlBean.getUrl(), urlBean.getUrlConteo().getUrl(), param,
                        true, GeneralParameterEnum.BANCO.getName());

    }

    public void oprimirGenerarDisco()
    {
        String discoDavivienda = null;
        int posicion;
        String codigoEntidad;
        try
        {
        	String archivoDavivienda = ejbSysmanUtil.consultarParametro(compania,
					"ARCHIVO PLANO DAVIVIENDA TXT", modulo, new Date(), false);
        	
        	String formatoBancos = ejbSysmanUtil.consultarParametro(compania,
					"FORMATO ESPECIAL BANCOS", modulo, new Date(), false);
        	
        	if (formatoBancos.equalsIgnoreCase("SI")) {
        		archivoDescarga = null;
				discoDavivienda = ejbNominaCuatro.generarDiscoDaviviendaIdi(compania, Integer.parseInt(proceso), Integer.parseInt(periodo), 
										Integer.parseInt(mes), Integer.parseInt(anio), fechaReporte, new Date(), banco, todosLosBancos);
				System.out.println(fechaReporte);
				if (todosLosBancos)
				{
					ArchivosBean.generarPlano(archivoDavivienda+".csv", discoDavivienda);
				}
				else {
					ArchivosBean.generarPlano(archivoDavivienda+".txt", discoDavivienda);
				}
			}else {
				discoDavivienda = ejbNominaBancos.getDiscoDavivienda(compania, Integer.parseInt(consecutivo), banco, codigoOficina,
                        Integer.parseInt(proceso), Integer.parseInt(anio), Integer.parseInt(mes),
                        Integer.parseInt(periodo), fechaReporte);

		        posicion = discoDavivienda.indexOf("\r\n");
		        codigoEntidad = discoDavivienda.substring(0, posicion);
		        discoDavivienda = discoDavivienda.substring(posicion + 2);
		        ArchivosBean.generarPlano(SysmanFunciones.concatenar(DiscoDaviviendaControladorEnum.NOMINA.getValue(), codigoEntidad, ".pln"),
		                        discoDavivienda);
			}
        }
        catch (IOException | SystemException | NumberFormatException e)
        {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    public void oprimirAuditoria()
    {

        String discoDaviviendaAuditoria;
        int posicion;
        String codigoEntidad;
        try
        {
            discoDaviviendaAuditoria = ejbNominaBancos.getDiscoDaviviendaAuditoria(compania, banco,
                            Integer.parseInt(proceso), Integer.parseInt(anio),
                            Integer.parseInt(mes), Integer.parseInt(periodo));
            discoDaviviendaAuditoria = discoDaviviendaAuditoria.indexOf("\r\n") == -1
                ? SysmanFunciones.concatenar(discoDaviviendaAuditoria, "\r\n") : discoDaviviendaAuditoria;
            posicion = discoDaviviendaAuditoria.indexOf("\r\n");
            codigoEntidad = discoDaviviendaAuditoria.substring(0,
                            posicion);
            discoDaviviendaAuditoria = discoDaviviendaAuditoria
                            .substring(posicion + 2);
            ArchivosBean.generarPlano(SysmanFunciones.concatenar(DiscoDaviviendaControladorEnum.NOMINA.getValue(), codigoEntidad, ".pln"),
                            discoDaviviendaAuditoria);
        }
        catch (NumberFormatException | SystemException | IOException e)
        {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    public void oprimirComando63()
    {

        String discoDaviviendaNuevo;
        int posicion;
        String codigoEntidad;
        try
        {
            discoDaviviendaNuevo = ejbNominaBancos.getDiscoAuditoriaNuevo(compania,
                            banco, Integer.parseInt(proceso), Integer.parseInt(anio),
                            Integer.parseInt(mes), Integer.parseInt(periodo));
            discoDaviviendaNuevo = discoDaviviendaNuevo.indexOf("\r\n") == -1 ? SysmanFunciones.concatenar(discoDaviviendaNuevo, "\r\n")
                : discoDaviviendaNuevo;
            posicion = discoDaviviendaNuevo.indexOf("\r\n");
            codigoEntidad = discoDaviviendaNuevo.substring(0,
                            posicion);
            discoDaviviendaNuevo = discoDaviviendaNuevo.substring(posicion + 2);
            ArchivosBean.generarPlano(SysmanFunciones.concatenar(DiscoDaviviendaControladorEnum.NOMINA.getValue(), codigoEntidad, ".pln"),
                            discoDaviviendaNuevo);
        }
        catch (NumberFormatException | SystemException | IOException e)
        {

            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }
    
    public void oprimirGenerarExcel() {
    	
    	archivoDescarga=null; 
    	String discodaviviendaExcel;
    	
    	try {
    		discodaviviendaExcel = null;
			discodaviviendaExcel = ejbNominaCuatro.generarDaviviendaExcel(compania, Integer.parseInt(proceso), Integer.parseInt(periodo), 
					Integer.parseInt(mes), Integer.parseInt(anio));
			generarExcel(discodaviviendaExcel);
		} catch (NumberFormatException | SystemException e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}
    }
    
    public void oprimirGeneraExcelSimple() 
    {
    	archivoDescarga=null;    
    	String reporte = "800728GeneracionExcelSimpleDavivienda";
		HashMap<String, Object> reemplazar = new HashMap<>();
		reemplazar.put("compania", compania);
		reemplazar.put("ano", anio == null ? "" : anio);
		reemplazar.put("mes", Integer.parseInt(mes));
		reemplazar.put("proceso", Integer.parseInt(proceso));
		reemplazar.put("periodo", Integer.parseInt(periodo));
		reemplazar.put("banco", SysmanFunciones.nvl(banco,"0"));
		reemplazar.put("todosLosBancos", todosLosBancos ? "-1" : "0");
		try {			
			String sql = Reporteador.resuelveConsulta(reporte, Integer.parseInt(SessionUtil.getModulo()),
					reemplazar);
			
			archivoDescarga = JsfUtil.exportarHojaDatosStreamed(sql, ConectorPool.ESQUEMA_SYSMAN, ReportesBean.FORMATOS.EXCEL,
					"DaviviendaExcelSimple");		

		} catch (JRException | IOException | SQLException | DRException | SysmanException   e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}
    }
    
    public void generarExcel(String datos) {
    	
    	Workbook libro = null;

    	try (ByteArrayOutputStream archivo = new ByteArrayOutputStream()) {
	    	// Crear un nuevo libro de Excel
	        libro = new XSSFWorkbook();
	        // Crear una hoja en el libro
	        Sheet hoja = libro.createSheet("Hoja1");        
	        
	        String[] lineas = datos.split(SysmanConstantes.SEPARADOR_REG);
	
	        int rowNum = 0;
	        for (String linea : lineas) {
	            Row fila = hoja.createRow(rowNum);
	
	            String[] columnas = linea.split(SysmanConstantes.SEPARADOR_COL);
	            int cellNum = 0;
	            for (String columna : columnas) {
	                Cell celda = fila.createCell(cellNum);
	                celda.setCellValue(columna);
	                cellNum++;
	            }
	
	            rowNum++;
	        }
	
            // Guardar el libro como archivo Excel
            libro.write(archivo);
            archivo.close();
            archivoDescarga = JsfUtil.getArchivoDescarga(new ByteArrayInputStream(archivo.toByteArray()),
					"DaviviendaExcel.xls");
    	}
        catch (IOException | NumberFormatException | JRException e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
        } finally {
			try {
				if (libro != null) {
					libro.close();
				}
			} catch (IOException e) {
				logger.error(e.getMessage(), e);
				JsfUtil.agregarMensajeError(e.getMessage());
			}
		}
	}

    public void cambiarAno1()
    {
        mes = null;
        periodo = null;
        cargarListaMes1();
        cargarListaPeriodo1();
    }

    public void cambiarMes1()
    {
        periodo = null;
        cargarListaPeriodo1();
    }
    
    public void cambiaropcionTodosLosBancos() {
    	banco = null;
        nombreBanco = null;
   }

    public void seleccionarFilaBanco1(SelectEvent event)
    {
        Registro registroAux = (Registro) event.getObject();
        banco = validarCampoCadenaMap(registroAux.getCampos(), GeneralParameterEnum.BANCO.getName());
        nombreBanco = validarCampoCadenaMap(registroAux.getCampos(), GeneralParameterEnum.NOMBRE.getName());
        todosLosBancos = false;
    }

    private String validarCampoCadenaMap(Map<String, Object> campos, String var)
    {
        return SysmanFunciones.validarCampoVacio(campos, var) ? "" : campos.get(var).toString();
    }

    public String getAnio()
    {
        return anio;
    }

    public void setAnio(String anio)
    {
        this.anio = anio;
    }

    public String getMes()
    {
        return mes;
    }

    public void setMes(String mes)
    {
        this.mes = mes;
    }

    public String getPeriodo()
    {
        return periodo;
    }

    public void setPeriodo(String periodo)
    {
        this.periodo = periodo;
    }

    public Date getFechaReporte()
    {
        return fechaReporte;
    }

    public void setFechaReporte(Date fechaReporte)
    {
        this.fechaReporte = fechaReporte;
    }

    public String getBanco()
    {
        return banco;
    }

    public void setBanco(String banco)
    {
        this.banco = banco;
    }

    public String getConsecutivo()
    {
        return consecutivo;
    }

    public void setConsecutivo(String consecutivo)
    {
        this.consecutivo = consecutivo;
    }

    public String getCodigoOficina()
    {
        return codigoOficina;
    }

    public void setCodigoOficina(String codigoOficina)
    {
        this.codigoOficina = codigoOficina;
    }

    public String getNombreBanco()
    {
        return nombreBanco;
    }

    public void setNombreBanco(String nombreBanco)
    {
        this.nombreBanco = nombreBanco;
    }

    public List<Registro> getListaAno1()
    {
        return listaAno1;
    }

    public void setListaAno1(List<Registro> listaAno1)
    {
        this.listaAno1 = listaAno1;
    }

    public List<Registro> getListaMes1()
    {
        return listaMes1;
    }

    public void setListaMes1(List<Registro> listaMes1)
    {
        this.listaMes1 = listaMes1;
    }

    public List<Registro> getListaPeriodo1()
    {
        return listaPeriodo1;
    }

    public void setListaPeriodo1(List<Registro> listaPeriodo1)
    {
        this.listaPeriodo1 = listaPeriodo1;
    }

    public RegistroDataModelImpl getListaBanco1()
    {
        return listaBanco1;
    }

    public void setListaBanco1(RegistroDataModelImpl listaBanco1)
    {
        this.listaBanco1 = listaBanco1;
    }

    /**
     * Retorna la variable todosLosBancos
     * 
     * @return  todosLosBancos
     */
    public boolean isTodosLosBancos() {
        return todosLosBancos;
    }
    /**
     * Asigna la variable  todosLosBancos
     * 
     * @param  todosLosBancos
     * Variable a asignar en  todosLosBancos
     */
    public void setTodosLosBancos(boolean todosLosBancos) {
        this.todosLosBancos = todosLosBancos;
    }
	
	/**
     * Atributo usado para descargar contenidos de archivos desde la
     * vista
     */
	public StreamedContent getArchivoDescarga() {
        return archivoDescarga;
    }

}
