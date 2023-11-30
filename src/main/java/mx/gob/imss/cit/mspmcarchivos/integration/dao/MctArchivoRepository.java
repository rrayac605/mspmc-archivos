/**
 * 
 */
package mx.gob.imss.cit.mspmcarchivos.integration.dao;

import mx.gob.imss.cit.mspmcarchivos.controller.BusquedaArchivoInput;
import mx.gob.imss.cit.mspmccommons.exception.BusinessException;
import mx.gob.imss.cit.mspmccommons.integration.model.ArchivoDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * @author roberto.raya
 *
 */
public interface MctArchivoRepository {
	
	
	Page<ArchivoDTO> searchFiles(Pageable pageable, BusquedaArchivoInput input) throws BusinessException;

}
