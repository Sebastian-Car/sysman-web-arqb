/*-
 * CuentasConfiguradasControlador.java
 *
 * 1.0
 * 
 * 28/12/2018
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */
package com.sysman.exogenas;

import com.sysman.beanbase.BeanBaseContinuoAcmeImpl;
import com.sysman.componentes.Direccionador;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.enums.GenericUrlEnum;
import com.sysman.exception.SystemException;
import com.sysman.exogenas.ejb.EjbExogenasCeroRemote;
import com.sysman.exogenas.enums.ConfigurarPlanContableExsControladorEnum;
import com.sysman.exogenas.enums.CuentasConfiguradasControladorUrlEnum;
import com.sysman.jsfutil.ArchivosBean;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.util.ContenedorArchivo;
import com.sysman.util.SysmanConstantes;
import com.sysman.util.SysmanFunciones;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Map;
import java.util.TreeMap;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.NumberToTextConverter;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.primefaces.event.RowEditEvent;
import org.primefaces.event.SelectEvent;

/**
 * Clase migrada para visualizar las cuentas configuradas con los
 * conceptos correspondientes
 *
 * @version 1.0, 28/12/2018
 * @author ybecerra
 */
@ManagedBean
@ViewScoped
public class CuentasConfiguradasControlador extends BeanBaseContinuoAcmeImpl {
    /**
     * Constante a nivel de clase que almacena el codigo de la
     * compania en la cual inicio sesion el usuario, el valor de esta
     * constante es asignado en el constructor a la variable de sesion
     * correspondiente
     */
    private final String compania;
    // <DECLARAR_ATRIBUTOS>
    /**
     * Atributo recibido por parametro del formulario configurar plan
     * de cuentas
     */
    private String ano;
    /**
     * Atributo recibido por parametro del formulario configurar plan
     * de cuentas
     */
    private String formato;
    /**
     * Variable que valida la visibilidad de la columna concepto 1001
     */
    private boolean verConceptoExogena;
    /**
     * Variable que validad la visibilidad de la columna concepto
     * distrital
     */
    private boolean verConceptoDistrital;
    // </DECLARAR_ATRIBUTOS>
    // <DECLARAR_PARAMETROS>
    // </DECLARAR_PARAMETROS>
    // <DECLARAR_LISTAS>
    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>
    /**
     * Lista de registros de la tabla CONCEPTOSEX
     */
    private RegistroDataModelImpl listaConceptoExogena;
    /**
     * Lista de registros de la tabla CONCEPTOSEX
     */
    private RegistroDataModelImpl listaConceptoExogenaE;
    /**
     * Lista de registros de la tabla CONCEPTOSEX
     */
    private RegistroDataModelImpl listaConceptoDistrital;
    /**
     * Lista de registros de la tabla CONCEPTOSEX
     */
    private RegistroDataModelImpl listaConceptoDistritalE;
    /**
     * Esta variable se usa como auxiliar para subformularios y en
     * esta se alamcena el identificador del registro que se
     * selecciono
     */
    private String auxiliar;
    
    private String salida;
    
    /**
	 * Variable que almacena la informacion del excel
	 */
	private StringBuilder cadena;
	    
    /**
	 * Este atributo se usa como auxiliar del componente selector de
	 * archivos cargarExcel y funciona como contenedor del archivo que se
	 * debe guardar
	 */
	private ContenedorArchivo contArchivocargarExcel;
	
	@EJB
    private EjbExogenasCeroRemote ejbExogenasCero;

    // </DECLARAR_LISTAS_COMBO_GRANDE>
    /**
     * Crea una nueva instancia de CuentasConfiguradasControlador
     */
    public CuentasConfiguradasControlador() {
        super();
        contArchivocargarExcel = new ContenedorArchivo();
        compania = SessionUtil.getCompania();
        try {
            // 2017
            numFormulario = GeneralCodigoFormaEnum.CUENTAS_CONFIGURADAS_CONTROLADOR
                            .getCodigo();
            validarPermisos();
            // <INI_ADICIONAL>
            Map<String, Object> parametrosEntrada = SessionUtil.getFlash();
            if (parametrosEntrada != null) {
                ano = parametrosEntrada.get(GeneralParameterEnum.ANO.getName()
                                .toLowerCase()).toString();
                formato = parametrosEntrada.get(GeneralParameterEnum.FORMATO
                                .getName().toLowerCase()).toString();
            }
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

        tabla = GenericUrlEnum.DETALLECOMPROBANTECNT.getTable();
        reasignarOrigen();
        buscarLlave();
        // <CARGAR_LISTA>
        // </CARGAR_LISTA>
        // <CARGAR_LISTA_COMBO_GRANDE>
        cargarListaConceptoExogena();
        cargarListaConceptoExogenaE();
        cargarListaConceptoDistrital();
        cargarListaConceptoDistritalE();
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

        parametrosListado.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
        parametrosListado.put(GeneralParameterEnum.ANO.getName(),
                        ano);
        parametrosListado.put(GeneralParameterEnum.FORMATO.getName(),
                        formato);
        if ("1001".equals(formato)) {
            urlListado = UrlServiceUtil.getInstance()
                            .getUrlServiceByUrlByEnumID(
                                            CuentasConfiguradasControladorUrlEnum.URL175
                                                            .getValue());
            verConceptoExogena = true;
            verConceptoDistrital = false;
        }
        else if ("1003".equals(formato)) {
            urlListado = UrlServiceUtil.getInstance()
                            .getUrlServiceByUrlByEnumID(
                                            CuentasConfiguradasControladorUrlEnum.URL183
                                                            .getValue());
            verConceptoDistrital = true;
            verConceptoExogena = false;
        }

        urlActualizacion = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        CuentasConfiguradasControladorUrlEnum.URL193
                                                        .getValue());

    }

    // <METODOS_CARGAR_LISTA>
    /**
     * 
     * Carga la lista listaConceptoExogena
     *
     */
    public void cargarListaConceptoExogena() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        CuentasConfiguradasControladorUrlEnum.URL197
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(ConfigurarPlanContableExsControladorEnum.FORMATO.getValue(),
                        formato);

        listaConceptoExogena = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param, true,
                        GeneralParameterEnum.CODIGO.getName());

    }

    /**
     * 
     * Carga la lista listaConceptoExogena
     *
     */
    public void cargarListaConceptoExogenaE() {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        CuentasConfiguradasControladorUrlEnum.URL197
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(ConfigurarPlanContableExsControladorEnum.FORMATO.getValue(),
                        formato);

        listaConceptoExogenaE = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param, true,
                        GeneralParameterEnum.CODIGO.getName());

    }

    /**
     * 
     * Carga la lista listaConceptoDistrital
     *
     */
    public void cargarListaConceptoDistrital() {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        CuentasConfiguradasControladorUrlEnum.URL197
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(ConfigurarPlanContableExsControladorEnum.FORMATO.getValue(),
                        formato);

        listaConceptoDistrital = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param, true,
                        GeneralParameterEnum.CODIGO.getName());

    }

    /**
     * 
     * Carga la lista listaConceptoDistrital
     *
     */
    public void cargarListaConceptoDistritalE() {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        CuentasConfiguradasControladorUrlEnum.URL197
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(ConfigurarPlanContableExsControladorEnum.FORMATO.getValue(),
                        formato);

        listaConceptoDistritalE = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param, true,
                        GeneralParameterEnum.CODIGO.getName());

    }

    // </METODOS_CARGAR_LISTA>
    // <METODOS_BOTONES>
    /**
	 * 
	 * Metodo ejecutado al oprimir el boton Cargar en la vista
	 *
	 * DOCUMENTACION ADICIONAL
	 *
	 */
	public void oprimirCargar() {

		Workbook workbook = null;
		salida = "";
		try (FileInputStream file = new FileInputStream(contArchivocargarExcel.getArchivo());) {
			

			if (validarArchivo()) {

				String rutaArchivo = contArchivocargarExcel.getArchivo().getPath();

				String extension = rutaArchivo.substring(rutaArchivo.indexOf('.'), rutaArchivo.length()).substring(1,
						rutaArchivo.substring(rutaArchivo.indexOf('.'), rutaArchivo.length()).length());

				if ("xls".equals(extension)) {
					workbook = new HSSFWorkbook(file);
				} else {
					workbook = new XSSFWorkbook(file);
				}
				cadena = new StringBuilder();
				leerHoja(workbook, 0, 9, cadena, 1);
				cargarDatos();							
				}
				ArchivosBean.generarPlano("RelacionDeConceptos.txt", salida);
				JsfUtil.agregarMensajeInformativo(idioma.getString("MSM_PROCESO_EJECUTADO"));
				
			}
		catch (IOException e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}
	}
	
	public void leerHoja(Workbook workbook, int hoja, int columnas,
	        StringBuilder cadena, int filainicial) {
	        cadena.append("TO_CLOB('");
	        Sheet sheet = workbook.getSheetAt(hoja);
	        Row fila;
	        Cell celda;
	        int num = 0;
	        for (int i = filainicial; i < sheet.getLastRowNum() + 1; i++) {
	            fila = sheet.getRow(i);
	            for (int j = 0; j < columnas; j++) {
	                celda = fila.getCell(j);
	                if (celda != null) {
	                    num = num
	                        + (celda.getCellType() == 1 ? celda.getStringCellValue()
	                                        .replaceFirst("'", " ").length()
	                            : NumberToTextConverter
	                                            .toText(celda.getNumericCellValue())
	                                            .length());
	                    cadena.append(celda.getCellType() == 1
	                        ? celda.getStringCellValue().replaceFirst("'", " ")
	                        : NumberToTextConverter
	                                        .toText(celda.getNumericCellValue())); 
	                }
	                else {
	                    cadena.append("");
	                }
	                if (num >= 10000) {
	                    cadena.append("') || TO_CLOB('");
	                    num = 0;
	                }
	                cadena.append(SysmanConstantes.SEPARADOR_COL);
	            }
	            cadena.append(SysmanConstantes.SEPARADOR_REG);
	        }
	        cadena.append("')"
	            + "");
	    }
	private void cargarDatos() {
		
		try {
			String parametro = (SysmanFunciones.esBdSqlServer())
					? cadena.toString().replace("TO_CLOB(", "").replace(")", "")
					: cadena.toString();

			salida =	salida +	ejbExogenasCero.actConceptoExogenaMasivo(compania,ano, parametro, SessionUtil.getUser().getCodigo());
		
		} catch (SystemException  e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}
	}
	
	/**
	 * Valida que se suba un archivo
	 * 
	 * @return
	 */
	public boolean validarArchivo() {
		    
		if (contArchivocargarExcel.getArchivo() == null) {
			JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB4001"));
			return false;
		} else {
			return true;
		}
	}
    // </METODOS_BOTONES>
    // <METODOS_CAMBIAR>
    // </METODOS_CAMBIAR>
    // <METODOS_COMBOS_GRANDES>
    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaConceptoExogena
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaConceptoExogena(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        registro.getCampos().put("CONCEPTO_EX", registroAux.getCampos()
                        .get(GeneralParameterEnum.CODIGO.getName()));
    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaConceptoExogena
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaConceptoExogenaE(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        auxiliar = SysmanFunciones.nvl(
                        registroAux.getCampos().get(
                                        GeneralParameterEnum.CODIGO.getName()),
                        "").toString();
    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaConceptoDistrital
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaConceptoDistrital(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        registro.getCampos().put("CONCEPTO_DIST", registroAux.getCampos()
                        .get(GeneralParameterEnum.CODIGO.getName()));
    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaConceptoDistrital
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaConceptoDistritalE(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        auxiliar = SysmanFunciones.nvl(
                        registroAux.getCampos().get(
                                        GeneralParameterEnum.CODIGO.getName()),
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
     * seleccionado
     */
    @Override
    public void cancelarEdicion(RowEditEvent event) {
        getListaInicial().load();
    }

    /**
     * Metodo ejecutado antes de realizar la insercion del registro
     * 
     * @return true
     */
    @Override
    public boolean insertarAntes() {
        // <CODIGO_DESARROLLADO>
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
     * 
     * @return true
     */
    @Override
    public boolean actualizarAntes() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>

        registro.getCampos().remove(GeneralParameterEnum.NOMBRE.getName());
        registro.getCampos().remove(GeneralParameterEnum.TERCERO.getName());
        registro.getCampos().remove(GeneralParameterEnum.VALOR_DEBITO.getName());
        registro.getCampos().remove(GeneralParameterEnum.VALOR_CREDITO.getName());

        return true;
    }

    /**
     * Metodo ejecutado despues de realizar la insercion y
     * actualizacion del registro
     * 
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
        registro.getCampos().remove(GeneralParameterEnum.COMPANIA.getName());
        registro.getCampos().remove(GeneralParameterEnum.ANO.getName());
        registro.getCampos().remove(GeneralParameterEnum.TIPO_CPTE.getName());
        registro.getCampos().remove(GeneralParameterEnum.COMPROBANTE.getName());
        registro.getCampos().remove(GeneralParameterEnum.CONSECUTIVO.getName());
        registro.getCampos().remove(GeneralParameterEnum.CUENTA.getName());
        registro.getCampos().remove(GeneralParameterEnum.DESCRIPCION.getName());
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado desde un comando remoto cuando se cierra el
     * formulario
     */
    public void ejecutarrcCerrar() {
        // <CODIGO_DESARROLLADO>
        Direccionador direccionador = new Direccionador();
        direccionador.setNumForm(Integer
                        .toString(GeneralCodigoFormaEnum.CONFIGURACION_PLAN_CONTABLE_EX_CONTROLADOR
                                        .getCodigo()));
        SessionUtil.redireccionarForma(direccionador, SessionUtil.getModulo());
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
     * @param ano
     * Variable a asignar en ano
     */
    public void setAno(String ano) {
        this.ano = ano;
    }

    /**
     * Retorna la variable formato
     * 
     * @return formato
     */
    public String getFormato() {
        return formato;
    }

    /**
     * Asigna la variable formato
     * 
     * @param formato
     * Variable a asignar en formato
     */
    public void setFormato(String formato) {
        this.formato = formato;
    }

    /**
     * Retorna la variable verConceptoExogena
     * 
     * @return verConceptoExogena
     */
    public boolean isVerConceptoExogena() {
        return verConceptoExogena;
    }

    /**
     * Asigna la variable verConceptoExogena
     * 
     * @param verConceptoExogena
     * Variable a asignar en verConceptoExogena
     */
    public void setVerConceptoExogena(boolean verConceptoExogena) {
        this.verConceptoExogena = verConceptoExogena;
    }

    /**
     * Retorna la variable verConceptoDistrital
     * 
     * @return verConceptoDistrital
     */
    public boolean isVerConceptoDistrital() {
        return verConceptoDistrital;
    }

    /**
     * Asigna la variable verConceptoDistrital
     * 
     * @param verConceptoDistrital
     * Variable a asignar en verConceptoDistrital
     */
    public void setVerConceptoDistrital(boolean verConceptoDistrital) {
        this.verConceptoDistrital = verConceptoDistrital;
    }

    // </SET_GET_ATRIBUTOS>
    // <SET_GET_PARAMETROS>
    // </SET_GET_PARAMETROS>
    // <SET_GET_LISTAS>
    // </SET_GET_LISTAS>
    // <SET_GET_LISTAS_COMBO_GRANDE>
    /**
     * Retorna la lista listaConceptoExogena
     * 
     * @return listaConceptoExogena
     */
    public RegistroDataModelImpl getListaConceptoExogena() {
        return listaConceptoExogena;
    }

    /**
     * Asigna la lista listaConceptoExogena
     * 
     * @param listaConceptoExogena
     * Variable a asignar en listaConceptoExogena
     */
    public void setListaConceptoExogena(
        RegistroDataModelImpl listaConceptoExogena) {
        this.listaConceptoExogena = listaConceptoExogena;
    }

    /**
     * Retorna la lista listaConceptoExogena
     * 
     * @return listaConceptoExogena
     */
    public RegistroDataModelImpl getListaConceptoExogenaE() {
        return listaConceptoExogenaE;
    }

    /**
     * Asigna la lista listaConceptoExogena
     * 
     * @param listaConceptoExogena
     * Variable a asignar en listaConceptoExogena
     */
    public void setListaConceptoExogenaE(
        RegistroDataModelImpl listaConceptoExogenaE) {
        this.listaConceptoExogenaE = listaConceptoExogenaE;
    }

    /**
     * Retorna la lista listaConceptoDistrital
     * 
     * @return listaConceptoDistrital
     */
    public RegistroDataModelImpl getListaConceptoDistrital() {
        return listaConceptoDistrital;
    }

    /**
     * Asigna la lista listaConceptoDistrital
     * 
     * @param listaConceptoDistrital
     * Variable a asignar en listaConceptoDistrital
     */
    public void setListaConceptoDistrital(
        RegistroDataModelImpl listaConceptoDistrital) {
        this.listaConceptoDistrital = listaConceptoDistrital;
    }

    /**
     * Retorna la lista listaConceptoDistrital
     * 
     * @return listaConceptoDistrital
     */
    public RegistroDataModelImpl getListaConceptoDistritalE() {
        return listaConceptoDistritalE;
    }

    /**
     * Asigna la lista listaConceptoDistrital
     * 
     * @param listaConceptoDistrital
     * Variable a asignar en listaConceptoDistrital
     */
    public void setListaConceptoDistritalE(
        RegistroDataModelImpl listaConceptoDistritalE) {
        this.listaConceptoDistritalE = listaConceptoDistritalE;
    }

    /**
     * Retorna la variable auxiliar
     * 
     * @return auxiliar
     */
    public String getAuxiliar() {
        return auxiliar;
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
    // </SET_GET_LISTAS_COMBO_GRANDE>

	public String getSalida() {
		return salida;
	}

	public void setSalida(String salida) {
		this.salida = salida;
	}

	public StringBuilder getCadena() {
		return cadena;
	}

	public void setCadena(StringBuilder cadena) {
		this.cadena = cadena;
	}	

	public ContenedorArchivo getContArchivocargarExcel() {
		return contArchivocargarExcel;
	}

	public void setContArchivocargarExcel(ContenedorArchivo contArchivocargarExcel) {
		this.contArchivocargarExcel = contArchivocargarExcel;
	}	
}
