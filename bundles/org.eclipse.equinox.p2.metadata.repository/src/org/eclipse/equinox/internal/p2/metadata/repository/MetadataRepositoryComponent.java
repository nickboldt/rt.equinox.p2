/*******************************************************************************
 * Copyright (c) 2009 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.equinox.internal.p2.metadata.repository;

import org.eclipse.equinox.internal.provisional.p2.core.eventbus.IProvisioningEventBus;
import org.eclipse.equinox.internal.provisional.p2.metadata.repository.IMetadataRepositoryManager;
import org.eclipse.equinox.p2.core.IAgentLocation;
import org.eclipse.equinox.p2.core.IProvisioningAgent;
import org.eclipse.equinox.p2.core.spi.IAgentServiceFactory;

/**
 * Service factory for creating {@link IMetadataRepositoryManager} instances.
 */
public class MetadataRepositoryComponent implements IAgentServiceFactory {

	public Object createService(IProvisioningAgent agent) {
		MetadataRepositoryManager manager = new MetadataRepositoryManager();
		final IProvisioningEventBus eventBus = (IProvisioningEventBus) agent.getService(IProvisioningEventBus.SERVICE_NAME);
		manager.setEventBus(eventBus);
		CacheManager cache = new CacheManager((IAgentLocation) agent.getService(IAgentLocation.SERVICE_NAME));
		cache.setEventBus(eventBus);
		Activator.cacheManager = cache;//todo avoid global cache
		return manager;
	}

}
