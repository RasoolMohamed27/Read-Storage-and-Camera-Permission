package com.rasoolmohamed.profile.ui.main

import android.Manifest
import android.app.Activity
import android.app.DatePickerDialog
import android.app.Dialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.provider.Settings
import android.provider.SyncStateContract
import android.util.Log
import android.view.*
import android.widget.ArrayAdapter
import android.widget.DatePicker
import androidx.fragment.app.Fragment
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.rasoolmohamed.profile.BuildConfig
import com.rasoolmohamed.profile.R
import com.rasoolmohamed.profile.databinding.ProfileFragmentBinding
import com.rasoolmohamed.profile.utils.Constants
import com.rasoolmohamed.profile.utils.FileUtils
import com.rasoolmohamed.profile.utils.SharedPreferencesUtils
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

class ProfileFragment : Fragment(R.layout.profile_fragment) {

    companion object {
        fun newInstance() = ProfileFragment()
    }

    private val TAG = ProfileFragment::class.java.simpleName

    private lateinit var profileFragmentBinding: ProfileFragmentBinding

    private lateinit var galleryLl: LinearLayout
    private lateinit var cameraLl: LinearLayout
    private lateinit var removePhotoLl: LinearLayout

    lateinit var file: File
    lateinit var imageBitmap: Bitmap

    private lateinit var requestStoragePermissionLauncher: ActivityResultLauncher<String>
    private lateinit var galleryLauncher: ActivityResultLauncher<Intent>

    private lateinit var requestCameraPermissionLauncher: ActivityResultLauncher<String>
    private lateinit var cameraLauncher: ActivityResultLauncher<Intent>

    private var cal = Calendar.getInstance()
    private lateinit var joinDateSetListener: DatePickerDialog.OnDateSetListener


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        registerStoragePermission()
        registerGalleryLauncher()

        registerCameraPermission()
        registerCameraLauncher()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        profileFragmentBinding = ProfileFragmentBinding.bind(view)

        profileFragmentBinding.imageViewCircleNoStroke.setOnClickListener {
            showImageUploadOptions()
        }

        profileFragmentBinding.updatebtn.setOnClickListener {
            profileFragmentBinding.name.setText(profileFragmentBinding.nameedt.text)
        }

        GetProfileImage()

        setHintCustomization()
        setGender()
        setJoinDate()
        onClickJoinDate()
    }

    private fun GetProfileImage() {
        val imagePath = SharedPreferencesUtils.getProfilePath(requireActivity())
        if (imagePath == null) {
            return
        }
        val imgFile = File(imagePath)
        if (imgFile.exists()) {
            val myBitmap = BitmapFactory.decodeFile(imgFile.absolutePath)
            profileFragmentBinding.imageViewCircleNoStroke.setImageBitmap(myBitmap)
            profileFragmentBinding.imageViewCircleNoStroke.setScaleType(ImageView.ScaleType.CENTER_CROP)
        }
    }

    private fun registerCameraPermission() {
        requestCameraPermissionLauncher =
            registerForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
                if (granted) {
                    Log.d(TAG, "registerCameraPermission - Camera Permission Granted")
                    openCamera()
                } else {
                    Log.d(TAG, "registerCameraPermission - Camera Permission NOT Granted")
                    requestCameraPermission()
                }
            }
    }


    private fun registerStoragePermission() {
        requestStoragePermissionLauncher =
            registerForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
                if (granted) {
                    Log.d(TAG, "registerStoragePermission - Storage Permission Granted")
                    viewGallery()
                } else {
                    Log.d(TAG, "registerStoragePermission - Storage Permission NOT Granted")
                    requestStoragePermission()
                }
            }
    }

    private fun registerCameraLauncher() {
        cameraLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == Activity.RESULT_OK) {
                    val data: Intent? = result.data
                    if (data == null) {
                        return@registerForActivityResult
                    }
                    val extras = data.extras
                    imageBitmap = extras!!["data"] as Bitmap
                    file = FileUtils.createFile(requireContext(),
                        getString(R.string.app_name),
                        "my_profile_image.png"
                    )
                    //FileUtils.saveBitmap(imageBitmap, file);
                    val imageLocalPath = FileUtils.saveImageToInternalStorage(file, imageBitmap)

                    SharedPreferencesUtils.setProfilePath(requireActivity(), imageLocalPath)
                    profileFragmentBinding.imageViewCircleNoStroke.setImageBitmap(imageBitmap)
                    profileFragmentBinding.imageViewCircleNoStroke.setScaleType(ImageView.ScaleType.CENTER_CROP)
                }
            }
    }

    private fun registerGalleryLauncher() {
        galleryLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == Activity.RESULT_OK) {
                    val data: Intent? = result.data
                    if (data == null) {
                        return@registerForActivityResult
                    }
                    val uri = data.data
                    var imageLocalPath = File(FileUtils.getPathReal(requireActivity(), uri!!))

                    file = imageLocalPath.absoluteFile

                    SharedPreferencesUtils.setProfilePath(requireActivity(), imageLocalPath.absolutePath)
                    Glide.with(requireActivity()).load(uri)
                        .into(profileFragmentBinding.imageViewCircleNoStroke)
                    profileFragmentBinding.imageViewCircleNoStroke.setScaleType(ImageView.ScaleType.CENTER_CROP)
                }
            }
    }


    private fun showImageUploadOptions() {
        val mDialog = activity.let { Dialog(it!!) }
        mDialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        mDialog.setContentView(R.layout.dialog_profile_image_option)
        mDialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        //val mAlertMessageTv = mDialog.findViewById<View>(R.id.id_alert_tv) as TextView
        //mAlertMessageTv.text = message
        galleryLl = mDialog.findViewById<View>(R.id.id_gallery_ll) as LinearLayout
        cameraLl = mDialog.findViewById<View>(R.id.id_camera_ll) as LinearLayout
        removePhotoLl = mDialog.findViewById<View>(R.id.id_remove_photo_ll) as LinearLayout

        galleryLl.setOnClickListener {
            CallStoragePermission()
            mDialog.dismiss()
        }

        cameraLl.setOnClickListener {
            CallCameraPermission()
            mDialog.dismiss()
        }

        removePhotoLl.setOnClickListener {
            CallRemovePhoto()
            mDialog.dismiss()
        }

        mDialog.setCancelable(true)
        mDialog.show()
        val metrics = resources.displayMetrics
        val width = metrics.widthPixels
        val height = metrics.heightPixels
        mDialog.window!!.setLayout(
            width,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )

    }

    fun CallStoragePermission() {

        if (!Status_checkReadExternalStoragePermission()) {
            requestStoragePermissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
        } else {
            viewGallery()
        }
    }

    private fun Status_checkReadExternalStoragePermission(): Boolean {
        val permissionState = ActivityCompat.checkSelfPermission(
            requireActivity(),
            Manifest.permission.READ_EXTERNAL_STORAGE
        )
        return permissionState == PackageManager.PERMISSION_GRANTED
    }

    private fun requestCameraPermission() {

        when {
            ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED -> {

                Log.d(TAG, "requestCameraPermission - Camera Permission Granted")
                openCamera()

                // The permission is granted
                // you can go with the flow that requires permission here
            }
            shouldShowRequestPermissionRationale(Manifest.permission.CAMERA) -> {
                // This case means user previously denied the permission
                // So here we can display an explanation to the user
                // That why exactly we need this permission
                Log.d(TAG, "requestCameraPermission - Camera Permission NOT Granted")
                showPermissionAlert(
                    getString(R.string.camera_permission),
                    getString(R.string.camera_permission_denied),
                    getString(R.string.ok_caps),
                    getString(R.string.cancel_caps)
                ) { requestCameraPermissionLauncher.launch(Manifest.permission.CAMERA) }
            }
            else -> {
                // Everything is fine you can simply request the permission

                showPermissionAlert(
                    getString(R.string.camera_permission),
                    getString(R.string.camera_permission_denied),
                    getString(R.string.settings_caps),
                    getString(R.string.cancel_caps)
                ) {
                    val intent = Intent()
                    intent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                    val uri = Uri.fromParts(
                        "package",
                        BuildConfig.APPLICATION_ID, null
                    )
                    intent.data = uri
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                    startActivity(intent)
                }

            }
        }
    }


    private fun requestStoragePermission() {

        when {
            ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.READ_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_GRANTED -> {

                Log.d(TAG, "requestStoragePermission - Storage Permission Granted")
                viewGallery()

                // The permission is granted
                // you can go with the flow that requires permission here
            }
            shouldShowRequestPermissionRationale(Manifest.permission.READ_EXTERNAL_STORAGE) -> {
                // This case means user previously denied the permission
                // So here we can display an explanation to the user
                // That why exactly we need this permission
                Log.d(TAG, "requestStoragePermission - Storage Permission NOT Granted")
                showPermissionAlert(
                    getString(R.string.read_storage_permission_required),
                    getString(R.string.storage_permission_denied),
                    getString(R.string.ok_caps),
                    getString(R.string.cancel_caps)
                ) { requestStoragePermissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE) }
            }
            else -> {
                // Everything is fine you can simply request the permission

                showPermissionAlert(
                    getString(R.string.read_storage_permission_required),
                    getString(R.string.storage_permission_denied),
                    getString(R.string.settings_caps),
                    getString(R.string.cancel_caps)
                ) {
                    val intent = Intent()
                    intent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                    val uri = Uri.fromParts(
                        "package",
                        BuildConfig.APPLICATION_ID, null
                    )
                    intent.data = uri
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                    startActivity(intent)
                }

            }
        }
    }

    private fun showPermissionAlert(
        title: String,
        message: String,
        ok: String,
        cancel: String,
        function: () -> Unit
    ) {
        val mDialog = requireActivity().let { Dialog(it) }
        mDialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        mDialog.setContentView(R.layout.dialog_permission_alert)
        mDialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        val mTitleTv = mDialog.findViewById<View>(R.id.id_title_tv) as AppCompatTextView
        mTitleTv.text = title

        val mMessageTv = mDialog.findViewById<View>(R.id.id_message_tv) as AppCompatTextView
        mMessageTv.text = message

        val mNoBtn = mDialog.findViewById<View>(R.id.no_btn) as AppCompatTextView
        mNoBtn.text = cancel

        val mYesBtn = mDialog.findViewById<View>(R.id.yes_btn) as AppCompatTextView
        mYesBtn.text = ok

        mYesBtn.setOnClickListener {
            function.invoke()
            mDialog.dismiss()
        }

        mNoBtn.setOnClickListener { mDialog.dismiss() }

        mDialog.setCancelable(true)
        mDialog.show()
        val metrics = resources.displayMetrics
        val width = metrics.widthPixels
        val height = metrics.heightPixels
        mDialog.window!!.setLayout(
            width,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
    }

    fun viewGallery() {
        val intentDocument = Intent(Intent.ACTION_GET_CONTENT)
        intentDocument.type = "image/*"
        intentDocument.putExtra(
            Constants.REQUEST_CODE,
            Constants.REQUEST_PHOTO_FROM_GALLERY
        )
        galleryLauncher.launch(intentDocument)
    }

    fun openCamera() {
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        takePictureIntent.putExtra(
            Constants.REQUEST_CODE,
            Constants.REQUEST_PERMISSIONS_REQUEST_CODE_CAMERA
        )
        cameraLauncher.launch(takePictureIntent)
    }

    fun CallCameraPermission() {
        if (!Status_checkCameraPermission()) {
            requestCameraPermissionLauncher.launch(Manifest.permission.CAMERA)
        } else {
            openCamera()
        }
    }

    private fun Status_checkCameraPermission(): Boolean {
        val camera = ActivityCompat.checkSelfPermission(
            requireActivity(),
            Manifest.permission.CAMERA
        )

        return camera == PackageManager.PERMISSION_GRANTED
    }

    fun CallRemovePhoto() {
        SharedPreferencesUtils.setProfilePath(requireContext(), null)
        profileFragmentBinding.imageViewCircleNoStroke.setImageDrawable(
            ContextCompat.getDrawable(
                requireContext(),
                R.drawable.ic_profile
            )
        )
        profileFragmentBinding.imageViewCircleNoStroke.setScaleType(ImageView.ScaleType.CENTER_INSIDE)
    }

    private fun setHintCustomization() {
        profileFragmentBinding.namelayout.setHintTextAppearance(R.style.ProfileEdt_Hint)
        profileFragmentBinding.emaillayout.setHintTextAppearance(R.style.ProfileEdt_Hint)
        profileFragmentBinding.mobilelayout.setHintTextAppearance(R.style.ProfileEdt_Hint)
        profileFragmentBinding.genderlayout.setHintTextAppearance(R.style.ProfileEdt_Hint)
        profileFragmentBinding.joindatelayout.setHintTextAppearance(R.style.ProfileEdt_Hint)

        profileFragmentBinding.name.setText("Rasool Mohamed")
        profileFragmentBinding.nameedt.setText("Rasool Mohamed")
        profileFragmentBinding.emailedt.setText("rmsoftwaredeveloper@gmail.com")
        profileFragmentBinding.mobileedt.setText("+91 12345 67890")
        profileFragmentBinding.genderedt.setText("Male")
        profileFragmentBinding.joindateedt.setText("1 Jan 2015, (Thu)")

    }

    private fun setGender() {
        val adapter = ArrayAdapter<String>(
            requireContext(),
            android.R.layout.simple_list_item_1,
            genderList()
        )

        profileFragmentBinding.genderedt.setAdapter(adapter)
    }

    private fun genderList(): ArrayList<String> {
        val mGenderList = ArrayList<String>()
        mGenderList.add("Male")
        mGenderList.add("Female")
        return mGenderList

    }

    private fun onClickJoinDate() {

        profileFragmentBinding.joindateedt.setOnClickListener(object : View.OnClickListener {
            override fun onClick(view: View) {
                DatePickerDialog(requireContext(),
                    joinDateSetListener,
                    // set DatePickerDialog to point to today's date when it loads up
                    cal.get(Calendar.YEAR),
                    cal.get(Calendar.MONTH),
                    cal.get(Calendar.DAY_OF_MONTH)).show()
            }

        })
    }


    private fun setJoinDate() {
        // create an OnDateSetListener
        joinDateSetListener = object : DatePickerDialog.OnDateSetListener {
            override fun onDateSet(view: DatePicker, year: Int, monthOfYear: Int,
                                   dayOfMonth: Int) {
                cal.set(Calendar.YEAR, year)
                cal.set(Calendar.MONTH, monthOfYear)
                cal.set(Calendar.DAY_OF_MONTH, dayOfMonth)

                updateJoinDateInView()
            }
        }
    }

    private fun updateJoinDateInView() {
        //val myFormat = "dd/MM/yyyy" // mention the format you need
        val myFormat = "d MMM yyyy, (EEE)" // mention the format you need
        val sdf = SimpleDateFormat(myFormat, Locale.US)
        val date = sdf.format(cal.getTime())
        profileFragmentBinding.joindateedt.setText(date)
    }

}