package top.xuqingquan.base.view.adapter.listadapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.recyclerview.widget.RecyclerView
import top.xuqingquan.base.view.adapter.viewholder.BaseViewHolder

/**
 * Created by 许清泉 on 2019/4/13 23:54
 * 简单的可持有数据的RecyclerViewAdapter
 */
class SimpleRecyclerAdapter<T>(private val list: MutableList<T>) :
    RecyclerView.Adapter<BaseViewHolder<T>>() {
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
        holder.onViewClickListener = object : BaseViewHolder.OnViewClickListener {
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

    override fun getItemCount() = list.size

    override fun onBindViewHolder(holder: BaseViewHolder<T>, position: Int) {
        holder.setData(list[position], position)
        setData(holder, getItem(position)!!, position)
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

    open fun setData(holder: BaseViewHolder<T>, data: T, position: Int) {}

    /**
     * 单击回调
     */
    open fun onClick(view: View, position: Int, data: T, viewType: Int) {}

    /**
     * 长按回调
     */
    open fun onLongClick(view: View, position: Int, data: T, viewType: Int): Boolean {
        return true
    }

    interface OnViewClickListener<T> {
        fun onClick(view: View, position: Int, data: T, viewType: Int)

        fun onLongClick(view: View, position: Int, data: T, viewType: Int): Boolean
    }

    fun getBaseList() = list

    fun getItem(position: Int) = list[position]

    fun addList(data: List<T>) {
        list.addAll(data)
        notifyDataSetChanged()
    }

    fun addData(data: T) {
        list.add(data)
        notifyItemInserted(list.size - 1)
    }

    fun removeData(data: T) {
        list.remove(data)
        notifyDataSetChanged()
    }

    fun removeDataAt(position: Int) {
        list.removeAt(position)
        notifyItemRemoved(position)
    }

    fun removeAll() {
        list.clear()
        notifyDataSetChanged()
    }

    fun resetData(list: List<T>) {
        removeAll()
        addList(list)
    }

    companion object {
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