package com.resources.uploadlib.base
import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.res.Configuration
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.WindowManager
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import com.resources.uploadlib.R


abstract class BaseDialog<Binding : ViewDataBinding?>(activity: Activity) :
    Dialog(activity, R.style.customDialog) {

    var mActivity:Activity
    init {
        mActivity = activity
    }

    var binding: Binding? = null

    private var anim = 0


    /**
     * 设置Dialog弹出动画
     * @param anim
     */
    fun setAnim(anim: Int) {
        this.anim = anim
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setOwnerActivity(mActivity)
        binding = DataBindingUtil.inflate(LayoutInflater.from(context), onLayout(), null, false)
        binding?.let {
            setContentView(it.root)
            onWeight(it)
            //按空白处不能取消动画
            setCanceledOnTouchOutside(false)
            val wm = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
            //全屏显示
//            windowDeploy(wm.defaultDisplay.width, wm.defaultDisplay.height, anim)
            val config: Configuration = context.resources.configuration
            val screenWidth = config.screenWidthDp.toFloat()
            val screenHeight = config.screenHeightDp.toFloat()
            windowDeploy(context.dp2px(screenWidth).toInt(),context.dp2px(screenHeight).toInt() , anim)
            it.root.setOnClickListener {
                if(isTouchOutCancel()){
                    dismiss()
                }
            }
        }

    }


    protected abstract  fun isTouchOutCancel():Boolean

    protected abstract fun onLayout(): Int

    protected abstract fun onWeight(binding: Binding)


    override fun dismiss() {
        super.dismiss()
    }



    /**
     * 设置窗口显示
     * @param x 显示宽
     * @param y 显示长
     * @param resAnim 弹出动画
     */
    fun windowDeploy(x: Int, y: Int, resAnim: Int) {
        var window = window  //得到对话框
        window?.let {
            if (resAnim != 0) {
                assert(true)
                it.setWindowAnimations(resAnim)
            }
            assert(true)
            it.setBackgroundDrawableResource(android.R.color.transparent) //设置对话框背景为透明
            val wl = it.attributes
            wl.width = x
            wl.height = y
            wl.gravity = Gravity.CENTER
            it.attributes = wl
        }
    }


}

