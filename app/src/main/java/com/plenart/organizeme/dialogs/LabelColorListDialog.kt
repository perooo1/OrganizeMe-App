package com.plenart.organizeme.dialogs

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.plenart.organizeme.adapters.LabelColorListAdapter
import com.plenart.organizeme.databinding.DialogListBinding
import com.plenart.organizeme.interfaces.LabelColorClickedInterface

abstract class LabelColorListDialog(context: Context,
                                    private var list: ArrayList<String>,
                                    private val title: String = "",
                                    private var mSelectedColor: String = ""
) : Dialog(context) {

    private lateinit var dialogListBinding: DialogListBinding
    private lateinit var labelColorAdapter: LabelColorListAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        dialogListBinding = DialogListBinding.inflate(layoutInflater);

        setContentView(dialogListBinding.root);
        setCanceledOnTouchOutside(true);
        setCancelable(true);

        setUpRecyclerView(dialogListBinding);

    }

    private fun setUpRecyclerView(binding: DialogListBinding ){

        val listener = object : LabelColorClickedInterface{
            override fun onClick(position: Int, color: String) {
                dismiss()
                onItemSelected(color)
            }

        }

        labelColorAdapter = LabelColorListAdapter(list, mSelectedColor, listener)
        binding.tvTitle.text = title

        labelColorAdapter = LabelColorListAdapter(list, mSelectedColor, listener)

        binding.apply {
            tvTitle.text = title
            rvList.apply{
                layoutManager = LinearLayoutManager(context)
                adapter = labelColorAdapter
            }

        }

    }

    protected abstract fun onItemSelected(color: String)
}