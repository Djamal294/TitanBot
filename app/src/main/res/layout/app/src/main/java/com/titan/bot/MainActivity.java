package com.titan.bot;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.webkit.WebStorage;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CopyOnWriteArrayList;

public class MainActivity extends Activity {

    private WebView myBrowser;
    private Button controlButton;
    private EditText linkInput;
    private TextView dashboardView;
    private Handler handler = new Handler();
    private Random random = new Random();
    
    private int visitCounter = 0;
    private int clickCounter = 0;
    private String currentStatus = "Idle";
    private String currentProxy = "Direct";
    private String hunterStatus = "Ready";
    private boolean isBotRunning = false;

    private CopyOnWriteArrayList<String> READY_TO_USE_PROXIES = new CopyOnWriteArrayList<>();
    private String PROXY_SOURCE = "https://raw.githubusercontent.com/TheSpeedX/PROXY-List/master/http.txt";

    // مصفوفة المتصفحات العالمية (User Agents) مع البصمة التقنية لكل منها
    private String[] USER_AGENTS = {
        "WIN10:::Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36:::Intel Inc.:::Intel(R) Iris(R) Xe Graphics",
        "MAC:::Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/17.2 Safari/605.1.15:::Apple Inc.:::Apple M2 GPU",
        "IPHONE:::Mozilla/5.0 (iPhone; CPU iPhone OS 17_2 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/17.2 Mobile/15E148 Safari/604.1:::Apple Inc.:::Apple A16 GPU"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        dashboardView = findViewById(R.id.dashboardView);
        linkInput = findViewById(R.id.linkInput);
        controlButton = findViewById(R.id.controlButton);
        myBrowser = findViewById(R.id.myBrowser);

        WebSettings settings = myBrowser.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setDomStorageEnabled(true);
        settings.setDatabaseEnabled(true);

        myBrowser.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                if (isBotRunning) {
                    injectTitanUltimatePatch(); // حقن بصمة المتصفح المتقدمة
                    startHumanBehavior(); // بدء محاكاة السلوك البشري
                    handler.postDelayed(() -> decideAndClick(), 15000); // اتخاذ قرار الضغط بعد 15 ثانية
                }
            }
        });

        controlButton.setOnClickListener(v -> toggleBot());
        startProxyHunter(); // تشغيل صياد البروكسي في الخلفية
    }

    private void toggleBot() {
        isBotRunning = !isBotRunning;
        if (isBotRunning) {
            controlButton.setText("STOP SYSTEM");
            controlButton.setBackgroundColor(Color.RED);
            startNewSession();
        } else {
            controlButton.setText("START TITAN FACTORY");
            controlButton.setBackgroundColor(Color.GREEN);
            myBrowser.loadUrl("about:blank");
        }
    }

    private void startProxyHunter() {
        new Thread(() -> {
            while (true) {
                try {
                    if (READY_TO_USE_PROXIES.size() < 10) {
                        hunterStatus = "Hunting...";
                        updateUI();
                        HttpURLConnection conn = (HttpURLConnection) new URL(PROXY_SOURCE).openConnection();
                        BufferedReader r = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                        String line;
                        while ((line = r.readLine()) != null && READY_TO_USE_PROXIES.size() < 50) {
                            if (line.contains(":")) READY_TO_USE_PROXIES.add(line.trim());
                        }
                        hunterStatus = "Vault Full";
                    }
                    Thread.sleep(30000);
                } catch (Exception e) { hunterStatus = "Error Fetching"; }
                updateUI();
            }
        }).start();
    }

    private void startNewSession() {
        if (!isBotRunning) return;
        CookieManager.getInstance().removeAllCookies(null);
        WebStorage.getInstance().deleteAllData();
        
        if (!READY_TO_USE_PROXIES.isEmpty()) {
            currentProxy = READY_TO_USE_PROXIES.remove(0);
            applyProxy(currentProxy);
        }

        String[] agentData = USER_AGENTS[random.nextInt(USER_AGENTS.length)].split(":::");
        myBrowser.getSettings().setUserAgentString(agentData[1]);
        
        visitCounter++;
        currentStatus = "Surfing...";
        updateUI();
        myBrowser.loadUrl(linkInput.getText().toString());
        
        handler.postDelayed(this::startNewSession, 60000 + random.nextInt(120000));
    }

    private void applyProxy(String proxyStr) {
        String[] p = proxyStr.split(":");
        System.setProperty("http.proxyHost", p[0]);
        System.setProperty("http.proxyPort", p[1]);
        System.setProperty("https.proxyHost", p[0]);
        System.setProperty("https.proxyPort", p[1]);
    }

    private void injectTitanUltimatePatch() {
        // كود تزييف البصمة (Canvas, WebGL, Hardware) لتبدو كمتصفح حقيقي
        String js = "javascript:(function() {" +
                "Object.defineProperty(navigator, 'webdriver', {get: () => undefined});" +
                "const getParameter = WebGLRenderingContext.prototype.getParameter;" +
                "WebGLRenderingContext.prototype.getParameter = function(parameter) {" +
                "if (parameter === 37445) return 'Intel Inc.';" +
                "if (parameter === 37446) return 'Intel(R) Iris(R) Xe Graphics';" +
                "return getParameter.apply(this, arguments);};" +
                "})()";
        myBrowser.loadUrl(js);
    }

    private void startHumanBehavior() {
        handler.postDelayed(() -> myBrowser.scrollBy(0, random.nextInt(500)), 3000);
    }

    private void decideAndClick() {
        if (random.nextInt(100) < 5) { // نسبة نقر 5% للحماية
            String clickJS = "javascript:(function() {" +
                    "var ads = document.querySelectorAll('iframe, ins, a[href*=\"googleads\"]');" +
                    "if(ads.length > 0) { ads[Math.floor(Math.random() * ads.length)].click(); }" +
                    "})()";
            myBrowser.loadUrl(clickJS);
            clickCounter++;
            currentStatus = "Target Clicked!";
        } else {
            currentStatus = "Organic View Only";
        }
        updateUI();
    }

    private void updateUI() {
        runOnUiThread(() -> {
            String dash = "VISITS: " + visitCounter + " | CLICKS: " + clickCounter + "\n" +
                          "STATUS: " + currentStatus + "\n" +
                          "PROXY: " + currentProxy + "\n" +
                          "HUNTER: " + hunterStatus + " (" + READY_TO_USE_PROXIES.size() + ")";
            dashboardView.setText(dash);
        });
    }
                                }
