package com.dicoding.storyapp.ui

import android.Manifest
import android.content.Intent
import android.content.IntentSender
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.location.Location
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.dicoding.storyapp.R
import com.dicoding.storyapp.databinding.ActivityAddStoryBinding
import com.dicoding.storyapp.network.AddStoryResponse
import com.dicoding.storyapp.network.ApiConfig
import com.dicoding.storyapp.network.LoginResult
import com.dicoding.storyapp.preferences.LoginPreferences
import com.dicoding.storyapp.utils.reduceImageFile
import com.dicoding.storyapp.utils.rotateBitmap
import com.dicoding.storyapp.utils.uriToFile
import com.google.android.gms.location.*
import com.google.android.gms.maps.model.LatLng
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
    private var latlng: LatLng? = null
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddStoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        mLoginPreferences = LoginPreferences(this)
        loginInfo = mLoginPreferences.getLogin()

        if (!allPermissionsGranted()) {
            requestPermissionLauncher.launch(
                REQUIRED_PERMISSIONS
            )
        }

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        getMyLocation()

        binding.btnAddStoryCamera.setOnClickListener { startCameraX() }
        binding.btnAddStoryGallery.setOnClickListener { startGallery() }
        binding.buttonAdd.setOnClickListener { uploadStory() }
    }

    private fun getMyLocation() {
        if (checkPermission(Manifest.permission.ACCESS_FINE_LOCATION) &&
            checkPermission(Manifest.permission.ACCESS_COARSE_LOCATION)
        ) {
            Log.d(TAG, "aaaaa")
            fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
                if (location != null) {
                    latlng = LatLng(location.latitude, location.longitude)
                    Log.d(TAG, latlng.toString())
                    binding.tvAddStoryLocation.text = resources.getString(R.string.latlng, location.latitude.toFloat(), location.longitude.toFloat())
                } else {
                    binding.tvAddStoryLocation.text = resources.getString(R.string.unknown)
                    Toast.makeText(
                        this@AddStoryActivity,
                        "Location is not found. Try Again",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    private val requestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { permissions ->
            when {
                permissions[Manifest.permission.ACCESS_FINE_LOCATION] ?: false -> {
                    // Precise location access granted.
                    getMyLocation()
                }
                permissions[Manifest.permission.ACCESS_COARSE_LOCATION] ?: false -> {
                    // Only approximate location access granted.
                    getMyLocation()
                }
                else -> {
                    // No location access granted.
                }
            }
        }

    //private val resolutionLauncher =
    //    registerForActivityResult(
    //        ActivityResultContracts.StartIntentSenderForResult()
    //    ) { result ->
    //        when (result.resultCode) {
    //            RESULT_OK ->
    //                Log.i(TAG, "onActivityResult: All location settings are satisfied.")
    //            RESULT_CANCELED ->
    //                Toast.makeText(
    //                    this@AddStoryActivity,
    //                    "Anda harus mengaktifkan GPS untuk menggunakan aplikasi ini!",
    //                    Toast.LENGTH_SHORT
    //                ).show()
    //        }
    //    }

    private fun checkPermission(permission: String): Boolean {
        return ContextCompat.checkSelfPermission(
            this,
            permission
        ) == PackageManager.PERMISSION_GRANTED
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

            if (latlng != null) {
                val service = ApiConfig.getApiService().addStory(
                    "Bearer ${loginInfo.token}", imageMultipart, description, latlng!!.latitude.toFloat(), latlng!!.longitude.toFloat()
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
                                resultIntent.putExtra(EXTRA_LATITUDE, latlng?.latitude?.toFloat())
                                resultIntent.putExtra(EXTRA_LONGITUDE, latlng?.longitude?.toFloat())
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
            }
        } else {
            binding.tvAddStoryError.text = resources.getString(R.string.please_add_image)
            binding.tvAddStoryError.visibility = View.VISIBLE
        }
    }

    companion object {
        const val TAG = "AddStoryActivity"
        const val CAMERA_X_RESULT = 200
        private val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.CAMERA, Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION)
        private const val REQUEST_CODE_PERMISSIONS = 10

        const val IMAGE_SOURCE_NONE = 0
        const val IMAGE_SOURCE_GALLERY = 1
        const val IMAGE_SOURCE_BACK_CAMERA = 2
        const val IMAGE_SOURCE_FRONT_CAMERA = 3

        const val RESULT_CODE = 111
        const val EXTRA_IMAGE_PATH = "extra_image_uri"
        const val EXTRA_DESCRIPTION = "extra_description"
        const val EXTRA_LATITUDE = "extra_latitude"
        const val EXTRA_LONGITUDE = "extra_longitude"
    }
}