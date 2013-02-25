/*
 * JaamSim Discrete Event Simulation
 * Copyright (C) 2011 Ausenco Engineering Canada Inc.
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
package com.sandwell.JavaSimulation;

import java.util.ArrayList;

import com.jaamsim.input.InputAgent;
import com.jaamsim.math.Vec3d;
import com.jaamsim.units.Unit;

public class Vec3dListInput extends ListInput<ArrayList<Vec3d>> {

	public Vec3dListInput(String key, String cat, ArrayList<Vec3d> def) {
		super(key, cat, def);
	}

	public void parse(StringVector input)
	throws InputErrorException {

		// Check if number of outer lists violate minCount or maxCount
		ArrayList<StringVector> splitData = Util.splitStringVectorByBraces(input);
		if (splitData.size() < minCount || splitData.size() > maxCount)
			throw new InputErrorException(INP_ERR_RANGECOUNT, minCount, maxCount, input.toString());

		ArrayList<Vec3d> tempValue = new ArrayList<Vec3d>();

		for( StringVector innerInput : splitData ) {
			DoubleVector temp;

			// If there is more than one value, and the last one is not a number, then assume it is a unit
			if( innerInput.size() > 1 && !Tester.isDouble( innerInput.get( innerInput.size()-1 ) ) ) {

				// Determine the units
				Unit unit = Input.parseUnits(innerInput.get(innerInput.size()- 1));

				// Determine the default units
				String unitName = unitString.replaceAll("[()]", "").trim();
				Unit defaultUnit = Input.tryParseEntity( unitName, Unit.class );
				if( defaultUnit == null) {
					throw new InputErrorException( "Could not determine default units " + unitString );
				}

				// Determine the conversion factor to the default units
				double conversionFactor = unit.getConversionFactorToUnit( defaultUnit );

				// Parse and convert the values
				Input.assertCountRange(innerInput.subString(0,innerInput.size()-2), 1, 3);
				temp = Input.parseDoubleVector(innerInput.subString(0,innerInput.size()-2), Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY, conversionFactor);

			}
			else {
				// Parse the values
				Input.assertCountRange(innerInput, 1, 3);
				temp = Input.parseDoubleVector(innerInput, Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY);

				if( unitString.length() > 0 )
					InputAgent.logWarning( "Missing units.  Assuming %s.", unitString );
			}

			// pad the vector to have 3 elements
			while (temp.size() < 3) {
				temp.add(0.0d);
			}

			tempValue.add(new Vec3d(temp.get(0), temp.get(1), temp.get(2)));
		}

		value = tempValue;
		this.updateEditingFlags();
	}

	@Override
	public String getDefaultString() {
		if (defValue == null)
			return NO_VALUE;

		if (defValue.size() == 0)
			return NO_VALUE;

		StringBuilder tmp = new StringBuilder();
		for (Vec3d each: defValue) {

			// blank space between elements
			if (tmp.length() > 0)
				tmp.append(SEPARATOR);

			if (each == null) {
				tmp.append(NO_VALUE);
				continue;
			}

			tmp.append("{");
			tmp.append(SEPARATOR);
			tmp.append(each.x);
			tmp.append(SEPARATOR);
			tmp.append(each.y);
			tmp.append(SEPARATOR);
			tmp.append(each.z);
			tmp.append(SEPARATOR);
			if (!unitString.isEmpty()) {
				tmp.append(unitString);
				tmp.append(SEPARATOR);
			}
			tmp.append("}");
		}
		return tmp.toString();
	}
}