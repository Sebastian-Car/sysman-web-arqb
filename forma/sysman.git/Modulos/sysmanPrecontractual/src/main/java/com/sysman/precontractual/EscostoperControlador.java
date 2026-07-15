package com.sysman.precontractual;

import com.sysman.beanbase.BeanBaseContinuoAcmeImpl;
import com.sysman.componentes.Direccionador;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.enums.GenericUrlEnum;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.precontractual.enums.EscostoperControladorEnum;
import com.sysman.precontractual.enums.EscostoperControladorUrlEnum;
import com.sysman.precontractual.enums.FrmestprevioproysControladorUrlEnum;
import com.sysman.recursos.ejb.EjbSysmanUtilRemote;
import com.sysman.util.SysmanFunciones;


import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.Date;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.context.RequestContext;
import org.primefaces.event.RowEditEvent;

/**
 *
 * @author otorres
 * @version 1, 28/03/2016
 * 
 * @version 2, 24/08/2017
 * @author jreina se realizaron los cambios de refactoring en cada uno
 * de los combos y en el origen de grilla.
 * 
 */

@ManagedBean
@ViewScoped
public class EscostoperControlador extends BeanBaseContinuoAcmeImpl {

    private final String compania;
    private final String modulo;

    /** Constante a nivel de clase que aloja el valor ##,##0.00 */
    private final String formato1;

    private String estudio;
    private String totalCantidad;
    private String sueldoBasico;
    private String total;
    private HashMap<String, Object> rid;
    private String vigenciaPeriodo;
    private List<Registro> listaGrupo;
    private List<Registro> listacategoriag;
    private boolean verCat; 

    /**
     * Atributo que gestiona la visibilidad de los botones insertar,
     * editar y eliminar
     */
    private boolean esCreador;
    
    @EJB
    private EjbSysmanUtilRemote ejbSysmanUtil;

    /**
     * Creates a new instance of EscostoperControlador
     */
    public EscostoperControlador() {
        super();
        compania = SessionUtil.getCompania();
        modulo = SessionUtil.getModulo();

        formato1 = "##,##0.00";

        try {
            numFormulario = GeneralCodigoFormaEnum.ESCOSTOPER_CONTROLADOR.getCodigo();
            Map<String, Object> parametrosEntrada = SessionUtil.getFlash();
            rid = (HashMap<String, Object>) parametrosEntrada.get("rid");
            estudio = (String) parametrosEntrada.get("txtCodEstudio");
            vigenciaPeriodo = (String) parametrosEntrada.get("vigenciaPeriodo");
            esCreador = Boolean.parseBoolean(
                            parametrosEntrada.get("esCreador").toString());

            SessionUtil.cleanFlash();
            validarPermisos();
        }
        catch (Exception ex) {
            Logger.getLogger(EscostoperControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            SessionUtil.redireccionarMenuPermisos();
        }
    }

    @PostConstruct
    public void inicializar() {
        enumBase= GenericUrlEnum.ES_COSTO_PER;
        buscarLlave();
        reasignarOrigen();
        registro = new Registro();
        obtenerTotales();
        cargarListaGrupo();
        cargarListacategoriag();
        abrirFormulario();
    }

    private void cargarListaGrupo() {
    	 Map<String, Object> param = new TreeMap<>();
         param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
         try {
             listaGrupo = RegistroConverter.toListRegistro(
                             requestManager.getList(UrlServiceUtil.getInstance()
                                             .getUrlServiceByUrlByEnumID(
                                            		 EscostoperControladorUrlEnum.URL0001
                                                                             .getValue())
                                             .getUrl(), param));
         }
         catch (SystemException e) {
             logger.error(e.getMessage(), e);
             JsfUtil.agregarMensajeError(e.getMessage());
         }

	}
    
     private void cargarListacategoriag() {
    	Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put("CODIGO",registro.getCampos().get("GRUPO"));
        param.put("ANO",vigenciaPeriodo);        
        try {
        	listacategoriag = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID("1920001")
                                            .getUrl(), param));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

	}
     
     private void cargarListacategoriagc(int rowNum) {
    	 Map<String, Object> campos = listaInicial.getDatasource()
                 .get(rowNum % 10)
                 .getCampos();

     	 Map<String, Object> param = new TreeMap<>();
         param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
         param.put("CODIGO",campos.get("GRUPO").toString());
         param.put("ANO",vigenciaPeriodo);        
         try {
         	listacategoriag = RegistroConverter.toListRegistro(
                             requestManager.getList(UrlServiceUtil.getInstance()
                                             .getUrlServiceByUrlByEnumID("1920001")
                                             .getUrl(), param));
         }
         catch (SystemException e) {
             logger.error(e.getMessage(), e);
             JsfUtil.agregarMensajeError(e.getMessage());
         }

 	}

	@Override
    public void reasignarOrigen() {
        buscarUrls();
        parametrosListado.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        parametrosListado.put(EscostoperControladorEnum.PARAM0.getValue(),estudio);
    }

    @Override
    public void abrirFormulario() {
    	 try
         {
	    	verCat = "SI".equals(ejbSysmanUtil.consultarParametro	(
	              													compania,
	              													"ACTIVAR CATEGORIAS GRUPO PERSONAL",
	              													modulo, new Date(), true)) ? true : false;
         }
    	 catch (SystemException e)
         {
             logger.error(e.getMessage(), e);
             JsfUtil.agregarMensajeError(e.getMessage());
         }
    }

    @Override
    public void cancelarEdicion(RowEditEvent event) {
        getListaInicial().load();
    }

    @Override
    public boolean insertarAntes() {
        // <CODIGO_DESARROLLADO>
        try {
            registro.getCampos().put("COMPANIA", compania);
            registro.getCampos().put("COD_ESTUDIO", estudio);
            registro.getCampos().put("ID_COSTO",
                            ejbSysmanUtil.generarConsecutivoConValorInicial(
                                            tabla, "COMPANIA = ''" + compania
                                                + "'' AND COD_ESTUDIO = "
                                                + estudio,
                                                "ID_COSTO", "1"));
            calcularValorParcial();
        }
        catch (SystemException ex) {
            Logger.getLogger(EscostoperControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            JsfUtil.agregarMensajeError(ex.getMessage());
        }
        return true;
        // </CODIGO_DESARROLLADO>
    }

    @Override
    public boolean insertarDespues() {
        // <CODIGO_DESARROLLADO>
       obtenerTotales();
        // </CODIGO_DESARROLLADO>
        return true;
    }

    @Override
    public boolean actualizarAntes() {
        // <CODIGO_DESARROLLADO>
        registro.getCampos().put("COD_ESTUDIO", estudio);
        calcularValorParcial();
        // </CODIGO_DESARROLLADO>
        return true;
    }

    @Override
    public boolean actualizarDespues() {
        // <CODIGO_DESARROLLADO>
        total = String.valueOf(0);
        totalCantidad = String.valueOf(0);
        sueldoBasico = String.valueOf(0);
        obtenerTotales();
        // </CODIGO_DESARROLLADO>
        return true;
    }

    @Override
    public boolean eliminarAntes() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
        return true;
    }

    @Override
    public boolean eliminarDespues() {
        // <CODIGO_DESARROLLADO>
        total = String.valueOf(0);
        totalCantidad = String.valueOf(0);
        sueldoBasico = String.valueOf(0);
        obtenerTotales();
        // </CODIGO_DESARROLLADO>
        return true;
    }

    @Override
    public void removerCombos() {
        // </CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    public void cerrarFormulario() {
        RequestContext.getCurrentInstance().closeDialog(null);
    }

    @Override
    public void asignarValoresRegistro() {
        // </CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }
    
    
    public void obtenerTotales(){
        try {
            String cadenaFormato = formato1;

            Map<String,Object> param = new TreeMap<>();
            param.put(GeneralParameterEnum.COMPANIA.getName(),compania);
            param.put(EscostoperControladorEnum.PARAM0.getValue(),estudio);

            Registro auxiliar = RegistroConverter.toRegistro(
                            requestManager.get(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            EscostoperControladorUrlEnum.URL2464
                                                            .getValue())
                                            .getUrl(), param));
            
            if (auxiliar != null && isRegistro(auxiliar, "TOTAL_CANTIDAD")) {

                Double totalCantidadAux = Double.valueOf(auxiliar.getCampos()
                                .get("TOTAL_CANTIDAD").toString());

                totalCantidad = new DecimalFormat(cadenaFormato)
                                .format(totalCantidadAux);
            }
            
            if (auxiliar != null && isRegistro(auxiliar, "TOTAL_SUELDO")) {
                Double totalSueldoAux = Double.valueOf(auxiliar.getCampos()
                                .get("TOTAL_SUELDO").toString());

                sueldoBasico = new DecimalFormat(cadenaFormato)
                                .format(totalSueldoAux);

            }
            
            if (auxiliar != null && isRegistro(auxiliar, "TOTAL")) {
                Double totalAux = Double.valueOf(
                                auxiliar.getCampos().get("TOTAL").toString());

                total = new DecimalFormat(cadenaFormato).format(totalAux);
            }
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    /**
     * Verifica que el campo del registro no tenga valor nulo.
     * 
     * @param miRegistro
     * Registro
     * @param campo
     * Nombre del campo
     * @return valor logico. <br>
     * true: el campo tiene valor diferente de null
     */
    private boolean isRegistro(Registro miRegistro, String campo) {
        return miRegistro.getCampos().get(campo) != null;
    }

    /**
     * Calcular el valor parcial
     */
    public void calcularValorParcial() {
        String fm = String.valueOf(SysmanFunciones.validarCampoVacio(registro.getCampos(), "FM") ? "1" : SysmanFunciones
            .nvl(registro.getCampos().get("FM"), "1"));
        
        double resultado = Double
                        .parseDouble(SysmanFunciones.nvlStr(String.valueOf(
                                        Double.parseDouble(
                                                        (String) SysmanFunciones
                                                                        .nvl(registro.getCampos()
                                                                                        .get("CANTIDAD"),
                                                                                        String.valueOf(0)))
                                            * (Double.parseDouble(
                                                            (String) SysmanFunciones
                                                                            .nvl(registro.getCampos()
                                                                                            .get("SUELDOBASICO"),
                                                                                            String.valueOf(0)))
                                                / 30)
                                            * Double.parseDouble(
                                                            (String) SysmanFunciones
                                                                            .nvl(registro.getCampos()
                                                                                            .get("TIEMPO"),
                                                                                            String.valueOf(0)))),
                                        String.valueOf(0)))
            * Double.parseDouble(fm);

        registro.getCampos().put("VALORPARCIAL", String.valueOf(resultado));
    }

    /**
     * @return the totalCantidad
     */
    public String getTotalCantidad() {
        return totalCantidad;
    }

    /**
     * @param totalCantidad
     * the totalCantidad to set
     */
    public void setTotalCantidad(String totalCantidad) {
        this.totalCantidad = totalCantidad;
    }

    /**
     * @return the sueldoBasico
     */
    public String getSueldoBasico() {
        return sueldoBasico;
    }

    /**
     * @param sueldoBasico
     * the sueldoBasico to set
     */
    public void setSueldoBasico(String sueldoBasico) {
        this.sueldoBasico = sueldoBasico;
    }

    /**
     * @return the total
     */
    public String getTotal() {
        return total;
    }

    /**
     * @param total
     * the total to set
     */
    public void setTotal(String total) {
        this.total = total;
    }

    public void ejecutarrcCerrar() {
        // <CODIGO_DESARROLLADO>
        Map<String,Object> parametros = new TreeMap<>();
        parametros.put("vigenciaPeriodo",vigenciaPeriodo);
        parametros.put("txtCodEstudio",estudio);
        parametros.put("ridEstPrevios",rid);
        
        Direccionador direccionador = new Direccionador();
        direccionador.setNumForm(Integer.toString(GeneralCodigoFormaEnum.FRMESTPREVIOPROYS_CONTROLADOR.getCodigo()));
        direccionador.setParametros(parametros);
        SessionUtil.redireccionarForma(direccionador, SessionUtil.getModulo());
        // </CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }
    
    public void cambiarGrupo() {
    	cargarListacategoriag();
    }
    
    public void cambiarGrupoC(int rowNum) {
    	cargarListacategoriagc(rowNum);
    }
    
    public boolean isEsCreador() {
        return esCreador;
    }

    public void setEsCreador(boolean esCreador) {
        this.esCreador = esCreador;
    }

	public List<Registro> getListaGrupo() {
		return listaGrupo;
	}

	public void setListaGrupo(List<Registro> listaGrupo) {
		this.listaGrupo = listaGrupo;
	}
	
	public List<Registro> getListacategoriag() {
        return listacategoriag;
    }
    
	public void setListacategoriag(List<Registro> listacategoriag) {
        this.listacategoriag = listacategoriag;
    }

	public boolean isVerCat() {
		return verCat;
	}

	public void setVerCat(boolean verCat) {
		this.verCat = verCat;
	}

	public String getModulo() {
		return modulo;
	}
	

    
}
