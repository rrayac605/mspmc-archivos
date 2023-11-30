/**
 * 
 */
package mx.gob.imss.cit.mspmcarchivos.services.impl;

import mx.gob.imss.cit.mspmcarchivos.controller.BusquedaArchivoInput;
import mx.gob.imss.cit.mspmcarchivos.integration.dao.MctArchivoRepository;
import mx.gob.imss.cit.mspmcarchivos.integration.dao.ParametroRepository;
import mx.gob.imss.cit.mspmcarchivos.services.PmcArchivosService;
import mx.gob.imss.cit.mspmccommons.exception.BusinessException;
import mx.gob.imss.cit.mspmccommons.integration.model.ArchivoDTO;
import mx.gob.imss.cit.mspmccommons.integration.model.ParametroDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * @author roberto.raya
 *
 */
@Service("pmcArchivosService")
public class PmcArchivosServiceImpl implements PmcArchivosService{
	
	@Autowired
	private MctArchivoRepository archivoRepository;
	
	@Autowired
	private ParametroRepository parametroRepository;
	
	private final Logger logger = LoggerFactory.getLogger(getClass());

	@Override
	public Page<ArchivoDTO> getEstadoArchivo(BusquedaArchivoInput input) throws BusinessException {
		logger.info("PmcArchivosServiceImpl:getEstadoArchivo");
		Optional<ParametroDTO> elementsPaginator = parametroRepository.findOneByCve("elementsPaginator");
		Pageable pageable = PageRequest.of(input.getPage(), Integer.parseInt(elementsPaginator.isPresent() ?elementsPaginator.get().getDesParametro():"1"));
		pageable.getSort();
		return archivoRepository.searchFiles(pageable, input);
	}

}
