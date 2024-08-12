//package com.example.learning_api.kafka.message;
//
//import lombok.Builder;
//import lombok.Data;
//
//@Data
//@Builder
//public class NotificationMsgData {
//    private String senderId;
//    private String senderRole;
//    private String title;
//    private String message;
//    private String formId;
//    private String formType;
//    private String type;
//
//    public NotificationMsgData() {
//
//    }
//
//    // Static factory method
//    public static NotificationMsgDataBuilder builder() {
//        return new NotificationMsgDataBuilder();
//    }
//
//    // Builder class
//    public static class NotificationMsgDataBuilder {
//        private NotificationMsgData instance = new NotificationMsgData();
//
//        public NotificationMsgDataBuilder senderId(String senderId) {
//            instance.setSenderId(senderId);
//            return this;
//        }
//
//        public NotificationMsgDataBuilder senderRole(String senderRole) {
//            instance.setSenderRole(senderRole);
//            return this;
//        }
//
//        public NotificationMsgDataBuilder title(String title) {
//            instance.setTitle(title);
//            return this;
//        }
//
//        public NotificationMsgDataBuilder message(String message) {
//            instance.setMessage(message);
//            return this;
//        }
//
//        public NotificationMsgDataBuilder formId(String formId) {
//            instance.setFormId(formId);
//            return this;
//        }
//
//        public NotificationMsgDataBuilder formType(String formType) {
//            instance.setFormType(formType);
//            return this;
//        }
//
//        public NotificationMsgDataBuilder type(String type) {
//            instance.setType(type);
//            return this;
//        }
//
//        public NotificationMsgData build() {
//            return instance;
//        }
//    }
//}