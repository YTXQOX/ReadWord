package com.ljstudio.android.readword;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.webkit.WebSettings;
import android.webkit.WebView;

import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.FileCallBack;

import java.io.File;

import okhttp3.Call;
import okhttp3.Request;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * 支持doc,docx,xls,xlsx
 */
public class MainActivity extends AppCompatActivity {

    private static final String FILE_NAME = "file.doc";
    public WebView wv_view;
    public File myFile;
    WordReader fr = null;
    // private CustomDialog dialog;
    private WebSettings webSettings;
    private ProgressDialog progressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        wv_view = (WebView) findViewById(R.id.wv_view);
        webSettings = wv_view.getSettings();
        webSettings.setSupportZoom(true);
        webSettings.setBuiltInZoomControls(true);
        wv_view.setHapticFeedbackEnabled(false);
        webSettings.setJavaScriptEnabled(true);
        webSettings.setJavaScriptCanOpenWindowsAutomatically(true);
        webSettings.setUseWideViewPort(true);//关键点
        webSettings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
        webSettings.setAllowFileAccess(true); // 允许访问文件
        webSettings.setDisplayZoomControls(false);

        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("加载中...");

        String url = "http://jianzhan.dj.cn/downs/网站建设产品服务合同 （云建站3.0版-含设计服务）- 电子版.doc";
        download(url);
    }

    private void download(String url) {
        File filePath = new File(Environment.getExternalStorageDirectory() + File.separator + "LJSTUDIO" + File.separator + "PANGPANG" + File.separator + "Download");
        if (!filePath.exists()) {
            filePath.mkdirs();
        }

        OkHttpUtils.get().url(url).build()
                .execute(new FileCallBack(filePath.getAbsolutePath(), FILE_NAME) {
                    @Override
                    public void onBefore(Request request, int id) {
                        super.onBefore(request, id);
                    }

                    @Override
                    public void inProgress(float progress, long total, int id) {
                        super.inProgress(progress, total, id);

                        progressDialog.show();
                    }

                    @Override
                    public void onError(Call call, Exception e, int id) {
                        progressDialog.dismiss();
                    }

                    @Override
                    public void onResponse(File response, int id) {
                        progressDialog.dismiss();

                        String filePath = response.getAbsolutePath();

                        myFile = new File(filePath);
                        if (myFile.exists()) {
                            rx.Observable.just(filePath).map(new Func1<String, String>() {
                                @Override
                                public String call(String s) {
                                    fr = new WordReader(s);
                                    return fr.returnPath;
                                }
                            }).subscribeOn(Schedulers.io())
                                    .observeOn(AndroidSchedulers.mainThread())
                                    .subscribe(new Action1<String>() {
                                        @Override
                                        public void call(String s) {
                                            //拿到call方法对"test"的数据进行处理的结果
                                            if (wv_view != null) {
                                                wv_view.loadUrl(s);
                                                webSettings.setLoadWithOverviewMode(true);
                                                // parseFinishListenner.onParseFinshed();
                                                // dialog.dismiss();
                                            }
                                        }
                                    });
                        }
                    }
                });

    }

}
