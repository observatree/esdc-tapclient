/*******************************************************************************
 * Copyright (c) 2016 European Space Agency.
 ******************************************************************************/
package esac.archive.gacs.cl.tapclient.exception;

import java.io.IOException;

public class TapPlusException extends IOException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7839736740601102962L;

	public TapPlusException(String responseMessage) {
		super(responseMessage);
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
	}

}
