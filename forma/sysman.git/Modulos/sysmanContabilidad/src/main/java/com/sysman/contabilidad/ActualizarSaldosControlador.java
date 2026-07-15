package com.sysman.contabilidad;

import com.sysman.beanbase.BeanBaseModal;
import com.sysman.contabilidad.ejb.EjbContabilidadCeroRemote;
import com.sysman.controladores.SessionUtil;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.util.SysmanFunciones;

import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

/**
 *
 * @author otorres
 * @version 1, 11/05/2016 18:05:43 -- Modificado por otorres
 * 
 * @author ybecerra
 * @version 2, 12/06/2017 Implementacion al llamado de
 * GeneralCodigoFormaEnum, para el codigo del formulario
 */
@ManagedBean
@ViewScoped
public class ActualizarSaldosControlador extends BeanBaseModal
{
    private final String compania;
    // <DECLARAR_ATRIBUTOS>
    private int anoFuente;
    private int anoDestino;
    // </DECLARAR_ATRIBUTOS>
    // <DECLARAR_PARAMETROS>
    // </DECLARAR_PARAMETROS>
    // <DECLARAR_LISTAS>
    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>
    // </DECLARAR_LISTAS_COMBO_GRANDE>

    @EJB
    private EjbContabilidadCeroRemote ejbContabilidadCero;

    /**
     * Creates a new instance of ActualizarSaldosControlador
     */
    public ActualizarSaldosControlador()
    {
        super();
        compania = SessionUtil.getCompania();
        try
        {
            numFormulario = GeneralCodigoFormaEnum.ACTUALIZAR_SALDOS_CONTROLADOR
                            .getCodigo();
            validarPermisos();

            // <INI_ADICIONAL>
            // </INI_ADICIONAL>
        }
        catch (Exception ex)
        {
            Logger.getLogger(ActualizarSaldosControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            SessionUtil.redireccionarMenuPermisos();
        }

    }

    @PostConstruct
    public void init()
    {
        // <CARGAR_LISTA>
        // </CARGAR_LISTA>
        // <CARGAR_LISTA_COMBO_GRANDE>
        // </CARGAR_LISTA_COMBO_GRANDE>
        abrirFormulario();
    }

    @Override
    public void abrirFormulario()
    {
        // <CODIGO_DESARROLLADO>
        anoFuente = SysmanFunciones
                        .ano(new Date());
        anoDestino = anoFuente + 1;
        /*
         * FR698-AL_ABRIR Private Sub Form_Open(Cancel As Integer)
         * DoCmd.Restore 'If par("ENTIDAD APLICA NIIF", Getcompany())
         * = "SI" Then ' Me!ENIIF.visible = True ' Me!NIIF.visible =
         * True 'End If End Sub
         *//*
           * FR698-AL_ABRIR Private Sub Form_Load() 'lblAviso.Visible
           * = False End Sub
           */
        // </CODIGO_DESARROLLADO>
    }

    // <METODOS_CARGAR_LISTA>
    // </METODOS_CARGAR_LISTA>
    // <METODOS_BOTONES>
    public void oprimirIniciar()
    {
        // <CODIGO_DESARROLLADO>

        if (anoDestino == (anoFuente + 1))
        {
            try
            {
                ejbContabilidadCero.prepararSaldosAnoSiguiente(compania,
                                anoDestino);
                JsfUtil.agregarMensajeInformativo(
                                idioma.getString("MSM_PROCESO_EJECUTADO"));
            }
            catch (SystemException e)
            {
                Logger.getLogger(PrepararcontaControlador.class.getName())
                                .log(Level.SEVERE, null, e);
                JsfUtil.agregarMensajeError(e.getMessage());
            }
        }
        else
        {
            JsfUtil.agregarMensajeError(idioma.getString("TB_TB565"));

        }

        // </CODIGO_DESARROLLADO>
    }

    // </METODOS_BOTONES>
    // <METODOS_CAMBIAR>
    // </METODOS_CAMBIAR>
    // <METODOS_COMBOS_GRANDES>
    // </METODOS_COMBOS_GRANDES>

    // <SET_GET_ATRIBUTOS>
    public int getAnoFuente()
    {
        return anoFuente;
    }

    public void setAnoFuente(int anoFuente)
    {
        this.anoFuente = anoFuente;
    }

    public int getAnoDestino()
    {
        return anoDestino;
    }

    public void setAnoDestino(int anoDestino)
    {
        this.anoDestino = anoDestino;
    }

    // </SET_GET_ATRIBUTOS>
    // <SET_GET_PARAMETROS>
    // </SET_GET_PARAMETROS>
    // <SET_GET_LISTAS>
    // </SET_GET_LISTAS>
    // <SET_GET_LISTAS_COMBO_GRANDE>
    // </SET_GET_LISTAS_COMBO_GRANDE>
}