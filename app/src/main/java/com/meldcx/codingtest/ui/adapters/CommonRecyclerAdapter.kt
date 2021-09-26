package com.meldcx.codingtest.ui.adapters

import android.view.ViewGroup
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView

/*
* Simple common recyclerview created by me.
* details in:
* */
class CommonRecyclerAdapter<T>: RecyclerView.Adapter<BaseViewHolder<T>>(){
    var listOfItems:MutableList<T>? = mutableListOf()
    set(value) {
        field = value
        notifyDataSetChanged()
    }
    var expressionViewHolderBinding: ((T, ViewDataBinding, Int) -> Unit)? = null
    var expressionOnCreateViewHolder:((ViewGroup)-> ViewDataBinding)? = null
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder<T> {
        return expressionOnCreateViewHolder?.let { it(parent) }?.let { BaseViewHolder(it, expressionViewHolderBinding!!) }!!
    }

    override fun onBindViewHolder(holder: BaseViewHolder<T>, position: Int) {
        holder.bind(listOfItems!![position],position)
    }

    override fun getItemCount(): Int {
        return listOfItems!!.size
    }
}

class BaseViewHolder<T> internal constructor(private val binding: ViewDataBinding, private val experssion:(T, ViewDataBinding, Int)->Unit)
    : RecyclerView.ViewHolder(binding.root){
    fun bind(item:T,position: Int){
        experssion(item,binding,position)
    }
}