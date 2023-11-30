package mx.gob.imss.cit.mspmcarchivos.integration.dao.impl;

import mx.gob.imss.cit.mspmcarchivos.controller.BusquedaArchivoInput;
import mx.gob.imss.cit.mspmcarchivos.integration.dao.MctArchivoRepository;
import mx.gob.imss.cit.mspmccommons.exception.BusinessException;
import mx.gob.imss.cit.mspmccommons.integration.model.ArchivoDTO;
import mx.gob.imss.cit.mspmccommons.integration.model.DetalleRegistroDTO;
import mx.gob.imss.cit.mspmccommons.integration.model.MctArchivo;
import mx.gob.imss.cit.mspmccommons.utils.AggregationUtils;
import mx.gob.imss.cit.mspmccommons.utils.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.aggregation.*;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Repository
public class MctArchivoRepositoryImpl implements MctArchivoRepository{
	
	@Autowired
	private MongoOperations mongoOperations;
	
	private final Logger logger = LoggerFactory.getLogger(getClass());

	private static final String FEC_PROCESO_CARGA = "fecProcesoCarga";

	public List<MctArchivo> getAllListArchivos(BusquedaArchivoInput input) throws BusinessException {
		
		//Se calculan las fechas inicio y fin para la consulta
		Date fecProcesoIni = DateUtils.calculateBeginDate(input.getFromYear(), input.getFromMonth(), null);
		Date fecProcesoFin = DateUtils.calculateEndDate(input.getToYear(), input.getToMonth(), null);
		Criteria cFecProcesoCarga = null;
		Query query = new Query();

		if (fecProcesoIni != null && fecProcesoFin != null) {
			cFecProcesoCarga = new Criteria().andOperator(Criteria.where(FEC_PROCESO_CARGA).gt(fecProcesoIni),
					Criteria.where(FEC_PROCESO_CARGA).lte(fecProcesoFin));
			query.addCriteria(cFecProcesoCarga);
		}

		if (input.getStatusFile() != null) {
			query.addCriteria(Criteria.where("cveEstadoArchivo").is(input.getStatusFile()));
		}
		
		logger.info("cveDelegacion recibida: " + input.getCveDelegation());
		logger.info("cveSubdelegacion recibida: " + input.getCveSubdelegation());
		if(input.getCveDelegation() != null && input.getCveDelegation() > 0){
  			Criteria crit1 = Criteria.where("detalleRegistroDTO.aseguradoDTO.cveDelegacionNss").is(input.getCveDelegation());
			Criteria crit2 = Criteria.where("detalleRegistroDTO.patronDTO.cveDelRegPatronal").is(input.getCveDelegation());
			query.addCriteria(new Criteria().orOperator(crit1, crit2));
		}
		if ((input.getCveDelegation() != null && input.getCveDelegation() > 0) &&
				(input.getCveSubdelegation() == null || input.getCveSubdelegation() == 0)) {
			Criteria crit1 = Criteria.where("detalleRegistroDTO.patronDTO.cveSubDelRegPatronal").is(input.getCveSubdelegation());
			query.addCriteria(Criteria.where("detalleRegistroDTO.aseguradoDTO.cveSubdelNss").is(input.getCveSubdelegation()).orOperator(crit1));
		}
			
		logger.info("cveDelegacion recibida: "+input.getCveDelegation());
		logger.info("cveSubdelegacion recibida: "+input.getCveSubdelegation());
		logger.info("Query ejecutado: {}", query);
		return mongoOperations.find(query, MctArchivo.class);
		
	}
	
	
	@Override
	public Page<ArchivoDTO> searchFiles(Pageable pageable, BusquedaArchivoInput input) throws BusinessException{
		//Se calculan las fechas inicio y fin para la consulta
		Date fecProcesoIni = DateUtils.calculateBeginDate(input.getFromYear(), input.getFromMonth(), null);
		Date fecProcesoFin = DateUtils.calculateEndDate(input.getToYear(), input.getToMonth(), null);
		Criteria cFecProcesoCarga = null;

		if (fecProcesoIni != null && fecProcesoFin != null) {
			cFecProcesoCarga = new Criteria().andOperator(Criteria.where(FEC_PROCESO_CARGA).gt(fecProcesoIni),
					Criteria.where(FEC_PROCESO_CARGA).lte(fecProcesoFin));
		}
  		//Criterios de busqueda Archivo
  		Criteria cDel = null;
  		Criteria cDelAndSubDel = null;
  		Criteria cCveEstadoArchivo = null;

  		if (input.getStatusFile() != null && !input.getStatusFile().isEmpty() && !input.getStatusFile().equals("-1")) {
			cCveEstadoArchivo = Criteria.where("cveEstadoArchivo").is(input.getStatusFile());
		}
  		
  		if (input.getCveDelegation() != null && input.getCveDelegation() > 0
				&& input.getCveSubdelegation() != null && input.getCveSubdelegation() > 0) {
  			Criteria delAsegurado = Criteria.where("aseguradoDTO.cveDelegacionNss").is(Integer.valueOf(input.getCveDelegation()));
  			Criteria subdelAsegurado = Criteria.where("aseguradoDTO.cveSubdelNss").is(Integer.valueOf(input.getCveSubdelegation()));
  			Criteria delPatron = Criteria.where("patronDTO.cveDelRegPatronal").is(Integer.valueOf(input.getCveDelegation()));
  			Criteria subdelPatron = Criteria.where("patronDTO.cveSubDelRegPatronal").is(Integer.valueOf(input.getCveSubdelegation()));
			cDelAndSubDel = new Criteria().orOperator(
					new Criteria().andOperator(delAsegurado, subdelAsegurado),
					new Criteria().andOperator(delPatron, subdelPatron));
		} else if ((input.getCveDelegation() != null && input.getCveDelegation() > 0) &&
				(input.getCveSubdelegation() == null || input.getCveSubdelegation() == 0)) {
			Criteria delAsegurado = Criteria.where("aseguradoDTO.cveDelegacionNss").is(Integer.valueOf(input.getCveDelegation()));
			Criteria delPatron = Criteria.where("patronDTO.cveDelRegPatronal").is(Integer.valueOf(input.getCveDelegation()));
			cDel = new Criteria().orOperator(delAsegurado, delPatron);
		}

		List<ArchivoDTO> listArchivos;
		AggregationResults<ArchivoDTO> aggregationResult;

		logger.info("--------------Query de agregacion-------------------");
		// Si cumple esta condicion voy a buscar a archivo
		if (input.getCveDelegation() == null || input.getCveDelegation() <= 0) {
			TypedAggregation<ArchivoDTO> aggregation = buildFileAggregation(cFecProcesoCarga, cCveEstadoArchivo);

			logger.info("agregacion: {}", aggregation);
			aggregationResult = mongoOperations.aggregate(aggregation, ArchivoDTO.class);
		} else {
			TypedAggregation<ArchivoDTO> aggregation = buildMovementAggregation(cFecProcesoCarga, cCveEstadoArchivo,
					cDelAndSubDel, cDel);

			logger.info("agregacion: {}", aggregation);
			aggregationResult = mongoOperations.aggregate(aggregation, DetalleRegistroDTO.class, ArchivoDTO.class);
		}
		logger.info("----------------------------------------------------");
		listArchivos = aggregationResult.getMappedResults();
		return new PageImpl<>(listArchivos , pageable, listArchivos.size());
	}

	private TypedAggregation<ArchivoDTO> buildFileAggregation(Criteria cFecProcesoCarga, Criteria cCveEstadoArchivo) {
		SortOperation sort = Aggregation.sort(Direction.ASC, FEC_PROCESO_CARGA);
		// Las operaciones deben ir en orden en la lista
		List<AggregationOperation> aggregationOperationList = Arrays.asList(
				AggregationUtils.validateMatchOp(cFecProcesoCarga),
				AggregationUtils.validateMatchOp(cCveEstadoArchivo),
				sort
		);
		aggregationOperationList = aggregationOperationList.stream()
				.filter(Objects::nonNull)
				.collect(Collectors.toList());
		return Aggregation.newAggregation(ArchivoDTO.class, aggregationOperationList);
	}

	private TypedAggregation<ArchivoDTO> buildMovementAggregation(Criteria cFecProcesoCarga, Criteria cCveEstadoArchivo,
																  Criteria cDelAndSubDel, Criteria cDel) {
		GroupOperation group = Aggregation.group("identificadorArchivo");
		String archivoDTO = "archivoDTO";
		LookupOperation lookup = Aggregation.lookup("MCT_ARCHIVO", "_id", "_id", archivoDTO);
		UnwindOperation unwind = Aggregation.unwind(archivoDTO);
		ReplaceRootOperation replaceRoot = Aggregation.replaceRoot(archivoDTO);
		SortOperation sort = Aggregation.sort(Direction.ASC, FEC_PROCESO_CARGA);
		// Las operaciones deben ir en orden en la lista
		List<AggregationOperation> aggregationOperationList = Arrays.asList(
				AggregationUtils.validateMatchOp(cDel),
				AggregationUtils.validateMatchOp(cDelAndSubDel),
				group,
				lookup,
				unwind,
				replaceRoot,
				AggregationUtils.validateMatchOp(cFecProcesoCarga),
				AggregationUtils.validateMatchOp(cCveEstadoArchivo),
				sort
		);
		aggregationOperationList = aggregationOperationList.stream()
				.filter(Objects::nonNull)
				.collect(Collectors.toList());
		return Aggregation.newAggregation(ArchivoDTO.class, aggregationOperationList);
	}

}

