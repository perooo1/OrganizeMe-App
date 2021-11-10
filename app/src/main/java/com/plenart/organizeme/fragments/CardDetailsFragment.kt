package com.plenart.organizeme.fragments

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.Toast
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.GridLayoutManager
import com.plenart.organizeme.R
import com.plenart.organizeme.adapters.CardMembersListItemAdapter
import com.plenart.organizeme.databinding.FragmentCardDetailsBinding
import com.plenart.organizeme.dialogs.LabelColorListDialog
import com.plenart.organizeme.dialogs.MembersListDialog
import com.plenart.organizeme.interfaces.MemberItemClickInterface
import com.plenart.organizeme.models.SelectedMembers
import com.plenart.organizeme.models.User
import com.plenart.organizeme.utils.Constants
import com.plenart.organizeme.utils.gone
import com.plenart.organizeme.utils.visible
import com.plenart.organizeme.viewModels.CardDetailsViewModel
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


class CardDetailsFragment : Fragment() {
    private lateinit var binding: FragmentCardDetailsBinding
    private lateinit var cardMembersListItemAdapter:CardMembersListItemAdapter
    private val viewModel: CardDetailsViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentCardDetailsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        getArgs()

        initObservers()
        initListeners()

        setUpCardNameEt()
        setUpSelectedColor()
        setUpDueDate()
        setHasOptionsMenu(true)

        getCardName()

    }

    private fun setUpDueDate() {
        viewModel.setSelectedDueDate(
            viewModel.getBoardDetails()?.taskList
                ?.get(viewModel.getTaskListPosition())
                ?.cards!![viewModel.getCardPosition()]
                .dueDate
        )

        if (viewModel.getSelectedDueDate() > 0) {
            val simpleDateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH)
            val selectedDate = simpleDateFormat.format(Date(viewModel.getSelectedDueDate()))
            binding.tvSelectDueDate.text = selectedDate

        }
    }

    private fun initListeners() {
        binding.apply {
            btnUpdateCardDetails.setOnClickListener {
                if (etNameCardDetails.text.toString().isNotEmpty()) {
                    viewModel.updateCardDetails()
                } else {
                    Toast.makeText(context,resources.getString(R.string.please_provide_a_card_name), Toast.LENGTH_SHORT).show()
                }
            }

            tvSelectLabelColor.setOnClickListener {
                labelColorListDialog()
            }

            tvSelectMembers.setOnClickListener {
                membersListDialog()
            }

            tvSelectDueDate.setOnClickListener {
                showDatePicker()
            }

        }

    }

    private fun getCardName() {
        binding.etNameCardDetails.doAfterTextChanged {
            viewModel.setCardName(it.toString())
        }
    }

    private fun setUpSelectedColor() {
        viewModel.setSelectedColor(
            viewModel.getBoardDetails()?.taskList
                ?.get(viewModel.getTaskListPosition())
                ?.cards!![viewModel.getCardPosition()]
                .labelColor
        )

        if (viewModel.getSelectedColor().isNotEmpty()) {
            setColor()
        }
    }

    private fun initObservers() {
        initAssignedMembers()
    }

    private fun initAssignedMembers() {
        viewModel.assignedMemberDetailList.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            if (it != null && it.isNotEmpty()) {
                setUpSelectedMembersList()
            } else {
                Log.i("assignedMembersObserver", "assigned mems is empty or null")
            }

        })
    }

    private fun setUpCardNameEt() {
        binding.apply {
            etNameCardDetails.apply {
                setText(
                    viewModel.getBoardDetails()
                        ?.taskList?.get(viewModel.getTaskListPosition())
                        ?.cards?.get(viewModel.getCardPosition())
                        ?.name
                )

                setSelection(
                    etNameCardDetails
                        .text
                        .toString()
                        .length
                )
            }
        }

    }

    private fun getArgs() {
        val args: CardDetailsFragmentArgs by navArgs()
        val assignedMembers = ArrayList((args.assignedUsers).toList())

        viewModel.apply {
            setBoardDetails(args.boardDetails)
            setAssignedMembers(assignedMembers as ArrayList<User>)
            setTaskListPosition(args.taskListItemPosition)
            setCardPosition(args.cardListItemPositoni)
        }

    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_delete_card, menu);
        return super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_delete_card -> {
                alertDialogForDeleteCard(
                    viewModel.getBoardDetails()
                        ?.taskList?.get(viewModel.getTaskListPosition())
                        ?.cards?.get(viewModel.getCardPosition())
                        ?.name!!
                )
                return true
            }
        }

        return super.onOptionsItemSelected(item)
    }

    private fun membersListDialog() {

        val cardAssignedMembersList = viewModel.getBoardDetails()
            ?.taskList
            ?.get(viewModel.getTaskListPosition())
            ?.cards
            ?.get(viewModel.getCardPosition())
            ?.assignedTo

        viewModel.assignedMemberDetailList.value?.apply {
            if (cardAssignedMembersList!!.size > 0) {
                for (i in indices) {
                    for (j in cardAssignedMembersList) {
                        if (get(i).id == j) {
                            get(i).selected = true
                        }
                    }
                }
            } else {
                for (i in indices) {
                    get(i).selected = false
                }
            }

        }

        val listDialog = object : MembersListDialog(
            requireContext(),
            viewModel.assignedMemberDetailList.value!!,
            resources.getString(R.string.str_select_member)
        ) {
            override fun onItemSelected(user: User, action: String) {
                if (action == Constants.SELECT) {

                    viewModel.apply {
                        getBoardDetails()?.taskList?.get(getTaskListPosition())?.cards?.get(
                            getCardPosition()
                        ).apply {
                            if (!(this?.assignedTo?.contains(user.id)!!)) {
                                this.assignedTo.add(user.id)
                            }

                        }
                    }

                } else {

                    viewModel.apply {
                        getBoardDetails()?.taskList?.get(getTaskListPosition())?.cards?.get(
                            getCardPosition()
                        ).apply {
                            this?.assignedTo?.remove(user.id)
                        }

                        assignedMemberDetailList.value?.apply {
                            for (i in indices) {
                                if (get(i).id == user.id) {
                                    get(i).selected = false
                                }
                            }

                        }

                    }

                }
                setUpSelectedMembersList()
            }
        }
        listDialog.show()
    }

    private fun setUpSelectedMembersList() {

        viewModel.apply {
            val cardAssignedMemberList = getBoardDetails()
                ?.taskList
                ?.get(viewModel.getTaskListPosition())
                ?.cards
                ?.get(viewModel.getCardPosition())
                ?.assignedTo

            val selectedMembersList: ArrayList<SelectedMembers> = ArrayList()

            assignedMemberDetailList.value?.apply {
                for (i in indices) {
                    for (j in cardAssignedMemberList!!) {
                        if (get(i).id == j) {
                            val selectedMember = SelectedMembers(
                                get(i).id,
                                get(i).image
                            )
                            selectedMembersList.add(selectedMember)
                        }
                    }
                }
            }

            binding.apply {
                if (selectedMembersList.size > 0) {
                    selectedMembersList.add(SelectedMembers("", ""))

                    tvSelectMembers.gone()
                    rvSelectedMembers.visible()

                    setupCardMembersListRecycler(selectedMembersList)

                } else {
                    tvSelectMembers.visible()
                    rvSelectedMembers.gone()
                }
            }

        }
    }

    private fun setupCardMembersListRecycler(selectedMembersList: ArrayList<SelectedMembers>) {

        val listener = object: MemberItemClickInterface{
            override fun onClick() {
                membersListDialog()
            }

        }

        cardMembersListItemAdapter = CardMembersListItemAdapter(true,listener)
        cardMembersListItemAdapter.submitList(selectedMembersList)        //same situation as in cardlistitemsadapter

        binding.rvSelectedMembers.apply {
            layoutManager = GridLayoutManager(requireContext(), 6)
            adapter = cardMembersListItemAdapter
        }

    }

    private fun alertDialogForDeleteCard(cardName: String) {
        val builder = AlertDialog.Builder(requireContext())

        builder.apply {
            setTitle(resources.getString(R.string.alert));
            setMessage(
                resources.getString(
                    R.string.confirmation_message_to_delete_card,
                    cardName
                )
            )
            setIcon(android.R.drawable.ic_dialog_alert);

            setPositiveButton(resources.getString(R.string.yes)) { dialogInterface, which ->
                dialogInterface.dismiss();
                viewModel.deleteCard();

            }

            setNegativeButton(resources.getString(R.string.no)) { dialogInterface, which ->
                dialogInterface.dismiss();
            }

        }

        val alertDialog: AlertDialog = builder.create()
        alertDialog.setCancelable(false)
        alertDialog.show()

    }

    private fun colorsList(): ArrayList<String> {
        val colorsList: ArrayList<String> = ArrayList()
        colorsList.add("#43C86F")
        colorsList.add("#0C90F1")
        colorsList.add("#F72400")
        colorsList.add("#7A8089")
        colorsList.add("#D57C1D")
        colorsList.add("#770000")
        colorsList.add("#0022F8")

        return colorsList
    }

    private fun setColor() {
        binding.apply {
            tvSelectLabelColor.text = ""
            tvSelectLabelColor.setBackgroundColor(
                Color.parseColor(viewModel.getSelectedColor())
            )

        }

    }

    private fun labelColorListDialog() {
        val colorsList: ArrayList<String> = colorsList();
        val listDialog = object : LabelColorListDialog(
            requireContext(),
            colorsList, resources.getString(R.string.str_select_label_color),
            viewModel.getSelectedColor()
        ) {
            override fun onItemSelected(color: String) {
                viewModel.setSelectedColor(color)
                setColor()
            }
        }
        listDialog.show()
    }

    private fun showDatePicker() {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(
            requireContext(),
            DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
                val sDayOfMonth = if (dayOfMonth < 1) "0$dayOfMonth" else "$dayOfMonth"
                val sMonthOfYear =
                    if ((monthOfYear + 1) < 10) "0${monthOfYear + 1}" else "${monthOfYear + 1}"

                val selectedDate = "$sDayOfMonth/$sMonthOfYear/$year"
                binding.tvSelectDueDate.text = selectedDate

                val sdf = SimpleDateFormat("dd/MM/yyy", Locale.ENGLISH)
                val theDate = sdf.parse(selectedDate)

                viewModel.setSelectedDueDate(theDate!!.time)
            },
            year,
            month,
            day
        )

        datePickerDialog.show()

    }

}