package com.sysman.contabilidad;

import com.sysman.beanbase.BeanBaseModal;
import com.sysman.componentes.Direccionador;
import com.sysman.contabilidad.enums.PeriodoComprobanteCntControladorEnum;
import com.sysman.contabilidad.enums.PeriodoComprobanteCntControladorUrlEnum;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.util.SysmanFunciones;

import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.naming.NamingException;

import org.primefaces.context.RequestContext;
import org.primefaces.event.SelectEvent;

/**
 *
 * @author otorres
 * @version 1, 04/03/2016
 * 
 * @version 2, 06/04/2017, pespitia :<br>
 * Se aplicaron las recomendaciones de SonarLint.<br>
 * Se realizo el refactoring.
 */

@ManagedBean
@ViewScoped
public class PeriodoComprobanteCntControlador extends BeanBaseModal {

    private final String compania;

    /**
     * Constante a nivel de clase que aloja la cadena {@code CODIGO}
     */
    private final String cCodigo;

    private int mes;
    private String tipoMovimiento;
    private int ano;
    private String nombreMovimiento;
    private RegistroDataModelImpl listaTipo;
    private List<Registro> listaAno;

    private String opcionMenu;
    private Map<String,Object> parametroswf;

    /**
     * Creates a new instance of PeriodoComprobanteCntControlador
     */
    public PeriodoComprobanteCntControlador() {
        super();

        compania = SessionUtil.getCompania();

        cCodigo = "CODIGO";

        try {
        	parametroswf = (Map<String,Object>) SessionUtil.getSessionVarContainer("parametroswf");
			if(parametroswf != null) {
				SessionUtil.setSessionVar("modulo", "1");
			}
            numFormulario = GeneralCodigoFormaEnum.PERIODO_COMPROBANTE_CNT_CONTROLADOR
                            .getCodigo();
            validarPermisos();
            cargarOpcionMenu();
        }
        catch (Exception ex) {
            Logger.getLogger(PeriodoComprobanteCntControlador.class.getName())
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
        cargarListaTipo();
        abrirFormulario();
        cargarListaAno();
    }

    public void cargarOpcionMenu() {
    	String menu = "";
    	
    	if(parametroswf != null) {
    		menu = SysmanFunciones.nvl(parametroswf.get("menu"),"").toString();
    	} else {
    		menu = SessionUtil.getMenuActual();
    	}
        if (menu == null) {
            SessionUtil.redireccionarMenuPermisos();
            return;
        }

        switch (menu) {
        case "10305":
            opcionMenu = "C";
            break;
        case "10306":
            opcionMenu = "P";
            break;
        case "10307":
            opcionMenu = "N";
            break;
        case "10308":
            opcionMenu = "V";
            break;
        case "10309":
            opcionMenu = "R";
            break;
        case "10310":
            opcionMenu = "Z";
            break;
        case "20201":
            opcionMenu = "E";
            break;
        case "20202":
            opcionMenu = "I";
            break;
        case "20203":
            opcionMenu = "B";
            break;
        case "20204":
            opcionMenu = "G";
            break;
        case "20205":
            opcionMenu = "S";
            break;
        case "20206":
            opcionMenu = "D";
            break;
        case "20207":
            opcionMenu = "A";
            break;
        case "20208":
            opcionMenu = "J";
            break;
        case "20209":
            opcionMenu = "L";
            break;
        case "10312":
            opcionMenu = "T";
            break;
        case "10316":
            opcionMenu = "U";
            break;
        case "1031901":
            opcionMenu = "X";
            break;
        case "1031902":
            opcionMenu = "Y";
            break;
        case "2021801":
            opcionMenu = "X";
            break;
        case "2021802":
            opcionMenu = "Y";
            break;
        case "10320":
            opcionMenu = "O";
            break;
        default:
            opcionMenu = "";
            break;
        }
    }

    public void cargarListaTipo() {
        Map<String, Object> param = new TreeMap<>();
        param.put(PeriodoComprobanteCntControladorEnum.COMPANIA.getValue(),
                        compania);
        param.put(GeneralParameterEnum.USUARIO.getName(), SessionUtil.getUser().getCodigo());
        param.put(GeneralParameterEnum.MODULO.getName(), SessionUtil.getModulo());
        
        String url = PeriodoComprobanteCntControladorUrlEnum.URL0001.getValue();

        if (!opcionMenu.isEmpty()) {
            url = PeriodoComprobanteCntControladorUrlEnum.URL0002.getValue();

            param.put(PeriodoComprobanteCntControladorEnum.CLASECONTABLE
                            .getValue(), opcionMenu);            
        }

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(url);

        listaTipo = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, cCodigo);

    }

    public void cargarListaAno() {
        Map<String, Object> param = new TreeMap<>();

        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        try {
            listaAno = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            PeriodoComprobanteCntControladorUrlEnum.URL4320
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    public void oprimirAceptar() {
        // <CODIGO_DESARROLLADO>
        Map<String, Object> parametros = new HashMap<>();
        parametros.put("ano", String.valueOf(ano));
        parametros.put("mes", String.valueOf(mes));
        parametros.put("tipoMov", tipoMovimiento);
        parametros.put("opcionMenu", opcionMenu);

        Direccionador direccionador = new Direccionador();
        direccionador.setNumForm(
                        String.valueOf(GeneralCodigoFormaEnum.COMPROBANTECNTS_CONTROLADOR
                                        .getCodigo()));
        direccionador.setParametros(parametros);
        if(parametroswf != null) {
        	try {
        		parametros.put("parametroswf",parametroswf);
				SessionUtil.setSessionVarContainer("parametros",parametros);
			} catch(NamingException e) {
				e.printStackTrace();
			}
        	JsfUtil.ejecutarJavaScript("window.location.href='/sysmanContabilidad/comprobantecnt.sysman';");
        } else {
        	RequestContext.getCurrentInstance().closeDialog(direccionador);
        }
        // </CODIGO_DESARROLLADO>
    }

    public void seleccionarFilaTipo(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        tipoMovimiento = asignarValorCampo(registroAux, cCodigo);
        nombreMovimiento = asignarValorCampo(registroAux, "NOMBRE");
    }

    private String asignarValorCampo(Registro reg, String campo) {
        return reg.getCampos().isEmpty() ? ""
            : reg.getCampos().get(campo).toString();
    }

    public void oprimirCancelar() {
        // <CODIGO_DESARROLLADO>
        RequestContext.getCurrentInstance().closeDialog(null);
        // </CODIGO_DESARROLLADO>
    }

    public int getMes() {
        return mes;
    }

    public void setMes(int mes) {
        this.mes = mes;
    }

    public String getTipoMovimiento() {
        return tipoMovimiento;
    }

    public void setTipoMovimiento(String tipoMovimiento) {
        this.tipoMovimiento = tipoMovimiento;
    }

    public int getAno() {
        return ano;
    }

    public void setAno(int ano) {
        this.ano = ano;
    }

    public String getNombreMovimiento() {
        return nombreMovimiento;
    }

    public void setNombreMovimiento(String nombreMovimiento) {
        this.nombreMovimiento = nombreMovimiento;
    }

    public RegistroDataModelImpl getListaTipo() {
        return listaTipo;
    }

    public void setListaTipo(RegistroDataModelImpl listaTipo) {
        this.listaTipo = listaTipo;
    }

    public List<Registro> getListaAno() {
        return listaAno;
    }

    public void setListaAno(List<Registro> listaAno) {
        this.listaAno = listaAno;
    }

    @Override
    public void abrirFormulario() {
        Calendar calendar = Calendar.getInstance();
        ano = calendar.get(Calendar.YEAR);
        mes = calendar.get(Calendar.MONTH) + 1;
        if (listaTipo.getDatasource() != null
            && !listaTipo.getDatasource().isEmpty()) {

            nombreMovimiento = (String) listaTipo.getDatasource().get(0)
                            .getCampos().get("NOMBRE");
            tipoMovimiento = (String) listaTipo.getDatasource().get(0)
                            .getCampos().get(cCodigo);

        }
    }

    @Override
    public int getNumFormulario() {
        return numFormulario;
    }

}
