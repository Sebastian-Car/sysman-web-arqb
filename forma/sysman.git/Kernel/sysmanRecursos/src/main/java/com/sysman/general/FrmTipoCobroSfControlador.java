/*-
 * FrmTipoCobroSfControlador.java
 *
 * 1.0
 * 
 * 23/11/2017
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */
package com.sysman.general;

import com.sysman.beanbase.BeanBaseDatosAcmeImpl;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.enums.GenericUrlEnum;
import com.sysman.exception.SystemException;
import com.sysman.general.enums.FrmTipoCobroSfControladorEnum;
import com.sysman.general.enums.FrmTipoCobroSfControladorUrlEnum;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.recursos.ejb.EjbSysmanUtilRemote;
import com.sysman.util.SysmanFunciones;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

/**
 * Formulario que lista los tipo de cobro por a�o, permite actualizar
 * los datos del cliente, eliminar o simplemente vizualizarlos.
 * 
 * @version 1.0, 23/11/2017
 * @author jromero
 */
@ManagedBean
@ViewScoped
public class FrmTipoCobroSfControlador extends BeanBaseDatosAcmeImpl {
    /**
     * Constante a nivel de clase que almacena el codigo de la
     * compania en la cual inicio sesion el usuario, el valor de esta
     * constante es asignado en el constructor a la variable de sesion
     * correspondiente
     */
    private final String compania;
    
    private int ano;

    // <DECLARAR_ATRIBUTOS>
    // </DECLARAR_ATRIBUTOS>
    // <DECLARAR_LISTAS>
    // </DECLARAR_ATRIBUTOS>
    // <DECLARAR_LISTAS>

    private List<Registro> listaAno;
    /**
     * Lista que carga los comprobantes de causacion para facturas
     * diferidas
     */
    private List<Registro> listaCpteFacDiferida;
    private List<Registro> listaCPTERECAUDO;
    private List<Registro> listaCPTEANULACION;
    private List<Registro> listaMenuCobro;
    private List<Registro> listaFormatoPlantilla;
    private List<Registro> listaCPTECAUSAINTERESES;
    private List<Registro> listaTipoCpteDevolucion;

    /**
     * Lista que carga los tipos de factura DIAN
     */
    private List<Registro> listaTipoFacDian;
    /**
     * Lista que carga los tipos de moneda
     */
    private List<Registro> listaTipoMoneda;

    /**
     * Variable que toma el valor del parametro SF CODIGO EAN POR CADA
     * TIPO DE COBRO y administra la vsibilidad del campo CODIGOEAN
     */
    private String visibleCodigoEan;

    private boolean bloquearDias;

    private String manejaAcuerdo;

    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>
    // </DECLARAR_LISTAS_COMBO_GRANDE>
    // <DECLARAR_LISTAS_SUBFORM>
    // </DECLARAR_LISTAS_SUBFORM>
    // <DECLARAR_PARAMETROS>
    // </DECLARAR_PARAMETROS>
    // <DECLARAR_ADICIONALES>
    // </DECLARAR_ADICIONALES>

    @EJB
    private EjbSysmanUtilRemote ejbSysmanUtil;

    /**
     * Crea una nueva instancia de
     */
    public FrmTipoCobroSfControlador() {
        super();
        compania = SessionUtil.getCompania();
        bloquearDias = true;
        visibleCodigoEan = "SI";
        try {
            numFormulario = GeneralCodigoFormaEnum.FRM_TIPOCOBRO_SF_CONTROLADOR
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
     * En este metodo se hace la invocacion de lo metodos de carga de
     * todas las listas, menos las que son de subformularios
     */
    @Override
    public void iniciarListas() {
        // <CARGAR_LISTA_COMBO_GRANDE>
        // </CARGAR_LISTA_COMBO_GRANDE>
        // <CARGAR_LISTA>
        cargarListaANO();
        cargarListaCPTERECAUDO();
        cargarListaCPTEANULACION();
        cargarListaCpteFacDiferida();
        cargarListaMenuCobro();
        cargarListaFormatoPlantilla();
        cargarListaTipoFacDian();
        cargarListaTipoMoneda();
        cargarListaCPTECAUSAINTERESES();
        cargarListaTipoCpteDevolucion();
        // </CARGAR_LISTA>
    }

    /**
     * En este metodo se hace la invocacion de lo metodos de carga de
     * todas las listas que son de subformularios
     */
    @Override
    public void iniciarListasSub() {
        // <CARGAR_LISTAS_SUBFORM>
        // </CARGAR_LISTAS_SUBFORM>
        // <CREAR_ARBOLES>
        // </CREAR_ARBOLES>
    }

    /**
     * En este metodo se iguala a null todas las listas de los
     * subformularios
     */
    @Override
    public void iniciarListasSubNulo() {
        // <CARGAR_LISTAS_SUBFORM_NULL>
        // </CARGAR_LISTAS_SUBFORM_NULL>
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
        enumBase = GenericUrlEnum.TIPOCOBRO;
        buscarLlave();
        asignarOrigenDatos();
    }

    /**
     * Se realiza la asignacion de la variable origenDatos por la
     * consulta correspondiente del formulario
     * 
     * 
     */
    @Override
    public void asignarOrigenDatos() {
        buscarUrls();
        parametrosListado.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
        if (ano > 0) {
    		parametrosListado.put(
    				FrmTipoCobroSfControladorEnum.ANO.getValue(),
    				String.valueOf(ano));
        	}
    }

    /**
     * Se realiza la asignacion de la variable origenGrilla por la
     * consulta correspondiente de la grilla del formulario, se hace
     * la asignacion de dicha consulta a los objetos listaInicial y
     * listaInicialF
     * 
     * 
     */
    // <METODOS_CARGAR_LISTA>
    /**
     * 
     * Carga la lista listaANO
     *
     */
    public void cargarListaANO() {

        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);

        try {
            listaAno = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            FrmTipoCobroSfControladorUrlEnum.URL5127
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
     * Carga la lista listaCpteFacDiferida
     *
     */
    public void cargarListaCpteFacDiferida() {

        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        param.put("CLASECONTABLE", "V");

        try {
            listaCpteFacDiferida = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            FrmTipoCobroSfControladorUrlEnum.URL4980
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
     * Carga la lista listaReacudo
     *
     */
    public void cargarListaCPTERECAUDO() {

        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        try {
            listaCPTERECAUDO = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            FrmTipoCobroSfControladorUrlEnum.URL5970
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
     * Carga la lista listaFormatoPlantilla
     *
     */
    public void cargarListaFormatoPlantilla() {

        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.CLASE.getName(),
                        "60");

        try {
            listaFormatoPlantilla = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            FrmTipoCobroSfControladorUrlEnum.URL4981
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
     * Carga la lista listaTipoFacDian
     *
     */
    public void cargarListaTipoFacDian() {

        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.TIPO.getName(),
                        "5");

        try {
            listaTipoFacDian = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            FrmTipoCobroSfControladorUrlEnum.URL3784
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
     * Carga la lista listaTipoMoneda
     *
     */
    public void cargarListaTipoMoneda() {

        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        try {
            listaTipoMoneda = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            FrmTipoCobroSfControladorUrlEnum.URL8894
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }
    
    public void cargarListaCPTECAUSAINTERESES(){
    	
	    	Map<String, Object> param = new TreeMap<>();
	        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
	        try {
	        	listaCPTECAUSAINTERESES = RegistroConverter.toListRegistro(
	                            requestManager.getList(UrlServiceUtil.getInstance()
	                                            .getUrlServiceByUrlByEnumID(
	                                                            FrmTipoCobroSfControladorUrlEnum.URL15011
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
     * Carga la lista listaAnulacion
     *
     */
    public void cargarListaCPTEANULACION() {
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        try {
            listaCPTEANULACION = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            FrmTipoCobroSfControladorUrlEnum.URL6724
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
     * Carga la lista listaMenuCobro
     *
     */
    public void cargarListaMenuCobro() {
        Map<String, Object> param = new TreeMap<>();

        try {
            listaMenuCobro = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            FrmTipoCobroSfControladorUrlEnum.URL3030
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
     * Carga la lista listaTipoCpteDevolucion
     *
     */
    public void cargarListaTipoCpteDevolucion(){

    	Map<String, Object> param = new TreeMap<>();
    	param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
    	try {
    		listaTipoCpteDevolucion = RegistroConverter.toListRegistro(
    				requestManager.getList(UrlServiceUtil.getInstance()
    						.getUrlServiceByUrlByEnumID(
    								FrmTipoCobroSfControladorUrlEnum.URL6724
    								.getValue())
    						.getUrl(), param));
    	}
    	catch (SystemException e) {
    		logger.error(e.getMessage(), e);
    		JsfUtil.agregarMensajeError(e.getMessage());

    	}
    }

    // </METODOS_CARGAR_LISTA>
    // <METODOS_CAMBIAR>
    /**
     * Metodo ejecutado al cambiar el control Ano
     * 
     * 
     * 
     */
    public void cambiarAno() {
        // <CODIGO_DESARROLLADO>

        registro.getCampos().put(GeneralParameterEnum.ANO.getName(),
                        registro.getCampos().get("ANO").toString());
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado al cambiar el control VENCIMIENTO_FACTURACION
     * 
     * 
     */
    public void cambiarVencimientoFacturacion() {

        validarCampoVencimientoDias();

    }

    private void validarCampoVencimientoDias() {
        if ("RD".equals(registro.getCampos().get("VENCIMIENTO_FACTURACION"))) {
            bloquearDias = false;
        }
        else {
            bloquearDias = true;

        }

    }

    // </METODOS_CAMBIAR>
    // <METODOS_COMBOS_GRANDES>
    // </METODOS_COMBOS_GRANDES>
    // <METODOS_ARBOL>
    // </METODOS_ARBOL>
    // <METODOS_BOTONES>
    // </METODOS_BOTONES>
    // <METODOS_SUBFORM>
    // </METODOS_SUBFORM>
    // <METODOS_ADICIONALES>
    // </METODOS_ADICIONALES>
    /**
     * Este metodo es invocado el metodo inicializar, se ejecutan las
     * acciones a tener en cuenta en el momento de apertura del
     * formulario
     */
    @Override
    public void abrirFormulario() {
        // <CODIGO_DESARROLLADO>

        try {
            /*
             * FR1469-AL_ABRIR Private Sub Form_Open(Cancel As
             * Integer) formularioAbrir 12, Me.Name ' docmd.Restore
             * Dim db As DAO.Database Set db = CurrentDb db.Execute
             * "UPDATE TIPO_COBRO_SF SET INTERFAZ_RECAUDO = 0 WHERE INTERFAZ_RECAUDO IS NULL"
             * db.Execute
             * "UPDATE TIPO_COBRO_SF SET MANEJA_RECAUDOTERCEROS = 0 WHERE MANEJA_RECAUDOTERCEROS IS NULL "
             * db.Execute
             * "UPDATE TIPO_COBRO_SF SET TIPOCOBRO_NOFACTURADO = 0 WHERE TIPOCOBRO_NOFACTURADO IS NULL "
             * End Sub
             */

            visibleCodigoEan = SysmanFunciones
                            .nvl(ejbSysmanUtil.consultarParametro(compania,
                                            "SF CODIGO EAN POR CADA TIPO DE COBRO",
                                            "69",
                                            new Date(),
                                            false), "NO")
                            .toString();
            manejaAcuerdo = SysmanFunciones
                            .nvl(ejbSysmanUtil.consultarParametro(compania,
                                            "PERMITE ACUERDO PREDIAL DESDE FACTURACION",
                                            "69",
                                            new Date(),
                                            false), "NO")
                            .toString();
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

        // </CODIGO_DESARROLLADO>
    }

    @Override
    public void cargarRegistro() {
        // <CODIGO_DESARROLLADO>
        precargarRegistro();

        if (accion.equals(ACCION_MODIFICAR)) {
            validarCampoVencimientoDias();
        }
        
        if (accion.equals(ACCION_INSERTAR)) {
        	ano = SysmanFunciones
        			.ano(new Date());
        	String consecutivo = ano + "000001";
			registro.getCampos().put("CONSECUTIVO_COBRO", consecutivo);

        }

        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado antes de realizar la insercion del registro
     * 
     */
    @Override
    public boolean insertarAntes() {
        // <CODIGO_DESARROLLADO>
        registro.getCampos().put("COMPANIA", compania);
        // </CODIGO_DESARROLLADO>
        return true;
    }

    /**
     * Metodo ejecutado despues de realizar la insercion del registro
     * 
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
     */
    @Override
    public boolean actualizarAntes() {
        // <CODIGO_DESARROLLADO>
        if (accion.equals(ACCION_MODIFICAR)) {

            registro.getCampos().remove("COMPANIA");

        }
        // </CODIGO_DESARROLLADO>
        return true;
    }

    /**
     * Metodo ejecutado despues de realizar la insercion y
     * actualizacion del registro
     * 
     * 
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
     */
    @Override
    public boolean eliminarDespues() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
        return true;
    }

    // <SET_GET_ATRIBUTOS>
    // </SET_GET_ATRIBUTOS>
    // <SET_GET_LISTAS>
    /**
     * Retorna la lista listaCPTERECAUDO
     * 
     * @return listaCPTERECAUDO
     */
    public List<Registro> getListaCPTERECAUDO() {
        return listaCPTERECAUDO;
    }

    /**
     * Asigna la lista listaCPTERECAUDO
     * 
     * @param listaCPTERECAUDO
     * Variable a asignar en listaCPTERECAUDO
     */
    public void setListaCPTERECAUDO(List<Registro> listaCPTERECAUDO) {
        this.listaCPTERECAUDO = listaCPTERECAUDO;
    }

    /**
     * Retorna la lista listaCPTEANULACION
     * 
     * @return listaCPTEANULACION
     */
    public List<Registro> getListaCPTEANULACION() {
        return listaCPTEANULACION;
    }

    /**
     * Asigna la lista listaCPTEANULACION
     * 
     * @param listaCPTEANULACION
     * Variable a asignar en listaCPTEANULACION
     */
    public void setListaCPTEANULACION(List<Registro> listaCPTEANULACION) {
        this.listaCPTEANULACION = listaCPTEANULACION;
    }

    /**
     * Retorna la lista listaAno
     * 
     * @return listaAno
     */
    public List<Registro> getListaAno() {
        return listaAno;
    }

    /**
     * Asigna la lista listaAno
     * 
     * @param listaAno
     * Variable a asignar en listaAno
     */
    public void setListaAno(List<Registro> listaAno) {
        this.listaAno = listaAno;
    }

    /**
     * Retorna la lista listaCpteFacDiferida
     * 
     * @return listaCpteFacDiferida
     */
    public List<Registro> getListaCpteFacDiferida() {
        return listaCpteFacDiferida;
    }

    /**
     * Asigna la lista listaCpteFacDiferida
     * 
     * @param listaCpteFacDiferida
     * Variable a asignar en listaCpteFacDiferida
     */
    public void setListaCpteFacDiferida(List<Registro> listaCpteFacDiferida) {
        this.listaCpteFacDiferida = listaCpteFacDiferida;
    }

    /**
     * Retorna la lista listaMenuCobro
     * 
     * @return listaMenuCobro
     */
    public List<Registro> getListaMenuCobro() {
        return listaMenuCobro;
    }

    /**
     * Asigna la lista listaMenuCobro
     * 
     * @param listaMenuCobro
     * Variable a asignar en listaMenuCobro
     */
    public void setListaMenuCobro(List<Registro> listaMenuCobro) {
        this.listaMenuCobro = listaMenuCobro;
    }

    public boolean isBloquearDias() {
        return bloquearDias;
    }

    public void setBloquearDias(boolean bloquearDias) {
        this.bloquearDias = bloquearDias;
    }

    public String getVisibleCodigoEan() {
        return visibleCodigoEan;
    }

    public void setVisibleCodigoEan(String visibleCodigoEan) {
        this.visibleCodigoEan = visibleCodigoEan;
    }

    public List<Registro> getListaFormatoPlantilla() {
        return listaFormatoPlantilla;
    }

    public void setListaFormatoPlantilla(List<Registro> listaFormatoPlantilla) {
        this.listaFormatoPlantilla = listaFormatoPlantilla;
    }

    public List<Registro> getListaTipoFacDian() {
        return listaTipoFacDian;
    }

    /**
     * Asigna la lista listaTipoFacDian
     * 
     * @param listaTipoFacDian
     * Variable a asignar en listaTipoFacDian
     */
    public void setListaTipoFacDian(List<Registro> listaTipoFacDian) {
        this.listaTipoFacDian = listaTipoFacDian;
    }

    /**
     * Retorna la lista listaTipoMoneda
     * 
     * @return listaTipoMoneda
     */
    public List<Registro> getListaTipoMoneda() {
        return listaTipoMoneda;
    }

    /**
     * Asigna la lista listaTipoMoneda
     * 
     * @param listaTipoMoneda
     * Variable a asignar en listaTipoMoneda
     */
    public void setListaTipoMoneda(List<Registro> listaTipoMoneda) {
        this.listaTipoMoneda = listaTipoMoneda;
    }
    
    /**
     * Retorna la lista listaCPTECAUSAINTERESES
     * 
     * @return listaCPTECAUSAINTERESES
     */
public List<Registro> getListaCPTECAUSAINTERESES() {
        return listaCPTECAUSAINTERESES;
    }
    /**
     * Asigna la lista listaCPTECAUSAINTERESES
     * 
     * @param listaCPTECAUSAINTERESES
     * Variable a asignar en  listaCPTECAUSAINTERESES
     */
public void setListaCPTECAUSAINTERESES(List<Registro> listaCPTECAUSAINTERESES) {
        this.listaCPTECAUSAINTERESES = listaCPTECAUSAINTERESES;
    }

    public String getManejaAcuerdo() {
        return manejaAcuerdo;
    }

    public void setManejaAcuerdo(String manejaAcuerdo) {
        this.manejaAcuerdo = manejaAcuerdo;
    }

	/**
	 * @return the listaTipoCpteDevolucion
	 */
	public List<Registro> getListaTipoCpteDevolucion() {
		return listaTipoCpteDevolucion;
	}

	/**
	 * @param listaTipoCpteDevolucion the listaTipoCpteDevolucion to set
	 */
	public void setListaTipoCpteDevolucion(List<Registro> listaTipoCpteDevolucion) {
		this.listaTipoCpteDevolucion = listaTipoCpteDevolucion;
	}
	
	public int getAno() {
			return ano;
	}

	public void setAno(int ano) {
			this.ano = ano;
	}
	

    // </SET_GET_LISTAS>
    // <SET_GET_LISTAS_COMBO_GRANDE>
    // </SET_GET_LISTAS_COMBO_GRANDE>
    // <SET_GET_LISTAS_SUBFORM>
    // </SET_GET_LISTAS_SUBFORM>
    // <SET_GET_PARAMETROS>
    // </SET_GET_PARAMETROS>
    // <SET_GET_ADICIONALES>
    // </SET_GET_ADICIONALES>

}
