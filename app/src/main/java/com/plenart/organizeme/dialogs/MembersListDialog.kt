package com.plenart.organizeme.dialogs

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.plenart.organizeme.adapters.MemberListItemAdapter
import com.plenart.organizeme.databinding.DialogListBinding
import com.plenart.organizeme.interfaces.SelectedMembersClickInterface
import com.plenart.organizeme.models.User

abstract class MembersListDialog(context: Context, private var list: ArrayList<User>, private val  title: String = ""
) :Dialog(context){

    private lateinit var dialogListBinding: DialogListBinding;
    private lateinit var memberListItemAdapter: MemberListItemAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    
        dialogListBinding = DialogListBinding.inflate(layoutInflater);
        setContentView(dialogListBinding.root);
        setCanceledOnTouchOutside(true);
        setCancelable(true);
        setUpRecyclerView(dialogListBinding);

    }

    private fun setUpRecyclerView(binding: DialogListBinding) {
        binding.tvTitle.text = title;

        if(list.size > 0){
            memberListItemAdapter = MemberListItemAdapter(list);

            binding.rvList.apply {
                layoutManager = LinearLayoutManager(context);
                adapter = memberListItemAdapter
            }

            memberListItemAdapter.setOnClickListener(object: SelectedMembersClickInterface{
                override fun onClick(position: Int, user: User, action: String) {
                    dismiss();
                    onItemSelected(user,action);
                }

            })

        }
    }

    protected abstract fun onItemSelected(user: User, action: String);


}