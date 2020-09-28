package com.dsl.orderrobot

import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.dsl.orderrobot.db.TriggerBean

/**
 * @author dsl-abben
 * on 2020/09/23.
 */
class TriggerAdapter(dataList: MutableList<TriggerBean>?) :
    BaseQuickAdapter<TriggerBean, BaseViewHolder>(
        R.layout.item_trigger, dataList
    ) {
    override fun convert(holder: BaseViewHolder, item: TriggerBean) {
        holder.setText(R.id.trigger, item.trigger)
        holder.setText(R.id.and_trigger, if (item.andTrigger.isEmpty()) "无" else item.andTrigger)
        holder.setText(R.id.auto_send, item.autoSend)
    }
}