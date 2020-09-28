package com.dsl.orderrobot

import android.accessibilityservice.AccessibilityService
import android.app.Notification
import android.app.PendingIntent
import android.os.Bundle
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo
import com.dsl.orderrobot.db.TriggerBean
import com.dsl.orderrobot.db.TriggerDatabase
import com.dsl.orderrobot.util.DebugLog
import io.reactivex.FlowableSubscriber
import io.reactivex.Observable
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import org.reactivestreams.Subscription
import java.util.concurrent.TimeUnit

/**
 * @author dsl-abben
 * on 2020/09/22.
 */
class OrderingService : AccessibilityService() {

    private val wechatPackage = "com.tencent.mm"

    /**
     * 是否需要自动发送信息
     */
    private var stateSend = false

    /**
     * 自动发送的词语
     */
    private var inputMessage = "测试"

    /**
     * 状态变化回调
     */
    override fun onAccessibilityEvent(p0: AccessibilityEvent?) {
        p0?.let {
            DebugLog.e("事件包名:${it.packageName} .. 事件类名:${it.className} \n事件类型:${it.eventType}")
            if (it.packageName != wechatPackage) return
            when (it.eventType) {
                AccessibilityEvent.TYPE_NOTIFICATION_STATE_CHANGED -> {
                    //通知变化
                    DebugLog.e("通知变化文本:${it.text}")
                    if (it.text.isEmpty()) return
                    val wchatNotificationText = it.text.toString()

                    for (item in MainActivity.triggerList) {
                        DebugLog.e("打印$item")
                        if (
                            wchatNotificationText.contains(item.trigger)
                            && wchatNotificationText.contains(item.andTrigger)
                        ) {
                            inputMessage = item.autoSend
                            stateSend = true
                            //打开通知栏
                            val notification = it.parcelableData as Notification
                            val pendingIntent = notification.contentIntent
                            try {
                                pendingIntent.send()
                            } catch (e: PendingIntent.CanceledException) {
                                DebugLog.e("打开通知栏出错$e")
                            }
                            break
                        }
                    }
                }
                AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED -> {
                    //界面改变
                    val classNameChr = it.className
                    val className = classNameChr.toString()
                    DebugLog.e("界面改变:$className")
                    //android.widget.LinearLayout
//                    if (className == "com.tencent.mm.ui.LauncherUI" && stateSend) {
                    if (stateSend) {
                        stateSend = false
                        inputAndSent()
                    }
                }
            }
        }
    }

    /**
     * 辅助功能中断的回调
     */
    override fun onInterrupt() {

    }

    private fun inputAndSent() {
        Observable.timer(500, TimeUnit.MILLISECONDS)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : Observer<Long> {
                override fun onSubscribe(d: Disposable) {

                }

                override fun onNext(t: Long) {
                    val nodeInfo = rootInActiveWindow
                    if (nodeInfo != null) {
                        val editNodes =
                            nodeInfo.findAccessibilityNodeInfosByViewId("com.tencent.mm:id/al_")
                        if (editNodes != null && editNodes.size > 0) {
                            val editNode = editNodes[0]
                            val arguments = Bundle()
                            arguments.putCharSequence(
                                AccessibilityNodeInfo.ACTION_ARGUMENT_SET_TEXT_CHARSEQUENCE,
                                inputMessage
                            )
                            editNode.performAction(AccessibilityNodeInfo.ACTION_SET_TEXT, arguments)
                        }
                    }
                }

                override fun onError(e: Throwable) {

                }

                override fun onComplete() {

                }
            })

        //发送信息
        Observable.timer(1000, TimeUnit.MILLISECONDS)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                val sendNodes =
                    rootInActiveWindow.findAccessibilityNodeInfosByViewId("com.tencent.mm:id/anv")
                if (sendNodes != null) {
                    val sendNode: AccessibilityNodeInfo?
                    if (sendNodes.size > 0) {
                        sendNode = sendNodes[0]
                        sendNode?.performAction(AccessibilityNodeInfo.ACTION_CLICK)
                    }
                }
            }
    }
}