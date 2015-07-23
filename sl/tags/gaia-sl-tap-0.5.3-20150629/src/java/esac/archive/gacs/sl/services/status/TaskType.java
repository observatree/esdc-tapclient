package esac.archive.gacs.sl.services.status;

/**
 * Task types.<br/>
 * These task types specifies the current task step.
 * @author juan.carlos.segovia@sciops.esa.int
 *
 */
public enum TaskType {

	/**
	 * Starts user table upload procedure
	 */
	UPLOAD,
	/**
	 * Parse user table (upload related)
	 */
	PARSE,
	/**
	 * User table ingestion (upload related)
	 */
	INGESTION,
	/**
	 * User table edit
	 */
	TABLE_EDIT,

	
	/**
	 * Cross match creation. Includes:
	 * <ul>
	 * <li>Selection of largest table</li>
	 * <li>Creation of cross match table</li>
	 * <li>Computation of matching keys</li>
	 * <li>Computation of estimator indexes</li>
	 * </ul>
	 */
	XMATCH_CREATE,
	/**
	 * Cross match table analysis.
	 */
	XMATCH_ANALYZE,
	/**
	 * Tap update related to cross match table creation.
	 */
	XMATCH_TAP_UPDATE
	

}
