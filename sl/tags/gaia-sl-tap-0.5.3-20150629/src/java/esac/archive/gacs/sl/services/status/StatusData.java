package esac.archive.gacs.sl.services.status;

public interface StatusData {
	
	/**
	 * Sets the information associated to this status type.
	 * @param info information associated to this status type.
	 */
	public void setData(String data);
	
	/**
	 * Returns the information associated to this status type.
	 * @return the information associated to this status type.
	 */
	public String getData();
	
	/**
	 * Returns the status type;
	 * @return the status type;
	 */
	public TaskType getType();

}
