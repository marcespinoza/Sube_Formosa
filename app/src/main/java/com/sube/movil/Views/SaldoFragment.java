package com.sube.movil.Views;



import android.app.Dialog;
import android.content.DialogInterface;
import android.net.http.SslError;
import android.os.Bundle;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import androidx.fragment.app.Fragment;
import androidx.appcompat.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.SslErrorHandler;
import android.webkit.ValueCallback;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import com.github.rahatarmanahmed.cpv.CircularProgressView;
import com.sube.movil.Presenters.DatosLoginPresenter;
import com.sube.movil.R;

/**
 * Created by Marcelo
 */
public class SaldoFragment extends Fragment implements com.sube.movil.Interfaces.SaldoFragment.View {

    View rootView;
    private AdView mAdView;
    private Dialog popup_login;
    String[] tipos_documento = new String[]{"Seleccione",
            "DNI - Documento Nacional de Identidad",
            "LE - Libreta enrolamiento",
            "LC - Libreta Civica",
            "DE - Documento extranjero"
    };
    private Button aceptar;
    private Button cancelar;
    private EditText usuario;
    private EditText clave;
    private Spinner tipo_documento;
    private com.sube.movil.Interfaces.SaldoFragment.Presenter presenter;
    String js_tipoDocumento = null;
    String js_usuario = null;
    String js_clave = null;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.fragment3, container, false);
        mAdView = rootView.findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);
        popup_login = new Dialog(getActivity());
        popup_login.setContentView(R.layout.popup_login);
        tipo_documento = popup_login.findViewById(R.id.tipo_documento);
        tipo_documento.setFocusable(true);
        tipo_documento.setFocusableInTouchMode(true);
        tipo_documento.requestFocus();
        usuario = popup_login.findViewById(R.id.usuario);
        clave = popup_login.findViewById(R.id.clave);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(),
                android.R.layout.simple_spinner_item, tipos_documento);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        tipo_documento.setAdapter(adapter);
        aceptar = popup_login.findViewById(R.id.aceptar);
        cancelar = popup_login.findViewById(R.id.cancelar);
        cancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                popup_login.dismiss();
            }
        });
        aceptar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                guardarUsuario();
            }
        });
        final WebView webView =  rootView.findViewById(R.id.webPage);
        final CircularProgressView progressView =rootView.findViewById(R.id.progress_view);
        presenter = new DatosLoginPresenter(this, getContext());
        progressView.startAnimation();
       /* cargarUsuario();*/
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
                webView.evaluateJavascript(js_tipoDocumento, new ValueCallback<String>() {
                    @Override
                    public void onReceiveValue(String s) {

                    }
                });
                webView.evaluateJavascript(js_usuario, new ValueCallback<String>() {
                    @Override
                    public void onReceiveValue(String s) {

                    }
                });
                webView.evaluateJavascript(js_clave, new ValueCallback<String>() {
                    @Override
                    public void onReceiveValue(String s) {

                    }
                });
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

    @Override
    public void cargarUsuario() {
        presenter.obtenerDatosLogin();
    }

    //-----Obtengo tipo documento, usuario y clave----//
    @Override
    public void guardarUsuario() {
        int tipoDocumento = tipo_documento.getSelectedItemPosition();
        int _usuario = Integer.parseInt(usuario.getText().toString());
        String _clave = clave.getText().toString();
        Log.i("clave","clave"+tipoDocumento);
        if(tipoDocumento!=0 && _usuario!= 0 && _clave!=null){
            presenter.guardarUsuario(tipoDocumento, _usuario, _clave);
            popup_login.dismiss();
        } else{

        }
    }

    @Override
    public void mostrarError() {

    }

    @Override
    public void retornarUsuario(int tipoDocumento, int _usuario, String _clave) {
        js_tipoDocumento = "javascript:var x = document.getElementById('ddlTipoDocumento').options["+tipoDocumento+"].selected=true;";
        js_usuario = "javascript:var x = document.getElementById('txtDocumento').value = '"+_usuario+"';";
        js_clave = "javascript:var x = document.getElementById('password').value = '"+_clave+"';";
        tipo_documento.setSelection(tipoDocumento);
        usuario.setText(String.valueOf(_usuario));
        clave.setText(_clave);
    }
}