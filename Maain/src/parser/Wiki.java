package parser;

import java.util.List;

public class Wiki {
    public List<Wiki.WikiPage> pageList;
    public void setPageList(List<Wiki.WikiPage> pageList) {
        this.pageList = pageList;
    }

    public List<Wiki.WikiPage> getPageList() {
        return this.pageList;
    }

    public static class WikiPage {
        private String title;
        private String text;
        public int id;

        public void setTitle(String title) {
            this.title = title;
        }

        public String getTitle() {
            return this.title;
        }

        public void setText(String text) {
            this.text = text;
        }

        public String getText() {
            return this.text;
        }

        public void setId(int id){
            this.id = id;
        }
    }
}