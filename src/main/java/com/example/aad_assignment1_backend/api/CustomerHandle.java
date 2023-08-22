package com.example.aad_assignment1_backend.api;

import com.example.aad_assignment1_backend.dto.CustomerDTO;
import jakarta.json.Json;
import jakarta.json.JsonArrayBuilder;
import jakarta.json.JsonObjectBuilder;
import jakarta.json.bind.Jsonb;
import jakarta.json.bind.JsonbBuilder;


import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.annotation.*;
import javax.sql.DataSource;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;
import java.rmi.RemoteException;
import java.sql.*;
import java.util.ArrayList;

@WebServlet(name = "CustomerHandle", value = "/CustomerHandle")
public class CustomerHandle extends HttpServlet {

    Connection connection;

    @Override
    public void init() throws ServletException {
        try {
            Class.forName(getServletContext().getInitParameter("mysql-driver")); //loading the mysql driver
            String username = getServletContext().getInitParameter("db-user");   // database configurations
            String password = getServletContext().getInitParameter("db-pw");
            String url = getServletContext().getInitParameter("db-url");
            this.connection = DriverManager.getConnection(url, username, password); //direct JDBC connection

        } catch (ClassNotFoundException | SQLException ex) {
            throw new RuntimeException(ex);
        }
        InitialContext ctx = null; //connection pool
        try {
            ctx = new InitialContext();
            DataSource pool = (DataSource) ctx.lookup("java:comp/env/jdbc/CustomerHandle"); //create an object
            this.connection = pool.getConnection(); //getting connection from the pool
        } catch (NamingException | SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse rsp) throws ServletException, IOException {
        if (req.getContentType() == null || !req.getContentType().toLowerCase().startsWith("application/json")) {
            rsp.sendError(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
        }
        System.out.println("do post");
        try {
            Jsonb jsonb = JsonbBuilder.create();
            CustomerDTO customerObj = jsonb.fromJson(req.getReader(), CustomerDTO.class);
            //validation
            if (customerObj.getId() == null || !customerObj.getId().matches("^[a-zA-Z0-9_]{3,20}$")) {
                throw new RuntimeException("Invalid ID");
            } else if (customerObj.getName() == null || !customerObj.getName().matches("[A-Za-z '-]+")) {
                throw new RuntimeException("Invalid Name");
            } else if (customerObj.getAddress() == null || !customerObj.getAddress().matches("[A-Za-z '-]+")) {
                throw new RuntimeException("Invalid Address");
         //   } else if (customerObj.getTelephone() = null || !customerObj.getTelephone().matches("[\n" + "^[0]{1}[7]{1}[01245678]{1}[0-9]{7}$\n]")) {
           //     throw new RemoteException("Invalid telephone");
            }
            PreparedStatement ps =
                    connection.prepareStatement("INSERT INTO customer(id,name,address,telephone) VALUES (?,?,?,?)");
            ps.setString(1, customerObj.getId());
            ps.setString(2, customerObj.getName());
            ps.setString(3, customerObj.getAddress());
            ps.setInt(4, customerObj.getTelephone());


            if (ps.executeUpdate() != 1) {
                throw new RuntimeException("Save Failed");
            }
            /*ResultSet rst = ps.getGeneratedKeys();
            rst.next();

            rsp.setStatus(HttpServletResponse.SC_CREATED);
            //the created json is sent to frontend
            rsp.setContentType("application/json");
            jsonb.toJson(customerObj, rsp.getWriter());*/

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        //Todo:Exception Handle
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse rsp) throws ServletException, IOException {
        if (req.getContentType() == null || !req.getContentType().toLowerCase().startsWith("application/json")) {
            rsp.sendError(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
            return;

        }
        try {
            Jsonb jsonb = JsonbBuilder.create();
            CustomerDTO customerObj = jsonb.fromJson(req.getReader(), CustomerDTO.class);
            //validation

            if (customerObj.getId() == null || !customerObj.getId().matches("^[a-zA-Z0-9_]{3,20}$")) {
                throw new RuntimeException("Invalid ID");
            } else if (customerObj.getName() == null || !customerObj.getName().matches("[A-Za-z '-]+")) {
                throw new RuntimeException("Invalid Name");
            } else if (customerObj.getAddress() == null || !customerObj.getAddress().matches("[A-Za-z '-]+")) {
                throw new RuntimeException("Invalid Address");
          //  } else if (customerObj.getTelephone() = null || !customerObj.getTelephone().matches("[\n" + "^[0]{1}[7]{1}[01245678]{1}[0-9]{7}$\n]")) {
               // throw new RemoteException("Invalid telephone");
            }
            PreparedStatement ps =
                    connection.prepareStatement("UPDATE customer SET name = ?, address = ?, telephone = ?  WHERE id = ?");

            ps.setString(4, customerObj.getId()); // Set the "id" parameter
            ps.setString(1, customerObj.getName());
            ps.setString(2, customerObj.getAddress());
            ps.setInt(3, customerObj.getTelephone());


            if (ps.executeUpdate() != 1) {
                throw new RuntimeException("Update Failed");
            }
            ResultSet rst = ps.getGeneratedKeys();
            rst.next();

            rsp.setStatus(HttpServletResponse.SC_CREATED);
            //the created json is sent to frontend
            rsp.setContentType("application/json");
            jsonb.toJson(customerObj, rsp.getWriter());

        } catch (SQLException e) {
            rsp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Database Error");
            return;
        }
        //Todo:Exception Handle
    }


    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse rsp) throws ServletException, IOException {
        if (req.getContentType() == null || !req.getContentType().toLowerCase().startsWith("application/json")) {
            rsp.sendError(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
        }
        try {
            Jsonb jsonb = JsonbBuilder.create();
            CustomerDTO customerObj = jsonb.fromJson(req.getReader(), CustomerDTO.class);
            //validation
            if (customerObj.getId() == null || !customerObj.getId().matches("^[a-zA-Z0-9_]{3,20}$")) {
                throw new RuntimeException("Invalid ID");
            }
            PreparedStatement ps =
                    connection.prepareStatement("delete from customer where id=?");


            ps.setString(1, customerObj.getId()); // Set the "id" parameter


            if (ps.executeUpdate() != 1) {
                throw new RuntimeException("Delete Failed");

            }

              ResultSet rst = ps.getGeneratedKeys();
              rst.next();

            rsp.setStatus(HttpServletResponse.SC_CREATED);
            //the created json is sent to frontend
            rsp.setContentType("application/json");
            jsonb.toJson(customerObj, rsp.getWriter());

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        //Todo:Exception Handle
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if (req.getContentType() == null || !req.getContentType().toLowerCase().startsWith("application/json")) {
            resp.sendError(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
        }

        try {
            ArrayList<CustomerDTO> allCustomer = new ArrayList<>();
            PreparedStatement ps =
                    connection.prepareStatement("SELECT*FROM customer");
            ResultSet rst = ps.executeQuery();
            while (rst.next()) {
                String id = rst.getString("id");
                String name = rst.getString("name");
                String address = rst.getString("address");
                int telephone = rst.getInt("telephone");
                allCustomer.add(new CustomerDTO(id, name, address, telephone));
            }

            //JSON Format
            String customerJson = "[";
            for (CustomerDTO customer : allCustomer) {
                String id = customer.getId();
                String name = customer.getName();
                String address = customer.getAddress();
                int telephone = customer.getTelephone();

                //json obj
                String cusOb = "{\"id\":\" "+ id + "\",\"name\":\"" + name + "\",\"address\":\"" + address + "\",\"telephone\":" + telephone + "},";
                customerJson += cusOb;
            }
            String substring = customerJson.substring(0, customerJson.length() - 1);
            substring+="]";
            resp.getWriter().write(substring);


        } catch ( SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
