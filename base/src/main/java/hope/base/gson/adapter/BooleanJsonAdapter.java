//package hope.base.gson.adapter;
//
//import com.google.gson.Gson;
//import com.google.gson.TypeAdapter;
//import com.google.gson.annotations.JsonAdapter;
//import com.google.gson.stream.JsonReader;
//import com.google.gson.stream.JsonToken;
//import com.google.gson.stream.JsonWriter;
//
//import java.io.IOException;
//
//import hope.base.utils.StringUtils;
//
///**
// * Created by zhangpeng on 16/10/13.
// */
//
//public class BooleanJsonAdapter extends TypeAdapter<Boolean> {
//    @Override
//    public void write(JsonWriter out, Boolean value) throws IOException {
//        int result = (value != null && value) ? 1 : 0;
//        out.value(result);
//    }
//
//    @Override
//    public Boolean read(JsonReader in) throws IOException {
//        JsonToken token = in.peek();
//        String value;
//        if (token == JsonToken.STRING) {
//            value = in.nextString();
//        } else {
//            value = String.valueOf(in.nextInt());
//        }
//        return !StringUtils.isEmpty(value) && Integer.parseInt(value) != 0;
//    }
//
//    static class User {
//        @JsonAdapter(BooleanJsonAdapter.class)
//        private boolean friend;
//    }
//
//    public static void main(String[] args) {
//
//
//        User friend = new User();
//        friend.friend = true;
//        System.out.println(new Gson().toJson(friend));
//        User fromJson = new Gson().fromJson("{\"friend\":1}", User.class);
//        System.out.println(fromJson.friend);
//    }
//}
