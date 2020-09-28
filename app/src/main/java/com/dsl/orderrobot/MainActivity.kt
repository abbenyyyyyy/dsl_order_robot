package com.dsl.orderrobot

import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.text.TextUtils
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.view.WindowManager
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.dsl.orderrobot.db.TriggerBean
import com.dsl.orderrobot.db.TriggerDatabase
import com.dsl.orderrobot.util.DebugLog
import com.dsl.orderrobot.widget.AddTriggerDialog
import com.dsl.orderrobot.widget.DividerLine
import com.dsl.orderrobot.widget.YesOrNoDialog
import io.reactivex.FlowableSubscriber
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_main.*
import org.reactivestreams.Subscription

class MainActivity : AppCompatActivity(), View.OnClickListener {

    private val openAccessibilityServiceRequestCode = 0x1432
    private val keyInitDb = "keyInitDb"
    private val initTriggerList = mutableListOf(
        TriggerBean("虾仁", "包间特色菜", "虾仁 一份"),
        TriggerBean("椒盐虾", "包间特色菜", "椒盐虾 一份"),
        TriggerBean("鸡翼", "包间特色菜", "鸡翼 一份"),
        TriggerBean("鸡中翅", "包间特色菜", "鸡中翅 一份"),
        TriggerBean("鸡扒", "包间特色菜", "鸡扒 一份"),
        TriggerBean("杂菇", "包间特色菜", "杂菇 一份"),
    )

    companion object {
        var triggerList = listOf<TriggerBean>()
    }

    private lateinit var triggerAdapter: TriggerAdapter
    private val compositeDisposable: CompositeDisposable = CompositeDisposable()

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //取消状态栏透明
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        //添加Flag把状态栏设为可绘制模式
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        //设置状态栏颜色
        window.statusBarColor = 0xff272731.toInt()
        //设置系统状态栏处于可见状态
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_VISIBLE;
        //让view不根据系统窗口来调整自己的布局
        val mContentView = window.findViewById<ViewGroup>(Window.ID_ANDROID_CONTENT)
        val mChildView = mContentView.getChildAt(0)
        if (mChildView != null) {
            ViewCompat.setFitsSystemWindows(mChildView, false)
            ViewCompat.requestApplyInsets(mChildView)
        }

        setContentView(R.layout.activity_main)
        trigger_recyclerview.layoutManager = LinearLayoutManager(this)
        trigger_recyclerview.addItemDecoration(DividerLine())
        triggerAdapter = TriggerAdapter(null)
        triggerAdapter.addChildClickViewIds(R.id.delete)
        triggerAdapter.setOnItemChildClickListener { adapter, view, position ->
            when (view.id) {
                R.id.delete -> {
                    val itemData = adapter.getItem(position) as TriggerBean
                    val yesOrNoDialog =
                        YesOrNoDialog.onNewInstance("是否删除\n${itemData.trigger} 与 ${itemData.andTrigger}\n该关键词")
                    yesOrNoDialog.yesOrNoDialogClickListener =
                        object : YesOrNoDialog.YesOrNoDialogClickListener {
                            override fun onYesOrNoDialogNegative() {
                            }

                            override fun onYesOrNoDialogPositive() {
                                compositeDisposable.add(TriggerDatabase.getInstance(this@MainActivity)
                                    .commonTriggerDao()
                                    .deleteTrigger(itemData)
                                    .subscribeOn(Schedulers.io())
                                    .observeOn(AndroidSchedulers.mainThread())
                                    .subscribe(
                                        {
                                            DebugLog.i("删除成功")
                                            updateStatic()
                                            triggerAdapter.removeAt(position)
                                            Toast.makeText(
                                                this@MainActivity,
                                                "删除成功.",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        },
                                        { error ->
                                            DebugLog.e("删除出错:$error")
                                            Toast.makeText(
                                                this@MainActivity,
                                                "删除出错:$error.",
                                                Toast.LENGTH_LONG
                                            ).show()
                                        }
                                    ))
                            }
                        }
                    yesOrNoDialog.show(supportFragmentManager, "YesOrNoDialog")
                }
            }
        }
        trigger_recyclerview.adapter = triggerAdapter

        to_start_service.setOnClickListener(this)
        to_video.setOnClickListener(this)
        add_trigger.setOnClickListener(this)

        val sharedPref = getPreferences(Context.MODE_PRIVATE)
        if (!sharedPref.getBoolean(keyInitDb, false)) {
            sharedPref.edit().putBoolean(keyInitDb, true).apply()
            //没有初始化过数据库
            compositeDisposable.add(
                TriggerDatabase.getInstance(this).commonTriggerDao()
                    .insertTrigger(initTriggerList)
                    .flatMap {
                        Single.just(
                            TriggerDatabase.getInstance(this@MainActivity).commonTriggerDao()
                                .queryTriggersOnWorkThread()
                        )
                    }
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(
                        {
                            DebugLog.i("初始化数据库成功")
                            triggerList = it
                            triggerAdapter.setNewInstance(it.toMutableList())
                        },
                        { error ->
                            DebugLog.e("初始化数据库出错:$error")
                            Toast.makeText(this, "自动增加初始关键词出错，请手动添加.", Toast.LENGTH_LONG).show()
                        }
                    )
            )
        } else {
            TriggerDatabase.getInstance(this).commonTriggerDao()
                .queryTriggers()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : FlowableSubscriber<List<TriggerBean>> {
                    override fun onSubscribe(s: Subscription) {
                        s.request(1)
                    }

                    override fun onNext(t: List<TriggerBean>?) {
                        t?.let {
                            triggerList = it
                            triggerAdapter.setNewInstance(it.toMutableList())
                        }
                    }

                    override fun onError(t: Throwable?) {
                        DebugLog.e("onError$t")
                    }

                    override fun onComplete() {
                        DebugLog.i("onComplete")
                    }
                })
        }
    }

    override fun onResume() {
        super.onResume()
        DebugLog.e("onResume检查${hasServicePermission()}")
        to_start_service.text =
            if (hasServicePermission()) "辅助功能已开启,正常工作,前往关闭" else "辅助功能未开启,无法自动回复,前往开启"
    }

    override fun onDestroy() {
        super.onDestroy()
        compositeDisposable.clear()
    }

    private fun hasServicePermission(): Boolean {
        var ok = 0
        try {
            ok = Settings.Secure.getInt(contentResolver, Settings.Secure.ACCESSIBILITY_ENABLED)
        } catch (e: Settings.SettingNotFoundException) {
        }
        val ms = TextUtils.SimpleStringSplitter(':')
        if (ok == 1) {
            val settingValue = Settings.Secure.getString(
                contentResolver, Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES
            )
            if (settingValue != null) {
                ms.setString(settingValue)
                while (ms.hasNext()) {
                    val accessibilityService = ms.next()
                    DebugLog.e(accessibilityService)
                    if (accessibilityService.contains("com.dsl.orderrobot")) {
                        return true
                    }
                }
            }
        }
        return false
    }

    /**
     * 更新关键词列表常量
     */
    private fun updateStatic() {
        TriggerDatabase.getInstance(this).commonTriggerDao()
            .queryTriggers()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : FlowableSubscriber<List<TriggerBean>> {
                override fun onSubscribe(s: Subscription) {
                    s.request(1)
                }

                override fun onNext(t: List<TriggerBean>?) {
                    t?.let {
                        triggerList = it
                    }
                }

                override fun onError(t: Throwable?) {
                    DebugLog.e("onError$t")
                }

                override fun onComplete() {
                    DebugLog.i("onComplete")
                }
            })
    }

    override fun onClick(p0: View?) {
        when (p0?.id) {
            R.id.to_start_service -> {
                val intent = Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS)
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivityForResult(intent, openAccessibilityServiceRequestCode)
            }
            R.id.to_video -> {
                val intent = Intent(this, PlayDemoActivity::class.java)
                startActivity(intent)
            }
            R.id.add_trigger -> {
                val addTriggerDialog = AddTriggerDialog.onNewInstance()
                addTriggerDialog.onClickListener =
                    object : AddTriggerDialog.AddTriggerDialogClickListener {
                        override fun onPositiveClcik(
                            trigger: String, editAddTrigger: String, autoSend: String
                        ) {
                            compositeDisposable.add(
                                TriggerDatabase.getInstance(this@MainActivity).commonTriggerDao()
                                    .insertSingleTrigger(
                                        TriggerBean(
                                            trigger, editAddTrigger, autoSend
                                        )
                                    )
                                    .subscribeOn(Schedulers.io())
                                    .observeOn(AndroidSchedulers.mainThread())
                                    .subscribe(
                                        {
                                            Toast.makeText(
                                                this@MainActivity,
                                                "自动增加关键词成功.",
                                                Toast.LENGTH_LONG
                                            ).show()
                                            updateStatic()
                                            triggerAdapter.addData(
                                                TriggerBean(
                                                    trigger, editAddTrigger, autoSend, it
                                                )
                                            )
                                        },
                                        { error ->
                                            DebugLog.e("增加关键词出错:$error")
                                        }
                                    )
                            )
                        }
                    }
                addTriggerDialog.show(supportFragmentManager, "AddTriggerDialog")
            }
        }
    }
}