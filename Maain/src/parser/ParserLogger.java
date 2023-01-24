package parser;

public class ParserLogger implements Runnable{
    private Integer pageCount;

    public ParserLogger(Integer pageCount){
        this.pageCount = pageCount;
    }

    @Override
    public void run() {
        try {
            Thread.sleep(1000);
        } catch (InterruptedException ignored) {

        }
    }
}
