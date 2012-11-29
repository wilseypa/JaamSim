/*
 * JaamSim Discrete Event Simulation
 * Copyright (C) 2012 Ausenco Engineering Canada Inc.
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
package com.jaamsim.render;

import com.jaamsim.math.AABB;
import com.jaamsim.math.MathUtils;
import com.jaamsim.math.Matrix4d;
import com.jaamsim.math.Plane;
import com.jaamsim.math.Sphere;
import com.jaamsim.math.Transform;
import com.jaamsim.math.Vector4d;

/**
 * In the JaamRenderer, Camera's represent a single viewer of the world. Camera's are responsible for providing
 * the Projection and View matrices at render time and are capable of view frustum culling against bounding
 * spheres and AABBs (both in world coords)
 * @author Matt Chudleigh
 *
 */
public class Camera {

/**
 * All the basic camera configuration information is stored in a CameraInfo, this can be copied and passed
 * back to the app. All other members are renderer owned information
 */
private CameraInfo _info;
private double _aspectRatio;

private Matrix4d _projMat;
private boolean _projMatDirty = true;

/**
 * An array of 6 Planes that represent the view frustum in world coordinates
 */
private Plane[] _frustum;
private boolean _frustumDirty = true;

/**
 * The transform to the current camera position, should never include a scale component (although
 * I don't know for a fact that it would be a bad thing....)
 */
//private Transform _trans;

/**
 * Construct a new camera, the parameters provided are needed to determine the view frustum and the
 * project matrix. Default camera is the openGL camera, at the origin looking in the -Z direction
 * @param FOV
 * @param aspectRatio
 * @param near
 * @param far
 */
public Camera(double FOV, double aspectRatio, double near, double far) {
	_info = new CameraInfo(FOV, near, far, Transform.ident);

	_aspectRatio = aspectRatio;

	_frustumDirty = true;
	_projMatDirty = true;
}

public Camera(CameraInfo camInfo, double aspectRatio) {
	_info = camInfo;
	_aspectRatio = aspectRatio;

	_frustumDirty = true;
	_projMatDirty = true;
}

public void getTransform(Transform out) {
	out.copyFrom(_info.trans);
}

public Transform getTransformRef() {
	return _info.trans;
}
/**
 * Update the camera position
 * @param t
 */
public void setTransform(Transform t) {
	_info.trans.copyFrom(t);
	_frustumDirty = true;
}

/**
 * Apply this transform to the existing camera transform.
 * Useful for incremental changes like rotating in place
 * @param t
 */
public void applyTransform(Transform t) {
	t.merge(_info.trans, _info.trans);
	_frustumDirty = true;
}

/**
 * Fills a Matrix4d with the projection matrix for this camera, lookup gluPerspective to understand the math
 * @param projOut
 */
private void updateProjMat() {
	if (_projMat == null) {
		_projMat = new Matrix4d();
	}
	_projMat.zero();

	double f = 1/Math.tan(_info.FOV/2);
	double denom = 1 / (_info.nearDist - _info.farDist);

	double fx, fy;
	if(_aspectRatio > 1) {
		fx = f;
		fy = f * _aspectRatio;
	} else {
		fy = f;
		fx = f / _aspectRatio;
	}

	// Note: the weird index is column, row indices
	_projMat.data[0*4 + 0] = fx;
	_projMat.data[1*4 + 1] = fy;
	_projMat.data[2*4 + 2] = (_info.farDist + _info.nearDist) * denom;
	_projMat.data[2*4 + 3] = -1;
	_projMat.data[3*4 + 2] = 2*_info.nearDist*_info.farDist * denom;

	_projMatDirty = false;
}

/**
 * Gets a reference to the projection matrix for this camera
 * @return
 */
public Matrix4d getProjMatRef() {
	if (_projMatDirty) {
		updateProjMat();
	}

	return _projMat;
}

/**
 * Get the view matrix for the current position (defined by the transform)
 * @param viewOut
 */
public void getViewMatrix(Matrix4d viewOut) {
	Transform tInv = new Transform();
	_info.trans.inverse(tInv);

	tInv.getMatrix(viewOut);
}

public void getViewTrans(Transform transOut) {
	_info.trans.inverse(transOut);
}

public double getNear() {
	return _info.nearDist;
}
public double getFar() {
	return _info.farDist;
}
public double getAspectRatio() {
	return _aspectRatio;
}
/**
 * Get the camera FOV in the y direction in radians
 * @return
 */
public double getFOV() {
	return _info.FOV;
}

public void setNear(double near) {
	boolean dirty =  !MathUtils.near(_info.nearDist, near);
	_frustumDirty = dirty || _frustumDirty;
	_projMatDirty = dirty || _projMatDirty;
	_info.nearDist = near;
}

public void setFar(double far) {
	boolean dirty = !MathUtils.near(_info.farDist, far);
	_frustumDirty = dirty || _frustumDirty;
	_projMatDirty = dirty || _projMatDirty;
	_info.farDist = far;
}

public void setAspectRatio(double aspect) {
	boolean dirty = !MathUtils.near(_aspectRatio, aspect);
	_frustumDirty = dirty || _frustumDirty;
	_projMatDirty = dirty || _projMatDirty;
	_aspectRatio = aspect;
}

public void setFOV(double FOV) {
	boolean dirty = !MathUtils.near(_info.FOV, FOV);
	_frustumDirty = dirty || _frustumDirty;
	_projMatDirty = dirty || _projMatDirty;
	_info.FOV = FOV;
}

/**
 * Get a direction vector in the direction of camera view (only accurate for the center of the view, as this
 * is a perspective transform)
 * @param dirOut
 */
public void getViewDir(Vector4d dirOut) {
	_info.trans.apply(new Vector4d(0 ,0, -1, 0), dirOut);
}

/**
 * Test frustum collision with sphere s
 * @param s
 * @return
 */
public boolean collides(Sphere s) {
	updateFrustum();

	// The sphere needs to be inside (or touching) all planes to be in the frustum
	for (int i = 0; i < 6; ++i) {
		double dist = _frustum[i].getNormalDist(s.getCenterRef());
		if (dist < - s.getRadius()) {
			return false;
		}
	}
	return true;
}

public boolean collides(AABB aabb) {
	if (aabb.isEmpty()) {
		return false;
	}

	updateFrustum();

	for (int i = 0; i < 6; ++i) {
		// Check if the AABB is completely outside any frustum plane
		if (aabb.testToPlane(_frustum[i]) == AABB.PlaneTestResult.NEGATIVE) {
			return false;
		}
	}
	return true;
}

/**
 * Update the stored frustum planes to account for the current parameters and transform
 */
private void updateFrustum() {
	if (!_frustumDirty) {
		return; // Already done
	}

	double thetaX, thetaY;
	if (_aspectRatio > 1) {
		thetaX = _info.FOV/2;
		thetaY = Math.atan(Math.tan(thetaX) / _aspectRatio);
	} else {
		thetaY = _info.FOV/2;
		thetaX = Math.atan(Math.tan(thetaY) * _aspectRatio);
	}

	double sinX = Math.sin(thetaX);
	double sinY = Math.sin(thetaY);

	double cosX = Math.cos(thetaX);
	double cosY = Math.cos(thetaY);

	if (_frustum == null) {
		_frustum = new Plane[6];
	}

	// Create the 6 planes that define the frustum, anything on the positive side of all 6 planes is
	// in the frustum
	// +Y
	_frustum[0] = new Plane(new Vector4d(0,  cosY, -sinY), 0);
	// -Y
	_frustum[1] = new Plane(new Vector4d(0, -cosY, -sinY), 0);

	// +X
	_frustum[2] = new Plane(new Vector4d( cosX, 0, -sinX), 0);
	// -X
	_frustum[3] = new Plane(new Vector4d(-cosX, 0, -sinX), 0);

	// +Z
	_frustum[4] = new Plane(new Vector4d(0, 0, -1), _info.nearDist);

	// -Z
	_frustum[5] = new Plane(new Vector4d(0, 0, 1), -_info.farDist);

	// Apply the current transform to the planes. Puts the planes in world space
	for (int i = 0; i < 6; ++i) {
		_frustum[i].transform(_info.trans, _frustum[i]);
	}

}

public CameraInfo getInfo() {
	return _info.getCopy();
}

/**
 * Overwrite all core camera state in one go
 */
public void setInfo(CameraInfo newInfo) {
	boolean dirty = !_info.isSame(newInfo);
	_info = newInfo.getCopy();
	_frustumDirty = dirty || _frustumDirty;
	_projMatDirty = dirty || _projMatDirty;
}

} // class Camera