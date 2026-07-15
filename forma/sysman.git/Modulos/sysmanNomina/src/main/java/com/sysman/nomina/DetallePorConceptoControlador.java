package com.sysman.nomina;

import com.sysman.beanbase.BeanBaseModal;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.ReportesBean.FORMATOS;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.nomina.enums.DetallePorConceptoControladorEnum;
import com.sysman.nomina.enums.DetallePorConceptoControladorUrlEnum;
import com.sysman.persistencia.ConectorPool;
import com.sysman.recursos.ejb.EjbSysmanUtilRemote;
import com.sysman.reportes.Reporteador;
import com.sysman.services.FormContinuoService;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.sesion.SessionBean;
import com.sysman.util.SysmanConstantes;
import com.sysman.util.SysmanFunciones;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.event.SelectEvent;
import org.primefaces.model.StreamedContent;

import net.sf.jasperreports.engine.JRException;

/**
 *
 * @author esarmiento
 * @version 1, 21/07/2015
 *
 * @author spina
 * @version 2, 06/09/2017 - se refactoriza para dss, depuracion sonar
 * y ejbs
 * 
* @author obarragan
* @version 3, 10/06/2019 - Se agrego opcion de imprimir header con imagenes adicionales. 
 */
@ManagedBean
@ViewScoped
public class DetallePorConceptoControlador extends BeanBaseModal {

    private final String compania;
    private final String proceso;
    private final String anio;
    private final String mes;
    private final String periodo;
    private final String nombrePeriodo;
    private final String modulo;
    private String concepto;
    private String conceptoSel;
    private String tipoEmpleadoInicial;
    private String observacion;
    private boolean todos;
    private String tipoEmpleado;

    private RegistroDataModelImpl listaTipoEmpleadoInicial;
    private RegistroDataModelImpl listaConcepto1;
    private StreamedContent archivoDescarga;
    
    private String headerEspecial;    

    @EJB
    private EjbSysmanUtilRemote ejbSysmanUtil;

    /**
     * Creates a new instance of DetallePorConceptoControlador
     */
    public DetallePorConceptoControlador() {
        super();
        compania = SessionUtil.getCompania();
        modulo = SessionUtil.getModulo();
        proceso = SysmanFunciones
                        .nvl(SessionUtil.getSessionVar("procesoNomina"), "")
                        .toString();
        anio = SysmanFunciones.nvl(SessionUtil.getSessionVar("anioNomina"), "")
                        .toString();
        mes = SysmanFunciones.nvl(SessionUtil.getSessionVar("mesNomina"), "")
                        .toString();
        periodo = SysmanFunciones
                        .nvl(SessionUtil.getSessionVar("periodoNomina"), "")
                        .toString();
        nombrePeriodo = SysmanFunciones.nvl(SessionUtil
                        .getSessionVar("nombrePeriodoNomina"), "").toString();
        try {
            numFormulario = GeneralCodigoFormaEnum.DETALLE_POR_CONCEPTO_CONTROLADOR
                            .getCodigo();
            validarPermisos();
        }
        catch (Exception ex) {
            Logger.getLogger(DetallePorConceptoControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            SessionUtil.redireccionarMenuPermisos();
        }
    }

    @PostConstruct
    public void inicializar() {
        cargarListaTipoEmpleadoInicial();
        cargarListaConcepto1();
        abrirFormulario();
    }

    public void cargarListaTipoEmpleadoInicial() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        DetallePorConceptoControladorUrlEnum.URL2160
                                                        .getValue());

        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        listaTipoEmpleadoInicial = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param, true,
                        DetallePorConceptoControladorEnum.ID_DE_TIPO
                                        .getValue());
    }

    public void cargarListaConcepto1() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        DetallePorConceptoControladorUrlEnum.URL2161
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        listaConcepto1 = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param, true,
                        DetallePorConceptoControladorEnum.ID_DE_CONCEPTO
                                        .getValue());
    }

    public void oprimirComando10() {
        archivoDescarga = null;
        generarInforme(FORMATOS.EXCEL97);
    }

    private boolean validarCampos() {
        if (SysmanFunciones.validarVariableVacio(concepto)) {
            JsfUtil.agregarMensajeInformativo(idioma.getString("TB_TB1885"));
            return true;
        }
        if (!todos && SysmanFunciones.validarVariableVacio(tipoEmpleado)) {
            JsfUtil.agregarMensajeInformativo(
                            idioma.getString("TB_TB2522"));
            return true;
        }
        return false;
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
                            new Date(), true);
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
        return parametro != null ? parametro : valorDefault;
    }

    private void generarInforme(FORMATOS formato) {
        if (validarCampos()) {
            return;
        }
        try {

            headerEspecial = ejbSysmanUtil.consultarParametro(compania,
                    "FORMATOS ESPECIALES BUCARAMANGA", modulo,
                    new Date(),
                    true);  
            

            String nombreReporte = "";
            
            if (conceptoSel.equals("125")) {
            	nombreReporte = SysmanFunciones.nvlStr(ejbSysmanUtil.consultarParametro(compania,
                        "FORMATO QUINCENAL CONCEPTO 125", modulo, new Date(), false),
						"000078Detallequincenalporconcepto");
			}else
				nombreReporte = SysmanFunciones.nvlStr(ejbSysmanUtil.consultarParametro(compania,
                        "FORMATO DETALLE QUINCENAL CONCEPTO", modulo, new Date(), false),
						"000078Detallequincenalporconcepto");
            
            String sticker = SessionUtil.getCompaniaIngreso().getRutaSticker();
            
            // MANEJO DE PARAMETROS DEL REPORTE
            HashMap<String, Object> reemplazar = new HashMap<>();

            reemplazar.put("proceso", proceso);
            reemplazar.put("anio", anio);
            reemplazar.put("mes", mes);
            reemplazar.put("periodo", periodo);
            reemplazar.put("concepto", conceptoSel);
            reemplazar.put("filtro", todos ? 1 : 0);
            reemplazar.put("tipoEmpleado", tipoEmpleado == null ? "" : tipoEmpleado);
        	
            Map<String, Object> parametros = new HashMap<>();

            String nombreJefe = ejbSysmanUtil.consultarParametro(compania,
                            "NOMBRE DEL JEFE DE RECURSOS HUMANOS", modulo,new Date(), true);
            String cargo = ejbSysmanUtil.consultarParametro(compania,
                            "CARGO JEFE RECURSOS HUMANOS", modulo, new Date(),true);
            
            if ("002874DetallePorConcepto".equals(nombreReporte)) {
            	 cargo = ejbSysmanUtil.consultarParametro(compania,
                        "CARGO DEL JEFE DE RECURSOS HUMANOS", modulo, new Date(),true);
            }
            
			
			parametros.put("PR_NOMBRE_JEFE_DESARROLLO_HUMANO", obtenerParametro("NOMBRE JEFE DESARROLLO HUMANO",""));
	        parametros.put("PR_CARGO_JEFE_DESARROLLO_HUMANO", obtenerParametro("CARGO JEFE DESARROLLO HUMANO",""));
	        parametros.put("PR_NOMBRE_JEFE_NOMINA", obtenerParametro("NOMBRE JEFE NOMINA",""));
	        parametros.put("PR_CARGO_RESPONSABLE_DE_NOMINA", obtenerParametro("CARGO RESPONSABLE DE NOMINA",""));
			
            parametros.put("PR_FORMS_DETALLE_POR_CONCEPTO_OBS",observacion);
            parametros.put("PR_NOMBREEMPRESA", SessionUtil.getCompaniaIngreso().getNombre());
            parametros.put("PR_FORMS_DETALLE_POR_CONCEPTO_TEXTOTIPO", tipoEmpleadoInicial.toUpperCase());
            parametros.put("PR_TEXTO_TITULO", idioma.getString("TB_TB3521")
                            .replace("s$nombrePeriodo$s",
                                            nombrePeriodo.toUpperCase())
                            .replace("s$mes$s", String.valueOf(
                                            SysmanConstantes.NOMBRE_MESES_CONTABILIDAD[Integer
                                                            .parseInt(mes)])
                                            .toUpperCase())
                            .replace("s$ano$s", anio));
            parametros.put("PR_CARGO_JEFE_RECURSOS_HUMANOS", cargo);            
            parametros.put("PR_NOMBRE_DEL_JEFE_DE_RECURSOS_HUMANOS", nombreJefe);
            parametros.put("PR_NOMBRE_DEL_GERENTE", obtenerParametro("NOMBRE DEL GERENTE",""));
            parametros.put("PR_CARGO_DEL_GERENTE", obtenerParametro("CARGO DEL GERENTE",""));
            parametros.put("PR_NOMBRE_DEL_CARGO_TESORERO_PAGADOR", obtenerParametro("NOMBRE DEL CARGO TESORERO PAGADOR", ""));
            parametros.put("PR_CARGO_DEL_TESORERO_PAGADOR", obtenerParametro("CARGO DEL TESORERO PAGADOR",""));
            parametros.put("PR_NOMBRE_DE_QUIEN_AUTORIZA_NOMINA", obtenerParametro("NOMBRE DE QUIEN AUTORIZA NOMINA",""));
            parametros.put("PR_CARGO_DE_QUIEN_AUTORIZA_NOMINA", obtenerParametro("CARGO DE QUIEN AUTORIZA NOMINA",""));
            parametros.put("PR_HEADER_ESPECIAL", headerEspecial.equals("SI")?true:false);
            parametros.put("PR_IMAGEN_ESPECIAL", sticker);       
            parametros.put("PR_NOMBRE_DE QUIEN_LIQUIDA_NOMINA", obtenerParametro("NOMBRE DE QUIEN LIQUIDA NOMINA",""));
            parametros.put("PR_CARGO_DE_QUIEN_LIQUIDA_NOMINA", obtenerParametro("CARGO DE QUIEN LIQUIDA NOMINA",""));
            parametros.put("PR_NOMBRE_DE_QUIEN_REVISA_NOMINA", obtenerParametro("NOMBRE DE QUIEN REVISA NOMINA",""));
            parametros.put("PR_CARGO_DE_QUIEN_REVISA_NOMINA", obtenerParametro("CARGO DE QUIEN REVISA NOMINA",""));
            parametros.put("PR_ELABORADO_POR", obtenerParametro("ELABORADO POR",""));
            
            // IMPLEMETACION PARAMETROS MARCA_BLANCA - ljdiaz (Luis Jacobo Diaz mu�oz)
            parametros.put("PR_EMPRESAPARAMETRIZADA", SessionBean.getImpresoPorEmpresaParamterizada());
            // FIN IMPLEMENTACION MARCA_BLANCA
            
            //7750292 - ljdiaz - cuarta firma
            String mostrarCuartaFirma = SysmanFunciones.nvlStr(ejbSysmanUtil.consultarParametro(compania, "ACTIVAR CUARTA FIRMA", modulo,
						new Date(), false), "NO");
			if("SI".equals(mostrarCuartaFirma)) {
				String nombreCuartaFirma = SysmanFunciones.nvlStr(ejbSysmanUtil.consultarParametro(compania, "NOMBRE CUARTA FIRMA", modulo,
						new Date(), false), "NO");
				String cargoCuartaFirma = SysmanFunciones.nvlStr(ejbSysmanUtil.consultarParametro(compania, "CARGO CUARTA FIRMA", modulo,
						new Date(), false), "NO");
				
				parametros.put("PR_MOSTRAR_CUARTA_FIRMA", mostrarCuartaFirma);
				parametros.put("PR_NOMBRE_CUARTA_FIRMA", nombreCuartaFirma);
				parametros.put("PR_CARGO_CUARTA_FIRMA", cargoCuartaFirma);
			}
            

            
            Reporteador.resuelveConsulta(nombreReporte,
                    Integer.parseInt(modulo), reemplazar, parametros);
            
            archivoDescarga = JsfUtil.exportarStreamed(nombreReporte,
                            parametros, ConectorPool.ESQUEMA_SYSMAN, formato);
        }
        catch (JRException | IOException | SysmanException
                        | SystemException ex) {
            JsfUtil.agregarMensajeError(ex.getMessage());
            Logger.getLogger(DetallePorConceptoControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
        }

    }

    public void oprimirPreliminarBancos() {
        // <CODIGO_DESARROLLADO>
        archivoDescarga = null;
        generarInforme(FORMATOS.PDF);
        // </CODIGO_DESARROLLADO>
    }

    public void seleccionarFilaConcepto1(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        concepto = SysmanFunciones.nvl(registroAux.getCampos()
                        .get(DetallePorConceptoControladorEnum.NOMBRE_CONCEPTO
                                        .getValue()),
                        "").toString();
        conceptoSel = SysmanFunciones.nvl(registroAux.getCampos()
                        .get(DetallePorConceptoControladorEnum.ID_DE_CONCEPTO
                                        .getValue()),
                        "").toString();

    }

    public void cambiarTodos() {
        // <CODIGO_DESARROLLADO>
        if (todos) {
            setTipoEmpleadoInicial(null);
            setTipoEmpleado(null);
        }
        // </CODIGO_DESARROLLADO>
    }

    public void seleccionarFilaTipoEmpleadoInicial(SelectEvent event) {
        if (todos) {
            setTodos(false);
        }
        Registro registroAux = (Registro) event.getObject();
        tipoEmpleadoInicial = SysmanFunciones.nvl(registroAux.getCampos()
                        .get(DetallePorConceptoControladorEnum.NOMBRE_TIPO
                                        .getValue()),
                        "").toString();
        tipoEmpleado = SysmanFunciones.nvl(registroAux.getCampos()
                        .get(DetallePorConceptoControladorEnum.ID_DE_TIPO
                                        .getValue()),
                        "").toString();

    }

    @Override
    public FormContinuoService getService() {
        return service;
    }

    @Override
    public void setService(FormContinuoService service) {
        this.service = service;
    }

    public String getConcepto() {
        return concepto;
    }

    public void setConcepto(String concepto) {
        this.concepto = concepto;
    }

    public String getTipoEmpleadoInicial() {
        return tipoEmpleadoInicial;
    }

    public void setTipoEmpleadoInicial(String tipoEmpleadoInicial) {
        this.tipoEmpleadoInicial = tipoEmpleadoInicial;
    }

    public RegistroDataModelImpl getListaTipoEmpleadoInicial() {
        return listaTipoEmpleadoInicial;
    }

    public void setListaTipoEmpleadoInicial(
        RegistroDataModelImpl listaTipoEmpleadoInicial) {
        this.listaTipoEmpleadoInicial = listaTipoEmpleadoInicial;
    }

    public boolean isTodos() {
        return todos;
    }

    public void setTodos(boolean todos) {
        this.todos = todos;
    }

    public RegistroDataModelImpl getListaConcepto1() {
        return listaConcepto1;
    }

    public void setListaConcepto1(RegistroDataModelImpl listaConcepto1) {
        this.listaConcepto1 = listaConcepto1;
    }

    public String getObservacion() {
        return observacion;
    }

    public void setObservacion(String observacion) {
        this.observacion = observacion;
    }

    public String getNombreConcepto() {
        return tipoEmpleado;
    }

    public void setNombreConcepto(String nombreConcepto) {
        this.tipoEmpleado = nombreConcepto;
    }

    public String getConceptoSel() {
        return conceptoSel;
    }

    public void setConceptoSel(String conceptoSel) {
        this.conceptoSel = conceptoSel;
    }

    public String getTipoEmpleado() {
        return tipoEmpleado;
    }

    public void setTipoEmpleado(String tipoEmpleado) {
        this.tipoEmpleado = tipoEmpleado;
    }

    @Override
    public void abrirFormulario() {
        todos = true;
    }

    public StreamedContent getArchivoDescarga() {
        return archivoDescarga;
    }

    public void setArchivoDescarga(StreamedContent archivoDescarga) {
        this.archivoDescarga = archivoDescarga;
    }

	public String getHeaderEspecial() {
		return headerEspecial;
	}

	public void setHeaderEspecial(String headerEspecial) {
		this.headerEspecial = headerEspecial;
	}

}
