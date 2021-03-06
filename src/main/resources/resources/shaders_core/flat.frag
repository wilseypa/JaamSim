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
@VERSION@

// This will be replaced with appropriate defines as a macro expansion
@DEFINES@

in vec2 texCoordFrag;
in vec3 normalFrag;

uniform float C;
uniform float FC;
in float interpZ;

//layout(location = 0) out vec4 output;
out vec4 outColour;

#ifdef DIFF_TEX
// Use a diffuse texture
uniform sampler2D diffuseTex;
#define DIFF_VAL texture(diffuseTex, texCoordFrag)

#else
// Constant diffuse color
uniform vec4 diffuseColor;
#define DIFF_VAL diffuseColor

#endif

const int MAX_LIGHTS = 10;

uniform vec3 lightDir[MAX_LIGHTS];
uniform float lightIntensity[MAX_LIGHTS];
uniform int numLights;

in vec3 viewDir;

uniform vec3 ambientColor;
uniform vec3 specColor;
uniform float shininess;

void main()
{
    outColour.a = 1;

    vec3 n = normalize(normalFrag);

    vec4 dColor = DIFF_VAL;
    outColour.a = dColor.a;

    vec3 d = vec3(0, 0, 0);
    vec3 s = vec3(0, 0, 0);

    float light = 0;
    for (int i = 0; i < numLights; ++i) {
        vec3 l = lightDir[i];

        float lDotN = dot(n, l);
        d += max(0, -1*lDotN) * lightIntensity[i] * dColor.rgb;

        if (shininess > 2) { // shininess == 1 is default, but all real values will be > 2
            vec3 ref = 2*lDotN*n - l;
            float specExp = pow(max(0, dot(ref, viewDir)), shininess);

            s += specExp * lightIntensity[i] * specColor;
        }
    }

    outColour.rgb = s + d + ambientColor * 0.1;

    gl_FragDepth = log(interpZ*C+1)*FC;
}
