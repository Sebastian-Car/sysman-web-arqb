package com.sysman.bancoproyectos;

import com.sysman.bancoproyectos.ejb.EjbBancoProyectoTresRemote;
import com.sysman.beanbase.BeanBaseModal;
import com.sysman.controladores.SessionUtil;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.JsfUtil;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.model.StreamedContent;

import net.sf.jasperreports.engine.JRException;

/**
 *
 * @author esarmiento
 * @version 1, 19/09/2015
 * 
 * @author ybecerra
 * @version 2, 13/09/2017, proceso de Refactoring
 */
@ManagedBean
@ViewScoped
public class CrearAuxiliaresProyectoControlador extends BeanBaseModal
{

    /**
     * Constante a nivel de clase que almacena el codigo de la
     * compania en la cual inicio sesion el usuario, el valor de esta
     * constante es asignado en el constructor a la variable de sesion
     * correspondiente
     */
    private final String compania;
    // <DECLARAR_ATRIBUTOS>
    private StreamedContent archivoDescarga;
    // </DECLARAR_ATRIBUTOS>
    // <DECLARAR_PARAMETROS>
    // </DECLARAR_PARAMETROS>
    // <DECLARAR_LISTAS>
    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>
    // </DECLARAR_LISTAS_COMBO_GRANDE>
    @EJB
    private EjbBancoProyectoTresRemote ejbBancoProyectoTres;

    /**
     * Crea una nueva instancia de CrearAuxiliaresProyectoControlador
     */
    public CrearAuxiliaresProyectoControlador()
    {
        super();
        compania = SessionUtil.getCompania();
        try
        {
            numFormulario = GeneralCodigoFormaEnum.CREAR_AUXILIARES_PROYECTO_CONTROLADOR
                            .getCodigo();
            validarPermisos();
            // <INI_ADICIONAL>
            // </INI_ADICIONAL>
        }
        catch (Exception ex)
        {
            Logger.getLogger(CrearAuxiliaresProyectoControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
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
        // <CARGAR_LISTA>
        // </CARGAR_LISTA>
        // <CARGAR_LISTA_COMBO_GRANDE>
        // </CARGAR_LISTA_COMBO_GRANDE>
        // <CREAR_ARBOLES>
        // </CREAR_ARBOLES>
        abrirFormulario();
    }

    /**
     * Este metodo es invocado el metodo inicializar, se ejecutan las
     * acciones a tener en cuenta en el momento de apertura del
     * formulario
     */
    @Override
    public void abrirFormulario()
    {
        // <CODIGO_DESARROLLADO>

        // </CODIGO_DESARROLLADO>
    }

    // <METODOS_CARGAR_LISTA>
    // </METODOS_CARGAR_LISTA>
    // <METODOS_BOTONES>
    /**
     * 
     * Metodo ejecutado al oprimir el boton Aceptar en la vista
     *
     */
    public void oprimirAceptar()
    {
        archivoDescarga = null;
        String retorno;
        String numRegistros;
        String auxiliares;

        // <CODIGO_DESARROLLADO>
        try
        {
            retorno = ejbBancoProyectoTres.crearAuxiliarDesdeProyecto(compania,
                            SessionUtil.getUser().getCodigo());

            auxiliares = retorno.substring(retorno.indexOf('\n') + 1);

            auxiliares = "\r\n" + " " + auxiliares.replace(",", "\r\n");
            numRegistros = retorno.substring(0, retorno.indexOf('\n'));

            String inf = idioma.getString("TB_TB2313")
                            .replace("#$numRegistros#$",
                                            numRegistros)
                + "\r\n" + idioma.getString("TB_TB3551")
                                .replace("#$auxiliares#$",
                                                auxiliares);

            ByteArrayInputStream streamTexto = JsfUtil.serializarPlano(inf);
            archivoDescarga = JsfUtil.getArchivoDescarga(streamTexto,
                            "Auxiliares.txt");
            ejecutarmensajeArchivo();

        }
        catch (SystemException | JRException | IOException e)
        {
            JsfUtil.agregarMensajeError(e.getMessage());
            logger.error(e.getMessage(), e);

        }

    }

    // </METODOS_BOTONES>
    // <METODOS_CAMBIAR>
    // </METODOS_CAMBIAR>
    // <METODOS_COMBOS_GRANDES>
    // </METODOS_COMBOS_GRANDES>
    // <METODOS_ARBOL>
    // </METODOS_ARBOL>
    public void ejecutarmensajeArchivo()
    {
        JsfUtil.agregarMensajeInformativo(idioma.getString("TB_TB234"));

    }

    // <SET_GET_ATRIBUTOS>
    public StreamedContent getArchivoDescarga()
    {
        return archivoDescarga;
    }
    // </SET_GET_ATRIBUTOS>
    // <SET_GET_PARAMETROS>
    // </SET_GET_PARAMETROS>
    // <SET_GET_LISTAS>
    // </SET_GET_LISTAS>
    // <SET_GET_LISTAS_COMBO_GRANDE>
    // </SET_GET_LISTAS_COMBO_GRANDE>

}
