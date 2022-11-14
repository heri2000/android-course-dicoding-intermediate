package com.dicoding.storyapp.ui

import android.content.res.Resources
import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.dicoding.storyapp.R
import com.dicoding.storyapp.adapter.StoryListAdapter
import com.dicoding.storyapp.databinding.ActivityMapsBinding
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.android.gms.maps.model.MarkerOptions

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapsBinding
    private val boundsBuilder = LatLngBounds.Builder()

    private val mainViewModel: MainViewModel by viewModels {
        ViewModelFactory(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        try {
            this.supportActionBar!!.hide()
        } catch (_: NullPointerException) {
        }

        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        mMap.uiSettings.isZoomControlsEnabled = true
        mMap.uiSettings.isIndoorLevelPickerEnabled = true
        mMap.uiSettings.isCompassEnabled = true
        mMap.uiSettings.isMapToolbarEnabled = true

        setMapStyle()
        getDataAndAddMarkers()
    }

    private fun setMapStyle() {
        try {
            val success =
                mMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(this, R.raw.map_style))
            if (!success) {
                Log.e(TAG, "Style parsing failed.")
            }
        } catch (exception: Resources.NotFoundException) {
            Log.e(TAG, "Can't find style. Error: ", exception)
        }
    }

    private fun getDataAndAddMarkers() {
        val adapter = StoryListAdapter()
        mainViewModel.story.observe(this) {
            adapter.submitData(lifecycle, it)
            adapter.snapshot().items.forEach { story ->
                if (
                    story.lat != null &&
                    story.lon != null &&
                    story.lat > -16 &&
                    story.lat < 13 &&
                    story.lon > 92 &&
                    story.lon < 148
                ) {
                    // Bounds dibatasi hanya untuk wilayah Indonesia saja, untuk tujuan demo
                    val latLng = LatLng(story.lat, story.lon)
                    mMap.addMarker(MarkerOptions()
                        .position(latLng)
                        .title(story.name)
                        .snippet(story.description))
                    boundsBuilder.include(latLng)
                }
            }
            if (adapter.snapshot().items.isNotEmpty()) {
                mMap.animateCamera(
                    CameraUpdateFactory.newLatLngBounds(
                        boundsBuilder.build(),
                        resources.displayMetrics.widthPixels,
                        resources.displayMetrics.heightPixels,
                        200
                    )
                )
            }
        }
    }

    companion object {
        const val TAG = "MapActivity"
    }
}