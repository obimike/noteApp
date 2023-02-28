package com.tupen;

public class Note {

        private int id;
        private String title;
        private String body;
        private String imagePath;
        private String imagePath2;
        private String audioPath;
        private String date;

        public Note(int id, String title, String body, String imagePath, String imagePath2, String audioPath, String date) {
            this.id = id;
            this.title = title;
            this.body = body;
            this.imagePath = imagePath;
            this.imagePath2 = imagePath2;
            this.audioPath = audioPath;
            this.date = date;
        }
        public String getImagePath2() {
            return imagePath2;
        }

        public void setImagePath2(String imagePath2) {
            this.imagePath2 = imagePath2;
        }


        public int getId() {
            return id;
        }

        public String getDate() {
            return date;
        }

        public void setDate(String date) {
            this.date = date;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getBody() {
            return body;
        }

        public void setBody(String body) {
            this.body = body;
        }

        public String getImagePath() {
            return imagePath;
        }

        public void setImagePath(String imagePath) {
            this.imagePath = imagePath;
        }

        public String getAudioPath() {
            return audioPath;
        }

        public void setAudioPath(String audioPath) {
            this.audioPath = audioPath;
        }
    }


