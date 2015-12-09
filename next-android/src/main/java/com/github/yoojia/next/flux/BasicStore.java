package com.github.yoojia.next.flux;

import android.app.Activity;

/**
 * A Basic Store contains a Dispatcher
 * @author 陈小锅 (yoojiachen@gmail.com)
 * @since 1.0
 */
public class BasicStore extends AbstractStore<Activity>{

    protected BasicStore(Activity context) {
        super(new Dispatcher(), context);
    }

}
