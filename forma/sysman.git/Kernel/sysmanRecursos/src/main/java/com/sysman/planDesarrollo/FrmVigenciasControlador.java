package com.sysman.planDesarrollo;

import com.sysman.beanbase.BeanBaseModal;
import com.sysman.componentes.Direccionador;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.planDesarrollo.enums.FrmVigenciasControladorEnum;
import com.sysman.planDesarrollo.enums.FrmVigenciasControladorUrlEnum;
import com.sysman.plandesarrollo.ejb.EjbPlanDesarrolloCeroGeneralRemote;
import com.sysman.plandesarrollo.ejb.EjbPlanDesarrolloUnoGeneralRemote;
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

import org.primefaces.context.RequestContext;
import org.primefaces.event.SelectEvent;

/**
 *
 * @author sdaza
 * 
 * @version 1, 09/08/2016
 * @version 2, 12/06/2017 jrodriguezr Se refactoriza el código: Se
 * pasa el numero del formulario al enumerado, se eliminan conexiones
 * y se ajustan metodos de generacion de reportes.
 * 
 * @version 2.5, 26/02/2018 eamaya, adicion del ejb que realiza el
 * cuadre de saldos
 *
 */
@ManagedBean
@ViewScoped
public class FrmVigenciasControlador extends BeanBaseModal
{
    private final String compania;
    // <DECLARAR_ATRIBUTOS>
    private String vigencia;
    private int existePlan;
    /**
     * variable que almacena la cantidad de digitos de id del plan
     * indicativo si maneja indicador de meta de producci�n
     **/
    private int digMetaProd;
    /**
     * variable que almacena la cantidad de digitos de id del plan
     * indicativo si maneja indicador de meta de resultado
     **/
    private int digMetaRes;
    /**
     * variable que almacena la cantidad de digitos de id del plan
     * indicativo si maneja indicador de accion
     **/
    private int digAccion;
    private String ano;
    private boolean aplicaTrazadores;
    // </DECLARAR_ATRIBUTOS>
    // <DECLARAR_PARAMETROS>
    // </DECLARAR_PARAMETROS>
    // <DECLARAR_LISTAS>
    private List<Registro> listaVIGENCIA;

    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>
    // </DECLARAR_LISTAS_COMBO_GRANDE>

    @EJB
    private EjbPlanDesarrolloCeroGeneralRemote ejbPlanDesarrollo;
    @EJB
    private EjbPlanDesarrolloUnoGeneralRemote ejbPlanDesarrolloUno;
    @EJB
    private EjbSysmanUtilRemote ejbSysmanUtil;

    /**
     * Creates a new instance of FrmVigenciasControlador
     */
    public FrmVigenciasControlador()
    {
        super();
        compania = SessionUtil.getCompania();
        try
        {
            numFormulario = GeneralCodigoFormaEnum.FRM_VIGENCIAS_CONTROLADOR
                            .getCodigo();
            validarPermisos();
            // <INI_ADICIONAL>
            // </INI_ADICIONAL>
        }
        catch (Exception ex)
        {
            Logger.getLogger(FrmVigenciasControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            SessionUtil.redireccionarMenuPermisos();
        }
    }

    @PostConstruct
    public void init() {
        // <CARGAR_LISTA>
        cargarListaVIGENCIA();
        // </CARGAR_LISTA>
        // <CARGAR_LISTA_COMBO_GRANDE>
        // </CARGAR_LISTA_COMBO_GRANDE>
        abrirFormulario();

    }

    @Override
    public void abrirFormulario() {
        // <CODIGO_DESARROLLADO>
    	try {
    		aplicaTrazadores = ("SI".equals(SysmanFunciones.nvl(ejbSysmanUtil.consultarParametro(compania,
								"APLICA TRAZADORES EN PLAN DE DESARROLLO",SessionUtil.getModulo(),new Date(),
						        true),"NO")));
    		
			if(aplicaTrazadores) {
				vigencia = ejbSysmanUtil.consultarParametro(compania,"VIGENCIA GUBERNAMENTAL ACTUAL",SessionUtil.getModulo(),new Date(),true);
			} else {
				vigencia = ano = Integer.toString(SysmanFunciones.ano(new Date()));
			}
		} catch (SystemException e) {
			e.printStackTrace();
		}
        // </CODIGO_DESARROLLADO>
    }

    // <METODOS_CARGAR_LISTA>

    public void cargarListaVIGENCIA() {
        Map<String, Object> param = new TreeMap<>();
        param.put(FrmVigenciasControladorEnum.PARAM0.getValue(),
                        compania);

        try
        {
            listaVIGENCIA = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            FrmVigenciasControladorUrlEnum.URL3599
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e)
        {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    public void oprimirACEPTAR() {
        // <CODIGO_DESARROLLADO>
        /**
         * En la siguientes se usa la funci�n en PL/SQL del
         * PCK_PLAN_DESARROLLO.FC_CARGAR_NIVEL la cual evalua si
         * existe configuraci�n, si no es asi retorna un cero y si
         * exite carga una colecci�n con el plan indicativo y retorna
         * la cantidad registros encontrados. Se usan las funciones
         * FC_GETM_PRO, FC_GETM_RES y FC_GET_ACCION para retornar el
         * numero de digitos del id que tenga el indicador
         * correspondiente
         **/

        try
        {
            existePlan = (int) ejbPlanDesarrollo.cargarNivel(compania,
                            Integer.parseInt(vigencia), "");

            if (existePlan == 0)
            {
                JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB4086"));
            }
            else
            {
                digMetaProd = ejbPlanDesarrollo.obtenerDigitosMetaProduccion();

                digMetaRes = ejbPlanDesarrollo.obtenerDigitosMetaResultado();

                digAccion = ejbPlanDesarrollo.obtenerDigitosAccion();

            }

            Direccionador dir = new Direccionador();

            if (SessionUtil.getMenuActual().equals(
                            FrmVigenciasControladorEnum.MENU670101
                                            .getValue()))
            {
                dir.setNumForm(Integer
                                .toString(GeneralCodigoFormaEnum.FRMPIINDICADORS_CONTROLADOR
                                                .getCodigo()));
                Map<String, Object> parametros = new HashMap<>();
                parametros.put(FrmVigenciasControladorEnum.VIGENCIA.getValue(),
                                vigencia);
                parametros.put(FrmVigenciasControladorEnum.DIGMETAPROD
                                .getValue(),
                                digMetaProd);
                parametros.put(FrmVigenciasControladorEnum.DIGMETARES
                                .getValue(),
                                digMetaRes);
                dir.setParametros(parametros);
                RequestContext.getCurrentInstance().closeDialog(dir);
            }
            else if (SessionUtil.getMenuActual().equals(
                            FrmVigenciasControladorEnum.MENU670102
                                            .getValue()))
            {
            	if(aplicaTrazadores) {
            		dir.setNumForm(Integer
                            .toString(GeneralCodigoFormaEnum.PLANDESARROLLOTRAZ_CONTROLADOR
                                            .getCodigo()));
            	} else {
	                dir.setNumForm(Integer
	                                .toString(GeneralCodigoFormaEnum.PIPLANDESARROLLOS_CONTROLADOR
	                                                .getCodigo()));
            	}
                Map<String, Object> parametros = new HashMap<>();
                parametros.put(FrmVigenciasControladorEnum.VIGENCIA.getValue(),
                                vigencia);
                parametros.put(FrmVigenciasControladorEnum.DIGACCION.getValue(),
                                digAccion);
                dir.setParametros(parametros);
                RequestContext.getCurrentInstance().closeDialog(dir);
            }

            else if (SessionUtil.getMenuActual().equals(
                            FrmVigenciasControladorEnum.MENU670203
                                            .getValue()))
            {

                ejbPlanDesarrollo.cuadrarSaldos(compania,
                                Integer.parseInt(vigencia));

                JsfUtil.agregarMensajeInformativo(
                                idioma.getString("MSM_PROCESO_EJECUTADO"));
            }

            else if (SessionUtil.getMenuActual().equals(
                            FrmVigenciasControladorEnum.MENU670106
                                            .getValue()))
            {
                dir.setNumForm(Integer
                                .toString(GeneralCodigoFormaEnum.FRMEQUIVALENCIAS_CONTROLADOR
                                                .getCodigo()));
                Map<String, Object> parametros = new HashMap<>();
                parametros.put(FrmVigenciasControladorEnum.VIGENCIA.getValue(),
                                vigencia);

                parametros.put(FrmVigenciasControladorEnum.DIGMETAPROD
                                .getValue(),
                                digMetaProd);

                dir.setParametros(parametros);
                RequestContext.getCurrentInstance().closeDialog(dir);
            }
            else if ("670401".equals(SessionUtil.getMenuActual()))
            {
                ejbPlanDesarrolloUno.generarMantenimientoPlan(compania, Integer.parseInt(vigencia),
                                SessionUtil.getUser().getCodigo());
                JsfUtil.agregarMensajeInformativo(
                                idioma.getString("MSM_PROCESO_EJECUTADO"));
            }
        }
        catch (SystemException e)
        {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

        // </CODIGO_DESARROLLADO>
    }

    public void oprimirCANCELAR() {
        // <CODIGO_DESARROLLADO>
        RequestContext.getCurrentInstance().closeDialog(null);
        // </CODIGO_DESARROLLADO>
    }

    public void retornarFormularioACEPTAR(SelectEvent event) {
        // <CODIGO_DESARROLLADO>

        // </CODIGO_DESARROLLADO>
    }

    // </METODOS_BOTONES>
    // <METODOS_CAMBIAR>
    // </METODOS_CAMBIAR>
    // <METODOS_COMBOS_GRANDES>
    // </METODOS_COMBOS_GRANDES>

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
    // </SET_GET_ATRIBUTOS>

    public String getVigencia() {
        return vigencia;
    }

    public void setVigencia(String vigencia) {
        this.vigencia = vigencia;
    }

    // <SET_GET_PARAMETROS>
    // </SET_GET_PARAMETROS>
    // <SET_GET_LISTAS>
    public List<Registro> getListaVIGENCIA() {
        return listaVIGENCIA;
    }

    public void setListaVIGENCIA(List<Registro> listaVIGENCIA) {
        this.listaVIGENCIA = listaVIGENCIA;
    }

    // </SET_GET_LISTAS>
    // <SET_GET_LISTAS_COMBO_GRANDE>
    // </SET_GET_LISTAS_COMBO_GRANDE>
}
