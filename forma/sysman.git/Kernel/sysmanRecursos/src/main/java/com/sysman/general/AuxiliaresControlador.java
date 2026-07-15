package com.sysman.general;

import com.sysman.beanbase.BeanBaseDatosAcmeImpl;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.enums.GenericUrlEnum;
import com.sysman.exception.SystemException;
import com.sysman.general.enums.AuxiliaresControladorUrlEnum;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.recursos.ejb.EjbSysmanUtilRemote;
import com.sysman.services.RegistroDataModelImpl;
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

import org.primefaces.event.SelectEvent;

/**
 *
 * @author lcortes
 * @version 1, 23/10/2015
 * @modified jguerrero
 * @version 2. 04/04/2017 Se realizo el refactory del origen de datos, origien de grilla y las consultas de las listas sencillas de lista ano y las listas de tipo RegistroDataModel listaBanco. Ademas
 * se hicieron las respectivas Correcciones del sonar.
 *
 * -- Modificado por lcortes 12/06/2017. Se reemplaza el valor de la variable numero de formulario y en el parametro form en los llamados al metodo cargarModalDatosFlashCerrar por el enumerado
 * correspondiente que contiene el numero de formulario.
 * 
 * @modified jguerrero
 * @version 2. 15/01/2018 Se creo la variable cargarComponentes Encargada de ocultar la pestaña Movimientos, Cód. de Inventario, Cód. de Proyecto, Cód. Banco de Proyectos, Código SIA y el botón
 * Generar Fuente Recursos
 */
@ManagedBean
@ViewScoped

public class AuxiliaresControlador extends BeanBaseDatosAcmeImpl
{

    private final String compania;
    private List<Registro> listaAno;
    private List<Registro> listaAnio;
    private List<Registro> listaCodigoSia;

    private RegistroDataModelImpl listaBanco;
    private String movimiento;
    private String nombre;
    private String nomProyecto;
    private String mesIni;

    private String mesFin;
    private String titulo;

    private final String etiquetaUnoCons;
    private final String etiquetaDosCons;
    private final String etiquetaTresCons;
    private final String codAuxiliarCons;
    private final String nombreAuxCons;
    private final String mesFinalQrCons;
    private final String anoQrCons;
    private final String nombreCons;
    private final String formularioCons;
    private final String auxiliarCons;
    private final String codigoBpCons;
    private final String nombreProyCons;
    private final String mesIniQrCons;

    private boolean cargarComponentes;
    
    private List<Registro> listaTipoVigencia;
    
    private boolean manejaTipoVigencia;
    
    private List<Registro> listaequivalenteSigVig;
    
   
	@EJB
	private EjbSysmanUtilRemote ejbSysmanUtil;

    public AuxiliaresControlador()
    {
        super();
        compania = SessionUtil.getCompania();

        etiquetaUnoCons = "TB_TB677";
        etiquetaDosCons = "TB_TB678";
        etiquetaTresCons = "TB_TB679";
        codAuxiliarCons = "codAuxiliar";
        nombreAuxCons = "nombreAux";
        mesFinalQrCons = "mesFinalQr";
        anoQrCons = "anoQr";
        nombreCons = GeneralParameterEnum.NOMBRE.getName();
        formularioCons = "formulario";
        auxiliarCons = "auxiliar";

        codigoBpCons = "CODIGOBP";
        nombreProyCons = "NOMBREPROYECTO";
        mesIniQrCons = "mesInicialQr";
        try
        {
            numFormulario = GeneralCodigoFormaEnum.AUXILIARES_CONTROLADOR
                            .getCodigo();
            validarPermisos();
            if ("3".equals(SessionUtil.getModulo()))
            {
                titulo = idioma.getString("TB_TB675");
            }
            else
            {
                titulo = idioma.getString("TB_TB676");
            }
        }
        catch (Exception ex)
        {
            Logger.getLogger(AuxiliaresControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            SessionUtil.redireccionarMenuPermisos();
        }

        registro = new Registro(new HashMap<String, Object>());
    }

    @PostConstruct
    public void inicializar()
    {
        enumBase = GenericUrlEnum.AUXILIAR;

        buscarLlave();
        asignarOrigenDatos();
    }

    public void asignarValoresRegistro()
    {
        registro = new Registro();

    }

    @Override
    public void asignarOrigenDatos()
    {

        buscarUrls();
        parametrosListado.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);

    }

    @Override
    public void iniciarListas()
    {
        cargarListaAno();
        cargarListaAnio();
        cargarListaBanco();
        cargarListaCodigoSia();
        cargarListaTipoVigencia();
    }

    @Override
    public void iniciarListasSub()
    {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    @Override
    public void iniciarListasSubNulo()
    {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    public void cargarListaAno()
    {

        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        try
        {
            listaAno = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            AuxiliaresControladorUrlEnum.URL3193
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e)
        {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    public void cargarListaAnio()
    {
        listaAnio = listaAno;
    }

    public void cargarListaBanco()
    {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        AuxiliaresControladorUrlEnum.URL4212
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        listaBanco = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, codigoBpCons);

    }

    /**
     * 
     * Carga la lista listaCodigoSia
     *
     */
    public void cargarListaCodigoSia()
    {
        try
        {
            listaCodigoSia = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            AuxiliaresControladorUrlEnum.URL001
                                                                            .getValue())
                                            .getUrl(), null));
        }
        catch (SystemException e)
        {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }
    
    /**
	 * 
	 * Carga la lista listaTipoVigencia
	 *
	 */
	public void cargarListaTipoVigencia() {
		try {
			listaTipoVigencia = RegistroConverter
					.toListRegistro(
							requestManager.getList(
									UrlServiceUtil.getInstance()
									.getUrlServiceByUrlByEnumID(
											AuxiliaresControladorUrlEnum.URL1766001.getValue())
									.getUrl(),
									null));
		} catch (SystemException e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}
	}
	
	 public void cargarlistaequivalenteSigVig() {
	   	 Map<String, Object> param = new TreeMap<>();
	        param.put(GeneralParameterEnum.COMPANIA.getName(),
	                        compania);
	        int anonom = Integer.parseInt(registro.getCampos().get("ANO").toString());
	        
	        param.put(GeneralParameterEnum.ANO.getName(),
	       		 anonom + 1);
	      
	       try {
	       	listaequivalenteSigVig = RegistroConverter.toListRegistro(
	                           requestManager.getList(UrlServiceUtil.getInstance()
	                                           .getUrlServiceByUrlByEnumID(
	                                        		   AuxiliaresControladorUrlEnum.URL23061
	                                                                           .getValue())
	                                           .getUrl(), param));
	       }
	       catch (SystemException e) {
	           logger.error(e.getMessage(), e);
	           JsfUtil.agregarMensajeError(e.getMessage());
	       }
	       
	   }

    public void oprimirContables()
    {
        // <CODIGO_DESARROLLADO>
        int mesInicio = Integer.parseInt(mesIni);
        int mesFinal = Integer.parseInt(mesFin);
        String codigoAux = SysmanFunciones
                        .nvl(registro.getCampos()
                                        .get(GeneralParameterEnum.CODIGO
                                                        .getName()),
                                        "")
                        .toString();
        if ((codigoAux == null) || codigoAux.isEmpty())
        {
            JsfUtil.agregarMensajeAlerta(idioma.getString(etiquetaUnoCons));
            return;
        }
        if (SysmanFunciones.validarVariableVacio(mesIni)
                        || SysmanFunciones.validarVariableVacio(mesFin)
                        || SysmanFunciones.validarCampoVacio(registro.getCampos(), "ANO"))
        {
            JsfUtil.agregarMensajeAlerta(idioma.getString(etiquetaDosCons));
            return;
        }
        if (mesInicio > mesFinal)
        {
            JsfUtil.agregarMensajeError(idioma.getString(etiquetaTresCons));
            return;
        }
        String[] campos = { "rid", codAuxiliarCons, nombreAuxCons,
                        mesIniQrCons,
                        mesFinalQrCons, anoQrCons, formularioCons };
        Object[] valores = { css, SysmanFunciones.nvl(registro.getCampos()
                        .get(GeneralParameterEnum.CODIGO.getName()), "")
                        .toString(),
                        SysmanFunciones.nvl(registro.getCampos()
                                        .get(nombreCons), "").toString(),
                        mesIni, mesFin,
                        registro.getCampos()
                                        .get(GeneralParameterEnum.ANO
                                                        .getName())
                                        .toString(),
                        auxiliarCons };
        SessionUtil.cargarModalDatosFlashCerrar(
                        Integer.toString(
                                        GeneralCodigoFormaEnum.SUBFORMCENTROS_CONTROLADOR
                                                        .getCodigo()),
                        SessionUtil.getModulo(),
                        campos,
                        valores);
        // </CODIGO_DESARROLLADO>
    }

    public void oprimirPresupuestales()
    {
        // <CODIGO_DESARROLLADO>
        String codigoAux = SysmanFunciones.nvl(registro.getCampos()
                        .get(GeneralParameterEnum.CODIGO.getName()), "")
                        .toString();
        int mesInicio = Integer.parseInt(mesIni);
        int mesFinal = Integer.parseInt(mesFin);
        if ((codigoAux == null) || codigoAux.isEmpty())
        {
            JsfUtil.agregarMensajeAlerta(idioma.getString(etiquetaUnoCons));
            return;
        }
        if (SysmanFunciones.validarVariableVacio(mesIni)
                        || SysmanFunciones.validarVariableVacio(mesFin)
                        || SysmanFunciones.validarCampoVacio(registro.getCampos(), "ANO"))
        {
            JsfUtil.agregarMensajeAlerta(idioma.getString(etiquetaDosCons));
            return;
        }
        if (mesInicio > mesFinal)
        {
            JsfUtil.agregarMensajeError(idioma.getString(etiquetaTresCons));
            return;
        }
        String[] campos = { "rid", codAuxiliarCons, nombreAuxCons,
                        mesIniQrCons,
                        mesFinalQrCons, anoQrCons, formularioCons };
        Object[] valores = { css, SysmanFunciones.nvl(registro.getCampos()
                        .get(GeneralParameterEnum.CODIGO.getName()), "")
                        .toString(),
                        SysmanFunciones.nvl(registro.getCampos()
                                        .get(nombreCons), "").toString(),
                        mesIni, mesFin,
                        registro.getCampos().get("ANO").toString(),
                        auxiliarCons };
        SessionUtil.cargarModalDatosFlashCerrar(
                        Integer.toString(
                                        GeneralCodigoFormaEnum.SUBFORMCENTROPS_CONTROLADOR
                                                        .getCodigo()),
                        SessionUtil.getModulo(),
                        campos,
                        valores);

        // </CODIGO_DESARROLLADO>
    }

    public void oprimirAlmacen()
    {
        // <CODIGO_DESARROLLADO>
        String codigoAux = SysmanFunciones.nvl(registro.getCampos()
                        .get(GeneralParameterEnum.CODIGO.getName()), "")
                        .toString();
        int mesInicio = Integer.parseInt(mesIni);
        int mesFinal = Integer.parseInt(mesFin);
        if ((codigoAux == null) || codigoAux.isEmpty())
        {
            JsfUtil.agregarMensajeAlerta(idioma.getString(etiquetaUnoCons));
            return;
        }
        if (SysmanFunciones.validarVariableVacio(mesIni)
                        || SysmanFunciones.validarVariableVacio(mesFin)
                        || SysmanFunciones.validarCampoVacio(registro.getCampos(), "ANO"))
        {
            JsfUtil.agregarMensajeAlerta(idioma.getString(etiquetaDosCons));
            return;
        }
        if (mesInicio > mesFinal)
        {
            JsfUtil.agregarMensajeError(idioma.getString(etiquetaTresCons));
            return;
        }
        String[] campos = { "rid", codAuxiliarCons, nombreAuxCons,
                        mesIniQrCons,
                        mesFinalQrCons, anoQrCons, formularioCons };
        Object[] valores = { css, SysmanFunciones.nvl(registro.getCampos()
                        .get(GeneralParameterEnum.CODIGO.getName()), "")
                        .toString(),
                        SysmanFunciones.nvl(registro.getCampos()
                                        .get(nombreCons), "").toString(),
                        mesIni, mesFin,
                        SysmanFunciones.nvl(
                                        registro.getCampos().get("ANO"),
                                        "").toString(),
                        auxiliarCons };
        SessionUtil.cargarModalDatosFlashCerrar(
                        Integer.toString(
                                        GeneralCodigoFormaEnum.SUBFORMCENTROAS_CONTROLADOR
                                                        .getCodigo()),
                        SessionUtil.getModulo(),
                        campos,
                        valores);

        // </CODIGO_DESARROLLADO>
    }

    public void oprimirgenFuenteRecursos()
    {
        JsfUtil.agregarMensajeInformativo(
                        idioma.getString("MSM_PROCESO_EJECUTADO"));
    }

    public void cambiarCodigo()
    {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    public void seleccionarFilaBanco(SelectEvent event)
    {
        Registro registroAux = (Registro) event.getObject();

        if (!SysmanFunciones.validarCampoVacio(registroAux.getCampos(),
                        nombreProyCons))
        {

            nomProyecto = registroAux.getCampos().get(nombreProyCons)
                            .toString();
        }
        else
        {
            nomProyecto = "";
        }

        registro.getCampos().put(codigoBpCons,
                        registroAux.getCampos().get(codigoBpCons));
        registro.getCampos().put(nombreProyCons, nomProyecto);
    }

    public void cambiarAnio()
    {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    @Override
    public void abrirFormulario()
    {
        // //<CODIGO_DESARROLLADO>
        validarCamposOpcionMenu();
        try {
			manejaTipoVigencia = "SI".equals(SysmanFunciones.nvl(
					ejbSysmanUtil.consultarParametro(compania, "RELACIONAR VIGENCIA DESDE AUXILIAR GENERAL", SessionUtil.getModulo(), new Date(), true),
					"NO"));
		} catch (SystemException e) {
			e.printStackTrace();
		}
        // </CODIGO_DESARROLLADO>
    }

    @Override
    public void cargarRegistro()
    {
        // <CODIGO_DESARROLLADO>
        precargarRegistro();
        mesIni = String.valueOf(SysmanFunciones
                        .mes(new Date()));
        mesFin = String.valueOf(SysmanFunciones
                        .mes(new Date()));
        cargarlistaequivalenteSigVig();
        // </CODIGO_DESARROLLADO>
    }

    public String getNomProyecto()
    {
        return nomProyecto;
    }

    public void setNomProyecto(String nomProyecto)
    {
        this.nomProyecto = nomProyecto;
    }

    public List<Registro> getListaAno()
    {
        return listaAno;
    }

    public void setListaAno(List<Registro> listaAno)
    {
        this.listaAno = listaAno;
    }

    public List<Registro> getListaAnio()
    {
        return listaAnio;
    }

    public void setListaAnio(List<Registro> listaAnio)
    {
        this.listaAnio = listaAnio;
    }

    /**
     * Retorna la lista listaCodigoSia
     * 
     * @return listaCodigoSia
     */
    public List<Registro> getListaCodigoSia()
    {
        return listaCodigoSia;
    }

    /**
     * Asigna la lista listaCodigoSia
     * 
     * @param listaCodigoSia
     * Variable a asignar en listaCodigoSia
     */
    public void setListaCodigoSia(List<Registro> listaCodigoSia)
    {
        this.listaCodigoSia = listaCodigoSia;
    }

    public RegistroDataModelImpl getListaBanco()
    {
        return listaBanco;
    }

    public void setListaBanco(RegistroDataModelImpl listaBanco)
    {
        this.listaBanco = listaBanco;
    }

    public String getMovimiento()
    {
        return movimiento;
    }

    public void setMovimiento(String movimiento)
    {
        this.movimiento = movimiento;
    }

    public String getNombre()
    {
        return nombre;
    }

    public void setNombre(String nombre)
    {
        this.nombre = nombre;
    }

    public String getMesIni()
    {
        return mesIni;
    }

    public void setMesIni(String mesIni)
    {
        this.mesIni = mesIni;
    }

    public String getMesFin()
    {
        return mesFin;
    }

    public void setMesFin(String mesFin)
    {
        this.mesFin = mesFin;
    }

    public String getTitulo()
    {
        return titulo;
    }

    public void setTitulo(String titulo)
    {
        this.titulo = titulo;
    }

    public boolean isCargarComponentes()
    {
        return cargarComponentes;
    }

    public void setCargarComponentes(boolean cargarComponentes)
    {
        this.cargarComponentes = cargarComponentes;
    }

    @Override
    public boolean insertarAntes()
    {
        registro.getCampos().put(GeneralParameterEnum.COMPANIA.getName(), compania);
        /*
         * if (!"9".equals(SessionUtil.getModulo())) { registro.getCampos().put("CODIGO_SIA", null); }
         */
        return true;
    }

    @Override
    public boolean insertarDespues()
    {
        asignarOrigenDatos();
        return true;
    }

    @Override
    public boolean actualizarAntes()
    {
        registro.getCampos().remove(nombreProyCons);

        if (css != null)
        {
            registro.getCampos().remove(GeneralParameterEnum.COMPANIA.getName());
            registro.getCampos().remove(GeneralParameterEnum.ANO.getName());
            registro.getCampos().remove(GeneralParameterEnum.CODIGO.getName());
            registro.getCampos().remove("REPORTAENREGISTROS");
        }

        return true;
    }

    @Override
    public boolean actualizarDespues()
    {

        HashMap<String, Object> codigoBp = new HashMap<>();

        codigoBp.put("CODIGOBP", registro.getCampos().get(codigoBpCons));

        try
        {
            if (!SysmanFunciones.validarCampoVacio(registro.getCampos(),
                            codigoBpCons)

                            && (listaBanco.getRegistroUnico(codigoBp) != null))
            {
                registro.getCampos().put(nombreProyCons,
                                listaBanco.getRegistroUnico(codigoBp)
                                                .getCampos()
                                                .get(nombreProyCons));
            }
        }
        catch (SystemException e)
        {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

        return true;

    }

    @Override
    public boolean eliminarAntes()
    {
        return true;
    }

    @Override
    public boolean eliminarDespues()
    {
        return true;
    }

    private void validarCamposOpcionMenu()
    {
        if ("6012020".equals(SessionUtil.getMenuActual()))
        {
            cargarComponentes = false;
        }
        else
        {
            cargarComponentes = true;
        }
    }

	public List<Registro> getListaTipoVigencia() {
		return listaTipoVigencia;
	}

	public void setListaTipoVigencia(List<Registro> listaTipoVigencia) {
		this.listaTipoVigencia = listaTipoVigencia;
	}

	public boolean isManejaTipoVigencia() {
		return manejaTipoVigencia;
	}

	public void setManejaTipoVigencia(boolean manejaTipoVigencia) {
		this.manejaTipoVigencia = manejaTipoVigencia;
	}
	
	 public List<Registro> getListaequivalenteSigVig() {
			return listaequivalenteSigVig;
		}

		public void setListaequivalenteSigVig(List<Registro> listaequivalenteSigVig) {
			this.listaequivalenteSigVig = listaequivalenteSigVig;
		}


}
