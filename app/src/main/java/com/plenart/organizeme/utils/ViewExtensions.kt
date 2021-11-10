package com.plenart.organizeme.utils

import android.view.View
import com.bumptech.glide.Glide
import com.plenart.organizeme.R
import de.hdodenhof.circleimageview.CircleImageView

fun View.gone() {this.visibility = View.GONE}
fun View.visible() {this.visibility = View.VISIBLE}
fun CircleImageView.loadImage(url: String){
    Glide.with(this)
        .load(url)
        .centerCrop()
        .placeholder(R.drawable.ic_board_place_holder)
        .into(this)
}
