package com.example.projetocamera.ui

import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.getMainExecutor
import com.example.projetocamera.Constats
import com.example.projetocamera.R
import com.example.projetocamera.databinding.ActivityMainBinding
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity(){

    private lateinit var binding: ActivityMainBinding
    private var imageCapture: ImageCapture?= null
    private lateinit var outputDirectory: File

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        outputDirectory = getOutputDirectory()

        if (allPermissionGranted()){
            startCamera()
        }
        else {
            ActivityCompat.requestPermissions(
                this, Constats.REQUIRED_PERMISSIONS,
                Constats.REQUEST_CODE_PERMISSIONS
            )
        }

        binding.btnButton.setOnClickListener {
            btn_button()
        }
    }

    private fun getOutputDirectory(): File{
        val mediaDir = externalMediaDirs.firstOrNull()?.let {mFile->
            File(mFile, resources.getString(R.string.app_name)).apply {
                mkdirs()
            }
        }
        return if ( mediaDir!=null && mediaDir.exists())
            mediaDir else filesDir
    }
    private fun btn_button(){

            val imageCapture = imageCapture?:return
            val photoFile = File( outputDirectory,
                SimpleDateFormat (
                    Constats.FILE_NAME_FORMAT,
                    Locale.getDefault())
                    .format(System.currentTimeMillis())+".arquivo.jpg")

            val outputFilesOption = ImageCapture.OutputFileOptions.Builder(photoFile).build()

            imageCapture.takePicture(
                outputFilesOption,getMainExecutor(this),
                object : ImageCapture.OnImageSavedCallback {

                    override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                        val savedUri = Uri.fromFile(photoFile)
                        val msg = "Photo saved Mensagem 3" // mensagem exibida ao salvar a foto

                        Toast.makeText(this@MainActivity,
                            "$msg $savedUri",
                            Toast.LENGTH_LONG)
                            .show()
                    }

                    override fun onError(exception: ImageCaptureException) {
                        Log.i("INFO captura de imagem", "*****************************onError: ${exception.message}", exception)
                    }
                }
            )
        }

    private fun startCamera() {

        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)

        cameraProviderFuture.addListener({

            val cameraProvider:ProcessCameraProvider = cameraProviderFuture.get()
            val preview = Preview.Builder().build()
                        .also { mPreview ->
                        mPreview.setSurfaceProvider(
                        binding.viewFinder.surfaceProvider)
                }

            imageCapture = ImageCapture.Builder().build()

                val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

            try {
                cameraProvider.unbindAll()
                    cameraProvider.bindToLifecycle(
                        this,cameraSelector,preview,imageCapture)
                }

            catch (e:Exception){
                Log.d(Constats.TAG, "Start camera fail")
            }

        },getMainExecutor(this))
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray) {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == Constats.REQUEST_CODE_PERMISSIONS)
            if (allPermissionGranted()) {
                startCamera() }
            else {
                Toast.makeText(applicationContext,"Message exibida 2", Toast.LENGTH_LONG).show()
            }
            finish()
    }
        private fun allPermissionGranted() =
            Constats.REQUIRED_PERMISSIONS.all {
                ContextCompat.checkSelfPermission(
                    baseContext,it
                ) == PackageManager.PERMISSION_GRANTED
            }
}

