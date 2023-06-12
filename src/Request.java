import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

public class Request {

  private Header header;

  private Body body;


  public Request() {
    this.header = new Header();
  }

  public Header getHeader() {
    return header;
  }

  public void setHeader(Header header) {
    this.header = header;
  }

  public Body getBody() {
    return body;
  }

  public void setBody(Body body) {
    this.body = body;
  }

  public class Header {

    private String action;

    private final String time;

    private final String id;

    public String getAction() {
      return action;
    }

    public void setAction(String action) {
      this.action = action;
    }

    public String getTime() {
      return time;
    }

    public String getId() {
      return id;
    }

    public Header() {
      this.id = UUID.randomUUID().toString();
      SimpleDateFormat format = new SimpleDateFormat("YYYY-MM-DD hh:mm:ss");
      this.time = format.format(new Date());
    }
  }

  public class Body {

    private Object object;

  }

}
