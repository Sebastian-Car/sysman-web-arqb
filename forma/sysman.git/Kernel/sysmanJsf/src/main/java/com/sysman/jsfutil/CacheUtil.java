/**
 * Clase: CacheUtil.java
 *
 */

package com.sysman.jsfutil;

import com.sysman.cache.RepositorioCacheServicio;
import com.sysman.cache.enums.UrlServiceCache;
import com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException;
import com.sysman.exception.SystemException;

/**
 * @version 1.0, 12 de ago. de 2016
 * @author cmanrique
 *
 */

public class CacheUtil {

    private CacheUtil() {
    }

    public static String[] getLlaveServicio(UrlServiceCache esquema,
        String tabla) throws SysmanException {
        RepositorioCacheServicio repositorioCache = RepositorioCacheServicio
                        .getInstance();
        try {
            return repositorioCache.getLlave(esquema, tabla);
        }
        catch (SystemException e) {
            throw new SysmanException(e, e.getMessage());
        }

    }

}
