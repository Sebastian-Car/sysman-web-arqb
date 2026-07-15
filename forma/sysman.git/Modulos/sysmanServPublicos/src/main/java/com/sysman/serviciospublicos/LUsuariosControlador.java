package com.sysman.serviciospublicos;

import com.sysman.beanbase.BeanBaseModal;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.jsfutil.ReportesBean.FORMATOS;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.persistencia.ConectorPool;
import com.sysman.recursos.ejb.EjbSysmanUtilRemote;
import com.sysman.reportes.Reporteador;
import com.sysman.serviciospublicos.enums.LUsuariosControladorUrlEnum;
import com.sysman.util.SysmanFunciones;

import java.io.IOException;
import java.text.ParseException;
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

import org.primefaces.model.StreamedContent;

import net.sf.jasperreports.engine.JRException;

/**
 *
 * @author dmaldonado
 * @version 1, 14/09/2016
 *
 * @version 2, 08/06/2017, <strong>pespitia</strong>:<br>
 * Refactoring.
 * @version 3, 13/06/2017, mzanguna Refactoring conexiones
 */
@ManagedBean
@ViewScoped
public class LUsuariosControlador extends BeanBaseModal
{
    private final String compania;
    private final String modulo;
    // <DECLARAR_ATRIBUTOS>
    private String titulo;
    private boolean estratos;
    private String ciclo;
    private Date fechaInicial;
    private Date fechaFinal;
    private StreamedContent archivoDescarga;
    private boolean visibleEstratos;
    // </DECLARAR_ATRIBUTOS>
    // <DECLARAR_PARAMETROS>
    // </DECLARAR_PARAMETROS>
    // <DECLARAR_LISTAS>
    private List<Registro> listaCiclo;
    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>
    // </DECLARAR_LISTAS_COMBO_GRANDE>

    // <EJBs>
    @EJB
    private EjbSysmanUtilRemote ejbSysmanUtil;
    // </EJBs>

    private String formatoInforme;

    /**
     * Creates a new instance of LUsuariosControlador
     */
    public LUsuariosControlador()
    {
        super();
        compania = SessionUtil.getCompania();
        modulo = SessionUtil.getModulo();
        try
        {
            numFormulario = GeneralCodigoFormaEnum.L_USUARIOS_CONTROLADOR.getCodigo();
            validarPermisos();
            // <INI_ADICIONAL>
            // </INI_ADICIONAL>
        }
        catch (Exception ex)
        {
            Logger.getLogger(LUsuariosControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            SessionUtil.redireccionarMenuPermisos();
        }
    }

    public void validarOpcionMenu()
    {
        String menuActual = SessionUtil.getMenuActual();

        if ("74070104".equals(menuActual))
        {
            visibleEstratos = true;
            titulo = idioma.getString("TB_TB1542");
            formatoInforme = "001076NuevosUsuarios";
        }
        else if ("74070105".equals(menuActual))
        {
            visibleEstratos = false;
            titulo = idioma.getString("TB_TB1543");

            try
            {
                boolean keyPar = "SI".equals(SysmanFunciones.nvlStr(
                                ejbSysmanUtil.consultarParametro(compania,
                                                "FORMATO CALIDAD", modulo,
                                                new Date(), true),
                                "NO"));

                formatoInforme = keyPar ? "001078BorrarUsuariosCOS"
                    : "001077BorrarUsuarios";

            }
            catch (SystemException e)
            {
                logger.error(e.getMessage(), e);
                JsfUtil.agregarMensajeError(e.getMessage());
            }
        }
        else
        {
            SessionUtil.redireccionarMenuPermisos();
        }
    }

    @PostConstruct
    public void inicializar()
    {
        // <CARGAR_LISTA>
        cargarListaCiclo();
        // </CARGAR_LISTA>
        // <CARGAR_LISTA_COMBO_GRANDE>
        // </CARGAR_LISTA_COMBO_GRANDE>
        abrirFormulario();
    }

    @Override
    public void abrirFormulario()
    {
        // <CODIGO_DESARROLLADO>
        validarOpcionMenu();
        fechaFinal = new Date();
        fechaInicial = SysmanFunciones.sumarRestarMesesFecha(fechaFinal, -12);
        // </CODIGO_DESARROLLADO>
    }

    // <METODOS_CARGAR_LISTA>
    public void cargarListaCiclo()
    {
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        try
        {
            listaCiclo = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            LUsuariosControladorUrlEnum.URL4705
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e)
        {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    // </METODOS_CARGAR_LISTA>
    // <METODOS_BOTONES>
    public void oprimirPresentar()
    {
        // <CODIGO_DESARROLLADO>
        archivoDescarga = null;
        generaInforme(FORMATOS.PDF);
        // </CODIGO_DESARROLLADO>
    }

    public void oprimirExcel()
    {
        // <CODIGO_DESARROLLADO>
        archivoDescarga = null;
        generaInforme(FORMATOS.EXCEL97);
        // </CODIGO_DESARROLLADO>
    }

    public void generaInforme(FORMATOS formato)
    {

        HashMap<String, Object> reemplazar = new HashMap<>();
        Map<String, Object> parametros = new HashMap<>();
        try
        {

            reemplazar.put("condicionCiclo", "T".equals(ciclo) ? ""
                : "AND SP_USUARIO.CICLO = " + ciclo);
            reemplazar.put("agrupado", estratos ? "SP_USUARIO.ESTRATO"
                : "SP_USUARIO.COMPANIA");
            reemplazar.put("fechaInicial",
                            SysmanFunciones.formatearFecha(fechaInicial));
            reemplazar.put("fechaFinal",
                            SysmanFunciones.formatearFecha(fechaFinal));
            parametros.put("PR_VIGILADOPOR", SessionUtil
                            .getCompaniaIngreso().getRutaVigiladoPor());
            parametros.put("PR_CICLO", "T".equals(ciclo) ? "Todos" : ciclo);
            parametros.put("PR_AGRUPADO", estratos);
            parametros.put("PR_FECHAINICIAL", SysmanFunciones
                            .convertirAFechaCadena(fechaInicial));
            parametros.put("PR_FECHAFINAL", SysmanFunciones
                            .convertirAFechaCadena(fechaFinal));

            Reporteador.resuelveConsulta(formatoInforme.startsWith("001078")
                ? "001077BorrarUsuarios" : formatoInforme,
                            Integer.valueOf(modulo), reemplazar,
                            parametros);

            archivoDescarga = JsfUtil.exportarStreamed(formatoInforme,
                            parametros, ConectorPool.ESQUEMA_SYSMAN,
                            formato);
        }
        catch (ParseException | OutOfMemoryError | JRException
                        | IOException | SysmanException e)
        {
            Logger.getLogger(LUsuariosControlador.class.getName())
                            .log(Level.SEVERE, null, e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    // </METODOS_BOTONES>
    // <METODOS_CAMBIAR>
    public void cambiarFechaInicial()
    {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    public void cambiarFechaFinal()
    {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    // </METODOS_CAMBIAR>
    // <METODOS_COMBOS_GRANDES>
    // </METODOS_COMBOS_GRANDES>

    // <SET_GET_ATRIBUTOS>
    public String getTitulo()
    {
        return titulo;
    }

    public void setTitulo(String titulo)
    {
        this.titulo = titulo;
    }

    public boolean getEstratos()
    {
        return estratos;
    }

    public void setEstratos(boolean estratos)
    {
        this.estratos = estratos;
    }

    public Date getFechaInicial()
    {
        return fechaInicial;
    }

    public void setFechaInicial(Date fechaInicial)
    {
        this.fechaInicial = fechaInicial;
    }

    public Date getFechaFinal()
    {
        return fechaFinal;
    }

    public void setFechaFinal(Date fechaFinal)
    {
        this.fechaFinal = fechaFinal;
    }

    public boolean isVisibleEstratos()
    {
        return visibleEstratos;
    }

    public void setVisibleEstratos(boolean visibleEstratos)
    {
        this.visibleEstratos = visibleEstratos;
    }

    public String getCiclo()
    {
        return ciclo;
    }

    public void setCiclo(String ciclo)
    {
        this.ciclo = ciclo;
    }

    public StreamedContent getArchivoDescarga()
    {
        return archivoDescarga;
    }

    // </SET_GET_ATRIBUTOS>
    // <SET_GET_PARAMETROS>
    // </SET_GET_PARAMETROS>
    // <SET_GET_LISTAS>
    public List<Registro> getListaCiclo()
    {
        return listaCiclo;
    }

    public void setListaCiclo(List<Registro> listaCiclo)
    {
        this.listaCiclo = listaCiclo;
    }
    // </SET_GET_LISTAS>
    // <SET_GET_LISTAS_COMBO_GRANDE>
    // </SET_GET_LISTAS_COMBO_GRANDE>
}
