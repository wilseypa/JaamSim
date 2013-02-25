/*
 * JaamSim Discrete Event Simulation
 * Copyright (C) 2013 Ausenco Engineering Canada Inc.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 */
package com.jaamsim.BasicObjects;

import com.sandwell.JavaSimulation.InputErrorException;
import com.sandwell.JavaSimulation3D.DisplayEntity;

/**
 * EntitySink kills the DisplayEntities sent to it.
 */
public class EntitySink extends LinkedComponent {

	@Override
	public void validate() {
		super.validate();

		// Confirm that the next entity in the chain has not been specified
		if( getNextComponent() != null ) {
			throw new InputErrorException( "The keyword NextEntity must not be set." );
		}
	}

	/**
	 * Add a DisplayEntity from upstream
	 */
	@Override
	public void addDisplayEntity( DisplayEntity ent ) {

		// Kill the added entity
		ent.kill();

		// Increment the total number killed
		numberProcessed++;

	}
}