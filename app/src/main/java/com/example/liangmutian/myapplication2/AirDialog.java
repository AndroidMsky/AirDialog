package com.example.liangmutian.myapplication2;

import android.app.Dialog;
import android.content.Context;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;

/**
 * Created by lmt on 16/10/31.
 */

public class AirDialog extends Dialog {

public AirDialog(Context context) {
        super(context, R.style.custom_dialog);
    }

    public static class Builder{

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


    }






}
