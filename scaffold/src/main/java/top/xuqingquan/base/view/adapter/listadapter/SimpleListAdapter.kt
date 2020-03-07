package top.xuqingquan.base.view.adapter.listadapter

import android.support.annotation.LayoutRes
import android.support.v7.recyclerview.extensions.ListAdapter
import android.support.v7.util.DiffUtil
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import top.xuqingquan.base.view.adapter.viewholder.BaseViewHolder

/**
 * Created by 许清泉 on 2019/4/14 01:37
 */
open class SimpleListAdapter<T>(diff: DiffUtil.ItemCallback<T>) : ListAdapter<T, BaseViewHolder<T>>(diff) {

    var listener: OnViewClickListener<T>? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder<T> {
        val holder = getViewHolder(parent, viewType)
        setOnClickListener(holder, viewType)
        return holder
    }

    protected fun setOnClickListener(
        holder: BaseViewHolder<T>,
        viewType: Int
    ) {
        holder.onViewClickListener = object : BaseViewHolder.OnViewClickListener() {
            override fun onClick(view: View, position: Int) {
                if (listener == null) {
                    onClick(view, position, getItem(position), viewType)
                } else {
                    listener!!.onClick(view, position, getItem(position), viewType)
                }
            }

            override fun onLongClick(view: View, position: Int): Boolean {
                return if (listener == null) {
                    onLongClick(view, position, getItem(position), viewType)
                } else {
                    listener!!.onLongClick(view, position, getItem(position), viewType)
                }
            }
        }
    }

    override fun onBindViewHolder(holder: BaseViewHolder<T>, position: Int) {
        holder.setData(getItem(position), position)
        setData(holder, getItem(position), position)
    }

    /**
     * 创建ViewHolder
     */
    open fun getViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder<T> {
        return BaseViewHolder(LayoutInflater.from(parent.context).inflate(getLayoutRes(viewType), parent, false))
    }

    /**
     * 默认设置布局的方式
     */
    @LayoutRes
    open fun getLayoutRes(viewType: Int) = 0

    open fun setData(holder: BaseViewHolder<T>, data: T?, position: Int) {}

    /**
     * 在Adapter内部实现单击回调
     */
    open fun onClick(view: View, position: Int, data: T?, viewType: Int) {

    }

    /**
     * 在Adapter内部实现长按回调
     */
    open fun onLongClick(view: View, position: Int, data: T?, viewType: Int): Boolean {
        return true
    }

    abstract class OnViewClickListener<T> {
        abstract fun onClick(view: View, position: Int, data: T?, viewType: Int)

        open fun onLongClick(view: View, position: Int, data: T?, viewType: Int) = true
    }

    override fun getItem(position: Int): T? {
        try {
            if (position >= itemCount) {
                return null
            }
            return super.getItem(position)
        } catch (t: Throwable) {
            return null
        }
    }


    companion object {
        @Suppress("unused")
        @JvmStatic
        fun releaseAllViewHolder(recyclerView: RecyclerView) {
            for (i in recyclerView.childCount - 1 downTo 0) {
                val view = recyclerView.getChildAt(i)
                val holder = recyclerView.getChildViewHolder(view)
                if (holder != null && holder is BaseViewHolder<*>) {
                    holder.onRelease()
                }

            }
        }
    }

}