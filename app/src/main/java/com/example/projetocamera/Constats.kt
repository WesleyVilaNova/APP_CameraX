package com.example.projetocamera

import android.nfc.Tag
import java.util.jar.Manifest

object Constats {

    const val TAG = "Camera"
    const val FILE_NAME_FORMAT = "yy-MM-dd-HH-mm-ss-SS"
    const val REQUEST_CODE_PERMISSIONS = 123
    val REQUIRED_PERMISSIONS = arrayOf(android.Manifest.permission.CAMERA) // pedido de permissão

}