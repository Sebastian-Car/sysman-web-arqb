package com.sysman.predial;

import com.sysman.beanbase.BeanBaseContinuoAcmeImpl;
import com.sysman.componentes.Direccionador;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.enums.GenericUrlEnum;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.predial.ejb.EjbPredialCeroRemote;

import java.sql.SQLException;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.naming.NamingException;

import org.primefaces.event.RowEditEvent;

/**
 *
 * @author sdaza
 * @version 1, 30/06/2016
 * 
 * @author spina - refactorizo conexiones
 * @version 2, 13/06/2017
 * 
 * @author spina
 * @version 3, 28/06/2017 - refactorizo dss, depuracion sonar y ejb
 */
@ManagedBean
@ViewScoped
public class FacturadosacuerdosdetsControlador extends BeanBaseContinuoAcmeImpl
{
    private final String compania;
    // <DECLARAR_ATRIBUTOS>
    private Map<String, Object> rid;
    private String codigoAcuerdo;
    private String codigoPredio;
    private boolean acuerdoInactivo;
    private String nomC1;
    private String nomC2;
    private String nomC3;
    private String nomC4;
    private String nomC5;
    private String nomC6;
    private String nomC7;
    private String nomC8;
    private String nomC9;
    private String nomC10;
    private String nomC11;
    private String nomC12;
    private String nomC13;
    private String nomC14;
    private String nomC15;
    private String nomC16;
    private String nomC17;
    private String nomC18;
    private String nomC19;
    private String nomC20;
    // </DECLARAR_ATRIBUTOS>
    // <DECLARAR_PARAMETROS>
    // </DECLARAR_PARAMETROS>
    // <DECLARAR_LISTAS>
    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>
    // </DECLARAR_LISTAS_COMBO_GRANDE>

    private Map<String, Object> parametrosEntrada;

    @EJB
    private EjbPredialCeroRemote ejbPredialCero;

    /**
     * Creates a new instance of FacturadosacuerdosdetsControlador
     */
    @SuppressWarnings("unchecked")
    public FacturadosacuerdosdetsControlador()
    {
        super();
        compania = SessionUtil.getCompania();
        try
        {
            numFormulario = GeneralCodigoFormaEnum.FACTURADOSACUERDOSDETS_CONTROLADOR
                            .getCodigo();
            validarPermisos();
            parametrosEntrada = SessionUtil.getFlash();

            if (parametrosEntrada != null)
            {
                rid = (Map<String, Object>) parametrosEntrada.get("rid");
                codigoPredio = (String) parametrosEntrada.get("codigoPredio");
                codigoAcuerdo = (String) parametrosEntrada.get("codigoAcuerdo");
                acuerdoInactivo = (boolean) parametrosEntrada
                                .get("acuerdoInactivo");
            }
            else
            {
                SessionUtil.redireccionarMenuPermisos();
                return;
            }
            // <INI_ADICIONAL>
            // </INI_ADICIONAL>
        }
        catch (Exception ex)
        {
            Logger.getLogger(FacturadosacuerdosdetsControlador.class.getName())
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
        enumBase = GenericUrlEnum.IP_FACTURADOSACUERDOS;
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
        parametrosListado.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
        parametrosListado.put(GeneralParameterEnum.CODIGO.getName(),
                        codigoAcuerdo);
    }

    // <METODOS_CARGAR_LISTA>
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
        try
        {
            nomC1 = obtenerNombre(1);
            nomC2 = obtenerNombre(2);
            nomC3 = obtenerNombre(3);
            nomC4 = obtenerNombre(4);
            nomC5 = obtenerNombre(5);
            nomC6 = obtenerNombre(6);
            nomC7 = obtenerNombre(7);
            nomC8 = obtenerNombre(8);
            nomC9 = obtenerNombre(9);
            nomC10 = obtenerNombre(10);
            nomC11 = obtenerNombre(11);
            nomC12 = obtenerNombre(12);
            nomC13 = obtenerNombre(13);
            nomC14 = obtenerNombre(14);
            nomC15 = obtenerNombre(15);
            nomC16 = obtenerNombre(16);
            nomC17 = obtenerNombre(17);
            nomC18 = obtenerNombre(18);
            nomC19 = obtenerNombre(19);
            nomC20 = obtenerNombre(20);
        }
        catch (NamingException | SQLException | ClassNotFoundException
                        | InstantiationException | IllegalAccessException e)
        {
            Logger.getLogger(AsobancariaControlador.class.getName())
                            .log(Level.SEVERE, null, e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

        // </CODIGO_DESARROLLADO>
    }

    private String obtenerNombre(int numero)
                    throws IllegalAccessException, InstantiationException,
                    ClassNotFoundException, SQLException, NamingException
    {
        String encabezado = "";
        try
        {
            encabezado = ejbPredialCero.consultarEncabezadoDeColumna(compania,
                            numero);
        }
        catch (SystemException e)
        {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
        return encabezado;
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
        // Actualmente no se requiere remover ningun combo.
    }

    public void ejecutarrcCerrar()
    {
        String ruta = "/acuerdosusuarios.sysman";
        Direccionador direccionador = new Direccionador();
        direccionador.setRuta(ruta);
        direccionador.setParametros(parametrosEntrada);
        SessionUtil.redireccionar(direccionador);
    }

    @Override
    public void asignarValoresRegistro()
    {
        // Actualmente no se requiere asignar ningun valor.
    }

    // <SET_GET_ATRIBUTOS>
    public String getCodigoAcuerdo()
    {
        return codigoAcuerdo;
    }

    public void setCodigoAcuerdo(String codigoAcuerdo)
    {
        this.codigoAcuerdo = codigoAcuerdo;
    }

    public String getCodigoPredio()
    {
        return codigoPredio;
    }

    public void setCodigoPredio(String codigoPredio)
    {
        this.codigoPredio = codigoPredio;
    }

    public String getNomC1()
    {
        return nomC1;
    }

    public void setNomC1(String nomC1)
    {
        this.nomC1 = nomC1;
    }

    public String getNomC2()
    {
        return nomC2;
    }

    public void setNomC2(String nomC2)
    {
        this.nomC2 = nomC2;
    }

    public String getNomC3()
    {
        return nomC3;
    }

    public void setNomC3(String nomC3)
    {
        this.nomC3 = nomC3;
    }

    public String getNomC4()
    {
        return nomC4;
    }

    public void setNomC4(String nomC4)
    {
        this.nomC4 = nomC4;
    }

    public String getNomC5()
    {
        return nomC5;
    }

    public void setNomC5(String nomC5)
    {
        this.nomC5 = nomC5;
    }

    public String getNomC6()
    {
        return nomC6;
    }

    public void setNomC6(String nomC6)
    {
        this.nomC6 = nomC6;
    }

    public String getNomC7()
    {
        return nomC7;
    }

    public void setNomC7(String nomC7)
    {
        this.nomC7 = nomC7;
    }

    public String getNomC8()
    {
        return nomC8;
    }

    public void setNomC8(String nomC8)
    {
        this.nomC8 = nomC8;
    }

    public String getNomC9()
    {
        return nomC9;
    }

    public void setNomC9(String nomC9)
    {
        this.nomC9 = nomC9;
    }

    public String getNomC10()
    {
        return nomC10;
    }

    public void setNomC10(String nomC10)
    {
        this.nomC10 = nomC10;
    }

    public String getNomC11()
    {
        return nomC11;
    }

    public void setNomC11(String nomC11)
    {
        this.nomC11 = nomC11;
    }

    /**
     * @return the nomC12
     */
    public String getNomC12()
    {
        return nomC12;
    }

    /**
     * @param nomC12
     * the nomC12 to set
     */
    public void setNomC12(String nomC12)
    {
        this.nomC12 = nomC12;
    }

    public String getNomC13()
    {
        return nomC13;
    }

    public void setNomC13(String nomC13)
    {
        this.nomC13 = nomC13;
    }

    public String getNomC14()
    {
        return nomC14;
    }

    public void setNomC14(String nomC14)
    {
        this.nomC14 = nomC14;
    }

    public String getNomC15()
    {
        return nomC15;
    }

    public void setNomC15(String nomC15)
    {
        this.nomC15 = nomC15;
    }

    public String getNomC16()
    {
        return nomC16;
    }

    public void setNomC16(String nomC16)
    {
        this.nomC16 = nomC16;
    }

    public String getNomC17()
    {
        return nomC17;
    }

    public void setNomC17(String nomC17)
    {
        this.nomC17 = nomC17;
    }

    public String getNomC18()
    {
        return nomC18;
    }

    public void setNomC18(String nomC18)
    {
        this.nomC18 = nomC18;
    }

    public String getNomC19()
    {
        return nomC19;
    }

    public void setNomC19(String nomC19)
    {
        this.nomC19 = nomC19;
    }

    public String getNomC20()
    {
        return nomC20;
    }

    public void setNomC20(String nomC20)
    {
        this.nomC20 = nomC20;
    }

    public Map<String, Object> getRid()
    {
        return rid;
    }

    public void setRid(Map<String, Object> rid)
    {
        this.rid = rid;
    }

    public boolean isAcuerdoInactivo()
    {
        return acuerdoInactivo;
    }

    public void setAcuerdoInactivo(boolean acuerdoInactivo)
    {
        this.acuerdoInactivo = acuerdoInactivo;
    }

    // </SET_GET_ATRIBUTOS>
    // <SET_GET_PARAMETROS>
    // </SET_GET_PARAMETROS>
    // <SET_GET_LISTAS>
    // </SET_GET_LISTAS>
    // <SET_GET_LISTAS_COMBO_GRANDE>
    // </SET_GET_LISTAS_COMBO_GRANDE>
}
