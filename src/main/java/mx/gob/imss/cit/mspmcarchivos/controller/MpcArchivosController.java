package mx.gob.imss.cit.mspmcarchivos.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.annotations.Api;
import mx.gob.imss.cit.mspmcarchivos.services.PmcArchivosService;
import mx.gob.imss.cit.mspmccommons.dto.ErrorResponse;
import mx.gob.imss.cit.mspmccommons.exception.BusinessException;
import mx.gob.imss.cit.mspmccommons.integration.model.ArchivoDTO;


@RestController
@RequestMapping("/mspmcarchivos/v1")
@Api(value = "Archivos PMC", tags = { "Archivos PMC Rest" })
public class MpcArchivosController {

	private final Logger logger = LoggerFactory.getLogger(getClass());
	
	@Autowired
	private PmcArchivosService pmcArchivosService;
	
    @RequestMapping("/health/ready")
    @ResponseStatus(HttpStatus.OK)
    public void ready() {
    	// Service to validate if the server is ready
	}

    @RequestMapping("/health/live")
    @ResponseStatus(HttpStatus.OK)
    public void live() {
		// Service to validate if the server is alive
	}
    
    
    @CrossOrigin(origins = "*", allowedHeaders="*")
    @PostMapping(value = "/archivos", produces=MediaType.APPLICATION_JSON_VALUE)
	public Object buscarEstadoArchivo(@RequestBody BusquedaArchivoInput input,
			@RequestHeader(value = "Authorization") String token) throws BusinessException{
    	Object resultado = null;
    	try {
    		logger.info("MpcArchivosController:buscarEstadoArchivo:try");
    		Page<ArchivoDTO> listado =  pmcArchivosService.getEstadoArchivo(input);
    		resultado = new ResponseEntity<Object>(listado, HttpStatus.OK);
    		logger.info("MpcArchivosController:buscarEstadoArchivo:returnOk");
		} catch (BusinessException be) {
			logger.info("MpcArchivosController:buscarEstadoArchivo:catch");
        	ErrorResponse errorResponse = be.getErrorResponse();
        	
        	int numberHTTPDesired = Integer.parseInt(errorResponse.getCode());
  
        	resultado= new ResponseEntity<ErrorResponse>(errorResponse, HttpStatus.valueOf(numberHTTPDesired));
        	logger.info("MpcArchivosController:buscarEstadoArchivo:numberHTTPDesired");
 
        }
		
    	logger.info("MpcArchivosController:buscarEstadoArchivo:FinalReturn");
		 return resultado;
	}
}

    