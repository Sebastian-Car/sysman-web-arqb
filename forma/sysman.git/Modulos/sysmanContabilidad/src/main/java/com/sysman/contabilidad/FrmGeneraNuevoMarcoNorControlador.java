/*-
 * FrmGeneraNuevoMarcoNorControlador.java
 *
 * 1.0
 * 
 * 14/02/2020
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */

package com.sysman.contabilidad;

import com.sysman.beanbase.BeanBaseModal;
import com.sysman.contabilidad.ejb.EjbContabilidadCuatroRemote;
import com.sysman.contabilidad.enums.FrmGeneraNuevoMarcoNorControladorEnum;
import com.sysman.contabilidad.enums.FrmGeneraNuevoMarcoNorControladorUrlEnum;
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
import com.sysman.util.SysmanConstantes;
import com.sysman.util.SysmanFunciones;

import java.io.IOException;
import java.text.ParseException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.event.SelectEvent;
import org.primefaces.model.StreamedContent;

import net.sf.jasperreports.engine.JRException;

/**
 * Formulario que permite imprimir los reportes de estados financieros
 *
 * @version 1.0, 14/02/2020
 * @author eamaya
 */
@ManagedBean
@ViewScoped
public class FrmGeneraNuevoMarcoNorControlador extends BeanBaseModal {
    /**
     * Constante a nivel de clase que almacena el codigo de la
     * compania en la cual inicio sesion el usuario, el valor de esta
     * constante es asignado en el constructor a la variable de sesion
     * correspondiente
     */
    private final String compania;

    private final String usuario;
    
    private final String modulo;
    /**
     * Constante a nivel de clase que aloja el nombre de la compania
     * desde la cual el usuario inicio sesion.
     */
    private final String nombreCompania;
    // <DECLARAR_ATRIBUTOS>

    /**
     * Variable que almacena el valor del indicador Estado Situacion
     * Financiera
     */
    private boolean situacionFinanciera;
    
    /**
     * Variable que almacena el valor del indicador Estado Situacion
     * Financiera Mayorizada
     */
    private boolean situaFinancieraMayorizada;
    
    /**
     * Variable que almacena el valor del indicador Estado Resultados
     * Individuales
     */
    private boolean resultadosIndividuales;
    /**
     * Variable que almacena el valor del indicador Estado Cambios
     * Patrimonio
     */
    private boolean cambiosPatrimonio;
    /**
     * Variable que almacena el valor del indicador Estado Flujo
     * Efectivo
     */
    private boolean flujoEfectivo;
    
    /**
     * Variable que almacena el valor del indicador Incluye Variaciones
     */
    private boolean incluyeVariaciones;
    
    /**
	 * Variable que almacena el valor del indicador Estado Flujo Efectivo Acumulado
	 */
	private boolean flujoEfectivoAcumulado;
    
    
    /**
     * impresion especial excel
     */   
    private boolean reporteExcel;

    /**
     * Variable que almacena el codigo inicial seleccionado en la
     * vista
     */
    private String codigoInicial;
    /**
     * Variable que almacena el codigo final seleccionado en la vista
     */
    private String codigoFinal;
    /**
     * Variable que almacena el mes de trabajo seleccionado en la
     * vista
     */
    private String mesTrabajo;
    /**
     * Variable que almacena el mes a comparar seleccionado en la
     * vista
     */
    private String mesComparar;
    /**
     * Variable que almacena el anio de trabajo seleccionado en la
     * vista
     */
    private String anioTrabajo;
    /**
     * Variable que almacena el anio a comparar seleccionado en la
     * vista
     */
    private String anioComparar;

    /**
     * Valores del parametro DIGITOS PARA CONFIGURACION DE INFORMES
     */
    private String digitos;

    /**
     * Atributo usado para descargar contenidos de archivos desde la
     * vista
     */
    private StreamedContent archivoDescarga;
    // </DECLARAR_ATRIBUTOS>
    // <DECLARAR_PARAMETROS>
    // </DECLARAR_PARAMETROS>
    // <DECLARAR_LISTAS>
    /**
     * Lista que carga los meses de trabajo por anio
     */
    private List<Registro> listaMesTrabajo;
    /**
     * Lista que carga los meses a comparar por anio
     */
    private List<Registro> listaMesComparar;
    /**
     * Lista que carga los anios de trabajo
     */
    private List<Registro> listaAnoTrabajo;
    /**
     * Lista que carga los anios a comparar
     */
    private List<Registro> listaAnoComparar;

    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>
    /**
     * Lista que carga los codigos iniciales
     */
    private RegistroDataModelImpl listaCodigoInicial;
    /**
     * Lista que carga los codigos finales
     */
    private RegistroDataModelImpl listaCodigoFinal;
    // </DECLARAR_LISTAS_COMBO_GRANDE>

    @EJB
    private EjbSysmanUtilRemote ejbSysmanUtil;
    @EJB
    private EjbContabilidadCuatroRemote ejbContabilidadCuatro;

    /**
     * Crea una nueva instancia de FrmGeneraNuevoMarcoNorControlador
     */
    public FrmGeneraNuevoMarcoNorControlador() {
        super();
        compania = SessionUtil.getCompania();
        usuario = SessionUtil.getUser().getCodigo();
        modulo = SessionUtil.getModulo();
        nombreCompania = SessionUtil.getCompaniaIngreso().getNombre();
        anioTrabajo = Integer.toString(SysmanFunciones.ano(new Date()));
        anioComparar = Integer.toString(SysmanFunciones.ano(new Date()));
        mesTrabajo = Integer.toString(SysmanFunciones.mes(new Date()));
        mesComparar = Integer.toString(SysmanFunciones.mes(new Date()));
        try {

            // 2159
            numFormulario = GeneralCodigoFormaEnum.FRM_GENERA_NUEVO_MARCO_NOR_CONTROLADOR
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
     * Este metodo se ejecuta justo despues de que el objeto de la
     * clase del Bean ha sido creado, en este se realizan las
     * asignaciones iniciales necesarias para la visualizacion del
     * formulario, como son tablas, origenes de datos, inicializacion
     * de listas y demas necesarios
     */
    @PostConstruct
    public void inicializar() {
        // <CARGAR_LISTA>
        cargarListaAnoTrabajo();
        cargarListaAnoComparar();
        cargarListaMesTrabajo();
        cargarListaMesComparar();
        // </CARGAR_LISTA>
        // <CARGAR_LISTA_COMBO_GRANDE>
        cargarListaCodigoInicial();
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
        // <CODIGO_DESARROLLADO>
        digitos = "4";
        codigoInicial = SysmanConstantes.DEFECTOINICIAL_STRING;
        codigoFinal = SysmanConstantes.DEFECTOFINAL_STRING;
        // </CODIGO_DESARROLLADO>
    }

    // <METODOS_CARGAR_LISTA>
    /**
     * 
     * Carga la lista listaMesTrabajo
     *
     */
    public void cargarListaMesTrabajo() {
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);

        param.put(GeneralParameterEnum.ANO.getName(),
                        anioTrabajo);

        try {
            listaMesTrabajo = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            FrmGeneraNuevoMarcoNorControladorUrlEnum.URL6197
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
     * Carga la lista listaMesComparar
     *
     */
    public void cargarListaMesComparar() {
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);

        param.put(GeneralParameterEnum.ANO.getName(),
                        anioComparar);

        try {
            listaMesComparar = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            FrmGeneraNuevoMarcoNorControladorUrlEnum.URL6594
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
     * Carga la lista listaAnoTrabajo
     *
     */
    public void cargarListaAnoTrabajo() {
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);

        try {
            listaAnoTrabajo = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            FrmGeneraNuevoMarcoNorControladorUrlEnum.URL6990
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
     * Carga la lista listaAnoComparar
     *
     */
    public void cargarListaAnoComparar() {
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);

        try {
            listaAnoComparar = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            FrmGeneraNuevoMarcoNorControladorUrlEnum.URL7433
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
     * Carga la lista listaCodigoInicial
     *
     */
    public void cargarListaCodigoInicial() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        FrmGeneraNuevoMarcoNorControladorUrlEnum.URL7882
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();

        param.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
        param.put(FrmGeneraNuevoMarcoNorControladorEnum.ANIOTRABAJO.getValue(),
                        anioTrabajo);

        param.put(FrmGeneraNuevoMarcoNorControladorEnum.ANIOCOMPARAR.getValue(),
                        anioComparar);

        listaCodigoInicial = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, GeneralParameterEnum.CODIGO.getName());
    }

    /**
     * 
     * Carga la lista listaCodigoFinal
     *
     */
    public void cargarListaCodigoFinal() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        FrmGeneraNuevoMarcoNorControladorUrlEnum.URL9675
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();

        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        param.put(FrmGeneraNuevoMarcoNorControladorEnum.CODIGOINICIAL
                        .getValue(),
                        codigoInicial);

        param.put(FrmGeneraNuevoMarcoNorControladorEnum.ANIOTRABAJO.getValue(),
                        anioTrabajo);

        param.put(FrmGeneraNuevoMarcoNorControladorEnum.ANIOCOMPARAR.getValue(),
                        anioComparar);

        listaCodigoFinal = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, GeneralParameterEnum.CODIGO.getName());
    }

    // </METODOS_CARGAR_LISTA>
    // <METODOS_BOTONES>
    /**
     * 
     * Metodo ejecutado al oprimir el boton Imprimir en la vista
     *
     *
     */
    public void oprimirImprimir() {
        // <CODIGO_DESARROLLADO>
        archivoDescarga = null;
        if (!validarCheckVacios()) {
            generarReporte(FORMATOS.PDF);
        }
        else {
            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB2124"));
        }

        
        try {
        		ejbContabilidadCuatro.actualizar3110(compania,
        				Integer.parseInt(anioTrabajo),
        				Integer.parseInt(anioComparar),
        				Integer.parseInt(mesTrabajo),
        				Integer.parseInt(mesComparar), false,
        				usuario);
        }
        catch (NumberFormatException | SystemException e) {
        	logger.error(e.getMessage(), e);
        	JsfUtil.agregarMensajeError(e.getMessage());
        }

        // </CODIGO_DESARROLLADO>
    }
    
    /**
     * 
     * Metodo ejecutado al oprimir el boton Imprimir Excel en la vista
     *
     *
     */
    public void oprimirImprimirExcel() 
    {
        // <CODIGO_DESARROLLADO>
    	archivoDescarga = null;
    	reporteExcel = true;
        if (!validarCheckVacios()) {
            generarReporte(FORMATOS.EXCEL);
        }
        else {
            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB2124"));
        }

        try {
        		ejbContabilidadCuatro.actualizar3110(compania,
        				Integer.parseInt(anioTrabajo),
        				Integer.parseInt(anioComparar),
        				Integer.parseInt(mesTrabajo),
        				Integer.parseInt(mesComparar), false,
        				usuario);
        }
        catch (NumberFormatException | SystemException e) {
        	logger.error(e.getMessage(), e);
        	JsfUtil.agregarMensajeError(e.getMessage());
        }
        // </CODIGO_DESARROLLADO>
    }

    private boolean validarCheckVacios() {
        return !situacionFinanciera && !resultadosIndividuales
            && !cambiosPatrimonio && !flujoEfectivo;

        
    }

    private void generarReporte(FORMATOS formato) {

        String reporte;
        Map<String, Object> reemplazar = new HashMap<>();
        Map<String, Object> parametros = new HashMap<>();

        reporte = nombreReporte();
        try {        	
            // <REEMPLAZAR VARIABLES EN CONSULTA>
            reemplazar.put("compania", compania);
            reemplazar.put("mesTrabajo", mesTrabajo);
            reemplazar.put("mesComparar", mesComparar);
            reemplazar.put("anioTrabajo", anioTrabajo);
            reemplazar.put("anioComparar", anioComparar);
            reemplazar.put("codigoInicial", codigoInicial);
            reemplazar.put("codigoFinal", codigoFinal);
            reemplazar.put("digitos", digitos);
            
          //Se usa logica para que el informe genere con sus auxiliares
            if(obtenerParametro("ESTADOS_FINANCIEROS_POR_AUXILIARES", "NO").equals("NO")) {
            	reemplazar.put("condiciondigitos", FrmGeneraNuevoMarcoNorControladorEnum.CONDICIONDIGITOS.getValue()+digitos);
            	//este parametro debe ir vacio para no generar error en la consulta del reporte
            	reemplazar.put("condicionmovauxiliares", "");
            }else {
            	reemplazar.put("condiciondigitos", FrmGeneraNuevoMarcoNorControladorEnum.CONDICIONDIGITOS6.getValue());
            	reemplazar.put("condicionmovauxiliares", FrmGeneraNuevoMarcoNorControladorEnum.CONDICIONMOVAUXILIARES.getValue());
            }
            
            // <ENVIAR PARAMETROS AL REPORTE>

            parametros.put("PR_NOMBRECOMPANIA", nombreCompania);
            parametros.put("PR_ANIOCOMPARAR", anioComparar);
            parametros.put("PR_ANIOTRABAJO", anioTrabajo);
            parametros.put("PR_MESCOMPARAR", mesComparar);
            parametros.put("PR_MESTRABAJO", mesTrabajo);

            parametros.put("PR_NOMBRE_MESCOMPARAR", ejbSysmanUtil
                            .mostrarNombreDeMes(Integer.parseInt(mesComparar)));

            parametros.put("PR_NOMBRE_MESTRABAJO", ejbSysmanUtil
                            .mostrarNombreDeMes(Integer.parseInt(mesTrabajo)));

            parametros.put("PR_NUMERODIAS_MESCOMPARAR",
                            SysmanFunciones.convertirAFechaCadena(
                                            SysmanFunciones.ultimoDiaDate(
                                                            SysmanFunciones.convertirAFecha(
                                                                            "01/"
                                                                                + mesComparar
                                                                                + "/"
                                                                                + anioComparar))));

            parametros.put("PR_NUMERODIAS_MESTRABAJO",
                            SysmanFunciones.convertirAFechaCadena(
                                            SysmanFunciones.ultimoDiaDate(
                                                            SysmanFunciones.convertirAFecha(
                                                                            "01/"
                                                                                +
                                                                                mesTrabajo
                                                                                + "/"
                                                                                + anioTrabajo))));

          //7707064 _CONTABILIDAD (MROSERO)
            parametros.put("PR_FIRMA_CONTABLE_1",
                            obtenerParametro("FIRMA CONTABLE 1", "NO"));            
            parametros.put("PR_FIRMA_CONTABLE_2",
                            obtenerParametro("FIRMA CONTABLE 2", "NO"));            
            parametros.put("PR_FIRMA_CONTABLE_3",
                    obtenerParametro("FIRMA CONTABLE 3", "NO"));            
            parametros.put("PR_CARGO_CONTABLE_1",
                            obtenerParametro("CARGO CONTABLE 1", "NO"));            
            parametros.put("PR_CARGO_CONTABLE_2",
                            obtenerParametro("CARGO CONTABLE 2", "NO"));            
            parametros.put("PR_CARGO_CONTABLE_3",
                    obtenerParametro("CARGO CONTABLE 3", "NO"));            
            parametros.put("PR_DOCUMENTO_CONTABLE_1",
                            obtenerParametro("DOCUMENTO CONTABLE 1", "NO"));            
            parametros.put("PR_DOCUMENTO_CONTABLE_2",
                            obtenerParametro("DOCUMENTO CONTABLE 2", "NO"));            
            parametros.put("PR_DOCUMENTO_CONTABLE_3",
                    obtenerParametro("DOCUMENTO CONTABLE 3", "NO"));
//7707064 _CONTABILIDAD (MROSERO)
            
            parametros.put("PR_FIRMA_CONTABLE_R",
                    obtenerParametro("FIRMA CONTABLE REPRESENTANTE", "NO"));
            parametros.put("PR_CARGO_CONTABLE_R",
                    obtenerParametro("CARGO CONTABLE REPRESENTANTE", "NO"));  
            parametros.put("PR_DOCUMENTO_CONTABLE_R",
                    obtenerParametro("DOCUMENTO CONTABLE REPRESENTANTE", "NO"));
            
            parametros.put("PR_FORMATO_ESPECIAL_EXCEL", reporteExcel);
            
            if(situacionFinanciera&&situaFinancieraMayorizada)            	
            {
            	parametros.put("PR_INCLUYE_VARIACION", incluyeVariaciones);
            }

            Reporteador.resuelveConsulta(reporte,
                            Integer.parseInt(SessionUtil.getModulo()),
                            reemplazar, parametros);

            archivoDescarga = JsfUtil.exportarStreamed(reporte, parametros,
                            ConectorPool.ESQUEMA_SYSMAN, formato);
            
            reporteExcel = false;
           
        }
        catch (NumberFormatException | SystemException | JRException
                        | IOException | SysmanException | ParseException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

	private String nombreReporte() {
		String nombreReporte = null;

		try {
			/*if (incluyeVariaciones) {
				JsfUtil.agregarMensajeInformativo("Incluye Variaciones esta funcionando");
			}*/
			if (situacionFinanciera || cambiosPatrimonio) {

				ejbContabilidadCuatro.actualizar3110(compania, Integer.parseInt(anioTrabajo),
						Integer.parseInt(anioComparar), Integer.parseInt(mesTrabajo), Integer.parseInt(mesComparar),
						true, usuario);

				if (cambiosPatrimonio) 
				{
					if(incluyeVariaciones) {
                        nombreReporte = "002429EstadoCambiosPatrimonio_Variacion";
                    }else {
                        nombreReporte = "002088EstadoCambiosPatrimonio";
                    }
				} 
				else if (situaFinancieraMayorizada)
				{
					nombreReporte = "002544SITUACIONFINANCIERAMAYORIZADA";
				}
				else if (incluyeVariaciones) 
					{
						nombreReporte = "002430EstadoSituacionFinanciera_Variacion";
					}				
				else
				{
					nombreReporte = "002086EstadoSituacionFinanciera";
				}
			} else if (resultadosIndividuales) {
				if (incluyeVariaciones) {
					nombreReporte = "002427RESULTADOSINDIVIDUAL_VARIACION";

				} else {
					nombreReporte = "002087ResultadosIndividual";
				}

			} else if (flujoEfectivo && flujoEfectivoAcumulado) {
				nombreReporte = "002436EstadoFlujoEfectivoAcumulado";
			} else {
				nombreReporte = SysmanFunciones
                        .nvl(ejbSysmanUtil.consultarParametro(compania,
                                "INFORME FLUJO DE EFECTIVO",
                                modulo, new Date(),
                                false), "002090EstadoFlujoEfectivo").toString();
			}
		}

		catch (NumberFormatException | SystemException e) {

			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}
		return nombreReporte;
	}

    /**
     * Obtiene el valor almacenado en la base de datos para el
     * parametro ingresado.
     *
     * @param nombreParametro
     * Nombre del parametro a consultar en la base de datos.
     * @param valorDefault
     * Valor por omision en caso de nulo.
     * @return valor asignado al parametro
     */
    private String obtenerParametro(String nombreParametro,
        String valorDefault) {
        String parametro = null;
        try {
            parametro = ejbSysmanUtil.consultarParametro(compania,
                            nombreParametro, SessionUtil.getModulo(),
                            new Date(),
                            true);
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
        return parametro != null ? parametro : valorDefault;
    }

    // </METODOS_BOTONES>
    // <METODOS_CAMBIAR>
    /**
     * Metodo ejecutado al cambiar el control AnoTrabajo
     * 
     * 
     */
    public void cambiarAnoTrabajo() {
        // <CODIGO_DESARROLLADO>
        mesTrabajo = null;
        cargarListaMesTrabajo();
        codigoInicial = null;
        cargarListaCodigoInicial();
        codigoFinal = null;
        cargarListaCodigoFinal();

        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado al cambiar el control AnoComparar
     * 
     * 
     */
    public void cambiarAnoComparar() {
        // <CODIGO_DESARROLLADO>
        mesComparar = null;
        cargarListaMesComparar();
        codigoInicial = null;
        cargarListaCodigoInicial();
        codigoFinal = null;
        cargarListaCodigoFinal();
        // </CODIGO_DESARROLLADO>
    }
    /**
     * Metodo ejecutado al cambiar el control Digitos
     * 
     */
    public void cambiarDigitos() {
         //<CODIGO_DESARROLLADO>
    	if(!(digitos.equals("2")||digitos.equals("4")||digitos.equals("6")))
    	{
    		JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB4398"));
    		digitos = null;
    	}
        //</CODIGO_DESARROLLADO>
    }
    /**
     * Metodo ejecutado al cambiar el control SituacionFinanciera
     * 
     */
    public void cambiarSituacionFinanciera() {
        // <CODIGO_DESARROLLADO>
    	situaFinancieraMayorizada = false;
    	resultadosIndividuales = false;
        cambiosPatrimonio = false;
        flujoEfectivo = false;
        flujoEfectivoAcumulado = false;
        // </CODIGO_DESARROLLADO>
    }
    
    /**
     * Metodo ejecutado al cambiar el control Situacion Financiera Mayorizada
     * 
     */
    public void cambiarSituaFinancieraMayorizada() {
        // <CODIGO_DESARROLLADO>
    	situacionFinanciera = true;
    	resultadosIndividuales = false;
        cambiosPatrimonio = false;
        flujoEfectivo = false;
        flujoEfectivoAcumulado = false;
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado al cambiar el control ResultadosIndividuales
     * 
     */
    public void cambiarResultadosIndividuales() {
        // <CODIGO_DESARROLLADO>
        situacionFinanciera = false;
        situaFinancieraMayorizada = false;
        cambiosPatrimonio = false;
        flujoEfectivo = false;
        flujoEfectivoAcumulado = false;
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado al cambiar el control CambiosPatrimonio
     * 
     */
    public void cambiarCambiosPatrimonio() {
        // <CODIGO_DESARROLLADO>
        situacionFinanciera = false;
        situaFinancieraMayorizada = false;
        resultadosIndividuales = false;
        flujoEfectivo = false;
        flujoEfectivoAcumulado = false;
        // </CODIGO_DESARROLLADO>
    }
    
    /**
	 * Metodo ejecutado al cambiar el control FlujoEfectivo
	 * 
	 */
	public void cambiarFlujoEfectivo() {
		// <CODIGO_DESARROLLADO>
		situacionFinanciera = false;
		situaFinancieraMayorizada = false;
		resultadosIndividuales = false;
		cambiosPatrimonio = false;
		flujoEfectivoAcumulado = false;
		// </CODIGO_DESARROLLADO>
	}
	
	  /**
		 * Metodo ejecutado al cambiar el control FlujoEfectivo
		 * 
		 */
		public void cambiarFlujoEfectivoAcumulado() {
			// <CODIGO_DESARROLLADO>
			situacionFinanciera = false;
			situaFinancieraMayorizada = false;
			resultadosIndividuales = false;
			cambiosPatrimonio = false;
			flujoEfectivo = true;
			// </CODIGO_DESARROLLADO>
		}


    /**
     * Metodo ejecutado al cambiar el control FlujoEfectivo
     * 
     */
   

    // </METODOS_CAMBIAR>
    // <METODOS_COMBOS_GRANDES>
    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaCodigoInicial
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaCodigoInicial(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        codigoInicial = SysmanFunciones
                        .nvl(registroAux.getCampos()
                                        .get(GeneralParameterEnum.CODIGO
                                                        .getName()),
                                        "")
                        .toString();
        codigoFinal = null;
        cargarListaCodigoFinal();
    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaCodigoFinal
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaCodigoFinal(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        codigoFinal = SysmanFunciones
                        .nvl(registroAux.getCampos()
                                        .get(GeneralParameterEnum.CODIGO
                                                        .getName()),
                                        "")
                        .toString();
    }

    // </METODOS_COMBOS_GRANDES>
    // <METODOS_ARBOL>
    // </METODOS_ARBOL>
    // <SET_GET_ATRIBUTOS>

    /**
     * Retorna la variable codigoInicial
     * 
     * @return codigoInicial
     */
    public String getCodigoInicial() {
        return codigoInicial;
    }

    public boolean isSituacionFinanciera() {
        return situacionFinanciera;
    }

    public void setSituacionFinanciera(boolean situacionFinanciera) {
        this.situacionFinanciera = situacionFinanciera;
    }

    public boolean isResultadosIndividuales() {
        return resultadosIndividuales;
    }

    public void setResultadosIndividuales(boolean resultadosIndividuales) {
        this.resultadosIndividuales = resultadosIndividuales;
    }

    public boolean isCambiosPatrimonio() {
        return cambiosPatrimonio;
    }

    public void setCambiosPatrimonio(boolean cambiosPatrimonio) {
        this.cambiosPatrimonio = cambiosPatrimonio;
    }

    public boolean isFlujoEfectivo() {
        return flujoEfectivo;
    }

    public void setFlujoEfectivo(boolean flujoEfectivo) {
        this.flujoEfectivo = flujoEfectivo;
    }

    /**
     * Asigna la variable codigoInicial
     * 
     * @param codigoInicial
     * Variable a asignar en codigoInicial
     */
    public void setCodigoInicial(String codigoInicial) {
        this.codigoInicial = codigoInicial;
    }

    /**
     * Retorna la variable codigoFinal
     * 
     * @return codigoFinal
     */
    public String getCodigoFinal() {
        return codigoFinal;
    }

    /**
     * Asigna la variable codigoFinal
     * 
     * @param codigoFinal
     * Variable a asignar en codigoFinal
     */
    public void setCodigoFinal(String codigoFinal) {
        this.codigoFinal = codigoFinal;
    }

    /**
     * Retorna la variable mesTrabajo
     * 
     * @return mesTrabajo
     */
    public String getMesTrabajo() {
        return mesTrabajo;
    }

    /**
     * Asigna la variable mesTrabajo
     * 
     * @param mesTrabajo
     * Variable a asignar en mesTrabajo
     */
    public void setMesTrabajo(String mesTrabajo) {
        this.mesTrabajo = mesTrabajo;
    }

    /**
     * Retorna la variable mesComparar
     * 
     * @return mesComparar
     */
    public String getMesComparar() {
        return mesComparar;
    }

    /**
     * Asigna la variable mesComparar
     * 
     * @param mesComparar
     * Variable a asignar en mesComparar
     */
    public void setMesComparar(String mesComparar) {
        this.mesComparar = mesComparar;
    }

    /**
     * Retorna la variable anioTrabajo
     * 
     * @return anioTrabajo
     */
    public String getAnioTrabajo() {
        return anioTrabajo;
    }

    /**
     * Asigna la variable anioTrabajo
     * 
     * @param anioTrabajo
     * Variable a asignar en anioTrabajo
     */
    public void setAnioTrabajo(String anioTrabajo) {
        this.anioTrabajo = anioTrabajo;
    }

    /**
     * Retorna la variable anioComparar
     * 
     * @return anioComparar
     */
    public String getAnioComparar() {
        return anioComparar;
    }

    /**
     * Asigna la variable anioComparar
     * 
     * @param anioComparar
     * Variable a asignar en anioComparar
     */
    public void setAnioComparar(String anioComparar) {
        this.anioComparar = anioComparar;
    }

    /**
     * Retorna la variable digitos
     * 
     * @return digitos
     */
    public String getDigitos() {
        return digitos;
    }

    /**
     * Asigna la variable digitos
     * 
     * @param digitos
     * Variable a asignar en digitos
     */
    public void setDigitos(String digitos) {
        this.digitos = digitos;
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
    /**
     * Retorna la lista listaMesTrabajo
     * 
     * @return listaMesTrabajo
     */
    public List<Registro> getListaMesTrabajo() {
        return listaMesTrabajo;
    }

    /**
     * Asigna la lista listaMesTrabajo
     * 
     * @param listaMesTrabajo
     * Variable a asignar en listaMesTrabajo
     */
    public void setListaMesTrabajo(List<Registro> listaMesTrabajo) {
        this.listaMesTrabajo = listaMesTrabajo;
    }

    /**
     * Retorna la lista listaMesComparar
     * 
     * @return listaMesComparar
     */
    public List<Registro> getListaMesComparar() {
        return listaMesComparar;
    }

    /**
     * Asigna la lista listaMesComparar
     * 
     * @param listaMesComparar
     * Variable a asignar en listaMesComparar
     */
    public void setListaMesComparar(List<Registro> listaMesComparar) {
        this.listaMesComparar = listaMesComparar;
    }

    /**
     * Retorna la lista listaAnoTrabajo
     * 
     * @return listaAnoTrabajo
     */
    public List<Registro> getListaAnoTrabajo() {
        return listaAnoTrabajo;
    }

    /**
     * Asigna la lista listaAnoTrabajo
     * 
     * @param listaAnoTrabajo
     * Variable a asignar en listaAnoTrabajo
     */
    public void setListaAnoTrabajo(List<Registro> listaAnoTrabajo) {
        this.listaAnoTrabajo = listaAnoTrabajo;
    }

    /**
     * Retorna la lista listaAnoComparar
     * 
     * @return listaAnoComparar
     */
    public List<Registro> getListaAnoComparar() {
        return listaAnoComparar;
    }

    /**
     * Asigna la lista listaAnoComparar
     * 
     * @param listaAnoComparar
     * Variable a asignar en listaAnoComparar
     */
    public void setListaAnoComparar(List<Registro> listaAnoComparar) {
        this.listaAnoComparar = listaAnoComparar;
    }

    // </SET_GET_LISTAS>
    // <SET_GET_LISTAS_COMBO_GRANDE>
    /**
     * Retorna la lista listaCodigoInicial
     * 
     * @return listaCodigoInicial
     */
    public RegistroDataModelImpl getListaCodigoInicial() {
        return listaCodigoInicial;
    }

    /**
     * Asigna la lista listaCodigoInicial
     * 
     * @param listaCodigoInicial
     * Variable a asignar en listaCodigoInicial
     */
    public void setListaCodigoInicial(
        RegistroDataModelImpl listaCodigoInicial) {
        this.listaCodigoInicial = listaCodigoInicial;
    }

    /**
     * Retorna la lista listaCodigoFinal
     * 
     * @return listaCodigoFinal
     */
    public RegistroDataModelImpl getListaCodigoFinal() {
        return listaCodigoFinal;
    }

    /**
     * Asigna la lista listaCodigoFinal
     * 
     * @param listaCodigoFinal
     * Variable a asignar en listaCodigoFinal
     */
    public void setListaCodigoFinal(RegistroDataModelImpl listaCodigoFinal) {
        this.listaCodigoFinal = listaCodigoFinal;
    }
    // </SET_GET_LISTAS_COMBO_GRANDE>

	/**
	 * @return the incluyeVariaciones
	 */
	public boolean isIncluyeVariaciones() {
		return incluyeVariaciones;
	}

	/**
	 * @param incluyeVariaciones the incluyeVariaciones to set
	 */
	public void setIncluyeVariaciones(boolean incluyeVariaciones) {
		this.incluyeVariaciones = incluyeVariaciones;
	}
	
	/**
	 * @return the flujoEfectivoAcumulado
	 */
	public boolean isFlujoEfectivoAcumulado() {
		return flujoEfectivoAcumulado;
	}

	/**
	 * @param flujoEfectivoAcumulado the flujoEfectivoAcumulado to set
	 */
	public void setFlujoEfectivoAcumulado(boolean flujoEfectivoAcumulado) {
		this.flujoEfectivoAcumulado = flujoEfectivoAcumulado;
	}

	/**
	 * @return the situaFinancieraMayorizada
	 */
	public boolean isSituaFinancieraMayorizada() {
		return situaFinancieraMayorizada;
	}

	/**
	 * @param situaFinancieraMayorizada the situaFinancieraMayorizada to set
	 */
	public void setSituaFinancieraMayorizada(boolean situaFinancieraMayorizada) {
		this.situaFinancieraMayorizada = situaFinancieraMayorizada;
	}
}
