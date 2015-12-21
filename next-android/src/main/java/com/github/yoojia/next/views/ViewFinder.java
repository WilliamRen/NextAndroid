package com.github.yoojia.next.views;

import android.view.View;

/**
 * @author 陈小锅 (yoojia.chen@gmail.com)
 * @since 1.0
 */
public class ViewFinder implements NextAutoView.Finder{

    private static final String TAG = ViewFinder.class.getSimpleName();

    private final View mRootView;

    public ViewFinder(View rootView) {
        mRootView = rootView;
    }

    @Override
    public View find(int targetId, int[] parents) {
        if (targetId == mRootView.getId()){
            return mRootView;
        }
        View view = mRootView;
        for (int viewId: parents){
            view = view.findViewById(viewId);
        }
        if (view != null){
            return view.findViewById(targetId);
        }else{
            throw new NotFoundException("View not found: targetId=" + targetId + ", parents.size=" + parents.length);
        }
    }

    @SuppressWarnings("unchecked")
    public <T> T find(int viewId) {
        return (T) mRootView.findViewById(viewId);
    }

    public static class NotFoundException extends RuntimeException{

        public NotFoundException(String detailMessage) {
            super(detailMessage);
        }
    }
}
