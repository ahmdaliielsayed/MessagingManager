package com.message.messagingmanager.view.activity.messages

import android.view.View
import androidx.recyclerview.widget.RecyclerView

abstract class BaseRecyclerAdapter<VH : RecyclerView.ViewHolder, T> : RecyclerView.Adapter<VH>, View.OnClickListener {

    private var mObjectList: List<T>
    private lateinit var mClickListener: RecyclerClickListener

    constructor(objectList: List<T>) {
        mObjectList = objectList
    }

    constructor(objectList: List<T>, clickListener: RecyclerClickListener) {
        mObjectList = objectList
        mClickListener = clickListener
    }

    override fun onClick(view: View?) {
        mClickListener.onClickAction(view)
    }

    override fun getItemCount(): Int {
        return mObjectList.size
    }

    open fun getItem(position: Int): T {
        return mObjectList[position]
    }

    interface RecyclerClickListener {
        fun onClickAction(view: View?)
    }
}