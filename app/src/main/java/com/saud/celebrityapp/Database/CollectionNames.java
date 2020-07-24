package com.saud.celebrityapp.Database;

public class CollectionNames {
    //collection names
    public static final String col_images="images";
    public static final String col_videos="videos";
    public static final String col_user_images="user_images";
    public static final String col_user_videos="user_videos";
    public static final String col_screensaver="screensavar";
    public static final String col_wallet="wallet";
    public static final String col_users="users";
    public static String col_admin_profile="admin_profile";
    public static String col_msg="messages";
    public static String col_price="message_price";

    //collection fields names
    public class images{
        public static final String field_id="id";
        public static final String field_name="name";
        public static final String field_url="url";
        public static final String field_price="price";
    }
    public class videos{
        public static final String field_id="id";
        public static final String field_name="name";
        public static final String field_url="url";
        public static final String field_price="price";
    }
    public class screensaver{
        public static final String field_image_url="image_url";
        public static final String doc_name="screensaver_document";

    }
    public static class wallet{
        public static final String field_balance="balance";
    }
    public static class user_images{
        public static final String field_user_id="user_id";
        public static final String field_image_url="image_url";
    }
    public static class user_videos{
        public static final String field_user_id="user_id";
        public static final String field_image_url="video_url";
    }

    public static class admin_profile{
        public static final String field_name="name";
        public static final String field_profile_image="image_url";
    }
    public static class user{
        public static final String field_name="name";
        public static final String field_profile_image="image_url";
    }
    public static class messages{
        public static final String field_sender_id="sender_id";
        public static final String field_receiver_id="receiver_id";
        public static final String field_msg="msg";
        public static final String field_msg_type="type";
        public static final String field_date_time="date_time";
    }

}
