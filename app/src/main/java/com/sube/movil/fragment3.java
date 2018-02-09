package com.sube.movil;



import android.content.DialogInterface;
import android.net.http.SslError;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.SslErrorHandler;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.github.rahatarmanahmed.cpv.CircularProgressView;

/**
 * Created by Marcelo
 */
public class fragment3 extends Fragment  {

    View rootView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.fragment3, container, false);
        final WebView webView = (WebView) rootView.findViewById(R.id.webPage);
        final CircularProgressView progressView = (CircularProgressView) rootView.findViewById(R.id.progress_view);
        progressView.startAnimation();
        webView.loadUrl("https://tarjetasube.sube.gob.ar/subeweb/WebForms/Account/Views/Login.aspx");
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setDomStorageEnabled(true);
        webView.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
        webView.getSettings().setBuiltInZoomControls(true);
        webView.getSettings().setSupportZoom(true);
        webView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
        webView.getSettings().setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
        webView.getSettings().setLoadWithOverviewMode(true);
        webView.getSettings().setUseWideViewPort(true);
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                webView.loadUrl(
                        "javascript:(function() { " +
                                "var element = document.getElementById('footer2');" +
                                "element.parentNode.removeChild(element);" +
                                "document.getElementsByClassName('well p-x-3 p-y-2')[0].style.display='block';" +
                                "document.getElementsByClassName('jumbotron login-bg')[0].style.display='none';" +
                                "document.getElementsByClassName('main-footer sticky-footer')[0].style.display='none';" +
                                "document.getElementsByClassName('main-footer sticky-footer')[0].style.display='none';" +
                                "})()");
                progressView.stopAnimation();
                progressView.setVisibility(View.INVISIBLE);
            }

            public void onReceivedSslError(WebView view, final SslErrorHandler handler, SslError error) {

                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                AlertDialog alertDialog = builder.create();
                String message = "SSL Certificate error.";
                switch (error.getPrimaryError()) {
                    case SslError.SSL_UNTRUSTED:
                        message = "The certificate authority is not trusted.";
                        break;
                    case SslError.SSL_EXPIRED:
                        message = "The certificate has expired.";
                        break;
                    case SslError.SSL_IDMISMATCH:
                        message = "The certificate Hostname mismatch.";
                        break;
                    case SslError.SSL_NOTYETVALID:
                        message = "The certificate is not yet valid.";
                        break;
                }

                message += " Do you want to continue anyway?";
                alertDialog.setTitle("SSL Certificate Error");
                alertDialog.setMessage(message);
                alertDialog.setButton(DialogInterface.BUTTON_POSITIVE, "OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Ignore SSL certificate errors
                        handler.proceed();
                    }
                });
                AlertDialog alert = builder.create();
                alert.show();
            }

        });

        return rootView;
    }



}