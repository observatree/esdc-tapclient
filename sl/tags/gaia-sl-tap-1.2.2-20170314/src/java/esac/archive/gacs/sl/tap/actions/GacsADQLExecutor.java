/*******************************************************************************
 * Copyright (C) 2017 European Space Agency
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/
package esac.archive.gacs.sl.tap.actions;

/*
 * This file is part of TAPLibrary.
 * 
 * TAPLibrary is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * TAPLibrary is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with TAPLibrary.  If not, see <http://www.gnu.org/licenses/>.
 * 
 * Copyright 2012 - UDS/Centre de Données astronomiques de Strasbourg (CDS)
 */

import esavo.uws.UwsException;
import esavo.uws.executor.UwsExecutor;
import esavo.uws.jobs.UwsJob;
import esavo.sl.tap.actions.EsacTapService;
import esavo.sl.tap.actions.EsdcADQLExecutor;
import esavo.tap.log.TAPLog;
import esavo.tap.parameters.TAPParameters;

public class GacsADQLExecutor extends EsdcADQLExecutor implements UwsExecutor {
	
	public static final String CAPTION = "If you use public Gaia DR1 data in your paper, "
			+ "please take note of our guide on how to acknowledge and cite Gaia DR1: "
			+ "http://gaia.esac.esa.int/documentation/GDR1/Miscellaneous/sec_credit_and_citation_instructions.html";
	
	
	public GacsADQLExecutor(final EsacTapService service, String appid, TAPLog logger){
		super(service, appid, logger);
	}
	
	@Override
	public Object execute(UwsJob job) throws InterruptedException, UwsException {
		job.getParameters().setParameter(TAPParameters.PARAM_CAPTION, CAPTION);
		return super.execute(job);
	}
	
}
