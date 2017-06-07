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
package esac.archive.gacs.sl.services.nameresolution;

import esac.archive.gacs.sl.services.nameresolution.actions.handlers.tgas.TgasTargetResolution;
import esavo.sl.services.nameresolution.TargetResolutionManager;

public class GaiaTargetResolutionManager {
	
	private static boolean _loaded = false;
	
	public static synchronized TargetResolutionManager getInstance(){
		TargetResolutionManager manager = TargetResolutionManager.getInstance();
		if(!_loaded){
			manager.addHandler(new TgasTargetResolution());
		}
		return manager;
	}

}
