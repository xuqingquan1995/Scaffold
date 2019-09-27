package top.xuqingquan.sample

import android.annotation.SuppressLint
import android.view.View
import android.widget.TextView
import top.xuqingquan.base.model.diffcallback.BaseDiffCallBack
import top.xuqingquan.base.view.adapter.listadapter.BasePagedListAdapter
import top.xuqingquan.base.view.adapter.listadapter.SimpleRecyclerAdapter
import top.xuqingquan.base.view.adapter.viewholder.BaseViewHolder
import top.xuqingquan.utils.Timber

class BeanAdapter(retry:()->Unit) : BasePagedListAdapter<Subjects>(retry,BaseDiffCallBack<Subjects>()) {

    override fun getLayoutRes(viewType: Int) = R.layout.item

    @SuppressLint("SetTextI18n")
    override fun setData(holder: BaseViewHolder<Subjects>, data: Subjects?, position: Int) {
        holder.getView<TextView>(R.id.text).text = "$position---${data?.title}"
    }

    override fun onClick(view: View, position: Int, data: Subjects?, viewType: Int) {
        super.onClick(view, position, data, viewType)
        Timber.d("data=$data")
    }

}