public class main {


  public static void main(String[] args) {
    Request request = new Request();
    request.getHeader().setAction("动作");
    System.out.println(request);
  }

}