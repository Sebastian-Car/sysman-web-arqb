
package com.sysman.plusvalia.ejb;

import com.sysman.exception.SystemException;

import java.math.BigDecimal;
import java.util.Date;

import javax.ejb.Remote;

@Remote
public interface EjbPlusvaliaCeroGeneralRemote {

    BigDecimal prorrateoGeneral(
        long acuerdo,
        int cuotaFinal,
        String usuario)
                    throws SystemException;

    void insertConfigurarPago(
        String compania,
        Date fecha,
        String banco,
        String paquete,
        int ncupones,
        String valorRep,
        String referencia,
        int aplicacion,
        String usuario)
                    throws SystemException;

}
