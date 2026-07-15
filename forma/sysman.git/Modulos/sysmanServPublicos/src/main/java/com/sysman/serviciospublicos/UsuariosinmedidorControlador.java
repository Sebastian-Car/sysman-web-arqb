package com.sysman.serviciospublicos;

import com.sysman.beanbase.BeanBaseContinuoAcmeImpl;
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
import com.sysman.reportes.Reporteador;
import com.sysman.serviciospublicos.ejb.EjbServiciosPublicosOchoRemote;
import com.sysman.serviciospublicos.enums.UsuariosinmedidorControladorEnum;
import com.sysman.serviciospublicos.enums.UsuariosinmedidorControladorUrlEnum;

import java.io.IOException;
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

import org.primefaces.event.RowEditEvent;
import org.primefaces.model.StreamedContent;

import net.sf.jasperreports.engine.JRException;

/**
 *
 * @author jrodriguezr
 * @version 1, 25/08/2016
 * 
 * @author spina - refactorizo conexiones
 * @version 2, 13/06/2017
 * 
 * @version 3, 21/06/2017
 * @author jreina se realizaron los cambios de refactoring en cada uno de los combos y en el origen de grilla.
 */

@ManagedBean
@ViewScoped
public class UsuariosinmedidorControlador extends BeanBaseContinuoAcmeImpl{
    private final String compania;
    private final String cMedidor;
    private final String cCodigoRuta;
    private final String consEstado;
    private final String consMarca;
    // <DECLARAR_ATRIBUTOS>

    private String titulo;
    private StreamedContent archivoDescarga;
    // </DECLARAR_ATRIBUTOS>
    // <DECLARAR_PARAMETROS>
    // </DECLARAR_PARAMETROS>
    // <DECLARAR_LISTAS>
    private List<Registro> listamarca;
    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>
    // </DECLARAR_LISTAS_COMBO_GRANDE>

    private String ciclo;
    
    @EJB
    private EjbServiciosPublicosOchoRemote ejbServiciosPublicosOchoRemote;

    /**
     * Creates a new instance of UsuariosinmedidorControlador
     */
    public UsuariosinmedidorControlador()
    {
        super();
        compania = SessionUtil.getCompania();
        cMedidor = "MEDIDOR";
        cCodigoRuta = GeneralParameterEnum.CODIGORUTA.getName();
        consEstado = GeneralParameterEnum.ESTADO.getName();
        consMarca = GeneralParameterEnum.MARCA.getName();
        try
        {
            numFormulario = GeneralCodigoFormaEnum.USUARIOSINMEDIDOR_CONTROLADOR.getCodigo();
            validarPermisos();
            // <INI_ADICIONAL>
            Map<String, Object> parametrosEntrada = SessionUtil.getFlash();
            if (parametrosEntrada != null)
            {
                ciclo = parametrosEntrada.get("ciclo").toString();
            }
            // </INI_ADICIONAL>
        }
        catch (Exception ex)
        {
            Logger.getLogger(UsuariosinmedidorControlador.class.getName())
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
        tabla = UsuariosinmedidorControladorEnum.TABLA.getValue();
        reasignarOrigen();
        buscarLlave();
        registro = new Registro();
        // <CARGAR_LISTA>
        titulo = " CICLO: " + ciclo;
        cargarListamarca();
        // </CARGAR_LISTA>
        // <CARGAR_LISTA_COMBO_GRANDE>
        // </CARGAR_LISTA_COMBO_GRANDE>
        abrirFormulario();
    }

    @Override
    public void reasignarOrigen()
    {
        urlListado = UrlServiceUtil.getInstance().getUrlServiceByUrlByEnumID(
                        UsuariosinmedidorControladorUrlEnum.URL5182.getValue());
        
        
        parametrosListado.put(GeneralParameterEnum.COMPANIA.getName(),compania);
        parametrosListado.put(UsuariosinmedidorControladorEnum.PARAM0.getValue(),ciclo);
    }

    // <METODOS_CARGAR_LISTA>
    public void cargarListamarca()
    {
        try {
            Map<String, Object> param = new TreeMap<>();
            param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
            
            listamarca= RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            UsuariosinmedidorControladorUrlEnum.URL5832
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
    public void oprimirInforme()
    {
        // <CODIGO_DESARROLLADO>
        archivoDescarga = null;
        getReporte("001047Usuariosinmedidor");
        // </CODIGO_DESARROLLADO>
    }

    private void getReporte(String reporte)
    {
        try
        {
            HashMap<String, Object> reemplazar = new HashMap<>();
            Map<String, Object> parametros = new HashMap<>();
            reemplazar.put("ciclo", ciclo);

            Reporteador.resuelveConsulta(reporte,
                            Integer.parseInt(SessionUtil.getModulo()),
                            reemplazar, parametros);
            parametros.put("PR_CICLO", ciclo);

            archivoDescarga = JsfUtil.exportarStreamed(reporte, parametros,
                            ConectorPool.ESQUEMA_SYSMAN, FORMATOS.PDF);
        }
        catch (JRException | IOException | SysmanException ex)
        {
            Logger.getLogger(UsuariosinmedidorControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            JsfUtil.agregarMensajeError(ex.getMessage());
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
        return true;
        // </CODIGO_DESARROLLADO>
        
    }
    
    @Override
    public void editar(RowEditEvent event) {
        Registro registro = (Registro) event.getObject();
        boolean aux;
        registro.getCampos();
        if(registro.getCampos().get(cMedidor) == null || "".equals(registro.getCampos().get(cMedidor)) ){
            JsfUtil.agregarMensajeInformativo(idioma.getString("TB_TB3332"));
            return;
        }
        if ((registro.getCampos().get(consEstado) == null) && (registro.getCampos().get(consMarca) == null))
        {
            JsfUtil.agregarMensajeInformativo(idioma.getString("TB_TB3242"));
            return;
        }
        else
        {
            int digitos = Integer.parseInt(service.buscarEnLista(registro.getCampos().get(consMarca).toString(),
                           consMarca, "DIGITOS", listamarca));
            try {
                aux=ejbServiciosPublicosOchoRemote.actualizarMedidorEstado(compania,
                                registro.getCampos().get(consEstado).toString(),
                                registro.getCampos().get(consMarca).toString(),
                                registro.getCampos().get(cMedidor).toString(),SessionUtil.getUser().getCodigo(),0,0,Integer.parseInt(ciclo),registro.getCampos().get(cCodigoRuta).toString(), digitos);
                if (aux) {
                JsfUtil.agregarMensajeInformativo(
                                idioma.getString(
                                                "MSM_REGISTRO_MODIFICADO"));
                }
            }
            catch (NumberFormatException | SystemException e) {
                logger.error(e.getMessage(), e);
                JsfUtil.agregarMensajeError(e.getMessage());
            }
        }
      
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
    
    public String getTitulo()
    {
        return titulo;
    }

    public void setTitulo(String titulo)
    {
        this.titulo = titulo;
    }

    public StreamedContent getArchivoDescarga()
    {
        return archivoDescarga;
    }

    // </SET_GET_ATRIBUTOS>
    // <SET_GET_PARAMETROS>
    // </SET_GET_PARAMETROS>
    // <SET_GET_LISTAS>
    public List<Registro> getListamarca()
    {
        return listamarca;
    }

    public void setListamarca(List<Registro> listamarca)
    {
        this.listamarca = listamarca;
    }
    // </SET_GET_LISTAS>
    // <SET_GET_LISTAS_COMBO_GRANDE>
    // </SET_GET_LISTAS_COMBO_GRANDE>
}
