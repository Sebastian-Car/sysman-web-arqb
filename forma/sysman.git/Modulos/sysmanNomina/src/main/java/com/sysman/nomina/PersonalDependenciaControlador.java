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
import com.sysman.nomina.enums.PersonalDependenciaControladorUrlEnum;
import com.sysman.persistencia.ConectorPool;
import com.sysman.recursos.ejb.EjbSysmanUtilRemote;
import com.sysman.reportes.Reporteador;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.util.SysmanFunciones;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.util.Map;
import java.util.TreeMap;

import org.primefaces.event.SelectEvent;
import org.primefaces.model.StreamedContent;

import net.sf.jasperreports.engine.JRException;

/**
 *
 * @author acaceres
 * @version 1, 29/09/2015
 * 
 * @author eamaya
 * @version 2.0, 18/07/2017, Proceso de Refactoring DSS,correcciones
 * SonarQube y cambio de numero de formulario por enum
 * 
 */
@ManagedBean
@ViewScoped
public class PersonalDependenciaControlador extends BeanBaseModal {
    /**
     * variable que almacena la compania
     */
    private final String compania;
    /**
     * variable que almacena la opcion
     */
    private String opcion;
    /**
     * variable que alamcena la dependencia
     */
    private String dependencias;
    /**
     * variable que almacena el nombre de la dependencia
     */
    private String nombreDependencia;
    /**
     * lista las dependencias
     */
    private RegistroDataModelImpl listaDependencia;
    /**
     * Atributo usado para descargar contenidos de archivos desde la
     * vista
     */
    private StreamedContent archivoDescarga;

    @EJB
    private EjbSysmanUtilRemote ejbSysmanUtil;
    
    
    /**
     * Creates a new instance of PersonalDependenciaControlador
     */
    public PersonalDependenciaControlador() {
        super();
        compania = SessionUtil.getCompania();
        try {
            numFormulario = GeneralCodigoFormaEnum.PERSONAL_DEPENDENCIA_CONTROLADOR
                            .getCodigo();
            validarPermisos();
        }
        catch (Exception ex) {
            Logger.getLogger(PersonalDependenciaControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            SessionUtil.redireccionarMenuPermisos();
        }
    }

    @PostConstruct
    public void inicializar() {
        cargarListaDependencia();
        abrirFormulario();

    }

    @Override
    public void abrirFormulario() {

        opcion = "2";

    }

    /**
     * metodo que carga una lista de dependencias
     */
    public void cargarListaDependencia() {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        PersonalDependenciaControladorUrlEnum.URL3227
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);

        listaDependencia = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, "CODIGO");
    }
  
    /**
     * metidi que se llama cuando se oprime el boton de pdf
     * 
     * @param ac
     */
    public void oprimirPresentar() {
        // <CODIGO_DESARROLLADO>
        archivoDescarga = null;
        getReporte(FORMATOS.PDF);
        // </CODIGO_DESARROLLADO>
    }

    /**
     * metodo que se llama cuando se oprime el boton de excel
     * 
     * @param ac
     */
    public void oprimirExcel() {
        // <CODIGO_DESARROLLADO>
        archivoDescarga = null;
        
        getReporte(FORMATOS.EXCEL97);
        // </CODIGO_DESARROLLADO>
    }

    /**
     * metodo que genera el reporte en pdf y excel
     * 
     * @param formatos
     */
    public void getReporte(FORMATOS formatos) {
        // <CODIGO_DESARROLLADO>
    	

        StringBuilder condicion = new StringBuilder("");
        if ("1".equals(opcion)
            && !SysmanFunciones.validarVariableVacio(dependencias)) {
            condicion.append(" AND PERSONAL.DEPENDENCIA = '" + dependencias
                + "' ");
            condicion.append(
                            " ORDER BY BANCOS_NOMINA.NOMBRE, Personal.nombrecompleto");
        }

        try {
    	    String paramFormulario = null;
			try {
				paramFormulario = ejbSysmanUtil.consultarParametro(compania,
				        "INFORME POR DEPENDENCIA",
				        SessionUtil.getModulo(), new Date(), false);
			} catch (SystemException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
            Map<String, Object> parametros = new HashMap<>();
            HashMap<String, Object> reemplazar = new HashMap<>();
            reemplazar.put("compania", compania);
            reemplazar.put("condicion", condicion.toString());

            Reporteador.resuelveConsulta(paramFormulario,
                            Integer.parseInt(SessionUtil.getModulo()),
                            reemplazar, parametros);
            parametros.put("PR_NOMBREEMPRESA",
                            SessionUtil.getCompaniaIngreso().getNombre());

            archivoDescarga = JsfUtil.exportarStreamed(
            				paramFormulario,
                            parametros,
                            ConectorPool.ESQUEMA_SYSMAN,
                            formatos);
        }
        catch (JRException | IOException | SysmanException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    /**
     * metodo que se llama cuando ocurre un camo de la opcion
     */
    public void cambiarOpcion() {
        // heredado del bean base
    }

    /**
     * metodo que se llama cuando se selecciona un a dependencia
     * 
     * @param event
     */
    public void seleccionarFilaDependencia(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        dependencias = SysmanFunciones
                        .nvl(registroAux.getCampos().get("CODIGO"), "")
                        .toString();
        nombreDependencia = SysmanFunciones
                        .nvl(registroAux.getCampos().get("NOMBRE"), "")
                        .toString();
    }

    public String getOpcion() {
        return opcion;
    }

    public void setOpcion(String opcion) {
        this.opcion = opcion;
    }

    public String getDependencias() {
        return dependencias;
    }

    public void setDependencias(String dependencias) {
        this.dependencias = dependencias;
    }

    public String getNombreDependencia() {
        return nombreDependencia;
    }

    public void setNombreDependencia(String nombreDependencia) {
        this.nombreDependencia = nombreDependencia;
    }

    public RegistroDataModelImpl getListaDependencia() {
        return listaDependencia;
    }

    public void setListaDependencia(RegistroDataModelImpl listaDependencia) {
        this.listaDependencia = listaDependencia;
    }

    /**
     * Atributo usado para descargar contenidos de archivos desde la
     * vista
     */
    public StreamedContent getArchivoDescarga() {
        return archivoDescarga;
    }

}
