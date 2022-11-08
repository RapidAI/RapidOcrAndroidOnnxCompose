package com.benjaminwan.ocrlibrary

import ai.onnxruntime.OrtEnvironment
import android.content.res.AssetManager

class Cls(private val ortEnv: OrtEnvironment, assetManager: AssetManager, modelName: String) {

    private val clsSession by lazy {
        val model = assetManager.open(modelName, AssetManager.ACCESS_UNKNOWN).readBytes()
        ortEnv.createSession(model)
    }


}