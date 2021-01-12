package com.example.income_and_expenses;

import android.app.Activity;
import android.graphics.Bitmap;
import android.widget.Toast;

import utils.ApplicationContext;
import utils.preDefiniation.BarcodeType;

public class PrintHelper {
    public ApplicationContext context;
    public Activity mActivity;
    public PrintHelper(ApplicationContext ctx){
        context = ctx;
    }
    public void printPicture(Bitmap bmp){
        context.getObject().CON_PageStart(context.getState(), true,380, 260); // true since we gonna print image
        context.getObject().ASCII_CtrlReset(context.getState());
        context.getObject().DRAW_SetFillMode(false);
        context.getObject().DRAW_SetLineWidth(4);

        context.getObject().DRAW_PrintPicture(context.getState(),
                bmp, 0, 0, 250, 200);

        context.getObject().CON_PageEnd(context.getState(),
                context.getPrintway());
        ;
    }
    public void printPlainText(String text){
        //state,boolean,width,height
//        this.printPicture(bmp);
        context.getObject().CON_PageStart(context.getState(), true,
                50,
                50);

        {

            // 对齐方式

            int alignType=1;//0 left,1:center,2:right


            context.getObject().ASCII_CtrlAlignType(context.getState(),
                    alignType);
            //graphic printing
            /*
            context.getObject().ASCII_CtrlReset(context.getState());
            context.getObject().DRAW_SetFillMode(true);
            context.getObject().DRAW_SetLineWidth(4);

            context.getObject().DRAW_PrintPicture(context.getState(),
                    bmp, 50, 0, 270, 180);
            */
            //state,[0,1]:width,:height,:bold,:underline,:inverse,:small
            context.getObject().ASCII_PrintString(context.getState(),
                    0,
                    0,
                    0,
                    0,
                    0,
                    text, "gb2312");

            context.getObject().ASCII_CtrlFeedLines(context.getState(),
                    1);
            context.getObject().ASCII_CtrlPrintCRLF(context.getState(),
                    1);

            // print qr code
//            context.getObject().ASCII_Print2DBarcode(
//                    context.getState(),
//                    BarcodeType.BT_QRcode.getValue(),
//                    text,
//                    50,
//                    50,
//                    5); //zoom 1-6
//            context.getObject().ASCII_Print1DBarcode(
//                    context.getState(),
//                    BarcodeType.BT_CODE128.getValue(),
//                    20,
//                    60,
//                    10,text);

        }
        context.getObject().CON_PageEnd(context.getState(),
                context.getPrintway());
    }

    public void printBarcode(String text) {


        context.getObject().CON_PageStart(context.getState(), false, 50,
                80); // aka kabiri  ni height
        int hri = 20;//label 0:none,1:above,2:below
        int wide = 20;
        int hight = 60;

        // 对齐方式
        //0:left,1:center,2:right
        context.getObject().ASCII_CtrlAlignType(context.getState(),
                context.getAlignType());

        //state,barcodetype:default(8:CODE_128),content,width,height,contentLabelat[none:0,left:1,right:2]
        context.getObject().ASCII_Print1DBarcode(
                context.getState(),
                BarcodeType.BT_CODE128.getValue(),
                wide,
                hight,
                hri, text);
        context.getObject().CON_PageEnd(context.getState(),
                context.getPrintway()); // ends the page and prints
    }

    public void printQrCode(String text) {
        //控制输入,确保不为空

        context.getObject().CON_PageStart(context.getState(), true, 50,
                150);
        int hri = 0;
        int wide = 0;
        int hight = 0;

        // 对齐方式

        context.getObject().ASCII_CtrlAlignType(context.getState(),
                context.getAlignType());

        //限制放大倍数不超过6并且不小于1
        int qrcHir = 4;//Zoom 0 - 6
        if(qrcHir>6||qrcHir<1){
            Toast.makeText(context, "Maximum is 6 and minium is 1", Toast.LENGTH_SHORT).show();
            return;
        }

        //state,type:default(0:PDF417,1:Datamatrix,2:Qrcode),content,width,height,contentLabelat[none:0,left:1,right:2]

        context.getObject().ASCII_Print2DBarcode(
                context.getState(),
                BarcodeType.BT_QRcode.getValue(),
                text,
                wide,
                hight,
                qrcHir); //label
        System.out.println("===============1:"+wide+"  2:"+hight+"  3:"+qrcHir);
        context.getObject().CON_PageEnd(context.getState(),context.getPrintway());
    }
}

