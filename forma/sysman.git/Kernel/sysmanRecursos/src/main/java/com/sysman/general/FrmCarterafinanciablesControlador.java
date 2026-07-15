/*-
 * FrmCarterafinanciablesControlador.java
 *
 * 1.0
 * 
 * 07/05/2021
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */
package com.sysman.general;

import com.sysman.beanbase.BeanBaseModal;
import com.sysman.general.enums.FrmCarterafinanciablesControladorEnum;
import com.sysman.general.enums.FrmCarterafinanciablesControladorUrlEnum;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.ReportesBean;
import com.sysman.jsfutil.ReportesBean.FORMATOS;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.persistencia.ConectorPool;
import com.sysman.reportes.Reporteador;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.util.SysmanConstantes;
import com.sysman.util.SysmanFunciones;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.primefaces.event.SelectEvent;
import org.primefaces.model.StreamedContent;

import net.sf.dynamicreports.report.exception.DRException;
import net.sf.jasperreports.engine.JRException;

/**
 * Formulario que permite buscar la informacion de cuentas por cobrar
 *
 * @version 1.0, 07/05/2021
 * @author jorduz
 */
@ManagedBean
@ViewScoped
public class FrmCarterafinanciablesControlador extends BeanBaseModal {
    /**
     * Constante a nivel de clase que almacena el codigo de la compania en la
     * cual inicio sesion el usuario, el valor de esta constante es asignado en
     * el constructor a la variable de sesion correspondiente
     */
    private final String compania;
    // <DECLARAR_ATRIBUTOS>

    private final String modulo;
    private String cntInicial;
    private String cntFinal;
    private Date fechaCorte;
    private Date fechaDesde;
    private Date fechaHasta;
    private boolean paralelo;
    private boolean fechaVencimiento;
    private int ano;

    private StreamedContent archivoDescarga;
    
    private boolean conTercero = false;

    private RegistroDataModelImpl listaCnt_Inicial;

    private RegistroDataModelImpl listaCnt_Final;
    
    private String terceroInicial = SysmanConstantes.DEFECTOINICIAL_STRING;
    private String terceroFinal = SysmanConstantes.DEFECTOFINAL_STRING;
    
    private RegistroDataModelImpl listaTerceroIni;
    private RegistroDataModelImpl listaTerceroFin;

    // </DECLARAR_LISTAS_COMBO_GRANDE>
    /**
     * Crea una nueva instancia de FrmCarterafinanciablesControlador
     */
    public FrmCarterafinanciablesControlador() {
        super();
        compania = SessionUtil.getCompania();
        modulo = "1";
        fechaCorte = new Date();
        fechaDesde = new Date();
        fechaHasta = new Date();
        ano = SysmanFunciones.ano(fechaDesde);
        try {
            numFormulario = GeneralCodigoFormaEnum.FRM_CARTERA_FINANCIABLES_CONTROLADOR
                    .getCodigo();
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
     * Este metodo se ejecuta justo despues de que el objeto de la clase del
     * Bean ha sido creado, en este se realizan las asignaciones iniciales
     * necesarias para la visualizacion del formulario, como son tablas,
     * origenes de datos, inicializacion de listas y demas necesarios
     */
    @PostConstruct
    public void inicializar() {
        cargarListaCnt_Inicial();
        cargarListaTerceroIni();
    	cargarListaTerceroFin();
        abrirFormulario();
    }

    @Override
    public void abrirFormulario() {
        paralelo = false;
    }

    // <METODOS_CARGAR_LISTA>
    /**
     */
    public void cargarListaCnt_Inicial() {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        FrmCarterafinanciablesControladorUrlEnum.URL16209
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.ANO.getName(), ano);

        setListaCnt_Inicial(new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param, true,
                        GeneralParameterEnum.CODIGO.getName()));

    }

    /**
     */
    public void cargarListaCnt_Final() {
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.ANO.getName(), ano);
        param.put(FrmCarterafinanciablesControladorEnum.PARAM0.getValue(),
                        cntInicial);
        
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                        		FrmCarterafinanciablesControladorUrlEnum.URL16207
                                                        .getValue());

        setListaCnt_Final(new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param, true,
                        GeneralParameterEnum.CODIGO.getName()));
    }
    
    
    public void cargarListaTerceroIni() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                        		FrmCarterafinanciablesControladorUrlEnum.URL14036
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        listaTerceroIni = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, FrmCarterafinanciablesControladorEnum.PARAM1.getValue());
    }
    
    
    
    public void cargarListaTerceroFin() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                        		FrmCarterafinanciablesControladorUrlEnum.URL14036
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        listaTerceroFin = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, FrmCarterafinanciablesControladorEnum.PARAM1.getValue());
    }
    
    

    // </METODOS_CARGAR_LISTA>
    // <METODOS_BOTONES>
    /**
     */
    public void oprimirImprimir() {
        // <CODIGO_DESARROLLADO>
        archivoDescarga = null;
        getReporte(ReportesBean.FORMATOS.PDF);
        // </CODIGO_DESARROLLADO>
    }

    /**
     */
    public void oprimirComando79() {
        // <CODIGO_DESARROLLADO>
        archivoDescarga = null;
        String reporte = "";
        String sql = "";
        ano = SysmanFunciones.ano(fechaCorte);

        HashMap<String, Object> reemplazar = new HashMap<>();

        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            reemplazar.put("compania", compania);
            reemplazar.put("cuentainicial", cntInicial);
            reemplazar.put("cuentafinal", cntFinal);
            reemplazar.put("fechadesde",
                            SysmanFunciones.convertirAFechaCadena(fechaDesde));
            reemplazar.put("fechahasta",
                            SysmanFunciones.convertirAFechaCadena(fechaHasta));
            reemplazar.put("fechacorte",
                            SysmanFunciones.convertirAFechaCadena(fechaCorte));
            reemplazar.put("ano", ano);

            if (paralelo) {
            	reporte = "800434CarteraBalanceCorpo";
            	/*sql = Reporteador.resuelveConsulta(reporte,
            			Integer.parseInt(modulo), reemplazar);
            	archivoDescarga = JsfUtil.exportarHojaDatosStreamed(sql,
            			ConectorPool.ESQUEMA_SYSMAN, FORMATOS.EXCEL,
            			reporte);*/  //comentado por JM 18/09/2025 CC 2356
            }
            else if (fechaVencimiento) {
            	if(conTercero) {
            		reemplazar.put("terceroInicial", terceroInicial);
            		reemplazar.put("terceroFinal", terceroFinal);

            		reporte = "002549CarteraFinanTerceroVencimiento";
            	}else {
            		reporte = "800444CarteraFinanciablesFechaVencimiento";
            	}
            	
            	/*sql = Reporteador.resuelveConsulta(reporte,
            			Integer.parseInt(modulo), reemplazar);
            	archivoDescarga = JsfUtil.exportarHojaDatosStreamed(sql,
            			ConectorPool.ESQUEMA_SYSMAN, FORMATOS.EXCEL,
            			reporte); */  //comentado por JM 18/09/2025 CC 2356

            }
            else {
            	if(conTercero) {
            		reemplazar.put("terceroInicial", terceroInicial);
            		reemplazar.put("terceroFinal", terceroFinal);

            		reporte = "002550CarteraFinanTercero";           		
            	}else {
            		reporte = "800444CarteraFinanciables";
            	}
            	/*sql = Reporteador.resuelveConsulta(reporte,
            			Integer.parseInt(SessionUtil.getModulo()),
            			reemplazar);

            	archivoDescarga = JsfUtil.exportarHojaDatosStreamed(sql,
            			ConectorPool.ESQUEMA_SYSMAN, FORMATOS.EXCEL,
            			"CarteraFinanciables");*/ //comentado por JM 18/09/2025 CC 2356
            }
            
            //JM 18/09/2025 CC 2356
            String nombreCompania = SessionUtil.getCompaniaIngreso().getNombre();
    		sql = Reporteador.resuelveConsulta(reporte,reemplazar);
    		
    		archivoDescarga = JsfUtil.exportarHojaDatosStreamed(sql,
                    ConectorPool.ESQUEMA_SYSMAN, FORMATOS.EXCEL, reporte);
    		
    		Workbook workbook = new XSSFWorkbook(
    				JsfUtil.exportarHojaDatosStreamed(sql,
    						ConectorPool.ESQUEMA_SYSMAN,
    						FORMATOS.EXCEL).getStream());
    		
    		Sheet sheet = workbook.getSheetAt(0);

    		sheet.shiftRows(0, sheet.getLastRowNum(),5);

    		sheet.createFreezePane(0,6);
    		
    		Font boldFont = workbook.createFont();
    		boldFont.setBold(true);
    		CellStyle boldStyle = workbook.createCellStyle();
    		boldStyle.setFont(boldFont);

    		Font font2 = workbook.createFont();
    		font2.setFontName("Calibri");
    		font2.setFontHeightInPoints((short) 11);
    		font2.setBold(false);
    			//titulo de compañia
    			Row r = sheet.createRow(0);
    			Cell cell1 = r.createCell(0);
    			cell1 = r.createCell(1);
    			cell1.setCellValue(nombreCompania.toUpperCase());
    			cell1.setCellStyle(boldStyle);
    			//titulo del reporte
    			Row r2 = sheet.createRow(1);
    			Cell cell2 = r2.createCell(0);
    			cell2 = r2.createCell(1);
    			cell2.setCellValue("INFORME DE CARTERA");
    			//titulo fechas desde hasta 
    			Row r3 = sheet.createRow(2);
    			Cell cell3 = r3.createCell(0);
    			cell3 = r3.createCell(1);
    			String fechaIncials = new SimpleDateFormat("dd-MM-yyy").format(fechaDesde);
    			String fechaFinals = new SimpleDateFormat("dd-MM-yyy").format(fechaHasta);
    			cell3.setCellValue("DESDE "+fechaIncials+" HASTA "+fechaFinals);
    			//titulo fecha de corte 
    			Row r4 = sheet.createRow(3);
    			Cell cell4 = r4.createCell(0);
    			cell4 = r4.createCell(1);
    			String fechaCortes = new SimpleDateFormat("dd-MM-yyy").format(fechaCorte);
    			cell4.setCellValue("FECHA DE CORTE:  "+fechaCortes);
    			//titulo fecha de generacion 
    			Row r5 = sheet.createRow(4);
    			Cell cell5 = r5.createCell(0);
    			cell5 = r5.createCell(1);
    			String fechaGenerado = new SimpleDateFormat("dd-MM-yyy").format(new Date());
    			cell5.setCellValue("FECHA DE GENERACIÓN:  "+fechaGenerado);
    			
    			
    			workbook.write(out);

        		archivoDescarga = JsfUtil.getArchivoDescarga(
        				new ByteArrayInputStream(out.toByteArray()), "CarteraFinanciables.xlsx");
        		workbook.close(); 

        }
            catch (JRException | IOException | SQLException | DRException
            		| SysmanException | ParseException e) {

            	logger.error(e.getMessage(), e);
            	JsfUtil.agregarMensajeError(e.getMessage());
            }

        // </CODIGO_DESARROLLADO>
    }

    public void oprimirbtnCorregir() {
        // <CODIGO_DESARROLLADO>

        // </CODIGO_DESARROLLADO>
    }

    public void getReporte(ReportesBean.FORMATOS formato) {

        ano = SysmanFunciones.ano(fechaCorte);

        Map<String, Object> parametros = new HashMap<>();
        HashMap<String, Object> reemplazar = new HashMap<>();

        try {
            reemplazar.put("compania", compania);
            reemplazar.put("cuentainicial", cntInicial);
            reemplazar.put("cuentafinal", cntFinal);
            reemplazar.put("fechadesde",
                            SysmanFunciones.convertirAFechaCadena(fechaDesde));
            reemplazar.put("fechahasta",
                            SysmanFunciones.convertirAFechaCadena(fechaHasta));
            reemplazar.put("fechacorte",
                            SysmanFunciones.convertirAFechaCadena(fechaCorte));
            reemplazar.put("ano", ano);
            
            String nombreConsulta = "";
            String rep = "";

            if (paralelo) {
                String reporte = "800434CarteraBalanceCorpo";
                String sql = Reporteador.resuelveConsulta(reporte,
                                Integer.parseInt(modulo), reemplazar);
                archivoDescarga = JsfUtil.exportarHojaDatosStreamed(sql,
                                ConectorPool.ESQUEMA_SYSMAN, formato, reporte);
            }
            
            else if (fechaVencimiento) {

            	if(conTercero) {
            		reemplazar.put("terceroInicial", terceroInicial);
            		reemplazar.put("terceroFinal", terceroFinal);

            		nombreConsulta = "002549CarteraFinanTerceroVencimiento";
            		rep = "002550CarteraFinanTercero";
            	}else {
            		nombreConsulta = "002122CarteraFinanciablesFechaVencimiento";
            		rep = "002122CarteraFinanciables";
            	}
            	Reporteador.resuelveConsulta(
            			nombreConsulta,
            			Integer.parseInt(modulo),
            			reemplazar,
            			parametros);
            	archivoDescarga = JsfUtil.exportarStreamed(
            			rep, parametros,
            			ConectorPool.ESQUEMA_SYSMAN, formato);
            	
            }else {
            	
            	if(conTercero) {
            		reemplazar.put("terceroInicial", terceroInicial);
            		reemplazar.put("terceroFinal", terceroFinal);

            		nombreConsulta = "002550CarteraFinanTercero";           		
            	}else {
            		nombreConsulta = "002122CarteraFinanciables";            		
            	}
            	
            		Reporteador.resuelveConsulta(nombreConsulta, Integer.parseInt(modulo),
            				reemplazar,
            				parametros);

            		archivoDescarga = JsfUtil.exportarStreamed(nombreConsulta, parametros,
            				ConectorPool.ESQUEMA_SYSMAN, formato);

            }

        }
        catch (JRException | IOException | SysmanException | SQLException
                        | DRException | ParseException e) {

            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    public void seleccionarFilaCnt_Inicial(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        cntInicial = registroAux == null ? ""
            : registroAux.getCampos().get("CODIGO").toString();
        cargarListaCnt_Final();

    }

    public void seleccionarFilaCnt_Final(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        cntFinal = registroAux == null ? ""
            : registroAux.getCampos().get("CODIGO").toString();
    }
    
    public void seleccionarFilaTerceroIni(SelectEvent event) {
    	Registro registroAux = (Registro) event.getObject();
    	terceroInicial = !SysmanFunciones.validarCampoVacio(
    			registroAux.getCampos(),
    			FrmCarterafinanciablesControladorEnum.PARAM1.getValue())
    			? registroAux.getCampos()
    					.get(FrmCarterafinanciablesControladorEnum.PARAM1
    							.getValue())
    					.toString()
    					: "";
    }

    
    public void seleccionarFilaTerceroFin(SelectEvent event) {
    	Registro registroAux = (Registro) event.getObject();
    	terceroFinal = !SysmanFunciones.validarCampoVacio(
    			registroAux.getCampos(),
    			FrmCarterafinanciablesControladorEnum.PARAM1.getValue())
    			? registroAux.getCampos()
    					.get(FrmCarterafinanciablesControladorEnum.PARAM1
    							.getValue())
    					.toString()
    					: "";
    }
    
    
    public void cambiartercero() {

    	
    }
    
    
    // </METODOS_COMBOS_GRANDES>
    // <METODOS_ARBOL>
    // </METODOS_ARBOL>
    // <SET_GET_ATRIBUTOS>
    /**
     * Retorna la variable cntInicial
     * 
     * @return cntInicial
     */
    public String getCntInicial() {
        return cntInicial;
    }

    /**
     * Asigna la variable cntInicial
     * 
     * @param cntInicial
     * Variable a asignar en cntInicial
     */
    public void setCntInicial(String cntInicial) {
        this.cntInicial = cntInicial;
        this.cntFinal = cntInicial;
    }

    /**
     * Retorna la variable cntFinal
     * 
     * @return cntFinal
     */
    public String getCntFinal() {
        return cntFinal;
    }

    /**
     * Asigna la variable cntFinal
     * 
     * @param cntFinal
     * Variable a asignar en cntFinal
     */
    public void setCntFinal(String cntFinal) {
        this.cntFinal = cntFinal;
    }

    public Date getFechaCorte() {
        return fechaCorte;
    }

    public void setFechaCorte(Date fechaCorte) {
        this.fechaCorte = fechaCorte;
    }

    public Date getFechaDesde() {
        return fechaDesde;
    }

    public void setFechaDesde(Date fechaDesde) {
        this.fechaDesde = fechaDesde;
    }

    public Date getFechaHasta() {
        return fechaHasta;
    }

    public void setFechaHasta(Date fechaHasta) {
        this.fechaHasta = fechaHasta;
    }

    public int getAno() {
        return ano;
    }

    public void setAno(int ano) {
        this.ano = ano;
    }

    // </SET_GET_ATRIBUTOS>
    // <SET_GET_PARAMETROS>
    // </SET_GET_PARAMETROS>
    // <SET_GET_LISTAS>
    // </SET_GET_LISTAS>
    // <SET_GET_LISTAS_COMBO_GRANDE>
    /**
     * Retorna la lista listaCnt_Inicial
     * 
     * @return listaCnt_Inicial
     */
    public RegistroDataModelImpl getListaCnt_Final() {
        return listaCnt_Final;
    }

    public void setListaCnt_Final(RegistroDataModelImpl listaCnt_Final) {
        this.listaCnt_Final = listaCnt_Final;
    }

    public RegistroDataModelImpl getListaCnt_Inicial() {
        return listaCnt_Inicial;
    }

    public void setListaCnt_Inicial(RegistroDataModelImpl listaCnt_Inicial) {
        this.listaCnt_Inicial = listaCnt_Inicial;
    }

    public boolean isParalelo() {
        return paralelo;
    }

    public void setParalelo(boolean paralelo) {
        this.paralelo = paralelo;
    }

    public StreamedContent getArchivoDescarga() {
        return archivoDescarga;
    }

    public void setArchivoDescarga(StreamedContent archivoDescarga) {
        this.archivoDescarga = archivoDescarga;
    }

    public boolean isFechaVencimiento() {
        return fechaVencimiento;
    }

    public void setFechaVencimiento(boolean fechaVencimiento) {
        this.fechaVencimiento = fechaVencimiento;
    }

	/**
	 * @return the terceroInicial
	 */
	public String getTerceroInicial() {
		return terceroInicial;
	}

	/**
	 * @param terceroInicial the terceroInicial to set
	 */
	public void setTerceroInicial(String terceroInicial) {
		this.terceroInicial = terceroInicial;
	}

	/**
	 * @return the terceroFinal
	 */
	public String getTerceroFinal() {
		return terceroFinal;
	}

	/**
	 * @param terceroFinal the terceroFinal to set
	 */
	public void setTerceroFinal(String terceroFinal) {
		this.terceroFinal = terceroFinal;
	}

	/**
	 * @return the listaTerceroIni
	 */
	public RegistroDataModelImpl getListaTerceroIni() {
		return listaTerceroIni;
	}

	/**
	 * @param listaTerceroIni the listaTerceroIni to set
	 */
	public void setListaTerceroIni(RegistroDataModelImpl listaTerceroIni) {
		this.listaTerceroIni = listaTerceroIni;
	}

	/**
	 * @return the listaTerceroFin
	 */
	public RegistroDataModelImpl getListaTerceroFin() {
		return listaTerceroFin;
	}

	/**
	 * @param listaTerceroFin the listaTerceroFin to set
	 */
	public void setListaTerceroFin(RegistroDataModelImpl listaTerceroFin) {
		this.listaTerceroFin = listaTerceroFin;
	}

	/**
	 * @return the conTercero
	 */
	public boolean isConTercero() {
		return conTercero;
	}

	/**
	 * @param conTercero the conTercero to set
	 */
	public void setConTercero(boolean conTercero) {
		this.conTercero = conTercero;
	}

    
    
    // </SET_GET_LISTAS_COMBO_GRANDE>
}
