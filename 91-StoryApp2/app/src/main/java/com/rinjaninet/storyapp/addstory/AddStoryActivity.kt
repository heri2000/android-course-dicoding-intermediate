package com.rinjaninet.storyapp.addstory

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.rinjaninet.storyapp.R
import com.rinjaninet.storyapp.network.ApiConfig
import com.rinjaninet.storyapp.databinding.ActivityAddStoryBinding
import com.rinjaninet.storyapp.login.LoginResult
import com.rinjaninet.storyapp.network.AddStoryResponse
import com.rinjaninet.storyapp.preferences.LoginPreferences
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File

class AddStoryActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddStoryBinding
    private var getFile: File? = null
    private lateinit var mLoginPreferences: LoginPreferences
    private lateinit var loginInfo: LoginResult
    private var imageSource = IMAGE_SOURCE_NONE

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddStoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        mLoginPreferences = LoginPreferences(this)
        loginInfo = mLoginPreferences.getLogin()

        if (!allPermissionsGranted()) {
            ActivityCompat.requestPermissions(
                this,
                REQUIRED_PERMISSIONS,
                REQUEST_CODE_PERMISSIONS
            )
        }

        binding.btnAddStoryCamera.setOnClickListener { startCameraX() }
        binding.btnAddStoryGallery.setOnClickListener { startGallery() }
        binding.buttonAdd.setOnClickListener { uploadStory() }
    }

    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(baseContext, it) == PackageManager.PERMISSION_GRANTED
    }

    private fun startCameraX() {
        if (!allPermissionsGranted()) {
            ActivityCompat.requestPermissions(
                this,
                REQUIRED_PERMISSIONS,
                REQUEST_CODE_PERMISSIONS
            )
        }
        if (allPermissionsGranted()) {
            val cameraXIntent = Intent(this, CameraActivity::class.java)
            launcherIntentCameraX.launch(cameraXIntent)
        } else {
            Toast.makeText(
                this,
                resources.getString(R.string.please_grant_camera_permission),
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private val launcherIntentCameraX = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        if (it.resultCode == CAMERA_X_RESULT) {
            val myFile = it.data?.getSerializableExtra("picture") as File
            val isBackCamera = it.data?.getBooleanExtra("isBackCamera", true) as Boolean

            imageSource = if (isBackCamera) IMAGE_SOURCE_BACK_CAMERA else IMAGE_SOURCE_FRONT_CAMERA

            getFile = myFile
            val result = rotateBitmap(BitmapFactory.decodeFile(myFile.path), isBackCamera)

            binding.ivAddStoryPreview.setImageBitmap(result)
        }
    }

    private fun startGallery() {
        val intent = Intent()
        intent.action = Intent.ACTION_GET_CONTENT
        intent.type = "image/*"
        val chooser = Intent.createChooser(intent, "Choose a Picture")
        launcherIntentGallery.launch(chooser)
    }

    private val launcherIntentGallery = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            val selectedImg: Uri = result.data?.data as Uri
            val myFile = uriToFile(selectedImg, this@AddStoryActivity)
            imageSource = IMAGE_SOURCE_GALLERY
            getFile = myFile
            binding.ivAddStoryPreview.setImageURI(selectedImg)
        }
    }

    private fun uploadStory() {
        if (getFile != null) {
            binding.tvAddStoryError.visibility = View.GONE
            binding.pbAddStoryProgress.visibility = View.VISIBLE

            val file = reduceImageFile(getFile as File, imageSource)

            val description = binding.edAddDescription.text
                .toString()
                .trim()
                .toRequestBody("text/plain".toMediaType())
            val requestImageFile = file.asRequestBody("image/jpeg".toMediaTypeOrNull())
            val imageMultipart: MultipartBody.Part = MultipartBody.Part.createFormData(
                "photo",
                file.name,
                requestImageFile
            )

            val service = ApiConfig.getApiService().addStory(
                "Bearer ${loginInfo.token}", imageMultipart, description
            )
            service.enqueue(object : Callback<AddStoryResponse> {
                override fun onResponse(
                    call: Call<AddStoryResponse>,
                    response: Response<AddStoryResponse>
                ) {
                    if (response.isSuccessful) {
                        val responseBody = response.body()
                        if (responseBody?.error != null && !responseBody.error) {
                            Toast.makeText(
                                this@AddStoryActivity,
                                resources.getString(R.string.success_uploading_story),
                                Toast.LENGTH_SHORT
                            ).show()

                            val imagePath = file.path
                            val desc = binding.edAddDescription.text.toString().trim()

                            val resultIntent = Intent()
                            resultIntent.putExtra(EXTRA_IMAGE_PATH, imagePath)
                            resultIntent.putExtra(EXTRA_DESCRIPTION, desc)
                            setResult(RESULT_CODE, resultIntent)
                            finish()
                        } else {
                            binding.tvAddStoryError.text = response.message()
                            binding.tvAddStoryError.visibility = View.VISIBLE
                            binding.pbAddStoryProgress.visibility = View.GONE
                        }
                    } else {
                        binding.tvAddStoryError.text = response.message()
                        binding.tvAddStoryError.visibility = View.VISIBLE
                        binding.pbAddStoryProgress.visibility = View.GONE
                    }
                }

                override fun onFailure(call: Call<AddStoryResponse>, t: Throwable) {
                    binding.tvAddStoryError.text = resources.getString(R.string.error_add_story_0402)
                    binding.tvAddStoryError.visibility = View.VISIBLE
                    binding.pbAddStoryProgress.visibility = View.GONE
                }
            })
        } else {
            binding.tvAddStoryError.text = resources.getString(R.string.please_add_image)
            binding.tvAddStoryError.visibility = View.VISIBLE
        }
    }

    companion object {
        const val CAMERA_X_RESULT = 200
        private val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.CAMERA)
        private const val REQUEST_CODE_PERMISSIONS = 10

        const val IMAGE_SOURCE_NONE = 0
        const val IMAGE_SOURCE_GALLERY = 1
        const val IMAGE_SOURCE_BACK_CAMERA = 2
        const val IMAGE_SOURCE_FRONT_CAMERA = 3

        const val RESULT_CODE = 111
        const val EXTRA_IMAGE_PATH = "extra_image_uri"
        const val EXTRA_DESCRIPTION = "extra_description"
    }
}