package nz.ac.vuw.swen301.assignment3.server;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

public class LogsServlet extends HttpServlet {

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("text/html");
        PrintWriter out = response.getWriter();
        out.println("<html>");
        out.println("<head>");
        out.println("<title>Get Logs</title>");
        out.println("</head>");
        out.println("<body>");
        int limit = Integer.parseInt(request.getParameter("limit"));
        String level = request.getParameter("level");
        // get logs in order of timestamp
        out.println("</body>");
        out.println("</html>");

        out.close();
    }

    public void doPost(HttpServletRequest request, HttpServletResponse response){
        // Store logs
    }
}
