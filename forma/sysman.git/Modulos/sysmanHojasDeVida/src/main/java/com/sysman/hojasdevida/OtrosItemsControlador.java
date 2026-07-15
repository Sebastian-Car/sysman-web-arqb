
package com.sysman.hojasdevida;

import com.sysman.beanbase.BeanBaseContinuoAcmeImpl;
import com.sysman.componentes.Direccionador;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.hojasdevida.enums.OtrosItemsControladorEnum;
import com.sysman.hojasdevida.enums.OtrosItemsControladorUrlEnum;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.event.RowEditEvent;

/*
 * @version 1.0, 14/12/2017
 * 
 * @author asana Migracion formulario
 */
@ManagedBean
@ViewScoped
public class OtrosItemsControlador extends BeanBaseContinuoAcmeImpl
{
    /**
     * Constante a nivel de clase que almacena el codigo de la
     * compania en la cual inicio sesion el usuario, el valor de esta
     * constante es asignado en el constructor a la variable de sesion
     * correspondiente
     */
    private final String compania;
    Map<String, Object> parametro = new HashMap<>();

    public OtrosItemsControlador()
    {
        super();
        compania = SessionUtil.getCompania();
        try
        {
            numFormulario = GeneralCodigoFormaEnum.OTROSITEMS_CONTROLADOR.getCodigo();
            validarPermisos();
            // <INI_ADICIONAL>
            // </INI_ADICIONAL>
        }
        catch (Exception ex)
        {
            logger.error(ex.getMessage(), ex);
            SessionUtil.redireccionarMenuPermisos();
        }

    }

    /**
     * Este metodo se ejecuta justo despues de que el objeto de la
     * clase del Bean ha sido creado, en este se realizan las
     * asignaciones iniciales necesarias para la visualizacion del
     * formulario, como son tablas, origenes de datos, inicializacion
     * de listas y demas necesarios
     */
    @PostConstruct
    public void inicializar()
    {
        tabla = OtrosItemsControladorEnum.PARAM0.getValue();
        reasignarOrigen();
        buscarLlave();
        abrirFormulario();
    }

    /**
     * En este metodo se asigna al atributo origenDatos del bean base
     * el valor de la consulta del formulario. Tambien carga la lista
     * del formulario por primera vez
     */
    @Override
    public void reasignarOrigen()
    {

        urlListado = UrlServiceUtil.getInstance().getUrlServiceByUrlByEnumID(OtrosItemsControladorUrlEnum.URL0002.getValue());

        parametrosListado.put(GeneralParameterEnum.COMPANIA.getName(), compania);

    }

    // Boton que abre formulario de auxilio Maternidad
    public void oprimirAuxMaternidad(Registro reg, int indice)
    {
        // <CODIGO_DESARROLLADO>
        parametro = cargarParametros(reg);
        parametro.put("opcion", "1");
        Direccionador direccionador = new Direccionador();
        direccionador.setNumForm(String.valueOf(GeneralCodigoFormaEnum.SUBAUXILIOM_CONTROLADOR.getCodigo()));
        direccionador.setParametros(parametro);
        SessionUtil.redireccionarForma(direccionador, SessionUtil.getModulo());
        // </CODIGO_DESARROLLADO>
    }

    // Boton que abre formulario de auxilio Mortuorio
    public void oprimirAuxMortuoso(Registro reg, int indice)
    {
        // <CODIGO_DESARROLLADO>

        parametro = cargarParametros(reg);
        parametro.put("opcion", "2");
        Direccionador direccionador = new Direccionador();
        direccionador.setNumForm(String.valueOf(GeneralCodigoFormaEnum.SUBAUXILIOM_CONTROLADOR.getCodigo()));
        direccionador.setParametros(parametro);
        SessionUtil.redireccionarForma(direccionador, SessionUtil.getModulo());

        // </CODIGO_DESARROLLADO>
    }

    // Boton que abre formulario De otros permisos
    public void oprimirPermisos(Registro reg, int indice)
    {
        // <CODIGO_DESARROLLADO>
        parametro = cargarParametros(reg);
        Direccionador direccionador = new Direccionador();
        direccionador.setNumForm(String.valueOf(GeneralCodigoFormaEnum.SUBPERMRENUM_CONTROLADOR.getCodigo()));
        direccionador.setParametros(parametro);
        SessionUtil.redireccionarForma(direccionador, SessionUtil.getModulo());
        // </CODIGO_DESARROLLADO>
    }

    // Boton que abre formulario De otros permisos
    public void oprimirOtros(Registro reg, int indice)
    {
        // <CODIGO_DESARROLLADO>

        parametro = cargarParametros(reg);
        parametro.put("remuneracion", "3");
        Direccionador direccionador = new Direccionador();
        direccionador.setNumForm(String.valueOf(GeneralCodigoFormaEnum.NAT_SUBLICREMUNS_CONTROLADOR.getCodigo()));
        direccionador.setParametros(parametro);
        SessionUtil.redireccionarForma(direccionador, SessionUtil.getModulo());
        // </CODIGO_DESARROLLADO>
    }

    // Método se llama desde los botones a abrir, se cargan los datos
    // basicos de cada empleado
    public Map<String, Object> cargarParametros(Registro r)
    {

        Map<String, Object> param = new HashMap<>();
        param.put("nombre", r.getCampos().get("NOMBRES").toString());
        param.put("idEmpleado", r.getCampos().get("CODIGO").toString());
        param.put("documento", r.getCampos().get("NUMERO_DCTO").toString());
        param.put("sucursal", r.getCampos().get(GeneralParameterEnum.SUCURSAL.getName()));

        return param;
    }

    public void ejecutarrcCerrar()
    {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
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
        getListaInicial().load();
    }

    @Override
    public boolean insertarAntes()
    {
        // <CODIGO_DESARROLLADO>
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

    @Override
    public void removerCombos()
    {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    @Override
    public void asignarValoresRegistro()
    {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

}
