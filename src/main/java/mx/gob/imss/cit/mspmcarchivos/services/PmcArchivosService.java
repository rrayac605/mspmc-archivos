/**
 * 
 */
package mx.gob.imss.cit.mspmcarchivos.services;

import mx.gob.imss.cit.mspmcarchivos.controller.BusquedaArchivoInput;
import mx.gob.imss.cit.mspmccommons.exception.BusinessException;
import mx.gob.imss.cit.mspmccommons.integration.model.ArchivoDTO;
import org.springframework.data.domain.Page;

/**
 * @author roberto.raya
 *
 */
public interface PmcArchivosService {
	
	
	Page<ArchivoDTO> getEstadoArchivo(BusquedaArchivoInput input) throws BusinessException; 

}
