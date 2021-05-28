package com.plenart.organizeme.interfaces

import com.plenart.organizeme.models.Board

interface BoardItemClickInterface {
    fun onClick(position: Int, model: Board);
}