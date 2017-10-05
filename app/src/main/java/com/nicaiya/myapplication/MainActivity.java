package com.nicaiya.myapplication;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.nicaiya.glview.ui.GLFrameLayout;
import com.nicaiya.glview.ui.GLRootView;
import com.nicaiya.glview.ui.GLTextView;
import com.nicaiya.glview.ui.GLViewGroup;

public class MainActivity extends AppCompatActivity {

    private GLRootView mGLRootView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mGLRootView = findViewById(R.id.gl_root_view);
        GLFrameLayout content = new GLFrameLayout();
        mGLRootView.setContentPane(content);
        content.setBackgroundColor(Color.WHITE);

        final GLTextView glTextView = new GLTextView();
        glTextView.setBackgroundColor(Color.RED);
        glTextView.setText("Hello World");
        glTextView.setTextSize(50);
        content.addView(glTextView, new GLViewGroup.LayoutParams(100, 100));

    }

    @Override
    protected void onResume() {
        super.onResume();
        mGLRootView.onResume();
    }

    @Override
    protected void onPause() {
        mGLRootView.onPause();
        super.onPause();
    }
}
