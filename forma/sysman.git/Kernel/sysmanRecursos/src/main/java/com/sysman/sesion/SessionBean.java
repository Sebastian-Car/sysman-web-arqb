/**
 * Clase: SessionBean.java
 *
 * Descripci�n TODO
 */

package com.sysman.sesion;

import com.sysman.controladores.SessionUtil;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.logica.Compania;
import com.sysman.logica.Usuario;
import com.sysman.util.SysmanConstantes;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ResourceBundle;
import java.util.TimeZone;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.naming.NamingException;

import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.primefaces.context.RequestContext;

/**
 * @version 1.0, 15 de abr. de 2016
 * @author cmanrique
 *
 */
@ManagedBean
@SessionScoped
public class SessionBean {

    private String tituloMensajes;
    /**
     * Para asignar zona horaria del servidor.
     */
    private TimeZone timeZone;
    /*
	 * variables implementacion marca blanca
	 */
	private String empresaParametrizada;
	private String tituloPagEmpresaParametrizada;
	private String bannerEmpresaParametrizada;
	private String loadEmpresaParametrizada;
	private String salirSoftwareParam;
	private static String impresoPorEmpresaParamterizada;
	protected ResourceBundle idioma;
	/**
	 * Arreglo de bytes que contiene la imagen de la compaÃ±ia que se carga desde el
	 * componente de Primefaces.
	 */
	private byte[] imagenLoadBytes;
    protected final Log LOGGER = LogFactory.getLog(this.getClass());

    public SessionBean() {
        idioma = ResourceBundle.getBundle(SysmanConstantes.RUTA_IDIOMA);
        tituloMensajes = JsfUtil.getTituloMensajes();
        timeZone = cargarZonaHoraria();
        // LJDIAZ (Luis Jabobo Diaz) 12/2022
		// MARCA BLANCA Aplica para la generalidad del aplicativo
		empresaParametrizada = JsfUtil.obtenerParametroMarcaBlanca("PIE_PAGINA");
		tituloPagEmpresaParametrizada = JsfUtil.obtenerParametroMarcaBlanca("TITULOPAG");
		bannerEmpresaParametrizada = cargarImagenBanner();
		JsfUtil.ejecutarJavaScript("cargarImagen('bannerParametrizado');");
		salirSoftwareParam = idioma.getString("TG_SALIR_SYSMAN");
		salirSoftwareParam = salirSoftwareParam.replace("s$empresaparam$s",tituloPagEmpresaParametrizada);
		impresoPorEmpresaParamterizada = JsfUtil.obtenerParametroMarcaBlanca("IMPRESOEMPRPARM");
    }
    private String cargarImagenBanner() {		
		try {
			String rutaBanner = JsfUtil.obtenerParametroMarcaBlanca("BANNER");
			File ficheroImagen = new File(rutaBanner);
			InputStream archivo = new FileInputStream(ficheroImagen);
			imagenLoadBytes = IOUtils.toByteArray(archivo);
			return JsfUtil.encodeImage(imagenLoadBytes);
		} catch (IOException e) {
			Logger.getLogger(SessionBean.class.getName()).log(Level.SEVERE, null, e);
			JsfUtil.agregarMensajeError(e.getMessage());
			return null;
		}
	}
    public boolean verificarSesion() {
        return SessionUtil.getSessionVar("usuario") != null;
    }

    public void cerrarSesion() {
        try {
            SessionUtil.eliminarSession();
        }
        catch (NamingException e) {
            Logger.getLogger(SessionBean.class.getName()).log(Level.SEVERE,
                            null, e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    public void cerrarSesionModal() {
        try {
            RequestContext.getCurrentInstance().closeDialog(null);
            SessionUtil.eliminarSession();
        }
        catch (NamingException e) {
            Logger.getLogger(SessionBean.class.getName()).log(Level.SEVERE,
                            null, e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    public void cerrarSesionModalRemoto() {
        try {
            RequestContext.getCurrentInstance().closeDialog(null);
            SessionUtil.eliminarSession();
        }
        catch (NamingException e) {
            Logger.getLogger(SessionBean.class.getName()).log(Level.SEVERE,
                            null, e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    public void redireccionarIdentificacion() {
        SessionUtil.redireccionarIdentificacion();
    }

    public void redireccionarMenu() {
        SessionUtil.redireccionarMenu();
    }

    public String getTituloMensajes() {
        return tituloMensajes;
    }

    public void setTituloMensajes(String tituloMensajes) {
        this.tituloMensajes = tituloMensajes;
    }

    public static TimeZone cargarZonaHoraria() {
        TimeZone timeZone = TimeZone.getDefault();
        return TimeZone.getTimeZone(timeZone.getID());
    }

    public TimeZone getTimeZone() {
        return timeZone;
    }

    public void setTimeZone(TimeZone timeZone) {
        this.timeZone = timeZone;
    }

    /**
     * @return the user
     */
    public Usuario getUser() {
        return SessionUtil.getUser();
    }

    /**
     * @return the compania
     */
    public Compania getCompania() {
        return SessionUtil.getCompaniaIngreso();
    }

    /**
     * Controla el tiempode sessión
     * 
     * @return tiempo de session en milisegundos
     */
    public int tiempoSesion() {

        if (SessionUtil.getUser() == null) {
            return 30 * 60000;
        }
        else {
            return SessionUtil.getMinutosBloqueo();
        }
    }

    /**
     * Abre un dialogo con un visualizador de documentos PDF, cargando
     * la gu&iacute;a de acuerdo al m&oacute;dulo actual.
     */
    public void abrirAyuda() {
        String modulo = SessionUtil.getModulo();
        modulo = modulo == null ? "-1" : modulo;
        String menu = SessionUtil.getMenu();
        modulo = "0".equals(menu) ? "-1" : modulo;
        String menuactual = SessionUtil.getMenuActual();
        
        String submodulo = traerSubmodulo(modulo, menu);
        String moduloayuda = "999";
        String[] campos = new String[1];
        String[] valores = new String[1];
        campos[0] = "Aplicacion";
        valores[0] = modulo;
        
            SessionUtil.cargarModalDatosFlash(
                        String.valueOf(GeneralCodigoFormaEnum.FRM_IMPRESION_AYUDAS
                                        .getCodigo()),
                        moduloayuda, campos, valores);
    }

    /**
     * Eval&uacute;a el m&oacute;dulo y menu actual para identificar
     * si es un subm&oacute;dulo; si lo es, retorna el c&oacute;digo
     * de lo contrario retorna nulo.
     * 
     * @param modulo
     * c&oacute;digo de la aplicaci&oacute;n
     * @param menu
     * c&oacute;digo del men&uacute;
     * @return c&oacute;digo del subm&oacute;dulo, si aplica, o si no
     * retorna nulo
     */
    private String traerSubmodulo(String modulo, String menu) {
        String submodulo = null;
        if (String.valueOf(SysmanConstantes.MODULO_CONTABILIDAD).equals(modulo)
            && menu.startsWith("2")) {
            // TESORERIA
            submodulo = "2";
        }
        else if (String.valueOf(SysmanConstantes.MODULO_NOMINA).equals(modulo)
            && menu.startsWith("7")) {
            // VIATICOS
            submodulo = "7";
        }
        else if (String.valueOf(SysmanConstantes.MODULO_HOJAS_DE_VIDA)
                        .equals(modulo)) {
            if (menu.startsWith("2102")) {
                // CARRERA ADMINISTRATIVA
                submodulo = "2102";
            }
            else if (menu.startsWith("2103")) {
                // SELECCIÓN DE PERSONAL
                submodulo = "2103";
            }
            else if (menu.startsWith("2104")) {
                // BIENESTAR Y CAPACITACIÓN
                submodulo = "2104";
            }
            else if (menu.startsWith("2107")) {
                // MANUAL DE FUNCIONES
                submodulo = "2107";
            }
            else if (menu.startsWith("2108")) {
                // SEGURIDAD Y SALUD EN EL TRABAJO
                submodulo = "2108";
            }
            else if (menu.startsWith("2109")) {
                // AUTOSERVICIO
                submodulo = "2109";
            }
            else if (menu.startsWith("2110")) {
                // EVALUACIONES
                submodulo = "2110";
            }
        }
        return submodulo;
    }
    public String getEmpresaParametrizada() {
		return empresaParametrizada;
	}

	public void setEmpresaParametrizada(String empresaParametrizada) {
		this.empresaParametrizada = empresaParametrizada;
	}

	public String getTituloPagEmpresaParametrizada() {
		return tituloPagEmpresaParametrizada;
	}

	public void setTituloPagEmpresaParametrizada(String tituloPagEmpresaParametrizada) {
		this.tituloPagEmpresaParametrizada = tituloPagEmpresaParametrizada;
	}

	public String getBannerEmpresaParametrizada() {
		return bannerEmpresaParametrizada;
	}

	public void setBannerEmpresaParametrizada(String bannerEmpresaParametrizada) {
		this.bannerEmpresaParametrizada = bannerEmpresaParametrizada;
	}

	public String getLoadEmpresaParametrizada() {
		return loadEmpresaParametrizada;
	}

	public void setLoadEmpresaParametrizada(String loadEmpresaParametrizada) {
		this.loadEmpresaParametrizada = loadEmpresaParametrizada;
	}

	public String getSalirSoftwareParam() {
		return salirSoftwareParam;
	}

	public void setSalirSoftwareParam(String salirSoftwareParam) {
		this.salirSoftwareParam = salirSoftwareParam;
	}
	public static String getImpresoPorEmpresaParamterizada() {
		return impresoPorEmpresaParamterizada;
	}
	public void setImpresoPorEmpresaParamterizada(String impresoPorEmpresaParamterizada) {
		this.impresoPorEmpresaParamterizada = impresoPorEmpresaParamterizada;
	}
}
