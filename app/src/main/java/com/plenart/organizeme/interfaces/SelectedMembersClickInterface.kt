package com.plenart.organizeme.interfaces

import com.plenart.organizeme.models.User

interface SelectedMembersClickInterface {
    fun onClick(position: Int, user: User, action: String);
}