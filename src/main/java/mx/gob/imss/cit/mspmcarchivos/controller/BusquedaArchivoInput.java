/**
 * 
 */
package mx.gob.imss.cit.mspmcarchivos.controller;

import javax.validation.constraints.NotNull;

import lombok.Getter;
import lombok.Setter;

/**
 * @author roberto.raya
 *
 */
public class BusquedaArchivoInput {
	
	
	@Setter
	@Getter
	@NotNull
	private String fromMonth;
	
	@Setter
	@Getter
	@NotNull
	private String fromYear;
	
	@Setter
	@Getter
	@NotNull
	private String toMonth;
	
	@Setter
	@Getter
	@NotNull
	private String toYear;
	
	@Setter
	@Getter
	@NotNull
	private String statusFile;
	
	@Setter
	@Getter
	private Integer cveDelegation;
	
	@Setter
	@Getter
	private Integer cveSubdelegation;
	
	@Getter
	@Setter
	private Integer page;
	
	@Getter
	@Setter
	private String token;
	
	@Setter
	@Getter
	private String[] roles;

}
