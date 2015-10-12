package com.github.yoojia.next.flux;

import com.github.yoojia.next.lang.ObjectMap;

/**
 * @author 陈小锅 (yoojiachen@gmail.com)
 * @since 1.0
 */
public final class Action {

    public static final Action EMPTY = new Action(null, new ObjectMap());

    public final String type;
    public final ObjectMap data;

    private Action(String type, ObjectMap data) {
        this.type = type;
        this.data = data;
    }

    public static class Builder {

        private String mType;
        private final ObjectMap mData = new ObjectMap();

        public Builder setName(String name){
            mType = name;
            return this;
        }

        public Builder putData(String key, Object value){
            mData.put(key, value);
            return this;
        }

        public Action build(){
            return new Action(mType, mData);
        }
    }

    @Override
    public String toString() {
        return "{" +
                "type='" + (type == null ? "<NO-TYPE>" : type) + '\'' +
                ", data=" + data +
                '}';
    }
}
