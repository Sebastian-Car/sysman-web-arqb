package com.sysman.general;

import com.sysman.beanbase.BeanBaseModal;
import com.sysman.componentes.Direccionador;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.exception.SystemException;
import com.sysman.general.enums.PeriodoContratosControladorUrlEnum;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.kernel.api.clientwso2.beans.Parameter;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.recursos.ejb.EjbSysmanUtilRemote;
import com.sysman.util.SysmanConstantes;
import com.sysman.util.SysmanFunciones;
import com.sysman.util.rest.ParametrosSIGEC;

import java.util.Calendar;
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
import javax.naming.NamingException;

import org.primefaces.context.RequestContext;

/**
 *
 * @author jrodriguezr
 * @version 1, 06/10/2015
 * @modified jguerrero
 * @version 2. 04/04/2017 Se realizo el refactory. Ademas se hicieron
 * las respectivas Correcciones del sonar.
 * @author asana
 * @version 3, 12/06/2017 Redireccion de formulario.
 */
@ManagedBean
@ViewScoped

public class PeriodoContratosControlador extends BeanBaseModal {

	private static final String TIPO_CONTRATO_COMPRAS_ALMACEN_CORPOBOYACA = "TIPO CONTRATO COMPRAS ALMACEN CORPOBOYACA";
	private static final String VALOR_DEFECTO_CONTRATO_COMPRAS_ALMACEN_CORPOBOYACA = "NO";
    private final String compania;

    private String tipoContrato;
    private String anio;
    private List<Registro> listaTipoContrato;
    private List<Registro> listaAno;

    private boolean anioVisible;
    private boolean etiqueta2Visible;

    @EJB
    private EjbSysmanUtilRemote ejbSysmanUtil;
    
    private Map<String,Object> parametroswf;
	private String modulo;
    /**
     * Creates a new instance of PeriodoContratosControlador
     */
    public PeriodoContratosControlador() {
        super();
        compania = SessionUtil.getCompania();
        try {
            numFormulario = GeneralCodigoFormaEnum.PERIODO_CONTRATOS_CONTROLADOR
                            .getCodigo();
            parametroswf = (Map<String,Object>) SessionUtil.getSessionVarContainer("parametroswf");
			if(parametroswf != null) {
				SessionUtil.setSessionVar("modulo", "9");
			}
			modulo = SessionUtil.getModulo();
            validarPermisos();
        }
        catch (Exception ex) {
            Logger.getLogger(PeriodoContratosControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            SessionUtil.redireccionarMenuPermisos();
        } finally {
        }

    }

    @PostConstruct
    public void inicializar() {
        cargarListaTipoContrato();
        cargarListaAno();
        abrirFormulario();
    }

    public void cargarListaTipoContrato() {

        try {

            
            String valoresTipoContrato = getParametro(TIPO_CONTRATO_COMPRAS_ALMACEN_CORPOBOYACA, VALOR_DEFECTO_CONTRATO_COMPRAS_ALMACEN_CORPOBOYACA);
            Map<String, Object> param = new TreeMap<>();
            param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
            
            if (Integer.parseInt(SessionUtil
                            .getModulo()) == SysmanConstantes.MODULO_ALMACEN) {
            	
            	if (valoresTipoContrato.equals("NO")) {

                listaTipoContrato = RegistroConverter.toListRegistro(
                                requestManager.getList(
                                                UrlServiceUtil.getInstance()
                                                                .getUrlServiceByUrlByEnumID(
                                                                                PeriodoContratosControladorUrlEnum.URL2987
                                                                                                .getValue())
                                                                .getUrl(),
                                                param));
            	}else
            	{
            		listaTipoContrato = RegistroConverter.toListRegistro(
                            requestManager.getList(
                                            UrlServiceUtil.getInstance()
                                                            .getUrlServiceByUrlByEnumID(
                                                                            PeriodoContratosControladorUrlEnum.URL3111
                                                                                            .getValue())
                                                            .getUrl(),
                                            param));
            	}

            }
            else {
                listaTipoContrato = RegistroConverter.toListRegistro(
                                requestManager.getList(
                                                UrlServiceUtil.getInstance()
                                                                .getUrlServiceByUrlByEnumID(
                                                                                PeriodoContratosControladorUrlEnum.URL2957
                                                                                                .getValue())
                                                                .getUrl(),
                                                param));
            }

        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    public void cargarListaAno() {

        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        try {
            listaAno = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            PeriodoContratosControladorUrlEnum.URL3110
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

        anio = String.valueOf(
                        SysmanFunciones.getParteFecha(
                                        new Date(),
                                        Calendar.YEAR));
    }

    public void oprimirAceptar() {

        // <CODIGO_DESARROLLADO>
        Map<String, Object> params = new TreeMap<>();
        params.put("TIPOCONTRATO", tipoContrato);
        params.put("COMPANIA", compania);
        Parameter p;
        
        Map<String,Object> parametroSigec = new TreeMap();
        parametroSigec.put("COMPANIA", compania);
        parametroSigec.put("CLASEF", tipoContrato);
        Parameter auxSigec = new Parameter();

        try {
            p = requestManager.get(UrlServiceUtil.getInstance()
                            .getUrlServiceByUrlByEnumID(
                                            PeriodoContratosControladorUrlEnum.URL29151
                                                            .getValue())
                            .getUrl(), params);
            
            auxSigec = requestManager.get(UrlServiceUtil.getInstance()
                    .getUrlServiceByUrlByEnumID(
                                    PeriodoContratosControladorUrlEnum.URL73058
                                                    .getValue())
                    .getUrl(), parametroSigec);

            String titulo = p.getFields().get("NOMBRE").toString();
            String convenio = p.getFields().get("CONVENIO").toString();
            String obligaCampos = SysmanFunciones.toString(p.getFields().get("OBLIGA_CAMPOS"));
            String habilitaActasInicio = SysmanFunciones.toString(p.getFields().get("APLICA_ACTA_INICIO"));
            String poseeEstampilla = SysmanFunciones.toString(auxSigec.getFields().get("ESTAMPILLA_SIGEC"));
            Map<String, Object> parametros = new HashMap<>();
            parametros.put("vigencia", anio);
            parametros.put("claseF", tipoContrato);
            parametros.put("titulo", titulo);
            parametros.put("convenio", convenio);
            parametros.put("obligaCampos", obligaCampos);
            parametros.put("habilitaActasInicio", habilitaActasInicio);
            parametros.put("poseeEstampilla", poseeEstampilla);
            
            if(parametroswf != null) {
            	String[] campos = { "vigencia", "claseF", "titulo", "convenio", "obligaCampos",
            			           "habilitaActasInicio", "poseeEstampilla" };

            	Object[] valores = { anio, tipoContrato, titulo, convenio, obligaCampos, habilitaActasInicio, poseeEstampilla };
            	SessionUtil.redireccionarFormularioModalFormulario(modulo, String.valueOf(GeneralCodigoFormaEnum.PCONTRATOS_CONTROLADOR
    					.getCodigo()), campos, valores, true);
            }else  {
            	Direccionador direccionador = new Direccionador();
            	direccionador.setNumForm(
            			String.valueOf(GeneralCodigoFormaEnum.PCONTRATOS_CONTROLADOR
            					.getCodigo()));
            	direccionador.setParametros(parametros);
            	RequestContext.getCurrentInstance().closeDialog(direccionador);
            }
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());

        }

    }

    public void oprimirCancelar() {
        // <CODIGO_DESARROLLADO>
        RequestContext.getCurrentInstance().closeDialog(null);
        // </CODIGO_DESARROLLADO>
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

    public List<Registro> getListaTipoContrato() {
        return listaTipoContrato;
    }

    public void setListaTipoContrato(List<Registro> listaTipoContrato) {
        this.listaTipoContrato = listaTipoContrato;
    }

    public List<Registro> getListaAno() {
        return listaAno;
    }

    public void setListaAno(List<Registro> listaAno) {
        this.listaAno = listaAno;
    }

    /**
     * Trae el valor almacenado en la base de datos para el parametro
     * ingresado.
     *
     * @param nombreParametro
     * Nombre del parametro en la base de datos.
     * @param valorDefault
     * Valor por omision en caso de nulo.
     * @return valor asignado al parametro
     */
    private String getParametro(String nombreParametro, String valorDefault) {
        String parametro = null;
        try {

            parametro = ejbSysmanUtil.consultarParametro(
                            compania,
                            nombreParametro,
                            SessionUtil.getModulo(),
                            new Date(), true);
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
        return parametro != null ? parametro : valorDefault;
    }
    @Override
    public void abrirFormulario() {

        // <CODIGO_DESARROLLADO>
        try {
            Map<String, Object> params = new TreeMap<>();
            params.put(GeneralParameterEnum.COMPANIA.getName(), compania);
            List<Registro> rs;

            rs = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            PeriodoContratosControladorUrlEnum.URL6199
                                                                            .getValue())
                                            .getUrl(), params));

            StringBuilder contratos = new StringBuilder();
            for (int i = 0; i < rs.size(); i++) {
                contratos.append(rs.get(i).getCampos().get("CODIGO") + ",");

            }
            // </CODIGO_DESARROLLADO>

            String parametro = ejbSysmanUtil.consultarParametro(
                            compania,
                            "MUESTRA CONTRATOS POR VIGENCIA",
                            SessionUtil.getModulo(),
                            new Date(), true);

            if (parametro == null) {
                parametro = "NO";
            }
            if ("NO".equals(parametro) || "".equals(parametro)) {
                anioVisible = false;
                etiqueta2Visible = false;
            }
            if ("SI".equals(parametro)) {
                anioVisible = true;
                etiqueta2Visible = true;
            }
        }
        catch (SystemException ex) {
            Logger.getLogger(PeriodoContratosControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            JsfUtil.agregarMensajeError(
                            "Transacci�n interrumpida " + ex.getMessage());
        }
    }

    public boolean getAnioVisible() {
        return anioVisible;
    }

    public void setAnioVisible(boolean anioVisible) {
        this.anioVisible = anioVisible;
    }

    public boolean getEtiqueta2Visible() {
        return etiqueta2Visible;
    }

    public void setEtiqueta2Visible(boolean etiqueta2Visible) {
        this.etiqueta2Visible = etiqueta2Visible;
    }

}
