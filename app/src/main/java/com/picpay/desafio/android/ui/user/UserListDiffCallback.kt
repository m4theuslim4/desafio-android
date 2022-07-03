package com.picpay.desafio.android.ui.user

import androidx.recyclerview.widget.DiffUtil
import com.picpay.desafio.android.data.local.user.UserLocalData

class UserListDiffCallback(
    private val oldList: List<UserLocalData>,
    private val newList: List<UserLocalData>
) : DiffUtil.Callback() {

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition].username.equals(newList[newItemPosition].username)
    }

    override fun getOldListSize(): Int {
        return oldList.size
    }

    override fun getNewListSize(): Int {
        return newList.size
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return true
    }
}