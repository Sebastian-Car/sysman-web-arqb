package com.sysman.general;

import com.sysman.beanbase.BeanBaseContinuoAcmeImpl;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.enums.GenericUrlEnum;
import com.sysman.exception.SystemException;
import com.sysman.general.enums.FuenterecursosControladorUrlEnum;
import com.sysman.general.enums.FuenterecursosppsControladorUrlEnum;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.event.RowEditEvent;

/**
 *
 * @author ybecerra
 * @version 1, 18/05/2016
 *
 * -- Modificado por lcortes 04/04/2017 16:19. Ajustes Refactoring y
 * SonarLint.
 *
 * @version 2, 12/06/2017 jrodriguezr Se refactoriza el código: Se
 * pasa el numero del formulario al enumerado, se eliminan conexiones
 * y se ajustan metodos de generacion de reportes.
 *
 */
@ManagedBean
@ViewScoped
public class FuenterecursosControlador extends BeanBaseContinuoAcmeImpl
{
    private final String compania;
    // <DECLARAR_ATRIBUTOS>
    private HashMap<String, Object> rid;
    private boolean insertarFormulario;
    private boolean actualizarFormulario;
    private boolean eliminarFormulario;
    // </DECLARAR_ATRIBUTOS>
    // <DECLARAR_PARAMETROS>
    // </DECLARAR_PARAMETROS>
    // <DECLARAR_LISTAS>
    private List<Registro> listaPais;
    private List<Registro> listaAno;
    /**
     * Lista de registro de la tabla tipo fuente sia
     */
    private List<Registro> listacodigoSia;
    private List<Registro> listaequivalenteSigVig;
    
    /**
     * Atributo auxliar el cual es asiganado en el momento que se
     * activa la edicion de un registro. Toma el valor del indice
     * dentro de la grilla del registro seleccionado para editar
     */
	private int indice;
	private int anoNom;
    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>
    // </DECLARAR_LISTAS_COMBO_GRANDE>

	/**
     * Creates a new instance of FuenterecursosControlador
     */
    @SuppressWarnings("unchecked")
    public FuenterecursosControlador()
    {
        super();
        compania = SessionUtil.getCompania();
        try
        {
            numFormulario = GeneralCodigoFormaEnum.FUENTERECURSOS_CONTROLADOR
                            .getCodigo();
            validarPermisos();
            // <INI_ADICIONAL>
            Map<String, Object> parametrosEntrada = SessionUtil.getFlash();
            if (parametrosEntrada != null)
            {
                rid = (HashMap<String, Object>) parametrosEntrada.get("rid");

            }
            // </INI_ADICIONAL>
        }
        catch (Exception ex)
        {
            Logger.getLogger(FuenterecursosControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            SessionUtil.redireccionarMenuPermisos();
        }
    }

    @PostConstruct
    public void inicializar()
    {
        enumBase = GenericUrlEnum.FUENTE_RECURSOS;
        buscarLlave();
        reasignarOrigen();
        registro = new Registro(new HashMap<String, Object>());
        // <CARGAR_LISTA>
        cargarListaPais();
        cargarListaAno();
        cargarListacodigoSia();
        // </CARGAR_LISTA>
        // <CARGAR_LISTA_COMBO_GRANDE>
        // </CARGAR_LISTA_COMBO_GRANDE>
        abrirFormulario();

    }

    @Override
    public void reasignarOrigen()
    {
        buscarUrls();
        parametrosListado.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);

    }

    // <METODOS_CARGAR_LISTA>
    public void cargarListaPais()
    {

        try
        {
            listaPais = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            FuenterecursosControladorUrlEnum.URL3183
                                                                            .getValue())
                                            .getUrl(), null));
        }
        catch (SystemException e)
        {
            Logger.getLogger(FuenterecursosControlador.class.getName())
                            .log(Level.SEVERE, null, e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
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
                                                            FuenterecursosControladorUrlEnum.URL3453
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e)
        {
            Logger.getLogger(FuenterecursosControlador.class.getName())
                            .log(Level.SEVERE, null, e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    /**
     * 
     * Carga la lista listacodigoSia
     *
     */
    public void cargarListacodigoSia()
    {

        try
        {
            listacodigoSia = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            FuenterecursosppsControladorUrlEnum.URL001
                                                                            .getValue())
                                            .getUrl(), null));
        }
        catch (SystemException e)
        {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }
    
    public void cargarlistaequivalenteSigVig() {
   	 Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
        param.put(GeneralParameterEnum.ANO.getName(),
       		 anoNom + 1);
      
       try {
       	listaequivalenteSigVig = RegistroConverter.toListRegistro(
                           requestManager.getList(UrlServiceUtil.getInstance()
                                           .getUrlServiceByUrlByEnumID(
                                        		   FuenterecursosppsControladorUrlEnum.URL34074
                                                                           .getValue())
                                           .getUrl(), param));
       }
       catch (SystemException e) {
           logger.error(e.getMessage(), e);
           JsfUtil.agregarMensajeError(e.getMessage());
       }

   }

    // </METODOS_CARGAR_LISTA>
    // <METODOS_BOTONES>
    // </METODOS_BOTONES>
    // <METODOS_CAMBIAR>
    // </METODOS_CAMBIAR>
    // <METODOS_COMBOS_GRANDES>
    // </METODOS_COMBOS_GRANDES>
    @Override
    public void abrirFormulario()
    {
        // <CODIGO_DESARROLLADO>

        if ("90105".equals(SessionUtil.getMenuActual()))
        {
            insertarFormulario = false;
            actualizarFormulario = true;
            eliminarFormulario = false;
        }
        else
        {
            insertarFormulario = true;
            actualizarFormulario = true;
            eliminarFormulario = true;
        }
        // </CODIGO_DESARROLLADO>
    }

    @Override
    public void cancelarEdicion(RowEditEvent event)
    {
        getListaInicial().load();
    }

    @Override
    public boolean insertarAntes()
    {
        // <CODIGO_DESARROLLADO>
        registro.getCampos().put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
        registro.getCampos().put(
                        GeneralParameterEnum.CREATED_BY.getName(),
                        SessionUtil.getUser().getCodigo());
        registro.getCampos().put(
                        GeneralParameterEnum.DATE_CREATED.getName(),
                        new Date());
        registro.getCampos().remove("NOMBREPAIS");
        registro.getCampos().remove("TIPOD");
        return true;
        // </CODIGO_DESARROLLADO>

    }

    @Override
    public boolean insertarDespues()
    {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
        return true;
    }

    @Override
    public boolean actualizarAntes()
    {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
        return true;
    }

    @Override
    public boolean actualizarDespues()
    {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
        return true;
    }

    @Override
    public boolean eliminarAntes()
    {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
        return true;
    }

    @Override
    public boolean eliminarDespues()
    {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
        return true;
    }

    @Override
    public void removerCombos()
    {
        // <CODIGO_DESARROLLADO>
        registro.getCampos().remove(GeneralParameterEnum.COMPANIA.getName());
        registro.getCampos().remove("NOMBREPAIS");
        registro.getCampos().remove("TIPOD");
        // </CODIGO_DESARROLLADO>
    }
    
    /**
     * Metodo ejecutado cuando se activa la edicion de un registro del
     * formulario
     * 
     *
     * @param registro
     * registro del cual se activo la edicion
     */
    public void activarEdicion(Registro registro) {
        indice = listaInicial.getRowIndex();
        anoNom = Integer.parseInt(registro.getCampos().get("ANO").toString());
        cargarlistaequivalenteSigVig();
    }

    public void ejecutarrcCerrar()
    {
        // <CODIGO_DESARROLLADO>
        String[] campos = { "rid" };
        Object[] valores = { rid };

        if (("1030401").equals(SessionUtil.getMenuActual()))
        {
            SessionUtil.redireccionar("/inversionrf.sysman", campos, valores);

        }
        else if (("1030402").equals(SessionUtil.getMenuActual()))
        {
            SessionUtil.redireccionar("/inversionrv.sysman", campos, valores);
        }
        else
        {
            SessionUtil.redireccionarMenuPermisos();
        }
        // </CODIGO_DESARROLLADO>
    }

    @Override
    public void asignarValoresRegistro()
    {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    // <SET_GET_ATRIBUTOS>
    public boolean isInsertarFormulario()
    {
        return insertarFormulario;
    }

    public void setInsertarFormulario(boolean insertarFormulario)
    {
        this.insertarFormulario = insertarFormulario;
    }

    public boolean isActualizarFormulario()
    {
        return actualizarFormulario;
    }

    public void setActualizarFormulario(boolean actualizarFormulario)
    {
        this.actualizarFormulario = actualizarFormulario;
    }

    public boolean isEliminarFormulario()
    {
        return eliminarFormulario;
    }

    public void setEliminarFormulario(boolean eliminarFormulario)
    {
        this.eliminarFormulario = eliminarFormulario;
    }

    // </SET_GET_ATRIBUTOS>
    // <SET_GET_PARAMETROS>
    // </SET_GET_PARAMETROS>
    // <SET_GET_LISTAS>
    /**
     * Retorna la lista listacodigoSia
     * 
     * @return listacodigoSia
     */
    public List<Registro> getListacodigoSia()
    {
        return listacodigoSia;
    }

    /**
     * Asigna la lista listacodigoSia
     * 
     * @param listacodigoSia
     * Variable a asignar en listacodigoSia
     */
    public void setListacodigoSia(List<Registro> listacodigoSia)
    {
        this.listacodigoSia = listacodigoSia;
    }

    public List<Registro> getListaPais()
    {
        return listaPais;
    }

    public void setListaPais(List<Registro> listaPais)
    {
        this.listaPais = listaPais;
    }

    public List<Registro> getListaAno()
    {
        return listaAno;
    }

    public void setListaAno(List<Registro> listaAno)
    {
        this.listaAno = listaAno;
    }
    
    public List<Registro> getListaequivalenteSigVig() {
		return listaequivalenteSigVig;
	}

	public void setListaequivalenteSigVig(List<Registro> listaequivalenteSigVig) {
		this.listaequivalenteSigVig = listaequivalenteSigVig;
	}

	public int getIndice() {
		return indice;
	}

	public void setIndice(int indice) {
		this.indice = indice;
	}
    // </SET_GET_LISTAS>
    // <SET_GET_LISTAS_COMBO_GRANDE>
    // </SET_GET_LISTAS_COMBO_GRANDE>
}
