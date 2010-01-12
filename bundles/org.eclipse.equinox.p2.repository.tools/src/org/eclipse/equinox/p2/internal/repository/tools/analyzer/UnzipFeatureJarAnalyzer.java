/******************************************************************************* 
* Copyright (c) 2009 EclipseSource and others. All rights reserved. This
* program and the accompanying materials are made available under the terms of
* the Eclipse Public License v1.0 which accompanies this distribution, and is
* available at http://www.eclipse.org/legal/epl-v10.html
*
* Contributors:
*   EclipseSource - initial API and implementation
******************************************************************************/
package org.eclipse.equinox.p2.internal.repository.tools.analyzer;

import org.eclipse.equinox.p2.metadata.ITouchpointData;
import org.eclipse.equinox.p2.metadata.ITouchpointInstruction;

import java.util.List;
import org.eclipse.equinox.p2.metadata.IInstallableUnit;
import org.eclipse.equinox.p2.repository.metadata.IMetadataRepository;
import org.eclipse.equinox.p2.repository.tools.analyzer.IUAnalyzer;

/**
 * This service checks that each Feature Jar IU has the unzip touchpoint
 */
public class UnzipFeatureJarAnalyzer extends IUAnalyzer {

	public void analyzeIU(IInstallableUnit iu) {
		if (iu.getId().indexOf("feature.jar") > -1) {
			List<ITouchpointData> touchpointData = iu.getTouchpointData();
			if (touchpointData.size() == 0) {
				error(iu, "[ERROR] No unzip touchpoint for: " + iu.getId());
			} else {
				boolean found = false;
				for (int i = 0; i < touchpointData.size(); i++) {
					ITouchpointInstruction instruction = touchpointData.get(i).getInstruction("zipped");
					if (instruction.getBody().equals("true"))
						found = true;
				}
				if (!found) {
					error(iu, "[ERROR] No unzip touchpoint for: " + iu.getId());
				}
			}
		}

	}

	public void preAnalysis(IMetadataRepository repository) {
		// Do nothing
	}

}
