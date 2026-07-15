package com.sysman.almacen;

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

import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.event.RowEditEvent;

/**
 *
 * @author vmolano
 * @version 1, 16/02/2016
 * 
 * @author jlramirez
 * @version 2, 27/04/2017, Se realizo factoring y manejo de EJBs
 * 
 * @author eamaya
 * @version 3.0, 12/06/2017 Cambio c�digo formulario
 */
@ManagedBean
@ViewScoped
public class FrmarchivosvariosControlador extends BeanBaseContinuoAcmeImpl {

    private final String compania;
    private String tituloActual;
    private final String menuActual;
    private String origenCodigo;
    private String origenDescripcion;
    private int numDigitos;
    private String condConsecutivo;
    private boolean insert;
    private String tablaUsada;
    @EJB
    private EjbSysmanUtilRemote sysmanUtil;

    /**
     * Creates a new instance of FrmarchivosvariosControlador
     */
    public FrmarchivosvariosControlador() {
        super();
        compania = SessionUtil.getCompania();
        insert = false;
        try {
            numFormulario = GeneralCodigoFormaEnum.FRMARCHIVOSVARIOS_CONTROLADOR
                            .getCodigo();

            validarPermisos();
        }
        catch (SysmanException ex) {
            Logger.getLogger(FrmarchivosvariosControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            JsfUtil.agregarMensajeError(ex.getMessage());
            SessionUtil.redireccionarMenuPermisos();
        }

        menuActual = SessionUtil.getMenuActual();
        numDigitos = 3;
        condConsecutivo = "compania=''" + compania + "''";

        evaluarMenuActual();
    }

    public void evaluarMenuActual() {
        if (("10070101").equals(menuActual)) {
            enumBase = GenericUrlEnum.MODALIDAD;
            tituloActual = idioma.getString("TB_TB3102");
            tablaUsada = "MODALIDAD";
            origenCodigo = "CODIGO_MODALIDAD";
            origenDescripcion = GeneralParameterEnum.DESCRIPCION.getName();
            condConsecutivo += " AND " + origenCodigo + " NOT IN(''999'')";
        }
        else if (("10070102").equals(menuActual)) {
            enumBase = GenericUrlEnum.NOTARIAESCRITURA;
            tituloActual = idioma.getString("TB_TB3103");
            numDigitos = 2;
            tablaUsada = "NOTARIA_ESCRITURA";
            origenCodigo = "CODIGO_NOTARIA";
            origenDescripcion = GeneralParameterEnum.NOMBRE.getName();
            condConsecutivo += " AND " + origenCodigo + " NOT IN(''99'')";
        }
        else if (("10070103").equals(menuActual)) {
            enumBase = GenericUrlEnum.SECTORINMUEBLE;
            tituloActual = idioma.getString("TB_TB3104");
            tablaUsada = "SECTOR_INMUEBLE";
            origenCodigo = "CODIGO_SECTOR";
            origenDescripcion = GeneralParameterEnum.DESCRIPCION.getName();
        }
        else if (("10070105").equals(menuActual)) {
            enumBase = GenericUrlEnum.SERVICIOSPUBLICOS;
            tituloActual = idioma.getString("TG_SERVICIOS_PUBLICOS");
            tablaUsada = "SERVICIOS_PUBLICOS";
            origenCodigo = "CODIGO_SERVICIO";
            origenDescripcion = GeneralParameterEnum.NOMBRE.getName();
        }
        else if (("10070106").equals(menuActual)) {
            enumBase = GenericUrlEnum.BIUBICACION;
            tituloActual = idioma.getString("TG_UBICACION");
            numDigitos = 4;
            tablaUsada = "BI_UBICACION";
            origenCodigo = GeneralParameterEnum.CODIGO.getName();
            origenDescripcion = GeneralParameterEnum.NOMBRE.getName();
        }
        else if (("10070107").equals(menuActual)) {
            enumBase = GenericUrlEnum.USOS;
            tituloActual = idioma.getString("TG_USOS");
            tablaUsada = "USOS";
            origenCodigo = "CODIGO_USO";
            origenDescripcion = GeneralParameterEnum.DESCRIPCION.getName();
        }
        else if (("1007010401").equals(menuActual)) {
            enumBase = GenericUrlEnum.TIPOVIA;
            tituloActual = idioma.getString("TB_TB3108");
            numDigitos = 2;
            tablaUsada = "TIPO_VIA";
            origenCodigo = "CODIGO_TIPO";
            origenDescripcion = GeneralParameterEnum.DESCRIPCION.getName();
        }
        else if (("1007010402").equals(menuActual)) {
            enumBase = GenericUrlEnum.ESTADOVIA;
            tituloActual = idioma.getString("TB_TB3109");
            numDigitos = 2;
            tablaUsada = "ESTADO_VIA";
            origenCodigo = "CODIGO_ESTADO";
            origenDescripcion = GeneralParameterEnum.DESCRIPCION.getName();
        }
        else if (("1007010403").equals(menuActual)) {
            enumBase = GenericUrlEnum.ITEMS;
            tituloActual = idioma.getString("TB_TB3110");
            tablaUsada = "ITEMS";
            origenCodigo = "CODIGO_ITEM";
            origenDescripcion = GeneralParameterEnum.DESCRIPCION.getName();
        }
    }

    public String getCondConsecutivo() {
        return condConsecutivo;
    }

    public void setCondConsecutivo(String condConsecutivo) {
        this.condConsecutivo = condConsecutivo;
    }

    public int getNumDigitos() {
        return numDigitos;
    }

    public void setNumDigitos(int numDigitos) {
        this.numDigitos = numDigitos;
    }

    public String getOrigenCodigo() {
        return origenCodigo;
    }

    public void setOrigenCodigo(String origenCodigo) {
        this.origenCodigo = origenCodigo;
    }

    public String getOrigenDescripcion() {
        return origenDescripcion;
    }

    public void setOrigenDescripcion(String origenDescripcion) {
        this.origenDescripcion = origenDescripcion;
    }

    public String getTituloActual() {
        return tituloActual;
    }

    public void setTituloActual(String tituloActual) {
        this.tituloActual = tituloActual;
    }

    public boolean isInsert() {
        return insert;
    }

    public void setInsert(boolean insert) {
        this.insert = insert;
    }

    @PostConstruct
    public void inicializar() {
        buscarLlave();
        reasignarOrigen();
        registro = new Registro(new HashMap<String, Object>());
        abrirFormulario();
    }

    @Override
    public void abrirFormulario() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    @Override
    public void cancelarEdicion(RowEditEvent event) {
        listaInicial.load();
    }

    @Override
    public void reasignarOrigen() {
        buscarUrls();
        parametrosListado.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
    }

    @Override
    public void removerCombos() {
        registro.getCampos().remove(GeneralParameterEnum.COMPANIA.getName());
    }

    @Override
    public boolean insertarAntes() {
        registro.getCampos().put("COMPANIA", compania);
        try {
            Long con = sysmanUtil.generarSiguienteConsecutivo(tablaUsada,
                            condConsecutivo, origenCodigo);
            String consecutivo = SysmanFunciones.padl("" + con, numDigitos,
                            "0");
            registro.getCampos().put(origenCodigo, consecutivo);
            insert = true;
            // Debido a que todos los formularios tienen nombres de
            // campos
            // diferentes se tiene que cambiar el key del hashMAP
            // antes de
            // guardar.
            if (!(GeneralParameterEnum.DESCRIPCION.getName())
                            .equals(origenDescripcion)) {
                registro.getCampos().put(origenDescripcion,
                                registro.getCampos()
                                                .get(GeneralParameterEnum.DESCRIPCION
                                                                .getName()));
                registro.getCampos().remove(
                                GeneralParameterEnum.DESCRIPCION.getName());
            }
            return true;
        }
        catch (SystemException ex) {
            Logger.getLogger(FrmarchivosvariosControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
        }

        return true;
    }

    @Override
    public boolean insertarDespues() {
        return true;
    }

    @Override
    public boolean actualizarAntes() {
        // Debido a que todos los formularios tienen nombres de campos
        // diferentes se tiene que cambiar el key del hashMAP antes de
        // guardar.
        if (!insert) {
            if (!(GeneralParameterEnum.CODIGO.getName()).equals(origenCodigo)) {

                registro.getLlave().put("KEY_" + origenCodigo,
                                registro.getCampos()
                                                .get(GeneralParameterEnum.CODIGO
                                                                .getName()));

            }
            if (!(GeneralParameterEnum.DESCRIPCION.getName())
                            .equals(origenDescripcion)) {
                registro.getCampos().put(origenDescripcion,
                                registro.getCampos()
                                                .get(GeneralParameterEnum.DESCRIPCION
                                                                .getName()));
                registro.getCampos().remove(
                                GeneralParameterEnum.DESCRIPCION.getName());
            }
            registro.getCampos()
                            .remove(GeneralParameterEnum.CODIGO.getName());
        }

        insert = false;
        return true;
    }

    @Override
    public boolean actualizarDespues() {
        return true;
    }

    @Override
    public boolean eliminarAntes() {
        registro.getLlave().put("KEY_" + origenCodigo,
                        registro.getCampos()
                                        .get(GeneralParameterEnum.CODIGO
                                                        .getName()));
        return true;
    }

    @Override
    public boolean eliminarDespues() {
        return true;
    }

    @Override
    public void asignarValoresRegistro() {
        // NO SE IMPLEMENTA
    }
}
