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
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import com.bumptech.glide.Glide
import com.plenart.organizeme.R
import com.plenart.organizeme.databinding.FragmentCreateBoardBinding
import com.plenart.organizeme.utils.Constants
import com.plenart.organizeme.viewModels.CreateBoardViewModel
import java.io.IOException


class CreateBoardFragment : Fragment() {
    private lateinit var binding : FragmentCreateBoardBinding
    private val viewModel: CreateBoardViewModel by activityViewModels()

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

        setUpActionBar()

        initObservers()
        initListeners()

        getIntentData()
        getBoardName()
    }


    private fun getIntentData() {
        val arguments = requireArguments()
        if(arguments.containsKey(Constants.NAME)){
            viewModel.setUserName(arguments.getString(Constants.NAME).toString())
        }
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
            if(viewModel.selectedImageFileUri?.value != null){
                viewModel.uploadBoardImage()
            }
            else{
                viewModel.createBoard()
            }
        }
    }

    private fun initObservers() {
        initBoardName()
        initBoardCreated()
    }

    private fun initBoardCreated() {
        viewModel.boardCreated.observe(viewLifecycleOwner, Observer {
            if (it){
                boardCreatedSuccessfully()
            }
            else{
                Log.i("boardCreatedObserver","Board creation failed: it==false")
            }
        })
    }

    private fun initBoardName() {
        viewModel.boardName.observe(viewLifecycleOwner, Observer {
            if(it == null || it.isEmpty()){
                Toast.makeText(context, "please provide a board name", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun getBoardName(){
        binding.etBoardNameCreateBoardActivity.doAfterTextChanged {
            viewModel.setBoardName(it.toString())
        }
    }

    private fun boardCreatedSuccessfully(){
        //hideProgressDialog()
        //setResult(Activity.RESULT_OK)
        //finish()
    }

    private fun setUpActionBar(){
        /*
        setSupportActionBar(createBoardBinding.toolbarCreateBoardActivity)

        createBoardBinding.toolbarCreateBoardActivity.setNavigationIcon(R.drawable.ic_action_navigation_menu)

        val actionBar = supportActionBar
        if(actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_white_color_back_24dp)
            actionBar.title = resources.getString(R.string.create_board_title)
        }
        createBoardBinding.toolbarCreateBoardActivity.setNavigationOnClickListener{
            onBackPressed()
        }
        */
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
            viewModel.setFileExtension(Constants.getFileExtension(requireActivity(),data.data))

            try{
                Glide.with(this)
                    .load(viewModel.selectedImageFileUri?.value)
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