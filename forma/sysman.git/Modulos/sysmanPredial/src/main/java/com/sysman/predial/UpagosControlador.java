package com.sysman.predial;

import com.sysman.beanbase.BeanBaseContinuoAcmeImpl;
import com.sysman.componentes.Direccionador;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.ReportesBean;
import com.sysman.jsfutil.ReportesBean.FORMATOS;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.persistencia.ConectorPool;
import com.sysman.predial.enums.UpagosControladorUrlEnum;
import com.sysman.reportes.Reporteador;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.event.RowEditEvent;
import org.primefaces.model.StreamedContent;

import net.sf.jasperreports.engine.JRException;

/**
 *
 * @author sdaza
 * @version 1, 02/06/2016 18:07:22 -- Modificado por sdaza
 *
 * @author spina
 * @version 2, 24/07/2017 - se refactoriza para dss, depuracion sonar y ejbs
 */
@ManagedBean
@ViewScoped
public class UpagosControlador extends BeanBaseContinuoAcmeImpl
{
    private final String compania;
    private String modulo;
    // <DECLARAR_ATRIBUTOS>
    private String codigoPadre;
    private String direccionPredio;
    private String nomPropietario;
    private String codigoPredio;
    private String codigoConsulta;
    private String numeroOrden;
    private Map<String, Object> rid;
    private boolean indOcultar;
    private StreamedContent archivoDescarga;
    private String accion;
    // </DECLARAR_ATRIBUTOS>
    // <DECLARAR_PARAMETROS>
    // </DECLARAR_PARAMETROS>
    // <DECLARAR_LISTAS>
    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>
    // </DECLARAR_LISTAS_COMBO_GRANDE>

    private Map<String, Object> parametrosEntrada;

    /**
     * Creates a new instance of UpagosControlador
     */
    @SuppressWarnings("unchecked")
    public UpagosControlador()
    {
        super();
        compania = SessionUtil.getCompania();
        modulo = SessionUtil.getModulo();
        try
        {
            numFormulario = GeneralCodigoFormaEnum.UPAGOS_CONTROLADOR
                            .getCodigo();
            validarPermisos();
            // <INI_ADICIONAL>
            parametrosEntrada = SessionUtil.getFlash();

            if (parametrosEntrada != null)
            {
                rid = (Map<String, Object>) parametrosEntrada.get("rid");
                codigoPredio = (String) parametrosEntrada.get("codigoPredio");
                numeroOrden = (String) parametrosEntrada.get("nroOrden");
                nomPropietario = (String) parametrosEntrada
                                .get("nomPropietario");
                direccionPredio = (String) parametrosEntrada
                                .get("direccionPredio");
                codigoPadre = (String) parametrosEntrada.get("codigoPadre");
                codigoConsulta = codigoPredio;
                accion = (String) parametrosEntrada.get("accion");
            }
            else
            {
                SessionUtil.redireccionarMenuPermisos();
                return;
            }
            // </INI_ADICIONAL>
        }
        catch (Exception ex)
        {
            Logger.getLogger(UpagosControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            SessionUtil.redireccionarMenuPermisos();
        }
        finally
        {
            SessionUtil.cleanFlash();
        }
    }

    @PostConstruct
    public void inicializar()
    {
        reasignarOrigen();

        registro = new Registro();
        // <CARGAR_LISTA>
        // </CARGAR_LISTA>
        // <CARGAR_LISTA_COMBO_GRANDE>
        // </CARGAR_LISTA_COMBO_GRANDE>
        abrirFormulario();

    }

    @Override
    public void reasignarOrigen()
    {
        urlListado = UrlServiceUtil
                        .getUrlBeanById(UpagosControladorUrlEnum.URL322
                                        .getValue());
        parametrosListado.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
        parametrosListado.put(GeneralParameterEnum.PREDIO.getName(),
                        codigoPredio);
    }

    // <METODOS_CARGAR_LISTA>
    // </METODOS_CARGAR_LISTA>
    // <METODOS_BOTONES>
    public void oprimirbtnVerPagos()
    {
        // <CODIGO_DESARROLLADO>
        archivoDescarga = null;
        codigoConsulta = codigoPadre;
        reasignarOrigen();
        // </CODIGO_DESARROLLADO>
    }

    public void oprimirComando18()
    {
        // <CODIGO_DESARROLLADO>
        archivoDescarga = null;
        generarReporte(FORMATOS.PDF);
        // </CODIGO_DESARROLLADO>
    }

    public void generarReporte(ReportesBean.FORMATOS formato)
    {
        if (codigoConsulta == null)
        {
            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB153"));
            return;
        }

        try
        {
            archivoDescarga = null;
            Map<String, Object> parametros = new HashMap<>();
            HashMap<String, Object> reemplazar = new HashMap<>();
            reemplazar.put("codigoConsulta", codigoConsulta);

            parametros.put("PR_NOM_COMPANIA",
                            SessionUtil.getCompaniaIngreso().getNombre());
            parametros.put("PR_NIT_COMPANIA",
                            "NIT " + SessionUtil.getCompaniaIngreso().getNit());
            parametros.put("PR_NOM_PROPIETARIO", nomPropietario);
            parametros.put("PR_DIR_PREDIO", direccionPredio);
            parametros.put("PR_USUARIO_LAB",
                            "Elaborado por :" + SessionUtil.getUser());
            Reporteador.resuelveConsulta("000863INFHISTORICOPAGOSPREDIO",
                            Integer.parseInt(modulo), reemplazar, parametros);

            archivoDescarga = JsfUtil.exportarStreamed(
                            "000863INFHISTORICOPAGOSPREDIO", parametros,
                            ConectorPool.ESQUEMA_SYSMAN, formato);
        }
        catch (JRException | IOException | SysmanException e)
        {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    // </METODOS_BOTONES>
    // <METODOS_CAMBIAR>
    // </METODOS_CAMBIAR>
    // <METODOS_COMBOS_GRANDES>
    // </METODOS_COMBOS_GRANDES>
    @Override
    public void abrirFormulario()
    {
        // <CODIGO_DESARROLLADO>
        if ((codigoPadre == null) || codigoPadre.isEmpty())
        {
            indOcultar = true;
        }
        else
        {
            indOcultar = false;
        }
        // </CODIGO_DESARROLLADO>
    }

    public void ejecutarrcCerrar()
    {
        // <CODIGO_DESARROLLADO>
        String ruta = "/usuariospredial.sysman";
        Direccionador direccionador = new Direccionador();
        direccionador.setRuta(ruta);
        direccionador.setParametros(parametrosEntrada);
        SessionUtil.redireccionar(direccionador);
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

    // <SET_GET_ATRIBUTOS>
    public String getCodigoPadre()
    {
        return codigoPadre;
    }

    public void setCodigoPadre(String codigoPadre)
    {
        this.codigoPadre = codigoPadre;
    }

    public String getDireccionPredio()
    {
        return direccionPredio;
    }

    public void setDireccionPredio(String direccionPredio)
    {
        this.direccionPredio = direccionPredio;
    }

    public String getNomPropietario()
    {
        return nomPropietario;
    }

    public void setNomPropietario(String nomPropietario)
    {
        this.nomPropietario = nomPropietario;
    }

    public String getCodigoPredio()
    {
        return codigoPredio;
    }

    public void setCodigoPredio(String codigoPredio)
    {
        this.codigoPredio = codigoPredio;
    }

    public boolean isIndOcultar()
    {
        return indOcultar;
    }

    public void setIndOcultar(boolean indOcultar)
    {
        this.indOcultar = indOcultar;
    }

    public StreamedContent getArchivoDescarga()
    {
        return archivoDescarga;
    }

    public String getModulo()
    {
        return modulo;
    }

    public void setModulo(String modulo)
    {
        this.modulo = modulo;
    }

    public String getCodigoConsulta()
    {
        return codigoConsulta;
    }

    public void setCodigoConsulta(String codigoConsulta)
    {
        this.codigoConsulta = codigoConsulta;
    }

    public String getNumeroOrden()
    {
        return numeroOrden;
    }

    public void setNumeroOrden(String numeroOrden)
    {
        this.numeroOrden = numeroOrden;
    }

    public Map<String, Object> getRid()
    {
        return rid;
    }

    public void setRid(Map<String, Object> rid)
    {
        this.rid = rid;
    }

    public String getAccion()
    {
        return accion;
    }

    public void setAccion(String accion)
    {
        this.accion = accion;
    }

    public void setArchivoDescarga(StreamedContent archivoDescarga)
    {
        this.archivoDescarga = archivoDescarga;
    }

    // </SET_GET_ATRIBUTOS>
    // <SET_GET_PARAMETROS>
    // </SET_GET_PARAMETROS>
    // <SET_GET_LISTAS>
    // </SET_GET_LISTAS>
    // <SET_GET_LISTAS_COMBO_GRANDE>
    // </SET_GET_LISTAS_COMBO_GRANDE>
}
