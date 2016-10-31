# AirDialog


**本文将从一下几个方面展开：
1Bulider模式的原理和使用
2系统Dailog的调用
3自定义Dailog
4自定义Dailog的一些小坑**




本文Github代码链接
https://github.com/AndroidMsky/AirDialog

自定义Dailog效果：
![这里写图片描述](http://img.blog.csdn.net/20161031143817212)



1.Bulider模式的原理和使用
----------------

当你看到这样的一串代码是不是觉得很炫酷？

```
  airDialog = new AirDialog.Builder(this).
                    setLeftText("gogo").
                    setRightText("nono").
                    create();
```
通过几个点点，像一个链子一样配置组装一个对象。今天就教给大家自己也可以设计这样一条链子。相比构造方法，这种模式更加灵活，更加清晰。

先看看设计图：
![这里写图片描述](http://img.blog.csdn.net/20161031144337035)

别怕相信大多数人都看不懂。不过对设计模式了解比较多的人呢还是可以看懂的，今天就咱不谈这个类图（你看懂不如会用）。
为什么可以里连续.......的调用方法，其实就是每次调用方法后return一个对象就好喽。当前的表达式值就一直是这个对象所以可以无限的....下去。

```
public Builder setLeftText(String left) {
            this.left=left;
            return this;
        }
```
代码很清晰没个设置方法都是这样的，方法的返回类型就是当前的Builder。那么我们就可以继续的调用Builder类中的方法喽。



2.系统Dailog的调用
------------



```
 new AlertDialog.Builder(MainActivity.this).setTitle("系统提示")//设置对话框标题  
  
     .setMessage("！")//设置显示的内容  
  
     .setPositiveButton("确定",new DialogInterface.OnClickListener() {//添加确定按钮  
@Override  
  
         public void onClick(DialogInterface dialog, int which) {
         }  
  
     }).setNegativeButton("返回",new DialogInterface.OnClickListener() {//添加返回按钮  
@Override  
public void onClick(DialogInterface dialog, int which) { 
 }  
 }).show();//在按键响应事件中显示此对话框 
```
系统提供了很多的Dialog的子类本文不去详细介绍，接下来就说如何自己写一个类似Dialog的子类。



3.自定义Dailog
----------


1继承Dialog并且实现构造方法：
```
public class AirDialog extends Dialog {

public AirDialog(Context context) {
        super(context, R.style.custom_dialog);
    }
```
super方法的第二个参数是为Dialog指定一个主题，那就在Styles。xml中加入如下代码：

```
<style name="custom_dialog" parent="@android:style/Theme.Dialog">
<item name="android:windowFrame">@null</item> <!-- 边框 -->
<item name="android:windowIsFloating">true</item> <!-- 是否浮现在activity之上 -->
<item name="android:windowIsTranslucent">true</item> <!-- 半透明 -->
<item name="android:windowNoTitle">true</item> <!-- 无标题 -->
<item name="android:windowBackground">@android:color/transparent</item>
</style>
```
常用的几个标签的意思已经注释。可以通过配置跟多的标签来扩展dialog的样式。
这样就可以去调用一个dialog了，但是不行，我们要加入Builder模式，于是参考安卓源码后新建内部静态类Builder

```
 public static class Builder{

        private Context context;
        private Context context;
        private String left;
        private String right;
        private View.OnClickListener leftOCL;
        private View.OnClickListener rightOCL;
        private Button leftButton;
        private Button rightButton;
       
        public Builder(Context context){
            this.context=context;

        }
```
指定各种组装配置方法：

```
public Builder setLeftText(String left) {
            this.left=left;
            return this;
        }
public Builder setRightText(String right) {
            this.right=right;
            return this;
        }
public Builder setLeftOnClick(View.OnClickListener left) {
            this.leftOCL=left;
            return this;
        }
public Builder setRightOnClick(View.OnClickListener right) {
            this.rightOCL=right;
            return this;
        }
```
可以看出没个方法都return 了一个Builder方便下次调用。

Builder类中的create方法最后调用根据前面的配置生成一个airDialog的对象：
```
 public AirDialog create() {

            final AirDialog airDialog=new AirDialog(context);

            View view = LayoutInflater.from(context).inflate(R.layout.dialog, null);
            leftButton=(Button)view.findViewById(R.id.button2);
            rightButton=(Button)view.findViewById(R.id.quxiao_btn);

            if (!TextUtils.isEmpty(left))
            leftButton.setText(left);
            if (!TextUtils.isEmpty(right))
            rightButton.setText(right);
            if (leftOCL!=null)
            leftButton.setOnClickListener(leftOCL);
            if (rightOCL!=null)
            rightButton.setOnClickListener(rightOCL);



            Window win = airDialog.getWindow();
            win.getDecorView().setPadding(0, 0, 0, 0);
            WindowManager.LayoutParams lp = win.getAttributes();
            lp.width = WindowManager.LayoutParams.MATCH_PARENT;
            lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
            win.setAttributes(lp);
           // win.setGravity(Gravity.NO_GRAVITY);
            win.setGravity(Gravity.CENTER);
            airDialog.setContentView(view);
            airDialog.setCanceledOnTouchOutside(false);
           // airDialog.setCancelable(false);

            return airDialog;

        }

```
可以看出Dialog的现实原理，是在WindowManager的控制下现实出来的：
Window win = airDialog.getWindow();
win.getDecorView().setPadding(0, 0, 0, 0);
WindowManager.LayoutParams lp = win.getAttributes();

Activity像一个工匠（控制单元），
Window像窗户（承载模型），
View像窗花（显示视图）
LayoutInflater像剪刀，
Xml配置像窗花图纸。（大家都这么比喻，不是我发明的）
清晰的说出了几者关系。

最后调用我们的dialog出来吧：

```
if (airDialog == null)

            airDialog = new AirDialog.Builder(this).
                    setLeftText("gogo").
                    setRightText("nono").
                    setRightOnClick(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            airDialog.dismiss();
                            Toast.makeText(MainActivity.this, "nono", Toast.LENGTH_SHORT).show();
                        }
                    }).
                    create();

        airDialog.show();
```
是不是很爽呢，自已可以做出这种链了。



4.自定义Dailog的一些小坑
---------------


第一次我xml是这样写的：

```
<?xml version="1.0" encoding="utf-8"?>
<RelativeLayout
    android:id="@+id/activity_main"
    xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
>


    <TextView
        android:id="@+id/text_content"
        android:layout_width="200dp"
        android:layout_height="150dp"
        android:background="#FFF222"
        android:gravity="center"
        android:text="(自定义Dialog弹窗)"
        android:layout_centerVertical="true"
        android:layout_centerHorizontal="true"/>

    <Button
        android:id="@+id/button2"
        android:layout_width="100dp"
        android:layout_height="wrap_content"
        android:text="yes"
        android:background="#FFF000"
        android:layout_alignParentBottom="true"
        android:layout_toLeftOf="@+id/quxiao_btn"
        android:layout_toStartOf="@+id/quxiao_btn"/>

    <Button
        android:id="@+id/quxiao_btn"
        android:layout_width="100dp"
        android:background="#FFF999"
        android:layout_height="wrap_content"
        android:text="no"
        android:layout_alignParentBottom="true"
        android:layout_alignRight="@+id/text_content"
        android:layout_alignEnd="@+id/text_content"/>

    <TextView
        android:id="@+id/textView"
        android:layout_width="200dp"
        android:layout_height="50dp"
        android:background="#cecb9e"
        android:gravity="center"
        android:text="标题"
        android:layout_above="@+id/text_content"
        android:layout_alignLeft="@+id/text_content"
        android:layout_alignStart="@+id/text_content"/>

</RelativeLayout>
```
textView设置再了剧中的content之上，在xml中正常显示但是到程序中就无法显示了。问题在于这行代码，高度设置成了WRAP_CONTENT。结果没有吧titleView计算到view中来。

```
 lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
```

后来我就在想安卓为什么这么设计，为什么不MATCH_PARENT，其实原因很简单：

```
// win.setGravity(Gravity.NO_GRAVITY);
            win.setGravity(Gravity.CENTER);
```
弹窗之所以叫弹窗，因为它只是窗口不是全屏幕的东西，所谓安卓希望我们的窗口可以动态指定位置如Gravity.CENTER，Gravity.BOTTOM等。这样窗口更加灵活不用多个xml支持。所以建议大家写dialog的布局的时候从“头“开始，不要以一个center的view为基准，本文的正确的xml如下：

```
<?xml version="1.0" encoding="utf-8"?>
<RelativeLayout
    android:id="@+id/activity_main"
    xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:paddingBottom="@dimen/activity_vertical_margin"
    android:paddingLeft="@dimen/activity_horizontal_margin"
    android:paddingRight="@dimen/activity_horizontal_margin"
    android:paddingTop="@dimen/activity_vertical_margin"
    >


    <TextView
        android:id="@+id/text_content"
        android:layout_width="200dp"
        android:layout_height="150dp"
        android:layout_below="@+id/text_title"
        android:layout_centerHorizontal="true"
        android:background="#FFF222"
        android:gravity="center"
        android:text="(自定义Dialog弹窗)"/>

    <Button
        android:id="@+id/quxiao_btn"
        android:layout_width="100dp"
        android:layout_height="wrap_content"
        android:layout_alignEnd="@+id/text_content"
        android:layout_alignRight="@+id/text_content"
        android:layout_below="@+id/text_content"
        android:background="#FFF999"
        android:text="no"/>

    <TextView
        android:id="@+id/text_title"
        android:layout_width="150dp"
        android:layout_height="50dp"
        android:layout_alignEnd="@+id/text_content"
        android:layout_alignParentTop="true"
        android:layout_alignRight="@+id/text_content"
        android:background="#cecb9e"
        android:gravity="center"
        android:text="标题"/>

    <ImageView
        android:id="@+id/icon"
        android:layout_width="50dp"
        android:layout_height="50dp"
        android:layout_alignLeft="@+id/text_content"
        android:layout_alignParentTop="true"
        android:background="#111FFF"
        android:layout_alignStart="@+id/text_content"
        />

    <Button
        android:id="@+id/button2"
        android:layout_width="100dp"
        android:layout_height="wrap_content"
        android:background="#bfba21"
        android:text="yes"
        android:layout_alignBaseline="@+id/quxiao_btn"
        android:layout_alignBottom="@+id/quxiao_btn"
        android:layout_alignLeft="@+id/text_content"
        android:layout_alignStart="@+id/text_content"/>

</RelativeLayout>
```
相信阅读本文后自定义花哨的dialog肯定不成问题了。
如果觉得这篇文章对你有帮助 欢迎star我的github。也算对笔者的一种支持。


本文Github代码链接
https://github.com/AndroidMsky/AirDialog


欢迎加安卓开发交流群：308372687

博主原创未经允许不许转载。


![这里写图片描述](http://img.blog.csdn.net/20161028111556438)
