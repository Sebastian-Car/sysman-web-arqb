package com.sysman.precontractual;

import com.sysman.beanbase.BeanBaseModal;
import com.sysman.componentes.Direccionador;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.precontractual.enums.PeriodoEtapaControladorUrlEnum;
import com.sysman.recursos.ejb.EjbSysmanUtilRemote;
import com.sysman.util.SysmanFunciones;

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
import javax.faces.event.ActionEvent;
import javax.naming.NamingException;

import org.primefaces.context.RequestContext;

/**
 *
 * @author lcortes
 * @version 1, 12/04/2016
 * 
 * @version 2, 01/09/2017, <strong>pespitia</strong>:
 * <li>Refactoring de sentencias SQL.
 * <li>Se aplicaron las recomendaciones de SonarLint.
 */
@ManagedBean
@ViewScoped
public class PeriodoEtapaControlador extends BeanBaseModal {
    private final String compania;

    /**
     * Constante a nivel de clase que aloja el codigo del modulo desde
     * el cual el usuario inicio sesion
     */
    private String modulo;

    /**
     * Constante a nivel de clase que aloja la cadena:
     * <code>COMPANIA</code>
     */
    private final String cCompania;

    /**
     * Constante a nivel de clase que aloja la cadena:
     * <code>DESCRIPCION</code>
     */
    private final String cDescripcion;

    /**
     * Constante a nivel de clase que aloja la cadena:
     * <code>TIPOCONTRATO</code>
     */
    private final String cTipoContrato;

    private String tipoContrato;
    private String anio;
    private List<Registro> listacmbTipo;
    private List<Registro> listacmbAno;

    /**
     * Atributo que permite utilizar las funciones y procedimientos
     * del paquete: <code>PCK_SYSMAN_UTL</code>
     */
    @EJB
    private EjbSysmanUtilRemote ejbSysmanUtil;
    
    private Map<String,Object> parametroswf;
    /**
     * Creates a new instance of PeriodoEtapaControlador
     */
    public PeriodoEtapaControlador() {
        super();

        compania = SessionUtil.getCompania();
        modulo = SessionUtil.getModulo();

        cCompania = GeneralParameterEnum.COMPANIA.getName();
        cDescripcion = GeneralParameterEnum.DESCRIPCION.getName();
        cTipoContrato = GeneralParameterEnum.TIPOCONTRATO.getName();

        try {
            numFormulario = GeneralCodigoFormaEnum.PERIODO_ETAPA_CONTROLADOR
                            .getCodigo();

            parametroswf = (Map<String,Object>) SessionUtil.getSessionVarContainer("parametroswf");
			if(parametroswf != null) {
				SessionUtil.setSessionVar("modulo", "19");
				modulo = SessionUtil.getModulo();
			}
            validarPermisos();
        }
        catch (Exception ex) {
            Logger.getLogger(PeriodoEtapaControlador.class.getName())
                            .log(Level.SEVERE, null, ex);

            SessionUtil.redireccionarMenuPermisos();
        } finally {
        	try {
				SessionUtil.removeSessionVarContainer("parametroswf");
			} catch(NamingException e) {
				e.printStackTrace();
			}
        }
    }

    @PostConstruct
    public void inicializar() {
        cargarListacmbTipo();
        cargarListacmbAno();
        abrirFormulario();
    }

    @Override
    public void abrirFormulario() {
        // <CODIGO_DESARROLLADO>
        anio = String.valueOf(SysmanFunciones.ano(new Date()));
        // </CODIGO_DESARROLLADO>
    }

    public void cargarListacmbTipo() {
        Map<String, Object> param = new TreeMap<>();
        param.put(cCompania, compania);

        try {
            listacmbTipo = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            PeriodoEtapaControladorUrlEnum.URL2704
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    public void cargarListacmbAno() {
        Map<String, Object> param = new TreeMap<>();
        param.put(cCompania, compania);

        try {
            listacmbAno = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            PeriodoEtapaControladorUrlEnum.URL3282
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    public void oprimircmdCancelar(ActionEvent ac) {
        // <CODIGO_DESARROLLADO>
        RequestContext.getCurrentInstance().closeDialog(null);
        // </CODIGO_DESARROLLADO>
    }

    public void oprimircmdAceptar(ActionEvent ac) {
        // <CODIGO_DESARROLLADO>
        if (anio.isEmpty() || tipoContrato.isEmpty()) {
            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB2185"));
            return;
        }

        /* Descripcion de la lista tipo precontrato */
        String descripcion = service.buscarEnLista(tipoContrato, cTipoContrato,
                        cDescripcion, listacmbTipo);

        try {
            /* Verifica el estado del año */
            String estado = ejbSysmanUtil.verificarEstadoPeriodoAnual(compania,
                            Integer.parseInt(anio), Integer.parseInt(modulo),
                            1);

            /* No exiten datos con los parametros suministrados */
            if ("E".equals(estado)) {
                JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB3509")
                                .replace("#ANIO#", anio));
                return;
            }

            Map<String, Object> parametros = new HashMap<>();
            parametros.put("anio", anio);
            parametros.put("tipoContrato", tipoContrato);
            parametros.put("desdeMonitor", "false");
            parametros.put("nombreTipoContrato", descripcion);
            parametros.put("estadoVigencia", estado);
            parametros.put("nombEstadoVigencia", nombrarEstado(estado));

            Direccionador direccionador = new Direccionador();
            direccionador.setParametros(parametros);

            direccionador.setNumForm(Integer
                            .toString(GeneralCodigoFormaEnum.TRANSACCIONS_CONTROLADOR
                                            .getCodigo()));
            if(parametroswf != null) {
            	try {
            		parametros.put("parametroswf",parametroswf);
    				SessionUtil.setSessionVarContainer("parametros",parametros);
    			} catch(NamingException e) {
    				e.printStackTrace();
    			}
            	JsfUtil.ejecutarJavaScript("window.location.href='/sysmanPrecontractual/transaccion.sysman';");
            } else {
            	RequestContext.getCurrentInstance().closeDialog(direccionador);
            }
        }
        catch (NumberFormatException | SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

        // </CODIGO_DESARROLLADO>
    }

    // <METODOS_CAMBIAR>
    /**
     * Metodo ejecutado al cambiar el control cmbAno asociado al combo
     * Anio.
     */
    public void cambiarcmbAno() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }
    // </METODOS_CAMBIAR>

    /**
     * Asigna un nombre al codigo del estado.
     * 
     * @param estado
     * -> Codigo del estado.
     * @return El nombre del estado.
     * <li><code>A</code>: Activo
     * <li><code>C</code>: Cancelado
     * <li><code>E</code>: E
     */
    private String nombrarEstado(String estado) {
        String nombre = estado;

        switch (estado) {
        case "A":
            nombre = "Activo";
            break;
        case "C":
            nombre = "Cancelado";
            break;
        default:
            break;
        }

        return nombre;
    }

    public String getTipoContrato() {
        return tipoContrato;
    }

    public void setTipoContrato(String tipoContrato) {
        this.tipoContrato = tipoContrato;
    }

    public String getAnio() {
        return anio;
    }

    public void setAnio(String anio) {
        this.anio = anio;
    }

    public List<Registro> getListacmbTipo() {
        return listacmbTipo;
    }

    public void setListacmbTipo(List<Registro> listacmbTipo) {
        this.listacmbTipo = listacmbTipo;
    }

    public List<Registro> getListacmbAno() {
        return listacmbAno;
    }

    public void setListacmbAno(List<Registro> listacmbAno) {
        this.listacmbAno = listacmbAno;
    }
}
