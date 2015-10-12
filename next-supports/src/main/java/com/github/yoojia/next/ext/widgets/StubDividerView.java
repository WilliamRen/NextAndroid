package com.github.yoojia.next.ext.widgets;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewStub;
import android.widget.FrameLayout;

import com.github.yoojia.next.ext.R;

/**
 * @author 陈小锅 (yoojia.chen@gmail.com)
 * @since 1.0
 */
public abstract class StubDividerView extends FrameLayout {

    private final View mDivider;
    protected final ViewStub mViewStub;

    public StubDividerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        inflate(context, R.layout.next_stub_divider, this);
        mDivider = findViewById(R.id.next_stubdivider_divider);
        mViewStub = Finder.use(this).find(R.id.next_stubdivider_container);
        // config divider
        final TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.NextStubDivider_Divider);
        final boolean show = array.getBoolean(R.styleable.NextStubDivider_Divider_showDivider, false);
        array.recycle();
        mDivider.setVisibility(show ? VISIBLE : GONE);
    }

    public void showDivider(boolean show){
        mDivider.setVisibility(show ? VISIBLE : GONE);
    }

}
