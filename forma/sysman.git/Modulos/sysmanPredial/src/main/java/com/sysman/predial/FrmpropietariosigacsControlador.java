package com.sysman.predial;

import com.sysman.beanbase.BeanBaseContinuoAcmeImpl;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.enums.GenericUrlEnum;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.predial.ejb.EjbPredialDosRemote;
import com.sysman.predial.enums.FrmpropietariosigacsControladorEnum;

import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.context.RequestContext;
import org.primefaces.event.RowEditEvent;

/**
 *
 * @author NGOMEZ
 * @version 1, 13/06/2016
 * 
 * @author spina - refactorizo conexiones
 * @version 2, 13/06/2017
 * 
 * @version 3, 06/07/2017
 * @author jreina se realizaron los cambios de refactoring en el origen de grilla.
 */

@ManagedBean
@ViewScoped
public class FrmpropietariosigacsControlador extends BeanBaseContinuoAcmeImpl
{
    private String indBorrado;
    // <DECLARAR_ATRIBUTOS>
    private String resolucion;
    private String fecha;
    private String nombre;
    private String codigo;
    private String compania;
    private String pais;
    private String departamento;
    private String municipio;
    private String consecutivo;
    private String ano;
    private String sucursal;
    private static final String IND_BORRADO = "IND_BORRADO";
    
    @EJB
    private EjbPredialDosRemote ejbPredialDosRemote;
    // </DECLARAR_ATRIBUTOS>
    // <DECLARAR_PARAMETROS>
    // </DECLARAR_PARAMETROS>
    // <DECLARAR_LISTAS>
    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>
    // </DECLARAR_LISTAS_COMBO_GRANDE>

    /**
     * Creates a new instance of FrmpropietariosigacsControlador
     */
    public FrmpropietariosigacsControlador()
    {
        super();
        indBorrado = IND_BORRADO;
        try
        {
            numFormulario = GeneralCodigoFormaEnum.FRMPROPIETARIOSIGACS_CONTROLADOR.getCodigo();
            validarPermisos();
            Map<String, Object> parametrosEntrada = SessionUtil.getFlash();
            if (parametrosEntrada != null)
            {
                resolucion = parametrosEntrada.get("resolucion").toString();
                nombre = parametrosEntrada.get("nombre").toString();
                codigo = parametrosEntrada.get("codigo").toString();
                fecha = parametrosEntrada.get("fecha").toString();
                compania = parametrosEntrada.get("compania").toString();
                pais = parametrosEntrada.get("pais").toString();
                departamento = parametrosEntrada.get("departamento").toString();
                municipio = parametrosEntrada.get("municipio").toString();
                consecutivo = parametrosEntrada.get("consecutivo").toString();
                ano = parametrosEntrada.get("ano").toString();
                sucursal = (String) parametrosEntrada.get("sucursal");
            }
            // <INI_ADICIONAL>
            // </INI_ADICIONAL>
        }
        catch (Exception ex)
        {
            Logger.getLogger(FrmpropietariosigacsControlador.class.getName())
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
        enumBase= GenericUrlEnum.IP_RESOLUCIONES_COPROPIETARIOS;
        buscarLlave();
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
        buscarUrls();
        parametrosListado.put(FrmpropietariosigacsControladorEnum.PARAM0.getValue(), "001");
        parametrosListado.put(GeneralParameterEnum.CODIGO.getName(), codigo);

    }

    // <METODOS_CARGAR_LISTA>
    // </METODOS_CARGAR_LISTA>
    // <METODOS_BOTONES>
    public void oprimirCmdAceptar()
    {
        // <CODIGO_DESARROLLADO>
        try
        {
            long res = ejbPredialDosRemote.insertarUsuariosResolucionIgac(
                            resolucion, pais, departamento, municipio,
                            Integer.parseInt(ano), codigo,
                            SessionUtil.getUser().getCodigo(), compania);
            StringBuilder mensaje = new StringBuilder();
            if (res == -1)
            {
                mensaje.append("E").append(idioma.getString("TB_TB1552"));
            }
            else if (res > 0)
            {
                mensaje.append("I").append(idioma.getString("TB_TB2903"));
            }
            else if (res == 0)
            {
                mensaje.append("N").append(idioma.getString("TB_TB2906"));
            }else if(res == -2)
            {
                mensaje.append("I").append(idioma.getString("TB_TB3283"));
            }
            
            RequestContext.getCurrentInstance().closeDialog(mensaje.toString());
        }
        catch (NumberFormatException | SystemException e)
        {
            Logger.getLogger(FrmpropietariosigacsControlador.class.getName())
                            .log(Level.SEVERE, null, e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
        // </CODIGO_DESARROLLADO>
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
        registro.getCampos().put("COMPANIA", compania);
        registro.getCampos().put("PAIS", pais);
        registro.getCampos().put("DEPARTAMENTO", departamento);
        registro.getCampos().put("MUNICIPIO", municipio);
        registro.getCampos().put("RESOLUCION", resolucion);
        registro.getCampos().put("CONSECUTIVO", consecutivo);
        registro.getCampos().put("ANO", ano);
        registro.getCampos().put("SUCURSAL", sucursal);
        registro.getCampos().put("CODIGO", codigo);
        registro.getCampos().put("INDBORRADO", 0);
        registro.getCampos().put("TIPOREGISTRO", "I");

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
        // NO SE IMPLEMENTA
    }

    public void cerrarFormulario()
    {
        RequestContext.getCurrentInstance().closeDialog(null);
    }

    @Override
    public void asignarValoresRegistro()
    {
        // NO SE IMPLEMENTA
    }

    // <SET_GET_ATRIBUTOS>
    public String getResolucion()
    {
        return resolucion;
    }

    public void setResolucion(String resolucion)
    {
        this.resolucion = resolucion;
    }

    public String getFecha()
    {
        return fecha;
    }

    public void setFecha(String fecha)
    {
        this.fecha = fecha;
    }

    public String getNombre()
    {
        return nombre;
    }

    public void setNombre(String nombre)
    {
        this.nombre = nombre;
    }

    public String getCodigo()
    {
        return codigo;
    }

    public void setCodigo(String codigo)
    {
        this.codigo = codigo;
    }

    public String getCompania()
    {
        return compania;
    }

    public void setCompania(String compania)
    {
        this.compania = compania;
    }

    public String getPais()
    {
        return pais;
    }

    public void setPais(String pais)
    {
        this.pais = pais;
    }

    public String getDepartamento()
    {
        return departamento;
    }

    public void setDepartamento(String departamento)
    {
        this.departamento = departamento;
    }

    public String getMunicipio()
    {
        return municipio;
    }

    public void setMunicipio(String municipio)
    {
        this.municipio = municipio;
    }

    public String getConsecutivo()
    {
        return consecutivo;
    }

    public void setConsecutivo(String consecutivo)
    {
        this.consecutivo = consecutivo;
    }

    public String getAno()
    {
        return ano;
    }

    public void setAno(String ano)
    {
        this.ano = ano;
    }

    public String getIndBorrado()
    {
        return indBorrado;
    }

    public void setIndBorrado(String indBorrado)
    {
        this.indBorrado = indBorrado;
    }

    // </SET_GET_ATRIBUTOS>
    // <SET_GET_PARAMETROS>
    // </SET_GET_PARAMETROS>
    // <SET_GET_LISTAS>
    // </SET_GET_LISTAS>
    // <SET_GET_LISTAS_COMBO_GRANDE>
    // </SET_GET_LISTAS_COMBO_GRANDE>
}