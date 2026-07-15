package com.sysman.general;

import com.sysman.beanbase.BeanBaseContinuoAcmeImpl;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.enums.GenericUrlEnum;
import com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.recursos.ejb.EjbSysmanUtilRemote;
import com.sysman.util.SysmanFunciones;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.naming.NamingException;

import org.primefaces.context.RequestContext;
import org.primefaces.event.RowEditEvent;

/**
 *
 * @author jrodriguezr
 * @version 1, 12/01/2016
 *
 * @author jlramirez
 * @version 2, 04/04/2017, se modifico la presentacion de los listados
 * y de la grilla. Se realizo el proceso de Refactoring y
 * modificaciones según especificaciones de SONARLINT
 *
 * --Modificado por lcortes 12/06/2017. Se reemplaza el valor del
 * atributo numero de formulario por el enumerado correspondiente. Se
 * reemplaza concatenacion de variable String por el metodo concatenar
 * de la clase SysmanFunciones.
 * 
 * --Modificado por asana 23/10/2017, Se modifica el criterio que se
 * estaba enviando para crear el consecutivo dado que estaba creando
 * registros en cero
 */
@ManagedBean
@ViewScoped
public class MultassancionesControlador extends BeanBaseContinuoAcmeImpl
{

    private final String compania;
    private final String cod;
    private final String desc;
    private final String tiptrans;
    private String claseOrden;
    private String titulo;
    private String numeroOrden;
    private String tipoTransaccion;
    private int indice;
    private List<Registro> listaEstadoMulta;
    private boolean actoRevocacionLocked;
    private boolean fechaActoRevocacionLocked;
    private List<Registro> listaIndicadorEnvio;
    /**
     * Nombre del campo Estado Multa.
     */
    private static final String ESTADOMULTA = "ESTADOMULTA";

    @EJB
    private EjbSysmanUtilRemote ejbSysmanUtil;
	private Map<String, Object> parametroswf;
	private String modulo;

    /**
     * Creates a new instance of MultassancionesControlador
     */
    public MultassancionesControlador()
    {
        super();
        compania = SessionUtil.getCompania();
        cod = "CODIGO";
        desc = "DESCRIPCION";
        tiptrans = "TIPOTRANSACCION";
        try
        {
        	parametroswf = (Map<String,Object>) SessionUtil.getSessionVarContainer("parametroswf");
        	if(parametroswf != null) {
        		SessionUtil.setSessionVar("modulo", "9");
        	}
            modulo = SessionUtil.getModulo();
            Map<String, Object> parametrosEntrada = SessionUtil.getFlash();
            if (parametrosEntrada != null)
            {
                claseOrden = (String) parametrosEntrada.get("claseF");
                numeroOrden = (String) parametrosEntrada.get("numeroOrden");
                tipoTransaccion = (String) parametrosEntrada
                                .get("tipoTransaccion");
                parametrosEntrada.remove("tipoTransaccion");
            }
            titulo = "S".equals(tipoTransaccion) ? "SANCIONES" : "MULTAS";

            numFormulario = GeneralCodigoFormaEnum.MULTASSANCIONES_CONTROLADOR
                            .getCodigo();
            validarPermisos();
        }
        catch (SysmanException | NamingException ex)
        {
            logger.error(ex.getMessage(), ex);
            SessionUtil.redireccionarMenuPermisos();
        }
    }

    @PostConstruct
    public void inicializar()
    {
        enumBase = GenericUrlEnum.MULTASSANCIONES;
        buscarLlave();
        reasignarOrigen();
        registro = new Registro(new HashMap<String, Object>());
        cargarListaEstadoMulta();
        cargarListaIndicadorEnvio();
        abrirFormulario();
    }

    @Override
    public void reasignarOrigen()
    {
        buscarUrls();
        parametrosListado.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
        parametrosListado.put(GeneralParameterEnum.CLASEORDEN.getName(),
                        claseOrden);
        parametrosListado.put("NUMEROORDEN", numeroOrden);
        parametrosListado.put(tiptrans, tipoTransaccion);
    }

    public void setIndice(int indice)
    {
        this.indice = indice;
    }

    public int getIndice()
    {
        return indice;
    }

    public List<Registro> getListaEstadoMulta()
    {
        return listaEstadoMulta;
    }

    public void setListaEstadoMulta(List<Registro> listaEstadoMulta)
    {
        this.listaEstadoMulta = listaEstadoMulta;
    }

    public List<Registro> getListaIndicadorEnvio()
    {
        return listaIndicadorEnvio;
    }

    public void setListaIndicadorEnvio(List<Registro> listaIndicadorEnvio)
    {
        this.listaIndicadorEnvio = listaIndicadorEnvio;
    }

    public String getClaseOrden()
    {
        return claseOrden;
    }

    public void setClaseOrden(String claseOrden)
    {
        this.claseOrden = claseOrden;
    }

    public String getNumeroOrden()
    {
        return numeroOrden;
    }

    public void setNumeroOrden(String numeroOrden)
    {
        this.numeroOrden = numeroOrden;
    }

    public String getTipoTransaccion()
    {
        return tipoTransaccion;
    }

    public void setTipoTransaccion(String tipoTransaccion)
    {
        this.tipoTransaccion = tipoTransaccion;
    }

    public String getTitulo()
    {
        return titulo;
    }

    public void setTitulo(String titulo)
    {
        this.titulo = titulo;
    }

    public boolean isActoRevocacionLocked()
    {
        return actoRevocacionLocked;
    }

    public void setActoRevocacionLocked(boolean actoRevocacionLocked)
    {
        this.actoRevocacionLocked = actoRevocacionLocked;
    }

    public boolean isFechaActoRevocacionLocked()
    {
        return fechaActoRevocacionLocked;
    }

    public void setFechaActoRevocacionLocked(
        boolean fechaActoRevocacionLocked)
    {
        this.fechaActoRevocacionLocked = fechaActoRevocacionLocked;
    }

    public void cargarListaEstadoMulta()
    {
        List<Registro> lista = new ArrayList<>();
        HashMap<String, Object> aux;
        if ("M".equals(tipoTransaccion))
        {
            aux = new HashMap<>();
            aux.put(desc, "EN FIRME");
            aux.put(cod, "0");
            lista.add(new Registro(aux));
            aux = new HashMap<>();
            aux.put(desc,
                            "EN FIRME SIN PAGO MULTA Y SE ENCUENTRA EN MORA");
            aux.put(cod, "1");
            lista.add(new Registro(aux));
            aux = new HashMap<>();
            aux.put(desc, "PAGADA");
            aux.put(cod, "2");
            lista.add(new Registro(aux));
            aux = new HashMap<>();
            aux.put(desc, "REVOCADA");
            aux.put(cod, "3");
            lista.add(new Registro(aux));
        }
        else
        {
            aux = new HashMap<>();
            aux.put(desc, "EN FIRME");
            aux.put(cod, "0");
            lista.add(new Registro(aux));
            aux = new HashMap<>();
            aux.put(desc, "REVOCADA");
            aux.put(cod, "1");
            lista.add(new Registro(aux));
        }
        listaEstadoMulta = lista;
    }

    public void cargarListaIndicadorEnvio()
    {
        List<Registro> lista = new ArrayList<>();
        HashMap<String, Object> aux;
        aux = new HashMap<>();
        aux.put(desc, "SANCION O MULTA NUEVA");
        aux.put(cod, "0");
        lista.add(new Registro(aux));
        aux = new HashMap<>();
        aux.put(desc,
                        "CAMBIO DATOS DE REVOCATORIA O CUMPLIMIENTO DE LA SANCION");
        aux.put(cod, "1");
        lista.add(new Registro(aux));
        aux = new HashMap<>();
        aux.put(desc, "INFORMACION ERRONEA");
        aux.put(cod, "2");
        lista.add(new Registro(aux));
        aux = new HashMap<>();
        aux.put(desc,
                        "INFORMACION ERRONEA EN NIT Y/O NUMERO DE CONTRATO') DESCRIPCION");
        aux.put(cod, "3");
        lista.add(new Registro(aux));

        listaIndicadorEnvio = lista;
    }

    public void cambiarEstadoMulta()
    {
        // <CODIGO_DESARROLLADO>
        if (("M".equals(tipoTransaccion)
            && ("3".equals(registro.getCampos().get(ESTADOMULTA).toString())))
            || ("S".equals(tipoTransaccion)
                && ("1".equals(registro.getCampos().get(ESTADOMULTA)))))
        {
            actoRevocacionLocked = false;
            fechaActoRevocacionLocked = false;
        }
        else
        {
            actoRevocacionLocked = true;
            fechaActoRevocacionLocked = true;
        }
        // </CODIGO_DESARROLLADO>
    }

    public void cambiarActoAdministrativo()
    {
        // </CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    public void cambiarEstadoMultaC(int rowNum)
    {
        // <CODIGO_DESARROLLADO>
        if (("M".equals(tipoTransaccion)
            && ("3".equals(listaInicial.getDatasource().get(rowNum % 10)
                            .getCampos()
                            .get(ESTADOMULTA))))
            || ("S".equals(tipoTransaccion)
                && ("1".equals(listaInicial.getDatasource().get(rowNum % 10)
                                .getCampos().get(ESTADOMULTA)))))
        {
            actoRevocacionLocked = false;
            fechaActoRevocacionLocked = false;
        }
        else
        {
            actoRevocacionLocked = true;
            fechaActoRevocacionLocked = true;
        }
        // </CODIGO_DESARROLLADO>
    }

    public void cambiarActoAdministrativoC(int rowNum)
    {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    public void activarEdicion(Registro registro)
    {
        cargarListaEstadoMulta();
    }

    @Override
    public void abrirFormulario()
    {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    @Override
    public void cancelarEdicion(RowEditEvent event)
    {
        listaInicial.load();
    }

    @Override
    public boolean insertarAntes()
    {
        // <CODIGO_DESARROLLADO>
        registro.getCampos().remove("NINDICADOR_ENVIO");
        registro.getCampos().remove("NESTADOMULTA");

        registro.getCampos().put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
        registro.getCampos().put(GeneralParameterEnum.CLASEORDEN.getName(),
                        claseOrden);
        registro.getCampos().put("NUMERO", numeroOrden);
        registro.getCampos().put(tiptrans, tipoTransaccion);

        try
        {
            StringBuilder criterio = new StringBuilder("");
            criterio.append(" COMPANIA = ''");
            criterio.append(compania);
            criterio.append("'' ");
            criterio.append(" AND CLASEORDEN = ''");
            criterio.append(claseOrden);
            criterio.append("'' ");
            criterio.append(" AND NUMERO = ");
            criterio.append(numeroOrden);
            criterio.append(" AND  TIPOTRANSACCION = ''");
            criterio.append(tipoTransaccion);
            criterio.append("'' ");

            registro.getCampos().put("CONSECUTIVO",
                            ejbSysmanUtil.generarConsecutivoConValorInicial("MULTAS_SANCIONES",
                                            criterio.toString(),
                                            "CONSECUTIVO", "1"));

            //
            // long consecutivo =
            // ejbSysmanUtil.generarConsecutivoConValorInicial(
            // "OBSERVACIONES_ETA",
            // SysmanFunciones.concatenar(
            // " COMPANIA = ''",
            // compania, "'' ",
            // " AND CLASEORDEN = ''",
            // claseOrden, "'' ",
            // " AND NUMERO = ",
            // numeroOrden, " ",
            // " AND TIPOTRANSACCION = ",
            // tipoTransaccion, " "),
            // "ID",
            // "1");

        }
        catch (SystemException ex)
        {
            Logger.getLogger(MultassancionesControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
        }
        // </CODIGO_DESARROLLADO>
        return true;
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
        registro.getCampos().remove("NESTADOMULTA");
        registro.getCampos().remove("NINDICADOR_ENVIO");
        String estadoMulta = registro.getCampos().get(ESTADOMULTA) == null ? " "
            : registro.getCampos().get(ESTADOMULTA).toString();
        Map<String, Object> campos = registro.getCampos();
        if (validarActoAdministrativo(estadoMulta)
            && (SysmanFunciones.validarCampoVacio(campos, "ACTO_ADMINISTRATIVO")
                || SysmanFunciones.validarCampoVacio(campos,
                                "FECHAACTOADMINISTRATIVO")))
        {
            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB2077"));
            return false;
        }
        else if (validarActoRevocacion(estadoMulta)
            && (SysmanFunciones.validarCampoVacio(campos, "ACTOREVOCACION")
                || SysmanFunciones.validarCampoVacio(campos,
                                "FECHAACTOREVOCACION")))
        {
            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB2078"));
            return false;
        }
        // </CODIGO_DESARROLLADO>
        return true;
    }

    private boolean validarActoRevocacion(String estadoMulta)
    {
        return ("M".equals(tipoTransaccion) && "3".equals(estadoMulta))
            || ("S".equals(tipoTransaccion) && "1".equals(estadoMulta));
    }

    private boolean validarActoAdministrativo(String estadoMulta)
    {
        return ("M".equals(tipoTransaccion) && !"3".equals(estadoMulta))
            || ("S".equals(tipoTransaccion) && !"1".equals(estadoMulta));
    }

    public void cerrarFormulario()
    {
        RequestContext.getCurrentInstance().closeDialog(null);
    }

    @Override
    public void asignarValoresRegistro()
    {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    @Override
    public void removerCombos()
    {
        // <CODIGO_DESARROLLADO>
        registro.getCampos().remove(GeneralParameterEnum.COMPANIA.getName());
        registro.getCampos().remove(GeneralParameterEnum.CLASEORDEN.getName());
        registro.getCampos().remove(GeneralParameterEnum.NUMERO.getName());
        registro.getCampos().remove(tiptrans);

        // </CODIGO_DESARROLLADO>
    }

    @Override
    public boolean actualizarDespues()
    {
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

}