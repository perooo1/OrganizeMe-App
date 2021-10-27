package com.plenart.organizeme.fragments

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.plenart.organizeme.databinding.FragmentMyProfileBinding
import com.plenart.organizeme.utils.Constants
import com.plenart.organizeme.utils.loadImage
import com.plenart.organizeme.viewModels.MyProfileViewModel
import java.io.IOException


class MyProfileFragment : Fragment() {

    private lateinit var binding: FragmentMyProfileBinding
    private val viewModel: MyProfileViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentMyProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initObservers()
        initListeners()
        getValues()

    }

    private fun initListeners() {
        binding.ivUserImage.setOnClickListener {
            if (Constants.isReadExternalStorageAllowed(requireActivity())) {
                Constants.showImageChooser(requireActivity())
            } else {
                Constants.requestStoragePermission(requireActivity())
            }
        }

        binding.btnUpdateMyProfileActivity.setOnClickListener {
            if (viewModel.getSelectedImageFileUri() != null) {
                viewModel.uploadUserImage()
            }
            if (viewModel.getMobile().toString().isEmpty()) {
                Toast.makeText(context, "Please provide a phone number", Toast.LENGTH_SHORT).show()
            } else {
                viewModel.updateUserProfileData()
            }
        }
    }

    private fun getValues() {
        getName()
        getMobile()
    }

    private fun initObservers() {
        initUser()
    }

    private fun initUser() {
        viewModel.user.observe(viewLifecycleOwner, Observer { newUser ->
            if (newUser != null) {
                setUserDataInUI()
            } else {
                Log.i("UserObserver", "error observing user")
            }
        })
    }

    private fun getName() {
        binding.etNameMyProfileActivity.addTextChangedListener {
            viewModel.setName(it.toString())
        }
    }

    private fun getMobile() {
        binding.etMobileMyProfileActivity.doAfterTextChanged {
            if (binding.etMobileMyProfileActivity.text!!.isEmpty()) {
                Toast.makeText(context, "Please provide a phone number", Toast.LENGTH_SHORT).show()
            } else {
                viewModel.setMobile(it.toString().toLong())
            }
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && requestCode == Constants.PICK_IMAGE_REQUEST_CODE && data!!.data != null) {

            viewModel.setSelectedImageFileUri(data.data)
            viewModel.setFileExtension(Constants.getFileExtension(requireActivity(), data.data).toString())

            try {
                binding.ivUserImage.loadImage(viewModel.getSelectedImageFileUri().toString())

            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }

    private fun setUserDataInUI() {

        binding.apply {

            ivUserImage.loadImage(viewModel.user.value?.image.toString())
            viewModel.apply {
                etNameMyProfileActivity.setText(user.value?.name)
                etEmailMyProfileActivity.setText(user.value?.email)

                if (user.value?.mobile != 0L) {
                    etMobileMyProfileActivity.setText(user.value?.mobile.toString())
                }

            }

        }

    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == Constants.READ_STORAGE_PERMISSION_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Constants.showImageChooser(requireActivity())
            }
        } else {
            Toast.makeText(
                context,
                "permission denied. You can change it in settings",
                Toast.LENGTH_LONG
            ).show()
        }
    }

}