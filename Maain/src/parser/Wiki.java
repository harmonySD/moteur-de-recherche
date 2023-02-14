package parser;

import java.util.List;

public class Wiki {
    public List<Wiki.WikiPage> pageList;
    public List<Wiki.WikiPage> allPageList;
    public void setPageList(List<Wiki.WikiPage> pageList) {
        this.pageList = pageList;
    }
    public void setAllPageList(List<Wiki.WikiPage> allPageList) {
        this.allPageList = allPageList;
    }

    public List<Wiki.WikiPage> getPageList() {
        return this.pageList;
    }
    public List<Wiki.WikiPage> getAllPageList() {
        return this.allPageList;
    }

    public static class WikiPage {
        public String title;
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