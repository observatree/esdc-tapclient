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

import java.io.File;

import esac.archive.gacs.sl.services.nameresolution.actions.handlers.tgas.TgasTargetResolution;
import esavo.sl.services.nameresolution.TargetResolutionManager;
import esavo.sl.tap.actions.EsacAbstractTapServiceConnection;
import esavo.sl.tap.actions.EsacTapService;
import esavo.tap.TAPException;
import esavo.tap.TAPFactory;
import esavo.uws.UwsException;

/**
 * <p>The framework will create this class as a service by reflection.
 * <pre><tt>
 * esavo.sl.service.class=esac.archive.gacs.sl.tap.actions.GacsTapService
 * </tt></pre>
 * <p>The framework will create this class as a service by reflection.
 * 
 * @author juan.carlos.segovia@sciops.esa.int
 *
 */
public class GacsTapService extends EsacAbstractTapServiceConnection implements EsacTapService {

	public static final String TAP_STAT_GRAPHS_DIRECTORY = "esac.archive.gacs.sl.tap.actions.statGraphsDirectory";

	public GacsTapService(String appid) throws UwsException, TAPException {
		super(appid);
	}

	@Override
	protected void initService() throws UwsException, TAPException {
		File storageDir = getStorageDir();
		//In case you want to use your own factory:
		//TAPFactory factory = new MyProjectTapFactory();
		TAPFactory factory = new GacsServiceFactory(this, getAppId(), storageDir, getConfiguration());
		initService(factory);

		TargetResolutionManager manager = TargetResolutionManager.getInstance();
		manager.addHandler(new TgasTargetResolution());
	}

}
