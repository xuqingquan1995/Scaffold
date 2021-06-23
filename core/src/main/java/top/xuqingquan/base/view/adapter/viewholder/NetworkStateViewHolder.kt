package top.xuqingquan.base.view.adapter.viewholder

import android.view.View
import android.widget.ProgressBar
import android.widget.TextView
import androidx.core.view.isVisible
import top.xuqingquan.R
import top.xuqingquan.base.model.entity.NetworkStatus

/**
 * Created by 许清泉 on 2019-04-27 16:11
 */
class NetworkStateViewHolder(view: View) :
    BaseViewHolder<NetworkStatus>(view) {
    private val loading_failure = getView<TextView>(R.id.loading_failure)
    private val loading = getView<ProgressBar>(R.id.loading)
    private val loading_tv = getView<TextView>(R.id.loading_tv)

    fun bind(data: NetworkStatus, listener: View.OnClickListener) {
        loading_failure.setOnClickListener(listener)
        loading_failure.isVisible = (data == NetworkStatus.FAILED)
        loading.isVisible = (data == NetworkStatus.RUNNING)
        loading_tv.isVisible = (data == NetworkStatus.RUNNING)
    }
}