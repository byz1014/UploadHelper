# UploadHelper
视频图片选择、拍摄上传 监听上传状态、内置自定义相机用于录制视频

1.自定义相机
  注意：目前没有开放比特率、帧率、分辨率 需要在代码中修改
  使用方法：
    调用
    Intent(this,CameraVideoActivity::class.java).apply{
     startActivityForResult(this,ResConfig.CAMERA_CODE)
    }
    回调
     var filePath = this.getStringExtra("path")
     var mFileName = this.getStringExtra("fileName")

2.图片上传框架
  注意：目前网络请求部分没有对外开放需要在代码中修改
  为避免同时多文件上传出现内存溢出的情况这里采用的是串行的上传方式
  使用方法：

      懒加载的方式创建upLoadFactory对象
      val upLoadFactory by lazy { UpLoadFactory( this) } 

      绑定RecycleView和Activity 这里一定要是Activity因为后面的Dialog必须绑定到Activity
      upLoadFactory.addAdapter(recycleView,activity)

      在Activity的onActivityResult方法中调用onResultData用于处理选中的资源文件
      upLoadFactory.onResultData(requestCode: Int, resultCode: Int, data: Intent?)

      获取当前数据
      upLoadFactory.getMediaData()

      添加数据 一般用于读取详情的时候
      upLoadFactory.setData(mList:MutableList<ResourcesBean>)

      设置弹框内容
      upLoadFactory.setDialogList(list:MutableList<ChooseActionState>)

      设置视频时长(拍摄视频和选择视频)
      upLoadFactory.setVideoMaxSecond(mMaxSecond:Int)

      设置是否可是可编辑 true:不可编辑  false:可编辑
      upLoadFactory.setLooker(looker:Boolean)

      设置上传的最大资源数
      upLoadFactory.setMax(max:Int)

      设置可选择视频的最大内存
      upLoadFactory.setVideoMaxSize(size:Float)

      设置可选择图片的最大内存
      upLoadFactory.setPictureMaxSize(size:Float)
      
      同一个Activity有多个选择文件的时候定义requestCode
      upLoadFactory.setRequestCode(mBean: RequestCodeBean)

