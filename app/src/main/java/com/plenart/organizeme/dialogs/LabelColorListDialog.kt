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

    private lateinit var dialogListBinding: DialogListBinding;

    private var adapter: LabelColorListAdapter? = null;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        dialogListBinding = DialogListBinding.inflate(layoutInflater);

        setContentView(dialogListBinding.root);
        setCanceledOnTouchOutside(true);
        setCancelable(true);

        setUpRecyclerView(dialogListBinding);

    }

    private fun setUpRecyclerView(binding: DialogListBinding ){
        binding.tvTitle.text = title;
        binding.rvList.layoutManager = LinearLayoutManager(context);
        adapter = LabelColorListAdapter(context,list, mSelectedColor);
        binding.rvList.adapter = adapter;

        adapter!!.onItemClickListener = object: LabelColorClickedInterface{
            override fun onClick(position: Int, color: String) {
                dismiss();
                onItemSelected(color)

            }

        }

    }

    protected abstract fun onItemSelected(color: String)
}