package com.devyk.aveditor.video.filter.magic

import android.content.Context
import android.opengl.GLES20
import com.devyk.aveditor.R
import com.devyk.aveditor.utils.OpenGLUtils
import com.devyk.aveditor.video.filter.gpuimage.base.GPUImageFilter


class AVToolsBrooklynFilter(context: Context) :
    GPUImageFilter(
        context,
        GPUImageFilter.NO_FILTER_VERTEX_SHADER,
        OpenGLUtils.readRawTextFile(context, R.raw.brooklyn)
    ) {
    private val inputTextureHandles = intArrayOf(-1, -1, -1)
    private val inputTextureUniformLocations = intArrayOf(-1, -1, -1)
    private var mGLStrengthLocation: Int = 0

    public override fun onDestroy() {
        super.onDestroy()
        GLES20.glDeleteTextures(inputTextureHandles.size, inputTextureHandles, 0)
        for (i in inputTextureHandles.indices)
            inputTextureHandles[i] = -1
    }

    override fun onDrawArraysAfter() {
        var i = 0
        while (i < inputTextureHandles.size && inputTextureHandles[i] != OpenGLUtils.NO_TEXTURE) {
            GLES20.glActiveTexture(GLES20.GL_TEXTURE0 + (i + 3))
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0)
            GLES20.glActiveTexture(GLES20.GL_TEXTURE0)
            i++
        }
    }

    override fun onDrawArraysPre() {
        var i = 0
        while (i < inputTextureHandles.size && inputTextureHandles[i] != OpenGLUtils.NO_TEXTURE) {
            GLES20.glActiveTexture(GLES20.GL_TEXTURE0 + (i + 3))
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, inputTextureHandles[i])
            GLES20.glUniform1i(inputTextureUniformLocations[i], i + 3)
            i++
        }
    }

    public override fun onInit() {
        super.onInit()
        for (i in inputTextureUniformLocations.indices)
            inputTextureUniformLocations[i] = GLES20.glGetUniformLocation(program, "inputImageTexture" + (2 + i))
        mGLStrengthLocation = GLES20.glGetUniformLocation(
            program,
            "strength"
        )
    }

    public override fun onInitialized() {
        super.onInitialized()
        setFloat(mGLStrengthLocation, 1.0f)
        runOnDraw(Runnable {
            inputTextureHandles[0] = OpenGLUtils.loadTexture(context, "filter/brooklynCurves1.png")
            inputTextureHandles[1] = OpenGLUtils.loadTexture(context, "filter/filter_map_first.png")
            inputTextureHandles[2] = OpenGLUtils.loadTexture(context, "filter/brooklynCurves2.png")
        })
    }
}
