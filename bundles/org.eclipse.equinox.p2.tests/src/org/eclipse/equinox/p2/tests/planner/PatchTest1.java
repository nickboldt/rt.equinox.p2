/*******************************************************************************
 *  Copyright (c) 2008, 2009 IBM Corporation and others.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 * 
 *  Contributors:
 *      IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.equinox.p2.tests.planner;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.equinox.internal.p2.metadata.IRequiredCapability;
import org.eclipse.equinox.internal.provisional.p2.director.IPlanner;
import org.eclipse.equinox.internal.provisional.p2.director.ProfileChangeRequest;
import org.eclipse.equinox.internal.provisional.p2.metadata.*;
import org.eclipse.equinox.p2.engine.*;
import org.eclipse.equinox.p2.metadata.*;
import org.eclipse.equinox.p2.tests.AbstractProvisioningTest;

public class PatchTest1 extends AbstractProvisioningTest {
	IInstallableUnit a1;
	IInstallableUnit b1;
	IInstallableUnitPatch p1;

	IProfile profile1;
	IPlanner planner;
	IEngine engine;

	protected void setUp() throws Exception {
		super.setUp();
		a1 = createIU("A", Version.create("1.0.0"), new IRequiredCapability[] {MetadataFactory.createRequiredCapability(IInstallableUnit.NAMESPACE_IU_ID, "B", new VersionRange("[1.0.0, 1.1.0)"), null, false, true)});
		b1 = createIU("B", Version.createOSGi(1, 2, 0), true);
		IRequirementChange change = MetadataFactory.createRequirementChange(MetadataFactory.createRequiredCapability(IInstallableUnit.NAMESPACE_IU_ID, "B", VersionRange.emptyRange, null, false, false, false), MetadataFactory.createRequiredCapability(IInstallableUnit.NAMESPACE_IU_ID, "B", new VersionRange("[1.1.0, 1.3.0)"), null, false, false, true));
		p1 = createIUPatch("P", Version.create("1.0.0"), true, new IRequirementChange[] {change}, new IRequiredCapability[][] {{MetadataFactory.createRequiredCapability(IInstallableUnit.NAMESPACE_IU_ID, "A", VersionRange.emptyRange, null, false, false)}}, null);

		createTestMetdataRepository(new IInstallableUnit[] {a1, b1, p1});

		profile1 = createProfile("TestProfile." + getName());
		planner = createPlanner();
		engine = createEngine();
	}

	public void testInstall() {
		//The requirement from A to B is broken because there is no B satisifying. Therefore A can only install if the P is installed as well
		ProfileChangeRequest req1 = new ProfileChangeRequest(profile1);
		req1.addInstallableUnits(new IInstallableUnit[] {a1, p1});
		IProvisioningPlan plan1 = planner.getProvisioningPlan(req1, null, null);
		assertEquals(IStatus.OK, plan1.getStatus().getSeverity());
	}
}
