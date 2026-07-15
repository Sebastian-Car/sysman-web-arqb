package com.sysman.nomina;

import com.sysman.beanbase.BeanBaseModal;
import com.sysman.controladores.SessionUtil;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.ReportesBean;
import com.sysman.nomina.ejb.EjbNominaCeroRemote;
import com.sysman.persistencia.ConectorPool;
import com.sysman.recursos.ejb.EjbSysmanUtilRemote;
import com.sysman.reportes.Reporteador;
import com.sysman.util.SysmanFunciones;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.model.StreamedContent;

import net.sf.jasperreports.engine.JRException;

/**
 *
 * @author jrodriguezr
 * @version 1, 31/08/2015
 * 
 * @author eamaya
 * @version 2.0, 19/10/2017, Cambio de numero de formulario por enum y
 * Manejo de EJBs, correccion de llamados a parametros
 */
@ManagedBean
@ViewScoped
public class PlanillaDotacionControlador extends BeanBaseModal {
    private final String compania;
    private final String proceso;
    private final String anio;
    private final String mes;
    private final String periodo;
    private String moduloNomina;

    /** Variable que almacena el valor del parametro SALARIO MINIMO */
    private String salarioMinimo;

    private String nombreCompania;

    private String nombreJefeRH;

    private String cargoJefeRH;

    private String date;

    private String nombreMes;

    private StreamedContent archivoDescarga;
    private static final String CTEPRNOMEMPRESA = "PR_NOMBREEMPRESA";
    private static final String CTEPRNOMJEFERH = "PR_NOMBRE_JEFE_RECURSOS_HUMANOS";
    private static final String CTEPRCARGOJEFERH = "PR_CARGO_JEFE_RECURSOS_HUMANOS";
    
    private String encabezado;
    private String titulo;
    private final String menuActual;

    @EJB
    private EjbSysmanUtilRemote ejbSysmanUtl;

    @EJB
    private EjbNominaCeroRemote ejbNominaCero;

    /**
     * Creates a new instance of PlanillaDotacionControlador
     */
    public PlanillaDotacionControlador() {
        super();
        numFormulario = GeneralCodigoFormaEnum.PLANILLA_DOTACION_CONTROLADOR
                        .getCodigo();
        compania = SessionUtil.getCompania();
        moduloNomina = SessionUtil.getModulo();
        proceso = (String) SessionUtil.getSessionVar("procesoNomina");
        anio = (String) SessionUtil.getSessionVar("anioNomina");
        mes = (String) SessionUtil.getSessionVar("mesNomina");
        periodo = (String) SessionUtil.getSessionVar("periodoNomina");
        nombreCompania = SessionUtil.getCompaniaIngreso()
                        .getNombre();
        menuActual = SessionUtil.getMenuActual();
        
        try {
            validarPermisos();
        }
        catch (Exception ex) {
            Logger.getLogger(PlanillaDotacionControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            SessionUtil.redireccionarMenuPermisos();
        }
    }

    @PostConstruct
    public void inicializar() {
        abrirFormulario();
        
        if (("6030218").equals(menuActual)) {
        	encabezado = "Planilla de Dotación";
        	titulo = "PLANILLA DE DOTACIÓN";
        } else {
        	encabezado = "Listado Básico Personal";
        	titulo = "LISTADO BÁSICO PERSONAL";
        }
    }

    @Override
    public void abrirFormulario() {
        // <CODIGO_DESARROLLADO>
        try {
            salarioMinimo = ejbSysmanUtl.consultarParametro(compania,
                            "SALARIO MINIMO",
                            moduloNomina, new Date(), false);

            nombreJefeRH = ejbSysmanUtl.consultarParametro(compania,
                            "NOMBRE JEFE RECURSOS HUMANOS", moduloNomina,
                            new Date(), false);

            cargoJefeRH = ejbSysmanUtl.consultarParametro(compania,
                            "CARGO JEFE RECURSOS HUMANOS", moduloNomina,
                            new Date(), false);

            Date dateFin = ejbNominaCero.getFechaPeriodoIniFin(compania,
                            Integer.parseInt(proceso), Integer.parseInt(anio),
                            Integer.parseInt(mes), Integer.parseInt(periodo),
                            false, false);

            date = SysmanFunciones.formatearFecha(dateFin);

            nombreMes = ejbSysmanUtl.mostrarNombreDeMes(Integer.parseInt(mes));

        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
        // </CODIGO_DESARROLLADO>
    }

    public void oprimirPresentar() {
        // <CODIGO_DESARROLLADO>
        archivoDescarga = null;
        
        if (("6030218").equals(menuActual)) {
        	generaInforme(ReportesBean.FORMATOS.PDF);
        } else {
        	generaInformeBasicoPersonal(ReportesBean.FORMATOS.PDF);
        }
        // </CODIGO_DESARROLLADO>
    }

    public void oprimirExcel() {
        // <CODIGO_DESARROLLADO>
        archivoDescarga = null;
        
        if (("6030218").equals(menuActual)) {
        	generaInforme(ReportesBean.FORMATOS.EXCEL97);
        } else {
        	generaInformeBasicoPersonal(ReportesBean.FORMATOS.EXCEL97);
        }
        // </CODIGO_DESARROLLADO>
    }

    private void generaInforme(ReportesBean.FORMATOS formato) {
        try {

            String reporte = "000119PlanilladeDotacion";
            String nombreParametro = "FORMATO PLANILLA DOTACION";
            String valorParametro = ejbSysmanUtl.consultarParametro(compania,
                            "FORMATO PLANILLA DOTACION", moduloNomina,
                            new Date(), false);

            /*
             * String nombreReporte = valorParametro.isEmpty() ?
             * "PLANILLA_DE_DOTACION" : valorParametro;
             */
            HashMap<String, Object> reemplazar = new HashMap<>();
            Map<String, Object> parametros = new HashMap<>();
            parametros.put("nombreParametro", nombreParametro);
            parametros.put("s$companiaInforme$s", nombreCompania);

            if (valorParametro == null
                || valorParametro.equals("PLANILLA_DE_DOTACION")) {
                String mensaje = idioma.getString("TB_TB1743");
                mensaje = mensaje.replace("s$nombreParametro$s",
                                nombreParametro);
                mensaje = mensaje.replace("s$companiaInforme$s",
                                nombreCompania);
                JsfUtil.agregarMensajeAlerta(mensaje);
            }
            else {

                if ("837.000.084-5".equals(SessionUtil.getCompaniaIngreso()
                                .getNit())) {

                    reemplazar.put("anio", anio);
                    reemplazar.put("salarioMinimo", salarioMinimo);
                    reemplazar.put("date", date);
                    parametros.put(CTEPRNOMEMPRESA, nombreCompania);
                    parametros.put(CTEPRNOMJEFERH,
                                    nombreJefeRH);
                    parametros.put(CTEPRCARGOJEFERH,
                                    cargoJefeRH);

                    Reporteador.resuelveConsulta(reporte,
                                    Integer.parseInt(SessionUtil.getModulo()),
                                    reemplazar, parametros);

                    archivoDescarga = JsfUtil.exportarStreamed(
                                    "000207PlanilladeDotacionoper", parametros,
                                    ConectorPool.ESQUEMA_SYSMAN, formato);

                }
                else if (valorParametro.equals(reporte)) {

                    reemplazar.put("anio", anio);
                    reemplazar.put("salarioMinimo", salarioMinimo);

                    reemplazar.put("date", date);

                    Reporteador.resuelveConsulta(reporte,
                                    Integer.parseInt(SessionUtil.getModulo()),
                                    reemplazar, parametros);
                    parametros.put(CTEPRNOMEMPRESA, nombreCompania);
                    parametros.put(CTEPRNOMJEFERH, nombreJefeRH);
                    parametros.put(CTEPRCARGOJEFERH, cargoJefeRH);
                    parametros.put("PR_NOMBRE_MES", nombreMes);
                    parametros.put("PR_ANIO", anio);

                    archivoDescarga = JsfUtil.exportarStreamed(
                                    reporte, parametros,
                                    ConectorPool.ESQUEMA_SYSMAN, formato);
                }
            }

        }

        catch (JRException | IOException | SysmanException
                        | SystemException ex) {
            Logger.getLogger(PlanillaDotacionControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            JsfUtil.agregarMensajeError(ex.getMessage());
        }

    }
    
    private void generaInformeBasicoPersonal(ReportesBean.FORMATOS formato) {
    	try {
            String reporte = "000056ListadoPersonalActivo";
            Map<String, Object> parametros = new HashMap<>();
            Map<String, Object> reemplazar = new HashMap<>();

            reemplazar.put("compania", "'" + compania + "'");
            reemplazar.put("procesoNomina", proceso);
            reemplazar.put("anioNomina", anio);
            reemplazar.put("mesNomina", mes);
            reemplazar.put("periodoNomina", periodo);
            
            parametros.put("PR_NOMBRECOMPANIA", SessionUtil.getCompaniaIngreso().getNombre());
            
            Reporteador.resuelveConsulta(reporte, Integer.parseInt(SessionUtil.getModulo()),
                            			 reemplazar, parametros);

            archivoDescarga = JsfUtil.exportarStreamed(reporte, parametros,
                              ConectorPool.ESQUEMA_SYSMAN,
                              formato);
            
        } catch (JRException | IOException | SysmanException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    public StreamedContent getArchivoDescarga() {
        return archivoDescarga;
    }

    public void setArchivoDescarga(StreamedContent archivoDescarga) {
        this.archivoDescarga = archivoDescarga;
    }
    
    public String getEncabezado() {
        return encabezado;
    }

    public void setEncabezado(String encabezado) {
        this.encabezado = encabezado;
    }
    
    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }
}
