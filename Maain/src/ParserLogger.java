public class ParserLogger implements Runnable{
    private Integer pageCount;

    ParserLogger(Integer pageCount){
        this.pageCount = pageCount;
    }

    @Override
    public void run() {
        System.out.println("Nombre de page visitées : " + pageCount);
        try {
            Thread.sleep(1000);
        } catch (InterruptedException ignored) {

        }
    }
}
