import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.apache.catalina.Context;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.startup.Tomcat;
import org.recognition.database.DB;
import org.recognition.fingerprint.Links;
import org.recognition.model.Song;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.io.Writer;
import java.util.List;

public class Server {
    private Server() throws LifecycleException {
        Tomcat tomcat = new Tomcat();
        tomcat.setPort(8080);
        tomcat.getConnector();
        Gson gson = new Gson();

        Context ctx = tomcat.addContext("", new File(".").getAbsolutePath());

        Tomcat.addServlet(ctx, "searchServlet", new HttpServlet() {
            protected void service(HttpServletRequest req, HttpServletResponse resp) throws IOException {
                List<Links.Link> linkList = gson.fromJson(req.getParameterMap().get("links")[0], new TypeToken<List<Links.Link>>() {
                }.getType());

                int id = new DB().searchId(linkList);
                Song song = new DB().getSongById(id);
                System.out.println(song);

                Writer w = resp.getWriter();
                w.write(gson.toJson(song));
                w.flush();
            }
        });


        ctx.addServletMappingDecoded("/search", "searchServlet");

        tomcat.start();
        tomcat.getServer().await();
    }

    public static void main(String[] args) throws LifecycleException {
        new Server();
    }

}