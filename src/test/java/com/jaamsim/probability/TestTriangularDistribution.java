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
package com.jaamsim.probability;

import static org.junit.Assert.assertTrue;

import org.junit.Test;
import com.jaamsim.ProbabilityDistributions.TriangularDistribution;
import com.jaamsim.input.InputAgent;

public class TestTriangularDistribution {

	@Test
	public void MeanAndStandardDeviation() {
		TriangularDistribution dist = new TriangularDistribution();
		InputAgent.processEntity_Keyword_Value( dist, "MinValue", "2.0");
		InputAgent.processEntity_Keyword_Value(dist, "MaxValue", "5.0");
		InputAgent.processEntity_Keyword_Value(dist, "Mode", "4.0");

		double total = 0.0d;
		int numSamples = 1000000;
		for (int i = 0; i < numSamples; i++) {
			total += dist.nextValue();
		}
		double mean = total / numSamples;

		assertTrue( Math.abs( dist.getSampleMean(0.0) - mean ) < 0.001 );
		assertTrue( Math.abs( dist.getSampleMean(0.0) / dist.getMeanValue(0.0) - 1.0 ) < 0.001 );
		assertTrue( Math.abs( dist.getSampleStandardDeviation(0.0) / dist.getStandardDeviation(0.0) - 1.0 ) < 0.001 );
	}
}