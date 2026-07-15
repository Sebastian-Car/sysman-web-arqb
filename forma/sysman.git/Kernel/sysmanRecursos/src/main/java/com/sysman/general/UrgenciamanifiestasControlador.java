package com.sysman.general;

import com.sysman.beanbase.BeanBaseContinuoAcmeImpl;
import com.sysman.componentes.Direccionador;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.enums.GenericUrlEnum;
import com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException;
import com.sysman.util.SysmanFunciones;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.context.RequestContext;
import org.primefaces.event.RowEditEvent;

/**
 *
 * @author jrodriguezr
 * @version 1, 14/12/2015
 * @author jcodriguez, Refactoring y Depuracion
 * @version 2, 15/08/2017
 */
@ManagedBean
@ViewScoped
public class UrgenciamanifiestasControlador extends BeanBaseContinuoAcmeImpl
{

    private final String compania;
    private String claseOrden;
    private String numeroOrden;
    private Map<String, Object> rid;
    private String vigencia;
    private String claseF;
    private String titulo;

    /**
     * Creates a new instance of UrgenciamanifiestasControlador
     */
    public UrgenciamanifiestasControlador()
    {
        super();
        compania = SessionUtil.getCompania();
        Map<String, Object> parametros = SessionUtil.getFlash();
        try
        {
            rid = (Map<String, Object>) parametros.get("rid");
            numFormulario = GeneralCodigoFormaEnum.URGENCIAMANIFIESTAS_CONTROLADOR.getCodigo();
            claseOrden = validarCadena(parametros, "claseOrden");
            numeroOrden = validarCadena(parametros, "numeroOrden");
            vigencia = validarCadena(parametros, "vigencia");
            claseF = validarCadena(parametros, "claseF");
            titulo = validarCadena(parametros, "titulo");
            validarPermisos();
        }
        catch (SysmanException ex)
        {
            Logger.getLogger(UrgenciamanifiestasControlador.class.getName()).log(Level.SEVERE, null, ex);
            RequestContext.getCurrentInstance().closeDialog(this);
        }
    }

    /**
     * metodo utilizado para validar un parametro de sesion
     * 
     * @param campos
     * @param var
     * @return
     */
    private String validarCadena(Map<String, Object> campos, String var)
    {
        return SysmanFunciones.validarCampoVacio(campos, var) ? "" : campos.get(var).toString();
    }

    /**
     * metodo que se llama al orpmimir el boton de cerrar del
     * formulario continuo
     */
    public void ejecutarrcCerrar()
    {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
        redicreccionar();
    }

    /**
     * metodo que redirecciona al formulario de datos con algunos
     * parametros
     */
    private void redicreccionar()
    {

        String ruta = "/pcontrato.sysman";
        Map<String, Object> parametros = new HashMap<>();
        parametros.put("claseOrden", claseOrden);
        parametros.put("numeroOrden", numeroOrden);
        parametros.put(GeneralParameterEnum.VIGENCIA.getName().toLowerCase(), vigencia);
        parametros.put("claseF", claseF);
        parametros.put("titulo", titulo);
        parametros.put("ridPcontrato", rid);
        Direccionador direccionador = new Direccionador();
        direccionador.setRuta(ruta);
        direccionador.setParametros(parametros);
        SessionUtil.redireccionar(direccionador);
    }

    @PostConstruct
    public void init()
    {
        enumBase = GenericUrlEnum.URGENCIAMANIFIESTA;
        buscarLlave();
        reasignarOrigen();
        registro = new Registro(new HashMap<String, Object>());
        abrirFormulario();
    }

    @Override
    public void reasignarOrigen()
    {
        buscarUrls();
        parametrosListado.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        parametrosListado.put(GeneralParameterEnum.CLASEORDEN.getName(), claseOrden);
        parametrosListado.put(GeneralParameterEnum.NUMERO_ORDEN.getName(), numeroOrden);

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

    @Override
    public void abrirFormulario()
    {
        // <CODIGO_DESARROLLADO>
        /*
         * FR412-AL_ABRIR Private Sub Form_Load() Me!CLASEORDEN =
         * Forms!PContrato!TipoContrato Me!ORDENDECOMPRA =
         * Forms!PContrato!numero Me!COMPANIA = Getcompany() End Sub
         */
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
        registro.getCampos().put(GeneralParameterEnum.COMPANIA.getName(), compania);
        registro.getCampos().put(GeneralParameterEnum.CLASEORDEN.getName(), claseOrden);
        registro.getCampos().put("ORDENDECOMPRA", numeroOrden);
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

    public void cerrarFormulario()
    {
        RequestContext.getCurrentInstance().closeDialog(null);
    }

    @Override
    public int getNumFormulario()
    {
        return numFormulario;
    }

    @Override
    public void removerCombos()
    {
        // HEREDADO DEL BEAN BASE
    }

    @Override
    public void asignarValoresRegistro()
    {
        // HEREDADO DEL BEAN BASE
    }
}