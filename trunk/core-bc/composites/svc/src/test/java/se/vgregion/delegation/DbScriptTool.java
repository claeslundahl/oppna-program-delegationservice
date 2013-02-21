package se.vgregion.delegation;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * Created with IntelliJ IDEA.
 * User: portaldev
 * Date: 2013-01-30
 * Time: 11:06
 * To change this template use File | Settings | File Templates.
 */
public class DbScriptTool {

    public static void mainll(String[] args) throws ClassNotFoundException, SQLException, IOException {
        Class.forName(org.postgresql.Driver.class.getName());
        Connection connection = null;
        connection = DriverManager.getConnection(
                "jdbc:postgresql://vgdb0203.vgregion.se:5432/delegation", "delegation", "deleg-eleg");

        String query = "select distinct delegateto from vgr_delegation where (ltrim(delegatedforemail, ' ') = '' or delegatedforemail is null) and status = 'ACTIVE'";

        Statement st = connection.createStatement();

        ResultSet rs = st.executeQuery(query);

        List<String> tos = new ArrayList<String>();


        while (rs.next()) {
            tos.add(rs.getString(1));
        }
        System.out.println("Antal innan uppdatering utan mail i db " + tos.size());
        System.out.println("De utan mail " + tos);
        rs.close();
        st.close();

        Properties mails = new Properties();
        mails.load(new FileReader("/home/portaldev/Dokument/Delegation/2013-01-31/hsaid-epost.properties"));

        List<String> idsHavingNoMail = new ArrayList<String>();

        int c = 0, updated = 0;
        for (String id : tos) {
            String mail = mails.getProperty(id);
            if ((mail == null || "".equals(mail.trim()))) {
                idsHavingNoMail.add(id);
            } else {


                System.out.print(c + " ");
                PreparedStatement ps = connection.prepareCall("update vgr_delegation set delegatedforemail = ? where delegateto = ? and status = 'ACTIVE'");
                updated++;
                ps.setString(1, mail);
                ps.setString(2, id);
                int r = ps.executeUpdate();
                if (r == 0) System.out.println("No update for " + id);
                connection.commit();
                ps.close();


            }
            c++;
        }
        //System.out.println("Uppdateringar utförda: " + updated);
        System.out.println("Antal utan mail (i kiv) efter uppdatering " + idsHavingNoMail.size());
        System.out.println("De utan mail " + idsHavingNoMail);

        connection.close();
    }


    public static void mainBaar(String[] args) throws ClassNotFoundException, SQLException, IOException {
        Class.forName(org.postgresql.Driver.class.getName());
        Connection connection = null;
        connection = DriverManager.getConnection(
                "jdbc:postgresql://vgdb0203.vgregion.se:5432/delegation", "delegation", "deleg-eleg");

        String query = "select distinct d.delegateto, d.delegatedfor, d.delegatedforemail from vgr_delegation d \n" +
                "where d.status = 'ACTIVE'";//+
        //" \n" +                "and (d.delegatedforemail is null or d.delegatedforemail = '')";

        Statement st = connection.createStatement();

        ResultSet rs = st.executeQuery(query);

        List<String> tos = new ArrayList<String>();
        List<String> fors = new ArrayList<String>();
        List<String> eposts = new ArrayList<String>();
        //List<String> ids = new ArrayList<String>();

        while (rs.next()) {
            tos.add(rs.getString(1));
            fors.add(rs.getString(2));
            eposts.add(rs.getString(3));
        }
        //System.out.println("Antal innan uppdatering utan mail " + tos.size());
        //System.out.println("De utan mail " + tos);
        rs.close();
        st.close();

        Properties mails = new Properties();
        mails.load(new FileReader("/home/portaldev/Dokument/Delegation/2013-01-31/hsaid-epost.properties"));

        List<String> idsHavingNoMail = new ArrayList<String>();

        int c = 0, updated = 0;
        for (String id : tos) {
            String mail = mails.getProperty(id);
            if ((mail == null || "".equals(mail.trim()))) {
                idsHavingNoMail.add(id);
            } else {
                String forsEpost = mails.getProperty(fors.get(c), "nep");

                if (forsEpost.equals(eposts.get(c))) {
                    System.out.print(c + " ");
                    PreparedStatement ps = connection.prepareCall("update vgr_delegation set delegatedforemail = ? where delegateto = ? and status = 'ACTIVE'");
                    updated++;
                    ps.setString(1, mail);
                    ps.setString(2, id);
                    int r = ps.executeUpdate();
                    if (r == 0) System.out.println("No update for " + id);
                    connection.commit();
                    ps.close();
                }

            }
            c++;
        }
        System.out.println("Uppdateringar utförda: " + updated);
        System.out.println("Antal utan mail efter uppdatering " + idsHavingNoMail.size());
        System.out.println("De utan mail " + idsHavingNoMail);

        connection.close();
    }


    public static void main2(String[] args) throws ClassNotFoundException, SQLException, IOException {
        Class.forName(org.postgresql.Driver.class.getName());
        Connection connection = null;
        connection = DriverManager.getConnection(
                "jdbc:postgresql://vgdb0203.vgregion.se:5432/delegation", "delegation", "deleg-eleg");

        /*String query = "select distinct d.delegateto from vgr_delegation d \n" +
                "where to_char(d.validto, 'YYYY-MM-DD') = '2013-03-31' and d.status = 'ACTIVE' \n" +
                "and (delegatedforemail is null or delegatedforemail = '')";*/

        String query = "select distinct d.delegateto from vgr_delegation d \n" +
                "where d.status = 'ACTIVE'";
        //" \n" +
        //"and (delegatedforemail is null or delegatedforemail = '')";

        Statement st = connection.createStatement();

        ResultSet rs = st.executeQuery(query);

        List<String> ids = new ArrayList<String>();

        while (rs.next()) {
            ids.add(rs.getString(1));
        }
        System.out.println("Antal " + ids.size());
        rs.close();
        st.close();

        System.out.println(ids);

        Properties mails = new Properties();
        mails.load(new FileReader("/home/portaldev/Dokument/Delegation/2013-01-31/hsaid-epost.properties"));

        List<String> idsHavingNoMail = new ArrayList<String>();

        int c = 0;
        for (String id : ids) {
            String mail = mails.getProperty(id);
            if (mail == null || "".equals(mail.trim())) {
                idsHavingNoMail.add(id);
            } else {

            }
        }
        System.out.println("Antal som inte har i kiv " + idsHavingNoMail.size());

        connection.close();
    }


    public static void main10(String[] args) throws ClassNotFoundException, SQLException, IOException {
        Class.forName(org.postgresql.Driver.class.getName());
        Connection connection = null;
        connection = DriverManager.getConnection(
                "jdbc:postgresql://vgdb0203.vgregion.se:5432/delegation", "delegation", "deleg-eleg");

        /*String query = "select distinct d.delegateto from vgr_delegation d \n" +
                "where to_char(d.validto, 'YYYY-MM-DD') = '2013-03-31' and d.status = 'ACTIVE' \n" +
                "and (delegatedforemail is null or delegatedforemail = '')";*/

        String query = "select distinct d.delegatedfor from vgr_delegation d \n" +
                "where d.status = 'ACTIVE'";


        Statement st = connection.createStatement();

        ResultSet rs = st.executeQuery(query);

        List<String> ids = new ArrayList<String>();

        while (rs.next()) {
            ids.add(rs.getString(1));
        }
        System.out.println("Antal " + ids.size());
        rs.close();
        st.close();

        System.out.println(ids);

        Properties mails = new Properties();
        mails.load(new FileReader("/home/portaldev/Dokument/Delegation/2013-01-30/kiv-id-mail3.properties"));

        List<String> idsHavingNoMail = new ArrayList<String>();

        int c = 0;
        for (String id : ids) {
            String mail = mails.getProperty(id);
            if (mail == null || "".equals(mail.trim())) {
                idsHavingNoMail.add(id);
            } else {

            }
        }
        System.out.println("Antal som inte har i kiv " + idsHavingNoMail.size());

        connection.close();
    }



    public static void mainGoo(String[] args) throws ClassNotFoundException, SQLException, IOException {
        Class.forName(org.postgresql.Driver.class.getName());
        Connection connection = null;
        connection = DriverManager.getConnection(
                "jdbc:postgresql://vgdb0203.vgregion.se:5432/delegation", "delegation", "deleg-eleg");

        String query = "select distinct delegatedfor, count(id) from vgr_delegation \n" +
                "where status = 'ACTIVE'\n" +
                "and to_char(validto, 'YYYY-MM-DD') = '2013-03-31'\n" +
                "group by delegatedfor \n" +
                "order by 1, 2";

        Statement st = connection.createStatement();

        ResultSet rs = st.executeQuery(query);

        Properties mails = new Properties();
        mails.load(new FileReader("/home/portaldev/Dokument/Delegation/2013-01-30/kiv-id-mail3.properties"));

        while (rs.next()) {
            String delegatedFor = rs.getString(1);
            String epost = mails.getProperty(delegatedFor, "");
            System.out.println(delegatedFor + ";" + epost + ";" + rs.getObject(2));
        }


        rs.close();
        st.close();



        connection.close();
    }


    public static void main(String[] args) throws ClassNotFoundException, SQLException, IOException {
        Class.forName(org.postgresql.Driver.class.getName());
        Connection connection = null;
        connection = DriverManager.getConnection(
                "jdbc:postgresql://vgdb0203.vgregion.se:5432/delegation", "delegation", "deleg-eleg");

        String query = "select distinct delegateto, count(id) from vgr_delegation \n" +
                "where status = 'ACTIVE'\n" +
                "and to_char(validto, 'YYYY-MM-DD') = '2013-03-31'\n" +
                "group by delegateto \n" +
                "order by 1, 2";

        Statement st = connection.createStatement();

        ResultSet rs = st.executeQuery(query);

        Properties mails = new Properties();
        mails.load(new FileReader("/home/portaldev/Dokument/Delegation/2013-01-30/kiv-id-mail3.properties"));

        while (rs.next()) {
            String delegateTo = rs.getString(1);
            String epost = mails.getProperty(delegateTo, "");
            //System.out.println(delegateTo + ";" + epost + ";" + rs.getObject(2));
            System.out.println(delegateTo + ";" + epost);
        }


        rs.close();
        st.close();



        connection.close();
    }




}
