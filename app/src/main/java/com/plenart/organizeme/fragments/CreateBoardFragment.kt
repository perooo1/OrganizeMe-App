package com.plenart.organizeme.fragments

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.plenart.organizeme.R
import com.plenart.organizeme.databinding.FragmentCreateBoardBinding
import com.plenart.organizeme.utils.Constants
import com.plenart.organizeme.viewModels.CreateBoardViewModel
import java.io.IOException

class CreateBoardFragment : Fragment() {
    private lateinit var binding : FragmentCreateBoardBinding
    private val viewModel: CreateBoardViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentCreateBoardBinding.inflate(inflater,container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initListeners()

        getArgs()
        getBoardName()
    }


    private fun getArgs() {
        val args: CreateBoardFragmentArgs by navArgs()
        viewModel.setUserName(args.userName)

    }

    private fun initListeners() {
        binding.ivBoardImageCreateBoardActivity.setOnClickListener {
            if(Constants.isReadExternalStorageAllowed(requireActivity())){
                Constants.showImageChooser(requireActivity())
            }
            else{
                Constants.requestStoragePermission(requireActivity())
            }
        }

        binding.btnCreateCreateBoardActivity.setOnClickListener{
            if(viewModel.getSelectedImageFileUri() != null){
                viewModel.uploadBoardImage()
            }
            else{
                if(viewModel.getBoardName().isEmpty()){
                    Toast.makeText(requireContext(),"Please enter Board name!",Toast.LENGTH_SHORT).show()
                }
                else{
                    viewModel.createBoard()
                }
            }
        }
    }

    private fun getBoardName(){
        binding.etBoardNameCreateBoardActivity.doAfterTextChanged {
            viewModel.setBoardName(it.toString())
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if(requestCode == Constants.READ_STORAGE_PERMISSION_CODE){
            if(grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                Constants.showImageChooser(requireActivity())
            }
        }
        else{
            Toast.makeText(context,"permission denied. You can change it in settings", Toast.LENGTH_LONG).show()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(resultCode == Activity.RESULT_OK && requestCode == Constants.PICK_IMAGE_REQUEST_CODE && data!!.data != null){
            viewModel.setSelectedImageFileUri(data.data);
            viewModel.setFileExtension(Constants.getFileExtension(requireActivity(),data.data).toString())

            try{
                Glide.with(this)
                    .load(viewModel.getSelectedImageFileUri())
                    .centerCrop()
                    .placeholder(R.drawable.ic_board_place_holder)
                    .into(binding.ivBoardImageCreateBoardActivity)

            }
            catch (e: IOException){
                e.printStackTrace()
            }
        }

    }
}