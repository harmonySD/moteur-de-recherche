package parser;

import java.util.List;

public class Wiki {
    private List<Wiki.WikiPage> pageList;
    public void setPageList(List<Wiki.WikiPage> pageList) {
        this.pageList = pageList;
    }

    public List<Wiki.WikiPage> getPageList() {
        return this.pageList;
    }

    protected static class WikiPage {
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