/*******************************************************************************
 *  Copyright (c) 2009 IBM Corporation and others.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 * 
 *  Contributors:
 *      IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.equinox.p2.tests.planner;

import org.eclipse.equinox.internal.p2.metadata.IRequiredCapability;
import org.eclipse.equinox.internal.provisional.p2.director.IPlanner;
import org.eclipse.equinox.internal.provisional.p2.director.ProfileChangeRequest;
import org.eclipse.equinox.internal.provisional.p2.metadata.*;
import org.eclipse.equinox.p2.engine.IProfile;
import org.eclipse.equinox.p2.engine.IProvisioningPlan;
import org.eclipse.equinox.p2.metadata.IInstallableUnit;
import org.eclipse.equinox.p2.metadata.IProvidedCapability;
import org.eclipse.equinox.p2.metadata.query.InstallableUnitQuery;
import org.eclipse.equinox.p2.query.Collector;
import org.eclipse.equinox.p2.tests.AbstractProvisioningTest;

public class SWTFragment extends AbstractProvisioningTest {
	public void testFragmentPickedByCapability() {
		IRequiredCapability[] reqs = createRequiredCapabilities("swt.fragment", "swt.fragment", new VersionRange("[1.0.0, 2.0.0)"), null);
		IInstallableUnit swt = createIU("SWT", reqs);

		MetadataFactory.InstallableUnitDescription iud = new MetadataFactory.InstallableUnitDescription();
		iud.setId("SWT.WIN32");
		iud.setVersion(Version.create("1.0.0"));
		iud.setCapabilities(new IProvidedCapability[] {MetadataFactory.createProvidedCapability("swt.fragment", "swt.fragment", Version.createOSGi(1, 0, 0))});
		iud.setFilter("(os=win32)");
		IInstallableUnit swtW = MetadataFactory.createInstallableUnit(iud);

		MetadataFactory.InstallableUnitDescription iud2 = new MetadataFactory.InstallableUnitDescription();
		iud.setId("SWT.LINUX");
		iud.setVersion(Version.create("1.0.0"));
		iud.setCapabilities(new IProvidedCapability[] {MetadataFactory.createProvidedCapability("swt.fragment", "swt.fragment", Version.createOSGi(1, 0, 0))});
		iud.setFilter("(os=linux)");
		IInstallableUnit swtL = MetadataFactory.createInstallableUnit(iud2);

		createTestMetdataRepository(new IInstallableUnit[] {swt, swtL, swtW});
		IProfile profile = createProfile(IUWithFilter.class.getName());
		IPlanner planner = createPlanner();

		ProfileChangeRequest req = new ProfileChangeRequest(profile);
		req.setProfileProperty("os", "win32");
		req.addInstallableUnits(new IInstallableUnit[] {swt});
		IProvisioningPlan plan = planner.getProvisioningPlan(req, null, null);
		assertOK("plan", plan.getStatus());
		Collector c = new Collector();
		c.addAll(plan.getAdditions().query(new InstallableUnitQuery("SWT"), null));
		c.addAll(plan.getAdditions().query(new InstallableUnitQuery("SWT.WIN32"), null));
		assertEquals(2, c.size());
	}
}
