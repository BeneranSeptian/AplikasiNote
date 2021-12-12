package com.septian.nurfaozy.aplikasinote.ui.home

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.os.Build
import android.os.Bundle
import android.text.method.TextKeyListener.clear
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationRequest.PRIORITY_HIGH_ACCURACY
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.LocationSource
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.tasks.CancellationTokenSource
import com.google.android.gms.tasks.Task
import com.septian.nurfaozy.aplikasinote.R
import com.septian.nurfaozy.aplikasinote.databinding.FragmentHomeBinding
import java.util.*

class HomeFragment : Fragment() {

    private lateinit var homeViewModel: HomeViewModel
    private var _binding: FragmentHomeBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private lateinit var mMap:GoogleMap


    private val fuseLocationClient:FusedLocationProviderClient by lazy{
        LocationServices.getFusedLocationProviderClient(requireContext())
    }

    private val cacellationTokenSource=CancellationTokenSource();

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        homeViewModel =
            ViewModelProvider(this).get(HomeViewModel::class.java)

        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root


        val mapFragment=childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync{googleMap->

            mMap=googleMap
            EnableLocation()
            mMap.setOnMapClickListener {
                mMap.clear()
                EnableLocation()
                val snippet = String.format(Locale.getDefault(),
                    "Lat: %1$.5f, Long:%2$.5f",
                    it.latitude,it.longitude)
                mMap.addMarker(
                    MarkerOptions()
                        .position((it))
                        .title("tempat janjian")
                        .snippet(snippet)
                        .icon(BitmapDescriptorFactory.defaultMarker(
                            BitmapDescriptorFactory.HUE_BLUE
                        ))
                )?.showInfoWindow()
            }


        }

        return root
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.btnShare.setOnClickListener{
            homeViewModel.insertNote()
        }
//        binding.btnShare.setOnClickListener(){
//            try{
//                val intent= Intent()
//                intent.apply {
//                    action=Intent.ACTION_SEND
//                    putExtra(Intent.EXTRA_TEXT,"Mencoba share text")
//                    type="text/plain"
//                }
//                startActivity(Intent.createChooser(intent,"Share Dengan"))
//            }catch (e:Exception){
//                Log.d("Error: ", e.message.orEmpty())
//            }
//        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun isPermisionGranted():Boolean{
        return ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION)==PackageManager.PERMISSION_GRANTED
    }

    @SuppressLint("MissingPermision")
    private fun EnableLocation(){
        if(isPermisionGranted()){
            mMap.isMyLocationEnabled=true
            RequestCurentLocation()
        }else{
            ActivityCompat.requestPermissions(requireActivity(), arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION),1)
        }

    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (requestCode==1){
            if(grantResults.contains(PackageManager.PERMISSION_GRANTED)){
                EnableLocation()



            }

        }


    }

    //untuk mendapatkan posisi saat ini
    @SuppressLint("MissingPermission")
    private fun RequestCurentLocation(){
        val currentLocationTask : Task<Location> = fuseLocationClient.getCurrentLocation(
            PRIORITY_HIGH_ACCURACY, cacellationTokenSource.token
        )
        currentLocationTask.addOnCompleteListener{task->
            if(task.isSuccessful && task.result != null){
                val location = task.result
                val zoomLevel = 15f
                val tempLocation=LatLng(location.latitude, location.longitude)



                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(tempLocation,zoomLevel))
                mMap.addMarker(MarkerOptions().position(tempLocation).title("Lokasi Sekarang"))

            }
        }

    }


    override fun onStop() {
        super.onStop()
        cacellationTokenSource.token
    }
}