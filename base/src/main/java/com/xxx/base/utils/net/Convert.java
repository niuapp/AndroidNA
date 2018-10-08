package com.xxx.base.utils.net;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonIOException;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSyntaxException;
import com.google.gson.stream.JsonReader;

import java.io.Reader;
import java.lang.reflect.Type;

/**
 * ================================================
 * 作    者：jeasonlzy（廖子尧）Github地址：https://github.com/jeasonlzy
 * 版    本：1.0
 * 创建日期：16/9/28
 * 描    述：
 * 修订历史：
 * ================================================
 */
public class Convert {

    private static Gson create() {
        return GsonHolder.gson;
    }

    private static class GsonHolder {
        private static Gson gson = new GsonBuilder()

//                //Array和Object 数据过滤
//                .registerTypeAdapter(ArticleBean.class, new FilterArrayConverter<ArticleBean>(ArticleBean.class, "accountInfo"))
//                .registerTypeAdapter(CommentAndPraiseItemModel.class, new FilterArrayConverter<CommentAndPraiseItemModel>(CommentAndPraiseItemModel.class, "receivePraiseBean", "receiveCommentBean"))
//                .registerTypeAdapter(CommentItemModel.class, new FilterArrayConverter<CommentItemModel>(CommentItemModel.class, "repliesBean"))
//                .registerTypeAdapter(CommunityItemModel.class, new FilterArrayConverter<CommunityItemModel>(CommunityItemModel.class, "articleBean"))
//                .registerTypeAdapter(ReceiveCommentBean.class, new FilterArrayConverter<ReceiveCommentBean>(ReceiveCommentBean.class, "articleInfo", "replyAccountInfo"))
//                .registerTypeAdapter(RepliesBean.class, new FilterArrayConverter<RepliesBean>(RepliesBean.class, "replyAccountInfo", "beReplyAccountInfo"))
//                .registerTypeAdapter(LikedActItemModel.class, new FilterArrayConverter<LikedActItemModel>(LikedActItemModel.class, "actInfo"))
//                .registerTypeAdapter(CoverBean.class, new FilterArrayConverter<CoverBean>(CoverBean.class, "musicInfo"))
                .create();
    }

    public static <T> T fromJson(String json, Class<T> type) throws JsonIOException, JsonSyntaxException {
        return create().fromJson(json, type);
    }

    public static <T> T fromJson(String json, Type type) {
        return create().fromJson(json, type);
    }

    public static <T> T fromJson(JsonReader reader, Type typeOfT) throws JsonIOException, JsonSyntaxException {
        return create().fromJson(reader, typeOfT);
    }

    public static <T> T fromJson(Reader json, Class<T> classOfT) throws JsonSyntaxException, JsonIOException {
        return create().fromJson(json, classOfT);
    }

    public static <T> T fromJson(Reader json, Type typeOfT) throws JsonIOException, JsonSyntaxException {
        return create().fromJson(json, typeOfT);
    }

    public static String toJson(Object src) {
        return create().toJson(src);
    }

    public static String toJson(Object src, Type typeOfSrc) {
        return create().toJson(src, typeOfSrc);
    }


    /**
     * 过滤Object变成list的字段
     * @param <T>
     */
    public static class FilterArrayConverter<T> implements JsonDeserializer<T> {

        private String[] fieldNameList;
        private Class<T> tClass;

        /**
         * @param tClass 该字段所属Bean.Class
         * @param fieldNameList  过滤的字段名
         */
        public FilterArrayConverter(Class<T> tClass, String... fieldNameList) {
            this.fieldNameList = fieldNameList;
            this.tClass = tClass;
        }

        @Override
        public T deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            if (!json.isJsonObject()) return null;//不是个JSON对象，解析为null
            JsonObject asJsonObject = json.getAsJsonObject();
            for (int i = 0; i < fieldNameList.length; i++) {
                String fieldName = fieldNameList[i];
                JsonElement fieldNode = asJsonObject.get(fieldName);
                if (fieldNode != null && fieldNode.isJsonArray()) {
                    asJsonObject.remove(fieldName);
                }
            }
            return new Gson().fromJson(asJsonObject, tClass);
        }
    }
}