/*
 *  ARActivity.java
 *  ARToolKit5
 *
 *  This file is part of ARToolKit.
 *
 *  ARToolKit is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Lesser General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  ARToolKit is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public License
 *  along with ARToolKit.  If not, see <http://www.gnu.org/licenses/>.
 *
 *  As a special exception, the copyright holders of this library give you
 *  permission to link this library with independent modules to produce an
 *  executable, regardless of the license terms of these independent modules, and to
 *  copy and distribute the resulting executable under terms of your choice,
 *  provided that you also meet, for each linked independent module, the terms and
 *  conditions of the license of that module. An independent module is a module
 *  which is neither derived from nor based on this library. If you modify this
 *  library, you may extend this exception to your version of the library, but you
 *  are not obligated to do so. If you do not wish to do so, delete this exception
 *  statement from your version.
 *
 *  Copyright 2015 Daqri, LLC.
 *  Copyright 2011-2015 ARToolworks, Inc.
 *
 *  Author(s): Julian Looser, Philip Lamb
 *
 */
package org.artoolkit.ar.base.rendering.gles20;
//package base2.rendering.gles20;

import android.opengl.GLES20;

/**
 * Created by Thorsten Bux on 21.01.2016.
 * Here you define your vertex shader and what it does with the geometry position.
 * This vertex shader class calculates the MVP matrix and applies it to the passed
 * in geometry position vectors.
 * <p/>
 * This class also provides the implementation of the {@link #configureShader()} method. So all you need to do is
 * call this one from your fragment shader implementation.
 */
public class BaseVertexShader implements OpenGLShader {

    private String vertexShaderSource =
            "uniform mat4 u_MVPMatrix; \n"     // A constant representing the combined model/view/projection matrix.

                    + "uniform mat4 " + OpenGLShader.projectionMatrixString + "; \n"        // projection matrix
                    + "uniform mat4 " + OpenGLShader.modelViewMatrixString + "; \n"        // modelView matrix

                    + "attribute vec4 " + OpenGLShader.positionVectorString + "; \n"     // Per-vertex position information we will pass in.

                    + "void main() \n"     // The entry point for our vertex shader.
                    + "{ \n"
                    + "   vec4 p = " + OpenGLShader.modelViewMatrixString + " * " + OpenGLShader.positionVectorString + "; \n "     // transform vertex position with modelview matrix
                    + "   gl_Position = " + OpenGLShader.projectionMatrixString + " \n"     // gl_Position is a special variable used to store the final position.
                    + "                     * p; \n"     // Multiply the vertex by the matrix to get the final point in
                    + "} \n";    // normalized screen coordinates.

    @Override
    public int configureShader() {
        // Load in the vertex shader.
        int vertexShaderHandle = GLES20.glCreateShader(GLES20.GL_VERTEX_SHADER);
        String vertexShaderErrorLog = "";

        if (vertexShaderHandle != 0) {
            // Pass in the shader source.
            GLES20.glShaderSource(vertexShaderHandle, vertexShaderSource);

            // Compile the shader.
            GLES20.glCompileShader(vertexShaderHandle);

            // Get the compilation status.
            final int[] compileStatus = new int[1];
            GLES20.glGetShaderiv(vertexShaderHandle, GLES20.GL_COMPILE_STATUS, compileStatus, 0);

            // If the compilation failed, delete the shader.
            if (compileStatus[0] == 0) {
                vertexShaderErrorLog = GLES20.glGetShaderInfoLog(vertexShaderHandle);
                GLES20.glDeleteShader(vertexShaderHandle);
                vertexShaderHandle = 0;
            }
        }

        if (vertexShaderHandle == 0) {
            throw new RuntimeException("Error creating vertex shader.\n" + vertexShaderErrorLog);
        }

        return vertexShaderHandle;
    }

    public void setShaderSource(String vertexShaderSource) {
        this.vertexShaderSource = vertexShaderSource;
    }
}
