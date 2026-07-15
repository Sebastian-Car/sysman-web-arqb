package com.sysman.general;

import com.sysman.beanbase.BeanBaseContinuoAcmeImpl;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.enums.GenericUrlEnum;
import com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException;
import com.sysman.exception.SystemException;
import com.sysman.general.enums.NivelplanindsControladorUrlEnum;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
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

import org.primefaces.event.RowEditEvent;
import org.primefaces.event.SelectEvent;

/**
 *
 * @author dmaldonado
 * @version 1, 19/08/2015
 *
 * @author lcortes
 * @version 2, 26,27,28/09/2017 09/10/2017. Refactorizaci�n del c�digo
 * y reemplazo del llamado a la clase Acciones por el ejb respectivo.
 * Se agrega validacion en el metodo retornarFormularioPasa.
 */
@ManagedBean
@ViewScoped

public class NivelplanindsControlador extends BeanBaseContinuoAcmeImpl {

    private final String compania;
    private final String modulo;
    
    /**
     * variable toma el valor del parametro = APLICA TRAZADORES EN PLAN DE DESARROLLO
     */
  	private boolean aplicaTrazadoresParam;

    private String anio;
    private List<Registro> listaAno;
    private final String moduloBancos = SessionUtil.getModulo();

    private int indice;

    @EJB
    private EjbSysmanUtilRemote ejbSysmanUtil;

    /**
     * Creates a new instance of NivelplanindsControlador
     */
    public NivelplanindsControlador() {
        super();
        numFormulario = GeneralCodigoFormaEnum.NIVELPLANINDS_CONTROLADOR
                        .getCodigo();
        compania = SessionUtil.getCompania();
        modulo = SessionUtil.getModulo();

        try {
            validarPermisos();
        }
        catch (SysmanException ex) {
            logger.error(ex.getMessage(), ex);
            SessionUtil.redireccionarMenuPermisos();
        }
    }

    @PostConstruct
    public void inicializar() {
        enumBase = GenericUrlEnum.BP_NIVEL_PLAN_IND;
        buscarLlave();
        registro = new Registro(new HashMap<String, Object>());
        try {
            anio = ejbSysmanUtil.consultarParametro(compania,
                            "VIGENCIA GUBERNAMENTAL ACTUAL",
                            SessionUtil.getModulo(), new Date(), true);

        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
        reasignarOrigen();

        registro = new Registro(new HashMap<String, Object>());
        cargarListaAno();
        abrirFormulario();

    }

    @Override
    public void reasignarOrigen() {
        buscarUrls();
        parametrosListado.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
        parametrosListado.put(GeneralParameterEnum.ANO.getName(), anio);
    }

    public void cargarListaAno() {

        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        try {
            listaAno = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            NivelplanindsControladorUrlEnum.URL4542
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    public void oprimirPasa() {
        // <CODIGO_DESARROLLADO>
        String[] campos = { "opcion" };
        String[] valores = { "NivPlan" };

        SessionUtil.cargarModalDatos(
                        String.valueOf(GeneralCodigoFormaEnum.FRMACTUVIG_CONTROLADOR
                                        .getCodigo()),
                        moduloBancos, campos, valores);
        // </CODIGO_DESARROLLADO>
    }

    public void cambiarAno() {

        reasignarOrigen();
        cargado = false;
    }

    @Override
    public void abrirFormulario() {
    	try {
    		
    		
			aplicaTrazadoresParam = "SI".equals(SysmanFunciones
					.nvl(ejbSysmanUtil.consultarParametro(compania, "APLICA TRAZADORES EN PLAN DE DESARROLLO",
							modulo, new Date(), true), "NO"));

		} catch (SystemException e) {
			Logger.getLogger(NivelplanindsControlador.class.getName()).log(Level.SEVERE, null, e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}
    }

    @Override
    public void cancelarEdicion(RowEditEvent event) {
        listaInicial.load();
    }

    public void retornarFormularioPasa(SelectEvent event) {
        // <CODIGO_DESARROLLADO>
        String[] dato = (String[]) event.getObject();
        if (dato != null) {
            anio = dato[0];
            cargarListaAno();
            cambiarAno();
            JsfUtil.agregarMensajeInformativo(idioma.getString("TB_TB2383")
                            .replace("#$dato1#$", dato[1])
                            .replace("#$dato2#$", dato[2])
                            .replace("#$dato0#$", dato[0]));
        }
        // </CODIGO_DESARROLLADO>
    }

    @Override
    public void removerCombos() {
        // Metodo que se hereda del bean base
        registro.getCampos().remove(GeneralParameterEnum.COMPANIA.getName());
        registro.getCampos().remove(GeneralParameterEnum.VIGENCIA.getName());
    }

    @Override
    public boolean insertarAntes() {
        registro.getCampos().put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
        registro.getCampos().put(GeneralParameterEnum.VIGENCIA.getName(), anio);
        return true;

    }

    @Override
    public boolean insertarDespues() {
        return true;
    }

    @Override
    public boolean actualizarAntes() {

        return true;
    }

    @Override
    public boolean actualizarDespues() {
        return true;
    }

    @Override
    public boolean eliminarAntes() {

        return true;
    }

    @Override
    public boolean eliminarDespues() {
        return true;
    }

    @Override
    public void asignarValoresRegistro() {
        // Metodo que se hereda del bean base
    }

    public List<Registro> getListaAno() {
        return listaAno;
    }

    public void setListaAno(List<Registro> listaAno) {
        this.listaAno = listaAno;
    }

    public String getAnio() {
        return anio;
    }

    public void setAnio(String anio) {
        this.anio = anio;
    }

    public int getIndice() {
        return indice;
    }

    public void setIndice(int indice) {
        this.indice = indice;
    }

    public void activarEdicion(Registro reg) {
        indice = listaInicial.getRowIndex();

    }

	/**
	 * @return the aplicaTrazadoresParam
	 */
	public boolean isAplicaTrazadoresParam() {
		return aplicaTrazadoresParam;
	}

	/**
	 * @param aplicaTrazadoresParam the aplicaTrazadoresParam to set
	 */
	public void setAplicaTrazadoresParam(boolean aplicaTrazadoresParam) {
		this.aplicaTrazadoresParam = aplicaTrazadoresParam;
	}
    
    
    

}
