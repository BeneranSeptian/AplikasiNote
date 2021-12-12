package com.septian.nurfaozy.aplikasinote.ui.inputnote

import android.Manifest
import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.pm.PackageManager
import android.graphics.Color
import android.location.Location
import android.os.Build
import android.os.Bundle
import android.renderscript.ScriptGroup
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.google.android.gms.tasks.CancellationTokenSource
import com.google.android.gms.tasks.Task
import com.google.maps.android.PolyUtil
import com.septian.nurfaozy.aplikasinote.R
import com.septian.nurfaozy.aplikasinote.databinding.FragmentDashboardBinding
import com.septian.nurfaozy.aplikasinote.databinding.InputNoteFragmentBinding
import com.septian.nurfaozy.aplikasinote.ui.dashboard.DashboardViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.sql.Time
import java.util.*

class InputNoteFragment : Fragment() {
    private var directionLine: Polyline? = null
    private lateinit var inputNoteViewModel: InputNoteViewModel
    private var _binding: InputNoteFragmentBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private lateinit var mMap: GoogleMap
    private lateinit var  selectedLocation :LatLng
    private val viewModel by viewModels<InputNoteViewModel>()

    //untuk menghapus selected marked bisa menggunakan tip data hashmap
    private var mapMarker:HashMap<Int, Marker> = hashMapOf()
    private val fuseLocationClient: FusedLocationProviderClient by lazy{
        LocationServices.getFusedLocationProviderClient(requireContext())
    }

    private val cacellationTokenSource= CancellationTokenSource();

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = InputNoteFragmentBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync{googleMap ->

            mMap = googleMap
            EnableLocation()
            mMap.setOnMapClickListener {
//                mMap.clear()
//                EnableLocation()
//                selectedLocation = it
//                val snippet = String.format(Locale.getDefault(),
//                    "Lat: %1$.5f, Long:%2$.5f",
//                    it.latitude,it.longitude)
//                mMap.addMarker(
//                    MarkerOptions()
//                        .position((it))
//                        .title("tempat janjian")
//                        .snippet(snippet)
//                        .icon(
//                            BitmapDescriptorFactory.defaultMarker(
//                            BitmapDescriptorFactory.HUE_BLUE
//                        ))
//                )?.showInfoWindow()
                selectedLocation = it

                viewModel.OnAction(InputNoteAction.EnterLatLong(
                    startLocation = mapMarker[0]?.position ?: LatLng(0.0,0.0),
                    endLocation = it))
            }
        }

        return root
    }

    private val requestPermissionLauncher= registerForActivityResult(
        ActivityResultContracts.RequestPermission()){
            isGranted ->
            if(isGranted){
                EnableLocation()

            }else{
                Toast.makeText(requireContext(), "Permission Denied", Toast.LENGTH_SHORT).show()
            }
        }

    private fun IsPermissionGranted(): Boolean{
        return ContextCompat.checkSelfPermission(requireContext(),
        Manifest.permission.ACCESS_FINE_LOCATION)==PackageManager.PERMISSION_GRANTED
    }

    @SuppressLint("MissingPermission")
    private fun EnableLocation(){
        if (IsPermissionGranted()){
            mMap.isMyLocationEnabled = true
            RequestCurentLocation()
        }else{
            if(shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION)){
                requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
            }else{
                requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
            }
        }

    }

    @SuppressLint("MissingPermission")
    private fun RequestCurentLocation(){
        val currentLocationTask : Task<Location> = fuseLocationClient.getCurrentLocation(
            LocationRequest.PRIORITY_HIGH_ACCURACY, cacellationTokenSource.token
        )
        currentLocationTask.addOnCompleteListener{task->
            if(task.isSuccessful && task.result != null){
                val location = task.result
                val zoomLevel = 15f
                val tempLocation= LatLng(location.latitude, location.longitude)



                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(tempLocation,zoomLevel))
                mMap.addMarker(MarkerOptions()
                    .position(tempLocation)
                    .title("Lokasi Sekarang")
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED))
                )//ini buat naro current location ke mapMarker bi
            ?.let { currentMarker ->
                    mapMarker[0]=currentMarker }
                mMap.maxZoomLevel
            }

        }

    }
    @RequiresApi(Build.VERSION_CODES.O)
    private fun setOnMapClickListener(googleMap: GoogleMap){
        googleMap.setOnMapClickListener { selected ->
            selectedLocation = selected

            viewModel.OnAction(InputNoteAction.EnterLatLong(
                startLocation = mapMarker[0]?.position ?: LatLng(0.0,0.0),
                endLocation = selected))


        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        lifecycleScope.launch{
            viewModel.eventFlow.collectLatest {effect ->
                when (effect){
                    is InputNoteEffect.DrawPolyLine -> drawPolyline(effect.location, effect.shape)

                    is InputNoteEffect.SaveNoteFailed->{
                        Toast.makeText(
                            requireContext(),
                            effect.errorMassage, Toast.LENGTH_SHORT
                        ).show()
                    }



                    InputNoteEffect.SaveNoteSuccess -> {
                        binding.etContent.setText("")
                        binding.etTitle.setText("")
                        binding.txtHariValue.setText("Pilih Hari")
                        binding.txtJamValue.setText("Pilih Tanggal")
                        mMap.clear()
                        EnableLocation()


                        Toast.makeText(
                            requireContext(),
                            "Data saved!", Toast.LENGTH_SHORT
                        ).show()
                    }
                }

            }

        }

        binding.btnSave.setOnClickListener{

            viewModel.OnAction(
                InputNoteAction.SaveNote(
                binding.etTitle.text.toString(),
                binding.etContent.text.toString(),
                binding.txtHariValue.text.toString(),
                binding.txtJamValue.text.toString(),
                    selectedLocation

            )
            )
        }
        binding.txtHariValue.setOnClickListener{
            showDatePicker()
        }
        binding.txtJamValue.setOnClickListener{
            showTimePicker()
        }
    }

    private fun drawPolyline(location: LatLng, shape: String) {
        directionLine?.remove()

        val decode = PolyUtil.decode(shape)
        val polyline = PolylineOptions()
            .addAll(decode)
            .width(8f)
            .color(Color.BLUE)
            .geodesic(true)
        directionLine = mMap.addPolyline(polyline)
        mMap.setOnPolylineClickListener { polyline ->
            polyline.color = polyline.color xor 0x00ffffff
        }
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(decode.last(),15f))
        val snippet = String.format(
            Locale.getDefault(),
            "Lat:%1$.5f, Long: %2$.5f",
            location.latitude,
            location.longitude
        )
        if(mapMarker[1]==null){
            mMap.addMarker(
                MarkerOptions()
                    .position(LatLng(location.latitude, location.longitude))
                    .title("Tempat Janjian")
                    .snippet(snippet)
                    .icon(BitmapDescriptorFactory
                        .defaultMarker(BitmapDescriptorFactory.HUE_BLUE)
                    )
            )?.let { newAppointmentMarker ->
                newAppointmentMarker.showInfoWindow()
                mapMarker[1]=newAppointmentMarker
                    }

        }else {
            val appointmentMarker = mapMarker[1]
            appointmentMarker?.remove()
            mapMarker.remove(1)

            mMap.addMarker(
                MarkerOptions()
                    .position(LatLng(location.latitude, location.longitude))
                    .title("Tempat Janjian")
                    .snippet(snippet)
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE))
            )?.let { newAppointmentMarker ->
                newAppointmentMarker.showInfoWindow()
                mapMarker[1] = newAppointmentMarker
            }
        }

    }


    private fun showDatePicker(){
        val calendar = Calendar.getInstance()
        val mYear= calendar[Calendar.YEAR]
        val mMonth= calendar[Calendar.MONTH]
        val mDay= calendar[Calendar.DAY_OF_MONTH]





        val datePickerDialog = DatePickerDialog(requireContext(),
        {view, year, monthOfYear, dayOfMonth->
            binding.txtHariValue.text="$dayOfMonth-${monthOfYear + 1}-$year"
        },
        mYear,mMonth,mDay)
        datePickerDialog.datePicker.minDate= calendar.timeInMillis
        datePickerDialog.show()
    }

    private fun showTimePicker(){
        val calendar = Calendar.getInstance()
        val mHour = calendar[Calendar.HOUR]
        val mMinute = calendar[Calendar.MINUTE]

        val timePickerDialog= TimePickerDialog(requireContext(),
            {view, Hour, Minute->

                val finalHour = Hour.toString().padStart(2,'0')
                val finalMinute = Minute.toString().padStart(2,'0')
                binding.txtJamValue.text = "$finalHour:$finalMinute"
            },mHour,mMinute,true)
        timePickerDialog.show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


}